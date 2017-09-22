package wydr.sellers.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.MessageEventManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wydr.sellers.R;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.CustomMultiPartEntity;
import wydr.sellers.acc.HiFiSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.acc.QuerySchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.activities.XmppConnection;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.ImageLoader;
import wydr.sellers.network.SessionManager;
import wydr.sellers.openfire.JSONMessage;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 20/7/15.
 */
public class ChatAdapter extends CursorAdapter {

    //  SimpleDateFormat format;
    ImageLoader imageLoader;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatNew = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatResult = new SimpleDateFormat("hh:mm aa");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
    ConnectionDetector cd;
    Helper helper = new Helper();
    long totalSize = 0;
    JSONMessage jsonMessage;
    private Context appContext;
    private Cursor cr;
    private int layout;
    private Context mContext;
    private SparseBooleanArray mSelectedItemsIds;
    private MessageEventManager messageEventManager;
    ListView listView;

    public ChatAdapter(Context context, Cursor cursor, MessageEventManager messageeventmanager, ListView view) {
        super(context, cursor, true);
        //    format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        mSelectedItemsIds = new SparseBooleanArray();
        messageEventManager = messageeventmanager;
        mContext = context;
        listView = view;
        cd = new ConnectionDetector(mContext);
        imageLoader = new ImageLoader(mContext);
        jsonMessage = new JSONMessage();
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item_chat_message, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        try {
            int position = cursor.getPosition();

            cursor.moveToPosition(super.getCount() - position - 1);
            final ViewHolder holder;
            holder = createViewHolder(view);
            final int iMsg = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG);
            int iDate = cursor.getColumnIndexOrThrow(ChatSchema.KEY_CREATED);
            final int iId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_ROWID);
            int iMe = cursor.getColumnIndexOrThrow(ChatSchema.IsMe);
            int iDis = cursor.getColumnIndexOrThrow(ChatSchema.KEY_DIS);
            final int iMsgId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG_ID);
            int iSender = cursor.getColumnIndexOrThrow(ChatSchema.KEY_SENDER);
            final int iRec = cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER);
            int iError = cursor.getColumnIndexOrThrow(ChatSchema.IsError);
            int iDownload = cursor.getColumnIndexOrThrow(ChatSchema.IsDownload);
            int iStatus = cursor.getColumnIndexOrThrow("status");
            int iBroadcast = cursor.getColumnIndexOrThrow(ChatSchema.BROADCAST);
            holder.accept.setTag(cursor.getString(iMsgId));
            holder.reject.setTag(cursor.getString(iMsgId));
            boolean myMsg;
            Spanned alignText = null;
            int myPro;
            //  Log.d("Broadcast value", " IS " + cursor.getInt(iBroadcast));


            if (cursor.getInt(iMe) == 2) {
                myMsg = true;
                myPro = 2;

                switch (cursor.getInt(iDis)) {
                    case -1:
                        if (cursor.getInt(iBroadcast) == 1) {


                            holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, R.drawable.message_not_sent, 0);
                            alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                        } else {
                            alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                            holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.message_not_sent, 0);
                        }


                        break;
                    case 1:
                        if (cursor.getInt(iBroadcast) == 1) {


                            holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, R.drawable.message_sent, 0);
                            alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                        } else {
                            alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                            holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.message_sent, 0);
                        }

                        // holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.message_sent, 0);

                        break;
                    case 2:
                        if (cursor.getInt(iBroadcast) == 1) {


                            holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, R.drawable.message_read, 0);
                            alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                        } else {
                            alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                            holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.message_read, 0);
                        }

                        // holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.message_read, 0);

                        break;
                }

            } else if (cursor.getInt(iMe) == 11) {
                myPro = 11;
//                Bitmap bitmap = decodeFile(new File(cursor.getString(iMsg)));
//                Log.d("File path ", cursor.getString(iMsg));
                if (cursor.getString(iMsg).contains("http"))
                    new DownloadFileFromURL(holder.bar_right, cursor.getString(iMsgId), holder.img_right).execute(cursor.getString(iMsg));
                else
                    holder.img_right.setImageBitmap(decodeSampledBitmapFromResource(cursor.getString(iMsg), 100, 100));
                // loadBitmap(cursor.getString(iMsg), holder.img_right);
                //    holder.img_right.setImageBitmap(bitmap);
                holder.img_right.setTag(cursor.getString(iMsg));
                holder.retryRight.setTag(cursor.getPosition());
                if (cursor.getInt(iDownload) == 1) {
                    holder.bar_right.setVisibility(View.GONE);
                    holder.retryRight.setVisibility(View.GONE);
                } else if (cursor.getInt(iError) == 1 && cursor.getInt(iDownload) == 0) {
                    holder.bar_right.setVisibility(View.GONE);
                    holder.retryRight.setVisibility(View.VISIBLE);
                    holder.retryRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                                cursor.moveToPosition((Integer) holder.retryRight.getTag());
                                Log.i("ChatAdapter**-", cursor.getString(iMsg));
                                if (cursor.getString(iMsg).contains("staging.wydr.in"))
                                    new DownloadFileFromURL(holder.bar_right, cursor.getString(iMsgId), holder.img_right).execute(cursor.getString(iMsg));
                                else
                                    new Uploadtask(holder.bar_right, cursor.getString(iId), cursor.getString(iRec), holder.retryRight, cursor.getString(iMsgId)).execute(cursor.getString(iMsg));

                            } else {

                                holder.bar_right.setVisibility(View.GONE);
                                holder.retryRight.setVisibility(View.VISIBLE);
                                ContentValues contentValues2 = new ContentValues();
                                contentValues2.put(ChatSchema.IsDownload, 0);
                                contentValues2.put(ChatSchema.IsError, 1);
                                mContext.getContentResolver().update(ChatProvider.CONTENT_URI, contentValues2, ChatSchema.KEY_ROWID + "=? ", new String[]{cursor.getString(iId)});

                                mContext.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                            }
                        }
                    });
                } else {
                    // Log.d("Here", "In Upload");

                    //Log.d("log", "" + cursor.getString(iStatus));
                    if (cd.isConnectingToInternet()) {
                        holder.bar_right.setVisibility(View.VISIBLE);
                        if (cursor.getInt(iStatus) == 0) {
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put(ChatSchema.STATUS, 1);
                            int count = mContext.getContentResolver().update(ChatProvider.CONTENT_URI, contentValues2, ChatSchema.KEY_ROWID + "=? ", new String[]{cursor.getString(iId)});

                            mContext.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                            Log.d("log", "count update" + count);
                            Log.d("log", "cursor value" + cursor.getInt(iStatus));
                            new Uploadtask(holder.bar_right, cursor.getString(iId), cursor.getString(iRec), holder.retryRight, cursor.getString(iMsgId)).execute(cursor.getString(iMsg));


                        }

                    } else {
                        holder.bar_right.setVisibility(View.GONE);
                        holder.retryRight.setVisibility(View.VISIBLE);
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(ChatSchema.IsDownload, 0);
                        contentValues2.put(ChatSchema.IsError, 1);
                        mContext.getContentResolver().update(ChatProvider.CONTENT_URI, contentValues2, ChatSchema.KEY_ROWID + "=? ", new String[]{cursor.getString(iId)});

                        mContext.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    }
                }

            } else if (cursor.getInt(iMe) == 12) {
                myPro = 12;

                String pathName = cursor.getString(iMsg);

                if (cursor.getInt(iDownload) == 1) {
                    holder.bar_left.setVisibility(View.GONE);
                    holder.img_left.setTag(cursor.getString(iMsg));
                    if (pathName != null && pathName != "") {
                        holder.img_left.setImageBitmap(decodeSampledBitmapFromResource(pathName, 100, 100));

                    }

                } else {
                    holder.bar_left.setVisibility(View.VISIBLE);
                    if (cd.isConnectingToInternet()) {
                        new DownloadFileFromURL(holder.bar_left, cursor.getString(iMsgId), holder.img_left).execute(cursor.getString(iMsg));
                    }


                }

            } else if (cursor.getInt(iMe) == 21) {
                myPro = 21;
                String msgID = cursor.getString(iMsgId);
                holder.chat_query_layout.setBackgroundResource(R.drawable.chat_right);
                Cursor c = context.getContentResolver().query(ChatProvider.QUERY_URI, null, QuerySchema.KEY_ROW_ID + "=?", new String[]{msgID}, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    int iText = c.getColumnIndexOrThrow(QuerySchema.QUERY_TEXT);
                    int iUrl = c.getColumnIndexOrThrow(QuerySchema.QUERY_URL);
                    holder.queryText.setText(Html.fromHtml(c.getString(iText)));
                    imageLoader.DisplayImage(c.getString(iUrl), holder.query_thumb);
                }

            } else if (cursor.getInt(iMe) == 22)

            {
                myPro = 22;
                String msgID = cursor.getString(iMsgId);
                holder.chat_query_layout.setBackgroundResource(R.drawable.chat_left);
                Cursor c = context.getContentResolver().query(ChatProvider.QUERY_URI, null, QuerySchema.KEY_ROW_ID + "=?", new String[]{msgID}, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    int iText = c.getColumnIndexOrThrow(QuerySchema.QUERY_TEXT);
                    int iUrl = c.getColumnIndexOrThrow(QuerySchema.QUERY_URL);
                    holder.queryText.setText(Html.fromHtml(c.getString(iText)));
                    imageLoader.DisplayImage(c.getString(iUrl), holder.query_thumb);
                }

            } else if (cursor.getInt(iMe) == 3)

            {
                myPro = 3;
                String msgID = cursor.getString(iMsgId);
                holder.product_left.setBackgroundResource(R.drawable.chat_right);
                Cursor c = context.getContentResolver().query(ChatProvider.URI_PRODUCT, null, ProSchema.KEY_ROW_ID + "=?", new String[]{msgID}, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    int iName = c.getColumnIndexOrThrow(ProSchema.PRODUCT_NAME);
                    int iCode = c.getColumnIndexOrThrow(ProSchema.PRODUCT_CODE);
                    int iPrice = c.getColumnIndexOrThrow(ProSchema.PRODUCT_PRICE);
                    int iMrp = c.getColumnIndexOrThrow(ProSchema.PRODUCT_MRP);
                    int iUrl = c.getColumnIndexOrThrow(ProSchema.PRODUCT_URL);
                    int iMoq = c.getColumnIndexOrThrow(ProSchema.PRODUCT_MOQ);
                    int iDate5 = c.getColumnIndexOrThrow(ProSchema.KEY_CREATED);

                    holder.title.setText(c.getString(iName));
                    holder.code.setText(c.getString(iCode));
                    holder.mrp.setText("MRP : " + context.getResources().getString(R.string.rs) + "" + c.getString(iMrp) + context.getString(R.string.pc));
                    holder.price.setText("SP : " + context.getResources().getString(R.string.rs) + "" + c.getString(iPrice) + context.getString(R.string.pc));
                    holder.moq.setText("MOQ : " + c.getString(iMoq));
                    imageLoader.DisplayImage(c.getString(iUrl), holder.imageView);
                    if (c.getString(iMrp).equalsIgnoreCase("") && c.getString(iPrice).equalsIgnoreCase("")) {
                        holder.mrp.setVisibility(View.INVISIBLE);
                        holder.price.setVisibility(View.INVISIBLE);
                    } else {
                        holder.mrp.setVisibility(View.VISIBLE);
                        holder.price.setVisibility(View.VISIBLE);
                    }
                    try {
                        //   Date date = format.parse(c.getString(iDate5));
                        //    Log.d("date ", "" + date);
                        Date date = formatNew.parse(c.getString(iDate5));
                        Date past = format.parse(c.getString(iDate5));

                        Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                        //  holder.date.setText(dateFormat(date, current));
                        Log.d("date ", "" + date);
                        Log.d("current ", "" + current);
                        Log.d("past ", "" + past);
                        holder.date.setText(dateFormat(date, current, past));


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    if (cursor.getInt(iDis) == 0) {

                        messageEventManager.sendDisplayedNotification(cursor.getString(iSender), cursor.getString(iMsgId));
                        ContentValues contentvalues = new ContentValues();
                        contentvalues.put("display", 2);

                        int l = context.getContentResolver().update(ChatProvider.CONTENT_URI, contentvalues, "msg_id=?", new String[]{
                                cursor.getString(iMsgId)});

                        context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);

                    }
                }
            } else if (cursor.getInt(iMe) == 19)

            {
                myPro = 19;
                holder.product_left.setBackgroundResource(R.drawable.chat_left);
                String msgID = cursor.getString(iMsgId);
                Cursor c = context.getContentResolver().query(ChatProvider.URI_PRODUCT, null, ProSchema.KEY_ROW_ID + "=?", new String[]{msgID}, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    int iName = c.getColumnIndexOrThrow(ProSchema.PRODUCT_NAME);
                    int iCode = c.getColumnIndexOrThrow(ProSchema.PRODUCT_CODE);
                    int iPrice = c.getColumnIndexOrThrow(ProSchema.PRODUCT_PRICE);
                    int iMrp = c.getColumnIndexOrThrow(ProSchema.PRODUCT_MRP);
                    int iUrl = c.getColumnIndexOrThrow(ProSchema.PRODUCT_URL);
                    int iMoq = c.getColumnIndexOrThrow(ProSchema.PRODUCT_MOQ);

                    int iDate5 = c.getColumnIndexOrThrow(ProSchema.KEY_CREATED);

                    holder.title.setText(c.getString(iName));
                    holder.code.setText(c.getString(iCode));
                    holder.mrp.setText("MRP : " + context.getResources().getString(R.string.rs) + " " + c.getString(iMrp) + context.getString(R.string.pc));
                    holder.price.setText("SP : " + context.getResources().getString(R.string.rs) + " " + c.getString(iPrice) + context.getString(R.string.pc));
                    holder.moq.setText("MOQ : " + c.getString(iMoq));
                    imageLoader.DisplayImage(c.getString(iUrl), holder.imageView);
                    if (c.getString(iMrp).equalsIgnoreCase("") && c.getString(iPrice).equalsIgnoreCase("")) {
                        holder.mrp.setVisibility(View.INVISIBLE);
                        holder.price.setVisibility(View.INVISIBLE);
                    } else {
                        holder.mrp.setVisibility(View.VISIBLE);
                        holder.price.setVisibility(View.VISIBLE);
                    }
                    try {
                        Date date = formatNew.parse(c.getString(iDate5));
                        Date past = format.parse(c.getString(iDate5));

                        Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                        //  holder.date.setText(dateFormat(date, current));
                        Log.d("date ", "" + date);
                        Log.d("current ", "" + current);
                        Log.d("past ", "" + past);
                        holder.date.setText(dateFormat(date, current, past));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    if (cursor.getInt(iDis) == 0) {

                        messageEventManager.sendDisplayedNotification(cursor.getString(iSender), cursor.getString(iMsgId));
                        ContentValues contentvalues = new ContentValues();
                        contentvalues.put("display", 2);

                        int l = context.getContentResolver().update(ChatProvider.CONTENT_URI, contentvalues, "msg_id=?", new String[]{
                                cursor.getString(iMsgId)});
                        //     Log.d("Count ", (new StringBuilder()).append("").append(l).toString());
                        context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);

                    }
                }
            } else if (cursor.getInt(iMe) == 4) {
                myPro = 4;
                String msgID = cursor.getString(iMsgId);
                Cursor c = context.getContentResolver().query(ChatProvider.HiFi_URI, null, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgID}, null);

                if (c.getCount() > 0) {
                    c.moveToFirst();
                    int iName = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_NAME);
                    int iCode = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_CODE);
                    int iPrice = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_PRICE);
                    int iQty = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_QUANTITY);
                    int iAccept = c.getColumnIndexOrThrow(HiFiSchema.ACCEPT);
                    int iDate5 = c.getColumnIndexOrThrow(HiFiSchema.KEY_CREATED);
                    int iRequestFor = c.getColumnIndexOrThrow(HiFiSchema.REQUEST_FOR);
                    holder.codeHiFi.setText("Product Code : " + c.getString(iCode));
                    //holder.mrp.setText("MRP : " + context.getResources().getString(R.string.rs) + " " + c.getString(iPrice));
                    holder.priceHiFi.setText("PRICE : " + context.getResources().getString(R.string.rs) + " " + c.getString(iPrice));

                    holder.qtyHiFi.setText("QUANTITY : " + c.getString(iQty));
                    int accept = c.getInt(iAccept);
                    if (accept == 5) {
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " " + c.getString(iName) + " sent you a deal ");
                        holder.acceptText.setText("Accepted");
                        holder.reject.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.txtDivider.setVisibility(View.GONE);
