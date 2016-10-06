package raj.workalley.user.fresh.host_details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import raj.workalley.HomeActivity;
import raj.workalley.Model.AmenitiesItem;
import raj.workalley.BaseActivity;
import raj.workalley.util.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.Model.WorkspaceList;
import raj.workalley.Model.UserInfo;
import raj.workalley.util.AmenitiesListAdapter;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class HostDetailsActivity extends BaseActivity {

    private static final String REQUEST_CANCEL = "CANCEL REQUEST";
    private static final String REQUEST_BOOK = "BOOK A SEAT";
    private static final String REQUEST_END = "SESSION IN PROGRESS";
    Session mSession;
    Context mContext;
    WorkspaceList.Workspace mWorkspace = null;
    Button bookSeat;
    String workspaceId;
    UserInfo mUser = null;
    private SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_details);
        mContext = this;
        mSession = Session.getInstance(mContext);
        mUser = mSession.getUser();

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
        bookSeat.setText(REQUEST_BOOK);

        bookSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (bookSeat.getText().toString()) {
                    case REQUEST_BOOK:
                        if (Helper.isConnected(mContext)) {
                            Helper.showProgressDialogSpinner(mContext, "Please wait", "connecting server", false);
                            mSession.requestSeat(mSession.getUser().get_id(), workspaceId, 4);
                        } else
                            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                        break;
                    case REQUEST_CANCEL:
                        if (SharedPrefsUtils.hasKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME)) {
                            String requestId = SharedPrefsUtils.getStringPreference(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                            if (Helper.isConnected(mContext)) {
                                Helper.showProgressDialogSpinner(mContext, "Please wait", "connecting server", false);
                                mSession.cancelRequestedSeat(requestId, 4);
                            } else
                                Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case REQUEST_END:
                        break;
                }
            }
        });

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SharedPrefsUtils.hasKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME)) {
                    String requestId = SharedPrefsUtils.getStringPreference(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                    if (Helper.isConnected(mContext)) {
                        mSession.getUpdatedRequestStatus(requestId, 4);
                    } else {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase(Constants.BOOKING_REJECT) || message.equalsIgnoreCase(Constants.BOOKING_ACCEPT))
                getLatestDataFromSharedPreference();
        }
    };


    public void getLatestDataFromSharedPreference() {
        TextView requestStatus = (TextView) findViewById(R.id.request_status);
        if (SharedPrefsUtils.hasKey(mContext, mUser.get_id(), Constants.SP_NAME)) {

            String value = SharedPrefsUtils.getStringPreference(mContext, mUser.get_id(), Constants.SP_NAME);

            String requestType = value.substring(0, value.lastIndexOf("|"));
            String workspace = value.substring(value.lastIndexOf("|") + 1, value.length());

            if (workspace.equalsIgnoreCase(workspaceId)) {
                switch (requestType) {
                    case Constants.BOOKING_ACCEPT:
                        bookSeat.setText(REQUEST_END);
                        requestStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_green_dark));
                        requestStatus.setText("*Your request to book a seat for this workspace has been accepted!");
                        //start session
                        break;
                    case Constants.BOOKING_REJECT:
                        bookSeat.setText(REQUEST_BOOK);
                        requestStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
                        requestStatus.setText("*Sorry! Your request to book a seat for this workspace has been rejected!");
                        SharedPrefsUtils.removePreferenceByKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                        SharedPrefsUtils.removePreferenceByKey(mContext, mUser.get_id(), Constants.SP_NAME);
                        break;
                    case Constants.SESSION_END_CONFIRMED:
                        bookSeat.setText(REQUEST_BOOK);
                        requestStatus.setText("");
                        SharedPrefsUtils.removePreferenceByKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                        SharedPrefsUtils.removePreferenceByKey(mContext, mUser.get_id(), Constants.SP_NAME);
                        break;
                }
            }

        } else if (SharedPrefsUtils.hasKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME)) {

            requestStatus.setText("Request Pending! Please refresh to get updated data!");
            bookSeat.setText(REQUEST_CANCEL);
            requestStatus.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(mContext))
            EventBus.getDefault().register(mContext);

        mContext.registerReceiver(notificationListener, new IntentFilter(Constants.REQUEST_RESPONSE));
        // getLatestDataFromSharedPreference();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(mContext))
            EventBus.getDefault().unregister(mContext);
        mContext.unregisterReceiver(notificationListener);
    }

    private void makeHostDataRequest() {
        setUpAndDisplayData();
    }

    private void setUpAndDisplayData() {
        TextView name = (TextView) findViewById(R.id.workspace_name);
        name.setText(mWorkspace.getName());

        TextView address = (TextView) findViewById(R.id.workspace_address);
        address.setText(mWorkspace.getAddress().getFullAddress());

        TextView email = (TextView) findViewById(R.id.workspace_email);
        email.setText(mWorkspace.getOwner().getEmail());

        RecyclerView amenitiesRecyclerView = (RecyclerView) findViewById(R.id.workspace_amenities);
        ArrayList<AmenitiesItem> amenitiesList = getAmenitiesList();
        AmenitiesListAdapter mAdapter = new AmenitiesListAdapter(amenitiesList, false, amenitiesRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        amenitiesRecyclerView.setLayoutManager(mLayoutManager);
        amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amenitiesRecyclerView.setAdapter(mAdapter);

    }

    private ArrayList<AmenitiesItem> getAmenitiesList() {

        ArrayList<String> amenityList = mWorkspace.getAmenities();
        ArrayList<AmenitiesItem> list = new ArrayList<>();
        for (String amenity : amenityList) {
            switch (amenity.trim()) {
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
        swipeRefresh.setRefreshing(false);
        switch (event.getType()) {
            case CobbocEvent.REQUEST_SEAT: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();

                /*    Intent intent = new Intent();
                    try {
                        Bundle b = new Bundle();
                        b.putString(Constants.WORKSPACE_NAME, jsonObject.getJSONObject("space").getString("name"));
                        intent.putExtras(b);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setResult(Constants.HOST_DETAILS_ACTIVITY_REQUEST_DETAILS, intent);
                    finish(); */

                    try {
                        JSONObject spaceObject = (JSONObject) jsonObject.getJSONObject("space");
                        mSession.setActiveWorkspace(spaceObject.getString("_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        SharedPrefsUtils.setStringPreference(mContext, Constants.BOOKING_REQUEST_ID, jsonObject.getString("_id"), Constants.SP_NAME);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView requestStatus = (TextView) findViewById(R.id.request_status);
                    requestStatus.setText("Request Pending! Please refresh to get updated data!");
                    bookSeat.setText(REQUEST_CANCEL);
                    requestStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark));
                    clearRejectAcceptSharedPreference();

                    final android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            intent.putExtra("swapToRequestPage", true);
                            startActivity(intent);
                            finish();
                        }
                    }, 500);

                    break;
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            case CobbocEvent.CANCEL_BOOKING_REQUEST: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {

                    /**
                     * Remove id as new request will have new request id
                     */
                    SharedPrefsUtils.removePreferenceByKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);

                    TextView requestStatus = (TextView) findViewById(R.id.request_status);
                    bookSeat.setText(REQUEST_BOOK);
                    requestStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark));
                    requestStatus.setText("*Request Canceled!");

                    final android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            intent.putExtra("swapToRequestPage", true);
                            startActivity(intent);
                            finish();
                        }
                    }, 500);
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;
            case CobbocEvent.LAST_STATUS: {
                Helper.dismissProgressDialog();

                if (event.getStatus()) {
                    try {
                        JSONObject object = (JSONObject) event.getValue();
                        JSONObject data = object.getJSONObject("data");
                        JSONObject user = data.getJSONObject("user");
                        String requestId = data.getString("_id");
                        JSONObject space = data.getJSONObject("space");

                        switch (data.getString("status")) {
                            case "requested":

                                if (!SharedPrefsUtils.hasKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME)) {

                                    SharedPrefsUtils.setStringPreference(mContext, Constants.BOOKING_REQUEST_ID, requestId, Constants.SP_NAME);
                                    TextView requestStatus = (TextView) findViewById(R.id.request_status);
                                    requestStatus.setText("Request Pending! Please refresh to get updated data!");
                                    bookSeat.setText(REQUEST_CANCEL);
                                    requestStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark));
                                    clearRejectAcceptSharedPreference();
                                }
                                break;
                            case "started":
                                SharedPrefsUtils.setStringPreference(mContext, user.getString("_id"), Constants.BOOKING_ACCEPT + "|" + space.getString("_id"), Constants.SP_NAME);
                                break;
                            case "rejected":
                                SharedPrefsUtils.setStringPreference(mContext, user.getString("_id"), Constants.BOOKING_REJECT + "|" + space.getString("_id"), Constants.SP_NAME);
                                break;
                            case "ended":
                                SharedPrefsUtils.setStringPreference(mContext, user.getString("_id"), Constants.SESSION_END_CONFIRMED + "|" + space.getString("_id"), Constants.SP_NAME);
                                break;
                            case "canceled":
                                SharedPrefsUtils.removePreferenceByKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                                TextView requestStatus = (TextView) findViewById(R.id.request_status);
                                bookSeat.setText(REQUEST_BOOK);
                                requestStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark));
                                requestStatus.setText("*Request Canceled!");
                                break;
                        }

                        getLatestDataFromSharedPreference();

                        final android.os.Handler handler = new android.os.Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                intent.putExtra("swapToRequestPage", true);
                                startActivity(intent);
                                finish();
                            }
                        }, 500);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;
        }
    }

    private void clearRejectAcceptSharedPreference() {

        if (SharedPrefsUtils.hasKey(mContext, mSession.getUser().get_id(), Constants.SP_NAME)) {
            SharedPrefsUtils.removePreferenceByKey(mContext, mSession.getUser().get_id(), Constants.SP_NAME);
        }
    }
}
