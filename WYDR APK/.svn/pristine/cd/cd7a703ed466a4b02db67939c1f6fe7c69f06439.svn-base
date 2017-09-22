package wydr.sellers.acc;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import wydr.sellers.R;

/**
 * Created by surya on 15/9/15.
 */
public class SecondLevelAdapter extends BaseExpandableListAdapter {
    public ObjectItems child;
    LayoutInflater inflater;
    Context mContext;

    public SecondLevelAdapter(ObjectItems child, Context context) {
        this.child = child;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ObjectItems getChild(int groupPosition, int childPosition) {
        return child.children.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // third level
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        View layout = convertView;
        final ObjectItems item = getChild(groupPosition, childPosition);

        ChildViewHolder holder;

        if (layout == null) {
            layout = inflater.inflate(R.layout.items_child, parent, false);

            holder = new ChildViewHolder();
            holder.title = (TextView) layout.findViewById(R.id.itemChildTitle);
            layout.setTag(holder);
        } else {
            holder = (ChildViewHolder) layout.getTag();
        }

        holder.title.setText(item.title.trim());

        return layout;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child.children.get(groupPosition).children.size();
    }

    @Override
    public ObjectItems getGroup(int groupPosition) {
        return child.children.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (child != null)
            return child.children.size();
        else
            return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // Second level
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        View layout = convertView;
        ViewHolder holder;

        final ObjectItems item = getGroup(groupPosition);

        if (layout == null) {
            layout = inflater.inflate(R.layout.item_parent, parent, false);
        }
        holder = new ViewHolder();
        holder.title = (TextView) layout.findViewById(R.id.itemParentTitle);
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
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        Log.d("SecondLevelAdapter", "Unregistering observer");
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        TextView title;
    }

    private static class ChildViewHolder {
        TextView title;
    }

}