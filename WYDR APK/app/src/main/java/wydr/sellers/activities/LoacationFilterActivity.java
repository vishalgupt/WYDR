package wydr.sellers.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import wydr.sellers.R;
import wydr.sellers.modal.Seller;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;

/**
 * Created by surya on 1/10/15.
 */
public class LoacationFilterActivity extends AppCompatActivity implements View.OnClickListener {
    static int p_id, min, max;
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
    JSONArray lastFilter, filter;
    Button cancel, apply;
    JSONArray array;
    ConnectionDetector cd;
    JSONObject jsonObject;
    JSONParser jsonParser;
    private ProgressDialog progress;
    //String cat_id, companyid;
    ////  int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_filter_activity);
        progressStuff();
        apply = (Button) findViewById(R.id.buttonApply2);
        cancel = (Button) findViewById(R.id.buttonCancel2);
        apply.setOnClickListener(this);
        cancel.setOnClickListener(this);
        lastFilter = new JSONArray();
        listView = (ListView) findViewById(R.id.listViewFilterMenu);
        filter = new JSONArray();
        try {
            jsonArray = new JSONObject(getIntent().getStringExtra("filter"));
            JSONObject object = jsonArray.getJSONObject("location");
            Log.d("result filter", object.toString());
            Log.d("result filter", jsonArray.toString());
            //  int i = 0;
            list = new ArrayList<>();
            JSONObject bulidObject = new JSONObject();

            if (object.has("selected_variants")) {
                if (object.get("selected_variants") instanceof JSONArray) {
                    bulidObject.put("selected", object.getJSONArray("selected_variants"));
                    array = new JSONArray();
                    JSONArray selected = object.getJSONArray("selected_variants");

                    for (int i = 0; i < selected.length(); i++) {
                        //array.put(selected.getString(i));
                        array.put(selected.getString(i));
                        Log.d("result filter", " Created " + array.toString());
                    }
                    if (array != null) {
                        lastFilter = array;
                        Log.d("result filter", " Added " + array.toString());
                    }
                }

            }

            if (object.has("variants") && object.get("variants") instanceof JSONArray) {
                bulidObject.put("all", object.getJSONArray("variants"));
                // bulidObject.put("parent_id", key.toString());

            } else {
                bulidObject.put("all", new JSONArray());
            }
            list.add(bulidObject.toString());


            // }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        displayView(0);


    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(LoacationFilterActivity.this);
        jsonParser = new JSONParser();
        progress = new ProgressDialog(LoacationFilterActivity.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    public void selectFilter(View v) {
        Seller sell = (Seller) v.getTag();
        Log.d("result filter", "Parent id " + sell.getName());
        //is chkIos checked?
        if (((CheckBox) v).isChecked()) {
            //Case 1
            array.put(sell.getName());
            sell.setIsSelected(true);
        } else {
            sell.setIsSelected(false);
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
            list.remove(sell.getName());
//Recreate JSON Array
            array = new JSONArray(list);

            //  }
            if (lastFilter.length() > 0) {
                ArrayList<String> lastList = new ArrayList<String>();
                JSONArray lastArray = null;
                lastArray = lastFilter;

                int lenth = lastArray.length();
                if (lastArray != null) {
                    for (int i = 0; i < lenth; i++) {
                        try {
                            lastList.add(lastArray.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                lastList.remove(sell.getName());
                JSONArray array2 = new JSONArray(lastList);
                lastFilter = array2;
                Log.d("result filter", " paraent id " + array2.toString());
            }

        }

        filter = array;


    }

    private void displayView(int position) {

        HashMap<String, String> map = (HashMap) listView.getItemAtPosition(position);
        selectedID = map.get("id");

        array = new JSONArray();

        // TODO Auto-generated method stub
        Fragment fragment = null;
        //   Bundle bundle = new Bundle();
        fragment = new LocationFilter();

        //  bundle.putString("list", topList.get(position));
        if (fragment != null) {
            Bundle bundle = new Bundle();
            //  if (list.size() > 0) {
            bundle.putString("items", list.get(position));
            //   }


            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container2, fragment).commit();

        } else {
            // error in creating fragment
            Log.e("NewCheckout", "Error in creating fragment");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonApply2:
                Log.d("result filter", "filter " + filter + " last filter " + lastFilter);
                //  filter {"11":["125"]} last filter {"10":["92","88"]}
                JSONArray result = null;
                try {
                    result = concatArray(filter, lastFilter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("result filter", result.toString());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result.toString());
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.buttonCancel2:
                Intent returnIntent1 = new Intent();
                JSONObject object = new JSONObject();
                returnIntent1.putExtra("result", object.toString());
                setResult(RESULT_CANCELED, returnIntent1);
                finish();
                break;

        }
    }

    private JSONArray concatArray(JSONArray arr1, JSONArray arr2)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (int i = 0; i < arr1.length(); i++) {
            result.put(arr1.get(i));
        }
        for (int i = 0; i < arr2.length(); i++) {
            result.put(arr2.get(i));
        }
        return result;
    }


}
