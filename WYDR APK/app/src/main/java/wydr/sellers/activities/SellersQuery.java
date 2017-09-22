package wydr.sellers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.navdrawer.SimpleSideDrawer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.acc.QueryCategoryAdapter;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.gson.QueryHolder;
import wydr.sellers.gson.QueryModal;
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
import wydr.sellers.slider.UserFunctions;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public class SellersQuery extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    static String from, to;
    static String cat_id = "";
    public Uri uri = null;
    public Toolbar mToolbar;
    Toolbar toolbar;
    ArrayList<wydr.sellers.gson.QueryModal> list;
    QueryAdapter adapter;
    ListView listView;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    String sellerid, username, companyid, userid, BackFlag, user_id, current_user_id, search_string;
    ImageView sliderMenu;
    SimpleSideDrawer slider;
    ListView items;
    NavDrawerListAdapter sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;
    AlertDialog.Builder alertDialog;
    ImageView menu;
    String[] backflags = new String[]{};
    String sort_order, sort_by;
    LinearLayout sort_button, category_button;
    Animation animation;
    LinearLayout ll;
    ImageView filter;
    Button all, my;
    ObjectItems obj;
    ExpandableListView explistView;
    QueryCategoryAdapter adapter2;
    SearchView searchView;
    TextView queryText, order_status;
    TextView allTab;
    int sortingOrder = 0;
    Helper helper = new Helper();
    int prev_sort = 1;
    private ProgressDialog progress;
    private int FILTER_REQUEST = 107;
    private boolean isSelect = true;
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    private String product_id, title;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    String chat_user_id;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycatalog);
        companyid = getIntent().getStringExtra("company_id");
        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        user_id = getIntent().getStringExtra("user_id");
        sellerid = getIntent().getStringExtra("seller_id");
        BackFlag = getIntent().getStringExtra("BackFlag");
        backflags = BackFlag.split("/");
        Log.i("userid", userid);
        Log.i("user_id", user_id);
        if (getIntent().getStringExtra("search_string") != null) {
            search_string = getIntent().getStringExtra("search_string");
        }
        if (getIntent().getStringExtra("product_id") != null) {
            product_id = getIntent().getStringExtra("product_id");
        }
        if (getIntent().getStringExtra("title") != null) {
            title = getIntent().getStringExtra("title");
        }

        current_user_id = helper.getDefaults("user_id", SellersQuery.this);
        progressStuff();
        iniStuff();
        queryText = (TextView) findViewById(R.id.sellerQueryText);

        NetAsync();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(helper.ConvertCamel(username) + "'s Leads");
        explistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                cat_id = items.id;
                if (items.title.contains("(")) {
                    queryText.setText(items.title.substring(0, items.title.indexOf("(")));
                } else {
                    queryText.setText(items.title);
                }
                closeDropdown();


                list.clear();

                page_no = 1;
                Log.i("sellers query", "tilll here 0");
                if (listView.getFooterViewsCount() == 0)
                    listView.addFooterView(loadingFooter);
                adapter.notifyDataSetChanged();
