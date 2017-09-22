package wydr.sellers.adapter;


/**
 * Created by Deepesh_pc on 06-08-2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wydr.sellers.R;
import wydr.sellers.modal.CategoryItem;
import wydr.sellers.modal.GridChild;
import wydr.sellers.modal.GridParent;

import static wydr.sellers.R.id;
import static wydr.sellers.R.layout;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    public static TextView tv_grand_title;
    public static int counter = 0;
    public static int categorycounter;
    public ToggleButton tb;
    public List<CategoryItem> CategoryValues = new ArrayList<>();
    public boolean[][] mStates;
    CheckBox cb;
    TextView catTotal;
    // child data in format of header title, child title
    //  private HashMap<String, List<String[]>> _listDataChild;
    ExpandableListView exp;
    boolean allSelected = false;
    private Context _context;
    private List<GridParent> listTitles; // header titles
    private ArrayList<Map<String, ArrayList<Map<String, GridChild>>>> categories;

    public ExpandableListAdapter(Context context, ArrayList<Map<String, ArrayList<Map<String, GridChild>>>> data, List<GridParent> titles, ExpandableListView expListView, TextView catcounter, Integer counter) {
//        super(context, resource);
        this._context = context;
        this.listTitles = titles;
        this.categories = data;
        this.exp = expListView;
        this.catTotal = catcounter;
        categorycounter = counter;
        catTotal.setText(categorycounter + " CATEGORIES SELECTED");
    }

    public void onclick(View v) {
        //Log.e("ee", "eee" + v.toString());

    }

    public void refresh(List<GridParent> listTitles) {
        this.listTitles = listTitles;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
      /*  //Log.e("", "getGroupCount: " + this.listTitles.size());*/
        return this.listTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
      /*  //Log.e("", "getChildrenCount" + categories.get(groupPosition).get("grand_name").size());*/
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
      /*  //Log.e("getGroup", "getGroup" + groupPosition);
        //Log.e("getGroup", "getGroup" + this.categories.get(groupPosition).get("grand_name"));*/
        return this.categories.get(groupPosition).get("grand_name");
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
       /* //Log.e("child format", "getChild" + categories.get(groupPosition).get("grand_name"));
        //Log.e("child format", "groupPosition" + groupPosition + "childPosition" + childPosition);
        //ArrayList is = (ArrayList) getGroup(groupPosition);*/
        return this.categories.get(groupPosition).get("grand_name");
    }

    @Override
    public long getGroupId(int groupPosition) {
        //Log.e("", "getGroupId" + groupPosition);
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //Log.e("", "getChildId" + childPosition);
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.i("", "grand_title" + "group view" + groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.list_group, null);


        tv_grand_title = (TextView) convertView.findViewById(R.id.lblListHeader);
       // Log.i("check", listTitles.get(groupPosition).getCounter() + "/" + listTitles.get(groupPosition).getTitle());
        if (listTitles.get(groupPosition).getCounter() > 0)
            tv_grand_title.setText(listTitles.get(groupPosition).getTitle().toUpperCase() + " (" + listTitles.get(groupPosition).getCounter() + ")");
        else
            tv_grand_title.setText(listTitles.get(groupPosition).getTitle().toUpperCase());
        LinearLayout bevGroup = (LinearLayout) convertView.findViewById(R.id.listgroup);
        LinearLayout bevGroup2 = (LinearLayout) convertView.findViewById(id.listsubgroup);
        if (isExpanded) {

            bevGroup.setBackgroundColor(Color.parseColor("#f3f1f2"));


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tv_grand_title.getLayoutParams();
            tv_grand_title.setPadding(12, 12, 12, 0);
            tv_grand_title.setLayoutParams(layoutParams);
            bevGroup2.setBackgroundColor(Color.parseColor("#f3f1f2"));
            tv_grand_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_up_arrow, 0);

        }
        Log.i("check-kalpit", tv_grand_title.getText().toString());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Log.e("", "grand_title" + "child view");

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(layout.list_item, null);
        }

        //Log.e("Here", categories.get(groupPosition).get("grand_name").toString());

        final GridView gridView = (GridView) convertView.findViewById(id.grid);
        ArrayList list = (ArrayList) getChild(groupPosition, childPosition);
        Log.d("here", list.toString());
        int count = list.size() % 2;
        int c = list.size() / 2;
        //Log.e("cccc", "" + count);
        //Log.e("c", "" + c);
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        int variableheight = 0;
        /// AbsListView.LayoutParams parm=gridView.getLayoutParams();

        if (c > 0) {
            int xyz = count * 39;
            if (xyz > 0) {
                variableheight = (39) * c + xyz;
            } else {
                variableheight = 39 * c;
            }

        } else {
            variableheight = 39;
        }
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, variableheight, _context.getResources().getDisplayMetrics());
        layoutParams.height = height + 20;


        gridView.setLayoutParams(layoutParams);
        cb = (CheckBox) convertView.findViewById(R.id.gridchk);
        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(this._context, id.grid, list, groupPosition, cb);
        gridView.setAdapter(categoryGridAdapter);

        if (listTitles.get(groupPosition).getCounter() == list.size()) {

            listTitles.get(groupPosition).setIsSelected(true);
        } else {
            listTitles.get(groupPosition).setIsSelected(false);

        }

        cb.setOnClickListener(new View.OnClickListener() {
            ArrayList<Map<String, GridChild>> list = (ArrayList) categories.get(groupPosition).get("grand_name");

            @Override
            public void onClick(View v) {
                if (!allSelected) {

                    Log.i("", "checked");
                    int count = list.size();
                    listTitles.get(groupPosition).setIsSelected(true);
                    listTitles.get(groupPosition).setCounter(list.size());
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(k).get("name").isSelected())
                            count--;
                    }

                    categorycounter = categorycounter + count;
                    allSelected = true;
                    notifyDataSetChanged();
                } else {
                    Log.i("", "not checked");
                    int count = 0;
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(k).get("name").isSelected())
                            count++;
                    }
                    categorycounter = categorycounter - count;
                    listTitles.get(groupPosition).setCounter(0);
                    listTitles.get(groupPosition).setIsSelected(false);
                    allSelected = false;
                    notifyDataSetChanged();
                }
                for (int k = 0; k < list.size(); k++) {

                    list.get(k).get("name").setIsSelected(listTitles.get(groupPosition).isSelected());

                }
                notifyDataSetChanged();
                catTotal.setText(categorycounter + " CATEGORIES SELECTED");
                cb.setOnCheckedChangeListener(null);
            }
        });


        cb.setChecked(listTitles.get(groupPosition).isSelected());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<CategoryItem> Returnval() {

        for (int i = 0; i < listTitles.size(); i++) {
            ArrayList<Map<String, GridChild>> list = (ArrayList) categories.get(i).get("grand_name");
            for (int k = 0; k < list.size(); k++) {
                // Log.e("cateogries--",categories.get(i).toString());
                // Log.e("list--",list.toString());
                if (list.get(k).get("name").isSelected()) {
                    CategoryItem item = new CategoryItem();
                    item.setID(list.get(k).get("name").getId());
                    item.setName(list.get(k).get("name").getName());
                    item.setParentid(list.get(k).get("name").getParentid());
                    Log.e("PArentid", list.get(k).get("name").getParentid());
                    Log.e("Childid", list.get(k).get("name").getId());
                    CategoryValues.add(item);
                }
            }


        }
        return CategoryValues;
    }

    private class CategoryGridAdapter extends ArrayAdapter<Map<String, GridChild>> {
        public Context con;
        private List<Map<String, GridChild>> categories;
        private int grouposition;
        private CheckBox cbox;

        public CategoryGridAdapter(Context context, int resource, List<Map<String, GridChild>> objects, int grouposition, CheckBox chkbx) {
            super(context, resource, objects);
            this.categories = objects;
            this.con = context;
            this.grouposition = grouposition;
            this.cbox = chkbx;
        }


        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            //Log.e("", "position: " + position + "category: " + this.categories.get(position).get("name"));

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layout.category_grid_item, null);


            tb = (ToggleButton) convertView.findViewById(id.griditem);
            //Log.e("togle", this.categories.get(position).get("name").getId() + "/" + this.categories.get(position).get("name").getName());
            tb.setId(Integer.parseInt(this.categories.get(position).get("name").getId()));
            tb.setTextOff(this.categories.get(position).get("name").getName().toUpperCase());
            tb.setTextOn(this.categories.get(position).get("name").getName().toUpperCase());
            tb.setText(this.categories.get(position).get("name").getName().toUpperCase());


            tb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (categories.get(position).get("name").isSelected()) {
                        categories.get(position).get("name").setIsSelected(false);
                        notifyDataSetChanged();
                        listTitles.get(grouposition).setCounter(listTitles.get(grouposition).getCounter() - 1);
                        notifyDataSetChanged();
                        //Log.e("isChecked", categories.size() +"yeah");notifyDataSetChanged();
                        categorycounter = categorycounter - 1;
//                        if(CategoryGridAdapter.this.cbox.isChecked())
                        CategoryGridAdapter.this.cbox.setChecked(false);
                        //tv_grand_title.setText(listTitles.get(grouposition).getTitle().toUpperCase() + " (" + listTitles.get(grouposition).getCounter() + ")");

                    } else {
                        //Log.e("isChecked", "yoooo");
                        categorycounter = categorycounter + 1;
                        categories.get(position).get("name").setIsSelected(true);
                        listTitles.get(grouposition).setCounter(listTitles.get(grouposition).getCounter() + 1);
                        notifyDataSetChanged();
                        notifyDataSetChanged();
                        if (listTitles.get(grouposition).getCounter() == categories.size()) {
                            CategoryGridAdapter.this.cbox.setChecked(true);
                        }
                        //tv_grand_title.setText(listTitles.get(grouposition).getTitle().toUpperCase() + " (" + listTitles.get(grouposition).getCounter() + ")");


                    }

