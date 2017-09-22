package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
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
import wydr.sellers.acc.ListLoader;
import wydr.sellers.modal.Member;

/**
 * Created by surya on 9/12/15.
 */
public class MemberAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    ListLoader loader;
    private Activity activity;
    private ArrayList<Member> data;
    private ArrayList<Member> memberData;


    public MemberAdapter(Activity a, ArrayList<Member> d) {
        activity = a;
        data = d;
        memberData = d;
        loader = new ListLoader(activity.getApplicationContext());
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        if (data == null) {
            return 0;
        }
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
            vi = inflater.inflate(R.layout.member_layout, null);

        TextView name = (TextView) vi.findViewById(R.id.textViewMemberName);
        ImageView close = (ImageView) vi.findViewById(R.id.imageViewDeleteMember);
        ImageView thumb = (ImageView) vi.findViewById(R.id.imageViewMember);


        Member song = data.get(position);
        name.setText(song.getName());
        close.setTag(song);
        loader.DisplayImage(song.getImg(), thumb, R.drawable.avtar);
        //address.setText(song.getAddress());

        return vi;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<Member> filterlist = new ArrayList<Member>();

                if (memberData == null) {
                    memberData = new ArrayList<Member>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++) {
                        String data = memberData.get(i).getName();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            Member modal = new Member();
                            modal.setName(memberData.get(i).getName());
                            modal.setJid(memberData.get(i).getJid());
                            modal.setName(memberData.get(i).getName());
                            modal.setUser_id(memberData.get(i).getUser_id());
                            modal.setImg(memberData.get(i).getImg());
                            //modal.setUrl(memberData.get(i).getUrl());
                            filterlist.add(modal);
                        }
                    }
                    results.values = filterlist;
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                data = (ArrayList<Member>) results.values;
                notifyDataSetChanged();
            }
        }

                ;

    }

}
