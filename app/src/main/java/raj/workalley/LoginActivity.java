package raj.workalley;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import raj.workalley.user.fresh.offers.OfferActivity;

/**
 * Created by vishal.raj on 9/2/16.
 */
public class LoginActivity extends BaseActivity {
    private Button mLogin;
    private TextView mCreateNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLogin = (Button) findViewById(R.id.login_button);
        mCreateNewAccount = (TextView) findViewById(R.id.create_new_account);
        mCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OfferActivity.class);
                startActivity(intent);
            }
        });
    }
}
