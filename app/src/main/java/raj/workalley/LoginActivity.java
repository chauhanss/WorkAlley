package raj.workalley;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import raj.workalley.host.HomeActivity;
import raj.workalley.user.fresh.offers.OfferActivity;

/**
 * Created by vishal.raj on 9/2/16.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.create_new_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OfferActivity.class);
                startActivity(intent);
            }
        });
    }
}
