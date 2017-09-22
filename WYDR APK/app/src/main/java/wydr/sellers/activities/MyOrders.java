package wydr.sellers.activities;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.modal.NavDrawerItem;


public class MyOrders extends AppCompatActivity implements View.OnClickListener {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see
    public static SharedPreferences sharedPreferences;
    TextView tab1, tab2;
    ImageView sliderMenu;
    SimpleSideDrawer slider;
    ListView items;
    NavDrawerListAdapter sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;

    ImageView menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

//        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
//            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                    "Orders"));
//        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.myorders));


        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);

        final ViewPager pager = (ViewPager) findViewById(R.id.catpager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Orders", "Move", "OrdersPlaced");
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
                application.trackEvent("My Orders", "Move", "OrdersReceived");
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
                    tab2.setTypeface(null, Typeface.NORMAL);
                    tab2.setTextColor(Color.parseColor("#3c3c3c"));
                    tab1.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                    tab2.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                   /* tab11.setVisibility(View.VISIBLE);
                    tab12.setVisibility(View.INVISIBLE);*/

                } else if (position == 1) {
                    tab2.setTypeface(null, Typeface.BOLD);
                    tab1.setTypeface(null, Typeface.NORMAL);
                    tab1.setTextColor(Color.parseColor("#3c3c3c"));
                    tab2.setTextColor(Color.parseColor("#2196F3"));
                    tab2.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                    tab1.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                  /*  tab11.setVisibility(View.INVISIBLE);
                    tab12.setVisibility(View.VISIBLE);*/

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                return new OrdersPlaced();
            } else if (position == 1) {

                return new OrdersReceived();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MyOrders");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}


