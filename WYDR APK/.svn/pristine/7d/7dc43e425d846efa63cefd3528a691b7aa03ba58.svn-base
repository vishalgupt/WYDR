package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.navdrawer.SimpleSideDrawer;
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
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.adapter.AllSellersAdapter;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.adapter.RootAdapterExp;
import wydr.sellers.gson.SellerList;
import wydr.sellers.gson.SellerModal;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.NavDrawerItem;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.MyCategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Navdeep on 4/9/15.
 */
public class SearchSellers extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    public static SharedPreferences sharedPreferences;
    ImageView sliderMenu, filter, menu;
    Helper helper = new Helper();
    AllSellersAdapter adapter;
    SimpleSideDrawer slider;
    ListView items, sellerList;
    NavDrawerListAdapter sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;
    JSONObject jsonArray = null;
    ArrayList<SellerModal> users = new ArrayList<>();
    Context context;
    Dialog alertDialog;
    Button all_category, my_category;
    Animation animation;
    ExpandableListView listView;
    RelativeLayout category_button;
    ObjectItems obj;
    RootAdapterExp adapter2;
    JSONParser parser;
    TextView category_name, tooltitle;
    LinearLayout all_header;
    SearchView searchView;
    Toolbar toolbar;
    Drawable upArrow;
    private int page_no, FILTER_REQUEST = 105;
    private boolean isLoading, isSelect = true, seller_count = true;
    PrefManager prefManager;
    ArrayList<String>ufList= new ArrayList<>();
    private String user_cur_id, compid, netid, user_name, filterResult, filtertext = "", sort_order, sort_by, catfiltr_id = "", search_string;
    private ProgressDialog progress;
    private ConnectionDetector cd;
    private AlertDialog.Builder alertDialog2;
    TextView order_status;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        sort_by = "timestamp";
        sort_order = "desc";
        search_string = getIntent().getStringExtra("search_string");
        sharedPreferences = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.all_sellers);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        progressStuff();
        initUff();
        upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        tooltitle = (TextView) findViewById(R.id.tooltext);
        category_name = (TextView) findViewById(R.id.as_txtSubCatName);
        order_status = (TextView) findViewById(R.id.sellers_record_status);
        category_name.setText("Category");
        tooltitle.setText(getString(R.string.search_sellers));
        alertDialog = new Dialog(SearchSellers.this, R.style.mydialogstyle);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.overlay_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        page_no = 1;
        isLoading = true;
        sellerList = (ListView) findViewById(R.id.as_sellerList);
//        loadingFooter = LayoutInflater.from(SearchSellers.this).inflate(R.layout.pagination_loading, null);
//        if (sellerList.getFooterViewsCount() == 0)
//            sellerList.addFooterView(loadingFooter);
        listView = (ExpandableListView) findViewById(R.id.as_expandableCategory);
        category_button = (RelativeLayout) findViewById(R.id.as_category_button);
        View view = View.inflate(SearchSellers.this, R.layout.category_header, null);
        all_header = (LinearLayout) view.findViewById(R.id.all_header);
        all_category = (Button) view.findViewById(R.id.allCategory);
        my_category = (Button) view.findViewById(R.id.myCategory);
        all_category.setOnClickListener(this);
        my_category.setOnClickListener(this);
        all_header.setOnClickListener(this);
        category_button.setOnClickListener(this);
        listView.addHeaderView(view, null, false);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.i("SearchSellers", "setOnGroupClickListener");

                if (isSelect) {
                    TextView tv = (TextView) v.findViewById(R.id.itemRootTitle);
                    if (parent.isGroupExpanded(groupPosition)) {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    } else {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }
                } else {

                    TextView tv;
                    tv = (TextView) v.findViewById(R.id.itemRootTitle);
                    if (tv == null) {
                        tv = (TextView) v.findViewById(R.id.itemParentTitle);
                    }


                    ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                    if (items.children.size() == 0) {
                        Log.i("ALLSELERS - bootm", "3");
                        closeDropdown();
                        if (items.title.contains("(")) {
                            category_name.setText(items.title.substring(0, items.title.indexOf("(")));
                        } else {
                            category_name.setText(items.title);
                        }

                        catfiltr_id = items.id;
                        users.clear();
                        adapter.swapItems(users);
                        adapter.notifyDataSetChanged();
                        page_no = 1;
                        prepareRequest();
                        //  new GetSearchSellers().execute();
                    } else {
                        Log.i("ALLSELERS - bootm", "4");
                        if (parent.isGroupExpanded(groupPosition)) {
                            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                        } else {
                            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                        }
                    }

                    listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            //   Log.i("ALLSELERS - bootm", "5");
                            ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                            closeDropdown();
                            catfiltr_id = items.id;
                            users.clear();
                            page_no = 1;
                            adapter.swapItems(users);
                            if (items.title.contains("(")) {
                                category_name.setText(items.title.substring(0, items.title.indexOf("(")));
                            } else {
                                category_name.setText(items.title);
                            }
                            adapter.notifyDataSetChanged();
                            prepareRequest();

                            return false;
                        }
                    });

                }
                return false;
            }
        });
        adapter = new AllSellersAdapter(context, users, SearchSellers.this);
        sellerList.setAdapter(adapter);
        sellerList.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        category_button.startAnimation(animation);
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
//                    if (sellerList.getFooterViewsCount() == 0)
//                        sellerList.addFooterView(loadingFooter);
                    adapter.notifyDataSetChanged();
                    prepareRequest();
                    //  new GetSearchSellers().execute();
                }
