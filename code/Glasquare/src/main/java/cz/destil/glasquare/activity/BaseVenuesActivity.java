package cz.destil.glasquare.activity;

import android.view.Menu;
import android.view.MenuItem;

import cz.destil.glasquare.R;
import cz.destil.glasquare.api.ExploreVenues;
import cz.destil.glasquare.util.IntentUtils;

/**
 * Activity handling common stuff for venue list and venue detail.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public abstract class BaseVenuesActivity extends BaseCardScrollActivity {

    protected ExploreVenues.Venue mSelectedVenue;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.venue, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSelectedVenue != null) {
            menu.findItem(R.id.menu_tips).setEnabled(mSelectedVenue.hasTips);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSelectedVenue != null) {
            switch (item.getItemId()) {
                case R.id.menu_navigate:
                    IntentUtils.launchNavigation(this, mSelectedVenue.latitude, mSelectedVenue.longitude);
                    return true;
                case R.id.menu_tips:
                    TipsActivity.call(this, mSelectedVenue.id);
                    return true;
                case R.id.menu_check_in:
                    CheckInActivity.call(this, mSelectedVenue.id, mSelectedVenue.name);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
