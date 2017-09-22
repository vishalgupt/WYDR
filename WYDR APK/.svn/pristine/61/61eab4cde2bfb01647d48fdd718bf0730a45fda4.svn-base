package wydr.sellers.registration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import wydr.sellers.R;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.WakeLocker;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.slider.MyContentProvider;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

import static wydr.sellers.activities.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static wydr.sellers.activities.CommonUtilities.EXTRA_MESSAGE;
import static wydr.sellers.activities.CommonUtilities.SENDER_ID;

public class Login extends AppCompatActivity implements View.OnClickListener {

    String reg = "";
    JSONObject table = new JSONObject();
    int flag = 0;
    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
            WakeLocker.release();
        }
    };
    public String KEY_SUCCESS = "registered";
    TextView count_code;
    String contact_number;
    int logged_count = 0;
    ConnectionDetector cd;
    JSONParser parser;
    Helper helper = new Helper();
    AsyncTask<Void, Void, Void> mRegisterTask;
    private EditText contact;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressStuff();
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Loggedin, null, null, null, null);
        logged_count = cursor.getCount();
        cursor.close();
        setContentView(R.layout.loginapp);

        if (cd.isConnectingToInternet()) {
            try {
                new ImageFetch().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialogManager().showAlertDialog(Login.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }

        init();
    }

    class ImageFetch extends AsyncTask<String,String,String >
    {
        @Override
        protected String doInBackground(String... strings) {
            Picasso.with(getApplicationContext())
                    .load(AppUtil.WalkthroughURL+"images/walkthrough/intro_scr_01.png")
                    .fetch();

            Picasso.with(getApplicationContext())
                    .load(AppUtil.WalkthroughURL+"images/walkthrough/intro_scr_02.png")
                    .fetch();

            Picasso.with(getApplicationContext())
                    .load(AppUtil.WalkthroughURL+"images/walkthrough/intro_scr_03.png")
                    .fetch();
            return null;
        }
    }

    private void progressStuff() {
        cd = new ConnectionDetector(Login.this);
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
    }

    private void init() {
        contact = (EditText) findViewById(R.id.EtOtp);
        Button login = (Button) findViewById(R.id.OTPNextBtn);
        login.setOnClickListener(this);
        try {
            registerGCM();
        } catch (Exception e) {
            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        count_code = (TextView) findViewById(R.id.country_code);

    }

    @Override
    public void onClick(View v)
    {
        if (contact.getText().length() != 10 || !ValidationUtil.isValidMobile(contact.getText().toString()))
        {
            new AlertDialogManager().showAlertDialog(Login.this, getResources().getString(R.string.alert), getResources().getString(R.string.invalid_mobile));
        }

        else
        {
            if (cd.isConnectingToInternet()) {
                progress.show();
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                contact_number = contact.getText().toString();
                RestClient.GitApiInterface service = RestClient.getClient();
                Call<JsonElement> call = service.sendOtp(contact_number, "application/json", "application/json");
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Response<JsonElement> response) {
                        progress.dismiss();
                        //     if (response.isSuccess()) {
                        // Log.i("LOGIN ",response.raw()+"");
                        JsonElement element = (JsonElement) response.body();
                        //Log.i("LOGIN ",response.body().toString()+"");
                        try {
                            if (element != null && element.isJsonObject()) {

                                JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                                if (json != null)
                                {
                                    Log.i("Login", json.toString());
                                    if (json.has(KEY_SUCCESS)) {
                                        Log.i("Login", "json.toString()");
                                        reg = json.getString(KEY_SUCCESS);
                                    } else if (json.has("error")) {
                                        flag = 2;
                                    }
                                }

                                else
                                {
                                    flag = 1;
                                }

                                try
                                {
                                    if (reg != null && reg != "") {
                                        getWindow().setSoftInputMode(
                                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                        );
                                        helper.setDefaults("contact", contact_number, getApplicationContext());
                                        Intent upanel = new Intent(getApplicationContext(), OTPCheck.class);
                                        upanel.putExtra("registered", reg);
                                        upanel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(upanel);
                                        finish();
                                    } else if (flag == 1) {
                                        alertDialog.setTitle(getResources().getString(R.string.sorry));
                                        alertDialog.setMessage(getResources().getString(R.string.server_error));
                                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                        alertDialog.show();
                                    } else if (flag == 2) {
                                        alertDialog.setTitle(getResources().getString(R.string.oops));
                                        alertDialog.setMessage(json.getString("error"));
                                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                        alertDialog.show();
                                    }

                                } catch (Exception e) {
                                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                                    alertDialog.show();
                                }
                            }
                            else {
                                if(Login.this!=null && !Login.this.isFinishing()) {
                                    new AlertDialogManager().showAlertDialog(Login.this,
                                            getResources().getString(R.string.error),
                                            getResources().getString(R.string.server_error));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }
                    }
                    // }

                    @Override
                    public void onFailure(Throwable t) {
                        if(Login.this!=null && !Login.this.isFinishing()) {
                        new AlertDialogManager().showAlertDialog(Login.this,
                                getResources().getString(R.string.error),
                                getResources().getString(R.string.server_error));}
                        progress.dismiss();
                    }
                });

                //new SendOTP().execute();
            } else {
                new AlertDialogManager().showAlertDialog(Login.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void registerGCM() {

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {

            GCMRegistrar.register(this, SENDER_ID);

        } else {

            if (GCMRegistrar.isRegisteredOnServer(this)) {
            } else {
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                if (cd.isConnectingToInternet())
                {//ISTIAQUE
                    mRegisterTask.execute(null, null, null);
                }

                else
                {
                    new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("LOGIN", "UnRegister Receiver Error" + e.getMessage());
        }
        super.onDestroy();
        progress.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Login");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

