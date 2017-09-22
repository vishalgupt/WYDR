package wydr.sellers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.gson.FavProdHolder;
import wydr.sellers.gson.FavProdModal;
import wydr.sellers.gson.MakeOrder;
import wydr.sellers.gson.Products;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.MyDatabaseHelper;
import wydr.sellers.slider.UserFunctions;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Navdeep on 12/9/15.
 */
public class FavouriteProductsFragment extends Fragment implements SearchView.OnQueryTextListener
{
    ArrayList<FavProdModal> list;
    FragPagarAdapter adapter;
    ListView listView;
    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    AlertDialog.Builder alertDialog;
    Helper helper = new Helper();
    private int page_no;
    private boolean isLoading;
    private View loadingFooter;
    private ArrayList<String> compIDlist = new ArrayList<>();
    private String user_id;
    TextView order_status;
    MyDatabaseHelper databaseHelper;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.myfavfragment, null);
        iniStuff(rootView);
        setHasOptionsMenu(true);
        progressStuff();
        user_id = helper.getDefaults("user_id", getActivity());
        NetAsync();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        databaseHelper = new MyDatabaseHelper(getActivity());
        //Toast.makeText(getActivity(),databaseHelper.companyids(),Toast.LENGTH_SHORT).show();
        compIDlist = new ArrayList<String>(Arrays.asList(databaseHelper.companyids().split(",")));
    }

    public void NetAsync()
    {
        if (cd.isConnectingToInternet())
        {
            prepareRequest();
        }

        else
        {
            new AlertDialogManager().showAlertDialog(this.getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
    }


    private void progressStuff()
    {
        // TODO Auto-generated method stub
        cd = new ConnectionDetector(this.getActivity());
        parser = new JSONParser();
        alertDialog = new AlertDialog.Builder(this.getActivity());
    }

    private void iniStuff(View rootView)
    {
        page_no = 1;
        isLoading = true;
        listView = (ListView) rootView.findViewById(R.id.listfavseller);
        order_status = (TextView) rootView.findViewById(R.id.record_status);
        list = new ArrayList<>();
        adapter = new FragPagarAdapter(getActivity(), list);
        loadingFooter = LayoutInflater.from(getActivity()).inflate(R.layout.pagination_loading, null);
        listView.addFooterView(loadingFooter);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isLoading || page_no == 0) {
                    return;
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {

                    page_no++;
                    NetAsync();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_cart_menu, menu);

        MenuItem item = menu.findItem(R.id.searchCart_cart);
        MenuItemCompat.setActionView(item, R.layout.cart_count);
        notifCount = (FrameLayout) MenuItemCompat.getActionView(item);
        if (mNotifCount == 0)
        {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
        }

        else
        {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            TextView textView = (TextView) notifCount.findViewById(R.id.count);
            textView.setText(String.valueOf(mNotifCount));
        }
        notifCount.setOnClickListener(new View.OnClickListener()
        {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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


    private void prepareRequest() {
        if (page_no != 0) {
            isLoading = true;
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<FavProdHolder> call = service.getFavProd(user_id, "product", page_no, helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<FavProdHolder>() {
                @Override
                public void onResponse(Response response) {
                    //      progress.dismiss();
                    isLoading = false;
                    listView.removeFooterView(loadingFooter);
                    Log.d("re", "" + response.code());
                    // Log.d("JSON", " " + element.getAsJsonObject().toString());
                    if (response.isSuccess()) {
                        //   progress.dismiss();
                        FavProdHolder holder = (FavProdHolder) response.body();

                        list.addAll(holder.getQuery());
                        if (holder.getQuery().size() < 10) {
                            page_no = 0;
                        }
                        if (page_no == 0) {
                            if (list.size() == 0) {
                                listView.setVisibility(View.GONE);
                                order_status.setVisibility(View.VISIBLE);
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                order_status.setVisibility(View.GONE);
                            }
                            listView.removeFooterView(loadingFooter);

                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        int statusCode = response.code();
                        page_no = 0;
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
                                new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.server_error));
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    page_no = 0;
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (list.size() == 0) {
                            listView.setVisibility(View.GONE);
                            order_status.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            order_status.setVisibility(View.GONE);
                        }
                        listView.removeFooterView(loadingFooter);
                        adapter.notifyDataSetChanged();
                        new AlertDialogManager().showAlertDialog(getActivity(),
                                getString(R.string.error),
                                getString(R.string.server_error));
                    }
                }
            });
        }

    }


    @Override
    public void onResume() {
        setNotifCount();
        super.onResume();
    }

    public class FragPagarAdapter extends BaseAdapter implements Filterable {

        private LayoutInflater inflater = null;
        public ListLoader imageLoader;
        Context context;

        JSONParser parser;

        JSONObject jsonObj;
        AlertDialog.Builder alertDialog;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Helper helper = new Helper();
        private Activity activity;
        private ArrayList<FavProdModal> data, memberData;

        private ProgressDialog progress;
        private String current_user_id, chat_user_id;
        ConnectionDetector cd;
        int flag = 0;


        public FragPagarAdapter(Activity a, ArrayList<FavProdModal> d) {
            activity = a;

            data = d;
            this.memberData = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ListLoader(activity.getApplicationContext());

            progressStuff();
            current_user_id = helper.getDefaults("user_id", a);
            cd = new ConnectionDetector(a);
        }

        public int getCount() {
            if (data != null)
                return data.size();
            else
                return 0;
        }

        public Object getItem(int position)
        {
            return data.get(position);
        }
        public long getItemId(int position)
        {
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null)
            vi = inflater.inflate(R.layout.edi_catalog_listlayout, null);
            TextView title = (TextView) vi.findViewById(R.id.txtTitleAttach); // title
            TextView code = (TextView) vi.findViewById(R.id.txtCodeAttech); // artist name
            TextView mrp = (TextView) vi.findViewById(R.id.txtMRPAttech); // duration
            TextView moq = (TextView) vi.findViewById(R.id.txtmoq);
            RelativeLayout rl = (RelativeLayout)vi.findViewById(R.id.rl_plp);
            final TextView sell = (TextView) vi.findViewById(R.id.txtsp); // duration
            TextView out_stock = (TextView) vi.findViewById(R.id.mycat_out_of_stock);
            ImageView thumb_image = (ImageView) vi.findViewById(R.id.my_product_thumb); // thumb image
            final ImageView edit_image = (ImageView) vi.findViewById(R.id.edititem);
            final String actname = activity.getClass().getName().replace("auriga.sellers.activities.", "");
            final FavProdModal song = data.get(position);
            edit_image.setTag(song);


            edit_image.setImageResource(R.drawable.popup);
            String id = song.getId();
            if (Long.parseLong(song.getQty()) < Long.parseLong(song.getMoq())) {
                out_stock.setVisibility(View.VISIBLE);
            } else {
                out_stock.setVisibility(View.GONE);
            }

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(activity,ProductDetailsActivity.class);
                    intent1.putExtra("product_id",song.getId());
                    intent1.putExtra("name",song.getName());
                    intent1.putExtra("screenVisited","Seller catalog");
                    activity.startActivity(intent1);
                }
            });


            edit_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final PopupWindow popupWindow = new PopupWindow(activity);
                    View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                    //  view.findViewById(R.id.book_wrap).setVisibility(View.VISIBLE);


                    ((ImageView) view.findViewById(R.id.like_img)).setImageResource(R.drawable.like_selected);
                    ((TextView) view.findViewById(R.id.action_like)).setText("Unlike");

                    if (song.getQty() != null && (Integer.parseInt(song.getQty()) == 0 || Integer.parseInt(song.getQty()) < Integer.parseInt(song.getMoq())))
                        view.findViewById(R.id.book_wrap).setVisibility(View.GONE);
                    else {
                        if (compIDlist.contains(data.get(position).getCompanyId())) {
                            view.findViewById(R.id.book_wrap).setVisibility(View.VISIBLE);
                        }
                    }


                    view.findViewById(R.id.like_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FavProdModal song = (FavProdModal) edit_image.getTag();

                            if (cd.isConnectingToInternet())
                                new LikeQuery(position, song).execute(song.getId());
                            else {
                                Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                            }
                            popupWindow.dismiss();
                        }
                    });

                    view.findViewById(R.id.chat_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FavProdModal song = (FavProdModal) edit_image.getTag();
                            if (cd.isConnectingToInternet()) {
                                //new PrimaryUser(song).execute();
                                getPrimary(song);

                            } else {
                                Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_LONG).show();
                            }
                            popupWindow.dismiss();


                        }
                    });
                    view.findViewById(R.id.share_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new Intent(activity, ShareCatalogWith.class);
                            shareIntent.putExtra("product_id", ((FavProdModal) edit_image.getTag()).getId());
                            activity.startActivity(shareIntent);
                            popupWindow.dismiss();
                        }
                    });


                    view.findViewById(R.id.book_wrap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final FavProdModal song = (FavProdModal) edit_image.getTag();
                            final Dialog d = new Dialog(activity);
                            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            View view = LayoutInflater.from(activity).inflate(R.layout.book_order_dialog, null);
                            d.setContentView(view);
                            final EditText np = (EditText) view.findViewById(R.id.numberPicker1);
                            final TextView moqcheck = (TextView) view.findViewById(R.id.moqalert);
                            moqcheck.setText("MOQ should be atleast " + song.getMoq());
                            final Button book = (Button) view.findViewById(R.id.book);
                            book.setEnabled(false);
                            book.setTextColor(activity.getResources().getColor(R.color.book_dis));
                            np.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (np.getText().toString().length() > 0) {
                                        Double a = Double.parseDouble(np.getText().toString());
                                        if (a >= Double.parseDouble(song.getMoq().toString())) {
                                            moqcheck.setVisibility(View.INVISIBLE);
                                            book.setEnabled(true);
                                            book.setTextColor(activity.getResources().getColor(R.color.white));
                                            np.setBackgroundResource(R.drawable.code_bar);
                                        } else {
                                            moqcheck.setVisibility(View.VISIBLE);
                                            book.setEnabled(false);
                                            book.setTextColor(activity.getResources().getColor(R.color.book_dis));
                                            np.setBackgroundResource(R.drawable.error_bar);

                                        }
                                    } else {
                                        moqcheck.setVisibility(View.VISIBLE);
                                        book.setEnabled(false);
                                        book.setTextColor(activity.getResources().getColor(R.color.book_dis));
                                    }
                                }
                            });
                            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
                                }
                            });
                            view.findViewById(R.id.book).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (cd.isConnectingToInternet()) {
                                        if (Long.parseLong(song.getQty()) < Long.parseLong(song.getMoq())) {
                                            new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.oops), activity.getString(R.string.out_of_stock));
                                        } else if (Long.parseLong(song.getQty()) < Long.parseLong(np.getText().toString())) {
                                            new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.only) + " " + Long.parseLong(song.getQty()) + " " + activity.getString(R.string.available));
                                        } else if (Long.parseLong(song.getQty()) >= Long.parseLong(np.getText().toString())) {
                                            jsonObj = new JSONObject();

                                            FavProdModal song2 = (FavProdModal) edit_image.getTag();

                                            HashMap<String, Products> map = new HashMap<>();
                                            JsonObject jsonObject = new JsonObject();
                                            map.put("1", new Products(song2.getId(), np.getText().toString(),jsonObject,""));
                                            prepareOrder(helper.getDefaults("user_id", activity), map);


                                        }
                                    } else {
                                        Toast.makeText(activity, activity.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
                                    }


                                    d.dismiss();
                                }
                            });
                            d.show();
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.setContentView(view);
                    popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int xOffset = -(view.getMeasuredWidth() - (edit_image.getWidth() / 2) + 10);
                    int yOffset = -4 * (edit_image.getHeight());
                    popupWindow.showAsDropDown(edit_image, xOffset, yOffset);
                }
            });


            moq.setText("MOQ : " + BigDecimal.valueOf(Integer.parseInt(song.getMoq())).toPlainString());
            title.setText(song.getName());
            code.setText("Product Code : " + song.getCode());
            mrp.setText("MRP :" + activity.getResources().getString(R.string.rs) + " " + (int)(Double.parseDouble(song.getPrice())) + "/pc");
            sell.setText("SP :" + activity.getResources().getString(R.string.rs) + " " + (int)(Double.parseDouble(song.getSp())) + "/pc");


            mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (song.getChatForPrice() != null && song.getChatForPrice().equalsIgnoreCase("1")) {
                mrp.setVisibility(View.INVISIBLE);
                sell.setVisibility(View.INVISIBLE);
            } else {
                mrp.setVisibility(View.VISIBLE);
                sell.setVisibility(View.VISIBLE);
            }
            if (song.getThumbnails().getUrl() != null) {
                //   Log.e("iamge", song.getImgUrl());
                imageLoader.DisplayImage(song.getThumbnails().getUrl(), thumb_image, R.drawable.default_product);

            }
            return vi;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults(); // Holds the
                    // values
                    ArrayList<FavProdModal> filterlist = new ArrayList<>();

                    if (memberData == null) {
                        memberData = new ArrayList<FavProdModal>();

                    }
                    if (constraint != null && memberData != null && memberData.size() > 0) {

                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < memberData.size(); i++) {
                            String data = memberData.get(i).getName();

                            if (data.toLowerCase().contains(constraint.toString())) {
                                FavProdModal modal = new FavProdModal();

                                modal.setId(memberData.get(i).getId());
                                modal.setCode(memberData.get(i).getCode());
                                modal.setPrice(memberData.get(i).getPrice());
                                modal.setSp(memberData.get(i).getSp());
                                modal.setName(memberData.get(i).getName());
                                modal.setQty(memberData.get(i).getQty());
                                modal.setMoq(memberData.get(i).getMoq());
                                modal.setUserid(memberData.get(i).getUserid());
                                modal.setCompanyId(memberData.get(i).getCompanyId());
                                modal.setThumbnails(memberData.get(i).getThumbnails());
                                modal.setChatForPrice(memberData.get(i).getChatForPrice());
                                filterlist.add(modal);
                            }
                        }
                        results.values = filterlist;

                    }

                    // }

                    return results;

                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    data = (ArrayList<FavProdModal>) results.values;
                    if (memberData.size() != 0)
                        notifyDataSetChanged();
                }
            }

                    ;

        }

        private void progressStuff() {
            // TODO Auto-generated method stub
            // session = new SessionManager(getApplicationContext());
            cd = new ConnectionDetector(activity);
            parser = new JSONParser();
            progress = new ProgressDialog(activity);
            progress.setMessage("Loading...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            alertDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }

        private void prepareOrder(String user_id, HashMap<String, Products> map) {
            progress.show();
            MakeOrder create = new MakeOrder(map, user_id, AppUtil.PAYMENT_COD,"1");

            RestClient.GitApiInterface service = RestClient.getClient();
            Call<String> call = service.bookMyOrders(create, helper.getB64Auth(activity), "application/json", "application/json");
            call.enqueue(new Callback<String>() {
                             @Override
                             public void onResponse(Response response) {

                                 progress.dismiss();
                                 if (response.isSuccess()) {

                                     if (activity != null && !activity.isFinishing()) {
                                         new AlertDialogManager().showAlertDialog(activity,
                                                 activity.getResources().getString(R.string.success),
                                                 activity.getResources().getString(R.string.order_plced_success));

                                     }


                                 } else {
                                     int statusCode = response.code();
                                     //}
                                     if (statusCode == 401) {
                                         final SessionManager sessionManager = new SessionManager(activity);
                                         Handler mainHandler = new Handler(Looper.getMainLooper());

                                         Runnable myRunnable = new Runnable() {
                                             @Override
                                             public void run() {
                                                 sessionManager.logoutUser();
                                             } // This is your code
                                         };
                                         mainHandler.post(myRunnable);
                                     } else {
                                         if (activity != null && !activity.isFinishing()) {
                                             new AlertDialogManager().showAlertDialog(activity,
                                                     activity.getString(R.string.error),
                                                     activity.getString(R.string.server_error));
                                         }
                                     }
                                 }
                                 // }
                             }

                             @Override
                             public void onFailure(Throwable t) {

                                 progress.dismiss();
                                 if (activity != null && !activity.isFinishing()) {
                                     new AlertDialogManager().showAlertDialog(activity,
                                             activity.getString(R.string.error),
                                             activity.getString(R.string.server_error));
                                 }
                             }
                         }

            );

        }


        private class LikeQuery extends AsyncTask<String, String, JSONObject> {

            public String error = "";
            public int flag = 0, pos;
            public FavProdModal productModal;
            JSONObject table = new JSONObject();
            Boolean success = false;

            public LikeQuery(int pos, FavProdModal tag) {
                this.productModal = tag;
                this.pos = pos;
                Log.e("productModal", productModal.getName() + "-" + pos);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!activity.isFinishing())
                    progress.show();
            }

            @Override
            protected JSONObject doInBackground(String... args) {

                UserFunctions userFunction = new UserFunctions();
                JSONObject json = null;
                try {
                    table.put("user_id", current_user_id);
                    table.put("object_id", args[0]);
                    table.put("object_type", "product");
                    table.put("unlike", "1");
                    Log.e("IIIII", args[0] + "///////");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = userFunction.LikeRequest("POST", table, "Query", activity);
                if (json != null) {
                    Log.e("JSON--", json.toString());
                    try {
                        if (json.has("message")) {
                            if (json.get("message").toString().equalsIgnoreCase("_your favourite item added successfully"))
                                success = true;
                        } else if (json.has("error")) {
                            if (json.get("error").toString().equalsIgnoreCase("NO CONTENT"))
                                success = true;
                            else
                                success = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    flag = 1;

                }
                return json;
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                if (!activity.isFinishing()) {
                    progress.dismiss();
                    if (success) {
                        data.remove(pos);
                        FragPagarAdapter.this.notifyDataSetChanged();
                        Toast.makeText(activity, activity.getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            Toast.makeText(activity, json.getString("error"), Toast.LENGTH_LONG);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }


        private void getPrimary(final FavProdModal song) {
            if (!activity.isFinishing()) {


                progress.show();
                RestClient.GitApiInterface service = RestClient.getClient();
                Call<JsonElement> call = service.getPrimary(song.getCompanyId(), "1", helper.getB64Auth(activity), "application/json", "application/json");
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Response response) {
                        progress.dismiss();

                        if (response.isSuccess()) {
                            JsonElement element = (JsonElement) response.body();
                            JSONObject json = null;
                            if (element != null && element.isJsonObject()) {
                                try {
                                    json = new JSONObject(element.getAsJsonObject().toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (json != null && json.has("users")) {
                                try {
                                    chat_user_id = json.getJSONObject("users").optString("user_id");
                                    Log.i("chat_user_id", chat_user_id);
                                    Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);
                                    cursor.moveToNext();
                                    Log.i("cursor.getCount()", "" + cursor.getCount());
                                    if (cursor.getCount() > 0) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name", "" + song.getName());
                                        bundle.putString("code", "" + song.getCode());
                                        String marketPrice = String.format("%.2f", Double.parseDouble(song.getPrice()));
                                        String sellingPrice = String.format("%.2f", Double.parseDouble(song.getSp()));
                                        if (song.getChatForPrice() != null && song.getChatForPrice().equalsIgnoreCase("1")) {
                                            bundle.putString("mrp", "");
                                            bundle.putString("price", "");

                                        } else {
                                            if (marketPrice.length() > 10) {
                                                marketPrice = marketPrice.substring(0, 7);
                                                bundle.putString("mrp", "" + marketPrice);

                                            } else {
                                                bundle.putString("mrp", "" + marketPrice);
                                            }

                                            if (sellingPrice.length() > 10) {
                                                sellingPrice = sellingPrice.substring(0, 7);
                                                bundle.putString("price", "" + sellingPrice);
                                            } else {
                                                bundle.putString("price", "" + sellingPrice);
                                            }
                                        }
                                        if (song.getThumbnails().getUrl() != null) {
                                            bundle.putString("url", "" + song.getThumbnails().getUrl());
                                        } else {
                                            bundle.putString("url", "");
                                        }
                                        bundle.putString("moq", "" + song.getMoq());
                                        int iCompanyId = cursor.getColumnIndexOrThrow(NetSchema.USER_NET_ID);
                                        activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", "" + cursor.getString(iCompanyId)).putExtra("from", 101).putExtra("result", bundle));


                                    } else {
                                        Log.i("getUserDetails", "  ");
                                        getUserDetails(chat_user_id, song);
                                        //new GetUserDetails(song).execute(chat_user_id);
                                    }
                                    cursor.close();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.user_not_found));
                            }

                        } else {
                            int statusCode = response.code();

                            if (statusCode == 401) {

                                final SessionManager sessionManager = new SessionManager(activity);
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.logoutUser();
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                            } else {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));
                            }

                        }


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progress.dismiss();

                        if (activity != null && !activity.isFinishing())
                            new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));

                    }
                });
            }


        }

        private void getUserDetails(String userId, final FavProdModal product) {
            if (!activity.isFinishing()) {
                final Calendar c = Calendar.getInstance();
                flag = 0;


                progress.show();
                RestClient.GitApiInterface service = RestClient.getClient();
                Call<JsonElement> call = service.getUserDetails(userId, helper.getB64Auth(activity), "application/json", "application/json");
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Response response) {
                        progress.dismiss();
                        if (response.isSuccess()) {
                            String userid = "";
                            JsonElement element = (JsonElement) response.body();
                            JSONObject json = null;
                            if (element != null && element.isJsonObject()) {
                                try {
                                    json = new JSONObject(element.getAsJsonObject().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            if (json != null) {

                                if (json.has("user_id") && json.optString("is_openfire").equalsIgnoreCase("1")) {
                                    try {

                                        ContentValues cv = new ContentValues();
                                        if (json.has("phone"))
                                            cv.put(NetSchema.USER_PHONE, json.getString("phone"));
                                        if (json.has("company_name"))
                                            cv.put(NetSchema.USER_COMPANY, json.getString("company_name"));
                                        cv.put(NetSchema.USER_DISPLAY, " ");
                                        if (json.has("company_id"))
                                            cv.put(NetSchema.USER_COMPANY_ID, json.getString("company_id"));
                                        if (json.has("user_id"))
                                            cv.put(NetSchema.USER_ID, json.getString("user_id"));
                                        if (json.has("user_login")) {
                                            userid = json.getString("user_login") + "@" + AppUtil.SERVER_NAME;
                                            cv.put(NetSchema.USER_NET_ID, userid);
                                        }
                                        cv.put(NetSchema.USER_STATUS, "0");
                                        cv.put(NetSchema.USER_NAME, helper.ConvertCamel(json.getString("firstname")) + " " + helper.ConvertCamel(json.getString("lastname")));
                                        cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));
                                        activity.getContentResolver().insert(ChatProvider.NET_URI, cv);
                                        Cursor cursor = activity.getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
                                        if (cursor.getCount() > 0)
                                            flag = 3;
                                        Log.e("count", cursor.getCount() + "--****");
                                        cursor.close();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {

                                    flag = 2;
                                }
                            } else {
                                flag = 1;
                            }
                            if (flag == 1) {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.server_error));

                            } else if (flag == 2) {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.sorry), activity.getString(R.string.user_not_exist));

                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("name", "" + product.getName());
                                bundle.putString("code", "" + product.getCode());
                                if (product.getChatForPrice() != null && product.getChatForPrice().equalsIgnoreCase("1")) {
                                    bundle.putString("mrp", "");
                                    bundle.putString("price", "");

                                } else {
                                    String marketPrice = String.format("%.2f", Double.parseDouble(product.getPrice()));
                                    String sellingPrice = String.format("%.2f", Double.parseDouble(product.getSp()));
                                    if (marketPrice.length() > 10) {
                                        marketPrice = marketPrice.substring(0, 7);
                                        bundle.putString("mrp", "" + marketPrice);

                                    } else {
                                        bundle.putString("mrp", "" + marketPrice);
                                    }

                                    if (sellingPrice.length() > 10) {
                                        sellingPrice = sellingPrice.substring(0, 7);
                                        bundle.putString("price", "" + sellingPrice);
                                    } else {
                                        bundle.putString("price", "" + sellingPrice);
                                    }
                                }

                                if (product.getThumbnails().getUrl() != null) {
                                    bundle.putString("url", "" + product.getThumbnails().getUrl());
                                } else {
                                    bundle.putString("url", "");

                                }
                                bundle.putString("moq", "" + product.getMoq());

                                activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("user", userid).putExtra("from", 101).putExtra("result", bundle));


                            }
                        } else {
                            int statusCode = response.code();

                            if (statusCode == 401) {

                                final SessionManager sessionManager = new SessionManager(activity);
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.logoutUser();
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                            } else {
                                if (activity != null && !activity.isFinishing())
                                    new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progress.dismiss();
                        if (activity != null && !activity.isFinishing())
                            new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.error), activity.getString(R.string.server_error));

                    }
                });
            }


        }

    }

}
