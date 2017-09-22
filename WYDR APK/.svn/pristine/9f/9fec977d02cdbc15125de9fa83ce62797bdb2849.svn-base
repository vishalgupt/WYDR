package wydr.sellers.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wydr.sellers.R;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.ForwardMessage;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.acc.QuerySchema;
import wydr.sellers.acc.RandomTest;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.adapter.ForwardAdapter;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.openfire.JSONMessage;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 13/10/15.
 */
public class ForwardAll
        extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public String username, user_cur_id, compid, netid;
    ListView listUser;
    ForwardAdapter adapter;
    View v;
    Dialog alertDialog;
    ConnectionDetector cd;
    android.app.AlertDialog.Builder alertDialog1, alertDialog2;
    JSONParser parser;
    String sortingOrder = "(CASE display_name  when \"\" THEN 0 ELSE 1 END ) DESC , display_name";
    ArrayList<ForwardMessage> messageArrayList;
    Chat newChat;
    //  SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ChatManager chatManager;
    XMPPConnection connection;
    Helper helper = new Helper();
    JSONMessage jsonMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        // Bundle bundle = intent.getExtras();
        messageArrayList = (ArrayList<ForwardMessage>) intent.getSerializableExtra("myList");
        connection = XmppConnection.getInstance().getConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_network, container, false);
        v = view;
        jsonMessage = new JSONMessage();
        iniStuff(view);

        cd = new ConnectionDetector(getActivity());
        alertDialog = new Dialog(getActivity(), R.style.mydialogstyle);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.overlay_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Calendar calendar = Calendar.getInstance();
                Cursor data = (Cursor) adapter.getItem(position);

                int iId = data.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                //     data.moveToFirst();

                String netUser = data.getString(iId);
                chattingSetup(netUser);

                for (int i = 0; i < messageArrayList.size(); i++) {
                    ForwardMessage message = messageArrayList.get(i);
                    if (message.getSub().equalsIgnoreCase("product")) {

                        ChatManager chatmanager = connection.getChatManager();
                        Chat newChat = chatmanager.createChat(netUser, new MessageListener() {
                            public void processMessage(Chat chat, Message message) {
                                System.out.println("Received message:njjjj " + message);
                            }
                        });
                        Cursor cursor = getActivity().getContentResolver().query(ChatProvider.URI_PRODUCT, null, ProSchema.KEY_ROW_ID + "=?", new String[]{message.getMsgId()}, null);
                        int iName = cursor.getColumnIndexOrThrow(ProSchema.PRODUCT_NAME);
                        int iCode = cursor.getColumnIndexOrThrow(ProSchema.PRODUCT_CODE);
                        int iPrice = cursor.getColumnIndexOrThrow(ProSchema.PRODUCT_PRICE);
                        int iMrp = cursor.getColumnIndexOrThrow(ProSchema.PRODUCT_MRP);
                        int iUrl = cursor.getColumnIndexOrThrow(ProSchema.PRODUCT_URL);
                        int iMoq = cursor.getColumnIndexOrThrow(ProSchema.PRODUCT_MOQ);
                        cursor.moveToFirst();
                        //     int iDate5 = c.getColumnIndexOrThrow(ProSchema.KEY_CREATED);
                        //     Bundle result = data.getBundleExtra("result");
                        String code = cursor.getString(iCode);
                        String name = cursor.getString(iName);
                        String price = cursor.getString(iPrice);
                        String mrp = cursor.getString(iMrp);
                        String url = cursor.getString(iUrl);
                        String moq = cursor.getString(iMoq);

                        String packet = jsonMessage.sendProduct(newChat, name, code, price, mrp, url, moq);


                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_SENDER, helper.getDefaults("login_id", getActivity().getApplicationContext()) + "@" + AppUtil.SERVER_NAME);
                        values.put(ChatSchema.KEY_RECEIVER, netUser);
                        values.put(ChatSchema.KEY_MSG, name);
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_SUB, "product");
                        values.put(ChatSchema.KEY_MSG_ID, packet);
                        values.put(ChatSchema.KEY_CREATED, "" + format.format(calendar.getTime()));
                        values.put(ChatSchema.IsMe, 3);
                        values.put("status", 1);
                        Log.e("name", name);
                        getActivity().getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                        ContentValues cv = new ContentValues();
                        cv.put(ProSchema.KEY_ROW_ID, packet);
                        cv.put(ProSchema.PRODUCT_NAME, name);
                        cv.put(ProSchema.PRODUCT_CODE, code);
                        cv.put(ProSchema.PRODUCT_MRP, mrp);
                        cv.put(ProSchema.PRODUCT_PRICE, price);
                        cv.put(ProSchema.PRODUCT_URL, url);
                        cv.put(ProSchema.PRODUCT_MOQ, moq);
                        cv.put(ProSchema.KEY_STATUS, 1);
                        cv.put(ProSchema.KEY_CREATED, "" + format.format(calendar.getTime()));
                        getActivity().getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);
                        getActivity().getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getActivity().getContentResolver().notifyChange(ChatProvider.URI_PRODUCT, null, false);
                    } else if (message.getSub().equalsIgnoreCase("query")) {

                        ChatManager chatmanager = connection.getChatManager();
                        Chat newChat = chatmanager.createChat(netUser, new MessageListener() {
                            public void processMessage(Chat chat, Message message) {
                                System.out.println("Received message:njjjj " + message);
                            }
                        });
                        Cursor cursor = getActivity().getContentResolver().query(ChatProvider.QUERY_URI, null, QuerySchema.KEY_ROW_ID + "=?", new String[]{message.getMsgId()}, null);
                        int iName = cursor.getColumnIndexOrThrow(QuerySchema.QUERY_TEXT);
                        int iUrl = cursor.getColumnIndexOrThrow(QuerySchema.QUERY_URL);
                        cursor.moveToFirst();
                        String name = cursor.getString(iName);
                        String url = cursor.getString(iUrl);
                        final Calendar calendar1 = Calendar.getInstance();
                        String packet = jsonMessage.sendQuery(newChat, name, url);


                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_SENDER, helper.getDefaults("login_id", getActivity().getApplicationContext()) + "@" + AppUtil.SERVER_NAME);
                        values.put(ChatSchema.KEY_RECEIVER, netUser);
                        values.put(ChatSchema.KEY_MSG, "" + name);
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_SUB, "query");
                        values.put(ChatSchema.KEY_MSG_ID, packet);
                        values.put(ChatSchema.KEY_CREATED, "" + format.format(calendar1.getTime()));
                        values.put(ChatSchema.IsMe, 21);
                        values.put("status", 1);


                        getActivity().getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                        ContentValues cv = new ContentValues();
                        cv.put(QuerySchema.KEY_ROW_ID, packet);
                        cv.put(QuerySchema.QUERY_TEXT, name);
                        cv.put(QuerySchema.KEY_CREATED, format.format(calendar1.getTime()));
                        if (ValidationUtil.isNull(url)) {
                            cv.put(QuerySchema.QUERY_URL, AppUtil.URL);
                            //  newMessage.setProperty("url", "" + AppUtil.URL);
                        } else {
                            cv.put(QuerySchema.QUERY_URL, url);
                        }
                        //  cv.put(QuerySchema.QUERY_URL, url);
                        cv.put(QuerySchema.KEY_STATUS, 1);

                        getActivity().getContentResolver().insert(ChatProvider.QUERY_URI, cv);
                        getActivity().getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getActivity().getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);
                    } else if (message.getSub().equalsIgnoreCase("img")) {

                        final Calendar calendar1 = Calendar.getInstance();
                        Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CONTENT_URI, null, ChatSchema.KEY_MSG_ID + "=? and " + ChatSchema.IsDownload + "=1", new String[]{message.getMsgId()}, null);
                        int final_Path = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG);

                        cursor.moveToFirst();
                        String finalPath = cursor.getString(final_Path);
                        Log.d("Final", finalPath);
                        Cursor cursor_user = getActivity().getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{netUser}, null);
                        if (cursor_user.getCount() > 0) {
                            ContentValues cv = new ContentValues();
                            //  cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            getActivity().getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{netUser});
                            getActivity().getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                            Log.d("Insert DB", "1");
                        } else {
                            ContentValues cv = new ContentValues();
                            //   cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                            cv.put(ChatUserSchema.KEY_UNREAD, 0);
                            cv.put(ChatUserSchema.KEY_CHAT_USER, netUser);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            getActivity().getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                            getActivity().getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        }
                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_SENDER, helper.getDefaults("login_id", getActivity().getApplicationContext()) + "@" + AppUtil.SERVER_NAME);
                        values.put(ChatSchema.KEY_RECEIVER, netUser);
                        values.put(ChatSchema.KEY_MSG, "" + finalPath);
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_SUB, "img");
                        RandomTest randomTest = new RandomTest();
                        String ui = randomTest.randomString(7);
                        Log.d("IMAGE ID", ui);
                        values.put(ChatSchema.KEY_MSG_ID, ui);
                        values.put(ChatSchema.IsDownload, 0);
                        values.put(ChatSchema.IsError, 0);
                        values.put(ChatSchema.KEY_CREATED, "" + format.format(calendar1.getTime()));
                        values.put(ChatSchema.IsMe, 11);
                        values.put("status", 0);
                        getActivity().getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                        getActivity().getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);


                    } else if (message.getSub().equalsIgnoreCase("text")) {



                        String packet = jsonMessage.sendText(newChat, message.getMsg());
                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_SENDER, helper.getDefaults("login_id", getActivity().getApplicationContext()) + "@" + AppUtil.SERVER_NAME);
                        values.put(ChatSchema.KEY_RECEIVER, netUser);
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_SUB, message.getSub());
                        values.put(ChatSchema.KEY_MSG_ID, packet);
                        values.put(ChatSchema.KEY_MSG, message.getMsg());

                        values.put(ChatSchema.KEY_CREATED, "" + format.format(calendar.getTime()));
                        values.put(ChatSchema.IsMe, 2);
                        values.put("status", 1);
                        // Log.e("hid", content);
                        Uri uri = getActivity().getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                        getActivity().getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getActivity().getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                        Log.d("User", uri.toString());


                }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("current_user", netUser);
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                    getActivity().finish();
            }
            }
                                        }

        );


        return view;
    }


    private void iniStuff(View view) {
        listUser = (ListView) view.findViewById(R.id.listViewMyNetwork);
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
        adapter = new ForwardAdapter(this.getActivity(), null);
        listUser.setAdapter(adapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), ChatProvider.NET_URI, null, NetSchema.USER_STATUS + "=?", new String[]{"1"}, sortingOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 1:
                adapter.swapCursor(data);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(1, null, this);
        super.onResume();
        getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
    }

    private void chattingSetup(String account) {
        if (connection != null) {
            chatManager = connection.getChatManager();

            MessageListener messageListener = new MessageListenerImpl();
            newChat = chatManager.createChat(account, messageListener);
            chatManager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean b) {
                    // MessageListener messageListener = new MessageListenerImpl();
                    chat.addMessageListener(new ChatStateListener() {
                        @Override
                        public void stateChanged(Chat chat, ChatState chatState) {
                            switch (chatState) {
                                case active:
                                    Log.d("state", "active");
                                    break;
                                case composing:
                                    Log.d("state", "composing");
                                    break;
                                case paused:
                                    Log.d("state", "paused");
                                    break;
                                case inactive:
                                    Log.d("state", "inactive");
                                    break;
                                case gone:
                                    Log.d("state", "gone");
                                    break;
                            }
                        }

                        @Override
                        public void processMessage(Chat chat, Message message) {
                            //  Log.d("state","gone");
                            Log.d("state", chat.getParticipant() + " " + message.toXML());

                        }
                    });
                }
            });

        }

    }

    public class MessageListenerImpl implements MessageListener, ChatStateListener {

        @Override
        public void processMessage(Chat arg0, Message arg1) {
            System.out.println("Received message: " + arg1);

            // arg0.addMessageListener();

        }

        @Override
        public void stateChanged(Chat arg0, ChatState arg1) {
            if (ChatState.composing.equals(arg1)) {
                Log.d("Chat State", arg0.getParticipant() + " is typing..");
            } else if (ChatState.gone.equals(arg1)) {
                Log.d("Chat State", arg0.getParticipant() + " has left the conversation.");
            } else {
                Log.d("Chat State", arg0.getParticipant() + ": " + arg1.name());
        }

    }


    }

}
