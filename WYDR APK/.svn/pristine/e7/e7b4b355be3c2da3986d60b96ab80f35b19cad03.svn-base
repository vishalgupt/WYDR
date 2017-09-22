package wydr.sellers.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.modal.ContacLlist;
import wydr.sellers.registration.Helper;

/**
 * Created by Deepesh_pc on 03-09-2015.
 */


/**
 * Created by Deepesh_pc on 07-07-2015.
 */
public class NetworkResultAdapter extends ArrayAdapter<ContacLlist> {
    private final Context context;
    public LayoutInflater mLayoutInflater;
    Helper helper = new Helper();
    private ArrayList<ContacLlist> values;

    public NetworkResultAdapter(Context context, ArrayList<ContacLlist> FillingValues) {
        super(context, R.layout.addcontactslayout, FillingValues);
        this.context = context;
        this.values = FillingValues;


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        View rowView = convertView;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_layout_mynetwork, parent, false);
            holder.textViewname = (TextView) rowView.findViewById(R.id.MyNetworkName);


            holder.textViewnumber = (TextView) rowView.findViewById(R.id.MyNetworkNumber);
            holder.image = (ImageView) rowView.findViewById(R.id.profileMyNet);
            holder.chat = (ImageView) rowView.findViewById(R.id.MyNetworkViewChat);
            //  holder.profile = (ImageView) rowView.findViewById(R.id.MyNetworkViewProfile);

            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        //Log.e("NAME", "/" + values.get(position).getName() + "/");
        if ((values.get(position).getDisp_name().equalsIgnoreCase(" ") || values.get(position).getDisp_name().equalsIgnoreCase("") || values.get(position).getDisp_name().equalsIgnoreCase(null) || values.get(position).getDisp_name().length() < 1 || values.get(position).getDisp_name().isEmpty()))
            holder.textViewname.setText(helper.ConvertCamel(values.get(position).getCompname()));
        else
            holder.textViewname.setText(helper.ConvertCamel(values.get(position).getDisp_name()));
        holder.textViewnumber.setText(values.get(position).getCompname().toUpperCase());
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.avtar);
        holder.image.setImageBitmap(icon);

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("i m in ", values.get(position).getNetwork_userid().toString());
                Log.e("name", values.get(position).getName().toString());
                Intent i = new Intent(getContext(), ChatActivity.class);
                i.putExtra("user", values.get(position).getNetwork_userid());
                i.putExtra("name", values.get(position).getName().toString());
                i.putExtra("company", values.get(position).getCompname().toString());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);


            }
        });


        return rowView;
    }

    public class ViewHolder {

        TextView textViewname, textViewnumber;
        ImageView image;
        ImageView chat, profile;


    }

}
