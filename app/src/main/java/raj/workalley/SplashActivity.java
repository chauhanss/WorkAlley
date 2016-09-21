package raj.workalley;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import raj.workalley.host.HomeActivity;

/**
 * Created by vishal.raj on 9/20/16.
 */
public class SplashActivity extends BaseActivity {

    Session mSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSession = Session.getInstance(this);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSession.getUser() == null) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (mSession.getUser().getRole().equals(Constants.PROVIDER)) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, raj.workalley.user.fresh.HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);

    }
}
