package cz.destil.glasquare.activity;

import com.google.android.glass.widget.CardScrollView;

import butterknife.InjectView;
import cz.destil.glasquare.R;

/**
 * Base activity which handles card scrolling.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
abstract public class CardScrollActivity extends ProgressActivity {

    @InjectView(R.id.card_scroll)
    CardScrollView vCardScroll;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_card_scroll;
    }

}
