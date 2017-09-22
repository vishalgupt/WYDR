package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.acc.QueryCategoryAdapter;
import wydr.sellers.adapter.MyQueryAdapter;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.QueryModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.MyCategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;

/**
 * Created by surya on 7/8/15.
 */
public class MyQuery extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener
{
    Toolbar toolbar;
    ArrayList<QueryModal> list;
    MyQueryAdapter adapter;
    ListView listView;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    android.app.AlertDialog.Builder alertDialog;
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    private String filterText = null;
    private int FILTER_REQUEST = 107;
    String sort_order, sort_by, category_id = "";
    static String from, to;ImageView filter;
    Animation animation;
    LinearLayout sort_button, category_button, ll;
    boolean backflag;
    ObjectItems obj;
    Button all, my;
    QueryCategoryAdapter adapter2;
    int sortingOrder = 0;
    private ExpandableListView explistView;
    TextView allTab;
    TextView queryText;
    Helper helper = new Helper();
    int prev_sort = 1;
    private String user_id;
    private ProgressDialog progressDialog;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    TextView order_status;
    PrefManager prefManager;
    ArrayList<String>ufList= new ArrayList<>();

    @Override
    protected void onResume()
    {
        super.onResume();
        setNotifCount();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.businessleads));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initUff()
    {
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.uf_rl_bussines);
        ImageView iv_bussiness=(ImageView)findViewById(R.id.uf_bussiness);
        prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Catalog.class);
                startActivity(intent);
            }
        });

        if(ufList.contains(AppUtil.TAG_Business_Lead))
        {
            rl.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Business_Lead+"_photo",getApplicationContext()))
                    .into(iv_bussiness);
        }
        else
        {
            rl.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_feed);
        progressStuff();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.businessleads));
        user_id = helper.getDefaults("user_id", MyQuery.this);
        iniStuff();
        initUff();
        queryText = (TextView) findViewById(R.id.myQueryText);
        if (cd.isConnectingToInternet())
        {
            new LoadQuery().execute();
        }

        else
        {
            new AlertDialogManager().showAlertDialog(MyQuery.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
        backflag = getIntent().getStringExtra("parent_myProfile") != null;
        explistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //  Log.i("clicked",groupPosition+"");
                ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                //   Log.i("id", items.id);
                // Log.i("name", items.title);
                category_id = items.id;
                queryText.setText(items.title);
                closeDropdown();
                // listView.setVisibility(View.GONE);

                list.clear();
                adapter.notifyDataSetChanged();
                if (cd.isConnectingToInternet())
                {//ISTIAQUE
                    if (from != null) {
                        new LoadQuery().execute(from, to);
                    } else {
                        new LoadQuery().execute();
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(MyQuery.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        parser = new JSONParser();
        alertDialog = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(MyQuery.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
    }

    private void iniStuff() {
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        order_status = (TextView) findViewById(R.id.lead_record_status);
        ll = (LinearLayout) findViewById(R.id.mqbottombar);
        sort_by = "created_at";
        sort_order = "desc";
        sortingOrder = 0;
        sort_button = (LinearLayout) findViewById(R.id.mqsort);
        category_button = (LinearLayout) findViewById(R.id.mqcategory_button);
        sort_button.setOnClickListener(this);
        category_button.setOnClickListener(this);
        page_no = 1;
        isLoading = true;
        listView = (ListView) findViewById(R.id.listViewFeed);
        loadingFooter = LayoutInflater.from(MyQuery.this).inflate(R.layout.pagination_loading, null);
        filter = (ImageView) findViewById(R.id.dateFilterQuery);
        filter.setOnClickListener(this);
        listView.addFooterView(loadingFooter);
        list = new ArrayList<>();
        adapter = new MyQueryAdapter(MyQuery.this, list);

        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // when list scrolling stops

                        //manipulateWithVisibleViews(view);

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ll.startAnimation(animation);
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (page_no != 0 && visibleItemCount != 0)
//                    ll.startAnimation(animation);
                if (isLoading || page_no == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {

                    page_no++;

                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new LoadQuery().execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(MyQuery.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
            }
        });
        explistView = (ExpandableListView) findViewById(R.id.expandableMyCatalog);
        View view2 = View.inflate(MyQuery.this, R.layout.category_header, null);
        allTab = (TextView) view2.findViewById(R.id.itemRootTitle);
        allTab.setOnClickListener(this);
        all = (Button) view2.findViewById(R.id.allCategory);
        my = (Button) view2.findViewById(R.id.myCategory);
        all.setOnClickListener(this);
        my.setOnClickListener(this);
        if (explistView.getHeaderViewsCount() == 0) {
            explistView.addHeaderView(view2, null, false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateFilterQuery:
                Log.d("date", from + "  " + to);
                startActivityForResult(new Intent(MyQuery.this, DateActivity.class).putExtra("date_from", from).putExtra("date_to", to), FILTER_REQUEST);

                break;

            case R.id.mqcategory_button:

                order_status.setVisibility(View.GONE);
                if (explistView.getVisibility() == View.VISIBLE) {
                    closeDropdown();

                } else {
                    if (explistView.getAdapter() != null) {

                        if (explistView.getAdapter().isEmpty()) {
                            loadCategory();
                        } else {
                            openDropdown();
                        }
                    } else {
                        loadCategory();
                    }
                }
                break;
            case R.id.allCategory:
                all.setTextColor(getResources().getColor(R.color.primary_500));
                my.setTextColor(getResources().getColor(R.color.text_color));
                loadCategory();
                break;
            case R.id.myCategory:
                all.setTextColor(getResources().getColor(R.color.text_color));
                my.setTextColor(getResources().getColor(R.color.primary_500));
                if (cd.isConnectingToInternet()) {
                    new GetUserCategory().execute(helper.getDefaults("company_id", MyQuery.this));
                } else {
                    loadSubCategory();
                }

                break;
            case R.id.itemRootTitle:
                category_id = "";
                page_no = 1;
                list.clear();
                adapter.notifyDataSetChanged();
                if (cd.isConnectingToInternet())
                {//ISTIAQUE
                    if (from != null) {
                        new LoadQuery().execute(from, to);
                    } else {
                        new LoadQuery().execute();
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }
                queryText.setText("All");
                closeDropdown();
                break;

            case R.id.mqsort:
                final PopupWindow popupWindow = new PopupWindow(MyQuery.this);
                View view = LayoutInflater.from(MyQuery.this).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.delete_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.sort_query_name).setVisibility(View.GONE);
                view.findViewById(R.id.sort_need_desc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_recent).setVisibility(View.VISIBLE);
                if (prev_sort == 1)
                    view.findViewById(R.id.action_recent_image).setVisibility(View.VISIBLE);
                else if (prev_sort == 2)
                    view.findViewById(R.id.action_need_image).setVisibility(View.VISIBLE);


                view.findViewById(R.id.sort_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("created_at", "desc", 1);
                        popupWindow.dismiss();
                    }
                });
                view.findViewById(R.id.sort_need_desc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("needed_date", "asc", 2);
                        popupWindow.dismiss();
                    }
                });

//                view.findViewById(R.id.sort_query_name).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        preTaskCall("product_name", "asc", 3);
//                        popupWindow.dismiss();
//                    }
//                });


                popupWindow.setContentView(view);
                popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int xOffset = -(view.getMeasuredWidth() - (sort_button.getWidth() / 2) + 10);
                // int yOffset = (sort_button.getHeight()/);
                popupWindow.showAsDropDown(sort_button, 0, 5);

                break;


        }
    }

    private void preTaskCall(String sort, String order, int prev) {
        prev_sort = prev;
        sort_by = sort;
        sort_order = order;
        page_no = 1;
        list.clear();
        adapter.notifyDataSetChanged();
        if (cd.isConnectingToInternet())
        {//ISTIAQUE
            if (from != null) {
                new LoadQuery().execute(from, to);
            } else {
                new LoadQuery().execute();
            }
        }

        else
        {
            new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                from = data.getStringExtra("from");
                to = data.getStringExtra("to");
                list.clear();
                adapter.notifyDataSetChanged();
                page_no = 1;
                if (cd.isConnectingToInternet())
                {//ISTIAQUE
                    new LoadQuery().execute(from, to);
                }

                else
                {
                    new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }

            } else {

                from = null;
                to = null;
                list.clear();
                adapter.notifyDataSetChanged();
                page_no = 1;
                if (cd.isConnectingToInternet())
                {//ISTIAQUE
                    new LoadQuery().execute();
                }

                else
                {
                    new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_search_menu, menu);

        MenuItem item = menu.findItem(R.id.add_srch_cart);
        //Log.i("item", item.toString() );
        MenuItemCompat.setActionView(item, R.layout.cart_count);
        //  Log.i("item", MenuItemCompat.getActionView(item).toString());
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
                Log.i("", " insdie GetUserCategories json ");
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");

                startActivity(new Intent(MyQuery.this, CartActivity.class));
            }
        });


        SearchView searchView = (SearchView) menu.findItem(R.id.add_srch_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);


        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_srch_add:
                startActivity(new Intent(MyQuery.this, SubmitQuery.class));
                list.clear();
                adapter.notifyDataSetChanged();
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {


        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");

            listView.clearTextFilter();
            filterText = null;
        } else {

            adapter.getFilter().filter(newText);
            filterText = newText;
        }
        return true;
    }

    private class LoadQuery extends AsyncTask<String, String, String> {
        String[] product_updcategory;

        int flag = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("vendor_id", user_id));
            params.add(new BasicNameValuePair("page", String.valueOf(page_no)));
            params.add(new BasicNameValuePair("sort_order", "" + sort_order));
            params.add(new BasicNameValuePair("sort_by", "" + sort_by));
            params.add(new BasicNameValuePair("category_id", "" + category_id));
            if (args.length > 0) {
                params.add(new BasicNameValuePair("date_from", "" + args[0]));
                params.add(new BasicNameValuePair("date_to", "" + args[1]));
            }
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "queries", "GET", params, MyQuery.this);
            if (json != null) {
                Log.e("JSS product", json.toString());
                if (json.has("query_data")) {

                    try {
                        JSONArray array = json.getJSONArray("query_data");
                        if (array.length() == 0) {
                            page_no = 0;
                            return null;
                        }
                        for (int i = 0; i < array.length(); i++) {


                            JSONObject obj = array.getJSONObject(i);
                            QueryModal modal = new QueryModal();
                            modal.setQuery_id(obj.getString("id"));
                            modal.setCode(obj.getString("product_code"));
                            modal.setLocation(obj.getString("location"));
                            modal.setNeeded(obj.getString("needed_date"));
                            modal.setTitle(obj.getString("product_name"));
                            modal.setQuantity(obj.getString("quantity"));
                            String cats = obj.getString("id_path");

                            product_updcategory = cats.split("/");
                            modal.setId(obj.getString("vendor_id"));
                            modal.setType(obj.getString("order_type"));
                            modal.setCategoryID(product_updcategory[0]);
                            if (product_updcategory.length > 1)
                                modal.setSubCategoryID(product_updcategory[1]);
                            if (product_updcategory.length > 2)
                                modal.setChild_categoryid(product_updcategory[2]);
                            else
                                modal.setChild_categoryid("");
                            if (product_updcategory.length > 3)
                                modal.setGrand_category_id(product_updcategory[3]);
                            else
                                modal.setGrand_category_id("");
                            // Log.e("--",product_updcategory[0]+"/"+product_updcategory[1]+"/"+product_updcategory[2]);
                            List<String> image_urls = new ArrayList<>();
                            if (obj.has("thumbnails") && obj.get("thumbnails") instanceof JSONObject) {
                                JSONObject obj2 = obj.getJSONObject("thumbnails");
                                modal.setImageurls(obj2.optString("image_path"));
                                image_urls.add(obj2.optString("detailed_image_path"));
                            }
                            if (obj.has("image_pairs") && obj.get("image_pairs") instanceof JSONObject) {
                                obj.getJSONObject("image_pairs").keys();
                                JSONObject image_pairs = obj.getJSONObject("image_pairs");
                                Iterator<String> iterator = image_pairs.keys();
                                while (iterator.hasNext()) {
                                    JSONObject object = image_pairs.getJSONObject(iterator.next());
                                    image_urls.add(object.getJSONObject("detailed").optString("image_path"));
                                }
                            }
                            modal.setImageURLs(image_urls);
                            list.add(modal);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject obj = json.getJSONObject("params");
                        if (obj.has("date_from")) {
                            from = obj.getString("date_from");

                        } else {
                            from = null;
                        }
                        if (obj.has("date_to")) {
                            to = obj.getString("date_to");
                        } else {
                            to = null;
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

            } else {
                flag = 1;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!isFinishing()) {
                super.onPostExecute(s);
                //  progress.dismiss();
                if (flag == 0) {
                    if (page_no == 0) {
                        listView.removeFooterView(loadingFooter);
                        if (list.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    if (list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        order_status.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        if(ufList.contains(AppUtil.TAG_Business_Lead))
                        {}
                        else
                        {
                        alertDialog.setTitle(getResources().getString(R.string.oops));
                        alertDialog.setMessage(getString(R.string.noqueries));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //   finish();
                            }
                        });
                        alertDialog.show();}
                    } else {
                        adapter.notifyDataSetChanged();
                        if (filterText != null) {
                            adapter.getFilter().filter(filterText);
                        }
                        isLoading = false;
                    }
                } else {
                    if (list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        order_status.setVisibility(View.VISIBLE);
                    }
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    alertDialog.show();
                    page_no = 0;
                    listView.removeFooterView(loadingFooter);
                    adapter.notifyDataSetChanged();
                }
            }


        }
    }

    @Override
    public void onBackPressed() {


        if (explistView.getVisibility() == View.VISIBLE) {
            closeDropdown();
        } else {
            category_id = "";
            if (backflag) {
                startActivity(new Intent(MyQuery.this, MyProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                finish();

            } else {
                startActivity(new Intent(MyQuery.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                finish();
            }
        }


    }

    private void openDropdown() {

        if (explistView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            explistView.startAnimation(anim);
            explistView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

        }
    }
    //  }

    private void closeDropdown()
    {
        // expend = false;
        if (explistView.getVisibility() == View.VISIBLE)
        {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            // ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            explistView.startAnimation(anim);
            explistView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void loadCategory()
    {
        obj = new ObjectItems();
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
        int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
        int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here

            ObjectItems root = new ObjectItems();
            root.id = cursor.getString(iId);
            root.children = new ArrayList<ObjectItems>();
            root.title = cursor.getString(iName) /*+ " (" + itemParent + ")"*/;
            obj.children.add(root);
            //cursorParent.close();
        }

        cursor.close();


        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {

                return false/* or false depending on what you need */;
            }
        };


        ExpandableListView.OnChildClickListener childLst = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView eListView, View view, int groupPosition,
                                        int childPosition, long id) {
                //Adapter adapter=eListView.getExpandableListAdapter().getChild();

                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter2 = new QueryCategoryAdapter(MyQuery.this, obj, grpLst, childLst, grpExpLst);
        explistView.setAdapter(adapter2);
        //   Log.d("cat status", "Load");
        openDropdown();

    }

    private void loadSubCategory() {
        obj = new ObjectItems();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(MyQuery.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + CategoryTable.TABLE_CONTACTS + " where " + CategoryTable.KEY_PARENT_ID + "=0  and " + CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" + MyCategoryTable.KEY_PARENT_ID + ") from " + MyCategoryTable.TABLE_CONTACTS + ")", null);
            int iId = cursor.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_ID);
            int iName = cursor.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_NAME);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ObjectItems root = new ObjectItems();
                root.id = cursor.getString(iId);
                root.children = new ArrayList<ObjectItems>();
                root.title = cursor.getString(iName) /*+ " (" + itemCount + ")"*/;
                obj.children.add(root);
            }
            cursor.close();
            db.close();
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("AllSELLERS", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("AllSELLERS", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("AllSELLERS", "Exception:" + e.getMessage());
        }
        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {

                return false/* or false depending on what you need */;
            }
        };
        ExpandableListView.OnChildClickListener childLst = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView eListView, View view, int groupPosition,
                                        int childPosition, long id) {
                //Adapter adapter=eListView.getExpandableListAdapter().getChild();

                return false/* or false depending on what you need */;
            }
        };
        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };
        adapter2 = new QueryCategoryAdapter(MyQuery.this, obj, grpLst, childLst, grpExpLst);
        explistView.setAdapter(adapter2);
        openDropdown();
    }

    private class GetUserCategory extends AsyncTask<String, String, JSONObject> {

        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        int flag = 0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing())
                progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params, MyQuery.this);
            try {
                if (json != null) {
                    //   Log.i(TAG, " insdie GetUserCategories json " + json.toString());
                    if (json.has(KEY_SUCCESS)) {
                        catdata = new ArrayList<>();
                        JSONObject childJSONObject = json.getJSONObject(KEY_SUCCESS);
                        Iterator<?> keys = childJSONObject.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject js = childJSONObject.getJSONObject(key);
                            CategoryDataModal cd1 = new CategoryDataModal();
                            cd1.setId(js.getString("category_id"));
                            cd1.setName(js.getString("category"));
                            cd1.setParentid(js.getString("parent_id"));
                            cd1.setProduct_count(js.getString("product_count"));
                            if (js.has("subcategories"))
                                cd1.setGot_child("1");
                            else
                                cd1.setGot_child("0");
                            Log.i("SUBCATEGORY", "DATA -- > " + cd1.getId() + "/" + cd1.getName() + "/" + cd1.getHas_child() + "/" + cd1.getParentid() + "/" + cd1.getProduct_count());
                            catdata.add(cd1);
                            if (js.has("subcategories")) {
                                JSONArray js1 = js.getJSONArray("subcategories");
                                for (int k = 0; k < js1.length(); k++) {
                                    JSONObject js2 = js1.getJSONObject(k);
                                    CategoryDataModal cd2 = new CategoryDataModal();
                                    cd2.setId(js2.getString("category_id"));
                                    cd2.setName(js2.getString("category"));
                                    cd2.setParentid(js2.getString("parent_id"));
                                    cd2.setProduct_count(js2.getString("product_count"));
                                    if (js2.has("subcategories"))
                                        cd2.setGot_child("1");
                                    else
                                        cd2.setGot_child("0");
                                    Log.i("SUBCATEGORY", "DATA -- > " + cd2.getId() + "/" + cd2.getName() + "/" + cd2.getHas_child() + "/" + cd2.getParentid() + "/" + cd2.getProduct_count());
                                    catdata.add(cd2);
                                    if (js2.has("subcategories")) {
                                        JSONArray js3 = js2.getJSONArray("subcategories");
                                        for (int l = 0; l < js3.length(); l++) {
                                            JSONObject js4 = js3.getJSONObject(l);
                                            CategoryDataModal cd3 = new CategoryDataModal();
                                            cd3.setId(js4.getString("category_id"));
                                            cd3.setName(js4.getString("category"));
                                            cd3.setParentid(js4.getString("parent_id"));
                                            cd3.setProduct_count(js4.getString("product_count"));
                                            if (js4.has("subcategories"))
                                                cd3.setGot_child("1");
                                            else
                                                cd3.setGot_child("0");
                                            Log.i("SUBCATEGORY", "DATA -- > " + cd3.getId() + "/" + cd3.getName() + "/" + cd3.getHas_child() + "/" + cd3.getParentid() + "/" + cd3.getProduct_count());
                                            catdata.add(cd3);
                                            if (js4.has("subcategories")) {
                                                JSONArray js6 = js4.getJSONArray("subcategories");
                                                for (int t = 0; t < js6.length(); t++) {
                                                    JSONObject js7 = js6.getJSONObject(t);
                                                    CategoryDataModal cd4 = new CategoryDataModal();
                                                    cd4.setId(js7.getString("category_id"));
                                                    cd4.setName(js7.getString("category"));
                                                    cd4.setParentid(js7.getString("parent_id"));
                                                    cd4.setProduct_count(js7.getString("product_count"));
                                                    if (js7.has("subcategories"))
                                                        cd4.setGot_child("1");
                                                    else
                                                        cd4.setGot_child("0");
                                                    Log.i("SUBCATEGORY", "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" + cd4.getProduct_count());
                                                    catdata.add(cd4);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                    /*********************** ISTIAQUE ***************************/
                    if (json.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/

                } else {
                    flag = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //Log.e("HERE", "PSOT");
//            Log.i("Act",getActivity().toString());
            if ((MyQuery.this != null) && !isFinishing()) {
                progressDialog.dismiss();
                if (flag == 1) {
                    loadSubCategory();
                } else {
                    if (catdata != null) {
                        getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
                        // Log.i("AddProdct", "DELETED" + a);
                        for (int s = 0; s < catdata.size(); s++) {
                            ContentValues values = new ContentValues();
                            values.put(MyCategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                            values.put(MyCategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                            values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getGot_child());
                            values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                            values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                            values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));
                            Log.i("SUBCATEOGRY", "DATA -- > final" + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getGot_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                            getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);
                            getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_MYCATEGORY, null, false);
                            //     Log.i("Helper", "uri at mycatalog " + uri.toString());

                        }

                    }
                    loadSubCategory();
                }
            }
        }
    }


}
