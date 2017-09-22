package wydr.sellers.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.adapter.FavoriteQueryAdapter;
import wydr.sellers.gson.FavoriteHolder;
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
 * Created by Deepesh_pc on 18-09-2015.
 */

/**
 * Created by Navdeep on 12/9/15.
 */
public class FavQueryFrag extends Fragment implements SearchView.OnQueryTextListener
{
    ArrayList<wydr.sellers.gson.FavoriteQueryModal> list;
    FavoriteQueryAdapter adapter;
    ListView listView;
    ImageView image;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    Helper helper = new Helper();
    //    private ProgressDialog progress;
    android.app.AlertDialog.Builder alertDialog;
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    private LinearLayout ll;
    private String user_id;
    TextView order_status;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_query_feed, null);
        user_id = helper.getDefaults("user_id", getActivity());
        iniStuff(rootView);
        progressStuff();
        NetAsync();
        setHasOptionsMenu(true);
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUff();
    }

    private void initUff()
    {
        ArrayList<String>ufList;
        Helper helper = new Helper();
        ImageView iv_bussiness=(ImageView)getActivity().findViewById(R.id.uf_queryfeed);
        LinearLayout ll= (LinearLayout)getActivity().findViewById(R.id.llquery);
        PrefManager prefManager = new PrefManager(getActivity());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));

        if(ufList.contains(AppUtil.TAG_My_Favourites_Leads))
        {
            ll.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            iv_bussiness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Catalog.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            Picasso.with(getActivity())
                    .load(helper.getDefaults(AppUtil.TAG_My_Favourites_Leads+"_photo",getActivity()))
                    .into(iv_bussiness);
        }
        else
        {
            ll.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    public void NetAsync() {

        if (cd.isConnectingToInternet()) {
            prepareRequest();
        } else {
            new AlertDialogManager().showAlertDialog(this.getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
    }


    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(this.getActivity());
        parser = new JSONParser();
        alertDialog = new AlertDialog.Builder(this.getActivity());
    }

    private void iniStuff(View rootView) {
        page_no = 1;
        isLoading = true;
        order_status = (TextView) rootView.findViewById(R.id.query_record_status);
        ll = (LinearLayout) rootView.findViewById(R.id.qbottombar);
        ll.setVisibility(View.GONE);
        listView = (ListView) rootView.findViewById(R.id.listViewHomeFeed);
        image = (ImageView) rootView.findViewById(R.id.dateFilter);
        image.setVisibility(View.GONE);
        list = new ArrayList<>();
        adapter = new FavoriteQueryAdapter(getActivity(), list);
        loadingFooter = LayoutInflater.from(getActivity()).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isLoading || page_no == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    Log.i("", "end reached");
                    page_no++;
                    Log.i("", "loading page_no " + page_no);
                    NetAsync();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_cart_menu, menu);

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
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");
                startActivity(new Intent(getActivity(), CartActivity.class));
            }
        });

        SearchView searchView = (SearchView) menu.findItem(R.id.searchCart_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {


        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
            //
            listView.clearTextFilter();
        } else {
            //
            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    public void onResume() {
        setNotifCount();
        super.onResume();
    }

    private void setNotifCount() {
        Cursor mCursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null) {
            if (mNotifCount == 0) {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            } else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }

        getActivity().invalidateOptionsMenu();
    }

    private void prepareRequest() {
        if (page_no != 0) {
            isLoading = true;
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<FavoriteHolder> call = service.getFavQuery(user_id, "query", page_no, helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<FavoriteHolder>() {
                @Override
                public void onResponse(Response response) {
                    //      progress.dismiss();
                    isLoading = false;
                    listView.removeFooterView(loadingFooter);
                    Log.d("re", "" + response.code());
                    // Log.d("JSON", " " + element.getAsJsonObject().toString());
                    if (response.isSuccess()) {
                        //   progress.dismiss();
                        FavoriteHolder holder = (FavoriteHolder) response.body();

                        list.addAll(holder.getQuery());
                        if (holder.getQuery().size() < 10) {
                            page_no = 0;
                        }
                        if (page_no == 0) {
                            if (list.size() == 0) {
                                listView.setVisibility(View.GONE);
                                order_status.setVisibility(View.VISIBLE);
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                order_status.setVisibility(View.GONE);
                            }
                            listView.removeFooterView(loadingFooter);

                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        int statusCode = response.code();
                        page_no = 0;
                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(getActivity());
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.server_error));
                            }
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    page_no = 0;
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (list.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        listView.removeFooterView(loadingFooter);
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(getActivity(),
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }

    }


}


