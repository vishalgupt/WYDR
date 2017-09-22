package wydr.sellers.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.InflateException;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.Notification.NotificationUtils;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.holder.DataHolder;
import wydr.sellers.network.ConnectionDetector;

import wydr.sellers.registration.Helper;
import wydr.sellers.registration.Login;
import wydr.sellers.slider.JSONParser;
import wydr.sellers.slider.MyContentProvider;


public class Splash extends AppCompatActivity
{
    ImageView spalshImage;
    ViewPager viewPager;
    Button _btn1,_btn2,_btn3;
    int logged_count = 0;
    PrefManager prefManager;
    Helper helper;
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;
    ConnectionDetector connectionDetector;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefManager = new PrefManager(getApplicationContext());
        connectionDetector = new ConnectionDetector(getApplicationContext());
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Loggedin, null, null, null, null);
        logged_count = cursor.getCount();
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        //Toast.makeText(getApplicationContext(),telephonyManager.getDeviceId().toString(),Toast.LENGTH_SHORT).show();
        helper = new Helper();
        getContentResolver().delete(ChatProvider.CART_URI, null, null);
        initViews();


    }


    @Override
    protected void onResume()
    {
        NotificationUtils.clearNotifications();
        super.onResume();
        if(connectionDetector.isConnectingToInternet())
        {
            if (helper.getDefaults("user_id", getApplicationContext()) != null)
            {
                new setAdapterTask().execute();
            }
            else {
                initThread();
            }
        }
        else
        {
            Snackbar.make(findViewById(android.R.id.content),"Please check your internet connection !!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void initViews()
    {
        spalshImage = (ImageView)findViewById(R.id.splashImage);
    }


    private class setAdapterTask extends AsyncTask<String,String,String>
    {
        String id;
        JSONObject jsonObject;
        List<NameValuePair> para;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings)
        {
            id = helper.getDefaults("user_id", getApplicationContext());

            jsonObject = jsonParser.getJSONFromUrlGet(AppUtil.URL + "access/" + id + "?osname=android&android_appversion_name=" + helper.getApp_Version(getApplicationContext()), para, Splash.this);
            //Log.d("JSONing" , jsonObject.getJSONObject("cart").getJSONArray("productsdata").toString());
           /* }

            catch (JSONException | NullPointerException e)
            {
                Log.d("Exception: ", e.getMessage());
                e.printStackTrace();
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
//           Log.d("ufff", jsonObject.toString());

            if (jsonObject != null)
            {
                ParseJSonUF(jsonObject);
            }
        }
    }

    private void ParseJSonUF(final JSONObject jsonObject)
    {
        try
        {
            String r = "";
            JSONArray jsonArray = jsonObject.getJSONArray("privilages");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                String uf = obj.getString("privilage");
                r += uf+",";
                helper.setDefaults(uf+"_photo",obj.getString("image"),getApplicationContext());
                helper.setDefaults(uf+"_tag", obj.getString("url"),getApplicationContext());
            }

            String term_condition = jsonObject.getString("term_condition");
            helper.setDefaults("term_condition", term_condition,Splash.this);
            helper.setDefaults("app_help", jsonObject.getString("app_help"), Splash.this);
            helper.setDefaults("help_url", jsonObject.getString("help_url"), Splash.this);

            if(jsonObject.has("cart"))
            {
                getContentResolver().delete(ChatProvider.CART_URI, null, null);
                getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);

                try
                {
                    JSONArray jsonArray1 = jsonObject.getJSONObject("cart").getJSONArray("productsdata");
                    Log.d("aki_array",jsonArray1.toString());
                    for (int j = 0; j < jsonArray1.length(); j++)
                    {
                        JSONObject obj = (JSONObject) jsonArray1.get(j);
                        String var = "";
                        String promoarray = "0";
                        String promoarrayapplied = "0";
                        String id = obj.getString("product_id");
                        String name = obj.getString("product_name");
                        String code = obj.getString("product_code");
                        String weight = obj.getString("productWeight");
                        String cat_id = obj.getString("main_category");
                        String qty = obj.getString("qty");

                        if(obj.has("selected_varients"))
                        {
                            var = obj.getString("selected_varients");
                        }

                        String image = obj.getString("image");
                        String cod_available = obj.getString("cod_available");
                        String s = obj.getString("status");
                        String moq = obj.getString("product_moq");
                        String freeShipping = obj.getString("product_moq");
                        String productInventory = obj.getString("product_inventory");
                        String productPrice = obj.getString("product_price");
                        String productvar="";
                        if(obj.has("product_options"))
                        {
                            productvar = obj.getString("product_options");
                        }
                        String productPriceDiscount = obj.getString("discount_price");

                        if(obj.has("cartpromotions"))
                        {
                            JSONArray jsonArray28 = obj.getJSONArray("cartpromotions");
                            Log.d("cartpromo",jsonArray28.toString());
                            promoarray = jsonArray28.toString();
                        }

                        if (obj.has("appliedpromo"))
                        {
                            JSONObject jsonArray29 = obj.getJSONObject("appliedpromo");
                            Log.d("Appliedpromo",jsonArray29.toString());
                            promoarrayapplied = jsonArray29.toString();
                        }

                        ContentValues cv = new ContentValues();
                        cv.put(CartSchema.PRODUCT_ID, id);
                        cv.put(CartSchema.PRODUCT_NAME, name);
                        cv.put(CartSchema.PAY_METHOD, cod_available);
                        cv.put(CartSchema.PRODUCT_CODE, code);
                        cv.put(CartSchema.PRODUCT_STATUS, s);
                        cv.put(CartSchema.PRODUCT_QTY, qty);
                        cv.put(CartSchema.PRODUCT_URL, image);
                        cv.put(CartSchema.PRODUCT_WEIGHT, weight);
                        cv.put(CartSchema.PRODUCT_CAT_ID,cat_id);
                        cv.put(CartSchema.DISCOUNT, "0");
                        cv.put(CartSchema.PRODUCT_MOQ, moq);
                        cv.put(CartSchema.PRODUCT_SHIPPING, freeShipping);
                        cv.put(CartSchema.PRODUCT_INVENTORY, productInventory);
                        cv.put(CartSchema.PRODUCT_PRICE,productPrice);
                        cv.put(CartSchema.CART_PROMO,promoarray);
                        cv.put(CartSchema.CART_PROMO_APPLIED,promoarrayapplied);
                        cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE,productPriceDiscount);
                        cv.put(CartSchema.PRODUCT_VARIENT,productvar);
                        cv.put(CartSchema.PROMO_MIN_QTY,"0");
                        cv.put(CartSchema.PROMO_MAX_AMT,"0");
                        cv.put(CartSchema.VARIENT_STRING,var);

                        Uri uri = getContentResolver().insert(ChatProvider.CART_URI, cv);
                        Log.d("Is three ", "IN " + uri.toString());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                getContentResolver().delete(ChatProvider.CART_URI, null, null);
                getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
            }

            prefManager.getUFString(r);
            Log.d("PArse",prefManager.putUFString());


        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try {
            if (jsonObject.getString("status").equalsIgnoreCase("mandetory"))
            {
                final AlertDialog ad = new AlertDialog.Builder(this)
                        .create();
                ad.setCancelable(false);
                ad.setTitle(jsonObject.getString("title"));
                ad.setMessage(jsonObject.getString("message"));
                ad.setButton(DialogInterface.BUTTON_POSITIVE , "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = null;
                        try {
                            url = jsonObject.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        ad.dismiss();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                        ad.dismiss();
                    }
                });
                ad.show();


            }

            else if(jsonObject.getString("status").equalsIgnoreCase("features"))
            {
                final AlertDialog ad = new AlertDialog.Builder(this)
                        .create();
                ad.setCancelable(false);
                ad.setTitle(jsonObject.getString("title"));
                ad.setMessage(jsonObject.getString("message"));
                ad.setButton(DialogInterface.BUTTON_POSITIVE , "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = null;
                        try {
                            url = jsonObject.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        ad.dismiss();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "Update Later", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ad.dismiss();
                        function();
                    }
                });
                ad.show();

            }

            else{
                function();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void initThread()
    {
        Thread background = new Thread()
        {
            public void run() {

                // Thread will sleep for 5 seconds
                try
                {
                    sleep(2 * 1000);


                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            function();

                            //viewPager.setVisibility(View.VISIBLE);
                            //spalshImage.setVisibility(View.GONE);
                        }
                    });
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        background.start();
    }

    public void function()
    {
        //Manoj:1

        DataHolder.getInstance().setList(getDataList());

        //Manoj:0
        if (logged_count > 0)
        {
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();

        }
    }

    //Manoj: Code

    public ArrayList<JSONObject> getDataList() {
        String phoneNumber;

        ArrayList<JSONObject> objects=new ArrayList<>();

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?"
                            , new String[]{contact_id}, DISPLAY_NAME);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        String string = phoneNumber.replaceAll("[^\\d]", "");
                        // String where2 = childJSONObject.getString("phone_number");
                        if (string.length() > 10)
                            string = string.substring(string.length() - 10);
                        JSONObject object = new JSONObject();
                        if (ValidationUtil.isValidMobileNumber(string)) {
                            if (string.length() > 2) {
                                try {
                                    object.put("name", helper.ConvertCamel(name));
                                    object.put("phone", Long.parseLong(string.trim()));
                                    objects.add(object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        return objects;
    }

    //Manoj: code end


}
