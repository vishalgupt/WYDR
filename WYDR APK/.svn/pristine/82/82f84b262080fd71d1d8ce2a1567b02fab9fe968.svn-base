package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
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
import wydr.sellers.modal.AttachModal;
import wydr.sellers.network.ImageLoader;

/**
 * Created by surya on 5/8/15.
 */
public class AttachAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private Activity activity;
    private ArrayList<AttachModal> data;
    private ArrayList<AttachModal> memberData;

    public AttachAdapter(Activity a, ArrayList<AttachModal> d) {
        activity = a;
        data = d;
        this.memberData = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.attech_product_layout, null);

        TextView title = (TextView) vi.findViewById(R.id.txtTitleAttach); // title
        TextView code = (TextView) vi.findViewById(R.id.txtCodeAttech); // artist name
        TextView mrp = (TextView) vi.findViewById(R.id.txtMRPAttech); // duration
        TextView moq = (TextView) vi.findViewById(R.id.txtMOQ);
        TextView sell = (TextView) vi.findViewById(R.id.txtsp); // duration
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.orderThumb); // thumb image


        AttachModal song = data.get(position);
        title.setText(song.getName());
        code.setText(song.getCode());
        mrp.setText("MRP : " + activity.getResources().getString(R.string.rs) + " " + song.getPrice());
        sell.setText("SP : " + activity.getResources().getString(R.string.rs) + " " + song.getSellingPrice());
        if (song.getMoq() != null) {
            if (song.getMoq().equalsIgnoreCase("null") || song.getMoq().equalsIgnoreCase("0")) {
                //  moq.setVisibility(View.GONE);
                moq.setText("MOQ : 1");
            } else {
                moq.setText("MOQ : " + song.getMoq());
            }
        }

        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        imageLoader.DisplayImage(song.getImgUrl(), thumb_image);
        return vi;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<AttachModal> filterlist = new ArrayList<AttachModal>();

                if (memberData == null) {
                    memberData = new ArrayList<AttachModal>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getName();
                        Log.d("data", data);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            AttachModal modal = new AttachModal();
                            //id, name, code, price, sellingPrice, imgUrl;
                            modal.setId(memberData.get(i).getId());
                            modal.setName(memberData.get(i).getName());
                            modal.setCode(memberData.get(i).getCode());
                            modal.setPrice(memberData.get(i)
                                    .getPrice());
                            modal.setSellingPrice(memberData.get(i).getSellingPrice());
                            modal.setImgUrl(memberData.get(i).getImgUrl());
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

                data = (ArrayList<AttachModal>) results.values;
                notifyDataSetChanged();
            }
        }

                ;

    }


}
