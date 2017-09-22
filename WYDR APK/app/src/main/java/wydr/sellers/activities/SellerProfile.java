package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by Deepesh_pc on 24-09-2015.
 */
public class SellerProfile extends AppCompatActivity implements View.OnClickListener
{
    public Boolean fav_flag = false;
    ConnectionDetector cd;
    AlertDialog.Builder alertDialog;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    Calendar c = Calendar.getInstance();
    Helper helper = new Helper();
    private String companyid, username, userid, seller_id, user_id, phone, company, name, backflag, curr_user_id;
    private String is_openFire;
    private TextView user_name, user_company, user_address, user_city;
    private ImageView user_pic, like_icon, chat_icon;
    private Button view_products, view_queries;
    private ProgressDialog progress;
    private ListLoader imageLoader;
    private Uri uri = null;
    private String product_id, title, search_string;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        curr_user_id = helper.getDefaults("user_id", SellerProfile.this);
        companyid = getIntent().getStringExtra("company_id");
        username = getIntent().getStringExtra("username");
        backflag = getIntent().getStringExtra("BackFlag");
        if (getIntent().getStringExtra("user_id") != null)
        {
            user_id = getIntent().getStringExtra("user_id");
        }
        if (getIntent().getStringExtra("userid") != null)
        {
            userid = getIntent().getStringExtra("userid");
        }
        if (getIntent().getStringExtra("product_id") != null)
        {
            product_id = getIntent().getStringExtra("product_id");
        }
        if (getIntent().getStringExtra("title") != null)
        {
            title = getIntent().getStringExtra("title");
        }
        if (getIntent().getStringExtra("search_string") != null) {
            search_string = getIntent().getStringExtra("search_string");
        }

        tooltitle.setText(helper.ConvertCamel(username) + "'s Profile");
        inistuff();
        progressStuff();
        BringDetails();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (userid != null) {
            if (userid.equalsIgnoreCase(helper.getDefaults("user_id", SellerProfile.this))) {
                like_icon.setVisibility(View.GONE);
                chat_icon.setVisibility(View.GONE);
                //    call_icon.setVisibility(View.GONE);

            }
        }

