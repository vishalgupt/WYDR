package wydr.sellers.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.MessageEventManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wydr.sellers.R;
import wydr.sellers.acc.BitmapWorkerTask;
import wydr.sellers.acc.BroadcastMessage;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.CustomMultiPartEntity;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.activities.XmppConnection;
import wydr.sellers.emojicon.EmojiconTextView;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.ImageLoader;
import wydr.sellers.network.SessionManager;
import wydr.sellers.openfire.SLog;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 11/12/15.
 */
public class BroadcastAdapter extends android.support.v4.widget.CursorAdapter implements Serializable {

    SimpleDateFormat formatNew = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatResult = new SimpleDateFormat("hh:mm aa");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Helper helper=new Helper();
    long totalSize = 0;
    String ids;
    XMPPConnection mConnection;
    ImageLoader loader;
    ConnectionDetector cd;
    MessageEventManager messageEventManager;
    Chat chat;
    Context context;
    //  ImageLoader imageLoader;

    public BroadcastAdapter(Context context, Cursor cursor, String ids) {

        super(context, cursor, 0);
        this.ids = ids;
        this.context=context;
        loader = new ImageLoader(context.getApplicationContext());
        cd = new ConnectionDetector(mContext);

    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.chat_cart = (RelativeLayout) view.findViewById(R.id.chat_right_layout);
        viewholder.image_cart = (RelativeLayout) view.findViewById(R.id.image_right_layout);
        viewholder.product_cart = (LinearLayout) view.findViewById(R.id.id_layout_chat);
        viewholder.message = (EmojiconTextView) view.findViewById(R.id.txtMyMsg2);
        viewholder.time = (TextView) view.findViewById(R.id.txtMyTime2);
        viewholder.chat_cart = (RelativeLayout) view.findViewById(R.id.chat_right_layout);
        // product
        viewholder.title = (TextView) view.findViewById(R.id.txtTitleAttachChat);
        viewholder.date = (TextView) view.findViewById(R.id.txtChatDateTime);
        viewholder.price = (TextView) view.findViewById(R.id.txtSellAttechChat);
        viewholder.mrp = (TextView) view.findViewById(R.id.txtMRPAttechChat);
        viewholder.code = (TextView) view.findViewById(R.id.txtCodeAttechChat);
        viewholder.time_right = (TextView) view.findViewById(R.id.txtTimeI2mg);
        viewholder.bar_right = (ProgressBar) view.findViewById(R.id.progressBarImg2);
        viewholder.moq = (TextView) view.findViewById(R.id.txtChatMOQ);
        viewholder.imageView = (ImageView) view.findViewById(R.id.productThumbChat);
        viewholder.img_right = (ImageView) view.findViewById(R.id.txtMyImg2);
        viewholder.retryRight = (TextView) view.findViewById(R.id.retryRight);

        return viewholder;
    }

