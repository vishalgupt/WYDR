package wydr.sellers.registration;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.PrefManager;
import wydr.sellers.R;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.activities.AddProduct;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.Home;
import wydr.sellers.activities.TermsAndUse;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.slider.JSONParser;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.SuperSpinner;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by deepesh on 26/6/15.
 */
public class BuisDetail extends Fragment implements View.OnClickListener
{
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    public JSONArray jsonMainArr;
    public EditText shop, address, state, pan, city, tin, username, txtemail, txtpin, txtvat, txtAccName, txtaccno, txtbank, txtIfscCode;
    public SuperSpinner spinner;
    public RadioGroup scope, intrest, radiobankingdetails, radioaccounttype;
    public boolean flag = true;
    public ImageView mrkt_arw, buis_arw, imgPersonal;
    public int mrkktflag, buisflag = 0;
    public TextView radioText, sellerAgreement_tv;
    JSONObject jsonObject;
    JSONParser jsonParser = new JSONParser();
    PrefManager prefManager;
    ArrayList<String>ufList= new ArrayList<>();
    public LinearLayout linearbusiness, linearpersonal, linearadditional, linearaccountDetails;
    RadioButton radio_pub, radio_pri, radio_buy, radio_sell, radio_both, radio_Current, radio_Saving, radio_Seller, radio_NotSeller;
    String[] values;
    Button submit, skip;
    Helper helper;
    ConnectionDetector cd;
    String sellerBankingDetails = "", statusMsg = "", sellerAgreementMsg = "", term_url = "";
    public static String KYC_STATUS = "0";
    private ProgressDialog progress;
    Controller application;
    Tracker mTracker;
    CheckBox sellerAgreement;
    final private String ACCEPTED = "1";
    final private String NOT_ACCEPTED = "0";
    private String seller_agreement = "";
    long startTime = 0;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        startTime = System.currentTimeMillis();
        view = inflater.inflate(R.layout.buisdetail, container, false);
        return view;

    }

    /****************************** Istiaque created onActivityCreated ***************************/
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        inistuff(view);

        setOnClickListener();

        if (cd.isConnectingToInternet()){
            //new GetKyc().execute();
            //getDetails();
            getBusinessDetails();
        } else {
            Toast.makeText(getActivity(), "Please Check your network connection", Toast.LENGTH_LONG).show();
        }

        prefManager = new PrefManager(getActivity());
        ufList = new ArrayList<String>(Arrays.asList(prefManager.putUFString().split(",")));
    }

    /*****************************************************************************************************/

    private void setOnClickListener() {

        submit.setOnClickListener(this);
        skip.setOnClickListener(this);
        mrkt_arw.setOnClickListener(this);
        buis_arw.setOnClickListener(this);
        imgPersonal.setOnClickListener(this);
        radio_pub.setOnClickListener(this);
        radio_pri.setOnClickListener(this);
        radio_buy.setOnClickListener(this);
        radio_sell.setOnClickListener(this);
        radio_both.setOnClickListener(this);
        radio_pri.setOnClickListener(this);
        shop.setOnClickListener(this);
        state.setOnClickListener(this);
        city.setOnClickListener(this);
        address.setOnClickListener(this);
        tin.setOnClickListener(this);
        pan.setOnClickListener(this);
        radio_Current.setOnClickListener(this);
        radio_Saving.setOnClickListener(this);
        radio_Seller.setOnClickListener(this);
        radio_NotSeller.setOnClickListener(this);
        txtvat.setOnClickListener(this);

        sellerAgreement_tv.setOnClickListener(this);
    }


    private void inistuff(View view)
    {
        radio_pub = (RadioButton) view.findViewById(R.id.radioButton);
        radio_pri = (RadioButton) view.findViewById(R.id.radioButton2);
        radio_buy = (RadioButton) view.findViewById(R.id.radioBuy);
        radio_sell = (RadioButton) view.findViewById(R.id.radioSell);
        radio_both = (RadioButton) view.findViewById(R.id.radioBoth);
        radio_Seller = (RadioButton) view.findViewById(R.id.radioSeller);
        radio_NotSeller = (RadioButton) view.findViewById(R.id.radioNotSeller);
        radio_Saving = (RadioButton) view.findViewById(R.id.radioSaving);
        radio_Current = (RadioButton) view.findViewById(R.id.radioCurrent);
        shop = (EditText) view.findViewById(R.id.txtprodname);
        address = (EditText) view.findViewById(R.id.txtaddress);
        city = (EditText) view.findViewById(R.id.txtcity);
        state = (EditText) view.findViewById(R.id.txtstate);
        pan = (EditText) view.findViewById(R.id.txtpan);
        tin = (EditText) view.findViewById(R.id.txttin);
        spinner = (SuperSpinner) view.findViewById(R.id.spinner1);
        scope = (RadioGroup) view.findViewById(R.id.radiogrp);
        intrest = (RadioGroup) view.findViewById(R.id.radiogrp2);
        radiobankingdetails = (RadioGroup) view.findViewById(R.id.radiobankingdetails);
        radioaccounttype = (RadioGroup) view.findViewById(R.id.radioaccounttype);
        mrkt_arw = (ImageView) view.findViewById(R.id.imgMarket);
        buis_arw = (ImageView) view.findViewById(R.id.imgBuis);
        imgPersonal = (ImageView) view.findViewById(R.id.imgPersonal);
        radioText = (TextView) view.findViewById(R.id.radioText);
        values = getResources().getStringArray(R.array.category_arrays);
        List<String> stringList = new ArrayList<>(Arrays.asList(values));
        linearbusiness = (LinearLayout) view.findViewById(R.id.linearbusiness);
        linearpersonal = (LinearLayout) view.findViewById(R.id.linearpersonal);
        linearadditional = (LinearLayout) view.findViewById(R.id.linearadditional);
        linearaccountDetails = (LinearLayout) view.findViewById(R.id.linearaccountDetails);

        username = (EditText) view.findViewById(R.id.username);
        txtemail = (EditText) view.findViewById(R.id.txtemail);
        txtpin = (EditText) view.findViewById(R.id.txtpin);
        txtvat = (EditText) view.findViewById(R.id.txtvat);
        txtAccName = (EditText) view.findViewById(R.id.txtAccName);
        txtaccno = (EditText) view.findViewById(R.id.txtaccno);
        txtbank = (EditText) view.findViewById(R.id.txtbank);
        txtIfscCode = (EditText) view.findViewById(R.id.txtIfscCode);
        sellerAgreement = (CheckBox) view.findViewById(R.id.sellerAgreement);
        sellerAgreement_tv = (TextView) view.findViewById(R.id.sellerAgreement_tv);
        spinner.init("Select Business Type");

        SuperSpinner.SpinnerItem spinnerItem;
        for (String catItem : values) {
            spinnerItem = new SuperSpinner.SpinnerItem();
            spinnerItem.setDisplayText(catItem);
            spinnerItem.setId(catItem);
            spinner.add(spinnerItem);
            //category.add();
        }
        submit = (Button) view.findViewById(R.id.sub_btn);
        skip = (Button) view.findViewById(R.id.btnskip);

        helper = new Helper();

        cd = new ConnectionDetector(getActivity());


        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);

    }

    private void getBusinessDetails() {
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.getUserKycStatus(helper.getDefaults("user_id", getActivity()), helper.getB64Auth(getActivity()), "application/json");//, "application/json");
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onFailure(Throwable t) {
                if (getActivity() != null && !getActivity().isFinishing())
                    new AlertDialogManager().showAlertDialog(getActivity(),
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response) {
                Log.d("NewConnectionResponse", "" + response.raw());
                progress.dismiss();
                JSONObject businessDetailJSON = null;
                if (response.isSuccess()) {
                    try {
                        JSONObject rootJSON = new JSONObject(response.body().toString());

                        if (rootJSON != null) {
                            Log.d("SelllerAgreement", rootJSON.toString());
                            if (rootJSON.has("business_detail")) {
                                businessDetailJSON = rootJSON.getJSONObject("business_detail");
                                Log.d("ishtiaque", businessDetailJSON.toString());

                                if (businessDetailJSON.has("kyc_status")) {
                                    KYC_STATUS = businessDetailJSON.getString("kyc_status");
                                    Log.e("istiaque1", KYC_STATUS);
                                }

                                if (businessDetailJSON.has("status")) {
                                    statusMsg = businessDetailJSON.getString("status");
                                }

                                if (businessDetailJSON.has("username")) {
                                    username.setText(businessDetailJSON.getString("username"));
                                }

                                if (businessDetailJSON.has("email")) {
                                    txtemail.setText(businessDetailJSON.getString("email"));
                                }

                                if (businessDetailJSON.has("zipcode")) {
                                    txtpin.setText(businessDetailJSON.getString("zipcode"));
                                }

                                if (businessDetailJSON.has("vat_num")) {
                                    txtvat.setText(businessDetailJSON.getString("vat_num"));
                                }

                                if (businessDetailJSON.has("accountHolderName")) {
                                    txtAccName.setText(businessDetailJSON.getString("accountHolderName"));
                                }

                                if (businessDetailJSON.has("accountNumber")) {
                                    txtaccno.setText(businessDetailJSON.getString("accountNumber"));
                                }

                                if (businessDetailJSON.has("bankName")) {
                                    txtbank.setText(businessDetailJSON.getString("bankName"));
                                }

                                if (businessDetailJSON.has("ifscCode")) {
                                    txtIfscCode.setText(businessDetailJSON.getString("ifscCode"));
                                }

                                if (businessDetailJSON.has("accountType")) {
                                    String accType = businessDetailJSON.getString("accountType");

                                    if (accType.equalsIgnoreCase("Saving")) {
                                        radio_Saving.setChecked(true);
                                        radio_Saving.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                                        radio_Current.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                                    } else if (accType.equalsIgnoreCase("Current")) {
                                        radio_Current.setChecked(true);
                                        radio_Saving.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                        radio_Current.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));

                                    }

                                }


                                if (businessDetailJSON.has("store_interest")) {
                                    String store_interest = businessDetailJSON.getString("store_interest");
                                    if (store_interest.equalsIgnoreCase("BUY")) {
                                        radio_buy.setChecked(true);
                                        radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                                        radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                        radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                                    } else if (store_interest.equalsIgnoreCase("SELL")) {
                                        radio_sell.setChecked(true);
                                        radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                        radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                                        radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                                    } else if (store_interest.equalsIgnoreCase("BOTH")) {
                                        radio_both.setChecked(true);
                                        radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                        radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                        radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));

                                    }

                                }
                                if (businessDetailJSON.has("pan_num")) {
                                    pan.setText(businessDetailJSON.getString("pan_num"));
                                }
                                if (businessDetailJSON.has("store_visibility")) {
                                    String store_visibility = businessDetailJSON.getString("store_visibility");
                                    if (store_visibility.equalsIgnoreCase("Public")) {
                                        radio_pub.setChecked(true);
                                        radio_pub.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                                        radio_pri.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                                    } else if (store_visibility.equalsIgnoreCase("Private")) {
                                        radio_pri.setChecked(true);
                                        radio_pub.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                        radio_pri.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));

                                    }
                                }
                                if (businessDetailJSON.has("address")) {
                                    address.setText(businessDetailJSON.getString("address"));
                                }
                                if (businessDetailJSON.has("company")) {
                                    shop.setText(businessDetailJSON.getString("company"));
                                }
                                if (businessDetailJSON.has("state")) {
                                    state.setText(businessDetailJSON.getString("state"));
                                }
                                if (businessDetailJSON.has("tan_num")) {
                                    tin.setText(businessDetailJSON.getString("tan_num"));
                                }
                                if (businessDetailJSON.has("store_type")) {
                                    String store_type = businessDetailJSON.getString("store_type");
                                    int i = 1;
                                    if (store_type != null) {
                                        for (String catItem2 : values) {
                                            if (catItem2.equals(store_type)) {
                                                Log.i("SPINNER", spinner.toString());
                                                spinner.setSelection(i);
                                                break;
                                            }
                                            i++;
                                            //category.add();
                                        }
                                    }
                                }
                                if (businessDetailJSON.has("city")) {
                                    city.setText(businessDetailJSON.getString("city"));
                                }


                                Log.e("istiaque2", KYC_STATUS);
                                if (KYC_STATUS.equalsIgnoreCase("1")) {
                                    pan.setFocusable(false);
                                    tin.setFocusable(false);
                                    txtvat.setFocusable(false);

                                    pan.setClickable(true);
                                    txtvat.setClickable(true);
                                    tin.setClickable(true);
                                }
                                else {
                                    pan.setFocusable(true);
                                    txtvat.setFocusable(true);
                                    tin.setFocusable(true);
                                }



                                if (txtAccName.getText().toString().trim().length() == 0 &&
                                        txtaccno.getText().toString().trim().length() == 0 &&
                                        txtbank.getText().toString().trim().length() == 0 &&
                                        txtIfscCode.getText().toString().trim().length() == 0) {
                                    radio_NotSeller.setChecked(true);
                                    radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                                    radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                    linearaccountDetails.setVisibility(View.GONE);
                                    sellerBankingDetails = "I don't want to sell";
                                    Log.d("sellerBankingDetails", sellerBankingDetails);
                                } else {
                                    radio_Seller.setChecked(true);
                                    radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                                    radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                                    linearaccountDetails.setVisibility(View.VISIBLE);
                                    sellerBankingDetails = "Seller Bank Details";
                                    Log.d("sellerBankingDetails", sellerBankingDetails);
                                }
                            }

                            if (rootJSON.has("business_t_c")){
                                sellerAgreementMsg = rootJSON.getString("business_t_c");
                            }

                            if (rootJSON.has("term_title")){
                                sellerAgreement_tv.setText(rootJSON.getString("term_title"));
                            }

                            if (rootJSON.has("term_url")) {
                                term_url = rootJSON.getString("term_url");
                            }

                            if (rootJSON.has("seller_agreement")) {
                                seller_agreement = rootJSON.getString("seller_agreement");

                                if (seller_agreement.equals(NOT_ACCEPTED)) {

                                    sellerAgreement.setChecked(false);
                                    sellerAgreement.setEnabled(true);

                                } else if (seller_agreement.equals(ACCEPTED)) {

                                    sellerAgreement.setChecked(true);
                                    sellerAgreement.setEnabled(false);

                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // finish();
                } else {
                    Toast.makeText(getActivity(), "Your request has been failed please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (txtAccName.getText().toString().trim().length() == 0 &&
                txtaccno.getText().toString().trim().length() == 0 &&
                txtbank.getText().toString().trim().length() == 0 &&
                txtIfscCode.getText().toString().trim().length() == 0) {
            radio_NotSeller.setChecked(true);
            radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
            radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
            linearaccountDetails.setVisibility(View.GONE);
        } else {
            radio_Seller.setChecked(true);
            radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
            radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));


                    /*txtAccName.setEnabled(false);
                    txtaccno.setEnabled(false);
                    txtbank.setEnabled(false);
                    txtIfscCode.setEnabled(false);*/

            linearaccountDetails.setVisibility(View.VISIBLE);
        }


        /*if (KYC_STATUS.equalsIgnoreCase("1")) {

            pan.setEnabled(false);
            tin.setEnabled(false);
            txtvat.setEnabled(false);
        }
        else
        {
            pan.setEnabled(true);
            txtvat.setEnabled(true);
            tin.setEnabled(true);
        }*/

    }

    private void getDetails() {
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MyContentProvider.CONTENT_URI_Login,
                new String[]{LoginDB.KEY_SCOPE,
                        LoginDB.KEY_ADDRESS,
                        LoginDB.KEY_CITY,
                        LoginDB.KEY_STATE,
                        LoginDB.KEY_COMPANY,
                        LoginDB.KEY_PAN,
                        LoginDB.KEY_TIN,
                        LoginDB.KEY_BUSINESSTYPE,
                        LoginDB.KEY_INTERESTED,
                        LoginDB.KEY_NAME, LoginDB.KEY_EMAIL,LoginDB.KEY_PINCODE,LoginDB.KEY_VAT,LoginDB.KEY_ACCOUNT_HOLDER_NAME,LoginDB.KEY_ACCOUNT_NUMBER,LoginDB.KEY_BANK_NAME,LoginDB.KEY_IFSC_CODE,LoginDB.KEY_ACCOUNT_TYPE},
                LoginDB.KEY_USERID + "=?",
                new String[]{helper.getDefaults("user_id", getActivity().getApplicationContext())}, null);

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(0).equalsIgnoreCase("Public")) {
                    radio_pub.setChecked(true);
                    radio_pub.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                    radio_pri.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                } else if (cursor.getString(0).equalsIgnoreCase("Private")) {
                    radio_pri.setChecked(true);
                    radio_pub.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                    radio_pri.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));

                }

                username.setText(cursor.getString(9));
                txtemail.setText(cursor.getString(10));
                txtpin.setText(cursor.getString(11));
                txtvat.setText(cursor.getString(12));

                txtAccName.setText(cursor.getString(13));
                txtaccno.setText(cursor.getString(14));
                txtbank.setText(cursor.getString(15));
                txtIfscCode.setText(cursor.getString(16));

                if (txtAccName.getText().toString().trim().length() == 0 &&
                        txtaccno.getText().toString().trim().length() == 0 &&
                        txtbank.getText().toString().trim().length() == 0 &&
                        txtIfscCode.getText().toString().trim().length() == 0) {
                    radio_NotSeller.setChecked(true);
                    radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                    radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                    linearaccountDetails.setVisibility(View.GONE);
                } else {
                    radio_Seller.setChecked(true);
                    radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                    radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));


                    /*txtAccName.setEnabled(false);
                    txtaccno.setEnabled(false);
                    txtbank.setEnabled(false);
                    txtIfscCode.setEnabled(false);*/

                    linearaccountDetails.setVisibility(View.VISIBLE);
                }

                if (!cursor.isNull(17)) {
                    if (cursor.getString(17).equalsIgnoreCase("Saving")) {
                        radio_Saving.setChecked(true);
                        radio_Saving.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                        radio_Current.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                    } else if (cursor.getString(17).equalsIgnoreCase("Current")) {
                        radio_Current.setChecked(true);
                        radio_Saving.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                        radio_Current.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));

                    }
                }




                shop.setText(cursor.getString(4));
                address.setText(cursor.getString(1));
                city.setText(cursor.getString(2));
                state.setText(cursor.getString(3));
                pan.setText(cursor.getString(5));
                tin.setText(cursor.getString(6));
                int i = 1;
                Log.d("TYPE VALUE ", cursor.getString(7));
                if (cursor.getString(7) != null) {
                    for (String catItem2 : values) {
                        if (catItem2.equals(cursor.getString(7))) {
                            Log.i("SPINNER", spinner.toString());
                            spinner.setSelection(i);
                            break;
                        }
                        i++;
                        //category.add();
                    }
                }

                if (cursor.getString(8).equalsIgnoreCase("BUY")) {
                    radio_buy.setChecked(true);
                    radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                    radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                    radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                } else if (cursor.getString(8).equalsIgnoreCase("SELL")) {
                    radio_sell.setChecked(true);
                    radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                    radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                    radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));

                } else if (cursor.getString(8).equalsIgnoreCase("BOTH")) {
                    radio_both.setChecked(true);
                    radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                    radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                    radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));

                }

                if (KYC_STATUS.equalsIgnoreCase("1")) {

                    pan.setEnabled(false);
                    tin.setEnabled(false);
                    txtvat.setEnabled(false);
                }
                else
                {
                    pan.setEnabled(true);
                    txtvat.setEnabled(true);
                    tin.setEnabled(true);
                }
            }
            cursor.close();
            //  cursor.close();
        }
    }

    private boolean CheckValidation() {

        int radioButtonID3 = radiobankingdetails.getCheckedRadioButtonId();
        View radioButton3 = radiobankingdetails.findViewById(radioButtonID3);
        int idx3 = radiobankingdetails.indexOfChild(radioButton3);

        if (idx3 == 0){
            sellerBankingDetails = "Seller Bank Details";

        } else if (idx3 == 1) {
            sellerBankingDetails = "I don't want to sell";
        }

        if (!ValidationUtil.isNull(username.getText().toString())) {
            if (!ValidationUtil.isNull(txtemail.getText().toString())) {
                if (!ValidationUtil.isNull(shop.getText().toString())) {
                    if (!ValidationUtil.isNull(address.getText().toString())) {
                        if (!ValidationUtil.isNull(city.getText().toString())) {
                            if (!(ValidationUtil.isNull(pan.getText().toString()) && ValidationUtil.isNull(tin.getText().toString()) && ValidationUtil.isNull(txtvat.getText().toString()))) {
                                if (!ValidationUtil.isNull(state.getText().toString())) {
                                    if (!ValidationUtil.isNull(txtpin.getText().toString())) {
                                        Log.e("sellerBankingDetails", sellerBankingDetails);
                                        if (sellerBankingDetails.equalsIgnoreCase("Seller Bank Details")) {
                                            if (!ValidationUtil.isNull(txtAccName.getText().toString())) {
                                                if (!ValidationUtil.isNull(txtaccno.getText().toString())){
                                                    if (!ValidationUtil.isNull(txtbank.getText().toString())) {
                                                        if (!ValidationUtil.isNull(txtIfscCode.getText().toString())) {
                                                            if (spinner.getSelectedItem() == null) {
                                                                spinner.setError(getResources().getString(R.string.select_buis_type));
                                                                return false;

                                                            } else {
                                                                return true;
                                                            }
                                                        } else {
                                                            txtIfscCode.setError("IFSC Code required");
                                                            txtIfscCode.requestFocus();
                                                        }
                                                    } else {
                                                        txtbank.setError("Bank name required");
                                                        txtbank.requestFocus();
                                                    }
                                                } else {
                                                    txtaccno.setError("Account number required");
                                                    txtaccno.requestFocus();
                                                }
                                            } else {
                                                txtAccName.setError("Account holder name required");
                                                txtAccName.requestFocus();
                                            }
                                        } else {
                                            ///////////////
                                            if (spinner.getSelectedItem() == null) {
                                                spinner.setError(getResources().getString(R.string.select_buis_type));
                                                return false;

                                            } else {
                                                return true;
                                            }
                                        }
                                    } else {
                                        txtpin.setError("PIN Code cannot be blank");
                                        txtpin.requestFocus();
                                    }

                                } else {
                                    state.setError(getResources().getString(R.string.state_empty));
                                    state.requestFocus();
                                }
                            } else {
                                pan.setError(getResources().getString(R.string.empty_pan));
                                pan.requestFocus();

                                tin.setError(getResources().getString(R.string.empty_pan));
                                tin.requestFocus();

                                txtvat.setError(getResources().getString(R.string.empty_pan));
                                txtvat.requestFocus();
                            }
                        } else {
                            city.setError(getResources().getString(R.string.empty_city));
                            city.requestFocus();
                        }
                    } else {
                        address.setError(getResources().getString(R.string.address_empty));
                        address.requestFocus();
                    }
                } else {
                    shop.setError(getResources().getString(R.string.empty_company));
                    shop.requestFocus();
                }
            } else {
                txtemail.setError("Email filed cannot be blank");
                txtemail.requestFocus();
            }
        } else {
            username.setError("Name field cannot be blank");
            username.requestFocus();
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case  R.id.sellerAgreement_tv:

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(term_url));
                startActivity(intent);

                /*WebView wv = new WebView(getActivity());
                wv.loadUrl("http://wydr.in/terms_use.php");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                new AlertDialog.Builder(getActivity())
                        .setView(wv)
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).create().show();*/

                break;


            case R.id.sub_btn:

                if (CheckValidation()) {

                    if (sellerAgreement.isChecked()) {

                        if (cd.isConnectingToInternet()) {

                            try {
                                new SubmitBuisness().execute();

                                /*******************************ISTIAQUE***************************************/
                                application = (Controller) getActivity().getApplication();
                                mTracker = application.getDefaultTracker();
                                application.trackEvent("BusiDetail", "onClick", "Submit Business Detail");
                                // Build and send an App Speed.
                                mTracker.send(new HitBuilders.TimingBuilder().setCategory("My Business - Submit").setValue(System.currentTimeMillis() - startTime).build());
                                /*******************************ISTIAQUE***************************************/

                            } catch (Exception e) {
                                Log.e("Exception: ", e.getMessage());
                            }
                        } else {

                            new AlertDialogManager().showAlertDialog(getActivity(), getActivity().getResources().getString(R.string.sorry), getActivity().getResources().getString(R.string.no_internet_conn));

                        }

                    } else {

                        new AlertDialog.Builder(getActivity())
                                .setTitle(sellerAgreement_tv.getText().toString())
                                .setMessage(sellerAgreementMsg)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // whatever...

                            }
                        }).create().show();
                    }
                }

                break;
            case R.id.btnskip:

                /*******************************ISTIAQUE***************************************/
                application = (Controller) getActivity().getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("BusiDetail", "onClick", "Skip Busibess Detail");
                /*******************************ISTIAQUE***************************************/

                //startActivity(new Intent("android.intent.action.Home"));COMMENTED BY ISTIAQUE
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();
                break;

            case R.id.imgPersonal:
                if (linearpersonal.isShown()) {
                    imgPersonal.setImageResource(R.drawable.gray_down_arrow_with_padding);
                    linearpersonal.setVisibility(View.GONE);
                    buisflag = 1;
                } else {
                    buisflag = 0;
                    imgPersonal.setImageResource(R.drawable.up_arrow_with_padding);
                    linearpersonal.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.imgMarket:
                if (mrkktflag == 0) {
                    mrkt_arw.setImageResource(R.drawable.gray_down_arrow_with_padding);
                    scope.setVisibility(View.GONE);
                    radioText.setVisibility(View.GONE);
                    linearadditional.setVisibility(View.GONE);
                    mrkktflag = 1;
                } else {
                    mrkktflag = 0;
                    mrkt_arw.setImageResource(R.drawable.up_arrow_with_padding);
                    scope.setVisibility(View.VISIBLE);
                    radioText.setVisibility(View.VISIBLE);
                    linearadditional.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.imgBuis:
                if (buisflag == 0) {
                    buis_arw.setImageResource(R.drawable.gray_down_arrow_with_padding);
                    linearbusiness.setVisibility(View.GONE);
                    buisflag = 1;
                } else {
                    buisflag = 0;
                    buis_arw.setImageResource(R.drawable.up_arrow_with_padding);
                    linearbusiness.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radioButton:
                radioText.setText(R.string.busidetpub);
                radio_pub.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_pri.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                break;
            case R.id.radioButton2:
                radioText.setText(R.string.busidetpri);
                radio_pri.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_pub.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                break;
            case R.id.radioSeller:
                radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                linearaccountDetails.setVisibility(View.VISIBLE);
                break;
            case R.id.radioNotSeller:
                radio_NotSeller.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_Seller.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                linearaccountDetails.setVisibility(View.GONE);
                break;
            case R.id.radioSaving:
                radio_Saving.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_Current.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                break;
            case R.id.radioCurrent:
                radio_Current.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_Saving.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                break;
            case R.id.radioBuy:
                radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                break;
            case R.id.radioSell:
                radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                break;
            case R.id.radioBoth:
                radio_buy.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                radio_sell.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked_form));
                radio_both.setButtonDrawable(getResources().getDrawable(R.drawable.checked_form));
                break;
            case R.id.txtprodname:
                helper.SetBack(shop, getResources().getDrawable(R.drawable.buiscat_bck));
                break;
            case R.id.txtaddress:
                helper.SetBack(address, getResources().getDrawable(R.drawable.buiscat_bck));
                break;
            case R.id.txtcity:
                helper.SetBack(city, getResources().getDrawable(R.drawable.buiscat_bck));
                break;
            case R.id.txtstate:
                helper.SetBack(state, getResources().getDrawable(R.drawable.buiscat_bck));
                break;
            case R.id.txtpan:
                helper.SetBack(pan, getResources().getDrawable(R.drawable.buiscat_bck));

                if (KYC_STATUS.equalsIgnoreCase("1")){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"Cannot edit KYC details as it is approved.", Snackbar.LENGTH_LONG).show();
                    Log.d("APPROVED", "Cannot edit KYC details as it is approved.");
                }
                break;
            case R.id.txttin:
                helper.SetBack(tin, getResources().getDrawable(R.drawable.buiscat_bck));

                if (KYC_STATUS.equalsIgnoreCase("1")){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"Cannot edit KYC details as it is approved.", Snackbar.LENGTH_LONG).show();
                    Log.d("APPROVED", "Cannot edit KYC details as it is approved.");
                }
                break;

            case R.id.txtvat:
                helper.SetBack(txtvat, getResources().getDrawable(R.drawable.buiscat_bck));

                if (KYC_STATUS.equalsIgnoreCase("1")){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"Cannot edit KYC details as it is approved.", Snackbar.LENGTH_LONG).show();
                    Log.d("APPROVED", "Cannot edit KYC details as it is approved.");
                }
                break;
        }
    }

    private class SubmitBuisness extends AsyncTask<String, String, JSONObject> {

        JSONObject table = new JSONObject();
        String share, interest, accName, accNo, bank, IfscCode, accType;
        int flag = 0;
        private ProgressDialog progressDialog1;
        String msg = "";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(getActivity());
            progressDialog1.setMessage(getResources().getString(R.string.update_profile));
            progressDialog1.setCancelable(false);
            progressDialog1.show();
            try {
                int radioButtonID = scope.getCheckedRadioButtonId();
                View radioButton = scope.findViewById(radioButtonID);
                int idx = scope.indexOfChild(radioButton);

                int radioButtonID2 = intrest.getCheckedRadioButtonId();
                View radioButton2 = intrest.findViewById(radioButtonID2);
                int idx2 = intrest.indexOfChild(radioButton2);

                int radioButtonID3 = radiobankingdetails.getCheckedRadioButtonId();
                View radioButton3 = radiobankingdetails.findViewById(radioButtonID3);
                int idx3 = radiobankingdetails.indexOfChild(radioButton3);

                int radioButtonID4 = radioaccounttype.getCheckedRadioButtonId();
                View radioButton4 = radioaccounttype.findViewById(radioButtonID4);
                int idx4 = radioaccounttype.indexOfChild(radioButton4);


                if (idx3 == 0){
                    sellerBankingDetails = "Seller Bank Details";
                    accName = txtAccName.getText().toString();
                    accNo= txtaccno.getText().toString();
                    bank =txtbank.getText().toString();
                    IfscCode = txtIfscCode.getText().toString();

                    table.put("txtAccName", accName.trim());
                    table.put("txtaccno", accNo.trim());
                    table.put("txtbank", bank.trim());
                    table.put("txtIfscCode", IfscCode.trim());

                } else if (idx3 == 1) {
                    sellerBankingDetails = "I don't want to sell";


                    txtAccName.setText("");
                    txtaccno.setText("");
                    txtbank.setText("");
                    txtIfscCode.setText("");

                    table.put("txtAccName", "");
                    table.put("txtaccno", "");
                    table.put("txtbank", "");
                    table.put("txtIfscCode", "");
                }

                if (idx4 == 0){
                    accType = "saving";
                } else {
                    accType = "current";
                }

                if (idx == 0)
                    share = "Public";
                else
                    share = "Private";
                if (idx2 == 0)
                    interest = "BUY";
                else if (idx2 == 1)
                    interest = "SELL";
                else
                    interest = "BOTH";


                table.put("username", username.getText().toString().trim());
                table.put("txtemail", txtemail.getText().toString().trim());
                table.put("txtpin", txtpin.getText().toString().trim());
                table.put("txtvat", txtvat.getText().toString().trim());

                /*table.put("txtAccName", accName.trim());
                table.put("txtaccno", accNo.trim());
                table.put("txtbank", bank.trim());
                table.put("txtIfscCode", IfscCode.trim());*/
                table.put("accType", accType.trim());

                table.put("company", shop.getText().toString().trim());
                table.put("address", address.getText().toString().trim());
                table.put("state", state.getText().toString().trim());
                table.put("city", city.getText().toString().trim());
                table.put("store_visibility", share.trim());
                table.put("store_type", spinner.getSelectedItem().getDisplayText().trim());
                table.put("pan_num", pan.getText().toString().trim());
                table.put("tan_num", tin.getText().toString().trim());
                table.put("store_interest", interest.trim());

                if (sellerAgreement.isChecked()) {
                    seller_agreement = "1";
                } else {
                    seller_agreement = "0";
                }

                table.put("seller_agreement", seller_agreement);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;

            json = userFunction.userbusinessdetails(table, helper.getDefaults("user_id", getActivity()), getActivity());
            //json = jsonParser.getJSONFromUrl(AppUtil.URL + "business/" + helper.getDefaults("user_id", getActivity()));


            try {
                if (json != null)
                {

                    Log.e("value Json28", json.toString());

                    String KEY_SUCCESS = "business_detail";
                    if (json.has(KEY_SUCCESS)) {

                        JSONObject jsonObject = json.getJSONObject(KEY_SUCCESS);
                        ContentValues values = new ContentValues();

                        if (jsonObject.has("status")){
                            statusMsg = jsonObject.getString("status");
                        }

                        if (jsonObject.has("username")) {
                            values.put(LoginDB.KEY_NAME, jsonObject.getString("username"));
                        }

                        if (jsonObject.has("txtemail")) {
                            values.put(LoginDB.KEY_EMAIL, jsonObject.getString("txtemail"));
                        }

                        if (jsonObject.has("txtpin")) {
                            values.put(LoginDB.KEY_PINCODE, jsonObject.getString("txtpin"));
                        }

                        if (jsonObject.has("txtvat")) {
                            values.put(LoginDB.KEY_VAT, jsonObject.getString("txtvat"));
                        }

                        if (jsonObject.has("txtAccName")) {
                            values.put(LoginDB.KEY_ACCOUNT_HOLDER_NAME, jsonObject.getString("txtAccName"));
                        }

                        if (jsonObject.has("txtaccno")) {
                            values.put(LoginDB.KEY_ACCOUNT_NUMBER, jsonObject.getString("txtaccno"));
                        }

                        if (jsonObject.has("txtbank")) {
                            values.put(LoginDB.KEY_BANK_NAME, jsonObject.getString("txtbank"));
                        }

                        if (jsonObject.has("txtIfscCode")) {
                            values.put(LoginDB.KEY_IFSC_CODE, jsonObject.getString("txtIfscCode"));
                        }

                        if (jsonObject.has("accType")) {
                            values.put(LoginDB.KEY_ACCOUNT_TYPE, jsonObject.getString("accType"));
                        }


                        if (jsonObject.has("store_interest")) {
                            values.put(LoginDB.KEY_INTERESTED, jsonObject.getString("store_interest"));
                        }
                        if (jsonObject.has("pan_num")) {
                            values.put(LoginDB.KEY_PAN, jsonObject.getString("pan_num"));
                        }
                        if (jsonObject.has("store_visibility")) {
                            values.put(LoginDB.KEY_SCOPE, jsonObject.getString("store_visibility"));
                        }
                        if (jsonObject.has("address")) {
                            values.put(LoginDB.KEY_ADDRESS, jsonObject.getString("address"));
                        }
                        if (jsonObject.has("company")) {
                            values.put(LoginDB.KEY_COMPANY, jsonObject.getString("company"));
                        }
                        if (jsonObject.has("state")) {
                            values.put(LoginDB.KEY_STATE, jsonObject.getString("state"));
                        }
                        if (jsonObject.has("tan_num")) {
                            values.put(LoginDB.KEY_TIN, jsonObject.getString("tan_num"));
                        }
                        if (jsonObject.has("store_type")) {
                            values.put(LoginDB.KEY_BUSINESSTYPE, jsonObject.getString("store_type"));
                        }
                        if (jsonObject.has("city")) {
                            values.put(LoginDB.KEY_CITY, jsonObject.getString("city"));
                        }

                        if (jsonObject.has("msg")) {
                            msg = jsonObject.getString("msg");
                        }

                        if (jsonObject.has("seller_agreement")) {
                            //values.put(LoginDB.SELLER_AGREEMENT, jsonObject.getString("seller_agreement"));
                        }

                        getActivity().getApplicationContext().getContentResolver().update(MyContentProvider.CONTENT_URI_Login, values, LoginDB.KEY_USERID + "=?", new String[]{helper.getDefaults("user_id", getActivity().getApplicationContext())});
                    } else if (json.has("error")) {

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
            if (flag == 1) {
                new AlertDialogManager().showAlertDialog(getActivity().getApplicationContext(),
                        getResources().getString(R.string.sorry),
                        getResources().getString(R.string.server_error));
            }
            if (flag == 2) {
                try {
                    new AlertDialogManager().showAlertDialog(getActivity().getApplicationContext(),
                            getResources().getString(R.string.error),
                            json.getString("error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(),
                        statusMsg,Toast.LENGTH_LONG).show();
                /*Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.profile_updated),
                        Toast.LENGTH_LONG).show();*/
                helper.setDefaults("company", shop.getText().toString(), getActivity().getApplicationContext());
                new UF().execute();
                if (ufList.contains(AppUtil.TAG_My_Orders_Orders_Received)) {
                    final AlertDialog ad = new AlertDialog.Builder(getActivity())
                            .create();
                    ad.setCancelable(false);
                    ad.setTitle("Thank you !");
                    /*ad.setMessage("Your application for Business User is accepted. Just submit 2 products and register as a seller !");*/
                    ad.setMessage(msg);
                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), AddProduct.class);
                            startActivity(i);
                            ad.dismiss();
                        }
                    });
                    ad.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                            ad.dismiss();
                        }
                    });
                    ad.show();

                }
            }

        }
    }




    class UF extends AsyncTask<String, String, String>
    {
        List<NameValuePair> para;
        String id = helper.getDefaults("user_id", getActivity());

        @Override
        protected String doInBackground(String... strings)
        {
            jsonObject = jsonParser.getJSONFromUrlGet(AppUtil.URL + "access/" + id,para, getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("ufff", jsonObject.toString());
            ParseJSonUF(jsonObject);

        }
    }

    public JSONObject getJSONFromUrl(final String url)
    {

        // Making HTTP request
        try {
            // Construct the client and the HTTP request.
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);
            httpPost.setHeader("Authorization","Basic aXN0aWFxdWUuc2lkZGlxaUB3dGRyLmluOjQ3OTZLNThRTENwQjA0MWZuNGU2MzE0VkhZb1p2RHA2");
            httpPost.setHeader("Content-Type","application/json");
            // Execute the POST request and store the response locally.
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // Extract data from the response.
            HttpEntity httpEntity = httpResponse.getEntity();
            // Open an inputStream with the data content.
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Create a BufferedReader to parse through the inputStream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            // Declare a string builder to help with the parsing.
            StringBuilder sb = new StringBuilder();
            // Declare a string to store the JSON object data in string form.
            String line = null;

            // Build the string until null.
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // Close the input stream.
            is.close();
            // Convert the string builder data to an actual string.
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // Try to parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // Return the JSON Object.
        return jObj;

    }


    class GetKyc extends AsyncTask<String, String, String>
    {
        JSONObject jsonObject2;


        @Override
        protected String doInBackground(String... strings)
        {
            jsonObject2 = getJSONFromUrl(AppUtil.URL + "business/" + helper.getDefaults("user_id",getActivity()));
            String kyc_status = "";
            if (jsonObject2.has("business_detail")) {

                try {
                    JSONObject jsonObject = jsonObject2.getJSONObject("business_detail");
                    if (jsonObject.has("kyc_status")){
                        kyc_status = jsonObject.getString("kyc_status");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return kyc_status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ParseJSonUF(jsonObject2);
            KYC_STATUS = s;
            getDetails();
        }
    }


    private void ParseJSonUF(JSONObject jsonObject) {
        try {
            String r = "";
            JSONArray jsonArray = jsonObject.getJSONArray("privilages");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = (JSONObject) jsonArray.get(i);
                String uf = obj.getString("privilage");
                r += uf + ",";
                helper.setDefaults(uf + "_photo", obj.getString("image"), getActivity());
                helper.setDefaults(uf + "_tag", obj.getString("url"), getActivity());
            }

            prefManager.getUFString(r);
            Log.d("PArse", prefManager.putUFString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("My Business");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}