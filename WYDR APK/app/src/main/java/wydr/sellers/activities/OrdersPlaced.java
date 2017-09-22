package wydr.sellers.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.Tracker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.adapter.OrdersAdapter;
import wydr.sellers.gson.OrdersHolder;
import wydr.sellers.holder.DataHolder;
import wydr.sellers.modal.OrderCancelBean;
import wydr.sellers.modal.OrdersModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.webconnector.callbacks.ResponseCallback;
import wydr.sellers.webconnector.callbacks.ResponseHolder;

public class OrdersPlaced extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener
{
    public OrdersAdapter adapter;
    public JSONArray jsonMainArr;
    ListView listView;
    ConnectionDetector cd;
    ArrayList<OrdersModal> ordersList;
    ArrayList<OrderCancelBean> orderCancelList;
    AlertDialog.Builder alertDialog;
    SearchView searchView;
    Helper helper = new Helper();
    JSONParser parser;
    TextView orderStatus;
    HashMap<String, String> statusCodes = new HashMap<>();
    private int pageNo;
    private boolean isLoading;
    private View loadingFooter;
    String userId;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders_placed, container, false);
        cd = new ConnectionDetector(getActivity().getApplicationContext());
        pageNo = 1;
        isLoading = true;
        userId = helper.getDefaults("user_id", getActivity());
        ordersList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.orderplacedList);
        orderStatus = (TextView) view.findViewById(R.id.order_status);
        setHasOptionsMenu(true);
        SetStatusCodes();
        loadingFooter = LayoutInflater.from(getActivity()).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        adapter = new OrdersAdapter(getActivity(), ordersList, 1);
        listView.setAdapter(adapter);

      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject object=new JSONObject();
                if(ordersList!=null){
                    int orderId=Integer.parseInt(ordersList.get(i).getOrder_id());

                }
                    showDialog(getActivity(),object);
            }
        });*/

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
                    prepareRequest();
                }
            }
        });
        alertDialog = new AlertDialog.Builder(getActivity());
        parser = new JSONParser();
        if (cd.isConnectingToInternet()) {
            prepareRequest();
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_conn), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void setNotifCount() {
        Cursor mCursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null) {
            if (mNotifCount == 0) {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            } else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }

        getActivity().invalidateOptionsMenu();
    }

    private void SetStatusCodes() {
        statusCodes.put("P", "Processed");
        statusCodes.put("C", "Complete");
        statusCodes.put("O", "Open");
        statusCodes.put("F", "Failed");
        statusCodes.put("D", "Declined");
        statusCodes.put("B", "Backordered");
        statusCodes.put("I", "Canceled");
        statusCodes.put("Y", "Awaiting call");
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_cart_menu, menu);
        MenuItem item = menu.findItem(R.id.searchCart_cart);
        MenuItemCompat.setActionView(item, R.layout.cart_count);
        notifCount = (FrameLayout) MenuItemCompat.getActionView(item);
        if (mNotifCount == 0) {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
        } else {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            TextView textView = (TextView) notifCount.findViewById(R.id.count);
            textView.setText(String.valueOf(mNotifCount));
        }
        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");
                startActivity(new Intent(getActivity(), CartActivity.class));
            }
        });

        SearchView searchView = (SearchView) menu.findItem(R.id.searchCart_search).getActionView();
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
            listView.clearTextFilter();
        } else {
            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    public void onResume() {
        setNotifCount();
        super.onResume();
    }


    private void prepareRequest()
    {
        isLoading = true;
        orderStatus.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        if (pageNo != 0)
        {
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<OrdersHolder> call = service.OrdersPlaced(userId, pageNo, "true", helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<OrdersHolder>() {
                @Override
                public void onResponse(Response response) {


                    isLoading = false;
                    listView.removeFooterView(loadingFooter);

                    if (response.isSuccess())
                    {
                        OrdersHolder holder = (OrdersHolder) response.body();

                        try {
                            JSONObject json = new JSONObject(holder.getParams().toString());
                            if (json.getInt("page") < pageNo) {
                                pageNo = 0;
                            }

                            jsonMainArr = new JSONArray(holder.getQuery().toString());
                            if (jsonMainArr.length() == 0) {
                                pageNo = 0;
                            } else {
                                orderCancelList=new ArrayList<OrderCancelBean>();
                                for (int i = 0; i < jsonMainArr.length(); i++) {  // **line 2**

                                    OrderCancelBean bean=new OrderCancelBean();

                                    JSONObject cancelOrderObject=jsonMainArr.getJSONObject(i);

                                    String cancelOrder=cancelOrderObject.getString("is_cancel");
                                    int order_id=cancelOrderObject.getInt("order_id");
                                    int issuer_id=cancelOrderObject.getInt("issuer_id");
                                    int user_id=cancelOrderObject.getInt("user_id");

                                    bean.setCancelOrder(cancelOrder);
                                    bean.setIssuerId(issuer_id);
                                    bean.setOrderId(order_id);
                                    bean.setUserId(user_id);
                                    bean.setPosition(i);

                                    orderCancelList.add(bean);

                                    if (jsonMainArr.length() < 10) {
                                        pageNo = 0;
                                    }

                                    JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                                    if (childJSONObject.has("products")) {
                                        JSONArray productsArray = childJSONObject.getJSONArray("products");
                                        for (int k = 0; k < productsArray.length(); k++) {
                                            OrdersModal item = new OrdersModal();
                                            JSONObject jsonObject = productsArray.getJSONObject(k);
                                            item.setOrder_id(childJSONObject.optString("order_id"));
                            //                item.setMrp(childJSONObject.optString("total"));
                                            item.setPlacedon(childJSONObject.optString("order_date"));
                                            item.setTitle(jsonObject.optString("product"));
                                            item.setShippingCost(childJSONObject.optString("shipping_cost"));
                                            item.setCode(jsonObject.optString("product_code"));
                                            if (childJSONObject.optString("seller_name").length() > 1)
                                                item.setPostedBy(MyTextUtils.toTitleCase(childJSONObject.optString("seller_name")) + "," + childJSONObject.optString("company_name").toUpperCase());

                                            else
                                            item.setPostedBy(childJSONObject.optString("company_name").toUpperCase());
                                            item.setMrp(jsonObject.optString("price"));
                                            item.setQty(jsonObject.optString("amount"));
                                            if (jsonObject.has("thumbnails")) {
                                                if (jsonObject.get("thumbnails") instanceof JSONObject) {
                                                    JSONObject obj = jsonObject.getJSONObject("thumbnails");
                                                    item.setImageurls(obj.optString("image_path"));

                                                }
                                            }
                                            /*item.setStatus(statusCodes.get(childJSONObject.optString("status").toUpperCase()));*/
                                            item.setStatus(childJSONObject.optString("status_description").toUpperCase());
                                            item.setList(orderCancelList);
                                            ordersList.add(item);

                                        }
                                    }
                                }
                               // DataHolder.getInstance().setOrderCancelBeanList(orderCancelList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (pageNo == 0)
                        {
                            if (ordersList.size() == 0)
                            {
                                listView.setVisibility(View.GONE);
                                orderStatus.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                listView.setVisibility(View.VISIBLE);
                                orderStatus.setVisibility(View.GONE);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        int statusCode = response.code();
                        if (statusCode == 401)
                        {
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
                        if (ordersList.size() == 0) {
                            listView.setVisibility(View.GONE);
                            orderStatus.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            orderStatus.setVisibility(View.GONE);
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