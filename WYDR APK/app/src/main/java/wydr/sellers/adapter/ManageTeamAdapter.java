package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.ManageTeam;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 19/1/16.
 */
public class ManageTeamAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public ListLoader imageLoader;
    private Activity activity;
    private ArrayList<ManageTeam> data;
Helper helper = new Helper();
    public ManageTeamAdapter(Activity a, ArrayList<ManageTeam> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ListLoader(activity.getApplicationContext());

    }

    public int getCount() {
        if (data != null)
            return data.size();
        else
        return 0;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        //if (convertView == null)
            vi = inflater.inflate(R.layout.team_layout, null);
        final ViewHolder holder = createViewHolder(vi);



        if (data.get(position).isOwner()) {
            holder.owner.setVisibility(View.VISIBLE);
        } else {
            holder.owner.setVisibility(View.GONE);
        }
        if (data.get(position).isPrimary()) {
            holder.primary.setVisibility(View.VISIBLE);
        } else {
            holder.primary.setVisibility(View.GONE);
        }
        // Setting all values in listview
        holder.title.setText(data.get(position).getName());
        holder.phone.setText(data.get(position).getPhone());

        String url = data.get(position).getThumb();
        Log.i("TAG URL", "" + url);
        if (url == null || url.equalsIgnoreCase("")) {
            holder.thumb_image.setImageResource(R.drawable.avtar);
        } else {
            if (url.contains("http")) {
                imageLoader.DisplayImage2(url, holder.thumb_image, R.drawable.avtar);
            } else {
                holder.thumb_image.setImageBitmap(helper.decodeSampledBitmapFromResource(url, 100, 100));
            }

        }
        return vi;
    }

    private static class ViewHolder {

        TextView title;
        TextView phone;
        TextView owner;
        ImageView thumb_image;
        TextView primary;


        private ViewHolder() {
        }

    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewholder = new ViewHolder();
        viewholder.title = (TextView)  view.findViewById(R.id.txtUserNameTeam);
        viewholder.phone = (TextView) view.findViewById(R.id.txtUserPhoneNumberTeam);
        viewholder.owner = (TextView) view.findViewById(R.id.txtOwner);
        viewholder.primary = (TextView) view.findViewById(R.id.txtPrimaryNumber);
        viewholder.thumb_image = (ImageView) view.findViewById(R.id.viewUserThumbTeam);
        return viewholder;
    }
}
