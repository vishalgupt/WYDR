package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.adapter.CartAdapter;
import wydr.sellers.gson.AddOnlineCart;
import wydr.sellers.gson.Products;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;

import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

import wydr.sellers.slider.MyDatabaseHelper;


public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener
{
    ListView cartList;
    CartAdapter adapter;
    Toolbar mToolbar;
    JSONObject json;
    TextView c_Discount;
    int int_discount;
    int shippingAmount = 0;
    TextView amount, shipCharge;
    String shipPrice;
    Button makePayment;
    String catid;
    wydr.sellers.slider.JSONParser jsonParser;
    Boolean e = false;
    int finalAmount = 0;
    MyDatabaseHelper db;
    Helper helper;
    List<NameValuePair> params;
    AlertDialog alertDialog;
    ConnectionDetector cd;
    JSONParser parser;
    ArrayList<String> cod = new ArrayList<>();
    private ProgressDialog progress;
    Tracker mTracker;
    Controller application;
    Snackbar snackbar;
    ArrayList<String> ids;
    RelativeLayout emptyList;
    Button shopNow_btn;
    RelativeLayout deliveryChrg, discountChrg;
    ArrayList<String> catidlist = new ArrayList<>();
    ArrayList<String> catidlistSort = new ArrayList<>();
    //double shippingAmount = 0;
    View footerView;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        helper = new Helper();
        progressStuff();
        if (cd.isConnectingToInternet())
        {
            new Attemptfrieght().execute();
        }

        else
        {
            new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }

        jsonParser = new wydr.sellers.slider.JSONParser();
        db = new MyDatabaseHelper(this);
        int_discount = db.discount();

        /*
        Iterator iterator = catidlist.iterator();
        while (iterator.hasNext())
        {
            catidlistSort.add(String.valueOf(iterator.next()));
        }*/

        Log.d("mylogic", catidlist.toString());
        cod = new ArrayList<String>(Arrays.asList(db.pay_method().split(",")));
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.cart));
        /*progressStuff();*/
        ids = new ArrayList<>();
        application = (Controller) getApplication();
        iniStuff();
        if (cd.isConnectingToInternet()) {
            Cursor mCursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
            if (mCursor.getCount() > 0) {
                new UpdateCart().execute();
                if ((cod.contains("0")) && (cod.contains("1"))) {
                    //Toast.makeText(getApplicationContext(),"Please update your cart",Toast.LENGTH_LONG).show();
                    makePayment.setVisibility(View.GONE);
                }

            }

            else {
                makePayment.setVisibility(View.GONE);
                emptyList.setVisibility(View.VISIBLE);
            }
        }

        else {
            Toast.makeText(CartActivity.this, "Please Check your network connection", Toast.LENGTH_LONG).show();
        }
    }

    public void updateonlineCart()
    {
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.addtocart(addOnlineCart(), helper.getB64Auth(getApplicationContext()), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>()
        {
            @Override
            public void onResponse(Response<JsonElement> response) {
                Log.d("There Cart", "" + response.raw());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Cart failure", "fail");
            }
        });
    }

    private AddOnlineCart addOnlineCart()
    {
        AddOnlineCart a;
        Cursor cursor = getContentResolver().query(ChatProvider.CART_URI, null, null, null, null);
        int iQty = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY);
        int iId = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_ID);
        int ivar = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_VARIENT);
        int ivarname = cursor.getColumnIndexOrThrow(CartSchema.VARIENT_STRING);
        String var_name="";
        HashMap<String, Products> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            if(cursor.getString(ivarname) != null)
            {
                var_name = cursor.getString(ivarname);
            }

            int pos = cursor.getPosition() + 1;
            try
            {
                Log.d("ivar",cursor.getString(ivar));
                if((!cursor.getString(ivar).equalsIgnoreCase("")) && (!cursor.getString(ivar).equalsIgnoreCase("[]"))) {

                JsonParser jsonParser = new JsonParser();
                JsonObject object = (JsonObject)jsonParser.parse(cursor.getString(ivar));
                map.put("" + pos, new Products(cursor.getString(iId), cursor.getString(iQty),object,var_name));
            }

                else
                {
                    //JsonParser jsonParser = new JsonParser();
                    JsonObject object = new JsonObject();
                    map.put("" + pos, new Products(cursor.getString(iId), cursor.getString(iQty),object,var_name));
                }

            }
                catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        a = new AddOnlineCart("cart_status*,wish_list*,checkout*,account_info*", map, helper.getDefaults("user_id", getApplicationContext()));
        Gson g = new Gson();
        String jsonString = g.toJson(a);
        Log.d("JSONCart ", jsonString.toString());
        cursor.close();
        return a;
    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(this);
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        //alertDialog = new android.app.AlertDialog.Builder(this);
    }

    private void iniStuff() {
        params = new ArrayList<NameValuePair>();
        Cursor mCursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        int iId = mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_ID);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            params.add(new BasicNameValuePair("pid[" + mCursor.getPosition() + "]", mCursor.getString(iId)));
            Log.d("Here ", params.toString());
            ids.add(mCursor.getString(iId));
        }

        cartList = (ListView) findViewById(R.id.listViewCart);
        /***************** ISTIAQUE CODE STARTS *************************************/
        // displaying message if there is no items on cart listview
        emptyList = (RelativeLayout) findViewById(R.id.list_empty);
        shopNow_btn = (Button) findViewById(R.id.empty_cart_shop_now_btn);
        shopNow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        /***************** ISTIAQUE CODE ENDS *************************************/
        footerView = ((LayoutInflater) CartActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_cart, null, false);
        shipCharge = (TextView) footerView.findViewById(R.id.txtDelevaryCharge);
        c_Discount = (TextView) footerView.findViewById(R.id.txtDelevaryCharge28);
        /*shipCharge = (TextView) footerView.findViewById(R.id.txtDelevaryCharge);*/
        amount = (TextView) footerView.findViewById(R.id.txtTotalAmount);
        cartList.addFooterView(footerView);
        makePayment = (Button) findViewById(R.id.btnMakePayment);
        makePayment.setOnClickListener(this);
        deliveryChrg = (RelativeLayout) findViewById(R.id.deliveryChrg);
        discountChrg = (RelativeLayout) findViewById(R.id.discountChrg);
    }

    /*public void refreshcursor(Context context) {

        //getSupportLoaderManager().initLoader(2, null,);
        adapter = new CartAdapter(context, null);
        cartList = (ListView) findViewById(R.id.listViewCart);
        cartList.setAdapter(adapter);
        cv
        adapter.notifyDataSetChanged();
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(this, ChatProvider.CART_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        showTotal();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.swapCursor(null);
    }

    public void onItemRemove(View view) {
        String s = (String) view.getTag();
        Log.d("Idd", " " + s);
        int count = getContentResolver().delete(ChatProvider.CART_URI, CartSchema.KEY_ROWID + "=?", new String[]{s});
        getContentResolver().notifyChange(ChatProvider.CART_URI, null);
        Log.d("count", " " + count);
        updateonlineCart();
    }

    private void showTotal()
    {
        catidlistSort.clear();
        catidlist = new ArrayList<String>(Arrays.asList(db.cat_id().split(",")));
        Set<String> temp = new HashSet<String>();
        temp.addAll(catidlist);
        catidlist.clear();
        catidlist.addAll(temp);
        int cat_shipping = 0;
        Log.d("running", "");
        double totalAmount = 0;
        Cursor cursor = getContentResolver().query(ChatProvider.CART_URI, null, null, null, null);
        int iId = cursor.getColumnIndexOrThrow(CartSchema.KEY_ROWID);
        int iName = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_NAME);
        int iCode = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_CODE);
        int iPrice = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_PRICE);
        int iUrl = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_URL);
        int iQty = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY);
        int iFree = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_SHIPPING);
        int iCat = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_CAT_ID);
        int iWieght = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_WEIGHT);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            application.sendDataToTwoTrackers(new HitBuilders.ItemBuilder()
                    .setTransactionId("101")
                    .setName(cursor.getString(iName))
                    .setSku("SKU")
                    .setCategory(cursor.getString(iCode))
                    .setPrice(cursor.getDouble(iPrice))
                    .setQuantity(cursor.getInt(iQty))
                    .setCurrencyCode("INR")
                    .build());

            if (cursor.getString(iPrice) != null)
            {
                totalAmount += cursor.getInt(iQty) * Double.parseDouble(cursor.getString(iPrice));
            }

            if (cursor.getString(iFree).equals("N")) {
                shippingAmount += Math.ceil(Double.parseDouble(String.valueOf(cursor.getInt(iQty))) * Double.parseDouble(cursor.getString(iWieght)));
            }
            //shippingAmount += cursor.getInt(iQty) * Integer.parseInt(cursor.getString(iWieght));
        }

        for (int a = 0; a < catidlist.size(); a++) {
            String id = catidlist.get(a);
            int aki = 0;

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (id.equalsIgnoreCase(cursor.getString(iCat))) {
                    aki += Math.ceil(Double.parseDouble(cursor.getString(iWieght)) * Double.parseDouble(cursor.getString(iQty)));
                }
            }
            Log.d("cartvalue", String.valueOf(aki));
            catidlistSort.add(parseJsonFeedpay(json, String.valueOf(aki)));
        }

        Log.d("shipplus", catidlistSort.toString());
        //shippingAmount += cursor.getInt(iQty) * Integer.parseInt(cursor.getString(iWieght));
        for (int i = 0; i < catidlistSort.size(); i++)
        {
            cat_shipping += Integer.parseInt(catidlistSort.get(i));
        }

        shippingAmount = cat_shipping;
        double payableAmount = totalAmount - db.discount();
        amount.setText("\u20B9 " + String.valueOf((int)(payableAmount + shippingAmount)));

        if (cat_shipping > 0)
        {
            shipCharge.setText("\u20B9 " + cat_shipping);
        }
        else
        {
            deliveryChrg.setVisibility(View.GONE);
        }

        if (db.discount() > 0)
        {
            Log.d("discountincart",""+db.discount());
            c_Discount.setText("\u20B9 " + String.valueOf(db.discount()));
        }

        else
        {
            Log.d("discountincart",""+db.discount());
            discountChrg.setVisibility(View.GONE);
        }


        //shipCharge.setText("\u20B9"+parseJsonFeedpay(json, String.valueOf(db.Shipping())));
        //double payableAmount = totalAmount + shippingAmount;

        double paisa = payableAmount * 100;
        Log.d("Amount here", "" + paisa);
        finalAmount = (int) paisa;
        //finalAmount = (int) Math.ceil(payableAmount);
        Log.d("Amount here", "" + finalAmount);
        cod = new ArrayList<String>(Arrays.asList(db.pay_method().split(",")));

        if ((cod.contains("0")) && (cod.contains("1"))) {
            //Toast.makeText(getApplicationContext(),"COD is detected in one of your product.Please update your cart",Toast.LENGTH_LONG).show();
            makePayment.setVisibility(View.GONE);
            snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "COD is detected in one of your product.Please update your cart", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                            e = true;
                        }
                    });
            snackbar.show();
        }
        else {
            makePayment.setVisibility(View.VISIBLE);
            if (e) {
                snackbar.dismiss();
            }
        }
        /**************** ISTIAQUE CODE STARTS *************************************/
        // total items on cart
        int totalItemsOnCart = cursor.getCount();
        //action bar displaying number of items on cart
        getSupportActionBar().setTitle(getString(R.string.cart) + " (" + totalItemsOnCart + ")");

        // getting number of items on cart listview
        if (totalItemsOnCart == 0) {
            makePayment.setVisibility(View.GONE);
            cartList.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }

        /**************** ISTIAQUE CODE ENDS *************************************/
    }

    @Override
    public void onClick(View v)
    {
        Cursor cursor = getContentResolver().query(ChatProvider.CART_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            helper.setDefaults("dis", parseJsonFeedpay(json, String.valueOf(db.Shipping())), getApplicationContext());
           // Toast.makeText(getApplicationContext(),adapter.couponList().toString(),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Checkout.class).putExtra("amount", "" + ((finalAmount/100)+db.discount())).putExtra("couponlist",adapter.couponList()).putExtra("ship", "" + shippingAmount));

            updateonlineCart();
            // updateonlineCart();
        } else {

            new AlertDialogManager().showAlertDialog(this, getString(R.string.empty_cart), getString(R.string.add_items_to_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UpdateCart extends AsyncTask<String, String, String>
    {
        String deletedProducts = "";
        ArrayList<String> delIds = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected String doInBackground(String... args) {
            JSONObject json = null;
            params.add(new BasicNameValuePair("items_per_page", "100"));
            params.add(new BasicNameValuePair("current_user_id", helper.getDefaults("user_id", getApplicationContext())));
            Log.d("param", params.toString());
            json = parser.makeHttpRequest(AppUtil.URL + "3.0/products", "GET", params, CartActivity.this);
            Log.d("update url",AppUtil.URL + "3.0/products&item_per_page=100&current_user_id=");
            //json = parser.makeHttpRequest(AppUtil.URL + "3.0/products?current_user_id="+id+"&", "GET", params, CartActivity.this);
            if (json != null)
            {
                Log.d("JSS", json.toString());
                if (json.has("products"))
                {
                    try
                    {
                        JSONArray array = json.getJSONArray("products");
                        Log.d("cartproducts",array.toString());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int amount = obj.getInt("amount");
                            int moq = obj.getInt("min_qty");
                            String product_id = obj.getString("product_id");
                            String productName = obj.getString("product");
                            ids.remove(product_id);
                            Log.d("MOQ", "" + moq);
                            if (amount > 0 && amount >= moq) {
                                // String product_id = obj.getString("product_id");
                                if (obj.getString("status").equals("A")) {
                                    String price = obj.getString("base_price");
                                    //String price = obj.getString("price");
                                    ContentValues cv = new ContentValues();
                                    cv.put(CartSchema.PRODUCT_MOQ, moq);
                                    cv.put(CartSchema.PRODUCT_PRICE, price);
                                    //cv.put(CartSchema.PRODUCT_INVENTORY, amount);
                                    //cv.put(CartSchema.PRODUCT_QTY, moq);
                                    cv.put(CartSchema.PRODUCT_SHIPPING, obj.optString("free_shipping"));
                                    //cv.put(CartSchema.PRODUCT_SHIPPING_CHARGES, obj.optString("shipping_freight"));
                                    cv.put(CartSchema.PRODUCT_SHIPPING_CHARGES, obj.optString("shipPrice"));
                                    cv.put(CartSchema.PRODUCT_WEIGHT, obj.optString("productWeight"));
                                    //cv.put(CartSchema.DISCOUNT,obj.optString(""));
                                    //Log.d("CV",cv.toString());
                                    getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.PRODUCT_ID + "=?", new String[]{product_id});
                                }

                                else
                                {
                                    getContentResolver().delete(ChatProvider.CART_URI, CartSchema.PRODUCT_ID + "=?", new String[]{product_id});
                                }

                            } else {
                                delIds.add(product_id);
                                deletedProducts = deletedProducts + productName + "( " + product_id + " ),";

                            }
                        }
                        if (ids.size() > 0) {
                            for (int k = 0; k < ids.size(); k++) {

//                                    int intValue = test[i];
                                // do some work here on intValue
                                getContentResolver().delete(ChatProvider.CART_URI, CartSchema.PRODUCT_ID + "=?", new String[]{ids.get(k)});
                                // }
                            }
                            // do some work here on intValue
                            // }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                /*********************** ISTIAQUE ***************************/
                if (json.has("401")) {
                    session.logoutUser();
                }
                /*********************** ISTIAQUE ***************************/

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (CartActivity.this != null && !CartActivity.this.isFinishing()) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }

                if (delIds.size() > 0) {
                    if (deletedProducts.contains(","))
                        deletedProducts = deletedProducts.substring(0, deletedProducts.length() - 1);
                    new AlertDialogManager().showAlertDialog(CartActivity.this, getString(R.string.sorry), deletedProducts + " is " + getString(R.string.out_of_stock));
                    for (int i = 0; i < delIds.size(); i++) {
                        getContentResolver().delete(ChatProvider.CART_URI, CartSchema.PRODUCT_ID + "=?", new String[]{delIds.get(i)});
                    }
                }

                Cursor cursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.KEY_ROWID}, null, null, null);
                if (cursor.getCount() == 0) {
                    cartList.removeFooterView(footerView);
                } else {
                    if (cartList.getFooterViewsCount() == 0)
                        cartList.addFooterView(footerView);
                }
                cursor.close();


                getSupportLoaderManager().initLoader(2, null, CartActivity.this);
                adapter = new CartAdapter(CartActivity.this, null);
                cartList.setAdapter(adapter);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Controller application = (Controller) getApplication();
        mTracker = application.getDefaultTracker();
        Log.i("TAG", "Setting screen name: " + "Cart ");
        mTracker.setScreenName("Cart");
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    class Attemptfrieght extends AsyncTask<String, String, String> {
        List<NameValuePair> para;

        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                json = jsonParser.getJSONFromUrlGet(AppUtil.URL + "shippingrange",para, getApplicationContext());
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            updateonlineCart();
        }
    }



    private String parseJsonFeedpay(JSONObject jsonpay, String a) {
        String x = "0";
        try {
            JSONArray array = jsonpay.getJSONArray("ranges");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                String range1 = obj.getString("range1");
                String range2 = obj.getString("range2");
                if (Integer.parseInt(range1) < Integer.parseInt(a) && Integer.parseInt(range2) >= Integer.parseInt(a)) {
                    x = obj.getString("price");
                } else if (Integer.parseInt(range2) < Integer.parseInt(a)) {
                    x = String.valueOf(Integer.parseInt(obj.getString("price")) + 30);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return x;
    }
}
