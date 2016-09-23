package raj.workalley.user.fresh.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import raj.workalley.CobbocEvent;
import raj.workalley.LoginActivity;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class SettingFragment extends Fragment {
    Session mSession;
    EditText name, phone, email;
    boolean editMode = false;
    private Context mContext;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_setting_fresh_user, null);
        mSession = Session.getInstance(getActivity());
        defineNonEditableViews(v);
        final Button save_n_logout_btn = (Button) v.findViewById(R.id.logout);

        save_n_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Helper.isConnected(mContext)) {
                    Helper.showProgressDialogSpinner(mContext, "Please wait", "Connecting server", false);
                    if (!editMode) {
                        mSession.logout();
                    } else
                        mSession.saveUserDetails();
                } else
                    Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();

            }
        });

        v.findViewById(R.id.edit_mode).setOnClickListener(new View.OnClickListener() {
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


        return v;
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

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.LOGOUT: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    Toast.makeText(getActivity(), "logout", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
        //            SharedPrefsUtils.clearSharedPreferenceFile(getActivity());
                    break;
                }
            }
        }
    }

    private void setEditTextEditable() {
        email.setEnabled(true);
        name.setEnabled(true);
        //phone.setEnabled(true);
    }

    private void setEditTextNonEditable() {
        email.setEnabled(false);
        name.setEnabled(false);
        //phone.setEnabled(false);
    }

    private void defineNonEditableViews(View v) {
        email = (EditText) v.findViewById(R.id.user_email);
        name = (EditText) v.findViewById(R.id.user_name);
        //phone = (EditText) v.findViewById(R.id.user_phone);

        email.setText(mSession.getUser().getEmail());
        name.setText(mSession.getUser().getName());
        //phone.setText("");

        setEditTextNonEditable();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
