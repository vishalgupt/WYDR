package wydr.sellers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.slider.ContactsDb;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;


public class ContactAdapter extends CursorAdapter {

    ArrayList<String> checklist = new ArrayList<>();
    boolean[] items;
    ListLoader loader;
    Context context;
    String user_num;
    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();

    public ContactAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
        loader = new ListLoader(context.getApplicationContext());
        //  Log.d("cursor size  2", "" + cursor.getCount());
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();

        viewholder.textViewname = (TextView) view.findViewById(R.id.user_name);
        viewholder.chBox = (CheckBox) view.findViewById(R.id.checkBox);
        viewholder.invite = (Button) view.findViewById(R.id.contactinvitebutton);
        viewholder.textViewnumber = (TextView) view.findViewById(R.id.contactnumber);
        viewholder.image = (ImageView) view.findViewById(R.id.contacticon);
        getUserNum();
        return viewholder;
    }


    private void getUserNum() {
        Cursor user_cursor = context.getContentResolver().query(MyContentProvider.CONTENT_URI_Login, null, null, null, null);
        while (user_cursor.moveToNext()) {
            user_num = user_cursor.getString(user_cursor.getColumnIndexOrThrow(LoginDB.KEY_PHONE));

        }
        user_cursor.close();
    }



    public void bindView(final View view, final Context context, final Cursor cursor) {
        //   Log.d("cursor size", "" + cursor.getCount());

        int position =cursor.getPosition();

        if (!(itemChecked.size() > 0)) {
            for (int i = 0; i < ContactAdapter.this.getCount(); i++) {
                itemChecked.add(i, false); // initializes all items value with false
            }
        }

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cursor.setNotificationUri(context.getContentResolver(), ChatProvider.BOOK_URI);
                cursor.setNotificationUri(context.getContentResolver(), ChatProvider.NET_URI);
                            }
        }, 2000);

        final ViewHolder holder = createViewHolder(view);
        holder.invite.setTag(cursor.getPosition());
        int iName = cursor.getColumnIndexOrThrow(ContactsDb.KEY_NAME);
        int iStatus = cursor.getColumnIndexOrThrow(ContactsDb.KEY_STATUS);

        int iCMP = cursor.getColumnIndexOrThrow(ContactsDb.KEY_COMPANY);
        int iNum = cursor.getColumnIndexOrThrow(ContactsDb.KEY_ID);

        holder.textViewname.setText(cursor.getString(iName));
        if (cursor.getString(iCMP) != null && holder.textViewnumber.getText().length() != 0)
            holder.textViewnumber.setText(cursor.getString(iCMP).toUpperCase());
        else
            holder.textViewnumber.setText(cursor.getString(iNum));
        holder.chBox.setTag(cursor.getPosition());
        int status = cursor.getInt(iStatus);

        if (status == 1) {
            holder.invite.setVisibility(View.VISIBLE);
        } else if (status == 2) {
            holder.invite.setVisibility(View.GONE);
            holder.chBox.setVisibility(View.GONE);
        } else {
            holder.chBox.setVisibility(View.VISIBLE);
            holder.invite.setVisibility(View.GONE);
        }
        if (user_num.length() >= 10 && cursor.getString(iNum).length() >= 10) {

            if (user_num.substring(user_num.length() - 10).equalsIgnoreCase(cursor.getString(iNum).substring(cursor.getString(iNum).length() - 10))) {
                holder.chBox.setVisibility(View.GONE);
                holder.invite.setVisibility(View.GONE);
            }
        }


        holder.chBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int iId = cursor.getColumnIndexOrThrow(ContactsDb.KEY_ID);
                int position = (int) holder.chBox.getTag();
                cursor.moveToPosition(position);
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    //itemChecked.
                    itemChecked.set(position, true);
                    checklist.add(cursor.getString(iId));
                    holder.isSelect = true;

                } else if (!cb.isChecked()) {
                    itemChecked.set(position, false);
                    checklist.remove(cursor.getString(iId));
                    holder.isSelect = false;

                }
            }
        });
      holder.chBox.setChecked(itemChecked.get(cursor.getPosition()));


        //   loader.DisplayImage(cursor.getString(iUrl), holder.image, R.drawable.avtar);

    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        return LayoutInflater.from(context).inflate(R.layout.addcontactlistlayout, viewgroup, false);
    }

    public String getChecked()
    {
        String joined = TextUtils.join(";", checklist);
        return joined;
    }

    private static class ViewHolder {
        CheckBox chBox;
        TextView textViewname, textViewnumber;
        ImageView image;
        Button invite;
        boolean isSelect = false;

        private ViewHolder() {
        }

    }
}
