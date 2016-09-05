package raj.workalley;

import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by vishal.raj on 9/2/16.
 */
public class SignUpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);



        tabHost.findViewById(R.id.tabHost);
        tabHost.setup();
        final TabHost.TabSpec user = tabHost.newTabSpec("start");
        user.setContent(R.id.user_sign_up);
        user.setIndicator(getString(R.string.user));

        final TabHost.TabSpec host = tabHost.newTabSpec("end");
        host.setContent(R.id.host_sign_up);
        host.setIndicator(getString(R.string.host));

        tabHost.addTab(user);
        tabHost.addTab(host);
    }
}
