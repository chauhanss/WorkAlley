package raj.workalley.host.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import raj.workalley.CobbocEvent;
import raj.workalley.LoginActivity;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.AmenitiesItem;
import raj.workalley.LoginActivity;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.util.AmenitiesListAdapter;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class SettingFragment extends Fragment {

    Session mSession;
    EditText email, name, phone, workspaceName, numberOfSeat;
    EditText line1, line2, city, state, pincode;
    ImageView editModeBtn;
    Button save_n_logout_btn, dlt_workspace;

    boolean editMode;
    private Context mContext;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_host_settings, null);
        mSession = Session.getInstance(getActivity());

        editModeBtn = (ImageView) v.findViewById(R.id.edit_mode);
        save_n_logout_btn = (Button) v.findViewById(R.id.host_save_logout);
        dlt_workspace = (Button) v.findViewById(R.id.delete);

        editModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMode) {
                    editMode = true;
                    save_n_logout_btn.setText(getString(R.string.save));
                    setEditTextEditable();
                } else {
                    editMode = false;
                    save_n_logout_btn.setText(getString(R.string.logout));
                    setEditTextNonEditable();
                }
            }
        });

        dlt_workspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.deleteWorkspace(mSession.getWorkspaces().getWorkspaceData().get(0).get_id());
            }
        });

        save_n_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode)
                    callHostDetailsSaveApi();
                else
                    callHostLogoutApi();

            }
        });

        defineNonEditableViews(v);
        defineAmenitiesList(v);

        return v;
    }

    private void defineAmenitiesList(View v) {
        RecyclerView amenitiesRecyclerView = (RecyclerView) v.findViewById(R.id.amenities_list);
        ArrayList<AmenitiesItem> amenitiesList = getAmenitiesList();
        AmenitiesListAdapter mAdapter;
        mAdapter = new AmenitiesListAdapter(amenitiesList, false,amenitiesRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        amenitiesRecyclerView.setLayoutManager(mLayoutManager);
        amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amenitiesRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<AmenitiesItem> getAmenitiesList() {
        ArrayList<String> listFromBackend = mSession.getWorkspaces().getWorkspaceData().get(0).getAmenities();
        ArrayList<AmenitiesItem> list = new ArrayList<>();

        for (String amenity : listFromBackend) {
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

    private void callHostLogoutApi() {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please wait", "Connecting server", false);
            mSession.logout();
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();

    }

    private void callHostDetailsSaveApi() {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please wait", "Connecting server", false);
            mSession.saveHostDetails();
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();

    }

    private void setEditTextEditable() {
        email.setEnabled(true);
        name.setEnabled(true);
        // phone.setEnabled(true);
        workspaceName.setEnabled(true);
        // numberOfSeat.setEnabled(true);

        line1.setEnabled(true);
        line2.setEnabled(true);
        city.setEnabled(true);
        state.setEnabled(true);
        pincode.setEnabled(true);
    }

    private void setEditTextNonEditable() {
        email.setEnabled(false);
        name.setEnabled(false);
//        phone.setEnabled(false);
        workspaceName.setEnabled(false);
        // numberOfSeat.setEnabled(false);

        line1.setEnabled(false);
        line2.setEnabled(false);
        city.setEnabled(false);
        state.setEnabled(false);
        pincode.setEnabled(false);
    }

    private void defineNonEditableViews(View v) {

        email = (EditText) v.findViewById(R.id.email);
        name = (EditText) v.findViewById(R.id.name);
        //phone = (EditText) v.findViewById(R.id.phone);
        workspaceName = (EditText) v.findViewById(R.id.workspace_name);
        //  numberOfSeat = (EditText) v.findViewById(R.id.number_of_seat);

        email.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getOwner().getEmail());
        name.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getOwner().getName());
//        phone.setText("");
        workspaceName.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getName());
        //      numberOfSeat.setText("");


        line1 = (EditText) v.findViewById(R.id.address1);
        line2 = (EditText) v.findViewById(R.id.address2);
        city = (EditText) v.findViewById(R.id.city);
        state = (EditText) v.findViewById(R.id.state);
        pincode = (EditText) v.findViewById(R.id.pincode);

        line1.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getAddress().getLine1());
        line2.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getAddress().getLocality());
        city.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getAddress().getCity());
        state.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getAddress().getState());
        pincode.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getAddress().getPincode().toString());

        setEditTextNonEditable();

    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.LOGOUT: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    Toast.makeText(getActivity(), "logout", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    SharedPrefsUtils.clearSharedPreferenceFile(getActivity());
                    break;
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
