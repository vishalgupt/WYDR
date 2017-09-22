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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.syncadap.AlteredCatalog;
import wydr.sellers.syncadap.FeedCatalog;


public class MyCatalog extends AppCompatActivity implements View.OnClickListener
{
    public static SharedPreferences sharedPreferences;
    public static int page_flag = 0;
    static boolean backflag;
    TextView tab1, tab2;
    ImageView sliderMenu;
    SimpleSideDrawer slider;
    ListView items;
    ImageView menu;
    ArrayList<String> ufList = new ArrayList<>();
    NonSwipeableViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);
//        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
//            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                    "MyCatalog"));
//        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.mycatalog));
        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        tab1.setText(getString(R.string.liveproducts));
        tab2.setText(getString(R.string.draftsproducts));
        pager = (NonSwipeableViewPager) findViewById(R.id.catpager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Catalog", "Move", "MyCatLive");
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
                application.trackEvent("My Catalog", "Move", "MyCatDrafts");
                /*******************************ISTIAQUE***************************************/

                pager.setCurrentItem(1);
            }
        });
//      Log.i("getIntent().", getIntent().getStringExtra("draft"));
        initUff();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position)
            {

                if (position == 0)
                {
                    page_flag = 0;
                    //Log.i("clicked at -", "0" + MyCatLive.listView.getVisibility());
                    tab1.setTextColor(Color.parseColor("#2196F3"));
                    tab1.setTypeface(null, Typeface.BOLD);
                    tab2.setTypeface(null, Typeface.NORMAL);
                    tab2.setTextColor(Color.parseColor("#3c3c3c"));
                    tab1.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                    tab2.setBackgroundResource(R.drawable.my_bussiness_top_bar);
//                    if(MyCatLive.listView.getVisibility()==View.VISIBLE)
//                        findViewById(R.id.actionbar_container).setVisibility(View.GONE);
//                    else
//                        findViewById(R.id.actionbar_container).setVisibility(View.VISIBLE);

                } else if (position == 1) {
                    page_flag = 1;
//                    Log.i("clicked at -", "1" + MyCatDrafts.listView.getVisibility());
                    tab2.setTypeface(null, Typeface.BOLD);
                    tab1.setTypeface(null, Typeface.NORMAL);
                    tab1.setTextColor(Color.parseColor("#3c3c3c"));
                    tab2.setTextColor(Color.parseColor("#2196F3"));
                    tab2.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                    tab1.setBackgroundResource(R.drawable.my_bussiness_top_bar);
//                    if(pager.isScrollContainer())
//                    if(MyCatDrafts.listView.getVisibility()==View.VISIBLE)
//                        findViewById(R.id.actionbar_container).setVisibility(View.GONE);
//                    else
//                        findViewById(R.id.actionbar_container).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        backflag = getIntent().getStringExtra("parent_myProfile") != null;
        if (getIntent().getStringExtra("draft") != null) {
            pager.setCurrentItem(1);
            //    tab2.performClick();
            Log.i("getIntent().", getIntent().getStringExtra("draft"));
        }
    }

    private void initUff()
    {
        Helper helper = new Helper();
        ImageView iv_bussiness = (ImageView) findViewById(R.id.uf_catalog);
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Catalog.class);
                startActivity(intent);
            }
        });
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.rl_cat);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));

        if (ufList.contains(AppUtil.TAG_My_Catalog_Draft_Products)) {
            ll.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(helper.getDefaults(AppUtil.TAG_My_Catalog_Draft_Products + "_photo", getApplicationContext()))
                    .into(iv_bussiness);
        } else {
            ll.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, new String[]{"Max(" + FeedCatalog.COLUMN_SP + ")", "Min(" + FeedCatalog.COLUMN_SP + ")"}, null, null, null);
        cursor.moveToFirst();
        MyCatLive.max_value = cursor.getLong(0);
        MyCatLive.min_value = cursor.getLong(1);
        Log.i("MyCatalog", "values--" + MyCatLive.max_value + "/" + MyCatLive.min_value);
        cursor.close();
        Cursor cursor2 = getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, new String[]{"Max(" + AlteredCatalog.COLUMN_SP + ")", "Min(" + AlteredCatalog.COLUMN_SP + ")"}, AlteredCatalog.COLUMN_LOCAL_FLAG + " not in (?) and " + AlteredCatalog.COLUMN_REQUEST_STATUS + " not in (?) and " + AlteredCatalog.COLUMN_SP + " is not null and " + AlteredCatalog.COLUMN_SP + " <> '' ", new String[]{"5", "0"}, null);
        cursor2.moveToFirst();
        MyCatDrafts.max_value = cursor2.getLong(0);
        MyCatDrafts.min_value = cursor2.getLong(1);
        Log.i("MyCatalog", "values--" + MyCatDrafts.max_value + "/" + MyCatDrafts.min_value);
        cursor2.close();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName(getString(R.string.mycatalog));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        if (ufList.contains(AppUtil.TAG_My_Catalog_Draft_Products)) {
            super.onBackPressed();
        } else {
            Log.i("SelelrsQuery ", backflag + " condition");
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (fragmentList != null) {
                //TODO: Perform your logic to pass back press here
                for (Fragment fragment : fragmentList) {
                    if (fragment instanceof OnBackPressedListener) {

                        if (page_flag == 0) {
                            if (fragment.toString().contains("MyCatLive"))
                                ((OnBackPressedListener) fragment).onBackPressed();
                        } else {
                            if (fragment.toString().contains("MyCatDrafts"))
                                ((OnBackPressedListener) fragment).onBackPressed();
                        }
                    }
                }


            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                /*startActivity(new Intent(SellersQuery.this, SellerProfile.class).putExtra("username", username).putExtra("company_id", companyid).putExtra("userid", userid));
                finish();*/
                finish();
                //onBackPressed();
                Log.d("1", "22");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", pager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pager.setCurrentItem(savedInstanceState.getInt("page"));
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
            {
                Log.i("1", "cl");
                // findViewById(R.id.actionbar_container).setVisibility(View.VISIBLE);
                return new MyCatLive();


            }

            else if (position == 1)
            {
                Log.i("2", "cl");
                // findViewById(R.id.actionbar_container).setVisibility(View.VISIBLE);
                return new MyCatDrafts();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}


