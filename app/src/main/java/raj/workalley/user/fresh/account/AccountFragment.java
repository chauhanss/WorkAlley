package raj.workalley.user.fresh.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.PublicKey;

import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.user.fresh.UserInfo;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;
import raj.workalley.user.fresh.offers.OfferActivity;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class AccountFragment extends Fragment {

    private Context mContext;
    private Session mSession;
    private UserInfo mUser;
    private LinearLayout requestLayout;
    TextView endSession;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_account_fresh_user, null);
        v.findViewById(R.id.recharge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OfferActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUser = mSession.getUser();
        requestLayout = (LinearLayout) view.findViewById(R.id.request_seat_layout);

        endSession = (TextView) requestLayout.findViewById(R.id.end_session_btn);
        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected(mContext)) {
                    String bookingRequest = SharedPrefsUtils.getStringPreference(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                    Helper.showProgressDialogSpinner(mContext, "Please Wait", "Ending Session", false);
                    Session.getInstance(mContext).endSessionInWorkspaceRequest(bookingRequest);
                } else
                    Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase(Constants.SESSION_END_CONFIRMED)) {
                SharedPrefsUtils.removePreferenceByKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                getAllActiveSessionsOfUser(mUser.get_id());
            } else if (message.equalsIgnoreCase(Constants.BOOKING_ACCEPT)) {
                getAllActiveSessionsOfUser(mUser.get_id());
            }
        }

    };

    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mContext.unregisterReceiver(notificationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mContext.registerReceiver(notificationListener, new IntentFilter(Constants.REQUEST_RESPONSE));
        getAllActiveSessionsOfUser(mUser.get_id());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mSession = Session.getInstance(mContext);
    }

    public void getAllActiveSessionsOfUser(String userId) {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Fetching Requests", false);
            Session.getInstance(mContext).getAllActiveSessionsOfUser(userId);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.ACTIVE_SESSIONS: {
                Helper.dismissProgressDialog();

                if (event.getStatus()) {
                    try {
                        JSONObject object = (JSONObject) event.getValue();

                        JSONArray array = object.getJSONArray("data");

                        if (array.length() > 0) {
                            /**
                             * one user can have only one active session at one time
                             */
                            JSONObject jsonObject = (JSONObject) array.get(0);

                            if (jsonObject.has("space") && !jsonObject.isNull("space")) {
                                JSONObject spaceObject = jsonObject.getJSONObject("space");
                                requestLayout.setVisibility(View.VISIBLE);

                                TextView workspaceName = (TextView) requestLayout.findViewById(R.id.workspace_name);
                                workspaceName.setText(spaceObject.getString("name"));

                                JSONObject addressObject = spaceObject.getJSONObject("address");
                                TextView workspaceAddress = (TextView) requestLayout.findViewById(R.id.workspace_address);
                                workspaceAddress.setText(addressObject.getString("line1") + ", " + addressObject.getString("locality") + ", " + addressObject.getString("state") + ", " + addressObject.getString("city") + ", " + addressObject.getString("pincode"));

                                if (!SharedPrefsUtils.hasKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME)) {
                                    endSession.setVisibility(View.GONE);
                                    requestLayout.findViewById(R.id.status).setVisibility(View.VISIBLE);
                                }
                            }
                        } else
                            requestLayout.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(mContext, "Some error occurred!", Toast.LENGTH_LONG).show();

            }
            break;
            case CobbocEvent.END_SESSION: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    Toast.makeText(mContext, "Session end request submitted!", Toast.LENGTH_LONG).show();
                    //          SharedPrefsUtils.removePreferenceByKey(mContext, Constants.BOOKING_REQUEST_ID, Constants.SP_NAME);
                    endSession.setVisibility(View.GONE);
                    requestLayout.findViewById(R.id.status).setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(mContext, "Some error occurred!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mUser != null) {
            getAllActiveSessionsOfUser(mUser.get_id());
        }
    }
}

