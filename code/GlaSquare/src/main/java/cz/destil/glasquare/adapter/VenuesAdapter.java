package cz.destil.glasquare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.glasquare.App;
import cz.destil.glasquare.R;
import cz.destil.glasquare.api.ExploreVenues;

/**
 * Adapter for list of venues.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class VenuesAdapter extends CardScrollAdapter {

    public static int MAX_IMAGE_WIDTH = 213;
    public static int MAX_IMAGE_HEIGHT = 360;
    private List<ExploreVenues.Venue> mVenues;

    public VenuesAdapter(List<ExploreVenues.Venue> venues) {
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
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(App.get()).inflate(R.layout.view_venue, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ExploreVenues.Venue venue = mVenues.get(i);
        holder.name.setText(venue.name);
        holder.category.setText(venue.category);
        if (venue.hours == null) {
            holder.hours.setVisibility(View.GONE);
        } else {
            holder.hours.setText(venue.hours);
            holder.hours.setVisibility(View.VISIBLE);
        }
        holder.distance.setText(venue.distance + " m");
        Picasso.with(App.get()).load(venue.imageUrl).resize(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT).centerCrop().placeholder(R.drawable
                .ic_venue_placeholder).into(holder.image);

        return view;
    }

    @Override
    public int findIdPosition(Object o) {
        return -1;
    }

    @Override
    public int findItemPosition(Object o) {
        return mVenues.indexOf(o);
    }

    static class ViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.category)
        TextView category;
        @InjectView(R.id.hours)
        TextView hours;
        @InjectView(R.id.distance)
        TextView distance;
        @InjectView(R.id.image)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
