package wydr.sellers.activities;

import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import wydr.sellers.acc.BookSchema;
import wydr.sellers.acc.BroadcastMessage;
import wydr.sellers.acc.BroadcastUser;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.HiFiSchema;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.acc.QuerySchema;
import wydr.sellers.acc.UserSchema;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.ContactsDb;
import wydr.sellers.slider.MyDatabaseHelper;

/**
 * Created by surya on 19/7/15.
 */
public class ChatProvider extends ContentProvider {

    public static final String CHAT_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.chat";
    public static final String CHAT_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.chat";

    public static final String CHAT_USER_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.chat_user";
    public static final String CHAT_USER_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.chat_user";

    public static final String CART_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.cart";
    public static final String CART_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.cart";


    public static final String QUERY_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.query";
    public static final String QUERY_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.query";

    public static final String BOOK_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.book";
    public static final String BOOK_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.book";


    public static final String NET_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.network";
    public static final String NET_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.network";

    public static final String USER_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.buyer";
    public static final String USER_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.buyer";

    public static final String PRODUCT_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.product";
    public static final String PRODUCT_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.product";

    public static final String HiFi_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.hifi";
    public static final String HiFi_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.hifi";

    public static final String BOOKMARK_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.bookmark";
    public static final String BOOKMARK_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.bookmark";

    public static final String CHAT_SEARCH_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.chatsearch";
    public static final String CHAT_SEARCH_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.chatsearch";

    public static final String BROADCAST_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.broadcast";
    public static final String BROADCAST_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.broadcast";
    public static final String BROADCAST_USER_ID_TYPE = "vnd.android.cursor.item/vnd.com.provider.Photo.broadcast_user";
    public static final String BROADCAST_USER_TYPE = "vnd.android.cursor.dir/vnd.com.provider.Photo.broadcast_user";

    public static final String AUTHORITY = "com.provider.Chat";

    public static final Uri CONTENT_URI = Uri.parse("content://com.provider.Chat/chat");
    public static final Uri URI_USER = Uri.parse("content://com.provider.Chat/user");
    public static final Uri URI_PRODUCT = Uri.parse("content://com.provider.Chat/product");
    public static final Uri NET_URI = Uri.parse("content://com.provider.Chat/network");
    public static final Uri BOOK_URI = Uri.parse("content://com.provider.Chat/book");
    public static final Uri HiFi_URI = Uri.parse("content://com.provider.Chat/hifi");
    public static final Uri BOOKMARK_URI = Uri.parse("content://com.provider.Chat/bookmark");
    public static final Uri SEARCH_URI = Uri.parse("content://com.provider.Chat/chatsearch");
    public static final Uri QUERY_URI = Uri.parse("content://com.provider.Chat/query");
    public static final Uri CART_URI = Uri.parse("content://com.provider.Chat/cart");
    public static final Uri BROADCAST_URI = Uri.parse("content://com.provider.Chat/broadcast");
    public static final Uri BROADCAST_USER_URI = Uri.parse("content://com.provider.Chat/broadcast_user");
    public static final Uri CHAT_USER_URI = Uri.parse("content://com.provider.Chat/chat_user");

    private static final UriMatcher URI_MATCHER;
    private static final int CHAT_ID = 2;
    private static final int CHAT_LIST = 1;
    private static final int CART_ID = 20;
    private static final int CART_LIST = 19;
    private static final int BROADCAST_ID = 22;
    private static final int BROADCAST_LIST = 21;

    private static final int BROADCAST_USER_ID = 23;
    private static final int BROADCAST_USER_LIST = 24;

    private static final int CHAT_USER_ID = 25;
    private static final int CHAT_USER_LIST = 26;

    private static final int USER_ID = 4;
    private static final int USER_LIST = 3;

    private static final int PRODUCT_ID = 6;
    private static final int PRODUCT_LIST = 5;

    private static final int NET_ID = 8;
    private static final int NET_LIST = 7;
    private static final int BOOK_ID = 10;
    private static final int BOOK_LIST = 9;
    private static final int HIFI_ID = 12;
    private static final int HIFI_LIST = 11;

