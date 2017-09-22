package wydr.sellers.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.R;
import wydr.sellers.adapter.OrderReviewAdapter;
import wydr.sellers.gson.OrderStatus;
import wydr.sellers.modal.OrderReviewObjects;
import wydr.sellers.modal.ProductOrderedDetails;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.network.Transactions;
import wydr.sellers.registration.Helper;

/**
 * Created by Ishtiyaq on 4/12/2016.
 */

public class OrderReview extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView orderReview_recycler_view;
    TextView user_selected_paymentMode, totalMargin, subTotal, discountedAmount, wydrCash, shippingCost, netPayableAmount;
    Button paynow;
    OrderReviewAdapter adapter;
    Controller application;
    Tracker mTracker;
    private ProgressDialog progress;
    ConnectionDetector cd;
    String payableAmount = "", emailString = "", phoneString = "", newOrderId = "";
    Helper helper;
    ArrayList<ProductOrderedDetails> productOrderedDetailsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_review);



        // Getting reference to views
        getViews();

        // Getting data from JSON
        getJsonDataFromSharedPreference();

        // on Pay now button click
        setOnClickListener();

    }



    private void setOnClickListener() {

        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPayment(payableAmount);
            }
        });
    }



    public void startPayment(String amount)
    {
        /**
         * Replace with your public key
         */
        //   final String public_key = "rzp_test_gtvNN3wTstphpz";
        /**
         * You need to pass current activity in order to let razorpay create CheckoutActivity
         */
        final Activity activity = this;
        final com.razorpay.Checkout co = new com.razorpay.Checkout();
        co.setPublicKey(AppUtil.public_key_test);
        try
        {
            JSONObject options = new JSONObject("{" +
                    "description: ''," +
                    "image: 'https://rzp-mobile.s3.amazonaws.com/images/rzp.png'," +
                    "currency: 'INR'}"
            );

            options.put("amount", amount);
            options.put("name", "WYDR");
            options.put("prefill", new JSONObject("{email: '" + emailString + "', contact: '" + phoneString + "'}"));
            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void onPaymentSuccess(String razorpayPaymentID)
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "P");
            JSONObject object = new JSONObject();
            object.put("transaction_id", razorpayPaymentID);
            jsonObject.put("payment_info", object);
            jsonObject.put("payment_id", AppUtil.payment_id);
            getContentResolver().delete(ChatProvider.CART_URI, null, null);
            getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
            setContentView(R.layout.activity_thank);
            updateOrders(new OrderStatus("P", AppUtil.PAYMENT_ID, new Transactions(razorpayPaymentID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //   Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }

    public void onPaymentError(int code, String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "F");
            getContentResolver().delete(ChatProvider.CART_URI, null, null);
            getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
            setContentView(R.layout.activity_thank);
            updateOrders(new OrderStatus("F", AppUtil.PAYMENT_ID,new Transactions("")));
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }



    private void updateOrders(final OrderStatus status)
    {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.updateOrder(newOrderId, status, helper.getB64Auth(OrderReview.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onFailure(Throwable t) {
                if (OrderReview.this != null && !OrderReview.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(OrderReview.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response)
            {
                Log.d("There", "" + response.raw());
                progress.dismiss();
                if (response.isSuccess())
                {
                    startActivity(new Intent(OrderReview.this, ThankActivity.class).putExtra("order_id", newOrderId).putExtra("status", status.getStatus())
                            .putExtra("productOrderedDetailsList", productOrderedDetailsList));
                    newOrderId = null;
                    finish();
                }

                else
                {
                    int statusCode = response.code();
                    //}
                    if (statusCode == 401)
                    {
                        final SessionManager sessionManager = new SessionManager(OrderReview.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }
                    // if(error2.equalsIgnoreCase(""))
                    String error2 = getString(R.string.server_error);
                    if (statusCode == 404) {
                        //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                        new AlertDialogManager().showAlertDialog(OrderReview.this, getString(R.string.error), error2);
                    } else if (statusCode == 500) {
                        new AlertDialogManager().showAlertDialog(OrderReview.this, getString(R.string.error), error2);
                        //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        new AlertDialogManager().showAlertDialog(OrderReview.this, getString(R.string.error), error2);
                        // Toast.makeText(AddressList.this, , Toast.AddAddress).show();
                    }
                }
            }
        });
    }



    private void getJsonDataFromSharedPreference() {

        // shared preference
        JSONObject rootJSON = new JSONObject();

        if (rootJSON != null) {

            if (rootJSON.has("cart")) {
                try {

                    JSONObject cartJSON = rootJSON.getJSONObject("cart");

                    JSONArray productsDataJSON = cartJSON.getJSONArray("productsdata");

                    ArrayList<OrderReviewObjects> orderReviewObjectsList = new ArrayList<OrderReviewObjects>();
                    for (int i = 0; i < productsDataJSON.length(); i++) {

                        JSONObject jsonObject = productsDataJSON.getJSONObject(i);
                        OrderReviewObjects orderReviewObjects = new OrderReviewObjects();

                        if (jsonObject.has("image")) {

                            orderReviewObjects.setProductImg(jsonObject.getString("image"));
                        }

                        if (jsonObject.has("product_name")) {

                            orderReviewObjects.setProductTitle(jsonObject.getString("product_name"));
                        }

                        if (jsonObject.has("qty")) {

                            orderReviewObjects.setProductQty(jsonObject.getString("qty"));
                        }

                        if (jsonObject.has("product_price")) {

                            orderReviewObjects.setOriginalPrice(jsonObject.getString("product_price"));
                        }

                        if (jsonObject.has("discount_price")) {

                            orderReviewObjects.setDisPrice(jsonObject.getString("discount_price"));
                        }

                        if (jsonObject.has("appliedpromo")) {

                            JSONObject couponsJSON = jsonObject.getJSONObject("appliedpromo");
                            orderReviewObjects.setCouponCode(jsonObject.getString(couponsJSON.getString("coupons")));
                            orderReviewObjects.setCouponCodeStatus(jsonObject.getString("Applied"));
                        }


                        if (jsonObject.has("promotions")) {

                            JSONObject promotionsJSON = jsonObject.getJSONObject("promotions");
                            Iterator<String> iter = promotionsJSON.keys();

                            // get all promotions keys from json
                            while (iter.hasNext()) {

                                String key = iter.next();
                                JSONArray bonusesJSON = promotionsJSON.getJSONObject(key).getJSONArray("bonuses");
                                for (int j = 0; j < bonusesJSON.length(); j++) {

                                    JSONObject discountUnitJSON = bonusesJSON.getJSONObject(j);

                                    String discountInPercentOrFixedAmount = discountUnitJSON.getString("discount_bonus");
                                    String discount_value = discountUnitJSON.getString("discount_value");
                                    String discount = discountUnitJSON.getString("discount");

                                    if (discountInPercentOrFixedAmount.equalsIgnoreCase("by_percentage")) {

                                        orderReviewObjects.setDiscountPercent(discount_value + " % Discount");

                                    } else {

                                        orderReviewObjects.setDiscountPercent(discount);

                                    }

                                }

                            }

                        }

                        orderReviewObjectsList.add(orderReviewObjects);
                    }

                    adapter = new OrderReviewAdapter(orderReviewObjectsList, this);
                    orderReview_recycler_view.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void getViews() {

        mToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Review");


        // RecyclerView reference
        orderReview_recycler_view = (RecyclerView) findViewById(R.id.orderReview_recycler_view);


        // RecyclerView row items views
        /*productImg = (ImageView) findViewById(R.id.productImg);
        productTitle = (TextView) findViewById(R.id.productTitle);
        productQty = (TextView) findViewById(R.id.productQty);
        originalPrice = (TextView) findViewById(R.id.originalPrice);
        disPrice = (TextView) findViewById(R.id.disPrice);
        couponCode = (TextView) findViewById(R.id.couponCode);
        couponCodeStatus = (TextView) findViewById(R.id.couponCodeStatus);
        discountPercent = (TextView) findViewById(R.id.discountPercent);*/


        //Order Summary views references
        user_selected_paymentMode = (TextView) findViewById(R.id.user_selected_paymentMode);
        subTotal = (TextView) findViewById(R.id.tv4);
        totalMargin = (TextView) findViewById(R.id.totalMargin);
        discountedAmount = (TextView) findViewById(R.id.tv1);
        wydrCash = (TextView) findViewById(R.id.wydrCash_tv);
        shippingCost = (TextView) findViewById(R.id.tv148);
        netPayableAmount = (TextView) findViewById(R.id.tv);


        // PayNow button reference
        paynow = (Button) findViewById(R.id.buy_pay);


        progress = new ProgressDialog(OrderReview.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);

        cd = new ConnectionDetector(OrderReview.this);

        helper = new Helper();

        payableAmount = getIntent().getStringExtra("payableAmount");
        phoneString = getIntent().getStringExtra("phoneString");
        emailString = getIntent().getStringExtra("emailString");
        newOrderId = getIntent().getStringExtra("newOrderId");
        productOrderedDetailsList = (ArrayList<ProductOrderedDetails>)getIntent().getSerializableExtra("productOrderedDetailsList");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
    }
}
