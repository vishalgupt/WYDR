package wydr.sellers.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.activities.Controller;
import wydr.sellers.gson.SellerModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Deepesh_pc on 07-07-2015.
 */
public class AllSellersAdapter extends ArrayAdapter<SellerModal> {
    private final Context context;
    Helper helper = new Helper();

    android.app.AlertDialog.Builder alertDialog;
    Uri uri;
    ListLoader loader;
    JSONParser parser;
    Activity activity;
    LayoutInflater inflater;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    private ArrayList<SellerModal> values, memberData;
    private ProgressDialog progress;
    ConnectionDetector cd;
    int flag = 0;
    Controller application;
    Tracker mTracker;

    public AllSellersAdapter(Context context, ArrayList<SellerModal> FillingValues, Activity a) {
        super(context, R.layout.all_seller_item, FillingValues);
        this.context = context;
        this.values = FillingValues;
        this.memberData = FillingValues;
        this.activity = a;
        loader = new ListLoader(context.getApplicationContext());
        progressStuff();
        cd = new ConnectionDetector(context);

    }

    public void swapItems(ArrayList<SellerModal> items) {
        this.values = items;
        memberData = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    @Override
    public SellerModal getItem(int position) {
        return values.get(position);
    }

    @Override
    public int getPosition(SellerModal item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        View rowView = convertView;
        if (convertView == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.all_seller_item, parent, false);
        holder.productname = (TextView) rowView.findViewById(R.id.sellername);
        holder.productcode = (TextView) rowView.findViewById(R.id.sellernumber);
        holder.seller_location = (TextView) rowView.findViewById(R.id.seller_location);
        holder.pic = (ImageView) rowView.findViewById(R.id.sellericon);
        holder.caticon = (ImageView) rowView.findViewById(R.id.sellerCatIcon);
        rowView.setTag(holder);


        final SellerModal seller = values.get(position);

        if (seller.getLastName() != null && !seller.getLastName().equalsIgnoreCase(""))
            holder.productname.setText(MyTextUtils.toTitleCase(seller.getFirstName()) + "," + seller.getLastName());
        else
            holder.productname.setText(MyTextUtils.toTitleCase(seller.getFirstName()));

        holder.productcode.setText((seller.getCompany().toUpperCase()));
        if (seller.getState() != null && !seller.getState().equalsIgnoreCase(""))
            holder.seller_location.setText(seller.getCity() + "," + seller.getState());
        else
            holder.seller_location.setText(seller.getCity());

        if (seller.getIsOpenFire() != null && seller.getIsOpenFire().equalsIgnoreCase("1")) {
            holder.caticon.setVisibility(View.VISIBLE);
        } else {
            holder.caticon.setVisibility(View.GONE);
        }
        holder.caticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                application = (Controller) activity.getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("All Sellers", "Move", "ChatActivity");
                /*******************************ISTIAQUE***************************************/

                if (cd.isConnectingToInternet()) {

                    Cursor cursor = context.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{values.get(position).getNetworkId()}, null);

                    if (cursor.getCount() > 0) {
                        activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + values.get(position).getNetworkId()));
                    } else {
                        getUserDetails(values.get(position).getUserId());
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                }


            }
        });

        if (values.get(position).getUserId().equalsIgnoreCase(helper.getDefaults("user_id", activity))) {
            holder.caticon.setVisibility(View.GONE);
        }
        if (seller.getImageUrl() != null) {
            Picasso.with(context)
                    .load(seller.getImageUrl().getIcon().getImageUrl())
                    .placeholder(R.drawable.avtar_bck1)
                    .into(holder.pic);
            //loader.DisplayImage2(seller.getImageUrl().getIcon().getImageUrl(), holder.pic, R.drawable.avtar_bck1);
        }
            //loader.DisplayImage2("", holder.pic, R.drawable.avtar_bck1);

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<SellerModal> filterlist = new ArrayList<>();

                if (memberData == null)
                {
                    memberData = new ArrayList<SellerModal>();
                }
                if (constraint != null && memberData != null && memberData.size() > 0)
                {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {

                        if ((memberData.get(i).getLastName() != null && memberData.get(i).getLastName().toLowerCase().contains(constraint.toString())) || memberData.get(i).getFirstName().toLowerCase().contains(constraint.toString()) || memberData.get(i).getCity().toLowerCase().contains(constraint.toString()) || memberData.get(i).getCompany().toLowerCase().contains(constraint.toString())) {
                            SellerModal modal = new SellerModal();
                            modal.setCompanyId(memberData.get(i).getCompanyId());
                            modal.setCompany(memberData.get(i).getCompany());
                            modal.setCity(memberData.get(i).getCity());
                            modal.setImageUrl(memberData.get(i).getImageUrl());
                            modal.setFirstName(memberData.get(i).getFirstName());
                            modal.setIsOpenFire(memberData.get(i).getIsOpenFire());
                            modal.setUserId(memberData.get(i).getUserId());
                            modal.setNetworkId(memberData.get(i).getNetworkId());
                            modal.setLastName(memberData.get(i).getLastName());
                            modal.setState(memberData.get(i).getState());
                            modal.setLastName(memberData.get(i).getLastName());
                            filterlist.add(modal);
                        }
                    }
                    results.values = filterlist;
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                values = (ArrayList<SellerModal>) results.values;
                notifyDataSetChanged();
            }
        }

                ;

    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());

        parser = new JSONParser();
        progress = new ProgressDialog(activity);
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(activity)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

    }

    public class ViewHolder {
        TextView productname, productcode, seller_location;
        ImageView pic, caticon;
    }

    private void getUserDetails(String userId) {
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
                                    activity.getContentResolver().insert(ChatProvider.NET_URI, cv);
                                    Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
                                    if (cursor.getCount() > 0)
                                        flag = 3;
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
                            activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", userid));

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