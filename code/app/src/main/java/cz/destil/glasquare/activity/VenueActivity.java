package cz.destil.glasquare.activity;

import java.util.ArrayList;
import java.util.List;

import cz.destil.glasquare.R;
import cz.destil.glasquare.adapter.VenuesAdapter;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.ExploreVenues;
import cz.destil.glasquare.util.DebugLog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activity with a single venue.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class VenueActivity extends BaseVenuesActivity {

    @Override
    protected void loadData() {
        String id = getIntent().getStringExtra("id");
        if (id == null) {
            if (getIntent().getData() == null) {
                finish();
                return;
            } else {
                // url like this: glasquare://venue/41059b00f964a520850b1fe3
                List<String> pathSegments = getIntent().getData().getPathSegments();
                if (pathSegments.size() > 0) {
                    id = pathSegments.get(0);
                } else {
                    finish();
                    return;
                }
            }
        }
        showProgress(R.string.loading);
        Api.get().create(ExploreVenues.class).detail(id, new Callback<ExploreVenues.ExploreVenueResponse>() {
            @Override
            public void success(ExploreVenues.ExploreVenueResponse exploreVenueResponse, Response response) {
                mSelectedVenue = exploreVenueResponse.getVenue();
                if (mSelectedVenue == null) {
                    showError(R.string.no_venues_found);
                } else {
                    List<ExploreVenues.Venue> venues = new ArrayList<ExploreVenues.Venue>();
                    venues.add(mSelectedVenue);
                    showContent(new VenuesAdapter(venues), new CardSelectedListener() {
                        @Override
                        public void onCardSelected(Object item) {
                            openOptionsMenu();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showError(R.string.error_please_try_again);
                DebugLog.e(error);
            }
        });

    }


}
