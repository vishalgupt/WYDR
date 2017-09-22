package wydr.sellers.registration;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.R;
import wydr.sellers.WalkThroughActivity;
import wydr.sellers.acc.IdHolder;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.PermissionsUtils;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.slider.Authentication;
import wydr.sellers.slider.LoggedIn;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyCategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by Deepesh_pc on 23-07-2015.
 */
public class OTPCheck extends Activity
{
    public static int OTPCounter, resendCounter, counter = 0;
    private final String TAG = "OTPCHECK";
    String city1 = "";
    public String contact;
    public String KEY_ID = "user", KEY_ERROR = "is_error", KEY_MESSAGE = "message", KEY_REGISTERED = "registered";
    public int flag = 0;
    public String SiteUrl = "wydr.in";
    Context context;
    AlertDialog.Builder alertDialog;
    EditText otpcode;
    TextView resend;
    boolean registered;
    Button submit;
    ConnectionDetector cd;
    Helper helper = new Helper();
    IntentFilter intentFilter;
    private Timer timer;
    JSONObject json;
    SessionManager session;
    String phone;
    private String KEY_ERROR_ARRAY = "error";
    private String KEY_SUCCESS = "store_id";
    private DonutProgress donutProgress;
    private SmsBroadcastReceiver receiver;
    private ProgressDialog progress;
    int logged_count = 0;
    long startTime = 0;


    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.otpverify);
        progressStuff();
        init();
        setTimer();
        if (PermissionsUtils.verifyLocationPermissions(OTPCheck.this))
        {
            displayLocation();
        }
        PermissionsUtils.verifyLocationPermissions(OTPCheck.this);
        phone = helper.getDefaults("contact", getApplicationContext());
        intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        receiver = new SmsBroadcastReceiver();
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Loggedin, null, null, null, null);
        logged_count = cursor.getCount();
        OTPCounter = 0;
        resendCounter = 0;
        contact = helper.getDefaults("contact", getApplicationContext());
        registered = Boolean.parseBoolean(getIntent().getExtras().getString("registered"));
        resend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet())
                    new SendOTP().execute();
                else
                    new AlertDialogManager().showAlertDialog(OTPCheck.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
            }
        });

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) OTPCheck.this.getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("OTP Screen", "click", "OTP generated");
                // Build and send an App Speed.
                mTracker.send(new HitBuilders.TimingBuilder().setCategory("OTP Screen - Submit Event").setValue(System.currentTimeMillis() - startTime).build());
                /*******************************ISTIAQUE***************************************/

                if (otpcode.length() != 5) {
                    new AlertDialogManager().showAlertDialog(OTPCheck.this, getResources().getString(R.string.alert), getResources().getString(R.string.invalid_otp));
                } else {
                    if (cd.isConnectingToInternet())
                        new VerifyOTP().execute();
                    else
                        new AlertDialogManager().showAlertDialog(OTPCheck.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }

            }
        });
        helper.setDefaults("CategoryFlag", "1", OTPCheck.this);
    }

    private void displayLocation()
    {
        GPSTracker gpsTracker = new GPSTracker(OTPCheck.this);
        //Log.i("GPS-latitude", gpsTracker.getLatitude() + "");
        //Log.i("GPS-getLongitude", gpsTracker.getLongitude() + "");
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    city1 = addresses.get(0).getLocality();
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("OTPCheck");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void progressStuff()
    {
        cd = new ConnectionDetector(OTPCheck.this);
        progress = new ProgressDialog(OTPCheck.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        session = new SessionManager(OTPCheck.this);
        alertDialog = new AlertDialog.Builder(OTPCheck.this);
    }


    private void setTimer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    volatile boolean running = true;

                    @Override
                    public void run() {
                        if (!running) return;
                        donutProgress.setProgress(donutProgress.getProgress() + 1);
                        counter++;
//                        //Log.e("counter", counter + "");
                        if (counter == 60)
                            timer.cancel();

                    }
                });

            }
        }, 1000, 1000);
    }


    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        progress.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (progress != null && progress.isShowing())
            progress.dismiss();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    private void init() {
        donutProgress = (DonutProgress) findViewById(R.id.donut_progress);
        otpcode = (EditText) findViewById(R.id.otpcode);
        resend = (TextView) findViewById(R.id.resendotp);
        submit = (Button) findViewById(R.id.submitbtn);
        context = OTPCheck.this;
    }


    public void verifyOtp_restclient()
    {
        String error = "";
        int flag = 0, navigate = 0;
        JSONObject table = new JSONObject();
        Intent upanel;
        String gcm_id;
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }

        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        gcm_id = new IdHolder(getApplicationContext()).getToken();
        progress.setMessage(getResources().getString(R.string.verifying));
        progress.show();
        try
        {
            table.put("phone", contact);
            table.put("login", registered);
            table.put("otp_code", otpcode.getText());
            table.put("android_appversion_name",version);
            table.put("osname", Build.VERSION.RELEASE);
            table.put("gcm_id", gcm_id);
            Log.d("JSONotp", table.toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.v_otp(table, helper.getB64Auth(OTPCheck.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Response<JsonElement> response) {
                if(response.isSuccess())
                {
                    try {
                        json = new JSONObject(response.body().toString());
                        Log.d("otpjson",json.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }


    public JSONObject otpjson(JSONObject jsonObject)
    {
        String error = "";
        int flag = 0, navigate = 0;
        JSONObject table = new JSONObject();
        Intent upanel;

        if (json.has(KEY_ERROR))
        {
            JSONArray arr = null;
            try {
                arr = json.getJSONArray("error");
                for (int i = 0; i < arr.length(); i++) {
                    error = error + arr.getString(i) + "\n";
                }
                flag = 2;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        } else if (json.has(KEY_ID)) {

            try {
                JSONObject js1 = json.getJSONObject("user");
                if (json.getJSONObject("user_data").getString("api_key").equalsIgnoreCase("") || json.getJSONObject("user_data").getString("status").equalsIgnoreCase("D")) {
                    Log.i("OTPCHECK", json.getJSONObject("user_data").getString("api_key"));
                    Log.i("OTPCHECK", json.getJSONObject("user_data").getString("api_key"));
                    navigate = 2;
                    upanel = new Intent(OTPCheck.this, Login.class);

                } else {
                    navigate = 1;
                    upanel = new Intent(OTPCheck.this, WalkThroughActivity.class);
                    helper.setDefaults("SyncFlag", "1", OTPCheck.this);
                    helper.setDefaults("Flag", "1", OTPCheck.this);
                    if (js1.has("user_id"))
                        helper.setDefaults("user_id", js1.getString("user_id"), getApplicationContext());
                    if (js1.has("company_id"))
                        helper.setDefaults("company_id", js1.getString("company_id"), getApplicationContext());

                    if (json.has("user_data")) {
                        JSONObject js = json.getJSONObject("user_data");
                        ContentValues values = new ContentValues();
                        String name = "";
                        if (js.has("user_login"))
                            helper.setDefaults("login_id", js.getString("user_login"), getApplicationContext());
                        if (js.has("chat_server_password"))
                            helper.setDefaults("login_password", js.getString("chat_server_password"), getApplicationContext());
                        if (js.has("firstname")) {
                            helper.setDefaults("firstname", js.getString("firstname"), getApplicationContext());
                            name = name + js.getString("firstname");
                        }

                        if (js.has("lastname")) {
                            helper.setDefaults("lastname", js.getString("lastname"), getApplicationContext());
                            name = name + " " + js.getString("lastname");
                        }
                        values.put(LoginDB.KEY_NAME, name);
                        if (js.has("company")) {
                            values.put(LoginDB.KEY_COMPANY, js.getString("company"));
                            helper.setDefaults("company", js.getString("company"), getApplicationContext());
                        }

                        if (js.has("email")) {
                            values.put(LoginDB.KEY_EMAIL, js.getString("email"));
                            helper.setDefaults("email", js.getString("email"), getApplicationContext());
                        }

                        if (js.has("b_city")) {
                            values.put(LoginDB.KEY_CITY, js.getString("b_city"));
                            helper.setDefaults("b_city", js.getString("b_city"), getApplicationContext());
                        }
                        if (js.has("phone")) {
                            values.put(LoginDB.KEY_PHONE, js.getString("phone"));

                        }
                        if (js.has("b_address")) {
                            values.put(LoginDB.KEY_ADDRESS, js.getString("b_address"));

                        }
                        if (js.has("b_state")) {
                            values.put(LoginDB.KEY_STATE, js.getString("b_state"));

                        }
                        if (js.has("user_id")) {
                            values.put(LoginDB.KEY_USERID, js.getString("user_id"));
                        }
                        helper.setDefaults("is_root", js.optString("is_root"), OTPCheck.this.getApplicationContext());
                        Log.d("IS ROOT", js.optString("is_root"));

                        values.put(LoginDB.KEY_PASSWORD, " ");
                        values.put(LoginDB.KEY_CATEGORY, " ");
                        values.put(LoginDB.KEY_SCOPE, " ");
                        values.put(LoginDB.KEY_PAN, " ");
                        values.put(LoginDB.KEY_TIN, " ");
                        values.put(LoginDB.KEY_BUSINESSTYPE, " ");
                        values.put(LoginDB.KEY_INTERESTED, " ");

                        getContentResolver().insert(MyContentProvider.CONTENT_URI_Login, values);
                        getContentResolver().delete(MyContentProvider.CONTENT_URI_Loggedin, null, null);

                        ContentValues values2 = new ContentValues();
                        values2.put(LoggedIn.KEY_IsLogin, true);
                        getContentResolver().insert(MyContentProvider.CONTENT_URI_Loggedin, values2);

                        ContentValues values1 = new ContentValues();
                        values1.put(Authentication.login, js.getString("email"));
                        values1.put(Authentication.api_key, js.getString("api_key"));
                        getContentResolver().insert(MyContentProvider.CONTENT_URI_AUTHENTICATION, values1);

                    }
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } else if (json.has("message") && !(json.has(KEY_ID))) {
            // upanel = new Intent(getApplicationContext(), Registration.class);
            new Register().execute();
            helper.setDefaults("SyncFlag", "1", OTPCheck.this);
            helper.setDefaults("Flag", "1", OTPCheck.this);
        } else if (json.has("error")) {

            try {
                error = json.getString("error");
                flag = 2;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } else {
            flag = 1;
        }
        return json;

    }

    private class VerifyOTP extends AsyncTask<String, String, JSONObject>
    {
        public String error = "";
        public int flag = 0, navigate = 0;
        JSONObject table = new JSONObject();
        Intent upanel;
        String gcm_id;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            PackageInfo pInfo = null;

            try
            {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            }

            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }

            String version = pInfo.versionName;
            gcm_id = new IdHolder(getApplicationContext()).getToken();
            progress.setMessage(getResources().getString(R.string.verifying));
            progress.show();

            try
            {
                table.put("phone", contact);
                table.put("login", registered);
                table.put("otp_code", otpcode.getText());
                table.put("android_appversion_name",version);
                table.put("osname", Build.VERSION.RELEASE);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args)
        {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            try {
                table.put("gcm_id", gcm_id);
                Log.d("JSONappname", table.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            json = userFunction.sendOtpNumber("POST", table, getApplicationContext());
            if (json != null) {
                Log.i("OTPCJECL", json.toString());
                if (json.has(KEY_ERROR)) {
                    JSONArray arr = null;
                    try {
                        arr = json.getJSONArray("error");
                        for (int i = 0; i < arr.length(); i++) {
                            error = error + arr.getString(i) + "\n";
                        }
                        flag = 2;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } else if (json.has(KEY_ID)) {

                    try {
                        JSONObject js1 = json.getJSONObject("user");
                        if (json.getJSONObject("user_data").getString("api_key").equalsIgnoreCase("") || json.getJSONObject("user_data").getString("status").equalsIgnoreCase("D")) {
                            Log.i("OTPCHECK", json.getJSONObject("user_data").getString("api_key"));
                            Log.i("OTPCHECK", json.getJSONObject("user_data").getString("api_key"));
                            navigate = 2;
                            upanel = new Intent(OTPCheck.this, Login.class);

                        } else {
                            navigate = 1;
                            upanel = new Intent(OTPCheck.this, WalkThroughActivity.class);
                            helper.setDefaults("SyncFlag", "1", OTPCheck.this);
                            helper.setDefaults("Flag", "1", OTPCheck.this);
                            if (js1.has("user_id"))
                                helper.setDefaults("user_id", js1.getString("user_id"), getApplicationContext());
                            if (js1.has("company_id"))
                                helper.setDefaults("company_id", js1.getString("company_id"), getApplicationContext());

                            if (json.has("user_data")) {
                                JSONObject js = json.getJSONObject("user_data");
                                ContentValues values = new ContentValues();
                                String name = "";
                                if (js.has("user_login"))
                                    helper.setDefaults("login_id", js.getString("user_login"), getApplicationContext());
                                if (js.has("chat_server_password"))
                                    helper.setDefaults("login_password", js.getString("chat_server_password"), getApplicationContext());
                                if (js.has("firstname")) {
                                    helper.setDefaults("firstname", js.getString("firstname"), getApplicationContext());
                                    name = name + js.getString("firstname");
                                }

                                if (js.has("lastname")) {
                                    helper.setDefaults("lastname", js.getString("lastname"), getApplicationContext());
                                    name = name + " " + js.getString("lastname");
                                }
                                values.put(LoginDB.KEY_NAME, name);
                                if (js.has("company")) {
                                    values.put(LoginDB.KEY_COMPANY, js.getString("company"));
                                    helper.setDefaults("company", js.getString("company"), getApplicationContext());
                                }

                                if (js.has("email")) {
                                    values.put(LoginDB.KEY_EMAIL, js.getString("email"));
                                    helper.setDefaults("email", js.getString("email"), getApplicationContext());
                                }

                                if (js.has("b_city")) {
                                    values.put(LoginDB.KEY_CITY, js.getString("b_city"));
                                    helper.setDefaults("b_city", js.getString("b_city"), getApplicationContext());
                                }
                                if (js.has("phone")) {
                                    values.put(LoginDB.KEY_PHONE, js.getString("phone"));

                                }
                                if (js.has("b_address")) {
                                    values.put(LoginDB.KEY_ADDRESS, js.getString("b_address"));

                                }
                                if (js.has("b_state")) {
                                    values.put(LoginDB.KEY_STATE, js.getString("b_state"));

                                }
                                if (js.has("user_id")) {
                                    values.put(LoginDB.KEY_USERID, js.getString("user_id"));
                                }
                                helper.setDefaults("is_root", js.optString("is_root"), OTPCheck.this.getApplicationContext());
                                Log.d("IS ROOT", js.optString("is_root"));

                                values.put(LoginDB.KEY_PASSWORD, " ");
                                values.put(LoginDB.KEY_CATEGORY, " ");
                                values.put(LoginDB.KEY_SCOPE, " ");
                                values.put(LoginDB.KEY_PAN, " ");
                                values.put(LoginDB.KEY_TIN, " ");
                                values.put(LoginDB.KEY_BUSINESSTYPE, " ");
                                values.put(LoginDB.KEY_INTERESTED, " ");

                                getContentResolver().insert(MyContentProvider.CONTENT_URI_Login, values);
                                getContentResolver().delete(MyContentProvider.CONTENT_URI_Loggedin, null, null);

                                ContentValues values2 = new ContentValues();
                                values2.put(LoggedIn.KEY_IsLogin, true);
                                getContentResolver().insert(MyContentProvider.CONTENT_URI_Loggedin, values2);

                                ContentValues values1 = new ContentValues();
                                values1.put(Authentication.login, js.getString("email"));
                                values1.put(Authentication.api_key, js.getString("api_key"));
                                getContentResolver().insert(MyContentProvider.CONTENT_URI_AUTHENTICATION, values1);

                            }
                        }

                    }

                    catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }
                }

                else if (json.has("message") && !(json.has(KEY_ID)))
                {
                   // upanel = new Intent(getApplicationContext(), Registration.class);
                    new Register().execute();
                    helper.setDefaults("SyncFlag", "1", OTPCheck.this);
                    helper.setDefaults("Flag", "1", OTPCheck.this);
                } else if (json.has("error")) {

                    try {
                        error = json.getString("error");
                        flag = 2;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                flag = 1;
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();

            /************** If condition added by Istiaque due to crash *****************************/
            if (OTPCheck.this != null && !OTPCheck.this.isFinishing()) {

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
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(error);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
                if (flag == 0) {

                    if (upanel != null) {
                        helper.setDefaults("coach_flag", "0", OTPCheck.this);
                        if (navigate == 2) {
                            alertDialog.setTitle(getResources().getString(R.string.sorry));
                            alertDialog.setMessage(getResources().getString(R.string.no_api_key));
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(upanel);
                                    finish();
                                }
                            });
                            alertDialog.show();

                        } else if (navigate == 1) {
                            if (cd.isConnectingToInternet())
                            {//ISTIAQUE
                                new GetUserDetails().execute();
                            }

                            else
                            {
                                new AlertDialogManager().showAlertDialog(OTPCheck.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                            }
                        } else {
                            startActivity(upanel);
                            finish();
                        }
                    }
                }

            }
        }
    }

    private class SendOTP extends AsyncTask<String, String, JSONObject> {


        public int flag2 = 0;
        JSONObject table = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage(getResources().getString(R.string.sending));
            progress.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser parser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phone", contact));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "otp", "GET", params, OTPCheck.this);

            /*********************** ISTIAQUE ***************************/
            if (json.has("401")) {
                session.logoutUser();
            }
            /*********************** ISTIAQUE ***************************/

            if (json != null) {
                if (json.has("error"))
                    flag2 = 2;
            } else {
                flag2 = 1;
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json)
        {
            try
            {
                //Log.e("KEY_SUCCESS", json.getString(KEY_REGISTERED));
                progress.dismiss();
                if (flag2 == 1) {
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (flag2 == 2) {
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    alertDialog.setMessage(json.getString("error"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (json.getString(KEY_REGISTERED) != null) {

                    String reg = json.getString(KEY_REGISTERED);
                    Intent upanel = new Intent(getApplicationContext(), OTPCheck.class);
                    upanel.putExtra("registered", reg);
                    upanel.putExtra("contact", contact);
                    upanel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(upanel);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public class SmsBroadcastReceiver extends BroadcastReceiver {
        public static final String SMS_BUNDLE = "pdus";

        @Override
        public void onReceive(Context context, Intent intent) {
//            Bundle intentExtras = intent.getExtras();
//            if (intentExtras != null) {
//                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
//
//                for (Object sm : sms) {
//                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sm);
//                    String smsBody = smsMessage.getMessageBody();
//
//                    String smscode = smsBody.replaceAll("[^0-9]", "");
//                    if (smsBody.startsWith(getResources().getString(R.string.one_time_password))) {
//                        otpcode.setText(smscode);
//                        submit.performClick();
//                    }
//
//                }
//            }
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    String smscode = messageBody.replaceAll("[^0-9]", "");
                    if (messageBody.startsWith(getResources().getString(R.string.one_time_password))) {
                        otpcode.setText(smscode);
                        // Log.i(TAG,"otp check");
                        if (!isFinishing())
                            submit.performClick();
                    }
                }
            }
        }
    }

    private class GetUserDetails extends AsyncTask<String, String, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progress.setMessage(getResources().getString(R.string.loading));
            progress.show();
        }

        @Override
        protected Boolean doInBackground(String... args)
        {
            return GetUserBuisDetails(helper.getDefaults("user_id", OTPCheck.this));
        }

        @Override
        protected void onPostExecute(Boolean flag1) {
            progress.dismiss();
            if (flag1) {

                startActivity(new Intent(OTPCheck.this, WalkThroughActivity.class));
                finish();
            } else {
                alertDialog.setTitle(getResources().getString(R.string.sorry));
                alertDialog.setMessage(getResources().getString(R.string.server_error));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        }

        public Boolean GetUserCategories(String cat_id) {

            String KEY_SUCCESS = "categories";
            ArrayList<CategoryDataModal> catdata;
            JSONParser parser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", cat_id));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + cat_id + "/categories", "GET", params, OTPCheck.this);
            try {
                if (json != null) {
                    if (json.has(KEY_SUCCESS)) {
                        catdata = new ArrayList<>();
                        JSONObject childJSONObject = json.getJSONObject(KEY_SUCCESS);
                        Iterator<?> keys = childJSONObject.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject js = childJSONObject.getJSONObject(key);
                            CategoryDataModal cd1 = new CategoryDataModal();
                            cd1.setId(js.getString("category_id"));
                            cd1.setName(js.getString("category"));
                            cd1.setParentid(js.getString("parent_id"));
                            cd1.setProduct_count(js.getString("product_count"));
                            if (js.has("subcategories"))
                                cd1.setHas_child(true);
                            else
                                cd1.setHas_child(false);
                            catdata.add(cd1);
                            if (js.has("subcategories")) {
                                JSONArray js1 = js.getJSONArray("subcategories");
                                for (int k = 0; k < js1.length(); k++) {
                                    JSONObject js2 = js1.getJSONObject(k);
                                    CategoryDataModal cd2 = new CategoryDataModal();
                                    cd2.setId(js2.getString("category_id"));
                                    cd2.setName(js2.getString("category"));
                                    cd2.setParentid(js2.getString("parent_id"));
                                    cd2.setProduct_count(js2.getString("product_count"));
                                    if (js2.has("subcategories"))
                                        cd2.setHas_child(true);
                                    else
                                        cd2.setHas_child(false);
                                    catdata.add(cd2);
                                    if (js2.has("subcategories")) {
                                        JSONArray js3 = js2.getJSONArray("subcategories");
                                        for (int l = 0; l < js3.length(); l++) {
                                            JSONObject js4 = js3.getJSONObject(l);
                                            CategoryDataModal cd3 = new CategoryDataModal();
                                            cd3.setId(js4.getString("category_id"));
                                            cd3.setName(js4.getString("category"));
                                            cd3.setParentid(js4.getString("parent_id"));
                                            cd3.setProduct_count(js4.getString("product_count"));
                                            if (js4.has("subcategories"))
                                                cd3.setHas_child(true);
                                            else
                                                cd3.setHas_child(false);
                                            catdata.add(cd3);
                                            if (js4.has("subcategories")) {
                                                JSONArray js6 = js4.getJSONArray("subcategories");
                                                for (int t = 0; t < js6.length(); t++) {
                                                    JSONObject js7 = js6.getJSONObject(t);
                                                    CategoryDataModal cd4 = new CategoryDataModal();
                                                    cd4.setId(js7.getString("category_id"));
                                                    cd4.setName(js7.getString("category"));
                                                    cd4.setParentid(js7.getString("parent_id"));
                                                    cd4.setProduct_count(js7.getString("product_count"));
                                                    if (js7.has("subcategories"))
                                                        cd4.setHas_child(true);
                                                    else
                                                        cd4.setHas_child(false);
                                                    Log.i(TAG, "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" + cd4.getProduct_count());
                                                    catdata.add(cd4);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (catdata != null) {
                            for (int s = 0; s < catdata.size(); s++) {
                                ContentValues values = new ContentValues();
                                values.put(MyCategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                                values.put(MyCategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                                values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                                values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                                values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                                values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));
                                //   Log.i(TAG, "DATA -- > " + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getHas_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                                getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);
                            }
                        }
                    }

                    /*********************** ISTIAQUE ***************************/
                    if (json.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/

                    return true;

                } else {
                    return false;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        private Boolean GetUserBuisDetails(String user_id)
        {
            Boolean user_cat = true;
            String error = "";
            JSONParser parser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/business/" + user_id, "GET", params, OTPCheck.this);
            if (json != null) {
                if (json.has("is_error")) {
                    JSONArray arr = null;

                    try {
                        arr = json.getJSONArray("error");
                        for (int i = 0; i < arr.length(); i++) {
                            error = error + arr.getString(i) + "\n";
                        }
                        alertDialog.setTitle(getResources().getString(R.string.error));
                        alertDialog.setMessage(error);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();

                        return false;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    if (json.has("business_detail")) {

                        try {
                            JSONObject js = json.getJSONObject("business_detail");
                            ContentValues values = new ContentValues();

                            if (js.has("company")) {
                                values.put(LoginDB.KEY_COMPANY, js.getString("company"));
                                helper.setDefaults("company", js.getString("company"), getApplicationContext());
                            }
                            if (js.has("address")) {
                                values.put(LoginDB.KEY_ADDRESS, js.getString("address"));
                            }
                            if (js.has("store_visibility")) {
                                values.put(LoginDB.KEY_SCOPE, js.getString("store_visibility"));

                            }
                            if (js.has("city")) {
                                values.put(LoginDB.KEY_CITY, js.getString("city"));

                            }
                            if (js.has("store_type")) {
                                values.put(LoginDB.KEY_BUSINESSTYPE, js.getString("store_type"));

                            }

                            if (js.has("state")) {
                                values.put(LoginDB.KEY_STATE, js.getString("state"));

                            }
                            if (js.has("pan_num")) {
                                values.put(LoginDB.KEY_PAN, js.getString("pan_num"));

                            }
                            if (js.has("tan_num")) {
                                values.put(LoginDB.KEY_TIN, js.getString("tan_num"));

                            }
                            if (js.has("store_interest")) {
                                values.put(LoginDB.KEY_INTERESTED, js.getString("store_interest"));

                            }

                            /************************* Added by Istiaque to get Pincode of existing user ********************/
                            if(js.has("zipcode")) {
                                values.put(LoginDB.KEY_PINCODE, js.getString("zipcode"));
                            }
                            /*************************************************************************************************/

                            JSONArray jsonArray = js.getJSONArray("category_ids");
                            if (jsonArray.length() == 1) {
                                user_cat = !jsonArray.getString(0).equalsIgnoreCase("0");
                            }
                            values.put(LoginDB.KEY_CATEGORY, "");
                            values.put(LoginDB.KEY_PASSWORD, "");
                            values.put(LoginDB.KEY_PARENT_CATEGORY, "");
                            int b = getContentResolver().update(MyContentProvider.CONTENT_URI_Login, values, LoginDB.KEY_USERID + "=?", new String[]{helper.getDefaults("user_id", getApplicationContext())});
                            if (b == 0) {
                                Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI_Login, values);
                                if (uri != null) {
                                    if (user_cat) {
                                        if (GetUserCategories(helper.getDefaults("company_id", OTPCheck.this))) {
                                            helper.setDefaults("sync_flag", "true", OTPCheck.this);
                                            return true;
                                        } else
                                            return false;
                                    }
                                } else {
                                    Log.e(TAG, "unable to insert into login table");
                                    return false;
                                }
                            }

                            else
                            {
                                if (user_cat)
                                {
                                    return GetUserCategories(helper.getDefaults("company_id", OTPCheck.this));
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }


                }
                /*********************** ISTIAQUE ***************************/
                if (json.has("401")) {
                    session.logoutUser();
                }
                /*********************** ISTIAQUE ***************************/

            } else {
                alertDialog.setTitle(getResources().getString(R.string.sorry));
                alertDialog.setMessage(getResources().getString(R.string.server_error));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

                return false;
            }
            return true;
        }
    }
    //---------------------------------akshay code starts----------------------------------------------//
    public String addemail()
    {
        String email = null;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
        for (Account account : accounts) {
            Log.d("email",account.name);
            if (emailPattern.matcher(accounts[0].name).matches()) {
                email = accounts[0].name;

            }
        }

        return "vishal.gupta.14cs@bml.edu.in";//email;

    }
    public String addname()
    {
        //String[] parts = addemail().split("@");
        return "Vishal Gupta";//parts[0];
    }

    private class Register extends AsyncTask<String, String, JSONObject>
    {
        public String error = "";
        public boolean validcondtion = false;
        JSONObject table = new JSONObject();
        AlertDialog.Builder alertDialog;
        int flag = 0;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(OTPCheck.this);
/*
            progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setMessage("Creating User.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
*/

          /*  if (name.getText().toString().contains(" "))
            {
                fname = name.getText().subSequence(0, name.getText().toString().indexOf(" ")).toString();
                lname = name.getText().subSequence(name.getText().toString().indexOf(" ") + 1, name.getText().length()).toString();
            }

            else
            {
                fname = name.getText().toString();
                lname = " ";
            }*/

            //progress.setTitle("Registering");
            //progress.show();
            try
            {
                table.put("company",addname());
                table.put("email", addemail());
                table.put("phone", helper.cleanPhoneNo(phone, getApplicationContext()));
                table.put("storefront", SiteUrl);
                table.put("categories", "");
                table.put("firstname", addname());
                table.put("lastname", "");
                table.put("password", "Welcome123@");
                table.put("address", "");
                table.put("state", "");
                table.put("city",city1);
                table.put("country", "");
                table.put("zipcode", "");
                table.put("login", "true");
                table.put("state",city1);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args)
        {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            json = userFunction.register(table, OTPCheck.this);
            if (json != null)
            {
                if (json.has("error"))
                {
                    try
                    {
                        JSONArray arr = json.getJSONArray(KEY_ERROR_ARRAY);
                        for (int i = 0; i < arr.length(); i++) {
                            error = error + arr.getString(i) + ".\n";
                        }
                        //   error = json.getString("error");
                        Log.i("REGISTRATION", error);
                        flag = 2;
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (json.has(KEY_ERROR))
                {
                    try
                    {
                        JSONArray arr = json.getJSONArray(KEY_ERROR_ARRAY);
                        for (int i = 0; i < arr.length(); i++)
                        {
                            error = error + arr.getString(i) + ".\n";
                        }
                        flag = 1;
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

                else if (json.has("user_id")) {
                    try {
                        validcondtion = true;
                        if (json.has("user_id"))
                            helper.setDefaults("user_id", json.getString("user_id"), getApplicationContext());

                        if (json.has("store_id"))
                            helper.setDefaults("company_id", json.getString("store_id"), getApplicationContext());

                        if (json.has("user_data")) {
                            JSONObject js = json.getJSONObject("user_data");

                            if (js.has("user_login"))
                                helper.setDefaults("login_id", js.getString("user_login"), getApplicationContext());
                            if (js.has("chat_server_password"))
                                helper.setDefaults("login_password", js.getString("chat_server_password"), getApplicationContext());
                            if (js.has("firstname"))
                                helper.setDefaults("firstname", js.getString("firstname"), getApplicationContext());
                            if (js.has("lastname"))
                                helper.setDefaults("lastname", js.getString("lastname"), getApplicationContext());
                            if (js.has("company"))
                                helper.setDefaults("company", js.getString("company"), getApplicationContext());
                            if (js.has("email"))
                                helper.setDefaults("email", js.getString("email"), getApplicationContext());
                            if (js.has("b_city"))
                                helper.setDefaults("b_city", js.getString("b_city"), getApplicationContext());
                            helper.setDefaults("is_root", js.optString("is_root"), OTPCheck.this.getApplicationContext());
                            ContentValues values1 = new ContentValues();
                            values1.put(Authentication.login, js.getString("email"));
                            values1.put(Authentication.api_key, js.getString("api_key"));
                            getContentResolver().insert(MyContentProvider.CONTENT_URI_AUTHENTICATION, values1);

                            Log.i("REGISTRATION api_key", js.getString("api_key"));
                            Log.i("REGISTRATION email", js.getString("email"));
                            Log.d("IS ROOT", js.optString("is_root"));
                        }
                        ContentValues values = new ContentValues();
                        values.put(LoginDB.KEY_PHONE, helper.cleanPhoneNo(phone, getApplicationContext())); // Contact Name
                        values.put(LoginDB.KEY_NAME, addname()); // Contact Phone
                        values.put(LoginDB.KEY_EMAIL, addemail());
                        values.put(LoginDB.KEY_CITY, city1);
                        values.put(LoginDB.KEY_PASSWORD, "Welcome123@");
                        values.put(LoginDB.KEY_USERID, json.getString("user_id"));
                        values.put(LoginDB.KEY_COMPANY, addname());
                        values.put(LoginDB.KEY_CATEGORY, "");
                        values.put(LoginDB.KEY_SCOPE, "");
                        values.put(LoginDB.KEY_STATE, "");
                        values.put(LoginDB.KEY_PAN, "");
                        values.put(LoginDB.KEY_TIN, "");
                        values.put(LoginDB.KEY_BUSINESSTYPE, "");
                        values.put(LoginDB.KEY_INTERESTED, "");
                        values.put(LoginDB.KEY_ADDRESS, "");

                        helper.setDefaults("SyncFlag", "1", OTPCheck.this);
                        getContentResolver().insert(MyContentProvider.CONTENT_URI_Login, values);


                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

                else if (json.has("message")) {
                    try {
                        error = json.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    flag = 1;


                }
            } else {
                flag = 3;

            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //progressDialog.dismiss();

           /* helper.setDefaults("SyncFlag", "1", OTPCheck.this);
            helper.setDefaults("Flag", "1", OTPCheck.this);*/
            helper.setDefaults("coach_flag", "0", OTPCheck.this);

            if (OTPCheck.this != null && !OTPCheck.this.isFinishing()) {
              //  progress.dismiss();
                if (flag == 0) {
                    if (validcondtion) {
                        getContentResolver().delete(MyContentProvider.CONTENT_URI_Loggedin, null, null);
                        ContentValues values2 = new ContentValues();
                        values2.put(LoggedIn.KEY_IsLogin, true);
                        getContentResolver().insert(MyContentProvider.CONTENT_URI_Loggedin, values2);
                        startActivity(new Intent(OTPCheck.this, WalkThroughActivity.class));
                        finish();
                    }
                } else if (flag == 1) {
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    alertDialog.setMessage(error);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (flag == 2) {
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    alertDialog.setMessage(error);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (flag == 3) {
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }

}
