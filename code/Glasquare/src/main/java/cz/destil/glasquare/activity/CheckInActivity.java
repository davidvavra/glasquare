package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.glasquare.R;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.Auth;
import cz.destil.glasquare.api.CheckIn;
import cz.destil.glasquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Base activity which performs a check-in.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class CheckInActivity extends BaseActivity {

    public static String EXTRA_VENUE_ID = "venue_id";
    @InjectView(R.id.loading)
    TextView vLoading;
    @InjectView(R.id.progress)
    ProgressBar vProgress;

    public static void call(Activity activity, String venueId) {
        Intent intent = new Intent(activity, CheckInActivity.class);
        intent.putExtra(EXTRA_VENUE_ID, venueId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.inject(this);
    }

    @Override
    protected void loadData() {
        String venueId = getIntent().getStringExtra(EXTRA_VENUE_ID);
        String ll = LocationUtils.getLatLon();
        String token = Auth.getToken();
        Api.get().create(CheckIn.class).add(token, venueId, ll, new Callback<CheckIn.CheckInResponse>() {
            @Override
            public void success(CheckIn.CheckInResponse checkInResponse, Response response) {
                showSuccess(R.string.checked_in);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (!Auth.handle(CheckInActivity.this, retrofitError)) {
                    showError(R.string.error_please_try_again);
                }
            }
        });
    }

    protected void showSuccess(int resourceId) {
        vLoading.setText(resourceId);
        vProgress.setVisibility(View.GONE);
    }

    protected void showError(int resourceId) {
        vLoading.setText(resourceId);
        vProgress.setVisibility(View.GONE);
    }
}
