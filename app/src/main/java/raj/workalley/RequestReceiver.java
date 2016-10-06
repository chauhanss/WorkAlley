package raj.workalley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by shruti.vig on 9/19/16.
 */
public class RequestReceiver extends BroadcastReceiver {
    private static final String USER = "user";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            Bundle bundle = intent.getBundleExtra("bundle");
            String requestType = bundle.getString(Constants.REQUEST_TYPE);

            if (requestType != null) {
                switch (requestType) {
                    case Constants.BOOKING_REJECT:
                    case Constants.BOOKING_ACCEPT:
                        try {
                            JSONObject userInfo = new JSONObject(bundle.getString(USER));
                            //       UserInfo user = (UserInfo) Session.getInstance(context).getParsedResponseFromGSON(userInfo, Session.workAlleyModels.UserInfo);

                            /**
                             * key : user ID, value : pipe separated requestType and workspace id
                             * user : user Info object saved with key requestType
                             */
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), bundle.getString(Constants.REQUEST_ID), Constants.SP_NAME);
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), requestType + "|" + bundle.getString("workspace_id"), Constants.SP_NAME);
                            //          SharedPrefsUtils.setStringPreference(context, Constants.BOOKING_REJECT, userInfo.toString(), Constants.SP_NAME);

                            sendBroadcastToActivity(context, requestType, bundle.getString(Constants.REQUEST_ID));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Constants.SESSION_END_CONFIRMED: {
                        try {
                            JSONObject userInfo = new JSONObject(bundle.getString(USER));

                            /**
                             * key : user ID, value : pipe separated requestType and workspace id
                             * user : user Info object saved with key requestType
                             */
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), bundle.getString(Constants.REQUEST_ID), Constants.SP_NAME);
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), requestType + "|" + bundle.getString("workspace_id"), Constants.SP_NAME);
                            sendBroadcastToActivity(context, requestType, bundle.getString(Constants.REQUEST_ID));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

            }
        }
    }

    static void sendBroadcastToActivity(Context context, String message, String userInfo) {

        Intent intent = new Intent(Constants.REQUEST_RESPONSE);
        intent.putExtra("message", message);
        intent.putExtra("USER", userInfo);
        context.sendBroadcast(intent);
    }
}
