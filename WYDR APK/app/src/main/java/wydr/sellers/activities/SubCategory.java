package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.adapter.HomeViewAdapter;
import wydr.sellers.adapter.ParentRootAdapter;
import wydr.sellers.adapter.RootAdapterExp;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.GridRow;
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
 * Created by surya on 17/9/15.
 */
public class SubCategory extends AppCompatActivity implements View.OnClickListener {
    static String title = "", header_title = "";
    HomeViewAdapter secondAdapter;
    GridView gridView;
    Button all, my;
    ExpandableListView listView;
    android.app.AlertDialog.Builder alertDialog;
    JSONObject jsonObj;
    Helper helper = new Helper();
    JSONParser parser;
    ObjectItems obj;
    TextView header;
    RootAdapterExp adapter;
    String categoryString;
    TextView textCategory;
    String jsonData, titleData;
    private JSONArray jsonArray;
    private ConnectionDetector cd;
    private ProgressDialog progress;
    private boolean isSelect = true;
    private RelativeLayout ll;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    SessionManager session;
    String screenVisited = "";

    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(screenVisited);
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        //Toast.makeText(SubCategory.this, screenVisited, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_category);
        cd = new ConnectionDetector(SubCategory.this);
        progressStuff();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.toolbarimg).setVisibility(View.GONE);
        header = (TextView) findViewById(R.id.toolbartext);
        jsonData = getIntent().getStringExtra("json");
        titleData = getIntent().getStringExtra("name");
        title = getIntent().getStringExtra("name");
        header_title = getIntent().getStringExtra("name");
        screenVisited += getIntent().getStringExtra("screenVisited");
        header.setText(titleData);
        listView = (ExpandableListView) findViewById(R.id.expandableCategory2);
        View view = View.inflate(SubCategory.this, R.layout.category_header, null);
        all = (Button) view.findViewById(R.id.allCategory);
        my = (Button) view.findViewById(R.id.myCategory);
        ll = (RelativeLayout) findViewById(R.id.cat_category_button);
        textCategory = (TextView) findViewById(R.id.txtSubCatName);
        all.setOnClickListener(this);
        my.setOnClickListener(this);
        ll.setOnClickListener(this);
        LinearLayout allTab = (LinearLayout) view.findViewById(R.id.all_header);
        TextView div = (TextView) view.findViewById(R.id.divRoot);
        allTab.setVisibility(View.GONE);
        div.setVisibility(View.GONE);
        // loader = new ProductLoader(getActivity().getApplicationContext());
        listView.addHeaderView(view, null, false);
        header.setOnClickListener(this);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
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
                    TextView tv;
                    tv = (TextView) v.findViewById(R.id.itemRootTitle);
                    if (tv == null) {
                        tv = (TextView) v.findViewById(R.id.itemParentTitle);
                    }


                    ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                    if (items.children.size() == 0) {

                        categoryString = items.id;
                        textCategory.setText(items.title);
                        closeDropdown();
                        Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                        intent.putExtra("id", items.id);
                        intent.putExtra("name", items.title);
                        intent.putExtra("screenVisited", screenVisited + items.title + "/");
                        startActivity(intent);
                        //Toast.makeText(SubCategory.this, "1-"+screenVisited, Toast.LENGTH_SHORT).show();
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
                    listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                            ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                            categoryString = items.id;
                            textCategory.setText(items.title);
                            closeDropdown();
                            Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                            intent.putExtra("id", items.id);
                            intent.putExtra("name", items.title);
                            intent.putExtra("screenVisited", screenVisited + items.title + "/");
                            startActivity(intent);
                            //Toast.makeText(SubCategory.this, "2-"+screenVisited, Toast.LENGTH_SHORT).show();
//
                            return false;
                        }
                    });
                }
                return false;
            }
        });
        setTitle("");
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView sliderMenu = (ImageView) findViewById(R.id.btnMenu);
        sliderMenu.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.grid_subcategory);
        secondAdapter = new HomeViewAdapter(this, R.layout.category_items,
                new ArrayList<GridRow>());
        try {
            jsonArray = new JSONArray(jsonData);
            int parentCount = 0;
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                String name = object.getString("category");
                String id = object.getString("category_id");
                Log.d("from server", "" + object.optInt("product_count"));
                String url;
                if (object.has("main_pair")) {
                    JSONObject js = object.getJSONObject("main_pair");
                    JSONObject js_obj = js.getJSONObject("detailed");
                    url = js_obj.getString("http_image_path");
                } else {
                    // url = "http://162.243.184.250/sellerapp/images/";
                    url = "";
                }
                GridRow gridRow1 = new GridRow(url, name, id);
                gridRow1.id = id;
                gridRow1.setCount(object.optInt("product_count"));
                gridRow1.subCategories = object.has("subcategories");
                secondAdapter.add(gridRow1);
            }
            gridView.setAdapter(secondAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridRow gridRow = (GridRow) gridView.getAdapter().getItem(position);
                if (gridRow.hasSubCategories()) {
                    try {
                        JSONObject object1 = jsonArray.getJSONObject(position);
                        JSONArray jsonArray = object1.getJSONArray("subcategories");

                        Intent intent = new Intent(SubCategory.this, SubCategory.class);
                        intent.putExtra("name", gridRow.title);
                        intent.putExtra("json", jsonArray.toString());
                        intent.putExtra("screenVisited", screenVisited + gridRow.title + "/");
                        startActivity(intent);
                        //Toast.makeText(SubCategory.this, "3-"+screenVisited, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*******************************ISTIAQUE***************************************/
                    Controller application = (Controller) SubCategory.this.getApplication();
                    Tracker mTracker = application.getDefaultTracker();
                    application.trackEvent("Subcategory", "Move", "SubCategory");
                    /*******************************ISTIAQUE***************************************/
                } else {

                    if (gridRow.getCount() > 0) {
                        /*******************************ISTIAQUE***************************************/
                        Controller application = (Controller) SubCategory.this.getApplication();
                        Tracker mTracker = application.getDefaultTracker();
                        application.trackEvent("Subcategory", "Move", "MarketPlaceProductsActivity");
                        /*******************************ISTIAQUE***************************************/

                        Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                        intent.putExtra("id", gridRow.id);
                        intent.putExtra("name", gridRow.title);
                        intent.putExtra("screenVisited", screenVisited + gridRow.title + "/");
                        startActivity(intent);
                        //Toast.makeText(SubCategory.this, "4-"+screenVisited, Toast.LENGTH_SHORT).show();
                    } else {
                        new AlertDialogManager().showAlertDialog(SubCategory.this, getString(R.string.oops), getString(R.string.no_prods_category));
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (listView.getVisibility() == View.VISIBLE) {
            closeDropdown();

        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            /******************* ISTIAQUE: CODE BEGINS ***********************/
            case R.id.search:
                startActivity(new Intent(SubCategory.this, SearchActivity.class));
                break;
            /******************* ISTIAQUE: CODE BEGINS ***********************/
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDropdown() {


        if (listView.getVisibility() == View.GONE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            // mDropdownTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            // R.drawable.icn_dropdown_close, 0);
            listView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            // listView.setImageResource(R.drawable.collapse_1);
        }
    }
    //  }

    private void closeDropdown() {

        if (listView.getVisibility() == View.VISIBLE) {
            Log.i("SubCategory", "ENTERED");
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //  case R.id.toolbartext:
            case R.id.cat_category_button:

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
                all.setTextColor(getResources().getColor(R.color.primary_500));
                my.setTextColor(getResources().getColor(R.color.text_color));
                loadCategory();
                break;
            case R.id.myCategory:
                all.setTextColor(getResources().getColor(R.color.text_color));
                my.setTextColor(getResources().getColor(R.color.primary_500));
                if (cd.isConnectingToInternet()) {
                    new GetUserCategory().execute(helper.getDefaults("company_id", SubCategory.this));
                } else {
                    loadSubCategory();
                }
                break;

        }
    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        //cd = new ConnectionDetector(SubCategory.this);
        parser = new JSONParser();
        progress = new ProgressDialog(SubCategory.this);
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new android.app.AlertDialog.Builder(SubCategory.this);
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

        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getGroup(groupPosition);
                if (items.children.size() == 0) {

                    closeDropdown();
                    Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                    intent.putExtra("id", items.id);
                    intent.putExtra("name", items.title);
                    intent.putExtra("screenVisited", screenVisited + items.title + "/");
                    startActivity(intent);
                    //Toast.makeText(SubCategory.this, "5-"+screenVisited, Toast.LENGTH_SHORT).show();
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
                closeDropdown();
                Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                intent.putExtra("id", items.id);
                intent.putExtra("name", items.title);
                intent.putExtra("screenVisited", screenVisited + items.title + "/");
                startActivity(intent);
                //Toast.makeText(SubCategory.this, "6-"+screenVisited, Toast.LENGTH_SHORT).show();
                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter = new RootAdapterExp(SubCategory.this, obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter);
        openDropdown();

    }


    private void loadSubCategory() {
        obj = new ObjectItems();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(SubCategory.this);
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

                    closeDropdown();
                    Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                    intent.putExtra("id", items.id);
                    intent.putExtra("name", items.title);
                    intent.putExtra("screenVisited", screenVisited + items.title + "/");
                    startActivity(intent);
                    //Toast.makeText(SubCategory.this, "7-"+screenVisited, Toast.LENGTH_SHORT).show();
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
                closeDropdown();
                Intent intent = new Intent(SubCategory.this, MarketPlaceProductsActivity.class);
                intent.putExtra("id", items.id);
                intent.putExtra("name", items.title);
                intent.putExtra("screenVisited", screenVisited + items.title + "/");
                startActivity(intent);
                //Toast.makeText(SubCategory.this, "8-"+screenVisited, Toast.LENGTH_SHORT).show();
                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        ParentRootAdapter adapter = new ParentRootAdapter(SubCategory.this, obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter);
        openDropdown();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("data", getIntent().getStringExtra("json"));
        outState.putString("title", titleData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        jsonData = savedInstanceState.getString("data");
        header.setText(savedInstanceState.getString("title"));

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
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params, SubCategory.this);
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
            if ((SubCategory.this != null) && !isFinishing()) {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

                startActivity(new Intent(SubCategory.this, CartActivity.class));
            }
        });
        /****************** ISTIAQUE: COMMENTED OUT **************************/
        //MenuItem sitem = menu.findItem(R.id.searchCart_search);
        //sitem.setVisible(false);
        /****************** ISTIAQUE: COMMENTED OUT **************************/

        /****************** ISTIAQUE: CODE BEGINS **************************/
        MenuItem aItem = menu.findItem(R.id.user_profile);
        aItem.setVisible(false);
        MenuItem addItem = menu.findItem(R.id.add);
        addItem.setVisible(false);
        /****************** ISTIAQUE: CODE ENDS **************************/
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
}
