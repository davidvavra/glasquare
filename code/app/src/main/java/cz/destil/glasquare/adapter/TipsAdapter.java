package cz.destil.glasquare.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

import cz.destil.glasquare.App;
import cz.destil.glasquare.api.Tips;
import cz.destil.glasquare.util.FormatUtils;

/**
 * Adapter for list of tips.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class TipsAdapter extends CardScrollAdapter {

    private List<Tips.Tip> mTips;

    public TipsAdapter(List<Tips.Tip> tips) {
        mTips = tips;
    }

    @Override
    public int getCount() {
        return mTips.size();
    }

    @Override
    public Object getItem(int i) {
        return mTips.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Card card = new Card(App.get());
        Tips.Tip tip = mTips.get(i);
        card.setText(tip.text);
        card.setTimestamp(FormatUtils.formatDate(tip.createdAt));
        return card.getView();
    }

    @Override
    public int getPosition(Object o) {
        return mTips.indexOf(o);
    }
}
