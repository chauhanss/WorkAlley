package raj.workalley.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import raj.workalley.R;

/**
 * Created by shruti.vig on 9/20/16.
 */
public class StringAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final Context mContext;
    private final List<String> list;

    /**
     * @param context
     * @param list    get pipe separated name, email, workspace name and status
     */
    public StringAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.cc_string_item, parent, false);
        StringViewHolder viewHolder = new StringViewHolder(convertView);
        convertView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String displayString = list.get(position);
        StringViewHolder viewHolder = (StringViewHolder) holder;

        viewHolder.name.setText(displayString.substring(0, displayString.indexOf("|")));

        String intermediateString = displayString.substring(displayString.indexOf("|") + 1, displayString.length());
        viewHolder.email.setText(intermediateString.substring(0, intermediateString.indexOf("|")));

        String intermediateString2 = intermediateString.substring(intermediateString.indexOf("|") + 1, intermediateString.length());
        viewHolder.workspace.setText(intermediateString2.substring(0, intermediateString2.indexOf("|")));

        viewHolder.status.setText(intermediateString2.substring(intermediateString2.indexOf("|") + 1, intermediateString2.length()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class StringViewHolder extends RecyclerView.ViewHolder {

        private TextView name, email, workspace, status;

        public StringViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.user_name);
            email = (TextView) v.findViewById(R.id.user_email);
            workspace = (TextView) v.findViewById(R.id.workspace_name);
            status = (TextView) v.findViewById(R.id.status);
        }

    }
}