//                if (visibleItemCount != 0)
//                    category_button.startAnimation(animation);
            }
        });
        sellerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter != null) {
                    SellerModal user_detail = adapter.getItem(position);
                    user_name = user_detail.getFirstName();
                    user_cur_id = user_detail.getUserId();
                    compid = user_detail.getCompanyId();
                    netid = user_detail.getNetworkId();
                    ImageView profile = (ImageView) alertDialog.findViewById(R.id.profile);
                    ImageView query = (ImageView) alertDialog.findViewById(R.id.query);
                    ImageView catalog = (ImageView) alertDialog.findViewById(R.id.catalog);
                    LinearLayout linearLayout = (LinearLayout) alertDialog.findViewById(R.id.overlayLayout);
                    //   Log.e("**********", user_detail.getName() + "*user_cur_id*" + user_cur_id);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                        }
                    });
                    profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //  Log.d("click", "profile");
                            if (cd.isConnectingToInternet()) {
                                startActivity(new Intent(SearchSellers.this, SellerProfile.class).putExtra("userid", user_cur_id).putExtra("username", user_name).putExtra("company_id", compid).putExtra("BackFlag", "SearchSellers").putExtra("search_string", search_string));
                            } else {
                                new AlertDialogManager().showAlertDialog(SearchSellers.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                            }

                            alertDialog.dismiss();
                        }
                    });
                    query.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //   Log.d("click", "query");
                            if (cd.isConnectingToInternet()) {
                                startActivity(new Intent(SearchSellers.this, SellersQuery.class).putExtra("seller_id", user_cur_id).putExtra("username", user_name).putExtra("company_id", compid).putExtra("userid", netid).putExtra("user_id", user_cur_id).putExtra("BackFlag", "SearchSellers" + "/1").putExtra("search_string", search_string));
                            } else {
                                new AlertDialogManager().showAlertDialog(SearchSellers.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                            }

                            alertDialog.dismiss();
                        }
                    });
                    catalog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cd.isConnectingToInternet()) {
                                startActivity(new Intent(SearchSellers.this, SellersCatalog.class).putExtra("username", user_name).putExtra("company_id", compid).putExtra("seller_id", user_cur_id).putExtra("userid", netid).putExtra("user_id", user_cur_id).putExtra("BackFlag", "SearchSellers" + "/1").putExtra("search_string", search_string))
                                ;
                            } else {
                                new AlertDialogManager().showAlertDialog(SearchSellers.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                            }

                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }

            }
        });
        prepareRequest();
        //new GetSearchSellers().execute();

    }

    private void initUff()
    {
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl_sellers);
        ImageView iv_bussiness=(ImageView)findViewById(R.id.iv_sellers);
        prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(),Catalog.class);
                startActivity(intent);
            }
        });
        if(ufList.contains(AppUtil.TAG_Search_Seller))
        {
            rl.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_Search_Seller+"_photo",getApplicationContext()))
                    .into(iv_bussiness);
        }

        else
        {
            rl.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }


    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(SearchSellers.this);
        parser = new JSONParser();
        progress = new ProgressDialog(SearchSellers.this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog2 = new AlertDialog.Builder(SearchSellers.this);
    }

    public void onFilter(View v) {
        if (jsonArray != null)
            startActivityForResult(new Intent(SearchSellers.this, LoacationFilterActivity.class).putExtra("filter", jsonArray.toString()).putExtra("request_code", FILTER_REQUEST), FILTER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //Log.d("result filter", data.getStringExtra("result"));
                filterResult = data.getStringExtra("result");
                //  Log.i("filterResult", filterResult);
                page_no = 1;
                users.clear();
                adapter.notifyDataSetChanged();
                prepareRequest();
                //   new GetSearchSellers().execute();
            } else {
                //      Log.i("filterResult", "" + filterResult);
                filterResult = "";
                page_no = 1;
                users.clear();
                adapter.notifyDataSetChanged();
                prepareRequest();
                //  new GetSearchSellers().execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView imageview = (ImageView) searchView.findViewById(searchImgId);
        imageview.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);
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


    @Override
    public void onBackPressed() {
        if (listView.getVisibility() == View.VISIBLE) {
            closeDropdown();
        } else {

            users.clear();
            adapter.notifyDataSetChanged();
            finish();
        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {

        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
            sellerList.clearTextFilter();
            filtertext = "";
        } else {
            filtertext = newText;
            adapter.getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.as_category_button:
                order_status.setVisibility(View.GONE);
                if (listView.getVisibility() == View.VISIBLE) {
                    closeDropdown();
                } else {
                    if (listView.getAdapter() != null) {
                        if (listView.getAdapter().isEmpty()) {
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
                all_category.setTextColor(getResources().getColor(R.color.primary_500));
                my_category.setTextColor(getResources().getColor(R.color.text_color));
                all_header.setVisibility(View.VISIBLE);
                loadCategory();
                break;
            case R.id.myCategory:
                all_category.setTextColor(getResources().getColor(R.color.text_color));
                my_category.setTextColor(getResources().getColor(R.color.primary_500));
                all_header.setVisibility(View.GONE);
                if (cd.isConnectingToInternet()) {
                    new GetUserCategory().execute(helper.getDefaults("company_id", SearchSellers.this));
                } else {
                    loadSubCategory();
                }
                break;
            case R.id.all_header:

                closeDropdown();
                category_name.setText("Category");
                catfiltr_id = "";
                users.clear();
                adapter.swapItems(users);
                adapter.notifyDataSetChanged();
                page_no = 1;
                prepareRequest();
                //  new GetSearchSellers().execute();
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        users.clear();
        adapter.notifyDataSetChanged();
    }

    private void openDropdown() {

        if (listView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            listView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        }
    }

    private void closeDropdown() {
        if (listView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            // ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            listView.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }


        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return false; /* or false depending on what you need */
            }
        });


    }

    private void loadCategory() {

        obj = new ObjectItems();

        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
        int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
        int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            ObjectItems root = new ObjectItems();
            root.id = cursor.getString(iId);
            root.children = new ArrayList<ObjectItems>();
            Cursor cursorParent = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{cursor.getString(iId)}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
            int RootCount = 0;
            root.level = 0;
            int iIdParent = cursorParent.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
            int iNameParent = cursorParent.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);

            for (cursorParent.moveToFirst(); !cursorParent.isAfterLast(); cursorParent.moveToNext()) {
                ObjectItems parent = new ObjectItems();

                parent.id = cursorParent.getString(iIdParent);

                if (!(parent.level == 2 || parent.level == 1))
                    parent.level = 0;
                if (!(root.level == 3 || root.level == 2))
                    root.level = 1;

                parent.children = new ArrayList<ObjectItems>();
                int ParentCount = 0;
                Cursor cursorChild = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{cursorParent.getString(iId)}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
                int iIdChild = cursorChild.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
                int iNameChild = cursorChild.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
                int iNameCount = cursorChild.getColumnIndexOrThrow(CategoryTable.KEY_PRODUCT_COUNT);


                for (cursorChild.moveToFirst(); !cursorChild.isAfterLast(); cursorChild.moveToNext()) {
                    ObjectItems child = new ObjectItems();
                    if (child.level != 1)
                        child.level = 0;
                    if (parent.level != 2)
                        parent.level = 1;
                    if (root.level != 3)
                        root.level = 2;
                    child.id = cursorChild.getString(iIdChild);
                    child.children = new ArrayList<ObjectItems>();
                    int itemsChildCount = 0;

                    Cursor cursorGrandChild = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{cursorChild.getString(iId)}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
                    int iIdGrandChild = cursorGrandChild.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
                    int iNameGrandChild = cursorGrandChild.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
                    int iNameGrandCount = cursorGrandChild.getColumnIndexOrThrow(CategoryTable.KEY_PRODUCT_COUNT);
                    for (cursorGrandChild.moveToFirst(); !cursorGrandChild.isAfterLast(); cursorGrandChild.moveToNext()) {
                        ObjectItems Grandchild = new ObjectItems();
                        Grandchild.title = cursorGrandChild.getString(iNameGrandChild) + " (" + cursorGrandChild.getInt(iNameGrandCount) + ")";
                        Grandchild.id = cursorGrandChild.getString(iIdGrandChild);
                        Grandchild.level = 0;
                        child.level = 1;
                        parent.level = 2;
                        root.level = 3;
                        child.children.add(Grandchild);
                        itemsChildCount += cursorGrandChild.getInt(iNameGrandCount);

                    }


                    itemsChildCount = cursorChild.getInt(iNameGrandCount);
                    child.title = cursorChild.getString(iNameChild) + " (" + itemsChildCount + ")";
                    cursorGrandChild.close();

                    parent.children.add(child);
                    ParentCount += itemsChildCount;


                }
                if (cursorChild.getCount() == 0) {
                    ParentCount = cursorParent.getInt(iNameCount);

                }
                RootCount += ParentCount;
                cursorChild.close();
                parent.title = cursorParent.getString(iNameParent) + " (" + ParentCount + ")";

                root.children.add(parent);
            }

            cursorParent.close();

            root.title = cursor.getString(iName) + " (" + RootCount + ")";

            obj.children.add(root);

        }
        cursor.close();
        isSelect = true;
        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {

                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getGroup(groupPosition);
                if (items.children.size() == 0) {
                    closeDropdown();
                    if (items.title.contains("(")) {
                        category_name.setText(items.title.substring(0, items.title.indexOf("(")));
                    } else {
                        category_name.setText(items.title);
                    }
                    catfiltr_id = items.id;
                    users.clear();
                    adapter.swapItems(users);
                    adapter.notifyDataSetChanged();
                    page_no = 1;
                    prepareRequest();
                    // new GetSearchSellers().execute();
                }
                TextView tv;
                tv = (TextView) view.findViewById(R.id.itemParentTitle);
                if (tv == null) {
                    tv = (TextView) view.findViewById(R.id.itemRootTitle);
                }

                if (eListView.isGroupExpanded(groupPosition)) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                } else {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                }

                return false/* or false depending on what you need */;
            }
        };


        ExpandableListView.OnChildClickListener childLst = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView eListView, View view, int groupPosition,
                                        int childPosition, long id) {
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getChild(groupPosition, childPosition);
                closeDropdown();
                if (items.title.contains("(")) {
                    category_name.setText(items.title.substring(0, items.title.indexOf("(")));
                } else {
                    category_name.setText(items.title);
                }
                catfiltr_id = items.id;
                users.clear();
                adapter.swapItems(users);
                adapter.notifyDataSetChanged();
                page_no = 1;
                prepareRequest();
                //    new GetSearchSellers().execute();
                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter2 = new RootAdapterExp(SearchSellers.this, obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter2);
        openDropdown();
    }


    private void loadSubCategory() {

        obj = new ObjectItems();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(SearchSellers.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + CategoryTable.TABLE_CONTACTS + " where " + CategoryTable.KEY_PARENT_ID + "=0  and " + CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" + MyCategoryTable.KEY_PARENT_ID + ") from " + MyCategoryTable.TABLE_CONTACTS + ")", null);
            // Log.i("allsellers", "select * from " + CategoryTable.TABLE_CONTACTS + " where " + CategoryTable.KEY_PARENT_ID + "=0  and " + CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" + MyCategoryTable.KEY_PARENT_ID + ") from " + MyCategoryTable.TABLE_CONTACTS + ")");
            int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
            int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
            //  Log.i("ALLSELELRS *", "cursor.getCount " + cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                ObjectItems root = new ObjectItems();
                root.id = cursor.getString(iId);
                root.children = new ArrayList<ObjectItems>();
                Cursor cursorParent = getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursor.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
                // Log.i("ALLSELELRS *", "cursorParent.getCount " + cursorParent.getCount());

                int RootCount = 0;
                root.level = 0;
                int iIdParent = cursorParent.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_ID);
                int iNameParent = cursorParent.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_NAME);

                for (cursorParent.moveToFirst(); !cursorParent.isAfterLast(); cursorParent.moveToNext()) {
                    ObjectItems parent = new ObjectItems();

                    parent.id = cursorParent.getString(iIdParent);

                    if (!(parent.level == 2 || parent.level == 1))
                        parent.level = 0;
                    if (!(root.level == 3 || root.level == 2))
                        root.level = 1;

                    parent.children = new ArrayList<ObjectItems>();
                    int ParentCount = 0;
                    Cursor cursorChild = getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursorParent.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
                    int iIdChild = cursorChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_ID);
                    int iNameChild = cursorChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_NAME);
                    int iNameCount = cursorChild.getColumnIndexOrThrow(MyCategoryTable.KEY_PRODUCT_COUNT);
                    //  Log.i("ALLSELELRS *", "cursorChild.getCount " + cursorChild.getCount());

                    for (cursorChild.moveToFirst(); !cursorChild.isAfterLast(); cursorChild.moveToNext()) {
                        ObjectItems child = new ObjectItems();
                        if (child.level != 1)
                            child.level = 0;
                        if (parent.level != 2)
                            parent.level = 1;
                        if (root.level != 3)
                            root.level = 2;
                        child.id = cursorChild.getString(iIdChild);
                        child.children = new ArrayList<ObjectItems>();
                        int itemsChildCount = 0;

                        Cursor cursorGrandChild = getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursorChild.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
                        int iIdGrandChild = cursorGrandChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_ID);
                        int iNameGrandChild = cursorGrandChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_NAME);
                        int iNameGrandCount = cursorGrandChild.getColumnIndexOrThrow(MyCategoryTable.KEY_PRODUCT_COUNT);
                        //   Log.i("ALLSELELRS *", "cursorGrandChild.getCount " + cursorGrandChild.getCount());
                        for (cursorGrandChild.moveToFirst(); !cursorGrandChild.isAfterLast(); cursorGrandChild.moveToNext()) {
                            if (cursorGrandChild.getInt(iNameGrandCount) > 0) {
                                ObjectItems Grandchild = new ObjectItems();
                                Grandchild.title = cursorGrandChild.getString(iNameGrandChild) + " (" + cursorGrandChild.getInt(iNameGrandCount) + ")";
                                Grandchild.id = cursorGrandChild.getString(iIdGrandChild);
                                Grandchild.level = 0;
                                child.level = 1;
                                parent.level = 2;
                                root.level = 3;
                                child.children.add(Grandchild);
                                itemsChildCount += cursorGrandChild.getInt(iNameGrandCount);
                                //   Log.i("ALLSELELRS *", "cursorGrandChild.getString(iNameGrandChild) " + cursorGrandChild.getString(iNameGrandChild) + itemsChildCount);

                            }
                        }


                        itemsChildCount = cursorChild.getInt(iNameGrandCount);
                        child.title = cursorChild.getString(iNameChild) + " (" + itemsChildCount + ")";
                        cursorGrandChild.close();
                        //   Log.i("ALLSELELRS *", "cursorChild.getString(iNameChild) " + cursorChild.getString(iNameChild) + itemsChildCount);
                        if (itemsChildCount > 0) {
                            parent.children.add(child);
                        }

                        ParentCount += itemsChildCount;


                    }
                    if (cursorChild.getCount() == 0) {
                        ParentCount = cursorParent.getInt(iNameCount);

                    }
                    RootCount += ParentCount;
                    cursorChild.close();
                    parent.title = cursorParent.getString(iNameParent) + " (" + ParentCount + ")";
                    // Log.i("ALLSELELRS *", "cursorParent.getString(iNameParent) " + cursorParent.getString(iNameParent) + ParentCount);
                    if (ParentCount > 0) {
                        root.children.add(parent);
                    }

                }

                cursorParent.close();

                root.title = cursor.getString(iName) + " (" + RootCount + ")";
                // Log.i("ALLSELELRS *", "cursor.getString(iName) " + cursor.getString(iName) + RootCount);
                if (RootCount > 0) {
                    obj.children.add(root);
                }


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
        isSelect = true;
        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {

                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getGroup(groupPosition);
                if (items.children.size() == 0) {
                    closeDropdown();
                    if (items.title.contains("(")) {
                        category_name.setText(items.title.substring(0, items.title.indexOf("(")));
                    } else {
                        category_name.setText(items.title);
                    }
                    catfiltr_id = items.id;
                    users.clear();
                    adapter.swapItems(users);
                    adapter.notifyDataSetChanged();
                    page_no = 1;
                    prepareRequest();
                    //  new GetSearchSellers().execute();
                }
                TextView tv;
                tv = (TextView) view.findViewById(R.id.itemParentTitle);
                if (tv == null) {
                    tv = (TextView) view.findViewById(R.id.itemRootTitle);
                }

                if (eListView.isGroupExpanded(groupPosition)) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                } else {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                }

                return false/* or false depending on what you need */;
            }
        };


        ExpandableListView.OnChildClickListener childLst = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView eListView, View view, int groupPosition,
                                        int childPosition, long id) {
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getChild(groupPosition, childPosition);
                closeDropdown();
                if (items.title.contains("(")) {
                    category_name.setText(items.title.substring(0, items.title.indexOf("(")));
                } else {
                    category_name.setText(items.title);
                }
                catfiltr_id = items.id;
                users.clear();
                adapter.swapItems(users);
                adapter.notifyDataSetChanged();
                page_no = 1;
                prepareRequest();
                //    new GetSearchSellers().execute();
                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter2 = new RootAdapterExp(SearchSellers.this, obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter2);
        openDropdown();
    }

