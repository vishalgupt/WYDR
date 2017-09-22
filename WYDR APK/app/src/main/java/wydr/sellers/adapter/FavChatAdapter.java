package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import wydr.sellers.R;
import wydr.sellers.acc.FavChat;
import wydr.sellers.acc.ListLoader;

/**
 * Created by surya on 14/10/15.
 */
public class FavChatAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    final Calendar calendar = Calendar.getInstance();
    public ListLoader imageLoader;
    SimpleDateFormat formatNew = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat formatResult = new SimpleDateFormat("hh:mm aa");
    private Activity activity;
    private ArrayList<FavChat> data, memberData;

    public FavChatAdapter(Activity a, ArrayList<FavChat> d) {
        activity = a;
        data = d;
        this.memberData = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ListLoader(activity.getApplicationContext());
    }

    public int getCount() {
        if(data!=null)
        return data.size();
        else
            return 0;
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
            vi = inflater.inflate(R.layout.user_layout, null);

        TextView title = (TextView) vi.findViewById(R.id.txtUserName); // title
        TextView date = (TextView) vi.findViewById(R.id.txtdate); //date
        TextView message = (TextView) vi.findViewById(R.id.txtStatus); // msg
        TextView count = (TextView) vi.findViewById(R.id.txtMsgCount); // Count
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.viewUserThumb); // thumb image

        FavChat song;
        song = data.get(position);

        // Setting all values in listview
        title.setText(song.getName());
        try {
            //Log.i("date2-date2",song.getDate().toString() + "---" );
            Date c = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(song.getDate().toString());
            //  Log.i("date2-c",c.toString() + "---" );
            // Log.i("DATE :" , format.format(Calendar.getInstance().getTime()));
            Date current = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss").parse(format.format(Calendar.getInstance().getTime()));
            //  Log.i("current",current.toString());

            date.setText(dateFormat(c, current, c));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        message.setText(song.getMsg());
        count.setVisibility(View.GONE);
        switch (song.getIndicator()) {
            case 0:
                message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.out_arrow_with_padding, 0, 0, 0);
                break;
            case 1:
                message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.in_arrow_with_padding, 0, 0, 0);
                break;
            case 2:
                message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.productbasket_with_padding, 0, 0, 0);
                break;
            case 3:
                message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.productbasket_with_padding, 0, 0, 0);
                break;

        }

        if (song.getUrl() != null) {
            imageLoader.DisplayImage(song.getUrl(), thumb_image, R.drawable.avtar);
            Log.i("Fav chat Adapter", "song.getUrl() " + song.getUrl() + "*" + song.getName());
        }

        return vi;
    }

    private String dateFormat(Date past, Date current, Date last) {
        //  Log.d("here Date current ", "  past " + past + " " + current.toString());


        //int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
        int diffInDays = (int) ((current.getTime() - past.getTime()) / (1000 * 60 * 60 * 24));
        // System.out.println("Difference in Days : " + diffInDays);
        if (diffInDays == 1) {
            return "Yesterday";
        } else if (diffInDays == 0) {
            return "" + formatResult.format(last);
        } else {
            return "" + formatDate.format(last);
        }
        //return "";
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<FavChat> filterlist = new ArrayList<>();

                if (memberData == null) {
                    memberData = new ArrayList<>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getMsg();
                        Log.d("data", data);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FavChat modal = new FavChat();


                            modal.setrId(memberData.get(i).getrId());
                            modal.setMsg(memberData.get(i).getMsg());
                            modal.setId(memberData.get(i).getId());
                            modal.setName(memberData.get(i).getName());
                            modal.setSubject(memberData.get(i).getSubject());
                            modal.setDate(memberData.get(i).getDate());
                            modal.setUser_id(memberData.get(i).getUser_id());
                            modal.setUrl(memberData.get(i).getUrl());
                            modal.setIndicator(0);

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

                data = (ArrayList<FavChat>) results.values;
                if (memberData.size() != 0)
                    notifyDataSetChanged();
            }
        }

                ;

    }
}