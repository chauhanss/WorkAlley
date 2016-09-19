package raj.workalley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import raj.workalley.user.fresh.UserInfo;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by shruti.vig on 9/19/16.
 */
public class RequestReceiver extends BroadcastReceiver {
    private static final String USER = "user";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SHRUTI", "Received");

        if (intent != null) {

            Bundle bundle = intent.getBundleExtra("bundle");
            String requestType = bundle.getString(Constants.REQUEST_TYPE);

            if (requestType != null) {
                switch (requestType) {
                    case Constants.BOOKING_REQUEST:
                        try {
                            JSONObject userInfo = new JSONObject(bundle.getString(USER));
                            //       UserInfo user = (UserInfo) Session.getInstance(context).getParsedResponseFromGSON(userInfo, Session.workAlleyModels.UserInfo);

                            Set<String> requestSet = new HashSet<>();
                            requestSet.add(userInfo.toString());
                            SharedPrefsUtils.setHashSetPreference(context, Constants.BOOKING_REQUEST, requestSet, Constants.SP_NAME);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }

            }
        }


    }
}
