package cz.destil.glasquare.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import cz.destil.glasquare.R;
import cz.destil.glasquare.adapter.CheckInSearchAdapter;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.Auth;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadVenues();
    }

    private void downloadVenues() {
        final String ll = LocationUtils.getLatLon();
        if (ll == null) {
            showError(R.string.no_location);
            return;
        }
        Api.get().create(SearchVenues.class).searchForCheckIn(Auth.getToken(), ll, new Callback<SearchVenues.SearchResponse>() {
            @Override
            public void success(SearchVenues.SearchResponse venuesResponse, Response response) {
                vCardScroll.setAdapter(new CheckInSearchAdapter(venuesResponse.getVenues()));
                vCardScroll.activate();
                vCardScroll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        CheckInActivity.call(CheckInSearchActivity.this, ((SearchVenues.Venue) vCardScroll.getItemAtPosition(position)).id);
                    }
                });
                hideProgress();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showError(R.string.error_please_try_again);
            }
        });
    }

}
