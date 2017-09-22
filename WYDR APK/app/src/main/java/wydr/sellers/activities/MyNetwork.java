package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import android.os.Handler;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.MyNetworkAdapter2;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.ContactsDb;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by surya on 27/8/15.
 */
public class MyNetwork extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public String username, user_cur_id, compid, netid;
    ListView listUser;
    MyNetworkAdapter2 adapter;
    View v;
    ImageView uf;
    LinearLayout ufll;
    ArrayList<String>ufList= new ArrayList<>();
    PrefManager prefManager;
    Button addconnection_btn;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog alertDialog;
    ConnectionDetector cd;
    AlertDialog.Builder alertDialog1, alertDialog2;
    JSONParser parser;
    String sortingOrder = "(CASE display_name  when \"\" THEN 0 ELSE 1 END ) DESC , display_name";
    Helper helper = new Helper();
    private ProgressDialog progress;
    RelativeLayout noConnectionInvite_layout;
    TextView record_status, helpTitle;
    Controller application;
    Tracker mTracker;
    long startTime = 0;
    FloatingActionButton networkFloatBtn;
    int total , failure = 0;
    FrameLayout.LayoutParams layoutParams;
    ImageButton networkFloatBtnClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.my_network, container, false);
        v = view;
        iniStuff(view);
        cd = new ConnectionDetector(getActivity());
        prefManager = new PrefManager(getActivity());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        alertDialog = new Dialog(getActivity(), R.style.mydialogstyle);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.overlay_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        /*************** ISTIAQUE *************************/
        setOnClick();
        /**************************************************/
        return view;
    }

    private void setOnClick() {

        networkFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("MyNetwork", "Move", "Help");
                startActivity(new Intent(getActivity(), Help.class));
                /*******************************ISTIAQUE***************************************/
            }
        });


        networkFloatBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkFloatBtn.setVisibility(View.GONE);
                networkFloatBtnClose.setVisibility(View.GONE);
                helpTitle.setVisibility(View.GONE);
            }
        });




        /*floatBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(floatBtn);

                    floatBtn.startDrag(data, shadowBuilder, floatBtn, 0);
                    //floatBtn.setVisibility(View.INVISIBLE);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });


        floatBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(),mimeTypes, item);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(floatBtn);
                v.startDrag(dragData, shadow, null, 0);
                return true;
            }
        });


        floatBtn.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch(action) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                        Log.d("msg", "Action is DragEvent.ACTION_DRAG_STARTED");
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d("msg", "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d("msg", "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d("msg", "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DROP:{
                        Log.d("msg", "ACTION_DROP event");
                        failure = failure+1;
                        return(true);
                        break;
                        }

                    case DragEvent.ACTION_DRAG_ENDED:{
                        Log.d("msg", "Action is DragEvent.ACTION_DRAG_ENDED");
                        total = total +1;
                        int suc = total - failure;
                        sucess.setText("Sucessful Drops :"+suc);
                        text.setText("Total Drops: "+total);
                        return(true);
                        break;

                    }

                    default:
                        break;
                }
                return true;
            }
        });*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        uf = (ImageView)getActivity().findViewById(R.id.uf);

        ufll = (LinearLayout)getActivity().findViewById(R.id.ufll);
        uf.setVisibility(View.GONE);
        uf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Catalog.class);
                startActivity(intent);
            }
        });
        ufll.setVisibility(View.GONE);
        if(ufList.contains(AppUtil.TAG_Network))
        {
            ufll.setVisibility(View.GONE);
            uf.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(helper.getDefaults(AppUtil.TAG_Network+"_photo",getActivity()))
                    .into(uf);
        }
        else
        {
            ufll.setVisibility(View.VISIBLE);
            uf.setVisibility(View.GONE);
         /*   if (adapter.getCount() == 0){
                record_status.setVisibility(View.VISIBLE);
                listUser.setVisibility(View.GONE);
            } else {
                record_status.setVisibility(View.GONE);
                listUser.setVisibility(View.VISIBLE);
            }*/
        }
    }



    private void iniStuff(View view)
    {
        listUser = (ListView) view.findViewById(R.id.listViewMyNetwork);
        record_status = (TextView) view.findViewById(R.id.record_status);
        networkFloatBtn = (FloatingActionButton) view.findViewById(R.id.networkFloatBtn);
        networkFloatBtnClose = (ImageButton) view.findViewById(R.id.networkFloatBtnClose);
        helpTitle = (TextView) view.findViewById(R.id.helpTitle);
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
        noConnectionInvite_layout = (RelativeLayout) view.findViewById(R.id.noConnectionInvite_layout);
        addconnection_btn = (Button)view.findViewById(R.id.addConnection_btn);
        addconnection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("MyNetwork", "Move", "Contact");
                /*******************************ISTIAQUE***************************************/
                startActivity(new Intent(getActivity(), Contact.class));

            }
        });

        adapter = new MyNetworkAdapter2(this.getActivity(), null);
        listUser.setAdapter(adapter);
        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*******************************ISTIAQUE***************************************/
                application = (Controller) getActivity().getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("My Connections", "onClick", "MyNetwork");
                /*******************************ISTIAQUE***************************************/

                Log.e("POSITO", position + "");
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                //String vendor_id = cursor.getString(cursor.getColumnIndexOrThrow(NetSchema.USER_ID));
                // final Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                Log.e("Count", cursor.getCount() + "");
                int iName = cursor.getColumnIndexOrThrow(NetSchema.USER_DISPLAY);
                final int iNet = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                int iNo = cursor.getColumnIndexOrThrow(NetSchema.USER_PHONE);
                final int iComapnyid = cursor.getColumnIndexOrThrow(NetSchema.USER_COMPANY_ID);
                final int userid = cursor.getColumnIndexOrThrow(NetSchema.USER_ID);
                final String user_name;
                if (cursor.getString(iName).length() > 2) {
                    user_name = cursor.getString(iName);
                }

                else {
                    user_name = cursor.getString(iNo);
                }

                user_cur_id = cursor.getString(userid);
                compid = cursor.getString(iComapnyid);
                netid = cursor.getString(iNet);
                ImageView profile = (ImageView) alertDialog.findViewById(R.id.profile);
                ImageView query = (ImageView) alertDialog.findViewById(R.id.query);
                ImageView catalog = (ImageView) alertDialog.findViewById(R.id.catalog);
                LinearLayout linearLayout = (LinearLayout) alertDialog.findViewById(R.id.overlayLayout);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                profile.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.i("click", "profile");
                        if (cd.isConnectingToInternet())
                        {
                            if (user_name != null && compid != null && user_cur_id != null)
                                startActivity(new Intent(getActivity(), SellerProfile.class).putExtra("userid", user_cur_id).putExtra("username", user_name).putExtra("company_id", compid).putExtra("BackFlag", "Home"));
                        }

                        else
                        {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                        }
                        alertDialog.dismiss();
                    }
                });

                query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("click", "query");
                        Log.i("click", "user_name" + "=" + user_name + "/" + "compid" + "=" + compid);
                        if (cd.isConnectingToInternet()) {
                            if (user_name != null && compid != null && user_cur_id != null && netid != null)
                                startActivity(new Intent(getActivity(), SellersQuery.class).putExtra("seller_id", user_cur_id).putExtra("username", user_name).putExtra("company_id", compid).putExtra("userid", netid).putExtra("user_id", user_cur_id).putExtra("BackFlag", "Home" + "/1"));

                        } else {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                        }
                        alertDialog.dismiss();
                    }
                });
                catalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cd.isConnectingToInternet()) {
                            if (user_name != null && compid != null && user_cur_id != null && netid != null)
                                startActivity(new Intent(getActivity(), SellersCatalog.class).putExtra("username", user_name).putExtra("company_id", compid).putExtra("seller_id", user_cur_id).putExtra("userid", netid).putExtra("user_id", user_cur_id).putExtra("BackFlag", "Home" + "/1"))
                                        ;
                        } else {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                        }

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        listUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listUser.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {

                final int checkedCount = listUser.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                alertDialog2 = new AlertDialog.Builder(getActivity());
                alertDialog2.setTitle(getResources().getString(R.string.delete_connection))
                        .setMessage(getResources().getString(R.string.are_u_sure))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (cd.isConnectingToInternet()) {
                                    SparseBooleanArray selected = adapter.getSelectedIds();

                                    for (int i = (selected.size() - 1); i >= 0; i--) {
                                        if (selected.valueAt(i)) {
                                            Cursor selecteditem = (Cursor) adapter.getItem(selected.keyAt(i));
                                            int USER_ID = selecteditem.getColumnIndexOrThrow(NetSchema.USER_ID);
                                            new DeleteConnection().execute(selecteditem.getString(USER_ID));
                                        }


                                    }
                                    mode.finish();

                                } else {
                                    new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                                }
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                alertDialog2.show();
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.list_del_user, menu);
                getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                adapter.removeSelection();
                getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this.getActivity(), ChatProvider.NET_URI, null, NetSchema.USER_STATUS + "=?", new String[]{"1"}, sortingOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Log.d("count",String.valueOf(listUser.getCount()));
        Log.d("count",String.valueOf(listUser.getCount()));
        if (listUser.getAdapter().getCount() == 0){
            /*record_status.setVisibility(View.VISIBLE);
            invite_btn.setVisibility(View.VISIBLE);*/
            noConnectionInvite_layout.setVisibility(View.VISIBLE);
            listUser.setVisibility(View.GONE);
        } else {
            /*record_status.setVisibility(View.GONE);
            invite_btn.setVisibility(View.GONE);*/
            noConnectionInvite_layout.setVisibility(View.GONE);
            listUser.setVisibility(View.VISIBLE);
        }

        switch (loader.getId()) {
            case 1:
                adapter.swapCursor(data);
                adapter.notifyDataSetChanged();
                break;
        }
      /*  if (listUser.getAdapter().getCount() == 0){
            record_status.setVisibility(View.VISIBLE);
            listUser.setVisibility(View.GONE);
        } else {
            record_status.setVisibility(View.GONE);
            listUser.setVisibility(View.VISIBLE);
        }



        switch (loader.getId()) {
            case 1:
                adapter.swapCursor(data);
                adapter.notifyDataSetChanged();
                break;
        }*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(1, null, this);
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName("My Network");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("My Connections").setValue(System.currentTimeMillis() - startTime).build());
        super.onResume();
        getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);

       /************************ ISTIAQUE *****************************/

        networkFloatBtn.setVisibility(View.VISIBLE);
        networkFloatBtnClose.setVisibility(View.VISIBLE);
        helpTitle.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                helpTitle.setVisibility(View.GONE);
            }
        }, 8000);

        /*******************************************************************/
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
    }

    private class DeleteConnection extends AsyncTask<String, String, JSONObject> {
        final Calendar c = Calendar.getInstance();
        int flag = 0;
        JSONObject table = new JSONObject();
        String arguments;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            if (getActivity() != null && !getActivity().isFinishing() && isAdded())
                progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            arguments = args[0];
            UserFunctions userFunction = new UserFunctions();
            try {
                table.put("user_id", args[0]);
                table.put("delete", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String userid = helper.getDefaults("user_id", getActivity());
            JSONObject json = userFunction.DeleteConnection(table, userid, getActivity());
            if (json != null) {
                if (json.has("message")) {
                    try {
                        Log.i("message --- ", json.getString("message"));
                        if (json.getString("message").contains("User is removed from your network"))
                            flag = 0;
                        else
                            flag = 2;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (json.has("error")) {
                    flag = 2;
                }
            } else {
                flag = 1;
            }
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json)
        {
            Log.e("1---", "");
            if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                progress.dismiss();
                if (flag == 1) {
                    alertDialog2.setTitle(getResources().getString(R.string.sorry));
                    alertDialog2.setMessage(getResources().getString(R.string.server_error));
                    alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
                    alertDialog2.show();
                } else if (flag == 2) {
                    alertDialog2.setTitle(getResources().getString(R.string.error));
                    try {
                        alertDialog2.setMessage(json.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
                    alertDialog2.show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(NetSchema.USER_STATUS, "0");
                    int a = getActivity().getContentResolver().update(ChatProvider.NET_URI, values, NetSchema.USER_ID + "=?", new String[]{arguments});
                    values.put(ContactsDb.KEY_STATUS, "1");
                    int b = getActivity().getContentResolver().update(ChatProvider.BOOK_URI, values, ContactsDb.USER_ID + "=?", new String[]{arguments});
                    getActivity().getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);

                }
            }

        }
    }

}
