package wydr.sellers.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
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
import wydr.sellers.activities.ProductDetailsActivity;
import wydr.sellers.activities.ShareCatalogWith;
import wydr.sellers.gson.ProductModal;
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
 * Created by surya on 5/8/15.
 */
public class SellerCatalogAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    public Uri uri = null;
    public ListLoader imageLoader;
    public String company_id, username,backFlag, user_id;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    ConnectionDetector cd;
    JSONParser parser;
    Context context;
    SessionManager session;
    android.app.AlertDialog.Builder alertDialog;
    Helper helper = new Helper();
    private Activity activity;
    private ArrayList<wydr.sellers.gson.ProductModal> data, memberData;
    private ProgressDialog progress;
    String chat_user_id;
    int flag = 0;

    public SellerCatalogAdapter(Activity a, ArrayList<wydr.sellers.gson.ProductModal> d, String company_id, String username,
                                String backFlag, String user_id) {
        activity = a;
        Log.d("act", a.getClass().getName());
        data = d;
        this.memberData = d;
        this.company_id = company_id;
        this.username = username;
        this.backFlag = backFlag;
        this.user_id = user_id;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ListLoader(activity);
        alertDialog = new AlertDialog.Builder(activity);
        progressStuff();
//      Log.e("ssssssss", userid);
    }

    public void swapItems(ArrayList<wydr.sellers.gson.ProductModal> items)
    {
        this.data = items;
        // memberData = items;
        notifyDataSetChanged();
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

        if (convertView == null) {
            vi = inflater.inflate(R.layout.edi_catalog_listlayout, null);
        }
        final ViewHolder holder = new ViewHolder(vi);

        String actname = activity.getClass().getName().replace("auriga.sellers.activities.", "");
        final wydr.sellers.gson.ProductModal song = data.get(position);
        holder.edit_image.setTag(song);
        Log.d("actname", actname);


        holder.edit_image.setImageResource(R.drawable.popup);
        if (user_id.equalsIgnoreCase(helper.getDefaults("user_id", activity))) {
            holder.edit_image.setVisibility(View.GONE);
        }

        holder.edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupWindow popupWindow = new PopupWindow(activity);
                View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                final ProductModal song2 = (ProductModal) holder.edit_image.getTag();
                if (song2.isFav() != null && song2.isFav().equalsIgnoreCase("1")) {
                    ((TextView) view.findViewById(R.id.action_like)).setText("Unlike");
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_selected);
                } else {
                    ((TextView) view.findViewById(R.id.action_like)).setText("Like");
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_unselected);
                }
                Log.i("userid------", user_id + "/" + helper.getDefaults("user_id", activity));
                if (user_id.equalsIgnoreCase(helper.getDefaults("user_id", activity))) {

                    view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                }
                if (company_id.equalsIgnoreCase(helper.getDefaults("company_id", activity))) {
                    view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                }
                view.findViewById(R.id.like_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductModal song = (ProductModal) holder.edit_image.getTag();

                        if (cd.isConnectingToInternet()) {
                            if (song.isFav() != null && song2.isFav().equalsIgnoreCase("1"))
                                new LikeQuery(position).execute(song.getId(), "1");
                            else
                                new LikeQuery(position).execute(song.getId(), "0");
                        }
                        popupWindow.dismiss();
                    }
                });

                view.findViewById(R.id.chat_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cd.isConnectingToInternet()) {
                            getPrimary(data.get(position));


                        } else {
                            Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                        }
                        popupWindow.dismiss();


                    }
                });
                view.findViewById(R.id.share_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent shareIntent = new Intent(activity, ShareCatalogWith.class);
                        shareIntent.putExtra("product_id", data.get(position).getId());
                        activity.startActivity(shareIntent);
                        popupWindow.dismiss();
                    }
                });
                popupWindow.setContentView(view);
                popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                int xOffset = -(view.getMeasuredWidth() - holder.edit_image.getWidth());
                int yOffset = -(holder.edit_image.getHeight() + (holder.edit_image.getHeight() / 2));
                popupWindow.showAsDropDown(holder.edit_image, xOffset, yOffset);


            }
        });


        holder.moq.setText("MOQ : " + BigDecimal.valueOf(Integer.parseInt(song.getMinQty())).toPlainString());
        holder.title.setText(MyTextUtils.toTitleCase(song.getName()));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.code.setText("Product Code : " + song.getCode());
        String marketPrice = String.format("%.0f", Double.parseDouble(song.getMrp()));
        String sellingPrice = String.format("%.0f", Double.parseDouble(song.getSp()));
        if (marketPrice.length() > 10) {
            marketPrice = marketPrice.substring(0, 7);
            if (marketPrice.contains(".00") || marketPrice.contains(".0")) {
                holder.mrp.setText("\u20B9" + marketPrice + "/pc");
            } else {
                holder.mrp.setText("\u20B9" + marketPrice + "../pc");
            }

        } else {
            holder.mrp.setText("\u20B9" + marketPrice + "/pc");
        }

        if (sellingPrice.length() > 10) {
            sellingPrice = sellingPrice.substring(0, 7);
            if (sellingPrice.contains(".00") || sellingPrice.contains(".0")) {
                holder.sell.setText("\u20B9" + sellingPrice + "/pc");
            } else {
                holder.sell.setText("\u20B9" + sellingPrice + "../pc");
            }
        } else {
            holder.sell.setText("\u20B9" + sellingPrice + "/pc");
        }

        if (Long.parseLong(song.getQty()) < Long.parseLong(song.getMinQty())) {
            holder.out_of_Stock.setVisibility(View.VISIBLE);
        } else {
            holder.out_of_Stock.setVisibility(View.GONE);
        }

        if (song.getChatAboutProduct() != null && song.getChatAboutProduct().equalsIgnoreCase("1")) {
            holder.mrp.setVisibility(View.INVISIBLE);
            holder.sell.setVisibility(View.INVISIBLE);
        }

        else
        {
            holder.mrp.setVisibility(View.VISIBLE);
            holder.sell.setVisibility(View.VISIBLE);
        }

        holder.thumb_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent1 = new Intent(activity,ProductDetailsActivity.class);
                intent1.putExtra("product_id",song.getId());
                intent1.putExtra("name",song.getName());
                intent1.putExtra("screenVisited","Seller catalog");
                activity.startActivity(intent1);
            }
        });

        holder.rl_plp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(activity,ProductDetailsActivity.class);
                intent1.putExtra("product_id",song.getId());
                intent1.putExtra("name",song.getName());
                intent1.putExtra("screenVisited","Seller catalog");
                activity.startActivity(intent1);
            }
        });
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (song.getThumbnails() != null)
            imageLoader.DisplayImage2(song.getThumbnails().getUrl(), holder.thumb_image, R.drawable.default_product);
        else
            imageLoader.DisplayImage2("", holder.thumb_image, R.drawable.default_product);
        return vi;
    }

    class ViewHolder {

        ImageView thumb_image, edit_image;
        TextView title, code, mrp, moq, sell, out_of_Stock;
        RelativeLayout rl_plp;

        public ViewHolder(View view)
        {
            title = (TextView) view.findViewById(R.id.txtTitleAttach);
            rl_plp = (RelativeLayout)view.findViewById(R.id.rl_plp);
            code = (TextView) view.findViewById(R.id.txtCodeAttech);
            mrp = (TextView) view.findViewById(R.id.txtMRPAttech);
            moq = (TextView) view.findViewById(R.id.txtmoq);
            sell = (TextView) view.findViewById(R.id.txtsp);
            out_of_Stock = (TextView) view.findViewById(R.id.mycat_out_of_stock);
            thumb_image = (ImageView) view.findViewById(R.id.my_product_thumb); // thumb image
            edit_image = (ImageView) view.findViewById(R.id.edititem);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<wydr.sellers.gson.ProductModal> filterlist = new ArrayList<>();

                if (memberData == null) {
                    memberData = new ArrayList<wydr.sellers.gson.ProductModal>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getName();
                        Log.d("data", data);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            wydr.sellers.gson.ProductModal modal = new wydr.sellers.gson.ProductModal();

                            modal.setId(memberData.get(i).getId());
                            modal.setName(memberData.get(i).getName());
                            modal.setCode(memberData.get(i).getCode());
                            modal.setFav(memberData.get(i).isFav());
                            modal.setThumbnails(memberData.get(i).getThumbnails());
                            modal.setCategory(memberData.get(i).getCategory());
                            modal.setQty(memberData.get(i).getQty());
                            modal.setChatAboutProduct(memberData.get(i).getChatAboutProduct());
                            modal.setCompanyId(memberData.get(i).getCompanyId());
                            modal.setMaxQty(memberData.get(i).getMaxQty());
                            modal.setMinQty(memberData.get(i).getMinQty());
                            modal.setMrp(memberData.get(i).getMrp());
                            modal.setSp(memberData.get(i).getSp());
                            modal.setUser(memberData.get(i).getUser());
                            modal.setVisibility(memberData.get(i).getVisibility());
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

                data = (ArrayList<wydr.sellers.gson.ProductModal>) results.values;
                if (memberData.size() != 0)
                    notifyDataSetChanged();
            }
        }

                ;

    }

    private void getPrimary(final ProductModal modal) {
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
                                Log.i("chat_user_id", chat_user_id);
                                Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);

                                cursor.moveToNext();
                                if (cursor.getCount() > 0) {

                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", "" + modal.getName());
                                    bundle.putString("code", "" + modal.getCode());
                                    if (modal.getChatAboutProduct() != null && modal.getChatAboutProduct().equalsIgnoreCase("1")) {
                                        bundle.putString("mrp", "");
                                        bundle.putString("price", "");
                                    } else {
                                        String marketPrice = String.format("%.2f", Double.parseDouble(modal.getMrp()));
                                        String sellingPrice = String.format("%.2f", Double.parseDouble(modal.getSp()));
                                        if (marketPrice.length() > 10) {
                                            marketPrice = marketPrice.substring(0, 7);
                                            bundle.putString("mrp", "" + marketPrice);

                                        } else {
                                            bundle.putString("mrp", "" + marketPrice);
                                        }

                                        if (sellingPrice.length() > 10) {
                                            sellingPrice = sellingPrice.substring(0, 7);
                                            bundle.putString("price", "" + sellingPrice);
                                        } else {
                                            bundle.putString("price", "" + sellingPrice);
                                        }

                                    }
                                    if (modal.getThumbnails() != null && modal.getThumbnails().getUrl() != null)
                                        bundle.putString("url", "" + modal.getThumbnails().getUrl());
                                    else
                                        bundle.putString("url", "");
                                    bundle.putString("moq", "" + modal.getMinQty());
                                    Log.d("MOQ", modal.getMinQty());
                                    int iCompanyId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                                    activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + cursor.getString(iCompanyId)).putExtra("from", 101).putExtra("result", bundle));


                                } else {
                                    getUserDetails(chat_user_id, modal);

                                }
                                cursor.close();


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

    private void getUserDetails(String userId, final ProductModal product) {
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
                            Bundle bundle = new Bundle();
                            bundle.putString("name", "" + product.getName());
                            bundle.putString("code", "" + product.getCode());
                            if (product.getChatAboutProduct() != null && product.getChatAboutProduct().equalsIgnoreCase("1")) {
                                bundle.putString("mrp", "");
                                bundle.putString("price", "");
                            } else {
                                String marketPrice = String.format("%.2f", Double.parseDouble(product.getMrp()));
                                String sellingPrice = String.format("%.2f", Double.parseDouble(product.getSp()));
                                if (marketPrice.length() > 10) {
                                    marketPrice = marketPrice.substring(0, 7);
                                    bundle.putString("mrp", "" + marketPrice);

                                } else {
                                    bundle.putString("mrp", "" + marketPrice);
                                }

                                if (sellingPrice.length() > 10) {
                                    sellingPrice = sellingPrice.substring(0, 7);
                                    bundle.putString("price", "" + sellingPrice);
                                } else {
                                    bundle.putString("price", "" + sellingPrice);
                                }
                            }
                            if (product.getThumbnails() != null && product.getThumbnails().getUrl() != null)
                                bundle.putString("url", "" + product.getThumbnails().getUrl());
                            else
                                bundle.putString("url", "");
                            bundle.putString("moq", "" + product.getMinQty());

                            activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", userid).putExtra("from", 101).putExtra("result", bundle));


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



    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(activity);
        parser = new JSONParser();
        progress = new ProgressDialog(activity);
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    private class LikeQuery extends AsyncTask<String, String, JSONObject> {

        public String error = "", argument;
        public int flag = 0, pos;
        Boolean success = false;
        JSONObject table = new JSONObject();

        public LikeQuery(int pos) {
            this.pos = pos;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            try {
                argument = args[1];
                table.put("user_id", helper.getDefaults("user_id", activity));
                table.put("object_id", args[0]);
                table.put("object_type", "product");
                if (args[1] == "1")
                    table.put("unlike", "1");
                Log.d("IIIII", args[0] + "///////");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = userFunction.LikeRequest("POST", table, "Product", activity);
            if (json != null) {
                Log.d("JSON--", json.toString());

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
            } else if (success) {
                if (activity.getClass().getName().equalsIgnoreCase("auriga.sellers.activities.SellersCatalog")) {
                    if (argument.equalsIgnoreCase("1")) {
                        data.get(pos).setFav("0");
                        Toast.makeText(activity, activity.getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                    } else {
                        data.get(pos).setFav("1");
                        Toast.makeText(activity, activity.getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                    }


                }

                SellerCatalogAdapter.this.notifyDataSetChanged();
            }


        }
    }


}
