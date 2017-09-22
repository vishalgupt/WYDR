package wydr.sellers.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import wydr.sellers.R;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.AddChatAdapter;

/**
 * Created by surya on 26/8/15.
 */
public class StartChat extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    AddChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_feed);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.start_chat));
        iniStuff();
    }

    private void iniStuff() {
        listView = (ListView) findViewById(R.id.listViewFeed);
        getSupportLoaderManager().initLoader(1, null, this);
        adapter = new AddChatAdapter(this, null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) StartChat.this.getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Start New Chat", "Move", "ChatActivity");
                /*******************************ISTIAQUE***************************************/

                Cursor data = (Cursor) parent.getItemAtPosition(position);
                int iId = data.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                // data.moveToFirst();
                startActivity((new Intent(StartChat.this, ChatActivity.class)).putExtra("user", data.getString(iId)));
            }
        });
        LinearLayout ll = (LinearLayout) findViewById(R.id.mqbottombar);
        ll.setVisibility(View.GONE);
        ImageView img = (ImageView) findViewById(R.id.dateFilterQuery);
        img.setVisibility(View.GONE);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortingOrder = "(CASE display_name  when \"\" THEN 0 ELSE 1 END ) DESC , display_name";
        return new CursorLoader(this, ChatProvider.NET_URI, null, NetSchema.USER_STATUS + "=?", new String[]{"1"}, sortingOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter == null)
            adapter = new AddChatAdapter(StartChat.this, null);
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("StartChat");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
