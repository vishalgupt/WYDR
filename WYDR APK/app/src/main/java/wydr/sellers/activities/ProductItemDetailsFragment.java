package wydr.sellers.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.TouchImageView;
import wydr.sellers.adapter.VarientsAdapter;
import wydr.sellers.gson.AddOnlineCart;
import wydr.sellers.gson.MakeOrder;
import wydr.sellers.gson.Products;
import wydr.sellers.modal.BannerModel;
import wydr.sellers.modal.CouponModal;
import wydr.sellers.modal.VarientsModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.DetailLoader;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

import wydr.sellers.slider.CirclePageIndicator;
import wydr.sellers.slider.MyDatabaseHelper;
import wydr.sellers.slider.UserFunctions;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Navdeep on 9/9/15.
 */
@SuppressLint("ValidFragment")
public class ProductItemDetailsFragment extends Fragment implements View.OnClickListener
{
    public String product_id, productid,chat_user_id;
    public RadioGroup radioGroup;
    public CirclePageIndicator titleIndicator;
    public static String var_ids;
    public boolean fav_flag = false;
    SimpleDateFormat formatDatabase = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    TextView productName, mrp, sp, quantity, sellerInfo, productDescription, product_qty, txtDelivers;
    RecyclerView coupontxt;
    ImageView fav_image, chatseller;
    JSONObject jsonObject;
    CouponAdapter couponAdapter;
    ArrayList<CouponModal>couponList= new ArrayList<>();
    RecyclerView rv_varients;
    ArrayList<String>ufList= new ArrayList<>();
    LinearLayout buyLinerar;
    ArrayList<String> compIDlist;
    ArrayList<VarientsModal>varientsModals = new ArrayList<>();
    MyDatabaseHelper databaseHelper;
    TextView outstock;
    String catid;
    String couponjson="0";
    String Chat_about_product = "N";
    Button book_order, buyNow, chat_about, out_stock_bottom,out_network_buy;
    ImageView out_network_cart, normalcart;
    DetailLoader loader;
    ViewPager viewPager;
    Helper helper = new Helper();
    JSONParser parser;
    ImageLoader pagerLoader;
    DisplayImageOptions options;
    int amount=0, moq = 1;
    LinearLayout nll,out_network_ll;
    String productAmount, productUrl, seller_id, seller_comp_id, seller_name, zeroAction = "";
    Long product_sp, product_mrp;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    String product_Weight = "";
    String cart_discount = "0";
    String pay_method = "";
    String discount_method;
    String dis;
    ImageView i;
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    LinearLayout ll121,ll122;
    //private List<Bitmap> imagegallery = new ArrayList<>();
    //RequestParams params;
    JSONObject jsonObj;
    private ProgressDialog progress;
    ConnectionDetector cd;
    String user_comp_id, cur_user_id, user_id, title;
    String freeShipping, shippingCharge;
    String tokan;
    TextView txt_discount;
    ImagePagerAdapter adapter;
    VarientsAdapter varientsAdapter;
    ArrayList<String> path = new ArrayList<>();
    int flag=0;
    public HashMap<String,String> varhashmap= new HashMap<>();
    public HashMap<String,String> namehashmap = new HashMap<>();
    Boolean isDescTitlePressed = false;
    Boolean iscouponTitlePressed = false;
    Boolean isFeatureTitlePressed = false;
    Button desc_title, feature_title, coupon_title;
    TextView productFeature,d_price,d_string;
    ScrollView pdp_scrollView;
    Controller application;
    Tracker mTracker;
    String screenVisited = "";
    long startTime = 0;

    public ProductItemDetailsFragment(String product_id, String title, String screenVisited)
    {
        this.product_id = product_id;
        this.title = title;
        this.screenVisited = screenVisited;
    }

    public ProductItemDetailsFragment()
    {

    }
    AlertDialog.Builder alertDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        startTime = System.currentTimeMillis();
        View contentView = inflater.inflate(R.layout.item_detail , null);
        pagerLoader = ImageLoader.getInstance();
        pagerLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        couponAdapter = new CouponAdapter(couponList);
        ll121 = (LinearLayout)contentView.findViewById(R.id.ll121);
        ll122 = (LinearLayout)contentView.findViewById(R.id.ll122);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_product)
