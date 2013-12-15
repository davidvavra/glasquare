package cz.destil.glasquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;

import cz.destil.glasquare.api.Auth;

/**
 * Base activity which handles common stuff like analytics.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
abstract public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    /**
     * Override in children
     */
    protected void loadData() {

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Auth.saveToken(data.getStringExtra(LoginActivity.EXTRA_TOKEN));
                loadData();
            } else {
                finish();
            }
        }
    }
}
