package wydr.sellers.registration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wydr.sellers.R;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.Home;
import wydr.sellers.adapter.ExpandableListAdapter;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.CategoryItem;
import wydr.sellers.modal.GridChild;
import wydr.sellers.modal.GridParent;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.slider.MyCategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by deepesh on 26/6/15.
 */
public class BuisCat extends Fragment implements View.OnClickListener {
    public static boolean switcher = true;
    public static String s1 = "", s2 = "";
    public static int counter = 0;
    private static String KEY_SUCCESS = "categories";
    private static int hitCounter;
    public JSONArray jsonMainArr;
    public List<GridParent> titles;
    public List<CategoryItem> CatValues = new ArrayList<>();
    public String[] storedcat;
    public int globalcounter = 0;
    public ArrayList<Integer> catids, catparentids;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    TextView catcounter;
    HashMap<String, List<String[]>> listDataChild;
    Button sub, skip;
    Helper helper = new Helper();
    ConnectionDetector cd;
    private int lastExpandedGroupPosition = -1;
    Controller application;
    Tracker mTracker;
    SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expandable, container, false);

        hitCounter = 0;
        sub = (Button) view.findViewById(R.id.btnsub1);
        skip = (Button) view.findViewById(R.id.btnskip);
        cd = new ConnectionDetector(getActivity());
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        expListView.setGroupIndicator(null);
        catcounter = (TextView) view.findViewById(R.id.totaltext);
       // ListView lv = (ListView) view.findViewById(R.id.NetworkList);
        session = new SessionManager(getActivity());
        if (cd.isConnectingToInternet()) {
            new GetUserCategory().execute(helper.getDefaults("company_id", getActivity()));
        } else {
            getDetails();
            NetAsync();
        }


        catids = new ArrayList<>();
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                application = (Controller) getActivity().getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("My Business", "onClick", "submit Business categories");
                /*******************************ISTIAQUE***************************************/

                //CatValues.clear();
                try {
                    CatValues = listAdapter.Returnval();
                } catch (NullPointerException e) {
                    Log.e("NullPointerException", e.getMessage());
                }
                String idvls = "", parentids = "";
                if (CatValues.size() > 0) {

                    for (int i = 0; i < CatValues.size(); i++) {

                        //Log.e("name", CatValues.get(i).getName());
                        //Log.e("id", CatValues.get(i).getId());
                        catids.add(Integer.valueOf(CatValues.get(i).getId()));
                        idvls = idvls + CatValues.get(i).getId() + ",";
                        parentids = parentids + CatValues.get(i).getParentid() + ",";
                        //Log.e("IDS", parentids);
                    }
                    if (idvls.length() > 1)
                        idvls = idvls.substring(0, idvls.length() - 1);
                    if (parentids.length() > 1)
                        parentids = parentids.substring(0, parentids.length() - 1);
                    netSync(catids, idvls, parentids);
                    CatValues.clear();
                    catids.clear();
                } else {

                    new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_value_selected));
                }

            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                application = (Controller) getActivity().getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("My Business", "onClick", "skip Business categories");
                /*******************************ISTIAQUE***************************************/

                /*startActivity(new Intent("android.intent.action.Home"));*/
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();
            }
        });
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {


                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {


            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                //  expListView.setDividerHeight(20);

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                return false;
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        //Log.e("displlll", width + "");
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width - GetDipsFromPixel(65), width - GetDipsFromPixel(10));
        } else {
            expListView.setIndicatorBoundsRelative(width - GetDipsFromPixel(65), width - GetDipsFromPixel(10));

        }


        return view;


    }

    private void netSync(ArrayList<Integer> catids, String idvls, String parent_idvls) {
        if (CheckNet()) {
            new SubmitCategory(catids, idvls, parent_idvls).execute();
        } else {
            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));


        }
    }

    private void getDetails() {
        Cursor cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, new String[]{MyCategoryTable.KEY_CATEGORY_ID}, null, null, null);

        int i = 0;
        storedcat = new String[cursor.getCount()];
        while (cursor.moveToNext()) {

            storedcat[i] = cursor.getString(0).toString();
            i++;
        }
        cursor.close();
    }

    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        // startActivity(new Intent(getActivity(), Contact.class));
    }

    public void NetAsync() {

        if (cd.isConnectingToInternet()) {
            new BringCategory().execute();
        } else {
            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));

        }
    }

    public void onclick(View v) {
        //Log.e("ee", "eee" + v.toString());

    }

    public Boolean CheckNet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        //Log.e("hyelll", "yeah");
        //   //Log.e("accc", activeNetworkInfo.toString());
        return activeNetworkInfo != null;

    }

    private class GetUserCategory extends AsyncTask<String, String, JSONObject> {
        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        int flag = 0;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", args[0]));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + args[0] + "/categories", "GET", params,getActivity());

            try {
                if (json != null) {
                    //   Log.i(TAG, " insdie GetUserCategories json " + json.toString());
                    if (json.has(KEY_SUCCESS)) {
                        catdata = new ArrayList<>();
                        JSONObject childJSONObject = json.getJSONObject(KEY_SUCCESS);
                        Iterator<?> keys = childJSONObject.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject js = childJSONObject.getJSONObject(key);
                            CategoryDataModal cd1 = new CategoryDataModal();
                            cd1.setId(js.getString("category_id"));
                            cd1.setName(js.getString("category"));
                            cd1.setParentid(js.getString("parent_id"));
                            cd1.setProduct_count(js.getString("product_count"));
                            if (js.has("subcategories"))
                                cd1.setHas_child(true);
                            else
                                cd1.setHas_child(false);
                            //  Log.i("ADdPRODICT", "DATA -- > " + cd1.getId() + "/" + cd1.getName() + "/" + cd1.getHas_child() + "/" + cd1.getParentid() + "/" + cd1.getProduct_count());
                            catdata.add(cd1);
                            if (js.has("subcategories")) {
                                JSONArray js1 = js.getJSONArray("subcategories");
                                for (int k = 0; k < js1.length(); k++) {
                                    JSONObject js2 = js1.getJSONObject(k);
                                    CategoryDataModal cd2 = new CategoryDataModal();
                                    cd2.setId(js2.getString("category_id"));
                                    cd2.setName(js2.getString("category"));
                                    cd2.setParentid(js2.getString("parent_id"));
                                    cd2.setProduct_count(js2.getString("product_count"));
                                    if (js2.has("subcategories"))
                                        cd2.setHas_child(true);
                                    else
                                        cd2.setHas_child(false);
                                    // Log.i("ADdPRODICT", "DATA -- > " + cd2.getId() + "/" + cd2.getName() + "/" + cd2.getHas_child() + "/" + cd2.getParentid() + "/" + cd2.getProduct_count());
                                    catdata.add(cd2);
                                    if (js2.has("subcategories")) {
                                        JSONArray js3 = js2.getJSONArray("subcategories");
                                        for (int l = 0; l < js3.length(); l++) {
                                            JSONObject js4 = js3.getJSONObject(l);
                                            CategoryDataModal cd3 = new CategoryDataModal();
                                            cd3.setId(js4.getString("category_id"));
                                            cd3.setName(js4.getString("category"));
                                            cd3.setParentid(js4.getString("parent_id"));
                                            cd3.setProduct_count(js4.getString("product_count"));
                                            if (js4.has("subcategories"))
                                                cd3.setHas_child(true);
                                            else
                                                cd3.setHas_child(false);
                                            // Log.i("ADdPRODICT", "DATA -- > " + cd3.getId() + "/" + cd3.getName() + "/" + cd3.getHas_child() + "/" + cd3.getParentid() + "/" + cd3.getProduct_count());
                                            catdata.add(cd3);
                                            if (js4.has("subcategories")) {
                                                JSONArray js6 = js4.getJSONArray("subcategories");
                                                for (int t = 0; t < js6.length(); t++) {
                                                    JSONObject js7 = js6.getJSONObject(t);
                                                    CategoryDataModal cd4 = new CategoryDataModal();
                                                    cd4.setId(js7.getString("category_id"));
                                                    cd4.setName(js7.getString("category"));
                                                    cd4.setParentid(js7.getString("parent_id"));
                                                    cd4.setProduct_count(js7.getString("product_count"));
                                                    if (js7.has("subcategories"))
                                                        cd4.setHas_child(true);
                                                    else
                                                        cd4.setHas_child(false);
                                                    Log.i("BUISCAT", "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" +cd4.getProduct_count());
                                                    catdata.add(cd4);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                    /*********************** ISTIAQUE ***************************/
                    if (json.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/

                } else {
                    flag = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
            // }
//            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if ((getActivity() != null)) {
                progressDialog.dismiss();
                if (flag == 1) {
                    getDetails();
                    NetAsync();

                } else {
                    if (catdata != null) {
                        getActivity().getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
                        for (int s = 0; s < catdata.size(); s++) {
                            ContentValues values = new ContentValues();
                            values.put(MyCategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                            values.put(MyCategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                            values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                            values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                            values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                            values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));
                            // Log.i("ADdPRODICT", "DATA -- > " + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getHas_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);
                        }
                    }


                    getDetails();
                    NetAsync();
                }
            }
        }
    }

    private class BringCategory extends AsyncTask<String, String, ArrayList<Map<String, ArrayList<Map<String, GridChild>>>>> {
        ArrayList<Map<String, ArrayList<Map<String, GridChild>>>> categories;
        AlertDialog.Builder alertDialog;
        int flag = 0;
        String message = "";
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(getActivity());
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected ArrayList<Map<String, ArrayList<Map<String, GridChild>>>> doInBackground(String... args) {

            categories = new ArrayList<>();
            Map<String, ArrayList<Map<String, GridChild>>> grand_data = new HashMap<>();
            ArrayList<Map<String, GridChild>> child_data = new ArrayList<>();

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            if (getActivity() != null) {
                json = userFunction.categoryfetch(getActivity());
                titles = new ArrayList<GridParent>();

//              //Log.e("res", json.toString()+"");
                try {

                    //    String res = json.getString(KEY_SUCCESS);
                    if (json != null) {
                        if (json.has(KEY_SUCCESS)) {

                            jsonMainArr = json.getJSONArray(KEY_SUCCESS);
                            //Log.e("JSON gala", String.valueOf(jsonMainArr.length()));


                            for (int i = 1; i < jsonMainArr.length(); i++) {  // **line 2**
                                //Log.e("entered", "entered");
                                JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                                //Log.e("entered", childJSONObject.toString());
                                Iterator<?> keys = childJSONObject.keys();

                                while (keys.hasNext()) {
                                    counter++;
                                    String key = (String) keys.next();
                                    //Log.e("Key", key);
                                    JSONObject js = childJSONObject.getJSONObject(key);
                                    if (js.getString("parent_id").contentEquals("0")) {
                                        //Log.e("dddd", js.getString("category"));

                                        List<String[]> nowShowing = new ArrayList<String[]>();
                                        Map<String, GridChild> child_map;
                                        Integer a = 0;
                                        if (js.has("subcategories")) {
                                            JSONArray js1 = js.getJSONArray("subcategories");

                                            for (int k = 0; k < js1.length(); k++) {
                                                JSONObject js2 = js1.getJSONObject(k);
                                                GridChild gc = new GridChild();
                                                gc.setId(js2.getString("category_id"));
                                                gc.setName(js2.getString("category"));
                                                gc.setParentid(js.getString("category_id"));
                                                gc.setIsSelected(false);
                                                //Log.e("GAME-", js.getString("category_id"));
                                                if (storedcat.length > 0) {
                                                    if (Arrays.asList(storedcat).contains(js2.getString("category_id"))) {
                                                        a++;
                                                        globalcounter++;
                                                        gc.setIsSelected(true);
                                                    }
                                                }
                                                //Log.e("id/name", js2.getString("category_id") + "/" + js2.getString("category"));
                                                child_map = new HashMap<String, GridChild>();
                                                child_map.put("name", gc);
                                                child_data.add(child_map);
                                            }
                                        }

                                        //Log.e("Child_dara", child_data.toString());

                                        GridParent gp = new GridParent();
                                        gp.setTitle(js.getString("category"));
                                        gp.setCounter(a);
                                        titles.add(gp);

                                    }

                                    grand_data.put("grand_name", child_data);
                                    child_data = new ArrayList<>();
                                    //Log.e("AT LAst", "last");
                                    categories.add(grand_data);
                                    grand_data = new HashMap<>();
                                }


                            }


                        } else if (json.has("error")) {
                            message = json.getString("error");
                            flag = 1;
                        }
                    } else {
                        flag = 2;


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return categories;
            // }
//            return json;

        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, ArrayList<Map<String, GridChild>>>> json) {
            //Log.e("HERE", "PSOT");
//            Log.i("Act",getActivity().toString());
            if ((getActivity() != null)) {
                progressDialog.dismiss();
                if (flag == 1) {

                    alertDialog.setTitle(getResources().getString(R.string.error));
                    alertDialog.setMessage(message);
                    alertDialog.show();


                } else if (flag == 2) {
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(getResources().getString(R.string.server_error));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
                    alertDialog.show();
                } else {
                    Log.i("Act 2", getActivity().toString());
                    listAdapter = new ExpandableListAdapter(getActivity(), categories, titles, expListView, catcounter, globalcounter);
                    expListView.setAdapter(listAdapter);
                    expListView.expandGroup(0);
                }
            }
//            Log.i("Act",getActivity().toString());

        }


    }

    private class SubmitCategory extends AsyncTask<String, String, JSONObject> {
        ArrayList<Integer> catid;
        JSONObject table = new JSONObject();
        int flag = 0;
        String idvls, parent_idvls;
        android.app.AlertDialog.Builder alertDialog;
        private ProgressDialog progressDialog1;

        public SubmitCategory(ArrayList<Integer> adding, String idvls, String parent_idvls) {
            this.catid = adding;
            this.idvls = idvls;
            this.parent_idvls = parent_idvls;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new android.app.AlertDialog.Builder(getActivity());
            progressDialog1 = new ProgressDialog(getActivity());
            progressDialog1.setMessage(getResources().getString(R.string.submitting));
            progressDialog1.setCancelable(false);
            progressDialog1.show();
            try {
                table.put("action", "category_ids");
                table.put("category_ids", new JSONArray(catid));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            json = userFunction.submitCategory(table, helper.getDefaults("user_id", getActivity()),getActivity());
            try {
                if (json != null) {

                    if (json.has("action")) {

                        JSONArray catArray = json.getJSONArray("category_ids");

                        for (int i = 0; i < catArray.length(); i++) {
                            //Log.e("arri", catArray.get(i).toString());
                        }


                    }
                    if (json.has("error")) {
                        flag = 2;
                    }
                } else {
                    flag = 1;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progressDialog1.dismiss();
            if (flag == 0) {

                new GetUserCategories().execute(helper.getDefaults("company_id", getActivity()));
                //Toast.makeText(getActivity().getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
            } else if(flag==2)
            {
                alertDialog.setTitle(getResources().getString(R.string.error));
                try {
                    alertDialog.setMessage(json.getString("error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
                alertDialog.show();
            }else {

                alertDialog.setTitle(getResources().getString(R.string.sorry));
                alertDialog.setMessage(getResources().getString(R.string.server_error));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
                alertDialog.show();

            }


        }


    }

    private class GetUserCategories extends AsyncTask<String, String, JSONObject> {
        int flag = 0;

        android.app.AlertDialog.Builder alertDialog;
        String KEY_SUCCESS = "categories", company_id = helper.getDefaults("company_id", getActivity());
        ArrayList<CategoryDataModal> catdata;
        JSONParser parser = new JSONParser();
        private ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(getActivity());
            progressDialog1.setMessage(getResources().getString(R.string.updating_category));
            progressDialog1.setCancelable(false);
            progressDialog1.show();
            alertDialog = new android.app.AlertDialog.Builder(getActivity());
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("simple", "true"));
            params.add(new BasicNameValuePair("force_product_count", "1"));
            params.add(new BasicNameValuePair("company_ids", company_id));
            JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/vendors/" + helper.getDefaults("company_id", getActivity()) + "/categories", "GET", params,getActivity());
            try {
                if (json != null) {
                    Log.i("BuisCat", " insdie GetUserCategories json " + json.toString());
                    if (json.has(KEY_SUCCESS)) {
                        //jsonMainArr = json.getJSONArray(KEY_SUCCESS);

                        catdata = new ArrayList<>();
//                        for (int i = 1; i < jsonMainArr.length(); i++) {  // **line 2**
                        JSONObject childJSONObject = json.getJSONObject(KEY_SUCCESS);
                        Iterator<?> keys = childJSONObject.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            //Log.i("Key", key);
                            JSONObject js = childJSONObject.getJSONObject(key);
                            //Log.i(TAG, "category " + js.getString("category"));
                            CategoryDataModal cd1 = new CategoryDataModal();
                            cd1.setId(js.getString("category_id"));
                            cd1.setName(js.getString("category"));
                            cd1.setParentid(js.getString("parent_id"));
                            cd1.setProduct_count(js.getString("product_count"));
                            if (js.has("subcategories"))
                                cd1.setHas_child(true);
                            else
                                cd1.setHas_child(false);
                            catdata.add(cd1);
                            if (js.has("subcategories")) {
                                JSONArray js1 = js.getJSONArray("subcategories");
                                for (int k = 0; k < js1.length(); k++) {
                                    JSONObject js2 = js1.getJSONObject(k);
                                    CategoryDataModal cd2 = new CategoryDataModal();
                                    cd2.setId(js2.getString("category_id"));
                                    cd2.setName(js2.getString("category"));
                                    cd2.setParentid(js2.getString("parent_id"));
                                    cd2.setProduct_count(js2.getString("product_count"));
                                    if (js2.has("subcategories"))
                                        cd2.setHas_child(true);
                                    else
                                        cd2.setHas_child(false);
                                    catdata.add(cd2);
                                    if (js2.has("subcategories")) {
                                        JSONArray js3 = js2.getJSONArray("subcategories");
                                        for (int l = 0; l < js3.length(); l++) {
                                            JSONObject js4 = js3.getJSONObject(l);
                                            CategoryDataModal cd3 = new CategoryDataModal();
                                            cd3.setId(js4.getString("category_id"));
                                            cd3.setName(js4.getString("category"));
                                            cd3.setParentid(js4.getString("parent_id"));
                                            cd3.setProduct_count(js4.getString("product_count"));
                                            if (js4.has("subcategories"))
                                                cd3.setHas_child(true);
                                            else
                                                cd3.setHas_child(false);
                                            catdata.add(cd3);
                                            if (js4.has("subcategories")) {
                                                JSONArray js6 = js4.getJSONArray("subcategories");
                                                for (int t = 0; t < js6.length(); t++) {
                                                    JSONObject js7 = js6.getJSONObject(t);
                                                    CategoryDataModal cd4 = new CategoryDataModal();
                                                    cd4.setId(js7.getString("category_id"));
                                                    cd4.setName(js7.getString("category"));
                                                    cd4.setParentid(js7.getString("parent_id"));
                                                    cd4.setProduct_count(js7.getString("product_count"));
                                                    if (js7.has("subcategories"))
                                                        cd4.setHas_child(true);
                                                    else
                                                        cd4.setHas_child(false);
                                                    Log.i("BUISCAT - 2", "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" +cd4.getProduct_count());
                                                    catdata.add(cd4);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    /*********************** ISTIAQUE ***************************/
                    if (json.has("401")) {
                        session.logoutUser();
                    }
                    /*********************** ISTIAQUE ***************************/

                } else {
                    flag = 1;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progressDialog1.dismiss();
            if (flag == 0) {

                getActivity().getContentResolver().delete(MyContentProvider.CONTENT_URI_MYCATEGORY, null, null);
                for (int s = 0; s < catdata.size(); s++) {
                    ContentValues values = new ContentValues();
                    values.put(MyCategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                    values.put(MyCategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                    values.put(MyCategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                    values.put(MyCategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                    values.put(MyCategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                    values.put(MyCategoryTable.KEY_UPDATED_AT, String.valueOf(Calendar.getInstance().getTime()));

                    getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI_MYCATEGORY, values);

                }


                Toast.makeText(getActivity().getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
            } else {
                alertDialog.setTitle(getResources().getString(R.string.sorry));
                alertDialog.setMessage(getResources().getString(R.string.server_error));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
                alertDialog.show();
            }


        }


    }


}