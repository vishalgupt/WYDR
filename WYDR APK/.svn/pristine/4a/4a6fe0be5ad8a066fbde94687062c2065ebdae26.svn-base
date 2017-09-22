package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.modal.CatalogProductModal;
import wydr.sellers.network.ImageLoader;


/**
 * Created by surya on 7/8/15.
 */
public class SellersAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    // public ImageLoader imageLoader;
    String str;
    private Activity activity;
    private ArrayList<CatalogProductModal> data;
    private ArrayList<CatalogProductModal> memberData;

    public SellersAdapter(Activity a, ArrayList<CatalogProductModal> d) {
        activity = a;
        data = d;
        this.memberData = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //    imageLoader=new ImageLoader(activity.getApplicationContext());
        imageLoader = new ImageLoader(activity.getApplicationContext());
        Log.e("Seller si", data.size() + "");
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

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.sellerlist, null);

        TextView title = (TextView) vi.findViewById(R.id.txtTitleAttach); // title
        TextView code = (TextView) vi.findViewById(R.id.txtCodeAttech); // artist name
        TextView mrp = (TextView) vi.findViewById(R.id.txtMRPAttech); // duration
        TextView moq = (TextView) vi.findViewById(R.id.txtmoq);
        TextView sell = (TextView) vi.findViewById(R.id.txtsp); // duration
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.orderThumb); // thumb image
        final ImageView chat = (ImageView) vi.findViewById(R.id.chat);
        ImageView share = (ImageView) vi.findViewById(R.id.share);
        ImageView like = (ImageView) vi.findViewById(R.id.like);
        Log.e("title si", title.toString() + "/" + position);
        Log.e("Seller si", data.size() + "");
        title.setText(data.get(position).getName());
        code.setText(data.get(position).getCode());
        mrp.setText(data.get(position).getMoq());
        moq.setText(data.get(position).getName());
        sell.setText(data.get(position).getSellingPrice());

        CatalogProductModal modal = data.get(position);
        Log.e("check in adapter", modal.getId());
        chat.setTag(modal.getUserid());

        Log.e("chat.getTag()r", chat.getTag() + "");
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", chat.getTag() + "@" + AppUtil.SERVER_NAME));
            }
        });
        if (data.get(position).getImgUrl() != null) {
            Log.e("iamge", data.get(position).getImgUrl());
            imageLoader.DisplayImage(data.get(position).getImgUrl(), thumb_image);

        }
        return vi;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<CatalogProductModal> filterlist = new ArrayList<CatalogProductModal>();

                if (memberData == null) {
                    memberData = new ArrayList<CatalogProductModal>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getName();
                        Log.d("data", data);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            CatalogProductModal modal = new CatalogProductModal();

                            modal.setId(memberData.get(i).getId());
                            modal.setName(memberData.get(i).getName());
                            modal.setCode(memberData.get(i).getCode());
                            modal.setPrice(memberData.get(i)
                                    .getPrice());
                            modal.setSellingPrice(memberData.get(i).getSellingPrice());
                            modal.setImgUrl(memberData.get(i).getImgUrl());
                            modal.setQty(memberData.get(i).getQty());
                            modal.setCat(memberData.get(i).getCat());
                            modal.setMinqty(memberData.get(i).getMinqty());
                            modal.setCategories(memberData.get(i).getCategories());
                            modal.setImageURLs(memberData.get(i).getImageURLs());
                            modal.setUserid(memberData.get(i).getUserid());
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

                data = (ArrayList<CatalogProductModal>) results.values;
                if (memberData.size() != 0)
                    notifyDataSetChanged();
            }
        }

                ;

    }
}
