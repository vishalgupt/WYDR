package wydr.sellers.registration;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
import java.util.Locale;
import java.util.regex.Pattern;

import wydr.sellers.R;
import wydr.sellers.WalkThroughActivity;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.PermissionsUtils;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.slider.Authentication;
import wydr.sellers.slider.LoggedIn;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.UserFunctions;


/**
 * Created by Deepesh_pc on 14-07-2015.
 */
@SuppressWarnings("ALL")
public class Registration extends AppCompatActivity implements OnClickListener
{
    public final String TAG = Registration.class.getSimpleName();
    public final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public boolean show = true;
    public SharedPreferences prefs;
    public String phone, fname, lname = "";
    public String SiteUrl = "wydr.in";
    TextView terms_condtns;
    ImageView showpass;
    Button submit;
    Context context;
    Helper helper = new Helper();
    ConnectionDetector cd;
    private EditText name, email, compname, pass;
    private AutoCompleteTextView city;
    private ProgressDialog progress;
    private boolean regflag = true;
    private Toolbar toolbar;
    private String KEY_ERROR = "is_error";
    private String KEY_ERROR_ARRAY = "error";
    private String KEY_SUCCESS = "store_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        iniStuff();
        progressStuff();
        cd = new ConnectionDetector(getApplicationContext());
        prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        phone = helper.getDefaults("contact", getApplicationContext());
        if (PermissionsUtils.verifyLocationPermissions(Registration.this)) {
            displayLocation();
        }
        //displayLocation();
        name.setOnClickListener(this);
        email.setOnClickListener(this);
        compname.setOnClickListener(this);
        pass.setOnClickListener(this);
    }

    public String addemail()
    {
        String email = null;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
        for (Account account : accounts)
        {
            if (emailPattern.matcher(account.name).matches())
            {
                email = account.name;
            }
        }
        return email;
    }




    private void progressStuff()
    {
        //cd = new ConnectionDetector(Registration.this);
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
    }

    public void launchURL()
    {
        Uri uri = Uri.parse(getResources().getString(R.string.terms_link));
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(launchBrowser);
    }

    private void iniStuff()
    {
        // TODO Auto-generated method stub
        terms_condtns = (TextView) findViewById(R.id.clickingtxt);
        terms_condtns.setText(Html.fromHtml(getResources().getString(R.string.btn_link_to_login)));
        terms_condtns.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                launchURL();
            }
        });
        terms_condtns.setMovementMethod(LinkMovementMethod.getInstance());
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        compname = (EditText) findViewById(R.id.compname);
        pass = (EditText) findViewById(R.id.password);
        city = (AutoCompleteTextView) findViewById(R.id.cityreg);
        final List<String> cities = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_cities_autosuggest, cities);
        city.setThreshold(2);
        city.setAdapter(adapter);
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().indexOf(",") != -1) {
                    int asterisk1 = s.toString().indexOf(',');
                    boolean hasTowAsterisks = asterisk1 != -1 && s.toString().indexOf(',', asterisk1 + 1) != -1;
                    if (hasTowAsterisks) {
                        city.setTag(s.subSequence(s.toString().indexOf(",") + 2, s.toString().indexOf(",", s.toString().indexOf(",") + 1)));
                        city.setText(s.subSequence(0, s.toString().indexOf(",")));
                    } else {
                        city.setTag(s.subSequence(0, s.toString().indexOf(",")));
                        city.setText(s.subSequence(0, s.toString().indexOf(",")));
                    }

                } else {
                    city.setTag("");
                }
                if (city.length() >= city.getThreshold()) {
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAUqbnkXE4z-TXleAIbqy8j3PyHjYqUzeg&types=(cities)&sensor=false&components=country:in&input=" + city.getText()).openConnection();
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

                                        adapter.add(cities_array.getJSONObject(i).getString("description"));
                                        cities.add(cities_array.getJSONObject(i).getString("description"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        showpass = (ImageView) findViewById(R.id.showpass);
        submit = (Button) findViewById(R.id.registernext);
        submit.setOnClickListener(this);
        showpass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registernext:

                if (CheckValidation())
                {
                    if (cd.isConnectingToInternet())
                    {
                        new Register().execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
                break;
            case R.id.showpass:

                if (show) {
                    int start = pass.getSelectionStart();
                    int end = pass.getSelectionEnd();
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass.setSelection(start, end);
                    show = false;
                } else {
                    int start = pass.getSelectionStart();
                    int end = pass.getSelectionEnd();
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setSelection(start, end);
                    show = true;
                }


                break;
            case R.id.name:
                helper.SetBack(name, getResources().getDrawable(R.drawable.edittxt));
                break;
            case R.id.email:
                helper.SetBack(email, getResources().getDrawable(R.drawable.edittxt));
                break;
            case R.id.compname:
                helper.SetBack(compname, getResources().getDrawable(R.drawable.edittxt));
                break;
            case R.id.password:
                helper.SetBack(pass, getResources().getDrawable(R.drawable.edittxt));
                break;
            case R.id.cityreg:
                helper.SetBack(city, getResources().getDrawable(R.drawable.edittxt));
                break;


        }
    }


    private boolean CheckValidation() {
        if (!ValidationUtil.isNull(name.getText().toString())) {
            if (!ValidationUtil.isNull(email.getText().toString())) {
                /*if (!ValidationUtil.isNull(pass.getText().toString())) {
                    if (!ValidationUtil.isNull(compname.getText().toString())) {*/
                if (!ValidationUtil.isNull(city.getText().toString()))
                {
                    if (!ValidationUtil.isValidEmailId(email.getText().toString()))
                    {
                        new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.invalid_email));
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            email.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        } else {
                            email.setBackground(getResources().getDrawable(R.drawable.error_bar));
                        }
                        email.setFocusable(true);
                        return false;
                    }
//                    if (pass.getText().toString().length() < 6) {
//                        new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.password_length));
//                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                            pass.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
//                        } else {
//                            pass.setBackground(getResources().getDrawable(R.drawable.error_bar));
//                        }
//                        pass.setFocusable(true);
//                        return false;
//                    }
                    return true;
                } else {
                    new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.empty_city));
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        city.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    } else {
                        city.setBackground(getResources().getDrawable(R.drawable.error_bar));
                    }
                    city.setFocusable(true);
                    return false;
                }
