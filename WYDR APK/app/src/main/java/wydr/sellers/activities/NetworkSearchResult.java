package wydr.sellers.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.NetworkResultAdapter;
import wydr.sellers.modal.ContacLlist;
import wydr.sellers.registration.Helper;

/**
 * Created by Deepesh_pc on 21-08-2015.
 */

/**
 * Created by surya on 12/8/15.
 */
public class NetworkSearchResult extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public ArrayList<ContacLlist> names = new ArrayList<>();
    ListView listView;
    NetworkResultAdapter adapter;
    String queryString;
    Cursor c;
    ArrayList<String>ufList= new ArrayList<>();
    Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_search_result);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_coon));
        queryString = getIntent().getStringExtra("query");
        // Log.e("here Query ", queryString);
        String Query = NetSchema.USER_STATUS + "=1 and ";
        //c = getContentResolver().query(ChatProvider.NET_URI, new String[]{NetSchema.USER_NAME,NetSchema.USER_NET_ID,NetSchema.USER_ID,NetSchema.USER_COMPANY,NetSchema.USER_COMPANY_ID,NetSchema.USER_DISPLAY,NetSchema.USER_PHONE},NetSchema.USER_NAME + " LIKE ?", new String[]{"%" + queryString + "%"}, null);
        c = getContentResolver().query(ChatProvider.NET_URI, null, Query + NetSchema.USER_DISPLAY + " LIKE ? or " + NetSchema.USER_COMPANY + " LIKE ? ", new String[]{"%" + queryString + "%","%" + queryString + "%"}, null);
        int USER_NAME = c.getColumnIndexOrThrow(NetSchema.USER_NAME);
        int USER_PHONE = c.getColumnIndexOrThrow(NetSchema.USER_PHONE);
        int USER_COMPANY = c.getColumnIndexOrThrow(NetSchema.USER_COMPANY);
        int USER_NET_ID = c.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
        int USER_DISPLAY = c.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
        int USER_COMPANY_ID = c.getColumnIndexOrThrow(NetSchema.USER_COMPANY_ID);
        int USER_ID = c.getColumnIndexOrThrow(NetSchema.USER_ID);
        Log.e("herecount ", c.getCount() + "");
        if (c.getCount() > 0) {
            Log.e("entered", "3");
            while (c.moveToNext()) {
                ContacLlist m = new ContacLlist();
                m.setName(helper.ConvertCamel(c.getString(USER_NAME)));
                m.setNumber(c.getString(USER_PHONE));
                m.setCompname(c.getString(USER_COMPANY));
                m.setNetwork_userid(c.getString(USER_NET_ID));
                m.setDisp_name(c.getString(USER_DISPLAY));
                m.setComp_id(c.getString(USER_COMPANY_ID));
                m.setUser_id(c.getString(USER_ID));
                names.add(m);
            }
        }
        iniStuff();
        initUff();
        if (c.getCount() == 0) {
            if(ufList.contains(AppUtil.TAG_Search_Connections))
            {}
            else
                showAlertDialog(NetworkSearchResult.this, getResources().getString(R.string.oops), getString(R.string.no_record));
        }
    }

    private void iniStuff() {
        listView = (ListView) findViewById(R.id.netlist);

        adapter = new NetworkResultAdapter(getApplicationContext(), names);
        listView.setAdapter(adapter);
    }

    private void initUff()
    {
        ImageView iv_bussiness=(ImageView)findViewById(R.id.iv_NETWORKsearchresult);
        iv_bussiness.setVisibility(View.GONE);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Catalog.class);
                startActivity(intent);
            }
        });
        if(ufList.contains(AppUtil.TAG_Search_Connections))
        {
            listView.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Search_Connections+"_photo",getApplicationContext()))
                    .into(iv_bussiness);
        }
        else
        {
            listView.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String Query = ContactsDb.KEY_STATUS + "=2 and ";
        return new CursorLoader(this, ChatProvider.NET_URI, null, NetSchema.USER_NAME + " LIKE ?", new String[]{"%" + queryString + "%"}, null);        // return new CursorLoader(SearchResult.this, ChatProvider.CONTENT_URI, null, ChatSchema.KEY_MSG +"LIKE ?", new String[]{"%" + queryString + "%"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if (adapter != null) {
        //  adapter.swapCursor(data);
        Log.d("size", "" + data.getColumnCount());
        //}

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // adapter.swapCursor(null);
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title).setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                        //     NavUtils.navigateUpFromSameTask(this);
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("NetworkSearchResult");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
