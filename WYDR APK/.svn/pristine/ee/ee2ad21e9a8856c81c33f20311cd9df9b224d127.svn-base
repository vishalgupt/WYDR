package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.adapter.QueryAdapter;
import wydr.sellers.gson.QueryHolder;
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
 * Created by surya on 21/8/15.
 */
public class SearchFeed extends AppCompatActivity {
    Toolbar toolbar;
    ArrayList<wydr.sellers.gson.QueryModal> list;
    QueryAdapter adapter;
    ListView listView;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    String queryString;
    private ProgressDialog progress;
    private boolean isLoading;
    int page_no = 1;
    Helper helper = new Helper();
    TextView order_status;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_feed);
        progressStuff();
        queryString = getIntent().getStringExtra("data");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setTitle(getString(R.string.search_results_for) + queryString);
        currentUserId = helper.getDefaults("user_id", SearchFeed.this);
        iniStuff();
        initUff();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (page_no == 0 || isLoading) {
                    return;
                } else {
                    if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                        page_no++;
                        prepareRequest(page_no, currentUserId);
                    }
                }
            }
        });
        prepareRequest(page_no, currentUserId);

    }

    private void initUff()
    {
        ArrayList<String>ufList;
        ImageView iv_bussiness=(ImageView)findViewById(R.id.uf_bussiness);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Catalog.class);
                startActivity(intent);
            }
        });
        if(ufList.contains(AppUtil.TAG_Search_Business_Leads))
        {
            listView.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Search_Business_Leads+"_photo",getApplicationContext()))
                    .into(iv_bussiness);
        }

        else
        {
            listView.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    private void prepareRequest(int pageNo, String userId) {
        isLoading = true;
        if (page_no != 0) {
            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<QueryHolder> call = service.getSearchFeed(
                    queryString,
                    pageNo,
                    userId,
                    helper.getB64Auth(SearchFeed.this),
                    "application/json",
                    "application/json");

            call.enqueue(new Callback<QueryHolder>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();
                    isLoading = false;

                    if (response.isSuccess()) {

                        QueryHolder holder = (QueryHolder) response.body();
                        if (holder.getQuery().size() < 10) {
                            page_no = 0;
                        }
                        list.addAll(holder.getQuery());
                        if (page_no == 0) {
                            if (list.size() == 0) {
                                listView.setVisibility(View.GONE);
                                order_status.setVisibility(View.VISIBLE);
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                order_status.setVisibility(View.GONE);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        page_no = 0;
                        int statusCode = response.code();

                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(SearchFeed.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (SearchFeed.this != null && !SearchFeed.this.isFinishing()) {
                                new AlertDialogManager().showAlertDialog(SearchFeed.this,
                                        getString(R.string.error),
                                        getString(R.string.server_error));
                            }
                        }


                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    if (SearchFeed.this != null && !SearchFeed.this.isFinishing()) {
                        if (list.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        page_no = 0;
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(SearchFeed.this,
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }

    }

    private void progressStuff() {

        cd = new ConnectionDetector(this);
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);

    }

    private void iniStuff() {
        isLoading = true;
        listView = (ListView) findViewById(R.id.listViewFeed);
        order_status = (TextView) findViewById(R.id.lead_record_status);
        list = new ArrayList<>();
        LinearLayout ll = (LinearLayout) findViewById(R.id.mqbottombar);
        ll.setVisibility(View.GONE);
        ImageView img = (ImageView) findViewById(R.id.dateFilterQuery);
        img.setVisibility(View.GONE);
        adapter = new QueryAdapter(SearchFeed.this, list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress.isShowing()) {
            progress.dismiss();
        }
        list.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SearchFeed");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
