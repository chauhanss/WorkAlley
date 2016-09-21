package raj.workalley.host.user_request;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import raj.workalley.R;
import raj.workalley.user.fresh.UserInfo;

/**
 * Created by shruti.vig on 9/13/16.
 */
public class UserRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BOOKING_REQUEST = 1;
    private static final int TYPE_END_REQUEST = 2;
    UserRequestFragment mContext;
    Context mActivityContext;
    List<UserInfo> users;


    public UserRequestAdapter(UserRequestFragment context, List<UserInfo> list, Context mActivityContext) {
        this.mContext = context;
        this.users = list;
        this.mActivityContext = mActivityContext;
    }

    @Override
    public int getItemViewType(int position) {

        if (users.get(position).isEndRequest())
            return TYPE_END_REQUEST;
        else
            return TYPE_BOOKING_REQUEST;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        switch (viewType) {
            case TYPE_BOOKING_REQUEST:
                convertView = LayoutInflater.from(mActivityContext).inflate(R.layout.booking_request_item, parent, false);
                UserRequestViewHolder viewHolder = new UserRequestViewHolder(convertView);
                convertView.setTag(viewHolder);
                return viewHolder;
            case TYPE_END_REQUEST:
                convertView = LayoutInflater.from(mActivityContext).inflate(R.layout.session_end_item, parent, false);
                UserRequestViewHolder sessionEndViewHolder = new UserRequestViewHolder(convertView);
                convertView.setTag(sessionEndViewHolder);
                return sessionEndViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case TYPE_BOOKING_REQUEST: {
                UserInfo user = users.get(position);
                UserRequestViewHolder viewHolder = (UserRequestViewHolder) holder;

                viewHolder.userEmail.setText(user.getEmail());
                viewHolder.userName.setText(user.getName());
                break;
            }
            case TYPE_END_REQUEST:
                UserInfo user = users.get(position);
                UserRequestViewHolder viewHolder = (UserRequestViewHolder) holder;

                viewHolder.userEmail.setText(user.getEmail());
                viewHolder.userName.setText(user.getName());
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    private class UserRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName, userEmail;
        Button rejectRequest, acceptRequest;

        public UserRequestViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.user_name);
            userEmail = (TextView) itemView.findViewById(R.id.user_email);
            acceptRequest = (Button) itemView.findViewById(R.id.accept);
            rejectRequest = (Button) itemView.findViewById(R.id.reject);

            if (acceptRequest != null)
                acceptRequest.setOnClickListener(this);
            rejectRequest.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            UserInfo user = (UserInfo) users.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.reject:
                    if (((TextView) v).getText().toString().equalsIgnoreCase("REJECT"))
                        mContext.rejectRequest(user);
                    else
                        mContext.approveEndRequest(user);
                    break;
                case R.id.accept:
                    mContext.acceptRequest(user);
                    break;
            }
        }
    }
}
