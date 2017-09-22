package wydr.sellers.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import wydr.sellers.R;


public class SearchAdapter extends BaseAdapter {
    private final Context context;
    String[] captions;

    int[] images;

    public SearchAdapter(Context context, String[] captions, int[] images) {

        this.context = context;
        this.captions = captions;
        this.images = images;
        Log.e("entererd", captions.length + "");

    }

    @Override
    public int getCount() {
        return captions.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        View rowView = convertView;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.search_list, parent, false);
            holder.textViewname1 = (TextView) rowView.findViewById(R.id.search_text);
            holder.image1 = (ImageView) rowView.findViewById(R.id.search_imageicon);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.textViewname1.setText(captions[position]);
        holder.image1.setImageResource(images[position]);


        return rowView;
    }

    public class ViewHolder {

        TextView textViewname1;
        ImageView image1;


    }


}
