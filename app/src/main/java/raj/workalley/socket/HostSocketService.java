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

import java.net.URISyntaxException;
import java.util.Map;

import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/19/16.
 */
public class HostSocketService extends Service {

    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */
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
        Log.e("here", "service started");
        initSocket();
        connectToServer();
        sendCookies(cookieStr);
        Log.e("here", ""+cookieStr);
        authHost();
        //Log.e("")
        mSocket.on("BOOKING_REQUESTED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //dialog goes here
                Log.e("here", "BOOKING_REQUESTED");
                createNoti(++count);
                //Toast.makeText(getApplicationContext(), "BOOKING_REQUESTED" + " " + "enjoy", Toast.LENGTH_LONG).show();
            }
        });
        return START_STICKY;
    }

    public void createNoti(int count) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_account)
                        .setContentTitle("My notification " + count)
                        .setContentText("Hello World!");

        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        Log.e("here", "service stopped");
        //Log.e(LOG_TAG, "------------------------------------------ Destroyed Location update Service");
        //cleanUp();
        super.onDestroy();
        // startService(new Intent(this, HostSocketService.class)); // add this line
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
