package wydr.sellers.adapter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.http.HEAD;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.CartActivity;
import wydr.sellers.activities.Catalog;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.gson.Products;
import wydr.sellers.gson.coupon;
import wydr.sellers.modal.CouponModal;
import wydr.sellers.network.ProductLoader;
import wydr.sellers.network.RestClient;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.JSONParser;
import wydr.sellers.slider.MyDatabaseHelper;

/**
 * Created by surya on 2/12/15.
 */
public class CartAdapter extends CursorAdapter implements Serializable
{
    ProductLoader loader;
    JSONArray array = new JSONArray();
    public static String coupondel="";
    Boolean check = false;
    static int minqty = 0;
    Boolean akilogic = false;
    Cursor akicursor;
    HashMap<String, Products> map = new HashMap<>();
    String rowid;
    String qty;
    Helper helper = new Helper();
    MyDatabaseHelper db = new MyDatabaseHelper(mContext);
    JSONParser jsonParser = new JSONParser();
    static final ArrayList<String> couponmap = new ArrayList<String>();

    public CartAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
        this.akicursor = cursor;
        loader = new ProductLoader(context.getApplicationContext());
    }

    private ViewHolder createViewHolder(View view)
    {
        ViewHolder viewholder = new ViewHolder();
        viewholder.name = (TextView) view.findViewById(R.id.listCartTitle);
        viewholder.amount = (TextView) view.findViewById(R.id.txtCartAmount);
        viewholder.code = (TextView) view.findViewById(R.id.listCartCode);
        viewholder.listCartQty = (TextView) view.findViewById(R.id.listCartQty);
        viewholder.add = (Button) view.findViewById(R.id.btnPlus);
        viewholder.sub = (Button) view.findViewById(R.id.btnMinus);
        viewholder.picker = (TextView) view.findViewById(R.id.editNumber);
        viewholder.remove = (ImageView) view.findViewById(R.id.btnRemoveItems);
        viewholder.product = (ImageView) view.findViewById(R.id.listCardThumb);
        viewholder.cod_code = (TextView) view.findViewById(R.id.pay_mode);
        viewholder.couponapply = (RelativeLayout) view.findViewById(R.id.coupon_applied);
        viewholder.couponavailable = (RelativeLayout) view.findViewById(R.id.coupon_applied2);
        viewholder.radiolist = (LinearLayout) view.findViewById(R.id.ll_28);
        viewholder.DiscountPrice = (TextView) view.findViewById(R.id.txtCartAmount_new);
        viewholder.radiolist.setVisibility(View.GONE);
        viewholder.x = (TextView) view.findViewById(R.id.x);
        viewholder.recyclerView = (RecyclerView) view.findViewById(R.id.listview28);
        return viewholder;
    }

    public ArrayList<String> couponList()
    {
        Log.d("couponmapsize", couponmap.toString());
        return couponmap;
    }


    public void bindView(View view, final Context context, final Cursor cursor)
    {
        final ViewHolder holder = createViewHolder(view);
        final int iId = cursor.getColumnIndexOrThrow(CartSchema.KEY_ROWID);
        int iName = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_NAME);
        int iCode = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_CODE);
        int iPrice = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_PRICE);
        int iUrl = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_URL);
        final int iQty = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY);
        final int iMoq = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_MOQ);
        final int iInv = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_INVENTORY);
        final int icod = cursor.getColumnIndexOrThrow(CartSchema.PAY_METHOD);
        final int idiscount = cursor.getColumnIndexOrThrow(CartSchema.DISCOUNT);
        final int ipromocode = cursor.getColumnIndexOrThrow(CartSchema.CART_PROMO);
        final int ipromo_code = cursor.getColumnIndexOrThrow(CartSchema.PROMO_CODE);
        final int ipromoapplied = cursor.getColumnIndexOrThrow(CartSchema.CART_PROMO_APPLIED);
        final int idiscountprice = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_DISCOUNT_PRICE);
        final int iminqty = cursor.getColumnIndexOrThrow(CartSchema.PROMO_MIN_QTY);
        final int imaxamt = cursor.getColumnIndexOrThrow(CartSchema.PROMO_MAX_AMT);
        final int ivar_string = cursor.getColumnIndexOrThrow(CartSchema.VARIENT_STRING);
        //if (cursor != null && cursor.moveToFirst()) {
        holder.name.setText(cursor.getString(iName));
        holder.code.setText(cursor.getString(iCode));
        holder.listCartQty.setText(cursor.getString(ivar_string));
        double total = Double.parseDouble(cursor.getString(iPrice));
        double discounttotal = Double.parseDouble(cursor.getString(idiscountprice));
        holder.amount.setText("\u20B9" + String.valueOf((int) total * cursor.getInt(iQty)));
        if ((cursor.getInt(iQty) * cursor.getInt(idiscount) > Integer.parseInt(cursor.getString(imaxamt)))&&(Integer.parseInt(cursor.getString(imaxamt))!=0) )
        {
            Log.d("max amount", cursor.getString(imaxamt));
            holder.DiscountPrice.setText("\u20B9" + String.valueOf((int) (Double.parseDouble(String.valueOf(total * cursor.getInt(iQty))) - Double.parseDouble(cursor.getString(imaxamt)))));
        }
        else if (Integer.parseInt(cursor.getString(imaxamt))==0)
        {
            Log.d("max amount", cursor.getString(imaxamt));
            holder.DiscountPrice.setText("\u20B9" + String.valueOf((int)(Math.ceil((discounttotal * cursor.getInt(iQty))))));        }
        else
        {
            Log.d("max amount", cursor.getString(imaxamt));
            holder.DiscountPrice.setText("\u20B9" + String.valueOf((int)(Math.ceil((discounttotal * cursor.getInt(iQty))))));
        }

        loader.DisplayImage(cursor.getString(iUrl), holder.product, R.drawable.default_product);
        holder.remove.setTag(cursor.getString(iId));
        holder.x.setTag(cursor.getString(iId));
        holder.couponavailable.setTag(cursor.getString(iId));
        /* if (cursor.getInt(idiscount)==0)
        {
            holder.couponapply.setVisibility(View.GONE);
        }*/

        if ("0".equalsIgnoreCase(cursor.getString(idiscountprice)))
        {
            holder.DiscountPrice.setVisibility(View.GONE);
        }

        if (!"0".equalsIgnoreCase(cursor.getString(idiscountprice)))
        {
            holder.amount.setBackgroundResource(R.drawable.strikethrough);
        }

        if ((!"0".equalsIgnoreCase(cursor.getString(ipromocode))) && (!akilogic)) {
            String abc = cursor.getString(ipromocode);
            try {
                JSONArray jsonArray = new JSONArray(abc);
                ArrayList<CouponModal> couponModals = new ArrayList<>();
                //couponModals.clear();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject obj = (JSONObject) jsonArray.get(j);
                    CouponModal couponModal = new CouponModal();
                    couponModal.setCoupondis(obj.getString("discription"));
                    couponModal.setCouponName(obj.getString("coupons"));
                    couponModal.setMinamount(obj.getString("min_bonous_qty"));
                    //couponModal.setMaxamount(obj.getString("max_bonous_amount"));
                    //couponModal.setDiscountType(obj.getString("discount_bonus"));
                    //couponModal.setDiscountvalue(obj.getString("bonous"));
                    couponModal.setTcDes(Html.fromHtml(obj.getString("term_condition")).toString());
                    couponModals.add(couponModal);
                    holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    holder.recyclerView.setAdapter(new CouponAdapter(couponModals, cursor));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if ("0".equalsIgnoreCase(cursor.getString(ipromocode))) {
            holder.couponavailable.setVisibility(View.GONE);
        }

        if ("0".equalsIgnoreCase(cursor.getString(ipromoapplied))) {
            holder.couponapply.setVisibility(View.GONE);
        }

        if (!"0".equalsIgnoreCase(cursor.getString(ipromoapplied))) {
            holder.couponavailable.setVisibility(View.GONE);
        }

        holder.couponavailable.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mCursor.moveToPosition(cursor.getPosition());
                qty = cursor.getString(iQty);
                minqty = Integer.parseInt(cursor.getString(iQty));
                //rowid = view.getTag();
                mCursor.moveToPosition(cursor.getPosition());
                if (!check) {
                    holder.radiolist.setVisibility(View.VISIBLE);
                    check = true;
                } else {
                    holder.radiolist.setVisibility(View.GONE);
                    check = false;
                }
            }
        });

        holder.x.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCursor.moveToPosition(cursor.getPosition());
                holder.couponapply.setVisibility(View.GONE);
                ContentValues cv = new ContentValues();
                akilogic = false;
                cv.put(CartSchema.DISCOUNT, "0");
                cv.put(CartSchema.CART_PROMO_APPLIED, "0");
                cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE, "0");
                cv.put(CartSchema.PROMO_CODE, "");
                couponmap.remove(cursor.getString(ipromo_code));
                for (int i = 0; i < couponmap.size(); i++) {
                    helper.setDefaults("coupon_map", "," + couponmap.get(i), mContext);
                }
                Log.d("db", db.couponstring());
                Log.d("dbstring", couponmap.toString());
                context.getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()});
                context.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                Intent intent = new Intent(mContext, CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });

        holder.add.setTag(cursor.getString(iId));
        holder.sub.setTag(cursor.getString(iId));

        if (cursor.getString(icod).equalsIgnoreCase("0")) {
            holder.cod_code.setText("COD not available!");
            holder.cod_code.setTextColor(Color.parseColor("#F44336"));
        } else {
            holder.cod_code.setText("COD available!");//#4CAF50
            holder.cod_code.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.picker.setText("" + cursor.getInt(iQty));
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                akilogic = true;
                //mCursor.moveToPosition(cursor.getPosition());
                //minqty = Integer.parseInt(cursor.getString(iQty));
                String no = holder.picker.getText().toString();
                int integer = Integer.parseInt(no);
                Cursor c = context.getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()}, null);
                c.moveToNext();
