package raj.workalley.host.signup;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import raj.workalley.AmenitiesItem;
import raj.workalley.BaseActivity;
import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;
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
    LatLng latLng;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_signup);
        mContext = this;
        mSession = Session.getInstance(this);
        continueBtn = (Button) findViewById(R.id.host_continue);

        final RecyclerView amenitiesRecyclerView = (RecyclerView) findViewById(R.id.amenities_list);
        ArrayList<AmenitiesItem> amenitiesList = getAmenitiesList();
        mAdapter = new AmenitiesListAdapter(amenitiesList, true, amenitiesRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        amenitiesRecyclerView.setLayoutManager(mLayoutManager);
        amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amenitiesRecyclerView.setAdapter(mAdapter);

       /* findViewById(R.id.from_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(HostSignUpActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        ((TextView) findViewById(R.id.from_time)).setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select From Time");
                mTimePicker.show();

            }
        });

        findViewById(R.id.to_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(HostSignUpActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        ((TextView) findViewById(R.id.to_time)).setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select To Time");
                mTimePicker.show();

            }
        });*/

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
                String[] address = getAddress();
                List<String> amenities = mAdapter.getSelectedItem();
                if (goodToGo(address, amenities)) {
                    new GeocoderTask().execute(address);
                } else
                    Toast.makeText(getApplicationContext(), "Review Details", Toast.LENGTH_LONG).show();
               /* String to = ((TextView) findViewById(R.id.to_time)).getText().toString();
                String from = ((TextView) findViewById(R.id.from_time)).getText().toString();*/
            }
        });


    }

    private boolean goodToGo(String[] address, List<String> amenities) {
        if (amenities.size() == 0)
            return false;

        if (((TextView) findViewById(R.id.workspace_name)).getText().toString().equals(""))
            return false;

        if (((TextView) findViewById(R.id.number_of_seat)).getText().toString().equals(""))
            return false;

        for (int i = 0; i < address.length; i++) {
            if (address[i].equals(""))
                return false;
        }
        return true;
    }

    private String[] getAddress() {
        String[] address = new String[5];
        address[0] = ((EditText) findViewById(R.id.address1)).getText().toString();
        address[1] = ((EditText) findViewById(R.id.address2)).getText().toString();
        address[2] = ((EditText) findViewById(R.id.state)).getText().toString();
        address[3] = ((EditText) findViewById(R.id.city)).getText().toString();
        address[4] = ((EditText) findViewById(R.id.pincode)).getText().toString();
        return address;
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
        //String[] address = {"hd 111 sector i", "jankipuram", "up", "lucknow", "226021"};
        double[] loc = {latLng.longitude, latLng.latitude};
        String name = ((TextView) findViewById(R.id.workspace_name)).getText().toString();
        mSession.createWorkSpaceApi(name, getAddress(), loc, mAdapter.getSelectedItem());
    }

    private List<ImageItem> getImageList() {
        return null;
    }

    public class ImageItem {
    }

    private ArrayList<AmenitiesItem> getAmenitiesList() {
        ArrayList<AmenitiesItem> list = new ArrayList<>();

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
                    mSession.getHostWorkspaceData(mSession.getUser().get_id());
                } else {
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                }
                break;

            case CobbocEvent.GET_HOST_DETAILS:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();
                    WorkspaceList parsedResponse = (WorkspaceList) Session.getInstance(mContext).getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.Workspaces);
                    Session.getInstance(mContext).setWorkspaces(parsedResponse);
                    Intent intent = new Intent(HostSignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                break;
        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses = null;
            String str = locationName[0] + " " + locationName[1] + " " + locationName[2] + " " + locationName[3];
            try {
                addresses = geocoder.getFromLocationName(str, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(HostSignUpActivity.this, "No Location Found!", Toast.LENGTH_SHORT);
            } else {

                //mMap.clear();

                Address address = addresses.get(0);

                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                makeCreateWorkSpaceApiCall();

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                // Add a marker in user's location, and move the camera.
                //mMap.addMarker(new MarkerOptions().position(latLng).title(addressText));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

            }
        }
    }
}
