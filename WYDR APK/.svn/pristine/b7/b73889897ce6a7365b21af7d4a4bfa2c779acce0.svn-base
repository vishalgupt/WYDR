package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.modal.SharedProductsModal;

public class SharedItemsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<SharedProductsModal> data;
    private ListLoader imageLoader;
    Activity activity;
    Context context;

    public SharedItemsAdapter(Activity a, ArrayList<SharedProductsModal> d) {
        data = d;
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ListLoader(a);
        activity=a;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.product_shared_items, parent, false);
        }

        holder.productname = (TextView) rowView.findViewById(R.id.share_title);
        holder.productcode = (TextView) rowView.findViewById(R.id.share_code);
        holder.mrp = (TextView) rowView.findViewById(R.id.share_mrp);
        holder.sp = (TextView) rowView.findViewById(R.id.share_sp);
        holder.moq = (TextView) rowView.findViewById(R.id.share_moq);
        holder.postedby = (TextView) rowView.findViewById(R.id.share_PostedBy);
        holder.img = (ImageView) rowView.findViewById(R.id.share_orderThumb);
        holder.out_stock = (TextView) rowView.findViewById(R.id.share_out_of_stock);


        final SharedProductsModal modal = data.get(position);

        holder.productname.setText(modal.getTitle());
        holder.productcode.setText("Product Code: " + modal.getCode());
        holder.mrp.setText("MRP: " + modal.getMRP() + "/pc");
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.sp.setText("SP: " + modal.getSP() + "/pc");
        holder.moq.setText("MOQ: " +modal.getMOQ());

       if(Long.parseLong(modal.getQuantity())<Long.parseLong(modal.getMOQ()))
       {
           holder.out_stock.setVisibility(View.VISIBLE);
       }
        else {
           holder.out_stock.setVisibility(View.GONE);
       }
        if (modal.getType().equals("by_me")) {
            holder.postedby.setVisibility(View.GONE);
        } else {
            holder.postedby.setVisibility(View.VISIBLE);
            holder.postedby.setText("Seller: " + modal.getPostedBy());
        }
        Picasso.with(context)
                .load(modal.getImageurl())
                .placeholder(R.drawable.default_product)
                .into(holder.img);
       // imageLoader.DisplayImage2(modal.getImageurl(), holder.img, R.drawable.default_product);
        rowView.setTag(holder);
        return rowView;
    }
    public class ViewHolder {

        TextView productname, productcode, mrp, sp, moq, out_stock, postedby;
        ImageView img;


    }
}
