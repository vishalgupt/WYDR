package wydr.sellers.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
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
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.adapter.ParentRootAdapter;
import wydr.sellers.modal.CatalogProductModal;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.MyCategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;
import wydr.sellers.syncadap.AlteredCatalog;
import wydr.sellers.syncadap.FeedCatalog;

public class MyCatDrafts extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, OnBackPressedListener {
    Helper helper = new Helper();
    Toolbar toolbar;
    ArrayList<CatalogProductModal> list;
    MyDraftAdapter adapter;
    ListView listViewItems;
    ConnectionDetector cd;
    JSONParser parser;
    private ProgressDialog progress;
    String catTo;
    ExpandableListView listView;
    ObjectItems obj;
    private boolean isSelect = true;
    Button allCategory, myCategory;
    String search_string = "";
    ViewPager viewPager;
    TextView categoryLabel;
    public String TAG = "MyCatalog";
    ImageView filter;
    Animation animation;
    LinearLayout sortButton, categoryButton, linearBottom, allHeader;
    String sortBy;
    static long min_value = 0, max_value = 0;
    int leftvalue = 0, rightValue = 0;
    public int FILTER_REQUEST = 105;
    private boolean is_selected = false, selected_range = false;
    private static String selected_value = "";
    String[] sortfilter = new String[]{"0", "0", "", "", ""};
    SearchView searchView;
    int prev_sort = 4;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    TextView orderStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.mycatalog, container, false);
        View view = inflater.inflate(R.layout.mycatalog, null);
        catTo = helper.getDefaults("company_id", getActivity());
        setHasOptionsMenu(true);
        iniStuff(view);
        progressStuff();

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                int row_id = cursor.getColumnIndexOrThrow(AlteredCatalog._ID);
                int prod_id = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PRODUCT_ID);
                int error_flag = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_ERROR_FLAG);
                int error_message = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_ERROR_MESSAGE);
                Bundle bundle = new Bundle();
                bundle.putString("row_id", cursor.getString(row_id));
                bundle.putString("prod_id", cursor.getString(prod_id));
                bundle.putString("error_flag", cursor.getString(error_flag));
                bundle.putString("error_message", cursor.getString(error_message));
                startActivity(new Intent(getActivity(), EditDraftProduct.class).putExtra("result", bundle));
            }
        });

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
                    if (parent.isGroupExpanded(groupPosition)) {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
                    } else {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
                    }


                    ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                    Log.i("MPF", "items.title *----*" + items.title);
                    if (items.children.size() == 0) {
                        closeDropdown();

                        sortfilter[4] = items.id;
                        if (items.title.contains("(")) {
                            categoryLabel.setText(items.title.substring(0, items.title.indexOf("(")));
                        } else {
                            categoryLabel.setText(items.title);
                        }
                        adapter.getFilter().filter(sortBy);
                        Log.i("MPF", "items.id *----*" + items.id);
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

                            closeDropdown();

                            sortfilter[4] = items.id;
                            if (items.title.contains("(")) {
                                categoryLabel.setText(items.title.substring(0, items.title.indexOf("(")));
                            } else {
                                categoryLabel.setText(items.title);
                            }
                            adapter.getFilter().filter(sortBy);
                            Log.i("MPF", "items.id *----*" + items.id);

                            return false;
                        }
                    });

                }
                return false;
            }
        });
        return view;
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

    private void progressStuff() {
        cd = new ConnectionDetector(getActivity());
        parser = new JSONParser();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
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
        // Log.i("DRADFRs","CALLEd");
        adapter.getFilter().filter("a");
    }

    private void iniStuff(View view) {
        viewPager = (ViewPager) getActivity().findViewById(R.id.catpager);
        orderStatus = (TextView) view.findViewById(R.id.catalog_record_status);
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        linearBottom = (LinearLayout) view.findViewById(R.id.obottombar);
        sortBy = AlteredCatalog.COLUMN_UPDATED + " desc";
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
        allCategory.setVisibility(View.GONE);
        listView.addHeaderView(view2, null, false);

        adapter = new MyDraftAdapter(this.getActivity(), null, this.getActivity());
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                //Log.i("Calling filter -", sortfilter[0] + "/" + sortfilter[1] + "/" + sortfilter[2] + "/" + sortfilter[3] + "/" + sortfilter[4]);
                if (sortfilter[4].equalsIgnoreCase("")) {

                    if (!search_string.equalsIgnoreCase("")) {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0"))) {
                            // Log.i("Calling filter -", "1");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //   Log.i("Calling filter -", "2");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], sortfilter[2], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                //  Log.i("Calling filter -", "3");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], "5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            // Log.i("Calling filter -", "4");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //  Log.i("Calling filter -", "5");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[2], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                // Log.i("Calling filter -", "6");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", "5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        }

                    } else {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

                        {
                            //  Log.i("Calling filter -", "7");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                // Log.i("Calling filter -", "8");

                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[0], sortfilter[1], sortfilter[2], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                // Log.i("Calling filter -", "9");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[0], sortfilter[1], "5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            // Log.i("Calling filter -", "10");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                //  Log.i("Calling filter -", "11");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[2], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                // Log.i("Calling filter -", "12");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        }
                    }
                } else {

                    if (!search_string.equalsIgnoreCase("")) {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

                        {
                            Log.i("Calling filter -", "13");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                Log.i("Calling filter -", "14");

                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], sortfilter[2], sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                Log.i("Calling filter -", "15");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[0], sortfilter[1], sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            Log.i("Calling filter -", "16");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                Log.i("Calling filter -", "17");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and" + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[2], sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                Log.i("Calling filter -", "18");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_TITLE + " LIKE ? and " + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"%" + search_string + "%", sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        }

                    } else {
                        if (sortfilter[2].contains(","))
                            sortfilter[2] = "";
                        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0")))

                        {
                            Log.i("Calling filter -", "19");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                Log.i("Calling filter -", "20");

                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[0], sortfilter[1], sortfilter[2], sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                Log.i("Calling filter -", "21");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ?  and " + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[0], sortfilter[1], sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            }
                        } else {
                            Log.i("Calling filter -", "22");
                            if (!sortfilter[2].equalsIgnoreCase("")) {
                                Log.i("Calling filter -", "23");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?)  and " + AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[2], sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
                            } else {
                                Log.i("Calling filter -", "24");
                                return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_CATEGORY_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[4], "5", "0"}, String.valueOf(sortfilter[3]));
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
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_desc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_sort = 2;
                        sortBy = AlteredCatalog.COLUMN_SP + " + 0 desc";
                        sortfilter[3] = sortBy;
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_sort = 3;

                        sortBy = " (CASE   when " + AlteredCatalog.COLUMN_TITLE + "*1=0 THEN 0 ELSE 1 END ) asc , " + AlteredCatalog.COLUMN_TITLE + " COLLATE NOCASE";
                        sortfilter[3] = sortBy;
                        adapter.getFilter().filter(sortBy);
                        popupWindow.dismiss();

                    }
                });
                view.findViewById(R.id.sort_recent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_sort = 4;
                        sortBy = AlteredCatalog.COLUMN_UPDATED + " desc";
                        sortfilter[3] = sortBy;
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

                JSONObject price_object = new JSONObject();
                try {
                    price_object.put("myslider", true);
                    price_object.put("filter", "Price");
                    price_object.put("filter_id", "1");
                    price_object.put("min", min_value);
                    price_object.put("max", max_value);
                    price_object.put("selected_range", selected_range);
                    price_object.put("left", leftvalue);
                    price_object.put("right", rightValue);
                    //   Log.i("filter--", "min_value" + min_value +"/" +  "max_value" + max_value +"/" + "left" + leftvalue +"/" + "right" + rightValue +"/" );

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
                    Log.i("Filter==", Pre_Filter_Array.toString());
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


    }


    public void onBackPressed() {

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
                try {
                    JSONObject resultobject = new JSONObject(data.getStringExtra("result"));
                    Iterator<?> keys = resultobject.keys();

                    is_selected = false;
                    if (resultobject.length() == 0) {
                        rightValue = leftvalue = 0;
                        sortfilter[4] = "";
                        selected_value = "";
                        sortfilter[0] = String.valueOf(leftvalue);
                        sortfilter[1] = String.valueOf(rightValue);
                        sortfilter[2] = selected_value;
                        adapter.getFilter().filter("a");
                    } else {
                        while (keys.hasNext()) {
                            int key = Integer.parseInt((String) keys.next());
                            JSONArray jsonArray = resultobject.getJSONArray(String.valueOf(key));

                            if (key == 3) {

                                leftvalue = jsonArray.getInt(0);
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
                        sortfilter[0] = String.valueOf(leftvalue);
                        sortfilter[1] = String.valueOf(rightValue);
                        sortfilter[2] = selected_value;
                        sortfilter[3] = sortBy;
                        if (leftvalue == 0 && rightValue == 0)
                            selected_range = false;
                        else
                            selected_range = true;
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
        if (!(sortfilter[0].equalsIgnoreCase("0") && sortfilter[1].equalsIgnoreCase("0"))) {
            if (!sortfilter[2].equalsIgnoreCase("")) {
                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <= ? and " + AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[0], sortfilter[1], sortfilter[2], "5", "0"}, String.valueOf(sortfilter[3]));
            } else {
                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_SP + " >= ? and " + AlteredCatalog.COLUMN_SP + " <=fcd ? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[0], sortfilter[1], "5", "0"}, String.valueOf(sortfilter[3]));
            }
        } else {
            if (!sortfilter[2].equalsIgnoreCase("")) {
                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_VISIBILTY + " COLLATE NOCASE  in  (?) and " + AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{sortfilter[2], "5", "0"}, String.valueOf(sortfilter[3]));
            } else {
                return new CursorLoader(getActivity(), MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) ", new String[]{"5", "0"}, String.valueOf(sortfilter[3]));
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
            int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
            int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ObjectItems root = new ObjectItems();
                root.id = cursor.getString(iId);
                root.children = new ArrayList<>();
                Cursor cursorParent = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, null, MyCategoryTable.KEY_PARENT_ID + "=?", new String[]{cursor.getString(iId)}, MyCategoryTable.KEY_CATEGORY_NAME + " ASC");
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
                                //  Log.i("ALLSELELRS *", "cursorGrandChild.getString(iNameGrandChild) " + cursorGrandChild.getString(iNameGrandChild) + itemsChildCount);

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


    public class MyDraftAdapter extends CursorAdapter implements Filterable {

        Context contxt;
        Activity activity;
        private LayoutInflater inflater = null;
        public ListLoader imageLoader;
        ConnectionDetector cd;
        JSONParser parser;
        SessionManager session;

        public String company_id, username, TAG = "MyDraftAdapter";
        android.app.AlertDialog.Builder alertDialog;

        public MyDraftAdapter(Context context, Cursor cursor, Activity activity) {
            super(context, cursor, 0);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ListLoader(context);
            this.contxt = context;
            this.activity = activity;

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(activity).inflate(R.layout.edi_catalog_listlayout, parent, false);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            cursor.setNotificationUri(activity.getContentResolver(), MyContentProvider.CONTENT_URI_FEED);
            final ViewHolder holder = createViewHolder(view);
            final int title = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE);
            int code = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CODE);
            int moq = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MINQTY);
            int mrp = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MRP);
            int sp = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_SP);
            int qty = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_QTY);

            int imagepath = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_THUMB_PATH);
            int request_status = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_REQUEST_STATUS);
            int error = cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_ERROR_FLAG);
            holder.moq.setText("MOQ : " + BigDecimal.valueOf(cursor.getInt(moq)).toPlainString());
            holder.title.setText(MyTextUtils.toTitleCase(cursor.getString(title)));
            holder.code.setText("Product Code : " + cursor.getString(code));
            holder.mrp.setText("MRP :" + context.getResources().getString(R.string.rs) + " " + BigDecimal.valueOf(cursor.getDouble(mrp)).toPlainString() + "/pc");
            holder.sp.setText("SP :" + context.getResources().getString(R.string.rs) + " " + BigDecimal.valueOf(cursor.getDouble(sp)).toPlainString() + "/pc");
            holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (cursor.getLong(qty) < cursor.getLong(moq)) {

                holder.qty.setVisibility(View.GONE);
            } else {

                holder.qty.setVisibility(View.VISIBLE);
                holder.qty.setText("Qty : " + cursor.getString(qty));
            }


            if (cursor.getString(imagepath) != null) {
                if (cursor.getString(imagepath).contains("http")) {
                    imageLoader.DisplayImage2(cursor.getString(imagepath), holder.thumb_image, R.drawable.default_product);
                } else if (cursor.getString(imagepath).equalsIgnoreCase("")) {
                    holder.thumb_image.setImageResource(R.drawable.default_product);
                } else {

                    holder.thumb_image.setImageBitmap(helper.decodeSampledBitmapFromResource(cursor.getString(imagepath), 100, 100));

                }
            } else {
                holder.thumb_image.setImageResource(R.drawable.default_product);
            }
            holder.edit_image.setVisibility(View.GONE);
            holder.status_image.setVisibility(View.VISIBLE);
            if (!cursor.getString(request_status).equalsIgnoreCase("2")) {
                if (cursor.getString(error) != null) {
                    if (cursor.getString(error).equalsIgnoreCase("true")) {
                        holder.status_image.setImageResource(R.drawable.error);
                    }
                } else {
                    holder.status_image.setImageResource(R.drawable.upload);
                }
            } else {
                holder.status_image.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }


        private class ViewHolder {
            TextView title, code, mrp, sp, moq, out_stock, status, qty;
            ImageView thumb_image, edit_image, status_image;

            private ViewHolder() {
            }
        }

        private ViewHolder createViewHolder(View view) {
            ViewHolder viewholder = new ViewHolder();
            viewholder.title = (TextView) view.findViewById(R.id.txtTitleAttach); // title
            viewholder.code = (TextView) view.findViewById(R.id.txtCodeAttech); // artist name
            viewholder.mrp = (TextView) view.findViewById(R.id.txtMRPAttech); // duration
            viewholder.moq = (TextView) view.findViewById(R.id.txtmoq);
            viewholder.status = (TextView) view.findViewById(R.id.mycat_status);
            viewholder.qty = (TextView) view.findViewById(R.id.mycat_qty);
            viewholder.sp = (TextView) view.findViewById(R.id.txtsp); // duration
            viewholder.thumb_image = (ImageView) view.findViewById(R.id.my_product_thumb); // thumb image
            viewholder.edit_image = (ImageView) view.findViewById(R.id.edititem);
            viewholder.status_image = (ImageView) view.findViewById(R.id.edititem2);
            viewholder.out_stock = (TextView) view.findViewById(R.id.mycat_out_of_stock);
            return viewholder;
        }


    }


}