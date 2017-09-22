package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.adapter.AttachAdapter;
import wydr.sellers.modal.AttachModal;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 19/12/15.
 */
public class AddProductInBroadcast extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ListView listViewCalalog;
    ArrayList<AttachModal> list;
    AttachAdapter adapter;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    Helper helper;
    String catTo;
    private ProgressDialog progress;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_in_broadcast);
//        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                "Add Product in Broadcast "));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.attach_product));
        helper = new Helper();
        catTo = helper.getDefaults("company_id", getApplicationContext());
//        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
//        tooltitle.setText("My Queries");

        iniStuff();
        progressStuff();
        new LoadCatalog().execute();
        listViewCalalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttachModal modal = (AttachModal) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", "" + modal.getName());
                bundle.putString("code", "" + modal.getCode());
                bundle.putString("mrp", "" + modal.getPrice());
                bundle.putString("price", "" + modal.getSellingPrice());
                bundle.putString("url", "" + modal.getImgUrl());
                bundle.putString("moq", "" + modal.getMoq());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", bundle);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void progressStuff() {
        // TODO Auto-generated method stub
         session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);

    }

    private void iniStuff() {
        listViewCalalog = (ListView) findViewById(R.id.listViewProductBroadcast);
        list = new ArrayList<>();
        adapter = new AttachAdapter(AddProductInBroadcast.this, list);
        listViewCalalog.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {


        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");

            listViewCalalog.clearTextFilter();
        } else {

            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //   NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadCatalog extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isFinishing())
            progress.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = parser.makeHttpRequest(AppUtil.URL
                            + "products?company_id=" + catTo,
                    "GET", params,AddProductInBroadcast.this);
            if (json.has("products")) {
                try {
                    JSONArray array = json.getJSONArray("products");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        AttachModal modal = new AttachModal();
                        modal.setId(object.getString("product_id"));
                        modal.setCode(object.getString("product_code"));
                        modal.setPrice(object.getString("list_price"));
                        modal.setSellingPrice(object.getString("price"));
                        modal.setName(object.getString("product"));
                        modal.setMoq(object.getString("min_qty"));
                        if (object.has("main_pair")) {
                            JSONObject obj = object.getJSONObject("main_pair");
                            JSONObject detailObj = obj.getJSONObject("detailed");
                            modal.setImgUrl(detailObj.getString("http_image_path"));
                        }
                        list.add(modal);
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

            // check your log for json response
            Log.d("Result attempt", json.toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(!isFinishing())
            {
                super.onPostExecute(s);
                progress.dismiss();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }

}
