package raj.workalley.host.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.BaseActivity;
import raj.workalley.R;
import raj.workalley.host.HomeActivity;
import raj.workalley.user.fresh.offers.OfferDummyItem;
import raj.workalley.user.fresh.offers.OffersAdapter;
import raj.workalley.util.AmenitiesListAdapter;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class HostSignUpActivity extends BaseActivity {

    Button continueBtn;

    public class AmenitiesItem {
        String amenitiesName;
        int amenitiesIcon;

        public AmenitiesItem(String amenitiesName, int amenitiesIcon) {
            this.amenitiesName = amenitiesName;
            this.amenitiesIcon = amenitiesIcon;
        }

        public String getAmenitiesName() {
            return amenitiesName;
        }

        public int getAmenitiesIcon() {
            return amenitiesIcon;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_signup);
        continueBtn = (Button) findViewById(R.id.host_continue);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostSignUpActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView amenitiesRecyclerView = (RecyclerView) findViewById(R.id.amenities_list);
        List<AmenitiesItem> amenitiesList = getAmenitiesList();
        AmenitiesListAdapter mAdapter = new AmenitiesListAdapter(amenitiesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        amenitiesRecyclerView.setLayoutManager(mLayoutManager);
        amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amenitiesRecyclerView.setAdapter(mAdapter);

    }

    private List<AmenitiesItem> getAmenitiesList() {
        List<AmenitiesItem> list = new ArrayList<>();

        AmenitiesItem item1 = new AmenitiesItem("Ac", R.drawable.ic_ac);
        list.add(item1);

        AmenitiesItem item2 = new AmenitiesItem("WiFi", R.drawable.ic_wifi);
        list.add(item2);

        AmenitiesItem item3 = new AmenitiesItem("Elevator", R.drawable.ic_lift);
        list.add(item3);

        AmenitiesItem item4 = new AmenitiesItem("Cafe", R.drawable.ic_cafe);
        list.add(item4);

        AmenitiesItem item5 = new AmenitiesItem("Conference", R.drawable.ic_conference);
        list.add(item5);

        AmenitiesItem item6 = new AmenitiesItem("Power Backup", R.drawable.ic_power_back);
        list.add(item6);

        return list;
    }
}
