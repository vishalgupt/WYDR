package wydr.sellers.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import wydr.sellers.R;
import wydr.sellers.acc.CustExpListview;
import wydr.sellers.acc.ObjectItems;
import wydr.sellers.acc.SecondLevelAdapter;

/**
 * Created by surya on 15/9/15.
 */
public class RootAdapterExp extends BaseExpandableListAdapter {
    private final LayoutInflater inflater;
    public Entry[] lsfirst;
    private ObjectItems root;

    // you can change the constructor depending on which listeners you wan't to use.
    public RootAdapterExp(Context context, ObjectItems root, ExpandableListView.OnGroupClickListener grpLst,
                          ExpandableListView.OnChildClickListener childLst, ExpandableListView.OnGroupExpandListener grpExpLst) {
        this.root = root;
        this.inflater = LayoutInflater.from(context);

        lsfirst = new Entry[root.children.size()];

        for (int i = 0; i < root.children.size(); i++) {
            final CustExpListview celv = new CustExpListview(context);
            Log.i("ROOT@", root.children.size() + " /" + root.children.get(i).title + " /" + root.children.get(i).level);
            if(root.children.get(i).level==1)
            {
                Log.i("ROOT@", " root.children.get(i).level 1");
                ChildAdapter adp = new ChildAdapter(context,root.children.get(i));
                celv.setAdapter(adp);
                celv.setGroupIndicator(null);
                celv.setOnChildClickListener(childLst);
                celv.setOnGroupClickListener(grpLst);
                celv.setOnGroupExpandListener(grpExpLst);
                lsfirst[i] = new Entry(celv, adp,0,0);
            }
            if(root.children.get(i).level==3)
            {
                Log.i("ROOT@", " root.children.get(i).level 3");
                ParentRootAdapter adp = new ParentRootAdapter(context,root.children.get(i), grpLst,childLst,grpExpLst);
                celv.setAdapter(adp);
                celv.setGroupIndicator(null);
                celv.setOnChildClickListener(childLst);
                celv.setOnGroupClickListener(grpLst);
                celv.setOnGroupExpandListener(grpExpLst);
                lsfirst[i] = new Entry(celv, adp,0);
            }
            else
            {
                Log.i("ROOT@", "root.children.get(i).level 2");
                SecondLevelAdapter adp = new SecondLevelAdapter(root.children.get(i), context);
                celv.setAdapter(adp);
                celv.setGroupIndicator(null);
                celv.setOnChildClickListener(childLst);
                celv.setOnGroupClickListener(grpLst);
                celv.setOnGroupExpandListener(grpExpLst);
                lsfirst[i] = new Entry(celv, adp);
            }



        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return root.children;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        // second level list
        return lsfirst[groupPosition].cls;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public ObjectItems getGroup(int groupPosition) {
        return root.children.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return root.children.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        // first level
        View layout = convertView;
        GroupViewHolder holder;
        final ObjectItems item = getGroup(groupPosition);

        if (layout == null) {
            layout = inflater.inflate(R.layout.item_root, parent, false);
        }
        holder = new GroupViewHolder();
        holder.title = (TextView) layout.findViewById(R.id.itemRootTitle);
        if (isExpanded) {
            holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
        } else {
            holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
        }
        layout.setTag(holder);

        holder.title.setText(item.title.trim());

        return layout;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class GroupViewHolder {
        TextView title;
    }

    public class Entry {
        public final CustExpListview cls;
        public  ParentRootAdapter radpt;
        public  SecondLevelAdapter sadpt;
        public  ChildAdapter cadpt;

        public Entry(CustExpListview cls, ParentRootAdapter sadpt,int a) {
            this.cls = cls;
            this.radpt = sadpt;
        }
        public Entry(CustExpListview cls, SecondLevelAdapter sadpt) {
            this.cls = cls;
            this.sadpt = sadpt;
        }

        public Entry(CustExpListview cls, ChildAdapter adp, int i, int i1) {
            this.cls = cls;
            this.cadpt = adp;
        }
    }
}