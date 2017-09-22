package wydr.sellers.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.MessageEventManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wydr.sellers.R;
import wydr.sellers.acc.BroadcastMessage;
import wydr.sellers.acc.BroadcastUser;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.acc.RandomTest;
import wydr.sellers.acc.ScalingUtilities;
import wydr.sellers.adapter.BroadcastAdapter;
import wydr.sellers.emojicon.EmojiconEditText;
import wydr.sellers.emojicon.EmojiconGridView;
import wydr.sellers.emojicon.EmojiconsPopup;
import wydr.sellers.emojicon.emoji.Emojicon;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.openfire.JSONMessage;
import wydr.sellers.openfire.SLog;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 10/12/15.
 */
public class Broadcast extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 12;
    private static final String IMAGE_DIRECTORY_NAME = "WYDR";
    final int DESIREDWIDTH = 800;
    final int DESIREDHEIGHT = 800;
    private final int SELECT_PICTURE = 11;
    EmojiconEditText msg;
    ListView listView;
    ImageView send, smileyIcon;
    Helper helper = new Helper();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Toolbar mToolbar;
    XMPPConnection mConnection;
    ChatManager chatManager;
    MessageListener msgListener;
    String tag = "Broadcast Activity";
    Chat chat;
    StringBuilder builder, builderName;
    String name, id;
    BroadcastAdapter adapter;
    MessageEventManager messageEventManager;
    String userLogin;
    ConnectionDetector cd;
    JSONMessage jsonMessage;
    TextView header;
    private Uri fileUri;

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

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
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

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        mediaStorageDir.mkdir();
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }

        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
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

    private static File getMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppUtil.IMAGE_DIRECTORY_SEND);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast);
        name = getIntent().getStringExtra("user");
        id = getIntent().getStringExtra("id");
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        header = (TextView) findViewById(R.id.tooltext);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Broadcast.this, BroadcastDetail.class).putExtra("name", name).putExtra("id", id));
            }
        });

        userLogin = helper.getDefaults("login_id", getApplicationContext()) + "@" + AppUtil.SERVER_NAME;

        Cursor cursor = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
        builder = new StringBuilder();
        builderName = new StringBuilder();
        int iId = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
        int iName = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_NAME);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            if (cursor.isLast()) {
                builder.append(cursor.getString(iId) + "/Smack");
                builderName.append(cursor.getString(iName));
            } else {
                builder.append(cursor.getString(iId) + "/Smack,");
                builderName.append(cursor.getString(iName) + ", ");
            }


        }

        iniStuff();
        smileyStuff();
        send.setOnClickListener(this);
        header.setText((name + " " + builderName.toString()));


    }

    private void iniStuff() {
        cd = new ConnectionDetector(getApplicationContext());
        jsonMessage = new JSONMessage();
        send = (ImageView) findViewById(R.id.buttonSendBroadcastMsg);
        msg = (EmojiconEditText) findViewById(R.id.editTextMessageBroadcast);
        listView = (ListView) findViewById(R.id.listViewBroadcast);
        smileyIcon = (ImageView) findViewById(R.id.buttonSmileyBroadcast);
        getSupportLoaderManager().initLoader(6, null, Broadcast.this);
        adapter = new BroadcastAdapter(this, null, builder.toString());
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.broadcast_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //   NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menuAddProduct:
                if (cd.isConnectingToInternet()) {
                    Intent i = new Intent(this, AddProductInBroadcast.class);
                    startActivityForResult(i, 1001);
                }
                break;
            case R.id.menuAddAttach:
                ContextThemeWrapper themedContext;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    themedContext = new ContextThemeWrapper(Broadcast.this, R.style.MyTheme);
                } else {
                    themedContext = new ContextThemeWrapper(Broadcast.this, R.style.MyTheme);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
                builder.setTitle(getString(R.string.get_image_from))
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

        }
        return super.onOptionsItemSelected(item);
    }

    private void smileyStuff() {
        final View rootView = findViewById(R.id.broadCastActivity);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(smileyIcon, R.drawable.smiley);
            }
        });
        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });


        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

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
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Calendar c = Calendar.getInstance();

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                if (mConnection != null) {
                    if (!mConnection.isConnected()) {
                        mConnection = XmppConnection.getInstance().getConnection();
                    }
                    //chattingSetup();
                    String messageId;
                    Log.d("Update Row", "1");
                    Bundle result = data.getBundleExtra("result");
                    String code = result.getString("code");
                    String name1 = result.getString("name");
                    String price = result.getString("price");
                    String mrp = result.getString("mrp");
                    String url = result.getString("url");
                    String moq = result.getString("moq");
                    Message message = jsonMessage.sendProductBroadcast(name1, code, price, mrp, url, moq);
                    messageId = message.getPacketID();
                    ContentValues cv = new ContentValues();
                    cv.put(ProSchema.KEY_ROW_ID, messageId);
                    cv.put(ProSchema.PRODUCT_NAME, name1);
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
                    Log.d("Update Row", "");
                    //broadcast
                    ContentValues values = new ContentValues();
                    values.put(BroadcastMessage.KEY_MSG, name1);
                    values.put(BroadcastMessage.KEY_MSG_ID, messageId);
                    values.put(BroadcastMessage.KEY_BROAD_CAST_ID, id);
                    values.put(BroadcastMessage.KEY_STATUS, 1);
                    values.put(BroadcastMessage.KEY_TYPE, "product");
                    values.put(BroadcastMessage.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    getContentResolver().insert(ChatProvider.BROADCAST_URI, values);
                    getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                    contentValues.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    int Count = getContentResolver().update(ChatProvider.CHAT_USER_URI, contentValues, ChatUserSchema.KEY_ROWID + "=?", new String[]{id});
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    Log.d("Update Row", "" + Count);

                    Cursor cursorUser = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
                    // builder = new StringBuilder();
                    int iId = cursorUser.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
                    for (cursorUser.moveToFirst(); !cursorUser.isAfterLast(); cursorUser.moveToNext()) {
                        chattingSetup(cursorUser.getString(iId), message);
                        ContentValues value = new ContentValues();
                        value.put(ChatSchema.KEY_SENDER, userLogin);
                        Log.d("Update Row", "3");
                        value.put(ChatSchema.KEY_RECEIVER, cursorUser.getString(iId));
                        value.put(ChatSchema.KEY_MSG, name1);
                        value.put(ChatSchema.KEY_DIS, 1);
                        value.put(ChatSchema.KEY_SUB, "product");
                        value.put(ChatSchema.KEY_MSG_ID, messageId);
                        value.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                        value.put(ChatSchema.IsMe, 3);
                        value.put(ChatSchema.STATUS, 1);
                        Log.d("Data=>", cv.toString());
                        Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, value);
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    }


                } else {
                    String messageId;
                    Log.d("Update Row", "1");
                    Bundle result = data.getBundleExtra("result");
                    String code = result.getString("code");
                    String name1 = result.getString("name");
                    String price = result.getString("price");
                    String mrp = result.getString("mrp");
                    String url = result.getString("url");
                    String moq = result.getString("moq");
                    RandomTest test = new RandomTest();
                    Message message = jsonMessage.sendProductBroadcast(name1, code, price, mrp, url, moq);
                    messageId = test.randomString(7);
                    ContentValues cv = new ContentValues();
                    cv.put(ProSchema.KEY_ROW_ID, messageId);
                    cv.put(ProSchema.PRODUCT_NAME, name1);
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
                    Log.d("Update Row", "");
                    //broadcast
                    ContentValues values = new ContentValues();
                    values.put(BroadcastMessage.KEY_MSG, name1);
                    values.put(BroadcastMessage.KEY_MSG_ID, messageId);
                    values.put(BroadcastMessage.KEY_BROAD_CAST_ID, id);
                    values.put(BroadcastMessage.KEY_STATUS, 1);
                    values.put(BroadcastMessage.KEY_TYPE, "product");
                    values.put(BroadcastMessage.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    getContentResolver().insert(ChatProvider.BROADCAST_URI, values);
                    getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                    contentValues.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    int Count = getContentResolver().update(ChatProvider.CHAT_USER_URI, contentValues, ChatUserSchema.KEY_ROWID + "=?", new String[]{id});
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    Log.d("Update Row", "" + Count);

                    Cursor cursorUser = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
                    // builder = new StringBuilder();
                    int iId = cursorUser.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
                    for (cursorUser.moveToFirst(); !cursorUser.isAfterLast(); cursorUser.moveToNext()) {
                        chattingSetup(cursorUser.getString(iId), message);
                        ContentValues value = new ContentValues();
                        value.put(ChatSchema.KEY_SENDER, userLogin);
                        Log.d("Update Row", "3");
                        value.put(ChatSchema.KEY_RECEIVER, cursorUser.getString(iId));
                        value.put(ChatSchema.KEY_MSG, name1);
                        value.put(ChatSchema.KEY_DIS, 1);
                        value.put(ChatSchema.KEY_SUB, "product");
                        value.put(ChatSchema.KEY_MSG_ID, messageId);
                        value.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                        value.put(ChatSchema.IsMe, 3);
                        value.put(ChatSchema.STATUS, -1);
                        Log.d("Data=>", cv.toString());
                        Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, value);
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    }


                }
                // }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
            // }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)

        {
            if (resultCode == RESULT_OK) {

                try {
                    String yourFile = fileUri.getPath();

                    //broadcast
                    RandomTest randomTest = new RandomTest();
                    String ui = randomTest.randomString(7);
                    Log.d("IMAGE ID", ui);
                    ContentValues values = new ContentValues();
                    values.put(BroadcastMessage.KEY_MSG, decodeFile(yourFile));
                    values.put(BroadcastMessage.KEY_MSG_ID, ui);
                    values.put(BroadcastMessage.KEY_BROAD_CAST_ID, id);
                    values.put(BroadcastMessage.KEY_STATUS, 0);
                    values.put(BroadcastMessage.KEY_DOWNLOAD, 0);
                    values.put(BroadcastMessage.KEY_ERROR, 0);
                    values.put(BroadcastMessage.KEY_TYPE, "img");

                    values.put(BroadcastMessage.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    getContentResolver().insert(ChatProvider.BROADCAST_URI, values);
                    getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ChatUserSchema.KEY_LAST_MSG, ui);
                    contentValues.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    int Count = getContentResolver().update(ChatProvider.CHAT_USER_URI, contentValues, ChatUserSchema.KEY_ROWID + "=?", new String[]{id});
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    Log.d("Update Row", "" + Count);

                    Cursor cursorUser = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
                    //  builder = new StringBuilder();
                    int iId = cursorUser.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
                    for (cursorUser.moveToFirst(); !cursorUser.isAfterLast(); cursorUser.moveToNext()) {
                        ContentValues value = new ContentValues();
                        value.put(ChatSchema.KEY_SENDER, userLogin);
                        value.put(ChatSchema.KEY_RECEIVER, cursorUser.getString(iId));
                        value.put(ChatSchema.KEY_MSG, "" + decodeFile(yourFile));
                        value.put(ChatSchema.KEY_DIS, 1);
                        value.put(ChatSchema.KEY_SUB, "img");
                        value.put(ChatSchema.KEY_CHAT_ID, "");
                        value.put(ChatSchema.KEY_MSG_ID, ui);
                        value.put(ChatSchema.IsDownload, 1);
                        value.put(ChatSchema.IsError, 0);
                        value.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                        value.put(ChatSchema.IsMe, 11);
                        value.put("status", 1);
                        getContentResolver().insert(ChatProvider.CONTENT_URI, value);
                        getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);

                        // Log.d("ggg", uri.toString());


                    }


                    //sendAttachFile(account, decodeFile(yourFile));


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error", e.getMessage());
                    // TODO: handle exception
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.user_cancelled_image), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.fail_load_image),
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == SELECT_PICTURE)

        {
            if (resultCode == RESULT_OK) {

                String selectedImagePath = null;
                Uri uri = data.getData();
                selectedImagePath = getPath(Broadcast.this, uri);
                String finalPath = decodeFile(selectedImagePath);
                Log.d("Final", finalPath);

                RandomTest randomTest = new RandomTest();
                String ui = randomTest.randomString(7);
                Log.d("IMAGE ID", ui);
                ContentValues values = new ContentValues();
                values.put(BroadcastMessage.KEY_MSG, finalPath);
                values.put(BroadcastMessage.KEY_MSG_ID, ui);
                values.put(BroadcastMessage.KEY_BROAD_CAST_ID, id);
                values.put(BroadcastMessage.KEY_STATUS, 0);
                values.put(BroadcastMessage.KEY_DOWNLOAD, 0);
                values.put(BroadcastMessage.KEY_ERROR, 0);
                values.put(BroadcastMessage.KEY_TYPE, "img");

                values.put(BroadcastMessage.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                getContentResolver().insert(ChatProvider.BROADCAST_URI, values);
                getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ChatUserSchema.KEY_LAST_MSG, ui);
                contentValues.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                int Count = getContentResolver().update(ChatProvider.CHAT_USER_URI, contentValues, ChatUserSchema.KEY_ROWID + "=?", new String[]{id});
                getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                Log.d("Update Row", "" + Count);

                Cursor cursorUser = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
                //     builder = new StringBuilder();
                int iId = cursorUser.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
                for (cursorUser.moveToFirst(); !cursorUser.isAfterLast(); cursorUser.moveToNext()) {
                    ContentValues value = new ContentValues();
                    value.put(ChatSchema.KEY_SENDER, userLogin);
                    value.put(ChatSchema.KEY_RECEIVER, cursorUser.getString(iId));
                    value.put(ChatSchema.KEY_MSG, "" + finalPath);
                    value.put(ChatSchema.KEY_DIS, 1);
                    value.put(ChatSchema.KEY_SUB, "img");
                    value.put(ChatSchema.KEY_CHAT_ID, "");
                    value.put(ChatSchema.KEY_MSG_ID, ui);
                    value.put(ChatSchema.IsDownload, 1);
                    value.put(ChatSchema.IsError, 0);
                    value.put(ChatSchema.KEY_CREATED, "" + format.format(c.getTime()));
                    value.put(ChatSchema.IsMe, 11);
                    value.put("status", 1);
                    getContentResolver().insert(ChatProvider.CONTENT_URI, value);
                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);

                    // Log.d("ggg", uri.toString());


                }


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.user_cancelled_image), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.fail_load_image), Toast.LENGTH_SHORT)
                        .show();

            }
        } else

        {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show();
        }

        getContentResolver()

                .

                        notifyChange(ChatProvider.CONTENT_URI, null, false);

    }

    private void chattingSetup() {
        mConnection = XmppConnection.getInstance().getConnection();
        if (mConnection != null) {
            messageEventManager = new MessageEventManager(mConnection);
            chatManager = mConnection.getChatManager();
            msgListener = new MessageListener() {
                @Override
                public void processMessage(Chat arg0, final Message message) {
                    SLog.i(tag, message.toXML());

                }
            };
            chat = chatManager.createChat(builder.toString(), msgListener);

            //n
        }
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    @Override
    public void onClick(View v) {

        String content = msg.getText().toString().trim();
        String packetId;
        if (TextUtils.isEmpty(content)) {

            return;
        }
        if (cd.isConnectingToInternet()) {
            try {
//                Message message = new Message();
//                message.setBody(content);
//                message.setSubject("text");
//                message.setProperty("broadcast", 1);
//                MessageEventManager.addNotificationsRequests(message,
//                        true, true, true, true);
//                //  chat.sendMessage(message);
                Message message = jsonMessage.sendTextBroadcast(content);
                packetId = message.getPacketID();
                ContentValues values = new ContentValues();
                values.put(BroadcastMessage.KEY_MSG, content);
                values.put(BroadcastMessage.KEY_MSG_ID, packetId);
                values.put(BroadcastMessage.KEY_BROAD_CAST_ID, id);
                values.put(BroadcastMessage.KEY_STATUS, 1);
                values.put(BroadcastMessage.KEY_TYPE, "text");
                values.put(BroadcastMessage.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                getContentResolver().insert(ChatProvider.BROADCAST_URI, values);
                getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ChatUserSchema.KEY_LAST_MSG, packetId);
                contentValues.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                int Count = getContentResolver().update(ChatProvider.CHAT_USER_URI, contentValues, ChatUserSchema.KEY_ROWID + "=?", new String[]{id});
                getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                Log.d("Update Row", "" + Count);
                msg.setText("");
                Cursor cursorUser = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
                // builder = new StringBuilder();
                int iId = cursorUser.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
                for (cursorUser.moveToFirst(); !cursorUser.isAfterLast(); cursorUser.moveToNext()) {
                    chattingSetup(cursorUser.getString(iId), message);

                    getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                    Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{cursorUser.getString(iId)}, null);
                    String chatId = "";
                    int iRowId = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_ROWID);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        chatId = cursor.getString(iRowId);
                        ContentValues cv = new ContentValues();
                        cv.put(ChatUserSchema.KEY_LAST_MSG, packetId);
                        cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                        cv.put(ChatUserSchema.KEY_TYPE, "chat");
                        cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                        getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{cursorUser.getString(iId)});
                        getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                    }


                    ContentValues values1 = new ContentValues();
                    values1.put(ChatSchema.KEY_SENDER, userLogin);
                    values1.put(ChatSchema.KEY_RECEIVER, cursorUser.getString(iId));
                    values1.put(ChatSchema.KEY_DIS, 1);
                    values1.put(ChatSchema.KEY_CHAT_ID, "" + chatId);
                    values1.put(ChatSchema.BROADCAST, 1);
                    values1.put(ChatSchema.KEY_SUB, "text");
                    values1.put(ChatSchema.KEY_MSG_ID, packetId);
                    values1.put(ChatSchema.KEY_MSG, content);
                    values1.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                    values1.put(ChatSchema.IsMe, 2);
                    values1.put("status", 1);
                    // Log.e("hid", content);
                    Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, values1);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            RandomTest test = new RandomTest();
            String idGenerate = test.randomString(7);
            ContentValues values = new ContentValues();
            values.put(BroadcastMessage.KEY_MSG, content);
            values.put(BroadcastMessage.KEY_MSG_ID, idGenerate);
            values.put(BroadcastMessage.KEY_BROAD_CAST_ID, id);
            values.put(BroadcastMessage.KEY_STATUS, 0);
            values.put(BroadcastMessage.KEY_TYPE, "text");
            values.put(BroadcastMessage.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
            getContentResolver().insert(ChatProvider.BROADCAST_URI, values);
            getContentResolver().notifyChange(ChatProvider.BROADCAST_URI, null, false);
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatUserSchema.KEY_LAST_MSG, idGenerate);
            contentValues.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
            int Count = getContentResolver().update(ChatProvider.CHAT_USER_URI, contentValues, ChatUserSchema.KEY_ROWID + "=?", new String[]{id});
            getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
            Log.d("Update Row", "" + Count);
            msg.setText("");
            Cursor cursorUser = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
            // builder = new StringBuilder();
            int iId = cursorUser.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
            for (cursorUser.moveToFirst(); !cursorUser.isAfterLast(); cursorUser.moveToNext()) {
                // do what you need with the cursor here
                Cursor cursor = getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{cursorUser.getString(iId)}, null);
                String chatId="";
                int iRowId = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_ROWID);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    chatId = cursor.getString(iRowId);
                    ContentValues cv = new ContentValues();
                    cv.put(ChatUserSchema.KEY_LAST_MSG, idGenerate);
                    cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                    cv.put(ChatUserSchema.KEY_TYPE, "chat");
                    cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                    getContentResolver().update(ChatProvider.CHAT_USER_URI, cv, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{cursorUser.getString(iId)});
                    getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                }
                ContentValues values1 = new ContentValues();
                values1.put(ChatSchema.KEY_SENDER, userLogin);
                values1.put(ChatSchema.KEY_RECEIVER, cursorUser.getString(iId));
                values1.put(ChatSchema.KEY_DIS, -1);
                values1.put(ChatSchema.KEY_CHAT_ID, chatId);
                values1.put(ChatSchema.BROADCAST, 1);
                values1.put(ChatSchema.KEY_SUB, "text");
                values1.put(ChatSchema.KEY_MSG_ID, idGenerate);
                values1.put(ChatSchema.KEY_MSG, content);
                values1.put(ChatSchema.KEY_CREATED, "" + format.format(Calendar.getInstance().getTime()));
                values1.put(ChatSchema.IsMe, 2);
                values1.put("status", 1);
                // Log.e("hid", content);
                Uri uri = getContentResolver().insert(ChatProvider.CONTENT_URI, values1);
                getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);


            }
        }
    }

    public void onZoom(View v) {
        String path = (String) v.getTag();
        if (path != null) {
            String[] imageUrls = new String[]{path};
            Intent intent = new Intent(this, ImagePagerActivity.class);
            intent.putExtra("url", imageUrls);
            //    intent.putExtra("pos", position);
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int ids, Bundle args) {
        return new CursorLoader(this, ChatProvider.BROADCAST_URI, null, BroadcastMessage.KEY_BROAD_CAST_ID + "=?", new String[]{id}, BroadcastMessage.KEY_CREATED + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

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
            // Log.d("STAGE", "" + 1);
            if (!(unscaledBitmap.getWidth() <= 1000 && unscaledBitmap
                    .getHeight() <= 1000)) {
                // Part 2: Scale image
                // Log.d("STAGE", "" + 2);
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
            // Log.d("STAGE", "" + 4 + strMyImagePath);
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

    private void chattingSetup(String jid, Message message) {
        mConnection = XmppConnection.getInstance().getConnection();
        if (mConnection != null) {
            messageEventManager = new MessageEventManager(mConnection);
            chatManager = mConnection.getChatManager();
            msgListener = new MessageListener() {
                @Override
                public void processMessage(Chat arg0, final Message message) {
                    SLog.i(tag, message.toXML());

                }
            };
            chat = chatManager.createChat(jid, msgListener);
            try {
                chat.sendMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            //n
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName("Broadcast");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
