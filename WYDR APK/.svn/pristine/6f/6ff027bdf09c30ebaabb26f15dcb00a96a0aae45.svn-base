package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.modal.Seller;

/**
 * Created by surya on 15/9/15.
 */
public class SellerFilterAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    public ArrayList<Seller> data;
    public ArrayList<Seller> memberData;
    boolean[] items;
    int pos;
    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    private Activity activity;
    private SparseBooleanArray mSelectedItemsIds;

    public SellerFilterAdapter(Activity a, ArrayList<Seller> d) {
        activity = a;
        data = d;
        memberData = d;
        mSelectedItemsIds = new SparseBooleanArray();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!(itemChecked.size() > 0)) {
            for (int i = 0; i < this.getCount(); i++) {
                itemChecked.add(i, false); // initializes all items value with false
            }
        }
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.seller_row, null);

        CheckBox box = (CheckBox) vi.findViewById(R.id.checkBoxSeller);

        final Seller song = data.get(position);

        box.setTag(song);
        box.setText(MyTextUtils.toTitleCase(song.getName()));

        if (song.isDisable()) {
            box.setClickable(false);
            box.setTextColor(Color.GRAY);

        } else {
            box.setClickable(true);
            box.setTextColor(activity.getResources().getColor(R.color.text_color));
        }
//        box.setOnClickListener( new View.OnClickListener() {
//            public void onClick(View v) {
//                CheckBox cb = (CheckBox) v ;
//                Seller seller = (Seller) cb.getTag();
////                Toast.makeText(getApplicationContext(),
////                        "Clicked on Checkbox: " + cb.getText() +
////                                " is " + cb.isChecked(),
////                        Toast.LENGTH_LONG).show();
//               // seller.setIsCheck(cb.isChecked());
//                seller.setIsSelected(cb.isChecked());
//            }
//        });
        box.setChecked(song.isSelected());
        // }

        return vi;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<Seller> filterlist = new ArrayList<Seller>();

                if (memberData == null) {
                    memberData = new ArrayList<Seller>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getName();
                        Log.d("data", data);

                        if (data.toLowerCase().contains(constraint.toString())) {
                            Seller modal = new Seller();
                            modal.setName(memberData.get(i).getName());
                            modal.setId(memberData.get(i).getId());
                            modal.setParent(memberData.get(i).getParent());
                            modal.setIsSelected(memberData.get(i).isSelected());
                            modal.setDisable(memberData.get(i).isDisable());
                            modal.setIsCheck(memberData.get(i).isCheck());
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

                data = (ArrayList<Seller>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    public void toggleSelection(int position) {

        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    //
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
