package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.media.CameraManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.InjectView;
import cz.destil.glasquare.App;
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
    @InjectView(R.id.background)
    ImageView vBackground;

    private String mShout = "#throughglass"; // default shout
    private boolean mAddingPhoto = false;
    private File mPhoto;
    private CheckIns.CheckInResponse mCheckInResponse;

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
        showShout();
        showGracePeriod(R.string.checking_in, new GracePeriodListener() {
            @Override
            public void onGracePeriodCompleted() {
                checkIn();
            }
        });
    }

    private void checkIn() {
        final String venueId = getIntent().getStringExtra(EXTRA_VENUE_ID);
        final Location location = LocationUtils.getLastLocation();
        final String ll = LocationUtils.getLatLon(location);
        int accuracy = (int) location.getAccuracy();
        int altitude = (int) location.getAltitude();
        final String token = Auth.getToken();
        showProgress(R.string.checking_in);
        showShout();
        Api.get().create(CheckIns.class).add(token, venueId, ll, mShout, accuracy, altitude, new Callback<CheckIns.CheckInResponse>() {
            @Override
            public void success(final CheckIns.CheckInResponse checkInResponse, Response response) {
                mCheckInResponse = checkInResponse;
                if (mAddingPhoto) {
                    ImageUtils.processPictureWhenReady(CheckInActivity.this, mPhoto, new ImageUtils.OnPictureReadyListener() {
                        @Override
                        public void onPictureReady() {
                            new BaseAsyncTask() {
                                @Override
                                public void inBackground() {
                                    ImageUtils.resize(mPhoto);
                                }

                                @Override
                                public void postExecute() {
                                    addPhoto();
                                }
                            }.start();
                        }
                    });
                } else {
                    showCheckInComplete();
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

    private void addPhoto() {
        String checkInId = mCheckInResponse.getCheckInId();
        TypedFile typedFile = new TypedFile("image/jpeg", mPhoto);
        final String token = Auth.getToken();
        Api.get().create(Photos.class).add(token, checkInId, typedFile, new Callback<Photos.PhotoAddResponse>() {
            @Override
            public void success(Photos.PhotoAddResponse photoAddResponse, Response response) {
                showCheckInComplete();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showCheckInComplete();
            }
        });
    }

    private void showCheckInComplete() {
        if (mCheckInResponse != null) {
            showSuccess(R.string.checked_in);
            if (mCheckInResponse.getPrimaryNotification() != null) {
                vPrimaryNotification.setVisibility(View.VISIBLE);
                vPrimaryNotification.setText(mCheckInResponse.getPrimaryNotification());
            }
            if (mCheckInResponse.getSecondaryNotification() != null) {
                vSecondaryNotification.setVisibility(View.VISIBLE);
                vSecondaryNotification.setText(mCheckInResponse.getSecondaryNotification());
            }
            vBackground.setImageBitmap(null);
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
                mMenuItemSelected = true;
                mPhoto = null;
                mAddingPhoto = false;
                IntentUtils.takePicture(this);
                return true;
            case R.id.menu_shout:
                mMenuItemSelected = true;
                IntentUtils.startSpeechRecognition(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String text = IntentUtils.processSpeechRecognitionResult(requestCode, resultCode, data);
        if (text != null) {
            mShout = text;
            showShout();
        } else if (requestCode == IntentUtils.TAKE_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            mAddingPhoto = true;
            mPhoto = new File(data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH));
            File thumbnail = new File(data.getStringExtra(CameraManager.EXTRA_THUMBNAIL_FILE_PATH));
            Picasso.with(App.get()).load(thumbnail).into(vBackground);
        }
        restartGrace();
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showShout() {
        if (!TextUtils.isEmpty(mShout)) {
            vPrimaryNotification.setText(mShout);
            vPrimaryNotification.setVisibility(View.VISIBLE);
        }
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

    @Override
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
