package wydr.sellers.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.BookSchema;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.FavChat;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.FavChatAdapter;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;

/**
 * Created by Deepesh_pc on 18-09-2015.
 */
public class FavChatFrag extends Fragment implements SearchView.OnQueryTextListener {
    ArrayList<FavChat> favChats;
    FavChatAdapter favChatAdapter;
    ListView listView;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    TextView order_status;
    View v;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myfavfragment, null);
        setHasOptionsMenu(true);
        iniStuff(rootView);
        progressStuff();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavChat chat = (FavChat) parent.getItemAtPosition(position);
                startActivity(new Intent(new Intent(getActivity(), ChatActivity.class)).putExtra("id", Integer.parseInt(chat.getrId())).
                        putExtra("user", chat.getUser_id()));
            }
        });
        v = rootView;
        return rootView;
    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(this.getActivity());
        parser = new JSONParser();

    }

    private void iniStuff(View rootView) {
        listView = (ListView) rootView.findViewById(R.id.listfavseller);
        order_status = (TextView) rootView.findViewById(R.id.record_status);
        favChats = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(ChatProvider.BOOKMARK_URI, null, null, null, null);
        cursor.setNotificationUri(getActivity().getContentResolver(), ChatProvider.CONTENT_URI);
        int iId = cursor.getColumnIndexOrThrow(BookSchema.KEY_ROWID);
        int IMsgId = cursor.getColumnIndexOrThrow(BookSchema.KEY_MSG_ID);
        int iMsg = cursor.getColumnIndexOrThrow(BookSchema.KEY_MSG);
        int iIsMe = cursor.getColumnIndexOrThrow(BookSchema.IsMe);
        int iSub = cursor.getColumnIndexOrThrow(BookSchema.KEY_SUB);
        int iUser = cursor.getColumnIndexOrThrow(BookSchema.KEY_USER);
        int iUserID = cursor.getColumnIndexOrThrow(BookSchema.KEY_USER_ID);
        int iCreated = cursor.getColumnIndexOrThrow(BookSchema.KEY_CREATED);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            FavChat favChat = new FavChat();
            favChat.setrId(cursor.getString(iId));
            favChat.setMsg(cursor.getString(iMsg));
            favChat.setId(cursor.getString(IMsgId));
            favChat.setName(cursor.getString(iUser));
            favChat.setSubject(cursor.getString(iSub));
            favChat.setDate(cursor.getString(iCreated));
            favChat.setUser_id(cursor.getString(iUserID));
            if (cursor.getString(iSub) == null) {
                if (cursor.getInt(iIsMe) == 0) {
                    favChat.setIndicator(0);
                } else if (cursor.getInt(iIsMe) == 1) {
                    favChat.setIndicator(1);
                }
            } else {
                favChat.setIndicator(2);
            }
            Log.i("Fav chat image", "cursor1.getString(0) -" + cursor.getString(iUserID) + "--" + cursor.getString(iUserID));
            Cursor cursor1 = getActivity().getContentResolver().query(ChatProvider.NET_URI, new String[]{NetSchema.USER_IMAGE, NetSchema.USER_ID}, NetSchema.USER_NET_ID + "=?", new String[]{cursor.getString(iUserID)}, null);
            Log.i("Fav chat image", "cursor1.getcouny(0) -" + cursor1.getCount());
            while (cursor1.moveToNext()) {
                favChat.setUrl(cursor1.getString(0));
                Log.i("Fav chat image", "cursor1.getString(0) -" + cursor1.getString(0));
            }
            favChats.add(favChat);
            cursor1.close();
        }
        favChatAdapter = new FavChatAdapter(getActivity(), favChats);
        listView.setAdapter(favChatAdapter);
        favChatAdapter.notifyDataSetChanged();
        if (favChats.size() == 0) {
            listView.setVisibility(View.GONE);
            order_status.setVisibility(View.VISIBLE);
        }
        cursor.close();


    }

    @Override
    public void onResume() {
        super.onResume();
        setNotifCount();
        iniStuff(v);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_cart_menu, menu);
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
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");
                startActivity(new Intent(getActivity(), CartActivity.class));
            }
        });


        SearchView searchView = (SearchView) menu.findItem(R.id.searchCart_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setNotifCount() {
        Cursor mCursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null) {
            if (mNotifCount == 0) {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            } else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {


        if (TextUtils.isEmpty(newText)) {
            favChatAdapter.getFilter().filter("");

            listView.clearTextFilter();
        } else {

            favChatAdapter.getFilter().filter(newText.toString());
        }
        return true;
    }

}

