package wydr.sellers.slider;

/**
 * Created by Deepesh_pc on 16-07-2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.syncadap.AlteredCatalog;
import wydr.sellers.syncadap.FeedCatalog;

public class MyDatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "WYDR";
    public static final int DATABASE_VERSION = 21 ;

    public MyDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlitedatabase)
    {
        sqlitedatabase.execSQL("CREATE TABLE " + Authentication.TABLE_CONTACTS + "("
                + Authentication.KEY_ID + " INTEGER PRIMARY KEY,"
                + Authentication.login + " TEXT,"
                + Authentication.api_key + " TEXT"+
                 ")");

        sqlitedatabase.execSQL("CREATE TABLE " + LoginDB.TABLE_LOGIN + "("
                + LoginDB.KEY_ID + " INTEGER PRIMARY KEY,"
                + LoginDB.KEY_PHONE + " TEXT,"
                + LoginDB.KEY_NAME + " TEXT,"
                + LoginDB.KEY_EMAIL + " TEXT,"
                + LoginDB.KEY_PASSWORD + " TEXT,"
                + LoginDB.KEY_USERID + " TEXT,"
                + LoginDB.KEY_CATEGORY + " TEXT,"
                + LoginDB.KEY_SCOPE + " TEXT,"
                + LoginDB.KEY_ADDRESS + " TEXT,"
                + LoginDB.KEY_STATE + " TEXT,"
                + LoginDB.KEY_CITY + " TEXT,"
                + LoginDB.KEY_PAN + " TEXT,"
                + LoginDB.KEY_TIN + " TEXT,"
                + LoginDB.KEY_BUSINESSTYPE + " TEXT,"
                + LoginDB.KEY_COMPANY + " TEXT,"
                + LoginDB.KEY_PARENT_CATEGORY + " TEXT,"
                + LoginDB.KEY_INTERESTED + " TEXT,"
                + LoginDB.KEY_PINCODE + " TEXT,"
                + LoginDB.KEY_VAT + " TEXT,"
                + LoginDB.KEY_ACCOUNT_HOLDER_NAME + " TEXT,"
                + LoginDB.KEY_ACCOUNT_NUMBER + " TEXT,"
                + LoginDB.KEY_BANK_NAME + " TEXT,"
                + LoginDB.KEY_IFSC_CODE + " TEXT,"
                + LoginDB.KEY_ACCOUNT_TYPE + " TEXT"+ ")");

        sqlitedatabase.execSQL("CREATE TABLE " +
                CategoryTable.TABLE_CONTACTS + "("
                + CategoryTable.KEY_ID + " INTEGER PRIMARY KEY,"
                + CategoryTable.KEY_CATEGORY_ID + " TEXT,"
                + CategoryTable.KEY_CATEGORY_NAME + " TEXT,"
                + CategoryTable.KEY_HAS_CHILD + " TEXT,"
                + CategoryTable.KEY_PARENT_ID + " TEXT,"
                + CategoryTable.KEY_TIMESTAMP + " TEXT,"
                + CategoryTable.KEY_PRODUCT_COUNT + " INTEGER" + ")");

        sqlitedatabase.execSQL("CREATE TABLE " + LoggedIn.TABLE_LOGGEDIN + "("
                + LoggedIn.KEY_ID + " INTEGER PRIMARY KEY,"
                + LoggedIn.KEY_IsLogin + " BOOLEAN" + ")");

        sqlitedatabase.execSQL("CREATE TABLE " + FeedCatalog.TABLE_NAME + "("
                + FeedCatalog._ID + " INTEGER PRIMARY KEY,"
                + FeedCatalog.COLUMN_PRODUCT_ID + " TEXT,"
                + FeedCatalog.COLUMN_TITLE + " TEXT,"
                + FeedCatalog.COLUMN_CODE + " TEXT ,"
                + FeedCatalog.COLUMN_IMAGEPATH + " TEXT,"
                + FeedCatalog.COLUMN_GRANDPARENTCAT + " TEXT,"
                + FeedCatalog.COLUMN_PARENTCAT + " TEXT,"
                + FeedCatalog.COLUMN_CHILDCAT + " TEXT,"
                + FeedCatalog.COLUMN_MRP + " DOUBLE,"
                + FeedCatalog.COLUMN_SP + " DOUBLE,"
                + FeedCatalog.COLUMN_QTY + " DOUBLE,"
                + FeedCatalog.COLUMN_MINQTY + " DOUBLE,"
                + FeedCatalog.COLUMN_DESC + " TEXT,"
                + FeedCatalog.COLUMN_VISIBILTY + " TEXT,"
                + FeedCatalog.COLUMN_CREATEDAT + " TEXT,"
                + FeedCatalog.COLUMN_UPDATEDAT + " DOUBLE,"
                + FeedCatalog.COLUMN_STATUS + " TEXT,"
                + FeedCatalog.COLUMN_THUMB_PATH + " TEXT,"
                + FeedCatalog.COLUMN_GRANDCHILDCAT + " TEXT,"
                + FeedCatalog.COLUMN_CATEGORY_ID + " TEXT,"
                + FeedCatalog.COLUMN_LOCAL_PROD_ID + " TEXT" +
                                ")");

        sqlitedatabase.execSQL("CREATE TABLE " + AlteredCatalog.TABLE_NAME + "("
                + AlteredCatalog._ID + " INTEGER PRIMARY KEY,"
                + AlteredCatalog.COLUMN_PRODUCT_ID + " TEXT,"
                + AlteredCatalog.COLUMN_TITLE + " TEXT,"
                + AlteredCatalog.COLUMN_CODE + " TEXT ,"
                + AlteredCatalog.COLUMN_IMAGEPATH + " TEXT,"
                + AlteredCatalog.COLUMN_GRANDPARENTCAT + " TEXT,"
                + AlteredCatalog.COLUMN_PARENTCAT + " TEXT,"
                + AlteredCatalog.COLUMN_CHILDCAT + " TEXT,"
                + AlteredCatalog.COLUMN_MRP + " DOUBLE,"
                + AlteredCatalog.COLUMN_SP + " DOUBLE,"
                + AlteredCatalog.COLUMN_QTY + " DOUBLE,"
                + AlteredCatalog.COLUMN_MINQTY + " DOUBLE,"
                + AlteredCatalog.COLUMN_DESC + " TEXT,"
                + AlteredCatalog.COLUMN_VISIBILTY + " TEXT,"
                + AlteredCatalog.COLUMN_LOCAL_FLAG + " TEXT,"
                + AlteredCatalog.COLUMN_DEFAULT_POSITION + " TEXT,"
                + AlteredCatalog.COLUMN_UPDATED + " TEXT,"
                + AlteredCatalog.COLUMN_COMPANY_ID + " TEXT,"
                + AlteredCatalog.COLUMN_REQUEST_STATUS + " TEXT,"
                + AlteredCatalog.COLUMN_LOCAL_PROD_ID + " TEXT,"
                + AlteredCatalog.COLUMN_ERROR_FLAG + " TEXT,"
                + AlteredCatalog.COLUMN_ERROR_MESSAGE + " TEXT,"
                + AlteredCatalog.COLUMN_STATUS + " TEXT,"
                + AlteredCatalog.COLUMN_THUMB_PATH + " TEXT,"
                + AlteredCatalog.COLUMN_GRANDCHILDCAT + " TEXT,"
                + AlteredCatalog.COLUMN_CATEGORY_ID + " TEXT,"
                + AlteredCatalog.COLUMN_CREATEDAT + " TEXT" + ")");


        sqlitedatabase.execSQL("CREATE TABLE " + MyCategoryTable.TABLE_CONTACTS + "("
                + MyCategoryTable.KEY_ID + " INTEGER PRIMARY KEY,"
                + MyCategoryTable.KEY_CATEGORY_ID + " TEXT,"
                + MyCategoryTable.KEY_CATEGORY_NAME + " TEXT,"
                + MyCategoryTable.KEY_HAS_CHILD + " TEXT,"
                + MyCategoryTable.KEY_PARENT_ID + " TEXT,"
                + MyCategoryTable.KEY_PRODUCT_COUNT + " INTEGER,"
                + MyCategoryTable.KEY_UPDATED_AT + " TEXT" + ")"
        );

        sqlitedatabase.execSQL("CREATE TABLE Chat (_id INTEGER PRIMARY KEY, sender TEXT NOT NULL, chat_id TEXT , receiver TEXT NOT NULL, msg TEXT NOT NULL, msg_id TEXT, created_at TEXT NOT NULL, isMe INTEGER NOT NULL, display INTEGER, subject TEXT, error INTEGER, download INTEGER, broadcast INTEGER, status INTEGER NOT NULL);");
        sqlitedatabase.execSQL("CREATE TABLE MyConnection (_id TEXT PRIMARY KEY, name TEXT NOT NULL, company_name TEXT, network_user_id TEXT, user_id TEXT, company_id TEXT, img_url TEXT, created_at TEXT, status INTEGER NOT NULL);");
        //sqlitedatabase.execSQL("CREATE TABLE User (_id TEXT PRIMARY KEY, name TEXT NOT NULL, user_id TEXT NOT NULL, company_id TEXT NOT NULL, created_at TEXT NOT NULL, status INTEGER NOT NULL);");
        sqlitedatabase.execSQL("CREATE TABLE Product (_id TEXT PRIMARY KEY, name TEXT NOT NULL, price TEXT NOT NULL, mrp TEXT NOT NULL, url TEXT NOT NULL, code TEXT NOT NULL, moq TEXT NOT NULL, created_at TEXT NOT NULL, status INTEGER NOT NULL);");
        /*  //----akshay-----/
        sqlitedatabase.execSQL("CREATE TABLE NetworkUser (_id TEXT PRIMARY KEY, network_id TEXT NOT NULL, name TEXT NOT NULL, display_name TEXT NOT NULL, phone TEXT NOT NULL, company TEXT NOT NULL, company_id TEXT NOT NULL, img_url TEXT, seller_id TEXT NOT NULL ,status_t TEXT NOT NULL,created_at TEXT NOT NULL, status INTEGER NOT NULL);");
        ///----akshay-------//*/
        sqlitedatabase.execSQL("CREATE TABLE NetworkUser (_id TEXT PRIMARY KEY, network_id TEXT NOT NULL, name TEXT NOT NULL, display_name TEXT NOT NULL, phone TEXT NOT NULL, company TEXT NOT NULL, company_id TEXT NOT NULL, img_url TEXT, created_at TEXT NOT NULL, status INTEGER NOT NULL);");
        sqlitedatabase.execSQL("CREATE TABLE HiFi (_id TEXT PRIMARY KEY, name TEXT NOT NULL, product_id TEXT, price TEXT NOT NULL, code TEXT NOT NULL, quantity TEXT NOT NULL, request_for, request_status INTEGER, created_at TEXT NOT NULL, status INTEGER NOT NULL);");
        sqlitedatabase.execSQL("CREATE TABLE Bookmark (_id INTEGER PRIMARY KEY, msg TEXT NOT NULL, msg_id TEXT, user_id TEXT, user TEXT, created_at TEXT NOT NULL, isMe INTEGER NOT NULL, subject TEXT, status INTEGER NOT NULL);");
        sqlitedatabase.execSQL("CREATE TABLE QueryFeed (_id TEXT PRIMARY KEY, query TEXT NOT NULL, url TEXT NOT NULL, created_at TEXT NOT NULL, status INTEGER NOT NULL);");

        sqlitedatabase.execSQL("CREATE TABLE Cart (_id INTEGER PRIMARY KEY, product_name TEXT NOT NULL, product_id TEXT NOT NULL, product_code TEXT NOT NULL, price TEXT, url TEXT, shipping TEXT, shipping_charges TEXT, qty INTEGER, moq INTEGER, inventory INTEGER, status INTEGER NOT NULL,discount TEXT,discount_value TEXT,promo_code TEXT,pay_method TEXT,promo_applied TEXT,promo_cart TEXT,weight INTEGER,catid INTEGER,minqty TEXT,maxamt TEXT,varients TEXT,var_string TEXT,created_at);");

        sqlitedatabase.execSQL("CREATE TABLE BroadcastUser (_id INTEGER PRIMARY KEY, broadcast_id INTEGER NOT NULL, name TEXT NOT NULL, jid TEXT NOT NULL, status INTEGER NOT NULL,created_at);");
        //sqlitedatabase.execSQL("CREATE TABLE Broadcast (_id INTEGER PRIMARY KEY, name TEXT NOT NULL, msg TEXT NOT NULL, status INTEGER NOT NULL,created_at);");
        sqlitedatabase.execSQL("CREATE TABLE ChatUser (_id INTEGER PRIMARY KEY, chat_user TEXT NOT NULL, last_msg_id TEXT, message_type TEXT, direction , unread INTEGER , created_at);");
        sqlitedatabase.execSQL("CREATE TABLE BroadcastMessage (_id INTEGER PRIMARY KEY, broadcast_id TEXT NOT NULL, message TEXT NOT NULL, message_id TEXT NOT NULL, message_type TEXT, error INTEGER, download INTEGER, status INTEGER NOT NULL,created_at);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlitedatabase, int oldVersion, int newVersion)
    {
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS Chat");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS Product");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS NetworkUser");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS MyConnection");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS HiFi");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS Bookmark");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS QueryFeed");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS Cart");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS Broadcast");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS BroadcastUser");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS BroadcastMessage");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS ChatUser");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + LoginDB.TABLE_LOGIN);
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE_CONTACTS);
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + LoggedIn.TABLE_LOGGEDIN);
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + FeedCatalog.TABLE_NAME);
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + AlteredCatalog.TABLE_NAME);
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + MyCategoryTable.TABLE_CONTACTS);
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + Authentication.TABLE_CONTACTS);
        onCreate(sqlitedatabase);
    }

    public String companyids()
    {
        String selectQuery = "SELECT * FROM MyConnection";
        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        String a="";

        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {

                    do
                    {
                        //if(cursor.getString(cursor.getColumnIndex(NetSchema.USER_STATUS_toggle)).equalsIgnoreCase("A")) {
                            a += cursor.getString(cursor.getColumnIndex("company_id")) + ",";
                        //}
                    }
                    while (cursor.moveToNext());
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }

        /******************************************************************/

        return a;

    }

    public String pay_method()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/
        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String a="";

        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    a+=cursor.getString(cursor.getColumnIndex("pay_method"))+",";
                }
                while (cursor.moveToNext());
            }

        }

        catch (RuntimeException e)
        {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }
        /******************************************************************/


        return a;
    }


    public String cat_id()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/
        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String a="";
        try {
            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    a+= String.valueOf(cursor.getInt(cursor.getColumnIndex(CartSchema.PRODUCT_CAT_ID)) + ",");
                }
                while (cursor.moveToNext());
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }

        /******************************************************************/

        return a;
    }

    public String SellerID()
    {
        String selectQuery = "SELECT * FROM NetworkUser";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String a="";

        if (cursor.moveToFirst())
        {
            do
            {
                a+=cursor.getString(cursor.getColumnIndex("status_t"))+",";
            }
            while (cursor.moveToNext());
        }
        return a;
    }


    public int discount()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/

        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();;
        Cursor cursor = null;
        Double a= 0.0;

        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    if(((Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount")))*cursor.getInt(cursor.getColumnIndex("qty"))) < Double.parseDouble(cursor.getString((cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT))))))
                    {
                        a+=Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount")))*cursor.getInt(cursor.getColumnIndex("qty"));
                    }

                    else if((Integer.parseInt(cursor.getString((cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT))))==0))
                    {
                        a+=Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount")))*cursor.getInt(cursor.getColumnIndex("qty"));
                    }

                    else
                    {
                        a+= Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT)));
                    }

                }
                while (cursor.moveToNext());
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }

        /******************************************************************/

        return (int)Math.floor(a);
    }

    public int changediscount()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/

        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int a=0;
        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do {
                    if ((Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount"))) * cursor.getInt(cursor.getColumnIndex("qty"))) < Integer.parseInt(cursor.getString((cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT))))) {
                        a += cursor.getInt(cursor.getColumnIndex("discount")) * cursor.getInt(cursor.getColumnIndex("qty"));
                    } else {
                        a += Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT)));
                    }
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return a;
    }

    public int singleDiscount()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/

        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int a=0;

        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if((cursor.getInt(cursor.getColumnIndex("discount"))*cursor.getInt(cursor.getColumnIndex("qty"))) < Integer.parseInt(cursor.getString((cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT)))))
            {
                a=cursor.getInt(cursor.getColumnIndex("discount"))*cursor.getInt(cursor.getColumnIndex("qty"));
            }

            else
            {
                a= Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT)));
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }

        /******************************************************************/

        return a;
    }

    public int Shipping()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/

        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int a=0;

        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    a+=cursor.getInt(cursor.getColumnIndex(CartSchema.PRODUCT_WEIGHT))*cursor.getInt(cursor.getColumnIndex("qty"));
                }
                while (cursor.moveToNext());
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }

        /******************************************************************/

        return a;
    }

    public String couponstring()
    {
        String selectQuery = "SELECT * FROM Cart";
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);*/

        /********************** Added by Istiaque **********************/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String a="";

        try {

            //db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    a+= String.valueOf(cursor.getInt(cursor.getColumnIndex(CartSchema.PROMO_CODE))+",");
                }
                while (cursor.moveToNext());
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException:", e.getMessage());
        } finally {

            db.close();
            cursor.close();
        }

        /******************************************************************/

        return a;
    }
}