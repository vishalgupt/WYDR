package wydr.sellers.openfire;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.DefaultMessageEventRequestListener;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.MessageEventNotificationListener;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wydr.sellers.R;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.HiFiSchema;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.acc.QuerySchema;
import wydr.sellers.acc.UserSchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.Home;
import wydr.sellers.activities.XmppConnection;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

public class SmackService extends Service {
    //   private static final String IMAGE_DIRECTORY_NAME = "WYDR";
    public static String user = null;
    public static String pass = null;
    static XMPPConnection mConnection;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Helper helper = new Helper();
    PacketListener packetListener;
    SessionManager session;
    DefaultMessageEventRequestListener m_DefaultMessageEventRequestListener = new DefaultMessageEventRequestListener() {

        @Override
        public void deliveredNotificationRequested(String from, String packetID, MessageEventManager messageEventManager) {
            super.deliveredNotificationRequested(from, packetID, messageEventManager); //To change body of generated methods, choose Tools | Templates.

            Message message = new Message();
            message.setPacketID(packetID);
            message.setType(Message.Type.headline);
            message.setTo(AppUtil.SERVER_NAME);
            mConnection.sendPacket(message);
            Log.d("Come there ", "Baba dekh lo");
            // System.out.println("deliveredNotification 8");
            // messageEventManager.sendDisplayedNotification(from,packetID);
        }


        @Override
        public void displayedNotificationRequested(String from, String packetID, MessageEventManager messageEventManager) {
            super.displayedNotificationRequested(from, packetID, messageEventManager); //To change body of generated methods, choose Tools | Templates.
            //System.out.println("deliveredNotification 9");
            // messageEventManager.sendDisplayedNotification(from, packetID);
        }

        @Override
        public void offlineNotificationRequested(String from, String packetID, MessageEventManager messageEventManager) {
            super.offlineNotificationRequested(from, packetID, messageEventManager); //To change body of generated methods, choose Tools | Templates.
            //  System.out.println("deliveredNotification");
        }

        @Override
        public void composingNotificationRequested(String from, String packetID, MessageEventManager messageEventManager) {
            super.composingNotificationRequested(from, packetID, messageEventManager); //To change body of generated methods, choose Tools | Templates.
            // System.out.println("deliveredNotification 10");
            //  messageEventManager.sendDisplayedNotification(from,packetID);
        }


    };
    MessageEventNotificationListener m_messageEventNotificationListener = new MessageEventNotificationListener() {

        @Override
        public void deliveredNotification(String fromJID, String messageID) {
            final String _messageID = messageID;
            System.out.println("deliveredNotification 1   hhh  " + fromJID + " kkk " + messageID);
            ContentValues values = new ContentValues();
            values.put(ChatSchema.KEY_DIS, 1);
            int count = getApplicationContext().getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_MSG_ID + "=?", new String[]{messageID});
            //   Log.d("Count ", "" + count);
            getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
        }

