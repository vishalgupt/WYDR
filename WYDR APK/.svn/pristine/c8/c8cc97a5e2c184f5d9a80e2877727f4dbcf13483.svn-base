package wydr.sellers.activities;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.adapter.ParentRootAdapter;
import wydr.sellers.adapter.RootAdapterExp;
import wydr.sellers.gson.MakeOrder;
import wydr.sellers.gson.ProductList;
import wydr.sellers.gson.ProductModal;
import wydr.sellers.gson.Products;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.ProductLoader;
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

public class MarketPlaceProductsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView sliderMenu;
    Helper helper = new Helper();
    private TextView toolbartext;
    // remove frag
    GridView productsGrid;
    JSONObject jsonObject;
    // LinearLayout ll;
    public List<ProductModal> productModals;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    final Calendar c = Calendar.getInstance();
    private ProgressDialog progress;
    String category_id;
    ExpandableListView listView;
    String name, sort_order, sort_by, filter_string;
    ImageView filter;
    ObjectItems obj;
    TextView header;
    RootAdapterExp adapter;
    JSONObject jsonArray = null;
    public static final int FILTER_REQUEST = 101;
    android.app.AlertDialog.Builder alertDialog;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    JSONObject jsonObj;
    private ConnectionDetector cd;
    JSONParser parser;
    Button all, my;
    LinearLayout sort_button, category_button;
    Animation animation;
    ProductLoader loader;
    public GridAdapter gridAdapter;
    String tokan;
    private int currentFirstVisibleItem;
    int prev_sort = 4;
    TextView txtSelected;
    private boolean isSelect = true;
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    MyDatabaseHelper databaseHelper;
    ArrayList<String>compIDlist = new ArrayList<>();
    private String cat_id = "";
    String user_id, company_id, chat_user_id;
    private static final int REQUEST_LIKE = 100;
    int flag = 0;
    SessionManager session;
    String screenVisited = "";
    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        startTime = System.currentTimeMillis();
        tokan = helper.getB64Auth(MarketPlaceProductsActivity.this);
        setContentView(R.layout.marketplace_products);
        cd = new ConnectionDetector(this);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        databaseHelper = new MyDatabaseHelper(getApplicationContext());
        compIDlist = new ArrayList<String>(Arrays.asList(databaseHelper.companyids().split(",")));
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.toolbarimg).setVisibility(View.GONE);
        toolbartext = (TextView) findViewById(R.id.toolbartext);
        Intent intent = getIntent();
        Uri data = intent.getData();

        if(data != null)
        {
            Scanner in = new Scanner(data.toString()).useDelimiter("[^0-9]+");
            String integer = String.valueOf(in.nextInt());
            cat_id = integer;
            name = "WYDR";
        }
        else
        {
            cat_id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            screenVisited += getIntent().getStringExtra("screenVisited");
        }

        setTitle("");
        sliderMenu = (ImageView) findViewById(R.id.btnMenu);
        sliderMenu.setVisibility(View.GONE);
        user_id = helper.getDefaults("user_id", MarketPlaceProductsActivity.this);
        company_id = helper.getDefaults("company_id", MarketPlaceProductsActivity.this);
        iniStuff();
        productsGrid.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (isLoading || page_no == 0)
                {
                    return;
                }

                else
                {
                    if ((firstVisibleItem + visibleItemCount) == totalItemCount)
                    {
                        page_no++;
                        prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
                    }
                }
            }
        });
    }


    private void iniStuff()
    {
        filter = (ImageView) findViewById(R.id.mpp_filter);
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        progressStuff();
        sort_by = "timestamp";
        sort_order = "desc";
        filter_string = "";
        header = (TextView) findViewById(R.id.toolbartext);
        header.setOnClickListener(this);
        listView = (ExpandableListView) findViewById(R.id.mpp_expandableCategory2);
        txtSelected = (TextView) findViewById(R.id.mpp_txtMarketCategory);
        View view = View.inflate(MarketPlaceProductsActivity.this, R.layout.category_header, null);
        all = (Button) view.findViewById(R.id.allCategory);
        my = (Button) view.findViewById(R.id.myCategory);
        LinearLayout allTab = (LinearLayout) view.findViewById(R.id.all_header);
        TextView div = (TextView) view.findViewById(R.id.divRoot);
        allTab.setVisibility(View.GONE);
        div.setVisibility(View.GONE);
        sort_button = (LinearLayout) findViewById(R.id.mpp_sort);
        category_button = (LinearLayout) findViewById(R.id.mpp_category_button);
        all.setOnClickListener(this);
        my.setOnClickListener(this);
        sort_button.setOnClickListener(this);
        category_button.setOnClickListener(this);
        loader = new ProductLoader(getApplicationContext());
        listView.addHeaderView(view, null, false);
        isLoading = true;
        page_no = 1;
        productsGrid = (GridView) findViewById(R.id.mpp_gridView2);
        productModals = new ArrayList<>();
        gridAdapter = new GridAdapter(productModals);
        productsGrid.setAdapter(gridAdapter);
        if (name.contains("("))
        {
            header.setText(name.substring(0, name.indexOf("(")));
        }

        else
        {
            header.setText(name);
        }
        loadingFooter = LayoutInflater.from(MarketPlaceProductsActivity.this).inflate(R.layout.pagination_loading, null);
        //  productsGrid.addFooterView(loadingFooter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (isSelect)
                {
                    TextView tv = (TextView) v.findViewById(R.id.itemRootTitle);
                    if (parent.isGroupExpanded(groupPosition))
                    {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    }
                    else
                    {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }
                }

                else
                {
                    TextView tv;
                    tv = (TextView) v.findViewById(R.id.itemRootTitle);
                    if (tv == null)
                    {
                        tv = (TextView) v.findViewById(R.id.itemParentTitle);
                    }
                    if (parent.isGroupExpanded(groupPosition))
                    {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    }
                    else
                    {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }

                    ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                    if (items.children.size() == 0)
                    {
                        header.setText(items.title.substring(0, items.title.indexOf("(")));
                        txtSelected.setText(items.title);
                        closeDropdown();
                        cat_id = items.id;
                        page_no = 1;
                        productModals.clear();
                        gridAdapter.swapItems(productModals);
                        filter_string = "";
                        jsonArray = null;
                        FilterActivity.isRange = false;
                        FilterActivity.min = Long.valueOf(0);
                        FilterActivity.max = Long.valueOf(0);
                        prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);

//
                        return false;
                    } else {
                        Log.i("ALLSELERS - bootm", "4");
                        if (parent.isGroupExpanded(groupPosition)) {
                            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                        } else {
                            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                        }
                    }
                    listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
                    {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                            header.setText(items.title.substring(0, items.title.indexOf("(")));
                            txtSelected.setText(items.title);
                            closeDropdown();
                            cat_id = items.id;
                            page_no = 1;
                            productModals.clear();
                            gridAdapter.swapItems(productModals);
                            filter_string = "";
                            jsonArray = null;
                            FilterActivity.isRange = false;
                            FilterActivity.min = Long.valueOf(0);
                            FilterActivity.max = Long.valueOf(0);

                            prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
                            // new GetProducts().execute();
                            return false;
                        }
                    });
                }
                return false;
            }
        });


        // new GetProducts().execute();
        prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
        productsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Products", "Move", "ProductDetailsActivity");
                /*******************************ISTIAQUE***************************************/

                String product_id = productModals.get(i).getId();
                Intent intent = new Intent(MarketPlaceProductsActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product_id", product_id);
                intent.putExtra("name", header.getText().toString().trim());
                intent.putExtra("screenVisited", screenVisited + header.getText().toString().trim() + "/");
                startActivityForResult(intent, REQUEST_LIKE);

                //  startActivity(intent);

            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MarketPlaceProductsActivity.this,FilterActivity));
                if (jsonArray != null) {
                    Log.i("JSON FILTER ARRAY", jsonArray.toString());
                    startActivityForResult(new Intent(MarketPlaceProductsActivity.this, FilterActivity.class).putExtra("filter", jsonArray.toString()).putExtra("cat_id", cat_id).putExtra("request_code", FILTER_REQUEST), FILTER_REQUEST);
                } else {
                    new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getResources().getString(R.string.oops), getString(R.string.no_filter_available));
                }
            }
        });
    }


    private void preTaskCall(String sort, String order, int prev) {
        prev_sort = prev;
        sort_by = sort;
        sort_order = order;
        page_no = 1;
        productModals.clear();
        gridAdapter.swapItems(productModals);
        Log.i("MARKET", productModals.size() + " before");
        //    new GetProducts().execute();
        prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        //  Log.i("", "toggle" + id);
        switch (id) {


            case android.R.id.home:
//                productModals.clear();
//                gridAdapter.swapItems(productModals);
//                FilterActivity.min = 0;
//                FilterActivity.max = 0;
//                FilterActivity.isRange = false;
//                filter_string = "";
//                jsonArray = null;
//                // adapter.notifyDataSetChanged();
                finish();
                //onBackPressed();
                break;

            /*********************** ISTIAQUE CODE STARTS ********************************/
            case R.id.search:
                startActivity(new Intent(MarketPlaceProductsActivity.this, SearchActivity.class));
                Log.e("item", "search");
                break;
            /*********************** ISTIAQUE CODE ENDS ********************************/

        }
        return super.onOptionsItemSelected(item);
    }

    private void progressStuff() {
        // TODO Auto-generated method stub
         session = new SessionManager(getApplicationContext());
        /*cd = new ConnectionDetector(this);*/
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new android.app.AlertDialog.Builder(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mpp_category_button:

                if (listView.getVisibility() == View.VISIBLE) {
                    closeDropdown();
                    // listView.setVisibility(View.GONE);
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
                    // listView.setVisibility(View.VISIBLE);
                    //openDropdown();
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
                    new GetUserCategory().execute(helper.getDefaults("company_id", MarketPlaceProductsActivity.this));
                } else {
                    loadSubCategory();
                }
                break;

            case R.id.mpp_sort:
                final PopupWindow popupWindow = new PopupWindow(MarketPlaceProductsActivity.this);
                View view = LayoutInflater.from(MarketPlaceProductsActivity.this).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.delete_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.sort_asc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_desc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_name).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_recent).setVisibility(View.VISIBLE);
                if (prev_sort == 1)
                    view.findViewById(R.id.action_asc_image).setVisibility(View.VISIBLE);
                else if (prev_sort == 2)
                    view.findViewById(R.id.action_desc_image).setVisibility(View.VISIBLE);
                else if (prev_sort == 3)
                    view.findViewById(R.id.action_name_image).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.action_recent_image).setVisibility(View.VISIBLE);

                view.findViewById(R.id.sort_asc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("price", "asc", 1);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_desc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("price", "desc", 2);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("product", "asc", 3);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preTaskCall("timestamp", "desc", 4);
                        popupWindow.dismiss();


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


    @Override
    public void onBackPressed() {
        if (listView.getVisibility() == View.VISIBLE) {
            closeDropdown();
        } else {
            productModals.clear();
            gridAdapter.swapItems(productModals);
            filter_string = "";
            jsonArray = null;
            FilterActivity.isRange = false;
            FilterActivity.min = Long.valueOf(0);
            FilterActivity.max = Long.valueOf(0);
            finish();
            super.onBackPressed();
        }
        // super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("MARKETPLAC", "111");
        productModals.clear();
        gridAdapter.notifyDataSetChanged();
        progress.dismiss();


    }


    private class LikeQuery extends AsyncTask<String, String, JSONObject> {

        public String error = "", arguments;
        public int flag = 0, pos;
        JSONObject table = new JSONObject();
        public ProductModal productModal;
        Boolean success = false;

        public LikeQuery(ProductModal tag, int pos) {
            this.productModal = tag;
            this.pos = pos;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isFinishing())
                progress.show();
        }


        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            try {
                arguments = args[0];
                Log.i("Deepesh", " productModal.getId() " + productModal.getId());
                if (args[0] == "1")
                    table.put("unlike", "1");
                table.put("user_id", user_id);
                table.put("object_id", productModal.getId());
                table.put("object_type", "product");
                if (args[0] == "1")
                    table.put("unlike", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = userFunction.LikeRequest("POST", table, "Query", MarketPlaceProductsActivity.this);
            if (json != null) {
                Log.e("JSON--", json.toString());
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
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();

                } else if (flag == 2) {
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    try {
                        alertDialog.setMessage(json.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();

                } else {
                    //  Log.e("SEET-1", productModals.get(pos).getIsFav() + "");
                    if (success) {
                        if (arguments.equalsIgnoreCase("1")) {
                            Toast.makeText(MarketPlaceProductsActivity.this, getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                            productModals.get(pos).setFav("0");
                        } else {
                            Toast.makeText(MarketPlaceProductsActivity.this, getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                            productModals.get(pos).setFav("1");
                        }
                        gridAdapter.notifyDataSetChanged();
                    }


                }
            }

        }
    }

    private void openDropdown() {

        if (listView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            productsGrid.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            // listView.setImageResource(R.drawable.collapse_1);
        }
    }
    //  }

    private void closeDropdown() {
        // expend = false;
        if (listView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            // ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            listView.setVisibility(View.GONE);
            productsGrid.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            page_no = 0;
            return;
        }
        switch (requestCode) {
            case FILTER_REQUEST: {
//                if(getIntent().getStringExtra("name").contains("("))
//                    header.setText(getIntent().getStringExtra("name").substring(0, getIntent().getStringExtra("name").indexOf("(")));
//                else
//                    header.setText(getIntent().getStringExtra("name"));
                filter_string = data.getStringExtra("result");
                Log.i("result filter", "filter_string " + filter_string);
                page_no = 1;
                productModals.clear();
                gridAdapter.swapItems(productModals);
                prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
                break;
            }
            case REQUEST_LIKE:
            {
                page_no = 1;
                productModals.clear();
                gridAdapter.swapItems(productModals);
                Log.i("MARKET", productModals.size() + " before");
                prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
            }

        }
    }


    private void loadCategory() {

        obj = new ObjectItems();

        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
        int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
        int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
        int iCount = cursor.getColumnIndexOrThrow(CategoryTable.KEY_PRODUCT_COUNT);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            ObjectItems root = new ObjectItems();
            root.id = cursor.getString(iId);
            root.children = new ArrayList<ObjectItems>();
            Cursor cursorParent = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null, CategoryTable.KEY_PARENT_ID + "=?", new String[]{cursor.getString(iId)}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
            int RootCount = 0;
            root.level = 0;
            int iIdParent = cursorParent.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
            int iNameParent = cursorParent.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
            int iParentCount = cursorParent.getColumnIndexOrThrow(CategoryTable.KEY_PRODUCT_COUNT);
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
                        Log.i("SUB", "child count for grand" + cursorGrandChild.getString(iNameGrandChild) + "is " + cursorGrandChild.getInt(iNameGrandCount));
                    }

                    itemsChildCount = cursorChild.getInt(iNameGrandCount);
                    child.title = cursorChild.getString(iNameChild) + " (" + itemsChildCount + ")";
                    cursorGrandChild.close();
                    Log.i("SUB", "child count for child " + cursorChild.getString(iNameChild) + "is " + itemsChildCount);
                    parent.children.add(child);
                    ParentCount += itemsChildCount;


                }
                if (cursorChild.getCount() == 0) {
                    ParentCount = cursorParent.getInt(iNameCount);

                }
                RootCount += ParentCount;
                cursorChild.close();
                parent.title = cursorParent.getString(iNameParent) + " (" + ParentCount + ")";
                Log.i("SUB", "child count for parent " + cursorParent.getString(iNameParent) + "is " + ParentCount);
                Log.i("SUB", "child count for ROOTCOUNT " + RootCount);
                root.children.add(parent);
            }

            cursorParent.close();

            root.title = cursor.getString(iName) + " (" + RootCount + ")";
            Log.i("SUB", "child count for root " + cursor.getString(iName) + "is " + RootCount);
            obj.children.add(root);

        }
        cursor.close();
        isSelect = true;
        Log.d("json", "Come Post ");
        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {
                Log.i("Item Clicked ", "OnGroupClickListener");

                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getGroup(groupPosition);
                if (items.children.size() == 0) {
                    header.setText(items.title.substring(0, items.title.indexOf("(")));
                    closeDropdown();
                    cat_id = items.id;
                    page_no = 1;
                    productModals.clear();
                    gridAdapter.swapItems(productModals);
                    filter_string = "";
                    //jsonArray = null;
                    FilterActivity.isRange = false;
                    FilterActivity.min = Long.valueOf(0);
                    FilterActivity.max = Long.valueOf(0);
                    prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
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
                //Adapter adapter=eListView.getExpandableListAdapter().getChild();
                Log.i("Item Clicked ", "OnChildClickListener");
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getChild(groupPosition, childPosition);
                header.setText(items.title.substring(0, items.title.indexOf("(")));
                closeDropdown();
                cat_id = items.id;
                page_no = 1;
                productModals.clear();
                gridAdapter.swapItems(productModals);
                filter_string = "";
                jsonArray = null;
                FilterActivity.isRange = false;
                FilterActivity.min = Long.valueOf(0);
                FilterActivity.max = Long.valueOf(0);
                prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter = new RootAdapterExp(MarketPlaceProductsActivity.this, obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter);
        //   Log.d("cat status", "Load");
        openDropdown();
    }


    private void loadSubCategory() {
        obj = new ObjectItems();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(MarketPlaceProductsActivity.this);
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
        isSelect = false;

        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getGroup(groupPosition);
                if (items.children.size() == 0) {

                    header.setText(items.title.substring(0, items.title.indexOf("(")));
                    closeDropdown();
                    cat_id = items.id;
                    page_no = 1;
                    productModals.clear();
                    gridAdapter.swapItems(productModals);
                    filter_string = "";
                    //jsonArray = null;
                    FilterActivity.isRange = false;
                    FilterActivity.min = Long.valueOf(0);
                    FilterActivity.max = Long.valueOf(0);
                    prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
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

                return false;
            }
        };


        ExpandableListView.OnChildClickListener childLst = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView eListView, View view, int groupPosition,
                                        int childPosition, long id) {
                //Adapter adapter=eListView.getExpandableListAdapter().getChild();
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getChild(groupPosition, childPosition);
                header.setText(items.title.substring(0, items.title.indexOf("(")));
                closeDropdown();
                cat_id = items.id;
                page_no = 1;
                productModals.clear();
                gridAdapter.swapItems(productModals);
                filter_string = "";
                //jsonArray = null;vdsv

                FilterActivity.isRange = false;
                FilterActivity.min = Long.valueOf(0);
                FilterActivity.max = Long.valueOf(0);
                prepareRequest(cat_id, page_no, sort_by, sort_order, filter_string);
                return false;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        ParentRootAdapter adapter = new ParentRootAdapter(MarketPlaceProductsActivity.this, obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter);
        openDropdown();


    }

    private class GetUserCategory extends AsyncTask<String, String, JSONObject>
    {
        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        int flag = 0;


        @Override
        protected void onPreExecute()
        {
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
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params, MarketPlaceProductsActivity.this);
            Log.i("TAG", " insdie GetUserCategories json " + json.toString());
            try {
                if (json != null) {
                    //    Log.i(TAG, " insdie GetUserCategories json " + json.toString());
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
            if ((MarketPlaceProductsActivity.this != null) && !isFinishing())
            {
                progress.dismiss();
                if (flag == 1)
                {
                    loadSubCategory();
                }

                else
                {
                    if (catdata != null)
                    {
                        getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
                        // Log.i("AddProdct", "DELETED" + a);
                        for (int s = 0; s < catdata.size(); s++)
                        {
                            ContentValues values = new ContentValues();
                            values.put(MyCategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                            values.put(MyCategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                            values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getGot_child());
                            values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                            values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                            values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));
                            //      Log.i("SUBCATEOGRY", "DATA -- > final" + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getGot_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
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

    private void prepareOrder(String user_id, HashMap<String, Products> map) {
        progress.show();
        MakeOrder create = new MakeOrder(map, user_id, AppUtil.PAYMENT_COD,"1");
        Gson gson = new Gson();
        Log.d("there", gson.toJson(create));
        RestClient.GitApiInterface service = RestClient.getClient();

        Call<String> call = service.bookMyOrders(create, helper.getB64Auth(MarketPlaceProductsActivity.this), "application/json", "application/json");
        call.enqueue(new Callback<String>() {
                         @Override
                         public void onResponse(Response response) {
                             progress.dismiss();
                             //      JsonElement element= (JsonElement) response.body();
                             Log.d("Herer", "" + response.raw());
                             if (response.isSuccess()) {
                                 if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing()) {
                                     new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this,
                                             getResources().getString(R.string.success),
                                             getResources().getString(R.string.order_plced_success));
                                 }
                             } else {
                                 int statusCode = response.code();
                                 //}
                                 if (statusCode == 401) {
                                     final SessionManager sessionManager = new SessionManager(MarketPlaceProductsActivity.this);
                                     Handler mainHandler = new Handler(Looper.getMainLooper());

                                     Runnable myRunnable = new Runnable() {
                                         @Override
                                         public void run() {
                                             sessionManager.logoutUser();
                                         } // This is your code
                                     };
                                     mainHandler.post(myRunnable);
                                 } else {
                                     if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing()) {
                                         new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this,
                                                 getString(R.string.error),
                                                 getString(R.string.server_error));
                                     }
                                 }

                             }
                             // }
                         }

                         @Override
                         public void onFailure(Throwable t) {

                             progress.dismiss();
                             if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing()) {
                                 new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.error), getString(R.string.server_error));
                             }

                         }
                     }

        );

    }

    class GridAdapter extends BaseAdapter
    {
        List<ProductModal> products;
        String mrp, sp;

        public GridAdapter(List<ProductModal> products) {
            this.products = products;
        }

        public void swapItems(List<ProductModal> items)
        {
            this.products = items;
            gridAdapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = LayoutInflater.from(MarketPlaceProductsActivity.this).inflate(R.layout.marketplace_grid_item, null);
            }
            final ViewHolder holder = new ViewHolder(view);
            holder.quantity.setText("MOQ : " + products.get(i).getMinQty());
            mrp = String.valueOf((int) Double.parseDouble(products.get(i).getMrp()));
            sp = String.valueOf((int) Double.parseDouble(products.get(i).getSp()));
            Log.i("SP--", sp);
            if (mrp.length() > 10) {
                mrp = mrp.substring(0, 7);

                if (mrp.contains(".00") || mrp.contains(".0"))
                {
                    holder.mrp.setText("\u20B9" + mrp + "/pc");
                }

                else
                {
                    holder.mrp.setText("\u20B9" + mrp + "../pc");
                }

            }

            else
            {
                holder.mrp.setText("\u20B9" + mrp + "/pc");
            }

            if (sp.length() > 10)
            {
                sp = sp.substring(0, 7);
                if (sp.contains(".00") || sp.contains(".0")) {
                    holder.maxSp.setText("\u20B9" + sp + "/pc");
                } else {
                    holder.maxSp.setText("\u20B9" + sp + "../pc");
                }
            }

            else {
                holder.maxSp.setText("\u20B9" + sp + "/pc");
            }

            if (products.get(i).getChatAboutProduct() != null && products.get(i).getChatAboutProduct().equalsIgnoreCase("1")) {
                holder.maxSp.setVisibility(View.INVISIBLE);
                holder.mrp.setVisibility(View.INVISIBLE);
            }

            else
            {
                holder.maxSp.setVisibility(View.VISIBLE);
                holder.mrp.setVisibility(View.VISIBLE);
            }
            holder.productCode.setText(products.get(i).getCode());
            holder.productName.setText(products.get(i).getName());

            holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (products.get(i).getThumbnails() != null) {
                // Log.i("SP-", products.get(i).getThumbnails().getUrl()+"");
                loader.DisplayImage(products.get(i).getThumbnails().getUrl(), holder.productImage, R.drawable.default_product);
            } else {
                //Log.i("SP-", "****");
                holder.productImage.setImageResource(R.drawable.default_product);
            }
            holder.menu_btn.setTag(products.get(i));
            if (company_id.equalsIgnoreCase(products.get(i).getCompanyId())) {
                holder.fav_btn.setVisibility(View.GONE);
            } else {

                if (products.get(i).getUser().size() == 0) {
                    holder.fav_btn.setVisibility(View.GONE);
                } else {
                    holder.fav_btn.setVisibility(View.VISIBLE);
                }

            }


            holder.menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupWindow popupWindow = new PopupWindow(MarketPlaceProductsActivity.this);
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.query_item_menu, null);
                    view.findViewById(R.id.book_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.chat_wrap).setVisibility(View.VISIBLE);
                    if (company_id.equalsIgnoreCase(products.get(i).getCompanyId())) {
                        view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                        view.findViewById(R.id.book_wrap).setVisibility(View.GONE);
                    }
                    if (compIDlist.contains(products.get(i).getCompanyId())) {
                        view.findViewById(R.id.book_wrap).setVisibility(View.VISIBLE);
                    }

                    //UserDetail user = products.get(i).getUser().get(0).getId();
                    if (products.get(i).getUser().size() > 0 && company_id.equalsIgnoreCase(products.get(i).getUser().get(0).getId()))
                    {
                        view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                        view.findViewById(R.id.book_wrap).setVisibility(View.GONE);
                    }
                    if (products.get(i).getUser().get(0).getId() == null) {
                        view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                        view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                        view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                    }


                    if (Long.parseLong(products.get(i).getQty()) == 0 || Long.parseLong(products.get(i).getQty()) < Long.parseLong(products.get(i).getMinQty())) {
                        view.findViewById(R.id.book_wrap).setVisibility(View.GONE);
                    }


                    view.findViewById(R.id.chat_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cd.isConnectingToInternet()) {
                                ProductModal productModal = (ProductModal) holder.menu_btn.getTag();
                                getPrimary(productModal);
//                                new PrimaryUser(productModal).execute();
                            }

                            else
                            {
                                Toast.makeText(MarketPlaceProductsActivity.this, getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                            }
                            popupWindow.dismiss();

                        }
                    });
                    view.findViewById(R.id.share_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new Intent(MarketPlaceProductsActivity.this, ShareCatalogWith.class);
                            shareIntent.putExtra("product_id", ((ProductModal) holder.menu_btn.getTag()).getId());
                            startActivity(shareIntent);
                            popupWindow.dismiss();
                        }
                    });
                    view.findViewById(R.id.book_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ProductModal song2 = (ProductModal) holder.menu_btn.getTag();
                            final Dialog d = new Dialog(MarketPlaceProductsActivity.this);
                            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            View view = LayoutInflater.from(MarketPlaceProductsActivity.this).inflate(R.layout.book_order_dialog, null);
                            d.setContentView(view);
                            final EditText np = (EditText) view.findViewById(R.id.numberPicker1);
                            final TextView moqcheck = (TextView) view.findViewById(R.id.moqalert);
                            moqcheck.setText(getString(R.string.atleast_moq) + song2.getMinQty());
                            final Button book = (Button) view.findViewById(R.id.book);
                            book.setEnabled(false);
                            book.setTextColor(getResources().getColor(R.color.book_dis));
                            np.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (np.getText().toString().length() > 0) {
                                        Double a = Double.parseDouble(np.getText().toString());
                                        if (a >= Double.parseDouble(song2.getMinQty().toString())) {
                                            moqcheck.setVisibility(View.INVISIBLE);
                                            book.setEnabled(true);
                                            book.setTextColor(getResources().getColor(R.color.white));
                                            np.setBackgroundResource(R.drawable.code_bar);
                                        } else {
                                            moqcheck.setVisibility(View.VISIBLE);
                                            book.setEnabled(false);
                                            book.setTextColor(getResources().getColor(R.color.book_dis));
                                            np.setBackgroundResource(R.drawable.error_bar);
                                        }
                                    } else {
                                        moqcheck.setVisibility(View.VISIBLE);
                                        book.setEnabled(false);
                                        book.setTextColor(getResources().getColor(R.color.book_dis));
                                    }
                                }
                            });


                            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
                                }
                            });
                            view.findViewById(R.id.book).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ProductModal song2 = (ProductModal) holder.menu_btn.getTag();
