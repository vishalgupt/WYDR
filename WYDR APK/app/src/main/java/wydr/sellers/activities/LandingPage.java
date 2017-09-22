package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.adapter.SlidingImage_Adapter;
import wydr.sellers.gson.CreateOrder;
import wydr.sellers.modal.BannerModel;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.UserFunctions;


public class LandingPage extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
    private static ViewPager mPager;
    private static int currentPage = 0;
    RecyclerView recyclerView,recyclerView2,recyclerView3,recyclerView4;
    JSONObject json,json2;
    android.support.v4.app.FragmentManager fm;
    SlidingImage_Adapter slidingImage_adapter;
    Fragment fr;
    JSONObject jsonObject,jsonObject2;
    UserFunctions userFunctions = new UserFunctions();
    RecyclerView.LayoutManager layoutManager,layoutManager2,layoutManager3,layoutManager4;
    private static int NUM_PAGES = 0;
    private ProgressDialog pDialog;
    ScrollView scrollView;
    private ArrayList<BannerModel> newsList = new ArrayList<BannerModel>();
    private ArrayList<BannerModel> newsList2 = new ArrayList<BannerModel>();
    private ArrayList<BannerModel> newsList3 = new ArrayList<BannerModel>();
    private ArrayList<BannerModel> newsList4 = new ArrayList<BannerModel>();
    private ArrayList<BannerModel> banerlist = new ArrayList<BannerModel>();
    private OrderStatusAdapter adapter,adapter2,adapter3,adapter4;
    private Button b;
    SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout fl;
    String lat ="";
    String lon ="";
    LinearLayout ll0,ll1,ll2,ll3;
    TextView txt0,txt1,txt2,txt3;
    Helper helper = new Helper();
    PrefManager prefManager;
    boolean i = false;
    ProgressDialog progressDialog;
    ImageView left_icon, right_icon, left2, right2, left3, right3, left4, right4;
    long startTime = 0;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        startTime = System.currentTimeMillis();
        /******************** ISTIAQUE ********************************************/
        view = inflater.inflate(R.layout.fragment_landing_page, container, false);
        /**************************************************************************/
        return view;
    }

    public String addemail()
    {
     //   String email = Build.MODEL+","+Build.DEVICE+","+Build.VERSION.RELEASE+","+Build.BRAND;
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

       /*Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");
        for (Account account : accounts) {
            Log.d("email",account.name);
            if (emailPattern.matcher(accounts[0].name).matches()) {
                email = accounts[0].name;

            }
        }*/


        return version;

    }

    private CreateOrder order()
    {
        TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        CreateOrder create;

        create = new CreateOrder(lat,
                    lon, Build.MODEL,Build.VERSION.RELEASE,Build.BRAND,helper.getApp_Version(getActivity()),telephonyManager.getDeviceId(),helper.getDefaults("user_id", getActivity()));

        Gson g = new Gson();
        String jsonString = g.toJson(create);
        Log.d("JSONing ", jsonString.toString());
        return create;
    }

    public void bannersrestclient()
    {
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.getbanner(helper.getDefaults("user_id", getActivity()), order(), helper.getB64Auth(getActivity()), "application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response response) {

                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.d("retrofit", jsonObject.getJSONObject("category0").toString());
                        parseJSONFeed2(jsonObject);
                        init();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

                swipeRefreshLayout.setRefreshing(false);
                Log.d("Here", t.toString());

            }
        });

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        final ConnectionDetector connectionDetector = new ConnectionDetector(getActivity());
       // Toast.makeText(getActivity(),addemail(),Toast.LENGTH_SHORT).show();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        scrollView = (ScrollView)view.findViewById(R.id.sv29);
        //bannersrestclient();
        swipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if (connectionDetector.isConnectingToInternet()) {
                    bannersrestclient();
                    // new setAdapterTask().execute();
                    // new UF().execute();
                    initswipefragment();
                } else {
                    Toast.makeText(getActivity(), "Please Check your network connection", Toast.LENGTH_LONG).show();
                }

            }
        });
        swipeRefreshLayout.setEnabled(false);


        prefManager = new PrefManager(getActivity());
        b=(Button)view.findViewById(R.id.catagory);
        ll0 = (LinearLayout)view.findViewById(R.id.ll0);
        ll1 = (LinearLayout)view.findViewById(R.id.ll1);
        ll2 = (LinearLayout)view.findViewById(R.id.ll2);
        ll3 = (LinearLayout)view.findViewById(R.id.ll3);
        txt0 = (TextView)view.findViewById(R.id.txt1);
        txt1 = (TextView)view.findViewById(R.id.txt2);
        txt2 = (TextView)view.findViewById(R.id.txt3);
        txt3 = (TextView)view.findViewById(R.id.txt4);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);// ISTIAQUE
        recyclerView2 = (RecyclerView) view.findViewById(R.id.my_recycler_view2);
        recyclerView2.setHasFixedSize(true);// ISTIAQUE
        recyclerView3 = (RecyclerView) view.findViewById(R.id.my_recycler_view3);
        recyclerView3.setHasFixedSize(true);// ISTIAQUE
        recyclerView4 = (RecyclerView) view.findViewById(R.id.my_recycler_view4);
        recyclerView4.setHasFixedSize(true);// ISTIAQUE
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        fl = (FrameLayout)view.findViewById(R.id.swipecontainer);
        adapter = new OrderStatusAdapter(newsList);
        adapter2 = new OrderStatusAdapter(newsList2);
        adapter3 = new OrderStatusAdapter(newsList3);
        adapter4 = new OrderStatusAdapter(newsList4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        /****************************************** ISTIAQUE: CODE BEGINS ********************************/
        adapter.notifyDataSetChanged();
        //recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
        /****************************************** ISTIAQUE: CODE ENDS **********************************/

        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(adapter2);

        /****************************************** ISTIAQUE: CODE BEGINS ********************************/
        adapter2.notifyDataSetChanged();
        //recyclerView2.smoothScrollToPosition(recyclerView2.getAdapter().getItemCount());
        /****************************************** ISTIAQUE: CODE ENDS **********************************/

        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setAdapter(adapter3);

        /****************************************** ISTIAQUE: CODE BEGINS ********************************/
        adapter3.notifyDataSetChanged();
        //recyclerView3.smoothScrollToPosition(recyclerView3.getAdapter().getItemCount());
        /****************************************** ISTIAQUE: CODE ENDS **********************************/

        recyclerView4.setLayoutManager(layoutManager4);
        recyclerView4.setAdapter(adapter4);

        /****************************************** ISTIAQUE: CODE BEGINS ********************************/
        adapter4.notifyDataSetChanged();
       // recyclerView4.smoothScrollToPosition(recyclerView4.getAdapter().getItemCount());
        /****************************************** ISTIAQUE: CODE ENDS **********************************/

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /****************************************** ISTIAQUE: CODE BEGINS ********************************/
        left_icon = (ImageView) view.findViewById(R.id.left);
        right_icon = (ImageView) view.findViewById(R.id.right);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                View firstVisibleView = recyclerView.getChildAt(0);
                int firstVisiblePosition = recyclerView.getChildPosition(firstVisibleView);
                int visibleRange = recyclerView.getChildCount();
                int lastVisiblePosition = firstVisiblePosition + visibleRange;
                int itemCount = recyclerView.getAdapter().getItemCount();
                int position;
                if (firstVisiblePosition == 0)
                {
                    position = 0;
                    //right_icon.setVisibility(View.VISIBLE);
                }

                else if (lastVisiblePosition == itemCount - 1)
                {
                    position = itemCount - 1;
                    //right_icon.setVisibility(View.GONE);
                    Log.d("Last", "Last");
                }

                else
                {
                    position = firstVisiblePosition;
                }

                if ((lastVisiblePosition == itemCount) && (itemCount == visibleRange))
                {
                    left_icon.setVisibility(View.GONE);
                    right_icon.setVisibility(View.GONE);
                }

                else
                {
                    if (firstVisiblePosition == 0)
                    {
                        left_icon.setVisibility(View.GONE);
                        right_icon.setVisibility(View.VISIBLE);
                    }

                    else if (lastVisiblePosition == itemCount){
                        left_icon.setVisibility(View.VISIBLE);
                        right_icon.setVisibility(View.GONE);
                    } else {
                        left_icon.setVisibility(View.VISIBLE);
                        right_icon.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        left2 = (ImageView) view.findViewById(R.id.left2);
        right2 = (ImageView) view.findViewById(R.id.right2);

        recyclerView2.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View firstVisibleView = recyclerView.getChildAt(0);
                int firstVisiblePosition = recyclerView.getChildPosition(firstVisibleView);
                int visibleRange = recyclerView.getChildCount();
                int lastVisiblePosition = firstVisiblePosition + visibleRange;
                int itemCount = recyclerView.getAdapter().getItemCount();
                int position;
                if (firstVisiblePosition == 0)
                {
                    position = 0;
                    //right_icon.setVisibility(View.VISIBLE);
                }

                else if (lastVisiblePosition == itemCount - 1)
                {
                    position = itemCount - 1;
                    //right_icon.setVisibility(View.GONE);
                    Log.d("Last", "Last");
                }

                else
                {
                    position = firstVisiblePosition;
                }

                if ((lastVisiblePosition == itemCount) && (itemCount == visibleRange)){
                    left2.setVisibility(View.GONE);
                    right2.setVisibility(View.GONE);
                } else {
                    if (firstVisiblePosition == 0){
                        left2.setVisibility(View.GONE);
                        right2.setVisibility(View.VISIBLE);
                        Log.d("Position1", firstVisiblePosition+"");
                    } else if (lastVisiblePosition == itemCount){
                        left2.setVisibility(View.VISIBLE);
                        right2.setVisibility(View.GONE);
                        Log.d("Position2", lastVisiblePosition + "");
                    } else {
                        left2.setVisibility(View.VISIBLE);
                        right2.setVisibility(View.VISIBLE);
                        Log.d("Position3", position + "");
                    }
                }
                Log.d("Position4", visibleRange+"");

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        left3 = (ImageView) view.findViewById(R.id.left3);
        right3 = (ImageView) view.findViewById(R.id.right3);


        recyclerView3.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View firstVisibleView = recyclerView.getChildAt(0);
                int firstVisiblePosition = recyclerView.getChildPosition(firstVisibleView);
                int visibleRange = recyclerView.getChildCount();
                int lastVisiblePosition = firstVisiblePosition + visibleRange;
                int itemCount = recyclerView.getAdapter().getItemCount();
                int position;
                if (firstVisiblePosition == 0) {
                    position = 0;
                    //right_icon.setVisibility(View.VISIBLE);
                } else if (lastVisiblePosition == itemCount - 1) {
                    position = itemCount - 1;
                    //right_icon.setVisibility(View.GONE);
                    Log.d("Last", "Last");
                }

                else
                {
                    position = firstVisiblePosition;
                }

                if ((lastVisiblePosition == itemCount) && (itemCount == visibleRange)){
                    left3.setVisibility(View.GONE);
                    right3.setVisibility(View.GONE);
                }

                else {
                    if (firstVisiblePosition == 0){
                        left3.setVisibility(View.GONE);
                        right3.setVisibility(View.VISIBLE);
                    } else if (lastVisiblePosition == itemCount){
                        left3.setVisibility(View.VISIBLE);
                        right3.setVisibility(View.GONE);
                    } else {
                        left3.setVisibility(View.VISIBLE);
                        right3.setVisibility(View.VISIBLE);
                    }

                }


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        left4 = (ImageView) view.findViewById(R.id.left4);
        right4 = (ImageView) view.findViewById(R.id.right4);

        recyclerView4.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View firstVisibleView = recyclerView.getChildAt(0);
                int firstVisiblePosition = recyclerView.getChildPosition(firstVisibleView);
                int visibleRange = recyclerView.getChildCount();
                int lastVisiblePosition = firstVisiblePosition + visibleRange;
                int itemCount = recyclerView.getAdapter().getItemCount();
                int position;
                if (firstVisiblePosition == 0) {
                    position = 0;
                    //right_icon.setVisibility(View.VISIBLE);
                } else if (lastVisiblePosition == itemCount - 1) {
                    position = itemCount - 1;
                    //right_icon.setVisibility(View.GONE);
                    Log.d("Last", "Last");
                }

                else
                {
                    position = firstVisiblePosition;
                }

                if ((lastVisiblePosition == itemCount) && (itemCount == visibleRange)){
                    left4.setVisibility(View.GONE);
                    right4.setVisibility(View.GONE);
                }

                else {
                    if (firstVisiblePosition == 0){
                        left4.setVisibility(View.GONE);
                        right4.setVisibility(View.VISIBLE);
                    } else if (lastVisiblePosition == itemCount){
                        left4.setVisibility(View.VISIBLE);
                        right4.setVisibility(View.GONE);
                    } else {
                        left4.setVisibility(View.VISIBLE);
                        right4.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        /****************************************** ISTIAQUE: CODE ENDS ********************************/


        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!i)
                {
                    fl.setVisibility(View.VISIBLE);
                   // swipeRefreshLayout.setEnabled(false);
                    b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp, 0);
                    i = true;

                }

                else
                {
                    fl.setVisibility(View.GONE);
                    //swipeRefreshLayout.setEnabled(true);
                    b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_white_24dp, 0);
                    i = false;
                }

                //Intent intent = new Intent(getActivity(), CatContainer.class);
                //startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });


    }

    public void initswipefragment()
    {
        fm = getChildFragmentManager();
        fr = new MarketPlace();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.swipecontainer, fr);

        //will execute before activity saves its state
        //fragmentTransaction.commit();

        //allows the commit to be executed after an activity's state is saved
        fragmentTransaction.commitAllowingStateLoss();


        /*******************************ISTIAQUE***************************************/
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        application.trackEvent("All Categories home page", "onClick", "MarketPlace");
        /*******************************ISTIAQUE***************************************/
    }

    /*private void parseJSONFeed(JSONObject response)
    {
        newsList.clear();
        try
        {
            JSONArray jsonArray = response.getJSONArray("details");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                ItemList28 movie = new ItemList28();
                movie.setItemName(obj.getString("item_name"));
                movie.setItemCatagory(obj.getString("item_id"));
                movie.setPhoto(obj.getString("photos"));
                newsList.add(movie);
            }

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

    }*/

    private void parseJSONFeed2(JSONObject response)
    {
        banerlist.clear();
        try
        {
            JSONArray jsonArray = response.getJSONArray("banners");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                BannerModel movie = new BannerModel();
                movie.setProduct_id((obj.getString("url").substring(1)));
                char a= obj.getString("url").charAt(0);
                movie.setProductType(String.valueOf(a));
                movie.setProductName(obj.getString("banner"));
                JSONObject obj2 = obj.getJSONObject("main_pair");
                JSONObject oj3 = obj2.getJSONObject("icon");
                movie.setBanner_photo(oj3.getString("http_image_path"));
                banerlist.add(movie);
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            newsList.clear();
            JSONObject obj = response.getJSONObject("category0");
            txt0.setText(obj.getString("category"));
            if(!obj.has("products"))
            {
                ll0.setVisibility(View.GONE);
            }
            else
            {
                // movie.setCat_title(obj.getString("category"));
                JSONArray arr = obj.getJSONArray("products");
                for (int j = 0; j < arr.length(); j++)
                {
                    BannerModel movie = new BannerModel();
                    JSONObject obj2 = (JSONObject) arr.get(j);
                    movie.setProduct_id(obj2.getString("product_id"));
                    if(obj2.has("discount_price"))
                    {
                        movie.setDiscounted_price(String.valueOf(obj2.getInt("discount_price")));
                        Log.d("DiscountedPrice", String.valueOf(obj2.getInt("discount_price")));
                        JSONArray jsonArray = obj2.getJSONArray("cartpromotions");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject obj12 = (JSONObject) jsonArray.get(i);
                            movie.setDiscount_String(obj12.getString("ddiscount_string"));
                        }
                        //movie.setDiscount_String(obj2.getJSONArray("cartpromotions").getString("ddiscount_string"));
                    }
                    else
                    {
                        movie.setDiscounted_price("0");
                    }
                    //movie.setProduct_com(obj2.getString("company_name"));
                    movie.setProductName(obj2.getString("product"));
                    movie.setProduct_mrp(obj2.getString("list_price"));
                    movie.setProduct_price(obj2.getString("price"));
                    movie.setMOQa(obj2.getString("min_qty"));
                    if (!obj2.getString("images").equalsIgnoreCase(""))
                    {
                        JSONObject oj3 = obj2.getJSONObject("images");
                        JSONObject oj4 = oj3.getJSONObject("detailed");
                        movie.setBanner_photo(oj4.getString("http_image_path"));
                    }
                    newsList.add(movie);
                }
            }

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

        try
        {
            newsList2.clear();
            JSONObject obj = response.getJSONObject("category1");
            txt1.setText(obj.getString("category"));
            if(!obj.has("products"))
            {
                ll1.setVisibility(View.GONE);
            }
            else
            {
                // movie.setCat_title(obj.getString("category"));
                JSONArray arr = obj.getJSONArray("products");
                for (int k = 0; k < arr.length(); k++)
                {
                    BannerModel movie = new BannerModel();
                    JSONObject obj2 = (JSONObject) arr.get(k);
                    if(obj2.has("discount_price"))
                    {
                        movie.setDiscounted_price(obj2.getString("discount_price"));
                        movie.setDiscount_String(obj2.getJSONObject("cartpromotions").getString("ddiscount_string"));
                    }
                    else
                    {
                        movie.setDiscounted_price("0");
                    }

                    movie.setProduct_id(obj2.getString("product_id"));
                   // movie.setProduct_com(obj2.getString("company_name"));
                    movie.setProductName(obj2.getString("product"));
                    movie.setProduct_price(obj2.getString("price"));
                    movie.setProduct_mrp(obj2.getString("list_price"));
                    movie.setMOQa(obj2.getString("min_qty"));
                    if (!obj2.getString("images").equalsIgnoreCase(""))
                    {
                        JSONObject oj3 = obj2.getJSONObject("images");
                        JSONObject oj4 = oj3.getJSONObject("detailed");
                        movie.setBanner_photo(oj4.getString("http_image_path"));
                    }
                    newsList2.add(movie);
                }
            }

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        adapter2.notifyDataSetChanged();


        try
        {
            newsList3.clear();
            JSONObject obj = response.getJSONObject("category2");
            txt2.setText(obj.getString("category"));
            if(!obj.has("products"))
            {
                ll2.setVisibility(View.GONE);
            }
            else
            {
                // movie.setCat_title(obj.getString("category"));
                JSONArray arr = obj.getJSONArray("products");
                for (int l = 0; l < arr.length(); l++)
                {
                    BannerModel movie = new BannerModel();
                    JSONObject obj2 = (JSONObject) arr.get(l);
                    if(obj2.has("discount_price"))
                    {
                        movie.setDiscounted_price(obj2.getString("discount_price"));
                        movie.setDiscount_String(obj2.getJSONObject("cartpromotions").getString("ddiscount_string"));
                    }
                    else
                    {
                        movie.setDiscounted_price("0");
                    }
                    movie.setProduct_id(obj2.getString("product_id"));
                    //movie.setProduct_com(obj2.getString("company_name"));
                    movie.setProduct_mrp(obj2.getString("list_price"));
                    movie.setProductName(obj2.getString("product"));
                    movie.setProduct_price(obj2.getString("price"));
                    movie.setMOQa(obj2.getString("min_qty"));
                    if (!obj2.getString("images").equalsIgnoreCase(""))
                    {
                        JSONObject oj3 = obj2.getJSONObject("images");
                        JSONObject oj4 = oj3.getJSONObject("detailed");
                        movie.setBanner_photo(oj4.getString("http_image_path"));
                    }
                    newsList3.add(movie);
                }

            }

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        adapter3.notifyDataSetChanged();


        try
        {
            newsList4.clear();
            JSONObject obj = response.getJSONObject("category3");
            txt3.setText(obj.getString("category"));
            if(!obj.has("products"))
            {
                ll3.setVisibility(View.GONE);
            }
            else
            {
                // movie.setCat_title(obj.getString("category"));
                JSONArray arr = obj.getJSONArray("products");
                for (int m = 0; m < arr.length(); m++)
                {
                    BannerModel movie = new BannerModel();
                    JSONObject obj2 = (JSONObject) arr.get(m);

                    if(obj2.has("discount_price"))
                    {
                        movie.setDiscounted_price(obj2.getString("discount_price"));
                        movie.setDiscount_String(obj2.getJSONObject("cartpromotions").getString("ddiscount_string"));
                    }
                    else
                    {
                        movie.setDiscounted_price("0");
                    }

                    movie.setProduct_id(obj2.getString("product_id"));
                    //movie.setProduct_com(obj2.getString("company_name"));
                    movie.setProductName(obj2.getString("product"));
                    movie.setProduct_mrp(obj2.getString("list_price"));
                    movie.setProduct_price(obj2.getString("price"));
                    movie.setMOQa(obj2.getString("min_qty"));
                    if (!obj2.getString("images").equalsIgnoreCase(""))
                    {
                        JSONObject oj3 = obj2.getJSONObject("images");
                        JSONObject oj4 = oj3.getJSONObject("detailed");
                        movie.setBanner_photo(oj4.getString("http_image_path"));
                    }
                    newsList4.add(movie);

                }

            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        adapter4.notifyDataSetChanged();

        //new SlidingImage_Adapter(getActivity(),newsList).notifyDataSetChanged();

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog()
    {
        if (pDialog != null)
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onRefresh() {

        bannersrestclient();

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onResume() {
        super.onResume();
        //init();

        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("LandingPage");
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        //mTracker.setSampleRate(100.0);
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("LandingPage").setValue(System.currentTimeMillis() - startTime)
                .setVariable("Screen Load").setLabel("Load Time").build());

        GoogleAnalytics.getInstance(getActivity()).setLocalDispatchPeriod(1800);
    }

    private class setAdapterTask extends AsyncTask<String,String,String>
    {
        String id;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            //id = helper.getDefaults("user_id", getActivity());
        }

        @Override
        protected String doInBackground(String... strings)
        {
            id = helper.getDefaults("user_id", getActivity());
            //json2 = userFunctions.getBanner(helper.getDefaults("user_id",getActivity()));
            //jsonObject = jsonParser.getJSONFromUrl(AppUtil.URL + "access/" + id);
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            //progressDialog.dismiss();
            //swipeRefreshLayout.setRefreshing(false);
            Log.d("ufff", jsonObject.toString());
            ParseJSonUF(jsonObject);
            //parseJSONFeed2(json2);
           //t init();
           //initswipefragment();
        }
    }



    private void ParseJSonUF(JSONObject jsonObject)
    {
        try
        {
            String r = "";
            JSONArray jsonArray = jsonObject.getJSONArray("privilages");
            for (int i = 0; i < jsonArray.length(); i++)
            {

                JSONObject obj = (JSONObject) jsonArray.get(i);
                String uf = obj.getString("privilage");
                r += uf+",";
                helper.setDefaults(uf+"_photo",obj.getString("image"),getActivity());
                helper.setDefaults(uf+"_tag", obj.getString("url"),getActivity());
            }

            prefManager.getUFString(r);

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }


    private void init()
    {
        mPager = (ViewPager) view.findViewById(R.id.pager28);
        mPager.setOffscreenPageLimit(10);
        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), banerlist);
        slidingImage_adapter.notifyDataSetChanged();
        mPager.setAdapter(slidingImage_adapter);
        CirclePageIndicator indicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES = banerlist.size();

        // Auto start of viewpager
        /*final Handler handler = new Handler();
        final Runnable Update = new Runnable()
        {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 2000);*/

        final Handler handler = new Handler();

        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 6000);

        /*if(currentPage<=5){
            mPager.setCurrentItem(currentPage);
            currentPage++;
        }else{
            currentPage = 0;
            mPager.setCurrentItem(currentPage);
        }*/


        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }


    public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder>
    {
        private ArrayList<BannerModel> orderItemses;

        public OrderStatusAdapter(ArrayList<BannerModel> newsList)
        {
            this.orderItemses = newsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deals_card,parent,false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position)
        {
            final TextView a = holder.name;
            final TextView b = holder.email;
            final ImageView h= holder.img;
            final ImageView c = holder.offer;
            final TextView d = holder.discount;
            final TextView y = holder.comp;
            final TextView z = holder.discount_amt;
            final LinearLayout ll = holder.ll3;
           // final LinearLayout ll7 = holder.ll7;
            z.setVisibility(View.GONE);
            ll.setVisibility(View.GONE);
            c.setVisibility(View.GONE);
            d.setVisibility(View.GONE);

            a.setText("MOQ: " + orderItemses.get(position).getMOQa());
            b.setText("₹ " + String.valueOf((int)Double.parseDouble(orderItemses.get(position).getProduct_price())));
            Picasso.with(getActivity())
                    .load(orderItemses.get(position).getBanner_photo())
                    .resize(200, 180)
                    .onlyScaleDown()
                    .centerInside()
                    .error(R.drawable.default_product)
                    .placeholder(R.drawable.default_product)
                    .into(h);
            //Toast.makeText(getActivity(), orderItemses.get(position).getBanner_photo(), Toast.LENGTH_SHORT).show();
            //BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            /*if(getBitmapFromURL(orderItemses.get(position).getBanner_photo()) != null) {
                h.setImageBitmap(getBitmapFromURL(orderItemses.get(position).getBanner_photo()));
            } else {
                //h.setImageBitmap(R.drawable.default_product);
                h.setImageResource(R.drawable.default_product);
            }*/
            if(!orderItemses.get(position).getDiscounted_price().equalsIgnoreCase("0"))
            {
                z.setVisibility(View.VISIBLE);
                c.setVisibility(View.VISIBLE);
                ll.setVisibility(View.VISIBLE);
                b.setText("₹ " + String.valueOf((orderItemses.get(position).getDiscounted_price())));
                z.setText("₹ " + String.valueOf((int)Double.parseDouble(orderItemses.get(position).getProduct_mrp())));
                d.setVisibility(View.VISIBLE);
                d.setText(orderItemses.get(position).getDiscount_String());
                z.setPaintFlags(z.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else
            {
                z.setVisibility(View.VISIBLE);
                ll.setVisibility(View.VISIBLE);
                z.setText("₹ " + String.valueOf((int) Double.parseDouble(orderItemses.get(position).getProduct_mrp())));
                z.setPaintFlags(z.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            y.setText(orderItemses.get(position).getProductName());

            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    /*******************************ISTIAQUE***************************************/
                    Controller application = (Controller) getActivity().getApplication();
                    Tracker mTracker = application.getDefaultTracker();
                    application.trackEvent("Landing Page", "click", "Product Id: " + orderItemses.get(position).getProduct_id() + " " +
                            orderItemses.get(position).getProductName());
                    /*******************************ISTIAQUE***************************************/
                    Intent intent = new Intent(getActivity(),ProductDetailsActivity.class);
                    intent.putExtra("name",orderItemses.get(position).getProductName());
                    intent.putExtra("product_id",orderItemses.get(position).getProduct_id());
                    intent.putExtra("screenVisited", "Marketplace" + "/" + orderItemses.get(position).getProductName() + "/");
                    startActivity(intent);

                    /*Toast.makeText(getActivity(), "Product Id: " + orderItemses.get(position).getProduct_id() + " " +
                            orderItemses.get(position).getProductName(), Toast.LENGTH_SHORT).show();*/
                }
            });

        }



        @Override
        public int getItemCount()
        {
            return orderItemses.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView name,email,comp,discount,discount_amt;
            ImageView img,offer;
            LinearLayout ll3,ll7;

            public MyViewHolder(View itemView)
            {
                super(itemView);
                this.name = (TextView) itemView.findViewById(R.id.tit28);
                this.email = (TextView) itemView.findViewById(R.id.tita28);
                this.img = (ImageView) itemView.findViewById(R.id.img28);
                this.offer = (ImageView) itemView.findViewById(R.id.offer_pic);
                this.comp = (TextView) itemView.findViewById(R.id.p_com);
                this.discount = (TextView) itemView.findViewById(R.id.tit29);
                this.discount_amt = (TextView) itemView.findViewById(R.id.tit30);
                this.ll3 = (LinearLayout)itemView.findViewById(R.id.llthird);
               // this.ll7 = (LinearLayout)itemView.findViewById(R.id.ll7);
            }
        }
    }

    /*private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }*/
}