package wydr.sellers.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

import wydr.sellers.R;
import wydr.sellers.adapter.SearchAdapter;


/**
 * Created by deepesh on 25/6/15.
 */

public class SearchActivity extends AppCompatActivity
{

    public static int hitCounter = 0;
    public static int[] prgmImages = {R.drawable.business_leads, R.drawable.chat_search, R.drawable.share_search, R.drawable.product_search, R.drawable.seller_search};
    public EditText search;
    public Toolbar toolbar;
    public ImageView img;
    public ListView lview;
    Controller application;
    Tracker mTracker;

    //  public static int [] prgmImages={R.drawable.bulb_white,R.drawable.chat_white,R.drawable.blue_bg_share,R.drawable.products,R.drawable.house_with_padding};
    public String[] prgmNameList = {"Business Leads", "Chats", "Connections", "Products", "Sellers"};
    public SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_query);

        toolbar = (Toolbar) findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        hitCounter = 0;
        //   Fabric.with(this, new Crashlytics());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back);
        search = (EditText) findViewById(R.id.searchQ);
        lview = (ListView) findViewById(R.id.search_list);
        adapter = new SearchAdapter(this, prgmNameList, prgmImages);
        lview.setAdapter(adapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (search.getText().toString().trim().length() > 0) {
                    switch (position) {
                        case 0:

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) SearchActivity.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("SearchActivity", "Move", "SearchFeed");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(SearchActivity.this, SearchFeed.class).putExtra("data", search.getText().toString().trim()));
                            break;
                        case 1:

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) SearchActivity.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("SearchActivity", "Move", "SearchResult");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(SearchActivity.this, SearchResult.class).putExtra("query", search.getText().toString().trim()));
                            break;
                        case 2:

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) SearchActivity.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("SearchActivity", "Move", "NetworkSearchResult");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(SearchActivity.this, NetworkSearchResult.class).putExtra("query", search.getText().toString().trim()));
                            break;
                        case 3:

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) SearchActivity.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("SearchActivity", "Move", "ProductSearch");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(SearchActivity.this, ProductSearch.class).putExtra("query", search.getText().toString().trim()));
                            break;
                        case 4:

                            /*******************************ISTIAQUE***************************************/
                            application = (Controller) SearchActivity.this.getApplication();
                            mTracker = application.getDefaultTracker();
                            application.trackEvent("SearchActivity", "Move", "SearchSellers");
                            /*******************************ISTIAQUE***************************************/

                            startActivity(new Intent(SearchActivity.this, SearchSellers.class).putExtra("search_string", search.getText().toString().trim()));
                            break;

                    }
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setTitle(getResources().getString(R.string.alert)).
                            setMessage(getString(R.string.enter_keyword))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    builder.show();
                }

            }
        });

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.search:

                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SearchActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}


