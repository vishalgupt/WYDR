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
 * Created by surya on 2/10/15.
 */
public class DateList extends Fragment {
    ListView listView;
    ArrayList<HashMap<String, String>> list;
    FilterMenuAdapter adapter;
    JSONObject jsonObject;
    ArrayList<HashMap<String, String>> filterAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        HashMap<String, String> map = new HashMap<>();
        map.put("id", "date");
        map.put("title", "Needed By");
        list.add(map);

        adapter = new FilterMenuAdapter(getActivity(), list);
        listView.setAdapter(adapter);

    }
}