    public void bindView(View view, final Context context, final Cursor cursor) {

        final ViewHolder holder = createViewHolder(view);

        final int iMsg = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_MSG);
        final int iMsgID = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_MSG_ID);
        int iDate = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_CREATED);
        int iError = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_ERROR);
        int iType = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_TYPE);
        int iDownload = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_DOWNLOAD);
        int iStatus = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_STATUS);
        final int iRID = cursor.getColumnIndexOrThrow(BroadcastMessage.KEY_ROWID);
        String type = cursor.getString(iType);
        if (type.equals("text")) {
            holder.product_cart.setVisibility(View.GONE);
            holder.image_cart.setVisibility(View.GONE);
            holder.chat_cart.setVisibility(View.VISIBLE);
            holder.message.setText(TextUtils.concat(cursor.getString(iMsg), " ", Html.fromHtml("&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;")));
            Date date = null;
            try {
                date = formatNew.parse(cursor.getString(iDate));
                Date past = format.parse(cursor.getString(iDate));
                Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                //  holder.date.setText(dateFormat(date, current));
                holder.time.setText(dateFormat(date, current, past));
//                if(cursor.getInt(iStatus)==0) {
//                    holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.out_arrow_with_padding, 0, 0, 0);
//                }else{
//                    holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.waiting_with_padding, 0, 0, 0);
//                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (type.equals("product")) {

            holder.image_cart.setVisibility(View.GONE);
            holder.chat_cart.setVisibility(View.GONE);
            holder.product_cart.setVisibility(View.VISIBLE);


            String msgID = cursor.getString(iMsgID);
            holder.product_cart.setBackgroundResource(R.drawable.chat_right);
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
                holder.mrp.setText("MRP : " + context.getResources().getString(R.string.rs) + " " + c.getString(iMrp));
                holder.price.setText("SP : " + context.getResources().getString(R.string.rs) + " " + c.getString(iPrice));
                holder.moq.setText("MOQ : " + c.getString(iMoq));
                loader.DisplayImage(c.getString(iUrl), holder.imageView);
                try {
                    Date date = format.parse(c.getString(iDate5));
                    //    Log.d("date ", "" + date);

                    holder.date.setText(formatResult.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                if (cursor.getInt(iStatus) == 0) {
//                    holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.out_arrow_with_padding, 0, 0, 0);
//                } else {
//                    holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.waiting_with_padding, 0, 0, 0);
//                }
//                if (cursor.getInt(iDis) == 0) {
//
//               //     messageEventManager.sendDisplayedNotification(cursor.getString(iSender), cursor.getString(iMsgId));
//                    ContentValues contentvalues = new ContentValues();
//                    contentvalues.put("display", 2);
//
//                    int l = context.getContentResolver().update(ChatProvider.CONTENT_URI, contentvalues, "msg_id=?", new String[]{
//                            cursor.getString(iMsgId)});
//
//                    context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
//                    context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
//
//                }
            }


        } else if (type.equals("img")) {
            holder.product_cart.setVisibility(View.GONE);
            holder.chat_cart.setVisibility(View.GONE);
            holder.image_cart.setVisibility(View.VISIBLE);


            //    Bitmap bitmap = decodeFile(new File(cursor.getString(iMsg)));
            Log.d("File path ", cursor.getString(iMsg));
            //   holder.img_right.setImageBitmap(bitmap);
            loadBitmap(cursor.getString(iMsg), holder.img_right);
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
                            Log.d("Insert DB", "2");
                            new Uploadtask(holder.bar_right, cursor.getString(iRID), holder.retryRight, cursor.getString(iMsgID)).execute(cursor.getString(iMsg));

                        } else {

                            holder.bar_right.setVisibility(View.GONE);
                            holder.retryRight.setVisibility(View.VISIBLE);
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put(ChatSchema.IsDownload, 0);
                            contentValues2.put(ChatSchema.IsError, 1);
                            mContext.getContentResolver().update(ChatProvider.BROADCAST_URI, contentValues2, BroadcastMessage.KEY_ROWID + "=? ", new String[]{cursor.getString(iRID)});

                            mContext.getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                        }
                    }
                });
            } else {
                Log.d("Here", "In Upload");
                holder.bar_right.setVisibility(View.VISIBLE);
                Log.d("log", "" + cursor.getString(iStatus));
                if (cd.isConnectingToInternet()) {
                    if (cursor.getInt(iStatus) == 0) {
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(BroadcastMessage.KEY_STATUS, 1);
                        int count = mContext.getContentResolver().update(ChatProvider.BROADCAST_URI, contentValues2, BroadcastMessage.KEY_ROWID + "=? ", new String[]{cursor.getString(iRID)});


                        mContext.getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                        Log.d("log", "count update" + count);
                        Log.d("log", "cursor value" + cursor.getInt(iStatus));
                        new Uploadtask(holder.bar_right, cursor.getString(iRID), holder.retryRight, cursor.getString(iMsgID)).execute(cursor.getString(iMsg));


                    }

                } else {
                    holder.bar_right.setVisibility(View.GONE);
                    holder.retryRight.setVisibility(View.VISIBLE);
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put(ChatSchema.IsDownload, 0);
                    contentValues2.put(ChatSchema.IsError, 1);
                    mContext.getContentResolver().update(ChatProvider.BROADCAST_URI, contentValues2, BroadcastMessage.KEY_ROWID + "=? ", new String[]{cursor.getString(iRID)});

                    mContext.getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                }
            }
            Date date = null;
            try {
                date = formatNew.parse(cursor.getString(iDate));
                Date past = format.parse(cursor.getString(iDate));
                Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
                //  holder.date.setText(dateFormat(date, current));
                holder.time_right.setText(dateFormat(date, current, past));
//                if(cursor.getInt(iStatus)==0) {
//                    holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.out_arrow_with_padding, 0, 0, 0);
//                }else{
//                    holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.waiting_with_padding, 0, 0, 0);
//                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        return LayoutInflater.from(context).inflate(R.layout.broadcast_layout, viewgroup, false);
    }

    private String dateFormat(Date past, Date current, Date last) {
        //  Log.d("here Date current ", "  past " + past + " " + current.toString());
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

    private void chattingSetup(String jid, Message message) {
        mConnection = XmppConnection.getInstance().getConnection();
        if (mConnection != null) {
            messageEventManager = new MessageEventManager(mConnection);
            ChatManager chatManager = mConnection.getChatManager();
            MessageListener msgListener = new MessageListener() {
                @Override
                public void processMessage(Chat arg0, final Message message) {
                    SLog.i("Broadcast Adapter", message.toXML());

                }
            };
            chat = chatManager.createChat(jid, msgListener);
            try {
                if (chat != null) {
                    chat.sendMessage(message);
                }
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            //n
        }
    }

    public void loadBitmap(String resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

//    private void chattingSetup(String builder) {
//        mConnection = XmppConnection.getInstance().getConnection();
//        if (mConnection != null) {
//            messageEventManager = new MessageEventManager(mConnection);
//            ChatManager chatManager = mConnection.getChatManager();
//            MessageListener msgListener = new MessageListener() {
//                @Override
//                public void processMessage(Chat arg0, final Message message) {
//                    SLog.i("TAG", message.toXML());
//
//                }
//            };
//            chat = chatManager.createChat(builder, msgListener);
//            //n
//        }
//    }

    private static class ViewHolder {

        public TextView title, date, price, mrp, code, moq;
        public ImageView imageView;
        TextView time;
        EmojiconTextView message;
        RelativeLayout image_cart, chat_cart;
        LinearLayout product_cart;
        ImageView img_right;
        TextView time_right, retryRight;
        ProgressBar bar_right;

        private ViewHolder() {
        }

    }

    private class Uploadtask extends AsyncTask<String, Integer, String> {
        ProgressBar pb;
        String id;
        String receiver;
        int statusCode;
        String responseString = "no";
        TextView textView;
        String msgId;

        public Uploadtask(ProgressBar bar, String id, TextView textView, String msg_id) {
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
            httppost.addHeader("Authorization", helper.getB64Auth(context));


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
            //   Chat newChat = null;
            Log.d("log", "come in post");
            if (statusCode == 401) {

                final SessionManager sessionManager = new SessionManager(context);
                Handler mainHandler = new Handler(Looper.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() { sessionManager.logoutUser();} // This is your code
                };
                mainHandler.post(myRunnable);
            }
            if (statusCode == 201) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(BroadcastMessage.KEY_DOWNLOAD, 1);
                contentValues.put(BroadcastMessage.KEY_ERROR, 0);
                mContext.getContentResolver().update(ChatProvider.BROADCAST_URI, contentValues, BroadcastMessage.KEY_ROWID + "=? ", new String[]{id});
                mContext.getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);


                try {
                    JSONObject json = new JSONObject(result);
                    stringUrl = json.getString("attachment_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message newMessage = new Message();
                newMessage.setBody(stringUrl);
                Log.d("Mesg id ", msgId);

                newMessage.setPacketID(msgId);
                MessageEventManager.addNotificationsRequests(newMessage,
                        true, true, true, true);
                newMessage.setSubject("img");
                //  newMessage.
                String[] jid = ids.split(",");
                for (int i = 0; i < jid.length; i++) {
                    chattingSetup(jid[i], newMessage);
                }
//                try {
//                    if (chat != null) {
//                        chat.sendMessage(newMessage);
//                    }
//
//                } catch (XMPPException e) {
//                    e.printStackTrace();
//                }
                // Server response


                pb.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                //  responseString = EntityUtils.toString(r_entity);
            } else {
                pb.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(BroadcastMessage.KEY_DOWNLOAD, 0);
                contentValues2.put(BroadcastMessage.KEY_ERROR, 1);
                mContext.getContentResolver().update(ChatProvider.BROADCAST_URI, contentValues2, BroadcastMessage.KEY_ROWID + "=? ", new String[]{id});
//                responseString = "Error occurred! Http Status Code: "
//                        + statusCode;
                mContext.getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
            }

            // }

        }
    }
}
