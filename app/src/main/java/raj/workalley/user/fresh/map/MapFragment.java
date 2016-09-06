package raj.workalley.user.fresh.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import raj.workalley.BaseFragment;
import raj.workalley.R;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class MapFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_login, null);
        return v;
    }
}
