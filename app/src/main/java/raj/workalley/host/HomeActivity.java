package raj.workalley.host;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.cleveroad.loopbar.adapter.ICategoryItem;
import com.cleveroad.loopbar.adapter.SimpleCategoriesAdapter;
import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.cleveroad.loopbar.widget.Orientation;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import raj.workalley.BaseActivity;
import raj.workalley.R;
import raj.workalley.host.dashboard.DashboardFragment;
import raj.workalley.host.settings.SettingFragment;


/**
 * Created by vishal.raj on 9/5/16.
 */
public class HomeActivity extends BaseActivity implements OnItemClickListener {

    private LoopBarView loopBarView;

    private SimpleCategoriesAdapter categoriesAdapter;
    private SimpleFragmentStatePagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private Socket mSocket;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("received", "enjoy");
        }
    };


    @Orientation
    private int orientation;
    @LoopBarView.GravityAttr
    private int endlessGravity = LoopBarView.SELECTION_GRAVITY_START;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_host);
        loopBarView = (LoopBarView) findViewById(R.id.endlessView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        List<ICategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem(R.drawable.ic_setting, "Settings"));
        items.add(new CategoryItem(R.drawable.ic_account, "Account"));


        categoriesAdapter = new SimpleCategoriesAdapter(items);
        loopBarView.setCategoriesAdapter(categoriesAdapter);
        loopBarView.addOnItemClickListener(this);

        List<Fragment> list = new ArrayList<>(2);
        list.add(SettingFragment.newInstance());
        list.add(DashboardFragment.newInstance());


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

        try {
            mSocket = IO.socket("http://app.workalley.in");
        } catch (URISyntaxException e) {
        }

        mSocket.connect();
        mSocket.on("CONNECTED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Ack ack = (Ack) args[args.length - 1];
                Log.e("received", "enjoy");
                ack.call();
            }
        });

        mSocket.on("AUTH", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Ack ack = (Ack) args[args.length - 1];
                Log.e("received", "enjoy");
                ack.call();
            }
        });

       // mSocket.on("CONNECTED", onNewMessage);
        //mSocket.on("AUTH", onNewMessage);
        mSocket.on("BOOKING_REQUESTED", onNewMessage);
        mSocket.on("BOOKING_ACCEPTED", onNewMessage);
        mSocket.on("BOOKING_REJECTED", onNewMessage);
        mSocket.on("BOOKING_CANCELED", onNewMessage);
        mSocket.on("BOOKING_END_REQUESTED", onNewMessage);
        mSocket.on("BOOKING_END_CONFIRMED", onNewMessage);


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