//              .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(500)).build();
        progressStuff();
        tokan = helper.getB64Auth(getActivity());
        buyLinerar = (LinearLayout) contentView.findViewById(R.id.linearLayout4);
        outstock = (TextView) contentView.findViewById(R.id.item_out_stock);
        user_comp_id = helper.getDefaults("company_id", getActivity());
        cur_user_id = helper.getDefaults("user_id", getActivity());
        PrefManager prefManager = new PrefManager(getActivity());
        d_price = (TextView)contentView.findViewById(R.id.d_price);
        d_string = (TextView)contentView.findViewById(R.id.d_string);
        d_price.setVisibility(View.GONE);
        d_string.setVisibility(View.GONE);
        //AKSHAY CODE starts
        /*databaseHelper = new MyDatabaseHelper(getContext());*/
        databaseHelper = new MyDatabaseHelper(getActivity());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
        //Toast.makeText(getActivity(),databaseHelper.companyids(),Toast.LENGTH_SHORT).show();
        compIDlist = new ArrayList<String>(Arrays.asList(databaseHelper.companyids().split(",")));
        out_network_cart = (ImageView) contentView.findViewById(R.id.out_network_buy_cart);
        out_network_cart.setOnClickListener(this);
        out_network_buy = (Button) contentView.findViewById(R.id.out_network_buy);
        i = (ImageView) contentView.findViewById(R.id.alert);
        i.setVisibility(View.GONE);

        if(ufList.contains(AppUtil.TAG_Network))
        {
            i.setVisibility(View.VISIBLE);
        }

        i.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Please note!");
                ad.setMessage("Register your business to avail displayed Wholesale Prices.\n" +
                        "15% extra will be added to cart for non-business users.");
                ad.setButton(DialogInterface.BUTTON_POSITIVE , "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i= new Intent(getActivity(), Catalog.class);
                        startActivity(i);
                        ad.dismiss();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        ad.dismiss();
                    }
                });
                ad.show();
            }
        });

        out_network_buy.setOnClickListener(this);
        normalcart = (ImageView) contentView.findViewById(R.id.book_order_cart);
        normalcart.setOnClickListener(this );
        //akshay code ends
        varientsAdapter = new VarientsAdapter(varientsModals,getActivity());
        rv_varients = (RecyclerView)contentView.findViewById(R.id.varients);
        rv_varients.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_varients.setLayoutManager(layoutManager);
        rv_varients.setAdapter(varientsAdapter);
        loader = new DetailLoader(getActivity().getApplicationContext());
        //productCode = (TextView) contentView.findViewById(R.id.procode);
        productName = (TextView) contentView.findViewById(R.id.proname);
        txt_discount = (TextView) contentView.findViewById(R.id.txt_discount);
        mrp = (TextView) contentView.findViewById(R.id.promrp);
        txtDelivers = (TextView) contentView.findViewById(R.id.txt_delivery);
        sp = (TextView) contentView.findViewById(R.id.prosp);
        nll = (LinearLayout) contentView.findViewById(R.id.normal_ll) ;
        out_network_ll= (LinearLayout) contentView.findViewById(R.id.out_network_buy_ll);
        quantity = (TextView) contentView.findViewById(R.id.prominorder);
        //category = (TextView) contentView.findViewById(R.id.procategory);
        sellerInfo = (TextView) contentView.findViewById(R.id.id_sellername);
        //callseller = (ImageView) contentView.findViewById(R.id.callseller);
        chatseller = (ImageView) contentView.findViewById(R.id.chatseller);
        productDescription = (TextView) contentView.findViewById(R.id.prodesc);
        //radioGroup = (RadioGroup)contentView.findViewById(R.id.sugg);
        //radioGroup.setVisibility(View.GONE);
        coupontxt = (RecyclerView) contentView.findViewById(R.id.my_recycler_view_pdp_aki);
        coupontxt.setVisibility(View.GONE);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        coupontxt.setLayoutManager(layoutManager1);
        coupontxt.setAdapter(couponAdapter);
        couponAdapter.notifyDataSetChanged();
        fav_image = (ImageView) contentView.findViewById(R.id.favourite_btn);
        book_order = (Button) contentView.findViewById(R.id.book_order);
        buyNow = (Button) contentView.findViewById(R.id.btnBuyNow);
        out_stock_bottom = (Button) contentView.findViewById(R.id.out_stock_bottom);
        chat_about = (Button) contentView.findViewById(R.id.item_chat_about);
        product_qty = (TextView) contentView.findViewById(R.id.qty);
        parser = new JSONParser();
        sellerInfo.setOnClickListener(this);
        buyNow.setOnClickListener(this);

        /***************** ISTIAQUE CODE STARTS ***************************/
        notifCount = (FrameLayout) getActivity().findViewById(R.id.counterPanel);
        productDescription.setVisibility(View.GONE);
        productFeature = (TextView) contentView.findViewById(R.id.prodfeature);
        productFeature.setVisibility(View.GONE);
        desc_title = (Button) contentView.findViewById(R.id.desc_title);
        coupon_title = (Button) contentView.findViewById(R.id.coupon_button);
        coupon_title.setVisibility(View.GONE);
        feature_title = (Button) contentView.findViewById(R.id.feature_title);
        feature_title.setVisibility(View.GONE);
        pdp_scrollView = (ScrollView) contentView.findViewById(R.id.pdp_scrollview);
        coupon_title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (coupontxt.isShown())
                {
                    coupon_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_down_arrow, 0);
                    coupontxt.setVisibility(coupontxt.isShown() ? View.GONE : View.VISIBLE);
                }

                else
                {
                    coupon_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_up_arrow, 0);
                    coupontxt.setVisibility(coupontxt.isShown() ? View.GONE : View.VISIBLE);
                    pdp_scrollView.post(new Runnable()
                    {
                        public void run()
                        {
                            pdp_scrollView.arrowScroll(View.FOCUS_DOWN);
                        }
                    });
                }

                /*if (radioGroup.isShown())
                {
                    coupon_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_down_arrow, 0);
                    radioGroup.setVisibility(radioGroup.isShown() ? View.GONE : View.VISIBLE);
                }
                else
                {
                    coupon_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_up_arrow, 0);
                    radioGroup.setVisibility(radioGroup.isShown() ? View.GONE : View.VISIBLE);
                    pdp_scrollView.post(new Runnable()
                    {
                        public void run()
                        {
                            pdp_scrollView.arrowScroll(View.FOCUS_DOWN);
                        }
                    });
                }*/
                iscouponTitlePressed = !iscouponTitlePressed;
            }
        });

        desc_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productDescription.isShown()) {
                    desc_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_down_arrow, 0);
                    productDescription.setVisibility(productDescription.isShown() ? View.GONE : View.VISIBLE);
                }

                else
                {
                    desc_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_up_arrow, 0);
                    productDescription.setVisibility(productDescription.isShown() ? View.GONE : View.VISIBLE);
                    pdp_scrollView.post(new Runnable() {
                        public void run() {
                            pdp_scrollView.arrowScroll(View.FOCUS_DOWN);
                        }
                    });
                }
                isDescTitlePressed = !isDescTitlePressed;
            }
        });

        feature_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFeatureTitlePressed) {
                    feature_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_down_arrow, 0);
                    productFeature.setVisibility(productFeature.isShown() ? View.GONE : View.VISIBLE);
                }

                else
                {
                    feature_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gray_up_arrow, 0);
                    productFeature.setVisibility(productFeature.isShown() ? View.GONE : View.VISIBLE);
                    pdp_scrollView.post(new Runnable() {
                        public void run() {
                            pdp_scrollView.arrowScroll(View.FOCUS_DOWN);
                        }
                    });
                }
                isFeatureTitlePressed = !isFeatureTitlePressed;
            }
        });
        /***************** ISTIAQUE CODE ENDS *************************************/

        fav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet() && productid != null) {
                    if (fav_flag) {
                        new LikeQuery().execute(productid, "1");
                        fav_flag = false;
                    } else {
                        new LikeQuery().execute(productid, "0");
                        fav_flag = true;
                    }
                } else {
                    alertDialog.setTitle(getResources().getString(R.string.oops));
                    alertDialog.setMessage(getResources().getString(R.string.no_internet_conn));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
                    alertDialog.show();
                }

            }
        });

        chat_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat();
            }
        });
        book_order.setOnClickListener(new View.OnClickListener()
                                      {
                                          @Override
                                          public void onClick(View v) {
                                              final Dialog d = new Dialog(getActivity());
                                              d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                              View view = LayoutInflater.from(getActivity()).inflate(R.layout.book_order_dialog, null);
                                              d.setContentView(R.layout.book_order_dialog);
                                              final EditText np = (EditText) d.findViewById(R.id.numberPicker1);
                                              final TextView moqcheck = (TextView) d.findViewById(R.id.moqalert);
                                              moqcheck.setText(getResources().getString(R.string.atleast_moq) + quantity.getText());
                                              final Button book = (Button) d.findViewById(R.id.book);
                                              book.setEnabled(false);
                                              book.setTextColor(getActivity().getResources().getColor(R.color.book_dis));
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
                                                          if (a >= Double.parseDouble(quantity.getText().toString().replace("MOQ : ", "")))
                                                          {
                                                              moqcheck.setVisibility(View.INVISIBLE);
                                                              book.setEnabled(true);
                                                              book.setTextColor(getActivity().getResources().getColor(R.color.white));
                                                              np.setBackgroundResource(R.drawable.code_bar);
                                                          }
                                                          else
                                                          {
                                                              moqcheck.setVisibility(View.VISIBLE);
                                                              book.setEnabled(false);
                                                              book.setTextColor(getActivity().getResources().getColor(R.color.book_dis));
                                                              np.setBackgroundResource(R.drawable.error_bar);
                                                          }
                                                      }

                                                      else
                                                      {
                                                          moqcheck.setVisibility(View.VISIBLE);
                                                          book.setEnabled(false);
                                                          book.setTextColor(getActivity().getResources().getColor(R.color.book_dis));
                                                      }
                                                  }
                                              });
                                              d.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      d.dismiss();
                                                  }
                                              });
                                              d.findViewById(R.id.book).setOnClickListener(new View.OnClickListener() {
                                                                                               @Override
                                                                                               public void onClick(View v) {
                                                                                                   Log.e("vlaue", np.getText() + "");
                                                                                                   if (amount < moq)
                                                                                                   {
                                                                                                       //  text_input_quantity.setErrorEnabled(true);
                                                                                                       // np.setError(getResources().getString(R.string.out_of_stock));
                                                                                                       new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.out_of_stock));
                                                                                                   } else if (amount < Long.parseLong(np.getText().toString())) {
                                                                                                       // text_input_quantity.setErrorEnabled(true);
                                                                                                       // np.setError(getResources().getString(R.string.requested_not_available));
                                                                                                       new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.sorry), getString(R.string.only) +" " + amount +" " +  getString(R.string.available));
                                                                                                   } else if (amount >= Long.parseLong(np.getText().toString())) {
                                                                                                       if (cd.isConnectingToInternet()) {

                                                                                                           HashMap<String, Products> map = new HashMap<>();
                                                                                                           JsonObject a = new JsonObject();
                                                                                                           JsonParser jsonParser = new JsonParser();
                                                                                                           if(!varhashmap.isEmpty()) {
                                                                                                               a = (JsonObject) jsonParser.parse(converttostring(varhashmap));
                                                                                                           }

                                                                                                           String s = "";
                                                                                                           map.put("1", new Products(productid, np.getText().toString(),a,""));
                                                                                                           if(namehashmap.size()==varientsAdapter.getItemCount()) {
                                                                                                               prepareOrder(cur_user_id, map);
                                                                                                           }
                                                                                                           else{
                                                                                                               Toast.makeText(getActivity(),"Please select all the variants",Toast.LENGTH_SHORT).show();
                                                                                                           }



                                                                                                       } else {
                                                                                                           alertDialog.setTitle(getResources().getString(R.string.oops));
                                                                                                           alertDialog.setMessage(getResources().getString(R.string.no_internet_conn));
                                                                                                           alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                                               public void onClick(DialogInterface dialog, int id) {
                                                                                                                   getActivity().finish();
                                                                                                               }
                                                                                                           });
                                                                                                           alertDialog.show();
                                                                                                       }
                                                                                                   }
                                                                                                   d.dismiss();
                                                                                               }
                                                                                           }

                                              );
                                              d.show();
                                          }
                                      }

        );

        try
        {
            adapter = new ImagePagerAdapter(path);
            titleIndicator = (CirclePageIndicator) contentView.findViewById(R.id.titles);
            viewPager = (ViewPager) contentView.findViewById(R.id.pager);
            adapter.notifyDataSetChanged();
            viewPager.setAdapter(adapter);
            titleIndicator.setViewPager(viewPager);
            //  adapter = new ImageViewPagerAdapter(getActivity(), imagegallery);

        } catch (IllegalStateException e) {
            Log.e("IllegalStateException: ", e.getMessage());
            e.printStackTrace();
        }

        if (cd.isConnectingToInternet())
        {
            try {
               // List<NameValuePair>p = new ArrayList<>();
                new GetProduct().execute();
            } catch (Exception e) {
                Log.e("Exception: ", e.getMessage());
            }
        }

        else
        {
            alertDialog.setTitle(getResources().getString(R.string.oops));
            alertDialog.setMessage(getResources().getString(R.string.no_internet_conn));
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });
            alertDialog.show();
        }

        chatseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat();
            }
        });
        setHasOptionsMenu(true);
        return contentView;
    }

    public int checkdiscount(String a, String bt)
    {
        int discount = 0;

        if ("by_fixed".equalsIgnoreCase(a))
        {
            discount = (int)Double.parseDouble(bt);
        }

        else
        {
            discount = (int) ((Double.parseDouble(productAmount) * Double.parseDouble(bt))/100);
        }
        return discount;
    }


    public void updateonlineCart()
    {
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.addtocart(addOnlineCart(),helper.getB64Auth(getActivity()),"application/json", "application/json");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response<JsonElement> response) {
                Log.d("There Cart", "" + response.raw());
            }

            @Override
            public void onFailure(Throwable t)
            {
                Log.d("Cart failure","fail");
            }
        });
    }

    private AddOnlineCart addOnlineCart()
    {
        AddOnlineCart a;
        Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, null, null, null, null);
        int iQty = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_QTY);
        int iId = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_ID);
        int ivar = cursor.getColumnIndexOrThrow(CartSchema.PRODUCT_VARIENT);
        int ivarname = cursor.getColumnIndexOrThrow(CartSchema.VARIENT_STRING);
        HashMap<String, Products> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            try
            {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject)jsonParser.parse(cursor.getString(ivar));
                int pos = cursor.getPosition() + 1;
                map.put("" + pos, new Products(cursor.getString(iId), cursor.getString(iQty),jsonObject,""));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.d("PDP_DETAILS", map.toString());
        a = new AddOnlineCart("cart_status*,wish_list*,checkout*,account_info*",map,helper.getDefaults("user_id",getActivity()));
        Gson g = new Gson();
        String jsonString = g.toJson(a);
        Log.d("JSONCart ",jsonString.toString());
        cursor.close();
        return a;
    }



    private void startChat()
    {
        if (cd.isConnectingToInternet())
        {
            getPrimary(seller_comp_id);
        }
        else
        {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        progress.dismiss();
    }

    private void progressStuff()
    {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getActivity());
        //parser = new JSONParser();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(getActivity());
        // progress.show();
    }

    public void fun(View v)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnBuyNow:
                Log.d("amount and moq","amount-"+amount+" "+"moq-"+moq);
                if (amount < moq)
                {
                    new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.out_of_stock));
                }

                else
                {
                    Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.PRODUCT_ID + "=?", new String[]{product_id}, null);
                    if (cursor.getCount() > 0)
                    {
                        ContentValues cv = new ContentValues();
                        cv.put(CartSchema.PRODUCT_VARIENT,converttostring(varhashmap));
                        cv.put(CartSchema.VARIENT_STRING, commaSeparatedString());
                        getActivity().getContentResolver().update(ChatProvider.CART_URI,cv,CartSchema.PRODUCT_ID + "=?",new String[]{product_id});
                        //new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.already_in_cart), getResources().getString(R.string.product_already_cart));
                        startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                    }

                    else
                    {
                        if (zeroAction.equals("R") && product_sp == 0)
                        {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.not_allowed));
                        }
                        else
                        {
                            if(namehashmap.size()==varientsAdapter.getItemCount()) {
                                Date date = Calendar.getInstance().getTime();
                                Log.d("Is three ", "IN ");
                                ContentValues cv = new ContentValues();
                                cv.put(CartSchema.PRODUCT_NAME, productName.getText().toString());
                                cv.put(CartSchema.PRODUCT_ID, product_id);
                                cv.put(CartSchema.PRODUCT_CODE, product_qty.getText().toString());
                                cv.put(CartSchema.PRODUCT_PRICE, product_sp);
                                cv.put(CartSchema.PRODUCT_URL, productUrl);
                                cv.put(CartSchema.PRODUCT_SHIPPING, freeShipping);
                                cv.put(CartSchema.PRODUCT_SHIPPING_CHARGES, shippingCharge);
                                cv.put(CartSchema.PRODUCT_STATUS, 1);
                                if (moq == 0)
                                {
                                    moq = 1;
                                }
                                cv.put(CartSchema.PRODUCT_QTY, moq);
                                cv.put(CartSchema.PRODUCT_INVENTORY, amount);
                                cv.put(CartSchema.PRODUCT_MOQ, moq);
                                cv.put(CartSchema.DISCOUNT,cart_discount);
                                cv.put(CartSchema.PAY_METHOD,pay_method);
                                cv.put(CartSchema.PRODUCT_WEIGHT,product_Weight);
                                cv.put(CartSchema.PRODUCT_CAT_ID,catid);
                                cv.put(CartSchema.CART_PROMO,couponjson);
                                cv.put(CartSchema.CART_PROMO_APPLIED,"0");
                                cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE,"0");
                                cv.put(CartSchema.PROMO_MIN_QTY,"0");
                                cv.put(CartSchema.PROMO_MAX_AMT,"0");
                                cv.put(CartSchema.VARIENT_STRING,commaSeparatedString());
                                cv.put(CartSchema.PRODUCT_VARIENT,converttostring(varhashmap));
                                cv.put(CartSchema.KEY_CREATED, formatDatabase.format(date));
                                Uri uri = getActivity().getContentResolver().insert(ChatProvider.CART_URI, cv);
                                Log.d("Is three ", "IN " + uri.toString());

                                /*******************************ISTIAQUE***************************************/
                                application = (Controller) getActivity().getApplication();
                                mTracker = application.getDefaultTracker();
                                application.trackEvent("PDP", "Move", "CartAcitvity");
                                /*******************************ISTIAQUE***************************************/
                                startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                            }
                            else{
                                Toast.makeText(getActivity(),"Please select all the variants",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    /**** ISTIAQUE: CODE BEGINS *****/
                    if(cursor != null){
                        cursor.close();
                    }
                    /**** ISTIAQUE: CODE ENDS *****/

                }


                break;

            case R.id.out_network_buy_cart:

                if (amount < moq)
                {
                    new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.out_of_stock));
                }
                else
                {
                    Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.PRODUCT_ID + "=?", new String[]{product_id}, null);
                    if (cursor.getCount() > 0)
                    {
                        //startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                        new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.already_in_cart), getResources().getString(R.string.product_already_cart));
                    }
                    else
                    {
                        if (zeroAction.equals("R") && product_sp == 0) {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.not_allowed));
                        }

                        else
                        {
                            if(namehashmap.size()==varientsAdapter.getItemCount()) {

                                Date date = Calendar.getInstance().getTime();
                                Log.d("Is three ", "IN ");
                                ContentValues cv = new ContentValues();
                                cv.put(CartSchema.PRODUCT_NAME, productName.getText().toString());
                                cv.put(CartSchema.PRODUCT_ID, product_id);
                                cv.put(CartSchema.PRODUCT_CODE, product_qty.getText().toString());
                                cv.put(CartSchema.PRODUCT_PRICE, product_sp);
                                cv.put(CartSchema.PRODUCT_URL, productUrl);
                                cv.put(CartSchema.PRODUCT_SHIPPING, freeShipping);
                                cv.put(CartSchema.PRODUCT_SHIPPING_CHARGES, shippingCharge);
                                cv.put(CartSchema.PRODUCT_STATUS, 1);
                                if (moq == 0) {
                                    moq = 1;
                                }
                                cv.put(CartSchema.PRODUCT_QTY, moq);
                                cv.put(CartSchema.PRODUCT_INVENTORY, amount);
                                cv.put(CartSchema.PRODUCT_MOQ, moq);
                                cv.put(CartSchema.DISCOUNT, cart_discount);
                                cv.put(CartSchema.PAY_METHOD, pay_method);
                                cv.put(CartSchema.PRODUCT_WEIGHT, product_Weight);
                                cv.put(CartSchema.PRODUCT_CAT_ID, catid);
                                cv.put(CartSchema.CART_PROMO, couponjson);
                                cv.put(CartSchema.CART_PROMO_APPLIED, "0");
                                cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE, "0");
                                cv.put(CartSchema.PROMO_MIN_QTY, "0");
                                cv.put(CartSchema.PROMO_MAX_AMT, "0");
                                cv.put(CartSchema.VARIENT_STRING, commaSeparatedString());
                                cv.put(CartSchema.PRODUCT_VARIENT, converttostring(varhashmap));
                                cv.put(CartSchema.KEY_CREATED, formatDatabase.format(date));
                                Uri uri = getActivity().getContentResolver().insert(ChatProvider.CART_URI, cv);
                                Log.d("Is three ", "IN " + uri.toString());
                                Toast.makeText(getActivity(), "Item Added to Cart", Toast.LENGTH_SHORT).show();
                                //      startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                                setNotifCount();
                                updateonlineCart();
                            }

                            else
                            {
                                Toast.makeText(getActivity(),"Please select all the variants",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    /**** ISTIAQUE: CODE BEGINS *****/
                    if(cursor != null){
                        cursor.close();
                    }
                    /**** ISTIAQUE: CODE ENDS *****/
                }


                break;

            case R.id.book_order_cart:

                if (amount < moq)
                {
                    new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.out_of_stock));
                }

                else
                {
                    Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.PRODUCT_ID + "=?", new String[]{product_id}, null);
                    if (cursor.getCount() > 0)
                    {
                        new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.already_in_cart), getResources().getString(R.string.product_already_cart));
                    }

                    else
                    {
                        if (zeroAction.equals("R") && product_sp == 0)
                        {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.not_allowed));
                        }

                        else
                        {
                            if(namehashmap.size()==varientsAdapter.getItemCount()) {
                            Date date = Calendar.getInstance().getTime();
                            Log.d("Is three ", "IN ");
                            ContentValues cv = new ContentValues();
                            cv.put(CartSchema.PRODUCT_NAME, productName.getText().toString());
                            cv.put(CartSchema.PRODUCT_ID, product_id);
                            cv.put(CartSchema.PRODUCT_CODE, product_qty.getText().toString());
                            cv.put(CartSchema.PRODUCT_PRICE, product_sp);
                            cv.put(CartSchema.PRODUCT_URL, productUrl);
                            cv.put(CartSchema.PRODUCT_SHIPPING, freeShipping);
                            cv.put(CartSchema.PRODUCT_SHIPPING_CHARGES, shippingCharge);
                            cv.put(CartSchema.PRODUCT_STATUS, 1);
                            if (moq == 0) {
                                moq = 1;
                            }
                            cv.put(CartSchema.PRODUCT_QTY, moq);
                            cv.put(CartSchema.PRODUCT_INVENTORY, amount);
                            cv.put(CartSchema.PRODUCT_MOQ, moq);
                            cv.put(CartSchema.DISCOUNT,cart_discount);
                            cv.put(CartSchema.PAY_METHOD,pay_method);
                            cv.put(CartSchema.PRODUCT_WEIGHT,product_Weight);
                            cv.put(CartSchema.PRODUCT_CAT_ID,catid);
                            cv.put(CartSchema.CART_PROMO,couponjson);
                            cv.put(CartSchema.CART_PROMO_APPLIED,"0");
                            cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE,"0");
                            cv.put(CartSchema.PROMO_MIN_QTY,"0");
                            cv.put(CartSchema.PROMO_MAX_AMT,"0");
                            cv.put(CartSchema.VARIENT_STRING,commaSeparatedString());
                            cv.put(CartSchema.PRODUCT_VARIENT,converttostring(varhashmap));
                            cv.put(CartSchema.KEY_CREATED, formatDatabase.format(date));
                            Uri uri = getActivity().getContentResolver().insert(ChatProvider.CART_URI, cv);
                            Log.d("Is three ", "IN " + uri.toString());
                            Toast.makeText(getActivity(),"Item Added to Cart",Toast.LENGTH_SHORT).show();
                            //      startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                            setNotifCount();
                            updateonlineCart();}else {
                                Toast.makeText(getActivity(),"Please select all the variants",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    /**** ISTIAQUE: CODE BEGINS *****/
                    if(cursor != null){
                        cursor.close();
                    }
                    /**** ISTIAQUE: CODE ENDS *****/
                }


                break;


            case R.id.out_network_buy:

                if (amount < moq) {
                    new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.out_of_stock));
                } else {
                    Cursor cursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, null, CartSchema.PRODUCT_ID + "=?", new String[]{product_id}, null);
                    if (cursor.getCount() > 0) {
                        ContentValues cv = new ContentValues();
                        cv.put(CartSchema.PRODUCT_VARIENT,converttostring(varhashmap));
                        cv.put(CartSchema.VARIENT_STRING, commaSeparatedString());
                        getActivity().getContentResolver().update(ChatProvider.CART_URI,cv,CartSchema.PRODUCT_ID + "=?",new String[]{product_id});

                        startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                        //new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.already_in_cart), getResources().getString(R.string.product_already_cart));
                    } else {
                        if (zeroAction.equals("R") && product_sp == 0) {
                            new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.oops), getResources().getString(R.string.not_allowed));
                        } else {
                            if(namehashmap.size()==varientsAdapter.getItemCount()) {

                                Date date = Calendar.getInstance().getTime();
                                Log.d("Is three ", "IN ");
                                ContentValues cv = new ContentValues();
                                cv.put(CartSchema.PRODUCT_NAME, productName.getText().toString());
                                cv.put(CartSchema.PRODUCT_ID, product_id);
                                cv.put(CartSchema.PRODUCT_CODE, product_qty.getText().toString());
                                cv.put(CartSchema.PRODUCT_PRICE, product_sp);
                                cv.put(CartSchema.PRODUCT_URL, productUrl);
                                cv.put(CartSchema.PRODUCT_SHIPPING, freeShipping);
                                cv.put(CartSchema.PRODUCT_SHIPPING_CHARGES, shippingCharge);
                                cv.put(CartSchema.PRODUCT_STATUS, 1);
                                if (moq == 0) {
                                    moq = 1;
                                }

                                cv.put(CartSchema.PRODUCT_QTY, moq);
                                cv.put(CartSchema.PRODUCT_INVENTORY, amount);
                                cv.put(CartSchema.PRODUCT_MOQ, moq);
                                cv.put(CartSchema.DISCOUNT, cart_discount);
                                cv.put(CartSchema.PAY_METHOD, pay_method);
                                cv.put(CartSchema.PRODUCT_WEIGHT, product_Weight);
                                cv.put(CartSchema.PRODUCT_CAT_ID, catid);
                                cv.put(CartSchema.CART_PROMO, couponjson);
                                cv.put(CartSchema.CART_PROMO_APPLIED, "0");
                                cv.put(CartSchema.PRODUCT_DISCOUNT_PRICE, "0");
                                cv.put(CartSchema.PROMO_MIN_QTY, "0");
                                cv.put(CartSchema.PROMO_MAX_AMT, "0");
                                cv.put(CartSchema.VARIENT_STRING, commaSeparatedString());
                                cv.put(CartSchema.PRODUCT_VARIENT, converttostring(varhashmap));
                                cv.put(CartSchema.KEY_CREATED, formatDatabase.format(date));
                                Uri uri = getActivity().getContentResolver().insert(ChatProvider.CART_URI, cv);
                                Log.d("Is three ", "IN " + uri.toString());


                                /*******************************ISTIAQUE***************************************/
                                application = (Controller) getActivity().getApplication();
                                mTracker = application.getDefaultTracker();
                                application.trackEvent("PDP", "Move", "CartActivity");
                                /*******************************ISTIAQUE***************************************/
                                startActivity(new Intent(new Intent(getActivity(), CartActivity.class)));
                            }
                                else{
                                Toast.makeText(getActivity(),"Please select all the variants",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    /**** ISTIAQUE: CODE BEGINS *****/
                    if(cursor != null){
                        cursor.close();
                    }
                    /**** ISTIAQUE: CODE ENDS *****/
                }


                break;

            case R.id.id_sellername:
                if (cd.isConnectingToInternet())
                {
                    if (seller_id != null) {
                        startActivity(new Intent(getActivity(), SellerProfile.class)
                                .putExtra("userid", seller_id)
                                .putExtra("username", seller_name)
                                .putExtra("company_id", seller_comp_id)
                                .putExtra("BackFlag", "product")
                                .putExtra("product_id", product_id)
                                .putExtra("title", title));
                        getActivity().finish();
                    } else
                        new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.seller_not_found));
                } else {
                    new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.oops), getString(R.string.no_internet_conn));
                }
                break;
        }

    }


    class GetProduct extends AsyncTask<Void, Void, Void>
    {
        int flag = 0;
        List<NameValuePair>params = new ArrayList<>();
        String base64 ="";

       // byte[] data = converttostring(varhashmap).getBytes("UTF-8");

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            if(namehashmap.size()==varientsAdapter.getItemCount())
            {
                byte[] data = new byte[0];
                try {
                    data = converttostring(varhashmap).getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //base64 = Base64.encodeToString(data, Base64.URL_SAFE);
                base64 = converttostring(varhashmap);
            }
            if (getActivity() != null && !getActivity().isFinishing() && isAdded())
                progress.show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            UserFunctions userFunctions = new UserFunctions();
            if (getActivity() != null && !getActivity().isFinishing() && isAdded())
            {
                try
                {
                    jsonObject = userFunctions.getProduct(product_id, cur_user_id, getActivity(),var_ids, URLEncoder.encode(base64,"UTF-8"));

                    if (jsonObject != null)
                    {
                        Log.d("Product Detail ", jsonObject.toString());

                        if (!jsonObject.getString("promotion").equalsIgnoreCase(""))
                        {
                            JSONObject object = jsonObject.getJSONObject("promotion");
                            JSONObject object1 = object.getJSONObject("bonuses");
                            discount_method= object1.getString("discount_bonus");
                            dis = object1.getString("discount_value");
                        }

                        else
                        {
                            cart_discount = "0";
                        }
                        pay_method = jsonObject.getString("cod");

                        if (jsonObject.has("main_pair"))
                        {
                            path.clear();
                            if (jsonObject.get("main_pair") instanceof JSONObject) {
                                if (jsonObject.getJSONObject("main_pair").has("detailed"))
                                    path.add(jsonObject.getJSONObject("main_pair").getJSONObject("detailed").getString("http_image_path"));
                                if (jsonObject.getJSONObject("main_pair").has("icon"))
                                    path.add(jsonObject.getJSONObject("main_pair").getJSONObject("icon").getString("http_image_path"));
                                productUrl = jsonObject.getJSONObject("main_pair").getJSONObject("detailed").getString("http_image_path");
                            }
                        }

                        if (jsonObject.has("image_pairs"))
                        {
                            if (jsonObject.get("image_pairs") instanceof JSONObject) {
                                JSONObject obj = jsonObject.getJSONObject("image_pairs");
                                Iterator<?> keys = obj.keys();
                                while (keys.hasNext()) {
                                    String key = (String) keys.next();
                                    JSONObject js = obj.getJSONObject(key);
                                    JSONObject detailObj = js.getJSONObject("detailed");
                                    Log.d("Image Path", detailObj.getString("http_image_path"));
                                    path.add(detailObj.getString("http_image_path"));
                                }
                            }
                        }

                        if (jsonObject.has("error")) {
                            flag = 2;
                        }

                    } else {
                        flag = 1;
                    }

                } catch (JSONException | NullPointerException e) {

                    e.printStackTrace();
                    Log.e("Exception", e.getMessage());

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else {
                flag = 1;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                progress.dismiss();
                if (flag == 0)
                {
                    try {
                        if (path.size() == 0) {
                            path.add("");
                        }
                        //adapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();
                        if (path.size() < 2)
                        titleIndicator.setVisibility(View.GONE);
                        productid = jsonObject.getString("product_id");
                        productName.setText(jsonObject.getString("product"));
                        productAmount = jsonObject.getString("price");
                        String strmrp = "\u20B9" + String.valueOf((int) Double.parseDouble(jsonObject.getString("list_price"))) + "/pc";
                        mrp.setText(strmrp);
                        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        sp.setText("\u20B9" + String.valueOf((int) Double.parseDouble(jsonObject.getString("price"))) + "/pc");
                        if (jsonObject.getInt("min_qty") > 0)
                            quantity.setText("MOQ : " + jsonObject.getString("min_qty"));
                        else
                            quantity.setText("MOQ : " + 1);
//                        if (jsonObject.has("category_tree")) {
//                            category.setText(jsonObject.getString("category_tree"));
//                        }
                        amount = jsonObject.getInt("amount");
                        moq = jsonObject.getInt("min_qty");
                        product_sp = jsonObject.getLong("price");
                        product_mrp = jsonObject.getLong("list_price");
                        product_qty.setText(jsonObject.getString("product_code"));
                        //shippingCharge = jsonObject.optString("shipping_freight");
                        /*shippingCharge = jsonObject.optString("shipping_cost");commented out by ISTIAQUE*/
                        shippingCharge = jsonObject.optString("shipPrice");
                        catid = String.valueOf(jsonObject.getInt("main_category"));
                        Log.d("shippingCharge", jsonObject.toString());
                        product_Weight = jsonObject.optString("productWeight");
                        if(jsonObject.has("discount_quote"))
                        {
                            String discountprice = jsonObject.optString("discount_price");
                            String discountstring = jsonObject.optString("discount_quote");
                            d_price.setText("\u20B9"+discountprice);
                            d_string.setText(discountstring);
                            sp.setPaintFlags(sp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            d_price.setVisibility(View.VISIBLE);
                            d_string.setVisibility(View.VISIBLE);
                        }
                        freeShipping = jsonObject.optString("free_shipping");
                        zeroAction = jsonObject.optString("zero_price_action");

                        //-------------------------akshay----------------------------------//
                        if (jsonObject.has("cartpromotions"))
                        {
                            couponList.clear();
                            coupon_title.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("cartpromotions");
                            couponjson = jsonArray.toString();
                            for (int m = 0; m < jsonArray.length(); m++)
                            {
                                JSONObject obj = (JSONObject) jsonArray.get(m);
                                CouponModal couponModal = new CouponModal();
                                couponModal.setCoupondis(obj.getString("discription"));
                                couponModal.setCouponName(obj.getString("coupons"));
                                //couponModal.setDiscountType(obj.getString("discount_bonus"));
                                //couponModal.setDiscountvalue(obj.getString("discount_value"));
                                //couponModal.setMinamount(obj.getString("min_bonous_qty"));
                                //Toast.makeText(getActivity(),obj.getJSONObject("bonous").getString("min_bonous_qty"),Toast.LENGTH_SHORT).show();
                                //couponModal.setMaxamount(obj.getJSONObject("bonous").getString("max_bonous_amount"));
                                couponModal.setTcDes(obj.getString("term_condition"));
/*

                                RadioButton radioButton = new RadioButton(getActivity());
                                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                radioButton.setId(m);
                                radioButton.setText("(" + obj.getString("coupons") + ") " + obj.getString("discription"));
                                radioButton.setTextColor(Color.BLACK);
                                radioGroup.addView(radioButton);
*/
                                couponList.add(couponModal);
                            }
                        }
                        if(jsonObject.has("option_amount"))
                        {
                            Log.d("optionamount",jsonObject.getString("option_amount"));
                                amount= Integer.parseInt(jsonObject.getString("option_amount"));
                        }

                        if(jsonObject.has("options"))
                        {
                            varientsModals.clear();
                            rv_varients.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("options");
                            Log.d("recylerview",jsonArray.toString());
                            for (int m1 = 0; m1 < jsonArray.length(); m1++)
                            {
                                JSONObject obj = (JSONObject) jsonArray.get(m1);
                                VarientsModal varientsModal = new VarientsModal();
                                varientsModal.setVar_name(obj.getString("option_name"));
                                varientsModal.setVar_json(obj.getString("variants"));
                                varientsModals.add(varientsModal);
                            }
                        }


                        //--------------------------akshay----------------------------------//

                      /*  if (!jsonObject.getString("promotion").equalsIgnoreCase(""))
                        {
                            if ("by_fixed".equalsIgnoreCase(discount_method))
                            {
                                cart_discount = (int)Double.parseDouble(dis);
                            }
                            else
                            {
                                cart_discount = (int) ((Double.parseDouble(productAmount) * Double.parseDouble(dis))/100);
                            }
                        }*/


                        if(cart_discount.equals("0"))
                        {
                            txt_discount.setVisibility(View.GONE);
                        }
                        else
                        {
                           // txt_discount.setText(getString(R.string.rs)+cart_discount+ " Per Piece Additional Discount");

                        }

                        if (freeShipping.equalsIgnoreCase("N"))
                        {
                            /*txtDelivers.setText("+" + getString(R.string.rs) + shippingCharge + " " + getString(R.string.delevery));COMMENTED BY ISTIAQUE*/
                            txtDelivers.setText("+ " + getString(R.string.rs) + shippingCharge + "/pc. Shipping " + "approx.");
                        } else {
                            txtDelivers.setText(getString(R.string.free_delevery));
                        }
                        if (jsonObject.getJSONArray("user_details").length() > 0)
                            sellerInfo.setText(MyTextUtils.toTitleCase(jsonObject.getJSONArray("user_details").getJSONObject(0).getString("name")) + (jsonObject.has("company_name") ? ", " + jsonObject.getString("company_name").toUpperCase() : ""));
                        else
                            sellerInfo.setText((jsonObject.has("company_name") ? jsonObject.getString("company_name").toUpperCase() : ""));
                        if (jsonObject.getString("is_favourite").equalsIgnoreCase("1")) {
                            fav_image.setImageResource(R.drawable.like_selected);
                            fav_flag = true;
                        } else {
                            fav_image.setImageResource(R.drawable.like_unselected);
                            fav_flag = false;
                        }
                        if (jsonObject.has("price_share")) {
                            if (jsonObject.getString("price_share").equalsIgnoreCase("1"))
                                Chat_about_product = "Y";

                        }
                        if (amount == 0 || amount < moq) {
                            if (Chat_about_product.equalsIgnoreCase("Y")) {
                                //   buyLinerar.setVisibility(View.GONE);
                                outstock.setVisibility(View.VISIBLE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                                mrp.setVisibility(View.GONE);
                                ll121.setVisibility(View.GONE);
                                ll122.setVisibility(View.GONE);
                                sp.setVisibility(View.GONE);
                                chat_about.setVisibility(View.VISIBLE);
                                txtDelivers.setVisibility(View.GONE);
                            } else {
                                outstock.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                                chat_about.setVisibility(View.GONE);
                                out_stock_bottom.setVisibility(View.VISIBLE);
                            }


                        }

                        else
                        {
                            if (Chat_about_product.equalsIgnoreCase("Y"))
                            {
                                mrp.setVisibility(View.GONE);
                                ll121.setVisibility(View.GONE);
                                ll122.setVisibility(View.GONE);
                                sp.setVisibility(View.GONE);
                                chat_about.setVisibility(View.VISIBLE);
                                buyNow.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                txtDelivers.setVisibility(View.GONE);

                            }

                            else
                            {
                                chat_about.setVisibility(View.GONE);
                                mrp.setVisibility(View.VISIBLE);
                                sp.setVisibility(View.VISIBLE);
                                buyNow.setVisibility(View.VISIBLE);
                                book_order.setVisibility(View.VISIBLE);
                            }

                        }
                        if (jsonObject.getJSONArray("user_details").length() > 0)
                        {
                            user_id = jsonObject.getJSONArray("user_details").getJSONObject(0).getString("user_login");
                            seller_id = jsonObject.getJSONArray("user_details").getJSONObject(0).getString("user_id");
                            seller_comp_id = jsonObject.getString("company_id");
                            seller_name = jsonObject.getJSONArray("user_details").getJSONObject(0).getString("name");

                            /***************** ISTIAQUE: CODE STARTS *************************************/

                            String feature = null;

                            /*if (jsonObject.getJSONObject("product_features") instanceof JSONObject) {
                                JSONObject product_features = jsonObject.getJSONObject("product_features");
                            } else {
                                JSONArray ja = (JSONArray) obj;
                            }*/

                            try {
                                // you have json object
                                JSONObject product_features = jsonObject.getJSONObject("product_features");
                                Log.d("Length",product_features.length()+"");
                                if (product_features.length() == 0) {
                                    Log.d("ja.length()", product_features.length()+"");
                                    feature_title.setVisibility(View.GONE);
                                    //productFeature.setVisibility(View.GONE);
                                } else {
                                    feature_title.setVisibility(View.VISIBLE);
                                    //productFeature.setVisibility(View.VISIBLE);
                                }
                                Iterator<String> iter = product_features.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    Log.d("KEY", key);
                                    try {
                                         String description = null, variant = null, prefix = null,suffix = null, value = null;
                                        //Object value = product_features.get(key);
                                        JSONObject features = product_features.getJSONObject(key);

                                        String feature_type = features.getString("feature_type");

                                        if (feature_type.equals("E")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("S")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("N")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("M")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("C")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("T")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("O")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        } else if (feature_type.equals("D")){
                                            description = features.getString("description");
                                            variant = features.getString("variant");
                                            prefix = features.getString("prefix");
                                            suffix = features.getString("suffix");
                                            value = features.getString("value");
                                        }
                                        feature = value + "\n" + description + "\n" + variant + "\n" + prefix + "\n" + suffix;
                                        Log.d("ABC", feature);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e){
                                try {
                                    // you have json array
                                    Log.d("product_features", "product_features");
                                    JSONArray ja = jsonObject.getJSONArray("product_features");
                                    if (ja.length() == 0) {
                                        Log.d("ja.length()", ja.length()+"");
                                        feature_title.setVisibility(View.GONE);
                                        productFeature.setVisibility(View.GONE);
                                    } else {
                                        feature_title.setVisibility(View.VISIBLE);
                                        productFeature.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException je){
                                    je.printStackTrace();
                                }
                            }

                            /***************** ISTIAQUE CODE ENDS *************************************/
                            String productDesc = jsonObject.getString("full_description");

                            if (productDesc.equalsIgnoreCase(""))
                            {
                                productDesc = jsonObject.getString("product");
                            }

                            //Spannable wordtoSpan = new SpannableString("Product Description : " + productDesc);
                            //wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            /*productDescription.setText(wordtoSpan.toString().replaceAll("null", ""));*/
                            productDescription.setText(productDesc);

                            //Spannable featuretoSpan = new SpannableString("Product Feature : " + feature);
                            //featuretoSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            /*productFeature.setText(featuretoSpan.toString().replaceAll("null", ""));*/
                            productFeature.setText(feature);

                            if (jsonObject.getString("company_id").equalsIgnoreCase(user_comp_id)) {
                                fav_image.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                            }
                            if (jsonObject.getJSONArray("user_details").getJSONObject(0).getString("user_id").equalsIgnoreCase(cur_user_id)) {
                                chatseller.setVisibility(View.GONE);
                                chat_about.setVisibility(View.GONE);
                            }

                        }
                        else {
                            chatseller.setVisibility(View.GONE);
                            chat_about.setVisibility(View.GONE);
                            fav_image.setVisibility(View.GONE);

                        }


                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                } else if (flag == 1) {
                    alertDialog.setTitle(getResources().getString(R.string.sorry)).setMessage(getResources().getString(R.string.server_error)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (flag == 2) {
                    try {
                        alertDialog.setTitle(getResources().getString(R.string.error)).
                                setMessage(jsonObj.getString("error")).
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    alertDialog.show();
                }


                super.onPostExecute(aVoid);
                Log.d("compIDList", compIDlist.toString());
                try {
                    if (jsonObject.getString("company_id").equalsIgnoreCase(user_comp_id)) {
                        fav_image.setVisibility(View.GONE);
                        book_order.setVisibility(View.GONE);
                        buyNow.setVisibility(View.GONE);
                        chat_about.setVisibility(View.GONE);
                        outstock.setVisibility(View.GONE);
                        book_order.setVisibility(View.GONE);
                        buyNow.setVisibility(View.GONE);
                        mrp.setVisibility(View.GONE);
                        ll121.setVisibility(View.GONE);
                        ll122.setVisibility(View.GONE);
                        sp.setVisibility(View.GONE);
                        chat_about.setVisibility(View.GONE);
                        out_network_ll.setVisibility(View.GONE);
                        nll.setVisibility(View.GONE);
                    }
                    else
                    {
                    if( compIDlist.contains(seller_comp_id))
                    {
                        if (amount == 0 || amount < moq) {
                            if (Chat_about_product.equalsIgnoreCase("Y"))
                            {
                                //buyLinerar.setVisibility(View.GONE);
                                outstock.setVisibility(View.VISIBLE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                                mrp.setVisibility(View.GONE);
                                ll121.setVisibility(View.GONE);
                                ll122.setVisibility(View.GONE);
                                sp.setVisibility(View.GONE);
                                chat_about.setVisibility(View.VISIBLE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.GONE);
                            } else {
                                outstock.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                                chat_about.setVisibility(View.GONE);
                                out_stock_bottom.setVisibility(View.VISIBLE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.GONE);
                            }


                        }

                        else
                        {
                            if (Chat_about_product.equalsIgnoreCase("Y"))
                            {
                                mrp.setVisibility(View.GONE);
                                ll121.setVisibility(View.GONE);
                                ll122.setVisibility(View.GONE);
                                sp.setVisibility(View.GONE);
                                chat_about.setVisibility(View.VISIBLE);
                                buyNow.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.GONE);

                            } else {
                                chat_about.setVisibility(View.GONE);
                                mrp.setVisibility(View.VISIBLE);
                                sp.setVisibility(View.VISIBLE);
                                buyNow.setVisibility(View.VISIBLE);
                                book_order.setVisibility(View.VISIBLE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.VISIBLE);
                            }





    //                        Toast.makeText(getActivity(),"user present in network",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        //Toast.makeText(getActivity(),"user not  present in network",Toast.LENGTH_SHORT).show();
                        if (amount == 0 || amount < moq) {
                            if (Chat_about_product.equalsIgnoreCase("Y")) {
                                //   buyLinerar.setVisibility(View.GONE);
                                outstock.setVisibility(View.VISIBLE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                                mrp.setVisibility(View.GONE);
                                ll121.setVisibility(View.GONE);
                                ll122.setVisibility(View.GONE);
                                sp.setVisibility(View.GONE);
                                chat_about.setVisibility(View.VISIBLE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.GONE);
                            } else {
                                outstock.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                buyNow.setVisibility(View.GONE);
                                chat_about.setVisibility(View.GONE);
                                out_stock_bottom.setVisibility(View.VISIBLE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.GONE);
                            }


                        } else {
                            if (Chat_about_product.equalsIgnoreCase("Y")) {
                                mrp.setVisibility(View.GONE);
                                ll121.setVisibility(View.GONE);
                                ll122.setVisibility(View.GONE);
                                sp.setVisibility(View.GONE);
                                chat_about.setVisibility(View.VISIBLE);
                                buyNow.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                out_network_ll.setVisibility(View.GONE);
                                nll.setVisibility(View.GONE);

                            } else {
                                chat_about.setVisibility(View.GONE);
                                mrp.setVisibility(View.VISIBLE);
                                sp.setVisibility(View.VISIBLE);
                                buyNow.setVisibility(View.GONE);
                                book_order.setVisibility(View.GONE);
                                out_network_ll.setVisibility(View.VISIBLE);
                                nll.setVisibility(View.GONE);
                            }

                        }
                    }}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

           adapter.notifyDataSetChanged();
            couponAdapter.notifyDataSetChanged();
            varientsAdapter.notifyDataSetChanged();
        }
    }

    private class LikeQuery extends AsyncTask<String, String, JSONObject> {
        public String error = "", argument, pid = "";
        public int flag = 0;
        Boolean success = false;
        JSONObject table = new JSONObject();
        //deepesh

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null && !getActivity().isFinishing() && isAdded())
                progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            try {
                Log.i("Prodict det", "object_id " + args[0] + "/ " + " args[1] " + args[1] + " / user_id" + cur_user_id);
                argument = args[1];
                pid = args[0];
                table.put("user_id", cur_user_id);
                table.put("object_id", args[0]);
                table.put("object_type", "product");
                if (args[1] == "1")
                    table.put("unlike", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = userFunction.LikeRequest("POST", table, "Query", getActivity());
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
                            flag = 2;
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
            if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                progress.dismiss();
                if (flag == 1) {

                    alertDialog.setTitle(getResources().getString(R.string.sorry)).setMessage(getResources().getString(R.string.server_error)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else if (flag == 2) {

                    alertDialog.setTitle(getResources().getString(R.string.error)).
                            setMessage(getResources().getString(R.string.page_not_found)).
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    alertDialog.show();
                } else {
                    if (success) {
                        getActivity().setResult(Activity.RESULT_OK);
                        if (argument.equalsIgnoreCase("1")) {
                            fav_image.setImageResource(R.drawable.like_unselected);
                            Toast.makeText(getActivity(), getActivity().getString(R.string.remove_fav), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.added_favs), Toast.LENGTH_LONG).show();
                            fav_image.setImageResource(R.drawable.like_selected);
                        }
                    }
                }
            }
        }
    }


    private class ImagePagerAdapter extends PagerAdapter
    {
        private ArrayList<String> images;
        private LayoutInflater inflater;

        ImagePagerAdapter(ArrayList<String> images)
        {
            this.images = images;
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount()
        {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position)
        {

            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            final TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image_zoom);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            if (images.size() == 0)
            {
                pagerLoader.displayImage("", imageView);
            }
            else
            {
                pagerLoader.displayImage(images.get(position), imageView, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                spinner.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                String message = null;
                                switch (failReason.getType()) {
                                    case IO_ERROR:
                                        message = "Input/Output error";
                                        break;
                                    case DECODING_ERROR:
                                        message = "Image can't be decoded";
                                        break;
                                    case NETWORK_DENIED:
                                        message = "Downloads are denied";
                                        break;
                                    case OUT_OF_MEMORY:
                                        message = "Out Of Memory error";
                                        break;
                                    case UNKNOWN:
                                        message = "Unknown error";
                                        break;
                                    default:
                                        message = "Unknown error";
                                        break;
                                }

                                if (getActivity() != null)
                                    Toast.makeText(getActivity().getApplicationContext(), message,
                                            Toast.LENGTH_SHORT).show();

                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                spinner.setVisibility(View.GONE);
                            }
                        });
            }


            view.addView(imageLayout, 0);
            //notifyDataSetChanged();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] stockArr = new String[images.size()];
                    stockArr = images.toArray(stockArr);
                    /*startActivity(new Intent(getActivity(), ImageViewer.class).putExtra("url", stockArr).putExtra("pos", position));*/
                    Log.i("IMAGEARRAY **  ", stockArr[0]);
                    if (stockArr.length == 1 && stockArr[0].equalsIgnoreCase("")) {

                    } else {
                        startActivity(new Intent(getActivity(), ImageViewer.class).putExtra("url", stockArr).putExtra("pos", position));
                    }
                }
            });
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    private void prepareOrder(String user_id, HashMap<String, Products> map)
    {
        progress.show();
        MakeOrder create = new MakeOrder(map, user_id, AppUtil.PAYMENT_COD,"1");
        Gson gson = new Gson();
        Log.i("create--", gson.toJson(create));
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<String> call = service.bookMyOrders(create, tokan, "application/json", "application/json");
        call.enqueue(new Callback<String>() {
                         @Override
                         public void onResponse(Response response) {
                             Log.d("Raw", "" + response.raw());

                             progress.dismiss();
                             if (response.isSuccess()) {
                                 new AlertDialogManager().showAlertDialog(getActivity(), getResources().getString(R.string.success), getResources().getString(R.string.order_plced_success));

                             } else {
                                 int statusCode = response.code();
                                 //}
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
                                 }
                                 // if(error2.equalsIgnoreCase(""))
                                 String error2 = getString(R.string.server_error);
                                 if (statusCode == 404) {
                                     //  Toast.makeText(AddressList.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                                     new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), error2);
                                 } else if (statusCode == 500) {
                                     new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), error2);
                                     //   Toast.makeText(AddressList.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                 } else {
                                     //new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), error2);
                                     // Toast.makeText(AddressList.this, , Toast.AddAddress).show();
                                     if (getActivity() != null && !getActivity().isFinishing()) {
                                         new AlertDialogManager().showAlertDialog(getActivity(),
                                                 getString(R.string.error),
                                                 getString(R.string.server_error));
                                     }
                                 }
                             }
                             // }
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
                     }

        );
    }

    private void getPrimary(String companyId) {
        if (!getActivity().isFinishing())
        {
            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getPrimary(companyId, "1", helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();

                    if (response.isSuccess()) {
                        JsonElement element = (JsonElement) response.body();
                        JSONObject json = null;
                        /*try {
                            json = new JSONObject(element.getAsJsonObject().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        if (element != null && element.isJsonObject()) {
                            try {
                                json = new JSONObject(element.getAsJsonObject().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (json != null && json.has("users"))
                        {
                            try
                            {
                                chat_user_id = json.getJSONObject("users").optString("user_id");
                                Log.i("chat_user_id", chat_user_id);
                                Cursor cursor = getActivity().getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_ID + "=?", new String[]{chat_user_id}, null);
                                cursor.moveToNext();
                                Log.d("cursor coumt", String.valueOf(cursor.getCount()));
                                if (cursor.getCount() > 0) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", "" + productName.getText().toString());
                                    bundle.putString("code", "" + product_qty.getText().toString());
                                    if (Chat_about_product.equalsIgnoreCase("Y")) {
                                        bundle.putString("mrp", "");
                                        bundle.putString("price", "");
                                    } else {
                                        String marketPrice = String.format("%.2f", Double.parseDouble(product_mrp.toString()));
                                        String sellingPrice = String.format("%.2f", Double.parseDouble(product_sp.toString()));
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

                                    bundle.putString("url", "" + productUrl);
                                    bundle.putString("moq", "" + moq);
                                    getActivity().startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("user", "" + user_id + "@" + AppUtil.SERVER_NAME).putExtra("from", 101).putExtra("result", bundle));
                                    //startActivity(new Intent(getActivity().getApplicationContext(), ChatActivity.class).putExtra("user", "" + user_id + "@" + XmppConnection.SERVER_NAME));

                                } else {
                                    Log.d("check","init get user details");
                                    getUserDetails(chat_user_id);

                                }
                                cursor.close();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            new AlertDialogManager().showAlertDialog(getActivity(),
                                    getString(R.string.sorry),
                                    getString(R.string.user_not_exist));
                        }

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
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.server_error));
                        }

                    }


                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();

                    if (getActivity() != null && !getActivity().isFinishing())
                        new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.server_error));

                }
            });
        }


    }

    private void getUserDetails(String userId)
    {
        if (!getActivity().isFinishing())
        {
            final Calendar c = Calendar.getInstance();
            flag = 0;
            progress.show();
            RestClient.GitApiInterface service = RestClient.getClient();
            Call<JsonElement> call = service.getUserDetails(userId, helper.getB64Auth(getActivity()), "application/json", "application/json");
            call.enqueue(new Callback<JsonElement>()
            {
                @Override
                public void onResponse(Response response) {
                    progress.dismiss();
                    if (response.isSuccess()) {
                        String userid = "";
                        JsonElement element = (JsonElement) response.body();
                        JSONObject json = null;
                        try {
                            json = new JSONObject(element.getAsJsonObject().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("JSS", json.toString());
                        if (json != null)
                        {

                            if (json.has("user_id") && json.optString("is_openfire").equalsIgnoreCase("1")) {
                                try {
                                    ContentValues cv = new ContentValues();
                                    JSONObject jsonObject = new JSONObject();
                                    if (json.has("phone"))
                                        cv.put(NetSchema.USER_PHONE, json.getString("phone"));
                                        jsonObject.put(NetSchema.USER_PHONE,json.getString("phone"));
                                    if (json.has("company_name"))
                                        cv.put(NetSchema.USER_COMPANY, json.getString("company_name"));
                                        jsonObject.put(NetSchema.USER_COMPANY, json.getString("company_name"));

                                    cv.put(NetSchema.USER_DISPLAY, " ");
                                    jsonObject.put(NetSchema.USER_DISPLAY, " ");
                                    if (json.has("company_id"))
                                        cv.put(NetSchema.USER_COMPANY_ID, json.getString("company_id"));
                                    jsonObject.put(NetSchema.USER_COMPANY_ID, json.getString("company_id"));
                                    if (json.has("user_id"))
                                        cv.put(NetSchema.USER_ID, json.getString("user_id"));
                                    jsonObject.put(NetSchema.USER_ID, json.getString("user_id"));
                                    if (json.has("user_login"))
                                    {
                                        userid = json.getString("user_login") + "@" + AppUtil.SERVER_NAME;
                                        cv.put(NetSchema.USER_NET_ID, userid);
                                        jsonObject.put(NetSchema.USER_NET_ID, userid);
                                    }
                                    cv.put(NetSchema.USER_STATUS, "0");
                                    cv.put(NetSchema.USER_NAME, helper.ConvertCamel(json.getString("firstname")) + " " + helper.ConvertCamel(json.getString("lastname")));
                                    cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));
                                    jsonObject.put(NetSchema.USER_STATUS, "0");
                                    jsonObject.put(NetSchema.USER_NAME, helper.ConvertCamel(json.getString("firstname")) + " " + helper.ConvertCamel(json.getString("lastname")));
                                    jsonObject.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));
                                    Log.d("cv", cv.toString());
                                    Log.d("user json", jsonObject.toString());
                                    //----------------akshay code---------------//
                                   helper = new Helper().setDefaults("aki_user_details",jsonObject.toString(),getActivity());

                                    getActivity().getContentResolver().insert(ChatProvider.NET_URI, cv);
                                    Cursor cursor = getActivity().getContentResolver().query(ChatProvider.NET_URI, null, NetSchema.USER_NET_ID + "=?", new String[]{userid}, null);
                                    Log.d("aki cursorsize",String.valueOf(cursor.getCount()));
                                    if (cursor.getCount() > 0)
                                        flag = 3;
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
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.sorry), getString(R.string.server_error));

                        } else if (flag == 2) {
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.sorry), getString(R.string.user_not_exist));

                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", "" + productName.getText().toString());
                            bundle.putString("code", "" + product_qty.getText().toString());
                            if (Chat_about_product.equalsIgnoreCase("Y")) {
                                bundle.putString("mrp", "");
                                bundle.putString("price", "");
                            } else {
                                String marketPrice = String.format("%.2f", Double.parseDouble(product_mrp.toString()));
                                String sellingPrice = String.format("%.2f", Double.parseDouble(product_sp.toString()));
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

                            bundle.putString("url", "" + productUrl);
                            bundle.putString("moq", "" + moq);

                            getActivity().startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("user", "" + userid).putExtra("from", 101).putExtra("result", bundle));


                        }
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
                            if (getActivity() != null && !getActivity().isFinishing())
                                new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progress.dismiss();
                    if (getActivity() != null && !getActivity().isFinishing())
                        new AlertDialogManager().showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.server_error));

                }
            });
        }


    }

    /********************************  ISTIAQUE: CODE BEGINS *****************************************/

    /*@Override
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
            public void onClick(View v) {
                Controller application = (Controller) getActivity().getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");
                startActivity(new Intent(getActivity(), CartActivity.class));
            }
        });

        //MenuItem sitem = menu.findItem(R.id.searchCart_search);
        //sitem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
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
    }*/

    private void setNotifCount()
    {
        Cursor mCursor = getActivity().getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();

        /*if (mNotifCount >= 0) {
            ProductDetailsActivity activity = (ProductDetailsActivity) getActivity();
            TextView tv = (TextView) activity.findViewById(R.id.count);
            tv.setText(String.valueOf(mNotifCount));
        }*/

        ProductDetailsActivity activity = (ProductDetailsActivity) getActivity();
        if (mNotifCount == 0) {
            activity.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);

            TextView tv = (TextView) activity.findViewById(R.id.count);
            tv.setText(String.valueOf(mNotifCount));
        }

    }



    //--------------------------------AKSHAY CODE-----------------------------------------------------------------//

    public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder>
    {
        private ArrayList<CouponModal> orderItemses;
        public CouponAdapter(ArrayList<CouponModal> newsList)
        {
            this.orderItemses = newsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_pdp,parent,false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position)
        {
            final TextView a = holder.coupon_des;
            //final Button b = holder.btn_apply;
            final TextView tc = holder.tc;
            final Boolean y = Boolean.FALSE;
            a.setText(orderItemses.get(position).getCoupondis());
            if(orderItemses.get(position).getTcDes().equalsIgnoreCase(""))
            {
                tc.setVisibility(View.GONE);
            }

            tc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .create();
                    ad.setCancelable(false);
                    ad.setTitle("Terms and Conditions");
                    ad.setMessage(Html.fromHtml(orderItemses.get(position).getTcDes()).toString());
                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ad.dismiss();
                        }
                    });
                    ad.show();
                }
            });
            /*b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(!y)
                    {
                        String a1 = orderItemses.get(position).getDiscountType();
                        String b1 = orderItemses.get(position).getDiscountvalue();
                        cart_discount = checkdiscount(a1, b1);
                        b.setText("Applied");

                    }
                    else
                    {
                        b.setEnabled(false);
                    }
                }
            });
*/

        }

        @Override
        public int getItemCount()
        {
            return orderItemses.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView coupon_des,tc;
            Button btn_apply;
            public MyViewHolder(View itemView)
            {
                super(itemView);
                this.coupon_des = (TextView) itemView.findViewById(R.id.coupon_des_d);
                //this.btn_apply = (Button) itemView.findViewById(R.id.btn_apply);
                this.tc = (TextView) itemView.findViewById(R.id.tandc_d);
            }
        }
    }


    public class VarientsAdapter extends RecyclerView.Adapter<VarientsAdapter.MyViewHolder>
    {
        private ArrayList<VarientsModal>varientsModals = new ArrayList<>();
        Context context;
        VarientTypeAdapter adapter;

        public VarientsAdapter(ArrayList<VarientsModal>v,Context context)
        {
            this.context = context;
            this.varientsModals = v;
        }

        @Override
        public VarientsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.varients,parent,false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(VarientsAdapter.MyViewHolder holder, int position)
        {
            holder.title.setText(varientsModals.get(position).getVar_name());
            if(!varientsModals.get(position).getVar_json().isEmpty())
            {
                try
                {
                    JSONArray jsonArray = new JSONArray(varientsModals.get(position).getVar_json());
                    ArrayList<VarientsModal>varientsModals= new ArrayList<>();

                    for (int j = 0; j < jsonArray.length(); j++)
                    {
                        JSONObject obj = (JSONObject) jsonArray.get(j);
                        VarientsModal varientsModal = new VarientsModal();
                        varientsModal.setVar_type_name(obj.getString("variant_name"));
                        varientsModal.setVar_type_id(obj.getString("variant_id"));
                        varientsModal.setVar_option_id(obj.getString("option_id"));
                        varientsModals.add(varientsModal);
                    }
                    adapter = new VarientTypeAdapter(varientsModals,context);
                    holder.var_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    holder.var_rv.setAdapter(adapter);
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount()
        {
            return varientsModals.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView title;
            RecyclerView var_rv;

            public MyViewHolder(View itemView)
            {
                super(itemView);
                title = (TextView)itemView.findViewById(R.id.var_title);
                var_rv = (RecyclerView)itemView.findViewById(R.id.var_list);
            }
        }

        //-------------------------akshay----------------------------//
        public class VarientTypeAdapter extends RecyclerView.Adapter<VarientTypeAdapter.MyViewHolder2>
        {
            private ArrayList<VarientsModal>varientsModalArrayList = new ArrayList<>();
            private Context context;
            boolean l = false;
            public VarientTypeAdapter(ArrayList<VarientsModal>as,Context context)
            {
                this.varientsModalArrayList = as;
                this.context = context;
            }

            @Override
            public VarientTypeAdapter.MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType)
            {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.varient_val, parent, false);
                MyViewHolder2 myViewHolder2 = new MyViewHolder2(view);
                return myViewHolder2;
            }

            @Override
            public void onBindViewHolder(final VarientTypeAdapter.MyViewHolder2 holder, final int position)
            {
                holder.val.setText(varientsModalArrayList.get(position).getVar_type_name());
                if(varhashmap.containsValue(varientsModalArrayList.get(position).getVar_type_id()))
                {
                    holder.val.setBackgroundResource(R.drawable.ring_solid);
                    holder.val.setTextColor(Color.WHITE);
                    holder.val.setTypeface(null, Typeface.BOLD);
                    l=true;
                }
                else
                {
                    holder.val.setBackgroundResource(R.drawable.ring_outline);
                    holder.val.setTextColor(Color.BLACK);
                    holder.val.setTypeface(null, Typeface.BOLD);
                    l=false;
                }


                holder.val.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //List<NameValuePair>param = new ArrayList<NameValuePair>();
                        //param.add(new BasicNameValuePair("varientid",varientsModalArrayList.get(position).getVar_type_id()));
                        var_ids = varientsModalArrayList.get(position).getVar_type_id();
                        varhashmap.put(varientsModalArrayList.get(position).getVar_option_id(),varientsModalArrayList.get(position).getVar_type_id());

                        if(!varhashmap.containsKey(varientsModalArrayList.get(position).getVar_option_id()))
                        {
                            holder.val.setBackgroundResource(R.drawable.ring_solid);
                            holder.val.setTextColor(Color.WHITE);
                            holder.val.setTypeface(null, Typeface.BOLD);
                            l=true;
                        }

                        else
                        {
                            holder.val.setBackgroundResource(R.drawable.ring_outline);
                            holder.val.setTextColor(Color.BLACK);
                            holder.val.setTypeface(null, Typeface.BOLD);
                            l=false;
                        }
                        namehashmap.put(varientsModalArrayList.get(position).getVar_option_id(), varientsModalArrayList.get(position).getVar_type_name());
                        //Toast.makeText(getActivity(),commaSeparatedString(),Toast.LENGTH_SHORT).show();
                        new GetProduct().execute();
                       /* if(!l)
                        {
                            holder.val.setBackgroundResource(R.drawable.ring_solid);
                            holder.val.setTextColor(Color.WHITE);
                            holder.val.setTypeface(null, Typeface.BOLD);
                            l=true;
                        }
                        else{
                            holder.val.setBackgroundResource(R.drawable.ring_outline);
                            holder.val.setTextColor(Color.BLACK);
                            holder.val.setTypeface(null, Typeface.BOLD);
                            l=false;
                        }*/
                    }
                });
            }

            @Override
            public int getItemCount()
            {
                return varientsModalArrayList.size();
            }

            public class MyViewHolder2 extends RecyclerView.ViewHolder {
                TextView val;
                public MyViewHolder2(View itemView)
                {
                    super(itemView);
                    val = (TextView)itemView.findViewById(R.id.txt_var);
                }
            }
        }
    }

    public String converttostring(HashMap<String,String> a)
    {
        if(!a.isEmpty())
        {
        Gson objGson= new Gson();
        String strObject = objGson.toJson(a);
        Log.d("akijson",strObject);
        return strObject;
        }
        else
        {
            return "";
        }
    }

    public String commaSeparatedString()
    {
        StringBuilder commaSepValueBuilder = new StringBuilder();
        ArrayList<String>al = new ArrayList<>(namehashmap.values());
      //String[] a = (String[]) namehashmap.values().toArray();
        if(al.isEmpty())
        {
            return "";
        }
        else {
        for ( int i = 0; i<al.size(); i++)
        {
            //append the value into the builder
            commaSepValueBuilder.append(al.get(i));
            //if the value is not the last element of the list
            //then append the comma(,) as well

            if ( i != al.size()-1)
            {
                commaSepValueBuilder.append(", ");
            }
        }

      return commaSepValueBuilder.toString();}
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("ProductItemDetailsFragment:" + screenVisited);
        mTracker.enableAdvertisingIdCollection(true); // tracks user behaviour
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // Build and send an App Speed.
        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Product Page").setValue(System.currentTimeMillis() - startTime).build());
    }
}