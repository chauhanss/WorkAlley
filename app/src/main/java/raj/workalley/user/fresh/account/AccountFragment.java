package raj.workalley.user.fresh.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.security.PublicKey;

import raj.workalley.R;
import raj.workalley.user.fresh.offers.OfferActivity;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class AccountFragment extends Fragment {

    EditText workspaceName;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_account_fresh_user, null);
        workspaceName = (EditText) v.findViewById(R.id.workspace_name);
        v.findViewById(R.id.recharge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OfferActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    public void changeLayout(String workspaceNameStr) {
        Toast.makeText(getActivity(), "here", Toast.LENGTH_LONG).show();
        workspaceName.setText(workspaceNameStr);
    }

    private void startUserService() {

    }
}
