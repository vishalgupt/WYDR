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
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.activities.EditProduct;
import wydr.sellers.activities.ShareCatalogWith;
import wydr.sellers.modal.CatalogProductModal;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.UserFunctions;
import wydr.sellers.syncadap.AlteredCatalog;
import wydr.sellers.syncadap.FeedCatalog;

/**
 * Created by surya on 10/2/16.
 */
public class MyLiveAdapter extends CursorAdapter implements Filterable {


    // Context contxt;
    //Cursor tempcursor;
    Helper helper;
    private ArrayList<CatalogProductModal> data, memberData;
    Activity activity;
    private LayoutInflater inflater = null;
    public ListLoader imageLoader;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    private ProgressDialog progress;
    public String company_id, username, TAG = "MYLIVEADAPTER";
    android.app.AlertDialog.Builder alertDialog;

    public MyLiveAdapter(Context context, Cursor cursor, Activity activity) {
        super(context, cursor, 0);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ListLoader(context);
        //   this.contxt = context;
        helper = new Helper();
        this.activity = activity;
        progressStuff();


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(activity).inflate(R.layout.edi_catalog_listlayout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Cursor mycursor = cursor;
        //  Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        // cursor.setNotificationUri(activity.getContentResolver(), MyContentProvider.CONTENT_URI_FEED);
        final ViewHolder holder = createViewHolder(view);
        final int title = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_TITLE);
        int code = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_CODE);
        int moq = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_MINQTY);
        int mrp = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_MRP);
        int qty = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_QTY);
        int sp = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_SP);
        int cat_id = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_CATEGORY_ID);
        int imagepath = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_THUMB_PATH);
        final int status = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_STATUS);
        final int prod_id = cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_PRODUCT_ID);

        String url = cursor.getString(imagepath);
        Log.d("TAG URL", "" + url);
        if (url == null || url.equalsIgnoreCase("")) {
            holder.thumb_image.setImageResource(R.drawable.default_product);
        } else {
            if (url.contains("http"))
            {
                Picasso.with(context)
                        .load(cursor.getString(imagepath))
                        .placeholder(R.drawable.default_product)
                        .into(holder.thumb_image);
                //imageLoader.DisplayImage2(cursor.getString(imagepath), holder.thumb_image, R.drawable.default_product);
            } else {
                holder.thumb_image.setImageBitmap(helper.decodeSampledBitmapFromResource(cursor.getString(imagepath), 100, 100));
            }

        }

        if (cursor.getInt(moq) > 0)
            holder.moq.setText("MOQ : " + BigDecimal.valueOf(cursor.getInt(moq)).toPlainString());
        else
            holder.moq.setText("MOQ : ");
        // holder.thumb_image.setImageResource(R.drawable.default_product);
        holder.title.setText(MyTextUtils.toTitleCase(cursor.getString(title)));
        holder.code.setText("Product Code : " + cursor.getString(code));
        String mrpVal = String.format("%.2f", cursor.getDouble(mrp));
        String spVal = String.format("%.2f", cursor.getDouble(sp));
        if (mrpVal.length() > 10) {
            mrpVal = mrpVal.substring(0, 7);
            if (mrpVal.contains(".00") || mrpVal.contains(".0")) {
                holder.mrp.setText("\u20B9" + mrpVal + "/pc");
            } else {
                holder.mrp.setText("\u20B9" + mrpVal + "../pc");
            }

        } else {
            holder.mrp.setText("\u20B9" + mrpVal + "/pc");
        }

        if (spVal.length() > 10) {
            spVal = spVal.substring(0, 7);
            if (spVal.contains(".00") || spVal.contains(".0")) {
                holder.sp.setText("\u20B9" + spVal + "/pc");
            } else {
                holder.sp.setText("\u20B9" + spVal + "../pc");
            }
        } else {
            holder.sp.setText("\u20B9" + spVal + "/pc");
        }
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (cursor.getLong(qty) < cursor.getLong(moq)) {
            holder.out_of_stock.setVisibility(View.VISIBLE);
            holder.qty.setVisibility(View.GONE);
        } else {
            holder.out_of_stock.setVisibility(View.GONE);
            holder.qty.setVisibility(View.VISIBLE);
            holder.qty.setText("Qty : " + cursor.getString(qty));
        }
        holder.status.setVisibility(View.VISIBLE);
        if (cursor.getString(status).equalsIgnoreCase("D"))
            holder.status.setText("Disabled");
        else
            holder.status.setText("Active");

        //Log.i("Values--DEEP", cursor.getString(imagepath) + "," + cursor.getString(cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_TITLE)));


        holder.edit_image.setImageResource(R.drawable.popup);
        holder.edit_image.setTag(cursor.getPosition());
        holder.edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupWindow popupWindow = new PopupWindow(activity);
                mycursor.moveToPosition((Integer) holder.edit_image.getTag());
                View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.edit_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.share_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.disable_wrap).setVisibility(View.VISIBLE);
                //Log.i("actname", view.toString());
                //Log.i("status", cursor.getString(status));
                if (mycursor.getString(status).equalsIgnoreCase("D")) {
                    ((TextView) view.findViewById(R.id.disble_text)).setText("Enable");
                    ((ImageView) view.findViewById(R.id.disble_image)).setImageResource(R.drawable.like_popup);
                } else {
                    ((TextView) view.findViewById(R.id.disble_text)).setText("Disable");
                    ((ImageView) view.findViewById(R.id.disble_image)).setImageResource(R.drawable.disable_popup);
                }
                view.findViewById(R.id.share_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent shareIntent = new Intent(activity, ShareCatalogWith.class);
                        shareIntent.putExtra("product_id", mycursor.getString(prod_id));
                        activity.startActivity(shareIntent);
                        popupWindow.dismiss();
                    }
                });
                view.findViewById(R.id.edit_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", mycursor.getString(prod_id));
                        bundle.putString("class_name", "Live");
                        activity.startActivity(new Intent(activity, EditProduct.class).putExtra("result", bundle));
                        activity.finish();
                        popupWindow.dismiss();
                    }
                });
                view.findViewById(R.id.disable_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (cd.isConnectingToInternet()) {
                            if (mycursor.getString(status).equalsIgnoreCase("A"))
                                new DisabelProduct(mycursor.getString(prod_id)).execute("1");
                            else
                                new DisabelProduct(mycursor.getString(prod_id)).execute("0");
                        } else {
                            //Log.i(TAG, "Disabling for " + cursor.getString(title) + "/" + cursor.getString(status));
                            if (mycursor.getString(status).equalsIgnoreCase("A"))
                                OfflineDisable(mycursor.getString(prod_id), "D");
                            else
                                OfflineDisable(mycursor.getString(prod_id), "A");
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
                int xOffset = -(view.getMeasuredWidth() - (holder.edit_image.getWidth() / 2) + 10);
                int yOffset = -(holder.edit_image.getHeight() + (holder.edit_image.getHeight() / 2));
                popupWindow.showAsDropDown(holder.edit_image, xOffset, yOffset);
            }
        });
        notifyDataSetChanged();

    }


    private void OfflineDisable(String pid, String status) {

        //Log.i(TAG, "OfflineDisable " + pid + ".//" + status);
        Cursor c = activity.getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, null, null, null);
        //Log.i(TAG, "COUNT--before---- " + c.getCount() + " -------------...");
        c.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, pid);
        contentValues.put(AlteredCatalog.COLUMN_STATUS, status);
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_FLAG, "5");
        contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "1");
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        contentValues.put(AlteredCatalog.COLUMN_UPDATED, time);

        int is_updated = activity.getContentResolver().update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog.COLUMN_PRODUCT_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=? and " + AlteredCatalog.COLUMN_REQUEST_STATUS + "=?", new String[]{pid, "5", "1"});
        //Log.i(TAG,"is_updated--" + is_updated);
        if (is_updated == 0) {
            Uri uri2 = activity.getContentResolver().insert(MyContentProvider.CONTENT_URI_ALTER, contentValues);
            //Log.i(TAG,"is_updated--intserting" + uri2.toString());
            if (uri2 != null) {
                ContentValues contentValues2 = new ContentValues();
                //Log.i(TAG,"is_updated--intserting" + uri2.toString());
                contentValues2.put(FeedCatalog.COLUMN_STATUS, status);
                int upd = activity.getContentResolver().update(MyContentProvider.CONTENT_URI_FEED, contentValues2, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{pid});
                //Log.i(TAG , "upd--for status change " + upd);
                activity.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_FEED, null, false);
                activity.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_ALTER, null, false);

            }
        } else if (is_updated == 1) {
            //Log.i(TAG, "is_updated--updating");
            int i = activity.getContentResolver().delete(MyContentProvider.CONTENT_URI_ALTER, AlteredCatalog.COLUMN_PRODUCT_ID + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=? and " + AlteredCatalog.COLUMN_REQUEST_STATUS + "=?", new String[]{pid, "5", "1"});
            //Log.i(TAG, "deleted --" + i);
            if (i == 1) {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(FeedCatalog.COLUMN_STATUS, status);
                int upd = activity.getContentResolver().update(MyContentProvider.CONTENT_URI_FEED, contentValues2, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{pid});
                //Log.i("MyliveAdpter", "upd--for status change " + upd);
                activity.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_FEED, null, false);
                activity.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_ALTER, null, false);
            } else {
                //Log.i("MyliveAdpter", "del failed " + i);
            }


        } else {

            alertDialog.setTitle("Status");
            alertDialog.setMessage("Product Update Failed");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Your positive action

                }
            });
            alertDialog.show();

        }


        Cursor c2 = activity.getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, null, null, null);
        //Log.i("Addproduct", "COUNT---after---- " + c2.getCount() + " -------------...");

        c2.close();


    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(activity);
        parser = new JSONParser();
        progress = new ProgressDialog(activity);
        progress.setMessage(activity.getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(activity);
    }

    private static class ViewHolder {
        TextView title, code, mrp, sp, moq, out_of_stock, status, qty;
        ImageView thumb_image, edit_image;

//        private ViewHolder() {
//        }
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.title = (TextView) view.findViewById(R.id.txtTitleAttach); // title
        viewholder.code = (TextView) view.findViewById(R.id.txtCodeAttech); // artist name
        viewholder.mrp = (TextView) view.findViewById(R.id.txtMRPAttech); // duration
        viewholder.moq = (TextView) view.findViewById(R.id.txtmoq);
        viewholder.status = (TextView) view.findViewById(R.id.mycat_status);
        viewholder.qty = (TextView) view.findViewById(R.id.mycat_qty);
        viewholder.sp = (TextView) view.findViewById(R.id.txtsp); // duration
        viewholder.thumb_image = (ImageView) view.findViewById(R.id.my_product_thumb); // thumb image
        viewholder.edit_image = (ImageView) view.findViewById(R.id.edititem);
        viewholder.out_of_stock = (TextView) view.findViewById(R.id.mycat_out_of_stock);

        return viewholder;
    }

    private class DisabelProduct extends AsyncTask<String, String, JSONObject> {

        public String error = "", argument, id;
        public int flag = 0;
        JSONObject table = new JSONObject();

        public DisabelProduct(String pos) {
            this.id = pos;
            //Log.d("product id --", pos);
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
                argument = args[0];

                if (args[0] == "1")
                    table.put("status", "D");
                else
                    table.put("status", "A");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = userFunction.DisableProduct(table, id, activity);
            if (json != null) {
                if (json.has("error"))
                    flag = 2;
            } else {
                flag = 1;
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!activity.isFinishing()) {

                progress.dismiss();
                if (flag == 0) {
                    ContentValues contentValues = new ContentValues();
                    if (argument.equalsIgnoreCase("1"))
                        contentValues.put(FeedCatalog.COLUMN_STATUS, "D");
                    else
                        contentValues.put(FeedCatalog.COLUMN_STATUS, "A");
                    int upd = activity.getContentResolver().update(MyContentProvider.CONTENT_URI_FEED, contentValues, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{id});
                    Log.i("MyliveAdpter", "upd--for status change " + upd);
                    // activity.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_FEED, null, false);
                    // adapter.swapCursor(data);
                    getFilter().filter("search");
                } else if (flag == 1) {
                    alertDialog.setTitle(activity.getResources().getString(R.string.sorry));
                    alertDialog.setMessage(activity.getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            activity.finish();
                        }
                    });
                    alertDialog.show();
                } else if (flag == 2) {
                    alertDialog.setTitle(activity.getResources().getString(R.string.error));
                    try {
                        alertDialog.setMessage(json.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            activity.finish();
                        }
                    });
                    alertDialog.show();
                }

            }
        }
    }


}

