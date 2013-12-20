package cz.destil.glasquare.activity;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollView;

import butterknife.InjectView;
import cz.destil.glasquare.R;

/**
 * Base activity which handles card scrolling.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
abstract public class CardScrollActivity extends BaseActivity {

    @InjectView(R.id.card_scroll)
    CardScrollView vCardScroll;
    @InjectView(R.id.loading)
    TextView vLoading;
    @InjectView(R.id.progress)
    ProgressBar vProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_card_scroll;
    }

    protected void showError(int resourceId) {
        vLoading.setText(resourceId);
        vProgress.setVisibility(View.GONE);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.ERROR);
    }

    protected void hideProgress() {
        vLoading.setVisibility(View.GONE);
        vProgress.setVisibility(View.GONE);
    }

}
