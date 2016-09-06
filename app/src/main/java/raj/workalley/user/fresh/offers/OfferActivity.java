package raj.workalley.user.fresh.offers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import raj.workalley.BaseActivity;
import raj.workalley.R;
import raj.workalley.user.fresh.HomeActivity;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class OfferActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_offer);

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
    }
}
