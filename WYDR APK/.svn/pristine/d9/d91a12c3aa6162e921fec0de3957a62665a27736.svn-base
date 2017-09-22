package wydr.sellers.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.razorpay.Checkout;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.MessageEventManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.AutoCode;
import wydr.sellers.acc.BookSchema;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.ForwardMessage;
import wydr.sellers.acc.HiFiSchema;
import wydr.sellers.acc.HiFiStatus;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.acc.ProductCode;
import wydr.sellers.acc.QuerySchema;
import wydr.sellers.acc.RandomTest;
import wydr.sellers.acc.ScalingUtilities;
import wydr.sellers.acc.UserSchema;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.adapter.ChatAdapter;
import wydr.sellers.emojicon.EmojiconEditText;
import wydr.sellers.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import wydr.sellers.emojicon.EmojiconsPopup;
import wydr.sellers.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import wydr.sellers.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import wydr.sellers.emojicon.emoji.Emojicon;
import wydr.sellers.gson.HifiDeal;
import wydr.sellers.gson.MessageRequest;
import wydr.sellers.gson.OrderInDeal;
import wydr.sellers.gson.OrderStatus;
import wydr.sellers.gson.ProductInDeal;
import wydr.sellers.gson.UpdateDeal;
import wydr.sellers.modal.ChatMessage;
import wydr.sellers.modal.ProductOrderedDetails;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.RestClientOpenfire;
import wydr.sellers.network.SessionManager;
import wydr.sellers.network.Transactions;
import wydr.sellers.openfire.JSONMessage;
import wydr.sellers.openfire.SLog;
import wydr.sellers.openfire.SmackService;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;

