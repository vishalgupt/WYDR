package wydr.sellers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.gson.CreateOrder;
import wydr.sellers.gson.OrderStatus;
import wydr.sellers.gson.Products;
import wydr.sellers.modal.AddressModal;
import wydr.sellers.modal.ProductOrderedDetails;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.network.Transactions;
import wydr.sellers.registration.GPSTracker;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public class Checkout extends AppCompatActivity
{
    TextView header, saved, newAddress,promodiscount;
    Button checkout,promocode_Button;
    JSONObject jsonObject,jsonpay;
    //Spinner spin;
    TextView spin;
    int wydrcash,totalmargin = 0;
    ListView listView;
    String lat = "";
    String lon = "";
    AlertDialog.Builder alertDialog;
    //AddressAdapter adapter;
    ArrayList<AddressModal>list = new ArrayList<AddressModal>();
    ArrayList<String>paymodelist = new ArrayList<String>();
    ArrayList<String>payment_id = new ArrayList<>();
    Toolbar mToolbar;
    EditText promocode;
    int pointstoredeem = 0;
    ProgressDialog progressDialog;
    String amount;
    String ShippingCost;
    MyDatabaseHelper db;
    ConnectionDetector cd;
    Helper helper = new Helper();
    wydr.sellers.slider.JSONParser jsonParser;
    PrefManager prefManager;
    String orderId, product_id, name, qty, category, ordertotal, price;
    int ordertotalint;
    String promid= "0";
    TextView txt_address,changeaddress,Total;
    Button add_addr,paynow;
    String[] testCOD = new String[]{"Pay Online"};
    String finalAmount;
    TextView discount,sum,promoMsg;
    LinearLayout shiplayout,discountlayout;
    ArrayList<String>cod=new ArrayList<>();
    String nameString, codeString, addressString, addressString2, landString, cityString, stateString, phoneString, emailString;
    private ProgressDialog progress;
    int shipamount;
    Button choose_payment_mode,CheckoutContinue;
    Controller application;
    TextView redeemPoints, info4NormalUser;
    Tracker mTracker;
    String promodel;
    TextView phoneNum, points2redeem, wydrCash_tv, totalMargin, pointValue;
    String newOrderId = "";
    ArrayList<String>Couponlist = new ArrayList<>();
    ArrayList<ProductOrderedDetails> productOrderedDetailsList;
    Button reedem;
    String redeemPoint = "";
    String redeempointaki = "";
    String promocodeaki;
    int valueproductsint=0,totalmarginint=0,couponsint=0,wydrcashint=0,shippingcostint=0;
    ArrayList<String>ufList= new ArrayList<>();
    CardView reedemWydrCash_card_view;
    String userPromoCode = "";
    Boolean isPromoApplied = false;
    private String paymentID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        db=new MyDatabaseHelper(this);
        getRewardPoints();
        mToolbar = (Toolbar)findViewById(R.id.tb);
        shiplayout = (LinearLayout)findViewById(R.id.shiplayout);
        discountlayout = (LinearLayout)findViewById(R.id.discountlayout);
        phoneNum = (TextView) findViewById(R.id.phoneNum);
        if(db.discount()==0)
        {
            discountlayout.setVisibility(View.GONE);
        }
        totalmargin=db.discount();
        promoMsg = (TextView) findViewById(R.id.promoMsg);
        CheckoutContinue = (Button)findViewById(R.id.checkoutContinue);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        choose_payment_mode = (Button) findViewById(R.id.choose_payment_mode);
        txt_address = (TextView)findViewById(R.id.address_edit);
        wydrCash_tv = (TextView) findViewById(R.id.wydrCash_tv);
        reedem = (Button) findViewById(R.id.reedem);
        points2redeem = (TextView) findViewById(R.id.points2redeem);
        redeemPoints = (TextView) findViewById(R.id.redeemPoints);
        totalMargin = (TextView) findViewById(R.id.totalMargin);
        totalMargin.setText("Rs. "+db.discount());
		info4NormalUser = (TextView) findViewById(R.id.info4NormalUser);
        reedemWydrCash_card_view = (CardView) findViewById(R.id.reedemWydrCash_card_view);
        prefManager = new PrefManager(this);
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        pointValue = (TextView) findViewById(R.id.pointValue);
        if (PermissionsUtils.verifyLocationPermissions(Checkout.this))
        {
            displayLocation();
        }
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.Checkout));
        discount = (TextView)findViewById(R.id.tv1);
        discount.setText("Rs. "+db.discount());

        jsonParser = new wydr.sellers.slider.JSONParser();

        sum = (TextView)findViewById(R.id.tv4);
        TextView shi = (TextView)findViewById(R.id.tv148);
        Total = (TextView)findViewById(R.id.tv);

        getPayMethods();
        spin = (TextView)findViewById(R.id.spin);
        ShippingCost = getIntent().getStringExtra("ship");

        Couponlist = getIntent().getStringArrayListExtra("couponlist");
        shipamount = Integer.parseInt(ShippingCost);
        if(shipamount==0)
        {
            shiplayout.setVisibility(View.GONE);
        }
        shi.setText("Rs. " + shipamount);
        Log.d("Shippingcost",ShippingCost);
        initcheckoutoutpromo();
        cod = new ArrayList<String>(Arrays.asList(db.pay_method().split(",")));
        progressDialog = new ProgressDialog(Checkout.this );
        new AttemptPaymethods().execute();

        //promodiscount = (TextView)findViewById(R.id.promo_discount);

        sum.setText("Rs. " + String.valueOf((((int ) Math.ceil(Double.parseDouble(getIntent().getStringExtra("amount"))) ))));

        prefManager = new PrefManager(getApplicationContext());
        finalAmount = getIntent().getStringExtra("amount");
        Log.d("amount", finalAmount);
        amount = String.valueOf((((int ) Math.ceil(Double.parseDouble(getIntent().getStringExtra("amount"))) )));

        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Login, null, null, null, null);
        if (cursor != null && cursor.moveToFirst())
        {
            int iName2 = cursor.getColumnIndexOrThrow(LoginDB.KEY_NAME);
            int iMob = cursor.getColumnIndexOrThrow(LoginDB.KEY_PHONE);
            int iAddress = cursor.getColumnIndexOrThrow(LoginDB.KEY_ADDRESS);
            int iState = cursor.getColumnIndexOrThrow(LoginDB.KEY_STATE);
            int iCity = cursor.getColumnIndexOrThrow(LoginDB.KEY_CITY);
            int iEmail = cursor.getColumnIndexOrThrow(LoginDB.KEY_EMAIL);
            int iZipCode = cursor.getColumnIndexOrThrow(LoginDB.KEY_PINCODE);

            nameString = cursor.getString(iName2);
            codeString = (cursor.getString(iZipCode) == null) ? "" : cursor.getString(iZipCode);
            /*if (!codeString.equals("")) {
                prefManager.getCodeString(codeString);
            }*/
            
            addressString = cursor.getString(iAddress);
            addressString2 = "";
            landString = "";
            cityString = cursor.getString(iCity);
            stateString = cursor.getString(iState);
            phoneString = cursor.getString(iMob);
            emailString = cursor.getString(iEmail);
            //int iPin = cursor.getColumnIndexOrThrow(LoginDB.KUSER_IMAGE);
            phoneNum.setText("Ph: " + phoneString);
            /*txt_address.setText((nameString.trim() + "\n" + addressString.trim() + " " + prefManager.putAdd2String().trim() + "\n" + stateString.trim() + "\n" + cityString.trim() + "\n" + codeString.trim() + "\n"));*/
        }

        /*txt_address.setText((nameString.trim() + "\n" + addressString.trim() + " " + prefManager.putAdd2String().trim() + "\n" + stateString.trim() + "\n" + cityString.trim() + "\n" + codeString.trim() + "\n"));
        Log.d("address",nameString+"\n"+addressString+"\n"+cityString + "\n"+stateString+"\n"+phoneString+"\n"+emailString);*/

        if (prefManager.putCodeString().equalsIgnoreCase(""))
        {
            prefManager.getNameString(nameString);
            prefManager.getAddString(addressString);
            prefManager.getCityString(cityString);
            prefManager.getStateString(stateString);
            prefManager.getPhoneString(phoneString);
            prefManager.getEmailString(emailString);
        }
        initViews();
        helper = new Helper();
        progressStuff();

        if (nameString.trim().isEmpty() || addressString.trim().isEmpty() || prefManager.putAdd2String().trim().isEmpty()
                && cityString.trim().isEmpty() || stateString.trim().isEmpty() || codeString.trim().isEmpty()) {

            add_addr.setVisibility(View.VISIBLE);
            changeaddress.setVisibility(View.GONE);
            txt_address.setVisibility(View.GONE);

        } else {
            prefManager.getCodeString(codeString);
            add_addr.setVisibility(View.GONE);
            changeaddress.setVisibility(View.VISIBLE);
            txt_address.setVisibility(View.VISIBLE);
            txt_address.setText((nameString.trim() + "\n" + addressString.trim() + " " + prefManager.putAdd2String().trim() + "\n" + stateString.trim() + "\n" + cityString.trim() + "\n" + codeString.trim() + "\n"));
        }

        reedem.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (paymentID.isEmpty()) {

                    new AlertDialogManager().showAlertDialog(Checkout.this, "Information", "Please select payment method.");

                } else {


                    if (ValidationUtil.isNumeric(points2redeem.getText().toString().trim())){

                        try {

                            if (points2redeem.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(Checkout.this, "Empty Field", Toast.LENGTH_SHORT).show();
                            }
                            else if (Double.parseDouble(points2redeem.getText().toString().trim()) <= 0){
                                Toast.makeText(Checkout.this, "Enter amount greater than  0", Toast.LENGTH_SHORT).show();
                            }
                            else if (Double.parseDouble(points2redeem.getText().toString().trim()) > Double.parseDouble(redeemPoint)) {
                                Toast.makeText(Checkout.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                JSONArray couponArray = new JSONArray();
                                pointstoredeem = Integer.parseInt(points2redeem.getText().toString().trim());
                                for(int i=0;i<Couponlist.size();i++)
                                {
                                    couponArray.put(Couponlist.get(i));
                                }

                                final JSONObject jsonObject28 = new JSONObject();
                                JSONObject redeemJSON = new JSONObject();

                                try
                                {
                                    redeemJSON.put("points_in_use", points2redeem.getText().toString().trim());
                                    redeempointaki = points2redeem.getText().toString().trim();
                                    redeemJSON.put("updateRewardPoints","1");
                                    redeemJSON.put("promotions_cods",couponArray);
                                    redeemJSON.put("payment_id", paymentID);

                                    if (cd.isConnectingToInternet())
                                    {
                                        /*******************************ISTIAQUE***************************************/
                                        application = (Controller) getApplication();
                                        mTracker = application.getDefaultTracker();
                                        application.trackEvent("Apply redeem points", "onClick", "Redeem");
                                        /*******************************ISTIAQUE***************************************/
                                        new CallPromo(redeemJSON).execute();
                                    }
                                    else
                                    {
                                        Toast.makeText(Checkout.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Checkout.this, "Only Numbers allowed", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }


    private void displayLocation() {
        GPSTracker gpsTracker = new GPSTracker(Checkout.this);
        //Log.i("GPS-latitude", gpsTracker.getLatitude() + "");
        //Log.i("GPS-getLongitude", gpsTracker.getLongitude() + "");
        lat = String.valueOf(gpsTracker.getLatitude());
        lon = String.valueOf(gpsTracker.getLongitude());
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        /*if(prefManager.putCodeString().equalsIgnoreCase(""))
        {
            add_addr.setVisibility(View.VISIBLE);
            changeaddress.setVisibility(View.GONE);
            txt_address.setVisibility(View.GONE);
        }
        else
        {
            add_addr.setVisibility(View.GONE);
            changeaddress.setVisibility(View.VISIBLE);
            txt_address.setVisibility(View.VISIBLE);

        }*/

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Checkout");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public void getPayMethods()
    {
        final ProgressDialog progressDialog = new ProgressDialog(Checkout.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.getpaymode(helper.getDefaults("user_id",getApplicationContext()),helper.getB64Auth(getApplicationContext()),"application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Response response) {
                progressDialog.dismiss();
                if (response.isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.d("retrofit", jsonObject.toString());
                        parseJsonFeedpay(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Log.d("Here", t.toString());
            }
        });
    }

    class AttemptPaymethods extends AsyncTask<String,String,String>
    {
        List<NameValuePair> params;

        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            jsonpay = jsonParser.getJSONFromUrlGet(AppUtil.URL + "payments?current_user_id="+helper.getDefaults("user_id",getApplicationContext()), params, getApplicationContext());
            Log.d("JsonPay",jsonpay.toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            //progressDialog.dismiss();
            super.onPostExecute(s);
            parseJsonFeedpay(jsonpay);

        }
    }

    private void parseJsonFeedpay(JSONObject jsonpay)
    {
        paymodelist.clear();
        try
        {
            Log.d("GATEWAY", jsonpay.toString());
            JSONArray array = jsonpay.getJSONArray("payments");

           // paymodelist.add("Select payment mode");

            for (int i = 0; i < array.length(); i++)
            {
                JSONObject obj = (JSONObject)array.get(i);
                String pay = obj.getString("payment");
                payment_id.add(obj.getString("payment_id"));
                paymodelist.add(pay);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Checkout.this, android.R.layout.simple_spinner_item,paymodelist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      //  spin.setAdapter(dataAdapter);

        choose_payment_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] paymodeArray = new String[paymodelist.size()];

                for (int i = 0; i < paymodelist.size(); i++) {
                    paymodeArray[i] = paymodelist.get(i);
                }

                alertDialog
                        .setTitle("Select Payment mode")
                        .setSingleChoiceItems(paymodeArray, 0, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                //Do something useful withe the position of the selected radio button
                                spin.setText(paymodelist.get(selectedPosition));
                                paymentID = payment_id.get(selectedPosition);
                                choose_payment_mode.setText("CHANGE");
                            }
                        })
                        .show();

            }
        });
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        progress.dismiss();
    }

    private void initViews()
    {
        changeaddress = (TextView)findViewById(R.id.chngeaddr);
        txt_address = (TextView)findViewById(R.id.address_edit);
        add_addr = (Button)findViewById(R.id.addaddre);
        paynow = (Button)findViewById(R.id.buy_pay);
        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*******************************ISTIAQUE***************************************/
                application = (Controller) getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("Pay Now", "Move", "Checkout");
                /*******************************ISTIAQUE***************************************/
                if (prefManager.putCodeString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please add your shipping address", Toast.LENGTH_SHORT).show();
                } /*else if ((((Integer.parseInt(getIntent().getStringExtra("amount"))) / 100) + shipamount) < 1) {
                    Toast.makeText(getApplicationContext(), "Total payable amount is invalid.", Toast.LENGTH_SHORT).show();
                }
                */
                else if (spin.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select payment method", Toast.LENGTH_SHORT).show();

                } else {
                    if (promocode.getText().toString().trim().equalsIgnoreCase(""))
                        helper.setDefaults("promocode", promocode.getText().toString().trim(), getApplicationContext());
                    prepareOrder();
                }
            }
        });
        Total = (TextView)findViewById(R.id.tv);
        Total.setText("Rs. " +((int) Math.ceil(Double.parseDouble(getIntent().getStringExtra("amount")))+ shipamount-totalmargin));


        changeaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*******************************ISTIAQUE***************************************/
                application = (Controller) getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("Change address", "Move", "AddAddress");
                /*******************************ISTIAQUE***************************************/
                Intent intent = new Intent(getApplicationContext(), AddAddress.class);
                startActivityForResult(intent, 2);
            }
        });

        if (!prefManager.putCodeString().equalsIgnoreCase(""))
        {
            txt_address.setText((prefManager.putNameString()+"\n"+prefManager.putAddString() + " " + prefManager.putAdd2String() + "\n" + prefManager.putStateString() + "\n" + prefManager.putCityString() + "\n" + prefManager.putCodeString() + "\n"));
            phoneNum.setText("Ph: " + phoneString);
        }


        /*if(codeString.equalsIgnoreCase("") || txt_address.getText().toString().equalsIgnoreCase(""))
        {
            add_addr.setVisibility(View.VISIBLE);
            changeaddress.setVisibility(View.GONE);
            txt_address.setVisibility(View.GONE);
        }

        else
        {
            add_addr.setVisibility(View.GONE);
            changeaddress.setVisibility(View.VISIBLE);
            txt_address.setVisibility(View.VISIBLE);
        }*/

        add_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddAddress.class);
                startActivityForResult(intent, 2);
            }
        });

    }


    public void initcheckoutoutpromo()
    {
        promocode = (EditText)findViewById(R.id.promotxt);
        promocode_Button = (Button)findViewById(R.id.prom_apply);
        promocode_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (paymentID.isEmpty()) {

                    new AlertDialogManager().showAlertDialog(Checkout.this, "Information", "Please select payment method.");

                } else {

                    if (!promocode.getText().toString().trim().equalsIgnoreCase(""))
                    {
                        isPromoApplied = true;
                        helper.setDefaults("promocode", promocode.getText().toString().trim(), getApplicationContext());
                        promocodeaki = promocode.getText().toString().trim();

                        if(!Couponlist.contains(promocodeaki))
                        {
                            Couponlist.add(promocodeaki);
                        }
                        //Couponlist.add(helper.getDefaults("promocode",getApplicationContext()));
                        //new promocode().execute();
                        //addString();
                        JSONArray couponArray = new JSONArray();
                        for(int i=0;i<Couponlist.size();i++)
                        {
                            couponArray.put(Couponlist.get(i));
                        }

                        final JSONObject jsonObject28 = new JSONObject();

                        JSONObject redeemJSON = new JSONObject();
                        try
                        {
                            redeemJSON.put("points_in_use", points2redeem.getText().toString().trim());
                            redeempointaki = points2redeem.getText().toString().trim();
                            redeemJSON.put("updateRewardPoints", "1");
                            redeemJSON.put("promotions_cods",couponArray);
                            redeemJSON.put("cart_promotion", promocode.getText().toString().trim());
                            redeemJSON.put("payment_id", paymentID);
                            Couponlist.add(promocode.getText().toString().trim());
                            Log.d("json",redeemJSON.toString());
                            if (cd.isConnectingToInternet())
                            {
                                /*******************************ISTIAQUE***************************************/
                                application = (Controller) getApplication();
                                mTracker = application.getDefaultTracker();
                                application.trackEvent("Apply promo code", "onClick", "Apply");
                                /*******************************ISTIAQUE***************************************/

                                new CallPromo(redeemJSON).execute();
                            }

                            else
                            {
                                Snackbar.make(findViewById(android.R.id.content), "Please check your internet connection !!", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    else
                    {
                        //Toast.makeText(getApplicationContext(), "Please enter the Promo Code", Toast.LENGTH_SHORT).show();
                        promoMsg.setText("Please enter the Promo Code");
                    }

                }

            }
        });

    }

    class promocode extends AsyncTask <String,String,String>
    {

        List<NameValuePair> para;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setMessage("Appling PromoCode");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings)
        {

            jsonObject = jsonParser.getJSONFromUrlGet(AppUtil.URL + "promotions?coupon_code=" + promocode.getText().toString().trim(), para, getApplicationContext());
            Log.d("promo", jsonObject.toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            progressDialog.dismiss();
            super.onPostExecute(s);
            try
            {
                if(jsonObject.has("promotion_id"))
                {
                    promid = jsonObject.getString("promotion_id");
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    JSONObject object1 = jsonObject.getJSONObject("bonuses");
                    //JSONObject object1 = object.getJSONObject("2");
                    String discountType = object1.getString("discount_bonus");
                    String discount1 = object1.getString("discount_value");
                    Log.d("total discount", discount1);
                    discountlayout.setVisibility(View.VISIBLE);
                    if (discountType.equalsIgnoreCase("by_percentage"))
                    {
                        Log.d("amount",getIntent().getStringExtra("amount"));
                        //sum.setText("Rs. " + String.valueOf((Integer.parseInt(getIntent().getStringExtra("amount"))+shipamount) / 100));
                        discount.setText("Rs. " + (((Integer.parseInt(getIntent().getStringExtra("amount")) / 100) * (Integer.parseInt(discount1))/ 100)));
                        amount = String.valueOf((Integer.parseInt(amount) - ((Integer.parseInt(amount) * (Integer.parseInt(discount1)) / 100))));
                        finalAmount = String.valueOf(Integer.parseInt(amount)*100);
                        Log.d("final amount", amount);
                        Total.setText("Rs." + ""+((Integer.parseInt(String.valueOf(finalAmount))/100)+shipamount-totalmargin));
                        //promodiscount.setText("PromoCode Applied: Flat "+discount+"% off on final amount");
                        promocode_Button.setEnabled(false);
                        promocode.setEnabled(false);
                    }

                    else
                    {
                        //sum.setText("Rs. " + String.valueOf((Integer.parseInt(getIntent().getStringExtra("amount"))+shipamount) / 100));
                        amount = String.valueOf(Integer.parseInt(amount) - Integer.parseInt(discount1));
                        finalAmount = String.valueOf(Integer.parseInt(amount)*100);
                        discount.setText("Rs. " + ((Integer.parseInt(discount1))));
                        Total.setText("Rs." + ((Integer.parseInt(String.valueOf(finalAmount))/100)+shipamount-totalmargin));
                        //promodiscount.setText("PromoCode Applied: Flat Rs."+discount+" off on final amount");
                        promocode_Button.setEnabled(false);
                        promocode.setEnabled(false);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(Checkout.this);
        //parser = new JSONParser();
        progress = new ProgressDialog(Checkout.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            add_addr.setVisibility(View.GONE);
            changeaddress.setVisibility(View.VISIBLE);
            //String message=data.getStringExtra("MESSAGE");
            //txt_address.setText(message);
            add_addr.setVisibility(View.GONE);
            changeaddress.setVisibility(View.VISIBLE);
            nameString = prefManager.putNameString();
            codeString = prefManager.putCodeString();
            addressString = prefManager.putAddString();
            addressString2 = prefManager.putAdd2String();
            landString = prefManager.putLandString();
            cityString = prefManager.putCityString();
            stateString = prefManager.putStateString();
            phoneString = prefManager.putPhoneString();
            emailString = prefManager.putEmailString();
            if (!prefManager.putCityString().equalsIgnoreCase(""))
            {

                txt_address.setText(prefManager.putNameString()+"\n"+prefManager.putAddString() + " " + prefManager.putAdd2String() + "\n" + prefManager.putStateString() + "\n" + prefManager.putCityString() + "\n" + prefManager.putCodeString() + "\n");
                phoneNum.setText("Ph: " + phoneString);
            }
        }
    }

   public void addString() {
       //  Couponlist.add(add);
       Set<String> temp = new HashSet<String>();
       temp.addAll(Couponlist);
       Couponlist.clear();
       Couponlist.addAll(temp);

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
        try
        {
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
        Call<JsonElement> call = service.updateOrder(orderId, status, helper.getB64Auth(Checkout.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>()
        {
            @Override
            public void onFailure(Throwable t)
            {
                if (Checkout.this != null && !Checkout.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(Checkout.this,
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
                    newOrderId = orderId;
                    //startActivity(new Intent(Checkout.this, ThankActivity.class).putExtra("order_id", orderId).putExtra("status", status.getStatus()));
                    startActivity(new Intent(Checkout.this, ThankActivity.class).putExtra("order_id", orderId).putExtra("status", status.getStatus())
                            .putExtra("product_id", product_id).putExtra("name", name).putExtra("qty", qty).putExtra("ordertotal", ordertotal)
                            .putExtra("category", category).putExtra("price", price).putExtra("productOrderedDetailsList", productOrderedDetailsList));
                    orderId = null;
                    finish();
                }

                else
                {
                    int statusCode = response.code();
                    //}
                    if (statusCode == 401)
                    {
                        final SessionManager sessionManager = new SessionManager(Checkout.this);
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
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                    } else if (statusCode == 500) {
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                        //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                        // Toast.makeText(AddressList.this, , Toast.AddAddress).show();
                    }
                }
            }
        });
    }

    private void updateOrderssilent(final OrderStatus status)
    {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.updateOrder(orderId, status, helper.getB64Auth(Checkout.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>()
        {
            @Override
            public void onFailure(Throwable t)
            {
                if (Checkout.this != null && !Checkout.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(Checkout.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response) {
                Log.d("There", "" + response.raw());
               // progress.dismiss();
                if (response.isSuccess()) {


                } else {
                    int statusCode = response.code();
                    //}
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(Checkout.this);
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
                    if (statusCode == 404)
                    {
                        //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                    } else if (statusCode == 500) {
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                        //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                        //Toast.makeText(AddressList.this, , Toast.AddAddress).show();
                    }
                }
            }
        });
    }


    private CreateOrder order()
    {
        nameString = prefManager.putNameString();
        codeString = prefManager.putCodeString();
        addressString = prefManager.putAddString();
        addressString2 = prefManager.putAdd2String();
        landString = prefManager.putLandString();
        cityString = prefManager.putCityString();
        stateString = prefManager.putStateString();
        phoneString = prefManager.putPhoneString();
        emailString = prefManager.putEmailString();

        CreateOrder create = null;
        Cursor cursor = getContentResolver().query(ChatProvider.CART_URI, null, null, null, null);
        int iQty = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY);
        int iId = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_ID);
        int ivar = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_VARIENT);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        HashMap<String, Products> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            int pos = cursor.getPosition() + 1;
            //Log.d("akivar", cursor.getString(ivar));
            try
            {
                JsonParser jsonParser = new JsonParser();
                if((!cursor.getString(ivar).equalsIgnoreCase("[]"))&&(!cursor.getString(ivar).equalsIgnoreCase("")))
                {
                   //Log.d("ivar",cursor.getString(ivar).toString());
                    JsonObject a = (JsonObject) jsonParser.parse(cursor.getString(ivar));
                    map.put("" + pos, new Products(cursor.getString(iId), cursor.getString(iQty),a,""));
                }
                else
                {
                    JsonObject a = new JsonObject();
                    map.put("" + pos, new Products(cursor.getString(iId), cursor.getString(iQty),a,""));
                }

            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        removeduplicacy();

        JSONArray couponArray = new JSONArray();

        for(int i=0;i<Couponlist.size();i++)
        {
            couponArray.put(Couponlist.get(i));
        }

        if (spin.getText().toString().equalsIgnoreCase("C.O.D"))
        {
            create = new CreateOrder(addressString, addressString2, cityString, stateString, nameString, codeString, landString, phoneString, addressString, addressString2
                    , cityString, stateString, nameString,codeString, landString, phoneString, helper.getDefaults("user_id", Checkout.this), AppUtil.PAYMENT_COD,"E",ShippingCost,lat,
                    lon, Build.MODEL,Build.VERSION.RELEASE,Build.BRAND,helper.getApp_Version(getApplicationContext()),telephonyManager.getDeviceId()
            ,couponArray,"1",redeempointaki);
        }

        else
        {
            create = new CreateOrder(addressString, addressString2, cityString, stateString, nameString, codeString, landString, phoneString, addressString, addressString2
                    , cityString, stateString, nameString, codeString, landString, phoneString, helper.getDefaults("user_id", Checkout.this), AppUtil.PAYMENT_ID,"E",ShippingCost,lat,
                    lon, Build.MODEL,Build.VERSION.RELEASE,Build.BRAND,helper.getApp_Version(getApplicationContext()),telephonyManager.getDeviceId()
                    ,couponArray,"1",redeempointaki);
        }
        Gson g = new Gson();
        String jsonString = g.toJson(create);
        Log.d("JSONing ",jsonString.toString());
        cursor.close();
        return create;
    }

    private void prepareOrder()
    {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.bookOrder(order(), helper.getB64Auth(Checkout.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response) {
                progress.dismiss();
                if (response.isSuccess()) {
                    //progress.dismiss();
                    JsonElement element = (JsonElement) response.body();
                    if (element != null && element.isJsonObject()) {
                        try {
                            Log.d("JSON", " " + element.getAsJsonObject().toString());
                            JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                            orderId = json.getString("order_id");
//                          int total = (int) json.getJSONObject("order_data").getDouble("total") * 100;
//                          Log.d("Total Price", " " + total);

                            ordertotal = json.getString("ordertotal");
                            ordertotalint = ((int)Math.ceil(Double.parseDouble(ordertotal)))*100;
                            productOrderedDetailsList = new ArrayList<ProductOrderedDetails>();
                            JSONArray jsonArray = json.getJSONArray("products");
                            for (int j = 0; j < jsonArray.length(); j++) {
                                ProductOrderedDetails productOrderedDetails = new ProductOrderedDetails();
                                JSONObject productJSON = jsonArray.getJSONObject(j);
                                product_id = productJSON.getString("product_id");
                                name = productJSON.getString("name");
                                qty = productJSON.getString("qty");
                                category = productJSON.getString("category");
                                price = productJSON.getString("price");
                                productOrderedDetails.setProduct_id(product_id);
                                productOrderedDetails.setName(name);
                                productOrderedDetails.setQty(qty);
                                productOrderedDetails.setCategory(category);
                                productOrderedDetails.setPrice(price);
                                productOrderedDetails.setOrderId(orderId);
                                productOrderedDetailsList.add(productOrderedDetails);
                            }



                            if (spin.getText().toString().equalsIgnoreCase("C.O.D")) {
                                try {
                                    //           Log.d("spinvalue ", spin.getText().toString());
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("status", "G");
                                    getContentResolver().delete(ChatProvider.CART_URI, null, null);
                                    getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                                    setContentView(R.layout.activity_thank);
                                    updateOrders(new OrderStatus("G", AppUtil.PAYMENT_COD, new Transactions("")));
                                    OrderStatus up;
                                    up = new OrderStatus("G", AppUtil.PAYMENT_COD, new Transactions(""));
                                    Gson g = new Gson();
                                    String jsonString2 = g.toJson(up);
                                    Log.d("JSONing2 ", jsonString2.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (ordertotalint ==0)
                            {
                                try {
                                    //           Log.d("spinvalue ", spin.getText().toString());
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("status", "P");
                                    getContentResolver().delete(ChatProvider.CART_URI, null, null);
                                    getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                                    setContentView(R.layout.activity_thank);
                                    updateOrders(new OrderStatus("P", AppUtil.PAYMENT_ID, new Transactions("")));
                                    OrderStatus up;
                                    up = new OrderStatus("P", AppUtil.PAYMENT_ID, new Transactions(""));
                                    Gson g = new Gson();
                                    String jsonString2 = g.toJson(up);
                                    Log.d("JSONing2 ", jsonString2.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            else {
                                try {
                                    Log.d("spinvalue ", spin.getText().toString());
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("status", "E");
                                    //getContentResolver().delete(ChatProvider.CART_URI, null, null);
                                    //getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                                    //setContentView(R.layout.activity_thank);
                                    updateOrderssilent(new OrderStatus("E", AppUtil.PAYMENT_ID, new Transactions("")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                startPayment("" + ordertotalint);

    /*                            *//*******************************ISTIAQUE***************************************//*
                                application = (Controller) getApplication();
                                mTracker = application.getDefaultTracker();
                                application.trackEvent("Next", "Move", "OrderReview");
                                *//*******************************ISTIAQUE***************************************//*
                                helper.setDefaults("reward_points",points2redeem.getText().toString().trim(),Checkout.this);
    */                            /*startActivity(new Intent(Checkout.this, OrderReview.class).putExtra("payableAmount", "" + ((Integer.parseInt(String.valueOf(finalAmount)) / 100) + shipamount) * 100)
                                        .putExtra("phoneString", phoneString).putExtra("emailString", emailString).putExtra("newOrderId", newOrderId).putExtra("productOrderedDetailsList", productOrderedDetailsList));
  */                          }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    int statusCode = response.code();
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(Checkout.this);
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
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                    } else if (statusCode == 500) {
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                        //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        new AlertDialogManager().showAlertDialog(Checkout.this, getString(R.string.error), error2);
                        // Toast.makeText(AddressList.this, , Toast.AddAddress).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progress.dismiss();
                Log.d("Here", t.toString());
                //  int statusCode
//                if (statusCode == 401) {
//
//                    final SessionManager sessionManager = new SessionManager(AddressList.this);
//                    Handler mainHandler = new Handler(Looper.getMainLooper());
//
//                    Runnable myRunnable = new Runnable() {
//                        @Override
//                        public void run() { sessionManager.logoutUser();} // This is your code
//                    };
//                    mainHandler.post(myRunnable);
//                }

//                if(error2.equalsIgnoreCase(""))
//                    error2=getString(R.string.server_error);
//                if (statusCode == 404) {
//                    //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
//                    new AlertDialogManager().showAlertDialog(AddressList.this, getString(R.string.error), error2);
//                } else if (statusCode == 500) {
//                    new AlertDialogManager().showAlertDialog(AddressList.this, getString(R.string.error), error2);
//                    //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                } else {
//                    new AlertDialogManager().showAlertDialog(AddressList.this, getString(R.string.error), error2);
//                    // Toast.makeText(AddressList.this, , Toast.LENGTH_LONG).show();
//                }
                if (Checkout.this != null && !Checkout.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(Checkout.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getRewardPoints() {
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.getUserRewardHistory(helper.getDefaults("user_id", Checkout.this), helper.getB64Auth(Checkout.this), "application/json");
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onFailure(Throwable t) {
                progress.dismiss();
                if (Checkout.this != null && !Checkout.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(Checkout.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response) {
                Log.d("Checkout reward points", "" + response.raw());
                progress.dismiss();
                if (response.isSuccess()) {
                    progress.dismiss();

                    try {
                        JSONObject rootJSON = new JSONObject(response.body().toString());

                        if (rootJSON != null) {
                            //Log.d("IstRew", rootJSON.toString());
                            if (rootJSON.has("msg")) {
                                redeemPoint = rootJSON.getString("balance");
                            } else {
                                redeemPoint = rootJSON.getString("total_points");
                            }

                            if (ufList.contains(AppUtil.TAG_Network)) {
                                info4NormalUser.setText(rootJSON.getString("nu_notification"));//"Register as business user to unlock and earn reward points.");
                                redeemPoints.setText(redeemPoint);
                                points2redeem.setEnabled(false);
                                reedem.setEnabled(false);
                            } else if (redeemPoint.equalsIgnoreCase("0")) {
                                redeemPoints.setText(redeemPoint);
                                points2redeem.setEnabled(false);
                                reedem.setEnabled(false);
                            } else {
                                redeemPoints.setText(redeemPoint);
                            }


                            if (rootJSON.has("pointmsg")) {
                                pointValue.setText(rootJSON.getString("pointmsg"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Checkout.this, "Your request has been failed please try again.", Toast.LENGTH_LONG).show();
                }
            }

        });

    }


    class CallPromo extends AsyncTask<String,String,String>
    {
        JSONObject c = new JSONObject();
        JSONObject d = new JSONObject();

        public CallPromo(JSONObject jsonObject)
        {
            this.c = jsonObject;

        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Checkout.this);
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            Log.d("promoi", c.toString());
            d = jsonParser.getJSONFromUrlPut(AppUtil.URL+"promotions/"+helper.getDefaults("user_id",getApplicationContext()),c,getApplicationContext());
            Log.d("rabba",d.toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (d!=null)
            {
                //if (isPromoApplied)
                {
                    try
                    {
                        //redeemPoints.setText(d.getJSONObject("cart").getString("cartremain"));

                        if(d.getJSONObject("cart").has("invalid_coupons"))
                        {    //Toast.makeText(getApplicationContext(),"Invalid coupon code",Toast.LENGTH_SHORT).show();
                                promoMsg.setText(d.getJSONObject("cart").getString("cart_promo"));
                                promocode.setText("");
                                Couponlist.remove(helper.getDefaults("promocode", getApplicationContext()));
                              //  Toast.makeText(getApplicationContext(),Couponlist.toString(),Toast.LENGTH_SHORT).show();

                        } else {

                            if (isPromoApplied) {

                                promoMsg.setText(d.getJSONObject("cart").getString("cart_promo"));
                                //setDiscount(d.getJSONObject("cart").getString("final_discount"));
                                //promocode.setText("");
                                promocode.setEnabled(false);
                                promocode_Button.setEnabled(false);

                                couponsint = (int) Math.floor(Double.parseDouble(d.getJSONObject("cart").getString("all_coupon_discount")));
                                setDiscount(couponsint);

                            }

                        }

                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                } /*else*/ {

                    try
                    {
                        if(d.getJSONObject("cart").has("cartremain"))
                        {
                            redeemPoints.setText(d.getJSONObject("cart").getString("cartremain"));
                            wydrCash_tv.setText("Rs. " + d.getJSONObject("cart").getString("cartreward"));
                            //wydrcash = Integer.parseInt(d.getJSONObject("cart").getString("cartreward"));
                            points2redeem.setEnabled(false);
                            reedem.setEnabled(false);

                        }

                        /*couponsint = (int) Math.floor(Double.parseDouble(d.getJSONObject("cart").getString("all_coupon_discount")));*/
                        if (d.getJSONObject("cart").has("rewarduses")) {
                            info4NormalUser.setText(d.getJSONObject("cart").getString("rewarduses"));//"You have redeemed " +wydrCash_tv.getText().toString() + " against " + redeempointaki + " ponits.");
                        }

                        /*if(couponsint != 0)
                        {
                            setDiscount(couponsint);
                        }*/
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }


                try {
                    totalMargin.setText("Rs. " + d.getJSONObject("cart").getString("final_discount"));
                    totalmargin = (int) Math.floor(Double.parseDouble(d.getJSONObject("cart").getString("final_discount")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Total.setText("Rs." + ((int)Math.ceil(Double.parseDouble(String.valueOf(finalAmount))) + shipamount - totalmargin));

                //Toast.makeText(Checkout.this, "Applied successfully.", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void setDiscount(int dis) {
        if(dis > 0) {
            discountlayout.setVisibility(View.VISIBLE);
            //discount.setVisibility(View.VISIBLE);
            discount.setText("Rs. "+dis);
        } else {
            discountlayout.setVisibility(View.GONE);
         //discount.setVisibility(View.GONE);
        }
    }

    public void removeduplicacy()
    {
        Set<String> temp = new HashSet<String>();
        temp.addAll(Couponlist);
        Couponlist.clear();
        Couponlist.addAll(temp);
    }


}