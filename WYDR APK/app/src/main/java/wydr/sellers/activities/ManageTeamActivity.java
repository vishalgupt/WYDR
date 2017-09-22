package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ManageTeam;
import wydr.sellers.adapter.ManageTeamAdapter;
import wydr.sellers.gson.PrimaryUser;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by surya on 20/1/16.
 */
public class ManageTeamActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    ListView listView;
    Button addMember;
    Helper helper;
    String companyId, primary_id = "";
    ConnectionDetector cd;
    JSONParser parser;
    ProgressDialog progress;
    ArrayList<ManageTeam> arrayList;
    ManageTeamAdapter adapter;
    int index;
    android.app.AlertDialog.Builder alertDialog;
    public static final int FILTER_REQUEST = 101;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_team);
        progressStuff();
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Team");
        helper = new Helper();
        initUff();
        companyId = helper.getDefaults("company_id", getApplicationContext());
        listView = (ListView) findViewById(R.id.listViewTeamMember);
        addMember = (Button) findViewById(R.id.buttonAddMemberTeam);
        addMember.setOnClickListener(this);
        arrayList = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(this);
        adapter = new ManageTeamAdapter(ManageTeamActivity.this, arrayList);
        listView.setAdapter(adapter);

        if (cd.isConnectingToInternet()) {
            new LoadTeam().execute();
        } else {
            new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MANAGETEAM", arrayList.get(position).getName() + arrayList.get(position).getId());
                index = position;

                if (arrayList.get(position).getId().equalsIgnoreCase(primary_id)) {
                    alertDialog = new AlertDialog.Builder(ManageTeamActivity.this);
                    alertDialog.setTitle(getResources().getString(R.string.oops))
                            .setMessage(getResources().getString(R.string.already_primary))
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    alertDialog.show();
                } else {
                    alertDialog = new AlertDialog.Builder(ManageTeamActivity.this);
                    alertDialog.setTitle(getResources().getString(R.string.set_primary))
                            .setMessage(getResources().getString(R.string.wanna_set_primary))
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (cd.isConnectingToInternet()) {
                                        prepareRequest(arrayList.get(index).getId());
                                        //new MakePrimary().execute(arrayList.get(index).getId());
                                    } else {
                                        Toast.makeText(ManageTeamActivity.this, getResources().getString(R.string.no_internet_conn), Toast.LENGTH_LONG);

                                    }
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    alertDialog.show();
                }


                return false;
            }
        });

    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(this);
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddMemberTeam:
                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Manage Team", "Move", "InviteUser");
                /*******************************ISTIAQUE***************************************/

                startActivityForResult(new Intent(ManageTeamActivity.this, InviteUser.class), FILTER_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case FILTER_REQUEST: {
                arrayList.clear();
                adapter.notifyDataSetChanged();
                if (cd.isConnectingToInternet())
                {//ISTIAQUE
                    new LoadTeam().execute();
                }

                else
                {
                    new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }
                break;
            }
        }
    }


    private class LoadTeam extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();

        }

        @Override
        protected String doInBackground(String... args) {


            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = null;
            params.add(new BasicNameValuePair("company_id", companyId));
            params.add(new BasicNameValuePair("get_image", "true"));
            Log.d("param", params.toString());
            json = parser.makeHttpRequest(AppUtil.URL + "3.0/users", "GET", params, ManageTeamActivity.this);

            if (json != null) {
                Log.d("JSS", json.toString());
                if (json.has("users")) {
                    try {
                        JSONArray array = json.getJSONArray("users");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            ManageTeam team = new ManageTeam();
                            team.setId(object.optString("user_id"));
                            team.setName(object.optString("firstname") + " " + object.optString("lastname"));
                            team.setPhone(object.optString("phone"));
                            if (object.has("main_pair") && object.get("main_pair") instanceof JSONObject) {
                                team.setThumb(object.getJSONObject("main_pair").getJSONObject("icon").getString("image_path"));
                            } else {
                                team.setThumb("");
                            }

                            String isPrimary = object.optString("is_primary");
                            if (isPrimary.equalsIgnoreCase("Y")) {
                                primary_id = object.optString("user_id");
                                team.setPrimary(true);
                            } else {
                                team.setPrimary(false);
                            }

                            String iOwner = object.optString("is_root");
                            if (iOwner.equalsIgnoreCase("Y")) {
                                team.setOwner(true);
                            } else {
                                team.setOwner(false);
                            }

                            arrayList.add(team);
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
            if (ManageTeamActivity.this != null && !ManageTeamActivity.this.isFinishing()) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Log.i("PRIMRY id", primary_id);
                adapter.notifyDataSetChanged();

            }

        }
    }

    private void initUff()
    {
        ArrayList<String>ufList;
        Helper helper = new Helper();
        ImageView iv_bussiness=(ImageView)findViewById(R.id.uf_manageteam);
        LinearLayout ll= (LinearLayout)findViewById(R.id.ll_manageteam);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(),Catalog.class);
                startActivity(intent);
            }
        });
        if(ufList.contains(AppUtil.TAG_Manage_Team))
        {
            ll.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Manage_Team+"_photo",getApplicationContext()))
                    .into(iv_bussiness);
        }
        else
        {
            ll.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

                startActivity(new Intent(ManageTeamActivity.this, CartActivity.class));
            }
        });
        MenuItem sitem = menu.findItem(R.id.searchCart_search);
        sitem.setVisible(false);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();

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

    private void prepareRequest(String id) {


        progress.show();
        PrimaryUser primaryUser = new PrimaryUser("Y", primary_id);
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.makePrimary(id, primaryUser, helper.getB64Auth(ManageTeamActivity.this), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response) {
                //      progress.dismiss();
                progress.dismiss();
                Boolean success = false;
                Log.i("re", "" + response.code());

                if (response.isSuccess()) {
                    //   progress.dismiss();
                    JsonElement element = (JsonElement) response.body();
                    if (element != null && element.isJsonObject()) {
                        try {
                            JSONObject jObject = new JSONObject(element.getAsJsonObject().toString());
                            Log.d("primaery user",jObject.toString());
                            if (jObject.optString("status").equalsIgnoreCase("true"))
                            {
                                success = true;
                            }
                            if (jObject.optString("status").equalsIgnoreCase("400") && jObject.optString("message").contains("Before making primary")) {
                                new AlertDialogManager().showAlertDialog(ManageTeamActivity.this,
                                        getResources().getString(R.string.sorry),
                                        jObject.getString("message"));
                            }
                            if (success) {
                                arrayList.clear();
                                if (cd.isConnectingToInternet()) {
                                    new LoadTeam().execute();
                                } else {
                                    new AlertDialogManager().showAlertDialog(ManageTeamActivity.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    int statusCode = response.code();
                    Log.i("in else ", "" + response.code());
                    if (statusCode == 400) {
                        if (ManageTeamActivity.this != null && !ManageTeamActivity.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(ManageTeamActivity.this,
                                    getString(R.string.sorry),
                                    getString(R.string.user_not_loggedin));
                        }

                    } else if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(ManageTeamActivity.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    } else {
                        if (ManageTeamActivity.this != null && !ManageTeamActivity.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(ManageTeamActivity.this, getString(R.string.error), getString(R.string.server_error));
                        }
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {

                if (ManageTeamActivity.this != null && !ManageTeamActivity.this.isFinishing()) {

                    adapter.notifyDataSetChanged();
                    new AlertDialogManager().showAlertDialog(ManageTeamActivity.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
                }
            }
        });


    }
}
