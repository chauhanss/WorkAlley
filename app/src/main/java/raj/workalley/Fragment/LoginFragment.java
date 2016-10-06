package raj.workalley.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import raj.workalley.util.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.SignUpActivity;
import raj.workalley.HomeActivity;
import raj.workalley.Model.UserInfo;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 10/4/16.
 */
public class LoginFragment extends BaseFragment {

    private Button mLogin;
    private TextView mCreateNewAccount;
    private Context mContext;
    private EditText mPassword;
    private EditText mUserName;
    private Session mSession;


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(150, 250, 150, 0);

        loginLayout.setLayoutParams(layoutParams);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_login, null);
        view.findViewById(R.id.wrapper_image).setVisibility(View.GONE);
        view.findViewById(R.id.skip_login).setVisibility(View.GONE);
        mContext = getActivity();
        mSession = Session.getInstance(mContext);

        initializeLayouts(view);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailFormat(mUserName)) {
                    if (Helper.isConnected(mContext)) {
                        makeLoginCall();
                    } else
                        Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
            }
        });


        mPassword.addTextChangedListener(new FieldTextWatcher());
        mUserName.addTextChangedListener(new FieldTextWatcher());

        return view;
    }

    private void makeLoginCall() {
        Helper.showProgressDialogSpinner(getActivity(), "Please Wait", "Connecting to server", false);
        mSession.login(mUserName.getText().toString().trim(), mPassword.getText().toString().trim(), 3);
    }

    private void initializeLayouts(View view) {
        mLogin = (Button) view.findViewById(R.id.login_button);
        mCreateNewAccount = (TextView) view.findViewById(R.id.create_new_account);
        mPassword = (EditText) view.findViewById(R.id.password);
        mUserName = (EditText) view.findViewById(R.id.user_name);
    }

    private boolean isEmailFormat(EditText userName) {
        if (!Constants.EMAIL_PATTERN.matcher(userName.getText().toString()).matches()) {
            Toast.makeText(mContext, "Please enter valid email", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    class FieldTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isUserPassEmpty(mUserName, mPassword)) {
                mLogin.setEnabled(true);
            } else
                mLogin.setEnabled(false);
        }
    }

    private boolean isUserPassEmpty(EditText userName, EditText password) {
        if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            return true;
        } else
            return false;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.LOGIN:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {

                    JSONObject jsonObject = (JSONObject) event.getValue();
                    UserInfo parsedResponse = (UserInfo) mSession.getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.UserInfo);
                    mSession.setUser(parsedResponse);

                    if (mSession.getUser().getRole().equalsIgnoreCase(Constants.USER)) {
                        mSession.getUserWorkspaceData(mSession.getUser().get_id(), 3);
                    }
                } else {
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                }
                break;
            case CobbocEvent.GET_USER_DETAILS: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    try {
                        JSONObject jsonObject = (JSONObject) event.getValue();
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                            JSONArray dataArray = (JSONArray) jsonObject.getJSONArray("data");
                            JSONObject dataObject = (JSONObject) dataArray.get(0);

                            if (dataObject.has("space") && !dataObject.isNull("space")) {
                                JSONObject space = (JSONObject) dataObject.getJSONObject("space");
                                mSession.setActiveWorkspace(space.getString("_id"));
                                mSession.setActiveWorkspaceRequestId(dataObject.getString("_id"));
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    mSession.setActiveWorkspace(null);

                ((HomeActivity) getActivity()).recreateThis();

            }
            break;
        }
    }
}
