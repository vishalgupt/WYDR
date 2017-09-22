package wydr.sellers.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wydr.sellers.R;
import wydr.sellers.modal.Seller;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;

/**
 * Created by surya on 15/9/15.
 */
public class MyCatalogFilter extends AppCompatActivity implements View.OnClickListener {
    static int p_id;
    static long min, max;
    static boolean isRange = false;
    static int pos = 0;
    ListView listView;
    JSONObject jsonArray;
    ArrayList<String> list;
    ArrayList<ArrayList> topList;
    String paramList;
    String selectedID;
    ArrayList<String> filterItem;
    ListView listView2;
    JSONObject lastFilter, filter;
    Button cancel, apply;
    JSONArray array;
    ConnectionDetector cd;
    JSONObject jsonObject;
    JSONParser jsonParser;

    int code;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        cd = new ConnectionDetector(MyCatalogFilter.this);
        progressStuff();
        code = getIntent().getIntExtra("request_code", 105);
        apply = (Button) findViewById(R.id.buttonApply);
        cancel = (Button) findViewById(R.id.buttonCancel);
        apply.setOnClickListener(this);
        cancel.setOnClickListener(this);
        lastFilter = new JSONObject();
        listView = (ListView) findViewById(R.id.listViewFilterMenu);
        filter = new JSONObject();
        try {
            jsonArray = new JSONObject(getIntent().getStringExtra("filter"));
            JSONObject objectCurrent = jsonArray.getJSONObject("current");
            Log.i("result filter", objectCurrent.toString());
            //  int i = 0;
            list = new ArrayList<>();
            for (Iterator<String> iter = objectCurrent.keys(); iter.hasNext(); ) {
                Log.i("HERE - ", "itr key-" + iter.toString());
                String key = iter.next();

                JSONObject object = objectCurrent.getJSONObject(key);
                JSONObject bulidObject = new JSONObject();
                if (object.has("myslider")) {
                    bulidObject.put("filter", "Price");
                    bulidObject.put("myslider", object.getBoolean("myslider"));
                    bulidObject.put("parent_id", key.toString());
                    bulidObject.put("min", object.getLong("min"));
                    bulidObject.put("max", object.getLong("max"));
                    if (object.getBoolean("selected_range")) {
                        bulidObject.put("selected_range", object.getBoolean("selected_range"));
                        bulidObject.put("left", object.getLong("left"));
                        bulidObject.put("right", object.getLong("right"));
                    }

                }


                if (object.has("variants") && object.get("variants") instanceof JSONObject) {


                    if (object.getBoolean("is_selected")) {
                        if (!object.getString("selected_value").equalsIgnoreCase("")) {
                            Log.i("HERE - ", "selected_value-" + object.getString("selected_value"));
                            bulidObject.put("selected_value", object.getString("selected_value"));
                            String[] selected_bools = object.getString("selected_value").split(",");
                            array = new JSONArray();
                            for (int i = 0; i < selected_bools.length; i++) {
                                array.put(selected_bools[i].replace("'", ""));

                                Log.i("selected_bools-", selected_bools[i].toString());
                            }
                            if (array != null) {
                                lastFilter.put(key.toString(), array);
                                Log.i("result filter", " Added " + array.toString() + " key " + key);
                            }


                        }
                    }

                    bulidObject.put("allvariants", object.getJSONObject("variants"));
                    bulidObject.put("parent_id", key.toString());

                }
                Log.i("buildobject", bulidObject.toString());
                list.add(bulidObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        displayView(0);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                pos = position;
                                                Log.i("result filter", "filter " + filter + " last filter " + lastFilter);
                                                //  filter {"11":["125"]} last filter {"10":["92","88"]}
                                                JSONObject result;
                                                result = filter;
                                                Iterator it = filter.keys();
                                                while (it.hasNext()) {
                                                    String key = (String) it.next();
                                                    if (lastFilter.has(key)) {

                                                        try {
                                                            JSONArray filterJSONArray = filter.getJSONArray(key);
                                                            JSONArray lastJSONArray = lastFilter.getJSONArray(key);
                                                            JSONArray resultArray = concatArray(filterJSONArray, lastJSONArray);
                                                            result.put(key, resultArray);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                }
                                                JSONObject merged = new JSONObject();
                                                try {
                                                    JSONObject[] objs = new JSONObject[]{lastFilter, result};
                                                    for (JSONObject obj : objs) {
                                                        Iterator itr = obj.keys();
                                                        while (itr.hasNext()) {
                                                            String key = (String) itr.next();
                                                            merged.put(key, obj.get(key));

                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                if (isRange) {
                                                    JSONArray array = new JSONArray();
                                                    array.put("" + min);
                                                    array.put("" + max);
                                                    try {
                                                        merged.put("" + p_id, array);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                displayView(pos);
                                                //      new GetProductsFilter().execute(cat_id, merged.toString());

                                            }


                                        }


        );

    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        /*cd = new ConnectionDetector(MyCatalogFilter.this);*/
        jsonParser = new JSONParser();
        progress = new ProgressDialog(MyCatalogFilter.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    public void selectFilter(View v) {
        Seller id = (Seller) v.getTag();
        Log.i("result filter", "Parent id " + id.getParent());
        //is chkIos checked?
        if (((CheckBox) v).isChecked()) {
            //Case 1
            array.put("" + id.getName());
        } else {
            ArrayList<String> list = new ArrayList<String>();
            JSONArray jsonArray = array;
            int len = jsonArray.length();
            if (jsonArray != null) {
                for (int i = 0; i < len; i++) {
                    try {
                        list.add(jsonArray.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
//Remove the element from arraylist
            list.remove(id.getName());
//Recreate JSON Array
            array = new JSONArray(list);
// un select privious array
            if (lastFilter.has(id.getParent())) {
                try {
                    ArrayList<String> lastList = new ArrayList<String>();
                    JSONArray lastArray = null;
                    lastArray = lastFilter.getJSONArray(id.getParent());

                    int lenth = lastArray.length();
                    if (lastArray != null) {
                        for (int i = 0; i < lenth; i++) {
                            try {
                                lastList.add(lastArray.get(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    lastList.remove(id.getName());
                    JSONArray array2 = new JSONArray(lastList);
                    lastFilter.put(id.getParent(), array2);
                    Log.i("result filter", " paraent id " + id.getId() + " " + array2.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            filter.put(id.getParent(), array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayView(int position) {
        try {
            HashMap<String, String> map = (HashMap) listView.getItemAtPosition(position);
            selectedID = map.get("id");
            array = new JSONArray();
            // TODO Auto-generated method stub
            Fragment fragment = null;
            //   Bundle bundle = new Bundle();
            fragment = new MyCatalogSellerFilter();
            //  bundle.putString("list", topList.get(position));
            if (fragment != null) {
                Bundle bundle = new Bundle();

                bundle.putString("items", list.get(position));
                //   }


                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

            } else {
                // error in creating fragment
                Log.e("NewCheckout", "Error in creating fragment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonApply:
                Log.i("result filter", "filter buttonApply " + filter + " last filter " + lastFilter);
                //  filter {"11":["125"]} last filter {"10":["92","88"]}
                JSONObject result;
                result = filter;
                Iterator it = filter.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Log.i("result filter", "key buttonApply " + key);
                    if (lastFilter.has(key)) {
                        Log.i("result filter", "lastFilter.has(key) true " + key);
                        try {
                            JSONArray filterJSONArray = filter.getJSONArray(key);
                            JSONArray lastJSONArray = lastFilter.getJSONArray(key);
                            Log.i("At concat--", filter.getJSONArray(key).length() + "/" + lastFilter.getJSONArray(key).length());
                            JSONArray resultArray = concatArray(filterJSONArray, lastJSONArray);
                            result.put(key, resultArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
//                    }else{
//                        result.put(key,lastFilter.getJSONArray(key));
//                    }
                }
                JSONObject merged = new JSONObject();
                try {
                    JSONObject[] objs = new JSONObject[]{lastFilter, result};
                    for (JSONObject obj : objs) {
                        Iterator itr = obj.keys();
                        while (itr.hasNext()) {
                            String key = (String) itr.next();
                            merged.put(key, obj.get(key));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isRange) {
                    JSONArray array = new JSONArray();
                    array.put("" + min);
                    array.put("" + max);
                    try {
                        merged.put("" + p_id, array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("result filter", merged.toString());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", merged.toString());
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.buttonCancel:
                MyCatalogFilter.isRange = false;
                MyCatalogFilter.max = max;
                MyCatalogFilter.min = min;
                Intent returnIntent1 = new Intent();
                JSONObject object = new JSONObject();
                returnIntent1.putExtra("result", object.toString());
                setResult(RESULT_OK, returnIntent1);
                finish();
                break;

        }
    }

    private JSONArray concatArray(JSONArray arr1, JSONArray arr2)
            throws JSONException {
        JSONArray result = new JSONArray();
        Log.i("At concat--", arr1.length() + "/" + arr2.length());
        for (int i = 0; i < arr1.length(); i++) {

            Log.i("At concat--", "arr1.get(i)" + "/" + arr1.get(i));
            result.put(arr1.get(i));
        }
        for (int i = 0; i < arr2.length(); i++) {
            Log.i("At concat--", "arr2.get(i)" + "/" + arr2.get(i));
            result.put(arr2.get(i));
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MyCatlogFilter");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
