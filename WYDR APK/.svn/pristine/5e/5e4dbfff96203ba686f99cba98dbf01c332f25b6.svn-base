package wydr.sellers.acc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import wydr.sellers.R;

/**
 * Created by surya on 15/9/15.
 */
public class RootAdapter extends BaseExpandableListAdapter
{
    private final LayoutInflater inflater;
    public Entry[] lsfirst;
    private ObjectItems root;

    // you can change the constructor depending on which listeners you wan't to use.
    public RootAdapter(Context context, ObjectItems root, ExpandableListView.OnGroupClickListener grpLst,
                       ExpandableListView.OnChildClickListener childLst, ExpandableListView.OnGroupExpandListener grpExpLst) {
        this.root = root;
        this.inflater = LayoutInflater.from(context);

        lsfirst = new Entry[root.children.size()];

        for (int i = 0; i < root.children.size(); i++) {
            Log.i("ROOT@ 1", root.children.size() + " /" + root.children.get(i).title + " /" + root.children.get(i).level);
            final CustExpListview celv = new CustExpListview(context);
            SecondLevelAdapter adp = new SecondLevelAdapter(root.children.get(i), context);
            celv.setAdapter(adp);
            celv.setGroupIndicator(null);
            celv.setOnChildClickListener(childLst);
            celv.setOnGroupClickListener(grpLst);
            celv.setOnGroupExpandListener(grpExpLst);
            lsfirst[i] = new Entry(celv, adp);
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
        public final SecondLevelAdapter sadpt;

        public Entry(CustExpListview cls, SecondLevelAdapter sadpt) {
            this.cls = cls;
            this.sadpt = sadpt;
        }
    }
}