package wydr.sellers.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.gson.QueryModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.UserFunctions;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by surya on 7/8/15.
 */
public class QueryAdapter extends BaseAdapter implements Filterable
{
    private static LayoutInflater inflater = null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
    String str;
    AlertDialog.Builder alertDialog;
    JSONParser parser;
    ListLoader imageLoader;
    String imgUrl;
    Context context;
    String queryText;
    Helper helper = new Helper();
    private Activity activity;
    private ArrayList<QueryModal> data, memberData;
    private ProgressDialog progress;
    private ConnectionDetector cd;
    private String current_user_id, chat_user_id;
    int flag=0;

    public QueryAdapter(Activity a, ArrayList<QueryModal> d)
    {
        activity = a;
        data = d;
        this.memberData = d;
        cd = new ConnectionDetector(activity.getApplicationContext());
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressStuff();
        imageLoader = new ListLoader(activity.getApplicationContext());
        current_user_id = helper.getDefaults("user_id", a);
    }



    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.quiry_layout, null);
        TextView text = (TextView) vi.findViewById(R.id.txtQueryText);
        TextView post = (TextView) vi.findViewById(R.id.txtPostedBy);// title
        ImageView query_img = (ImageView) vi.findViewById(R.id.imageView2);
        final ImageView menu_btn = (ImageView) vi.findViewById(R.id.menu_btn);
        final QueryModal modal = data.get(position);

        try
        {
            text.setText(Html.fromHtml("<b>" + modal.getTitle() + "</b>" + "<br>" + "Product Code : <b>" + modal.getCode() + "</b> Quantity : <b>" + modal.getQuantity() + "</b> Order Type : <b>" + modal.getType() + " </b> Location : <b>" + modal.getLocation() + "</b>" + " Needed By : <b>" + format2.format(format.parse(modal.getNeeded())) + "</b>"));
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }
        //text.setText(Html.fromHtml(modal.getTitle() +"<br>"+ "<b> Product Code : </b>" + modal.getCode() + "<b> Quantity : </b>" + modal.getQuantity() + "<b> Order Type : </b>" + modal.getType() + "<b> Location : </b>" + modal.getLocation() + "<b> Needed By : </b>" + modal.getNeeded().substring(0, 11)));
        post.setText("POSTED BY: " + modal.getPostedBy());
        //if(modal.getImgUrl()!=null && !modal.getImgUrl().equalsIgnoreCase(""))

        Picasso.with(activity.getApplicationContext())
                .load(modal.getThumbnails().getUrl())
                .placeholder(R.drawable.default_product)
                .into(query_img);
       // imageLoader.DisplayImage2(modal.getThumbnails().getUrl(), query_img, R.drawable.default_product);
        if (modal.getVendorId().equalsIgnoreCase(helper.getDefaults("user_id", activity).toString()) &&
                modal.getCompanyId().equalsIgnoreCase(helper.getDefaults("company_id", activity).toString())
                ) {
            menu_btn.setVisibility(View.GONE);
            menu_btn.setTag(View.GONE);
        } else {
            menu_btn.setVisibility(View.VISIBLE);
            menu_btn.setTag(View.VISIBLE);
        }

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Dialog dialog = new Dialog(activity);
                final PopupWindow popupWindow = new PopupWindow(activity);
                View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                Log.i("QUERY-", modal.isFav() + "/" + modal.getTitle() + "/" + modal.getId());
                if (modal.isFav() != null && modal.isFav().equalsIgnoreCase("query")) {
                    ((TextView) view.findViewById(R.id.action_like)).setText("Unlike");
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_selected);
                } else {
                    ((TextView) view.findViewById(R.id.action_like)).setText("Like");
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_unselected);
                }

                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.like_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cd.isConnectingToInternet()) {
                            Log.e("---", modal.isFav() + "");
                            if (modal.isFav() != null && modal.isFav().equalsIgnoreCase("query")) {
                                new LikeQuery(position, modal).execute("1");
                            } else {
                                new LikeQuery(position, modal).execute("0");
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                        }
                        popupWindow.dismiss();
                    }
                });
                view.findViewById(R.id.chat_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cd.isConnectingToInternet()) {
                            if (modal.getThumbnails().getUrl() != null) {
                                imgUrl = modal.getThumbnails().getUrl();
                            } else {
                                imgUrl = "";
                            }

                            try {
                                queryText = "<b>" + modal.getTitle() + "</b>" + "<br>" + "Product Code : <b>" + modal.getCode() + "</b> Quantity : <b>" + modal.getQuantity() + "</b> Order Type : <b>" + modal.getType() + " </b> Location : <b>" + modal.getLocation() + "</b>" + " Needed By : <b>" + format2.format(format.parse(modal.getNeeded())) + "</b>";
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            getPrimary(modal, queryText, imgUrl);
                            // new PrimaryUser(queryText, imgUrl, modal).execute();

                        } else {
                            Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                        }
                        popupWindow.dismiss();
                    }
                });
                if (modal.getCompanyId().equalsIgnoreCase(helper.getDefaults("company_id", activity).toString())) {
                    view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                }
                if (modal.getId().equalsIgnoreCase(helper.getDefaults("user_id", activity).toString())) {
                    view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                }

                popupWindow.setContentView(view);
                popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int xOffset = -(view.getMeasuredWidth() - (menu_btn.getWidth() / 2) + 10);
                int yOffset = -(view.getMeasuredHeight() + menu_btn.getHeight());
                popupWindow.showAsDropDown(menu_btn, xOffset, yOffset);
            }
        });
