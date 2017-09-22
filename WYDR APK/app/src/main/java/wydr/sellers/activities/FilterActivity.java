package wydr.sellers.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.modal.Seller;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by surya on 15/9/15.
 */
public class FilterActivity extends AppCompatActivity implements View.OnClickListener {
    static int p_id;
    static Long min, max;
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
    Helper helper = new Helper();
    ConnectionDetector cd;
    JSONObject jsonObject;
    JSONParser jsonParser;
    String cat_id, companyid;
    int code;
    private ProgressDialog progress;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        cd = new ConnectionDetector(FilterActivity.this);
        progressStuff();
        code = getIntent().getIntExtra("request_code", 102);
        cat_id = getIntent().getStringExtra("cat_id");
        companyid = getIntent().getStringExtra("companyid");
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
            Log.i("FilterActivity", "result filter" + jsonArray.toString());
            //  int i = 0;
            list = new ArrayList<>();
            for (Iterator<String> iter = objectCurrent.keys(); iter.hasNext(); ) {
                //    Log.d("itr key", iter.toString());
                String key = iter.next();

                JSONObject object = objectCurrent.getJSONObject(key);
                JSONObject bulidObject = new JSONObject();
                if (object.has("slider")) {

                    bulidObject.put("slider", object.getBoolean("slider"));
                    bulidObject.put("parent_id", key.toString());
                    bulidObject.put("min", object.getString("min"));
                    bulidObject.put("max", object.getString("max"));
                    if (object.has("selected_range")) {
                        bulidObject.put("selected_range", object.getBoolean("selected_range"));
                        bulidObject.put("left", object.getLong("left"));
                        bulidObject.put("right", object.getLong("right"));
                    }

                    // bulidObject.put(key, sliderobject);
                }
                if (object.has("selected_variants")) {
                    if (object.getJSONObject("selected_variants").length() > 0) {
                        bulidObject.put("selected", object.getJSONObject("selected_variants"));
                        bulidObject.put("parent_id", key.toString());
                        // list.add(object.getJSONObject("selected_variants").toString());
                        JSONObject selected = object.getJSONObject("selected_variants");
                        array = new JSONArray();
                        for (Iterator<String> iter2 = selected.keys(); iter2.hasNext(); ) {
                            String key2 = iter2.next();
                            JSONObject objectitems = selected.getJSONObject(key2);
                            // seller.setId(objectitems.getString("variant_id"));
                            array.put(objectitems.getString("variant_id"));
                            Log.d("result filter", " Created " + array.toString());
                        }
                        if (array != null) {
                            lastFilter.put(key, array);
                            Log.d("result filter", " Added " + array.toString() + " key " + key);
                        }
                        //object.put("",array);
                    }

                }

                if (object.has("variants") && object.get("variants") instanceof JSONObject) {
                    bulidObject.put("all", object.getJSONObject("variants"));
                    bulidObject.put("parent_id", key.toString());

                } else {
                    bulidObject.put("all", new JSONObject());
                }

                Log.i("FILTERING--","KEY : " + key.toString() + " - " + bulidObject.toString() );
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
                                                if (cd.isConnectingToInternet())
                                                {//ISTIAQUE
                                                    new GetProductsFilter().execute(cat_id, merged.toString());
                                                }

                                                else
                                                {
                                                    new AlertDialogManager().showAlertDialog(FilterActivity.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                                                }

                                            }

                                        }


        );

    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getApplicationContext());
        /*cd = new ConnectionDetector(FilterActivity.this);*/
        jsonParser = new JSONParser();
        progress = new ProgressDialog(FilterActivity.this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    public void selectFilter(View v) {
        Seller id = (Seller) v.getTag();
        Log.d("result filter", "Parent id " + id.getParent());
        //is chkIos checked?
        if (((CheckBox) v).isChecked()) {
            //Case 1
            array.put("" + id.getId());
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
            list.remove(id.getId());
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
                    lastList.remove(id.getId());
                    JSONArray array2 = new JSONArray(lastList);
                    lastFilter.put(id.getParent(), array2);
                    Log.d("result filter", " paraent id " + id.getId() + " " + array2.toString());
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
            fragment = new SellerFilter();

            //  bundle.putString("list", topList.get(position));
            if (fragment != null) {
                Bundle bundle = new Bundle();
                //  if (list.size() > 0) {
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
                Log.d("result filter", "filter " + filter + " last filter " + lastFilter);
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
                Log.d("result filter", merged.toString());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", merged.toString());
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.buttonCancel:
                FilterActivity.isRange = false;
                FilterActivity.max = max;
                FilterActivity.min = min;
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
        for (int i = 0; i < arr1.length(); i++) {
            result.put(arr1.get(i));
        }
        for (int i = 0; i < arr2.length(); i++) {
            result.put(arr2.get(i));
        }
        return result;
    }


    class GetProductsFilter extends AsyncTask<String, Void, Void> {
        Boolean flag = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jsonObject = new JSONObject();
            jsonArray = new JSONObject();
            progress.show();
        }

        @Override
        protected Void doInBackground(String... args) {

            switch (code) {
                case 101:
                    UserFunctions userFunctions = new UserFunctions();
                    Log.i("FilterActivity", "filter-" + args[1]);
                    jsonObject = userFunctions.getProductsFilter(args[0], helper.getDefaults("user_id", FilterActivity.this), args[1],FilterActivity.this);
                    break;
                case 102:

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("company_id", helper.getDefaults("company_id", getApplicationContext())));
                    params.add(new BasicNameValuePair("sort_by", "timestamp"));
                    //   params.add(new BasicNameValuePair("page", String.valueOf(page_no)));


                    if (args.length > 0) {
//                    list.clear();
//                    page_no = 0;
                        if (args[0].length() > 0) {
                            params.add(new BasicNameValuePair("cid", args[0]));
                        }
                        if (args[1].length() > 0) {
                            params.add(new BasicNameValuePair("filter_hash_array", args[1]));
                        }


                    }
                    jsonObject = jsonParser.makeHttpRequest(AppUtil.URL + "3.0/"
                                    + "products",
                            "GET", params,FilterActivity.this);

                    /*********************** ISTIAQUE ***************************/
                    if (jsonObject.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/

                    break;
                case 104:


                    String user_id = helper.getDefaults("user_id", FilterActivity.this);
                    List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                    params2.add(new BasicNameValuePair("get_image", "0"));
                    params2.add(new BasicNameValuePair("user_detail", "1"));
                    params2.add(new BasicNameValuePair("status", "A"));
                    params2.add(new BasicNameValuePair("product_visibility", "public"));
                    params2.add(new BasicNameValuePair("current_user_id", user_id));
                    String url = null;
                    url = AppUtil.URL + "3.0/"
                            + "vendors/" + companyid + "/products";

                    if (ValidationUtil.isNull(cat_id)) {
                        params2.add(new BasicNameValuePair("filter_hash_array", args[1]));
//                        url = AppUtil.URL + "3.0/"
//                                + "vendors/" + companyid + "/products?get_image=0&user_detail=1&status=A&product_visibility=public&current_user_id=" + user_id;
                    } else {
                        params2.add(new BasicNameValuePair("filter_hash_array", args[1]));
//                        url = AppUtil.URL + "3.0/"
//                                + "vendors/" + companyid + "/products?get_image=0&user_detail=1&status=A&product_visibility=public&current_user_id=" + user_id + "&cid=" + args[0];
                    }
                    Log.d("param", params2.toString());
                    jsonObject = jsonParser.makeHttpRequest(url,
                            "GET", params2,FilterActivity.this);

                    /*********************** ISTIAQUE ***************************/
                    if (jsonObject.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/

                    break;


            }


            try {
                //  Log.d("jsonArray",jsonObject.toString());
                if (jsonObject.has("error")) {
                    flag = true;

                } else {
                    jsonArray = jsonObject.getJSONObject("filters");
                }

                Log.d("jsonArray", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //  productModals = new ArrayList<>();
            progress.dismiss();
            try {
                if (!flag) {
                    jsonArray = new JSONObject(jsonArray.toString());
                    JSONObject objectCurrent = jsonArray.getJSONObject("current");
                    Log.d("result filter", objectCurrent.toString());
                    //  int i = 0;
                    list = new ArrayList<>();
                    for (Iterator<String> iter = objectCurrent.keys(); iter.hasNext(); ) {
                        //    Log.d("itr key", iter.toString());
                        String key = iter.next();

                        JSONObject object = objectCurrent.getJSONObject(key);
                        JSONObject bulidObject = new JSONObject();
                        if (object.has("slider")) {
                            JSONObject sliderobject = new JSONObject();
                            bulidObject.put("slider", object.getBoolean("slider"));
                            bulidObject.put("parent_id", key.toString());
                            bulidObject.put("min", object.getString("min"));
                            bulidObject.put("max", object.getString("max"));
                            if (object.has("selected_range")) {
                                bulidObject.put("selected_range", object.getBoolean("selected_range"));
                                bulidObject.put("left", object.getLong("left"));
                                bulidObject.put("right", object.getLong("right"));
                            }

                            // bulidObject.put(key, sliderobject);
                        }
                        if (object.has("selected_variants")) {
                            if (object.getJSONObject("selected_variants").length() > 0) {
                                bulidObject.put("selected", object.getJSONObject("selected_variants"));
                                bulidObject.put("parent_id", key.toString());
                                // list.add(object.getJSONObject("selected_variants").toString());
                                JSONObject selected = object.getJSONObject("selected_variants");
                                array = new JSONArray();
                                for (Iterator<String> iter2 = selected.keys(); iter2.hasNext(); ) {
                                    String key2 = iter2.next();
                                    JSONObject objectitems = selected.getJSONObject(key2);
                                    // seller.setId(objectitems.getString("variant_id"));
                                    array.put(objectitems.getString("variant_id"));
                                    Log.d("result filter", " Created " + array.toString());
                                }
                                if (array != null) {
                                    lastFilter.put(key, array);
                                    Log.d("result filter", " Added " + array.toString() + " key " + key);
                                }
                                //object.put("",array);
                            }

                        }

                        if (object.has("variants") && object.get("variants") instanceof JSONObject) {
                            bulidObject.put("all", object.getJSONObject("variants"));
                            bulidObject.put("parent_id", key.toString());

                        } else {
                            bulidObject.put("all", new JSONObject());
                        }
                        list.add(bulidObject.toString());


                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            displayView(pos);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("FilterActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
