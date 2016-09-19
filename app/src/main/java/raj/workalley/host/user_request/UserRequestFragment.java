package raj.workalley.host.user_request;

import android.content.Context;
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
        toolbar.setTitle("USER REQUESTS");

        userRequestsListView = (RecyclerView) view.findViewById(R.id.requests);
        userRequests = SharedPrefsUtils.getHashSetPreference(getActivity(), Constants.BOOKING_REQUEST, Constants.SP_NAME);
        setUpRecyclerViewAdapter();

    }

    private void setUpRecyclerViewAdapter() {

        if (userRequests != null && userRequests.size() > 0) {

            userRequestList = new ArrayList<>();

            for (String user : userRequests) {

                try {
                    JSONObject userObject = new JSONObject(user);
                    UserInfo userRequest = (UserInfo) Session.getInstance(mContext).getParsedResponseFromGSON(userObject, Session.workAlleyModels.UserInfo);
                    userRequestList.add(userRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            UserRequestAdapter requestAdapter = new UserRequestAdapter(UserRequestFragment.this, userRequestList, mContext);
            userRequestsListView.setAdapter(requestAdapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            userRequestsListView.setLayoutManager(linearLayoutManager);

        }
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

    @Override
    public void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    public void rejectRequest(UserInfo user) {

        if (Helper.isConnected(mContext)) {
            Helper.showProgressDialogSpinner(mContext, "Please Wait", "Rejecting request", false);
            Session.getInstance(mContext).acceptRejectWorkspaceBookSeatRequest(user, true);
        } else
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        Helper.dismissProgressDialog();
        switch (event.getType()) {
            case CobbocEvent.ACCEPT_REJECT_BOOKING_REQUEST:
                try {
                    JSONObject jsonObject = (JSONObject) event.getValue();
                    if (jsonObject.getBoolean("isRejectRequest")) {

                        UserInfo user = (UserInfo) jsonObject.get("user");
                        if (userRequestList != null) {
                            userRequestList.remove(user.get_id());
                            invalidateList(userRequestList);
                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}
