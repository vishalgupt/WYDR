package wydr.sellers.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.modal.NavDrawerItem;
import wydr.sellers.registration.BuisCat;
import wydr.sellers.registration.BuisDetail;


public class Catalog extends AppCompatActivity implements View.OnClickListener {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see
    public static SharedPreferences sharedPreferences;
    // slide menu items
    public Toolbar mToolbar;
    ViewPager viewPager;
    TextView tab1, tab2;
    ImageView sliderMenu;
    SimpleSideDrawer slider;
    ListView items;
    NavDrawerListAdapter sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;
    ImageView menu;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catelog);

//        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
//            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                    "Catalog"));
//        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.my_business));


        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
       /* tab11 = (ImageView) findViewById(R.id.tab11);
        tab12 = (ImageView) findViewById(R.id.tab12);*/
        final ViewPager pager = (ViewPager) findViewById(R.id.catpager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Catalog", "Move", "BuisDetail");
                /*******************************ISTIAQUE***************************************/

                pager.setCurrentItem(0);
            }
        });

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Catalog", "Move", "BuisCat");
                /*******************************ISTIAQUE***************************************/

                pager.setCurrentItem(1);
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    tab1.setTextColor(Color.parseColor("#2196F3"));
                    tab1.setTypeface(null, Typeface.BOLD);
                    tab1.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                    tab2.setTypeface(null, Typeface.NORMAL);
                    tab2.setTextColor(Color.parseColor("#3c3c3c"));
                    tab2.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                   /* tab11.setVisibility(View.VISIBLE);
                    tab12.setVisibility(View.INVISIBLE);*/

                } else if (position == 1) {
                    tab1.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                    tab1.setTypeface(null, Typeface.NORMAL);
                    tab1.setTextColor(Color.parseColor("#3c3c3c"));
                    tab2.setTypeface(null, Typeface.BOLD);
                    tab2.setTextColor(Color.parseColor("#2196F3"));
                    tab2.setBackgroundResource(R.drawable.my_bussiness_top_arrow);


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new BuisDetail();
            }

            else if (position == 1)
            {
                return new BuisCat();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setNotifCount();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName(getString(R.string.my_business));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setNotifCount()
    {
        Cursor mCursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null)
        {
            if (mNotifCount == 0) {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            } else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

                startActivity(new Intent(Catalog.this, CartActivity.class));
            }
        });
        MenuItem sitem = menu.findItem(R.id.searchCart_search);
        sitem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

}



