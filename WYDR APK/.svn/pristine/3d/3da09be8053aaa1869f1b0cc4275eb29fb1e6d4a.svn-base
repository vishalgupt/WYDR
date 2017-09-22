package wydr.sellers.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.adapter.ChatSearchAdapter;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 12/8/15.
 */
public class SearchResult extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    ChatSearchAdapter adapter;
    String queryString;
    Cursor c;
    Helper helper = new Helper();

    ArrayList<String>ufList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
//        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
//            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                    "ChatSearchResult"));
//        }

        PrefManager prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        queryString = getIntent().getStringExtra("query");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_results_for) + queryString);
        toolbar.setNavigationIcon(R.drawable.back);

        c = getContentResolver().query(ChatProvider.SEARCH_URI, null, ChatSchema.KEY_MSG + " LIKE ?", new String[]{"%" + queryString + "%"}, ChatSchema.KEY_CREATED + " desc");

        iniStuff();
        initUff();
        Log.d("here Query ", queryString);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                int KEY_ROWID = cursor.getColumnIndexOrThrow(ChatSchema.KEY_ROWID);
                int iReceiver = cursor.getColumnIndexOrThrow(ChatSchema.KEY_RECEIVER);

                if (cursor.getString(iReceiver).equalsIgnoreCase(helper.getDefaults("login_id", SearchResult.this) + "@" + AppUtil.SERVER_NAME)) {
                    iReceiver = cursor.getColumnIndexOrThrow(ChatSchema.KEY_SENDER);
                }
                Log.i("KEY_ROWID", cursor.getString(KEY_ROWID));
                Log.i("iReceiver", cursor.getString(iReceiver));
                startActivity(new Intent(new Intent(SearchResult.this, ChatActivity.class)).putExtra("id", Long.parseLong(cursor.getString(KEY_ROWID))).
                        putExtra("user", cursor.getString(iReceiver)));
            }
        });

    }

    private void iniStuff() {
        listView = (ListView) findViewById(R.id.listViewSearch);
        getSupportLoaderManager().initLoader(0, null, this);
        adapter = new ChatSearchAdapter(SearchResult.this, c);
        listView.setAdapter(adapter);
        if (c.getCount() == 0)
        {

            if(ufList.contains(AppUtil.TAG_Search_Chat))
            {

            }
            else {
                new AlertDialogManager().showAlertDialog(SearchResult.this, getString(R.string.oops), getString(R.string.no_record));
                // showAlertDialog(SearchResult.this, "Sorry", "No record found");
            }
        }
        //   list = new ArrayList<>();
        // new LoadData().execute();
    }

    private void initUff()
    {

        ImageView iv_bussiness=(ImageView)findViewById(R.id.iv_searchresult);
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(),Catalog.class);
                startActivity(intent);
            }
        });
        if(ufList.contains(AppUtil.TAG_Search_Chat))
        {
            listView.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Search_Chat+"_photo",getApplicationContext()))
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
        /*switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.i("11111","11111");
                startActivity(new Intent(SearchResult.this, SearchActivity.class));
                finish();

        }*/
        super.onBackPressed();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ChatProvider.SEARCH_URI, null, ChatSchema.KEY_MSG + " LIKE ?", new String[]{"%" + queryString + "%"}, ChatSchema.KEY_CREATED + " desc");        // return new CursorLoader(SearchResult.this, ChatProvider.CONTENT_URI, null, ChatSchema.KEY_MSG +"LIKE ?", new String[]{"%" + queryString + "%"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if (adapter != null) {
        adapter.swapCursor(data);
        Log.d("size", "" + data.getColumnCount());
        //}

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
