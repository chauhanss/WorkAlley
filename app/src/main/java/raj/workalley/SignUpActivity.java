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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import raj.workalley.Model.UserInfo;

import raj.workalley.util.CobbocEvent;
import raj.workalley.util.Helper;

/**
 * Created by vishal.raj on 9/2/16.
 */
public class SignUpActivity extends BaseActivity {

    private LinearLayout userSignUpLayout;
    private Context mContext;
    private EditText mEmailUser, mNameUser, mPasswordUser, mConfirmPasswordUser;
    private Button mSignUpUser;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;
        mSession = Session.getInstance(mContext);

        userSignUpLayout = (LinearLayout) findViewById(R.id.user_sign_up);

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
    }

    private boolean isFieldDataEmpty(boolean isHostSignUp) {

        if (mEmailUser.getText().toString().isEmpty() || mNameUser.getText().toString().isEmpty() || mPasswordUser.getText().toString().isEmpty() || mConfirmPasswordUser.getText().toString().isEmpty()) {
            return true;
        } else
            return false;
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
        email = mEmailUser.getText().toString().trim();
        name = mNameUser.getText().toString().trim();
        password = mPasswordUser.getText().toString().trim();


        if (isEmailFormat(email) && Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
            mSession.signUpApi(email, name, password, isHostSignUp);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();

    }

    private void makeLoginCall(boolean isHost) {

        Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);

        mSession.login(mEmailUser.getText().toString().trim(), mPasswordUser.getText().toString().trim(), -1);
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
                        mSession.getUserWorkspaceData(mSession.getUser().get_id(),-1);
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

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            break;


        }
    }

}
