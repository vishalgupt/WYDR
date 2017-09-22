package wydr.sellers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;

/**
 * Created by surya on 27/8/15.
 */
public class AddChatAdapter extends CursorAdapter {

    ListLoader loader;

    public AddChatAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        loader = new ListLoader(context.getApplicationContext());

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.add_chat_layout, parent, false);
        // return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = createViewHolder(view);
        int iName2 = cursor.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
        int iCompany = cursor.getColumnIndexOrThrow(NetSchema.USER_COMPANY);
        int iNo = cursor.getColumnIndexOrThrow(NetSchema.USER_PHONE);
        int iUser = cursor.getColumnIndexOrThrow(NetSchema.USER_IMAGE);

        if (cursor.getString(iName2).length() > 2) {
            holder.name.setText(cursor.getString(iName2));
        } else {

            holder.name.setText(cursor.getString(iNo));
        }
        holder.company.setText(cursor.getString(iCompany));
        loader.DisplayImage(cursor.getString(iUser), holder.profile, R.drawable.avtar);

    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.txtAddUserName);
        viewholder.company = (TextView) view.findViewById(R.id.txtAddCompany);
        viewholder.profile = (ImageView) view.findViewById(R.id.viewAddUserThumb);
        return viewholder;
    }

    private static class ViewHolder {


        TextView name;
        TextView company;
        ImageView profile;

        private ViewHolder() {
        }

    }

}
