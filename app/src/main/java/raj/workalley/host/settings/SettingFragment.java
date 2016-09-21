package raj.workalley.host.settings;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import raj.workalley.LoginActivity;
import raj.workalley.R;
import raj.workalley.Session;

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

        return v;
    }

    private void callHostLogoutApi() {
        mSession.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void callHostDetailsSaveApi() {
        mSession.saveHostDetails();
    }

    private void setEditTextEditable() {
        email.setEnabled(true);
        name.setEnabled(true);
        phone.setEnabled(true);
        workspaceName.setEnabled(true);
        numberOfSeat.setEnabled(true);

        line1.setEnabled(true);
        line2.setEnabled(true);
        city.setEnabled(true);
        state.setEnabled(true);
        pincode.setEnabled(true);
    }

    private void setEditTextNonEditable() {
        email.setEnabled(false);
        name.setEnabled(false);
        phone.setEnabled(false);
        workspaceName.setEnabled(false);
        numberOfSeat.setEnabled(false);

        line1.setEnabled(false);
        line2.setEnabled(false);
        city.setEnabled(false);
        state.setEnabled(false);
        pincode.setEnabled(false);
    }

    private void defineNonEditableViews(View v) {

        email = (EditText) v.findViewById(R.id.email);
        name = (EditText) v.findViewById(R.id.name);
        phone = (EditText) v.findViewById(R.id.phone);
        workspaceName = (EditText) v.findViewById(R.id.workspace_name);
        numberOfSeat = (EditText) v.findViewById(R.id.number_of_seat);

        email.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getOwner().getEmail());
        name.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getOwner().getName());
        phone.setText("");
        workspaceName.setText(mSession.getWorkspaces().getWorkspaceData().get(0).getName());
        numberOfSeat.setText("");


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


}