        @Override
        public void displayedNotification(String string, String string1) {
            System.out.println("display Notification 2 " + " str 1" + string + "  str 2" + string1);

            ContentValues values = new ContentValues();
            values.put(ChatSchema.KEY_DIS, 2);
            int count = getApplicationContext().getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_MSG_ID + "=?", new String[]{string1});
            Log.d("Count ", "" + count);
            getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);

        }

        @Override
        public void composingNotification(String string, String string1) {
            //    System.out.println("deliveredNotification 5");
            //  aController.displayMessageOnScreen(getApplication(), true, false);

        }

        @Override
        public void offlineNotification(String string, String string1) {
            // System.out.println("deliveredNotification 6");

        }

        @Override
        public void cancelledNotification(String string, String string1) {
            //   System.out.println("deliveredNotification 7");
            //   aController.displayMessageOnScreen(getApplication(), false, true);
        }

    };
    Chat newChat;
    JSONMessage jsonMessage;
    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;
    private int logintime = 1000;
    private Timer tExit;
    private Controller aController = null;

    public SmackService() {
    }

    @Override
    public void onCreate() {
        String username = helper.getDefaults("login_id", getApplicationContext());
        String password = helper.getDefaults("login_password", getApplicationContext());
        user = username;
        pass = password;
        jsonMessage = new JSONMessage();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Start", "Service started");
        initConnection();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //+++  stop();
    }

    public void start() {
        Log.d("in connection ini", "1");
        if (!mActive) {
            mActive = true;
            Log.d("in connection ini", "2");
            // Create ConnectionThread Loop
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        mTHandler = new Handler();
                        Log.d("in connection ini", "4");
                        initConnection();
                        Looper.loop();
                    }

                });
                mThread.start();
            }

        }
    }


    private void initConnection() {
        Log.d("in connection ini", "3");
        new Connect().execute();

    }


    public void setConnection(final XMPPConnection connection) {
        //    final Calendar c = Calendar.getInstance();
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            packetListener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    {
                        if (packet instanceof Message) {
                            Message messageText = (Message) packet;

                            JSONObject content = null;
                            String subject = null;
                            if (messageText.getBody() != null) {
                                try {

                                    content = new JSONObject(messageText.getBody().toString());
                                    subject = content.optString("subject");
                              //     Log.i("**LOAD TEST" , content.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String fromName = StringUtils.parseBareAddress(messageText
                                        .getFrom());

                                Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName}, null);

                                if (cursor != null && cursor.getCount() > 0) {

                                    if (subject.equals("text")) {
                                        Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                                        int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                                        int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                                        cursor2.moveToFirst();
                                        String chatId;
                                        if (cursor2.getCount() > 0) {
                                            chatId = cursor2.getString(iRid);
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            int unread = cursor2.getInt(iUnread);
                                            cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                        } else {
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                                            cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                            long id = ContentUris.parseId(uri);
                                            chatId = "" + id;
                                        }

                                        ContentValues values = new ContentValues();
                                        values.put(ChatSchema.KEY_SENDER, fromName);
                                        values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(messageText
                                                .getTo()));

                                        values.put(ChatSchema.KEY_MSG, content.optString("text"));
                                        values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                        values.put(ChatSchema.IsMe, 1);
                                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                        values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                        values.put(ChatSchema.KEY_SUB, "text");
                                        values.put(ChatSchema.KEY_MSG_ID, messageText.getPacketID());
                                        //  values.put(ChatSchema.KEY_SUB, message.getSubject());
                                        values.put(ChatSchema.KEY_DIS, 0);
                                        values.put("status", 1);
                                        getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                        //ursor.setNotificationUri(context.getContentResolver(), ChatProvider.CONTENT_URI);

                                        Cursor c = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName.toString()}, null);
                                        int iPhone = c.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                                        int iName = c.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                                        int iCmp = c.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                                        //  int l = c.getColumnIndexOrThrow("created_at");
                                        c.moveToFirst();
                                        String name;
                                        if (c.getString(iName).length() > 1) {
                                            name = c.getString(iName);
                                        } else {
                                            name = c.getString(iCmp);
                                        }

                                        // Log.d("notify user =>", "here" + fromName);
                                        generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "You have a message from " + name, fromName, name);
                                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);


                                    } else if (subject.equalsIgnoreCase("product")) {
                                        String chatId;
                                        String nameProduct = null;
                                        String code = null;
                                        String mrp = null;
                                        String price = null;
                                        String url = null;
                                        String moq = null;
                                        try {
                                            nameProduct = content.getString("name");
                                            code = content.getString("code");
                                            mrp = content.getString("mrp");
                                            price = content.getString("price");
                                            url = content.getString("url");
                                            moq = content.getString("moq");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                                        int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                                        int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                                        cursor2.moveToFirst();
                                        if (cursor2.getCount() > 0) {
                                            chatId = cursor2.getString(iRid);
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            int unread = cursor2.getInt(iUnread);
                                            cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                        } else {
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                                            cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                            long id = ContentUris.parseId(uri);
                                            chatId = "" + id;

                                        }


                                        ContentValues values = new ContentValues();
                                        values.put(ChatSchema.KEY_SENDER, fromName);
                                        values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(messageText
                                                .getTo()));

                                        values.put(ChatSchema.KEY_MSG, content.optString("name"));
                                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                        values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                        values.put(ChatSchema.IsMe, 19);
                                        values.put(ChatSchema.BROADCAST, (Integer) content.optInt("broadcast"));
                                        values.put(ChatSchema.KEY_MSG_ID, messageText.getPacketID());
                                        values.put(ChatSchema.KEY_SUB, subject);
                                        values.put(ChatSchema.KEY_DIS, 0);
                                        values.put("status", 1);
                                        getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                        ContentValues cv = new ContentValues();
                                        cv.put(ProSchema.KEY_ROW_ID, messageText.getPacketID());
                                        cv.put(ProSchema.PRODUCT_NAME, nameProduct);
                                        cv.put(ProSchema.PRODUCT_CODE, code);
                                        cv.put(ProSchema.PRODUCT_MRP, mrp);
                                        cv.put(ProSchema.PRODUCT_PRICE, price);
                                        cv.put(ProSchema.PRODUCT_URL, url);
                                        cv.put(ProSchema.PRODUCT_MOQ, moq);
                                        cv.put(ProSchema.KEY_STATUS, 1);
                                        cv.put(ProSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                        getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);

                                        Cursor c = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName.toString()}, null);
                                        int iPhone = c.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                                        int iName = c.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                                        int iCmp = c.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                                        //  int l = c.getColumnIndexOrThrow("created_at");
                                        c.moveToFirst();
                                        String name;
                                        if (c.getString(iName).length() > 1) {
                                            name = c.getString(iName);
                                        } else {
                                            name = c.getString(iCmp);
                                        }
                                        generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "A Product share with you " + name, fromName, name);
                                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);


                                    } else if (subject.equalsIgnoreCase("HiFi")) {
                                        String chatId;
                                        Log.d("Hifi", "Come");
//
                                        String nameProduct = content.optString("name");
                                        String code = content.optString("code");
                                        String id = content.optString("product_id");
                                        String price = content.optString("price");
                                        String qty = content.optString("qty");
                                        String requestFor = content.optString("request_for");


                                        Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                                        int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                                        int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                                        cursor2.moveToFirst();
                                        if (cursor2.getCount() > 0) {
                                            chatId = cursor2.getString(iRid);
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            int unread = cursor2.getInt(iUnread);
                                            cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                        } else {
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                                            cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                            long ids = ContentUris.parseId(uri);
                                            chatId = "" + ids;
                                        }
                                        ContentValues values = new ContentValues();
                                        values.put(ChatSchema.KEY_SENDER, fromName);
                                        values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(messageText
                                                .getTo()));

                                        values.put(ChatSchema.KEY_MSG, content.optString("HiFi"));
                                        values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                        values.put(ChatSchema.IsMe, 4);
                                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                        values.put(ChatSchema.KEY_MSG_ID, messageText.getPacketID());
                                        values.put(ChatSchema.KEY_SUB, subject);
                                        values.put(ChatSchema.KEY_DIS, 0);
                                        values.put("status", 1);
                                        //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                                        getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                                        Cursor c = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName.toString()}, null);

                                        // if (c.getCount() > 0) {
                                        Log.d("c", "here" + c.getCount());
                                        int iPhone = c.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                                        int iName = c.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                                        int iCmp = c.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                                        //  int l = c.getColumnIndexOrThrow("created_at");
                                        c.moveToFirst();
                                        String name;
                                        if (c.getString(iName).length() > 1) {
                                            name = c.getString(iName);
                                        } else {
                                            name = c.getString(iCmp);
                                        }

                                        ContentValues cv = new ContentValues();

                                        cv.put(HiFiSchema.KEY_ROW_ID, messageText.getPacketID());
                                        cv.put(HiFiSchema.PRODUCT_NAME, nameProduct);
                                        cv.put(HiFiSchema.PRODUCT_ID, id);
                                        cv.put(HiFiSchema.PRODUCT_CODE, code);
                                        cv.put(HiFiSchema.ACCEPT, 1);
                                        cv.put(HiFiSchema.REQUEST_FOR, requestFor);
                                        cv.put(HiFiSchema.PRODUCT_PRICE, price);
                                        cv.put(HiFiSchema.PRODUCT_QUANTITY, qty);
                                        //   cv.put(HiFiSchema.PRODUCT_MOQ, moq);       H
                                        cv.put(HiFiSchema.KEY_STATUS, 1);
                                        cv.put(HiFiSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                        Uri uri = getContentResolver().insert(ChatProvider.HiFi_URI, cv);
                                        Log.d("Hifi", "isert uri " + uri.toString());
                                        generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "A High Five deal with you " + name, fromName, name);
                                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                        getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);

                                    } else if (subject.equalsIgnoreCase("action")) {

                                        ContentValues cv = new ContentValues();
                                        String action = content.optString("action");
                                        String msg_id = content.optString("msg_id");
                                        Log.d("Action ", action);
                                        //  Log.d("Action ", message.getBody());
                                        if (action.equals("accept")) {
                                            cv.put(HiFiSchema.ACCEPT, 3);

                                        } else {
                                            cv.put(HiFiSchema.ACCEPT, 4);
                                        }
                                        int count = getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msg_id});
                                        getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                        //  context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                                        generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "High Five " + action + "ed", action, "");
                                    } else if (subject.equalsIgnoreCase("img")) {
                                        // receiveFile(con, message);
                                        String chatId;
                                        Cursor c = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName.toString()}, null);

                                        // if (c.getCount() > 0) {
                                        Log.d("c", "here" + c.getCount());
                                        int iPhone = c.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                                        int iName = c.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                                        int iCmp = c.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                                        //  int l = c.getColumnIndexOrThrow("created_at");
                                        c.moveToFirst();
                                        String name;
                                        if (c.getString(iName).length() > 1) {
                                            name = c.getString(iName);
                                        } else {
                                            name = c.getString(iCmp);
                                        }
                                        Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                                        int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                                        int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                                        cursor2.moveToFirst();
                                        if (cursor2.getCount() > 0) {
                                            chatId = cursor2.getString(iRid);
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            int unread = cursor2.getInt(iUnread);
                                            cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                        } else {
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, messageText.getPacketID());
                                            cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                                            cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                            long ids = ContentUris.parseId(uri);
                                            chatId = "" + ids;
                                        }
                                        ContentValues values = new ContentValues();
                                        values.put(ChatSchema.KEY_SENDER, fromName);
                                        values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(messageText
                                                .getTo()));
                                        values.put(ChatSchema.KEY_MSG, content.optString("url"));
                                        values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                        values.put(ChatSchema.IsMe, 12);
                                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                        values.put(ChatSchema.BROADCAST, (Integer) content.optInt("broadcast"));
                                        values.put(ChatSchema.KEY_MSG_ID, messageText.getPacketID());
                                        values.put(ChatSchema.KEY_SUB, subject);
                                        values.put(ChatSchema.KEY_DIS, 2);
                                        values.put("status", 1);
                                        values.put(ChatSchema.IsDownload, 0);
                                        values.put(ChatSchema.IsError, 0);
                                        generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "You have a message from " + name, "IMAGE", name);
                                        //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                                        getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                                    } else if (subject.equalsIgnoreCase("query")) {
                                        Cursor cursor1 = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName.toString()}, null);
                                        String chatId;
                                        // if (c.getCount() > 0) {
                                        Log.d("c", "here" + cursor1.getCount());
                                        int iPhone = cursor1.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                                        int iName = cursor1.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                                        int iCmp = cursor1.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                                        //  int l = c.getColumnIndexOrThrow("created_at");
                                        cursor1.moveToFirst();
                                        String name;
                                        if (cursor1.getString(iName).length() > 1) {
                                            name = cursor1.getString(iName);
                                        } else {
                                            name = cursor1.getString(iCmp);
                                        }
                                        // String msgId = newMessage.getPacketID();
                                        final Calendar c = Calendar.getInstance();

                                        String to = StringUtils.parseBareAddress(messageText
                                                .getTo());
                                        String packetID = messageText.getPacketID();

                                        Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                                        int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                                        int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                                        cursor2.moveToFirst();
                                        if (cursor2.getCount() > 0) {
                                            chatId = cursor2.getString(iRid);
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, packetID);
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            int unread = cursor2.getInt(iUnread);
                                            cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                        } else {
                                            ContentValues cv1 = new ContentValues();
                                            cv1.put(ChatUserSchema.KEY_LAST_MSG, packetID);
                                            cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                                            cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                                            cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                                            cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                                            cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                            long ids = ContentUris.parseId(uri);
                                            chatId = "" + ids;
                                        }
                                        ContentValues values = new ContentValues();
                                        values.put(ChatSchema.KEY_SENDER, fromName);
                                        values.put(ChatSchema.KEY_RECEIVER, to);
                                        values.put(ChatSchema.KEY_MSG, content.optString("query"));
                                        values.put(ChatSchema.KEY_DIS, 1);
                                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                        values.put(ChatSchema.KEY_SUB, "query");
                                        values.put(ChatSchema.KEY_MSG_ID, packetID);
                                        values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                                        values.put(ChatSchema.IsMe, 22);
                                        values.put("status", 1);
                                        getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                        ContentValues cv = new ContentValues();
                                        cv.put(QuerySchema.KEY_ROW_ID, packetID);
                                        cv.put(QuerySchema.QUERY_TEXT, content.optString("query"));
                                        cv.put(QuerySchema.KEY_CREATED, format.format(c.getTime()));
                                        cv.put(QuerySchema.QUERY_URL, "" + content.optString("url"));
                                        cv.put(QuerySchema.KEY_STATUS, 1);
                                        getContentResolver().insert(ChatProvider.QUERY_URI, cv);
                                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                        getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);
                                        generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "You have a query from " + name, "Query", name);


                                    }


                                    //}
                                } else {
                                    String[] jid = fromName.split("@");
                                    String user = jid[0];
                                    Log.d("JID =>", user);
                                    new GetUserDetails(messageText, fromName).execute(user);
                                }
                            }
                        } else {
                            if (packet instanceof Presence) {
                                Presence presence = (Presence) packet;
                                String fromName = StringUtils.parseBareAddress(presence
                                        .getFrom());
                                //  Bundle extras = intent.getExtras();
                                Intent i = new Intent("broadCastName");
                                // Data you need to pass to activity
                                i.putExtra("online", presence.isAvailable());
                                i.putExtra("user", fromName);
                                i.putExtra("status", presence.getType().name());
                                getApplicationContext().sendBroadcast(i);
                                //   System.out.println(presence.isAvailable());
                                // System.out.println(fromName);
                            }

                        }

                    }
                }
            };
        }

        mConnection.addPacketListener(packetListener, null);
    }

    private void chattingSetup(String account) {
        XMPPConnection connection = XmppConnection.getInstance().getConnection();
        if (connection != null) {
            ChatManager chatManager = connection.getChatManager();
            MyMessageListener messageListener = new MyMessageListener();
            newChat = chatManager.createChat(account, messageListener);

        }
    }

    private void generateNotification(Context context, String title,
                                      String message, String user, String username) {
        if (!ChatActivity.isForeground) {

            Intent notificationIntent = null;
            Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_UNREAD + "> ?", new String[]{"0"}, null);
//            if (cursor.getCount() == 0)
//                return;
            if (cursor.getCount() == 1) {
                notificationIntent = new Intent(context, ChatActivity.class);
                cursor.moveToFirst();
                int iUnread = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);

                int unreadCount = cursor.getInt(iUnread);

                if (unreadCount > 1) {
                    message = "" + unreadCount + " Message from " + username;
                }
                Log.d("Generated message here", message);
            } else if (cursor.getCount() > 1) {
                notificationIntent = new Intent(context, Home.class);
                title = "WYDR";
                int unRead = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    // do what you need with the cursor here
                    int iUnread = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                    unRead += cursor.getInt(iUnread);
                }

                message = "" + unRead + " Message from " + cursor.getCount() + " Chat";
                Log.d("Generated message here", message);
            }
            int icon = R.drawable.ic_launcher;

            int mNotificationId = AppUtil.NOTIFICATION_ID;

            // set intent so it does not start a new activity
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            notificationIntent.putExtra("user", user);
            notificationIntent.putExtra("pos", 103);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBuilder.setPriority(Notification.PRIORITY_HIGH);
            }
            Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(inboxStyle)
                    .setContentIntent(resultPendingIntent)
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.demonstrative))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                    .setContentText(message)
                    .build();

