package cz.destil.glasquare.activity;

import android.speech.RecognizerIntent;

import java.util.ArrayList;

/**
 * Activity with handles search string recognition and then launches VenueActivity.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class SearchActivity extends BaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> voiceResults = getIntent().getExtras()
                .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        String query = null;
        if (voiceResults != null && voiceResults.size() > 0) {
            query = voiceResults.get(0);
        }
        VenueListActivity.call(this, VenueListActivity.TYPE_SEARCH, query);
        finish();
    }
}
