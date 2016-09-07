package raj.workalley.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import raj.workalley.R;
import raj.workalley.host.signup.HostSignUpActivity;
import raj.workalley.user.fresh.offers.OfferDummyItem;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class AmenitiesListAdapter extends RecyclerView.Adapter<AmenitiesListAdapter.AmenitiesViewHolder> {
    List<HostSignUpActivity.AmenitiesItem> amenitiesList;

    public AmenitiesListAdapter(List<HostSignUpActivity.AmenitiesItem> amenitiesList) {
        this.amenitiesList = amenitiesList;
    }

    @Override
    public AmenitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_amenities_item, parent, false);

        return new AmenitiesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AmenitiesViewHolder holder, int position) {
        HostSignUpActivity.AmenitiesItem amenities = amenitiesList.get(position);
        holder.name.setText(amenities.getAmenitiesName());
        holder.icon.setBackgroundResource(amenities.getAmenitiesIcon());
    }

    @Override
    public int getItemCount() {
        return amenitiesList.size();
    }

    public class AmenitiesViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView icon;

        public AmenitiesViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.amenities_name);
            icon = (ImageView) view.findViewById(R.id.amenities_icon);
        }
    }
}