//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(mNotificationId, notification);


        } else {

        }

    }

    private class Connect extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            mConnection = XmppConnection.getInstance().getConnection();

            try {

                //if (con.isConnected()) {
                String username = helper.getDefaults("login_id", getApplicationContext());
                String password = helper.getDefaults("login_password", getApplicationContext());
                Log.d("user ", username + " " + password);

                mConnection.login(username, password);
                MessageEventManager messageEventManager = new MessageEventManager(mConnection);
                messageEventManager.addMessageEventNotificationListener(m_messageEventNotificationListener);
                messageEventManager.addMessageEventRequestListener(m_DefaultMessageEventRequestListener);
                setConnection(mConnection);
                XmppConnection.getInstance().setPresence(0);
                mConnection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void connectionClosed() {

                        XmppConnection.getInstance().closeConnection();
                        Log.d("mConnection", "Close");
                        tExit = new Timer();
                        tExit.schedule(new timetask(), logintime);
                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {
                        XmppConnection.getInstance().closeConnection();
                        tExit = new Timer();
                        tExit.schedule(new timetask(), logintime);
                        Log.d("mConnection", "Close due to Exception" + e.toString());
                    }

                    @Override
                    public void reconnectingIn(int i) {
//                        Log.d("mConnection", "reconnect");
//                        XmppConnection.getInstance().closeConnection();
//                        tExit = new Timer();
//                        tExit.schedule(new timetask(), logintime);

                    }

                    @Override
                    public void reconnectionSuccessful() {
                        Log.d("mConnection", "connect Successful");

                    }

                    @Override
                    public void reconnectionFailed(Exception e) {
                        Log.d("mConnection", "reconnect");

                    }
                });
                PingManager pingManager = PingManager.getInstanceFor(mConnection);
                pingManager.setPingIntervall(300);
                pingManager.registerPingFailedListener(new PingFailedListener() {
                    @Override
                    public void pingFailed() {
                        start();
                        Log.d("PING FAILED ", " IS TRUE");
                    }
                });
                //  pingManager.setPingMinimumInterval(30);
                //  UUID.randomUUID().toString();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                // Thread.sleep(10000);
                Cursor cursor = getContentResolver().query(ChatProvider.CONTENT_URI, null, ChatSchema.KEY_DIS + "=?", new String[]{"-1"}, null);
                Log.d("unread message", "" + cursor.getCount());

                int iMsg = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG);
                //  int iDate = cursor.getColumnIndexOrThrow(ChatSchema.KEY_CREATED);
                int iId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_ROWID);
                int iSub = cursor.getColumnIndexOrThrow(ChatSchema.KEY_SUB);
                //int iDis = cursor.getColumnIndexOrThrow(ChatSchema.KEY_DIS);
                int iMsgId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG_ID);
                int iBroadcast = cursor.getColumnIndexOrThrow(ChatSchema.BROADCAST);
                int iRec = cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER);
                //int iError = cursor.getColumnIndexOrThrow(ChatSchema.IsError);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (mConnection.isConnected()) {
                        Log.d("Is Connected", "Ture");
                    }
                    chattingSetup(cursor.getString(iRec));
                    if (cursor.getString(iSub) == null) {
                        String messageId = jsonMessage.resendText(newChat, cursor.getString(iMsg), cursor.getInt(iBroadcast));

                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_MSG_ID, messageId);
                        int uri = getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_ROWID + "=?", new String[]{cursor.getString(iId)});
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);

                    } else if (cursor.getString(iSub).equals("text")) {

                        String messageId = jsonMessage.resendText(newChat, cursor.getString(iMsg), cursor.getInt(iBroadcast));


                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_MSG_ID, messageId);
                        int uri = getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_ROWID + "=?", new String[]{cursor.getString(iId)});
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);

                    } else if (cursor.getString(iSub).equals("HiFi")) {
                        //  Log.d("Subject",cursor.getString(iSub));
                        Cursor cursor1 = getContentResolver().query(ChatProvider.HiFi_URI, null, HiFiSchema.KEY_ROW_ID + "=?", new String[]{cursor.getString(iMsgId)}, null);
                        //   int iAgree = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_PRICE);
                        Log.d("Is there", "" + cursor1.getCount());
                        Log.d("Is there", "" + cursor.getString(iMsg));
                        int iName = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_NAME);
                        int iProductId = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_ID);
                        int iProductCode = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_CODE);
                        int iPrice = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_PRICE);
                        int iQty = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_QUANTITY);
                        int iRid = cursor1.getColumnIndexOrThrow(HiFiSchema.KEY_ROW_ID);
                        int iRequestFor = cursor1.getColumnIndexOrThrow(HiFiSchema.REQUEST_FOR);
                        cursor1.moveToFirst();

                        jsonMessage.sendHiFi(newChat, cursor1.getString(iProductCode), cursor1.getString(iName), cursor1.
                                getString(iProductId), cursor1.getString(iPrice), cursor1.getString(iQty), cursor1.getString(iRequestFor), cursor1.getString(iRid));
                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_MSG_ID, cursor1.getString(iRid));
                        int uri = getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_ROWID + "=?", new String[]{cursor.getString(iId)});
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                    } else if (cursor.getString(iSub).equals("query")) {
                        Cursor cursor1 = getContentResolver().query(ChatProvider.QUERY_URI, null, QuerySchema.KEY_ROW_ID + "=?", new String[]{cursor.getString(iMsgId)}, null);
                        //   int iAgree = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_PRICE);
                        Log.d("Is there", "" + cursor1.getCount());
                        Log.d("Is there", "" + cursor.getString(iMsg));
                        int iquery = cursor1.getColumnIndexOrThrow(QuerySchema.QUERY_TEXT);
                        int iUrl = cursor1.getColumnIndexOrThrow(QuerySchema.QUERY_URL);
                        int iIds = cursor1.getColumnIndexOrThrow(QuerySchema.KEY_ROW_ID);
                        cursor1.moveToFirst();
//                        Message newMessage = new Message();
//                        newMessage.setBody(cursor1.getString(iquery));
//                        newMessage.setSubject("query");
//                        newMessage.setPacketID(cursor.getString(iMsgId));
//                        newMessage.setProperty("url", "" + cursor1.getString(iUrl));
//                        MessageEventManager.addNotificationsRequests(newMessage,
//                                true, true, true, true);
//                        try {
//                            newChat.sendMessage(newMessage);
//                        } catch (XMPPException e) {
//                            e.printStackTrace();
//                        }
                        jsonMessage.resendQuery(newChat, cursor1.getString(iquery), cursor1.getString(iUrl), cursor1.getString(iIds));
                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_MSG_ID, cursor1.getString(iIds));
                        int uri = getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_ROWID + "=?", new String[]{cursor.getString(iId)});
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                    } else if (cursor.getString(iSub).equals("product")) {

                        Cursor cursor1 = getContentResolver().query(ChatProvider.URI_PRODUCT, null, ProSchema.KEY_ROW_ID + "=?", new String[]{cursor.getString(iMsgId)}, null);
                        //   int iAgree = cursor1.getColumnIndexOrThrow(HiFiSchema.PRODUCT_PRICE);
                        Log.d("Is there", "" + cursor1.getCount());
                        Log.d("Is there", "" + cursor.getString(iMsg));
                        int iName = cursor1.getColumnIndexOrThrow(ProSchema.PRODUCT_NAME);
                        int iCode = cursor1.getColumnIndexOrThrow(ProSchema.PRODUCT_CODE);
                        int iMrp = cursor1.getColumnIndexOrThrow(ProSchema.PRODUCT_MRP);
                        int iPrice = cursor1.getColumnIndexOrThrow(ProSchema.PRODUCT_PRICE);
                        int iUrl = cursor1.getColumnIndexOrThrow(ProSchema.PRODUCT_URL);
                        int iMoq = cursor1.getColumnIndexOrThrow(ProSchema.PRODUCT_MOQ);
                        int iRid = cursor1.getColumnIndexOrThrow(ProSchema.KEY_ROW_ID);

                        cursor1.moveToFirst();
                        jsonMessage.resendProduct(newChat, cursor1.getString(iName), cursor1.getString(iCode), cursor1.getString(iPrice), cursor1.getString(iMrp), cursor1.getString(iUrl), cursor1.getString(iMoq), cursor1.getString(iRid));

//                        Message newMessage = new Message();
//                        newMessage.setBody(cursor1.getString(iName));
//
//                        newMessage.setSubject("product");
//                        newMessage.setProperty("broadcast", cursor.getInt(iBroadcast));
//                        newMessage.setProperty("name", cursor1.getString(iName));
//                        newMessage.setProperty("code", cursor1.getString(iCode));
//                        newMessage.setProperty("price", cursor1.getString(iPrice));
//                        newMessage.setProperty("mrp", cursor1.getString(iMrp));
//                        newMessage.setProperty("url", cursor1.getString(iUrl));
//                        newMessage.setProperty("moq", cursor1.getString(iMoq));
//                        newMessage.setPacketID(cursor1.getString(iRid));
//                        MessageEventManager.addNotificationsRequests(newMessage,
//                                true, true, true, true);
//                        try {
//                            newChat.sendMessage(newMessage);
//                        } catch (XMPPException e) {
//                            e.printStackTrace();
//                        }
                    } else if (cursor.getString(iSub).equals("action")) {
                        Cursor c = getContentResolver().query(ChatProvider.HiFi_URI, null, HiFiSchema.KEY_ROW_ID + "=?", new String[]{cursor.getString(iMsgId)}, null);
                        int iAccept = c.getColumnIndexOrThrow(HiFiSchema.ACCEPT);
                        int iRid = c.getColumnIndexOrThrow(HiFiSchema.KEY_ROW_ID);
                        c.moveToFirst();
                        String action;
                        String msgId = null;
                        if (c.getInt(iAccept) == 0) {
                            msgId = c.getString(iRid);
                            //responseID = msgId;
                        }

                        if (c.getInt(iAccept) == 5) {
                            action = "accept";

                        } else {
                            action = "decline";


                        }
                        msgId = jsonMessage.sendAction(newChat, action, msgId);
//                        Message newMessage = new Message();
//                        MessageEventManager.addNotificationsRequests(newMessage,
//                                true, true, true, true);
//                        //  newMessage.setBody(msgId);
//                        newMessage.setSubject("action");
//
//
//                        try {
//                            newChat.sendMessage(newMessage);
//                        } catch (XMPPException e) {
//                            e.printStackTrace();
//                        }
                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_MSG_ID, msgId);
                        int uri = getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_ROWID + "=?", new String[]{cursor.getString(iId)});
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    }
                }
            } catch (
                    Exception e
                    )

            {
                e.printStackTrace();
            }
        }

    }

    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            String from = message.getFrom();
            String body = message.getBody();

            System.out.println(String.format(
                    "Received message '%1$s' from %2$s", body, from));
            System.out.println(String.format(
                    "Error  message '%1$s' from %2$s", message.getError(), from));


        }
    }

    private class GetUserDetails extends AsyncTask<String, String, JSONObject> {
        final Calendar c = Calendar.getInstance();
        int flag = 0;

        // ProductModal productModal;
        Message oldMessage;
        String fromName;
        JSONParser parser = new JSONParser();
        private String userid, error_msg;

        public GetUserDetails(Message modal, String fromName) {
            this.fromName = fromName;
            this.oldMessage = modal;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            Log.e("id--", args[0]);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_login", args[0]));

            Log.e("params--", params.toString());
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/sellers", "GET", params, SmackService.this);
            Log.e("params--", json.toString());
            if (json != null) {

                if (json.has("users")) {

                    try {
                        JSONArray jsonArray = json.getJSONArray("users");

                        if (jsonArray.length() == 0) {
                            flag = 2;
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.optString("is_openfire").equalsIgnoreCase("1")) {
                                ContentValues cv = new ContentValues();
                                if (jsonObject.has("phone"))
                                    cv.put(NetSchema.USER_PHONE, jsonObject.getString("phone"));
                                if (jsonObject.has("company_name"))
                                    cv.put(NetSchema.USER_COMPANY, jsonObject.getString("company_name"));
                                if (jsonObject.has("firstname"))
                                    cv.put(NetSchema.USER_DISPLAY, " ");
                                if (jsonObject.has("company_id"))
                                    cv.put(NetSchema.USER_COMPANY_ID, jsonObject.getString("company_id"));
                                if (jsonObject.has("user_id")) {
                                    cv.put(NetSchema.USER_ID, jsonObject.getString("user_id"));
                                }
                                if (jsonObject.has("user_login")) {
                                    cv.put(NetSchema.USER_NET_ID, jsonObject.getString("user_login") + "@" + AppUtil.SERVER_NAME);
                                }
                                cv.put(NetSchema.USER_STATUS, "0");
                                cv.put(NetSchema.USER_NAME, helper.ConvertCamel(jsonObject.getString("firstname")) + " " + helper.ConvertCamel(jsonObject.getString("lastname")));
                                cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));
                                Uri uri = getContentResolver().insert(ChatProvider.NET_URI, cv);
                                getContentResolver().notifyChange(ChatProvider.NET_URI, null);
                                Log.e("ID--2", uri.toString());
                            } else {
                                flag = 2;
                            }
                        }


                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    if (json.has("message")) {
                        try {
                            error_msg = json.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    flag = 2;
                }

                /*********************** ISTIAQUE ***************************/
                if (json.has("401")) {
                    session = new SessionManager(getApplicationContext());
                    session.logoutUser();
                }
                /*********************** ISTIAQUE ***************************/
            } else {
                flag = 1;
            }
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (flag == 0) {
                Cursor c = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName.toString()}, null);
                int iPhone = c.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                int iName = c.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                int iCmp = c.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                //  int l = c.getColumnIndexOrThrow("created_at");
                String name = "";
                if (c != null && c.moveToFirst()) {
                    if (c.getString(iName).length() > 1) {
                        name = c.getString(iName);
                    } else {
                        name = c.getString(iCmp);
                    }
                }
                Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{fromName}, null);
                Log.d("--------", "" + cursor.getCount());
                //  if (cursor != null && cursor.getCount() > 0) {
                JSONObject content = null;
                String subject = "";
                String messageID = oldMessage.getPacketID();

                try {
                    content = new JSONObject(oldMessage.getBody().toString());
                    subject = content.optString("subject");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (subject.equalsIgnoreCase("product")) {
                    String nameProduct = content.optString("name");
                    String code = content.optString("code");
                    String mrp = content.optString("mrp");
                    String price = content.optString("price");
                    String url = content.optString("url");
                    String moq = content.optString("moq");
                    String chatId;
                    Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                    int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                    int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    cursor2.moveToFirst();
                    if (cursor2.getCount() > 0) {
                        chatId = cursor2.getString(iRid);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        int unread = cursor2.getInt(iUnread);
                        cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                        cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long id = ContentUris.parseId(uri);
                        chatId = "" + id;

                    }


                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, fromName);
                    values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(oldMessage
                            .getTo()));
