
package wydr.sellers.emojicon;


        import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import wydr.sellers.R;
import wydr.sellers.emojicon.emoji.Emojicon;


/**
 * Created by surya on 20/7/15.
 */
class EmojiAdapter2 extends ArrayAdapter<Emojicon> {
    EmojiconGridView.OnEmojiconClickedListener emojiClickListener;

    public EmojiAdapter2(Context context, List<Emojicon> data) {
        super(context, R.layout.emojicon_item2, data);
    }

    public EmojiAdapter2(Context context, Emojicon[] data) {
        super(context, R.layout.emojicon_item2, data);
    }

    public void setEmojiClickListener(EmojiconGridView.OnEmojiconClickedListener listener) {
        this.emojiClickListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item2, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (TextView) v.findViewById(R.id.emojicon_icon2);
            v.setTag(holder);
        }
        Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getEmoji());
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiClickListener.onEmojiconClicked(getItem(position));
            }
        });
        return v;
    }

    class ViewHolder {
        TextView icon;
    }
}