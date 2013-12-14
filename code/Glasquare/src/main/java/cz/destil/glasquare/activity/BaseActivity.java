package cz.destil.glasquare.activity;

import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Base activity which handles common stuff like analytics.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class BaseActivity extends Activity {

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
}
