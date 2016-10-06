package raj.workalley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.loopbar.adapter.ICategoryItem;
import com.cleveroad.loopbar.adapter.SimpleCategoriesAdapter;
import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.cleveroad.loopbar.widget.Orientation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import raj.workalley.BaseActivity;
import raj.workalley.util.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.socket.HostSocketService;
import raj.workalley.Fragment.AccountFragment;
import raj.workalley.Fragment.LoginFragment;
import raj.workalley.Fragment.MapFragment;
import raj.workalley.Fragment.SettingFragment;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class HomeActivity extends BaseActivity implements OnItemClickListener {
    private LoopBarView loopBarView;

    private SimpleCategoriesAdapter categoriesAdapter;
    private SimpleFragmentStatePagerAdapter pagerAdapter;
    String workspaceName;

    private ViewPager viewPager;
    private Session mSession;
    private Context mContext;
    public Intent startIntent;

    @Orientation
    private int orientation;
    @LoopBarView.GravityAttr
    private int endlessGravity = LoopBarView.SELECTION_GRAVITY_START;
    private MapFragment mMapFragment;
    private SettingFragment mSettingsFragment;
    private AccountFragment mAccountFragment;
    private LoginFragment mLoginFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        mContext = this;
        mSession = Session.getInstance(this);
        startIntent = getIntent();
        // isUserLoggedIn = mSession.getUser() == null ? false : true;
        initNavToolBar();
        //   startHostService();


        initNavToolBar();

        if (getIntent() != null) {
            boolean swapToRequest = getIntent().getBooleanExtra("swapToRequestPage", false);

            if (swapToRequest)
                viewPager.setCurrentItem(2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(mContext))
            EventBus.getDefault().register(mContext);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (EventBus.getDefault().isRegistered(mContext))
            EventBus.getDefault().unregister(mContext);
    }

    public void startHostService() {
        Intent intent = new Intent(getBaseContext(), HostSocketService.class);
        Bundle b = new Bundle();
        b.putString(Constants.SESSION_COOKIES_ID, mSession.getToken());
        intent.putExtras(b);
        startService(intent);
    }

    public void stopSocketService() {
        Intent intent = new Intent(getBaseContext(), HostSocketService.class);
        Bundle b = new Bundle();
        b.putString(Constants.SESSION_COOKIES_ID, mSession.getToken());
        intent.putExtras(b);
        stopService(intent);
    }

    public void initNavToolBar() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        List<Fragment> list = new ArrayList<>(3);

        mMapFragment = MapFragment.newInstance();
        list.add(mMapFragment);

        if (mSession.isLoggedIn()) {
            mSettingsFragment = SettingFragment.newInstance();
            list.add(mSettingsFragment);

            mAccountFragment = AccountFragment.newInstance();
            list.add(mAccountFragment);
        } else {
            mLoginFragment = LoginFragment.newInstance();
            list.add(mLoginFragment);
        }

        pagerAdapter = new SimpleFragmentStatePagerAdapter(getSupportFragmentManager(), list);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabsStrip = (TabLayout) findViewById(R.id.tab_strip);
        if (tabsStrip != null) {
            tabsStrip.setupWithViewPager(viewPager);
        }
    }

    public void setCurrentPage(int page) {
        viewPager.setCurrentItem(page);
    }

    public void invalidatePager(int page) {
        mMapFragment.invalidateMapFragment(mSession.getActiveWorkspace());
        setCurrentPage(page);
    }


    @Override
    public void onItemClicked(int position) {
        ICategoryItem categoryItem = categoriesAdapter.getItem(position);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 0) {
            mMapFragment.onBackPressed();
        } else
            super.onBackPressed();
    }

    public final class SimpleFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;
        private String tabTitlesForLoggedUsr[] = new String[]{"Dashboard", "Profile", "Account"};
        private String tabTitlesForNoUsr[] = new String[]{"Explore", "Login"};

        public SimpleFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mSession.isLoggedIn())
                return tabTitlesForLoggedUsr[position];
            return tabTitlesForNoUsr[position];
        }
    }

    public Intent getStartIntent() {
        return startIntent;
    }

    public void recreateThis() {
        viewPager.setAdapter(null);
        initNavToolBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.HOST_DETAILS_ACTIVITY_REQUEST_DETAILS) {
            if (resultCode == Constants.HOST_DETAILS_ACTIVITY_REQUEST_DETAILS) {
                Bundle b = data.getExtras();
                workspaceName = b.getString(Constants.WORKSPACE_NAME);
                viewPager.setCurrentItem(3, true);
                //startUserService();
            }
        }
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {

        if (event.getStatus()) {
            JSONObject jsonObject = (JSONObject) event.getValue();
            if (jsonObject.has(Constants.FRAGMENT_Id) && !jsonObject.isNull(Constants.FRAGMENT_Id)) {
                try {
                    int fragmentId = jsonObject.getInt(Constants.FRAGMENT_Id);

                    switch (fragmentId) {
                        case 0:
                            mMapFragment.onEventMainThread(event);
                            break;
                        case 1:
                            mSettingsFragment.onEventMainThread(event);
                            break;
                        case 2:
                            mAccountFragment.onEventMainThread(event);
                            break;
                        case 3:
                            mLoginFragment.onEventMainThread(event);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {

            int fragmentId = event.getFragment();

            switch (fragmentId) {
                case 0:
                    mMapFragment.onEventMainThread(event);
                    break;
                case 1:
                    mSettingsFragment.onEventMainThread(event);
                    break;
                case 2:
                    mAccountFragment.onEventMainThread(event);
                    break;
                case 3:
                    mLoginFragment.onEventMainThread(event);
                    break;
                default:
                    Helper.dismissProgressDialog();
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }

        }

    }
}