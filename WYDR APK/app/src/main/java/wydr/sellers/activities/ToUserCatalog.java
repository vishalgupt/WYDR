package wydr.sellers.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.adapter.AttachAdapter;
import wydr.sellers.modal.AttachModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 5/8/15.
 */
public class ToUserCatalog extends Fragment implements SearchView.OnQueryTextListener {
    ListView listViewCalalog;
    ArrayList<AttachModal> list;
    AttachAdapter adapter;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    String catTo;
    SearchView searchView;
    android.widget.Filter filter;
    Helper helper = new Helper();
    private ProgressDialog progress;
    private boolean success = false;
    TextView label;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //  catFrom = getActivity().getIntent().getStringExtra("id");
        catTo = helper.getDefaults("company_id", getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.to_user_catalog, container, false);
        label = (TextView) v.findViewById(R.id.textViewNoRecordFound);
        iniStuff(v);
        setHasOptionsMenu(true);

        progressStuff();
        if (cd.isConnectingToInternet())
        {
            new LoadCatalog().execute();
        }

        else
        {
            new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.no_internet_conn));
        }


        listViewCalalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       // ChatProduct product = (ChatProduct) parent.getItemAtPosition(position);
                                                       AttachModal modal = (AttachModal) parent.getItemAtPosition(position);

                                                       Bundle bundle = new Bundle();
                                                       bundle.putString("name", "" + modal.getName());
                                                       bundle.putString("code", "" + modal.getCode());
                                                       bundle.putString("mrp", "" + modal.getPrice());
                                                       bundle.putString("price", "" + modal.getSellingPrice());
                                                       bundle.putString("url", "" + modal.getImgUrl());
                                                       bundle.putString("moq", "" + modal.getMoq());
                                                       //   bundle.putString("code",modal.getCode());
//                String result = "Product Name : Demo" + "\nProduct Code : ";
                                                       Intent returnIntent = new Intent();
                                                       returnIntent.putExtra("result", bundle);
                                                       getActivity().setResult(Activity.RESULT_OK, returnIntent);
                                                       getActivity().finish();
                                                   }
                                               }

        );

        return v;
    }


    private void progressStuff() {
        // TODO Auto-generated method stub
        session = new SessionManager(getActivity().getApplicationContext());
        cd = new ConnectionDetector(getActivity());
        parser = new JSONParser();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        // progress.show();
    }

    private void iniStuff(View v) {
        listViewCalalog = (ListView) v.findViewById(R.id.listView_attach_catalog_to);
        list = new ArrayList<>();
        adapter = new AttachAdapter(getActivity(), list);
        listViewCalalog.setAdapter(adapter);
//        listViewCalalog.setTextFilterEnabled(false);
//        filter = adapter.getFilter();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {


        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");

            listViewCalalog.clearTextFilter();
        } else {

            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    private class LoadCatalog extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!getActivity().isFinishing() && isAdded())
                progress.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = parser.makeHttpRequest(AppUtil.URL
                            + "products?product_visibility=public&status=A&company_id=" + catTo,
                    "GET", params, getActivity());
            if (json.has("products")) {
                try {
                    JSONArray array = json.getJSONArray("products");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        AttachModal modal = new AttachModal();
                        modal.setId(object.getString("product_id"));
                        modal.setCode(object.getString("product_code"));
                        modal.setPrice(object.getString("list_price"));
                        modal.setSellingPrice(object.getString("base_price"));
                        modal.setName(object.getString("product"));
                        modal.setMoq(object.getString("min_qty"));
                        if (object.has("main_pair")) {
                            JSONObject obj = object.getJSONObject("main_pair");
                            JSONObject detailObj = obj.getJSONObject("detailed");
                            modal.setImgUrl(detailObj.getString("http_image_path"));
                        }
                        list.add(modal);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /*********************** ISTIAQUE ***************************/
            if (json.has("401")) {
                session.logoutUser();
            }
            /*********************** ISTIAQUE ***************************/

            // check your log for json response
            Log.d("Result attempt", json.toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!getActivity().isFinishing() && isAdded()) {
                super.onPostExecute(s);
                progress.dismiss();

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                if (listViewCalalog.getCount() == 0) {
                    listViewCalalog.setVisibility(View.GONE);
                    label.setVisibility(View.VISIBLE);
                }
            }

        }
    }
}
