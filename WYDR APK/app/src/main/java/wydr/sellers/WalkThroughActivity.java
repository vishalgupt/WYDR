package wydr.sellers;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.appsbonesdk.Appsbone;
import com.android.appsbonesdk.Events;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wydr.sellers.acc.CartSchema;
import wydr.sellers.activities.AppUtil;

import wydr.sellers.activities.ChatProvider;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.Home;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.JSONParser;

public class WalkThroughActivity extends AppCompatActivity
{
    ImageView spalshImage;
    ViewPager viewPager;
    PrefOrder prefOrder;
    Button _btn1,_btn2,_btn3,skip,pre,nex;
    int wt=1;
    Helper helper = new Helper();
    FragmentManager fm;
    JSONObject jsonObject;
    JSONParser jsonParser = new JSONParser();
    int logged_count = 0;
    Fragment fr;
    PrefManager prefManager;
    FragmentManager fragmentManager;
    Controller application;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);
        prefOrder = new PrefOrder(getApplicationContext());
        initViews();
        Appsbone.tagEvent(Events.SIGN_UP,WalkThroughActivity.this);
        prefManager = new PrefManager(getApplicationContext());
        new setAdapterTask().execute();

    }

    private void initViews()
    {
        //spalshImage = (ImageView)findViewById(R.id.splashImage);
        viewPager = (ViewPager)findViewById(R.id.pager);
        fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdapter(fragmentManager));
        setTab();
        initButton();
    }


    private class setAdapterTask extends AsyncTask<String,String,String>
    {
        String id;
        List<NameValuePair> para;

        @Override
        protected String doInBackground(String... strings)
        {
            id = helper.getDefaults("user_id", getApplicationContext());
            jsonObject = jsonParser.getJSONFromUrlGet(AppUtil.URL + "access/" + id + "?osname=android&android_appversion_name=" + helper.getApp_Version(getApplicationContext()), para, getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            /************** If condition added by Istiaque due to crash *****************************/
            if (WalkThroughActivity.this != null && !WalkThroughActivity.this.isFinishing()) {

                if(jsonObject != null)
                {
                    //Log.d("json",jsonObject.toString());
                    ParseJSonUF(jsonObject);
                }

            }
        }
    }

    private void ParseJSonUF(final JSONObject jsonObject)
    {
        try
        {
            String r = "";
            Log.i("HELP_URL", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("privilages");
            for (int i = 0; i < jsonArray.length(); i++)
            {

                JSONObject obj = (JSONObject) jsonArray.get(i);
                String uf = obj.getString("privilage");
                r += uf+",";
                helper.setDefaults(uf+"_photo",obj.getString("image"),getApplicationContext());
                helper.setDefaults(uf+"_tag", obj.getString("url"),getApplicationContext());
            }
            prefManager.getUFString(r);
            Log.d("PArse",prefManager.putUFString());

            String term_condition = jsonObject.getString("term_condition");
            helper.setDefaults("term_condition", term_condition,WalkThroughActivity.this);
            helper.setDefaults("app_help", jsonObject.getString("app_help"), WalkThroughActivity.this);
            helper.setDefaults("help_url", jsonObject.getString("help_url"), WalkThroughActivity.this);

            if(jsonObject.has("cart"))
            {
                try {

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

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }


        try {
            if (jsonObject.getString("status").equalsIgnoreCase("mandetory"))
            {
                final AlertDialog ad = new AlertDialog.Builder(WalkThroughActivity.this)
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
                final AlertDialog ad = new AlertDialog.Builder(WalkThroughActivity.this)
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
                     //   function();
                    }
                });
                ad.show();

            }

            else{
                //function();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void initButton()
    {
        _btn1=(Button)findViewById(R.id.btn1);
        _btn2=(Button)findViewById(R.id.btn2);
        _btn3= (Button)findViewById(R.id.btn3);
        skip = (Button)findViewById(R.id.skip);
        pre = (Button)findViewById(R.id.pre);
        pre.setVisibility(View.GONE);
        nex = (Button)findViewById(R.id.nex);
        nex.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (wt == 1)
                {
                    application = (Controller) WalkThroughActivity.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Introduction", "Move", "WalkThrough1");
                    viewPager.setCurrentItem(1);
                }
                else if (wt == 2)
                {
                    application = (Controller) WalkThroughActivity.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Introduction", "Move", "WalkThrough2");
                    viewPager.setCurrentItem(2);
                }
                else
                {
                    application = (Controller) WalkThroughActivity.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Introduction", "Move", "Home");
                    Intent intent = new Intent(getApplicationContext(),Home.class);
                    startActivity(intent);

                }
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wt == 2)
                {
                    application = (Controller) WalkThroughActivity.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Introduction", "Move", "WalkThrough1");
                    viewPager.setCurrentItem(0);
                }
                else if (wt == 3)
                {
                    application = (Controller) WalkThroughActivity.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Introduction", "Move", "WalkThrough2");
                    viewPager.setCurrentItem(1);
                }

            }
        });


        _btn1.setBackgroundResource(R.drawable.count_indicator_bg);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                application = (Controller) WalkThroughActivity.this.getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("Introduction Skipped", "Move", "Home");
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setTab()
    {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                btnAction(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void btnAction(int position)
    {
        switch (position)
        {
            case 0:
                _btn1.setBackgroundResource(R.drawable.count_indicator_bg);
                _btn2.setBackgroundResource(R.drawable.rounded_cell);
                _btn3.setBackgroundResource(R.drawable.rounded_cell);
                pre.setVisibility(View.GONE);
                nex.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                nex.setText("Next");
                wt = 1;
                break;

            case 1:
                _btn2.setBackgroundResource(R.drawable.count_indicator_bg);
                _btn1.setBackgroundResource(R.drawable.rounded_cell);
                _btn3.setBackgroundResource(R.drawable.rounded_cell);
                pre.setVisibility(View.VISIBLE);
                nex.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                nex.setText("Next");
                wt = 2;
                break;

            case 2:
                _btn3.setBackgroundResource(R.drawable.count_indicator_bg);
                _btn1.setBackgroundResource(R.drawable.rounded_cell);
                _btn2.setBackgroundResource(R.drawable.rounded_cell);
                pre.setVisibility(View.VISIBLE);
                nex.setVisibility(View.VISIBLE);
                nex.setText("Done");
                skip.setVisibility(View.GONE);
                wt = 3;
                break;
        }
    }

    class MyAdapter extends FragmentPagerAdapter
    {

        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i)
        {
            Fragment fragment = null;

            if(i==0)
            {
                fragment = new Walkthrough1();
            }
            if(i==1)
            {
                fragment = new walk_through3();
            }

            if(i==2)
            {
                fragment = new WalkThrough2();
            }
            return fragment;
        }

        @Override
        public int getCount()
        {
            return 3;
        }
    }




}
