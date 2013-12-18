package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.InjectView;
import cz.destil.glasquare.R;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.Auth;
import cz.destil.glasquare.api.CheckIns;
import cz.destil.glasquare.util.IntentUtils;
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
    @InjectView(R.id.result)
    TextView vResult;
    @InjectView(R.id.progress)
    ProgressBar vProgress;
    @InjectView(R.id.icon)
    ImageView vIcon;
    String mCheckInId = null;

    public static void call(Activity activity, String venueId) {
        Intent intent = new Intent(activity, CheckInActivity.class);
        intent.putExtra(EXTRA_VENUE_ID, venueId);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_in;
    }

    @Override
    protected void loadData() {
        String venueId = getIntent().getStringExtra(EXTRA_VENUE_ID);
        String ll = LocationUtils.getLatLon();
        String token = Auth.getToken();
        showProgress(R.string.checking_in);
        Api.get().create(CheckIns.class).add(token, venueId, ll, new Callback<CheckIns.CheckInResponse>() {
            @Override
            public void success(CheckIns.CheckInResponse checkInResponse, Response response) {
                mCheckInId = checkInResponse.getCheckInId();
                checkInResponse.printNotifications();
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

    @Override
    protected void onTap() {
        if (mCheckInId != null) {
            openOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_photo:
                // TODO
                return true;
            case R.id.menu_comment:
                IntentUtils.startSpeechRecognition(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String text = IntentUtils.processSpeechRecognitionResult(requestCode, resultCode, data);
        if (text != null) {
            addComment(text);
        }
    }

    private void addComment(String text) {
        String token = Auth.getToken();
        showProgress(R.string.adding_comment);
        Api.get().create(CheckIns.class).addComment(token, mCheckInId, text, new Callback<CheckIns.CheckInResponse>() {
            @Override
            public void success(CheckIns.CheckInResponse checkInResponse, Response response) {
                showSuccess(R.string.comment_added);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showError(R.string.error_please_try_again);
            }
        });
    }

    protected void showProgress(int resourceId) {
        vResult.setText(resourceId);
        vProgress.setVisibility(View.VISIBLE);
        vIcon.setVisibility(View.GONE);
    }

    protected void showSuccess(int resourceId) {
        vResult.setText(resourceId);
        vProgress.setVisibility(View.GONE);
        vIcon.setVisibility(View.VISIBLE);
    }

    protected void showError(int resourceId) {
        vResult.setText(resourceId);
        vProgress.setVisibility(View.GONE);
        vIcon.setVisibility(View.GONE);
    }
}
