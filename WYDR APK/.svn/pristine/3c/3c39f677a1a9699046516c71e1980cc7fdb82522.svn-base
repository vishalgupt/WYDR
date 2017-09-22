package wydr.sellers.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.adapter.SharedItemsAdapter;
import wydr.sellers.gson.SharedHolder;
import wydr.sellers.modal.SharedProductsModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public class SharedWithMe extends Fragment {
    //    private ProgressDialog progress;
    public SharedItemsAdapter adapter;
    public JSONArray jsonMainArr;
    ListView listView;
    ConnectionDetector cd;
    ArrayList<SharedProductsModal> productsList;
    Helper helper = new Helper();
    private int pageNo;
    private boolean isLoading;
    private View loadingFooter;
    AlertDialog.Builder alertDialog;
    private String current_user_id;
    TextView order_status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders_placed, container, false);
        cd = new ConnectionDetector(getActivity().getApplicationContext());
        pageNo = 1;
        isLoading = true;
        current_user_id = helper.getDefaults("user_id", getActivity());
        order_status = (TextView) view.findViewById(R.id.order_status);
        order_status.setText(getResources().getString(R.string.no_products_shared));
        alertDialog = new AlertDialog.Builder(getActivity());
        productsList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.orderplacedList);
        loadingFooter = LayoutInflater.from(getActivity()).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        adapter = new SharedItemsAdapter(getActivity(), productsList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isLoading || pageNo == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {

                    pageNo++;
                    NetAsync();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String product_id = productsList.get(position).getProduct_id();
                Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                intent.putExtra("product_id", product_id);
                intent.putExtra("name", productsList.get(position).getTitle());
                startActivity(intent);
            }
        });
        NetAsync();
        return view;
    }


    public void NetAsync() {
        if (cd.isConnectingToInternet()) {
            prepareRequest();
        } else {
            listView.removeFooterView(loadingFooter);
            adapter.notifyDataSetChanged();
        }
    }

    private void prepareRequest() {
        isLoading = true;
        order_status.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        if (pageNo != 0) {

            RestClient.GitApiInterface service = RestClient.getClient();
            Call<SharedHolder> call = service.SharedWithMe("false", pageNo, current_user_id, "with_me", "product", helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<SharedHolder>() {
                @Override
                public void onResponse(Response response) {

                    isLoading = false;
                    listView.removeFooterView(loadingFooter);

                    if (response.isSuccess()) {
                        SharedHolder holder = (SharedHolder) response.body();

                        try {
                            JSONObject json = new JSONObject(holder.getParams().toString());
                            if (json.getInt("page") < pageNo) {
                                pageNo = 0;
                            }

                            jsonMainArr = new JSONArray(holder.getData().toString());
                            if (jsonMainArr.length() == 0) {
                                pageNo = 0;
                            } else {
                                for (int i = 0; i < jsonMainArr.length(); i++) {  // **line 2**
                                    Log.e("entered", "entered");

                                    JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                                    Log.e("i--", i + "");
                                    SharedProductsModal product = new SharedProductsModal();
                                    product.setTitle(childJSONObject.optString("product"));
                                    product.setProduct_id(childJSONObject.optString("product_id"));
                                    product.setCode(childJSONObject.optString("product_code"));
                                    product.setMRP(childJSONObject.optString("list_price"));
                                    product.setSP(childJSONObject.optString("price"));
                                    product.setQuantity(childJSONObject.optString("amount"));
                                    product.setMOQ(childJSONObject.optString("min_qty"));
                                    product.setType("with_me");
                                    product.setPostedBy(MyTextUtils.toTitleCase(childJSONObject.optString("name")) + ", " + childJSONObject.optString("company").toUpperCase());
                                    if (childJSONObject.has("thumbnails") && childJSONObject.get("thumbnails") instanceof JSONObject) {
                                        JSONObject obj = childJSONObject.getJSONObject("thumbnails");
                                        product.setImageurl(obj.optString("image_path"));

                                    }
                                    productsList.add(product);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (pageNo == 0) {
                            if (productsList.size() == 0) {
                                listView.setVisibility(View.GONE);
                                order_status.setVisibility(View.VISIBLE);
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                order_status.setVisibility(View.GONE);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        int statusCode = response.code();
                        if (statusCode == 401) {

                            final SessionManager sessionManager = new SessionManager(getActivity());
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.logoutUser();
                                } // This is your code
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                new AlertDialogManager().showAlertDialog(getActivity(),
                                        getString(R.string.error),
                                        getString(R.string.server_error));
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (listView.getFooterViewsCount() > 0)
                            listView.removeFooterView(loadingFooter);
                        if (productsList.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        pageNo = 0;
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(getActivity(),
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }
    }
}