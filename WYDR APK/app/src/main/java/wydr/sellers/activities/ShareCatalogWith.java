package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.ShareCatalogWithAdapter;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.UserFunctions;

public class ShareCatalogWith extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public ConnectionDetector cd;
    Helper helper = new Helper();
    private ShareCatalogWithAdapter adapter;
    private List<String> sel_vendors = new ArrayList<>();
    private ProgressDialog progress;
    JSONParser parser;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_catalog);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.share_catalog_with));
        progressStuff();
        cd = new ConnectionDetector(ShareCatalogWith.this);
        ListView userlist = (ListView) findViewById(R.id.share_catalog_list);
        getSupportLoaderManager().initLoader(1, null, this);
        adapter = new ShareCatalogWithAdapter(ShareCatalogWith.this, null);
        userlist.setAdapter(adapter);
        Button btn_share = (Button) findViewById(R.id.btn_share);

//        userlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String vendor_id = cursor.getString(cursor.getColumnIndexOrThrow(NetSchema.USER_ID));
                Log.i("", "cursor click: " + vendor_id);
                if (sel_vendors.contains(vendor_id)) {
                    sel_vendors.remove(vendor_id);
                } else {
                    sel_vendors.add(vendor_id);
                }
                adapter.toggleSelection(cursor.getPosition());
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sel_vendors.size() < 1) {
                    Toast.makeText(ShareCatalogWith.this, getString(R.string.select_atleast_oneuser), Toast.LENGTH_LONG).show();
                    return;
                }
                if (cd.isConnectingToInternet()) {

                    new ShareProduct().execute();

                } else {
                    new AlertDialogManager().showAlertDialog(ShareCatalogWith.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                }

            }
        });
    }

    private void progressStuff() {

        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                Log.i("1", "22");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortingOrder = "(CASE display_name  when \"\" THEN 0 ELSE 1 END ) DESC , display_name";
        return new CursorLoader(ShareCatalogWith.this, ChatProvider.NET_URI, null, NetSchema.USER_STATUS + "=?", new String[]{"1"}, sortingOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("Size here", "" + data.getCount());
        switch (loader.getId()) {
            case 1: {
                adapter.swapCursor(data);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class ShareProduct extends AsyncTask<String, String, JSONObject> {
        JSONObject table = new JSONObject();

        private int flag = 0;

        public ShareProduct() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing())
                progress.show();
            try {
                table.put("user_id", helper.getDefaults("user_id", ShareCatalogWith.this));
                table.put("object_type", "product");
                table.put("object_id", getIntent().getStringExtra("product_id"));
                table.put("shared_user_id", new JSONArray(sel_vendors));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.shareCatalogWith(table,ShareCatalogWith.this);
            if (json != null) {
                if (json.has("error"))
                    flag = 2;
            } else
                flag = 1;
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!isFinishing()) {
                progress.dismiss();
                if (flag == 0) {
                    Toast.makeText(ShareCatalogWith.this, getString(R.string.shared_successfully), Toast.LENGTH_LONG).show();
                    ShareCatalogWith.this.finish();
                } else if (flag == 1) {
                    alertDialog.setTitle(getString(R.string.sorry));
                    alertDialog.setMessage(getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    alertDialog.show();
                } else if (flag == 2){
                    alertDialog.setTitle(getString(R.string.error));
                    alertDialog.setMessage(getString(R.string.page_not_found));
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
