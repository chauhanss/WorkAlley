package raj.workalley.host.user_request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.user.fresh.UserInfo;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/19/16.
 */
public class UserRequestFragment extends Fragment {

    private Set<String> userRequests;
    private RecyclerView userRequestsListView;
    private Context mContext;
    private ArrayList<UserInfo> userRequestList;

    public static UserRequestFragment newInstance() {
        UserRequestFragment fragment = new UserRequestFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user_request, null);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpRecyclerViewAdapter();
            }
        });

        userRequestsListView = (RecyclerView) view.findViewById(R.id.requests);
        setUpRecyclerViewAdapter();

    }

    private void setUpRecyclerViewAdapter() {

        userRequests = SharedPrefsUtils.getHashSetPreference(getActivity(), Constants.BOOKING_REQUEST, Constants.SP_NAME);
        userRequestList = new ArrayList<>();
        userRequestList.clear();
        if (userRequests != null) {
            for (String user : userRequests) {

                try {
                    JSONObject userObject = new JSONObject(user);
                    UserInfo userRequest = (UserInfo) Session.getInstance(mContext).getParsedResponseFromGSON(userObject, Session.workAlleyModels.UserInfo);
                    userRequestList.add(userRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (SharedPrefsUtils.hasKey(mContext, Constants.SESSION_END_REQUEST, Constants.SP_NAME)) {
            Set<String> userEndRequests = SharedPrefsUtils.getHashSetPreference(mContext, Constants.SESSION_END_REQUEST, Constants.SP_NAME);

            for (String endRequests : userEndRequests) {
                try {
                    JSONObject userObject = new JSONObject(endRequests);
                    UserInfo userRequest = (UserInfo) Session.getInstance(mContext).getParsedResponseFromGSON(userObject, Session.workAlleyModels.UserInfo);
                    userRequest.setIsEndRequest(true);
                    userRequestList.add(userRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }


        UserRequestAdapter requestAdapter = new UserRequestAdapter(UserRequestFragment.this, userRequestList, mContext);
        userRequestsListView.setAdapter(requestAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        userRequestsListView.setLayoutManager(linearLayoutManager);

    }

    public void invalidateList(ArrayList<UserInfo> userRequests) {
        UserRequestAdapter requestAdapter = new UserRequestAdapter(UserRequestFragment.this, userRequests, mContext);
        userRequestsListView.setAdapter(requestAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        userRequestsListView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase(Constants.BOOKING_REQUEST))
                setUpRecyclerViewAdapter();
            else if (message.equalsIgnoreCase(Constants.BOOKING_CANCELED)) {

                JSONObject user = null;
                try {
                    user = new JSONObject(intent.getStringExtra("USER"));

                    SharedPrefsUtils.removePreferenceByKey(mContext, user.getString("_id"), Constants.SP_NAME);
                    SharedPrefsUtils.removeSetInHashSetPreference(mContext, Constants.BOOKING_REQUEST, user.toString(), Constants.SP_NAME);
                    setUpRecyclerViewAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.equalsIgnoreCase(Constants.SESSION_END_REQUEST)) {
                setUpRecyclerViewAdapter();
            }
        }

    };

    @Override
    public void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mContext.registerReceiver(notificationListener, new IntentFilter(Constants.REQUEST_RESPONSE));
        setUpRecyclerViewAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        mContext.unregisterReceiver(notificationListener);
    }

    public void rejectRequest(UserInfo user) {

        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Rejecting request", false);

            String requestId = SharedPrefsUtils.getStringPreference(mContext, user.get_id(), Constants.SP_NAME);
            Session.getInstance(mContext).acceptRejectWorkspaceBookSeatRequest(user, true, requestId);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    public void acceptRequest(UserInfo user) {

        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Accepting request", false);

            String requestId = SharedPrefsUtils.getStringPreference(mContext, user.get_id(), Constants.SP_NAME);
            Session.getInstance(mContext).acceptRejectWorkspaceBookSeatRequest(user, false, requestId);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    public void approveEndRequest(UserInfo user) {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Ending session", false);

            String requestId = SharedPrefsUtils.getStringPreference(mContext, user.get_id(), Constants.SP_NAME);

            String endToken = SharedPrefsUtils.getHashSetTokenValueForUser(mContext, Constants.SESSION_END_TOKEN, user.get_id(), Constants.SP_NAME);
            Session.getInstance(mContext).endSessionByHostConfirmed(user, requestId, endToken);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        Helper.dismissProgressDialog();
        switch (event.getType()) {
            case CobbocEvent.ACCEPT_REJECT_BOOKING_REQUEST:

                if (event.getStatus()) {
                    try {

                        JSONObject jsonObject = (JSONObject) event.getValue();

                        UserInfo user = (UserInfo) jsonObject.get("user");
                        if (userRequestList != null) {
                            userRequestList.remove(user);

                            Gson gson = new Gson();
                            String jsonUser = gson.toJson(user);

                            if (jsonObject.getBoolean("isRejectRequest"))
                                SharedPrefsUtils.removePreferenceByKey(mContext, user.get_id(), Constants.SP_NAME);
                            SharedPrefsUtils.removeSetInHashSetPreference(mContext, Constants.BOOKING_REQUEST, jsonUser, Constants.SP_NAME);
                            //      invalidateList(userRequestList);
                            setUpRecyclerViewAdapter();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
                break;
            case CobbocEvent.END_SESSION_CONFIRMED:

                if (event.getStatus()) {
                    try {
                        JSONObject jsonObject = (JSONObject) event.getValue();

                        UserInfo user = (UserInfo) jsonObject.get("user");

                        if (userRequestList != null) {
                            userRequestList.remove(user);

                            Gson gson = new Gson();
                            String jsonUser = gson.toJson(user);
                            SharedPrefsUtils.removePreferenceByKey(mContext, user.get_id(), Constants.SP_NAME);
                            SharedPrefsUtils.removeSetInHashSetPreference(mContext, Constants.SESSION_END_REQUEST, jsonUser, Constants.SP_NAME);
                            setUpRecyclerViewAdapter();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                } else
                    Toast.makeText(mContext, event.getValue().toString(), Toast.LENGTH_LONG).show();
        }
    }
}
