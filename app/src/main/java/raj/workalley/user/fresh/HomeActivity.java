package raj.workalley.user.fresh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cleveroad.loopbar.adapter.ICategoryItem;
import com.cleveroad.loopbar.adapter.SimpleCategoriesAdapter;
import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.cleveroad.loopbar.widget.Orientation;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.BaseActivity;
import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;
import raj.workalley.socket.HostSocketService;
import raj.workalley.user.fresh.account.AccountFragment;
import raj.workalley.user.fresh.map.MapFragment;
import raj.workalley.user.fresh.settings.SettingFragment;
import raj.workalley.user.fresh.offers.OffersFragment;
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

    @Orientation
    private int orientation;
    @LoopBarView.GravityAttr
    private int endlessGravity = LoopBarView.SELECTION_GRAVITY_START;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        mContext = this;
        mSession = Session.getInstance(this);

        initNavToolBar(0);
        startHostService();

        if (getIntent() != null) {
            boolean swapToRequest = getIntent().getBooleanExtra("swapToRequestPage", false);

            if (swapToRequest)
                viewPager.setCurrentItem(2);
        }
    }

    private void startHostService() {
        Intent intent = new Intent(getBaseContext(), HostSocketService.class);
        Bundle b = new Bundle();
        b.putString(Constants.SESSION_COOKIES_ID, mSession.getToken());
        intent.putExtras(b);
        startService(intent);
    }

    public void initNavToolBar(int page) {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        List<Fragment> list = new ArrayList<>(3);

        if (mSession.getActiveWorkspace() == null)
            list.add(MapFragment.newInstance());
        else
            list.add(CurrentWorkspace.newInstance());
        //list.add(OffersFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(AccountFragment.newInstance());
        pagerAdapter = new SimpleFragmentStatePagerAdapter(getSupportFragmentManager(), list);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabsStrip = (TabLayout) findViewById(R.id.tab_strip);
        if (tabsStrip != null) {
            tabsStrip.setupWithViewPager(viewPager);
        }

        viewPager.setCurrentItem(page);
    }

    public void invalidatePager(int page) {
        viewPager.setAdapter(null);
        initNavToolBar(page);
    }


    @Override
    public void onItemClicked(int position) {
        ICategoryItem categoryItem = categoriesAdapter.getItem(position);
        viewPager.setCurrentItem(position);
    }


    public final class SimpleFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;
        private String tabTitles[] = new String[]{"Dashboard", "Profile", "Account"};

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
            return tabTitles[position];
        }
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


}