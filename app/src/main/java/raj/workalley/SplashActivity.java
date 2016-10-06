package raj.workalley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
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
 * Created by vishal.raj on 9/20/16.
 */
public class SplashActivity extends BaseActivity {

    Session mSession;
    Context mContext;
    Button retry;

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
                if (!Helper.isConnected(mContext)) {
                    notGoodToGo();
                } else {
                    goodToGo();
                }
            }
        }, 1000);

    }

    public void notGoodToGo() {
        setContentView(R.layout.no_internet);
        retry = (Button) findViewById(R.id.no_net_retry_button);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected(mContext))
                    goodToGo();
            }
        });
    }

    public void goodToGo() {
        setContentView(R.layout.activity_splash);
        if (!mSession.isLoggedIn()) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Connecting to server", false);
            mSession.login(mSession.getUserEmail(), mSession.getUserPassword(), -1);
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
                        mSession.getUserWorkspaceData(mSession.getUser().get_id(),-1);

                    } else {
                        startActivity(new Intent(this, LoginActivity.class));
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

                            //     SharedPrefsUtils.setStringPreference(mContext, Constants.BOOKING_REQUEST_ID, dataObject.getString("_id"), Constants.SP_NAME);

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