//                    } else {
//                        new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.empty_company));
//                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                            compname.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
//                        } else {
//                            compname.setBackground(getResources().getDrawable(R.drawable.error_bar));
//                        }
//                        compname.setFocusable(true);
//                        return false;
//                    }
//                } else {
//                    new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.empty_password));
//                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        pass.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
//                    } else {
//                        pass.setBackground(getResources().getDrawable(R.drawable.error_bar));
//                    }
//                    pass.setFocusable(true);
//                    return false;
//                }
            } else {
                new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.empty_email));
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    email.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                } else {
                    email.setBackground(getResources().getDrawable(R.drawable.error_bar));
                }
                email.setFocusable(true);
                return false;
            }
        } else {
            new AlertDialogManager().showAlertDialog(Registration.this, getResources().getString(R.string.alert), getResources().getString(R.string.empty_name));
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                name.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            }

            else {
                name.setBackground(getResources().getDrawable(R.drawable.error_bar));
            }
            name.setFocusable(true);
            return false;
        }

    }


    private void displayLocation()
    {
        GPSTracker gpsTracker = new GPSTracker(Registration.this);
        //Log.i("GPS-latitude", gpsTracker.getLatitude() + "");
        //Log.i("GPS-getLongitude", gpsTracker.getLongitude() + "");
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String city1 = "";

            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    city1 = addresses.get(0).getLocality();
                    city.setText(city1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        progress.dismiss();
    }

    private class Register extends AsyncTask<String, String, JSONObject>
    {
        public String error = "";
        public boolean validcondtion = false;
        JSONObject table = new JSONObject();
        AlertDialog.Builder alertDialog;
        int flag = 0;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(Registration.this);

            if (name.getText().toString().contains(" ")) {
                fname = name.getText().subSequence(0, name.getText().toString().indexOf(" ")).toString();
                lname = name.getText().subSequence(name.getText().toString().indexOf(" ") + 1, name.getText().length()).toString();
            } else {
                fname = name.getText().toString();
                lname = " ";
            }
            progress.setTitle("Registering");
            progress.show();

            try
            {
                if (!compname.getText().toString().trim().equalsIgnoreCase("")) {
                table.put("company", compname.getText().toString());
            }
                else
                {
                    table.put("company", fname);
                }
                table.put("email", email.getText().toString());
                table.put("phone", helper.cleanPhoneNo(phone, getApplicationContext()));
                table.put("storefront", SiteUrl);
                table.put("categories", "");
                table.put("firstname", fname);
                table.put("lastname", lname);
                table.put("password", "Welcome123@");
                table.put("address", "");
                table.put("state", "");
                table.put("city", city.getText().toString());
                table.put("country", "");
                table.put("zipcode", "");
                table.put("login", "true");
                table.put("state", city.getTag().toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            JSONObject json = null;
            json = userFunction.register(table, Registration.this);
            if (json != null) {
                if (json.has("error")) {
                    try {
                        JSONArray arr = json.getJSONArray(KEY_ERROR_ARRAY);
                        for (int i = 0; i < arr.length(); i++) {
                            error = error + arr.getString(i) + ".\n";
                        }
                        //   error = json.getString("error");
                        Log.i("REGISTRATION", error);
                        flag = 2;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (json.has(KEY_ERROR)) {
                    try {
                        JSONArray arr = json.getJSONArray(KEY_ERROR_ARRAY);
                        for (int i = 0; i < arr.length(); i++) {
                            error = error + arr.getString(i) + ".\n";
                        }
                        flag = 1;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (json.has("user_id")) {
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
                            helper.setDefaults("is_root", js.optString("is_root"), Registration.this.getApplicationContext());
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
                        values.put(LoginDB.KEY_NAME, name.getText().toString()); // Contact Phone
                        values.put(LoginDB.KEY_EMAIL, email.getText().toString());
                        values.put(LoginDB.KEY_CITY, city.getText().toString());
                        values.put(LoginDB.KEY_PASSWORD, pass.getText().toString());
                        values.put(LoginDB.KEY_USERID, json.getString("user_id"));
                        values.put(LoginDB.KEY_COMPANY, compname.getText().toString());
                        values.put(LoginDB.KEY_CATEGORY, "");
                        values.put(LoginDB.KEY_SCOPE, "");
                        values.put(LoginDB.KEY_STATE, "");
                        values.put(LoginDB.KEY_PAN, "");
                        values.put(LoginDB.KEY_TIN, "");
                        values.put(LoginDB.KEY_BUSINESSTYPE, "");
                        values.put(LoginDB.KEY_INTERESTED, "");
                        values.put(LoginDB.KEY_ADDRESS, "");

                        helper.setDefaults("SyncFlag", "1", Registration.this);
                        getContentResolver().insert(MyContentProvider.CONTENT_URI_Login, values);


                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } else if (json.has("message")) {
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

            if (Registration.this != null) {
                progress.dismiss();
                if (flag == 0) {
                    if (validcondtion) {
                        getContentResolver().delete(MyContentProvider.CONTENT_URI_Loggedin, null, null);
                        ContentValues values2 = new ContentValues();
                        values2.put(LoggedIn.KEY_IsLogin, true);
                        getContentResolver().insert(MyContentProvider.CONTENT_URI_Loggedin, values2);
                        startActivity(new Intent(Registration.this, WalkThroughActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Registration");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
