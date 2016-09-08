package raj.workalley.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.List;

import raj.workalley.R;
import raj.workalley.host.signup.HostSignUpActivity;

/**
 * Created by vishal.raj on 9/8/16.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>{

    public ImageListAdapter(List<HostSignUpActivity.ImageItem> imageList) {

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
    public CheckBox name;
    public ImageView icon;

    public ImageViewHolder(View view) {
        super(view);
        name = (CheckBox) view.findViewById(R.id.amenities_name);
        icon = (ImageView) view.findViewById(R.id.amenities_icon);
    }

}

}