//                    tv_grand_title.setText(listTitles.get(grouposition).getTitle().toUpperCase() + " (" + listTitles.get(grouposition).getCounter() + ")");
//                    Log.i("check", listTitles.get(grouposition).getCounter() + "/"+listTitles.get(grouposition).getTitle());
                    notifyDataSetChanged();
                    refresh(listTitles);
                    tv_grand_title.setText(listTitles.get(grouposition).getTitle().toUpperCase() + " (" + listTitles.get(grouposition).getCounter() + ")");
                    Log.i("check", tv_grand_title.getText().toString());
                    //Log.e("counterrrrr", listTitles.get(grouposition).getCounter() + "");
                    catTotal.setText(categorycounter + " CATEGORIES SELECTED");
                    for (Map<String, GridChild> map : categories) {
                        allSelected = true;
                        GridChild gc = map.get("name");
                        if (!gc.isSelected()) {
                            allSelected = false;
                            break;
                        }
                    }
                    cbox.setChecked(allSelected);
                }
            });

            if (categories.get(position).get("name").isSelected()) {
                tb.setBackgroundColor(0xff42a2da);
                tb.setTextColor(0xffffffff);
            } else {
                tb.setTextColor(0xff3c3c3c);
                tb.setBackgroundColor(0xffffffff);
            }
            return convertView;
        }
    }
}