/**
 * Created by surya on 14/7/15.
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 4;
    private static final String IMAGE_DIRECTORY_NAME = "WYDR";
    public static int company;
    static public boolean isForeground = false;
    static XMPPConnection connection;
    static int PICK_REQUEST = 1009;
    final int DESIREDWIDTH = 800;
    final int DESIREDHEIGHT = 800;
    private final int SELECT_PICTURE = 9;
    boolean has = false;
    String registrationDate = "2016-01-01 07:07:33";
    EmojiconEditText msg;
    int moq, amount;
    EditText agreePrice, agreeQuantity;
    AutoCompleteTextView agreeCode;
    ListView listView;
    String account, user_name, user_id, nameString, catTo, company_id, userLogin, selectedCode, orderId, userName, userCompany;
    ImageView back, addProduct, search, send;
    TextView name, status;
    Controller aController;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatNew = new SimpleDateFormat("yyyy-MM-dd");
    ConnectionDetector cd;
    ChatAdapter chatAdapter;
    LinearLayout chatHeader;
    RadioGroup radioGroup;
    MessageEventManager messageEventManager;
    Button sendHifi, cancelHifi;
    MyDatabaseHelper myDatabaseHelper;
    private ArrayList<String> compIDlist = new ArrayList<>();
    Dialog dialog;
    int rId = -1;
    int messageCount = 0;
    int mScrollState = 0;
    View footerView;
    int limit = 40;
    Helper helper = new Helper();
    AutoCode autoCode;
    ImageView smileyIcon, icon;
    int request_for = 0;
    Intent mServiceIntent;
    StatusReceiver mReceiver;
    IntentFilter mFilter;
    JSONMessage jsonMessage;
    Cursor myCursor;
    Controller application;
    int section = 0;
    ArrayList<ProductOrderedDetails> productOrderedDetailsList;
    String ordertotal;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle b = intent.getExtras();
            String user = b.getString("user");
            boolean online = b.getBoolean("online");
            if (user.equalsIgnoreCase(account))
            {
                status.setVisibility(View.VISIBLE);
                if (online) {
                    status.setText("Online");
                } else {
                    status.setText("Offline");
                }
            }
        }
    };
    private String tag = "ChatActivity";
    private ProgressDialog progress;
    private ChatManager chatManager;
    private Chat chat;
    private MessageListener msgListener;
    private ArrayList<ChatMessage> chatHistory;
    private Uri fileUri;
    private int keyboardHeight;
    private int cursor_position;

    private static File getOutputMediaFile(int type)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        mediaStorageDir.mkdir();
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }

        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private static File getMediaFile(int type)
    {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppUtil.IMAGE_DIRECTORY_SEND);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d("TAG", "Oops! Failed create "
                        + AppUtil.IMAGE_DIRECTORY_SEND + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
        return mediaFile;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        cd = new ConnectionDetector(getApplicationContext());
        application = (Controller) getApplication();
        myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());
        PrefManager prefManager = new PrefManager(getApplicationContext());
        compIDlist = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        userLogin = helper.getDefaults("login_id", getApplicationContext()) + "@" + AppUtil.SERVER_NAME;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        jsonMessage = new JSONMessage();
        mServiceIntent = new Intent(getApplicationContext(), StatusService.class);
        userName = helper.getDefaults("firstname", getApplicationContext());
        String account2 = getIntent().getStringExtra("user");
        userCompany = helper.getDefaults("company", ChatActivity.this);
        rId = getIntent().getIntExtra("id", -1);
        msg = (EmojiconEditText) findViewById(R.id.editTextMessage);
        final View rootView = findViewById(R.id.activityRoot);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_footer, null, false);
        footerView.setVisibility(View.GONE);
        loadActivity(account2);
        setBroadcast();
        getSupportLoaderManager().initLoader(5, null, ChatActivity.this);
        popup.setSizeForSoftKeyboard();
        popup.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                changeEmojiKeyboardIcon(smileyIcon, R.drawable.smiley);
            }
        });
        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
                //  scroll();
                if (listView.getCount() > 4) {
                    listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                    listView.setStackFromBottom(true);
                }
            }

            @Override
            public void onKeyboardClose() {
                if (listView.getCount() > 9) {
                    listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                    listView.setStackFromBottom(true);
                }

                else {
                    listView.setStackFromBottom(false);
                }

                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (msg == null || emojicon == null) {
                    return;
                }

                int start = msg.getSelectionStart();
                int end = msg.getSelectionEnd();
                if (start < 0) {
                    msg.append(emojicon.getEmoji());
                } else {
                    msg.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                msg.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        smileyIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(smileyIcon, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        msg.setFocusableInTouchMode(true);
                        msg.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(msg, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(smileyIcon, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });


    }

    private void setBroadcast() {
        mReceiver = new StatusReceiver();

        // Creating an IntentFilter with action
        mFilter = new IntentFilter(AppUtil.BROADCAST_ACTION);

        // Registering BroadcastReceiver with this activity for the intent filter
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, mFilter);
    }

    private void loadActivity(String acc)
    {
        aController = (Controller) getApplicationContext();
        account = acc;
        name = (TextView) findViewById(R.id.txtName);
        status = (TextView) findViewById(R.id.txtOnlineStatus);
        Log.d("user", "user_id" + account);
        /*cd = new ConnectionDetector(getApplicationContext());*/
        chatHeader = (LinearLayout) findViewById(R.id.chatHeader);
        progressStuff();
        catTo = helper.getDefaults("company_id", ChatActivity.this);
        Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{account}, null);
        Log.d("cursor size", String.valueOf(cursor.getCount()));
        String aa = helper.getDefaults("aki_user_details",ChatActivity.this);
        try {
            if(aa!=null) {
                JSONObject jsonObject = new JSONObject(aa);

                user_id = jsonObject.getString(NetSchema.USER_ID);
                company_id = jsonObject.getString(NetSchema.USER_COMPANY_ID);
                name.setText(jsonObject.getString(NetSchema.USER_COMPANY));
                autoCode = new AutoCode(this, R.layout.auto_code_layout, company_id, catTo);
                user_name = jsonObject.getString(NetSchema.USER_COMPANY);
                nameString = jsonObject.getString(NetSchema.USER_COMPANY);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst())
        {
            int iName = cursor.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
            int iNo = cursor.getColumnIndexOrThrow(NetSchema.USER_PHONE);
            int iCompany = cursor.getColumnIndexOrThrow(NetSchema.USER_COMPANY);
            int iCompanyId = cursor.getColumnIndexOrThrow(NetSchema.USER_COMPANY_ID);
            int iRow = cursor.getColumnIndexOrThrow(NetSchema.USER_ID);
            user_id = cursor.getString(iRow);
            company_id = cursor.getString(iCompanyId);

            autoCode = new AutoCode(this, R.layout.auto_code_layout, company_id, catTo);
            if (cursor.getString(iName).length() > 2) {
                Log.d("company name",cursor.getString(iCompany));
                nameString = cursor.getString(iName);
                user_name = cursor.getString(iCompany);
                name.setText(MyTextUtils.toTitleCase(cursor.getString(iName) + ", " + cursor.getString(iCompany)));
            } else {
                Log.d("company name",cursor.getString(iCompany));
                name.setText(MyTextUtils.toTitleCase(cursor.getString(iCompany)));
                user_name = cursor.getString(iCompany);
                nameString = cursor.getString(iCompany);
            }
            cursor.close();
        }

        iniStuff();
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // scroll();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    send.setVisibility(View.VISIBLE);
                    send.setImageResource(R.drawable.send_with_padding);
                } else {
                    if (!compIDlist.contains(AppUtil.TAG_High_Five))
                    {
                        send.setVisibility(View.VISIBLE);
                        send.setImageResource(R.drawable.hifi_with_padding);
                    }
                    else
                    {
                        //send.setImageResource(R.drawable.send_with_padding);
                        send.setVisibility(View.GONE);
                    }


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                scroll();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        chattingSetup();
        int from = getIntent().getIntExtra("from", 0);
        Log.d("FROM", "" + from);
        if (from == 101) {
            String messageId;
            final Calendar c = Calendar.getInstance();
            Bundle result = getIntent().getBundleExtra("result");
            String code = result.getString("code");
            String name = result.getString("name");
            String price = result.getString("price");
            String mrp = result.getString("mrp");
            String url = result.getString("url");
            String moq = result.getString("moq");


            messageId = jsonMessage.sendProduct(chat, name, code, price, mrp, url, moq);
            String chatId;
            Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
            if (cursor2.getCount() > 0) {
                cursor2.moveToFirst();
                int iRId = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                chatId = cursor2.getString(iRId);
                ContentValues cv1 = new ContentValues();
                cv1.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
            } else {
                ContentValues cv1 = new ContentValues();
                cv1.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                cv1.put(ChatUserSchema.KEY_UNREAD, 0);
                cv1.put(ChatUserSchema.KEY_CHAT_USER, account);
                cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                long id = ContentUris.parseId(uri);
                chatId = "" + id;
            }


            ContentValues values = new ContentValues();
            values.put(ChatSchema.KEY_SENDER, userLogin);
            values.put(ChatSchema.KEY_RECEIVER, account);
            values.put(ChatSchema.KEY_MSG, name);
            values.put(ChatSchema.KEY_DIS, 1);
            values.put(ChatSchema.KEY_CHAT_ID, chatId);
            values.put(ChatSchema.KEY_SUB, "product");
            values.put(ChatSchema.KEY_MSG_ID, messageId);
            values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
            values.put(ChatSchema.IsMe, 3);
            values.put("status", 1);
            Log.e("name", name);
            getContentResolver().insert(ChatProvider.CONTENT_URI, values);
            ContentValues cv = new ContentValues();
            cv.put(ProSchema.KEY_ROW_ID, messageId);
            cv.put(ProSchema.PRODUCT_NAME, name);
            cv.put(ProSchema.PRODUCT_CODE, code);
            cv.put(ProSchema.PRODUCT_MRP, mrp);
            cv.put(ProSchema.PRODUCT_PRICE, price);
            cv.put(ProSchema.PRODUCT_URL, url);
            cv.put(ProSchema.PRODUCT_MOQ, moq);
            cv.put(ProSchema.KEY_STATUS, 1);
            cv.put(ProSchema.KEY_CREATED, "" + format.format(c.getTime()));
            getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);
            getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            getContentResolver().notifyChange(ChatProvider.URI_PRODUCT, null, false);

        } else if (from == 102) {
            final Calendar c = Calendar.getInstance();

            String messageId;
            String chatId;
            String query = getIntent().getStringExtra("query");
            String url = getIntent().getStringExtra("url");
            messageId = jsonMessage.sendQuery(chat, query, url);

            Cursor cursor2 = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
            if (cursor2.getCount() > 0) {
                cursor2.moveToFirst();
                int iRId = cursor2.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                chatId = cursor2.getString(iRId);
                ContentValues cv1 = new ContentValues();
                cv1.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
            } else {
                ContentValues cv1 = new ContentValues();
                cv1.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                cv1.put(ChatUserSchema.KEY_UNREAD, 0);
                cv1.put(ChatUserSchema.KEY_CHAT_USER, account);
                cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                long id = ContentUris.parseId(uri);
                chatId = "" + id;
            }


            ContentValues values = new ContentValues();
            values.put(ChatSchema.KEY_SENDER, userLogin);
            values.put(ChatSchema.KEY_RECEIVER, account);

            values.put(ChatSchema.KEY_MSG, "" + query);
            values.put(ChatSchema.KEY_DIS, 1);
            values.put(ChatSchema.KEY_CHAT_ID, chatId);
            values.put(ChatSchema.KEY_SUB, "query");
            values.put(ChatSchema.KEY_MSG_ID, messageId);
            values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
            values.put(ChatSchema.IsMe, 21);
            values.put("status", 1);
            getContentResolver().insert(ChatProvider.CONTENT_URI, values);

            ContentValues cv = new ContentValues();
            cv.put(QuerySchema.KEY_ROW_ID, messageId);
            cv.put(QuerySchema.QUERY_TEXT, query);
            cv.put(QuerySchema.KEY_CREATED, format.format(c.getTime()));
            if (ValidationUtil.isNull(url)) {
                cv.put(QuerySchema.QUERY_URL, AppUtil.URL);

            } else {
                cv.put(QuerySchema.QUERY_URL, url);
            }

            cv.put(QuerySchema.KEY_STATUS, 1);
            getContentResolver().insert(ChatProvider.QUERY_URI, cv);
            getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);

        }

    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        cd = new ConnectionDetector(getApplicationContext());
        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);

    }

    private void iniStuff() {
        back = (ImageView) findViewById(R.id.btnChatBack);
        addProduct = (ImageView) findViewById(R.id.btnAddProductChat);
        back.setOnClickListener(this);
        addProduct.setOnClickListener(this);
        search = (ImageView) findViewById(R.id.btnSearchChat);
        search.setOnClickListener(this);
        status = (TextView) findViewById(R.id.txtOnlineStatus);
        send = (ImageView) findViewById(R.id.buttonSendMsg);
        if (compIDlist.contains(AppUtil.TAG_High_Five))
        {
            send.setVisibility(View.GONE);
        }
        else
        {
            send.setVisibility(View.VISIBLE);
        }
        // msg = (EditText) findViewById(R.id.editTextMessage);
        listView = (ListView) findViewById(R.id.listViewChat);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        smileyIcon = (ImageView) findViewById(R.id.buttonSmiley);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = chatAdapter.getSelectedCount();//CheckedItemCount();
                mode.setTitle(checkedCount + 1 + " Selected");
                chatAdapter.toggleSelection(position);
                chatAdapter.notifyDataSetChanged();

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Cursor cursor = chatAdapter.getCursor();
                Calendar calendar = Calendar.getInstance();
                int iMsg = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG);
                int iId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_ROWID);
                int iMe = cursor.getColumnIndexOrThrow(ChatSchema.IsMe);
                int iSub = cursor.getColumnIndexOrThrow(ChatSchema.KEY_SUB);
                int iMsgId = cursor.getColumnIndexOrThrow(ChatSchema.KEY_MSG_ID);
                int iSender = cursor.getColumnIndexOrThrow(ChatSchema.KEY_SENDER);
                int iReceiver = cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER);
                SparseBooleanArray checked = chatAdapter.getSelectedIds();//getCheckedItemPositions();
                switch (item.getItemId()) {
                    case R.id.copy:
                        StringBuilder builder = new StringBuilder();
                        String copy_string;
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                // Do something
                                cursor.moveToPosition(cursor.getCount() - i);

                                if (builder.length() > 0) {
                                    builder.append("\n" + cursor.getString(iMsg));
                                } else {
                                    builder.append(cursor.getString(iMsg));
                                }
                            }
                        }
                        copy_string = builder.toString().replace("<b>", "").replace("</b><br>", "\n").replace("</b> ", "\n").replace("</b>", "");
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(copy_string);
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Message", copy_string);
                            clipboard.setPrimaryClip(clip);
                        }
                        mode.finish();
                        break;
                    case R.id.forward:
                        ArrayList<ForwardMessage> forwardMessages = new ArrayList<ForwardMessage>();
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                // Do something

                                cursor.moveToPosition(cursor.getCount() - i);
                                if (cursor != null) {
                                    if (!cursor.getString(iSub).equalsIgnoreCase("hifi")) {
                                        //if (!cursor.getString(iSub).equalsIgnoreCase("HiFi")) {
                                        ForwardMessage message = new ForwardMessage();
                                        message.setId(cursor.getString(iId));
                                        message.setIsMe(cursor.getString(iMe));
                                        message.setMsg(cursor.getString(iMsg));
                                        message.setMsgId(cursor.getString(iMsgId));
                                        message.setSub(cursor.getString(iSub));
                                        forwardMessages.add(message);
                                    }
                                }
                            }
                        }
                        mode.finish();
                        if (forwardMessages.size() > 0) {
                            Intent intent = new Intent(ChatActivity.this, ForwardActivity.class);
                            intent.putExtra("myList", forwardMessages);
                            intent.putExtra("account", account);
                            startActivityForResult(intent, PICK_REQUEST);
                        }
                        break;

                    case R.id.bookMark:
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                // Do something
                                String keyUser;
                                cursor.moveToPosition(cursor.getCount() - i);
                                String username = userLogin;
                                if (username.equals(cursor.getString(iReceiver))) {
                                    keyUser = cursor.getString(iSender);
                                } else {
                                    keyUser = cursor.getString(iReceiver);
                                }
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(BookSchema.KEY_ROWID, cursor.getString(iId));
                                contentValues.put(BookSchema.KEY_MSG_ID, cursor.getString(iMsgId));
                                contentValues.put(BookSchema.KEY_MSG, cursor.getString(iMsg));
                                contentValues.put(BookSchema.KEY_SUB, cursor.getString(iSub));
                                contentValues.put(BookSchema.KEY_USER_ID, keyUser);
                                contentValues.put(BookSchema.IsMe, cursor.getString(iMe));
                                contentValues.put(BookSchema.KEY_CREATED, format.format(calendar.getTime()));
                                contentValues.put(BookSchema.KEY_STATUS, "1");
                                contentValues.put(BookSchema.KEY_USER, name.getText().toString());
                                Uri uri = getContentResolver().insert(ChatProvider.BOOKMARK_URI, contentValues);
                                getContentResolver().notifyChange(ChatProvider.BOOKMARK_URI, null, false);


                            }
                        }
                        mode.finish();
                        Toast.makeText(ChatActivity.this, "Added To Favorites",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.delete_chat_row:
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                // Do something
                                cursor.moveToPosition(cursor.getCount() - i);
                                getContentResolver().delete(ChatProvider.CONTENT_URI, ChatSchema.KEY_ROWID + "=?", new String[]{cursor.getString(iId)});
                                getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                chatAdapter.notifyDataSetChanged();
                            }
                        }
                        mode.finish();
                        Cursor myCursor = getContentResolver().query(ChatProvider.CONTENT_URI, null, "(" + ChatSchema.KEY_RECEIVER + "=?" + " AND " + ChatSchema.KEY_SENDER + "=? ) OR (" + ChatSchema.KEY_RECEIVER + "=? AND " + ChatSchema.KEY_SENDER + "=? )", new String[]{account, userLogin, userLogin, account}, "_id DESC limit " + limit);
                        if (myCursor.getCount() > 0) {
                            chatAdapter.changeCursor(myCursor);
                            Cursor c = chatAdapter.getCursor();
                            c.moveToFirst();
                            ContentValues cv = new ContentValues();
                            cv.put(ChatUserSchema.KEY_LAST_MSG, c.getString(iMsgId));
                            int isMe = c.getInt(iMe);
                            if (isMe == 2 || isMe == 11 || isMe == 21 || isMe == 3) {
                                cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            } else {
                                cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.INCOMING);
                            }
                            cv.put(ChatUserSchema.KEY_LAST_MSG, c.getString(iMsgId));
                            int count = getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});

                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);

                        }
                        break;


                }

                //   mode.finish();
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.list_select_menu, menu);
                chatHeader.setVisibility(View.GONE);

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                chatAdapter.removeSelection();
                chatHeader.setVisibility(View.VISIBLE);

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        if (cd.isConnectingToInternet()) {
            try {
                connection = XmppConnection.getInstance().getConnection();
                messageEventManager = new MessageEventManager(connection);
                mServiceIntent.putExtra("account", account);
                // Starting the CapitalService to fetch the capital of the country
                startService(mServiceIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            chattingSetup();

        }
        listView.addHeaderView(footerView);
        chatAdapter = new ChatAdapter(this, null, messageEventManager, listView);
        listView.setAdapter(chatAdapter);
        send.setOnClickListener(this);
        final Cursor c = getContentResolver().query(ChatProvider.CONTENT_URI, new String[]{ChatSchema.KEY_ROWID, ChatSchema.KEY_CREATED}, "(" + ChatSchema.KEY_RECEIVER + "=?" + " AND " + ChatSchema.KEY_SENDER + "=? ) OR (" + ChatSchema.KEY_RECEIVER + "=? AND " + ChatSchema.KEY_SENDER + "=? )", new String[]{account, userLogin, userLogin, account},  ChatSchema.KEY_CREATED + " DESC");
        String end;
        Log.d("Count Here ", " " + c.getCount());
        if (c != null) {
            int iDate = c.getColumnIndexOrThrow(ChatSchema.KEY_CREATED);
            c.moveToLast();

            if (c.getCount() == 0) {
                end = format.format(Calendar.getInstance().getTime());

            } else {
                end = c.getString(iDate);
            }
        } else {
            end = format.format(Calendar.getInstance().getTime());
        }
        hasMessage(new MessageRequest(userLogin, "chat_exist", account, "", end));
    }

    private void chattingSetup()
    {
        if (connection != null) {

            chatManager = connection.getChatManager();
            msgListener = new MessageListener() {
                @Override
                public void processMessage(Chat arg0, final Message message) {
                    SLog.i(tag, message.toXML());
                    String error = String.valueOf(message.getError());

                    if (error != null) {
                        if (!error.equals("null")) {
                            ContentValues values = new ContentValues();
                            values.put(ChatSchema.KEY_DIS, -1);
                            int count = getContentResolver().update(ChatProvider.CONTENT_URI, values, ChatSchema.KEY_MSG_ID + "=?", new String[]{message.getPacketID()});
                            getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                            getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                            stopService(new Intent(ChatActivity.this, SmackService.class));
                            startService(new Intent(ChatActivity.this, SmackService.class));

                        }
                    }
                    if (null != message.getBody() && StringUtils.parseBareAddress(message.getFrom()).equals(account)) {


                        SLog.i(tag, message.getFrom() + " ：" + message.getBody());
                    }
                }
            };
            chat = chatManager.createChat(account + "/Smack", msgListener);

        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.btnSearchChat:
                ContextThemeWrapper themedContext;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    themedContext = new ContextThemeWrapper(ChatActivity.this, R.style.MyTheme);
                } else {
                    themedContext = new ContextThemeWrapper(ChatActivity.this, R.style.MyTheme);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
                builder.setTitle("Get image from...")
                        .setCancelable(true)
                        .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,
                                        "Select Picture"), SELECT_PICTURE);
                            }
                        }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                fileUri);
                        startActivityForResult(intent,
                                CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    }
                });

                builder.show();


                break;
            case R.id.btnChatBack:
                onBackPressed();
                break;
            case R.id.cancelHifi:
                dialog.dismiss();
                break;
            case R.id.sendHifi:
                //   chattingSetup();

                double price = 0;
                String doublePrice = agreePrice.getText().toString().trim();
                if (isFloat(doublePrice)) {
                    price = Double.parseDouble(doublePrice);
                }

                if (!ValidationUtil.isNull(agreeCode.getText().toString().trim())) {
                    if (!ValidationUtil.isNull(agreePrice.getText().toString().trim())) {
                        if (price > 0) {
                            if (!ValidationUtil.isNull(agreeQuantity.getText().toString().trim())) {
                                if (ValidationUtil.isNumeric(agreeQuantity.getText().toString().trim())) {
                                    if (Integer.parseInt(agreeQuantity.getText().toString().trim()) > 0) {
                                        if (Integer.parseInt(agreeQuantity.getText().toString().trim()) <= 1000) {
                                            //    if (Integer.parseInt(agreePrice.getText().toString().trim()) > 0) {
                                            if (selectedCode != null) {
                                                if (cd.isConnectingToInternet()) {
                                                    if (!connection.isConnected()) {
                                                        connection = XmppConnection.getInstance().getConnection();
                                                    }
                                                    chattingSetup();
                                                    RandomTest test = new RandomTest();
                                                    String msg_id = test.randomString(7);
                                                    try {
//
                                                        String deal_type;
                                                        if (request_for == 0) {
                                                            deal_type = "sell";
                                                        } else {
                                                            deal_type = "buy";
                                                        }

                                                        HifiDeal deal = new HifiDeal(helper.getDefaults("user_id", getApplicationContext()), user_id, deal_type, agreeCode.getText().toString(), agreeQuantity.getText().toString().trim(), agreePrice.getText().toString().trim(), msg_id);
                                                        sendHifiDeal(deal, msg_id);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    dialog.dismiss();
                                                    // }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(ChatActivity.this, "Sorry", getResources().getString(R.string.no_internet_conn));
                                                }

                                            } else {
                                                agreeCode.setError("Product code not valid");
                                                requestFocus(agreeCode);
                                            }

                                        } else {
                                            agreeQuantity.setError("Quantity cannot be greater than 1000");
                                            requestFocus(agreeQuantity);
                                        }
                                    } else {
                                        agreeQuantity.setError("Quantity should be more then zero");
                                        requestFocus(agreeQuantity);

                                    }
                                } else {
                                    agreeQuantity.setError("Enter valid quantity");
                                    requestFocus(agreeQuantity);

                                }
                            } else {
                                agreeQuantity.setError("Enter product quantity");
                                requestFocus(agreeQuantity);

                            }
                        } else {
                            agreePrice.setError("Enter Valid price");
                            requestFocus(agreePrice);
                        }
                    } else {
                        agreePrice.setError("Enter product price");
                        requestFocus(agreePrice);

                    }
                } else {
                    agreeCode.setError("Enter product code");
                    requestFocus(agreeCode);

                }

                break;
            case R.id.btnAddProductChat:
                if (cd.isConnectingToInternet()) {
                    if (connection != null) {
                        if (!connection.isConnected()) {
                            connection = XmppConnection.getInstance().getConnection();
                        }
                        if (!connection.isAuthenticated()) {
                            getApplicationContext().stopService(new Intent(ChatActivity.this, SmackService.class));
                            getApplicationContext().startService(new Intent(ChatActivity.this, SmackService.class));
                        }
                    }
                    Intent i = new Intent(this, AttachProduct.class);
                    i.putExtra("id", company_id);
                    i.putExtra("name", nameString);
                    startActivityForResult(i, 1);
                }
                break;
            case R.id.buttonSendMsg:
                // has = false;
                scroll();
                String content = msg.getText().toString().trim();
                Log.d("Content", "" + content);
                String messageId;
                if (TextUtils.isEmpty(content)) {
                    createHifi();

                    return;
                }
                if (cd.isConnectingToInternet()) {
                    XMPPConnection con = XmppConnection.getInstance().getConnection();
                    //  Log.d("Here", "" + con.isAuthenticated());
                    if (con != null) {
                        if (con.isAuthenticated()) {
                            connection = con;
                            chattingSetup();
                        }
                    }
                    Log.d("Here", "" + connection.isAuthenticated());
                    if (cd.isConnectingToInternet() && connection.isAuthenticated()) {

                        messageId = jsonMessage.sendText(chat, content);
                        String chatId;
                        Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                            chatId = cursor.getString(iRId);
                            ContentValues cv = new ContentValues();
                            cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);

                        } else {
                            ContentValues cv = new ContentValues();
                            cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                            cv.put(ChatUserSchema.KEY_UNREAD, 0);
                            cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                            long id = ContentUris.parseId(uri);
                            chatId = "" + id;
                        }

                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_SENDER, userLogin);
                        values.put(ChatSchema.KEY_RECEIVER, account);
                        values.put(ChatSchema.KEY_DIS, 1);
                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                        values.put(ChatSchema.KEY_SUB, "text");
                        values.put(ChatSchema.KEY_MSG_ID, messageId);
                        values.put(ChatSchema.KEY_MSG, content);
                        values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                        values.put(ChatSchema.IsMe, 2);
                        values.put("status", 1);
                        // Log.e("hid", content);
                        Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                        // Log.d("ggg", uri.toString());
                        msg.setText("");


                    } else {
                        if (cd.isConnectingToInternet()) {
                            stopService(new Intent(ChatActivity.this, SmackService.class));
                            startService(new Intent(ChatActivity.this, SmackService.class));
                        }
                        String chatId;
                        Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                            chatId = cursor.getString(iRId);
                            ContentValues cv = new ContentValues();
                            //  cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        } else {
                            ContentValues cv = new ContentValues();
                            //   cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                            cv.put(ChatUserSchema.KEY_UNREAD, 0);
                            cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                            long id = ContentUris.parseId(uri);
                            chatId = "" + id;
                        }


                        ContentValues values = new ContentValues();
                        values.put(ChatSchema.KEY_SENDER, userLogin);
                        values.put(ChatSchema.KEY_RECEIVER, account);
                        values.put(ChatSchema.KEY_DIS, -1);
                        values.put(ChatSchema.KEY_CHAT_ID, chatId);
                        values.put(ChatSchema.KEY_MSG, content);
                        values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                        values.put(ChatSchema.IsMe, 2);
                        values.put("status", 1);
                        // Log.e("hid", content);
                        Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
                        // Log.d("ggg", uri.toString());
                        msg.setText("");


                    }

                } else {
                    String chatId;
                    Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                        chatId = cursor.getString(iRId);
                        ContentValues cv = new ContentValues();
                        //  cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                        cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                        cv.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv = new ContentValues();
                        //   cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                        cv.put(ChatUserSchema.KEY_UNREAD, 0);
                        cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                        cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                        cv.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long id = ContentUris.parseId(uri);
                        chatId = "" + id;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, userLogin);
                    values.put(ChatSchema.KEY_RECEIVER, account);
                    values.put(ChatSchema.KEY_DIS, -1);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_MSG, content);
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                    values.put(ChatSchema.IsMe, 2);
                    values.put("status", 1);

                    Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);

                    msg.setText("");

                }
                rId = -1;
                section = 0;
                break;

        }

    }

    private void sendHifiDeal(HifiDeal deal, final String msg_id) {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.sendDeal(deal, helper.getB64Auth(ChatActivity.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response) {
                progress.dismiss();
                int statusCode = response.code();

                if (response.isSuccess()) {
                    //  progress.dismiss();
                    JsonElement element = (JsonElement) response.body();

                    try {

                        String chatId;
                        JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                        final Calendar c2 = Calendar.getInstance();


                        jsonMessage.sendHiFi(chat, agreeCode.getText().toString().trim(), userCompany, selectedCode, agreePrice.getText().toString().trim(), agreeQuantity.getText().toString().trim(), "" + request_for, msg_id);
                        Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                            chatId = cursor.getString(iRId);
                            ContentValues cv = new ContentValues();
                            cv.put(ChatUserSchema.KEY_LAST_MSG, msg_id);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        } else {
                            ContentValues cv = new ContentValues();
                            cv.put(ChatUserSchema.KEY_LAST_MSG, msg_id);
                            cv.put(ChatUserSchema.KEY_UNREAD, 0);
                            cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                            cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                            cv.put(ChatUserSchema.KEY_TYPE, "chat");
                            cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                            Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                            long id = ContentUris.parseId(uri);
                            chatId = "" + id;
                        }


                        ContentValues values1 = new ContentValues();
                        values1.put(ChatSchema.KEY_SENDER, userLogin);
                        values1.put(ChatSchema.KEY_RECEIVER, account);
                        values1.put(ChatSchema.KEY_MSG, selectedCode);
                        values1.put(ChatSchema.KEY_DIS, 1);
                        values1.put(ChatSchema.KEY_CHAT_ID, chatId);
                        values1.put(ChatSchema.KEY_SUB, "HiFi");
                        values1.put(ChatSchema.KEY_MSG_ID, msg_id);
                        values1.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                        values1.put(ChatSchema.IsMe, 4);
                        values1.put("status", 1);
                        // Log.e("name", name);
                        getContentResolver().insert(ChatProvider.CONTENT_URI, values1);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(HiFiSchema.KEY_ROW_ID, msg_id);
                        cv1.put(HiFiSchema.PRODUCT_NAME, user_name);
                        cv1.put(HiFiSchema.PRODUCT_ID, selectedCode);
                        cv1.put(HiFiSchema.PRODUCT_CODE, agreeCode.getText().toString());
                        cv1.put(HiFiSchema.PRODUCT_PRICE, agreePrice.getText().toString().trim());
                        cv1.put(HiFiSchema.PRODUCT_QUANTITY, agreeQuantity.getText().toString().trim());
                        cv1.put(HiFiSchema.ACCEPT, 0);
                        cv1.put(HiFiSchema.REQUEST_FOR, request_for);
                        cv1.put(HiFiSchema.KEY_STATUS, 1);
                        cv1.put(HiFiSchema.KEY_CREATED, "" + format.format(c2.getTime()));
                        getContentResolver().insert(ChatProvider.HiFi_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                        selectedCode = null;
                        rId = -1;
                        section = 0;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(ChatActivity.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }

                    String error2 = getString(R.string.server_error);
                    if (statusCode == 404) {

                        new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                    } else if (statusCode == 500) {
                        new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);

                    } else {
                        new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);

                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {

                progress.dismiss();
            }
        });
    }

    private void createHifi() {
        dialog = new Dialog(ChatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.hi_fi_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        cancelHifi = (Button) dialog.findViewById(R.id.cancelHifi);
        sendHifi = (Button) dialog.findViewById(R.id.sendHifi);
        agreeCode = (AutoCompleteTextView) dialog.findViewById(R.id.editHifiCode);
        agreePrice = (EditText) dialog.findViewById(R.id.editHifiPrice);
        agreeQuantity = (EditText) dialog.findViewById(R.id.editHifiQuintity);
        radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupSell);
        company = Integer.parseInt(catTo);
        final RadioButton RB1 = (RadioButton) dialog.findViewById(R.id.radioSell);
        final RadioButton RB2 = (RadioButton) dialog.findViewById(R.id.radioBuy);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                if (checkedId == R.id.radioSell) {
                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        Drawable d1 = getResources().getDrawable(R.drawable.checked_form, ChatActivity.this.getTheme());
                        Drawable d2 = getResources().getDrawable(R.drawable.unchecked_form, ChatActivity.this.getTheme());
                        RB1.setButtonDrawable(d1);
                        RB2.setButtonDrawable(d2);

                    } else {
                        RB1.setButtonDrawable(R.drawable.checked_form);
                        RB2.setButtonDrawable(R.drawable.unchecked_form);

                    }
                    company = Integer.parseInt(catTo);
                    selectedCode = null;
                    agreeCode.setText("");
                    request_for = 0;

                } else {
                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        Drawable d1 = getResources().getDrawable(R.drawable.checked_form, ChatActivity.this.getTheme());
                        Drawable d2 = getResources().getDrawable(R.drawable.unchecked_form, ChatActivity.this.getTheme());
                        RB2.setButtonDrawable(d1);
                        RB1.setButtonDrawable(d2);
                    } else {
                        RB2.setButtonDrawable(R.drawable.checked_form);
                        RB1.setButtonDrawable(R.drawable.unchecked_form);
                    }
                    selectedCode = null;
                    agreeCode.setText("");
                    company = Integer.parseInt(company_id);
                    request_for = 1;
                }

            }

        });

        cancelHifi.setOnClickListener(this);
        sendHifi.setOnClickListener(this);
        agreeCode.setAdapter(autoCode);
        agreeCode.setThreshold(1);
        agreeCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                autoCode.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        agreeCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ProductCode selected = (ProductCode) arg0.getAdapter().getItem(arg2);
                selectedCode = selected.getId();


            }
        });

    }

    private void scroll() {
        if (rId != -1) {
            listView.setSelection(rId - 1);
        } else {
            listView.setSelection(listView.getCount() - 1);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ChatProvider.CONTENT_URI, null, "(" + ChatSchema.KEY_RECEIVER + "=?" + " AND " + ChatSchema.KEY_SENDER + "=? ) OR (" + ChatSchema.KEY_RECEIVER + "=? AND " + ChatSchema.KEY_SENDER + "=? )", new String[]{account, userLogin, userLogin, account}, ChatSchema.KEY_CREATED + " DESC limit " + limit);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor myCursor = getContentResolver().query(ChatProvider.CONTENT_URI, new String[]{ChatSchema.KEY_ROWID}, "(" + ChatSchema.KEY_RECEIVER + "=?" + " AND " + ChatSchema.KEY_SENDER + "=? ) OR (" + ChatSchema.KEY_RECEIVER + "=? AND " + ChatSchema.KEY_SENDER + "=? )", new String[]{account, userLogin, userLogin, account}, null);
        if (myCursor.getCount() > data.getCount()) {
            footerView.setVisibility(View.VISIBLE);
        } else {
            if (has) {
                footerView.setVisibility(View.VISIBLE);
            } else {
                footerView.setVisibility(View.GONE);
//                rId = -1;
//                section = 0;
//                scroll();
            }

        }
        chatAdapter.swapCursor(data);
        if (section != 0) {
            //scroll();
            listView.setSelection(data.getCount() - section);

        } else {
            scroll();
        }

        ContentValues cv = new ContentValues();
        cv.put(ChatUserSchema.KEY_UNREAD, 0);
        int count = getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        chatAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Calendar c = Calendar.getInstance();

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (connection != null) {
                    if (!connection.isConnected()) {
                        connection = XmppConnection.getInstance().getConnection();
                    }
                    chattingSetup();
                    String messageId = "";

                    Bundle result = data.getBundleExtra("result");
                    String code = result.getString("code");
                    String name = result.getString("name");
                    String price = result.getString("price");
                    String mrp = result.getString("mrp");
                    String url = result.getString("url");
                    String moq = result.getString("moq");

                    messageId = jsonMessage.sendProduct(chat, name, code, price, mrp, url, moq);
                    String chatId;
                    Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                        chatId = cursor.getString(iRId);
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv1, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    } else {
                        ContentValues cv1 = new ContentValues();
                        cv1.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                        cv1.put(ChatUserSchema.KEY_UNREAD, 0);
                        cv1.put(ChatUserSchema.KEY_CHAT_USER, account);
                        cv1.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                        cv1.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv1.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv1);
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                        long id = ContentUris.parseId(uri);
                        chatId = "" + id;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, userLogin);
                    values.put(ChatSchema.KEY_RECEIVER, account);
                    values.put(ChatSchema.KEY_MSG, name);
                    values.put(ChatSchema.KEY_DIS, 1);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_SUB, "product");
                    values.put(ChatSchema.KEY_MSG_ID, messageId);
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                    values.put(ChatSchema.IsMe, 3);
                    values.put("status", 1);
                    Log.e("name", name);
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                    ContentValues cv = new ContentValues();
                    cv.put(ProSchema.KEY_ROW_ID, messageId);
                    cv.put(ProSchema.PRODUCT_NAME, name);
                    cv.put(ProSchema.PRODUCT_CODE, code);
                    cv.put(ProSchema.PRODUCT_MRP, mrp);
                    cv.put(ProSchema.PRODUCT_PRICE, price);
                    cv.put(ProSchema.PRODUCT_URL, url);
                    cv.put(ProSchema.PRODUCT_MOQ, moq);
                    cv.put(ProSchema.KEY_STATUS, 1);
                    cv.put(ProSchema.KEY_CREATED, "" + format.format(c.getTime()));
                    getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.URI_PRODUCT, null, false);


                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
            // }
        } else if (requestCode == 1009) {
            if (resultCode == RESULT_OK) {
                startActivity((new Intent(ChatActivity.this, ChatActivity.class)).putExtra("user", data.getStringExtra("current_user")));
                finish();

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
            //  }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                String chatId;
                Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    chatId = cursor.getString(iRId);
                    ContentValues cv = new ContentValues();
                    cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                    cv.put(ChatUserSchema.KEY_TYPE, "chat");
                    cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(ChatUserSchema.KEY_UNREAD, 0);
                    cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                    cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                    cv.put(ChatUserSchema.KEY_TYPE, "chat");
                    cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    long id = ContentUris.parseId(uri);
                    chatId = "" + id;
                }

                try {
                    String yourFile = fileUri.getPath();
                    ContentValues values = new ContentValues();
                    values.put(ChatSchema.KEY_SENDER, userLogin);
                    values.put(ChatSchema.KEY_RECEIVER, account);
                    values.put(ChatSchema.KEY_MSG, "" + decodeFile(yourFile));
                    values.put(ChatSchema.KEY_DIS, 1);
                    values.put(ChatSchema.KEY_CHAT_ID, chatId);
                    values.put(ChatSchema.KEY_SUB, "img");
                    RandomTest randomTest = new RandomTest();
                    String ui = randomTest.randomString(7);
                    values.put(ChatSchema.KEY_MSG_ID, ui);
                    values.put(ChatSchema.IsDownload, 0);
                    values.put(ChatSchema.IsError, 0);
                    values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                    values.put(ChatSchema.IsMe, 11);
                    values.put("status", 0);
                    getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {

                String selectedImagePath = null;
                Uri uri = data.getData();
                String chatId;
                selectedImagePath = getPath(ChatActivity.this, uri);
                String finalPath = decodeFile(selectedImagePath);
                Log.d("Final", finalPath);
                Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                    chatId = cursor.getString(iRId);
                    ContentValues cv = new ContentValues();
                    cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                    cv.put(ChatUserSchema.KEY_TYPE, "chat");
                    cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    Log.d("Insert DB", "1");
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(ChatUserSchema.KEY_UNREAD, 0);
                    cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                    cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                    cv.put(ChatUserSchema.KEY_TYPE, "chat");
                    cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    Uri uri1 = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    long id = ContentUris.parseId(uri1);
                    chatId = "" + id;
                }
                ContentValues values = new ContentValues();
                values.put(ChatSchema.KEY_SENDER, userLogin);
                values.put(ChatSchema.KEY_RECEIVER, account);
                values.put(ChatSchema.KEY_MSG, "" + finalPath);
                values.put(ChatSchema.KEY_DIS, 1);
                values.put(ChatSchema.KEY_CHAT_ID, chatId);
                values.put(ChatSchema.KEY_SUB, "img");
                RandomTest randomTest = new RandomTest();
                String ui = randomTest.randomString(7);
                Log.d("IMAGE ID", ui);
                values.put(ChatSchema.KEY_MSG_ID, ui);
                values.put(ChatSchema.IsDownload, 0);
                values.put(ChatSchema.IsError, 0);
                values.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                values.put(ChatSchema.IsMe, 11);
                values.put("status", 0);
                getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled pick image", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to pick image", Toast.LENGTH_SHORT)
                        .show();

            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry!! something went wrong", Toast.LENGTH_SHORT)
                    .show();
        }
        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (XmppConnection.getInstance().getConnection() == null) {
            this.startService(new Intent(this, SmackService.class));
        }
        clearNotification(ChatActivity.this);
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));
        isForeground = true;
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Chat");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    public void clearNotification(Context mContext) {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    protected void onPause() {
        super.onPause();
        isForeground = false;
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
        getContentResolver().notifyChange(ChatProvider.URI_USER, null, false);
        //flag = 0;
        //  has = false;
