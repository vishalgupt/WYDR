package wydr.sellers.activities;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import wydr.sellers.Become_a_Seller;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ProSchema;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.modal.NavDrawerItem;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.openfire.SmackService;
import wydr.sellers.registration.ContactsSyncService;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.MyDatabaseHelper;
import wydr.sellers.slider.UserFunctions;
import wydr.sellers.syncadap.GenericAccountService;
import wydr.sellers.syncadap.SyncUtils;

/**
 * Created by surya on 13/8/15.
 */
public class Home extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "HOME";
    // slide menu items
    public Toolbar mToolbar;
    ImageView sliderMenu;
    SimpleSideDrawer slider, right_slider;
    ListView items, right_listViewMenu;
    public TextView mCounter;
    private int count = 0;
    Helper helper = new Helper();
    NavDrawerListAdapter sliderAdapter, right_sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems, right_navDrawerItems;
    TabLayout tabLayout;
    ViewPager pager;
    TabsPagerAdapter mAdapter;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    ConnectionDetector cd;
    JSONParser parser;
    LinearLayout overlayLayout, lay1, lay2, lay3, lay4;
    SessionManager session;
    MenuItem addMenu;
    PrefManager prefManager;
    Controller application;
    ArrayList<String>ufList= new ArrayList<>();
    TabWidget tabwidget;
    RelativeLayout headerLogo, right_headerLogo;
    int page_flag = 0;
    int pos;
    AlertDialog.Builder alertDialog;
    ExpandableListView explistView;
    String userid;
    Tracker mTracker;
    Integer[] image_array_root =
            {
                    R.drawable.all_sellers,
                    R.drawable.my_catalog,
                    R.drawable.my_qury,
                    R.drawable.my_fvrt,
                    R.drawable.my_business,
                    R.drawable.my_orders,
                    R.drawable.shared_items,
                    R.drawable.all_sellers,
                    R.drawable.my_profile,
                    R.drawable.invite_users,
                    R.drawable.manage_team,
                    R.drawable.invite_users,
                    R.drawable.wydr_cash_icon,
                    R.drawable.help_icon,
                    R.drawable.all_sellers,
                    0
            };

    Integer[] image_array_sub =
            {
                    R.drawable.my_catalog,
                    R.drawable.my_qury,
                    R.drawable.my_fvrt,
                    R.drawable.my_business,
                    R.drawable.my_orders,
                    R.drawable.shared_items,
                    R.drawable.all_sellers,
                    R.drawable.my_profile,
                    R.drawable.invite_users,
                    0
            };

    static FrameLayout notifCount;
    static int mNotifCount = 0;
    MarketPlace marketPlace;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ImageView tab1, tab2, tab3, tab4;
    private ProgressDialog progress;
    private boolean success = false;
    private TextView name, company;
    //private PendingIntent pendingIntent;
    //private AlarmManager manager;
    private Object mSyncObserverHandle;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /**
         * Callback invoked with the sync adapter status changes.
         */
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by

                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null)
                    {
                        // setRefreshActionButtonState(false);
                        return;
                    }
                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, MyContentProvider.AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, MyContentProvider.AUTHORITY);
                    //setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceivers();
        if (mSyncObserverHandle != null)
        {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        mLayout = (View)findViewById(R.id.home_page);
        Picasso.with(getApplicationContext())
                .load(AppUtil.WalkthroughURL + "/images/walkthrough/wydr_app_popup_steps.png")
                .fetch();
        Calendar updateTime = Calendar.getInstance();
        MyDatabaseHelper db = new MyDatabaseHelper(getApplicationContext());
//       Toast.makeText(getApplicationContext(),db.companyids()
        //             ,Toast.LENGTH_SHORT).show();

        updateTime.set(Calendar.HOUR_OF_DAY, 15);
        updateTime.set(Calendar.MINUTE, 17);
        updateTime.set(Calendar.SECOND, 0);
        Intent downloader = new Intent(this, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(this,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) this.getSystemService(
                Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, recurringDownload);
        cd = new ConnectionDetector(this);
        pos = getIntent().getIntExtra("pos", 0);
        hideSoftKeyboard();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        userid = helper.getDefaults("user_id", Home.this);
        alertDialog = new AlertDialog.Builder(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        progressStuff();
        tabwidget = (TabWidget) findViewById(R.id.tabs);
        //---akshay---///
        prefManager = new PrefManager(this);
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        if (helper.getDefaults("coach_flag", Home.this).equalsIgnoreCase("0"))
        {
            if(ufList.contains(AppUtil.TAG_Network)) {
                akitest();
            }
            helper.setDefaults("coach_flag", "1", Home.this);
        }
        overlayLayout = (LinearLayout) findViewById(R.id.overlayLayout2);
        explistView = (ExpandableListView) findViewById(R.id.qexpandableCategory2);
        OverlayClickListener listener = new OverlayClickListener(this, overlayLayout);
        findViewById(R.id.start_chat).setOnClickListener(listener);
        findViewById(R.id.add_catalog).setOnClickListener(listener);
        findViewById(R.id.add_connection).setOnClickListener(listener);
        findViewById(R.id.submit_query).setOnClickListener(listener);
        findViewById(R.id.close).setOnClickListener(listener);
        findViewById(R.id.create_group).setOnClickListener(listener);
        findViewById(R.id.create_broadcast).setOnClickListener(listener);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle("");
        navStuff();
        getContentResolver()
                .registerContentObserver(
                        ContactsContract.Contacts.CONTENT_URI, true,
                        new MyCOntentObserver());
        PermissionsUtils.verifyStoragePermissions(Home.this);

        if (getContentResolver().query(ChatProvider.NET_URI, null, null, null, null).getCount() > 0)
        {
            if (helper.getDefaults("SyncFlag", Home.this).contentEquals("1")) {
                helper.setDefaults("SyncFlag", "2", Home.this);
                if (Build.VERSION.SDK_INT >= 23) {
                    //do your check here
                    if (PermissionsUtils.verifyContactPermissions(Home.this)) {
                        startService(new Intent(Home.this, ContactsSyncService.class));
                    }
                } else {
                    startService(new Intent(this, ContactsSyncService.class));
                }

            }

        }

        else
        {
            // ;
            if (cd.isConnectingToInternet())
            {
                try {
                    new GetNetwork().execute();
                } catch (OutOfMemoryError e) {
                    Log.e("OutOfMemoryError: ", e.getMessage());
                }
            }

            else
            {
                new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
            }

        }

        if (!isServiceRunning()) {
            Intent intent = new Intent(this, SmackService.class);
            this.startService(intent);
        }

        //Register receivers for push notifications
        registerReceivers();
        //Create and start push manager
        PushManager pushManager = PushManager.getInstance(this);

        //Start push manager, this will count app open for Pushwoosh stats as well
        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
            //push notifications are not available or AndroidManifest.xml is not configured properly
        }

        //Register for push!
        pushManager.registerForPushNotifications();

        checkMessage(getIntent());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        lay1 = (LinearLayout) findViewById(R.id.lay_tab1);
        lay2 = (LinearLayout) findViewById(R.id.lay_tab2);
        lay3 = (LinearLayout) findViewById(R.id.lay_tab3);
        lay4 = (LinearLayout) findViewById(R.id.lay_tab4);
        tab1 = (ImageView) findViewById(R.id.tab_bar_1);
        tab2 = (ImageView) findViewById(R.id.tab_bar_2);
        tab3 = (ImageView) findViewById(R.id.tab_bar_3);
        tab4 = (ImageView) findViewById(R.id.tab_bar_4);
        lay1.setOnClickListener(this);
        lay2.setOnClickListener(this);
        lay3.setOnClickListener(this);
        lay4.setOnClickListener(this);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                switch (position) {
                    case 0:
                        pager.setCurrentItem(0);
                        tabwidget.setVisibility(View.VISIBLE);
                        tab1.setImageResource(R.drawable.chat_unselected);
                        tab2.setImageResource(R.drawable.share_unselected);
                        tab3.setImageResource(R.drawable.shopping_bag_selected);
                        tab4.setImageResource(R.drawable.query_unselected);
                        lay1.setBackgroundResource(R.drawable.bar);
                        lay2.setBackgroundResource(R.drawable.bar);
                        lay3.setBackgroundResource(R.drawable.arrow_bar);
                        lay4.setBackgroundResource(R.drawable.bar);
                        page_flag = 0;
                        break;
                    case 1:
                        pager.setCurrentItem(1);
                        tabwidget.setVisibility(View.VISIBLE);
                        tab1.setImageResource(R.drawable.chat_unselected);
                        tab2.setImageResource(R.drawable.share_selected);
                        tab3.setImageResource(R.drawable.shopping_bag_unselected);
                        tab4.setImageResource(R.drawable.query_unselected);
                        lay1.setBackgroundResource(R.drawable.bar);
                        lay2.setBackgroundResource(R.drawable.arrow_bar);
                        lay3.setBackgroundResource(R.drawable.bar);
                        lay4.setBackgroundResource(R.drawable.bar);
                        page_flag = 1;

                        /*************************** ISTAIQUE **************************/
                        try {

                            findViewById(R.id.networkFloatBtn).setVisibility(View.VISIBLE);
                            findViewById(R.id.networkFloatBtnClose).setVisibility(View.VISIBLE);
                            findViewById(R.id.helpTitle).setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    findViewById(R.id.helpTitle).setVisibility(View.GONE);
                                }
                            }, 8000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /****************************************************************/
                        break;
                    case 2:
                        pager.setCurrentItem(2);
                        tabwidget.setVisibility(View.VISIBLE);
                        tab1.setImageResource(R.drawable.chat_selected);
                        tab2.setImageResource(R.drawable.share_unselected);
                        tab3.setImageResource(R.drawable.shopping_bag_unselected);
                        tab4.setImageResource(R.drawable.query_unselected);
                        lay1.setBackgroundResource(R.drawable.arrow_bar);
                        lay2.setBackgroundResource(R.drawable.bar);
                        lay3.setBackgroundResource(R.drawable.bar);
                        lay4.setBackgroundResource(R.drawable.bar);
                        page_flag = 2;

                        /*************************** ISTAIQUE **************************/
                        try{

                            findViewById(R.id.floatBtnChat).setVisibility(View.VISIBLE);
                            findViewById(R.id.close_help).setVisibility(View.VISIBLE);
                            final TextView chatHelpTitle = (TextView) findViewById(R.id.chatHelpTitle);
                            chatHelpTitle.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    chatHelpTitle.setVisibility(View.GONE);
                                }
                            }, 8000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /****************************************************************/
                        break;
                    case 3:
                        pager.setCurrentItem(3);
                        if (explistView != null && explistView.getVisibility() == View.VISIBLE)
                            tabwidget.setVisibility(View.GONE);
                        else
                            tabwidget.setVisibility(View.VISIBLE);
                        tab1.setImageResource(R.drawable.chat_unselected);
                        tab2.setImageResource(R.drawable.share_unselected);
                        tab3.setImageResource(R.drawable.shopping_bag_unselected);
                        tab4.setImageResource(R.drawable.query_selected);
                        lay1.setBackgroundResource(R.drawable.bar);
                        lay2.setBackgroundResource(R.drawable.bar);
                        lay3.setBackgroundResource(R.drawable.bar);
                        lay4.setBackgroundResource(R.drawable.arrow_bar);

                        page_flag = 3;
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("sss", "" + state);

            }
        });
        SyncUtils.CreateSyncAccount(this);
        //Cursor max_cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, new String[]{"MAX(" + FeedCatalog.COLUMN_UPDATEDAT + ")"}, null, null, null);


    }

    public void akitest()
    {
        final AlertDialog ad = new AlertDialog.Builder(Home.this).create();
        ad.setCancelable(false);
        ad.setTitle("Please note!");
        ad.setMessage("Register your Business with us and get access to exciting features on the app!");
        ad.setButton(DialogInterface.BUTTON_POSITIVE, "Register Now", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent i = new Intent(getApplicationContext(), Catalog.class);
                startActivity(i);
                ad.dismiss();
            }
        });

        ad.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });
        ad.show();
    }

    private void navStuff() {
        // TODO Auto-generated method stub
        slider = new SimpleSideDrawer(Home.this);
        slider.setHorizontalFadingEdgeEnabled(true);
        slider.setLeftBehindContentView(R.layout.slider);
        items = (ListView) findViewById(R.id.listViewMenu);
        sliderMenu = (ImageView) findViewById(R.id.btnMenu);
        headerLogo = (RelativeLayout) findViewById(R.id.headerLogo);
        sliderMenu.setVisibility(View.VISIBLE);

        /******************* ISTIAQUE: CODE STARTS **********************************/
        right_slider = new SimpleSideDrawer(Home.this);
        right_slider.setHorizontalFadingEdgeEnabled(true);
        right_slider.setRightBehindContentView(R.layout.right_slider);
        right_listViewMenu = (ListView) findViewById(R.id.right_listViewMenu);
        right_headerLogo = (RelativeLayout) findViewById(R.id.right_headerLogo);

        /******************* ISTIAQUE: CODE ENDS **********************************/


        sliderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slider.isClosed()) {
                    slider.openLeftSide();

                }

                else
                {
                    slider.close();
                }
            }
        });
        navDrawerItems = new ArrayList<NavDrawerItem>();
        /******************* ISTIAQUE: CODE STARTS **********************************/
        right_navDrawerItems = new ArrayList<NavDrawerItem>();
        /******************* ISTIAQUE: CODE ENDS **********************************/

        // load slide menu items
        String isRoot = helper.getDefaults("is_root", getApplicationContext());
        if (isRoot.equalsIgnoreCase("Y")) {
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items2);
            navMenuIcons = getResources()
                    .obtainTypedArray(R.array.nav_drawer_icons_root);
            for (int i = 0; i < navMenuTitles.length; i++) {
                /********************************** ISTIAQUE: CODE STARTS **********************************/
                if (navMenuTitles[i].equalsIgnoreCase("My Catalog") || navMenuTitles[i].equalsIgnoreCase("My Favorites") ||
                        navMenuTitles[i].equalsIgnoreCase("My Business") || navMenuTitles[i].equalsIgnoreCase("My Orders") ||
                        navMenuTitles[i].equalsIgnoreCase("My Profile") || navMenuTitles[i].equalsIgnoreCase("Wydr Cash")){

                    //checking whether user is NU (Normal user) or not
                    if (ufList.contains(AppUtil.TAG_Network) && navMenuTitles[i].equalsIgnoreCase("Wydr Cash")) {

                        continue;

                    } else {

                        right_navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
                                image_array_root[i], i));
                    }

                }
                /********************************** ISTIAQUE: CODE ENDS **********************************/
                else {
                    if ((ufList.size() == 1) && (i == 0)){
                        continue;
                    } else {
                        navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
                                image_array_root[i], i));
                    }
                }
            }
        } else {
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
            navMenuIcons = getResources()
                    .obtainTypedArray(R.array.nav_drawer_icons_sub);
            for (int i = 0; i < navMenuTitles.length; i++) {
                /********************************** ISTIAQUE: CODE STARTS **********************************/
                if (navMenuTitles[i].equalsIgnoreCase("My Catalog") || navMenuTitles[i].equalsIgnoreCase("My Favorites") ||
                        navMenuTitles[i].equalsIgnoreCase("My Business") || navMenuTitles[i].equalsIgnoreCase("My Orders") ||
                        navMenuTitles[i].equalsIgnoreCase("My Profile") || navMenuTitles[i].equalsIgnoreCase("Wydr Cash")){
                    right_navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
                            image_array_root[i], i));
                }
                /********************************** ISTIAQUE: CODE ENDS **********************************/
                else {
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
                            image_array_sub[i], i));
                }
            }
        }


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        sliderAdapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        /********************************** ISTIAQUE: CODE STARTS **********************************/
        right_sliderAdapter = new NavDrawerListAdapter(getApplicationContext(),
                right_navDrawerItems);
        /********************************** ISTIAQUE: CODE ENDS **********************************/

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.slider_header, items,
                false);
        /********************************** ISTIAQUE: CODE STARTS **********************************/
        ViewGroup right_header = (ViewGroup) inflater.inflate(R.layout.slider_header, right_listViewMenu,
                false);
        /********************************** ISTIAQUE: CODE ENDS **********************************/

        name = (TextView) header.findViewById(R.id.txtProfileName);
        company = (TextView) header.findViewById(R.id.txtCompanyName);
        name.setText(MyTextUtils.toTitleCase("Welcome " + helper.getDefaults("firstname", getApplicationContext())));
        company.setText(MyTextUtils.toTitleCase(helper.getDefaults("company", getApplicationContext())));
        items.addHeaderView(header);
        items.setAdapter(sliderAdapter);
        items.setOnItemClickListener(new SlideMenuClickListener());
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.close();

            }
        });

        /********************************** ISTIAQUE: CODE STARTS **********************************/
        name = (TextView) right_header.findViewById(R.id.txtProfileName);
        company = (TextView) right_header.findViewById(R.id.txtCompanyName);
        name.setText(MyTextUtils.toTitleCase("Welcome " + helper.getDefaults("firstname", getApplicationContext())));
        company.setText(MyTextUtils.toTitleCase(helper.getDefaults("company", getApplicationContext())));
        right_listViewMenu.addHeaderView(right_header);
        right_listViewMenu.setAdapter(right_sliderAdapter);
        right_listViewMenu.setOnItemClickListener(new SlideMenuClickListener());
        right_headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right_slider.close();

            }
        });
        /********************************** ISTIAQUE: CODE ENDS **********************************/
    }

    @Override
    public void onBackPressed()
    {

        if (!slider.isClosed())
        {
            slider.close();

        } else if (overlayLayout.getVisibility() == View.VISIBLE) {
            overlayLayout.setVisibility(View.GONE);
            addMenu.setIcon(R.drawable.add);
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (fragmentList != null) {
                //TODO: Perform your logic to pass back press here
                for (Fragment fragment : fragmentList) {
                    if (fragment instanceof OnBackPressedListener) {
                        if (page_flag == 3 && fragment.toString().contains("QueryFeed")) {
                            ((OnBackPressedListener) fragment).onBackPressed();
                        }
                    }
                }
            }
            if (pager.getCurrentItem() == 2 && MarketPlace.ADAPTER_FLAG) {

                marketPlace.changeAdapter();
            } else if (pager.getCurrentItem() != 3) {
                alertDialog.setTitle(getString(R.string.alert));
                alertDialog.setMessage(getString(R.string.are_u_sure_exit));
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        finish();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        addMenu = menu.findItem(R.id.add);
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

                startActivity(new Intent(Home.this, CartActivity.class));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setNotifCount()

    {
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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.search:

                /*******************************ISTIAQUE***************************************/
                application = (Controller) Home.this.getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("Home", "Move", "SearchActivity");
                /*******************************ISTIAQUE***************************************/

                startActivity(new Intent(Home.this, SearchActivity.class));
                Log.e("item", "search");
                break;
            case R.id.add:
                if (overlayLayout.getVisibility() == View.VISIBLE) {
                    addMenu.setIcon(R.drawable.add);
                    overlayLayout.setVisibility(View.GONE);
                } else {
                    overlayLayout.setVisibility(View.VISIBLE);
                    addMenu.setIcon(R.drawable.close_new);
                }


                break;
            /*************************** ISTIAQUE: CODE BEGINS ************************/
            case R.id.user_profile:
                if (right_slider.isClosed()) {
                    right_slider.openRightSide();

                } else {
                    right_slider.close();
                }
                break;
            /*************************** ISTIAQUE: CODE ENDS ************************/

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_tab1:
                pager.setCurrentItem(2);
                tabwidget.setVisibility(View.VISIBLE);
                tab1.setImageResource(R.drawable.chat_selected);
                tab2.setImageResource(R.drawable.share_unselected);
                tab3.setImageResource(R.drawable.shopping_bag_unselected);
                tab4.setImageResource(R.drawable.query_unselected);
                lay1.setBackgroundResource(R.drawable.arrow_bar);
                lay2.setBackgroundResource(R.drawable.bar);
                lay3.setBackgroundResource(R.drawable.bar);
                lay4.setBackgroundResource(R.drawable.bar);

                break;
            case R.id.lay_tab2:
                pager.setCurrentItem(1);
                tabwidget.setVisibility(View.VISIBLE);
                tab1.setImageResource(R.drawable.chat_unselected);
                tab2.setImageResource(R.drawable.share_selected);
                tab3.setImageResource(R.drawable.shopping_bag_unselected);
                tab4.setImageResource(R.drawable.query_unselected);
                lay1.setBackgroundResource(R.drawable.bar);
                lay2.setBackgroundResource(R.drawable.arrow_bar);
                lay3.setBackgroundResource(R.drawable.bar);
                lay4.setBackgroundResource(R.drawable.bar);
                break;
            case R.id.lay_tab3:
                pager.setCurrentItem(0);
                tabwidget.setVisibility(View.VISIBLE);
                tab1.setImageResource(R.drawable.chat_unselected);
                tab2.setImageResource(R.drawable.share_unselected);
                tab3.setImageResource(R.drawable.shopping_bag_selected);
                tab4.setImageResource(R.drawable.query_unselected);
                lay1.setBackgroundResource(R.drawable.bar);
                lay2.setBackgroundResource(R.drawable.bar);
                lay3.setBackgroundResource(R.drawable.arrow_bar);
                lay4.setBackgroundResource(R.drawable.bar);


                break;
            case R.id.lay_tab4:
                pager.setCurrentItem(3);
                tabwidget.setVisibility(View.VISIBLE);
                tab1.setImageResource(R.drawable.chat_unselected);
                tab2.setImageResource(R.drawable.share_unselected);
                tab3.setImageResource(R.drawable.shopping_bag_unselected);
                tab4.setImageResource(R.drawable.query_selected);
                lay1.setBackgroundResource(R.drawable.bar);
                lay2.setBackgroundResource(R.drawable.bar);
                lay3.setBackgroundResource(R.drawable.bar);
                lay4.setBackgroundResource(R.drawable.arrow_bar);


                break;
        }
    }

    public void hideoverlay(View v) {
        addMenu.setIcon(R.drawable.add);
        overlayLayout.setVisibility(View.GONE);
    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        //cd = new ConnectionDetector(this);
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    private int getContactCount() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    null);
            if (cursor != null) {
                return cursor.getCount();
            } else {
                return 0;
            }
        } catch (Exception ignore) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    private int getContactOWNCount() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    ChatProvider.BOOK_URI, null, null, null,
                    null);
            if (cursor != null) {
                return cursor.getCount();
            } else {
                return 0;
            }
        } catch (Exception ignore) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
        //   }
    }

    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmppConnection.getInstance().setPresence(3);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
        setNotifCount();


        if (pos == 103) {
            pager.setCurrentItem(2);
        }

        name.setText(MyTextUtils.toTitleCase("Welcome " + helper.getDefaults("firstname", getApplicationContext())));
        mSyncStatusObserver.onStatusChanged(0);
        company.setText(MyTextUtils.toTitleCase(helper.getDefaults("company", getApplicationContext())));
        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
        if (XmppConnection.getInstance().getConnection() == null) {
            //  if(!XmppConnection.getInstance().getConnection().isConnected())
            this.startService(new Intent(this, SmackService.class));
        }

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Home");
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        Intent intent = getIntent();
        String action = intent.getAction();

        //Uri uri = intent.getData();
        String campaignData = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && campaignData != null) {
            //String campaignData = uri.toString();
            //Toast.makeText(Home.this, campaignData, Toast.LENGTH_SHORT).show();
            mTracker.send(new HitBuilders.ScreenViewBuilder().setCampaignParamsFromUrl(campaignData).build());
        } else {
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", pager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (pos == 0) {
            pager.setCurrentItem(savedInstanceState.getInt("page"));
        } else {
            pager.setCurrentItem(2);
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SmackService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item

//            int pos = item.getPosition();

            if (!slider.isClosed()) {
                switch (position) {
                    case 0:
                        slider.close();
                        break;

                    case 1:
                        if (ufList.size() == 1){

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Business Leads", "Move", "MyQuery");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, MyQuery.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Become a Seller", "Move", "Become_a_Seller");
                            /*******************************ISTIAQUE***************************************/
                            startActivity(new Intent(Home.this, Become_a_Seller.class));
                        }
                        slider.close();
                        break;

                    case 2:
                        if (ufList.size() == 1){

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Products Shared", "Move", "SharedItems");
                            /*******************************ISTIAQUE***************************************/
                            startActivity(new Intent(Home.this, SharedItems.class));
                        } else {
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Business Leads", "Move", "MyQuery");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, MyQuery.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        /*startActivity(new Intent(Home.this, MyCatalog.class));*/
                        slider.close();
                        break;
                    case 3:
                        if (ufList.size() == 1){
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("All Sellers", "Move", "AllSellers");
                            /*******************************ISTIAQUE***************************************/
                            startActivity(new Intent(Home.this, AllSellers.class));
                        } else {
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Products Shared", "Move", "SharedItems");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, SharedItems.class));
                        }
                        /*startActivity(new Intent(Home.this, MyQuery.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));*/
                        slider.close();
                        break;
                    case 4:
                        if (ufList.size() == 1){
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("App Feedback", "Move", "Feedback");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, Feedback.class));
                        } else {
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("All Sellers", "Move", "AllSellers");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, AllSellers.class));
                        }
                        /*startActivity(new Intent(Home.this, MyFavourite.class));*/
                        slider.close();
                        break;
                    case 5:
                        if (ufList.size() == 1){

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Manage Team", "Move", "ManageTeamActivity");
                            /*******************************ISTIAQUE***************************************/
                            startActivity(new Intent(Home.this, ManageTeamActivity.class));
                        } else {
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("App Feedback", "Move", "Feedback");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, Feedback.class));
                        }
                        /*startActivity(new Intent(Home.this, Catalog.class));*/
                        slider.close();
                        break;
                    case 6:
                        if (ufList.size() == 1){

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Contact Us", "Move", "ContactUs");
                            /*******************************ISTIAQUE***************************************/
                            startActivity(new Intent(Home.this, ContactUs.class));
                        } else {
                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Manage Team", "Move", "ManageTeamActivity");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, ManageTeamActivity.class));
                        }
                        /*startActivity(new Intent(Home.this, MyOrders.class));*/
                        slider.close();

                        break;
                    case 7:
                        if (ufList.size() == 1){

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Help Tutorial", "Move", "Help");
                            startActivity(new Intent(Home.this, Help.class));
                            /*******************************ISTIAQUE***************************************/

                        } else {

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Contact Us", "Move", "ContactUs");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(Home.this, ContactUs.class));
                        }
                        slider.close();

                        break;
                    case 8:
                        if (ufList.size() == 1){

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Terms and Use", "Move", "TermsAndUse");
                            startActivity(new Intent(Home.this, TermsAndUse.class));
                            /*******************************ISTIAQUE***************************************/

                        } else {

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Help Tutorial", "Move", "Help");
                            startActivity(new Intent(Home.this, Help.class));
                            /*******************************ISTIAQUE***************************************/

                        }
                        slider.close();

                        break;
                    case 9:
                        if (ufList.size() == 1){


                        } else {

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) Home.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("Terms and Use", "Move", "TermsAndUse");
                            startActivity(new Intent(Home.this, TermsAndUse.class));
                            /*******************************ISTIAQUE***************************************/

                        }
                        slider.close();

                        break;
                    case 10:
                        /*startActivity(new Intent(Home.this, Feedback.class));*/
                        slider.close();
                        break;
                    case 11:
                        /*startActivity(new Intent(Home.this, ManageTeamActivity.class));*/
                        slider.close();
                        break;
                }

            }

            /********************************* ISTIAQUE: CODE BEGINS *******************************/
            if (!right_slider.isClosed()) {
                switch (position) {
                    case 0:
                        right_slider.close();
                        break;
                    case 1:
                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) Home.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("My Catalog", "Move", "MyCatalog");
                        /*******************************ISTIAQUE***************************************/

                        startActivity(new Intent(Home.this, MyCatalog.class));
                        right_slider.close();
                        break;
                    case 2:
                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) Home.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("My Favourites", "Move", "MyFavourite");
                        /*******************************ISTIAQUE***************************************/

                        startActivity(new Intent(Home.this, MyFavourite.class));
                        right_slider.close();
                        break;
                    case 3:
                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) Home.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("My Business", "Move", "Catalog");
                        /*******************************ISTIAQUE***************************************/

                        startActivity(new Intent(Home.this, Catalog.class));
                        right_slider.close();
                        break;
                    case 4:
                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) Home.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("My Orders", "Move", "MyOrders");
                        /*******************************ISTIAQUE***************************************/

                        startActivity(new Intent(Home.this, MyOrders.class));
                        right_slider.close();
                        break;
                    case 5:
                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) Home.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("My Profile", "Move", "MyProfile");
                        /*******************************ISTIAQUE***************************************/

                        startActivity(new Intent(Home.this, MyProfile.class));
                        right_slider.close();
                        break;

                    case 6:
                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) Home.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("My Rewards", "Move", "Reward");
                        /*******************************ISTIAQUE***************************************/

                        startActivity(new Intent(Home.this, Reward.class));
                        right_slider.close();
                        break;
                }
            }
            /****************************** ISTIAQUE: CODE ENDS *************************************/
        }
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index)
        {
            switch (index)
            {
                case 0:
                    //return new MarketPlace();
                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Landing Page", "Move", "Marketplace");
                    /*******************************ISTIAQUE***************************************/
                    return new LandingPage();
                case 1:
                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Landing Page", "Move", "My Network");
                    /*******************************ISTIAQUE***************************************/
                    return new MyNetwork();
                case 2:
                    //marketPlace = new MarketPlace();
                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Landing Page", "Move", "Chat");
                    /*******************************ISTIAQUE***************************************/
                    return new ChatUser();
                case 3:
                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Landing Page", "Move", "Business Leads");
                    /*******************************ISTIAQUE***************************************/
                    return new QueryFeed();
               /*  default:
                  return new ChatUser();*/
            }
            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 4;
        }

    }

    private class GetNetwork extends AsyncTask<String, Void, JSONObject>
    {
        final Calendar c = Calendar.getInstance();
        JSONObject table = new JSONObject();
        int flag = 0;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            if (!isFinishing())
                progress.show();
           /* getContentResolver().delete(ChatProvider.NET_URI, null, null);
            getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);*/
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            //  Log.e("updated value ", "agye getnetwork me" + helper.getDefaults("user_id", getApplicationContext()));
            try {
                table.put("userid", userid);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            json = userFunction.getNetwork(table, Home.this);
            //  Log.e("AT json", "gettttetetetet");
            try {
                if (json != null) {
                    if (json.has("error")) {
                        flag = 2;
                    } else {
                        JSONArray jsonMainArr = json.getJSONArray("network");
                        for (int i = 0; i < jsonMainArr.length(); i++) {  // **line 2**
                            JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                            ContentValues values = new ContentValues();

                            values.put(NetSchema.USER_COMPANY, childJSONObject.getString("company"));
                            values.put(NetSchema.USER_DISPLAY, "");
                            values.put(NetSchema.USER_COMPANY_ID, childJSONObject.getString("company_id"));
                            values.put(NetSchema.USER_ID, childJSONObject.getString("user_id"));

                            values.put(NetSchema.USER_NET_ID, childJSONObject.getString("user_login") + "@" + AppUtil.SERVER_NAME);
                            values.put(NetSchema.USER_STATUS, "1");
                            values.put(NetSchema.USER_NAME, childJSONObject.getString("name"));
                            //-------------------akshay basotra-------------------------//
                            values.put(NetSchema.USER_STATUS_toggle,childJSONObject.getString("status"));
                            //values.put(NetSchema.USER_STATUS_toggle,"P");
                            values.put(NetSchema.USER_SELLER_ID,childJSONObject.getString("seller_id"));
                            //------------------akshay basotra--------------------------//
                            //  values.put(NetSchema.USER_NAME,  childJSONObject.getString("name"));
                            values.put(ProSchema.KEY_CREATED, "" + format.format(c.getTime()));
                            String where = childJSONObject.getString("phone");
                            if (where.length() > 10)
                                where = where.substring(where.length() - 10);
                            Log.e("where", where);
                            values.put(NetSchema.USER_PHONE, where);
                            getContentResolver().insert(ChatProvider.NET_URI, values);
                            getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);

//                        ContentValues cv = new ContentValues();
//                        //  values.put(ContactsDb.KEY_PH_NO, helper.cleanPhoneNo(childJSONObject.getString("phone"), getApplicationContext()));
//                        cv.put(ContactsDb.KEY_STATUS, 2);
//                        Log.i("Home ","Where - "+ where);
//                        getContentResolver().update(ChatProvider.BOOK_URI, cv, ContactsDb.KEY_ID + "=?", new String[]{where});


                        }
                        Log.e("size", String.valueOf(jsonMainArr.length()));
                    }


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
            if (!isFinishing()) {
                progress.dismiss();
                //      getContentResolver().notifyChange(ChatProvider.BOOK_URI, null, false);
                if (flag == 1) {
                    Toast.makeText(Home.this, getResources().getString(R.string.server_error), Toast.LENGTH_LONG);
                } else if (flag == 2) {
                    try {
                        Toast.makeText(Home.this, json.getString("error"), Toast.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (flag == 0 && helper.getDefaults("SyncFlag", Home.this).contentEquals("1")) {
                    Log.e("", "Once");
                    helper.setDefaults("SyncFlag", "2", Home.this);
                    if (Build.VERSION.SDK_INT >= 23) {
                        //do your check here
                        if (PermissionsUtils.verifyContactPermissions(Home.this)) {
                            startService(new Intent(Home.this, ContactsSyncService.class));
                        }
                        //  }
                    } else {
                        startService(new Intent(Home.this, ContactsSyncService.class));
                    }
                    // Log.e("SyncFlag 2", helper.getDefaults("SyncFlag", Home.this) + "");
                    // startService(new Intent(Home.this, ContactsSyncService.class));
                }
            }


        }
    }

    public class MyCOntentObserver extends ContentObserver {
        public MyCOntentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            final int currentCount = getContactCount();

            // Get count from your sqlite database
            int mContactCount = getContactOWNCount();
            if (Build.VERSION.SDK_INT >= 23) {
                //do your check here
                if (PermissionsUtils.verifyContactPermissions(Home.this)) {
                    startService(new Intent(Home.this, ContactsSyncService.class));
                }
            } else {
                startService(new Intent(Home.this, ContactsSyncService.class));
            }

            if (currentCount < mContactCount) {
                // DELETE HAPPEN.
                Log.e("Status", "Deletion");
                //contactDBOperaion.SyncContacts(1);
            } else if (currentCount == mContactCount) {
                // UPDATE HAPPEN.
                //  contactDBOperaion.SyncContacts(0);
            } else {
                // INSERT HAPPEN.
                Log.e("Status", "Insertion");
                // contactDBOperaion.SyncContacts(2);
            }
            Log.e("", "~~~~~~" + selfChange);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }

    public class OverlayClickListener implements View.OnClickListener {
        Context context;
        View overlayLayout;

        public OverlayClickListener(Context context, View overlayLayout) {
            this.context = context;
            this.overlayLayout = overlayLayout;
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.start_chat:


                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Start New Chat", "Move", "StartChat");
                    /*******************************ISTIAQUE***************************************/

                    Log.e("item", "New_Chat");
                    context.startActivity(new Intent(context, StartChat.class));
                    overlayLayout.setVisibility(View.GONE);
                    addMenu.setIcon(R.drawable.add);
                    break;

                case R.id.add_connection:

                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Add Connection", "Move", "Contact");
                    /*******************************ISTIAQUE***************************************/

                    context.startActivity(new Intent(context, Contact.class));
                    overlayLayout.setVisibility(View.GONE);
                    addMenu.setIcon(R.drawable.add);
                    Log.e("item", "Add_Connection");
                    break;

                case R.id.add_catalog:

                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Add Catalog", "Move", "AddProduct");
                    /*******************************ISTIAQUE***************************************/

                    context.startActivity(new Intent(context, AddProduct.class).putExtra("flag", "1").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("Return", "2"));
                    Log.e("item", "Add_Catalog");
                    overlayLayout.setVisibility(View.GONE);
                    addMenu.setIcon(R.drawable.add);
                    break;

                case R.id.submit_query:

                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Submit Query Catalog", "Move", "SubmitQuery");
                    /*******************************ISTIAQUE***************************************/

                    Log.e("item", "Submit_query");
                    context.startActivity(new Intent(context, SubmitQuery.class));
                    overlayLayout.setVisibility(View.GONE);
                    addMenu.setIcon(R.drawable.add);
                    break;

                case R.id.create_group:
                    break;

                case R.id.create_broadcast:

                    /*******************************ISTIAQUE***************************************/
                    application = (Controller) Home.this.getApplication();
                    mTracker = application.getDefaultTracker();
                    application.trackEvent("Broadcast", "Move", "CreateBroadcast");
                    /*******************************ISTIAQUE***************************************/

                    startActivity(new Intent(context, CreateBroadcast.class));
                    overlayLayout.setVisibility(View.GONE);
                    addMenu.setIcon(R.drawable.add);
                    break;

                case R.id.close:
                    overlayLayout.setVisibility(View.GONE);
                    addMenu.setIcon(R.drawable.add);
                    break;

            }
        }
    }

    //Registration receiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
            checkMessage(intent);
        }
    };

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            //JSON_DATA_KEY contains JSON payload of push notification.
            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    //Registration of the receivers
    public void registerReceivers()
    {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers()
    {
        //Unregister receivers on pause
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // pass.
        }

        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            //pass through
        }
    }

    private void checkMessage(Intent intent) {
        if (null != intent) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                Log.d("here", intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                //showMessage("register");
            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                //  showMessage("unregister");
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                //  showMessage("register error");
            } else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
                //   showMessage("unregister error");
            }

            resetIntentValues();
        }
    }

    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        checkMessage(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PermissionsUtils.REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for contact permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
                startService(new Intent(Home.this, ContactsSyncService.class));

            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
