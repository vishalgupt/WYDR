package wydr.sellers.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;

import wydr.sellers.R;

/**
 * Created by surya on 13/10/15.
 */
public class ForwardActivity extends AppCompatActivity implements View.OnClickListener {
    Button all, recent;
    ViewPager pager;
    TabsForwardAdapter mAdapter;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fordward_activity);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tab_toolbar_forward);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Contact");
        pager = (ViewPager) findViewById(R.id.forward_pager);
        mAdapter = new TabsForwardAdapter(getSupportFragmentManager());
        //  pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        all = (Button) findViewById(R.id.forward_bar_2);
        recent = (Button) findViewById(R.id.forward_bar_1);

        all.setOnClickListener(this);
        recent.setOnClickListener(this);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                Log.d("postiton sel", "" + position);

                switch (position) {
                    case 0:
                        pager.setCurrentItem(0);
                        all.setTextColor(getResources().getColor(R.color.text_color));
                        recent.setTextColor(getResources().getColor(R.color.primary_500));
                        all.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                        recent.setBackgroundResource(R.drawable.my_bussiness_top_arrow);


                        break;
                    case 1:
                        pager.setCurrentItem(1);
                        all.setTextColor(getResources().getColor(R.color.primary_500));
                        recent.setTextColor(getResources().getColor(R.color.text_color));
                        recent.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                        all.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                        break;


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("sss", "" + state);
                // pager.setAdapter(mAdapter);
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forward_bar_1:
                pager.setCurrentItem(0);
                all.setTextColor(getResources().getColor(R.color.text_color));
                recent.setTextColor(getResources().getColor(R.color.primary_500));
                all.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                recent.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                break;
            case R.id.forward_bar_2:
                pager.setCurrentItem(1);
                all.setTextColor(getResources().getColor(R.color.primary_500));
                recent.setTextColor(getResources().getColor(R.color.text_color));
                recent.setBackgroundResource(R.drawable.my_bussiness_top_bar);
                all.setBackgroundResource(R.drawable.my_bussiness_top_arrow);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("ForwardActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
