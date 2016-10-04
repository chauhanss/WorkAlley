package raj.workalley.user.fresh.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

import raj.workalley.BaseFragment;
import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;
import raj.workalley.user.fresh.host_details.HostDetailsActivity;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    Context mContext;
    private GoogleMap mMap;
    private String destination = "random";
    private LatLng latLng = new LatLng(12.9539974, 77.6309394);
    private Session mSession;
    EditText locationSearch;
    HashMap<String, String> mMarkersMap = new HashMap<String, String>();

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

        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
            mSession.getAllActiveWorkspace();
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();

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


    @Override
    public void onPause() {
        super.onPause();
        Helper.hideKeyboardIfShown((Activity) mContext, locationSearch);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void setWorkspaceDataOnMap() {
        mMap.clear();
        WorkspaceList workspaceList = Session.getInstance(mContext).getWorkspaces();

        if (workspaceList.getWorkspaceData() != null && workspaceList.getWorkspaceData().size() > 0) {
            int i = 0;
            for (final WorkspaceList.Workspace workspace : workspaceList.getWorkspaceData()) {

                WorkspaceList.Address workspaceAddress = workspace.getAddress();
                if (workspaceAddress != null && workspaceAddress.getLoc().size() == 2) {
                    //          LatLng latLng = new LatLng(workspaceAddress.getLoc().get(0), workspaceAddress.getLoc().get(1));
                    /**
                     * get(0) field is longitude
                     * get(1) filed is latitude
                     */
                    LatLng latLng = new LatLng(workspaceAddress.getLoc().get(1), workspaceAddress.getLoc().get(0));
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

        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), HostDetailsActivity.class);
                intent.putExtra(Constants.WORKSPACE_ID, mMarkersMap.get(marker.getId()));
                //        getActivity().startActivityForResult(intent, Constants.HOST_DETAILS_ACTIVITY_REQUEST_DETAILS);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }, 500);
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
                    break;
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    public HashMap<Integer, ArrayList<Double>> dummyLocationList() {

        HashMap<Integer, ArrayList<Double>> hashMap = new HashMap<>();

        ArrayList<Double> arrayList = new ArrayList<>();
        arrayList.add(12.9583888);
        arrayList.add(77.6789617);

        hashMap.put(0, arrayList);

        ArrayList<Double> arrayList2 = new ArrayList<>();
        arrayList2.add(13.0555923);
        arrayList2.add(77.643937);

        hashMap.put(1, arrayList2);

        return hashMap;
    }
}
