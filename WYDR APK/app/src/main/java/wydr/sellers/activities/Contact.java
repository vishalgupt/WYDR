package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.ContactAdapter;
import wydr.sellers.modal.AddNewConnection;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.ContactsDb;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by surya on 31/8/15.
 */
public class Contact extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
    public static int flag = 0;
    ListView listView;
    ContactAdapter adapter;
    String msg;
    ArrayList<String> ufList = new ArrayList<>();
    ConnectionDetector cd;
    JSONParser parser;
    ProgressDialog progress;
    JSONObject table;
    Button invite;
    Helper helper = new Helper();

    private ProgressDialog mProgressDialog;

    //String queryString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conatct_2);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        //tooltitle.setText(getString(R.string.add_connection));
        PermissionsUtils.verifySMSPermissions(Contact.this);
        progressStuff();
        iniStuff();
        initUff();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //   Toast.makeText(Contact.this, "OK", Toast.LENGTH_LONG).show();
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                //  c.getString()
            }
        });
        //;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(ufList.contains(AppUtil.TAG_Quick_Launch_Add_Connection))
        {}
        else {
            // menu.
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.search_menu, menu);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            int searchImgId = android.support.v7.appcompat.R.id.search_button;
            ImageView v = (ImageView) searchView.findViewById(searchImgId);
            v.setImageResource(R.drawable.search);
            searchView.setOnQueryTextListener(this);
            searchView.requestFocus();
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initUff()
    {

        ImageView iv_bussiness=(ImageView)findViewById(R.id.uf_contact);
        LinearLayout ll= (LinearLayout)findViewById(R.id.llcontact);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Catalog.class);
                startActivity(intent);
            }
        });
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));

        if(ufList.contains(AppUtil.TAG_Quick_Launch_Add_Connection))
        {
            ll.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Quick_Launch_Add_Connection+"_photo",getApplicationContext()))
                    .into(iv_bussiness);
        }

        else
        {
            ll.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    private void iniStuff() {
        listView = (ListView) findViewById(R.id.list_contact);
        invite = (Button) findViewById(R.id.buttonSendInvite);
        getSupportLoaderManager().initLoader(10, null, this);

        adapter = new ContactAdapter(Contact.this, null);
        listView.setAdapter(adapter);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return getContentResolver().query(ChatProvider.BOOK_URI, null, ContactsDb.KEY_NAME + " LIKE ?", new String[]{"%" + constraint + "%"}, ContactsDb.KEY_NAME);
            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) Contact.this.getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Add Connection", "Move", "Invite New Connection");
                /*******************************ISTIAQUE***************************************/

                if (PermissionsUtils.verifySMSPermissions(Contact.this)) {
                    Log.d("is there", adapter.getChecked());
                    Toast.makeText(Contact.this, adapter.getChecked(), Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(Contact.this)

                            .setMessage(getString(R.string.standard_sms_charges))
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    /************************************************* ISTIAQUE: CODE BEGINS ****************************************************************/

                                    if (cd.isConnectingToInternet()) {
                                        if(adapter.getChecked()!="") {
                                            inviteNewConnection(new AddNewConnection(helper.getDefaults("user_id", getApplicationContext()), adapter.getChecked()));
                                        }

                                        else {
                                            Toast.makeText(getApplicationContext(),"Please select at least one contact",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(Contact.this, "Please Check your network connection", Toast.LENGTH_LONG).show();
                                    }

                                    /************************************************* ISTIAQUE: CODE ENDS ****************************************************************/


                                    Uri uri = Uri.parse("smsto:" + adapter.getChecked());

                                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);

                                    smsIntent.putExtra("sms_body", getString(R.string.wydr_invite_sms));

                                    startActivity(smsIntent);
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // whatever...

                        }
                    }).create().show();


                }
            }
        });
    }

    private void inviteNewConnection(AddNewConnection addNewConnection)
    {
        Gson gson = new Gson();
        Log.e("JSONConnection", gson.toJson(addNewConnection));
        RestClient.GitApiInterface service = RestClient.getClient();
        //Call<JsonElement> call = service.newConnection(helper.getDefaults("user_id", getApplicationContext()), addNewConnection, helper.getB64Auth(Contact.this));
        Call<JsonElement> call = service.newConnection(addNewConnection, helper.getB64Auth(Contact.this));//, "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>()
        {
            @Override
            public void onFailure(Throwable t)
            {
                if (Contact.this != null && !Contact.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(Contact.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response)
            {
                Log.d("NewConnectionResponse", "" + response.raw());
                progress.dismiss();
                if (response.isSuccess())
                {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.d("keymesage",jsonObject.toString());
                        Toast.makeText(Contact.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    // finish();
                }

                else
                {
                    Toast.makeText(Contact.this, "Your request has been failed please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        parser = new JSONParser();
        progress = new ProgressDialog(Contact.this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ChatProvider.BOOK_URI, null, null, null, ContactsDb.KEY_NAME);        // return new CursorLoader(SearchResult.this, ChatProvider.CONTENT_URI, null, ChatSchema.KEY_MSG +"LIKE ?", new String[]{"%" + queryString + "%"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }

    public void doJob(View v) {
        int con2 = (int) v.getTag();
        Cursor c = adapter.getCursor();
        c.moveToPosition(con2);
        int iName = c.getColumnIndexOrThrow(ContactsDb.KEY_ID);
        table = new JSONObject();

        try {

            table.put("seller_id", helper.getDefaults("user_id", getApplicationContext()));
            table.put("phone", c.getString(iName));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (cd.isConnectingToInternet()) {
            new AddConnection().execute(c.getString(iName));
        } else {
            new AlertDialogManager().showAlertDialog(Contact.this, getString(R.string.oops), getString(R.string.no_internet_conn));


        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
            listView.clearTextFilter();
        } else {

            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
    }

    private class AddConnection extends AsyncTask<String, JSONObject, JSONObject> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing())
                progress.show();
        }


        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();

            JSONObject json = null;

            json = userFunction.addconnection(table, Contact.this);
            if (json != null) {


                getContentResolver().notifyChange(ChatProvider.BOOK_URI, null, false);
                getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
                try {

                    if (json.has("error")) {
                        title = getString(R.string.oops);
                        msg = "";
                        if (json.get("error") instanceof JSONArray) {
                            JSONArray jsonArray = json.getJSONArray("error");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                msg = msg + jsonArray.get(i) + ", ";
                            }
                            if (msg.length() > 2)
                                msg = msg.substring(0, msg.length() - 2);
                        } else {
                            msg = json.getString("error");
                        }

                        if (msg.contains("Vendor already")) {
                            ContentValues values = new ContentValues();
                            values.put(NetSchema.USER_STATUS, "1");
                             getContentResolver().update(ChatProvider.NET_URI, values, NetSchema.USER_PHONE + "=?", new String[]{args[0]});

                            ContentValues values1 = new ContentValues();
                            values1.put(ContactsDb.KEY_STATUS, "2");
                            getContentResolver().update(ChatProvider.BOOK_URI, values1, ContactsDb.KEY_ID + "=?", new String[]{args[0]});

                        }
                    } else {
                        title = getString(R.string.success);
                        ContentValues values = new ContentValues();
                        values.put(NetSchema.USER_STATUS, "1");
                        int a = getContentResolver().update(ChatProvider.NET_URI, values, NetSchema.USER_PHONE + "=?", new String[]{args[0]});
                        Log.d("ID--2", a + "");
                        ContentValues values1 = new ContentValues();
                        values1.put(ContactsDb.KEY_STATUS, "2");
                        int b = getContentResolver().update(ChatProvider.BOOK_URI, values1, ContactsDb.KEY_ID + "=?", new String[]{args[0]});
                        Log.d("ID--2", b + "");

                        msg = json.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!isFinishing()) {
                progress.dismiss();
                adapter.notifyDataSetChanged();
                adapter.getFilter().filter("");
                // listView.scrollListBy(1);

                if (json == null) {
                    new AlertDialogManager().showAlertDialog(Contact.this, getString(R.string.sorry), getString(R.string.server_error));

                } else {

                        new AlertDialogManager().showAlertDialog(Contact.this, title, msg);

                }
            }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Contact");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /*** Async task class to get json by making HTTP call ****/







}
