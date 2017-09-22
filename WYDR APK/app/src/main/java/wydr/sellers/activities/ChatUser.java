package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.jivesoftware.smack.RosterEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.ChatSchema;
import wydr.sellers.acc.ChatUserSchema;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.adapter.UserAdapter2;
import wydr.sellers.gson.MessageRequest;
import wydr.sellers.modal.User;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClientOpenfire;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by surya on 14/7/15.
 */
public class ChatUser extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listUser;
    ArrayList<User> list;
    UserAdapter2 adapter;
    List<RosterEntry> entries;
    XmppConnection connection;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    View v;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Cursor myCursor;
    View footerView;
    Helper helper = new Helper();
    private ProgressDialog progress;
    private boolean success = false;
    Toolbar mToolbar;
    TextView textView, record_status, chatHelpTitle;
    RelativeLayout noChatRecordInvite_layout;
    String userLogin;
    Controller application;
    Tracker mTracker;
    Button chatAddConnection_btn;
    long startTime = 0;
    FloatingActionButton floatBtn;
    ImageButton close_help;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        startTime = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.chat_user, container, false);
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.chat_footer, null, false);
        v = view;
        progressStuff();
        userLogin = helper.getDefaults("login_id", getActivity()) + "@" + AppUtil.SERVER_NAME;
        if (!new SessionManager(getActivity()).isChatIn()) {
            getChatUser(new MessageRequest(userLogin, "last_chat_user", "", "", ""));
        }
        iniStuff(view);

        /************************ ISTIAQUE *********************************/
        setOnClick();
        /************************ ISTIAQUE *********************************/
        chatAddConnection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Chat User", "Move", "Contact");
                /*******************************ISTIAQUE***************************************/
                startActivity(new Intent(getActivity(), Contact.class));

            }
        });



        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);

                /*******************************ISTIAQUE***************************************/
                application = (Controller) getActivity().getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("Chat User", "onClick", "ChatUser");
                /*******************************ISTIAQUE***************************************/

                // listUser.setSelection(position);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int iChatUser = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_CHAT_USER);
                int iRowID = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_ROWID);
                int iType = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_TYPE);
                if (cursor.getString(iType).equals("broadcast")) {
                    startActivity((new Intent(getActivity(), Broadcast.class)).putExtra("user", cursor.getString(iChatUser)).putExtra("id", cursor.getString(iRowID)));
                } else {
                    //    Log.i("Chatiser", cursor.getString(iChatUser));
                    startActivity((new Intent(getActivity(), ChatActivity.class)).putExtra("user", cursor.getString(iChatUser)));
                }
            }
        });

        return view;
    }

    private void setOnClick() {

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent(getResources().getString(R.string.chat_user), "Move", "Help");
                startActivity(new Intent(getActivity(), Help.class));
                /*******************************ISTIAQUE***************************************/
            }
        });


        close_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatBtn.setVisibility(View.GONE);
                close_help.setVisibility(View.GONE);
                chatHelpTitle.setVisibility(View.GONE);
            }
        });
    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getActivity());
        parser = new JSONParser();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        // progress.show();
    }


    private void iniStuff(View view)
    {
        listUser = (ListView) view.findViewById(R.id.listViewChatUser);
        record_status = (TextView) view.findViewById(R.id.record_status);
        floatBtn = (FloatingActionButton) view.findViewById(R.id.floatBtnChat);
        close_help = (ImageButton) view.findViewById(R.id.close_help);
        chatHelpTitle = (TextView) view.findViewById(R.id.chatHelpTitle);
        noChatRecordInvite_layout = (RelativeLayout) view.findViewById(R.id.noChatRecordInvite_layout);
        chatAddConnection_btn = (Button) view.findViewById(R.id.chatAddConnection_btn);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        getActivity().getSupportLoaderManager().initLoader(2, null, this);
        adapter = new UserAdapter2(this.getActivity(), null);
        listUser.setAdapter(adapter);
        listUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listUser.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = listUser.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.list_menu, menu);
                mToolbar.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getResources().getString(R.string.delete_connection))
                        .setMessage(getResources().getString(R.string.delete_chat))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Cursor cursor = adapter.getCursor();
                                int iUserId = cursor.getColumnIndexOrThrow("user_id");

                                int iChatUser = cursor.getColumnIndexOrThrow(ChatUserSchema.KEY_CHAT_USER);

                                SparseBooleanArray checked = listUser.getCheckedItemPositions();

                                for (int i = 0; i < listUser.getAdapter().getCount(); i++) {
                                    if (checked.get(i)) {
                                        // Do something
                                        cursor.moveToPosition(i);
                                        getActivity().getContentResolver().delete(ChatProvider.CHAT_USER_URI, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{cursor.getString(iChatUser)});
                                        getActivity().getContentResolver().delete(ChatProvider.CONTENT_URI, ChatSchema.KEY_RECEIVER + "=? OR " + ChatSchema.KEY_SENDER + "=?", new String[]{cursor.getString(iChatUser), cursor.getString(iChatUser)});
                                    }
                                }
                                mode.finish();
                                getActivity().getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                getActivity().getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        mode.finish();
                    }
                });
                dialog.show();


                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.removeSelection();
                mToolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //return new CursorLoader(this.getActivity(), ChatProvider.CHAT_USER_URI, null, null, null, ChatUserSchema.KEY_CREATED + " DESC");
        return new CursorLoader(this.getActivity(), ChatProvider.CHAT_USER_URI, null, null, null, "broadcast" + " ASC," + ChatUserSchema.KEY_CREATED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (listUser.getAdapter().getCount() > 0)
        {
            //record_status.setVisibility(View.GONE);
            noChatRecordInvite_layout.setVisibility(View.GONE);
            listUser.setVisibility(View.VISIBLE);
            // listUser.ha
            if (listUser.getFooterViewsCount() == 0)
            {
                listUser.addFooterView(footerView);
                adapter.notifyDataSetChanged();
            }
        }
        else
        {
            //record_status.setVisibility(View.VISIBLE);
            noChatRecordInvite_layout.setVisibility(View.VISIBLE);
            listUser.setVisibility(View.GONE);
        }
        switch (loader.getId())
        {
            case 2:
                adapter.swapCursor(data);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onResume()
    {

        //floatBtn.setVisibility(View.VISIBLE);

        getLoaderManager().restartLoader(2, null, this);
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName(getResources().getString(R.string.chat_user));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Chat").setValue(System.currentTimeMillis() - startTime).build());

        /************************** ISTIAQUE ***************************************/

        floatBtn.setVisibility(View.VISIBLE);
        close_help.setVisibility(View.VISIBLE);
        chatHelpTitle.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                chatHelpTitle.setVisibility(View.GONE);
            }
        }, 8000);

        /******************************************************************************/

        super.onResume();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void getChatUser(MessageRequest request) {

        Gson gson = new Gson();
        Log.d("HAS REQUEST", "" + gson.toJson(request));
        if (!progress.isShowing()) {
            progress.show();
        }
        RestClientOpenfire.GitApiInterface service = RestClientOpenfire.getClient();
        Call<JsonElement> call = service.getMessage(request);
        call.enqueue(new Callback<JsonElement>() {
                         @Override
                         public void onResponse(Response response) {
                             if (getActivity() != null && !getActivity().isFinishing()) {
                                 Log.d("response", "" + response.raw() + " " + response.code());
                                 progress.dismiss();
                                 if (response.isSuccess()) {
                                     JsonElement element = (JsonElement) response.body();
                                     if (element != null && element.isJsonObject()) {
                                         try {
                                             JSONObject json = new JSONObject(element.getAsJsonObject().toString());
                                             JSONArray array = json.getJSONArray("users");
                                             for (int i = 0; i < array.length(); i++) {
                                                 Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CHAT_USER_URI, null, ChatUserSchema.KEY_CHAT_USER + "=?", new String[]{array.optString(i)}, null);
                                                 if (cursor.getCount() == 0) {
                                                     Cursor netCursor = getActivity().getContentResolver().query(ChatProvider.NET_URI, new String[]{NetSchema.USER_ID}, NetSchema.USER_NET_ID + "=?", new String[]{array.optString(i)}, null);
                                                     if (netCursor.getCount() > 0) {
                                                         ContentValues cv = new ContentValues();
                                                         //  cv.put(ChatUserSchema.KEY_LAST_MSG, messageId);
                                                         cv.put(ChatUserSchema.KEY_UNREAD, 0);
                                                         cv.put(ChatUserSchema.KEY_CHAT_USER, array.optString(i));
                                                         cv.put(ChatUserSchema.KEY_DIRECTION, ChatUserSchema.OUTGOING);
                                                         cv.put(ChatUserSchema.KEY_TYPE, "chat");
                                                         cv.put(ChatUserSchema.KEY_CREATED, format.format(Calendar.getInstance().getTime()));
                                                         Uri uri = getActivity().getContentResolver().insert(ChatProvider.CHAT_USER_URI, cv);
                                                         getActivity().getContentResolver().notifyChange(ChatProvider.CHAT_USER_URI, null, false);
                                                     }
                                                 }
                                             }
                                             new SessionManager(getActivity()).createSession();

                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                     }

                                 } else {
                                     int statusCode = response.code();
                                     if (statusCode == 401) {

                                         final SessionManager sessionManager = new SessionManager(getActivity());
                                         Handler mainHandler = new Handler(Looper.getMainLooper());
                                         Runnable myRunnable = new Runnable() {
                                             @Override
                                             public void run() {
                                                 sessionManager.logoutUser();
                                             } // This is your code
                                         };
                                         mainHandler.post(myRunnable);
                                     } else {
                                         if (getActivity() != null && !getActivity().isFinishing()) {
                                             new AlertDialogManager().showAlertDialog(getActivity(),
                                                     getString(R.string.error),
                                                     getString(R.string.server_error));
                                         }
                                     }
                                 }

                             }
                         }

                         @Override
                         public void onFailure(Throwable t) {
                             if (getActivity() != null && !getActivity().isFinishing()) {
                                 progress.dismiss();
                             }
                             Log.d("Here", t.toString());


                         }
                     }

        );

    }

}