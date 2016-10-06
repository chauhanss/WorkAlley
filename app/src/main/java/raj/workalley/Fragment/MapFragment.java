package raj.workalley.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import raj.workalley.Model.AmenitiesItem;
import raj.workalley.util.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.Model.WorkspaceList;
import raj.workalley.HomeActivity;
import raj.workalley.util.AmenitiesListAdapter;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final String REQUEST_CANCEL = "CANCEL REQUEST";
    private static final String REQUEST_BOOK = "BOOK SEAT";
    private static final String REQUEST_END = "END SESSION";
    private static final String END_REQUEST = "SESSION END REQUESTED";
    Context mContext;
    private GoogleMap mMap;
    private String destination = "random";
    private LatLng latLng = new LatLng(12.9539974, 77.6309394);
    private Session mSession;
    EditText locationSearch;
    HashMap<String, String> mMarkersMap = new HashMap<String, String>();
    private View rootView;
    private WorkspaceList.Workspace mWorkspace;
    private FrameLayout currentWorkspace;
    private CardView searchLayout;
    private String workspaceId;
    private TextView bookSeat;
    private TextView lastStatus;
    private TextView currentTimer;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_map, null);
        locationSearch = (EditText) v.findViewById(R.id.location_search);


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_1);
        mapFragment.getMapAsync(this);

        locationSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (locationSearch.getRight() - locationSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        onMapSearch();
                        Helper.hideKeyboardIfShown((Activity) mContext, locationSearch);
                        return true;
                    }
                }
                return false;
            }
        });

        locationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    onMapSearch();
                    Helper.hideKeyboardIfShown((Activity) mContext, locationSearch);
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        searchLayout = (CardView) rootView.findViewById(R.id.search_view);
        currentWorkspace = (FrameLayout) rootView.findViewById(R.id.current_workspace);

        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
            mSession.getAllActiveWorkspace(0);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    public void showWorkspaceView() {
        if (mSession.getActiveWorkspace() != null && mSession.getActiveWorkspaceRequestId() != null) {
            if (Helper.isConnected(mContext)) {
                mSession.getUpdatedRequestStatus(mSession.getActiveWorkspaceRequestId(), 0);
            } else {
                Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
            }
        } else {
            setDataInWorkspaceView("new", "");
            final Animation animBounceBottomTop = AnimationUtils.loadAnimation(mContext, R.anim.decelerate_translation_enter_bottom);
            android.os.Handler handle = new android.os.Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchLayout.setVisibility(View.GONE);
                    currentWorkspace.setVisibility(View.VISIBLE);
                    currentWorkspace.startAnimation(animBounceBottomTop);
                }
            }, 200);
        }
    }

    private void setDataInWorkspaceView(String status, String updatedAt) {

        if (mWorkspace != null) {
            LinearLayout bottomLayout = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
            if (!mSession.isLoggedIn())
                bottomLayout.setVisibility(View.GONE);
            else
                bottomLayout.setVisibility(View.VISIBLE);

            TextView workspaceName = (TextView) rootView.findViewById(R.id.current_workspace_name);
            workspaceName.setText(mWorkspace.getName());

            TextView workspaceAddress = (TextView) rootView.findViewById(R.id.current_workspace_address);
            workspaceAddress.setText(mWorkspace.getAddress().getFullAddress());

            RecyclerView amenitiesRecyclerView = (RecyclerView) rootView.findViewById(R.id.workspace_amenities);
            ArrayList<AmenitiesItem> amenitiesList = getAmenitiesList();
            AmenitiesListAdapter mAdapter = new AmenitiesListAdapter(amenitiesList, false, amenitiesRecyclerView);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            amenitiesRecyclerView.setLayoutManager(mLayoutManager);
            amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
            amenitiesRecyclerView.setAdapter(mAdapter);

            TextView ownerName = (TextView) rootView.findViewById(R.id.owner_name);
            ownerName.setText("Owned By: " + mWorkspace.getOwner().getName());

            TextView ownerEmail = (TextView) rootView.findViewById(R.id.owner_email);
            ownerEmail.setText(mWorkspace.getOwner().getEmail());

            bookSeat = (TextView) rootView.findViewById(R.id.book_seat);
            bookSeat.setOnClickListener(seatStatusClickListener);

            currentTimer = (TextView) rootView.findViewById(R.id.session_timer);


            lastStatus = (TextView) rootView.findViewById(R.id.last_update);
            lastStatus.setVisibility(View.GONE);
            switch (status) {
                case "requested":
                    ((HomeActivity) mContext).startHostService();
                    bookSeat.setText(REQUEST_CANCEL);
                    lastStatus.setVisibility(View.VISIBLE);
                    lastStatus.setText("Seat booked on: " + Helper.getFormattedDate(updatedAt));
                    break;
                case END_REQUEST:
                    ((HomeActivity) mContext).startHostService();
                    bookSeat.setText(END_REQUEST);
                    lastStatus.setVisibility(View.VISIBLE);
                    lastStatus.setText("Session end requested on: " + updatedAt);
                    break;
                case "started":
                    ((HomeActivity) mContext).startHostService();
                    if (SharedPrefsUtils.hasKey(mContext, END_REQUEST, Constants.SP_NAME)) {

                        updatedAt = SharedPrefsUtils.getStringPreference(mContext, END_REQUEST, Constants.SP_NAME);
                        bookSeat.setText(END_REQUEST);
                        lastStatus.setVisibility(View.VISIBLE);
                        lastStatus.setText("Session end requested on: " + updatedAt);
                    } else {
                        bookSeat.setText(REQUEST_END);
                        lastStatus.setVisibility(View.VISIBLE);
                        lastStatus.setText("Session started on: " + Helper.getFormattedDate(updatedAt));
                    }
                    break;
                case "ended":
                case "rejected":
                default:
                    mSession.setActiveWorkspace(null);
                    mSession.setActiveWorkspaceRequestId(null);

                    ((HomeActivity) mContext).stopSocketService();

                    if (SharedPrefsUtils.hasKey(mContext, END_REQUEST, Constants.SP_NAME))
                        SharedPrefsUtils.removePreferenceByKey(mContext, END_REQUEST, Constants.SP_NAME);
                    bookSeat.setText(REQUEST_BOOK);
                    break;
            }

        }

        if (mSession.getActiveWorkspace() == null) {
            TextView sessionTimer = (TextView) rootView.findViewById(R.id.session_timer);
            sessionTimer.setVisibility(View.GONE);

            TextView availableSeats = (TextView) rootView.findViewById(R.id.session_seat_available);
            availableSeats.setText(getString(R.string.available_seats, 5));
            availableSeats.setVisibility(View.VISIBLE);
        } else {
            TextView sessionTimer = (TextView) rootView.findViewById(R.id.session_timer);
            sessionTimer.setVisibility(View.VISIBLE);

            TextView availableSeats = (TextView) rootView.findViewById(R.id.session_seat_available);
            availableSeats.setVisibility(View.GONE);
        }
    }

    View.OnClickListener seatStatusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Button button = (Button) v;
            switch (button.getText().toString()) {
                case REQUEST_BOOK:
                    if (Helper.isConnected(mContext)) {
                        Helper.showProgressDialogSpinner(mContext, "Please wait", "connecting server", false);
                        mSession.requestSeat(mSession.getUser().get_id(), workspaceId, 0);
                    } else
                        Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_END:

                    if (!SharedPrefsUtils.hasKey(mContext, END_REQUEST, Constants.SP_NAME)) {
                        if (Helper.isConnected(mContext)) {
                            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Ending Session", false);
                            Session.getInstance(mContext).endSessionInWorkspaceRequest(mSession.getActiveWorkspaceRequestId(), 0);
                        } else
                            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                    } else {
                        setDataInWorkspaceView(END_REQUEST, SharedPrefsUtils.getStringPreference(mContext, END_REQUEST, Constants.SP_NAME));
                    }
                    break;

                case REQUEST_CANCEL:
                    if (Helper.isConnected(mContext)) {
                        Helper.showProgressDialogSpinner(mContext, "Please wait", "connecting server", false);
                        mSession.cancelRequestedSeat(mSession.getActiveWorkspaceRequestId(), 0);
                    } else
                        Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mSession != null) {
            currentWorkspace = (FrameLayout) rootView.findViewById(R.id.current_workspace);
            currentWorkspace.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            invalidateMapFragment(mSession.getActiveWorkspace());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.clear();
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setOnMarkerClickListener(this);

        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        } else {
            //error
        }
    }

    public void onMapSearch() {
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public void invalidateMapFragment(String workspaceId) {

        if (workspaceId != null) {
            this.workspaceId = workspaceId;
            WorkspaceList workspaces = mSession.getWorkspaces();

            if (workspaces != null && workspaces.getWorkspaceData() != null && workspaces.getWorkspaceData().size() > 0) {
                for (WorkspaceList.Workspace workspace : workspaces.getWorkspaceData()) {

                    if (workspaceId.equalsIgnoreCase(workspace.get_id())) {
                        mWorkspace = workspace;
                    }
                }
                showWorkspaceView();
            } else {
                if (Helper.isConnected(mContext)) {
                    Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
                    mSession.getWorkspaceInfoFromId(workspaceId, 0);
                } else
                    Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
            }

        }
    }


    private ArrayList<AmenitiesItem> getAmenitiesList() {

        ArrayList<String> amenityList = mWorkspace.getAmenities();
        ArrayList<AmenitiesItem> list = new ArrayList<>();
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


    @Override
    public void onPause() {
        super.onPause();
        Helper.hideKeyboardIfShown((Activity) mContext, locationSearch);
        mContext.unregisterReceiver(notificationListener);
    }


    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(notificationListener, new IntentFilter(Constants.REQUEST_RESPONSE));
    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            String requestID = intent.getStringExtra("USER");
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            if (message.equalsIgnoreCase(Constants.SESSION_END_CONFIRMED)) {
                if (requestID.equalsIgnoreCase(mSession.getActiveWorkspaceRequestId())) {
                    setDataInWorkspaceView(Constants.SESSION_END_CONFIRMED, currentDateTimeString);
                }
            } else if (message.equalsIgnoreCase(Constants.BOOKING_ACCEPT) || message.equalsIgnoreCase(Constants.BOOKING_REJECT)) {
                if (requestID.equalsIgnoreCase(mSession.getActiveWorkspaceRequestId())) {
                    String status = (message.equalsIgnoreCase(Constants.BOOKING_ACCEPT)) ? "started" : "";
                    setDataInWorkspaceView(status, currentDateTimeString);
                }
            }
        }
    };

    private void setWorkspaceDataOnMap() {
        mMap.clear();
        WorkspaceList workspaceList = Session.getInstance(mContext).getWorkspaces();

        if (workspaceList.getWorkspaceData() != null && workspaceList.getWorkspaceData().size() > 0) {
            int i = 0;
            for (final WorkspaceList.Workspace workspace : workspaceList.getWorkspaceData()) {

                WorkspaceList.Address workspaceAddress = workspace.getAddress();
                if (workspaceAddress != null && workspaceAddress.getLoc().size() == 2) {
                    createMarker(workspace.isAvailable(), workspaceAddress.getLoc().get(1), workspaceAddress.getLoc().get(0), workspace.getName(), workspace.getAddress().getState() + " " + workspace.getAddress().getCity(), workspace.get_id());
                    i++;
                }
            }
        }
    }

    public void createMarker(boolean isAvailable, double latitude, double longitude, String title, String snippet, String workspaceId) {
        Marker marker;
        if (isAvailable) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        marker.showInfoWindow();
        mMarkersMap.put(marker.getId(), workspaceId);
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        marker.showInfoWindow();

        invalidateMapFragment(mMarkersMap.get(marker.getId()));

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mSession = Session.getInstance(mContext);
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.GET_ALL_WORKSPACES: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();
                    WorkspaceList parsedWorkspaceResponse = (WorkspaceList) Session.getInstance(mContext).getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.Workspaces);
                    mSession.setWorkspaces(parsedWorkspaceResponse);

                    setWorkspaceDataOnMap();
                    invalidateMapFragment(mSession.getActiveWorkspace());
                    break;
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;
            case CobbocEvent.GET_WORKSPACE_FROM_ID:
                Helper.dismissProgressDialog();

                if (event.getStatus()) {

                    JSONObject data = (JSONObject) event.getValue();
                    mWorkspace = (WorkspaceList.Workspace) mSession.getParsedResponseFromGSON(data, Session.workAlleyModels.Workspace);
                    showWorkspaceView();
                }

                break;

            case CobbocEvent.REQUEST_SEAT: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();

                    ((HomeActivity) mContext).startHostService();
                    try {
                        JSONObject spaceObject = (JSONObject) jsonObject.getJSONObject("space");
                        mSession.setActiveWorkspace(spaceObject.getString("_id"));
                        mSession.setActiveWorkspaceRequestId(jsonObject.getString("_id"));

                        setDataInWorkspaceView(jsonObject.getString("status"), jsonObject.getString("updatedAt"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
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

                        setDataInWorkspaceView(data.getString("status"), data.getString("updatedAt"));
                        final Animation animBounceBottomTop = AnimationUtils.loadAnimation(mContext, R.anim.decelerate_translation_enter_bottom);
                        android.os.Handler handle = new android.os.Handler();
                        handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                searchLayout.setVisibility(View.GONE);
                                currentWorkspace.setVisibility(View.VISIBLE);
                                currentWorkspace.startAnimation(animBounceBottomTop);
                            }
                        }, 200);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;

            case CobbocEvent.CANCEL_BOOKING_REQUEST: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    ((HomeActivity) mContext).stopSocketService();
                    try {
                        JSONObject jsonObject = (JSONObject) event.getValue();
                        setDataInWorkspaceView(jsonObject.getString("status"), jsonObject.getString("updatedAt"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;
            case CobbocEvent.END_SESSION: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    SharedPrefsUtils.setStringPreference(mContext, END_REQUEST, currentDateTimeString, Constants.SP_NAME);
                    setDataInWorkspaceView(END_REQUEST, currentDateTimeString);
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;
        }
    }

    public void showMapLayout() {

        final Animation animBounceBottomExit = AnimationUtils.loadAnimation(mContext, R.anim.decelerate_translation_exit_bottom);
        android.os.Handler handle = new android.os.Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentWorkspace.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                currentWorkspace.startAnimation(animBounceBottomExit);
            }
        }, 200);
    }

    public void onBackPressed() {

        if (currentWorkspace.getVisibility() == View.VISIBLE) {
            if (mSession.getActiveWorkspace() == null) {
                showMapLayout();
            } else
                ((HomeActivity) mContext).finish();
        } else
            ((HomeActivity) mContext).finish();
    }
}
