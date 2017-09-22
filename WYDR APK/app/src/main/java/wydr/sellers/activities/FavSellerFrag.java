package wydr.sellers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.modal.UserProfile;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.DrawableBackgroundDrawable;
import wydr.sellers.slider.UserFunctions;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Deepesh_pc on 18-09-2015.
 */
public class FavSellerFrag extends Fragment implements SearchView.OnQueryTextListener {
    ArrayList<UserProfile> list;
    FavSellerAdapter adapter;
    ListView listView;
    JSONParser parser;
    SessionManager session;
    Helper helper = new Helper();
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    String str;
    AlertDialog.Builder alertDialog;
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    private ProgressDialog progress;
    private ConnectionDetector cd;
    private String user_id;
    TextView order_status;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    String chat_user_id;
    int flag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favsellerfrag, null);
        user_id = helper.getDefaults("user_id", getActivity());
        iniStuff(rootView);
        progressStuff();
        NetAsync();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUff();
    }

    private void initUff()
    {
        ArrayList<String>ufList;
        Helper helper = new Helper();
        ImageView iv_bussiness=(ImageView)getActivity().findViewById(R.id.uf_myfav1);
        iv_bussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getActivity(),Catalog.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        LinearLayout ll= (LinearLayout)getActivity().findViewById(R.id.akill);
        PrefManager prefManager = new PrefManager(getActivity());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));

        if(ufList.contains(AppUtil.TAG_My_Favourites_Seller))
        {
            ll.setVisibility(View.GONE);
            iv_bussiness.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(helper.getDefaults(AppUtil.TAG_My_Favourites_Seller+"_photo",getActivity()))
                    .into(iv_bussiness);
        }
        else
        {
            ll.setVisibility(View.VISIBLE);
            iv_bussiness.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        setNotifCount();
        super.onResume();
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

    public void NetAsync() {

        if (cd.isConnectingToInternet()) {

            new BringFavSellers().execute();

        } else {
            new AlertDialogManager().showAlertDialog(this.getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
    }


    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(this.getActivity());
        parser = new JSONParser();
        progress = new ProgressDialog(this.getActivity());
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(this.getActivity());
    }

    private void iniStuff(View rootView) {
        page_no = 1;
        isLoading = true;
        listView = (ListView) rootView.findViewById(R.id.listfavseller1);
        order_status = (TextView) rootView.findViewById(R.id.record_status1);
        list = new ArrayList<>();
        loadingFooter = LayoutInflater.from(getActivity()).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        adapter = new FavSellerAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isLoading || page_no == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    Log.i("", "end reached");
                    page_no++;
                    Log.i("", "loading page_no " + page_no);
                    NetAsync();
                }
            }
        });

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        //

        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
            //
            listView.clearTextFilter();
        } else {
            //
            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.notifyDataSetChanged();
        progress.dismiss();
    }

    private class BringFavSellers extends AsyncTask<String, String, JSONObject> {

        public String error = "";
        public int flag = 0;
        List<String> imagearray;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress.show();
            isLoading = true;
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            if (getActivity() != null && isAdded()) {
                json = userFunction.GetLikes("vendor", user_id, page_no, getActivity());
                if (json != null) {
                    Log.e("JSON--", json.toString());
                    if (json.has("favourite_data")) {
                        try {
                            JSONArray array = json.getJSONArray("favourite_data");
                            if (array.length() == 0) {
                                page_no = 0;
                                return null;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                imagearray = new ArrayList<>();
                                JSONObject object = array.getJSONObject(i);
                                UserProfile modal = new UserProfile();
                                modal.setId(object.getString("id"));
                                modal.setUserid(object.getString("user_id"));
                                modal.setUser_net_id(object.getString("user_login"));
                                modal.setusername(object.getString("firstname"));
                                if (!object.getString("lastname").equalsIgnoreCase(""))
                                    modal.setusername(object.getString("firstname") + " " + object.getString("lastname"));
                                modal.setUsercompany_id(object.getString("company_id"));
                                modal.setUsercompany(object.getString("company"));
                                modal.setIsFav(true);
                                modal.setuseraddress(object.getString("city"));
                                if (!object.getString("state").equalsIgnoreCase(""))
                                    modal.setuseraddress(object.getString("city") + " " + object.getString("state"));
                                if (object.has("main_pair")) {
                                    if (object.get("main_pair") instanceof JSONObject) {
                                        JSONObject obj = object.getJSONObject("main_pair").getJSONObject("icon");

                                        modal.setUser_imgUrl(obj.getString("image_path"));
                                        Log.i("USER IMAGE", obj.getString("image_path"));
                                    }

                                }

                                list.add(modal);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (json.has("error")) {

                        flag = 1;
                    }

                } else {

                    flag = 1;

                }
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
//            progress.dismiss();
            if (getActivity() != null) {
                if (page_no == 0) {
                    if (list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        order_status.setVisibility(View.VISIBLE);
                    }
                    listView.removeFooterView(loadingFooter);
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                    return;
                }
                if (flag == 1) {
                    listView.removeFooterView(loadingFooter);
                    if (list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        order_status.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
                if (flag == 2) {
                    listView.removeFooterView(loadingFooter);
                    if (list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        order_status.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    alertDialog.setTitle(getResources().getString(R.string.error));
                    try {
                        alertDialog.setMessage(json.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
                if (flag == 0) {
                    if (list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        order_status.setVisibility(View.VISIBLE);
                    }
                    Log.e("ouput--", json.toString());
                    adapter.notifyDataSetChanged();


                }
                isLoading = false;
            }
            //   Log.i("ACT at FAV Seller" , getActivity().toString());
        }
    }

    public class FavSellerAdapter extends BaseAdapter implements Filterable {

        public ListLoader imageLoader;
        public String company_id, username, userid, sellerid, backFlag;
        ConnectionDetector cd2;
        JSONParser parser2;
        SessionManager session;
        android.app.AlertDialog.Builder alertDialog2;
        DrawableBackgroundDrawable drawableDownloader = new DrawableBackgroundDrawable();
        private Activity activity;
        private ArrayList<UserProfile> data, memberData;
        private LayoutInflater inflater = null;
        private ProgressDialog progress2;

        public FavSellerAdapter(Activity a, ArrayList<UserProfile> d) {
            activity = getActivity();
//            Log.e("act", a.getClass().getName());
            data = d;
            Log.i("data.size()", data.size() + "");
            this.memberData = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ListLoader(activity);
            //progressStuff();
        }

        public int getCount() {
            if (data != null)
                return data.size();
            else
                return 0;
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.fav_sellers_list, null);

            TextView title = (TextView) vi.findViewById(R.id.txtTitleAttach); // title
            // artist name
            TextView mrp = (TextView) vi.findViewById(R.id.txtCodeAttech); // duration

            ImageView thumb_image = (ImageView) vi.findViewById(R.id.orderThumb); // thumb image
            final ImageView edit_image = (ImageView) vi.findViewById(R.id.edititem);
            final UserProfile song = data.get(position);
            edit_image.setTag(song);
            edit_image.setImageResource(R.drawable.popup);

            edit_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final UserProfile song2 = (UserProfile) edit_image.getTag();
                    final PopupWindow popupWindow = new PopupWindow(activity);
                    View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                    view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_selected);
                    ((TextView) view.findViewById(R.id.action_like)).setText("Unlike");
                    view.findViewById(R.id.like_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!getActivity().isFinishing() && isAdded()) {
                                if (cd.isConnectingToInternet()) {
                                    new LikeQuery(position, song2).execute();
                                } else {
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.oops), Toast.LENGTH_LONG);
                                }
                                popupWindow.dismiss();
                            }

                        }
                    });
                    view.findViewById(R.id.chat_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cd.isConnectingToInternet()) {
                                getPrimary(data.get(position));
                                //    new PrimaryUser(data.get(position)).execute();

                            } else {
                                Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_LONG).show();
                            }
                            popupWindow.dismiss();


                        }
                    });
                    popupWindow.setContentView(view);
                    popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int xOffset = -(view.getMeasuredWidth() - (edit_image.getWidth() / 2) + 10);
                    int yOffset = -(edit_image.getHeight());
                    popupWindow.showAsDropDown(edit_image, xOffset, yOffset);
                }
            });


            title.setText(MyTextUtils.toTitleCase(song.getusername()));

            mrp.setText(song.getUsercompany().toUpperCase());
