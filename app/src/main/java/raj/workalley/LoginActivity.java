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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import raj.workalley.host.HomeActivity;
import raj.workalley.host.signup.HostSignUpActivity;

import raj.workalley.user.fresh.UserInfo;

import raj.workalley.user.fresh.offers.OfferActivity;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

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
        mSession = Session.getInstance(mContext);

        if (mSession.isLoggedIn()) {

        }

        setContentView(R.layout.activity_login);
        mContext = this;


        initializeLayouts();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
      /*              } else if (mSession.getUser().getRole().equalsIgnoreCase(Constants.PROVIDER)) {
                        Intent intent = new Intent(LoginActivity.this, raj.workalley.user.fresh.HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } /*else if (mSession.getUser().getRole().equalsIgnoreCase(Constants.PROVIDER)) {
                        Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
                        mSession.getHostWorkspaceData(mSession.getUser().get_id());
                       *//* mSession.getHostWorkspaceData(mSession.getUser().get_id());
                        Intent intent = new Intent(LoginActivity.this, HostSignUpActivity.class);
                        startActivity(intent);
                       // finish();*//*
                    }*/
                    } else {
                        Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                    }
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

                            SharedPrefsUtils.setStringPreference(mContext, Constants.BOOKING_REQUEST_ID, dataObject.getString("_id"), Constants.SP_NAME);

                            if (dataObject.has("space") && !dataObject.isNull("space")) {
                                JSONObject space = (JSONObject) dataObject.getJSONObject("space");
                                mSession.setActiveWorkspace(space.getString("_id"));
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    mSession.setActiveWorkspace(null);
                Intent intent = new Intent(LoginActivity.this, raj.workalley.user.fresh.HomeActivity.class);
                startActivity(intent);
                finish();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}