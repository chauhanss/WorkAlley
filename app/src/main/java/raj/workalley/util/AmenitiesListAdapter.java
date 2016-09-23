package raj.workalley.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.AmenitiesItem;
import raj.workalley.R;
import raj.workalley.host.signup.HostSignUpActivity;
import raj.workalley.user.fresh.offers.OfferDummyItem;

/**
 * Created by vishal.raj on 9/7/16.
 */
public class AmenitiesListAdapter extends RecyclerView.Adapter<AmenitiesListAdapter.AmenitiesViewHolder> {
    List<AmenitiesItem> amenitiesList;
    boolean enableCheckList;
    RecyclerView recyclerView;
    List<Integer> mSelectedPosition;

    public AmenitiesListAdapter(ArrayList<AmenitiesItem> amenitiesList, boolean enableCheckList, RecyclerView recyclerView) {
        this.amenitiesList = amenitiesList;
        this.enableCheckList = enableCheckList;
        this.recyclerView = recyclerView;
        mSelectedPosition = new ArrayList<>();
    }


    @Override
    public AmenitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_amenities_item, parent, false);
        if (enableCheckList)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int thisPos = recyclerView.getChildAdapterPosition(v);
                    if (v.isSelected()) {
                        v.setSelected(false);
                        mSelectedPosition.remove(new Integer(thisPos));
                    } else {
                        v.setSelected(true);
                        mSelectedPosition.add(thisPos);
                    }
                    //amenitiesList.get(thisPos).setActive(v.isSelected());
                }
            });
        return new AmenitiesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AmenitiesViewHolder holder, int position) {
        AmenitiesItem amenities = amenitiesList.get(position);
        holder.name.setText(amenities.getAmenitiesName());
        holder.icon.setBackgroundResource(amenities.getAmenitiesIcon());
        if (posInSelectedList(position)) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    private boolean posInSelectedList(int position) {
        for (Integer i : mSelectedPosition) {
            if (i == position)
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return amenitiesList.size();
    }

    public class AmenitiesViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView icon;
        View itemView;

        public AmenitiesViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.amenities_name);
            icon = (ImageView) view.findViewById(R.id.amenities_icon);
            itemView = view;
        }
    }

    public List<String> getSelectedItem() {
        List<String> list = new ArrayList<>();
        for (Integer i : mSelectedPosition) {
            list.add(amenitiesList.get(i).getAmenitiesName());
        }
        return list;
    }
}
