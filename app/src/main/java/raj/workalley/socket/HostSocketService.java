package raj.workalley.socket;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.RequestReceiver;
import raj.workalley.Session;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/19/16.
 */
public class HostSocketService extends Service {

    private static final String USER = "user";
    private static final String REQUEST_TYPE = "requestType";
    int mStartMode;
    IBinder mBinder;
    boolean mAllowRebind;
    public Socket mSocket;
    static int count = 0;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {

    }

    public void initSocket() {
        IO.Options headers = new IO.Options();
        try {
            mSocket = IO.socket("http://app.workalley.in", headers);
        } catch (URISyntaxException e) {
        }
        mSocket.connect();
    }

    public void connectToServer() {
        mSocket.on("connected", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.e("here", "connected");
            }
        });
    }

    public void sendCookies(final String sessionCookiesId) {
        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("here", "ok");
                Transport transport = (Transport) args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>) args[0];
                        // modify request headers
                        headers.put("Cookie", sessionCookiesId);
                        Log.e("here", "ok1");
                    }
                });
            }
        });
    }

    public void authHost() {
        mSocket.on("AUTH", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //dialog goes here
                Log.e("here", "auth");
            }
        });
    }


    public static void openSocketForBookingRequest() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Bundle b = intent.getExtras();
            String cookieId = b.getString(Constants.SESSION_COOKIES_ID);
            SharedPrefsUtils.setStringPreference(this, Constants.SESSION_COOKIES_ID, cookieId, Constants.SP_NAME);
        }
        String cookieStr = SharedPrefsUtils.getStringPreference(this, Constants.SESSION_COOKIES_ID, Constants.SP_NAME);
        initSocket();
        connectToServer();
        sendCookies(cookieStr);
        authHost();
        mSocket.on("BOOKING_REQUESTED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                try {

                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    Intent i = new Intent(HostSocketService.this, RequestReceiver.class);

                    Bundle bundle = new Bundle();
                    if (jsonObject.has("user") && !jsonObject.isNull("user")) {

                        JSONObject userObject = (JSONObject) jsonObject.get("user");
                        bundle.putString(USER, userObject.toString());
                        bundle.putString(Constants.REQUEST_ID, jsonObject.get("_id").toString());
                        bundle.putString(REQUEST_TYPE, "BOOKING_REQUESTED");

                        if (userObject.has("name") && !userObject.isNull("name"))
                            createNotification(userObject.getString("name"), "xyz", "BOOKING_REQUESTED");
                        i.putExtra("bundle", bundle);
                        sendBroadcast(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mSocket.on("BOOKING_REJECTED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    Intent i = new Intent(HostSocketService.this, RequestReceiver.class);

                    Bundle bundle = new Bundle();
                    if (jsonObject.has("user") && !jsonObject.isNull("user")) {

                        JSONObject userObject = (JSONObject) jsonObject.get("user");
                        bundle.putString(USER, jsonObject.get("user").toString());
                        bundle.putString(REQUEST_TYPE, "BOOKING_REJECTED");
                        if (userObject.has("name") && !userObject.isNull("name"))
                            createNotification(userObject.getString("name"), "xyz", "BOOKING_REJECTED");
                        i.putExtra("bundle", bundle);
                        sendBroadcast(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        return START_STICKY;
    }

    public void createNotification(String userName, String workspace, String requestType) {

        String messageText = "";
        switch (requestType) {
            case "BOOKING_REQUESTED":
                messageText = userName + " has requested a seat in your workspace " + workspace + ".Please accept/reject their request";
                break;
            case "BOOKING_REJECTED":
                messageText = "Sorry " + userName + "! Your request for a seat in " + workspace + " has been rejected. Please try again later.";

        }
        Intent dismissIntent = new Intent(this, raj.workalley.LoginActivity.class);
        PendingIntent piDismiss = PendingIntent.getActivity(this, 0, dismissIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_account)
                        .setContentTitle(messageText)
                        .setContentIntent(piDismiss);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        inboxStyle.setBigContentTitle(messageText);
        mBuilder.setStyle(inboxStyle);


        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        Log.e("here", "service stopped");
        super.onDestroy();
        //     startService(new Intent(this, HostSocketService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
