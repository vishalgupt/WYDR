package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.modal.AddressModal;

/**
 * Created by surya on 2/12/15.
 */
public class AddressAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private ArrayList<AddressModal> data;


    public AddressAdapter(Activity a, ArrayList<AddressModal> d) {
        activity = a;
        data = d;
        //   this.memberData = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.address_layout, null);

        TextView title = (TextView) vi.findViewById(R.id.txtAddressName);
        TextView phone = (TextView) vi.findViewById(R.id.txtAddressNumber);
        TextView address = (TextView) vi.findViewById(R.id.txtAddressAddress);


        AddressModal song = data.get(position);
        title.setText(song.getName());
        phone.setText(song.getPhone());
        address.setText(song.getAddress());

        return vi;
    }


}
