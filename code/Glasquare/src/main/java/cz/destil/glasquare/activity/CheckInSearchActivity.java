package cz.destil.glasquare.activity;

import cz.destil.glasquare.R;
import cz.destil.glasquare.adapter.CheckInSearchAdapter;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.SearchVenues;
import cz.destil.glasquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activity with shows places where you are likely to check in.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class CheckInSearchActivity extends CardScrollActivity {

    @Override
    protected void loadData() {
        showProgress(R.string.loading);
        final String ll = LocationUtils.getLatLon();
        if (ll == null) {
            showError(R.string.no_location);
            return;
        }
        Api.get().create(SearchVenues.class).searchForCheckIn(ll, new Callback<SearchVenues.SearchResponse>() {
            @Override
            public void success(SearchVenues.SearchResponse venuesResponse, Response response) {
                showContent(new CheckInSearchAdapter(venuesResponse.getVenues()), new CardSelectedListener() {
                    @Override
                    public void onCardSelected(Object item) {
                        CheckInActivity.call(CheckInSearchActivity.this, ((SearchVenues.Venue) item).id);
                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showError(R.string.error_please_try_again);
            }
        });
    }

}