//        Intent i = new Intent(ChatActivity.this, Home.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
        finish();
        super.onBackPressed();
    }

    public void onAccept(View v) {

        if (!connection.isConnected()) {
            connection = XmppConnection.getInstance().getConnection();
            chattingSetup();
        }
        String msgId = (String) v.getTag();
        Cursor c = getContentResolver().query(ChatProvider.HiFi_URI, null, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId}, null);
        if (c != null && c.moveToFirst()) {
            int iAccept = c.getColumnIndexOrThrow(HiFiSchema.ACCEPT);
            int iProduct = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_ID);
            int iQty = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_QUANTITY);
            int iPrice = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_PRICE);
            int iName = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_NAME);
            int iRequest = c.getColumnIndexOrThrow(HiFiSchema.REQUEST_FOR);
            int iCode = c.getColumnIndexOrThrow(HiFiSchema.PRODUCT_CODE);
            int accept = c.getInt(iAccept);
            int request = c.getInt(iRequest);
            double proposedAmount = c.getDouble(iQty) * c.getDouble(iPrice);
            if (accept == 1) {

                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 5);
                int count = getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                if (count > 0) {
                    jsonMessage.sendAction(chat, "accept", msgId);
                    UpdateDeal deal = new UpdateDeal("1", msgId);
                    updateDeal(deal);

                }
            } else if (accept == 3) {
                if (request == 1) {
                    application.sendDataToTwoTrackers(new HitBuilders.ItemBuilder()
                            .setTransactionId("101")
                            .setName(c.getString(iName))
                            .setSku("SKU")
                            .setCategory(c.getString(iCode))
                            .setPrice(c.getDouble(iPrice))
                            .setQuantity(c.getInt(iQty))
                            .setCurrencyCode("INR")
                            .build());
                    prepareOrder(msgId, helper.getDefaults("user_id", ChatActivity.this), c.getString(iProduct), c.getDouble(iQty), c.getDouble(iPrice), proposedAmount);
                }

            } else if (accept == 5) {
                if (request == 0) {
                    application.sendDataToTwoTrackers(new HitBuilders.ItemBuilder()
                            .setTransactionId("101")
                            .setName(c.getString(iName))
                            .setSku("SKU")
                            .setCategory(c.getString(iCode))
                            .setPrice(c.getDouble(iPrice))
                            .setQuantity(c.getInt(iQty))
                            .setCurrencyCode("INR")
                            .build());

                    prepareOrder(msgId, helper.getDefaults("user_id", ChatActivity.this), c.getString(iProduct), c.getDouble(iQty), c.getDouble(iPrice), proposedAmount);

                }
            }
        }
        c.close();
    }

    public void onDecline(View v) {
        if (!connection.isConnected()) {
            connection = XmppConnection.getInstance().getConnection();
            chattingSetup();
        }
        //   String responseID;
        String msgId = (String) v.getTag();

        Cursor c = getContentResolver().query(ChatProvider.HiFi_URI, null, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId}, null);
        if (c.getCount() > 0) {
            int iAccept = c.getColumnIndexOrThrow(HiFiSchema.ACCEPT);
            c.moveToFirst();
            if (c.getInt(iAccept) == 1 || c.getInt(iAccept) == 0) {
                int count;
                if (c.getInt(iAccept) == 0) {
                    ContentValues cv = new ContentValues();
                    cv.put(HiFiSchema.ACCEPT, 9);
                    count = getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                    getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(HiFiSchema.ACCEPT, 6);
                    count = getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                    getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);

                }
                jsonMessage.sendAction(chat, "decline", msgId);
                if (count > 0) {
                    UpdateDeal deal = new UpdateDeal("2", msgId);
                    updateDeal(deal);


                }
            }
        }

    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private String decodeFile(String path) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path,
                    DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= 1000 && unscaledBitmap
                    .getHeight() <= 1000)) {
                // Part 2: Scale image

                scaledBitmap = ScalingUtilities.createScaledBitmap(
                        unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT,
                        ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            File f = getMediaFile(MEDIA_TYPE_IMAGE);

            strMyImagePath = f.getAbsolutePath();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return "";
        }
        return strMyImagePath;

    }

    public void onZoom(View v) {
        String path = (String) v.getTag();
        if (path != null) {
            String[] imageUrls = new String[]{path};
            Intent intent = new Intent(this, ImagePagerActivity.class);
            intent.putExtra("url", imageUrls);
            startActivity(intent);
        }
    }

    public void onZoomLeft(View v) {
        String path = (String) v.getTag();
        if (path != null) {
            String[] imageUrls = new String[]{path};
            Intent intent = new Intent(this, ImagePagerActivity.class);
            intent.putExtra("url", imageUrls);

            startActivity(intent);
        }
    }

    public void onLoadMessage(View v) {

        final Cursor c = chatAdapter.getCursor();
        final int count = c.getCount();

        myCursor = getContentResolver().query(ChatProvider.CONTENT_URI, new String[]{ChatSchema.KEY_ROWID}, "(" + ChatSchema.KEY_RECEIVER + "=?" + " AND " + ChatSchema.KEY_SENDER + "=? ) OR (" + ChatSchema.KEY_RECEIVER + "=? AND " + ChatSchema.KEY_SENDER + "=? )", new String[]{account, userLogin, userLogin, account}, null);

        if (c.getCount() == myCursor.getCount()) {
            section = chatAdapter.getCursor().getCount();
            if (cd.isConnectingToInternet()) {
                progress.show();
                limit += 40;
                String endDate = null;
                //   final Cursor c = chatAdapter.getCursor();
                if (c != null) {
                    int iDate = c.getColumnIndexOrThrow(ChatSchema.KEY_CREATED);
                    c.moveToLast();
                    if (c.getCount() == 0) {
                        endDate = format.format(Calendar.getInstance().getTime());
                    } else {
                        endDate = c.getString(iDate);

                    }
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(format.parse(endDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    cal.add(Calendar.DAY_OF_MONTH, -5);
                    Date time = cal.getTime();
                    String start = format.format(time);
                    MessageRequest request = new MessageRequest(userLogin, "chat", account, start, endDate);
                    Gson gson = new Gson();
                    Log.d("Re", gson.toJson(request));
                    getMessage(request);
                }
            } else {
                new AlertDialogManager().showAlertDialog(ChatActivity.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
            }

        } else {
            section = chatAdapter.getCursor().getCount();
            limit += 40;
            getSupportLoaderManager().restartLoader(5, null, ChatActivity.this);
            listView.setSelection(section - count);
        }
    }


    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    public void startPayment(String amount)
    {
        String email = null, phone = null;
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Login, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int iMob = cursor.getColumnIndexOrThrow(LoginDB.KEY_PHONE);
            int iEmail = cursor.getColumnIndexOrThrow(LoginDB.KEY_EMAIL);
            email = cursor.getString(iEmail);
            phone = cursor.getString(iMob);
        }
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setPublicKey(AppUtil.public_key_test);

        try {
            JSONObject options = new JSONObject("{" +
                    "description: ''," +
                    "image: 'https://rzp-mobile.s3.amazonaws.com/images/rzp.png'," +
                    "currency: 'INR'}"
            );

            options.put("amount", amount);
            options.put("name", "WYDR");
            options.put("prefill", new JSONObject("{email: '" + email + "', contact: '" + phone + "'}"));

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void onPaymentSuccess(String razorpayPaymentID) {

        updateOrders(new OrderStatus("P", AppUtil.PAYMENT_ID, new Transactions(razorpayPaymentID)));

    }

    public void onPaymentError(int code, String response) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "F");
            updateOrders(new OrderStatus("F", AppUtil.PAYMENT_ID, new Transactions("")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean Exists(String searchItem) {

        String[] columns = {ChatSchema.KEY_ROWID, ChatSchema.KEY_MSG_ID, ChatSchema.KEY_MSG};
        String selection = ChatSchema.KEY_MSG_ID + "=?";
        String[] selectionArgs = {searchItem};
        Cursor cursor = getContentResolver().query(ChatProvider.CONTENT_URI, columns, selection, selectionArgs, null);
        if (cursor.getCount() > 0) {
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        }
        boolean exists = (cursor.getCount() > 0);
        Log.d("IS ", "" + exists);
        cursor.close();
        return exists;
    }

    // Defining a BroadcastReceiver
    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String capital = intent.getStringExtra(AppUtil.EXTRA_STATUS);
            status.setText(capital);
            status.setVisibility(View.VISIBLE);
        }

    }

    private void updateOrders(final OrderStatus status) {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        final Call<JsonElement> call = service.updateOrder(orderId, status, helper.getB64Auth(ChatActivity.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response) {
                progress.dismiss();
                int statusCode = response.code();
                if (response.isSuccess()) {
                    progress.dismiss();
                    getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    startActivity(new Intent(ChatActivity.this, ThankActivity.class).putExtra("order_id", orderId).putExtra("status", status.getStatus()).
                            putExtra("ordertotal", ordertotal).putExtra("productOrderedDetailsList", productOrderedDetailsList));
                    orderId = null;
                    finish();
                } else {

                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(ChatActivity.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }

                    String error2 = getString(R.string.server_error);
                    if (statusCode == 404) {

                        new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                    } else if (statusCode == 500) {
                        new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);

                    } else {
                        new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);

                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                progress.dismiss();
            }
        });


    }

    private OrderInDeal order(String msgId, String user_id, String product_id, double qty,
                              double price) {
        HashMap<String, ProductInDeal> map = new HashMap<>();
        map.put("1", new ProductInDeal(product_id, "Y", qty, price));
        OrderInDeal inDeal = new OrderInDeal(user_id, AppUtil.PAYMENT_ID, msgId, true, map);
        Gson g = new Gson();
        String jsonString = g.toJson(inDeal);
        Log.d("JSON ", jsonString.toString());


        return inDeal;
    }

    private void prepareOrder(final String msgId, String user_id, String product_id,
                              double qty, double price, final double preposedAmount) {
        progress.show();
        /*Gson gson = new Gson();
        Log.e("HiFi", gson.toJson(order(msgId, user_id, product_id, qty, price)));*/
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.bookOrderInDeal(order(msgId, user_id, product_id, qty, price), helper.getB64Auth(ChatActivity.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
                         @Override
                         public void onResponse(Response response) {
                             progress.dismiss();
                             if (response.isSuccess()) {
                                 JsonElement element = (JsonElement) response.body();
                                 try {
                                     Log.d("JSON", " " + element.getAsJsonObject().toString());
                                     JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                                     ContentValues cv = new ContentValues();
                                     cv.put(HiFiSchema.ACCEPT, 7);
                                     int count = getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                                     int finalAmount = (int) preposedAmount * 100;
                                     try {

                                         orderId = json.getString("order_id");
                                         /************************** ISTIAQUE ****************************************/
                                         ordertotal = json.getString("ordertotal");
                                         productOrderedDetailsList = new ArrayList<ProductOrderedDetails>();
                                         JSONArray jsonArray = json.getJSONArray("products");
                                         for (int j = 0; j < jsonArray.length(); j++) {
                                             ProductOrderedDetails productOrderedDetails = new ProductOrderedDetails();
                                             JSONObject productJSON = jsonArray.getJSONObject(j);
                                             String hiFiProduct_id = productJSON.getString("product_id");
                                             String name = productJSON.getString("name");
                                             String qty = productJSON.getString("qty");
                                             String category = productJSON.getString("category");
                                             String price = productJSON.getString("price");
                                             productOrderedDetails.setProduct_id(hiFiProduct_id);
                                             productOrderedDetails.setName(name);
                                             productOrderedDetails.setQty(qty);
                                             productOrderedDetails.setCategory(category);
                                             productOrderedDetails.setPrice(price);
                                             productOrderedDetails.setOrderId(orderId);
                                             productOrderedDetailsList.add(productOrderedDetails);
                                             //Log.e("HiFIO->", orderId + "\n" + ordertotal + "\n" + hiFiProduct_id + "\n" + name + "\n" + qty + "\n" + category + "\n" + price);
                                         }
                                         /******************************************************************************/
                                         startPayment("" + finalAmount);
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }

                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }

                             } else {
                                 int statusCode = response.code();
                                 if (statusCode == 401) {
                                     final SessionManager sessionManager = new SessionManager(ChatActivity.this);
                                     Handler mainHandler = new Handler(Looper.getMainLooper());

                                     Runnable myRunnable = new Runnable() {
                                         @Override
                                         public void run() {
                                             sessionManager.logoutUser();
                                         } // This is your code
                                     };
                                     mainHandler.post(myRunnable);
                                 }
                                 // if(error2.equalsIgnoreCase(""))
                                 String error2 = getString(R.string.server_error);
                                 if (statusCode == 404) {
                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                 } else if (statusCode == 500) {
                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                 } else {
                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                 }
                             }


                         }

                         @Override
                         public void onFailure(Throwable t) {
                             progress.dismiss();


                         }
                     }

        );

    }

    private void updateDeal(UpdateDeal deal) {
        progress.show();
        String user = helper.getDefaults("user_id", getApplicationContext());
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.updateDeal(user, deal, helper.getB64Auth(ChatActivity.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
                         @Override
                         public void onResponse(Response response) {
                             progress.dismiss();
                             if (response.isSuccess()) {

                             } else {
                                 int statusCode = response.code();
                                 //}
                                 if (statusCode == 401) {

                                     final SessionManager sessionManager = new SessionManager(ChatActivity.this);
                                     Handler mainHandler = new Handler(Looper.getMainLooper());

                                     Runnable myRunnable = new Runnable() {
                                         @Override
                                         public void run() {
                                             sessionManager.logoutUser();
                                         } // This is your code
                                     };
                                     mainHandler.post(myRunnable);
                                 }
                                 // if(error2.equalsIgnoreCase(""))
                                 String error2 = getString(R.string.server_error);
                                 if (statusCode == 404) {
                                     //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                 } else if (statusCode == 500) {
                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                     //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                 } else {
                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                     // Toast.makeText(AddressList.this, , Toast.LENGTH_LONG).show();
                                 }
                             }


                         }

                         @Override
                         public void onFailure(Throwable t) {

                             progress.dismiss();
                             Log.d("Here", t.toString());


                         }
                     }

        );

    }

    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    public static boolean isFloat(String s) {
        return DOUBLE_PATTERN.matcher(s).matches();
    }

    public int checkDates(Date startDate, Date endDate) {
        int b = 0;
        try {
            if (startDate.compareTo(endDate) > 0) {
                b = 0;
            } else if (startDate.equals(endDate)) {
                b = 1;  // If two dates are equal.
            } else {
                b = 2; // If start date is after the end date.
            }
            Calendar firstCalendar = Calendar.getInstance();
            firstCalendar.setTime(startDate);
            Calendar secondCalendar = Calendar.getInstance();
            secondCalendar.setTime(endDate); //set the time as the second java.util.Date
            int year = Calendar.YEAR;
            int difference = firstCalendar.get(year) - secondCalendar.get(year);
            if (difference > 10) {
                b = 3;  // If  end date comes of 1970 (wrong date).
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }


    private boolean dateFormat(Date past, Date current) {

        //int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
        int diffInDays = (int) ((current.getTime() - past.getTime()) / (1000 * 60 * 60 * 24));
        // System.out.println("Difference in Days : " + diffInDays);
        return diffInDays <= 1;
        //return "";
    }

    private void getMessage(MessageRequest request) {
        if (!progress.isShowing()) {
            progress.show();
        }

        final String endDate = request.getStartdate();
        RestClientOpenfire.GitApiInterface service = RestClientOpenfire.getClient();
        Call<JsonElement> call = service.getMessage(request);
        call.enqueue(new Callback<JsonElement>() {
                         @Override
                         public void onResponse(Response response) {
                             Log.d("response", "" + response.raw() + " " + response.code());
                             //    progress.dismiss();
                             if (ChatActivity.this != null && !ChatActivity.this.isFinishing()) {
                                 if (response.isSuccess()) {
                                     JsonElement element = (JsonElement) response.body();
                                     Log.d("JSON", " " + element.getAsJsonObject().toString());
                                     try {
                                         JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                                         int msgCount = json.optInt("message_count");
                                         if (msgCount > 0) {
                                             String chatId;
                                             Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account}, null);
                                             if (cursor.getCount() > 0) {
                                                 cursor.moveToFirst();
                                                 int iRId = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                                                 chatId = cursor.getString(iRId);
                                                 ContentValues cv = new ContentValues();
                                                 //   cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                                                 cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                                                 cv.put(ChatUserSchema.KEY_TYPE, "chat");
                                                 cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                                 getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{account});
                                                 getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);

                                             } else {
                                                 ContentValues cv = new ContentValues();
                                                 //  cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                                                 cv.put(ChatUserSchema.KEY_UNREAD, 0);
                                                 cv.put(ChatUserSchema.KEY_CHAT_USER, account);
                                                 cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                                                 cv.put(ChatUserSchema.KEY_TYPE, "chat");
                                                 cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                                 Uri uri = getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                                                 getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                                 long id = ContentUris.parseId(uri);
                                                 chatId = "" + id;
                                             }

                                             JSONArray array = json.getJSONArray("messages");
                                             for (int i = 0; i < array.length(); i++) {
                                                 JSONObject jsonObject = array.getJSONObject(i);
                                                 String direction = jsonObject.optString("direction");
                                                 String receiveTimeString = jsonObject.optString("time");
                                                 Log.i("Content**", jsonObject.toString());
                                                 if (direction.equalsIgnoreCase("from")) {
                                                     JSONObject content = new JSONObject(jsonObject.optString("body"));
                                                     Log.d("Content", content.toString());
                                                     String subject = content.optString("subject");
                                                     String packetData = content.optString("packet_id");

                                                     if (packetData != null) {
                                                         if (!Exists(packetData)) {
                                                             {
                                                                 if (subject.equals("text")) {

                                                                     ContentValues values = new ContentValues();
                                                                     values.put(ChatSchema.KEY_SENDER, account);
                                                                     values.put(ChatSchema.KEY_RECEIVER, userLogin);
                                                                     values.put(ChatSchema.KEY_MSG, content.optString("text"));
                                                                     values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                     values.put(ChatSchema.IsMe, 1);
                                                                     values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                     values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                                                     values.put(ChatSchema.KEY_SUB, "text");
                                                                     values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                     values.put(ChatSchema.KEY_DIS, 2);
                                                                     values.put("status", 1);
                                                                     getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                                                                 } else if (subject.equals("product")) {
                                                                     String nameProduct = null;
                                                                     String code = null;
                                                                     String mrp = null;
                                                                     String price = null;
                                                                     String url = null;
                                                                     String moq = null;

                                                                     nameProduct = content.optString("name");
                                                                     code = content.optString("code");
                                                                     mrp = content.optString("mrp");
                                                                     price = content.optString("price");
                                                                     url = content.optString("url");
                                                                     moq = content.optString("moq");

                                                                     ContentValues values = new ContentValues();
                                                                     values.put(ChatSchema.KEY_SENDER, account);
                                                                     values.put(ChatSchema.KEY_RECEIVER, userLogin);
                                                                     values.put(ChatSchema.KEY_MSG, content.optString("name"));
                                                                     values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                     values.put(ChatSchema.IsMe, 19);
                                                                     values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                     values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                                                     values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                     values.put(ChatSchema.KEY_SUB, subject);
                                                                     values.put(ChatSchema.KEY_DIS, 2);
                                                                     values.put("status", 1);
                                                                     getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                                                     ContentValues cv = new ContentValues();
                                                                     cv.put(ProSchema.KEY_ROW_ID, packetData);
                                                                     cv.put(ProSchema.PRODUCT_NAME, nameProduct);
                                                                     cv.put(ProSchema.PRODUCT_CODE, code);
                                                                     cv.put(ProSchema.PRODUCT_MRP, mrp);
                                                                     cv.put(ProSchema.PRODUCT_PRICE, price);
                                                                     cv.put(ProSchema.PRODUCT_URL, url);
                                                                     cv.put(ProSchema.PRODUCT_MOQ, moq);
                                                                     cv.put(ProSchema.KEY_STATUS, 1);
                                                                     cv.put(ProSchema.KEY_CREATED, receiveTimeString);
                                                                     getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);
                                                                     // getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                     getContentResolver().notifyChange(ChatProvider.URI_PRODUCT, null, false);
                                                                 } else if (subject.equalsIgnoreCase("HiFi")) {
                                                                     String nameProduct = content.optString("name");
                                                                     String code = content.optString("code");
                                                                     String id = content.optString("product_id");
                                                                     String price = content.optString("price");
                                                                     String qty = content.optString("qty");
                                                                     String requestFor = content.optString("request_for");

                                                                     ContentValues values = new ContentValues();
                                                                     values.put(ChatSchema.KEY_SENDER, account);
                                                                     values.put(ChatSchema.KEY_RECEIVER, userLogin);

                                                                     values.put(ChatSchema.KEY_MSG, content.optString("HiFi"));
                                                                     values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                     values.put(ChatSchema.IsMe, 4);
                                                                     values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                     values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                     values.put(ChatSchema.KEY_SUB, subject);
                                                                     values.put(ChatSchema.KEY_DIS, 2);
                                                                     values.put("status", 1);
                                                                     //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                                                                     getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                                                                     ContentValues cv = new ContentValues();
                                                                     cv.put(HiFiSchema.KEY_ROW_ID, packetData);
                                                                     cv.put(HiFiSchema.PRODUCT_NAME, nameProduct);
                                                                     cv.put(HiFiSchema.PRODUCT_ID, id);
                                                                     cv.put(HiFiSchema.PRODUCT_CODE, code);
                                                                     cv.put(HiFiSchema.ACCEPT, 1);
                                                                     cv.put(HiFiSchema.REQUEST_FOR, requestFor);
                                                                     cv.put(HiFiSchema.PRODUCT_PRICE, price);
                                                                     cv.put(HiFiSchema.PRODUCT_QUANTITY, qty);
                                                                     //   cv.put(HiFiSchema.PRODUCT_MOQ, moq);       H
                                                                     cv.put(HiFiSchema.KEY_STATUS, 1);
                                                                     cv.put(HiFiSchema.KEY_CREATED, receiveTimeString);
                                                                     Uri uri = getContentResolver().insert(ChatProvider.HiFi_URI, cv);
                                                                     // getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                     getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                                                                     if (cd.isConnectingToInternet())
                                                                     {//ISTIAQUE
                                                                         new HiFiStatus(ChatActivity.this, packetData, 1).execute();
                                                                     }

                                                                     else
                                                                     {
                                                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                                                                     }
                                                                 } else if (subject.equalsIgnoreCase("query")) {

                                                                     ContentValues values = new ContentValues();
                                                                     values.put(ChatSchema.KEY_SENDER, account);
                                                                     values.put(ChatSchema.KEY_RECEIVER, userLogin);
                                                                     values.put(ChatSchema.KEY_MSG, content.optString("query"));
                                                                     values.put(ChatSchema.KEY_DIS, 2);
                                                                     values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                     values.put(ChatSchema.KEY_SUB, "query");
                                                                     values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                     values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                     values.put(ChatSchema.IsMe, 22);
                                                                     values.put("status", 1);
                                                                     getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                                                     ContentValues cv = new ContentValues();
                                                                     cv.put(QuerySchema.KEY_ROW_ID, packetData);
                                                                     cv.put(QuerySchema.QUERY_TEXT, content.optString("query"));
                                                                     cv.put(QuerySchema.KEY_CREATED, receiveTimeString);
                                                                     cv.put(QuerySchema.QUERY_URL, "" + content.optString("url"));
                                                                     cv.put(QuerySchema.KEY_STATUS, 1);
                                                                     getContentResolver().insert(ChatProvider.QUERY_URI, cv);
                                                                     //  getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                     getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);
                                                                 } else if (subject.equalsIgnoreCase("img")) {

                                                                     ContentValues values = new ContentValues();
                                                                     values.put(ChatSchema.KEY_SENDER, account);
                                                                     values.put(ChatSchema.KEY_RECEIVER, userLogin);
                                                                     values.put(ChatSchema.KEY_MSG, content.optString("url"));
                                                                     values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                     values.put(ChatSchema.IsMe, 12);
                                                                     values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                     values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                                                     values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                     values.put(ChatSchema.KEY_SUB, subject);
                                                                     values.put(ChatSchema.KEY_DIS, 2);
                                                                     values.put("status", 1);
                                                                     values.put(ChatSchema.IsDownload, 0);
                                                                     values.put(ChatSchema.IsError, 0);
                                                                     //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                                                                     getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                                                     //   getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                     // getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);
                                                                 }
                                                             }
                                                         }
                                                     }
                                                 } else {
                                                     JSONObject content = new JSONObject(jsonObject.optString("body"));
                                                     String subject = content.optString("subject");
                                                     String packetData = content.optString("packet_id");
                                                     if (packetData != null) {
                                                         if (!Exists(packetData)) {
                                                             if (subject.equals("text")) {
                                                                 ContentValues values = new ContentValues();
                                                                 values.put(ChatSchema.KEY_SENDER, userLogin);
                                                                 values.put(ChatSchema.KEY_RECEIVER, account);
                                                                 values.put(ChatSchema.KEY_MSG, content.optString("text"));
                                                                 values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                 values.put(ChatSchema.IsMe, 2);
                                                                 values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                 values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                                                 values.put(ChatSchema.KEY_SUB, "text");
                                                                 values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                 values.put(ChatSchema.KEY_DIS, 2);
                                                                 values.put("status", 1);
                                                                 getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                                                             } else if (subject.equals("product")) {
                                                                 String nameProduct = null;
                                                                 String code = null;
                                                                 String mrp = null;
                                                                 String price = null;
                                                                 String url = null;
                                                                 String moq = null;

                                                                 nameProduct = content.optString("name");
                                                                 code = content.optString("code");
                                                                 mrp = content.optString("mrp");
                                                                 price = content.optString("price");
                                                                 url = content.optString("url");
                                                                 moq = content.optString("moq");


                                                                 ContentValues values = new ContentValues();
                                                                 values.put(ChatSchema.KEY_SENDER, userLogin);
                                                                 values.put(ChatSchema.KEY_RECEIVER, account);

                                                                 values.put(ChatSchema.KEY_MSG, content.optString("name"));
                                                                 values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                 values.put(ChatSchema.IsMe, 3);
                                                                 values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                                                 values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                 values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                 values.put(ChatSchema.KEY_SUB, subject);
                                                                 values.put(ChatSchema.KEY_DIS, 2);
                                                                 values.put("status", 1);
                                                                 getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                                                 ContentValues cv = new ContentValues();
                                                                 cv.put(ProSchema.KEY_ROW_ID, packetData);
                                                                 cv.put(ProSchema.PRODUCT_NAME, nameProduct);
                                                                 cv.put(ProSchema.PRODUCT_CODE, code);
                                                                 cv.put(ProSchema.PRODUCT_MRP, mrp);
                                                                 cv.put(ProSchema.PRODUCT_PRICE, price);
                                                                 cv.put(ProSchema.PRODUCT_URL, url);
                                                                 cv.put(ProSchema.PRODUCT_MOQ, moq);
                                                                 cv.put(ProSchema.KEY_STATUS, 1);
                                                                 cv.put(ProSchema.KEY_CREATED, receiveTimeString);
                                                                 getContentResolver().insert(ChatProvider.URI_PRODUCT, cv);
                                                                 // getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                 getContentResolver().notifyChange(ChatProvider.URI_PRODUCT, null, false);
                                                             } else if (subject.equalsIgnoreCase("HiFi")) {
                                                                 //    String nameProduct = content.optString("name");
                                                                 String code = content.optString("code");
                                                                 String id = content.optString("product_id");
                                                                 String price = content.optString("price");
                                                                 String qty = content.optString("qty");
                                                                 String requestFor = content.optString("request_for");

                                                                 Log.i("account***-", account);
                                                                 Cursor HifiNameCursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{account}, null);
                                                                 int iCmp = HifiNameCursor.getColumnIndexOrThrow(UserSchema.KEY_COMPANY_NAME);
                                                                 //  int l = c.getColumnIndexOrThrow("created_at");
                                                                 String name = "";
                                                                 if (HifiNameCursor != null && HifiNameCursor.moveToFirst()) {
                                                                     name = HifiNameCursor.getString(iCmp);
                                                                 }
                                                                 HifiNameCursor.close();

                                                                 ContentValues values = new ContentValues();
                                                                 values.put(ChatSchema.KEY_SENDER, userLogin);
                                                                 values.put(ChatSchema.KEY_RECEIVER, account);
                                                                 values.put(ChatSchema.KEY_MSG, content.optString("HiFi"));
                                                                 values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                 values.put(ChatSchema.IsMe, 4);
                                                                 values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                 values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                 values.put(ChatSchema.KEY_SUB, subject);
                                                                 values.put(ChatSchema.KEY_DIS, 2);
                                                                 values.put("status", 1);
                                                                 //    Log.d("Property", "" + message.getProperty("favoriteColor"));
                                                                 getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                                                                 ContentValues cv = new ContentValues();
                                                                 cv.put(HiFiSchema.KEY_ROW_ID, packetData);
                                                                 cv.put(HiFiSchema.PRODUCT_NAME, name);
                                                                 cv.put(HiFiSchema.PRODUCT_ID, id);
                                                                 cv.put(HiFiSchema.PRODUCT_CODE, code);
//                                                             Date past = format.parse(receiveTimeString);
//                                                             Date current = formatNew.parse(format.format(Calendar.getInstance().getTime()));
//                                                             if (dateFormat(past, current)) {
//                                                                 cv.put(HiFiSchema.ACCEPT, 0);
//                                                             } else {
//                                                                 cv.put(HiFiSchema.ACCEPT, 8);
//                                                             }
                                                                 cv.put(HiFiSchema.ACCEPT, 0);
                                                                 cv.put(HiFiSchema.REQUEST_FOR, requestFor);
                                                                 cv.put(HiFiSchema.PRODUCT_PRICE, price);
                                                                 cv.put(HiFiSchema.PRODUCT_QUANTITY, qty);
                                                                 //   cv.put(HiFiSchema.PRODUCT_MOQ, moq);       H
                                                                 cv.put(HiFiSchema.KEY_STATUS, 1);
                                                                 cv.put(HiFiSchema.KEY_CREATED, receiveTimeString);
                                                                 Uri uri = getContentResolver().insert(ChatProvider.HiFi_URI, cv);
                                                                 // getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                 getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                                                                 if (cd.isConnectingToInternet())
                                                                 {//ISTIAQUE
                                                                     new HiFiStatus(ChatActivity.this, packetData, 0).execute();
                                                                 }

                                                                 else
                                                                 {
                                                                     new AlertDialogManager().showAlertDialog(ChatActivity.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                                                                 }
                                                             } else if (subject.equalsIgnoreCase("query")) {

                                                                 ContentValues values = new ContentValues();
                                                                 values.put(ChatSchema.KEY_SENDER, userLogin);
                                                                 values.put(ChatSchema.KEY_RECEIVER, account);
                                                                 values.put(ChatSchema.KEY_MSG, content.optString("query"));
                                                                 values.put(ChatSchema.KEY_DIS, 2);
                                                                 values.put(ChatSchema.KEY_CHAT_ID, chatId);
                                                                 values.put(ChatSchema.KEY_SUB, "query");
                                                                 values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                 values.put(ChatSchema.KEY_CREATED, receiveTimeString);
                                                                 values.put(ChatSchema.IsMe, 21);
                                                                 values.put("status", 1);
                                                                 getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                                                 ContentValues cv = new ContentValues();
                                                                 cv.put(QuerySchema.KEY_ROW_ID, packetData);
                                                                 cv.put(QuerySchema.QUERY_TEXT, content.optString("query"));
                                                                 cv.put(QuerySchema.KEY_CREATED, receiveTimeString);
                                                                 cv.put(QuerySchema.QUERY_URL, "" + content.optString("url"));
                                                                 cv.put(QuerySchema.KEY_STATUS, 1);
                                                                 getContentResolver().insert(ChatProvider.QUERY_URI, cv);
                                                                 //   getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                                                                 getContentResolver().notifyChange(ChatProvider.QUERY_URI, null, false);
                                                             } else if (subject.equalsIgnoreCase("img")) {

                                                                 ContentValues values = new ContentValues();
                                                                 values.put(ChatSchema.KEY_SENDER, account);
                                                                 values.put(ChatSchema.KEY_RECEIVER, account);
                                                                 values.put(ChatSchema.KEY_MSG, content.optString("url"));
                                                                 values.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                                                                 values.put(ChatSchema.IsMe, 11);
                                                                 values.put(ChatSchema.BROADCAST, content.optInt("broadcast"));
                                                                 values.put(ChatSchema.KEY_MSG_ID, packetData);
                                                                 values.put(ChatSchema.KEY_SUB, subject);
                                                                 values.put(ChatSchema.KEY_DIS, 2);
                                                                 values.put("status", 1);
                                                                 values.put(ChatSchema.IsDownload, 0);
                                                                 values.put(ChatSchema.IsError, 0);
                                                                 getContentResolver().insert(ChatProvider.CONTENT_URI, values);
                                                             }
                                                         }
                                                     }
                                                 }
                                             }
                                             getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null);
                                             chatAdapter.notifyDataSetChanged();
                                             if (messageCount != 0) {
                                                 final Cursor c = getContentResolver().query(ChatProvider.CONTENT_URI, new String[]{ChatSchema.KEY_ROWID, ChatSchema.KEY_CREATED}, "(" + ChatSchema.KEY_RECEIVER + "=?" + " AND " + ChatSchema.KEY_SENDER + "=? ) OR (" + ChatSchema.KEY_RECEIVER + "=? AND " + ChatSchema.KEY_SENDER + "=? )", new String[]{account, userLogin, userLogin, account}, null);
                                                 if (c != null) {

                                                     int iDate = c.getColumnIndexOrThrow(ChatSchema.KEY_CREATED);
                                                     c.moveToLast();

                                                     if (c.getCount() > 0) {

                                                         hasMessage(new MessageRequest(userLogin, "chat_exist", account, "", c.getString(iDate)));
                                                     } else {
                                                         if (progress.isShowing()) {
                                                             progress.dismiss();
                                                         }
                                                     }


                                                 } else {

                                                     if (progress.isShowing()) {
                                                         progress.dismiss();
                                                     }
                                                 }
                                             }
                                         } else {
                                             Log.d("Message Count", "" + messageCount);
                                             if (messageCount == 0) {
                                                 if (progress.isShowing()) {
                                                     progress.dismiss();
                                                 }
                                                 footerView.setVisibility(View.GONE);
                                             } else {
                                                 Log.d("Message Count", "come in else");
                                                 Date end = format.parse(endDate);
                                                 if (checkDates(format.parse(registrationDate), end) == 2) {
                                                     Log.d("Message Count", "come in request");
                                                     Calendar cal = Calendar.getInstance();
                                                     cal.setTime(end);
                                                     cal.add(Calendar.DAY_OF_MONTH, -5);
                                                     Date time = cal.getTime();
                                                     String start = format.format(time);
                                                     MessageRequest messageRequest = new MessageRequest(userLogin, "chat", account, start, endDate);
                                                     getMessage(messageRequest);
                                                 } else {
                                                     Log.d("Message Count", "come in false");
                                                     if (progress.isShowing()) {
                                                         progress.dismiss();
                                                     }
                                                     footerView.setVisibility(View.GONE);
                                                 }

                                             }

                                         }


                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     } catch (java.text.ParseException e) {
                                         e.printStackTrace();
                                     }
                                 } else {
                                     int statusCode = response.code();
                                     //}
                                     if (statusCode == 401) {

                                         final SessionManager sessionManager = new SessionManager(ChatActivity.this);
                                         Handler mainHandler = new Handler(Looper.getMainLooper());

                                         Runnable myRunnable = new Runnable() {
                                             @Override
                                             public void run() {
                                                 sessionManager.logoutUser();
                                             } // This is your code
                                         };
                                         mainHandler.post(myRunnable);
                                     }
                                     // if(error2.equalsIgnoreCase(""))
                                     String error2 = getString(R.string.server_error);
                                     if (statusCode == 404) {
                                         //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                     } else if (statusCode == 500) {
                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                         //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                     } else {
                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                         // Toast.makeText(AddressList.this, , Toast.LENGTH_LONG).show();
                                     }
                                 }


                             }
                         }

                         @Override
                         public void onFailure(Throwable t) {
                             if (ChatActivity.this != null && !ChatActivity.this.isFinishing()) {
                                 progress.dismiss();
                             }
                             Log.d("Here", t.toString());


                         }
                     }

        );

    }

    private boolean hasMessage(MessageRequest request) {
        Gson gson = new Gson();
        Log.d("HAS REQUEST", "" + gson.toJson(request));
        if (!progress.isShowing()) {
            progress.show();
        }

        has = false;
        RestClientOpenfire.GitApiInterface service = RestClientOpenfire.getClient();
        Call<JsonElement> call = service.getMessage(request);
        call.enqueue(new Callback<JsonElement>() {
                         @Override
                         public void onResponse(Response response) {
                             if (ChatActivity.this != null && !ChatActivity.this.isFinishing()) {
                                 Log.d("response", "" + response.raw() + " " + response.code());
                                 progress.dismiss();
                                 if (response.isSuccess()) {
                                     JsonElement element = (JsonElement) response.body();
                                     Log.d("JSON", " " + element.getAsJsonObject().toString());
                                     try {
                                         JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                                         int msgCount = json.optInt("message_count");
                                         Log.d("MSG COUNT", " " + msgCount);
                                         if (msgCount == 0) {
                                             has = false;
                                             rId = -1;
                                             section = 0;
                                         }
                                         messageCount = msgCount;
                                         if (msgCount > 0) {

                                             has = true;
                                             runOnUiThread(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     footerView.setVisibility(View.VISIBLE);
                                                 }
                                             });
                                         } else {
                                             has = false;

                                         }


                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 } else {
                                     int statusCode = response.code();
                                     //}
                                     if (statusCode == 401) {

                                         final SessionManager sessionManager = new SessionManager(ChatActivity.this);
                                         Handler mainHandler = new Handler(Looper.getMainLooper());

                                         Runnable myRunnable = new Runnable() {
                                             @Override
                                             public void run() {
                                                 sessionManager.logoutUser();
                                             } // This is your code
                                         };
                                         mainHandler.post(myRunnable);
                                     }
                                     // if(error2.equalsIgnoreCase(""))
                                     String error2 = getString(R.string.server_error);
                                     if (statusCode == 404) {
                                         //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                     } else if (statusCode == 500) {
                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                         //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                     } else {
                                         new AlertDialogManager().showAlertDialog(ChatActivity.this, getString(R.string.error), error2);
                                         // Toast.makeText(AddressList.this, , Toast.LENGTH_LONG).show();
                                     }
                                 }

                             }
                         }

                         @Override
                         public void onFailure(Throwable t) {
                             if (ChatActivity.this != null && !ChatActivity.this.isFinishing()) {
                                 progress.dismiss();
                             }
                             Log.d("Here", t.toString());


                         }
                     }

        );
        return has;
    }
}