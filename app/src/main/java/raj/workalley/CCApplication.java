package raj.workalley;

import android.app.Application;
import android.util.Log;

import com.firebase.client.Firebase;


public class CCApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Sagar", "Entered in Application");
        Firebase.setAndroidContext(getApplicationContext());

    }


}
