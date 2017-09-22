package wydr.sellers.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.holder.DataHolder;
import wydr.sellers.modal.OrderCancelBean;
import wydr.sellers.modal.OrdersModal;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.DrawableBackgroundDrawable;
import wydr.sellers.webconnector.WebHits;
import wydr.sellers.webconnector.callbacks.ResponseCallback;
import wydr.sellers.webconnector.callbacks.ResponseHolder;

/**
 * Created by Deepesh_pc on 07-07-2015.
 */
public class OrdersAdapter extends BaseAdapter implements Filterable,ResponseCallback{
    private Context context;
   // private ArrayList<OrderCancelBean> orderCancelList;
    public LayoutInflater mLayoutInflater;
    public int flag;
    Helper helper = new Helper();

    SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:a");
    SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");

    DrawableBackgroundDrawable drawableDownloader = new DrawableBackgroundDrawable();
    private ArrayList<OrdersModal> values, memberData;


    public OrdersAdapter(Context context, ArrayList<OrdersModal> FillingValues,int flag)
    {
        this.context = context;
        this.values = FillingValues;
        this.memberData = FillingValues;

        this.flag = flag;

    }


    @Override
    public int getCount() {
        return values.size();
    }

    public Object getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View rowView = convertView;
        if (convertView == null) {
            holder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.orders_placed_list, parent, false);
            holder.productname = (TextView) rowView.findViewById(R.id.txtTitleAttach);
            holder.productcode = (TextView) rowView.findViewById(R.id.txtCodeAttech);
            holder.postedby = (TextView) rowView.findViewById(R.id.txtPostedBy);
            holder.order_id = (TextView) rowView.findViewById(R.id.txtOrderId);
            holder.layoutcancelOrder= (RelativeLayout) rowView.findViewById(R.id.ll_order_cancel);

