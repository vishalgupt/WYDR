package wydr.sellers.acc;

/**
 * Created by surya on 12/10/15.
 */
public class BookSchema {
    public static final String KEY_CREATED = "created_at";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_MSG_ID = "msg_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_SUB = "subject";
    public static final String KEY_MSG = "msg";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_ID = "user_id";
    public static final String TABLE = "Bookmark";
    public static final String IsMe = "isMe";


    public static final String SORT_ORDER_DEFAULT =
            KEY_CREATED + " DESC";
//
//    public static String getDateTime() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(
//                "dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
//        Date date = new Date();
//        return dateFormat.format(date);
//    }
}
