package raj.workalley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;


import raj.workalley.host.HomeActivity;
import raj.workalley.host.signup.HostSignUpActivity;

import raj.workalley.user.fresh.UserInfo;

import raj.workalley.user.fresh.offers.OfferActivity;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 9/2/16.
 */
public class SignUpActivity extends BaseActivity {

    private LinearLayout userSignUpLayout, hostSignUpLayout;
    private Context mContext;
    private TabHost tabHost;
    private EditText mEmailUser, mNameUser, mPasswordUser, mConfirmPasswordUser;
    private EditText mEmailHost, mNameHost, mPasswordHost, mConfirmPasswordHost;
    private Button mSignUpUser, mSignUpHost;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;
        mSession = Session.getInstance(mContext);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        userSignUpLayout = (LinearLayout) findViewById(R.id.user_sign_up);
        hostSignUpLayout = (LinearLayout) findViewById(R.id.host_sign_up);
        mEmailHost = (EditText) hostSignUpLayout.findViewById(R.id.email);
        mNameHost = (EditText) hostSignUpLayout.findViewById(R.id.name);
        mPasswordHost = (EditText) hostSignUpLayout.findViewById(R.id.password);
        mConfirmPasswordHost = (EditText) hostSignUpLayout.findViewById(R.id.confirmPassword);

        mSignUpHost = (Button) findViewById(R.id.hostSignup);
        mEmailUser = (EditText) userSignUpLayout.findViewById(R.id.email);
        mNameUser = (EditText) userSignUpLayout.findViewById(R.id.name);
        mPasswordUser = (EditText) userSignUpLayout.findViewById(R.id.password);
        mConfirmPasswordUser = (EditText) userSignUpLayout.findViewById(R.id.confirmPassword);

        mSignUpUser = (Button) userSignUpLayout.findViewById(R.id.userSignup);

        mSignUpUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFieldDataEmpty(false) && mPasswordUser.getText().toString().trim().equals(mConfirmPasswordUser.getText().toString().trim()))
                    proceedForSignUp(false);
                else
                    Toast.makeText(mContext, "Please check your details", Toast.LENGTH_SHORT).show();
            }
        });
        mSignUpHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFieldDataEmpty(true) && mPasswordHost.getText().toString().trim().equals(mConfirmPasswordHost.getText().toString().trim()))
                    proceedForSignUp(true);
                else
                    Toast.makeText(mContext, "Please check your details", Toast.LENGTH_SHORT).show();
           /*     Intent intent = new Intent(SignUpActivity.this, HostSignUpActivity.class);
                startActivity(intent);  */
            }
        });

        tabHost.findViewById(R.id.tabHost);
        tabHost.setup();
        final TabHost.TabSpec user = tabHost.newTabSpec("start");
        user.setContent(R.id.user_sign_up);
        user.setIndicator(getString(R.string.user));

        final TabHost.TabSpec host = tabHost.newTabSpec("end");
        host.setContent(R.id.host_sign_up);
        host.setIndicator(getString(R.string.host));

        tabHost.addTab(user);
        tabHost.addTab(host);
    }

    private boolean isFieldDataEmpty(boolean isHostSignUp) {

        if (isHostSignUp) {
            if (mEmailHost.getText().toString().isEmpty() || mNameHost.getText().toString().isEmpty() || mPasswordHost.getText().toString().isEmpty() || mConfirmPasswordHost.getText().toString().isEmpty()) {
                return true;
            } else
                return false;
        } else {
            if (mEmailUser.getText().toString().isEmpty() || mNameUser.getText().toString().isEmpty() || mPasswordUser.getText().toString().isEmpty() || mConfirmPasswordUser.getText().toString().isEmpty()) {
                return true;
            } else
                return false;
        }
    }

    private boolean isEmailFormat(String userName) {
        if (!Constants.EMAIL_PATTERN.matcher(userName).matches()) {
            Toast.makeText(mContext, "Please enter valid email", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    private void proceedForSignUp(boolean isHostSignUp) {

        String email, name, password;
        if (isHostSignUp) {
            email = mEmailHost.getText().toString().trim();
            name = mNameHost.getText().toString().trim();
            password = mPasswordHost.getText().toString().trim();
        } else {
            email = mEmailUser.getText().toString().trim();
            name = mNameUser.getText().toString().trim();
            password = mPasswordUser.getText().toString().trim();
        }

        if (isEmailFormat(email) && Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
            mSession.signUpApi(email, name, password, isHostSignUp);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();

    }

    private void makeLoginCall(boolean isHost) {

        Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);

        if (isHost)
            mSession.login(mEmailHost.getText().toString().trim(), mPasswordHost.getText().toString().trim());
        else
            mSession.login(mEmailUser.getText().toString().trim(), mPasswordUser.getText().toString().trim());
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


    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.SIGNUP:
                if (event.getStatus()) {
                    boolean isHost = false;
                    JSONObject response = (JSONObject) event.getValue();
                    if (response != null && response.has("isProvider") && !response.isNull("isProvider")) {
                        try {
                            isHost = response.getBoolean("isProvider");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    makeLoginCall(isHost);
                } else {
                    Helper.dismissProgressDialog();
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                }
                break;
            case CobbocEvent.LOGIN:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();
                    UserInfo parsedResponse = (UserInfo) Session.getInstance(mContext).getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.UserInfo);
                    mSession.setUser(parsedResponse);


                    if (mSession.getUser().getRole().equalsIgnoreCase(Constants.USER)) {
                        mSession.getUserWorkspaceData(mSession.getUser().get_id());
                        Intent intent = new Intent(SignUpActivity.this, raj.workalley.user.fresh.HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } /*else if (mSession.getUser().getRole().equalsIgnoreCase(Constants.PROVIDER)) {
                        mSession.getHostWorkspaceData(mSession.getUser().get_id());
                    }*/

                } else {
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                }
                break;
            case CobbocEvent.GET_HOST_DETAILS:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {

                    JSONObject jsonObject = (JSONObject) event.getValue();
                    try {
                        if (jsonObject.getJSONArray(Constants.DATA).length() == 0) {
                            Intent intent = new Intent(SignUpActivity.this, HostSignUpActivity.class);
                            startActivity(intent);
                        } else {
                            WorkspaceList parsedResponse = (WorkspaceList) Session.getInstance(mContext).getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.Workspaces);
                            Session.getInstance(mContext).setWorkspaces(parsedResponse);
                            Intent intent = new Intent(SignUpActivity.this, raj.workalley.host.HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                break;
        }
    }


}
