package wydr.sellers.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import wydr.sellers.R;
import wydr.sellers.adapter.SellerFilterAdapter;
import wydr.sellers.modal.Seller;

/**
 * Created by surya on 15/9/15.
 */
public class LocationFilter extends Fragment {
    ArrayList<Seller> list;
    ListView listView;
    EditText editsearch;
    //LinearLayout layoutRange;
    SellerFilterAdapter adapter;
    //  TextView minView, maxView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_filter, container, false);
        // layoutRange = (LinearLayout) view.findViewById(R.id.layoutRange);
        list = new ArrayList<>();
        iniStuff(view);

        return view;

    }

    private void iniStuff(View view) {
        listView = (ListView) view.findViewById(R.id.listViewLocationFilter);
//        minView = (TextView) view.findViewById(R.id.textMin);
//        maxView = (TextView) view.findViewById(R.id.textMax);
        editsearch = (EditText) view.findViewById(R.id.editTextLocSearch);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                android.widget.Filter f = adapter.getFilter();
                f.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        Bundle bundle = getArguments();
        try {

            JSONObject object = new JSONObject(bundle.getString("items"));
            Log.d("result filter", "items there " + object.toString());

            if (object.has("selected")) {
                //  String parant_id = object.getString("parent_id");
                JSONArray selected = object.getJSONArray("selected");
                for (int i = 0; i < selected.length(); i++) {

                    Seller seller = new Seller();
                    seller.setName(selected.getString(i));
                    // seller.setId(objectitems.getString("variant_id"));
                    seller.setIsSelected(true);
                    //seller.setParent(parant_id);
                    list.add(seller);

                }

            }
            if (object.has("all")) {
                if (object.getJSONArray("all").length() > 0) {
                    //String parant_id = object.getString("parent_id");
                    JSONArray all = object.getJSONArray("all");
                    for (int i = 0; i < all.length(); i++) {
                        //  String key2 = iter2.next();
                        //  JSONObject objectitems = all.getJSONObject(key2);
                        // list.add(objectitems.getString("variant"));
                        Seller seller = new Seller();

                        seller.setName(all.getString(i));
                        //  seller.setId(objectitems.getString("variant_id"));
//                        if (objectitems.has("disabled")) {
//                            seller.setDisable(objectitems.getBoolean("disabled"));
//                        } else {
                        seller.setDisable(false);
                        //  }

                        //  seller.setParent(parant_id);
                        seller.setIsSelected(false);
                        list.add(seller);
                    }
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        list = activity.getParamList();
//        if(list!=null) {
//            for (int i = 0; i < 7; i++) {
//                Seller seller = new Seller();
//                seller.setName(list.get(i).toString());
//                list.add(seller);
//            }
        adapter = new SellerFilterAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }
}
