package raj.workalley.host.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import raj.workalley.UserInfo;
import raj.workalley.host.HomeActivity;
import raj.workalley.user.fresh.offers.OfferActivity;
import raj.workalley.user.fresh.offers.OfferDummyItem;
import raj.workalley.user.fresh.offers.OffersAdapter;
import raj.workalley.util.AmenitiesListAdapter;
import raj.workalley.util.Helper;
import raj.workalley.util.ImageListAdapter;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class HostSignUpActivity extends BaseActivity {

    Button continueBtn;
    AmenitiesListAdapter mAdapter;
    Session mSession;
    ImageListAdapter imageListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_signup);
        mSession = Session.getInstance(this);
        continueBtn = (Button) findViewById(R.id.host_continue);

        RecyclerView amenitiesRecyclerView = (RecyclerView) findViewById(R.id.amenities_list);
        List<AmenitiesItem> amenitiesList = getAmenitiesList();
        mAdapter = new AmenitiesListAdapter(amenitiesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        amenitiesRecyclerView.setLayoutManager(mLayoutManager);
        amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amenitiesRecyclerView.setAdapter(mAdapter);

       /* RecyclerView imageRecyclerView = (RecyclerView) findViewById(R.id.image_list);
        List<ImageItem> imageList = getImageList();
        imageListAdapter = new ImageListAdapter(imageList);
        imageRecyclerView.setLayoutManager(mLayoutManager);
        imageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerView.setAdapter(imageListAdapter);*/


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), mAdapter.getSelectedItem().size() + " ", Toast.LENGTH_LONG).show();
                makeCreateWorkSpaceApiCall();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void makeCreateWorkSpaceApiCall() {
        String[] address = {"hd 111 sector i", "jankipuram", "up", "lucknow", "226021"};
        int[] loc = {23, 45};
        mSession.createWorkSpaceApi("xyz", address, loc);
    }

    private List<ImageItem> getImageList() {
        return null;
    }

    public class ImageItem {
    }


    public class AmenitiesItem {
        String amenitiesName;
        int amenitiesIcon;
        boolean active;

        public AmenitiesItem(String amenitiesName, int amenitiesIcon, boolean active) {
            this.amenitiesName = amenitiesName;
            this.amenitiesIcon = amenitiesIcon;
            this.active = active;
        }

        public String getAmenitiesName() {
            return amenitiesName;
        }

        public int getAmenitiesIcon() {
            return amenitiesIcon;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    private List<AmenitiesItem> getAmenitiesList() {
        List<AmenitiesItem> list = new ArrayList<>();

        AmenitiesItem item1 = new AmenitiesItem("Ac", R.drawable.ic_ac, false);
        list.add(item1);

        AmenitiesItem item2 = new AmenitiesItem("WiFi", R.drawable.ic_wifi, false);
        list.add(item2);

        AmenitiesItem item3 = new AmenitiesItem("Elevator", R.drawable.ic_lift, false);
        list.add(item3);

        AmenitiesItem item4 = new AmenitiesItem("Cafe", R.drawable.ic_cafe, false);
        list.add(item4);

        AmenitiesItem item5 = new AmenitiesItem("Conference", R.drawable.ic_conference, false);
        list.add(item5);

        AmenitiesItem item6 = new AmenitiesItem("Power Backup", R.drawable.ic_power_back, false);
        list.add(item6);

        return list;
    }


    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.CREATE_WORKSPACE:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    Log.d("here", "ok");
                } else {
                    Toast.makeText(getApplicationContext(), "Not able to login. Please check your details.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
