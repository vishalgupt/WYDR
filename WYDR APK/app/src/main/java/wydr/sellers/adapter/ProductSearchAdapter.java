package wydr.sellers.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import wydr.sellers.activities.ShareCatalogWith;
import wydr.sellers.modal.CatalogProductModal;
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
 * Created by Deepesh_pc on 16-11-2015.
 */
public class ProductSearchAdapter extends BaseAdapter {

    public ListLoader imageLoader;
    public Uri uri = null;
    Activity activity;
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    ConnectionDetector cd;
    JSONParser parser;
    String chat_user_id;
    Helper helper = new Helper();
    private ArrayList<CatalogProductModal> data;
    AlertDialog.Builder alertDialog;
    private ProgressDialog progress;
    int flag=0;
    public ProductSearchAdapter(Activity context, ArrayList<CatalogProductModal> list) {
        this.activity = context;
        this.context = context;
        imageLoader = new ListLoader(context);
        cd = new ConnectionDetector(context);
        data = list;
        alertDialog = new AlertDialog.Builder(context);
        progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.loading));
        progress.setIndeterminate(false);

        parser = new JSONParser();
    }

    @Override
    public int getCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.edi_catalog_listlayout, null);
        }
        final ViewHolder holder = createViewHolder(view);
        final CatalogProductModal song = data.get(position);
        holder.moq.setText("MOQ : " + BigDecimal.valueOf(Long.parseLong(song.getMoq())).toPlainString());
        holder.title.setText(MyTextUtils.toTitleCase(song.getName()));
        holder.code.setText("Product Code : " + song.getCode());
        holder.mrp.setText("MRP :" + activity.getResources().getString(R.string.rs) + " " + (int)(Double.parseDouble(song.getPrice()))+ "/pc");
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.sp.setText("SP :" + activity.getResources().getString(R.string.rs) + " " + (int)(Double.parseDouble(song.getSellingPrice())) + "/pc");
        if (Long.parseLong(song.getMoq()) > Long.parseLong(song.getQty())) {
            holder.out_of_stock.setVisibility(View.VISIBLE);
        } else {
            holder.out_of_stock.setVisibility(View.GONE);
        }
        if (song.getChat_price().equalsIgnoreCase("Y")) {
            holder.mrp.setVisibility(View.INVISIBLE);
            holder.sp.setVisibility(View.INVISIBLE);
        } else {
            holder.mrp.setVisibility(View.VISIBLE);
            holder.sp.setVisibility(View.VISIBLE);
        }

        if (song.getImgUrl() != null) {
            if (song.getImgUrl().contains("http"))
                Picasso.with(context)
                .load(song.getImgUrl())
                .resize(85, 85)
                .onlyScaleDown()
                .placeholder(R.drawable.default_product)
                .into(holder.thumb_image);
                //imageLoader.DisplayImage2(song.getImgUrl(), holder.thumb_image, R.drawable.default_product);
            else {

                holder.thumb_image.setImageBitmap(decodeFile(new File(song.getImgUrl())));
            }
        }
        holder.edit_image.setImageResource(R.drawable.popup);

        holder.edit_image.setTag(song);
        holder.edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupWindow popupWindow = new PopupWindow(activity);

                View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.edit_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.disable_wrap).setVisibility(View.GONE);
                final CatalogProductModal song2 = (CatalogProductModal) holder.edit_image.getTag();

                if (song2.getUserid().equalsIgnoreCase(helper.getDefaults("user_id", activity))) {
                    view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                }
                if (song2.getCompany_id().equalsIgnoreCase(helper.getDefaults("company_id", activity))) {
                    view.findViewById(R.id.like_wrap).setVisibility(View.GONE);

                }
                if (song2.getIsfav()) {
                    ((TextView) view.findViewById(R.id.action_like)).setText("Unlike");
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_selected);
                } else {
                    ((TextView) view.findViewById(R.id.action_like)).setText("Like");
                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_unselected);
                }
                view.findViewById(R.id.like_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CatalogProductModal song = (CatalogProductModal) holder.edit_image.getTag();

                        if (cd.isConnectingToInternet()) {
                            if (song.getIsfav())
                                new LikeQuery(position).execute(song.getId(), "1");
                            else
                                new LikeQuery(position).execute(song.getId(), "0");
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
                            CatalogProductModal song = (CatalogProductModal) holder.edit_image.getTag();
                            getPrimary(song);
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                        }
                        popupWindow.dismiss();

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
                int xOffset = -(view.getMeasuredWidth() - (holder.edit_image.getWidth() / 2) + 10);
                int yOffset = -(holder.edit_image.getHeight() + (holder.edit_image.getHeight() / 2));
                popupWindow.showAsDropDown(holder.edit_image, xOffset, yOffset);
            }
        });


        return view;
    }







    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.title = (TextView) view.findViewById(R.id.txtTitleAttach); // title
        viewholder.code = (TextView) view.findViewById(R.id.txtCodeAttech); // artist name
        viewholder.mrp = (TextView) view.findViewById(R.id.txtMRPAttech); // duration
        viewholder.moq = (TextView) view.findViewById(R.id.txtmoq);
        viewholder.out_of_stock = (TextView) view.findViewById(R.id.mycat_out_of_stock);
        viewholder.sp = (TextView) view.findViewById(R.id.txtsp); // duration
        viewholder.thumb_image = (ImageView) view.findViewById(R.id.my_product_thumb); // thumb image
        viewholder.edit_image = (ImageView) view.findViewById(R.id.edititem);
        return viewholder;
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 200;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;
            //Log.i("fff" ,f.toString() +"//"+o.outHeight+"//"+o.outHeight);
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            } //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return b;
    }

    private class ViewHolder {
        TextView title, code, mrp, sp, moq, out_of_stock;
        ImageView thumb_image, edit_image;

        private ViewHolder() {
        }
    }

    private class LikeQuery extends AsyncTask<String, String, JSONObject> {

        public String error = "", argument;
        public int flag = 0, pos;
        Boolean success = false;
        JSONObject table = new JSONObject();
        private ProgressDialog progress;

        public LikeQuery(int pos) {
            this.pos = pos;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(activity);
            progress.setMessage("Loading...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);

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
            json = userFunction.LikeRequest("POST", table, "Query", activity);
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
            } else if (flag == 2) {
                alertDialog.setTitle(activity.getResources().getString(R.string.error));
                alertDialog.setMessage(activity.getResources().getString(R.string.page_not_found));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            } else {
                if (success) {
                    if (argument.equalsIgnoreCase("1")) {
                        Toast.makeText(activity, activity.getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                        data.get(pos).setIsfav(false);
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                        data.get(pos).setIsfav(true);
                    }


                    notifyDataSetChanged();
                }
            }
            //  Log.d("II", "----" + company_id);


        }
    }
    private void getPrimary(final CatalogProductModal modal) {
        if (!activity.isFinishing()) {

            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getPrimary(modal.getCompany_id(), "1", helper.getB64Auth(activity), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();

                    Log.d("re", "" + response.code());
                    // Log.d("JSON", " " + element.getAsJsonObject().toString());
                    if (response.isSuccess()) {
                        JsonElement element = (JsonElement) response.body();

//                        Log.i("JSON", " " + element.getAsJsonObject().toString());
                        JSONObject json = null;
                        if (element != null && element.isJsonObject()) {
                            try {
                                json = new JSONObject(element.getAsJsonObject().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if (json!=null && json.has("users")) {
                            try {
                                chat_user_id = json.getJSONObject("users").optString("user_id");
                                Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);

                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", "" + modal.getName());
                                    bundle.putString("code", "" + modal.getCode());
                                    if (modal.getChat_price().equalsIgnoreCase("Y")) {
                                        bundle.putString("mrp", "");
                                        bundle.putString("price", "");
                                    } else {
                                        String marketPrice = String.format("%.2f", Double.parseDouble(modal.getPrice()));
                                        String sellingPrice = String.format("%.2f", Double.parseDouble(modal.getSellingPrice()));
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

                                    bundle.putString("url", "" + modal.getImgUrl());
                                    bundle.putString("moq", "" + modal.getMoq());
                                    int iCompanyId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                                    activity.startActivity(new Intent(activity.getApplicationContext(), ChatActivity.class).putExtra("user", "" + cursor.getString(iCompanyId)).putExtra("from", 101).putExtra("result", bundle));

                                } else {
                                    getUserDetails(modal, chat_user_id);
                                    //  new getUserDetails(modal).execute(chat_user_id);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
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
    private void getUserDetails(final CatalogProductModal modal,String userId) {
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
                        String userid="";
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


                                    uri = activity.getContentResolver().insert(ChatProvider.NET_URI, cv);

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
                            if (uri != null && flag == 3) {
                                Bundle bundle = new Bundle();
                                bundle.putString("name", "" + modal.getName());
                                bundle.putString("code", "" + modal.getCode());
                                if (modal.getChat_price().equalsIgnoreCase("Y")) {
                                    bundle.putString("mrp", "");
                                    bundle.putString("price", "");
                                } else {
                                    String marketPrice = String.format("%.2f", Double.parseDouble(modal.getPrice()));
                                    String sellingPrice = String.format("%.2f", Double.parseDouble(modal.getSellingPrice()));
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
                                bundle.putString("url", "" + modal.getImgUrl());
                                bundle.putString("moq", "" + modal.getMoq());
                                activity.startActivity(new Intent(activity.getApplicationContext(), ChatActivity.class).putExtra("user", "" + userid).putExtra("from", 101).putExtra("result", bundle));
                            }

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