    private static final int BOOKMARK_ID = 14;
    private static final int BOOKMARK_LIST = 13;

    private static final int SEARCH_ID = 16;
    private static final int SEARCH_LIST = 15;
    private static final int QUERY_ID = 18;
    private static final int QUERY_LIST = 17;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY,
                "chat",
                CHAT_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "chat/#",
                CHAT_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "user",
                USER_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "user/#",
                USER_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "product",
                PRODUCT_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "product/#",
                PRODUCT_ID);

        URI_MATCHER.addURI(AUTHORITY,
                "network",
                NET_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "network/#",
                NET_ID);

        URI_MATCHER.addURI(AUTHORITY,
                "book",
                BOOK_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "book/#",
                BOOK_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "hifi/#",
                HIFI_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "hifi",
                HIFI_LIST);

        URI_MATCHER.addURI(AUTHORITY,
                "bookmark/#",
                BOOKMARK_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "bookmark",
                BOOKMARK_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "chatsearch",
                SEARCH_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "chatsearch/#",
                SEARCH_ID);

        URI_MATCHER.addURI(AUTHORITY,
                "query",
                QUERY_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "query/#",
                QUERY_ID);

        URI_MATCHER.addURI(AUTHORITY,
                "cart",
                CART_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "cart/#",
                CART_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "broadcast",
                BROADCAST_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "broadcast/#",
                BROADCAST_ID);
        URI_MATCHER.addURI(AUTHORITY,
                "broadcast_user",
                BROADCAST_USER_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "broadcast_user/#",
                BROADCAST_USER_ID);

        URI_MATCHER.addURI(AUTHORITY,
                "chat_user",
                CHAT_USER_LIST);
        URI_MATCHER.addURI(AUTHORITY,
                "chat_user/#",
                CHAT_USER_ID);


    }

    Helper helper = new Helper();
    private SQLiteDatabase ourDatabase;
    private MyDatabaseHelper ourHelper;

    public ChatProvider() {
        ourHelper = null;
    }

    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(NetSchema.TABLE + "." + NetSchema.USER_NET_ID, NetSchema.TABLE + "." + NetSchema.USER_NET_ID + " AS user_id");
        map.put(NetSchema.TABLE + "." + NetSchema.USER_PHONE, NetSchema.TABLE + "." + NetSchema.USER_PHONE + " AS user_phone");
        map.put(NetSchema.TABLE + "." + NetSchema.USER_DISPLAY, NetSchema.TABLE + "." + NetSchema.USER_DISPLAY + " AS display_name");
        map.put(NetSchema.TABLE + "." + NetSchema.USER_IMAGE, NetSchema.TABLE + "." + NetSchema.USER_IMAGE + " AS profile_pic");
        map.put(NetSchema.TABLE + "." + NetSchema.USER_COMPANY, NetSchema.TABLE + "." + NetSchema.USER_COMPANY + " AS company");

        map.put(ChatSchema.TABLE + "." + ChatSchema.KEY_MSG, ChatSchema.TABLE + "." + ChatSchema.KEY_MSG + " AS message");
        map.put(ChatSchema.TABLE + "." + ChatSchema.KEY_SUB, ChatSchema.TABLE + "." + ChatSchema.KEY_SUB + " AS subject");
        map.put(ChatSchema.TABLE + "." + ChatSchema.KEY_CREATED, ChatSchema.TABLE + "." + ChatSchema.KEY_CREATED + " AS timestamp_message");

        map.put(BroadcastMessage.TABLE + "." + BroadcastMessage.KEY_CREATED, BroadcastMessage.TABLE + "." + BroadcastMessage.KEY_CREATED + " AS timestamp_broadcast");
        map.put(BroadcastMessage.TABLE + "." + BroadcastMessage.KEY_TYPE, BroadcastMessage.TABLE + "." + BroadcastMessage.KEY_TYPE + " AS type");
        map.put(BroadcastMessage.TABLE + "." + BroadcastMessage.KEY_MSG, BroadcastMessage.TABLE + "." + BroadcastMessage.KEY_MSG + " AS broadcast_message");
        map.put(ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_CREATED, ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_CREATED + " AS " + ChatUserSchema.KEY_CREATED);
        map.put(ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_ROWID, ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_ROWID + " AS " + ChatUserSchema.KEY_ROWID);
        map.put(ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_CHAT_USER, ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_CHAT_USER + " AS " + ChatUserSchema.KEY_CHAT_USER);
        map.put(ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_TYPE, ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_TYPE + " AS " + ChatUserSchema.KEY_TYPE);
        map.put(ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_DIRECTION, ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_DIRECTION + " AS " + ChatUserSchema.KEY_DIRECTION);
        map.put(ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_UNREAD, ChatUserSchema.TABLE + "." + ChatUserSchema.KEY_UNREAD + " AS " + ChatUserSchema.KEY_UNREAD);

        return map;
    }

    private Uri getUriForId(long l, Uri uri) {
        if (l > 0L) {
            uri = ContentUris.withAppendedId(uri, l);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        } else {
            throw new SQLException((new StringBuilder()).append("Problem while inserting into uri: ").append(uri).toString());
        }
    }

    @Override
    public boolean onCreate() {
        ourHelper = new MyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {
        SQLiteDatabase db = ourHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String groupby = null;
        String username = helper.getDefaults("login_id", getContext());
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case NET_LIST:
                builder.setTables(NetSchema.TABLE);

                break;
            case NET_ID:
                builder.setTables(NetSchema.TABLE);
                // limit query to one row at most:
                builder.appendWhere(ChatSchema.KEY_ROWID + " = " +
                        uri.getLastPathSegment());
                break;


            case CHAT_LIST:
                builder.setTables(ChatSchema.TABLE);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ChatSchema.SORT_ORDER_DEFAULT;
                }
                break;
            case CHAT_ID:
                builder.setTables(ChatSchema.TABLE);
                // limit query to one row at most:
                builder.appendWhere(ChatSchema.KEY_ROWID + " = " +
                        uri.getLastPathSegment());
                break;
            case USER_LIST:
                builder.setTables(ChatSchema.TABLE);
                groupby = "CASE  WHEN sender='" + username + "@" + AppUtil.SERVER_NAME + "' THEN receiver ELSE sender END";

                break;
            case USER_ID:
                builder.setTables(UserSchema.TABLE);
                // limit query to one row at most:
                builder.appendWhere(UserSchema.KEY_ROW_ID + " = " +
                        uri.getLastPathSegment());
                break;
            case PRODUCT_LIST:
                builder.setTables(ProSchema.TABLE);

                break;
            case PRODUCT_ID:
                builder.setTables(ProSchema.TABLE);
                // limit query to one row at most:
                builder.appendWhere(UserSchema.KEY_ROW_ID + " = " +
                        uri.getLastPathSegment());
                break;
            case BOOK_LIST:
                builder.setTables(ContactsDb.TABLE_CONTACTS);

                break;
            case BOOK_ID:
                builder.setTables(ContactsDb.TABLE_CONTACTS);

                break;

            case HIFI_LIST:
                builder.setTables(HiFiSchema.TABLE);

                break;
            case HIFI_ID:
                builder.setTables(HiFiSchema.TABLE);

                break;

            case BOOKMARK_LIST:
                builder.setTables(BookSchema.TABLE);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BookSchema.SORT_ORDER_DEFAULT;
                }

                break;
            case BOOKMARK_ID:
                builder.setTables(BookSchema.TABLE);

                break;


            case QUERY_LIST:
                builder.setTables(QuerySchema.TABLE);

                break;
            case QUERY_ID:
                builder.setTables(QuerySchema.TABLE);

                break;
            case CART_LIST:
                builder.setTables(CartSchema.TABLE);

                break;
            case CART_ID:
                builder.setTables(CartSchema.TABLE);

                break;
            case BROADCAST_LIST:
                builder.setTables(BroadcastMessage.TABLE);

                break;
            case BROADCAST_ID:
                builder.setTables(BroadcastMessage.TABLE);

                break;

            case BROADCAST_USER_LIST:
                builder.setTables(BroadcastUser.TABLE);

                break;
            case BROADCAST_USER_ID:
                builder.setTables(BroadcastUser.TABLE);

                break;
            case CHAT_USER_LIST:
                String tables = "ChatUser LEFT JOIN networkuser ON (ChatUser.chat_user = networkuser.network_id) LEFT JOIN chat ON (ChatUser.last_msg_id = chat.msg_id AND chatuser.message_type='chat') LEFT JOIN BroadcastMessage ON (ChatUser.last_msg_id = BroadcastMessage.message_id AND chatuser.message_type='broadcast') ";
                builder.setProjectionMap(buildColumnMap());
                builder.setTables(tables);
                groupby = " chat_user";

                break;
            case CHAT_USER_ID:
                builder.setTables(ChatUserSchema.TABLE);

                break;


            case SEARCH_LIST:
                Log.i("HERE", "at group");
                builder.setTables(ChatSchema.TABLE);
                groupby = "CASE  WHEN sender='" + username + "@" + AppUtil.SERVER_NAME + "' THEN receiver ELSE sender END,strftime('%d-%m-%Y ',created_at)";


                break;
            case SEARCH_ID:
                builder.setTables(ChatSchema.TABLE);
                // limit query to one row at most:
                builder.appendWhere(UserSchema.KEY_ROW_ID + " = " +
                        uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        groupby,
                        null,
                        sortOrder);


        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    CONTENT_URI);
        } else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }

        return cursor;
        // return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case CHAT_LIST:
                return CHAT_TYPE;
            case CHAT_ID:
                return CHAT_ID_TYPE;
            case USER_LIST:
                return USER_TYPE;
            case USER_ID:
                return USER_ID_TYPE;
            case PRODUCT_LIST:
                return PRODUCT_TYPE;
            case PRODUCT_ID:
                return PRODUCT_ID_TYPE;
            case NET_LIST:
                return NET_TYPE;
            case NET_ID:
                return NET_ID_TYPE;

            case BOOK_LIST:
                return BOOK_TYPE;
            case BOOK_ID:
                return BOOK_ID_TYPE;

            case HIFI_LIST:
                return HiFi_TYPE;
            case HIFI_ID:
                return HiFi_ID_TYPE;

            case BOOKMARK_LIST:
                return BOOKMARK_TYPE;
            case BOOKMARK_ID:
                return BOOKMARK_ID_TYPE;

            case SEARCH_LIST:
                return CHAT_SEARCH_TYPE;
            case SEARCH_ID:
                return CHAT_SEARCH_ID_TYPE;


            case QUERY_LIST:
                return QUERY_TYPE;
            case QUERY_ID:
                return QUERY_ID_TYPE;

            case CART_LIST:
                return CART_TYPE;
            case CART_ID:
                return CART_ID_TYPE;

            case BROADCAST_LIST:
                return BROADCAST_TYPE;
            case BROADCAST_ID:
                return BROADCAST_ID_TYPE;
            case BROADCAST_USER_LIST:
                return BROADCAST_USER_TYPE;
            case BROADCAST_USER_ID:
                return BROADCAST_USER_ID_TYPE;


            case CHAT_USER_LIST:
                return CHAT_USER_TYPE;
            case CHAT_USER_ID:
                return CHAT_USER_ID_TYPE;
            default:
                return null;
        }
        //   return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        BackupManager bm = new BackupManager(getContext());
        bm.dataChanged();
        SQLiteDatabase db = ourHelper.getWritableDatabase();
        try {
            if (URI_MATCHER.match(uri) == CHAT_LIST) {
                long id =
                        db.insertWithOnConflict(
                                ChatSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == USER_LIST) {
                long id =
                        db.insertWithOnConflict(
                                UserSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == PRODUCT_LIST) {
                //     Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                ProSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == NET_LIST) {
                //   Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                NetSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == BOOK_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                ContactsDb.TABLE_CONTACTS,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);

                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == HIFI_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                HiFiSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == QUERY_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                QuerySchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == BOOKMARK_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                BookSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == CART_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                CartSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == BROADCAST_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                BroadcastMessage.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == BROADCAST_USER_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                BroadcastUser.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else if (URI_MATCHER.match(uri) == CHAT_USER_LIST) {
                // Log.d("here", "" + uri);
                long id =
                        db.insertWithOnConflict(
                                ChatUserSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            } else {

                long id =
                        db.insertWithOnConflict(
                                ChatSchema.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            }
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("MyContentProvider", "Exception:" + e.getMessage());
        }
        // }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = ourHelper.getWritableDatabase();
        int delCount = 0;
        try{
        switch (URI_MATCHER.match(uri)) {
            case CHAT_LIST:
                delCount = db.delete(
                        ChatSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case CHAT_ID:
                String idStr = uri.getLastPathSegment();
                String where = ChatSchema.KEY_ROWID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        ChatSchema.TABLE,
                        where,
                        selectionArgs);
                break;
            case USER_LIST:
                delCount = db.delete(
                        UserSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case USER_ID:
                String idStrUser = uri.getLastPathSegment();
                String whereUser = UserSchema.KEY_ROW_ID + " = " + idStrUser;
                if (!TextUtils.isEmpty(selection)) {
                    idStrUser += " AND " + selection;
                }
                delCount = db.delete(
                        ChatSchema.TABLE,
                        idStrUser,
                        selectionArgs);
                break;

            case NET_LIST:
                delCount = db.delete(
                        NetSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case NET_ID:
                String idStrNet = uri.getLastPathSegment();
                String whereNet = UserSchema.KEY_ROW_ID + " = " + idStrNet;
                if (!TextUtils.isEmpty(selection)) {
                    idStrNet += " AND " + selection;
                }
                delCount = db.delete(
                        NetSchema.TABLE,
                        idStrNet,
                        selectionArgs);
                break;
            case PRODUCT_LIST:
                delCount = db.delete(
                        ProSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case PRODUCT_ID:
                String idStrPro = uri.getLastPathSegment();
                String wherepro = ProSchema.KEY_ROW_ID + " = " + idStrPro;
                if (!TextUtils.isEmpty(selection)) {
                    idStrPro += " AND " + selection;
                }
                delCount = db.delete(
                        ProSchema.TABLE,
                        idStrPro,
                        selectionArgs);
                break;


            case BOOK_LIST:
                delCount = db.delete(
                        ContactsDb.TABLE_CONTACTS,
                        selection,
                        selectionArgs);
                break;
            case BOOK_ID:
                String idStrcon = uri.getLastPathSegment();
                String wherecon = ProSchema.KEY_ROW_ID + " = " + idStrcon;
                if (!TextUtils.isEmpty(selection)) {
                    idStrcon += " AND " + selection;
                }
                delCount = db.delete(
                        ProSchema.TABLE,
                        idStrcon,
                        selectionArgs);
                break;
            case HIFI_LIST:
                delCount = db.delete(
                        HiFiSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case HIFI_ID:
                String idStrHifi = uri.getLastPathSegment();
                String whereHifi = ProSchema.KEY_ROW_ID + " = " + idStrHifi;
                if (!TextUtils.isEmpty(selection)) {
                    idStrHifi += " AND " + selection;
                }
                delCount = db.delete(
                        HiFiSchema.TABLE,
                        idStrHifi,
                        selectionArgs);
                break;
            case BOOKMARK_LIST:
                delCount = db.delete(
                        BookSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case BOOKMARK_ID:
                String idStrBookmark = uri.getLastPathSegment();
                String whereBookmark = BookSchema.KEY_ROWID + " = " + idStrBookmark;
                if (!TextUtils.isEmpty(selection)) {
                    idStrBookmark += " AND " + selection;
                }
                delCount = db.delete(
                        BookSchema.TABLE,
                        idStrBookmark,
                        selectionArgs);
                break;
            case QUERY_LIST:
                delCount = db.delete(
                        QuerySchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case QUERY_ID:
                String idStrQuery = uri.getLastPathSegment();
                String whereQuery = QuerySchema.KEY_ROW_ID + " = " + idStrQuery;
                if (!TextUtils.isEmpty(selection)) {
                    idStrQuery += " AND " + selection;
                }
                delCount = db.delete(
                        QuerySchema.TABLE,
                        idStrQuery,
                        selectionArgs);
                break;
            case CART_LIST:
                delCount = db.delete(
                        CartSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case CART_ID:
                String idStrCart = uri.getLastPathSegment();
                String whereCart = QuerySchema.KEY_ROW_ID + " = " + idStrCart;
                if (!TextUtils.isEmpty(selection)) {
                    idStrCart += " AND " + selection;
                }
                delCount = db.delete(
                        CartSchema.TABLE,
                        idStrCart,
                        selectionArgs);
                break;
            case BROADCAST_LIST:
                delCount = db.delete(
                        BroadcastMessage.TABLE,
                        selection,
                        selectionArgs);
                break;
            case BROADCAST_ID:
                String idStrBroad = uri.getLastPathSegment();
                String whereBroad = QuerySchema.KEY_ROW_ID + " = " + idStrBroad;
                if (!TextUtils.isEmpty(selection)) {
                    idStrBroad += " AND " + selection;
                }
                delCount = db.delete(
                        BroadcastMessage.TABLE,
                        idStrBroad,
                        selectionArgs);
                break;
            case BROADCAST_USER_LIST:
                delCount = db.delete(
                        BroadcastUser.TABLE,
                        selection,
                        selectionArgs);
                break;
            case BROADCAST_USER_ID:
                String idStrBroadUser = uri.getLastPathSegment();
                String whereBroadUser = QuerySchema.KEY_ROW_ID + " = " + idStrBroadUser;
                if (!TextUtils.isEmpty(selection)) {
                    idStrBroadUser += " AND " + selection;
                }
                delCount = db.delete(
                        BroadcastUser.TABLE,
                        idStrBroadUser,
                        selectionArgs);
                break;


            case CHAT_USER_LIST:
                delCount = db.delete(
                        ChatUserSchema.TABLE,
                        selection,
                        selectionArgs);
                break;
            case CHAT_USER_ID:
                String idStrChatUser = uri.getLastPathSegment();
                String whereChatUser = QuerySchema.KEY_ROW_ID + " = " + idStrChatUser;
                if (!TextUtils.isEmpty(selection)) {
                    idStrChatUser += " AND " + selection;
                }
                delCount = db.delete(
                        ChatUserSchema.TABLE,
                        idStrChatUser,
                        selectionArgs);
                break;


            default:
                // no support for deleting photos or entities –
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    } catch (android.database.sqlite.SQLiteConstraintException e) {
        Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
    } catch (android.database.sqlite.SQLiteException e) {
        Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
    } catch (Exception e) {
        Log.e("MyContentProvider", "Exception:" + e.getMessage());
    }
        // notify all listeners of changesProSchema:

        return delCount;
        // return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = ourHelper.getWritableDatabase();
        int updateCount = 0;
        try{
        switch (URI_MATCHER.match(uri)) {

            case BOOK_LIST:
                updateCount = db.update(
                        ContactsDb.TABLE_CONTACTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case BOOK_ID:
                String idStrcon = uri.getLastPathSegment();
                String wherecon = ContactsDb.KEY_ID + " = " + idStrcon;
                if (!TextUtils.isEmpty(selection)) {
                    wherecon += " AND " + selection;
                }
                updateCount = db.update(
                        ChatSchema.TABLE,
                        values,
                        wherecon,
                        selectionArgs);
                break;
            case CHAT_LIST:
                updateCount = db.update(
                        ChatSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CHAT_ID:
                String idStr = uri.getLastPathSegment();
                String where = ChatSchema.KEY_ROWID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        ChatSchema.TABLE,
                        values,
                        where,
                        selectionArgs);
                break;
            case USER_LIST:
                updateCount = db.update(
                        UserSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case USER_ID:
                String idStrUser = uri.getLastPathSegment();
                String whereUser = UserSchema.KEY_ROW_ID + " = " + idStrUser;
                if (!TextUtils.isEmpty(selection)) {
                    whereUser += " AND " + selection;
                }
                updateCount = db.update(
                        ChatSchema.TABLE,
                        values,
                        idStrUser,
                        selectionArgs);
                break;
            case NET_LIST:
                updateCount = db.update(
                        NetSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case NET_ID:
                String idNetUser = uri.getLastPathSegment();
                String whereNetUser = UserSchema.KEY_ROW_ID + " = " + idNetUser;
                if (!TextUtils.isEmpty(selection)) {
                    whereNetUser += " AND " + selection;
                }
                updateCount = db.update(
                        NetSchema.TABLE,
                        values,
                        idNetUser,
                        selectionArgs);
                break;

            case HIFI_LIST:
                updateCount = db.update(
                        HiFiSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case HIFI_ID:
                String idHifi = uri.getLastPathSegment();
                String whereHifi = UserSchema.KEY_ROW_ID + " = " + idHifi;
                if (!TextUtils.isEmpty(selection)) {
                    idHifi += " AND " + selection;
                }
                updateCount = db.update(
                        HiFiSchema.TABLE,
                        values,
                        idHifi,
                        selectionArgs);
                break;
            case BOOKMARK_LIST:
                updateCount = db.update(
                        BookSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case BOOKMARK_ID:
                String idBookmark = uri.getLastPathSegment();
                String whereBookmark = UserSchema.KEY_ROW_ID + " = " + idBookmark;
                if (!TextUtils.isEmpty(selection)) {
                    idBookmark += " AND " + selection;
                }
                updateCount = db.update(
                        BookSchema.TABLE,
                        values,
                        idBookmark,
                        selectionArgs);
                break;
            case CART_LIST:
                updateCount = db.update(
                        CartSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CART_ID:
                String idCart = uri.getLastPathSegment();
                String whereCart = UserSchema.KEY_ROW_ID + " = " + idCart;
                if (!TextUtils.isEmpty(selection)) {
                    idCart += " AND " + selection;
                }
                updateCount = db.update(
                        CartSchema.TABLE,
                        values,
                        idCart,
                        selectionArgs);
                break;
            case BROADCAST_LIST:
                updateCount = db.update(
                        BroadcastMessage.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case BROADCAST_ID:
                String idBroad = uri.getLastPathSegment();
                String whereBroad = UserSchema.KEY_ROW_ID + " = " + idBroad;
                if (!TextUtils.isEmpty(selection)) {
                    idBroad += " AND " + selection;
                }
                updateCount = db.update(
                        BroadcastMessage.TABLE,
                        values,
                        idBroad,
                        selectionArgs);
                break;
            case BROADCAST_USER_LIST:
                updateCount = db.update(
                        BroadcastUser.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case BROADCAST_USER_ID:
                String idBroadUser = uri.getLastPathSegment();
                String whereBroadUser = UserSchema.KEY_ROW_ID + " = " + idBroadUser;
                if (!TextUtils.isEmpty(selection)) {
                    idBroadUser += " AND " + selection;
                }
                updateCount = db.update(
                        BroadcastUser.TABLE,
                        values,
                        idBroadUser,
                        selectionArgs);
                break;
            case CHAT_USER_LIST:
                updateCount = db.update(
                        ChatUserSchema.TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CHAT_USER_ID:
                String idChatUser = uri.getLastPathSegment();
                String whereChatUser = UserSchema.KEY_ROW_ID + " = " + idChatUser;
                if (!TextUtils.isEmpty(selection)) {
                    idChatUser += " AND " + selection;
                }
                updateCount = db.update(
                        ChatUserSchema.TABLE,
                        values,
                        idChatUser,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    } catch (android.database.sqlite.SQLiteConstraintException e) {
        Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
    } catch (android.database.sqlite.SQLiteException e) {
        Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
    } catch (Exception e) {
        Log.e("MyContentProvider", "Exception:" + e.getMessage());
    }
        return updateCount;
        // return 0;


    }
}