//        cursor.close();
        return vi;
    }

    private void progressStuff() {
        // TODO Auto-generated method stub

        cd = new ConnectionDetector(activity);
        parser = new JSONParser();
        progress = new ProgressDialog(activity);
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(activity);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<QueryModal> filterlist = new ArrayList<QueryModal
                        >();

                if (memberData == null) {
                    memberData = new ArrayList<QueryModal>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getTitle();
                        Log.d("data", data);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            QueryModal modal = new QueryModal();

                            modal.setId(memberData.get(i).getId());
                            modal.setCode(memberData.get(i).getCode());
                            modal.setLocation(memberData.get(i).getLocation());
                            modal.setNeeded(memberData.get(i).getNeeded());
                            modal.setTitle(memberData.get(i).getTitle());
                            modal.setQuantity(memberData.get(i).getQuantity());
                            modal.setId(memberData.get(i).getId());
                            modal.setType(memberData.get(i).getType());
                            modal.setUserName(memberData.get(i).getUserName());
                            modal.setCompany(memberData.get(i).getCompany());
                            modal.setCompanyId(memberData.get(i).getCompanyId());
                            modal.setThumbnails(memberData.get(i).getThumbnails());
                            modal.setFav(memberData.get(i).isFav());
                            modal.setPostedBy(memberData.get(i).getPostedBy());
                            modal.setVendorId(memberData.get(i).getVendorId());

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

                data = (ArrayList<QueryModal>) results.values;
                if (memberData.size() != 0)
                    notifyDataSetChanged();
            }
        }

                ;

    }



    private class LikeQuery extends AsyncTask<String, String, JSONObject> {
        public String error = "", is_liked;
        public int flag = 0, pos;
        public QueryModal productModal;
        Boolean success = false;
        JSONObject table = new JSONObject();

        public LikeQuery(int pos, QueryModal tag) {
            this.productModal = tag;
            this.pos = pos;
            Log.e("productModal", productModal.getTitle() + "-" + pos + "-" + productModal.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!activity.isFinishing())
                progress.show();


        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            try {

                is_liked = args[0];

                table.put("user_id", current_user_id);
                table.put("object_id", productModal.getId());
                table.put("object_type", "query");
                if (args[0].equalsIgnoreCase("1"))
                    table.put("unlike", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = userFunction.LikeRequest("POST", table, "Query", activity);
            if (json != null) {

                try {
                    if (json.has("message")) {
                        if (json.get("message").toString().equalsIgnoreCase("_your favourite item added successfully"))
                            success = true;
                    } else if (json.has("error")) {
                        if (json.get("error").toString().equalsIgnoreCase("NO CONTENT"))
                            success = true;
                        else
                            flag = 2;
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
            if (!activity.isFinishing()) {
                progress.dismiss();
                if (flag == 1) {

                    alertDialog.setTitle(activity.getResources().getString(R.string.sorry));
                    alertDialog.setMessage(activity.getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
                if (flag == 2) {

                    alertDialog.setTitle(activity.getResources().getString(R.string.error));
                    alertDialog.setMessage(activity.getResources().getString(R.string.page_not_found));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
                if (flag == 0) {
                    Log.d("aaaaaaaa", activity.toString());
                    if (success) {
                        if (activity.getClass().toString().contains("MyFavourite")) {
                            data.remove(pos);
                            QueryAdapter.this.notifyDataSetChanged();
                        } else {
                            if (is_liked.equalsIgnoreCase("1")) {
                                data.get(pos).setFav("0");
                                Toast.makeText(activity, activity.getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                            } else {
                                data.get(pos).setFav("query");
                                Toast.makeText(activity, activity.getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                            }

                            QueryAdapter.this.notifyDataSetChanged();
                        }
                    }


                }
            }


        }
    }

    private void getPrimary(final QueryModal modal, final String queryText, final String imgUrl) {
        if (!activity.isFinishing()) {


            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getPrimary(modal.getCompanyId(), "1", helper.getB64Auth(activity), "application/json", "application/json");
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
                        if (json !=null && json.has("users")) {
                            try {
                                chat_user_id = json.getJSONObject("users").optString("user_id");
                                Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);
                                int iId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                                cursor.moveToFirst();
                                if (cursor.getCount() == 0) {
                                    getUserDetails(chat_user_id, queryText, imgUrl);
                                } else {
                                    str = cursor.getString(iId);
                                    activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + str).putExtra("from", 102).putExtra("url", imgUrl).putExtra("query", queryText));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if (activity != null && !activity.isFinishing())
                                new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.user_not_found));
                        }

                    } else {
                        int statusCode = response.code();

                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(activity);
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (activity != null && !activity.isFinishing())
                                new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();
                    if (activity != null && !activity.isFinishing())
                        new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));

                }
            });
        }


    }

    private void getUserDetails(String userId, final String queryText, final String imgUrl) {
        if (!activity.isFinishing()) {
            final Calendar c = Calendar.getInstance();
            flag = 0;


            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getUserDetails(userId, helper.getB64Auth(activity), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();

                    Log.d("re", "" + response.code());
                    // Log.d("JSON", " " + element.getAsJsonObject().toString());
                    if (response.isSuccess()) {
                        String userid = "";
                        JsonElement element = (JsonElement) response.body();


                        JSONObject json = null;
                        try {
                            json = new JSONObject(element.getAsJsonObject().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
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


                                    activity.getContentResolver().insert(ChatProvider.NET_URI, cv);

                                    Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
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
                            if (activity != null && !activity.isFinishing())
                                new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.server_error));

                        } else if (flag == 2) {
                            if (activity != null && !activity.isFinishing())
                                new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.user_not_exist));

                        } else {
                            activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + userid).putExtra("from", 102).putExtra("url", imgUrl).putExtra("query", queryText));
                        }

                    } else {
                        int statusCode = response.code();

                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(activity);
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (activity != null && !activity.isFinishing())
                                new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();
                    if (activity != null && !activity.isFinishing())
                        new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));

                }
            });
        }


    }

}
