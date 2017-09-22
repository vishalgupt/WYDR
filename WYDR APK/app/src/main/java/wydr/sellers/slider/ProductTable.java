package wydr.sellers.slider;

import android.database.sqlite.SQLiteDatabase;

public class ProductTable {


    public static final String TABLE_CONNECTIONS = "connections";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone_number";
    public static final String KEY_UserID = "network_user_id";
    public static final String KEY_DIS_NAME = "display_name";
    public static final String KEY_COMPANY = "company_name";


    public static void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONNECTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DIS_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT," + KEY_COMPANY + " TEXT," + KEY_UserID + " TEXT"

                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
        onCreate(db);
    }

}