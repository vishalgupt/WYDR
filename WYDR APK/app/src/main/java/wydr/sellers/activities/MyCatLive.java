package wydr.sellers.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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
import wydr.sellers.adapter.MyLiveAdapter;
import wydr.sellers.adapter.ParentRootAdapter;
import wydr.sellers.modal.CatalogProductModal;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.MyCategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;
import wydr.sellers.syncadap.FeedCatalog;

public class MyCatLive extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, OnBackPressedListener {
    Helper helper = new Helper();
    Toolbar toolbar;
    ArrayList<CatalogProductModal> list;
    MyLiveAdapter adapter;
    ListView listViewItems;
    ConnectionDetector cd;
    JSONParser parser;
    private ProgressDialog progressDialog;
    String catTo;
    TextView categoryLabel, orderStatus;
    ExpandableListView listView;
    ObjectItems obj;
    private boolean isSelect = true;
    Button allCategory, myCategory;
    String search_string = "", sortBy;
    ViewPager viewPager;
    android.app.AlertDialog.Builder alertDialog;
    public final String TAG = "MyCatalog";
    ImageView filter;
    Animation animation;
    LinearLayout sortButton, categoryButton, linearBottom, allHeader;
    static long min_value = 0, max_value = 0;
    long leftValue = 0, rightValue = 0;
    public int FILTER_REQUEST = 105;
    private boolean is_selected = false, selected_range = false;
    private static String selected_value = "";
    String[] sortfilter = new String[]{"0", "0", "", "", ""};
    SearchView searchView;
    int prev_sort = 4;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mycatalog, null);
        catTo = helper.getDefaults("company_id", getActivity());
        setHasOptionsMenu(true);
        progressStuff();
        iniStuff(view);


        // LoadMyCatalog();
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                int getId = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_PRODUCT_ID);
                int getName = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_TITLE);
                Intent intent = new Intent(getActivity(), MyCatalogProdDetails.class);
                intent.putExtra("product_id", cursor.getString(getId));
                intent.putExtra("name", cursor.getString(getName));
                // Log.i(TAG, "product_id" + cursor.getString(getId) + " // " + cursor.getString(getName));
                startActivity(intent);
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (isSelect)
                {
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
                    if (parent.isGroupExpanded(groupPosition)) {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    }

                    else
                    {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }


                    ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                    //  //Log.i("MPF", "items.title *----*" + items.title);
                    if (items.children.size() == 0) {
                        closeDropdown();

                        sortfilter[4] = items.id;
                        if (items.title.contains("(")) {
                            categoryLabel.setText(items.title.substring(0, items.title.indexOf("(")));
                        } else {
                            categoryLabel.setText(items.title);
                        }

                        adapter.getFilter().filter(sortBy);
                        // //Log.i("MPF", "items.id *----*" + items.id);
                    }
                    else {
                        // //Log.i("ALLSELERS - bootm", "4");
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
                            closeDropdown();
                            sortfilter[4] = items.id;
                            if (items.title.contains("(")) {
                                categoryLabel.setText(items.title.substring(0, items.title.indexOf("(")));
                            } else {
                                categoryLabel.setText(items.title);
                            }
                            adapter.getFilter().filter(sortBy);
                            return false;
                        }
                    });
                }
                return false;
            }
        });
        return view;
    }


    private void progressStuff() {

        session = new SessionManager(getActivity().getApplicationContext());
        cd = new ConnectionDetector(getActivity());
        parser = new JSONParser();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getString(R.string.loading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        alertDialog = new android.app.AlertDialog.Builder(getActivity());
    }

    @Override
    public void onResume() {
        setNotifCount();
        super.onResume();
        if (listView.getVisibility() == View.VISIBLE) {
            getActivity().findViewById(R.id.actionbar_container).setVisibility(View.GONE);
            listViewItems.setVisibility(View.GONE);
        } else {
            getActivity().findViewById(R.id.actionbar_container).setVisibility(View.VISIBLE);
            listViewItems.setVisibility(View.VISIBLE);
        }
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

    private void iniStuff(View view) {
        cd = new ConnectionDetector(getActivity());
        viewPager = (ViewPager) getActivity().findViewById(R.id.catpager);
        orderStatus = (TextView) view.findViewById(R.id.catalog_record_status);
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        linearBottom = (LinearLayout) view.findViewById(R.id.obottombar);
        sortBy = FeedCatalog.COLUMN_UPDATEDAT + " desc";
        sortfilter[3] = sortBy;
        sortButton = (LinearLayout) view.findViewById(R.id.osort);
        categoryButton = (LinearLayout) view.findViewById(R.id.ocategory_button);
        sortButton.setOnClickListener(this);
        categoryButton.setOnClickListener(this);
        filter = (ImageView) view.findViewById(R.id.odateFilterQuery);
        linearBottom.setVisibility(View.VISIBLE);
        filter.setVisibility(View.VISIBLE);
        categoryLabel = (TextView) view.findViewById(R.id.sellerQueryText);
        listView = (ExpandableListView) view.findViewById(R.id.expandableMyCatalog);
        View view2 = View.inflate(getActivity(), R.layout.category_header, null);
        allCategory = (Button) view2.findViewById(R.id.allCategory);
        myCategory = (Button) view2.findViewById(R.id.myCategory);
        allHeader = (LinearLayout) view2.findViewById(R.id.all_header);
        listViewItems = (ListView) view.findViewById(R.id.listViewCatalog);
        list = new ArrayList<>();
        getLoaderManager().initLoader(1, null, this);
        // allCategory.setOnClickListener(this);
        allCategory.setVisibility(View.GONE);
        //   myCategory.setOnClickListener(this);
        listView.addHeaderView(view2, null, false);

        adapter = new MyLiveAdapter(this.getActivity(), null, this.getActivity());
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                //   //Log.i("Calling filter -", sortfilter[0] + "/" + sortfilter[1] + "/" + sortfilter[2] + "/" + sortfilter[3] + "/" + sortfilter[4]);
                if (sortfilter[4].equalsIgnoreCase("")) {

                    if (!search_string.equalsIgnoreCase("")) {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0"))) {
                            //   //Log.i("Calling filter -", "1");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //  //Log.i("Calling filter -", "2");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], sortfilter[2]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "3");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ?", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1]}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            //Log.i("Calling filter -", "4");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "5");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)", new String[]{"%" + search_string + "%", sortfilter[2]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "6");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ?", new String[]{"%" + search_string + "%"}, String.valueOf(sortfilter[3]));
                            }
                        }

                    } else {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

                        {
                            //Log.i("Calling filter -", "1");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "2");

                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)", new String[]{sortfilter[0], sortfilter[1], sortfilter[2]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "3");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ?", new String[]{sortfilter[0], sortfilter[1]}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            //Log.i("Calling filter -", "4");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "5");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)", new String[]{sortfilter[2]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "6");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, null, null, String.valueOf(sortfilter[3]));
                            }
                        }
                    }
                } else {

                    if (!search_string.equalsIgnoreCase("")) {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

                        {
                            //Log.i("Calling filter -", "1");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "2");

                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + FeedCatalog.COLUMN_CATEGORY_ID + "=? ", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], sortfilter[2], sortfilter[4]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "3");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ? and " + FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], sortfilter[4]}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            //Log.i("Calling filter -", "4");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "5");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and" + FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{"%" + search_string + "%", sortfilter[2], sortfilter[4]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "6");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_TITLE + " LIKE ? and " + FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{"%" + search_string + "%", sortfilter[4]}, String.valueOf(sortfilter[3]));
                            }
                        }

                    } else {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

                        {
                            //Log.i("Calling filter -", "1");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "2");

                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{sortfilter[0], sortfilter[1], sortfilter[2], sortfilter[4]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "3");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ?  and " + FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{sortfilter[0], sortfilter[1], sortfilter[4]}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            //Log.i("Calling filter -", "4");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //Log.i("Calling filter -", "5");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)  and " + FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{sortfilter[2], sortfilter[4]}, String.valueOf(sortfilter[3]));
                            } else {
                                //Log.i("Calling filter -", "6");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_CATEGORY_ID + "=?", new String[]{sortfilter[4]}, String.valueOf(sortfilter[3]));
                            }
                        }
                    }
                }


            }
        });
        listViewItems.setAdapter(adapter);
        allHeader.setOnClickListener(this);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        toolbar.setVisibility(View.GONE);
        listViewItems.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        linearBottom.startAnimation(animation);
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // linearBottom.startAnimation(animation);

            }
        });
        filter.setOnClickListener(this);
        if (cd.isConnectingToInternet()) {
            new GetUserCategory().execute();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ocategory_button:
                orderStatus.setVisibility(View.GONE);
                if (listView.getVisibility() == View.VISIBLE) {
                    closeDropdown();

                } else {
                    if (listView.getAdapter() != null) {
                        if (listView.getAdapter().isEmpty()) {
                            loadSubCategory();
                        } else {
                            openDropdown();
                        }
                    } else {

                        loadSubCategory();
                    }

                }

                break;
            case R.id.all_header:
                closeDropdown();
                sortfilter[4] = "";
                categoryLabel.setText("CATEGORY");
                adapter.getFilter().filter(sortBy);
                break;
            case R.id.osort:

                final PopupWindow popupWindow = new PopupWindow(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.query_item_menu, null);
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
                        sortBy = FeedCatalog.COLUMN_SP + " + 0 asc";
                        sortfilter[3] = sortBy;
                        prev_sort = 1;
                        //adapter.getFilter().filter(FeedCatalog.COLUMN_TITLE + "COLLATE  NOCASE DESC");
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_desc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_sort = 2;
                        sortBy = FeedCatalog.COLUMN_SP + " + 0 desc";
                        sortfilter[3] = sortBy;
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_sort = 3;
                        //sortBy = FeedCatalog.COLUMN_TITLE;
                        sortBy = " (CASE   when " + FeedCatalog.COLUMN_TITLE + "*1=0 THEN 0 ELSE 1 END ) asc , " + FeedCatalog.COLUMN_TITLE + " COLLATE NOCASE";
                        //adapter.getFilter().filter(FeedCatalog.COLUMN_TITLE + "COLLATE  NOCASE DESC");
                        sortfilter[3] = sortBy;
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_sort = 4;
                        sortBy = FeedCatalog.COLUMN_UPDATEDAT + " desc";
                        sortfilter[3] = sortBy;
                        //adapter.getFilter().filter(FeedCatalog.COLUMN_TITLE + "COLLATE  NOCASE DESC");
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();


                    }
                });


                popupWindow.setContentView(view);
                popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                popupWindow.showAsDropDown(sortButton, 0, 5);

                break;
            case R.id.odateFilterQuery:
                Log.d("date", "Mycatalog");
                JSONObject price_object = new JSONObject();
                try {
                    price_object.put("myslider", true);
                    price_object.put("filter", "Price");
                    price_object.put("filter_id", "1");
                    price_object.put("min", min_value);
                    price_object.put("max", max_value);
                    price_object.put("selected_range", selected_range);
                    price_object.put("left", leftValue);
                    price_object.put("right", rightValue);
                    //Log.i("filter--", "min_value" + min_value + "/" + "max_value" + max_value + "/" + "left" + leftValue + "/" + "right" + rightValue + "/");

                    JSONObject visibility_object = new JSONObject();
                    JSONObject variant_object = new JSONObject();
                    JSONArray jsonArray = new JSONArray();

                    JSONObject visibility_object_child = new JSONObject();
                    visibility_object_child.put("name", "Public");
                    visibility_object_child.put("id", "5");
                    jsonArray.put(visibility_object_child);
                    JSONObject visibility_object_child2 = new JSONObject();
                    visibility_object_child2.put("name", "Private");
                    visibility_object_child2.put("id", "6");
                    jsonArray.put(visibility_object_child2);

                    variant_object.put("variant_val", jsonArray);
                    visibility_object.put("variants", variant_object);
                    visibility_object.put("filter", "Product Visibility");
                    visibility_object.put("is_selected", is_selected);
                    visibility_object.put("selected_value", selected_value);
                    visibility_object.put("filter_id", "2");

                    JSONObject Pre_Filter_Array = new JSONObject();
                    Pre_Filter_Array.put("3", price_object);
                    Pre_Filter_Array.put("4", visibility_object);

                    JSONObject Filter_array = new JSONObject();
                    Filter_array.put("current", Pre_Filter_Array);
                    //Log.i("Filter==", Pre_Filter_Array.toString());
                    startActivityForResult(new Intent(getActivity(), MyCatalogFilter.class).putExtra("filter", Filter_array.toString()).putExtra("request_code", FILTER_REQUEST), FILTER_REQUEST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;


        }
    }

    public void openDropdown() {

        if (listView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            // mDropdownTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            // R.drawable.icn_dropdown_close, 0);\
            getActivity().findViewById(R.id.actionbar_container).setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listViewItems.setVisibility(View.GONE);
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }
    //  }

    public void closeDropdown() {
        // expend = false;
        if (listView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            listView.startAnimation(anim);
            listView.setVisibility(View.GONE);
            listViewItems.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.actionbar_container).setVisibility(View.VISIBLE);
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

        }
        //}


    }


    public void onBackPressed() {
        //Log.i("CATLIVe", "BACKING");
        if (this.listView.getVisibility() == View.VISIBLE) {
            closeDropdown();
        } else {
            if (MyCatalog.backflag) {

                startActivity(new Intent(getActivity(), MyProfile.class));
                getActivity().finish();

            } else {
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();
            }
        }
        //Log.i("MyCatDrafts", "2");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.add_srch_add:
                startActivity(new Intent(getActivity(), AddProduct.class).putExtra("Return", "1"));

                adapter.notifyDataSetChanged();
                getActivity().finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_search_menu, menu);

        MenuItem item = menu.findItem(R.id.add_srch_cart);
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
                //Log.i("", " insdie GetUserCategories json ");
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");

                startActivity(new Intent(getActivity(), CartActivity.class));
            }
        });


        SearchView searchView = (SearchView) menu.findItem(R.id.add_srch_search).getActionView();
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
            search_string = "";
            adapter.getFilter().filter("search");


            listViewItems.clearTextFilter();
        } else {

            search_string = newText.toString();
            adapter.getFilter().filter("search");
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                Log.i("result filter------", data.getStringExtra("result"));
                try {
                    JSONObject resultobject = new JSONObject(data.getStringExtra("result"));
                    Iterator<?> keys = resultobject.keys();

                    is_selected = false;
                    //Log.i("result filter------", "length--" + resultobject.length());
                    if (resultobject.length() == 0) {

                        rightValue = leftValue = 0;
                        sortfilter[4] = "";
                        selected_value = "";
                        sortfilter[0] = String.valueOf(leftValue);
                        sortfilter[1] = String.valueOf(rightValue);
                        sortfilter[2] = selected_value;
                        adapter.getFilter().filter("a");
                    } else {
                        while (keys.hasNext()) {
                            int key = Integer.parseInt((String) keys.next());
                            JSONArray jsonArray = resultobject.getJSONArray(String.valueOf(key));
                            if (key == 3) {

                                leftValue = jsonArray.getInt(0);
                                rightValue = jsonArray.getInt(1);
                            }
                            if (key == 4) {
                                selected_value = "";
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    selected_value = selected_value + jsonArray.get(i) + ",";
                                }
                                if (selected_value.length() > 2)
                                    selected_value = selected_value.substring(0, selected_value.length() - 1);
                                is_selected = true;
                            }
                        }
                        sortfilter[0] = String.valueOf(leftValue);
                        sortfilter[1] = String.valueOf(rightValue);
                        sortfilter[2] = selected_value;
                        sortfilter[3] = sortBy;
                        if (leftValue == 0 && rightValue == 0)
                            selected_range = false;
                        else
                            selected_range = true;
                        adapter.getFilter().filter("a");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (sortfilter[2].contains(","))
            sortfilter[2] = "";
        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

        {

            if (!sortfilter[2].equalsIgnoreCase("")) {

                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ? and " + FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)", new String[]{sortfilter[0], sortfilter[1], sortfilter[2]}, String.valueOf(sortfilter[3]));
            } else {

                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_SP + " >= ? and " + FeedCatalog.COLUMN_SP + " <= ?", new String[]{sortfilter[0], sortfilter[1]}, String.valueOf(sortfilter[3]));
            }
        }

        else
        {
            if (!sortfilter[2].equalsIgnoreCase(""))
            {
                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_FEED, null, FeedCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)", new String[]{sortfilter[2]}, String.valueOf(sortfilter[3]));
            }

            else
            {
                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_FEED, null, null, null, String.valueOf(sortfilter[3]));
            }
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
        if (data != null && data.getCount() == 0) {

            orderStatus.setVisibility(View.VISIBLE);
            listViewItems.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }


    private void loadSubCategory() {
        obj = new ObjectItems();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + CategoryTable.TABLE_CONTACTS + " where " + CategoryTable.KEY_PARENT_ID + "=0  and " + CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" + MyCategoryTable.KEY_PARENT_ID + ") from " + MyCategoryTable.TABLE_CONTACTS + ")", null);
            //Log.i("allsellers", "select * from " + CategoryTable.TABLE_CONTACTS + " where " + CategoryTable.KEY_PARENT_ID + "=0  and " + CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" + MyCategoryTable.KEY_PARENT_ID + ") from " + MyCategoryTable.TABLE_CONTACTS + ")");
            int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
            int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
            //Log.i("ALLSELELRS *", "cursor.getCount " + cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                ObjectItems root = new ObjectItems();
                root.id = cursor.getString(iId);
                root.children = new ArrayList<ObjectItems>();
                Cursor cursorParent = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursor.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
                //Log.i("ALLSELELRS *", "cursorParent.getCount " + cursorParent.getCount());

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
                    Cursor cursorChild = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursorParent.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
                    int iIdChild = cursorChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_ID);
                    int iNameChild = cursorChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_NAME);
                    int iNameCount = cursorChild.getColumnIndexOrThrow(MyCategoryTable.KEY_PRODUCT_COUNT);
                    //Log.i("ALLSELELRS *", "cursorChild.getCount " + cursorChild.getCount());

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

                        Cursor cursorGrandChild = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursorChild.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
                        int iIdGrandChild = cursorGrandChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_ID);
                        int iNameGrandChild = cursorGrandChild.getColumnIndexOrThrow(MyCategoryTable.KEY_CATEGORY_NAME);
                        int iNameGrandCount = cursorGrandChild.getColumnIndexOrThrow(MyCategoryTable.KEY_PRODUCT_COUNT);
                        //Log.i("ALLSELELRS *", "cursorGrandChild.getCount " + cursorGrandChild.getCount());
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
                                //Log.i("ALLSELELRS *", "cursorGrandChild.getString(iNameGrandChild) " + cursorGrandChild.getString(iNameGrandChild) + itemsChildCount);

                            }
                        }


                        itemsChildCount = cursorChild.getInt(iNameGrandCount);
                        child.title = cursorChild.getString(iNameChild) + " (" + itemsChildCount + ")";
                        cursorGrandChild.close();
                        //Log.i("ALLSELELRS *", "cursorChild.getString(iNameChild) " + cursorChild.getString(iNameChild) + itemsChildCount);
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
                    //Log.i("ALLSELELRS *", "cursorParent.getString(iNameParent) " + cursorParent.getString(iNameParent) + ParentCount);
                    if (ParentCount > 0) {
                        root.children.add(parent);
                    }

                }

                cursorParent.close();

                root.title = cursor.getString(iName) + " (" + RootCount + ")";
                //Log.i("ALLSELELRS *", "cursor.getString(iName) " + cursor.getString(iName) + RootCount);
                if (RootCount > 0) {
                    obj.children.add(root);
                }


            }
            cursor.close();
            db.close();
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("MyContentProvider", "Exception:" + e.getMessage());
        }
        isSelect = false;

        ExpandableListView.OnGroupClickListener grpLst = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView eListView, View view, int groupPosition,
                                        long id) {
                ObjectItems items = (ObjectItems) eListView.getExpandableListAdapter().getGroup(groupPosition);
                if (items.children.size() == 0) {
                    sortfilter[4] = items.id;
                    adapter.getFilter().filter("a");
                    if (items.title.contains("(")) {
                        categoryLabel.setText(items.title.substring(0, items.title.indexOf("(")));
                    } else {
                        categoryLabel.setText(items.title);
                    }
                    closeDropdown();
                }
                //Log.i("MPF", ".title *----*" + items.title);
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
                sortfilter[4] = items.id;

                adapter.getFilter().filter("a");
                if (items.title.contains("(")) {
                    categoryLabel.setText(items.title.substring(0, items.title.indexOf("(")));
                } else {
                    categoryLabel.setText(items.title);
                }

                return false/* or false depending on what you need */;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        ParentRootAdapter adapter = new ParentRootAdapter(getActivity(), obj, grpLst, childLst, grpExpLst);
        listView.setAdapter(adapter);
        openDropdown();


    }


    private class GetUserCategory extends AsyncTask<String, String, JSONObject> {
        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        int flag = 0;
        String company_id = helper.getDefaults("company_id", getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (!getActivity().isFinishing() && !progressDialog.isShowing())
//                progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", company_id));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + company_id + "/categories", "GET", params, getActivity());
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
                            //Log.i("SUBCATEGORY", "DATA -- > " + cd1.getId() + "/" + cd1.getName() + "/" + cd1.getHas_child() + "/" + cd1.getParentid() + "/" + cd1.getProduct_count());
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
                                    //Log.i("SUBCATEGORY", "DATA -- > " + cd2.getId() + "/" + cd2.getName() + "/" + cd2.getHas_child() + "/" + cd2.getParentid() + "/" + cd2.getProduct_count());
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
                                            //Log.i("SUBCATEGORY", "DATA -- > " + cd3.getId() + "/" + cd3.getName() + "/" + cd3.getHas_child() + "/" + cd3.getParentid() + "/" + cd3.getProduct_count());
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
                                                    //Log.i("SUBCATEGORY", "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" + cd4.getProduct_count());
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
//            //Log.i("Act",getActivity().toString());
            if ((getActivity() != null) && !getActivity().isFinishing()) {
                // progressDialog.dismiss();
                if (flag == 1) {
                    loadSubCategory();
                } else {
                    if (catdata != null) {
                        getActivity().getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
                        // //Log.i("AddProdct", "DELETED" + a);
                        for (int s = 0; s < catdata.size(); s++) {
                            ContentValues values = new ContentValues();
                            values.put(MyCategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                            values.put(MyCategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                            values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getGot_child());
                            values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                            values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                            values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));
                            //Log.i("SUBCATEOGRY", "DATA -- > final" + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getGot_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);
                            getActivity().getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_MYCATEGORY, null, false);
                            //     //Log.i("Helper", "uri at mycatalog " + uri.toString());
                        }
                    }
                    // loadSubCategory();
                }
            }
        }
    }
}