//                Log.i("CRTADAPT", c.getInt(iInv) + "/" + cursor.getInt(iMoq));
                if (c != null)
                {
                    if (integer > 0 && integer < c.getInt(iInv))
                    {
                        integer = integer + 1;
                        holder.picker.setText("" + integer);
                        ContentValues cv = new ContentValues();
                        cv.put(CartSchema.PRODUCT_QTY, integer);
                        context.getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()});
                        context.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                    }
                }
            }
        });

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                akilogic = true;
                //mCursor.moveToPosition(cursor.getPosition());
                //minqty = Integer.parseInt(cursor.getString(iQty));
                if (!"0".equalsIgnoreCase(cursor.getString(iminqty)))
                {
                    if (Integer.parseInt(cursor.getString(iQty)) - 1 < Integer.parseInt(cursor.getString(iminqty))) {
                        String no = holder.picker.getText().toString();
                        int integer = Integer.parseInt(no);
                        Cursor c = context.getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()}, null);
                        if (c != null && c.moveToFirst())
                        {
                            if (integer != 1 && integer > c.getInt(iMoq))
                            {
                                integer = integer - 1;
                                holder.picker.setText("" + integer);
                                ContentValues cv = new ContentValues();
                                cv.put(CartSchema.PRODUCT_QTY, integer);
                                context.getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()});
                                context.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                            }
                        }
                        ContentValues cv = new ContentValues();
                        cv.put(CartSchema.DISCOUNT, "0");
                        cv.put(CartSchema.CART_PROMO_APPLIED, "0");
                        cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE, "0");
                        cv.put(CartSchema.PROMO_CODE, "");
                        cv.put(CartSchema.PROMO_MIN_QTY, "0");
                        context.getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()});
                        context.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                        Intent intent = new Intent(mContext, CartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                    }

                    else {
                        String no = holder.picker.getText().toString();
                        int integer = Integer.parseInt(no);
                        Cursor c = context.getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()}, null);
                        if (c != null && c.moveToFirst()) {
                            if (integer != 1 && integer > c.getInt(iMoq)) {
                                integer = integer - 1;
                                holder.picker.setText("" + integer);
                                ContentValues cv = new ContentValues();
                                cv.put(CartSchema.PRODUCT_QTY, integer);
                                context.getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()});
                                context.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                            }
                        }
                    }
                }
                 else {
                    String no = holder.picker.getText().toString();
                    int integer = Integer.parseInt(no);
                    Cursor c = context.getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()}, null);
                    if (c != null && c.moveToFirst()) {
                        if (integer != 1 && integer > c.getInt(iMoq)) {
                            integer = integer - 1;
                            holder.picker.setText("" + integer);
                            ContentValues cv = new ContentValues();
                            cv.put(CartSchema.PRODUCT_QTY, integer);
                            context.getContentResolver().update(ChatProvider.CART_URI, cv, CartSchema.KEY_ROWID + "=?", new String[]{(String) v.getTag()});
                            context.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                        }
                    }
                }
            }
        });

    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return LayoutInflater.from(context).inflate(R.layout.card_layout, viewgroup, false);
    }

    private static class ViewHolder
    {
        TextView code;
        TextView name;
        TextView amount, cod_code, x;
        ImageView product;
        Button add, sub;
        TextView picker;
        RelativeLayout couponapply, couponavailable;
        LinearLayout radiolist;
        TextView DiscountPrice,listCartQty;
        //RadioGroup radioGroup;
        ImageView remove;
        RecyclerView recyclerView;

        private ViewHolder()
        {

        }

    }

    //-------------------------akshay----------------------------//
    public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {
        JSONArray couponarray = new JSONArray();
        private final ArrayList<CouponModal> orderItemses;
        Cursor cursor;

        public CouponAdapter(ArrayList<CouponModal> newsList, Cursor cursor) {
            this.cursor = cursor;
            this.orderItemses = newsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.couponcard, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final TextView a = holder.coupon_des;
            final TextView c = holder.tc;
            final Button b = holder.btn_apply;
            final Boolean y = Boolean.FALSE;
            a.setText(orderItemses.get(position).getCoupondis());

            if(orderItemses.get(position).getTcDes().equalsIgnoreCase(""))
            {
                c.setVisibility(View.INVISIBLE);
            }
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(mContext)
                            .create();
                    ad.setCancelable(false);
                    ad.setTitle("Terms and Conditions");
                    ad.setMessage(orderItemses.get(position).getTcDes());
                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ad.dismiss();
                        }
                    });
                    ad.show();
                }
            });

            b.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         // Toast.makeText(mContext,qty+orderItemses.get(position).getMinamount(),Toast.LENGTH_SHORT).show();
                                    /*  if (mCursor.getInt(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY)) < Integer.parseInt(orderItemses.get(position).getMinamount()))
                                   //      if (minqty < Integer.parseInt(orderItemses.get(position).getMinamount()))
                                         {
                                             Toast.makeText(mContext, "Quantity should be " + orderItemses.get(position).getMinamount() + " or more to apply this promotion"+" "+ mCursor.getInt(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY)) +","+orderItemses.get(position).getMinamount(), Toast.LENGTH_SHORT).show();
                                         }

                                         else
                                         {*/

                                         if (!y) {
                                             b.setEnabled(false);
                                             coupondel = orderItemses.get(position).couponName;
                                             couponmap.add(coupondel);
                                             Log.d("jsonp", couponmap.toString());
                                             for (int i = 0; i < couponmap.size(); i++) {
                                                 couponarray.put(couponmap.get(i));
                                             }

                                             for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                                                 int pos = mCursor.getPosition() + 1;
                                                 try {
                                                     JSONObject j = new JSONObject();
                                                     if ((!mCursor.getString(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_VARIENT)).equalsIgnoreCase("")) && (!mCursor.getString(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_VARIENT)).equalsIgnoreCase("[]"))) {
                                                         j = new JSONObject(mCursor.getString(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_VARIENT)));
                                                     }

                                                     JSONObject jsonObject = new JSONObject();
                                                     try {
                                                         jsonObject.put("product_id", mCursor.getString(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_ID)));
                                                         jsonObject.put("amount", mCursor.getString(mCursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY)));
                                                         jsonObject.put("product_options", j);
                                                         //jsonObject.put(mCursor.getString(,mCursor.getString(mCursor.getColumnIndexOrThrow()));
                                                         array.put(jsonObject);
                                                     } catch (JSONException e) {
                                                         e.printStackTrace();
                                                     }
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }


                                             }
                                         }

                                         final JSONObject jsonObject28 = new JSONObject();

                                         try {
                                             jsonObject28.put("promotions_cods", couponarray);
                                             jsonObject28.put("product_data", array);
                                             jsonObject28.put("result_ids", "cart_status*,wish_list*,checkout*,account_info*");
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                         Log.d("jsonparams", jsonObject28.toString());
                                         new callpromo(jsonObject28).execute();

                      /*  RestClient.GitApiInterface service = RestClient.getClient();
                        Call<JsonElement> call = service.promo_cart_download(helper.getDefaults("user_id", mContext),jsonObject28.toString(), helper.getB64Auth(mContext), "application/json", "application/json");
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Response<JsonElement> response) {
                                try {
                                    JSONObject object = new JSONObject(response.body().toString());
                                    Log.d("promocartdownload",object.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {

                            }
                        });*/
                                     }
                                 }


            );
        }

      public class callpromo extends AsyncTask<String, String, String>
      {
            JSONObject c = new JSONObject();
            JSONObject d = new JSONObject();
            ProgressDialog progressDialog;

            public callpromo(JSONObject jsonObject) {
                this.c = jsonObject;
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Please wait..");
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings)
            {
                d = jsonParser.getJSONFromUrlPut(AppUtil.URL + "promotions/" + helper.getDefaults("user_id", mContext), c, mContext);
                Log.d("promourl",AppUtil.URL + "promotions/" + helper.getDefaults("user_id", mContext));
                Log.d("rabba", d.toString());
                return null;
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                progressDialog.dismiss();
                if (d.has("cart"))
                {
                    mContext.getContentResolver().delete(ChatProvider.CART_URI, null, null);
                    //mContext.getContentResolver().notifyChange(ChatProvider.CART_URI, null, false);
                    try
                    {
                        JSONArray jsonArray1 = d.getJSONObject("cart").getJSONArray("productsdata");

                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject obj = (JSONObject) jsonArray1.get(j);
                            String promoarray = "0";
                            String productvar="";
                            String var="";
                            String promoarrayapplied = "0";
                            String id = obj.getString("product_id");
                            String name = obj.getString("product_name");
                            String code = obj.getString("product_code");
                            String weight = obj.getString("productWeight");
                            String cat_id = obj.getString("main_category");
                            String qty = obj.getString("qty");
                            String image = obj.getString("image");
                            String cod_available = obj.getString("cod_available");
                            String s67 = obj.getString("status");
                            String moq = obj.getString("product_moq");
                            String freeShipping = obj.getString("free_shipping");
                            String productInventory = obj.getString("product_inventory");
                            String productPrice = obj.getString("product_price");
                            if(obj.has("product_options"))
                            {
                                productvar = obj.getString("product_options");
                                var = obj.getString("selected_varients");
                            }
                            Log.d("varients",var.toString());
                            String productPriceDiscount = obj.getString("discount_price");
                            Log.d("discountprice",productPriceDiscount);
                            String discount = "0";
                            if(obj.has("invalid_num_prod"))
                            {
                                Toast.makeText(mContext,"Quantity should be "+obj.getString("invalid_num_prod")+" or more to apply this promotion",Toast.LENGTH_SHORT).show();
                                couponmap.remove(coupondel);
                            }

                            if (obj.has("cartpromotions"))
                            {
                                JSONArray jsonArray28 = obj.getJSONArray("cartpromotions");
                                Log.d("cartpromo", jsonArray28.toString());
                                promoarray = jsonArray28.toString();
                            }
                            String promocodeapplied = "0";
                            String minpromoqty = "0";
                            String maxpromoamt = "0";

                            if (obj.has("appliedpromo"))
                            {
                                JSONObject jsonArray29 = obj.getJSONObject("appliedpromo");
                                promocodeapplied = jsonArray29.getString("coupons");
                                minpromoqty = jsonArray29.getString("min_bonous_qty");
                                maxpromoamt = jsonArray29.getString("max_bonous_amount");
                                Log.d("Appliedpromo", jsonArray29.toString());
                                promoarrayapplied = jsonArray29.toString();
                                discount = obj.getJSONObject("appliedpromo").getString("bonous");
                            }

                            ContentValues cv = new ContentValues();
                            cv.put(CartSchema.PRODUCT_ID, id);
                            cv.put(CartSchema.PRODUCT_NAME, name);
                            cv.put(CartSchema.PAY_METHOD, cod_available);
                            cv.put(CartSchema.PRODUCT_CODE, code);
                            cv.put(CartSchema.PRODUCT_STATUS, s67);
                            cv.put(CartSchema.PRODUCT_QTY, qty);
                            cv.put(CartSchema.PRODUCT_URL, image);
                            cv.put(CartSchema.PRODUCT_WEIGHT, weight);
                            cv.put(CartSchema.PRODUCT_CAT_ID, cat_id);
                            Log.d("disscsc",""+discount);
                            cv.put(CartSchema.DISCOUNT, discount);
                            cv.put(CartSchema.PRODUCT_MOQ, moq);
                            cv.put(CartSchema.PRODUCT_SHIPPING, freeShipping);
                            cv.put(CartSchema.PRODUCT_INVENTORY, productInventory);
                            cv.put(CartSchema.PRODUCT_PRICE, productPrice);
                            cv.put(CartSchema.CART_PROMO, promoarray);
                            cv.put(CartSchema.CART_PROMO_APPLIED, promoarrayapplied);
                            cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE, productPriceDiscount);
                            cv.put(CartSchema.PROMO_CODE, promocodeapplied);
                            cv.put(CartSchema.PROMO_MIN_QTY, minpromoqty);
                            cv.put(CartSchema.PROMO_MAX_AMT, maxpromoamt);
                            cv.put(CartSchema.PRODUCT_VARIENT,productvar);
                            cv.put(CartSchema.VARIENT_STRING,var);
                            Uri uri = mContext.getContentResolver().insert(ChatProvider.CART_URI, cv);
                            mContext.getContentResolver().notifyChange(ChatProvider.CART_URI, null);
                            Log.d("Is three ", "IN " + uri.toString());
                            //new CartActivity().refreshcursor(mContext);
                            Intent intent = new Intent(mContext, CartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        @Override
        public int getItemCount() {
            return orderItemses.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView coupon_des, tc;
            Button btn_apply;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.coupon_des = (TextView) itemView.findViewById(R.id.coupon_des);
                this.btn_apply = (Button) itemView.findViewById(R.id.btn_apply);
                this.tc = (TextView) itemView.findViewById(R.id.tandc);
            }
        }
    }


}
