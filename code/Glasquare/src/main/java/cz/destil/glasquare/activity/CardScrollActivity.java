package cz.destil.glasquare.activity;

import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.widget.CardScrollAdapter;
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

    public void showContent(final CardScrollAdapter adapter, final CardSelectedListener listener) {
        vCardScroll.setAdapter(adapter);
        vCardScroll.activate();
        if (listener != null) {
            vCardScroll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    listener.onCardSelected(adapter.getItem(position));
                }
            });
        }
        hideProgress();
    }

    public interface CardSelectedListener {
        void onCardSelected(Object item);
    }

}
