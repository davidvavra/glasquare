package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import butterknife.InjectView;
import cz.destil.glasquare.R;
import cz.destil.glasquare.api.Api;
import cz.destil.glasquare.api.Auth;
import cz.destil.glasquare.api.CheckIns;
import cz.destil.glasquare.api.Photos;
import cz.destil.glasquare.util.BaseAsyncTask;
import cz.destil.glasquare.util.ImageUtils;
import cz.destil.glasquare.util.IntentUtils;
import cz.destil.glasquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Base activity which performs a check-in.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class CheckInActivity extends ProgressActivity {

    public static String EXTRA_VENUE_ID = "venue_id";

    @InjectView(R.id.primary_notification)
    TextView vPrimaryNotification;
    @InjectView(R.id.secondary_notification)
    TextView vSecondaryNotification;
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
                showSuccess(R.string.checked_in);
                if (checkInResponse.getPrimaryNotification() != null) {
                    vPrimaryNotification.setVisibility(View.VISIBLE);
                    vPrimaryNotification.setText(checkInResponse.getPrimaryNotification());
                }
                if (checkInResponse.getSecondaryNotification() != null) {
                    vSecondaryNotification.setVisibility(View.VISIBLE);
                    vSecondaryNotification.setText(checkInResponse.getSecondaryNotification());
                }
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
                IntentUtils.takePicture(this);
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
        } else {
            IntentUtils.processTakePictureResult(this, requestCode, resultCode, data, new IntentUtils.OnPictureReadyListener() {
                @Override
                public void onPathKnown() {
                    showProgress(R.string.adding_photo);
                }

                @Override
                public void onPictureReady(File image) {
                    resizeImage(image);
                }
            });
        }
    }

    private void resizeImage(final File image) {
        new BaseAsyncTask() {
            @Override
            public void inBackground() {
                ImageUtils.resize(image);
            }

            @Override
            public void postExecute() {
                addPhoto(image);
            }
        }.start();
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

    private void addPhoto(File image) {
        String token = Auth.getToken();
        TypedFile typedFile = new TypedFile("image/jpeg", image);
        Api.get().create(Photos.class).add(token, mCheckInId, typedFile, new Callback<Photos.PhotoAddResponse>() {
            @Override
            public void success(Photos.PhotoAddResponse photoAddResponse, Response response) {
                showSuccess(R.string.photo_added);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showError(R.string.error_please_try_again);
            }
        });
    }

    @Override
    protected void showProgress(int resourceId) {
        super.showProgress(resourceId);
        hideIcon();
        vPrimaryNotification.setVisibility(View.GONE);
        vSecondaryNotification.setVisibility(View.GONE);
    }

    @Override
    protected void showSuccess(int resourceId) {
        super.showSuccess(resourceId);
        showIcon();
        vPrimaryNotification.setVisibility(View.GONE);
        vSecondaryNotification.setVisibility(View.GONE);
    }

    protected void showError(int resourceId) {
        super.showError(resourceId);
        hideIcon();
        vPrimaryNotification.setVisibility(View.GONE);
        vSecondaryNotification.setVisibility(View.GONE);
    }

    private void showIcon() {
        vProgressText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_menu_checked_in), null, null, null);
    }

    private void hideIcon() {
        vProgressText.setCompoundDrawables(null, null, null, null);
    }
}
