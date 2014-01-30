package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

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
 * Activity with list of venues.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class VenuesActivity extends CardScrollActivity {

    public static final String EXTRA_QUERY = "query";
    public static final String EXTRA_TYPE = "type";
    public static final int TYPE_EXPLORE = 0;
    public static final int TYPE_SEARCH = 1;
    private ExploreVenues.Venue mSelectedVenue;

    public static void call(Activity activity, int type, String query) {
        Intent intent = new Intent(activity, VenuesActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_QUERY, query);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.venue, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSelectedVenue != null) {
            menu.findItem(R.id.menu_tips).setEnabled(mSelectedVenue.hasTips);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSelectedVenue != null) {
            switch (item.getItemId()) {
                case R.id.menu_navigate:
                    IntentUtils.launchNavigation(this, mSelectedVenue.latitude, mSelectedVenue.longitude);
                    return true;
                case R.id.menu_tips:
                    TipsActivity.call(this, mSelectedVenue.id);
                    return true;
                case R.id.menu_check_in:
                    CheckInActivity.call(this, mSelectedVenue.id);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadData() {
        showProgress(R.string.loading);
        final String ll = LocationUtils.getLatLon();
        if (ll == null) {
            showError(R.string.no_location);
            return;
        }

        Callback<ExploreVenues.ExploreVenuesResponse> callback = new Callback<ExploreVenues.ExploreVenuesResponse>() {
            @Override
            public void success(ExploreVenues.ExploreVenuesResponse exploreVenuesResponse, Response response) {
                if (exploreVenuesResponse.getVenues().size() == 0) {
                    showError(R.string.no_venues_found);
                } else {
                    showContent(new VenuesAdapter(exploreVenuesResponse.getVenues()), new CardSelectedListener() {
                        @Override
                        public void onCardSelected(Object item) {
                            mSelectedVenue = (ExploreVenues.Venue) item;
                            openOptionsMenu();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showError(R.string.error_please_try_again);
                DebugLog.e(retrofitError);
            }
        };


        int type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_EXPLORE);
        switch (type) {
            case TYPE_EXPLORE:
                Api.get().create(ExploreVenues.class).best(ll, callback);
                break;
            case TYPE_SEARCH:
                String query = getIntent().getStringExtra(EXTRA_QUERY);
                Api.get().create(ExploreVenues.class).search(ll, query, callback);
                break;
        }
    }

}
