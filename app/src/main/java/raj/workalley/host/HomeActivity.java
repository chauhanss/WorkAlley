package raj.workalley.host;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.cleveroad.loopbar.widget.Orientation;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.BaseActivity;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.host.dashboard.DashboardFragment;
import raj.workalley.host.settings.SettingFragment;
import raj.workalley.host.user_request.UserRequestFragment;
import raj.workalley.socket.HostSocketService;


/**
 * Created by vishal.raj on 9/5/16.
 */
public class HomeActivity extends BaseActivity implements OnItemClickListener {

    private SimpleFragmentStatePagerAdapter pagerAdapter;
    private IO.Options headers = new IO.Options();
    Session session;

    private ViewPager viewPager;

    private Socket mSocket;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //JSONObject data = (JSONObject) args[0];
                    Toast.makeText(getApplicationContext(), "connected" + " " + "enjoy", Toast.LENGTH_LONG).show();
                }
            });
        }
    };


    @Orientation
    private int orientation;
    @LoopBarView.GravityAttr
    private int endlessGravity = LoopBarView.SELECTION_GRAVITY_START;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_host);
        session = Session.getInstance(this);
        initNavToolBar();
        startHostService();

       /* try {
            mSocket = IO.socket("http://app.workalley.in", headers);
        } catch (URISyntaxException e) {
        }

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
                        headers.put("Cookie", session.getSessionIdCookies());
                        Log.e("here", "ok1");
                    }
                });
            }
        });

        // mSocket.on("new message", onNewMessage);

        mSocket.on("connected", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Map<String, String> headers = (Map<String, String>) args[0];
                        // modify request headers
                        //headers.put("Cookie", session.getSessionIdCookies());
                        Log.e("here", "connected");
                    }
                });
            }
        });

        mSocket.on("AUTH", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //dialog goes here
                        Log.e("here", "auth");
                        Toast.makeText(getApplicationContext(), "auth" + " " + "enjoy", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mSocket.on("BOOKING_REQUESTED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //dialog goes here
                        Log.e("here", "BOOKING_REQUESTED");
                        Toast.makeText(getApplicationContext(), "BOOKING_REQUESTED" + " " + "enjoy", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });*/
       /* mSocket.on("AUTH", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Ack ack = (Ack) args[args.length - 1];
                Log.e("received", "enjoy");
                ack.call();
            }
        });

        // mSocket.on("CONNECTED", onNewMessage);
        //mSocket.on("AUTH", onNewMessage);
        mSocket.on("BOOKING_REQUESTED", onNewMessage);
        mSocket.on("BOOKING_ACCEPTED", onNewMessage);
        mSocket.on("BOOKING_REJECTED", onNewMessage);
        mSocket.on("BOOKING_CANCELED", onNewMessage);
        mSocket.on("BOOKING_END_REQUESTED", onNewMessage);
        mSocket.on("BOOKING_END_CONFIRMED", onNewMessage);*/
        // mSocket.connect();

    }

    private void startHostService() {
        /*HostSocketService.initSocket();
        HostSocketService.connectToServer();
        HostSocketService.setSessionCookiesId(session.getSessionIdCookies());
        HostSocketService.sendCookies();
        HostSocketService.authHost();*/
        Intent intent = new Intent(getBaseContext(), HostSocketService.class);
        Bundle b = new Bundle();
        b.putString(Constants.SESSION_COOKIES_ID, session.getSessionIdCookies());
        intent.putExtras(b);
        startService(intent);
    }

    @Override
    public void onItemClicked(int position) {
    }

    private void initNavToolBar() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);

        List<Fragment> list = new ArrayList<>(3);
        list.add(SettingFragment.newInstance());
        list.add(DashboardFragment.newInstance());
        list.add(UserRequestFragment.newInstance());
        pagerAdapter = new SimpleFragmentStatePagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(pagerAdapter);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_setting),
                        Color.parseColor("#EE946F")
                ).build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_account),
                        Color.parseColor("#EE946F")
                ).build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_calendar),
                        Color.parseColor("#EE946F")
                ).build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        viewPager.addOnPageChangeListener(new AbstractPageChangedListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("tag", "on page scrolled");
            }
        });
    }


    public final class SimpleFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;

        public SimpleFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    public abstract class AbstractPageChangedListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


}
