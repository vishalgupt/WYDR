package wydr.sellers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 10/19/2015.
 */
public class PrefOrder {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE=0;
    private static final String PREF_NAME="order";
    private static final String KEY_PICKUPdate= "ordxer";
    private static final String KEY_PickupTime= "deli";
    private static final String KEY_SPARE_TYRE= "pay";
    private static final String KEY_JACK= "ex";
    private static final String KEY_Lattitude="x";
    private static final String KEY_Longitude="y";
    private static final String KEY_SANITYCHECK="asap";
    private static final String KEY_SERVICES="final_amount";
    private static final String KEY_OtherProblem="sxcxz";
    private static final String KEY_Fragment="we";
    private static final String KEY_CAR_MODEL="carmodel";
    private static final String KEY_Address_Flag="carmodel123";
    private static final String KEY_IMAGE_1="img1";
    private static final String KEY_IMAGE_2="img2";
    private static final String KEY_IMAGE_3="img3";
    private static final String KEY_IMAGE_4="img4";




    public PrefOrder(Context context)
    {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void getKeyLattitude(String lati)
    {
        editor.putString(KEY_Lattitude,lati);
        editor.apply();
    }

    public String putKeyLattitude()
    {
        return preferences.getString(KEY_Lattitude,null);
    }

    public void getKeyLongitude(String longi)
    {
        editor.putString(KEY_Longitude,longi);
        editor.apply();
    }

    public String putKeyLongitude()
    {
        return preferences.getString(KEY_Longitude,null);
    }

    public void getKeyFrag(String fra)
    {
        editor.putString(KEY_Fragment,fra);
        editor.apply();
    }

    public String putKeyfrag()
    {
        return preferences.getString(KEY_Fragment,null);
    }


    public void getKeyAddressFlag(String fra28)
    {
        editor.putString(KEY_Address_Flag,fra28);
        editor.apply();
    }

    public String putKeyAddressFlag()
    {
        return preferences.getString(KEY_Address_Flag,null);
    }


    public void getKeySANITYCHECK(String ass)
    {
        editor.putString(KEY_SANITYCHECK,ass);
        editor.apply();
    }

    public String putKeySANITYCHECK()
    {
        return preferences.getString(KEY_SANITYCHECK,null);
    }

    public void getKeyServices(String ser)
    {
        editor.putString(KEY_SERVICES,ser);
        editor.apply();
    }

    public String putKeyServices()
    {
        return preferences.getString(KEY_SERVICES,null);
    }

    public void getKeyPickupdate(String pick)
    {
        editor.putString(KEY_PICKUPdate,pick);
        editor.apply();
    }

    public String putKeyPickupdate()
    {
        return preferences.getString(KEY_PICKUPdate,null);
    }

    public void getImage1(String img1)
    {
        editor.putString(KEY_IMAGE_1,img1);
        editor.apply();
    }

    public String putImage1()
    {
        return preferences.getString(KEY_IMAGE_1,"");
    }

    public void getImage2(String img2)
    {
        editor.putString(KEY_IMAGE_2,img2);
        editor.apply();
    }

    public String putImage2()
    {
        return preferences.getString(KEY_IMAGE_2,"");
    }

    public void getImage3(String img3)
    {
        editor.putString(KEY_IMAGE_3,img3);
        editor.apply();
    }

    public String putImage3()
    {
        return preferences.getString(KEY_IMAGE_3,"");
    }


    public void getImage4(String img4)
    {
        editor.putString(KEY_IMAGE_4,img4);
        editor.apply();
    }

    public String putImage4()
    {
        return preferences.getString(KEY_IMAGE_4,"");
    }

    public void getKeyPickuptime(String del)
    {
        editor.putString(KEY_PickupTime,del);
        editor.apply();
    }

    public String putKeyPickUptime()
    {
        return preferences.getString(KEY_PickupTime,null);
    }

    public void getKeySpareTyre (String p)
    {
        editor.putString(KEY_SPARE_TYRE,p);
        editor.apply();
    }

    public String putKeySpareTyre()
    {
        return preferences.getString(KEY_SPARE_TYRE,null);
    }

    public String putKeyJack()
    {
        return preferences.getString(KEY_JACK,null);
    }

    public void getKeyJack(String xe)
    {
        editor.putString(KEY_JACK,xe);
        editor.apply();
    }

    public void getKeyOtherProblems(String oth)
    {
        editor.putString(KEY_OtherProblem,oth);
        editor.apply();
    }

    public String putKeyOtherProblems()
    {
        return preferences.getString(KEY_OtherProblem,null);
    }
    public void getKeyCarModel(String car)
    {
        editor.putString(KEY_CAR_MODEL,car);
        editor.apply();
    }

    public String putKeCarModel()
    {
        return preferences.getString(KEY_CAR_MODEL,null);
    }



    public void prefClear()
    {
        editor.clear();
        editor.commit();
    }


}
