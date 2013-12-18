package cz.destil.glasquare.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

import cz.destil.glasquare.App;
import cz.destil.glasquare.R;
import cz.destil.glasquare.api.SearchVenues;

/**
 * Adapter for list of tips.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class CheckInSearchAdapter extends CardScrollAdapter {

    private List<SearchVenues.Venue> mVenues;

    public CheckInSearchAdapter(List<SearchVenues.Venue> venues) {
        mVenues = venues;
    }

    @Override
    public int getCount() {
        return mVenues.size();
    }

    @Override
    public Object getItem(int i) {
        return mVenues.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Card card = new Card(App.get());
        SearchVenues.Venue venue = mVenues.get(i);
        card.setText(venue.name);
        card.setFootnote(R.string.tap_to_check_in);
        return card.toView();
    }

    @Override
    public int findIdPosition(Object o) {
        return -1;
    }

    @Override
    public int findItemPosition(Object o) {
        return mVenues.indexOf(o);
    }
}
