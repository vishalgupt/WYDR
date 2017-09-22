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

public class ShareCatalogWithAdapter extends CursorAdapter {
    private SparseBooleanArray mSelectedItemsIds;

    public ShareCatalogWithAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_share_catalog_with, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), ChatProvider.NET_URI);
        final ViewHolder holder = createViewHolder(view);
//        int vendor_id = cursor.getColumnIndexOrThrow(NetSchema.USER_ID);
        view.setTag(cursor.getPosition());
        int iName = cursor.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
        final int iComapny = cursor.getColumnIndexOrThrow(NetSchema.USER_COMPANY);
        int iNo = cursor.getColumnIndexOrThrow(NetSchema.USER_PHONE);
        if (cursor.getString(iName).length() > 2) {
            holder.name.setText(cursor.getString(iName));
        } else {
            holder.name.setText(cursor.getString(iNo));
        }
        holder.company.setText(cursor.getString(iComapny));

        if (mSelectedItemsIds.get(cursor.getPosition())) {
            view.setBackgroundColor(0x99999999);
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
            holder.tick.setVisibility(View.GONE);
        }
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.itemshre_MyNetworkName);
        viewholder.company = (TextView) view.findViewById(R.id.itemshre_MyNetworkNumber);
        viewholder.tick = (ImageView) view.findViewById(R.id.itemshre_tick);
        return viewholder;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView name;
        TextView company;
        ImageView tick;

        private ViewHolder() {
        }
    }

}