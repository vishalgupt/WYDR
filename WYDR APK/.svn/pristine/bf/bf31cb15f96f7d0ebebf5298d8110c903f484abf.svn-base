package wydr.sellers.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

import wydr.sellers.R;

/**
 * Created by Navdeep on 3/9/15.
 */
public class MyFavourite extends AppCompatActivity
{
    public static SharedPreferences sharedPreferences;
    TextView tab1, tab2, tab3, tab4;
    ImageView sliderMenu;
    SimpleSideDrawer slider;
    ListView items;
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_favourit);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.myfavs));
        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        tab3 = (TextView) findViewById(R.id.tab3);
        tab4 = (TextView) findViewById(R.id.tab4);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new SlidePageAdapter(getSupportFragmentManager()));
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Favorites", "Move", "FavouriteProductsFragment");
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
                application.trackEvent("My Favorites", "Move", "FavChatFrag");
                /*******************************ISTIAQUE***************************************/

                pager.setCurrentItem(1);
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Favorites", "Move", "FavSellerFrag");
                /*******************************ISTIAQUE***************************************/

                pager.setCurrentItem(2);
            }
        });
        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Favorites", "Move", "FavQueryFrag");
                /*******************************ISTIAQUE***************************************/

                pager.setCurrentItem(3);
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tab1.setTextColor(Color.parseColor("#2196F3"));
                        tab1.setTypeface(null, Typeface.BOLD);
                        tab2.setTypeface(null, Typeface.NORMAL);
                        tab2.setTextColor(Color.parseColor("#3c3c3c"));
                        tab3.setTypeface(null, Typeface.NORMAL);
                        tab3.setTextColor(Color.parseColor("#3c3c3c"));
                        tab4.setTypeface(null, Typeface.NORMAL);
                        tab4.setTextColor(Color.parseColor("#3c3c3c"));
                        tab4.setBackgroundResource(R.drawable.line);
                        tab1.setBackgroundResource(R.drawable.line_with_arrow);
                        tab2.setBackgroundResource(R.drawable.line);
                        tab3.setBackgroundResource(R.drawable.line);
                        break;
                    case 1:
                        tab2.setTypeface(null, Typeface.BOLD);
                        tab1.setTypeface(null, Typeface.NORMAL);
                        tab1.setTextColor(Color.parseColor("#3c3c3c"));
                        tab2.setTextColor(Color.parseColor("#2196F3"));
                        tab3.setTypeface(null, Typeface.NORMAL);
                        tab3.setTextColor(Color.parseColor("#3c3c3c"));
                        tab2.setBackgroundResource(R.drawable.line_with_arrow);
                        tab1.setBackgroundResource(R.drawable.line);
                        tab3.setBackgroundResource(R.drawable.line);
                        tab4.setTypeface(null, Typeface.NORMAL);
                        tab4.setTextColor(Color.parseColor("#3c3c3c"));
                        tab4.setBackgroundResource(R.drawable.line);
                        break;
                    case 2:
                        tab3.setTypeface(null, Typeface.BOLD);
                        tab1.setTypeface(null, Typeface.NORMAL);
                        tab1.setTextColor(Color.parseColor("#3c3c3c"));
                        tab3.setTextColor(Color.parseColor("#2196F3"));
                        tab2.setTypeface(null, Typeface.NORMAL);
                        tab2.setTextColor(Color.parseColor("#3c3c3c"));
                        tab3.setBackgroundResource(R.drawable.line_with_arrow);
                        tab1.setBackgroundResource(R.drawable.line);
                        tab2.setBackgroundResource(R.drawable.line);
                        tab4.setTypeface(null, Typeface.NORMAL);
                        tab4.setTextColor(Color.parseColor("#3c3c3c"));
                        tab4.setBackgroundResource(R.drawable.line);
                        break;
                    case 3:
                        tab3.setTypeface(null, Typeface.NORMAL);
                        tab1.setTypeface(null, Typeface.NORMAL);
                        tab1.setTextColor(Color.parseColor("#3c3c3c"));
                        tab3.setTextColor(Color.parseColor("#3c3c3c"));
                        tab2.setTypeface(null, Typeface.NORMAL);
                        tab2.setTextColor(Color.parseColor("#3c3c3c"));
                        tab3.setBackgroundResource(R.drawable.line);
                        tab1.setBackgroundResource(R.drawable.line);
                        tab2.setBackgroundResource(R.drawable.line);
                        tab4.setTypeface(null, Typeface.BOLD);
                        tab4.setTextColor(Color.parseColor("#2196F3"));
                        tab4.setBackgroundResource(R.drawable.line_with_arrow);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SlidePageAdapter extends FragmentStatePagerAdapter
    {
        public SlidePageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FavouriteProductsFragment();
                case 1:
                    return new FavChatFrag();
                case 2:
                    return new FavSellerFrag();
                case 3:
                    return new FavQueryFrag();
            }
            return new Fragment();
        }

        @Override
        public int getCount()
        {
            return 4;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("My Favourite");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
