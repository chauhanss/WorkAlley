package raj.workalley.user.fresh.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import raj.workalley.BaseFragment;
import raj.workalley.R;
import raj.workalley.user.fresh.host_details.HostDetailsActivity;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    Context mContext;
    private GoogleMap mMap;
    private String destination = "random";
    private LatLng latLng = new LatLng(-34.8799074, 174.7565664);

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_map, null);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_1);
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.clear();
        mMap.setOnMarkerClickListener(this);
        if (latLng != null) {
            if (destination != null && !destination.isEmpty()) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(destination));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        } else {
            //error
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equals(destination)) {
            Intent intent = new Intent(getActivity(), HostDetailsActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
