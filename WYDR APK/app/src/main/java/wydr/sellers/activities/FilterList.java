package wydr.sellers.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import wydr.sellers.R;
import wydr.sellers.adapter.FilterMenuAdapter;

/**
 * Created by surya on 25/8/15.
 */
public class FilterList extends Fragment {
    ListView listView;
    ArrayList<HashMap<String, String>> list;
    FilterMenuAdapter adapter;
    JSONObject jsonObject;
    ArrayList<HashMap<String, String>> filterAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        try {
//            filterAll = new ArrayList<>();
//            jsonObject = new JSONObject(getActivity().getIntent().getStringExtra("filter"));
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_menu, container, false);
        iniStuff(view);
        return view;
    }

    private void iniStuff(View view) {
        listView = (ListView) view.findViewById(R.id.listViewFilterMenu);
        list = new ArrayList<>();
        // try {

        // list.add(new NavDrawerItem("Category", R.drawable.category, 0));
        //int i = 0;
//            JSONObject objectCurrent = jsonObject.getJSONObject("current");
//            for (Iterator<String> iter = objectCurrent.keys(); iter.hasNext(); ) {
        //    String key = iter.next();
        //  JSONObject object = objectCurrent.getJSONObject(key);
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "location");
        map.put("title", "Location");
        list.add(map);
        // list.add(new NavDrawerItem(, R.drawable.money, i));
//
        //     i++;

//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        list.add(new NavDrawerItem("Price Range", R.drawable.money, 1));
//        list.add(new NavDrawerItem("Min Qty", R.drawable.bag, 2));
//        list.add(new NavDrawerItem("Location", R.drawable.location, 3));
//        list.add(new NavDrawerItem("Seller", R.drawable.allsellers, 4));
        adapter = new FilterMenuAdapter(getActivity(), list);
        listView.setAdapter(adapter);

    }

}
