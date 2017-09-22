package wydr.sellers.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import wydr.sellers.R;
import wydr.sellers.activities.Catalog;
import wydr.sellers.activities.Home;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by Deepesh_pc on 01-09-2015.
 */


/**
 * Created by deepesh on 26/6/15.
 */
public class FetchCategory extends Activity implements View.OnClickListener {

    private static String KEY_SUCCESS = "categories";
    public JSONArray jsonMainArr;
    public ArrayList<CategoryDataModal> catdata;
    public int flag, errorflag = 0;
    ConnectionDetector cd;

    private void NetAsync() {
        if (cd.isConnectingToInternet()) {
            new BringCategory().execute();
        } else {
            new AlertDialogManager().showAlertDialog(getApplicationContext(), "Error", "Network issue");

        }
    }

    @Override
    public void onClick(View v) {
        // startActivity(new Intent(getActivity(), Contact.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable);
        catdata = new ArrayList<>();
        cd = new ConnectionDetector(getApplicationContext());
        flag = Integer.parseInt(getIntent().getExtras().getString("Flag"));
        NetAsync();
    }

    private class BringCategory extends AsyncTask<String, String, JSONObject> {

        android.app.AlertDialog.Builder alertDialog;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new android.app.AlertDialog.Builder(FetchCategory.this);
            progressDialog = new ProgressDialog(FetchCategory.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;

            json = userFunction.getCategory(FetchCategory.this);

            try {

                //    String res = json.getString(KEY_SUCCESS);
                if (json != null) {
                    if (json.has(KEY_SUCCESS)) {

                        jsonMainArr = json.getJSONArray(KEY_SUCCESS);
                        Log.e("JSON gala", String.valueOf(jsonMainArr.length()));


                        for (int i = 1; i < jsonMainArr.length(); i++) {  // **line 2**
                            Log.e("entered", "entered");
                            JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                            Log.e("entered", childJSONObject.toString());
                            Iterator<?> keys = childJSONObject.keys();

                            while (keys.hasNext()) {

                                String key = (String) keys.next();
                                Log.e("Key", key);
                                JSONObject js = childJSONObject.getJSONObject(key);

                                Log.e("dddd", js.getString("category"));
                                CategoryDataModal cd1 = new CategoryDataModal();
                                cd1.setId(js.getString("category_id"));
                                cd1.setName(js.getString("category"));
                                cd1.setParentid(js.getString("parent_id"));
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
                                                if (js4.has("subcategories"))
                                                    cd3.setHas_child(true);
                                                else
                                                    cd3.setHas_child(false);
                                                catdata.add(cd3);
                                            }

                                        }


                                    }
                                }

                                //  Log.e("Child_dara", catdata.toString());


                            }


                        }
                        for (int s = 0; s < catdata.size(); s++) {
                            ContentValues values = new ContentValues();
                            values.put(CategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                            values.put(CategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                            values.put(CategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                            values.put(CategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                            Log.e("DATA -- > ", catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getHas_child() + "/" + catdata.get(s).getParentid());
                            getContentResolver().insert(MyContentProvider.CONTENT_URI_Category, values);

                        }


                    } else if (json.has("error")) {
                        errorflag = 1;
                        // new AlertDialogManager().showAlertDialog(getActivity().getApplicationContext(), "Sorry", json.getString("errorr"));
                    }
                } else {
                    errorflag = 2;
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
            Log.e("flag", flag + "");
            progressDialog.dismiss();
            if (errorflag == 0) {
                if (flag == 1)
                    startActivity(new Intent(FetchCategory.this, Home.class));
                else if (flag == 2)

                    startActivity(new Intent(FetchCategory.this, Catalog.class));
            } else if (errorflag == 1) {
                alertDialog.setTitle(getResources().getString(R.string.sorry));
                alertDialog.setMessage(getResources().getString(R.string.server_error));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            } else if (errorflag == 2) {
                alertDialog.setTitle(getResources().getString(R.string.error));
                try {
                    alertDialog.setMessage(json.getString("error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        }


    }


}