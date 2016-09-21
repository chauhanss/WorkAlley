package raj.workalley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
public class LoginActivity extends BaseActivity {
    private Button mLogin;
    private TextView mCreateNewAccount;
    private Context mContext;
    private EditText mPassword;
    private EditText mUserName;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mSession = Session.getInstance(mContext);

        initializeLayouts();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   if (mUserName.getText().toString().equalsIgnoreCase("user")) {
                    Intent intent = new Intent(LoginActivity.this, raj.workalley.user.fresh.HomeActivity.class);
                    startActivity(intent);
                } else if (mUserName.getText().toString().equalsIgnoreCase("host")) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }  */
                if (isEmailFormat(mUserName) && Helper.isConnected(mContext)) {
                    makeLoginCall();
                } else
                    Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
            }
        });

        mCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        mPassword.addTextChangedListener(new FieldTextWatcher());
        mUserName.addTextChangedListener(new FieldTextWatcher());

    }

    private void makeLoginCall() {
        Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
        mSession.login(mUserName.getText().toString().trim(), mPassword.getText().toString().trim());
    }

    private void initializeLayouts() {
        mLogin = (Button) findViewById(R.id.login_button);
        mCreateNewAccount = (TextView) findViewById(R.id.create_new_account);
        mPassword = (EditText) findViewById(R.id.password);
        mUserName = (EditText) findViewById(R.id.user_name);
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
            case CobbocEvent.LOGIN:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {

                    JSONObject jsonObject = (JSONObject) event.getValue();
                    UserInfo parsedResponse = (UserInfo) mSession.getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.UserInfo);
                    mSession.setUser(parsedResponse);

                    if (mSession.getUser().getRole().equalsIgnoreCase(Constants.USER)) {
                        mSession.getUserWorkspaceData(mSession.getUser().get_id());
                        Intent intent = new Intent(LoginActivity.this, raj.workalley.user.fresh.HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (mSession.getUser().getRole().equalsIgnoreCase(Constants.PROVIDER)) {
                        Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
                        mSession.getHostWorkspaceData(mSession.getUser().get_id());
                       /* mSession.getHostWorkspaceData(mSession.getUser().get_id());
                        Intent intent = new Intent(LoginActivity.this, HostSignUpActivity.class);
                        startActivity(intent);
                       // finish();*/
                    }
                } else {
                    Toast.makeText(mContext, "Not able to login. Please check your details.", Toast.LENGTH_LONG).show();
                }
                break;
            case CobbocEvent.GET_USER_DETAILS:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    Toast.makeText(mContext, "Details fetched successfully.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Details not fetched.", Toast.LENGTH_LONG).show();
                }
                break;
            case CobbocEvent.GET_HOST_DETAILS:
                Helper.dismissProgressDialog();
                if (event.getStatus()) {

                    JSONObject jsonObject = (JSONObject) event.getValue();
                    try {
                        if (jsonObject.getJSONArray(Constants.DATA).length() == 0) {
                            Intent intent = new Intent(LoginActivity.this, HostSignUpActivity.class);
                            startActivity(intent);
                        } else {
                            WorkspaceList parsedResponse = (WorkspaceList) Session.getInstance(mContext).getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.Workspaces);
                            Session.getInstance(mContext).setWorkspaces(parsedResponse);
                            Intent intent = new Intent(LoginActivity.this, raj.workalley.host.HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}