//                    Log.d("from", fromName + "  receiver " + StringUtils.parseBareAddress(message
//                            .getTo()));
                    values.put(ChatSchema.KEY_MSG, nameProduct);
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    values.put(ChatSchema.IsMe, 19);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_MSG_ID, messageID);
                    values.put(ChatSchema.KEY_SUB, subject);
                    values.put(ChatSchema.KEY_DIS, 0);
                    values.put("status", 1);
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                    ContentValues cv = new ContentValues();
                    cv.put(ProSchema.KEY_ROW_ID, messageID);
                    cv.put(ProSchema.PRODUCT_NAME, nameProduct);
                    cv.put(ProSchema.PRODUCT_CODE, code);
                    cv.put(ProSchema.PRODUCT_MRP, mrp);
                    cv.put(ProSchema.PRODUCT_PRICE, price);
                    cv.put(ProSchema.PRODUCT_URL, url);
                    cv.put(ProSchema.PRODUCT_MOQ, moq);
                    cv.put(ProSchema.KEY_STATUS, 1);
                    cv.put(ProSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);

                    generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "A Product share with you " + name, fromName, name);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);


                } else if (subject.equalsIgnoreCase("HiFi")) {
                    Log.d("Hifi", "Come");
                    String chatId;
//
                    String nameProduct = content.optString("name");
                    String code = content.optString("code");
                    String id = content.optString("product_id");
                    String price = content.optString("price");
                    String qty = content.optString("qty");


                    Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                    int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                    int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    cursor2.moveToFirst();
                    if (cursor2.getCount() > 0) {
                        chatId = cursor2.getString(iRid);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        int unread = cursor2.getInt(iUnread);
                        cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                        cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long ids = ContentUris.parseId(uri);
                        chatId = "" + ids;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, fromName);
                    values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(oldMessage
                            .getTo()));

                    values.put(ChatSchema.KEY_MSG, nameProduct);
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    values.put(ChatSchema.IsMe, 4);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_MSG_ID, messageID);
                    values.put(ChatSchema.KEY_SUB, subject);
                    values.put(ChatSchema.KEY_DIS, 0);
                    values.put("status", 1);
                    //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                    ContentValues cv = new ContentValues();

                    cv.put(HiFiSchema.KEY_ROW_ID, messageID);
                    cv.put(HiFiSchema.PRODUCT_NAME, nameProduct);
                    cv.put(HiFiSchema.PRODUCT_ID, id);
                    cv.put(HiFiSchema.PRODUCT_CODE, code);
                    cv.put(HiFiSchema.ACCEPT, 1);
                    cv.put(HiFiSchema.PRODUCT_PRICE, price);
                    cv.put(HiFiSchema.PRODUCT_QUANTITY, qty);
                    //   cv.put(HiFiSchema.PRODUCT_MOQ, moq);       H
                    cv.put(HiFiSchema.KEY_STATUS, 1);
                    cv.put(HiFiSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    Uri uri = getContentResolver().insert(ChatProvider.HiFi_URI, cv);
                    Log.d("Hifi", "isert uri " + uri.toString());
                    generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "A Hi-Fi deal with you " + name, fromName, name);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);

                } else if (subject.equalsIgnoreCase("action")) {

                    ContentValues cv = new ContentValues();
                    String action = content.optString("action");
                    String id = content.optString("msg_id");
                    if (action.equals("accept")) {
                        cv.put(HiFiSchema.ACCEPT, 3);

                    } else {
                        cv.put(HiFiSchema.ACCEPT, 4);
                    }
                    int count = getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{id});
                    getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    //  context.getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                    generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "Hi-Fi deal " + action + "ed", action, name);
                } else if (subject.equalsIgnoreCase("img")) {
                    // receiveFile(con, message);
                    String chatId;
                    Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                    int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                    int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    cursor2.moveToFirst();
                    if (cursor2.getCount() > 0) {
                        chatId = cursor2.getString(iRid);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        int unread = cursor2.getInt(iUnread);
                        cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                        cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long id = ContentUris.parseId(uri);
                        chatId = "" + id;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, fromName);
                    values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(oldMessage
                            .getTo()));
                    values.put(ChatSchema.KEY_MSG, content.optString("url"));
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    values.put(ChatSchema.IsMe, 12);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_MSG_ID, messageID);
                    values.put(ChatSchema.KEY_SUB, subject);
                    values.put(ChatSchema.KEY_DIS, 2);
                    values.put("status", 1);
                    values.put(ChatSchema.IsDownload, 0);
                    values.put(ChatSchema.IsError, 0);
                    generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "You have a message from ", "IMAGE", name);
                    //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                } else if (subject.equalsIgnoreCase("query")) {
                    // String msgId = newMessage.getPacketID();
                    //   final Calendar c = Calendar.getInstance();
                    String chatId;
                    String to = StringUtils.parseBareAddress(oldMessage
                            .getTo());
                    //String packetID = message.getPacketID();

                    Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                    int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                    int iRId = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    cursor2.moveToFirst();
                    if (cursor2.getCount() > 0) {
                        chatId = cursor2.getString(iRId);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        int unread = cursor2.getInt(iUnread);
                        cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageID);
                        cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                        cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long id = ContentUris.parseId(uri);
                        chatId = "" + id;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, fromName);
                    values.put(ChatSchema.KEY_RECEIVER, to);
                    values.put(ChatSchema.KEY_MSG, content.optString("query"));
                    values.put(ChatSchema.KEY_DIS, 1);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_SUB, "query");
                    values.put(ChatSchema.KEY_MSG_ID, messageID);
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    values.put(ChatSchema.IsMe, 22);
                    values.put("status", 1);
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                    ContentValues cv = new ContentValues();
                    cv.put(QuerySchema.KEY_ROW_ID, messageID);
                    cv.put(QuerySchema.QUERY_TEXT, content.optString("query"));
                    cv.put(QuerySchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    cv.put(QuerySchema.QUERY_URL, "" + content.optString("url"));
                    cv.put(QuerySchema.KEY_STATUS, 1);
                    getContentResolver().insert(ChatProvider.QUERY_URI, cv);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);
                    generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "You have a query from " + name, fromName, name);

                } else if (subject.equals("text")) {
                    String chatId;
                    Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName}, null);
                    int iUnread = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_UNREAD);
                    int iRid = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    cursor2.moveToFirst();

                    if (cursor2.getCount() > 0) {
                        chatId = cursor2.getString(iRid);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, oldMessage.getPacketID());
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        int unread = cursor2.getInt(iUnread);
                        cv1.put(ChatUserSchema.KEY_UNREAD, unread + 1);
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{fromName});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, oldMessage.getPacketID());
                        cv1.put(ChatUserSchema.KEY_UNREAD, 1);
                        cv1.put(ChatUserSchema.KEY_CHAT_USER, fromName);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long id = ContentUris.parseId(uri);
                        chatId = "" + id;
                    }

                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, fromName);
                    values.put(ChatSchema.KEY_RECEIVER, StringUtils.parseBareAddress(oldMessage
                            .getTo()));
                    try {
                        values.put(ChatSchema.KEY_MSG, content.getString("text"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    values.put(ChatSchema.IsMe, 1);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                    values.put(ChatSchema.KEY_SUB, "text");
                    values.put(ChatSchema.KEY_MSG_ID, oldMessage.getPacketID());
                    //  values.put(ChatSchema.KEY_SUB, message.getSubject());
                    values.put(ChatSchema.KEY_DIS, 0);
                    values.put("status", 1);
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                    // Log.d("notify user =>", "here" + fromName);
                    generateNotification(getApplicationContext(), getResources().getString(R.string.app_name), "You have a message from " + name, fromName, name);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);


                }
            }


            // }

        }
    }

    class timetask extends TimerTask {
        @Override
        public void run() {
            Log.d("Attemp", "Timer Task  ");
            if (new ConnectionDetector(getApplicationContext()).isConnectingToInternet()) {
                if (!mActive) {
                    mActive = true;
                    Log.d("in connection ini", "2");
                    // Create ConnectionThread Loop
                    if (mThread == null || !mThread.isAlive()) {
                        mThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();

                                mTHandler = new Handler();
                                Log.d("in connection ini", "4");
                                initConnection();
                                Looper.loop();
                            }

                        });
                        mThread.start();
                    }

                }
                // initConnection();
            }

        }
    }

//
}
