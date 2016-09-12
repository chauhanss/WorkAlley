package raj.workalley.user.fresh;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.BaseActivity;
import raj.workalley.CobbocEvent;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;
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
        loopBarView = (LoopBarView) findViewById(R.id.endlessView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mContext = this;
        mSession = Session.getInstance(this);

        List<ICategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem(R.drawable.ic_map, "Map"));
        items.add(new CategoryItem(R.drawable.ic_offer, "Offers"));
        items.add(new CategoryItem(R.drawable.ic_setting, "Settings"));
        items.add(new CategoryItem(R.drawable.ic_account, "Account"));


        categoriesAdapter = new SimpleCategoriesAdapter(items);
        loopBarView.setCategoriesAdapter(categoriesAdapter);
        loopBarView.addOnItemClickListener(this);

        List<Fragment> list = new ArrayList<>(3);
        list.add(MapFragment.newInstance());
        list.add(OffersFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(AccountFragment.newInstance());


        pagerAdapter = new SimpleFragmentStatePagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new AbstractPageChangedListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                loopBarView.setCurrentItem(position);
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

    class CategoryItem implements ICategoryItem {
        private int categoryItemDrawableId;
        private String categoryName;

        public CategoryItem(int categoryItemDrawableId, String categoryName) {
            this.categoryItemDrawableId = categoryItemDrawableId;
            this.categoryName = categoryName;
        }

        @Override
        public int getCategoryIconDrawable() {
            return categoryItemDrawableId;
        }

        @Override
        public String getCategoryName() {
            return categoryName;
        }

        @Override
        public String toString() {
            return categoryName;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof CategoryItem && ((CategoryItem) o).categoryName.equals(categoryName);
        }
    }
}