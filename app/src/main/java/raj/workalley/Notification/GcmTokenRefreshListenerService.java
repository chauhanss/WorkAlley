package raj.workalley.Notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by shruti.vig on 4/14/16.
 */
public class GcmTokenRefreshListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        /*String storedGCMToken = CCSharedPrefsUtils.getStringPreference(mContext, CCConstants.GCM_TOKEN, CCConstants.SP_USER_NAME);
        if (storedGCMToken != null && !storedGCMToken.equalsIgnoreCase(gcmToken) && CCSession.getInstance(mContext).getToken() != null)
            sendRegistrationToServer(gcmToken);
        CCSharedPrefsUtils.setStringPreference(mContext, CCConstants.GCM_TOKEN, gcmToken, CCConstants.SP_USER_NAME);
        CCSession.getInstance(this).updateGcmApi(token);*/
    }
}