        like_icon.setOnClickListener(this);
        chat_icon.setOnClickListener(this);
        // call_icon.setOnClickListener(this);
        view_products.setOnClickListener(this);
        view_queries.setOnClickListener(this);
    }

    private void BringDetails()
    {
        if (cd.isConnectingToInternet()) {
            new GetSellerDetails().execute();
        } else {
            new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();

    }

    @Override
    protected void onDestroy() {
        companyid = "";
        username = "";
        userid = "";
        backflag = "";
        product_id = "";
        title = "";
        search_string = "";

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (backflag.equalsIgnoreCase("AllSellers")) {
            if (cd.isConnectingToInternet()) {
                startActivity(new Intent(SellerProfile.this, AllSellers.class));
                finish();
            } else {
                new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));
            }

        } else if (backflag.equalsIgnoreCase("product")) {
            Log.i("BACKFLAG-", "product/" + product_id + title);
            if (cd.isConnectingToInternet()) {

                Intent intent = new Intent(SellerProfile.this, ProductDetailsActivity.class);
                intent.putExtra("product_id", product_id);
                intent.putExtra("name", title);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));
            }

        } else if (backflag.equalsIgnoreCase("SearchSellers")) {
            if (cd.isConnectingToInternet()) {
                startActivity(new Intent(SellerProfile.this, SearchSellers.class).putExtra("search_string", search_string));
                finish();
            }

            else {
                new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));
            }
        }

        else {
            Log.i("BACKFLAG-", "else/" + product_id + title);
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            // startActivity(new Intent(SellersCatalog.this, Home.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                /*startActivity(new Intent(SellersQuery.this, SellerProfile.class).putExtra("username", username).putExtra("company_id", companyid).putExtra("userid", userid));
                finish();*/
                onBackPressed();
                Log.e("1", "22");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inistuff() {

        user_name = (TextView) findViewById(R.id.user_name);
        //user_email = (TextView) findViewById(R.id.user_email);
        //user_phone = (TextView) findViewById(R.id.user_contact);
        user_company = (TextView) findViewById(R.id.txtcomp);
        user_address = (TextView) findViewById(R.id.txtadd);
        user_city = (TextView) findViewById(R.id.txtcounty);
        view_products = (Button) findViewById(R.id.btnviewprod);
        view_queries = (Button) findViewById(R.id.btnviewdet);
        user_pic = (ImageView) findViewById(R.id.contacticon);
        like_icon = (ImageView) findViewById(R.id.like_icon);
        chat_icon = (ImageView) findViewById(R.id.chat_icon);
        //    call_icon = (ImageView) findViewById(R.id.call_icon);
        cd = new ConnectionDetector(getApplicationContext());
        imageLoader = new ListLoader(SellerProfile.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.like_icon:
                if (cd.isConnectingToInternet()) {
                    if (fav_flag) {
                        new LikeQuery().execute(seller_id, "1");

                        fav_flag = false;

                    } else {
                        new LikeQuery().execute(seller_id, "0");

                        fav_flag = true;
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));

                }

                break;
            case R.id.chat_icon:

                if (is_openFire.equalsIgnoreCase("1")) {
                    Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{userid}, null);
                    if (cursor.getCount() > 0) {

                        startActivity(new Intent(SellerProfile.this, ChatActivity.class).putExtra("user", "" + user_id + "@" + AppUtil.SERVER_NAME));

                    } else {
                        Log.i("SELLER_PROFILE", phone.trim().substring(phone.length() - 10));
                        ContentValues cv = new ContentValues();
                        cv.put(NetSchema.USER_PHONE, phone.trim().substring(phone.length() - 10));
                        cv.put(NetSchema.USER_COMPANY, company);
                        cv.put(NetSchema.USER_DISPLAY, " ");
                        cv.put(NetSchema.USER_COMPANY_ID, companyid);
                        cv.put(NetSchema.USER_ID, userid);
                        user_id = user_id + "@" + AppUtil.SERVER_NAME;
                        cv.put(NetSchema.USER_NET_ID, user_id);
                        cv.put(NetSchema.USER_STATUS, "0");
                        cv.put(NetSchema.USER_NAME, helper.ConvertCamel(name));
                        cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));

                        uri = getContentResolver().insert(ChatProvider.NET_URI, cv);
                        if (uri != null)
                            startActivity(new Intent(SellerProfile.this, ChatActivity.class).putExtra("user", "" + user_id));
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.user_not_exist));
                }


                break;

            case R.id.btnviewprod:

                if (cd.isConnectingToInternet()) {

                    startActivity(new Intent(SellerProfile.this, SellersCatalog.class).
                            putExtra("username", username).
                            putExtra("company_id", companyid).
                            putExtra("userid", userid).
                            putExtra("seller_id", seller_id).
                            putExtra("user_id", user_id).
                            putExtra("product_id", product_id).
                            putExtra("title", title).
                            putExtra("BackFlag", "SellerProfile" + "/" + backflag));
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                }


                break;
            case R.id.btnviewdet:
                if (cd.isConnectingToInternet()) {
                    Log.i("userid", userid);
                    Log.i("user_id", user_id);
                    startActivity(new Intent(SellerProfile.this, SellersQuery.class).


                            putExtra("seller_id", seller_id).
                            putExtra("username", username).
                            putExtra("company_id", companyid).
                            putExtra("userid", userid).
                            putExtra("user_id", user_id).
                            putExtra("product_id", product_id).
                            putExtra("title", title).
                            putExtra("BackFlag", "SellerProfile" + "/" + backflag));
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(SellerProfile.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                }

                break;
        }

    }

    private void progressStuff() {
        // TODO Auto-generated method stub

        /*cd = new ConnectionDetector(getApplicationContext());*/

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(this);

    }

    private class LikeQuery extends AsyncTask<String, String, JSONObject> {

        public String error = "";
        public int flag = 0;
        JSONObject table = new JSONObject();
        Boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing())
                progress.show();
        }


        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            try {

                if (args[1] == "1")
                    table.put("unlike", "1");
                table.put("user_id", curr_user_id);
                table.put("object_id", args[0]);
                table.put("object_type", "vendor");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = userFunction.LikeRequest("POST", table, "seller", SellerProfile.this);
            if (json != null) {
                Log.e("JSON--", json.toString());
                try {
                    if (json.has("message")) {
                        if (json.get("message").toString().equalsIgnoreCase("_your favourite item added successfully"))
                            success = true;
                    } else if (json.has("error")) {
                        if (json.get("error").toString().equalsIgnoreCase("NO CONTENT"))
                            success = true;
                        else
                            flag = 2;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                flag = 1;

            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!isFinishing()) {
                progress.dismiss();
                if (flag == 1) {

                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (flag == 2) {
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    alertDialog.setMessage(getResources().getString(R.string.page_not_found));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else {
                    if (success) {
                        if (fav_flag) {
                            like_icon.setImageResource(R.drawable.like_selected);
                            Toast.makeText(SellerProfile.this, getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                        } else {
                            like_icon.setImageResource(R.drawable.like_unselected_white);
                            Toast.makeText(SellerProfile.this, getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                        }


                    }

                }
            }


        }
    }

    private class GetSellerDetails extends AsyncTask<String, String, JSONObject> {

        public String error = "", address, city, favflag, imagepath, pincode;

        public int flag = 0, pos;
        JSONObject table = new JSONObject();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing())
                progress.show();
        }


        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;

            json = userFunction.GetSellerDetails(curr_user_id, userid, SellerProfile.this);
            if (json != null) {

                if (json.has("error")) {
                    flag = 2;
                } else {
                    name = json.optString("firstname");
                    name = name + " " + json.optString("lastname");
                    is_openFire = json.optString("is_openfire");
                    phone = json.optString("phone");
                    company = json.optString("company_name");
                    address = json.optString("address");
                    city = json.optString("city");
                    city = city + "," + json.optString("state");
                    address = json.optString("address");
                    seller_id = json.optString("user_id");
                    user_id = json.optString("user_login");
                    favflag = json.optString("is_favourite");
                    if (json.has("main_pair")) {
                        imagepath = json.optJSONObject("main_pair").optJSONObject("icon").optString("image_path");
                    }

                    pincode = json.optString("zipcode");

                }
            } else {
                flag = 1;
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!isFinishing()) {
                progress.dismiss();
                if (flag == 1) {
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    alertDialog.show();


                }
                if (flag == 2) {
                    if (user_id != null && !user_id.equalsIgnoreCase("")) {

                    } else {
                        view_products.setEnabled(false);
                        view_queries.setEnabled(false);
                        like_icon.setEnabled(false);
                        chat_icon.setEnabled(false);
                    }
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    alertDialog.setMessage(getResources().getString(R.string.inactive_user));

                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    alertDialog.show();


                } else {
                    if (user_id != null && !user_id.equalsIgnoreCase("")) {

                    } else {
                        view_products.setEnabled(false);
                        view_queries.setEnabled(false);
                        like_icon.setEnabled(false);
                        chat_icon.setEnabled(false);
                    }
                    if (!ValidationUtil.isNull(user_id)) {
                        ContentValues cv = new ContentValues();
                        cv.put(NetSchema.USER_IMAGE, imagepath);
                        int count = getContentResolver().update(ChatProvider.NET_URI, cv, NetSchema.USER_NET_ID + "=?", new String[]{user_id + "@" + AppUtil.SERVER_NAME});
                        Log.d("Count", "Count Success " + count + "  " + user_id);
                    }

                    if (name != null)
                        user_name.setText(helper.ConvertCamel(name));
                    else
                        user_name.setText("");

                    if (company != null)
                        user_company.setText(company.toUpperCase());
                    else
                        user_company.setText("");

                    if (address != null)
                        user_address.setText(helper.ConvertCamel(address));
                    else
                        user_address.setText("");

                    if (city != null)
                        user_city.setText(helper.ConvertCamel(city));
                    else
                        user_city.setText("");


                    fav_flag = favflag.equalsIgnoreCase("1");
                    if (imagepath != null) {

                        imageLoader.DisplayImage(imagepath, user_pic, R.drawable.avtar);

                    }
                    if (fav_flag)
                        like_icon.setImageResource(R.drawable.like_selected);
                    else
                        like_icon.setImageResource(R.drawable.like_unselected_white);


                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SellerProfile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setNotifCount() {
        Cursor mCursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null) {
            if (mNotifCount == 0) {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            } else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.search_cart_menu, menu);
        MenuItem item = menu.findItem(R.id.searchCart_cart);
        MenuItemCompat.setActionView(item, R.layout.cart_count);
        notifCount = (FrameLayout) MenuItemCompat.getActionView(item);
        if (mNotifCount == 0) {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
        } else {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);

            TextView textView = (TextView) notifCount.findViewById(R.id.count);
            textView.setText(String.valueOf(mNotifCount));

        }
        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");

                startActivity(new Intent(SellerProfile.this, CartActivity.class));
            }
        });
        MenuItem sitem = menu.findItem(R.id.searchCart_search);
        sitem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
}