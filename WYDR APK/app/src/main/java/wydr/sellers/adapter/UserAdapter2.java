package wydr.sellers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wydr.sellers.R;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.emojicon.EmojiconTextView;

/**
 * Created by surya on 11/12/15.
 */
public class UserAdapter2 extends CursorAdapter implements Serializable {

    final Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatNew = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat formatDatabase = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat formatResult = new SimpleDateFormat("hh:mm aa");
    ListLoader loader;
    private SparseBooleanArray mSelectedItemsIds;

    public UserAdapter2(Context context, Cursor cursor)
    {

        super(context, cursor, 0);
        loader = new ListLoader(context.getApplicationContext());
        mSelectedItemsIds = new SparseBooleanArray();
    }

    private ViewHolder createViewHolder(View view)
    {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.txtUserName);
        viewholder.date = (TextView) view.findViewById(R.id.txtdate);
        viewholder.status = (EmojiconTextView) view.findViewById(R.id.txtStatus);
        viewholder.count = (TextView) view.findViewById(R.id.txtMsgCount);
        viewholder.profile = (ImageView) view.findViewById(R.id.viewUserThumb);
        return viewholder;
    }

    public void bindView(View view, Context context, Cursor cursor)
    {
        //cursor.setNotificationUri(context.getContentResolver(), ChatProvider.CONTENT_URI);
        ViewHolder holder = createViewHolder(view);
        int iUserId = cursor.getColumnIndexOrThrow("user_id");
        int iDisplay = cursor.getColumnIndexOrThrow("display_name");
        int iPhone = cursor.getColumnIndexOrThrow("user_phone");
        int iChatMessage = cursor.getColumnIndexOrThrow("message");
        int iMessageBroadcast = cursor.getColumnIndexOrThrow("broadcast_message");
        int iProfilePic = cursor.getColumnIndexOrThrow("profile_pic");
        int iCreateMessage = cursor.getColumnIndexOrThrow("timestamp_message");
        int iCreateBroadcast = cursor.getColumnIndexOrThrow("timestamp_broadcast");
        int iSubject = cursor.getColumnIndexOrThrow("subject");
        int iChatUser = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_CHAT_USER);
        int iDirection = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_DIRECTION);
        int iUnread = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
        int iType = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_TYPE);
        int iCreated = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_CREATED);
        int iCompany = cursor.getColumnIndexOrThrow("company");
        int iTypeMsg = cursor.getColumnIndexOrThrow("type");
        // user detail start
        if (cursor.getString(iType).equals("chat")) {
            String name = cursor.getString(iDisplay);
            //    Log.d("ddd", "" + name);
            if (name != null) {
                /*if (name.length() > 1) {
                    holder.name.setText(MyTextUtils.toTitleCase(name) + ", " + cursor.getString(iCompany).toUpperCase());
                } else {*/
                    //holder.name.setText(MyTextUtils.toTitleCase(cursor.getString(iPhone)) + ", " + cursor.getString(iCompany).toUpperCase());
                    holder.name.setText(MyTextUtils.toTitleCase(cursor.getString(iCompany).toUpperCase()));
                //}

            }

            else {
                holder.name.setText(MyTextUtils.toTitleCase(cursor.getString(iPhone) + ", " + cursor.getString(iCompany)));
            }
            if (cursor.getString(iSubject) == null) {
                holder.status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.status.setText("");
            } else if (cursor.getString(iSubject).equals("text")) {
                if (cursor.getString(iDirection).equals(ChatUserSchema.INCOMING)) {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.inarrow_with_padding, 0, 0, 0);
                    if (cursor.getInt(iUnread) > 0) {
                        holder.status.setTypeface(null, Typeface.BOLD);
                    } else {
                        holder.status.setTypeface(null, Typeface.NORMAL);
                    }

                } else {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.out_arrow_with_padding, 0, 0, 0);
                    holder.status.setTypeface(null, Typeface.NORMAL);
                }
                holder.status.setText("" + cursor.getString(iChatMessage));
            } else if (cursor.getString(iSubject).equals("HiFi")) {
                if (cursor.getString(iDirection).equals(ChatUserSchema.INCOMING)) {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_five_in, 0, 0, 0);
                } else {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_five_out, 0, 0, 0);
                }

                holder.status.setText("");

            } else if (cursor.getString(iSubject).equals("query")) {
                if (cursor.getString(iDirection).equals(ChatUserSchema.INCOMING)) {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.query_in, 0, 0, 0);
                } else {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.query_out, 0, 0, 0);
                }

                holder.status.setText("");

            } else if (cursor.getString(iSubject).equals("img")) {
                if (cursor.getString(iDirection).equals(ChatUserSchema.INCOMING)) {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_in, 0, 0, 0);
                } else {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_out, 0, 0, 0);
                }
                holder.status.setText("");

            } else if (cursor.getString(iSubject).equals("product")) {
                //     holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.productbasket_with_padding, 0, 0, 0);
                if (cursor.getString(iDirection).equals(ChatUserSchema.INCOMING)) {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.product_in, 0, 0, 0);
                    if (cursor.getInt(iUnread) > 0) {
                        holder.status.setTypeface(null, Typeface.BOLD);
                    } else {
                        holder.status.setTypeface(null, Typeface.NORMAL);
                    }
                    //   holder.status.setTypeface(null, Typeface.BOLD);
                } else {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.product_out, 0, 0, 0);
                    holder.status.setTypeface(null, Typeface.NORMAL);
                }
                holder.status.setText("" + cursor.getString(iChatMessage));
            }
            if (cursor.getString(iSubject) == null)
            {
                try
                {
                    Date dataDate = formatDatabase.parse(cursor.getString(iCreated));
                    //Date date = formatNew.parse();
                    String dateString = format.format(dataDate);
                    Date past = format.parse(dateString);
                    Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                    holder.date.setText(dateFormat(dataDate, current, past));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            } else {
                try {
                    Date dataDate = formatDatabase.parse(cursor.getString(iCreateMessage));
                    //    Date date = formatNew.parse();
                    String dateString = format.format(dataDate);
                    Date past = format.parse(dateString);
                    Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                    holder.date.setText(dateFormat(dataDate, current, past));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (cursor.getInt(iUnread) > 0) {
                holder.count.setText("" + cursor.getInt(iUnread));
                holder.count.setVisibility(View.VISIBLE);
            } else {
                holder.count.setVisibility(View.GONE);
            }
            loader.DisplayImage(cursor.getString(iProfilePic), holder.profile, R.drawable.avtar);
        } else if (cursor.getString(iType).equals("broadcast")) {
            holder.profile.setImageResource(R.drawable.broadcast_icon);
            holder.name.setText(MyTextUtils.toTitleCase(cursor.getString(iChatUser)));
            holder.count.setVisibility(View.GONE);


            if (cursor.getString(iMessageBroadcast) != null) {
                String type = cursor.getString(iTypeMsg);
                Log.d("Type", type);
                if (type.equals("product")) {
                    holder.status.setText(cursor.getString(iMessageBroadcast));
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.productbasket_with_padding, 0, 0, 0);
                } else if (type.equals("img")) {
                    holder.status.setText("");
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera, 0, 0, 0);
                } else {
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.out_arrow_with_padding, 0, 0, 0);
                    holder.status.setText(cursor.getString(iMessageBroadcast));
                }

            } else {
                holder.status.setText("");
            }

            try {
                Date dataDate;
                if (cursor.getString(iCreateBroadcast) != null) {
                    dataDate = formatDatabase.parse(cursor.getString(iCreateBroadcast));
                } else {
                    dataDate = formatDatabase.parse(cursor.getString(iCreated));
                }

                //    Date date = formatNew.parse();
                String dateString = format.format(dataDate);
                Date past = format.parse(dateString);
                Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                holder.date.setText(dateFormat(dataDate, current, past));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        view.setBackgroundColor(mSelectedItemsIds.get(cursor.getPosition()) ? 0x99D9D9D9 : Color.TRANSPARENT);
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        return LayoutInflater.from(context).inflate(R.layout.user_layout, viewgroup, false);
    }

    private String dateFormat(Date past, Date current, Date last) {

        //int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
        int diffInDays = (int) ((current.getTime() - past.getTime()) / (1000 * 60 * 60 * 24));
        // System.out.println("Difference in Days : " + diffInDays);
        if (diffInDays == 1) {
            return "Yesterday";
        } else if (diffInDays == 0) {
            return "" + formatResult.format(last);
        } else {
            return "" + formatDate.format(last);
        }
        //return "";
    }

    private static class ViewHolder {

        TextView count;
        TextView date;
        TextView name;
        EmojiconTextView status;
        ImageView profile;

        private ViewHolder() {
        }

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

}
