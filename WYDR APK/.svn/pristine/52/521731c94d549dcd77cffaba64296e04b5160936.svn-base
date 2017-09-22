package wydr.sellers.acc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class IdHolder {
    public static final String TOKEN = "gcm_wydr";
    // Sharedpref file name
    private static final String PREF_NAME = "gcm_wydr_id";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public IdHolder(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getToken() {

        return pref.getString(TOKEN, null);
    }

    /**
     * Create login session
     */

    public void setToken(String gcm) {

        editor.putString(TOKEN, gcm);

        editor.commit();
    }

}
