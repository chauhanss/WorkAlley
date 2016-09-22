package raj.workalley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import raj.workalley.host.HomeActivity;
import raj.workalley.host.signup.HostSignUpActivity;
import raj.workalley.user.fresh.UserInfo;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/20/16.
 */
public class SplashActivity extends BaseActivity {

    Session mSession;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSession = Session.getInstance(this);
        mContext = this;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mSession.isLoggedIn()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
                    mSession.login(mSession.getUserEmail(), mSession.getUserPassword());
                }
            }
        }, 1000);

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
                        Intent intent = new Intent(SplashActivity.this, raj.workalley.user.fresh.HomeActivity.class);
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
                            Intent intent = new Intent(SplashActivity.this, HostSignUpActivity.class);
                            startActivity(intent);
                        } else {
                            WorkspaceList parsedResponse = (WorkspaceList) Session.getInstance(mContext).getParsedResponseFromGSON(jsonObject, Session.workAlleyModels.Workspaces);
                            Session.getInstance(mContext).setWorkspaces(parsedResponse);
                            Intent intent = new Intent(SplashActivity.this, raj.workalley.host.HomeActivity.class);
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
}
