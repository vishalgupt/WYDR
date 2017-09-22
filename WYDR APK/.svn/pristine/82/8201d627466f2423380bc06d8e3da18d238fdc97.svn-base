package wydr.sellers.adapter;

/**
 * Created by Deepesh_pc on 11-01-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import wydr.sellers.R;
import wydr.sellers.acc.ObjectItems;

public class ChildAdapter extends BaseExpandableListAdapter {

    private final ObjectItems groups;
    public LayoutInflater inflater;
    public Context activity;

    public ChildAdapter(Context act, ObjectItems groups) {
        activity = act;
        this.groups = groups;
        inflater =(LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.children.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.items_child, null);
        }
        text = (TextView) convertView.findViewById(R.id.itemChildTitle);
        text.setText(children);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, children,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.children.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.count;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_parent, null);
        }
        final ObjectItems item = (ObjectItems) getGroup(groupPosition);
        TextView title = (TextView) convertView.findViewById(R.id.itemParentTitle);
        if (isExpanded) {
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black_24dp, 0);
        } else {
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
        }
        convertView.setTag(title);


       title.setText(item.title.trim());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}