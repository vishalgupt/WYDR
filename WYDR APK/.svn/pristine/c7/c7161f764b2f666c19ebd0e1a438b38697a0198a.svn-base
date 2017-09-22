package wydr.sellers.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


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
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.acc.QueryCategoryAdapter;
import wydr.sellers.adapter.QueryAdapter;
import wydr.sellers.gson.QueryHolder;
import wydr.sellers.gson.QueryModal;
import wydr.sellers.modal.CategoryDataModal;
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
 * Created by surya on 7/8/15.
 */
public class QueryFeed extends Fragment implements View.OnClickListener, OnBackPressedListener
{
    static String from, to;
    ListView listView;
    ArrayList<QueryModal> list;
    QueryAdapter adapter;
    ConnectionDetector cd;
    JSONParser parser;
    android.app.AlertDialog.Builder alertDialog;
    ImageView dateFilter;
    String sortOrder, sortBy, catId = "", userId;
    LinearLayout sortButton, category_button, ll;
    Animation animation;
    Button all, my;
    SwipeRefreshLayout swipeRefreshLayout;
    ExpandableListView expListView;
    QueryCategoryAdapter adapter2;
    ObjectItems obj;
    TextView allTab, queryText, order_status;
    int allTabPosition = 0, prev_sort = 1, page_no, FILTER_REQUEST = 106;
    Helper helper = new Helper();
    private boolean isLoading;
    private View loadingFooter;
    private ProgressDialog progress;
    SessionManager session;
    long startTime = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        startTime = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.home_query_feed, container, false);
        progressStuff();
        iniStuff(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initUff();
    }


    private void iniStuff(View view)
    {
        prev_sort = 1;
        animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        sortBy = "created_at";
        sortOrder = "desc";
        page_no = 1;
        isLoading = true;
        ll = (LinearLayout) view.findViewById(R.id.qbottombar);
        order_status = (TextView) view.findViewById(R.id.query_record_status);
        queryText = (TextView) view.findViewById(R.id.txtQueryCategory);
        expListView = (ExpandableListView) view.findViewById(R.id.qexpandableCategory2);
        listView = (ListView) view.findViewById(R.id.listViewHomeFeed);
        dateFilter = (ImageView) view.findViewById(R.id.dateFilter);
        View view2 = View.inflate(getActivity(), R.layout.category_header, null);
        allTab = (TextView) view2.findViewById(R.id.itemRootTitle);
        all = (Button) view2.findViewById(R.id.allCategory);
        my = (Button) view2.findViewById(R.id.myCategory);
        all.setOnClickListener(this);
        allTab.setOnClickListener(this);
        my.setOnClickListener(this);
        dateFilter.setOnClickListener(this);
        loadingFooter = LayoutInflater.from(getActivity()).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        list = new ArrayList<>();
        sortButton = (LinearLayout) view.findViewById(R.id.qsort);
        category_button = (LinearLayout) view.findViewById(R.id.qcategory_button);
        sortButton.setOnClickListener(this);
        category_button.setOnClickListener(this);
        adapter = new QueryAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                this.currentScrollState = scrollState;
                switch (scrollState)
                {
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
                if (isLoading || page_no == 0)
                {
                    return;
                }

                else
                {
                    ll.startAnimation(animation);
                    if ((firstVisibleItem + visibleItemCount) == totalItemCount)
                    {
                        page_no++;
                        prepareRequest(page_no, sortOrder, catId, from, to, sortBy);
                    }
                }
            }
        });
        if (expListView.getHeaderViewsCount() == 0) {
            expListView.addHeaderView(view2, null, false);
        }

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                final ObjectItems items = (ObjectItems) parent.getExpandableListAdapter().getGroup(groupPosition);
                catId = items.id;
                closeDropdown();
                list.clear();
                adapter.notifyDataSetChanged();
                page_no = 1;
                prepareRequest(page_no, sortOrder, catId, from, to, sortBy);
                queryText.setText(items.title);
                return true;
            }
        });
        userId = helper.getDefaults("user_id", getActivity());

        if (cd.isConnectingToInternet()) {

            prepareRequest(page_no, sortOrder, catId, from, to, sortBy);
        } else {
            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
            listView.removeFooterView(loadingFooter);
            adapter.notifyDataSetChanged();
        }
    }

    private void prepareRequest(int pageNo, String sortOrder, String catId, String fromString, String toString, String sortBy)
    {
        isLoading = true;
        order_status.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        if (page_no != 0)
        {
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<QueryHolder> call = service.getFeed(userId, pageNo, sortOrder, sortBy, catId, fromString, toString, helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<QueryHolder>()
            {
                @Override
                public void onResponse(Response response)
                {
                    isLoading = false;

                    listView.removeFooterView(loadingFooter);

                    if (response.isSuccess())
                    {
                        QueryHolder holder = (QueryHolder) response.body();
                        if (holder.getQuery().size() < 10)
                        {
                            page_no = 0;
                        }

                        list.addAll(holder.getQuery());
                        if (page_no == 0)
                        {
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
                                new AlertDialogManager().showAlertDialog(getActivity(),
                                        getString(R.string.error),
                                        getString(R.string.server_error));
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
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
                        new AlertDialogManager().showAlertDialog(getActivity(),
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateFilter:
                startActivityForResult(new Intent(getActivity(), DateActivity.class).putExtra("date_from", from).putExtra("date_to", to), FILTER_REQUEST);
                break;

            case R.id.qcategory_button:
                order_status.setVisibility(View.GONE);
                if (expListView.getVisibility() == View.VISIBLE) {
                    closeDropdown();
                } else {
                    if (expListView.getAdapter() != null) {

                        if (expListView.getAdapter().isEmpty()) {
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
                allTabPosition = 0;
                all.setTextColor(getResources().getColor(R.color.primary_500));
                my.setTextColor(getResources().getColor(R.color.text_color));
                loadCategory();
                break;
            case R.id.myCategory:
                allTabPosition = 1;
                all.setTextColor(getResources().getColor(R.color.text_color));
                my.setTextColor(getResources().getColor(R.color.primary_500));
                if (cd.isConnectingToInternet()) {
                    new GetUserCategory().execute(helper.getDefaults("company_id", getActivity()));
                } else {
                    loadSubCategory();
                }
                break;

            case R.id.itemRootTitle:
                catId = "";
                list.clear();
                adapter.notifyDataSetChanged();
                page_no = 1;
                prepareRequest(page_no, sortOrder, catId, from, to, sortBy);
                closeDropdown();
                queryText.setText("Category");
                break;
            case R.id.qsort:

                final PopupWindow popupWindow = new PopupWindow(getActivity());
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.delete_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.sort_query_name).setVisibility(View.GONE);
                view.findViewById(R.id.sort_need_desc).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sort_recent).setVisibility(View.VISIBLE);
                view.findViewById(R.id.action_recent_image).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.action_need_image).setVisibility(View.INVISIBLE);
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
                        preTaskCall("needed_date", "desc", 2);
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


        }
    }

    private void initUff()
    {
        Helper helper = new Helper();
        ArrayList<String> ufList;
        ImageView iv_bussiness=(ImageView)getActivity().findViewById(R.id.uf_queryfeed);
        RelativeLayout ll= (RelativeLayout)getActivity().findViewById(R.id.gridLayout);
        PrefManager prefManager = new PrefManager(getActivity());
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(),Catalog.class);
                startActivity(intent);
            }
        });
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(),Catalog.class);
                startActivity(intent);
            }
        });
        if(ufList.contains(AppUtil.TAG_Business_Leads))
        {
            ll.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(helper.getDefaults(AppUtil.TAG_Business_Leads+"_photo",getActivity()))
                    .into(iv_bussiness);
        }
        else
        {
            ll.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    private void preTaskCall(String sort, String order, int prev) {
        prev_sort = prev;
        sortBy = sort;
        sortOrder = order;
        list.clear();
        adapter.notifyDataSetChanged();
        page_no = 1;
        prepareRequest(page_no, sortOrder, catId, from, to, sortBy);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {

                from = data.getStringExtra("from");
                to = data.getStringExtra("to");
                list.clear();
                adapter.notifyDataSetChanged();
                page_no = 1;
                prepareRequest(page_no, sortOrder, catId, from, to, sortBy);


            } else {
                list.clear();
                adapter.notifyDataSetChanged();
                page_no = 1;
                from = null;
                to = null;
                prepareRequest(page_no, sortOrder, catId, from, to, sortBy);

            }
        }
    }


    private void openDropdown() {
        order_status.setVisibility(View.GONE);
        if (expListView.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            expListView.startAnimation(anim);
            expListView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);

        }
    }
    //  }

    private void closeDropdown() {

        if (expListView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);

            anim.setDuration(getResources().getInteger(
                    R.integer.dropdown_amination_time));
            expListView.startAnimation(anim);
            expListView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            getActivity().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        }
