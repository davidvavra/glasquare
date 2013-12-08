package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cz.destil.glasquare.R;
import cz.destil.glasquare.adapter.TipsAdapter;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.Tips;
import cz.destil.glasquare.util.DebugLog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activity with list of tips.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class TipsActivity extends CardScrollActivity {

    public static String EXTRA_VENUE_ID = "venue_id";

    public static void call(Activity activity, String venueId) {
        Intent intent = new Intent(activity, TipsActivity.class);
        intent.putExtra(EXTRA_VENUE_ID, venueId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadTips();
    }

    private void downloadTips() {
        String venueId = getIntent().getStringExtra(EXTRA_VENUE_ID);
        Api.get().create(Tips.class).get(venueId, new Callback<Tips.TipsResponse>() {
            @Override
            public void success(Tips.TipsResponse tipsResponse, Response response) {
                vCardScroll.setAdapter(new TipsAdapter(tipsResponse.getTips()));
                vCardScroll.activate();
                hideProgress();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showError(R.string.error_please_try_again);
                DebugLog.e(retrofitError);
            }
        });
    }

}
