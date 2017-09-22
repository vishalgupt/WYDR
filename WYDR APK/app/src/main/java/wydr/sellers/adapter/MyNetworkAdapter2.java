package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.activities.Controller;

/**
 * Created by surya on 27/8/15.
 */
public class MyNetworkAdapter2 extends CursorAdapter {
    ListLoader loader;
    private SparseBooleanArray mSelectedItemsIds;
    Controller application;
    Tracker mTracker;
    Activity activity;

    public MyNetworkAdapter2(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mSelectedItemsIds = new SparseBooleanArray();
        loader = new ListLoader(context.getApplicationContext());
        activity = (Activity)context;
    }

    @Override
    public int getCount() {
        return super.getCount();
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
        int iUrl = cursor.getColumnIndexOrThrow(NetSchema.USER_IMAGE);

        if (cursor.getString(iName).length() > 2) {

            holder.name.setText(cursor.getString(iName));

        } else {
            holder.name.setText(cursor.getString(iNo));
        }

        holder.company.setText(cursor.getString(iComapny).toUpperCase());
        holder.chat.setTag(cursor.getString(iNet));

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                application = (Controller) activity.getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("My Connections", "Move", "ChatActivity");
                /*******************************ISTIAQUE***************************************/

                context.startActivity(new Intent(context, ChatActivity.class).putExtra("user", "" + holder.chat.getTag()));
            }
        });
        view.setBackgroundColor(mSelectedItemsIds.get(cursor.getPosition()) ? 0x99999999 : Color.TRANSPARENT);
        loader.DisplayImage(cursor.getString(iUrl), holder.profile, R.drawable.avtar);

    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.MyNetworkName);
        viewholder.company = (TextView) view.findViewById(R.id.MyNetworkNumber);
        viewholder.chat = (ImageView) view.findViewById(R.id.MyNetworkViewChat);
        viewholder.profile = (ImageView) view.findViewById(R.id.profileMyNet);
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
        ImageView chat, profile;

        private ViewHolder() {
        }

    }

}
