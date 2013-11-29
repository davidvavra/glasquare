package cz.destil.glasquare;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.ExploreVenues;
import cz.destil.glasquare.util.DebugLog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Destil on 29.11.13.
 */
public class MainActivity extends Activity {

    public static int MAX_IMAGE_WIDTH = 213;
    public static int MAX_IMAGE_HEIGHT = 360;

    @InjectView(R.id.image)
    ImageView vImage;
    @InjectView(R.id.name)
    TextView vName;
    @InjectView(R.id.category)
    TextView vCategory;
    @InjectView(R.id.distance)
    TextView vDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_venue);
        ButterKnife.inject(this);
        downloadVenues();
    }

    private void downloadVenues() {
        String location = getLocation();
        Api.get().create(ExploreVenues.class).get(location, new Callback<ExploreVenues.ExploreVenuesResponse>() {
            @Override
            public void success(ExploreVenues.ExploreVenuesResponse exploreVenuesResponse, Response response) {
                ExploreVenues.Venue venue = exploreVenuesResponse.getVenues().get(1);
                vName.setText(venue.name);
                vCategory.setText(venue.category);
                vDistance.setText(venue.distance+" m");
                Picasso.with(MainActivity.this).load(venue.imageUrl).resize(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT).centerCrop().placeholder(R.drawable.ic_venue_placeholder).into(vImage);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                DebugLog.e(retrofitError.toString());
            }
        });
    }

    private String getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        return location.getLatitude() + "," + location.getLongitude();
    }

}
