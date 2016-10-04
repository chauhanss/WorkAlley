package raj.workalley.user.fresh.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;

import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.StringAdapter;
import raj.workalley.UserRequestAdapter;
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
    TextView endSession;
    private RecyclerView sessionListView;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        sessionListView = (RecyclerView) view.findViewById(R.id.session_list);
        getAllActiveSessionsOfUser(mUser.get_id());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllActiveSessionsOfUser(mUser.get_id());
            }
        });

    }

    public void endSession(String requestId) {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Ending Session", false);
            Session.getInstance(mContext).endSessionInWorkspaceRequest(requestId);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    public void cancelBooking(String requestId) {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please wait", "Cancelling request", false);
            mSession.cancelRequestedSeat(requestId);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase(Constants.SESSION_END_CONFIRMED)) {
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
                swipeRefreshLayout.setRefreshing(false);
                if (event.getStatus()) {
                    try {
                        JSONObject object = (JSONObject) event.getValue();

                        JSONArray array = object.getJSONArray("data");

                        ArrayList<String> adapterData = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);


                            if (jsonObject.has("space") && !jsonObject.isNull("space")) {
                                JSONObject spaceObject = jsonObject.getJSONObject("space");


                                JSONObject addressObject = spaceObject.getJSONObject("address");
                                String address = addressObject.getString("line1") + ", " + addressObject.getString("locality") + ", " + addressObject.getString("state") + ", " + addressObject.getString("city") + ", " + addressObject.getString("pincode");

                                //                 if (jsonObject.getString("status").equalsIgnoreCase("requested") || jsonObject.getString("status").equalsIgnoreCase("started"))
                                adapterData.add(spaceObject.getString("name") + "|" + address + "|" + jsonObject.getString("status") + "|" + jsonObject.get("updatedAt") + "|" + jsonObject.getString("_id"));
                            }
                        }

                        setUpRecyclerView(adapterData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();

            }
            break;
            case CobbocEvent.CANCEL_BOOKING_REQUEST: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    getAllActiveSessionsOfUser(mUser.get_id());
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
            break;
            case CobbocEvent.END_SESSION: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    Toast.makeText(mContext, "Session end request submitted!", Toast.LENGTH_LONG).show();
                    getAllActiveSessionsOfUser(mUser.get_id());
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpRecyclerView(ArrayList<String> adapterData) {

        if (adapterData.size() <= 0)
            adapterData.add("No data! Pull to refresh.");

        UserRequestAdapter stringAdapter = new UserRequestAdapter(this, mContext, adapterData);
        sessionListView.setAdapter(stringAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        sessionListView.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mUser != null) {
            getAllActiveSessionsOfUser(mUser.get_id());
        }
    }
}