//
                                    if (Long.parseLong(song2.getQty()) < Long.parseLong(song2.getMinQty())) {
                                        //   np.setError(getResources().getString(R.string.out_of_stock));

                                        new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.oops), getString(R.string.out_of_stock));
                                    } else if (Long.parseLong(song2.getQty()) < Long.parseLong(np.getText().toString())) {
                                        //np.setError(getResources().getString(R.string.requested_not_available));

                                        new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.sorry), getString(R.string.only) + " " + Long.parseLong(song2.getQty()) + " " + getString(R.string.available));
                                    } else if (Long.parseLong(song2.getQty()) >= Long.parseLong(np.getText().toString())) {

                                        HashMap<String, Products> map = new HashMap<>();
                                        JsonObject jsonObject = new JsonObject();
                                        map.put("1", new Products(song2.getId(), np.getText().toString(),jsonObject,""));
                                        prepareOrder(user_id, map);
                                    }

                                    d.dismiss();
                                }
                            });
                            d.show();
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.setContentView(view);
                    popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int xOffset = -(view.getMeasuredWidth() - (holder.menu_btn.getWidth() / 2) + 10);
                    int yOffset = -(view.getMeasuredHeight() + holder.menu_btn.getHeight());
                    popupWindow.showAsDropDown(holder.menu_btn, xOffset, yOffset);
                }
            });

            if (products.get(i).isFav().equalsIgnoreCase("1")) {
                holder.fav_btn.setImageResource(R.drawable.like_selected);
            } else {
                holder.fav_btn.setImageResource(R.drawable.like_unselected);
            }
            holder.fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Log.e("TOUCH--", "1");
                    if (cd.isConnectingToInternet()) {//ISTIAQUE
                        if (products.get(i).isFav().equalsIgnoreCase("1"))
                            new LikeQuery(products.get(i), i).execute("1");
                        else
                            new LikeQuery(products.get(i), i).execute("0");
                    } else {
                        new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                    // holder.fav_btn.setImageResource(R.drawable.like_selected);
                }
            });

                        if (products.get(i).getChatAboutProduct() != null && products.get(i).getChatAboutProduct().equals("1")) {
                //holder.chat_about.setVisibility(View.VISIBLE);
                holder.mrp.setVisibility(View.INVISIBLE);
                holder.maxSp.setVisibility(View.INVISIBLE);

            } else {
                holder.chat_about.setVisibility(View.GONE);
                holder.mrp.setVisibility(View.VISIBLE);
                //holder.maxSp.setVisibility(View.VISIBLE);
            }
            if (Long.parseLong(products.get(i).getQty()) == 0 || Long.parseLong(products.get(i).getQty()) < Long.parseLong(products.get(i).getMinQty())) {
                holder.product_stock.setVisibility(View.VISIBLE);
                holder.parent.setBackgroundColor(getResources().getColor(R.color.white));

            } else {
                holder.parent.setBackgroundColor(getResources().getColor(R.color.white));
                holder.product_stock.setVisibility(View.GONE);
            }
            if(sp!=null)
            {
                if (products.get(i).getDiscountPrice() == null)
                {

                    holder.offer_tag2.setVisibility(View.GONE);
                    //holder.offer_price.setVisibility(View.INVISIBLE);
                    holder.maxSp.setVisibility(View.INVISIBLE);
                    holder.offerPricered.setText("\u20B9" + sp + "/pc");
                    holder.offerStringorange.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.offerPricered.setText("\u20B9"+products.get(i).getDiscountPrice());
                    holder.offerStringorange.setText(products.get(i).getDiscountQuote());
                    Log.d("obaid", products.get(i).getDiscountPrice() + "," + products.get(i).getDiscountQuote());
                    holder.maxSp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.offerStringorange.setVisibility(View.VISIBLE);
                    holder.offer_tag2.setVisibility(View.VISIBLE);
                }

            }



            return view;
        }

        class ViewHolder {
            RelativeLayout parent;
            ImageView productImage;
            TextView product_stock;
            TextView chat_about;
            TextView productName;
            TextView mrp;
            TextView maxSp;
            TextView productCode;
            TextView quantity;
            TextView offerPricered,offerStringorange;
            LinearLayout offer_price;
            ImageView menu_btn, fav_btn, offer_tag2;

            public ViewHolder(View view)
            {
                parent = (RelativeLayout) view.findViewById(R.id.grid_semiparent);
                productImage = (ImageView) view.findViewById(R.id.mpgi_product_image);
                //favouriteImage = (ImageView) view.findViewById(R.id.favourite_btn);
                productName = (TextView) view.findViewById(R.id.mpgi_product_name);
                mrp = (TextView) view.findViewById(R.id.mpgi_mrp);
                maxSp = (TextView) view.findViewById(R.id.mpgi_maxsp);
                productCode = (TextView) view.findViewById(R.id.mpgi_product_code);
                quantity = (TextView) view.findViewById(R.id.mpgi_quantity);
                menu_btn = (ImageView) view.findViewById(R.id.mpgi_menu_btn);
                fav_btn = (ImageView) view.findViewById(R.id.mpgi_favourite_btn);
                product_stock = (TextView) view.findViewById(R.id.out_stock);
                offer_price = (LinearLayout) view.findViewById(R.id.offer_price);
                chat_about = (TextView) view.findViewById(R.id.chat_about);
                offerPricered = (TextView)view.findViewById(R.id.offerpricered);
                offerStringorange = (TextView)view.findViewById(R.id.offerstringorange);
                offer_tag2 = (ImageView) view.findViewById(R.id.offer_tag2);
            }
        }
    }


    private void getPrimary(final ProductModal modal) {
        if (!MarketPlaceProductsActivity.this.isFinishing())
        {
            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getPrimary(modal.getCompanyId(), "1", helper.getB64Auth(MarketPlaceProductsActivity.this), "application/json", "application/json");
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
                                Log.i("chat_user_id", chat_user_id);
                                Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);

                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", "" + modal.getName());
                                    bundle.putString("code", "" + modal.getCode());
                                    if (modal.getChatAboutProduct() != null && modal.getChatAboutProduct().equalsIgnoreCase("1")) {
                                        bundle.putString("mrp", "");
                                        bundle.putString("price", "");
                                    } else {
                                        String marketPrice = String.format("%.2f", Double.parseDouble(modal.getMrp()));
                                        String sellingPrice = String.format("%.2f", Double.parseDouble(modal.getSp()));
                                        if (marketPrice.length() > 10) {
                                            marketPrice = marketPrice.substring(0, 7);
                                            bundle.putString("mrp", "" + marketPrice);

                                        } else {
                                            bundle.putString("mrp", "" + marketPrice);
                                        }

                                        if (sellingPrice.length() > 10) {
                                            sellingPrice = sellingPrice.substring(0, 7);
                                            bundle.putString("price", "" + sellingPrice);
                                        } else {
                                            bundle.putString("price", "" + sellingPrice);
                                        }
                                    }
                                    if (modal.getThumbnails() != null && modal.getThumbnails().getUrl() != null)
                                        bundle.putString("url", "" + modal.getThumbnails().getUrl());
                                    else
                                        bundle.putString("url", "");

                                    bundle.putString("moq", "" + modal.getMinQty());

                                    int iUserId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                                    if (cd.isConnectingToInternet() && !isFinishing())
                                        startActivity(new Intent(MarketPlaceProductsActivity.this, ChatActivity.class).putExtra("user", "" + cursor.getString(iUserId)).putExtra("from", 101).putExtra("result", bundle));
                                    else {
                                        if (!isFinishing())
                                            Toast.makeText(MarketPlaceProductsActivity.this, getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    getUserDetails(chat_user_id, modal);

                                }
                                cursor.close();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                                new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.sorry), getString(R.string.user_not_found));
                        }

                    } else {
                        int statusCode = response.code();

                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(MarketPlaceProductsActivity.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                                new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.error), getString(R.string.server_error));
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();

                    if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                        new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.error), getString(R.string.server_error));

                }
            });
        }


    }

    private void getUserDetails(String userId, final ProductModal productModal)
    {
        if (!MarketPlaceProductsActivity.this.isFinishing())
        {
            final Calendar c = Calendar.getInstance();
            flag = 0;
            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getUserDetails(userId, helper.getB64Auth(MarketPlaceProductsActivity.this), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();
                    if (response.isSuccess()) {
                        String userid = "";
                        JsonElement element = (JsonElement) response.body();
                        JSONObject json = null;

                        if (element != null && element.isJsonObject()) {
                            try {
                                json = new JSONObject(element.getAsJsonObject().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("JSS", json.toString());
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
                                    getContentResolver().insert(ChatProvider.NET_URI, cv);
                                    Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
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
                            if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                                new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.sorry), getString(R.string.server_error));

                        } else if (flag == 2) {
                            if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                                new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.sorry), getString(R.string.user_not_exist));

                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", "" + productModal.getName());
                            bundle.putString("code", "" + productModal.getCode());


                            if (productModal.getChatAboutProduct() != null && productModal.getChatAboutProduct().equals("1")) {

                                bundle.putString("mrp", "");
                                bundle.putString("price", "");
                            } else {

                                String marketPrice = String.format("%.2f", Double.parseDouble(productModal.getMrp()));
                                String sellingPrice = String.format("%.2f", Double.parseDouble(productModal.getSp()));
                                if (marketPrice.length() > 10) {
                                    marketPrice = marketPrice.substring(0, 7);
                                    bundle.putString("mrp", "" + marketPrice);

                                } else {
                                    bundle.putString("mrp", "" + marketPrice);
                                }

                                if (sellingPrice.length() > 10) {
                                    sellingPrice = sellingPrice.substring(0, 7);
                                    bundle.putString("price", "" + sellingPrice);
                                } else {
                                    bundle.putString("price", "" + sellingPrice);
                                }

                            }
                            if (productModal.getThumbnails() != null && productModal.getThumbnails().getUrl() != null)
                                bundle.putString("url", "" + productModal.getThumbnails().getUrl());
                            else
                                bundle.putString("url", "");
                            bundle.putString("moq", "" + productModal.getMinQty());
                            startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra("user", "" + userid).putExtra("from", 101).putExtra("result", bundle));


                        }
                    } else {
                        int statusCode = response.code();

                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(MarketPlaceProductsActivity.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                                new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.error), getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();
                    if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing())
                        new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.error), getString(R.string.server_error));

                }
            });
        }
    }

    private void prepareRequest(String categoryId, final int pageNo, String sortBy, String sortOrder, String filter) {
        isLoading = true;
        if (!isFinishing())
            progress.show();

        if (page_no != 0)
        {
            RestClient.GitApiInterface service = RestClient.getClient();
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            Call<ProductList> call = service.getProduct(categoryId, "A", user_id, "1", "grid", sortBy, pageNo, sortOrder, "public", filter, "true", helper.getB64Auth(MarketPlaceProductsActivity.this), "application/json", "application/json");
            call.enqueue(new Callback<ProductList>()
            {
                @Override
                public void onResponse(Response response)
                {
                    progress.dismiss();
                    listView.removeFooterView(loadingFooter);
                    if (response.isSuccess())
                    {
                        //progress.dismiss();
                        isLoading = false;
                        ProductList holder = (ProductList) response.body();
                        productModals.addAll(holder.getProducts());
                        JsonElement element = holder.getFilters();
                        jsonArray = null;
                        if (element != null && element.isJsonObject())
                        {
                            try
                            {
                                jsonArray = new JSONObject(element.getAsJsonObject().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("jsonArray", element.getAsJsonObject().toString());
                        ((BaseAdapter) productsGrid.getAdapter()).notifyDataSetChanged();
                        gridAdapter.swapItems(productModals);
                        txtSelected.setText(header.getText().toString());
                        if (holder.getProducts().size() < 10) {
                            page_no = 0;
                        }

                    } else {

                        page_no = 0;
                        //}
                        if (response.code() == 401) {

                            final SessionManager sessionManager = new SessionManager(MarketPlaceProductsActivity.this);
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing()) {
                                new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this,
                                        getString(R.string.error), getString(R.string.server_error));
                            }
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    page_no = 0;

                    progress.dismiss();
                    if (MarketPlaceProductsActivity.this != null && !MarketPlaceProductsActivity.this.isFinishing()) {
                        new AlertDialogManager().showAlertDialog(MarketPlaceProductsActivity.this, getString(R.string.error), getString(R.string.server_error));
                    }

                }
            });
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setNotifCount();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(screenVisited);
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Category Page").setValue(System.currentTimeMillis() - startTime).build());
    }

    private void setNotifCount()
    {
        Cursor mCursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null)
        {
            if (mNotifCount == 0)
            {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            }

            else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /****************ISTIAQUE COMMENTED OUT *******************/
        /*getMenuInflater().inflate(R.menu.search_cart_menu, menu);
        MenuItem item = menu.findItem(R.id.searchCart_cart);*/
        /****************ISTIAQUE COMMENTED OUT *******************/
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.cart);
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

                startActivity(new Intent(MarketPlaceProductsActivity.this, CartActivity.class));
            }
        });
        /***************************** ISTIAQUE: COMMENTED OUT ****************/
        //MenuItem sitem = menu.findItem(R.id.searchCart_search);
        //sitem.setVisible(false);
        /***************************** ISTIAQUE: COMMENTED OUT ****************/

        /****************** ISTIAQUE: CODE BEGINS **************************/
        MenuItem aItem = menu.findItem(R.id.user_profile);
        aItem.setVisible(false);
        MenuItem addItem = menu.findItem(R.id.add);
        addItem.setVisible(false);
        /****************** ISTIAQUE: CODE ENDS **************************/
        return super.onCreateOptionsMenu(menu);
    }
}
