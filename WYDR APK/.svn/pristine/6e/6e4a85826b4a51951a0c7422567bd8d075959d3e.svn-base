package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.activities.CartActivity;
import wydr.sellers.modal.StockModal;

public class StockoutAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<StockModal> data;
    private ListLoader imageLoader;
    Activity activity;


    public StockoutAdapter(CartActivity a, ArrayList<StockModal> delIds) {
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ListLoader(a);
        activity = a;
        data= delIds;
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
            rowView = inflater.inflate(R.layout.stockout_list, parent, false);
        }

        holder.productname = (TextView) rowView.findViewById(R.id.stock_text);
        holder.img = (ImageView) rowView.findViewById(R.id.stock_image);
        holder.productname.setText(data.get(position).getName());
        imageLoader.DisplayImage2(data.get(position).getUrl(), holder.img, R.drawable.default_product);
        rowView.setTag(holder);
        return rowView;
    }

    public class ViewHolder {
        TextView productname;
        ImageView img;
    }
}
