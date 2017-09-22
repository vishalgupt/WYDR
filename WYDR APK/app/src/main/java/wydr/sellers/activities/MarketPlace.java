package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

import wydr.sellers.R;
import wydr.sellers.acc.CategoryHolder;
import wydr.sellers.adapter.HomeViewAdapter;
import wydr.sellers.modal.GridRow;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by surya on 13/8/15.
 */
public class MarketPlace extends Fragment
{
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    private ProgressDialog progress;
    GridView gridView;
    HomeViewAdapter mAdapter, secondAdapter;
    ArrayList<GridRow> list;
    Helper helper = new Helper();

    String[] idCat, catName;
    public static boolean ADAPTER_FLAG = false;
    //  ConnectionDetector cd;
    //  View footer;
    public static String apiKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_place, container, false);
        apiKey = helper.getB64Auth(getActivity());
        inlization(view);
        progressStuff();
        if (cd.isConnectingToInternet()) {
            loadData();
            //     loadSever();
            //new LoadCategory().execute();
        } else {
            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));

        }
        return view;

    }

    private void loadData()
    {
//        private String baseUrl = "https://api.github.com" ;
        // final ProgressDialog dialog = ProgressDialog.show(getA, "", "loading...");
        if (isAdded() && !getActivity().isFinishing())
        {
    //      progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();

            Call<CategoryHolder> call = service.getCategory(helper.getB64Auth(getActivity()),"application/json", "application/json");
            //Call call1=service.getCategory();
            call.enqueue(new Callback<CategoryHolder>() {
                @Override
                public void onResponse(Response<CategoryHolder> response) {
      //              progress.dismiss();
                    Log.d("NewCheckout", "Status Code = " + response.code());
                    if (response.isSuccess()) {
                        try {
                            CategoryHolder result = response.body();
                            categoryObject = new JSONObject(result.getCategories().getAsJsonObject().toString());
                            idCat = new String[categoryObject.length()];
                            catName = new String[categoryObject.length()];
                            Iterator<?> keys = categoryObject.keys();
                            ArrayList<String> keyList = new ArrayList();
                            while (keys.hasNext()) {
                                keyList.add((String) keys.next());
                            }
                            Collections.sort(keyList);

                            for (String key : keyList) {
                                int rootCount = 0;

                                JSONObject object = categoryObject.getJSONObject(key);
                                String name = object.getString("category");
                                String id = object.getString("category_id");

                                String url = "";
                                if (object.has("main_pair")) {
                                    JSONObject js = object.getJSONObject("main_pair");
                                    JSONObject js_obj = js.getJSONObject("detailed");
                                    url = js_obj.getString("http_image_path");
                                }

                                if (object.has("subcategories")) {

                                    JSONArray parentObject = object.getJSONArray("subcategories");
                                    int parentCount = 0;
                                    for (int i = 0; i < parentObject.length(); i++) {
                                        JSONObject objectChild = parentObject.getJSONObject(i);
//
                                        if (objectChild.has("subcategories")) {
                                            int childCount = 0;
                                            JSONArray childObject = objectChild.getJSONArray("subcategories");
                                            for (int j = 0; j < childObject.length(); j++) {
                                                //   String childKey = (String) parentKeys.next();
                                                JSONObject child = childObject.getJSONObject(j);
                                                int count = child.optInt("product_count");
                                                childCount += count;


                                            }
                                            parentCount += childCount;
                                        }


                                    }
                                    if (parentCount == 0)
                                        rootCount += 1;
                                    else
                                        rootCount += parentCount;
                                }
                                final GridRow gridRow = new GridRow(url, name, id);
                                gridRow.id = id;
                                gridRow.setCount(rootCount);
                                gridRow.subCategories = object.has("subcategories");
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.add(gridRow);
                                        }
                                    });
                                }

                                //Log.i("CATEGORY--", name);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // response received but request not successful (like 400,401,403 etc)
                        //Handle errors

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();

                    if (getActivity() != null && !getActivity().isFinishing()) {
                        new AlertDialogManager().showAlertDialog(getActivity(),
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }

    }

    public void changeAdapter() {
        if (gridView.getAdapter() != mAdapter) {
            gridView.setAdapter(mAdapter);
            ADAPTER_FLAG = false;
        } else {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progress != null && progress.isShowing())
            progress.dismiss();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void inlization(View v) {

        gridView = (GridView) v.findViewById(R.id.grid_home);
        //  LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        mAdapter = new HomeViewAdapter(getActivity(), R.layout.category_items,
                new ArrayList<GridRow>());
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view1, int i, long l) {
                GridRow gridRow = (GridRow) gridView.getAdapter().getItem(i);
                if (gridRow != null) {
                    //  Log.i("MArket", "deeeeeeeee");
                    if (gridRow.hasSubCategories()) {
                        secondAdapter = new HomeViewAdapter(getActivity(), R.layout.category_items,
                                new ArrayList<GridRow>());


                        Iterator<?> iterator = categoryObject.keys();
                        ArrayList<String> keys = new ArrayList<String>();
                        while (iterator.hasNext())
                            keys.add((String) iterator.next());
                        Collections.sort(keys);
                        try {
                            //  Log.d("Count Items", " " + gridRow.getCount());
                            if (gridRow.getCount() > 0) {

                                JSONObject object1 = categoryObject.getJSONObject(keys.get(i));
                                JSONArray jsonArray = object1.getJSONArray("subcategories");
                                Intent intent = new Intent(getActivity(), SubCategory.class);
                                intent.putExtra("name", gridRow.title);
                                intent.putExtra("screenVisited", gridRow.title + "/");
                                intent.putExtra("json", jsonArray.toString());
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), MarketPlaceProductsActivity.class);
                        intent.putExtra("id", gridRow.id);
                        intent.putExtra("name", gridRow.title);
                        intent.putExtra("screenVisited", gridRow.title + "/");
                        startActivity(intent);
                    }
                }
                Log.i("MArket", "deeeeeeeee   2");

            }
        });
    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getActivity());
        parser = new JSONParser();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        // progress.show();
    }

    JSONObject categoryObject;


    //    private void loadSever() {
