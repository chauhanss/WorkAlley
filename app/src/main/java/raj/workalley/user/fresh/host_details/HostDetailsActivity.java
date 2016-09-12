package raj.workalley.user.fresh.host_details;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import raj.workalley.BaseActivity;
import raj.workalley.Constants;
import raj.workalley.R;
import raj.workalley.Session;
import raj.workalley.WorkspaceList;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class HostDetailsActivity extends BaseActivity {

    Session mSession;
    Context mContext;
    WorkspaceList.Workspace mWorkspace = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_details);
        mContext = this;
        mSession = Session.getInstance(mContext);

        if (getIntent() != null) {
            String workspaceId = getIntent().getStringExtra(Constants.WORKSPACE_ID);
            WorkspaceList workspaces = mSession.getWorkspaces();

            if (workspaces != null && workspaces.getWorkspaceData() != null && workspaces.getWorkspaceData().size() > 0) {
                for (WorkspaceList.Workspace workspace : workspaces.getWorkspaceData()) {

                    if (workspaceId.equalsIgnoreCase(workspace.get_id())) {
                        mWorkspace = workspace;
                    }
                }
            }

            if (mWorkspace == null) {
                Toast.makeText(mContext, "NO Workspace match!", Toast.LENGTH_LONG).show();
                return;
            } else
                setUpAndDisplayData();
        }
    }

    private void setUpAndDisplayData() {

    }
}