//            if (song.getUser_imgUrl() != null) {
//                //   Log.e("iamge", song.getImgUrl());
//                imageLoader.DisplayImage(song.getUser_imgUrl(), thumb_image, R.drawable.avtar_bck1);
//
//            }
            drawableDownloader.loadDrawable(song.getUser_imgUrl(), thumb_image, getResources().getDrawable(R.drawable.avtar));
            return vi;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults(); // Holds the
                    // values
                    ArrayList<UserProfile> filterlist = new ArrayList<UserProfile>();

                    if (memberData == null) {
                        memberData = new ArrayList<UserProfile>();

                    }
                    if (constraint != null && memberData != null && memberData.size() > 0) {

                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < memberData.size(); i++) {
                            String data = memberData.get(i).getusername();
                            Log.d("data", data);
                            if (data.toLowerCase().contains(constraint.toString())) {
                                UserProfile modal = new UserProfile();

                                modal.setId(memberData.get(i).getId());
                                modal.setUserid(memberData.get(i).getUserid());
                                modal.setUser_net_id(memberData.get(i).getUser_net_id());
                                modal.setusername(memberData.get(i).getusername());
                                modal.setUsercompany_id(memberData.get(i).getUsercompany_id());
                                modal.setUsercompany(memberData.get(i).getUsercompany());
                                modal.setIsFav(true);
                                modal.setuseraddress(memberData.get(i).getuseraddress());
                                modal.setUser_imgUrl(memberData.get(i).getUser_imgUrl());

                                filterlist.add(modal);
                            }
                        }
                        results.values = filterlist;

                    }

                    // }

                    return results;

                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    data = (ArrayList<UserProfile>) results.values;
                    if (memberData.size() != 0)
                        notifyDataSetChanged();
                }
            }

                    ;

        }

        private class LikeQuery extends AsyncTask<String, String, JSONObject> {

            public String error = "";
            public int flag = 0, pos;
            public UserProfile productModal;
            JSONObject table = new JSONObject();
            Boolean success = false;

            public LikeQuery(int pos, UserProfile tag) {
                this.productModal = tag;
                this.pos = pos;
                Log.e("productModal", productModal.getusername() + "-" + pos);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (getActivity() != null && !getActivity().isFinishing() && isAdded())
                    progress.show();
            }

            @Override
            protected JSONObject doInBackground(String... args) {

                UserFunctions userFunction = new UserFunctions();
                JSONObject json = null;
                try {
                    table.put("user_id", user_id);
                    table.put("object_id", productModal.getUserid());
                    table.put("object_type", "vendor");

                    table.put("unlike", "1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = userFunction.LikeRequest("POST", table, "vendor", getActivity());
                if (json != null) {
                    Log.e("JSON--", json.toString());
                    try {
                        if (json.has("message")) {
                            if (json.get("message").toString().equalsIgnoreCase("_your favourite item added successfully"))
                                success = true;
                        } else if (json.has("error")) {
                            if (json.get("error").toString().equalsIgnoreCase("NO CONTENT"))
                                success = true;
                            else
                                success = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    flag = 1;

                }
                return json;
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                    progress.dismiss();

                    if (success) {
                        data.remove(pos);
                        memberData = data;
                        notifyDataSetChanged();
                        Toast.makeText(getActivity(), getActivity().getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            Toast.makeText(getActivity(), json.getString("error"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }


    }


    private void getPrimary(final UserProfile modal) {
        if (!getActivity().isFinishing()) {


            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getPrimary(modal.getUsercompany_id(), "1", helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();

                    if (response.isSuccess()) {
                        JsonElement element = (JsonElement) response.body();
                        JSONObject json = null;
                        if (element != null && element.isJsonObject()) {
                            try {
                                json = new JSONObject(element.getAsJsonObject().toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (json != null && json.has("users")) {
                            try {
                                chat_user_id = json.getJSONObject("users").optString("user_id");
                                Log.i("chat_user_id", chat_user_id);
                                Cursor cursor = getActivity().getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);

                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("user", "" + modal.getUser_net_id() + "@" + AppUtil.SERVER_NAME));

                                } else {
                                    if (!getActivity().isFinishing() && isAdded()) {
                                        if (cd.isConnectingToInternet()) {
                                            getUserDetails(chat_user_id);
                                        } else {
                                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.oops), Toast.LENGTH_LONG);
                                        }

                                    }


                                }
                                cursor.close();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.sorry), getActivity().getString(R.string.user_not_found));
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
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.error), getActivity().getString(R.string.server_error));
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();

                    if (getActivity() != null && !getActivity().isFinishing())
                        new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.error), getActivity().getString(R.string.server_error));

                }
            });
        }


    }

    private void getUserDetails(String userId) {
        if (!getActivity().isFinishing()) {
            final Calendar c = Calendar.getInstance();
            flag = 0;


            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getUserDetails(userId, helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();
                    if (response.isSuccess()) {
                        String userid = "";
                        JsonElement element = (JsonElement) response.body();
                        JSONObject json = null;
                        if (element != null && element.isJsonObject()) {
                            try {
                                json = new JSONObject(element.getAsJsonObject().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if (json != null) {

                            if (json.has("user_id") && json.optString("is_openfire").equalsIgnoreCase("1")) {
                                try {

                                    ContentValues cv = new ContentValues();
                                    if (json.has("phone"))
                                        cv.put(NetSchema.USER_PHONE, json.getString("phone"));
                                    if (json.has("company_name"))
                                        cv.put(NetSchema.USER_COMPANY, json.getString("company_name"));
                                    cv.put(NetSchema.USER_DISPLAY, " ");
                                    if (json.has("company_id"))
                                        cv.put(NetSchema.USER_COMPANY_ID, json.getString("company_id"));
                                    if (json.has("user_id"))
                                        cv.put(NetSchema.USER_ID, json.getString("user_id"));
                                    if (json.has("user_login")) {
                                        userid = json.getString("user_login") + "@" + AppUtil.SERVER_NAME;
                                        cv.put(NetSchema.USER_NET_ID, userid);
                                    }
                                    cv.put(NetSchema.USER_STATUS, "0");
                                    cv.put(NetSchema.USER_NAME, helper.ConvertCamel(json.getString("firstname")) + " " + helper.ConvertCamel(json.getString("lastname")));
                                    cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));
                                    getActivity().getContentResolver().insert(ChatProvider.NET_URI, cv);
                                    Cursor cursor = getActivity().getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
                                    if (cursor.getCount() > 0)
                                        flag = 3;
                                    Log.e("count", cursor.getCount() + "--****");
                                    cursor.close();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {

                                flag = 2;
                            }
                        } else {
                            flag = 1;
                        }
                        if (flag == 1) {
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.sorry), getActivity().getString(R.string.server_error));

                        } else if (flag == 2) {
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.sorry), getActivity().getString(R.string.user_not_exist));

                        } else {
                            getActivity().startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("user", userid));

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
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.error), getActivity().getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();
                    if (getActivity() != null && !getActivity().isFinishing())
                        new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getString(R.string.error), getActivity().getString(R.string.server_error));

                }
            });
        }


    }

}