//    public class GetSearchSellers extends AsyncTask<String, Void, JSONObject> {
//
//        JSONObject obj;
//        int flag = 0;
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            isLoading = true;
//        }
//
//        @Override
//        protected JSONObject doInBackground(String... args) {
//            JSONParser parser = new JSONParser();
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("user_type", "v"));
//            params.add(new BasicNameValuePair("get_image", "true"));
//            params.add(new BasicNameValuePair("page", "" + page_no));
//            params.add(new BasicNameValuePair("sort_order", "" + sort_order));
//            params.add(new BasicNameValuePair("sort_by", "" + sort_by));
//            params.add(new BasicNameValuePair("category_id", "" + catfiltr_id));
//            params.add(new BasicNameValuePair("is_root", "Y"));
//            params.add(new BasicNameValuePair("name", search_string));
//            if (filterResult != null) {
//                if (filterResult.length() > 0) {
//                    try {
//                        JSONArray array = new JSONArray(filterResult);
//                        for (int i = 0; i < array.length(); i++) {
//                            params.add(new BasicNameValuePair("location[" + i + "]", array.getString(i)));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            obj = parser.makeHttpRequest(AppUtil.URL + "sellers", "GET", params, SearchSellers.this);
//            if (obj != null) {
//                Log.i("obj--", obj.toString() + "");
//                if (obj.has("users")) {
//                    try {
//                        if (obj.getJSONObject("params").getInt("page") < page_no) {
//                            page_no = 0;
//                            return null;
//                        }
//                        JSONArray jsonArray = obj.getJSONArray("users");
//                        if (jsonArray.length() == 0) {
//                            page_no = 0;
//                            users.clear();
//                            seller_count = false;
//                            return null;
//                        } else {
//                            seller_count = true;
//                            for (int a = 0; a < jsonArray.length(); a++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(a);
//                                SellerModal lis = new SellerModal();
//                                lis.s(jsonObject.getString("firstname"));
//                                lis.setCompname(jsonObject.getString("company_name"));
//                                lis.setComp_id(jsonObject.getString("company_id"));
//                                lis.setUser_id(jsonObject.getString("user_id"));
//                                lis.setNetwork_userid(jsonObject.getString("user_login") + "@" + AppUtil.SERVER_NAME);
//                                if (jsonObject.has("main_pair")) {
//                                    JSONObject pair = jsonObject.getJSONObject("main_pair");
//                                    if (pair.has("icon")) {
//                                        JSONObject icon = pair.getJSONObject("icon");
//                                        lis.setUrl(icon.getString("image_path"));
//                                    }
//                                } else {
//                                    lis.setUrl("");
//                                }
//                                if (!jsonObject.getString("state").equalsIgnoreCase(""))
//                                    lis.setLocation(MyTextUtils.toTitleCase(jsonObject.getString("city")) + "," + MyTextUtils.toTitleCase(jsonObject.getString("state")));
//                                else
//                                    lis.setLocation(MyTextUtils.toTitleCase(jsonObject.getString("city")));
//                                users.add(lis);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (obj.has("filters")) {
//                    try {
//                        jsonArray = obj.getJSONObject("filters");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else if (obj.has("error")) {
//                    flag = 1;
//
//                }
//
//            } else {
//                flag = 1;
//            }
//            return obj;
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject aVoid) {
//            if (!isFinishing()) {
//                if (flag == 1) {
//                    new AlertDialogManager().showAlertDialog(SearchSellers.this, getString(R.string.sorry), getString(R.string.server_error));
//                    page_no = 0;
//                    if (sellerList.getFooterViewsCount() > 0) {
//                        sellerList.removeFooterView(loadingFooter);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                } else {
//                    if (page_no == 0) {
//                        if (sellerList.getFooterViewsCount() > 0) {
//                            sellerList.removeFooterView(loadingFooter);
//                            adapter.notifyDataSetChanged();
//                        }
//                        if (!seller_count) {
//                            new AlertDialogManager().showAlertDialog(SearchSellers.this, getString(R.string.oops), getString(R.string.no_user_found));
//                        }
//                    } else {
//                        if (filtertext != null) {
//                            adapter.getFilter().filter(filtertext);
//                        } else {
//                            adapter.swapItems(users);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                    super.onPostExecute(aVoid);
//                    isLoading = false;
//                }
//            }
//
//        }
//    }

    private class GetUserCategory extends AsyncTask<String, String, JSONObject> {
        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        int flag = 0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing())
                progress.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params, SearchSellers.this);
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
            if ((SearchSellers.this != null) && !isFinishing()) {
                progress.dismiss();
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

    private void prepareRequest() {
        ArrayList<String> loc = new ArrayList<>();
        if (!isFinishing())
            progress.show();
        isLoading = true;
        if (page_no != 0) {
            if (filterResult != null) {
                if (filterResult.length() > 0) {
                    try {
                        JSONArray array = new JSONArray(filterResult);
                        for (int i = 0; i < array.length(); i++) {
                            loc.add(array.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            RestClient.GitApiInterface service = RestClient.getClient();
            Call<SellerList> call = service.getSellers("v", page_no, "true", sort_order, sort_by,
                    catfiltr_id, "Y", loc, search_string, helper.getB64Auth(SearchSellers.this), "application/json", "application/json");
            call.enqueue(new Callback<SellerList>() {
                @Override
                public void onResponse(Response response) {

                    isLoading = false;
                    progress.dismiss();

                    if (response.isSuccess()) {
                        SellerList holder = (SellerList) response.body();
                        if (holder.getUsers().size() < 10) {
                            page_no = 0;
                        }
                        JsonElement element = holder.getFilters();
                        try {
                            jsonArray = null;
                            if (element != null && element.isJsonObject()) {

                                jsonArray = new JSONObject(element.getAsJsonObject().toString());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        users.addAll(holder.getUsers());
                        if (page_no == 0) {
                            if (users.size() == 0) {
                                sellerList.setVisibility(View.GONE);
                                order_status.setVisibility(View.VISIBLE);
                            } else {
                                sellerList.setVisibility(View.VISIBLE);
                                order_status.setVisibility(View.GONE);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        int statusCode = response.code();
                        if (statusCode == 401) {
                            
                            final SessionManager sessionManager = new SessionManager(SearchSellers.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (SearchSellers.this != null && !SearchSellers.this.isFinishing()) {
                                new AlertDialogManager().showAlertDialog(SearchSellers.this,
                                        getString(R.string.error),
                                        getString(R.string.server_error));
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    if (SearchSellers.this != null && !SearchSellers.this.isFinishing()) {
                        progress.dismiss();
                        if (users.size() == 0) {
                            sellerList.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            sellerList.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        page_no = 0;
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(SearchSellers.this,
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

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SearchSellers");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
