package wydr.sellers.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Scanner;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;

/**
 * Created by Navdeep on 10/9/15.
 */
public class ProductDetailsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private ImageView sliderMenu;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    Controller application;
    Tracker mTracker;
    String named="WYDR";
    String pid;
    String screenVisited = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("");
        Intent intent = getIntent();
        Uri data = intent.getData();

        if(data != null)
        {

            Scanner in = new Scanner(data.toString()).useDelimiter("[^0-9]+");
            String integer = String.valueOf(in.nextInt());
            pid = integer;

        }
        else
        {
            pid = getIntent().getStringExtra("product_id");
            named = getIntent().getStringExtra("name");
            screenVisited = getIntent().getStringExtra("screenVisited");
        }
        findViewById(R.id.toolbarimg).setVisibility(View.GONE);
        TextView toolbartext = (TextView) findViewById(R.id.toolbartext);
        toolbartext.setText(named);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sliderMenu = (ImageView) findViewById(R.id.btnMenu);
        sliderMenu.setVisibility(View.GONE);

        /*******************************ISTIAQUE***************************************/
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        application.trackEvent("PDP", "Move", "ProductItemDetailsFragment");
        /*******************************ISTIAQUE***************************************/
        
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProductItemDetailsFragment(pid, named, screenVisited)).commit();
        }

    public void fun(View v) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            /*********************** ISTIAQUE: CODE BEGINS *************************************/
            case R.id.search:

                /*******************************ISTIAQUE***************************************/
                application = (Controller) ProductDetailsActivity.this.getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("ProductDetailsActivity", "Move", "SearchActivity");
                /*******************************ISTIAQUE***************************************/

                startActivity(new Intent(ProductDetailsActivity.this, SearchActivity.class));
                break;
            /********************** ISTIAQUE: CODE ENDS ***************************************/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(screenVisited);
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        //Toast.makeText(ProductDetailsActivity.this, screenVisited, Toast.LENGTH_SHORT).show();
    }

    private void setNotifCount() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        /****************ISTIAQUE COMMENTED OUT *******************/
        /*getMenuInflater().inflate(R.menu.search_cart_menu, menu);
        MenuItem item = menu.findItem(R.id.searchCart_cart);*/
        /****************ISTIAQUE COMMENTED OUT *******************/
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

                startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
            }
        });
        /************************ ISTIAQUE: COMMENTED ****************************/
        //MenuItem sitem = menu.findItem(R.id.searchCart_search);
        //sitem.setVisible(false);
        /************************ ISTIAQUE: COMMENTED ****************************/

        /****************** ISTIAQUE: CODE BEGINS **************************/
        MenuItem aItem = menu.findItem(R.id.user_profile);
        aItem.setVisible(false);
        MenuItem addItem = menu.findItem(R.id.add);
        addItem.setVisible(false);
        /****************** ISTIAQUE: CODE ENDS **************************/
        return super.onCreateOptionsMenu(menu);
    }
}