//                if (from != null) {
//                    new LoadQuery().execute(from, to);
//                } else {
//                    new LoadQuery().execute();
//                }
                prepareRequest();
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

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
                    Log.i("", "end reached");
                    page_no++;
                    Log.i("", "loading page_no " + page_no);
                    // new LoadQuery().execute();
                    prepareRequest();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (explistView.getVisibility() == View.VISIBLE) {
            closeDropdown();
        } else {
            cat_id = "";
            if (backflags[0].equalsIgnoreCase("AllSellers")) {

                startActivity(new Intent(SellersQuery.this, AllSellers.class));
                finish();

            } else if (backflags[0].equalsIgnoreCase("Home")) {

                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                finish();

            } else if (backflags[0].equalsIgnoreCase("SellerProfile")) {

                startActivity(new Intent(SellersQuery.this, SellerProfile.class).
                        putExtra("username", username)
                        .putExtra("company_id", companyid)
                        .putExtra("userid", userid)
                        .putExtra("user_id", user_id)
                        .putExtra("product_id", product_id)
                        .putExtra("title", title)
                        .putExtra("BackFlag", backflags[1]));
                finish();
            } else if (backflags[0].equalsIgnoreCase("SearchSellers")) {
                if (cd.isConnectingToInternet()) {
                    startActivity(new Intent(SellersQuery.this, SearchSellers.class).putExtra("search_string", search_string));
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(SellersQuery.this, getString(R.string.oops), getString(R.string.no_internet_conn));
                }

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
        adapter.notifyDataSetChanged();

    }

    public void onFilter(View v) {
        Log.d("date", from + "  " + to);
        startActivityForResult(new Intent(SellersQuery.this, DateActivity.class).putExtra("date_from", from).putExtra("date_to", to), FILTER_REQUEST);
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

                page_no = 1;
                if (listView.getFooterViewsCount() == 0)
                    listView.addFooterView(loadingFooter);
                adapter.notifyDataSetChanged();
                //new LoadQuery().execute(from, to);
                prepareRequest();

            } else {
                list.clear();

                from = null;
                to = null;

                page_no = 1;
                if (listView.getFooterViewsCount() == 0)
                    listView.addFooterView(loadingFooter);
                adapter.notifyDataSetChanged();
                //  new LoadQuery().execute();
                prepareRequest();
            }
        }
    }

    public void NetAsync() {
        if (cd.isConnectingToInternet()) {
            if (listView.getFooterViewsCount() == 0)
                listView.addFooterView(loadingFooter);
            adapter.notifyDataSetChanged();
            prepareRequest();
            // new LoadQuery().execute();
        } else {
            new AlertDialogManager().showAlertDialog(SellersQuery.this, getString(R.string.oops), getString(R.string.no_internet_conn));
        }
    }


    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(SellersQuery.this);
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(this);
    }

    private void iniStuff() {
        explistView = (ExpandableListView) findViewById(R.id.expandableMyCatalog);
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        ll = (LinearLayout) findViewById(R.id.obottombar);
        order_status = (TextView) findViewById(R.id.catalog_record_status);
        page_no = 1;
        isLoading = true;
        sort_by = "created_at";
        sort_order = "desc";
        sortingOrder = 0;
        sort_button = (LinearLayout) findViewById(R.id.osort);
        category_button = (LinearLayout) findViewById(R.id.ocategory_button);
        sort_button.setOnClickListener(this);
        category_button.setOnClickListener(this);
        filter = (ImageView) findViewById(R.id.odateFilterQuery);
        ll.setVisibility(View.VISIBLE);
        filter.setVisibility(View.VISIBLE);
        filter.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewCatalog);
        loadingFooter = LayoutInflater.from(SellersQuery.this).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        list = new ArrayList<>();
        adapter = new QueryAdapter(SellersQuery.this, list);
        listView.setAdapter(adapter);

        View view2 = View.inflate(SellersQuery.this, R.layout.category_header, null);
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

                startActivity(new Intent(SellersQuery.this, CartActivity.class));
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
                /*startActivity(new Intent(SellersQuery.this, SellerProfile.class).putExtra("username", username).putExtra("company_id", companyid).putExtra("userid", userid));
                finish();*/
                onBackPressed();

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
        } else {

            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.odateFilterQuery:
                startActivityForResult(new Intent(SellersQuery.this, DateActivity.class).putExtra("date_from", from).putExtra("date_to", to), FILTER_REQUEST);
                break;

            case R.id.ocategory_button:
                order_status.setVisibility(View.GONE);
                if (explistView.getVisibility() == View.VISIBLE) {
                    closeDropdown();
                } else {
                    if (explistView.getAdapter() != null) {
                        Log.i("SELLERSQUERY", "1");
                        if (explistView.getAdapter().isEmpty()) {
                            Log.i("SELLERSQUERY", "2");
                            loadCategory();
                        } else {
                            Log.i("SELLERSQUERY", "3");
                            openDropdown();
                        }
                    } else {
                        Log.i("SELLERSQUERY", "3");
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
                    new GetUserCategory().execute(helper.getDefaults("company_id", SellersQuery.this));
                } else {
                    loadSubCategory();
                }
                break;
            case R.id.itemRootTitle:
                cat_id = "";
                page_no = 1;
                list.clear();

                if (listView.getFooterViewsCount() == 0)
                    listView.addFooterView(loadingFooter);
                adapter.notifyDataSetChanged();
//                if (from != null) {
//                    new LoadQuery().execute(from, to);
//                } else {
//                    new LoadQuery().execute();
//                }
                prepareRequest();
                queryText.setText("All");
                closeDropdown();
                break;
            case R.id.osort:
                final PopupWindow popupWindow = new PopupWindow(SellersQuery.this);
                View view = LayoutInflater.from(SellersQuery.this).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.delete_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.sort_query_name).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_need_desc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_recent).setVisibility(View.VISIBLE);
                if (prev_sort == 1)
                    view.findViewById(R.id.action_recent_image).setVisibility(View.VISIBLE);
                else if (prev_sort == 2)
                    view.findViewById(R.id.action_need_image).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.action_query_image).setVisibility(View.VISIBLE);

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
                        preTaskCall("needed_date", "desc", 2);
                        popupWindow.dismiss();
                    }
                });

                view.findViewById(R.id.sort_query_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("product_name", "asc", 3);
                        popupWindow.dismiss();
                    }
                });


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
        sortingOrder = 1;
        page_no = 1;
        list.clear();

        if (listView.getFooterViewsCount() == 0)
            listView.addFooterView(loadingFooter);
        adapter.notifyDataSetChanged();
