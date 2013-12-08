package cz.destil.glasquare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.glasquare.App;
import cz.destil.glasquare.R;
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
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(App.get()).inflate(R.layout.view_tip, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Tips.Tip tip = mTips.get(i);
        holder.text.setText(tip.text);
        holder.createdAt.setText(FormatUtils.formatDate(tip.createdAt));
        if (tip.photoUrl != null) {
            Picasso.with(App.get()).load(tip.photoUrl).into(holder.image);
        }

        return view;
    }

    @Override
    public int findIdPosition(Object o) {
        return -1;
    }

    @Override
    public int findItemPosition(Object o) {
        return mTips.indexOf(o);
    }

    static class ViewHolder {
        @InjectView(R.id.text)
        TextView text;
        @InjectView(R.id.createdAt)
        TextView createdAt;
        @InjectView(R.id.image)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
