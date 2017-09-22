package wydr.sellers.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.gson.OrderStatus;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.network.Transactions;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by surya on 2/12/15.
 */
public class AddAddress extends AppCompatActivity implements View.OnClickListener
{
    EditText name, code, address, address2, landmark, state, phone;
    Button save, cancel;
    Toolbar mToolbar;
    String amount;
    ConnectionDetector cd;
    Helper helper;
    String emailString;
    String orderId, nameString, codeString, addressString, addressString2, landString, cityString, stateString, phoneString;
    private AutoCompleteTextView et_city;
    private ProgressDialog progress;
    private TextInputLayout inputLayoutName, inputLayoutPin, inputLayoutAddress, inputLayoutCity, inputLayoutState, inputLayoutMobile, inputLayoutLandmark;
    MyDatabaseHelper myDatabaseHelper;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);
//        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                "Add Address"));
        progressStuff();
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        helper = new Helper();
        myDatabaseHelper = new MyDatabaseHelper(this);
        prefManager = new PrefManager(getApplicationContext());
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_address));
        name = (EditText) findViewById(R.id.editAddName);
        code = (EditText) findViewById(R.id.editAddCode);
        address = (EditText) findViewById(R.id.editAddAddress);
        address2 = (EditText) findViewById(R.id.editAddAddress2);
        landmark = (EditText) findViewById(R.id.editAddLandmark);
        et_city = (AutoCompleteTextView) findViewById(R.id.editAddCity);
        state = (EditText) findViewById(R.id.editAddState);
        phone = (EditText) findViewById(R.id.editAddPhone);
        cancel = (Button) findViewById(R.id.btnAddCancel);
        save = (Button) findViewById(R.id.btnAddSave);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutPin = (TextInputLayout) findViewById(R.id.input_layout_pincode);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.input_layout_address);
        inputLayoutCity = (TextInputLayout) findViewById(R.id.input_layout_city);
        inputLayoutState = (TextInputLayout) findViewById(R.id.input_layout_state);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputLayoutLandmark = (TextInputLayout) findViewById(R.id.input_layout_landmark);
        name.setOnClickListener(this);
        code.setOnClickListener(this);
        address.setOnClickListener(this);
        et_city.setOnClickListener(this);
        state.setOnClickListener(this);
        phone.setOnClickListener(this);
        landmark.setOnClickListener(this);

        if (!prefManager.putPhoneString().equalsIgnoreCase(""))
        {
            name.setText(prefManager.putNameString());
            code.setText(prefManager.putCodeString());
            address.setText(prefManager.putAddString());
            address2.setText(prefManager.putAdd2String());
            landmark.setText(prefManager.putLandString());
            et_city.setText(prefManager.putCityString());
            state.setText(prefManager.putStateString());
            phone.setText(prefManager.putPhoneString());
        }

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        /*progressStuff();*/
        helper = new Helper();
        amount = getIntent().getStringExtra("amount");
        //Log.d("amount", amount);

        // name.requestFocus();


        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Login, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int iEmail = cursor.getColumnIndexOrThrow(LoginDB.KEY_EMAIL);
            emailString = cursor.getString(iEmail);

        }
        final List<String> cities = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_cities_autosuggest, cities);
        et_city.setThreshold(2);
        et_city.setAdapter(adapter);
        et_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().indexOf(",") != -1) {
                    int asterisk1 = s.toString().indexOf(',');
                    boolean hasTowAsterisks = asterisk1 != -1 && s.toString().indexOf(',', asterisk1 + 1) != -1;
                    if (hasTowAsterisks) {
                        et_city.setTag(s.subSequence(s.toString().indexOf(",") + 2, s.toString().indexOf(",", s.toString().indexOf(",") + 1)));
                        et_city.setText(s.subSequence(0, s.toString().indexOf(",")));
                        state.setText(s.subSequence(s.toString().indexOf(",") + 2, s.toString().indexOf(",", s.toString().indexOf(",") + 1)));
                    } else {
                        et_city.setTag(s.subSequence(0, s.toString().indexOf(",")));
                        et_city.setText(s.subSequence(0, s.toString().indexOf(",")));
                        state.setText(s.subSequence(0, s.toString().indexOf(",")));
                    }


                } else {
                    et_city.setTag("");
                }


                Log.i("", "len: " + et_city.length());
                if (et_city.length() >= et_city.getThreshold()) {
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAUqbnkXE4z-TXleAIbqy8j3PyHjYqUzeg&types=(cities)&sensor=false&components=country:in&input=" + et_city.getText()).openConnection();
                                    httpUrlConnection.setUseCaches(false);
                                    httpUrlConnection.setDoOutput(true);
                                    httpUrlConnection.setRequestMethod("GET");
                                    httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                                    httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
//            httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();
                                    while ((inputLine = bufferedReader.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    bufferedReader.close();
                                    httpUrlConnection.disconnect();
                                    Log.i("", "response: " + response);
                                    return response.toString();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String response) {
                                super.onPostExecute(response);
                                if (response == null) {
                                    return;
                                }
                                try {
                                    JSONArray cities_array = new JSONObject(response.toString()).getJSONArray("predictions");
                                    cities.clear();
                                    adapter.clear();
                                    for (int i = 0; i <= cities_array.length() - 1; i++) {

                                        Log.i("", "city: " + cities_array.getJSONObject(i).getString("description"));
                                        adapter.add(cities_array.getJSONObject(i).getString("description"));
                                        cities.add(cities_array.getJSONObject(i).getString("description"));
                                    }
//                                adapter.notifyDataSetChanged();
//                                city.setAdapter(adapter);
//                                adapter.getFilter().filter(city.getText(), null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(AddAddress.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(AddAddress.this);
        //parser = new JSONParser();
        progress = new ProgressDialog(AddAddress.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);

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

    @Override
    protected void onPause() {
        super.onPause();

        progress.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editAddName:
                inputLayoutName.setError(null);
                break;
            case R.id.editAddCode:
                inputLayoutPin.setError(null);
                break;
            case R.id.editAddAddress:
                inputLayoutAddress.setError(null);
                break;
            case R.id.editAddLandmark:
                inputLayoutLandmark.setError(null);
                break;
            case R.id.editAddCity:
                inputLayoutCity.setError(null);
                break;
            case R.id.editAddState:
                inputLayoutState.setError(null);
                break;
            case R.id.editAddPhone:
                inputLayoutMobile.setError(null);
                break;
            case R.id.btnAddCancel:
                finish();
                break;
            case R.id.btnAddSave:
                nameString = name.getText().toString().trim();
                codeString = code.getText().toString().trim();
                addressString = address.getText().toString().trim();
                addressString2 = address2.getText().toString().trim();
                landString = landmark.getText().toString().trim();
                cityString = et_city.getText().toString().trim();
                stateString = state.getText().toString().trim();
                phoneString = phone.getText().toString().trim();
                prefManager.getNameString(nameString);
                prefManager.getCodeString(codeString);
                prefManager.getAddString(addressString);
                prefManager.getAdd2String(addressString2);
                prefManager.getLandString(landString);
                prefManager.getCityString(cityString);
                prefManager.getStateString(stateString);
                prefManager.getPhoneString(phoneString);
                prefManager.getEmailString(emailString);


                if (ValidationUtil.isValidName(nameString)) {
                    if (ValidationUtil.zipcodeValidation(codeString) && codeString.length() == 6) {
                        if (!ValidationUtil.isNull(addressString)) {
                            //if (!ValidationUtil.isNull(landString)) {
                                if (!ValidationUtil.isNull(cityString)) {
                                    if (ValidationUtil.isValidText(cityString)) {
                                        if (!ValidationUtil.isNull(stateString)) {
                                            if (ValidationUtil.isValidText(stateString)) {
                                                if (ValidationUtil.isValidMobile(phoneString))
                                                {
                                                    Intent intent=new Intent();
                                                    intent.putExtra("MESSAGE",prefManager.putNameString()+"\n"+prefManager.putAddString() + " " + prefManager.putAdd2String() + "\n" + prefManager.putStateString() + "\n" + prefManager.putCityString() + "\n" + prefManager.putCodeString() + "\n" + prefManager.putPhoneString());
                                                    setResult(2,intent);
                                                     prepareOrder();
                                                    finish();
                                                }

                                                else
                                                {
                                                    inputLayoutMobile.setError(getString(R.string.invalid_mobile));
                                                    requestFocus(phone);
                                                }
                                            }
                                            else
                                            {
                                                inputLayoutState.setError(getString(R.string.enter_valid_state));
                                                requestFocus(state);

                                            }
                                        }

                                        else
                                        {
                                            inputLayoutState.setError(getString(R.string.state_empty));
                                            requestFocus(state);

                                        }
                                    } else {
                                        inputLayoutCity.setError(getString(R.string.enter_vaild_city));
                                        requestFocus(et_city);

                                    }
                                } else {
                                    inputLayoutCity.setError(getString(R.string.empty_city));
                                    requestFocus(et_city);


                                }
                            //} else {
                              //  inputLayoutLandmark.setError(getString(R.string.landmark_empty));
                               // requestFocus(landmark);

                            //}
                        } else {
                            inputLayoutAddress.setError(getString(R.string.address_empty));
                            requestFocus(address);

                        }
                    } else {
                        inputLayoutPin.setError(getString(R.string.enter_valid_pin));
                        requestFocus(code);

                    }
                } else {
                    inputLayoutName.setError(getString(R.string.empty_name));
                    requestFocus(name);

                }
                break;

        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void startPayment(String amount) {
        /**
         * Replace with your public key
         */
        //   final String public_key = "rzp_test_gtvNN3wTstphpz";

        /**
         * You need to pass current activity in order to let razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setPublicKey(AppUtil.public_key_test);

        try {
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
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void onPaymentSuccess(String razorpayPaymentID) {

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
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        //   Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }

    public void onPaymentError(int code, String response) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "F");
            getContentResolver().delete(ChatProvider.CART_URI, null, null);
            getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
            setContentView(R.layout.activity_thank);
            updateOrders(new OrderStatus("F", AppUtil.PAYMENT_ID, new Transactions("")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateOrders(final OrderStatus status)
    {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.updateOrder(orderId, status, helper.getB64Auth(AddAddress.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onFailure(Throwable t) {
                if (AddAddress.this != null && !AddAddress.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(AddAddress.this,
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
                    startActivity(new Intent(AddAddress.this, ThankActivity.class).putExtra("order_id", orderId).putExtra("status", status.getStatus()));
                    orderId = null;
                    finish();
                }
                else
                {
                    int statusCode = response.code();
                    //}
                    if (statusCode == 401)
                    {

                        final SessionManager sessionManager = new SessionManager(AddAddress.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }

                    else
                    {
                        if (AddAddress.this != null && !AddAddress.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(AddAddress.this,
                                    getString(R.string.error),
                                    getString(R.string.server_error));
                        }
                    }

                }


            }
        });


    }

    /*private CreateOrder order() {

        Cursor cursor = getContentResolver().query(ChatProvider.CART_URI, null, null, null, null);
        int iQty = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY);
        int iId = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_ID);
        HashMap<String, Products> map = new HashMap<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            int pos = cursor.getPosition() + 1;

            map.put("" + pos, new Products(cursor.getString(iId), cursor.getString(iQty)));
        }

        CreateOrder create = new CreateOrder(addressString, addressString2, cityString, stateString, nameString, codeString, landString, phoneString, addressString, addressString2
                , cityString, stateString, nameString, cityString, landString, phoneString, helper.getDefaults("user_id", AddAddress.this), AppUtil.PAYMENT_ID, map);
        Log.i("ORDERRRR--", map.toString() + "/");
        Gson g = new Gson();
        String jsonString = g.toJson(create);
        Log.d("JSON ", jsonString.toString());
        cursor.close();
        return create;
    }*/

    private wydr.sellers.gson.UpdateAddress add()
    {
        wydr.sellers.gson.UpdateAddress add;
        nameString = name.getText().toString().trim();
        codeString = code.getText().toString().trim();
        addressString = address.getText().toString().trim();
        addressString2 = address2.getText().toString().trim();
        landString = landmark.getText().toString().trim();
        cityString = et_city.getText().toString().trim();
        stateString = state.getText().toString().trim();
        phoneString = phone.getText().toString().trim();
        add = new wydr.sellers.gson.UpdateAddress(nameString,codeString,addressString,addressString2, landString,cityString, stateString,phoneString);
        //Log.i("ORDERRRR--",map.toString()+"/");
        Gson g = new Gson();
        String jsonString = g.toJson(add);
        Log.d("JSONing ", jsonString.toString());
        return add;
    }

    /*private void prepareOrder() {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();

        Call<JsonElement> call = service.bookOrder(order(), helper.getB64Auth(AddAddress.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response) {
                progress.dismiss();
                if (response.isSuccess()) {
                    //   progress.dismiss();
                    JsonElement element = (JsonElement) response.body();
                    if (element != null && element.isJsonObject()) {
                        try {

                            Log.d("JSON", " " + element.getAsJsonObject().toString());
                            JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                            orderId = json.getString("order_id");
//                        int total = (int) json.getJSONObject("order_data").getDouble("total") * 100;
//                        Log.d("Total Price", " " + total);
                            startPayment("" + amount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    int statusCode = response.code();
                    //}
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(AddAddress.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }
                    else {
                        if (AddAddress.this != null && !AddAddress.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(AddAddress.this,
                                    getString(R.string.error),
                                    getString(R.string.server_error));
                        }
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {

                progress.dismiss();
                Log.d("Here", t.toString());
                if (AddAddress.this != null && !AddAddress.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(AddAddress.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }
        });

    }*/

    private void prepareOrder()
    {
        progress.show();
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.UpdateAddress(helper.getDefaults("user_id",getApplicationContext()),add(),helper.getB64Auth(AddAddress.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response)
            {
                progress.dismiss();
                if (response.isSuccess())
                {
                    //Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    //Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
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

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("AddAddress");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
