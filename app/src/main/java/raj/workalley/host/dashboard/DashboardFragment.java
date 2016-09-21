package raj.workalley.host.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.CobbocEvent;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.StringAdapter;
import raj.workalley.WorkspaceList;
import raj.workalley.util.Helper;
import raj.workalley.util.SharedPrefsUtils;

/**
 * Created by vishal.raj on 9/6/16.
 */
public class DashboardFragment extends Fragment {

    RecyclerView activeUserList;
    TextView numberOfSeats;
    private Context mContext;
    private Session mSession;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

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
        makeActiveUserApi();

    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase(Constants.SESSION_END_CONFIRMED) || message.equalsIgnoreCase(Constants.BOOKING_ACCEPT)) {
                makeActiveUserApi();
            }
        }

    };

    public void makeActiveUserApi() {
        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Fetching data!", false);

            /**
             * All workspaces by this owner will have same owner data.
             * Thats why taking first object in list
             */
            String hostId = mSession.getWorkspaces().getWorkspaceData().get(0).getOwner().get_id();
            mSession.getAllActiveUsersInWorkspace(hostId);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_host_dashboard, null);


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activeUserList = (RecyclerView) view.findViewById(R.id.active_users);
        numberOfSeats = (TextView) view.findViewById(R.id.seats);

    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.GET_ALL_ACTIVE_USERS: {
                Helper.dismissProgressDialog();
                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();
                    try {
                        JSONArray data = jsonObject.getJSONArray("data");

//                        numberOfSeats.setText(data.length());

                        ArrayList<String> adapterData = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject objectData = data.getJSONObject(i);
                            JSONObject user = objectData.getJSONObject("user");
                            JSONObject space = objectData.getJSONObject("space");


                            adapterData.add(user.getString("name") + "|" + user.getString("email") + "|" + space.getString("name") + "|" + objectData.getString("status"));
                        }

                        setUpRecyclerView(adapterData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }else
                    Toast.makeText(mContext, "Some error occurred!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpRecyclerView(List<String> list) {
        StringAdapter stringAdapter = new StringAdapter(mContext, list);
        activeUserList.setAdapter(stringAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        activeUserList.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mSession = Session.getInstance(mContext);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            makeActiveUserApi();
        }
    }
}
