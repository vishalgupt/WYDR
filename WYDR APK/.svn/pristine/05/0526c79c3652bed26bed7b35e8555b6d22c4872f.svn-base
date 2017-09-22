package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import wydr.sellers.R;

/**
 * Created by surya on 23/9/15.
 */
public class FilterMenuAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    SparseBooleanArray mCheckStates;
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    // public ImageLoader imageLoader;

    public FilterMenuAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCheckStates = new SparseBooleanArray(data.size());
        //    imageLoader=new ImageLoader(activity.getApplicationContext());
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
            vi = inflater.inflate(R.layout.filter_menu_layout, null);

        TextView title = (TextView) vi.findViewById(R.id.titleFilter); // title

        HashMap<String, String> group;
        group = data.get(position);

        // Setting all values in listview
        title.setText(group.get("title"));

        return vi;
    }
}
