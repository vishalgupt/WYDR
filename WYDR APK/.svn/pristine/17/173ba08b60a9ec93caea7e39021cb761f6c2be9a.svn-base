package wydr.sellers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wydr.sellers.R;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.activities.ChatProvider;

/**
 * Created by surya on 15/10/15.
 */
public class ForwardAdapter extends CursorAdapter {
    private SparseBooleanArray mSelectedItemsIds;

    public ForwardAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_layout_mynetwork, parent, false);
        // return null;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), ChatProvider.NET_URI);

        final ViewHolder holder = createViewHolder(view);
        int iName = cursor.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
        int iNet = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
        final int iComapny = cursor.getColumnIndexOrThrow(NetSchema.USER_COMPANY);
        int iNo = cursor.getColumnIndexOrThrow(NetSchema.USER_PHONE);

        if (cursor.getString(iName).length() > 2) {

            holder.name.setText(cursor.getString(iName));

        } else {
            holder.name.setText(cursor.getString(iNo));
        }

        holder.company.setText(cursor.getString(iComapny));
        holder.chat.setVisibility(View.GONE);
        // holder.chat.setTag(cursor.getString(iNet));

//        holder.chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, ChatActivity.class).putExtra("user", "" + holder.chat.getTag()));
//            }
//        });
        view.setBackgroundColor(mSelectedItemsIds.get(cursor.getPosition()) ? 0x99D9D9D9 : Color.TRANSPARENT);
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.MyNetworkName);
        viewholder.company = (TextView) view.findViewById(R.id.MyNetworkNumber);
        viewholder.chat = (ImageView) view.findViewById(R.id.MyNetworkViewChat);

        return viewholder;
    }

    public void toggleSelection(int position) {

        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    //
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private static class ViewHolder {


        TextView name;
        TextView company;
        ImageView chat;

        private ViewHolder() {
        }

    }
}
