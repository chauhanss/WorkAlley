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
import raj.workalley.user.fresh.host_details.HostDetailsActivity;
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

                            Set<String> requestSet = new HashSet<>();
                            requestSet.add(userInfo.toString());

                            /**
                             * Mapped request id with user id as the key.
                             * This can be done because host will receive only one request from one user.
                             * Request can be retrieved later using that user's id.
                             */
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), bundle.getString(Constants.REQUEST_ID), Constants.SP_NAME);
                            SharedPrefsUtils.setHashSetPreference(context, Constants.BOOKING_REQUEST, requestSet, Constants.SP_NAME);
                            sendBroadcastToActivity(context, requestType, null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Constants.BOOKING_REJECT:
                    case Constants.BOOKING_ACCEPT:
                        try {
                            JSONObject userInfo = new JSONObject(bundle.getString(USER));
                            //       UserInfo user = (UserInfo) Session.getInstance(context).getParsedResponseFromGSON(userInfo, Session.workAlleyModels.UserInfo);

                            /**
                             * key : user ID, value : pipe separated requestType and workspace id
                             * user : user Info object saved with key requestType
                             */
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), requestType + "|" + bundle.getString("workspace_id"), Constants.SP_NAME);
                            //          SharedPrefsUtils.setStringPreference(context, Constants.BOOKING_REJECT, userInfo.toString(), Constants.SP_NAME);

                            sendBroadcastToActivity(context, requestType, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;

                    case Constants.BOOKING_CANCELED: {
                        JSONObject userInfo = null;
                        try {
                            userInfo = new JSONObject(bundle.getString(USER));
                            sendBroadcastToActivity(context, requestType, userInfo.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                    case Constants.SESSION_END_REQUEST: {
                        JSONObject userInfo = null;
                        try {
                            userInfo = new JSONObject(bundle.getString(USER));

                            Set<String> requestSet = new HashSet<>();
                            requestSet.add(userInfo.toString());

                            SharedPrefsUtils.setHashSetPreference(context, Constants.SESSION_END_REQUEST, requestSet, Constants.SP_NAME);
                            sendBroadcastToActivity(context, requestType, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case Constants.SESSION_END_CONFIRMED: {
                        try {
                            JSONObject userInfo = new JSONObject(bundle.getString(USER));

                            /**
                             * key : user ID, value : pipe separated requestType and workspace id
                             * user : user Info object saved with key requestType
                             */
                            SharedPrefsUtils.setStringPreference(context, userInfo.getString("_id"), requestType + "|" + bundle.getString("workspace_id"), Constants.SP_NAME);
                            sendBroadcastToActivity(context, requestType, null);
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
