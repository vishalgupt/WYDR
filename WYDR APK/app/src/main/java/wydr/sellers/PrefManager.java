package wydr.sellers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Akshay on 2/5/2016.
 */
public class PrefManager
{
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE=0;
    private static final String PREF_NAME="WYDR";
    private static final String IS_LOGIN="isLoggedIn";
    private static final String STANDERD_SELECTED="standerd_selected";
    private static final String KEY_EMAIL="email";
    private static final String KEY_PROFILE_PIC="pic";
    private static final String KEY_USER_ID="id";
    private static final String KEY_USERNAME="username_p";
    private static final String KEY_PINCODE="pincode_";
    private static final String KEY_ADDRESS="add";
    private static final String KEY_ADDRESS_2="add2";
    private static final String KEY_LANDSTRING="lnd";
    private static final String KEY_CITY="locality";
    private static final String KEY_State="cata";
    private static final String KEY_MOBILE="mob";
    private static final String KEY_UF="scid";
    private static final String KEY_paysta="paysta";
    private static final String KEY_STATUS="stat";
    private static final String KEY_L_flag="flag";

    public PrefManager(Context context)
    {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = preferences.edit();
    }


    public void createLoginSession()
    {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing email in pref
        // editor.putString(KEY_EMAIL, email);
        // commit changes
        editor.commit();
    }



    public void getUFString(String tuf)
    {
        editor.putString(KEY_UF, tuf);
        editor.commit();
    }

    public String putUFString()
    {
        return preferences.getString(KEY_UF, "");
    }

    public void getNetworkString(String t)
    {
        editor.putString(STANDERD_SELECTED, t);
        editor.commit();
    }

    public String putNetworkString()
    {
        return preferences.getString(STANDERD_SELECTED, "");
    }

    public void getNameString(String t1)
    {
        editor.putString(KEY_USERNAME, t1);
        editor.commit();
    }

    public String putNameString()
    {
        return preferences.getString(KEY_USERNAME, "");
    }

    public void getCodeString(String t2)
    {
        editor.putString(KEY_PINCODE, t2);
        editor.commit();
    }

    public String putCodeString()
    {
        return preferences.getString(KEY_PINCODE, "");
    }

    public void getAddString(String t3)
    {
        editor.putString(KEY_ADDRESS, t3);
        editor.commit();
    }

    public String putAddString()
    {
        return preferences.getString(KEY_ADDRESS, "");
    }

    public void getAdd2String(String t4)
    {
        editor.putString(KEY_ADDRESS_2, t4);
        editor.commit();
    }

    public String putAdd2String()
    {
        return preferences.getString(KEY_ADDRESS_2 , "");
    }
    public void getLandString(String t5)
    {
        editor.putString(KEY_LANDSTRING, t5);
        editor.commit();
    }

    public String putLandString()
    {
        return preferences.getString(KEY_LANDSTRING, "");
    }

    public void getCityString(String t6)
    {
        editor.putString(KEY_CITY, t6);
        editor.commit();
    }

    public String putCityString()
    {
        return preferences.getString(KEY_CITY, "");
    }

    public void getStateString(String t7)
    {
        editor.putString(KEY_State, t7);
        editor.commit();
    }

    public String putStateString()
    {
        return preferences.getString(KEY_State, "");
    }

    public void getPhoneString(String t7)
    {
        editor.putString(KEY_MOBILE, t7);
        editor.commit();
    }

    public String putPhoneString()
    {
        return preferences.getString(KEY_MOBILE, "");
    }

    public void getEmailString(String t8)
    {
        editor.putString(KEY_EMAIL, t8);
        editor.commit();
    }

    public String putEmailString()
    {
        return preferences.getString(KEY_EMAIL, "");
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }
}
