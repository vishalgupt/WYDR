package wydr.sellers.acc;

/**
 * Created by surya on 30/7/15.
 */
public class UserSchema {
    public static final String KEY_CREATED = "created_at";
    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_COMPANY = "company_id";
    public static final String KEY_COMPANY_NAME = "company";
    //public static final String KEY_UNREAD = "unread_msg";
    //public static final String KEY_ACTIVITY = "last_activity";
    public static final String TABLE = "User";
    public static final String SORT_ORDER_DEFAULT =
            KEY_NAME + " ASC";

}
