package cz.destil.glasquare.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.PowerManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;

import butterknife.InjectView;
import cz.destil.glasquare.R;

/**
 * Base activity which handles showing progress.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public abstract class ProgressActivity extends BaseActivity {

    @InjectView(R.id.progress_bar)
    protected ProgressBar vProgressBar;
    @InjectView(R.id.progress_text)
    protected TextView vProgressText;
    PowerManager.WakeLock wakeLock;

    protected void showProgress(int resourceId) {
        vProgressText.setText(resourceId);
        vProgressBar.setVisibility(View.VISIBLE);
        vProgressText.setVisibility(View.VISIBLE);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "progress");
        wakeLock.acquire();
    }

    protected void showSuccess(int resourceId) {
        vProgressText.setText(resourceId);
        vProgressBar.setVisibility(View.GONE);
        vProgressText.setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.SUCCESS);
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    protected void hideProgress() {
        vProgressBar.setVisibility(View.GONE);
        vProgressText.setVisibility(View.GONE);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.SUCCESS);
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    protected void showError(int resourceId) {
        vProgressText.setText(resourceId);
        vProgressBar.setVisibility(View.GONE);
        vProgressText.setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.ERROR);
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}
