package cz.destil.glasquare.activity;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.glasquare.R;
import cz.destil.glasquare.adapter.VenuesAdapter;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.ExploreVenues;
import cz.destil.glasquare.util.DebugLog;
import cz.destil.glasquare.util.IntentUtils;
import cz.destil.glasquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Destil on 29.11.13.
 */
public class MainActivity extends Activity {

    @InjectView(R.id.card_scroll)
    CardScrollView vCardScroll;
    @InjectView(R.id.loading)
    TextView vLoading;
    @InjectView(R.id.progress)
    ProgressBar vProgress;
    private ExploreVenues.Venue mSelectedVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        downloadVenues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.venue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_navigate && mSelectedVenue != null) {
            IntentUtils.launchNavigation(this, mSelectedVenue.latitude, mSelectedVenue.longitude);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadVenues() {
        final Location location = LocationUtils.getCurrentLocation();
        String ll = location.getLatitude() + "," + location.getLongitude();
        Api.get().create(ExploreVenues.class).get(ll, new Callback<ExploreVenues.ExploreVenuesResponse>() {
            @Override
            public void success(ExploreVenues.ExploreVenuesResponse exploreVenuesResponse, Response response) {
                vCardScroll.setAdapter(new VenuesAdapter(exploreVenuesResponse.getVenues(), location));
                vCardScroll.activate();
                vCardScroll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mSelectedVenue = (ExploreVenues.Venue) vCardScroll.getItemAtPosition(position);
                        openOptionsMenu();
                    }
                });
                vLoading.setVisibility(View.GONE);
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                vLoading.setText(R.string.error_please_try_again);
                vProgress.setVisibility(View.GONE);
                DebugLog.e(retrofitError.getCause().toString());
            }
        });
    }

}
