package cz.destil.glasquare.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.InjectView;
import com.google.android.glass.app.Card;
import com.google.android.glass.media.CameraManager;
import com.google.android.glass.timeline.TimelineManager;
import com.squareup.picasso.Picasso;
import cz.destil.glasquare.App;
import cz.destil.glasquare.R;
import cz.destil.glasquare.api.*;
import cz.destil.glasquare.util.*;
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
	public static String EXTRA_VENUE_NAME = "venue_name";

	@InjectView(R.id.primary_notification)
	TextView vPrimaryNotification;
	@InjectView(R.id.secondary_notification)
	TextView vSecondaryNotification;
	@InjectView(R.id.background)
	ImageView vBackground;

	private String mShout = "#throughglass"; // default shout
	private boolean mAddingPhoto = false;
	private File mPhoto;
	private boolean mTwitter = false;
	private boolean mFacebook = false;
	private CheckIns.CheckInResponse mCheckInResponse;

	public static void call(Activity activity, String venueId, String venueName) {
		Intent intent = new Intent(activity, CheckInActivity.class);
		intent.putExtra(EXTRA_VENUE_ID, venueId);
		intent.putExtra(EXTRA_VENUE_NAME, venueName);
		activity.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_check_in;
	}

	@Override
	protected void loadData() {
		showCheckInInfo();
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
		showProgress(R.string.checking_in);
		showCheckInInfo();
		String broadcast = getBroadcast();
		Api.get().create(CheckIns.class).add(venueId, ll, mShout, broadcast, accuracy, altitude, new Callback<CheckIns.CheckInResponse>() {
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

	private String getBroadcast() {
		String broadcast = "public";
		if (mTwitter) {
			broadcast += ",twitter";
		}
		if (mFacebook) {
			broadcast += ",facebook";
		}
		return broadcast;
	}

	private void addPhoto() {
		String checkInId = mCheckInResponse.getCheckInId();
		TypedFile typedFile = new TypedFile("image/jpeg", mPhoto);
		Api.get().create(Photos.class).add(checkInId, typedFile, new Callback<Photos.PhotoAddResponse>() {
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
			addStaticCard();
		}
	}

	private void addStaticCard() {
		String venueId = getIntent().getStringExtra(EXTRA_VENUE_ID);
		final String venueName = getIntent().getStringExtra(EXTRA_VENUE_NAME);
		Api.get().create(Tips.class).get(venueId, new Callback<Tips.TipsResponse>() {
				@Override
				public void success(Tips.TipsResponse tipsResponse, Response response) {
					Card card = new Card(App.get());
					String tip = App.get().getString(R.string.no_tips_here);
					if (tipsResponse.getTips().size() > 0) {
						tip = "Tip: \"" + tipsResponse.getTips().get(0).text + "\"";
					}
					card.setText(App.get().getString(R.string.static_card_text, venueName, tip));
					card.setFootnote(FormatUtils.formatDateTime()+" via Glasquare");
					if (mAddingPhoto) {
						card.addImage(Uri.parse(mPhoto.toURI().toString()));
					}
					TimelineManager.from(App.get()).insert(card);
				}

				@Override
				public void failure(RetrofitError error) {
					// ignore
				}
			}
		);
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
			case R.id.menu_twitter:
				if (mTwitter) {
					mTwitter = false;
					item.setTitle(R.string.share_to_twitter);
				} else {
					mTwitter = true;
					item.setTitle(R.string.dont_share_to_twitter);
				}
				showCheckInInfo();
				return true;
			case R.id.menu_facebook:
				if (mFacebook) {
					mFacebook = false;
					item.setTitle(R.string.share_to_facebook);
				} else {
					mFacebook = true;
					item.setTitle(R.string.dont_share_to_facebook);
				}
				showCheckInInfo();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String text = IntentUtils.processSpeechRecognitionResult(requestCode, resultCode, data);
		if (text != null) {
			mShout = text;
			showCheckInInfo();
		} else if (requestCode == IntentUtils.TAKE_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
			mAddingPhoto = true;
			mPhoto = new File(data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH));
			File thumbnail = new File(data.getStringExtra(CameraManager.EXTRA_THUMBNAIL_FILE_PATH));
			Picasso.with(App.get()).load(thumbnail).into(vBackground);
		}
		restartGrace();
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void showCheckInInfo() {
		String additional = "";
		if (mTwitter) {
			additional += "+Twitter";
		}
		if (mTwitter && mFacebook) {
			additional += ", ";
		}
		if (mFacebook) {
			additional += "+Facebook";
		}
		if (!TextUtils.isEmpty(additional)) {
			vPrimaryNotification.setText(mShout + " (" + additional + ")");
		} else {
			vPrimaryNotification.setText(mShout);
		}
		vPrimaryNotification.setVisibility(View.VISIBLE);
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
