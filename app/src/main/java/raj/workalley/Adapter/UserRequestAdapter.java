package raj.workalley.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import raj.workalley.Fragment.AccountFragment;
import raj.workalley.R;

/**
 * Created by shruti.vig on 9/20/16.
 */
public class UserRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext = null;
    private Fragment mFragmentContext = null;
    private List<String> list;

    /**
     * @param context
     * @param list    get pipe separated  workspace name, address and status
     */
    public UserRequestAdapter(Fragment fragmentContext, Context context, List<String> list) {
        this.mFragmentContext = fragmentContext;
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.user_request_item, parent, false);
        StringViewHolder viewHolder = new StringViewHolder(convertView);
        convertView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String displayString = list.get(position);
        StringViewHolder viewHolder = (StringViewHolder) holder;

        if (displayString.contains("|")) {
            viewHolder.noData.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.workspacePlaceholder.setVisibility(View.VISIBLE);
            viewHolder.address.setVisibility(View.VISIBLE);
            viewHolder.endCancelButton.setVisibility(View.VISIBLE);
            viewHolder.name.setText(displayString.substring(0, displayString.indexOf("|")));

            String intermediateString = displayString.substring(displayString.indexOf("|") + 1, displayString.length());
            viewHolder.address.setText(intermediateString.substring(0, intermediateString.indexOf("|")));

            String intermediateString2 = intermediateString.substring(intermediateString.indexOf("|") + 1, intermediateString.length());
            String status = intermediateString2.substring(0, intermediateString2.indexOf("|"));

            String lastUpdated = intermediateString2.substring(intermediateString2.indexOf("|") + 1, intermediateString2.lastIndexOf("|"));

            String formattedDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = format.parse(lastUpdated);
                CharSequence date1 = android.text.format.DateFormat.format("dd MMM yy hh:mm:ss", date);
                if (date1 != null)
                    formattedDate = date1.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.lastUpdatedTime.setText(formattedDate);

            //   viewHolder.status.setTag(intermediateString2.substring(intermediateString.indexOf("|") + 1, intermediateString2.length()));

            viewHolder.endCancelButton.setVisibility(View.VISIBLE);

            viewHolder.status.setVisibility(View.GONE);
            switch (status) {
                case "requested":
                    viewHolder.endCancelButton.setText("CANCEL BOOKING REQUEST");
                    break;
                case "started":
                    viewHolder.endCancelButton.setText("END SESSION");
                    break;
                default:
                    viewHolder.status.setVisibility(View.VISIBLE);
                    viewHolder.status.setText("Session " + status);
                    viewHolder.endCancelButton.setVisibility(View.GONE);
                    break;
            }
        } else

        {
            viewHolder.noData.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.workspacePlaceholder.setVisibility(View.GONE);
            viewHolder.address.setVisibility(View.GONE);
            viewHolder.endCancelButton.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StringViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name, address, noData, workspacePlaceholder, status, lastUpdatedTime;
        private Button endCancelButton;

        public StringViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.workspace_name);
            workspacePlaceholder = (TextView) v.findViewById(R.id.workspace_label);
            address = (TextView) v.findViewById(R.id.workspace_address);
            status = (TextView) v.findViewById(R.id.status);
            endCancelButton = (Button) v.findViewById(R.id.end_cancel_session_btn);
            noData = (TextView) v.findViewById(R.id.no_data);
            endCancelButton.setOnClickListener(this);
            lastUpdatedTime = (TextView) v.findViewById(R.id.time);
        }

        @Override
        public void onClick(View v) {
            String request = (String) list.get(getPosition());
            String requestId = request.substring(request.lastIndexOf("|") + 1, request.length());

            String intermediateString1 = request.substring(0, request.lastIndexOf("|"));
            String intermediateString = intermediateString1.substring(0, intermediateString1.lastIndexOf("|"));
            String status = intermediateString.substring(intermediateString.lastIndexOf("|") + 1, intermediateString.length());

            switch (status) {
                case "requested":
                    ((AccountFragment) mFragmentContext).cancelBooking(requestId);
                    break;
                case "started":
                    ((AccountFragment) mFragmentContext).endSession(requestId);
                    break;
            }

        }
    }
}
