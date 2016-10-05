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
import raj.workalley.user.fresh.login_signup.LoginFragment;
import raj.workalley.user.fresh.map.MapFragment;
import raj.workalley.user.fresh.settings.SettingFragment;
import raj.workalley.user.fresh.offers.OffersFragment;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class HomeActivity extends BaseActivity implements OnItemClickListener {
    private LoopBarView loopBarView;
    // private boolean isUserLoggedIn;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        startIntent = getIntent();
        mContext = this;
        mSession = Session.getInstance(this);
        // isUserLoggedIn = mSession.getUser() == null ? false : true;
        initNavToolBar();
        startHostService();

        //loopBarView = (LoopBarView) findViewById(R.id.endlessView);


        if (getIntent() != null) {
            boolean swapToRequest = getIntent().getBooleanExtra("swapToRequestPage", false);

            if (swapToRequest)
                viewPager.setCurrentItem(2);
        }

       /* List<ICategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem(R.drawable.ic_map, "Map"));
        items.add(new CategoryItem(R.drawable.ic_offer, "Offers"));
        items.add(new CategoryItem(R.drawable.ic_setting, "Settings"));
        items.add(new CategoryItem(R.drawable.ic_account, "Account"));*/


        /*categoriesAdapter = new SimpleCategoriesAdapter(items);
        loopBarView.setCategoriesAdapter(categoriesAdapter);
        loopBarView.addOnItemClickListener(this);
*/


    }

    private void startHostService() {
        /*HostSocketService.initSocket();
        HostSocketService.connectToServer();
        HostSocketService.setSessionCookiesId(session.getSessionIdCookies());
        HostSocketService.sendCookies();
        HostSocketService.authHost();*/
        Intent intent = new Intent(getBaseContext(), HostSocketService.class);
        Bundle b = new Bundle();
        b.putString(Constants.SESSION_COOKIES_ID, mSession.getToken());
        intent.putExtras(b);
        startService(intent);
    }

    private void initNavToolBar() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);

        List<Fragment> list = new ArrayList<>(3);
        list.add(MapFragment.newInstance());
        //list.add(OffersFragment.newInstance());
        if (mSession.isLoggedIn()) {
            list.add(SettingFragment.newInstance());
            list.add(AccountFragment.newInstance());
        } else {
            list.add(LoginFragment.newInstance());
        }
        pagerAdapter = new SimpleFragmentStatePagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabsStrip = (TabLayout) findViewById(R.id.tab_strip);
        if (tabsStrip != null) {
            tabsStrip.setupWithViewPager(viewPager);
        }

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_map),
                        Color.parseColor("#EE946F")
                ).build()
        );
       /* models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_offer),
                        Color.parseColor("#EE946F")
                ).build()
        ); */
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_setting),
                        Color.parseColor("#EE946F")
                ).build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_account),
                        Color.parseColor("#EE946F")
                ).build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        viewPager.addOnPageChangeListener(new AbstractPageChangedListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("tag", "on page scrolled");
            }
        });
    }


    @Override
    public void onItemClicked(int position) {
        ICategoryItem categoryItem = categoriesAdapter.getItem(position);
        viewPager.setCurrentItem(position);
    }


    public final class SimpleFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;
        private String tabTitlesForLoggedUsr[] = new String[]{"Explore", "Profile", "Active Sessions"};
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

    public abstract class AbstractPageChangedListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

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


}