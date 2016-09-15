package raj.workalley.user.fresh.host_details;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.AmenitiesItem;
import raj.workalley.BaseActivity;
import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;
import raj.workalley.util.AmenitiesListAdapter;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class HostDetailsActivity extends BaseActivity {

    Session mSession;
    Context mContext;
    WorkspaceList.Workspace mWorkspace = null;
    Button bookSeat;
    String workspaceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_details);
        mContext = this;
        mSession = Session.getInstance(mContext);

        if (getIntent() != null) {
            workspaceId = getIntent().getStringExtra(Constants.WORKSPACE_ID);
            WorkspaceList workspaces = mSession.getWorkspaces();

            if (workspaces != null && workspaces.getWorkspaceData() != null && workspaces.getWorkspaceData().size() > 0) {
                for (WorkspaceList.Workspace workspace : workspaces.getWorkspaceData()) {

                    if (workspaceId.equalsIgnoreCase(workspace.get_id())) {
                        mWorkspace = workspace;
                    }
                }
            }

            if (mWorkspace == null) {
                Toast.makeText(mContext, "NO Workspace match!", Toast.LENGTH_LONG).show();
                return;
            } else
                makeHostDataRequest();
        }
        bookSeat = (Button) findViewById(R.id.book_seat);
        bookSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), mSession.getUser().get_id() + " " + workspaceId, Toast.LENGTH_LONG).show();
                mSession.requestSeat(mSession.getUser().get_id(), workspaceId);
            }
        });
    }

    private void makeHostDataRequest() {
        setUpAndDisplayData();
    }

    private void setUpAndDisplayData() {

        TextView name = (TextView) findViewById(R.id.workspace_name);
        name.setText(mWorkspace.getName());

        TextView address = (TextView) findViewById(R.id.workspace_address);
        address.setText(mWorkspace.getAddress().getFullAddress());

        RecyclerView amenitiesRecyclerView = (RecyclerView) findViewById(R.id.workspace_amenities);
        List<AmenitiesItem> amenitiesList = getAmenitiesList();
        AmenitiesListAdapter mAdapter = new AmenitiesListAdapter(amenitiesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        amenitiesRecyclerView.setLayoutManager(mLayoutManager);
        amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amenitiesRecyclerView.setAdapter(mAdapter);

    }

    private List<AmenitiesItem> getAmenitiesList() {

        ArrayList<String> amenityList = mWorkspace.getAmenities();
        List<AmenitiesItem> list = new ArrayList<>();
        for (String amenity : amenityList) {

            switch (amenity.toLowerCase()) {

                case "Ac":
                    AmenitiesItem item1 = new AmenitiesItem("Ac", R.drawable.ic_ac, false);
                    list.add(item1);
                    break;
                case "WiFi":
                    AmenitiesItem item2 = new AmenitiesItem("WiFi", R.drawable.ic_wifi, false);
                    list.add(item2);
                    break;
                case "Elevator":
                    AmenitiesItem item3 = new AmenitiesItem("Elevator", R.drawable.ic_lift, false);
                    list.add(item3);
                    break;
                case "Cafe":
                    AmenitiesItem item4 = new AmenitiesItem("Cafe", R.drawable.ic_cafe, false);
                    list.add(item4);
                    break;
                case "Conference":
                    AmenitiesItem item5 = new AmenitiesItem("Conference", R.drawable.ic_conference, false);
                    list.add(item5);
                    break;
                case "Power Backup":
                    AmenitiesItem item6 = new AmenitiesItem("Power Backup", R.drawable.ic_power_back, false);
                    list.add(item6);
                    break;
            }
        }

        return list;
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.REQUEST_SEAT: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();

                    break;
                }
            }
        }
    }
}