//        if (from != null) {
//            new LoadQuery().execute(from, to);
//        } else {
//            new LoadQuery().execute();
//        }
        prepareRequest();
    }

    private void openDropdown() {

        if (explistView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            explistView.startAnimation(anim);
            // mDropdownTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            // R.drawable.icn_dropdown_close, 0);
            explistView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            //getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
            //  getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
            // listView.setImageResource(R.drawable.collapse_1);
        }
    }

    private void closeDropdown() {
        // expend = false;
        if (explistView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            // ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            explistView.startAnimation(anim);
            explistView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
        }


    }

    private void loadCategory() {
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
        isSelect = true;

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

                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };
        Log.i("SELLERSQUERY", "obj.size" + obj.toString());
        adapter2 = new QueryCategoryAdapter(SellersQuery.this, obj, grpLst, childLst, grpExpLst);
        explistView.setAdapter(adapter2);
        //   Log.d("cat status", "Load");
        openDropdown();

    }

    private void loadSubCategory() {
        obj = new ObjectItems();

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(SellersQuery.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + CategoryTable.TABLE_CONTACTS +
                            " where " + CategoryTable.KEY_PARENT_ID + "=0  and " +
                            CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" +
                            MyCategoryTable.KEY_PARENT_ID + ") from " + MyCategoryTable.TABLE_CONTACTS + ")",
                    null);
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
        isSelect = false;
        Log.i("json", "Come Post ");
        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {

                return false;

            }
        };


        ExpandableListView.OnChildClickListener childLst = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView eListView, View view, int groupPosition,
                                        int childPosition, long id) {

                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter2 = new QueryCategoryAdapter(SellersQuery.this, obj, grpLst, childLst, grpExpLst);
        explistView.setAdapter(adapter2);
        openDropdown();
    }


    private class QueryAdapter extends BaseAdapter implements Filterable {

        ArrayList<wydr.sellers.gson.QueryModal> data, memberData;
        ListLoader imageLoader;
        String str;
        String imgUrl, queryText;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        private Activity activity;
        private LayoutInflater inflater = null;

        public QueryAdapter(Activity a, ArrayList<wydr.sellers.gson.QueryModal> d) {
            activity = a;
            data = d;
            memberData = d;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            imageLoader = new ListLoader(activity.getApplicationContext());
            //    imageLoader=new ImageLoader(activity.getApplicationContext());
        }

        public void swapItems(ArrayList<wydr.sellers.gson.QueryModal> items) {
            this.data = items;
            memberData = items;
            notifyDataSetChanged();
        }

        public int getCount() {
            if (data != null)
                return data.size();
            else return 0;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.quiry_layout, null);

            TextView text = (TextView) vi.findViewById(R.id.txtQueryText);
            final ImageView menu_btn = (ImageView) vi.findViewById(R.id.menu_btn);
            ImageView query_img = (ImageView) vi.findViewById(R.id.imageView2);
            TextView textpost = (TextView) vi.findViewById(R.id.txtPostedBy);
            LinearLayout lineatpost = (LinearLayout) vi.findViewById(R.id.linear_posted);

            final wydr.sellers.gson.QueryModal modal = data.get(position);

            final Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{modal.getId()}, null);

            cursor.moveToFirst();

            try {
                text.setText(Html.fromHtml("<b>" + modal.getTitle() + "</b>" + "<br>" + "Product Code : <b>" + modal.getCode() + "</b> Quantity : <b>" + modal.getQuantity() + "</b> Order Type : <b>" + modal.getType() + " </b> Location : <b>" + modal.getLocation() + "</b>" + " Needed By : <b>" + format2.format(format.parse(modal.getNeeded())) + "</b>"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            imageLoader.DisplayImage2(modal.getThumbnails().getUrl(), query_img, R.drawable.default_product);
            textpost.setText("POSTED BY: " + helper.ConvertCamel(modal.getUserName()) + ", " + modal.getCompany().toUpperCase());
            Log.i("SellerQuery", "user_id " + user_id);
            Log.i("SellerQuery" ,"current_user_id " +  current_user_id);
            if (user_id.equalsIgnoreCase(current_user_id)) {
                lineatpost.setVisibility(View.GONE);
            } else {
                lineatpost.setVisibility(View.VISIBLE);
            }

            menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupWindow popupWindow = new PopupWindow(activity);
                    View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);

                    view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                    if (modal.isFav() != null && modal.isFav().equalsIgnoreCase("1")) {
                        ((TextView) view.findViewById(R.id.action_like)).setText("Unlike");
                        ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_selected);
                    } else {
                        ((TextView) view.findViewById(R.id.action_like)).setText("Like");
                        ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_unselected);
                    }


                    view.findViewById(R.id.action_like).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cd.isConnectingToInternet()) {
                                if (data.get(position).isFav() != null && data.get(position).isFav().equalsIgnoreCase("query")) {
                                    new LikeQuery(data.get(position), position).execute("1");
                                } else {
                                    new LikeQuery(data.get(position), position).execute("0");
                                }
                            } else {
                                alertDialog.setTitle(getResources().getString(R.string.oops));
                                alertDialog.setMessage(getResources().getString(R.string.no_internet_conn));
                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alertDialog.show();
                            }
                            popupWindow.dismiss();
                        }
                    });
                    view.findViewById(R.id.action_chat).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (cd.isConnectingToInternet()) {
                                if (modal.getThumbnails().getUrl() != null) {
                                    imgUrl = modal.getThumbnails().getUrl();
                                } else {
                                    imgUrl = "";
                                }

                                queryText = "<b>" + modal.getTitle() + "</b>" + "<br>" + "Product Code : <b>" + modal.getCode() + "</b> Quantity : <b>" + modal.getQuantity() + "</b> Order Type : <b>" + modal.getType() + " </b> Location : <b>" + modal.getLocation() + "</b>" + " Needed By : <b>" + modal.getNeeded().substring(0, 11) + "</b>";

                                getPrimary(modal, queryText, imgUrl);


                            } else {
                                Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                            }
                            popupWindow.dismiss();

                        }
                    });
                    if (user_id.equalsIgnoreCase(current_user_id)) {

                        view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                        view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                    }
                    if (companyid.equalsIgnoreCase(helper.getDefaults("company_id", activity))) {
                        view.findViewById(R.id.like_wrap).setVisibility(View.GONE);

                    }
                    popupWindow.setContentView(view);
                    popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int xOffset = -(view.getMeasuredWidth() - (menu_btn.getWidth() / 2) + 10);
                    int yOffset = -(menu_btn.getHeight());
                    popupWindow.showAsDropDown(menu_btn, xOffset, yOffset);
                }
            });
            //   cursor.close();
            return vi;
        }


        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults(); // Holds the
                    // values
                    ArrayList<wydr.sellers.gson.QueryModal> filterlist = new ArrayList<>();

                    if (memberData == null) {
                        memberData = new ArrayList<>();

                    }
                    if (constraint != null && memberData != null && memberData.size() > 0) {

                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < memberData.size(); i++) {
                            String data = memberData.get(i).getTitle();
                            Log.d("data", data);
                            if (data.toLowerCase().contains(constraint.toString())) {
                                wydr.sellers.gson.QueryModal modal = new wydr.sellers.gson.QueryModal();


                                modal.setId(memberData.get(i).getId());
                                modal.setCode(memberData.get(i).getCode());
                                modal.setLocation(memberData.get(i).getLocation());
                                modal.setNeeded(memberData.get(i).getNeeded());
                                modal.setTitle(memberData.get(i).getTitle());
                                modal.setQuantity(memberData.get(i).getQuantity());
                                modal.setId(memberData.get(i).getId());
                                modal.setType(memberData.get(i).getType());
                                modal.setUserName(memberData.get(i).getUserName());
                                modal.setCompany(memberData.get(i).getCompany());
                                modal.setCompanyId(memberData.get(i).getCompanyId());
                                modal.setThumbnails(memberData.get(i).getThumbnails());
                                modal.setFav(memberData.get(i).isFav());
                                modal.setPostedBy(memberData.get(i).getPostedBy());
                                modal.setVendorId(memberData.get(i).getVendorId());


                                filterlist.add(modal);
                            }
                        }
                        results.values = filterlist;
                    }
                    return results;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    data = (ArrayList<wydr.sellers.gson.QueryModal>) results.values;
                    if (memberData.size() != 0)
                        notifyDataSetChanged();
                }
            }

                    ;

        }

        private void getPrimary(final QueryModal modal, final String queryText, final String imgUrl) {
            if (!activity.isFinishing()) {


                progress.show();
                RestClient.GitApiInterface service = RestClient.getClient();
                Call<JsonElement> call = service.getPrimary(modal.getCompanyId(), "1", helper.getB64Auth(activity), "application/json", "application/json");
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Response response) {
                        progress.dismiss();

                        if (response.isSuccess()) {
                            JsonElement element = (JsonElement) response.body();
                            JSONObject json = null;
                            if (element != null && element.isJsonObject()) {
                                try {
                                    json = new JSONObject(element.getAsJsonObject().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (json != null && json.has("users")) {
                                try {
                                    chat_user_id = json.getJSONObject("users").optString("user_id");
                                    Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);
                                    int iId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                                    cursor.moveToFirst();
                                    if (cursor.getCount() == 0) {
                                        getUserDetails(chat_user_id, queryText, imgUrl);
                                    } else {
                                        str = cursor.getString(iId);
                                        activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + str).putExtra("from", 102).putExtra("url", imgUrl).putExtra("query", queryText));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.user_not_found));
                            }

                        } else {
                            int statusCode = response.code();

                            if (statusCode == 401) {

                                final SessionManager sessionManager = new SessionManager(activity);
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.logoutUser();
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                            } else {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));
                            }

                        }


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progress.dismiss();
                        if (activity != null && !activity.isFinishing())
                            new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));

                    }
                });
            }


        }

        private void getUserDetails(String userId, final String queryText, final String imgUrl) {
            if (!activity.isFinishing()) {
                final Calendar c = Calendar.getInstance();
                flag = 0;


                progress.show();
                RestClient.GitApiInterface service = RestClient.getClient();
                Call<JsonElement> call = service.getUserDetails(userId, helper.getB64Auth(activity), "application/json", "application/json");
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Response response) {
                        progress.dismiss();

                        Log.d("re", "" + response.code());
                        // Log.d("JSON", " " + element.getAsJsonObject().toString());
                        if (response.isSuccess()) {
                            String userid = "";
                            JsonElement element = (JsonElement) response.body();


                            JSONObject json = null;
                            try {
                                json = new JSONObject(element.getAsJsonObject().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (json != null) {

                                if (json.has("user_id") && json.optString("is_openfire").equalsIgnoreCase("1")) {
                                    try {

                                        ContentValues cv = new ContentValues();
                                        if (json.has("phone"))
                                            cv.put(NetSchema.USER_PHONE, json.getString("phone"));
                                        if (json.has("company_name"))
                                            cv.put(NetSchema.USER_COMPANY, json.getString("company_name"));
                                        cv.put(NetSchema.USER_DISPLAY, " ");
                                        if (json.has("company_id"))
                                            cv.put(NetSchema.USER_COMPANY_ID, json.getString("company_id"));
                                        if (json.has("user_id"))
                                            cv.put(NetSchema.USER_ID, json.getString("user_id"));
                                        if (json.has("user_login")) {
                                            userid = json.getString("user_login") + "@" + AppUtil.SERVER_NAME;
                                            cv.put(NetSchema.USER_NET_ID, userid);
                                        }

                                        cv.put(NetSchema.USER_STATUS, "0");
                                        cv.put(NetSchema.USER_NAME, helper.ConvertCamel(json.getString("firstname")) + " " + helper.ConvertCamel(json.getString("lastname")));
                                        cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));


                                        activity.getContentResolver().insert(ChatProvider.NET_URI, cv);

                                        Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
                                        if (cursor.getCount() > 0)
                                            flag = 3;

                                        Log.e("count", cursor.getCount() + "--****");
                                        cursor.close();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {

                                    flag = 2;
                                }
                            } else {
                                flag = 1;
                            }
                            if (flag == 1) {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.server_error));

                            } else if (flag == 2) {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.user_not_exist));

                            } else {
                                activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + userid).putExtra("from", 102).putExtra("url", imgUrl).putExtra("query", queryText));
                            }

                        } else {
                            int statusCode = response.code();

                            if (statusCode == 401) {

                                final SessionManager sessionManager = new SessionManager(activity);
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.logoutUser();
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                            } else {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));
                            }

                        }


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progress.dismiss();
                        if (activity != null && !activity.isFinishing())
                            new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));

                    }
                });
            }


        }


        private class LikeQuery extends AsyncTask<String, String, JSONObject> {
            public String error = "";
            public int flag = 0, pos;
            public wydr.sellers.gson.QueryModal productModal;
            Boolean success = false;
            JSONObject table = new JSONObject();

            public LikeQuery(wydr.sellers.gson.QueryModal tag, int pos) {
                this.productModal = tag;
                this.pos = pos;

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!isFinishing())
                    progress.show();


            }

            @Override
            protected JSONObject doInBackground(String... args) {

                UserFunctions userFunction = new UserFunctions();
                JSONObject json = null;
                try {

                    table.put("user_id", current_user_id);
                    table.put("object_id", productModal.getId());
                    table.put("object_type", "query");
                    if (args[0].equalsIgnoreCase("1"))
                        table.put("unlike", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = userFunction.LikeRequest("POST", table, "Query", SellersQuery.this);
                if (json != null) {
                    try {
                        if (json.has("message")) {
                            if (json.get("message").toString().equalsIgnoreCase("_your favourite item added successfully"))
                                success = true;
                        } else if (json.has("error")) {
                            if (json.get("error").toString().equalsIgnoreCase("NO CONTENT"))
                                success = true;
                            else
                                flag = 2;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    flag = 1;

                }


                return json;
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                if (!isFinishing()) {
                    progress.dismiss();
                    if (flag == 1) {

                        alertDialog.setTitle(getResources().getString(R.string.sorry));
                        alertDialog.setMessage(getResources().getString(R.string.server_error));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        alertDialog.show();
                    } else if (flag == 2) {

                        alertDialog.setTitle(getResources().getString(R.string.error));
                        alertDialog.setMessage(getResources().getString(R.string.page_not_found));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        alertDialog.show();
                    }

                    if (flag == 0) {
                        if (success) {
                            adapter.swapItems(list);
                            for (int i = 0; i < data.size(); i++) {
                                if (productModal.getId().equalsIgnoreCase(data.get(i).getId())) {
                                    // Log.i("click", "id " + data.get(i).getQuery_id() + "/" + productModal.getQuery_id());
                                    Log.i("click", "id " + data.get(i).getTitle() + "/" + productModal.getTitle());
                                    if (data.get(i).isFav() != null && data.get(i).isFav().equalsIgnoreCase("query")) {
                                        data.get(i).setFav("0");
                                        Toast.makeText(SellersQuery.this, getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                                    } else {
                                        data.get(i).setFav("1");
                                        Toast.makeText(SellersQuery.this, getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                                    }
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        }
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
                progress.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params, SellersQuery.this);
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
                                cd1.setHas_child(true);
                            else
                                cd1.setHas_child(false);
                            //  Log.i("ADdPRODICT", "DATA -- > " + cd1.getId() + "/" + cd1.getName() + "/" + cd1.getHas_child() + "/" + cd1.getParentid() + "/" + cd1.getProduct_count());
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
                                        cd2.setHas_child(true);
                                    else
                                        cd2.setHas_child(false);
                                    // Log.i("ADdPRODICT", "DATA -- > " + cd2.getId() + "/" + cd2.getName() + "/" + cd2.getHas_child() + "/" + cd2.getParentid() + "/" + cd2.getProduct_count());
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
                                                cd3.setHas_child(true);
                                            else
                                                cd3.setHas_child(false);
                                            // Log.i("ADdPRODICT", "DATA -- > " + cd3.getId() + "/" + cd3.getName() + "/" + cd3.getHas_child() + "/" + cd3.getParentid() + "/" + cd3.getProduct_count());
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
                                                        cd4.setHas_child(true);
                                                    else
                                                        cd4.setHas_child(false);
                                                    Log.i("SELLERSQUERY", "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" + cd4.getProduct_count());
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
            if ((SellersQuery.this != null) && !isFinishing()) {
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
                            values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                            values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                            values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                            values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));
                            // Log.i("ADdPRODICT", "DATA -- > " + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getHas_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                            getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);
                            //     Log.i("Helper", "uri at mycatalog " + uri.toString());

                        }
                    }

                    loadSubCategory();
                }
            }
        }
    }

    private void prepareRequest() {
        isLoading = true;
        if (page_no != 0) {
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<QueryHolder> call = service.getSellerQuery(current_user_id, page_no, sellerid, sort_order, sort_by, cat_id, from, to, helper.getB64Auth(SellersQuery.this), "application/json", "application/json");
            call.enqueue(new Callback<QueryHolder>() {
                @Override
                public void onResponse(Response response) {

                    isLoading = false;
                    listView.removeFooterView(loadingFooter);

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
                        int statusCode = response.code();
                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(SellersQuery.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (SellersQuery.this != null && !SellersQuery.this.isFinishing()) {
                                new AlertDialogManager().showAlertDialog(SellersQuery.this,
                                        getString(R.string.error),
                                        getString(R.string.server_error));
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    if (SellersQuery.this != null && !SellersQuery.this.isFinishing()) {
                        if (listView.getFooterViewsCount() > 0)
                            listView.removeFooterView(loadingFooter);
                        if (list.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        page_no = 0;
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(SellersQuery.this,
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
        mTracker.setScreenName("SellersQuery");
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

