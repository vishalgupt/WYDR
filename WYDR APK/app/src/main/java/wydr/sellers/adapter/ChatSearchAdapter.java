package wydr.sellers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import wydr.sellers.R;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 12/8/15.
 */
public class ChatSearchAdapter extends CursorAdapter {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat formatResultprevious = new SimpleDateFormat("dd/MM/yy");
    SimpleDateFormat formatResulttoday = new SimpleDateFormat("hh:mm aa");
    ListLoader loader;
    Context mcontext;
    Helper helper = new Helper();

    public ChatSearchAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mcontext = context;
        loader = new ListLoader(context.getApplicationContext());
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.txtChatName);
        viewholder.date = (TextView) view.findViewById(R.id.txtChatDate);
        viewholder.profile = (ImageView) view.findViewById(R.id.imageView6);
        //  viewholder.msg = (TextView) view.findViewById(R.id.txtChatText);

        return viewholder;
    }

    public void bindView(View view, Context context, Cursor cursor) {
        // cursor.setNotificationUri(context.getContentResolver(), ChatProvider.CONTENT_URI);
        ViewHolder holder = createViewHolder(view);
        int iId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_ROWID);
        int iName = cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER);
        int iMsg = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG);
        int iDate = cursor.getColumnIndexOrThrow(ChatSchema.KEY_CREATED);
        //Log.d("here", cursor.getString(iName));
        //  Log.d("param", cursor.getString(iId));
//        Log.i("KEY_ROWID", cursor.getString(cursor.getColumnIndexOrThrow(ChatSchema.KEY_ROWID)));
//        Log.i("iReceiver", cursor.getString(cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER)));
//        Log.i("KEY_MSG", cursor.getString(cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG)));
//        Log.i("lgin id ", helper.getDefaults("login_id",mcontext));
        if (cursor.getString(cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER)).equalsIgnoreCase(helper.getDefaults("login_id", mcontext) + "@" + AppUtil.SERVER_NAME)) {
            iName = cursor.getColumnIndexOrThrow(ChatSchema.KEY_SENDER);
        }
        Cursor CurorNet = context.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{
                cursor.getString(iName)}, null);
        CurorNet.moveToFirst();
        int iPhone = CurorNet.getColumnIndexOrThrow(NetSchema.USER_COMPANY);
        int iUrl = CurorNet.getColumnIndexOrThrow(NetSchema.USER_IMAGE);
//        Cursor cursorName = context.getContentResolver().query(ChatProvider.BOOK_URI, null, ContactsDb.KEY_ID + " Like ?", new String[]{
//               "%" +CurorNet.getString(iPhone)+"%"}, null);
        int iUName = CurorNet.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
//        cursorName.moveToFirst();
        String message = cursor.getString(iMsg);
        if (message.trim().length() > 40) {
            message = message.substring(0, 40) + "...";
        }
        if (CurorNet.getCount() > 0) {
            if (CurorNet.getString(iUName).length() > 2) {
                holder.name.setText(Html.fromHtml("<b>" + CurorNet.getString(iUName) + " : </b>" + message));
            } else {
                holder.name.setText(Html.fromHtml("<b>" + helper.ConvertCamel(CurorNet.getString(iPhone)) + " : </b>" + message));
            }
        }

        try {
            Date currentdate = new Date();
            Date date = format.parse(cursor.getString(iDate));
            long diff = currentdate.getTime() - date.getTime();
            if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 1)
                holder.date.setText(formatResultprevious.format(date));
            else
                holder.date.setText(formatResulttoday.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        loader.DisplayImage(CurorNet.getString(iUrl), holder.profile, R.drawable.avtar);

    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        return LayoutInflater.from(context).inflate(R.layout.search_result_layout, viewgroup, false);
    }

    private static class ViewHolder {

        //TextView count;
        TextView date;
        TextView name;
        ImageView profile;
        //   TextView msg;

        private ViewHolder() {
        }

    }
}
