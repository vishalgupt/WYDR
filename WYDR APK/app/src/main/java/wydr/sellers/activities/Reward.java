package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.adapter.RewardHistoryAdapter;
import wydr.sellers.modal.RewardHistoryObject;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.registration.Helper;

/**
 * Created by Ishtiyaq on 4/6/2016.
 */
public class Reward extends AppCompatActivity {

    Toolbar mToolbar;
    ListView rewardHistory;
    RewardHistoryAdapter adapter;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    Helper helper;
    private ProgressDialog progress;
    TextView availablePoints_tv, msg;
    RelativeLayout no_history;
    ConnectionDetector cd;
    List<RewardHistoryObject> rewardHistories;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.reward));

        helper = new Helper();

        progress = new ProgressDialog(Reward.this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);

        recyclerView = (RecyclerView) findViewById(R.id.reward_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cd = new ConnectionDetector(Reward.this);

        availablePoints_tv = (TextView) findViewById(R.id.availablePoints_tv);

        no_history = (RelativeLayout) findViewById(R.id.no_history);
        msg = (TextView) findViewById(R.id.msg);

        if (cd.isConnectingToInternet()){
            progress.show();
            getRewardHistory();
        } else {
            Snackbar.make(Reward.this.findViewById(android.R.id.content), "Network error. Please check your internet connection", Snackbar.LENGTH_LONG).show();
        }

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getRewardHistory() {
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.getUserRewardHistory(helper.getDefaults("user_id", Reward.this), helper.getB64Auth(Reward.this), "application/json");
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onFailure(Throwable t) {
                progress.dismiss();
                if (Reward.this != null && !Reward.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(Reward.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response) {
                Log.d("Rewards", "" + response.raw());
                progress.dismiss();
                if (response.isSuccess()) {
                    progress.dismiss();
                    try {
                        JSONObject rootJSON = new JSONObject(response.body().toString());
                        Log.d("IstRew", rootJSON.toString());

                        if (rootJSON != null) {
                            if (rootJSON.has("msg")) {
                                availablePoints_tv.setText("Bal. 0");
                                no_history.setVisibility(View.VISIBLE);
                                msg.setText(rootJSON.getString("msg"));
                            } else {
                                availablePoints_tv.setText("Bal. " + rootJSON.getString("total_points"));

                                JSONArray rewardPoints = rootJSON.getJSONArray("reward_points");
                                rewardHistories = new ArrayList<RewardHistoryObject>();

                                for (int i = 0; i < rewardPoints.length(); i++)
                                {
                                    JSONObject jsonObject = rewardPoints.getJSONObject(i);
                                    RewardHistoryObject rewardHistoryObject = new RewardHistoryObject();

                                    rewardHistoryObject.setDeductionDate(getDateFromTimeStamp(jsonObject.getString("timestamp")));
                                    rewardHistoryObject.setExpirationDate(jsonObject.getString("expire"));
                                    rewardHistoryObject.setDeductionReason(jsonObject.getString("reason"));
                                    rewardHistoryObject.setAmount(jsonObject.getString("amount"));
                                    rewardHistories.add(rewardHistoryObject);
                                }

                                adapter = new RewardHistoryAdapter(rewardHistories);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Reward.this, "Your request has been failed please try again.", Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    private String getDateFromTimeStamp(String time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time) * 1000L);
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        return date.toUpperCase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Reward");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.
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

                startActivity(new Intent(Reward.this, CartActivity.class));
            }
        });

        MenuItem sitem = menu.findItem(R.id.searchCart_search);
        sitem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
