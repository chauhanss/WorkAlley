package raj.workalley.host.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import raj.workalley.R;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class SettingFragment extends Fragment {

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_host_settings, null);
        defineNonEditableViews();
        return v;
    }

    private void defineNonEditableViews() {
        EditText email,name,password,phone,workspaceName,numberOfSeat;

    }
}
