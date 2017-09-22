package wydr.sellers.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import wydr.sellers.R;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.registration.Login;
import wydr.sellers.slider.MyContentProvider;


public class SessionManager
{
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_ID = "id";
    // Sharedpref file name
    private static final String PREF_NAME = "SamsPref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_CHAT= "IsChat";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    android.app.AlertDialog.Builder alertDialog;

    public String MyPREFERENCES = "MyPrefs";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        alertDialog = new AlertDialog.Builder(context);
    }
    public void createSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_CHAT, true);
        // commit changes
        // editor.
        editor.commit();
    }

    public void logoutUser() {
      /*  alertDialog.setCancelable(false);
        alertDialog.setTitle(_context.getResources().getString(R.string.sorry));
        alertDialog.setMessage(_context.getResources().getString(R.string.unauthorized_user));
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {*/
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_AUTHENTICATION, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_FEED, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_ALTER, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_Login, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_Category, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_Contacts, null, null);
                _context.getContentResolver().delete(MyContentProvider.CONTENT_URI_Loggedin, null, null);
//
//                _context.getContentResolver().delete(ChatProvider.CONTENT_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.URI_USER,null,null);
//                _context.getContentResolver().delete(ChatProvider.URI_PRODUCT,null,null);
                _context.getContentResolver().delete(ChatProvider.NET_URI, null, null);
//                _context.getContentResolver().delete(ChatProvider.BOOK_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.HiFi_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.BOOKMARK_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.SEARCH_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.QUERY_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.CART_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.BROADCAST_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.BROADCAST_USER_URI,null,null);
//                _context.getContentResolver().delete(ChatProvider.CHAT_USER_URI,null,null);
                _context.getSharedPreferences(MyPREFERENCES, 0).edit().clear().commit();
                Intent i = new Intent( _context, Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
/*

            }
        });
        alertDialog.show();*/
    }

    public boolean isChatIn() {
        return pref.getBoolean(IS_CHAT, false);
    }
}
