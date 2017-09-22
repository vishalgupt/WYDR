package wydr.sellers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.JSONParser;

/**
 * Created by Deepesh_pc on 02-12-2015.
 */
public class InviteUser extends AppCompatActivity implements View.OnClickListener {

    ConnectionDetector cd;
    Helper helper = new Helper();
    private EditText et_name, et_email, et_num;
    private Button btn_invite;
    private JSONParser jsonParser;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.invite_user));
        inistuff();
    }

    private void inistuff() {
        et_name = (EditText) findViewById(R.id.inv_name);
        et_email = (EditText) findViewById(R.id.inv_email);
        et_num = (EditText) findViewById(R.id.inv_number);
        btn_invite = (Button) findViewById(R.id.invite_button);
        cd = new ConnectionDetector(InviteUser.this);
        et_name.setOnClickListener(this);
        et_email.setOnClickListener(this);
        et_num.setOnClickListener(this);
        btn_invite.setOnClickListener(this);
        jsonParser = new JSONParser();
    }

    private boolean CheckValidation() {
        if (!ValidationUtil.isNull(et_name.getText().toString())) {
            if (!ValidationUtil.isNull(et_email.getText().toString())) {
                if (!ValidationUtil.isNull(et_num.getText().toString())) {
                    if (ValidationUtil.isValidEmailId(et_email.getText().toString())) {
                        if (ValidationUtil.isValidMobile(et_num.getText().toString())) {
                            return true;
                        } else {
                            new AlertDialogManager().showAlertDialog(InviteUser.this, getResources().getString(R.string.oops), getResources().getString(R.string.invalid_mobile));
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                et_num.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                            } else {
                                et_num.setBackground(getResources().getDrawable(R.drawable.error_bar));
                            }
                            et_num.requestFocus();

                        }
                    } else {
                        new AlertDialogManager().showAlertDialog(InviteUser.this, getResources().getString(R.string.oops), getResources().getString(R.string.invalid_email));
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            et_email.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        } else {
                            et_email.setBackground(getResources().getDrawable(R.drawable.error_bar));
                        }
                        et_email.requestFocus();

                    }
                } else {
                    new AlertDialogManager().showAlertDialog(InviteUser.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_number));
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        et_num.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    } else {
                        et_num.setBackground(getResources().getDrawable(R.drawable.error_bar));
                    }
                    et_num.requestFocus();
                }
            } else {
                new AlertDialogManager().showAlertDialog(InviteUser.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_email));
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    et_email.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                } else {
                    et_email.setBackground(getResources().getDrawable(R.drawable.error_bar));
                }
                et_email.requestFocus();
            }


        } else {
            new AlertDialogManager().showAlertDialog(InviteUser.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_name));
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                et_name.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            } else {
                et_name.setBackground(getResources().getDrawable(R.drawable.error_bar));
            }
            et_name.requestFocus();

        }

        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite_button:

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Invite User", "onClick", "Submit Invite User details");
                /*******************************ISTIAQUE***************************************/

                if (CheckValidation()) {
                    if (cd.isConnectingToInternet()) {
                        // Log.i("Invite User--", "True");
                        new Invite().execute();
                    } else {
                        new AlertDialogManager().showAlertDialog(InviteUser.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
                break;
            case R.id.inv_name:
                helper.SetBack(et_name, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.inv_email:
                helper.SetBack(et_email, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.inv_number:
                helper.SetBack(et_num, getResources().getDrawable(R.drawable.code_bar));
                break;
        }
    }

    private class Invite extends AsyncTask<Void, Integer, String> {
        JSONObject table = new JSONObject();
        AlertDialog.Builder alertDialog;
        int flag = 0;
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progress = new ProgressDialog(InviteUser.this);
            progress.setMessage(getResources().getString(R.string.inviting));
            progress.setIndeterminate(false);
            progress.setCancelable(true);
            progress.show();
            alertDialog = new AlertDialog.Builder(InviteUser.this);

            try {
                table.put("company_id", helper.getDefaults("company_id", InviteUser.this));
                table.put("email", et_email.getText().toString());
                table.put("phone", et_num.getText().toString());
                table.put("status", "A");
                table.put("password", "subuser");
                table.put("firstname", et_name.getText().toString());
                table.put("user_type", "V");
                table.put("current_user_type","BSU");
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String responseString = "";
            JSONObject json = null;
            json = jsonParser.ProcessStatus_Invite(AppUtil.URL + "3.0/users", table,InviteUser.this);
            Log.i("json--", json.toString());
            if (json != null) {
                if (json.has("message")) {
                    try {
                        responseString = json.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (json.has("user_id")) {
                    responseString = getResources().getString(R.string.invited_success);
                }

            } else {
                flag = 1;

            }

//            Log.i("InviteUser", responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(final String result) {
            progress.dismiss();
            Log.i("RESPonse", result);
            if (flag != 0) {
                alertDialog.setTitle(getResources().getString(R.string.sorry));
                alertDialog.setMessage(getResources().getString(R.string.server_error));
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();


            } else {
                if (result.equalsIgnoreCase(getResources().getString(R.string.invited_success))) {
                    alertDialog.setTitle(getResources().getString(R.string.success));
                    alertDialog.setMessage(result);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    });
                    alertDialog.show();

                } else {
                    alertDialog.setTitle(getResources().getString(R.string.oops));
                    alertDialog.setMessage(result);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.show();
                }

            }

            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

                startActivity(new Intent(InviteUser.this, CartActivity.class));
            }
        });
        MenuItem sitem = menu.findItem(R.id.searchCart_search);
        sitem.setVisible(false);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("InviteUser");
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
}