            holder.qty = (TextView) rowView.findViewById(R.id.txtqty);
            holder.price = (TextView) rowView.findViewById(R.id.txtsp);
            holder.status = (TextView) rowView.findViewById(R.id.txtStatus);
            holder.orderedon = (TextView) rowView.findViewById(R.id.txtplacedon);
            holder.img = (ImageView) rowView.findViewById(R.id.orderThumb);
            rowView.setTag(holder);
        }else {
            holder= (ViewHolder) rowView.getTag();
        }

        rowView.setClickable(true);

        holder.productname.setText(helper.ConvertCamel(values.get(position).getTitle()));
        holder.productcode.setText("Product Code : " + values.get(position).getCode());
        if (flag == 1)
            holder.postedby.setText("Seller : " + values.get(position).getPostedBy());
        else
            holder.postedby.setText("Buyer : " + values.get(position).getPostedBy());

        holder.qty.setText("QTY : " + values.get(position).getQty());
        if(values.get(position).getShippingCost() != null && (int)(Double.parseDouble(values.get(position).getShippingCost()))!=0){
            holder.price.setText(context.getResources().getString(R.string.rs) +  (int)(Math.ceil(Double.parseDouble(values.get(position).getShippingCost()) + (Math.ceil(Double.parseDouble(values.get(position).getMrp()) * Double.parseDouble(values.get(position).getQty()))))));
            //holder.price.setText(context.getResources().getString(R.string.rs) +  ((int)(Double.parseDouble(values.get(position).getShippingCost()) + (int)((Double.parseDouble(values.get(position).getMrp())))))*(Double.parseDouble(values.get(position).getQty())));
        }
        else
        {
            holder.price.setText(context.getResources().getString(R.string.rs) +  (int)(Math.ceil(Double.parseDouble(values.get(position).getMrp()) * Double.parseDouble(values.get(position).getQty()))));
            //holder.price.setText(context.getResources().getString(R.string.rs) +" "+((Math.ceil(Double.parseDouble(values.get(position).getMrp())))*(Integer.parseInt(values.get(position).getQty()))));
        }

        holder.status.setText("STATUS : " + values.get(position).getStatus());
        holder.order_id.setText("ORDER ID : " + values.get(position).getOrder_id());
        try {

            holder.orderedon.setText("Placed On : " + format2.format(format.parse(values.get(position).getPlacedon())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
      //  this.orderCancelList= values.get(position).getList();
            if(values.get(position).getList().get(position).getCancelOrder().equalsIgnoreCase("Y")){
                holder.layoutcancelOrder.setVisibility(View.VISIBLE);
            }else if(values.get(position).getList().get(position).getCancelOrder().equalsIgnoreCase("N")){
                holder.layoutcancelOrder.setVisibility(View.GONE);
            }

        holder.layoutcancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    int order_id=values.get(position).getList().get(position).getOrderId();
                    int issuer_id=values.get(position).getList().get(position).getIssuerId();
                    int user_id=values.get(position).getList().get(position).getUserId();
                    showDialog(context,order_id,issuer_id,user_id);



            }
        });

        // imageLoader.DisplayImage2(values.get(position).getImageurls(), holder.img, R.drawable.default_product);
        drawableDownloader.loadDrawable(values.get(position).getImageurls(), holder.img, context.getResources().getDrawable(R.drawable.default_product));
        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<OrdersModal> filterlist = new ArrayList<OrdersModal>();

                if (memberData == null) {
                    memberData = new ArrayList<OrdersModal>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {

                        if (memberData.get(i).getTitle().toLowerCase().contains(constraint.toString()) ||
                                memberData.get(i).getOrder_id().toLowerCase().contains(constraint.toString()) ||
                                memberData.get(i).getPostedBy().toLowerCase().contains(constraint.toString())) {
                            OrdersModal item = new OrdersModal();
                            item.setPlacedon(memberData.get(i).getPlacedon());
                            item.setOrder_id(memberData.get(i).getOrder_id());
                            item.setTitle(memberData.get(i).getTitle());
                            item.setCode(memberData.get(i).getCode());
                            item.setPostedBy(memberData.get(i).getPostedBy());
                            item.setMrp(memberData.get(i).getMrp());
                            item.setQty(memberData.get(i).getQty());
                            item.setImageurls(memberData.get(i).getImageurls());
                            item.setStatus(memberData.get(i).getStatus());

                            filterlist.add(item);

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

                values = (ArrayList<OrdersModal>) results.values;
                if (memberData.size() != 0)
                    notifyDataSetChanged();
            }
        };


    }

    @Override
    public void onSuccess(ResponseHolder holder, int token) {
        if(holder!=null){
            try {
                JSONObject object=new JSONObject(holder.getData());
                String message=null;
                if(object.has("message")){
                   message= object.getString("message");
                }
                showMessage(context,message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String error) {

    }


    public class ViewHolder {

        TextView productname, productcode, status, orderedon, price, qty, postedby, order_id;
        ImageView img;
        RelativeLayout layoutcancelOrder;


    }


    public void showDialog(final Context context, final int order_id, final int issuer_id, final int user_id ){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_cancel_order, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.et_message_dailog);
        final TextInputLayout inputLayoutName= (TextInputLayout) promptsView.findViewById(R.id.err_msg_dialog);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                if (userInput.getText().toString().trim().isEmpty()) {
                                    inputLayoutName.setError("Can't be blank!");
                                    inputLayoutName.setErrorEnabled(true);
                                } else {
                                    inputLayoutName.setErrorEnabled(false);
                                    String message=userInput.getText().toString();
                                    JSONObject jsonObject=new JSONObject();

                                    try {
                                        jsonObject.put("message",message);
                                        jsonObject.put("order_id",order_id);
                                        jsonObject.put("user_id",user_id);
                                        jsonObject.put("issuer_id",issuer_id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    WebHits.cancelOrder(context, OrdersAdapter.this, jsonObject);
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showMessage(Context context,String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create(); //Read Update
        alertDialog.setTitle("Cancel order status");
        alertDialog.setMessage(message);

        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

}

