package wydr.sellers.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import wydr.sellers.R;
import wydr.sellers.acc.RangeSeekBar;
import wydr.sellers.adapter.SellerFilterAdapter;
import wydr.sellers.modal.Seller;

/**
 * Created by surya on 15/9/15.
 */
public class MyCatalogSellerFilter extends Fragment {
    ArrayList<Seller> list;
    ListView listView;
    LinearLayout layoutRange;
    SellerFilterAdapter adapter;
    TextView minView, maxView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seller_filter, container, false);
        layoutRange = (LinearLayout) view.findViewById(R.id.layoutRange);
        list = new ArrayList<>();
        iniStuff(view);

        return view;

    }

    private void iniStuff(View view) {
        listView = (ListView) view.findViewById(R.id.listViewSellerFilter);
        minView = (TextView) view.findViewById(R.id.textMin);
        maxView = (TextView) view.findViewById(R.id.textMax);
        Bundle bundle = getArguments();
        try {

            JSONObject object = new JSONObject(bundle.getString("items"));
            //Log.d("result filter", "items there " + object.toString());
            if (object.has("slider")) {
                Long min = object.getLong("min");
                Long max = object.getLong("max");

                MyCatalogFilter.p_id = object.getInt("parent_id");
                RangeSeekBar<Long> seekBar = new RangeSeekBar<Long>(min, max, getActivity().getApplicationContext());
                minView.setText("" + seekBar.getSelectedMinValue());
                maxView.setText("" + seekBar.getSelectedMaxValue());
                if (object.has("selected_range")) {
                    seekBar.setSelectedMaxValue(object.getLong("right"));
                    seekBar.setSelectedMinValue(object.getLong("left"));
                    minView.setText("" + seekBar.getSelectedMinValue());
                    maxView.setText("" + seekBar.getSelectedMaxValue());
                }

                seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
                        // handle changed range values

                        Log.i("TAG", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
                        minView.setText("" + minValue);
                        maxView.setText("" + maxValue);
                        MyCatalogFilter.isRange = true;
                        MyCatalogFilter.max = maxValue;
                        MyCatalogFilter.min = minValue;
                    }
                });
                seekBar.setNotifyWhileDragging(true);

                layoutRange.addView(seekBar);
                //  listView.setVisibility(View.GONE);
            }
            if (object.has("selected")) {
                String parant_id = object.getString("parent_id");
                JSONObject selected = object.getJSONObject("selected");
                for (Iterator<String> iter2 = selected.keys(); iter2.hasNext(); ) {
                    String key2 = iter2.next();
                    JSONObject objectitems = selected.getJSONObject(key2);
                    // list.add(objectitems.getString("variant"));
                    Seller seller = new Seller();
                    seller.setName(objectitems.getString("variant"));
                    seller.setId(objectitems.getString("variant_id"));
                    seller.setIsSelected(true);
                    seller.setParent(parant_id);
                    list.add(seller);

                }
                if (!object.has("slider")) {
                    maxView.setVisibility(View.GONE);
                    minView.setVisibility(View.GONE);
                }
            }
            if (object.has("all")) {
                if (object.getJSONObject("all").length() > 0) {
                    String parant_id = object.getString("parent_id");
                    JSONObject all = object.getJSONObject("all");
                    for (Iterator<String> iter2 = all.keys(); iter2.hasNext(); ) {
                        String key2 = iter2.next();
                        JSONObject objectitems = all.getJSONObject(key2);
                        // list.add(objectitems.getString("variant"));
                        Seller seller = new Seller();

                        seller.setName(objectitems.getString("variant"));
                        seller.setId(objectitems.getString("variant_id"));
                        if (objectitems.has("disabled")) {
                            seller.setDisable(objectitems.getBoolean("disabled"));
                        } else {
                            seller.setDisable(false);
                        }
                        seller.setParent(parant_id);
                        seller.setIsSelected(false);
                        list.add(seller);
                    }
                }
                if (!object.has("slider")) {
                    maxView.setVisibility(View.GONE);
                    minView.setVisibility(View.GONE);
                }
            }
            // Log.i("variant", "Here");
            if (object.has("allvariants")) {
                if (object.getJSONObject("allvariants").length() > 0) {
                    // Log.i("HERE2 - ", "allvariants-" + object.getJSONObject("allvariants").toString());
                    String parant_id = object.getString("parent_id");
                    JSONObject all = object.getJSONObject("allvariants");
                    JSONArray jsonArray = all.getJSONArray("variant_val");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(a);
                        //Log.i("HERE2 - ", jsonObject.getString("name") + "/" + jsonObject.getString("id"));
                        if (object.has("selected_value")) {
                            //Log.i("HERE2 - ", "object.getString(\"selected_value\") --" + object.getString("selected_value"));
                            if (!object.getString("selected_value").equalsIgnoreCase("")) {
                                String[] selected_vals = object.getString("selected_value").split(",");
                                if (Arrays.asList(selected_vals).contains(jsonObject.getString("name"))) {
                                    Seller seller = new Seller();
                                    seller.setName(jsonObject.getString("name"));
                                    seller.setId(jsonObject.getString("id"));
                                    // Log.i("variant", jsonObject.getString("name") + "/" + jsonObject.getString("id"));
                                    seller.setParent(parant_id);
                                    seller.setIsSelected(true);
                                    list.add(seller);
                                } else {
                                    Seller seller = new Seller();
                                    seller.setName(jsonObject.getString("name"));
                                    seller.setId(jsonObject.getString("id"));
                                    //  Log.i("variant", jsonObject.getString("name") + "/" + jsonObject.getString("id"));
                                    seller.setParent(parant_id);
                                    seller.setIsSelected(false);
                                    list.add(seller);
                                }
                            }
                        } else {
                            Seller seller = new Seller();
                            seller.setName(jsonObject.getString("name"));
                            seller.setId(jsonObject.getString("id"));
                            //  Log.i("HERE2 variant", jsonObject.getString("name") + "/" + jsonObject.getString("id"));
                            seller.setParent(parant_id);
                            seller.setIsSelected(false);
                            list.add(seller);
                        }

                    }

                }
                if (!object.has("myslider")) {
                    maxView.setVisibility(View.GONE);
                    minView.setVisibility(View.GONE);
                }


            }
            if (object.has("myslider")) {
                Long min = object.getLong("min");
                Long max = object.getLong("max");

                MyCatalogFilter.p_id = object.getInt("parent_id");
                RangeSeekBar<Long> seekBar = new RangeSeekBar<Long>(min, max, getActivity().getApplicationContext());
                minView.setText("" + seekBar.getSelectedMinValue());
                maxView.setText("" + seekBar.getSelectedMaxValue());
                if (object.has("selected_range")) {
                    seekBar.setSelectedMaxValue(object.getLong("right"));
                    seekBar.setSelectedMinValue(object.getLong("left"));
                    minView.setText("" + seekBar.getSelectedMinValue());
                    maxView.setText("" + seekBar.getSelectedMaxValue());
                }
                seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
                        // handle changed range values

                        // Log.i("TAG", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
                        minView.setText("" + minValue);
                        maxView.setText("" + maxValue);
                        MyCatalogFilter.isRange = true;
                        MyCatalogFilter.max = maxValue;
                        MyCatalogFilter.min = minValue;
                    }
                });
                seekBar.setNotifyWhileDragging(true);

                layoutRange.addView(seekBar);
                //  listView.setVisibility(View.GONE);
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
//}
}
