package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.google.android.glass.app.Card;

import cz.destil.glasquare.R;

/**
 * Base activity which show how to login.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class LoginActivity extends BaseActivity {

    public static final int REQUEST_CODE = 42;
    public static final String EXTRA_TOKEN = "token";

    private Card mCard;

    public static void call(Activity activity) {
        activity.startActivityForResult(new Intent(activity, LoginActivity.class), REQUEST_CODE);
    }

    @Override
    public void onStart() {
        super.onStart();
        acquireWakeLock();
    }

    @Override
    public void onStop() {
        releaseWakeLock();
        super.onStop();
    }

    @Override
    protected View getLayoutView() {
        mCard = new Card(this);
        mCard.setText(R.string.login_hint);
        mCard.setFootnote(R.string.tap_to_scan_qr_code);
        return mCard.toView();
    }

    @Override
    protected void onTap() {
        QrScanActivity.call(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QrScanActivity.REQUEST_CODE) {
            if (resultCode==Activity.RESULT_OK) {
                String text = data.getStringExtra(QrScanActivity.EXTRA_TEXT);
                if (isValidQr(text)) {
                    String token = parseToken(text);
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_TOKEN, token);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    showStatus(R.string.invalid_qr);
                }
            } else {
                showStatus(R.string.qr_scanning_failed);
            }
        }
    }

    private void showStatus(int status) {
        mCard.setText(status);
        setContentView(mCard.toView());
    }

    private String parseToken(String text) {
        return text.split("4sq=")[1];
    }

    private boolean isValidQr(String text) {
        return text.startsWith("4sq=");
    }
}
