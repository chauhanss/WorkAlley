package raj.workalley.user.offers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import raj.workalley.BaseFragment;
import raj.workalley.R;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class OffersFragment extends BaseFragment {

    public static OffersFragment newInstance() {
        OffersFragment fragment = new OffersFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_offers, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView offerListView = (RecyclerView) view.findViewById(R.id.list);
        List<OfferDummyItem> offerlist = getOfferList();
        OffersAdapter mAdapter = new OffersAdapter(offerlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        offerListView.setLayoutManager(mLayoutManager);
        offerListView.setItemAnimator(new DefaultItemAnimator());
        offerListView.setAdapter(mAdapter);
    }

    private List<OfferDummyItem> getOfferList() {
        List<OfferDummyItem> offerDummyItemList = new ArrayList<>();

        OfferDummyItem offer = new OfferDummyItem("Super Saver", "Rs 5000", "100 Hrs", "3 Months");
        offerDummyItemList.add(offer);

        OfferDummyItem offer1 = new OfferDummyItem("Super Saver", "Rs 5000", "100 Hrs", "3 Months");
        offerDummyItemList.add(offer1);

        OfferDummyItem offer2 = new OfferDummyItem("Super Saver", "Rs 5000", "100 Hrs", "3 Months");
        offerDummyItemList.add(offer2);

        OfferDummyItem offer3 = new OfferDummyItem("Super Saver", "Rs 5000", "100 Hrs", "3 Months");
        offerDummyItemList.add(offer3);

        return offerDummyItemList;
    }
}