//        AsyncHttpClient client = new AsyncHttpClient();
//
//        client.addHeader("Accept", "application/json");
//        client.addHeader("Authorization", helper.getB64Auth(getActivity()));
//
//        // progress.show();
//        client.get(getActivity(), AppUtil.URL + "3.0/categories?get_images=true&simple=true&item_per_page=0&force_product_count=1", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//                System.out.println(response);
//                //   progress.dismiss();
//                if (isAdded()) {
//                    JSONObject json = null;
//                    try {
//                        json = new JSONObject(response);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    if (json != null) {
//                        if (json.has("categories")) {
//                            try {
//                                categoryObject = json.getJSONObject("categories");
//                                idCat = new String[categoryObject.length()];
//                                catName = new String[categoryObject.length()];
//                                Iterator<?> keys = categoryObject.keys();
//                                while (keys.hasNext()) {
//                                    int rootCount = 0;
//                                    String key = (String) keys.next();
//                                    JSONObject object = categoryObject.getJSONObject(key);
//                                    String name = object.getString("category");
//                                    String id = object.getString("category_id");
//
//                                    String url = "";
//                                    if (object.has("main_pair")) {
//                                        JSONObject js = object.getJSONObject("main_pair");
//                                        JSONObject js_obj = js.getJSONObject("detailed");
//                                        url = js_obj.getString("http_image_path");
//                                    }
//
//                                    if (object.has("subcategories")) {
//
//                                        JSONArray parentObject = object.getJSONArray("subcategories");
//                                        int parentCount = 0;
//                                        for (int i = 0; i < parentObject.length(); i++) {
//                                            JSONObject objectChild = parentObject.getJSONObject(i);
////
//                                            if (objectChild.has("subcategories")) {
//                                                int childCount = 0;
//                                                JSONArray childObject = objectChild.getJSONArray("subcategories");
//                                                for (int j = 0; j < childObject.length(); j++) {
//                                                    //   String childKey = (String) parentKeys.next();
//                                                    JSONObject child = childObject.getJSONObject(j);
//                                                    int count = child.getInt("product_count");
//                                                    childCount += count;
//
//
//                                                }
//                                                parentCount += childCount;
//                                            }
//
//
//                                        }
//                                        if (parentCount == 0)
//                                            rootCount += 1;
//                                        else
//                                            rootCount += parentCount;
//                                    }
//                                    final GridRow gridRow = new GridRow(url, name, id);
//                                    gridRow.id = id;
//                                    gridRow.setCount(rootCount);
//                                    gridRow.subCategories = object.has("subcategories");
//                                    if (getActivity() != null) {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                mAdapter.add(gridRow);
//                                            }
//                                        });
//                                    }
//
//                                    Log.i("CATEGORY--", name);
//
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    } else {
//
//                    }
//
//                    mAdapter.notifyDataSetChanged();
//                    //  mAdapter.notifyDataSetChanged();
//                    ADAPTER_FLAG = false;
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Throwable error,
//                                  String content) {
//                // TODO Auto-generated method stub
//                // if(progress!null)
//                if (isAdded()) {
//                    progress.dismiss();
//                    JSONObject json;
//
//                    String error2 = "";
//                    try {
//                        if (content != null) {
//                            json = new JSONObject(content);
//                            if (json != null)
//                                error2 = json.getString("message");
//                        } else {
//                            error2 = getResources().getString(R.string.something_went_wrong);
//
//                        }
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    if (statusCode == 401) {
//
//                        final SessionManager sessionManager = new SessionManager(getActivity());
//                        Handler mainHandler = new Handler(Looper.getMainLooper());
//
//                        Runnable myRunnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                sessionManager.logoutUser();
//                            } // This is your code
//                        };
//                        mainHandler.post(myRunnable);
//                    }
//                    if (error2.equalsIgnoreCase(""))
//                        error2 = getString(R.string.server_error);
//                    if (statusCode == 404 || statusCode == 500) {
//                        Toast.makeText(getActivity(), error2, Toast.LENGTH_LONG).show();
//                        //     new AlertDialogManager().showAlertDialog(getActivity().getApplicationContext(), "Error", error2);
//                    } else {
//                        //new AlertDialogManager().showAlertDialog(getActivity().getApplicationContext(), "Error", error2);
//                        Toast.makeText(getActivity(), error2, Toast.LENGTH_LONG).show();
//                    }
//
//                    mAdapter.notifyDataSetChanged();
//                }
//
//            }
//        });
//
//        //}
//    }
    @Override
    public void onResume() {
        super.onResume();
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        Log.i("TAG", "Setting screen name: " + "Cart ");
        mTracker.setScreenName("All Categories");
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
