package raj.workalley.user.offers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import raj.workalley.R;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {
    List<OfferDummyItem> offerlist;

    public OffersAdapter(List<OfferDummyItem> offerlist) {
        this.offerlist = offerlist;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_offer_item, parent, false);

        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, int position) {
        OfferDummyItem offer = offerlist.get(position);
        holder.title.setText(offer.getTitle());
        holder.amount.setText(offer.getAmount());
        holder.time.setText(offer.getTime());
        holder.validity.setText(offer.getValidity());
    }

    @Override
    public int getItemCount() {
        return offerlist.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView title, amount, time, validity;

        public OfferViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.offer_title);
            amount = (TextView) view.findViewById(R.id.offer_amount);
            time = (TextView) view.findViewById(R.id.offer_time);
            validity = (TextView) view.findViewById(R.id.offer_validity);
        }
    }
}
