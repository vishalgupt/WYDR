package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.adapter.ProductSearchAdapter;
import wydr.sellers.modal.CatalogProductModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

/**
 * Created by Deepesh_pc on 16-11-2015.
 */
public class ProductSearch extends AppCompatActivity {
    public Toolbar toolbar;
    ArrayList<CatalogProductModal> result = new ArrayList<CatalogProductModal>();
    ConnectionDetector cd;
    AlertDialog.Builder alertDialog;
    ProductSearchAdapter adapter;
    JSONParser parser;
    ArrayList<CatalogProductModal> list;
    ListView lv;
    Helper helper = new Helper();
    private ProgressDialog progress;
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    private String user_id;
    SessionManager session;
    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.my_catalog);
        progressStuff();
        lv = (ListView) findViewById(R.id.listViewproduct);
        loadingFooter = LayoutInflater.from(ProductSearch.this).inflate(R.layout.pagination_loading, null);
        lv.addFooterView(loadingFooter);
        list = new ArrayList();
        user_id = helper.getDefaults("user_id", ProductSearch.this);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setTitle("");
        TextView tooltext = (TextView) findViewById(R.id.toolbartext);
        parser = new JSONParser();
        tooltext.setText(getString(R.string.search_results_for) + getIntent().getStringExtra("query"));
        NetSync(getIntent().getStringExtra("query"));
        adapter = new ProductSearchAdapter(ProductSearch.this, result);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CatalogProductModal modal = (CatalogProductModal) parent.getItemAtPosition(position);
                Intent intent = new Intent(ProductSearch.this, ProductDetailsActivity.class);
                intent.putExtra("product_id", modal.getId());
                intent.putExtra("name", modal.getName());
                startActivity(intent);
            }
        });
        page_no = 1;
        isLoading = true;
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (page_no != 0 && visibleItemCount != 0)
//                    ll.startAnimation(animation);
                if (isLoading || page_no == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    Log.i("", "end reached");
                    page_no++;
                    Log.i("", "loading page_no " + page_no);
                    NetSync(getIntent().getStringExtra("query"));
                }
            }
        });

    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list.clear();
        adapter.notifyDataSetChanged();
    }

    private void NetSync(String query) {
        if (cd.isConnectingToInternet()) {
            new SearchSeller().execute(query);
        } else {
            new AlertDialogManager().showAlertDialog(ProductSearch.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
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

    private class SearchSeller extends AsyncTask<String, String, JSONObject> {

        public JSONObject js = new JSONObject();
        JSONObject table = new JSONObject();
        int flag = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;
            // progress.show();
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("page", String.valueOf(page_no)));
            params.add(new BasicNameValuePair("pname", "Y"));
            params.add(new BasicNameValuePair("q", args[0]));
            params.add(new BasicNameValuePair("user_detail", "1"));
            params.add(new BasicNameValuePair("status", "A"));
            params.add(new BasicNameValuePair("product_visibility", "public"));
            params.add(new BasicNameValuePair("current_user_id", user_id));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "search", "GET", params, ProductSearch.this);
            try {
                if (json != null) {
                    if (json.has("products")) {
                        js = json;
                        JSONArray array = json.getJSONArray("products");
                        if (array.length() == 0) {
                            page_no = 0;
                            return null;
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.i("", "jsonobject: " + object.getString("product_id"));
                                CatalogProductModal modal = new CatalogProductModal();
                                modal.setId(object.getString("product_id"));
                                modal.setCode(object.getString("product_code"));
                                modal.setPrice(object.getString("list_price"));
                                modal.setSellingPrice(object.getString("base_price"));
                                modal.setName(object.getString("product"));
                                modal.setQty(object.getString("amount"));
                                modal.setMoq(object.getString("min_qty"));
                                if (object.getString("is_favourite").equalsIgnoreCase("1"))
                                    modal.setIsfav(true);
                                else
                                    modal.setIsfav(false);
                                if (object.has("user_details")) {
                                    modal.setUser_net_id(object.getJSONArray("user_details").getJSONObject(0).getString("user_login") + "@" + AppUtil.SERVER_NAME);
                                    modal.setUserid(object.getJSONArray("user_details").getJSONObject(0).getString("user_id"));

                                }

                                if (object.has("thumbnails")) {
                                    //JSONObject obj = object.getJSONObject("thumbnails");
                                    // JSONObject detailObj = obj.getJSONObject("detailed");
                                    modal.setImgUrl(object.getJSONObject("thumbnails").getString("image_path"));
                                    //  imagearray.add(obj.getString("image_path"));
                                }
                                if (object.has("price_share")) {
                                    if (object.getString("price_share").equalsIgnoreCase("1"))
                                        modal.setChat_price("Y");
                                    else
                                        modal.setChat_price("N");

                                } else {
                                    modal.setChat_price("N");
                                }
                                modal.setCompany_id(object.getString("company_id"));
                                result.add(modal);
                            }
                        }


                        Log.e("Seller", list.size() + "");
                    }

                    /*********************** ISTIAQUE ***************************/
                    if (json.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/
                    else {
                        js.put("error", " No record found");
                    }
                } else {
                    flag = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return js;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!isFinishing()) {
                if (flag == 0) {
                    if (page_no == 0) {
                        lv.removeFooterView(loadingFooter);
                        adapter.notifyDataSetChanged();
                    }
                    if (result.size() != 0) {
                        adapter.notifyDataSetChanged();
                    } else if (result.size() == 0) {

                        alertDialog.setTitle(getResources().getString(R.string.oops)).setMessage(getResources().getString(R.string.no_record))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        finish();

                                    }
                                }).create();
                        alertDialog.show();
                    }
                    isLoading = false;

                } else {
                    alertDialog.setTitle(getResources().getString(R.string.sorry)).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    finish();

                                }
                            }).create();
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
        mTracker.setScreenName("ProductSearch");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Search in Product").setValue(System.currentTimeMillis() - startTime).build());
    }
}


