package wydr.sellers.acc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by surya on 19/7/15.
 */
public class ChatSchema {
    public static final String KEY_CREATED = "created_at";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_CHAT_ID = "chat_id";
    public static final String KEY_MSG_ID = "msg_id";
    public static final String KEY_DIS = "display";
    //public static final String KEY_DIV = "display";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_MSG = "msg";
    public static final String KEY_SUB = "subject";
    public static final String TABLE = "Chat";
    public static final String IsMe = "isMe";
    public static final String IsError = "error";
    public static final String STATUS = "status";
    public static final String IsDownload = "download";
    public static final String BROADCAST = "broadcast";
    //broadcast


    public static final String SORT_ORDER_DEFAULT =
            KEY_ROWID + " ASC";

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
