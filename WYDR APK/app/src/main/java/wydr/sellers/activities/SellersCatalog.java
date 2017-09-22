package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.navdrawer.SimpleSideDrawer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.acc.SecondLevelAdapter;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.adapter.SellerCatalogAdapter;
import wydr.sellers.gson.ProductList;
import wydr.sellers.gson.ProductModal;
import wydr.sellers.modal.NavDrawerItem;
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
 * Created by surya on 14/8/15.
 */
public class SellersCatalog extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {
    public static int FILTER_REQUEST = 104;
    public Toolbar mToolbar;
    Toolbar toolbar;
    ArrayList<ProductModal> list;
    SellerCatalogAdapter adapter;
    ListView listView;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    Button all, my;
    String companyid, username, userid, BackFlag, user_id, search_string, currentUserId;
    ImageView sliderMenu;
    SimpleSideDrawer slider;
    ListView items;
    Helper helper = new Helper();
    // RootAdapter rootAdapter;
    ExpandableListView expandableListView;
    NavDrawerListAdapter sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;
    ObjectItems obj;
    JSONObject myObject, allObejct;
    ImageView menu;
    TextView tooltitle;
    JSONObject jsonArray2 = null;
    String cat_id = "";
    String[] backflags = new String[]{};
    ImageView filter;
    Animation animation;
    LinearLayout sort_button, category_button, ll;
    String filterResult, sort_order, sort_by;
    SearchView searchView;
    TextView queryText, category_filter, order_status;
    int prev_view = 4, curr_view = 4;
    LinearLayout all_header;
    private ProgressDialog progress;
    // slide menu items
    private boolean isSelect = true;
    private int page_no;
    private boolean isLoading;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    private boolean search_Str = true;
    android.app.AlertDialog.Builder alertDialog;
    private String product_id, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycatalog);
        companyid = getIntent().getStringExtra("company_id");
        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        BackFlag = getIntent().getStringExtra("BackFlag");
        user_id = getIntent().getStringExtra("user_id");
        backflags = BackFlag.split("/");
        progressStuff();
        if (getIntent().getStringExtra("search_string") != null) {
            search_string = getIntent().getStringExtra("search_string");
        }
        if (getIntent().getStringExtra("product_id") != null) {
            product_id = getIntent().getStringExtra("product_id");
        }
        if (getIntent().getStringExtra("title") != null) {
            title = getIntent().getStringExtra("title");
        }

        iniStuff();

        list.clear();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(helper.ConvertCamel(username) + "'s Catalog");
        currentUserId = helper.getDefaults("user_id", SellersCatalog.this);
        tooltitle.setOnClickListener(this);
        NetAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                list.clear();

                filterResult = data.getStringExtra("result");
                page_no = 1;
                prepareRequest();

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.odateFilterQuery:
                if (jsonArray2 != null) {
                    if (jsonArray2.length() > 1) {
                        startActivityForResult(new Intent(SellersCatalog.this, FilterActivity.class).putExtra("filter", jsonArray2.toString()).putExtra("request_code", FILTER_REQUEST).putExtra("companyid", companyid).putExtra("cat_id", cat_id), FILTER_REQUEST);
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(SellersCatalog.this, getString(R.string.oops), getString(R.string.no_filter_available));
                }
                break;
            case R.id.ocategory_button:
                order_status.setVisibility(View.GONE);
                if (expandableListView.getVisibility() == View.VISIBLE) {
                    closeDropdown();
                    // listView.setVisibility(View.GONE);
                } else {
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        if (expandableListView.getAdapter() != null) {
                            if (expandableListView.getAdapter().isEmpty()) {
                                if (!companyid.equalsIgnoreCase(helper.getDefaults("company_id", SellersCatalog.this))) {
                                    new MyCategory().execute(companyid);
                                } else {
                                    new MySellCategory().execute(helper.getDefaults("company_id", getApplicationContext()));
                                }
                            } else {
                                openDropdown();
                            }
                        } else {
                            if (!companyid.equalsIgnoreCase(helper.getDefaults("company_id", SellersCatalog.this))) {
                                new MyCategory().execute(companyid);
                            } else {
                                new MySellCategory().execute(helper.getDefaults("company_id", getApplicationContext()));
                            }
                        }
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                    // listView.setVisibility(View.VISIBLE);
                    //openDropdown();
                }
                break;
            case R.id.allCategory:
                if (!companyid.equalsIgnoreCase(helper.getDefaults("company_id", SellersCatalog.this))) {
                    all.setTextColor(getResources().getColor(R.color.primary_500));
                    my.setTextColor(getResources().getColor(R.color.text_color));
                    all_header.setVisibility(View.VISIBLE);
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new MyCategory().execute(companyid);
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }

                break;
            case R.id.myCategory:
                if (!companyid.equalsIgnoreCase(helper.getDefaults("company_id", SellersCatalog.this))) {
                    my.setTextColor(getResources().getColor(R.color.primary_500));
                    all.setTextColor(getResources().getColor(R.color.text_color));
                    all_header.setVisibility(View.GONE);
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new MySellCategory().execute(helper.getDefaults("company_id", getApplicationContext()));
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
                break;
            case R.id.all_header:
                closeDropdown();
                cat_id = "";
                category_filter.setText("Category");
                Log.i("clicked", "clicked");
                filterResult = new JSONObject().toString();
                list.clear();

                page_no = 1;
//                if (listView.getFooterViewsCount() == 0)
//                    listView.addFooterView(loadingFooter);
//                adapter.notifyDataSetChanged();
                // new LoadCatalog().execute();
                prepareRequest();
                break;
            case R.id.osort:
                final PopupWindow popupWindow = new PopupWindow(SellersCatalog.this);
                final View view = LayoutInflater.from(SellersCatalog.this).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.delete_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.sort_asc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_desc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_name).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_recent).setVisibility(View.VISIBLE);
                if (prev_view == 1)
                    view.findViewById(R.id.action_asc_image).setVisibility(View.VISIBLE);
                else if (prev_view == 2)
                    view.findViewById(R.id.action_desc_image).setVisibility(View.VISIBLE);
                else if (prev_view == 3)
                    view.findViewById(R.id.action_name_image).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.action_recent_image).setVisibility(View.VISIBLE);

                view.findViewById(R.id.sort_asc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("price", "asc", 1);
                        popupWindow.dismiss();
                        view.findViewById(R.id.action_asc_image).setVisibility(View.INVISIBLE);

                    }
                });
                view.findViewById(R.id.sort_desc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("price", "desc", 2);
                        popupWindow.dismiss();
                        view.findViewById(R.id.action_desc_image).setVisibility(View.INVISIBLE);

                    }
                });
                view.findViewById(R.id.sort_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("product", "asc", 3);
                        popupWindow.dismiss();
                        view.findViewById(R.id.action_name_image).setVisibility(View.INVISIBLE);

                    }
                });
                view.findViewById(R.id.sort_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("timestamp", "desc", 4);
                        popupWindow.dismiss();
                        view.findViewById(R.id.action_recent_image).setVisibility(View.INVISIBLE);
                    }
                });
                popupWindow.setContentView(view);
                popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                popupWindow.showAsDropDown(sort_button, 0, 5);
                break;

        }
    }

    private void preTaskCall(String sort, String order, int prev) {
        sort_by = sort;
        sort_order = order;
        prev_view = prev;
        list.clear();

        page_no = 1;
        if (cd.isConnectingToInternet()) {
//            if (listView.getFooterViewsCount() == 0)
//                listView.addFooterView(loadingFooter);
//            adapter.notifyDataSetChanged();
            prepareRequest();
//            new LoadCatalog().execute();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
        adapter.notifyDataSetChanged();

    }

    private void openDropdown() {

        if (expandableListView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            expandableListView.startAnimation(anim);
            // mDropdownTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            // R.drawable.icn_dropdown_close, 0);
            expandableListView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            // listView.setImageResource(R.drawable.collapse_1);
        }
    }
    //  }

    private void closeDropdown() {

        if (expandableListView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            // ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            expandableListView.startAnimation(anim);
            expandableListView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
        }

    }


    public void NetAsync() {

        if (cd.isConnectingToInternet()) {
            list.clear();
            adapter.notifyDataSetChanged();
            prepareRequest();
            //  new LoadCatalog().execute();

        } else {
            new AlertDialogManager().showAlertDialog(SellersCatalog.this, getString(R.string.oops), getString(R.string.no_internet_conn));
        }
    }


    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        parser = new JSONParser();
        progress = new ProgressDialog(SellersCatalog.this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(this);
    }

    private void iniStuff() {
        queryText = (TextView) findViewById(R.id.sellerQueryText);
        order_status = (TextView) findViewById(R.id.catalog_record_status);
        page_no = 1;
        isLoading = true;
        filterResult = "";
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        ll = (LinearLayout) findViewById(R.id.obottombar);
        sort_by = "timestamp";
        sort_order = "desc";
        sort_button = (LinearLayout) findViewById(R.id.osort);
        category_button = (LinearLayout) findViewById(R.id.ocategory_button);
        sort_button.setOnClickListener(this);
        category_button.setOnClickListener(this);
        category_filter = (TextView) findViewById(R.id.sellerQueryText);
        filter = (ImageView) findViewById(R.id.odateFilterQuery);
        ll.setVisibility(View.VISIBLE);
        filter.setVisibility(View.VISIBLE);
        filter.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewCatalog);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableMyCatalog);
        View view = View.inflate(SellersCatalog.this, R.layout.category_header, null);
        all = (Button) view.findViewById(R.id.allCategory);
        all.setText(helper.ConvertCamel(username) + "'s Categories");
        all_header = (LinearLayout) view.findViewById(R.id.all_header);
        my = (Button) view.findViewById(R.id.myCategory);
        my.setText(getString(R.string.common_cats));
        if (companyid.equalsIgnoreCase(helper.getDefaults("company_id", SellersCatalog.this))) {
            all.setVisibility(View.GONE);
            my.setText(getString(R.string.categories));
        }
        expandableListView.addHeaderView(view, null, false);
        all.setOnClickListener(this);
        all_header.setOnClickListener(this);
        my.setOnClickListener(this);
        list = new ArrayList<>();
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (isSelect) {
                    TextView tv = (TextView) v.findViewById(R.id.itemRootTitle);
                    if (parent.isGroupExpanded(groupPosition)) {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    } else {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }
                } else {
                    TextView tv = (TextView) v.findViewById(R.id.itemParentTitle);
                    if (parent.isGroupExpanded(groupPosition)) {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    } else {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }
                    expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                            ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                            //header.setText(items.title);
                            closeDropdown();
                            cat_id = items.id;
                            if (items.title.contains("(")) {
                                category_filter.setText(items.title.substring(0, items.title.indexOf("(")));
                            } else {
                                category_filter.setText(items.title);
                            }
                            filterResult = new JSONObject().toString();
                            list.clear();
                            page_no = 1;
                            prepareRequest();
                            return false;
                        }
                    });
                }
                return false;
            }
        });
        adapter = new SellerCatalogAdapter(SellersCatalog.this, list, companyid, username, BackFlag, user_id);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
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

                if (isLoading || page_no == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    page_no++;
                    if (search_Str) {
                        prepareRequest();
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

                startActivity(new Intent(SellersCatalog.this, CartActivity.class));
            }
        });

        searchView = (SearchView) menu.findItem(R.id.searchCart_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                Log.d("1", "22");
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
            search_Str = true;
            adapter.getFilter().filter("");

            listView.clearTextFilter();
        } else {

            adapter.getFilter().filter(newText.toString());
            search_Str = false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (expandableListView.getVisibility() == View.VISIBLE) {
            closeDropdown();
        } else {
            if (backflags[0].equalsIgnoreCase("AllSellers")) {
                startActivity(new Intent(SellersCatalog.this, AllSellers.class));
                finish();
            } else if (backflags[0].equalsIgnoreCase("Home")) {
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                finish();
            } else if (backflags[0].equalsIgnoreCase("SearchSellers")) {
                if (cd.isConnectingToInternet()) {
                    startActivity(new Intent(SellersCatalog.this, SearchSellers.class).putExtra("search_string", search_string));
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(SellersCatalog.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                }
            } else {

                startActivity(new Intent(SellersCatalog.this, SellerProfile.class)
                        .putExtra("username", username)
                        .putExtra("company_id", companyid)
                        .putExtra("product_id", product_id)
                        .putExtra("title", title)
                        .putExtra("userid", userid)
                        .putExtra("user_id", user_id)
                        .putExtra("BackFlag", backflags[1]));
                finish();
            }
        }

    }


    private class MyCategory extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Log.d("json", "Come pre ");
            if (!isFinishing())
                progress.show();

        }

        @Override
        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("status", "A"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            Log.d("Param", params.toString());
            JSONObject json = parser.makeHttpRequest(AppUtil.URL
                    + "3.0/categories", "GET", params, SellersCatalog.this);


            if (json.has("categories")) {
                try {
                    obj = new ObjectItems();
                    obj.children = new ArrayList<>();

                    JSONObject myObj = json.getJSONObject("categories");
                    allObejct = myObj;

                    for (Iterator<String> iter = myObj.keys(); iter.hasNext(); ) {
                        String key = iter.next();
                        JSONObject objectRoot = myObj.getJSONObject(key);

                        int countRootItem = 0;
                        ObjectItems root = new ObjectItems();
                        root.title = objectRoot.getString("category");
                        root.id = objectRoot.getString("category_id");
                        root.children = new ArrayList<ObjectItems>();
                        if (objectRoot.has("subcategories")) {
                            JSONArray parentArray = objectRoot.getJSONArray("subcategories");
                            for (int j = 0; j < parentArray.length(); j++) {
                                JSONObject parentObject = parentArray.getJSONObject(j);
                                int parentCount = parentObject.getInt("product_count");

                                if (parentCount > 0) {
                                    Log.i("Entered Details--", parentObject.getString("category"));
                                    //  int countParantItem = 0;
                                    ObjectItems parent = new ObjectItems();
                                    countRootItem += parentCount;
                                    parent.title = parentObject.getString("category");
                                    parent.count = parentCount;
                                    parent.id = parentObject.getString("category_id");
                                    root.children.add(parent);


                                }
                            }
                            if (countRootItem > 0) {
                                root.title = objectRoot.getString("category")/* + " (" + countRootItem + ")"*/;
                                root.count = countRootItem;
                                obj.children.add(root);
                            }

                        }
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (this != null && !isFinishing()) {
                super.onPostExecute(s);
                progress.dismiss();
                isSelect = false;
                Log.d("json", "Come Post ");
                SecondLevelAdapter adapter = new SecondLevelAdapter(obj, SellersCatalog.this);
                expandableListView.setAdapter(adapter);
                Log.d("cat status", "Load");
                openDropdown();
            }


        }
    }

    private class MySellCategory extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Log.d("json", "Come pre ");
            progress.show();

        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("status", "A"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            Log.d("Param", params.toString());

            JSONObject json = parser.makeHttpRequest(AppUtil.URL
                    + "3.0/categories", "GET", params, SellersCatalog.this);


            if (json.has("categories")) {
                try {
                    obj = new ObjectItems();
                    obj.children = new ArrayList<>();

                    myObject = json.getJSONObject("categories");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /*********************** ISTIAQUE ***************************/
            if (json.has("401")) {
                session.logoutUser();
            }
            /*********************** ISTIAQUE ***************************/

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (this != null && !isFinishing()) {
                super.onPostExecute(s);
                progress.dismiss();
                isSelect = false;
                ObjectItems obj = new ObjectItems();
                obj.children = new ArrayList<>();

                for (Iterator<String> iter = myObject.keys(); iter.hasNext(); )
                {
                    String key = iter.next();
                    try
                    {
                        if (!companyid.equalsIgnoreCase(helper.getDefaults("company_id", SellersCatalog.this))) {
                            if (myObject.has(key) && allObejct.has(key)) {
                                int rootcount = 0;
                                JSONObject objectRoot = allObejct.getJSONObject(key);
                                ObjectItems root = new ObjectItems();
                                root.title = objectRoot.getString("category");
                                root.id = objectRoot.getString("category_id");
                                root.children = new ArrayList<>();
                                if (objectRoot.has("subcategories")) {

                                    JSONArray parentArray = objectRoot.getJSONArray("subcategories");
                                    for (int j = 0; j < parentArray.length(); j++) {

                                        JSONObject parentObject = parentArray.getJSONObject(j);
                                        int parentcount = parentObject.getInt("product_count");
                                        rootcount = rootcount + parentcount;

                                        if (parentcount > 0) {

                                            ObjectItems parent = new ObjectItems();
                                            parent.title = parentObject.getString("category");
                                            parent.id = parentObject.getString("category_id");
                                            parent.children = new ArrayList<ObjectItems>();
                                            root.children.add(parent);
                                        }


                                    }

                                    if (rootcount > 0)
                                        obj.children.add(root);

                                }
                            }
                        } else {

                            int rootcount = 0;
                            JSONObject objectRoot = myObject.getJSONObject(key);
                            ObjectItems root = new ObjectItems();
                            root.title = objectRoot.getString("category");
                            root.id = objectRoot.getString("category_id");
                            root.children = new ArrayList<>();

                            if (objectRoot.has("subcategories")) {

                                JSONArray parentArray = objectRoot.getJSONArray("subcategories");
                                for (int j = 0; j < parentArray.length(); j++) {

                                    JSONObject parentObject = parentArray.getJSONObject(j);
                                    int parentcount = parentObject.getInt("product_count");
                                    rootcount = rootcount + parentcount;

                                    if (parentcount > 0) {
                                        Log.i("Entered obj Details--", parentObject.getString("category"));
                                        ObjectItems parent = new ObjectItems();
                                        parent.title = parentObject.getString("category");
                                        parent.id = parentObject.getString("category_id");
                                        parent.children = new ArrayList<ObjectItems>();
                                        root.children.add(parent);
                                    }


                                }

                                if (rootcount > 0)
                                    obj.children.add(root);

                            }

                        }
                        //  }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                isSelect = false;
                SecondLevelAdapter adapter = new SecondLevelAdapter(obj, SellersCatalog.this);
                expandableListView.setAdapter(adapter);
                openDropdown();
            }


        }
    }

    private void prepareRequest() {
        if (!isFinishing())
            progress.show();
        isLoading = true;
        if (page_no != 0) {
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<ProductList> call = service.getSellersProduct(companyid, "0", page_no, "1", "A",
                    sort_order, sort_by, cat_id, filterResult, "public", currentUserId
                    , helper.getB64Auth(SellersCatalog.this), "application/json", "application/json");
            call.enqueue(new Callback<ProductList>() {
                @Override
                public void onResponse(Response response) {

                    isLoading = false;
                    progress.dismiss();

                    if (response.isSuccess()) {
                        ProductList holder = (ProductList) response.body();
                        if (holder.getProducts().size() < 10) {
                            page_no = 0;
                        }
                        JsonElement element = holder.getFilters();
                        jsonArray2 = null;
                        if (element != null && element.isJsonObject()) {
                            try {
                                jsonArray2 = new JSONObject(element.getAsJsonObject().toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        list.addAll(holder.getProducts());
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
                        int statusCode = response.code();
                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(SellersCatalog.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (SellersCatalog.this != null && !SellersCatalog.this.isFinishing()) {
                                new AlertDialogManager().showAlertDialog(SellersCatalog.this,
                                        getString(R.string.error),
                                        getString(R.string.server_error));
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    if (SellersCatalog.this != null && !SellersCatalog.this.isFinishing()) {
                        progress.dismiss();
                        if (list.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        page_no = 0;
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(SellersCatalog.this,
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SellersCatalog");
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
}
