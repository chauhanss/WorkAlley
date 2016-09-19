package raj.workalley.host.user_request;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import raj.workalley.R;

/**
 * Created by vishal.raj on 9/19/16.
 */
public class UserRequestFragment extends Fragment {

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


}
