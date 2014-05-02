package cz.destil.glasquare.activity;

import android.location.Location;

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
public class CheckInSearchActivity extends BaseCardScrollActivity {

    @Override
    protected void loadData() {
        showProgress(R.string.loading);

	    LocationUtils.getRecentLocation(new LocationUtils.LocationListener() {
		    @Override
		    public void onLocationAcquired(Location location) {
			    String ll = LocationUtils.getLatLon(location);
			    Api.get().create(SearchVenues.class).searchForCheckIn(ll, new Callback<SearchVenues.SearchResponse>() {
				    @Override
				    public void success(SearchVenues.SearchResponse venuesResponse, Response response) {
					    showContent(new CheckInSearchAdapter(venuesResponse.getVenues()), new CardSelectedListener() {
						    @Override
						    public void onCardSelected(Object item) {
							    SearchVenues.Venue venue = (SearchVenues.Venue) item;
							    CheckInActivity.call(CheckInSearchActivity.this, venue.id, venue.name);
						    }
					    });
				    }

				    @Override
				    public void failure(RetrofitError retrofitError) {
					    showError(R.string.error_please_try_again);
				    }
			    });
		    }

		    @Override
		    public void onLocationFailed() {
			    showError(R.string.no_location);
		    }
	    });
    }

}