//
    }


    private void loadCategory() {
        obj = new ObjectItems();
        Cursor cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_Category, null,
                CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, CategoryTable.KEY_CATEGORY_NAME + " ASC");
        int iId = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_ID);
        int iName = cursor.getColumnIndexOrThrow(CategoryTable.KEY_CATEGORY_NAME);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {


            ObjectItems root = new ObjectItems();
            root.id = cursor.getString(iId);
            root.children = new ArrayList<ObjectItems>();
            root.title = cursor.getString(iName) /*+ " (" + itemParent + ")"*/;
            obj.children.add(root);

        }

        cursor.close();
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

                return false;
            }
        };

        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };

        adapter2 = new QueryCategoryAdapter(getActivity(), obj, grpLst, childLst, grpExpLst);
        expListView.setAdapter(adapter2);
        openDropdown();

    }

    private void loadSubCategory() {
        obj = new ObjectItems();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + CategoryTable.TABLE_CONTACTS
                    + " where " + CategoryTable.KEY_PARENT_ID + "=0  and "
                    + CategoryTable.KEY_CATEGORY_ID + " in (select distinct(" + MyCategoryTable.KEY_PARENT_ID + ") from "
                    + MyCategoryTable.TABLE_CONTACTS + ")", null);

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

                return false/* or false depending on what you need */;
            }
        };
        ExpandableListView.OnGroupExpandListener grpExpLst = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        };
        adapter2 = new QueryCategoryAdapter(getActivity(), obj, grpLst, childLst, grpExpLst);
        expListView.setAdapter(adapter2);
        openDropdown();
    }

    public void onBackPressed() {

        if (this.expListView.getVisibility() == View.VISIBLE) {
            closeDropdown();

        } else {
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(getString(R.string.are_u_sure_exit));
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    getActivity().finish();

                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
    }

    private void progressStuff() {
        session = new SessionManager(getActivity().getApplicationContext());
        cd = new ConnectionDetector(getActivity());
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new android.app.AlertDialog.Builder(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.notifyDataSetChanged();
        if (adapter2 != null)
            adapter2.notifyDataSetChanged();
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("My Query");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Business Leads").setValue(System.currentTimeMillis() - startTime).build());
    }



    private class GetUserCategory extends AsyncTask<String, String, JSONObject> {
        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        int flag = 0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!getActivity().isFinishing())
                progress.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params, getActivity());
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
            if ((getActivity() != null) && !getActivity().isFinishing()) {
                progress.dismiss();
                if (flag == 1) {
                    loadSubCategory();
                } else {
                    if (catdata != null) {
                        getActivity().getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
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
                            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);
                            getActivity().getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_MYCATEGORY, null, false);
                            //     Log.i("Helper", "uri at mycatalog " + uri.toString());

                        }

                    }
                    loadSubCategory();
                }
            }
        }
    }

}
