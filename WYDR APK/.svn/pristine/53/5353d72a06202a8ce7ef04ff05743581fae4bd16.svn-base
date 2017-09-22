package wydr.sellers.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.BroadcastUser;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.MemberAdapter;
import wydr.sellers.adapter.MemberListAdapter;
import wydr.sellers.modal.Member;
import wydr.sellers.network.AlertDialogManager;

/**
 * Created by surya on 6/1/16.
 */
public class BroadcastDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {
    TextView title, created;
    ListView listView;
    AutoCompleteTextView editRecipients;
    Toolbar mToolbar;
    ArrayList<Member> arrayList, memberArrayList;
    MemberListAdapter adapter;
    MemberAdapter memberAdapter;
    String name, id;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_detail);
        //    ButterKnife.inject(this);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        arrayList = new ArrayList<>();
        memberArrayList = new ArrayList<>();
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.textViewTitleBroadcast);
        created = (TextView) findViewById(R.id.textViewCreatedTime);
        editRecipients = (AutoCompleteTextView) findViewById(R.id.editTextAddRecipients);
        listView = (ListView) findViewById(R.id.listViewAddRecipients);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);
        Cursor cursor = getContentResolver().query(ChatProvider.BROADCAST_USER_URI, null, BroadcastUser.KEY_BID + "=?", new String[]{id}, null);
        title.setText("" + cursor.getCount() + " Recipient");
        cursor.moveToFirst();
        int iCreated = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_CREATED);
        created.setText("Created at " + cursor.getString(iCreated));
        //  int iName = cursor.getColumnIndexOrThrow(BroadcastUser.USER_DISPLAY);
        int iName = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_NAME);
        int iJid = cursor.getColumnIndexOrThrow(BroadcastUser.KEY_JID);
        //int iUrl = cursor.getColumnIndexOrThrow(BroadcastUser.USER_IMAGE);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            Member member = new Member();
//            //    member.setUser_id(cursor.getString(iId));
            member.setName(cursor.getString(iName));
            member.setJid(cursor.getString(iJid));
//            member.setName(cursor.getString(iName));

            memberArrayList.add(member);
            // Log.d("Member =>", member.toString());

        }
        memberAdapter = new MemberAdapter(this, memberArrayList);
        listView.setAdapter(memberAdapter);


        addListUser();
    }

    public void onUserDelete(View view) {
        if (memberArrayList.size() ==2) {
            new AlertDialogManager().showAlertDialog(BroadcastDetail.this, getString(R.string.oops), getString(R.string.recipent));
        } else {
            Member member = (Member) view.getTag();
            memberArrayList.remove(member);
            memberAdapter.notifyDataSetChanged();
            int count = getContentResolver().delete(ChatProvider.BROADCAST_USER_URI, BroadcastUser.KEY_BID + "=? AND " + BroadcastUser.KEY_JID + "=?", new String[]{id, member.getJid()});

        }
    }

    private void addListUser() {
        Cursor cursor = getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_STATUS + "=?", new String[]{"1"}, null);
        int iId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
        int iName = cursor.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
        int iNetId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
        int iNo = cursor.getColumnIndexOrThrow(NetSchema.USER_PHONE);
        int iUrl = cursor.getColumnIndexOrThrow(NetSchema.USER_IMAGE);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            Member member = new Member();
            member.setUser_id(cursor.getString(iId));
            member.setName(cursor.getString(iUrl));
            member.setJid(cursor.getString(iNetId));
            if (cursor.getString(iName).length() > 1) {
                member.setName(cursor.getString(iName));
            } else {
                member.setName(cursor.getString(iNo));
            }
            arrayList.add(member);
            // Log.d("Member =>", member.toString());

        }
        adapter = new MemberListAdapter(this, arrayList);
        editRecipients.setAdapter(adapter);
        //   autoCompleteTextView.setOnItemSelectedListener(this);
        editRecipients.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
    //onI

//    case android.R.id.home:
//            NavUtils.navigateUpFromSameTask(this);
//    return true;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {
        Member member = (Member) parent.getItemAtPosition(position);
        if (!equals(memberArrayList, member)) {
            memberArrayList.add(member);
            memberAdapter.notifyDataSetChanged();
            editRecipients.setText("");
            ContentValues values = new ContentValues();
            values.put(BroadcastUser.KEY_BID, "" + id);
            values.put(BroadcastUser.KEY_NAME, member.getName());
            values.put(BroadcastUser.KEY_JID, member.getJid());
            values.put(BroadcastUser.KEY_STATUS, 1);
            values.put(BroadcastUser.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
            Uri uriUser = getContentResolver().insert(ChatProvider.BROADCAST_USER_URI, values);
            Toast.makeText(this, getString(R.string.persion), Toast.LENGTH_LONG).show();
        } else {
            editRecipients.setText("");
            new AlertDialogManager().showAlertDialog(BroadcastDetail.this, getResources().getString(R.string.alert), getString(R.string.person_already_added));
        }
    }

    public boolean equals(ArrayList memberList, Member object) {
        List<Member> listOfCircles = memberList;
        boolean flag = true;

        for (Member currentCircle : listOfCircles) {
            if (currentCircle.getName().equals(object.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
