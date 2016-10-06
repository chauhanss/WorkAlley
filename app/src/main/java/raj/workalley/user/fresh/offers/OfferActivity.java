package raj.workalley.user.fresh.offers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import raj.workalley.BaseActivity;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.socket.HostSocketService;
import raj.workalley.HomeActivity;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class OfferActivity extends BaseActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_offer);
        mContext = this;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ((TextView) findViewById(R.id.toolbar_title)).setText("Offers");

        OffersFragment offersFragment = OffersFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.container, offersFragment).commit();

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent intent = new Intent(OfferActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        startUserService();
    }

    private void startUserService() {
        Intent intent = new Intent(getBaseContext(), HostSocketService.class);
        Bundle b = new Bundle();
        b.putString(Constants.SESSION_COOKIES_ID, Session.getInstance(mContext).getSessionIdCookies());
        intent.putExtras(b);
        startService(intent);
    }
}