//                        holder.acceptText.setClickable(false);
//                        holder.declineText.setClickable(false);
                        Date past = format.parse(c.getString(iDate5));
                        Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                        if (c.getInt(iRequestFor) == 0) {
                            if (dateFormat(past, current)) {
                                holder.acceptText.setTextColor(context.getResources().getColor(R.color.primary_500));
                                holder.acceptText.setText("Pay Now");
                                // holder.acceptText.setClickable(true);
                            } else {
                                //  holder.acceptText.setClickable(false);
                                holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                                holder.acceptText.setText("Accepted");
                                //  holder.declineText.setText("Decline");
                            }
                        } else {
                            //   holder.acceptText.setClickable(false);
                            holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                            holder.acceptText.setText("Accepted");
                        }
                    } else if (accept == 6) {
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " " + c.getString(iName) + " sent you a deal ");
                        holder.acceptText.setText("Accept");
                        holder.declineText.setText("Rejected");
                        //    holder.acceptText.setClickable(false);
                        holder.txtDivider.setVisibility(View.GONE);
                        //   holder.declineText.setClickable(false);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.VISIBLE);

                    } else if (accept == 9) {
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.acceptText.setText("Accept");
                        holder.declineText.setText("Rejected");
                        //  holder.acceptText.setClickable(false);
                        holder.txtDivider.setVisibility(View.GONE);
                        // holder.declineText.setClickable(false);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.VISIBLE);

                    } else if (accept == 1) {
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " " + c.getString(iName) + " sent you a deal ");
                        holder.acceptText.setText("Accept");
                        holder.declineText.setText("Reject");
                        // holder.acceptText.setClickable(true);
                        //   holder.declineText.setClickable(true);
                        holder.txtDivider.setVisibility(View.VISIBLE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.primary_500));
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.primary_500));
                    } else if (accept == 0) {
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.acceptText.setText("Accept");
                        holder.declineText.setText("Reject");
                        //   holder.acceptText.setClickable(true);
                        //   holder.declineText.setClickable(true);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.txtDivider.setVisibility(View.GONE);
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.primary_500));
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                    } else if (accept == 3) {
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.reject.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.txtDivider.setVisibility(View.GONE);
                        // holder.reject.setVisibility(View.VISIBLE);

                        //   holder.acceptText.setClickable(false);
                        //   holder.declineText.setClickable(false);

                        Date past = format.parse(c.getString(iDate5));
                        Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                        if (c.getInt(iRequestFor) == 1) {
                            if (dateFormat(past, current)) {
                                holder.acceptText.setTextColor(context.getResources().getColor(R.color.primary_500));
                                holder.acceptText.setText("Pay Now");
                                //   holder.acceptText.setClickable(true);
                            } else {
                                //  holder.acceptText.setClickable(false);
                                holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                                holder.acceptText.setText("Accepted");
                                //  holder.declineText.setText("Decline");
                            }
                        } else {
                            // holder.acceptText.setClickable(false);
                            holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                            holder.acceptText.setText("Accepted");
                        }

                    } else if (accept == 7) {
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.reject.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.txtDivider.setVisibility(View.GONE);
                        // holder.reject.setVisibility(View.VISIBLE);
                        //holder.acceptText.setClickable(false);
                        // holder.declineText.setClickable(false);
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.acceptText.setText("Order Placed");

                    } else if (accept == 4) {
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " " + c.getString(iName) + " sent you a deal ");
                        //holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.acceptText.setText("Accept");
                        holder.declineText.setText("Rejected");
                        //    holder.acceptText.setClickable(false);
                        holder.txtDivider.setVisibility(View.GONE);
                        //   holder.declineText.setClickable(false);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.VISIBLE);


                    } else if (accept == 2) {
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.acceptText.setText("Accepted");
                        holder.reject.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.txtDivider.setVisibility(View.GONE);
                    } else {
                        holder.declineText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        // holder.acceptText.setVisibility(View.GONE);
                        holder.acceptText.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.titleHiFi.setText(context.getResources().getString(R.string.high_five) + " you sent a deal" + " to " + c.getString(iName));
                        holder.declineText.setText("Rejected");
                        //   holder.acceptText.setClickable(false);
                        holder.txtDivider.setVisibility(View.GONE);
                        //   holder.declineText.setClickable(false);
                        //  holder.reject.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.VISIBLE);
                        // holder.acceptText.setVisibility(View.GONE);
                    }

                    //  holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    if (cursor.getInt(iDis) == 0) {

                        // messageEventManager.sendDisplayedNotification(cursor.getString(iSender), cursor.getString(iMsgId));
                        ContentValues contentvalues = new ContentValues();
                        contentvalues.put("display", 2);
                        //   Log.d("there come ", "" + cursor.getPosition());
                        int l = context.getContentResolver().update(ChatProvider.CONTENT_URI, contentvalues, "msg_id=?", new String[]{
                                cursor.getString(iMsgId)});
                        //     Log.d("Count ", (new StringBuilder()).append("").append(l).toString());
                        context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);

                    }
                }

            } else {
                myMsg = false;
                myPro = 1;
                holder.txtInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (cursor.getInt(iDis) == 0) {
                    if (messageEventManager != null) {
                        //  messageEventManager.
                        try {
                            messageEventManager.sendDisplayedNotification(cursor.getString(iSender), cursor.getString(iMsgId));
                        } catch (Exception e) {
                            messageEventManager = new MessageEventManager(XmppConnection.getInstance().getConnection());
                        }
                        ContentValues contentvalues = new ContentValues();
                        contentvalues.put("display", 2);
                        //   Log.d("there come ", "" + cursor.getPosition());
                        int l = context.getContentResolver().update(ChatProvider.CONTENT_URI, contentvalues, "msg_id=?", new String[]{
                                cursor.getString(iMsgId)});
                        // Log.d("Count ", (new StringBuilder()).append("").append(l).toString());
                        context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                    }
                }


                //  Log.d("Broadcast value", " IS " + cursor.getInt(iBroadcast));
                if (cursor.getInt(iBroadcast) == 1) {

                    holder.txtInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, 0, 0);
                    //holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, 0, 0);
                    holder.time_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, 0, 0);
                    // holder.time_right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, 0, 0);
                    alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                } else {
                    alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                    holder.txtInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    // holder.txtInfo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    holder.time_left.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    //holder.time_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            setAlignment(holder, myPro);

            // holder.txtMessage.setText(cursor.getString(iMsg));
            if (cursor.getInt(iBroadcast) == 1) {
                holder.time_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, 0, 0);
                holder.time_right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.broadcast_small, 0, 0, 0);
                alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
            } else {
                alignText = Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                holder.time_left.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.time_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            holder.txtMessage.setText(TextUtils.concat(cursor.getString(iMsg), " ", alignText));
            try

            {
                Date date = formatNew.parse(cursor.getString(iDate));
                Date past = format.parse(cursor.getString(iDate));

                Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                //  holder.date.setText(dateFormat(date, current));
                holder.txtInfo.setText(dateFormat(date, current, past));
                holder.txtInfo2.setText(dateFormat(date, current, past));
                holder.time_left.setText(dateFormat(date, current, past));
                holder.time_right.setText(dateFormat(date, current, past));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.txtMessage2.setText(TextUtils.concat(cursor.getString(iMsg), " ", alignText));
//            if(getListView().isItemChecked(super.getCount() - cursor.getPosition() - 1)){
//                view.setBackgroundColor(Color.RED);
//            }else{
//                view.setBackgroundColor(Color.TRANSPARENT);
//            }

            view.setBackgroundColor(mSelectedItemsIds.get(super.getCount() - cursor.getPosition()) ? 0x99D9D9D9 : Color.TRANSPARENT);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();

        holder.txtMessage = (TextView) v.findViewById(R.id.txtMyMsg);
        holder.txtMessage2 = (TextView) v.findViewById(R.id.txtMyMsg2);
        holder.chat_left = (RelativeLayout) v.findViewById(R.id.chat_left_layout);
        holder.chat_right = (RelativeLayout) v.findViewById(R.id.chat_right_layout);
        holder.product_left = (RelativeLayout) v.findViewById(R.id.bubble_layout);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtMyTime);
        holder.txtInfo2 = (TextView) v.findViewById(R.id.txtMyTime2);
        holder.product_layout = (LinearLayout) v.findViewById(R.id.id_layout_chat);
        holder.title = (TextView) v.findViewById(R.id.txtTitleAttachChat);
        holder.date = (TextView) v.findViewById(R.id.txtChatDateTime);
        holder.price = (TextView) v.findViewById(R.id.txtSellAttechChat);
        holder.mrp = (TextView) v.findViewById(R.id.txtMRPAttechChat);
        holder.code = (TextView) v.findViewById(R.id.txtCodeAttechChat);
        holder.moq = (TextView) v.findViewById(R.id.txtChatMOQ);
        holder.imageView = (ImageView) v.findViewById(R.id.productThumbChat);
        // HiFi stauff
        holder.hifi = (LinearLayout) v.findViewById(R.id.hifiLayout);
        holder.titleHiFi = (TextView) v.findViewById(R.id.txtHifiName);
        holder.priceHiFi = (TextView) v.findViewById(R.id.txtHifiPrice);
        holder.codeHiFi = (TextView) v.findViewById(R.id.txtHifiCode);
        holder.qtyHiFi = (TextView) v.findViewById(R.id.txtHifiQty);
        holder.accept = (LinearLayout) v.findViewById(R.id.layoutAccept);
        holder.reject = (LinearLayout) v.findViewById(R.id.layoutDecline);
        holder.acceptText = (TextView) v.findViewById(R.id.txtAccept);
        holder.declineText = (TextView) v.findViewById(R.id.txtDecline);
        holder.txtDivider = (TextView) v.findViewById(R.id.txtDevider);

        holder.image_left = (RelativeLayout) v.findViewById(R.id.image_left_layout);
        holder.image_right = (RelativeLayout) v.findViewById(R.id.image_right_layout);
        holder.img_left = (ImageView) v.findViewById(R.id.txtMyImage);
        holder.img_right = (ImageView) v.findViewById(R.id.txtMyImg2);
        holder.time_left = (TextView) v.findViewById(R.id.txtTimeImg);
        holder.time_right = (TextView) v.findViewById(R.id.txtTimeI2mg);
        holder.bar_left = (ProgressBar) v.findViewById(R.id.progressBarImg);
        holder.bar_right = (ProgressBar) v.findViewById(R.id.progressBarImg2);

        holder.query_layout = (LinearLayout) v.findViewById(R.id.query_chat);
        holder.chat_query_layout = (RelativeLayout) v.findViewById(R.id.chat_query_layout);
        holder.query_thumb = (ImageView) v.findViewById(R.id.image_chat);
        holder.queryText = (TextView) v.findViewById(R.id.txtChatQueryText);
        holder.retryRight = (TextView) v.findViewById(R.id.retryRight);

        return holder;
    }

    private void setAlignment(ViewHolder holder, int myPro) {
        if (myPro == 1) {
            holder.hifi.setVisibility(View.GONE);
            holder.product_layout.setVisibility(View.GONE);
            holder.chat_left.setVisibility(View.VISIBLE);
            holder.chat_right.setVisibility(View.GONE);
            holder.image_right.setVisibility(View.GONE);
            holder.image_left.setVisibility(View.GONE);
            holder.query_layout.setVisibility(View.GONE);

        } else if (myPro == 2) {
            holder.hifi.setVisibility(View.GONE);
            holder.product_layout.setVisibility(View.GONE);
            holder.chat_left.setVisibility(View.GONE);
            holder.chat_right.setVisibility(View.VISIBLE);
            holder.image_right.setVisibility(View.GONE);
            holder.image_left.setVisibility(View.GONE);
            holder.query_layout.setVisibility(View.GONE);

        } else if (myPro == 3 || myPro == 19) {
            holder.hifi.setVisibility(View.GONE);
            holder.product_layout.setVisibility(View.VISIBLE);
            holder.chat_left.setVisibility(View.GONE);
            holder.chat_right.setVisibility(View.GONE);
            holder.image_right.setVisibility(View.GONE);
            holder.image_left.setVisibility(View.GONE);
            holder.query_layout.setVisibility(View.GONE);

        } else if (myPro == 4) {
            holder.product_layout.setVisibility(View.GONE);
            holder.chat_left.setVisibility(View.GONE);
            holder.chat_right.setVisibility(View.GONE);
            holder.hifi.setVisibility(View.VISIBLE);
            holder.image_right.setVisibility(View.GONE);
            holder.query_layout.setVisibility(View.GONE);
            holder.image_left.setVisibility(View.GONE);
        } else if (myPro == 11) {
            holder.product_layout.setVisibility(View.GONE);
            holder.chat_left.setVisibility(View.GONE);
            holder.chat_right.setVisibility(View.GONE);
            holder.hifi.setVisibility(View.GONE);
            holder.image_right.setVisibility(View.VISIBLE);
            holder.image_left.setVisibility(View.GONE);
            holder.query_layout.setVisibility(View.GONE);

        } else if (myPro == 12) {
            holder.product_layout.setVisibility(View.GONE);
            holder.chat_left.setVisibility(View.GONE);
            holder.chat_right.setVisibility(View.GONE);
            holder.hifi.setVisibility(View.GONE);
            holder.image_right.setVisibility(View.GONE);
            holder.image_left.setVisibility(View.VISIBLE);
            holder.query_layout.setVisibility(View.GONE);
        } else if (myPro == 21 || myPro == 22) {
            holder.product_layout.setVisibility(View.GONE);
            holder.chat_left.setVisibility(View.GONE);
            holder.chat_right.setVisibility(View.GONE);
            holder.hifi.setVisibility(View.GONE);
            holder.image_right.setVisibility(View.GONE);
            holder.image_left.setVisibility(View.GONE);
            holder.query_layout.setVisibility(View.VISIBLE);
        }
    }

    private String dateFormat(Date past, Date current, Date last) {
        //     Log.i("here Date current ", "  past " + past + " *** " + current.toString());
        int diffInDays = (int) ((current.getTime() - past.getTime()) / (1000 * 60 * 60 * 24));
        // System.out.println("Difference in Days : " + diffInDays);
        if (diffInDays == 1) {
            return "Yesterday";
        } else if (diffInDays == 0) {
            return "" + formatResult.format(last);
        } else {
            return "" + formatDate.format(past);
        }
        //return "";
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

    //
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 1000;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;
            //Log.i("fff" ,f.toString() +"//"+o.outHeight+"//"+o.outHeight);
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            } //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return b;
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(super.getCount() - position - 1);
    }

    @Override
    protected void onContentChanged() {
        SparseBooleanArray checkedItems = getSelectedIds();
        // removeSelection();
        for (int i = 0; i < getCount(); i++) {
            if (checkedItems.get(i)) {
                selectView(i, true);
            } else {
                selectView(i, false);
            }
        }
        notifyDataSetChanged();

        super.onContentChanged();
    }

    private boolean dateFormat(Date past, Date current) {

        //int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
        int diffInDays = (int) ((current.getTime() - past.getTime()) / (1000 * 60 * 60 * 24));
        // System.out.println("Difference in Days : " + diffInDays);
        return diffInDays <= 1;
        //return "";
    }

    private static class ViewHolder {
        // LinearLayout ;
        public TextView txtMessage, txtMessage2;
        public TextView txtInfo, txtInfo2;
        public ImageView imageView;
        public LinearLayout product_layout, hifi, accept, reject;
        public TextView titleHiFi, priceHiFi, codeHiFi, qtyHiFi, acceptText, declineText, txtDivider;
        public TextView title, date, price, mrp, code, moq;
        RelativeLayout chat_left, chat_right, image_left, image_right, product_left;
        ImageView img_left, img_right;
        TextView time_left, time_right, retryRight;
        ProgressBar bar_right, bar_left;
        RelativeLayout chat_query_layout;
        LinearLayout query_layout;
        ImageView query_thumb;
        TextView queryText;
    }

    private class Uploadtask extends AsyncTask<String, Integer, String> {
        ProgressBar pb;
        String id;
        String receiver;
        int statusCode;
        String responseString = "no";
        TextView textView;
        String msgId;

        public Uploadtask(ProgressBar bar, String id, String receiver, TextView textView, String msg_id) {
            this.pb = bar;
            this.id = id;
            this.receiver = receiver;
            this.textView = textView;
            this.msgId = msg_id;
            Log.d("log", "come in constructor");

        }

        @Override
        protected void onPreExecute() {

            pb.setProgress(0);
            pb.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            //   tv.setText("shuru");
            Log.d("log", "come in onpre");
            Log.d("Insert DB", "3");

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pb.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            return upload(params[0]);
        }

        private String upload(String filename) {

            //long totalSize;

            File sourceFile = new File(filename);
            if (!sourceFile.isFile()) {
                return "not a file";
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AppUtil.URL + "api/attachment");
            httppost.addHeader("Connection", "Keep-Alive");
            httppost.addHeader("Accept", "*/*");
            httppost.addHeader("Content-Type", "multipart/form-data");
            httppost.addHeader("Authorization", helper.getB64Auth(mContext));


            try {
                CustomMultiPartEntity entity = new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                entity.addPart("type", new StringBody("image"));
                entity.addPart("uploadedfile", new FileBody(sourceFile));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                // HttpEntity r_entity = response.getEntity();

                statusCode = response.getStatusLine().getStatusCode();
                Log.d("Status Code", "" + response.getStatusLine().getStatusCode());
                responseString = EntityUtils.toString(r_entity);
                android.util.Log.d("response", responseString);
                android.util.Log.d("response", "" + response.getStatusLine().getStatusCode());
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }


        @Override
        protected void onPostExecute(String result) {
            // tv.setText(result);
            super.onPostExecute(result);
            String stringUrl = null;
            Chat newChat = null;
            Log.d("log", "come in post");
            if (statusCode == 401) {

                final SessionManager sessionManager = new SessionManager(mContext);
                Handler mainHandler = new Handler(Looper.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        sessionManager.logoutUser();
                    } // This is your code
                };
                mainHandler.post(myRunnable);
            }
            if (statusCode == 201) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ChatSchema.IsDownload, 1);
                contentValues.put(ChatSchema.IsError, 0);
                mContext.getContentResolver().update(ChatProvider.CONTENT_URI, contentValues, ChatSchema.KEY_ROWID + "=? ", new String[]{id});
                mContext.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);


                if (XmppConnection.getInstance().getConnection().getChatManager() != null) {
                    ChatManager chatmanager = XmppConnection.getInstance().getConnection().getChatManager();
                    newChat = chatmanager.createChat(receiver, new MessageListener() {
                        public void processMessage(Chat chat, Message message) {
                            System.out.println("Received message:njjjj " + message);
                        }
                    });
                }
                try {
                    JSONObject json = new JSONObject(result);
                    stringUrl = json.getString("attachment_url");
                    jsonMessage.sendImage(newChat, stringUrl, msgId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                pb.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                //  responseString = EntityUtils.toString(r_entity);
            } else {
                pb.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(ChatSchema.IsDownload, 0);
                contentValues2.put(ChatSchema.IsError, 1);
                mContext.getContentResolver().update(ChatProvider.CONTENT_URI, contentValues2, ChatSchema.KEY_ROWID + "=? ", new String[]{id});
//                responseString = "Error occurred! Http Status Code: "
//                        + statusCode;
                mContext.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            }

            // }

        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressBar pb;
        String id;
        String receiver;
        int statusCode;
        ImageView imageView;
        String responseString;

        public DownloadFileFromURL(ProgressBar bar, String id, ImageView view) {
            this.pb = bar;
            this.id = id;
            this.receiver = receiver;
            this.imageView = view;

        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setProgress(0);
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
//            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                Calendar calendar = Calendar.getInstance();
                // External sdcard location
                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        AppUtil.IMAGE_DIRECTORY_SEND);

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        android.util.Log.d("TAG", "Oops! Failed create "
                                + AppUtil.IMAGE_DIRECTORY_SEND + " directory");
                        return null;
                    }
                }

                // Create a media file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(calendar.getTime());
                File mediaFile = null;
                // if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
                responseString = mediaStorageDir.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg";
                // Output stream to write file
                OutputStream output = new FileOutputStream(mediaFile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pb.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         */
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pb.setVisibility(View.GONE);
            if (responseString != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ChatSchema.IsDownload, 1);
                contentValues.put(ChatSchema.IsError, 0);
                contentValues.put(ChatSchema.KEY_MSG, responseString);

                mContext.getContentResolver().update(ChatProvider.CONTENT_URI, contentValues, ChatSchema.KEY_MSG_ID + "=? ", new String[]{id});
                imageView.setImageBitmap(decodeSampledBitmapFromResource(responseString, 100, 100));
                // loadBitmap(responseString, imageView);
//                Bitmap d = decodeFile(new File(responseString));
//                imageView.setImageBitmap(d);
                mContext.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                // Displaying downloaded image into image view
            }

        }

    }

    private ListView getListView() {
        return listView;
    }
}

