package cz.destil.glasquare.adapter;

import android.location.Location;
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
 * Created by Destil on 2.12.13.
 */
public class VenuesAdapter extends CardScrollAdapter {

    public static int MAX_IMAGE_WIDTH = 213;
    public static int MAX_IMAGE_HEIGHT = 360;
    private List<ExploreVenues.Venue> mVenues;
    private Location mLocation;

    public VenuesAdapter(List<ExploreVenues.Venue> venues, Location location) {
        mVenues = venues;
        mLocation = location;
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
        holder.hours.setText(venue.hours);
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
