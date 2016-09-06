package raj.workalley;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vishal.raj on 9/2/16.
 */
public class SignUpActivity extends BaseActivity {

    private LinearLayout userSignUpLayout, hostSignUpLayout;
    private Context mContext;
    private TabHost tabHost;
    private EditText mEmailUser, mNameUser, mPasswordUser, mConfirmPasswordUser;
    private EditText mEmailHost, mNameHost, mPasswordHost, mConfirmPasswordHost;
    private Button mSignUpUser, mSignUpHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;
        tabHost = (TabHost) findViewById(R.id.tabHost);
        userSignUpLayout = (LinearLayout) findViewById(R.id.user_sign_up);
        hostSignUpLayout = (LinearLayout) findViewById(R.id.host_sign_up);
        mEmailUser = (EditText) userSignUpLayout.findViewById(R.id.email);

        mNameUser = (EditText) userSignUpLayout.findViewById(R.id.name);
        mPasswordUser = (EditText) userSignUpLayout.findViewById(R.id.password);
        mConfirmPasswordUser = (EditText) userSignUpLayout.findViewById(R.id.confirmPassword);
        mSignUpUser = (Button) userSignUpLayout.findViewById(R.id.userSignup);
        mSignUpUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordUser.getText().toString().trim().equals(mConfirmPasswordUser.getText().toString().trim()))
                    proceedForUserSignUp();
                else
                    Toast.makeText(mContext, "Confirm  password field didn't match with Password field", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void proceedForUserSignUp() {
        String email = mEmailUser.getText().toString().trim();
        String name = mNameUser.getText().toString().trim();
        String password = mPasswordUser.getText().toString().trim();
        Session.getInstance(this).signUpUserApi(email, name, password);
    }
}
