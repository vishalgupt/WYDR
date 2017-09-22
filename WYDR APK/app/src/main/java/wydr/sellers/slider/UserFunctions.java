package wydr.sellers.slider;


import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import wydr.sellers.activities.AppUtil;
import wydr.sellers.registration.Helper;


public class UserFunctions
{
    private JSONParser jsonParser;
    Helper helper = new Helper();
    Context context;
    //URL of the PHP API WORKING!!!!!!
    // constructor
    public UserFunctions()
    {
        jsonParser = new JSONParser();
    }
    public JSONObject sendOtpNumber(String Type, JSONObject name,Context context) {
        String OTPUrl = "";
        JSONObject table = name;

        try
        {
            Log.e("value sendOtpNumber", table.get("phone").toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "otp";
        json = jsonParser.getJSONFromUrlPost(OTPUrl, table, context);
        return json;
    }

    public JSONObject getBanner(String id)
    {
        String OTPUrl = "";
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "home/569?current_user_id="+id;
        // OTPUrl = AppUtil.URL + "/business/5";
        Log.e("value banerurl", OTPUrl);
        json = jsonParser.getJSONFromUrl(OTPUrl);
        Log.d("banners json",json.toString());
        return json;
    }

    public JSONObject register(JSONObject params,Context context) {
        String OTPUrl = "";
        JSONObject table = params;
        try
         {
            Log.e("value city", table.getString("city").toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject json = null;

        OTPUrl = AppUtil.URL + "sellers";
        Log.i("RAW DATA--",table.toString());
        json = jsonParser.getJSONFromUrlPost(OTPUrl, table,context);


        return json;

    }

    public JSONObject getNetworkStatuschange (JSONObject params,Context context)
    {
        String OTPUrl = "";
        JSONObject table = params;

        //List<NameValuePair> params1 = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "network/"+helper.getDefaults("user_id",context);
        json = jsonParser.getJSONFromUrlPut(OTPUrl, params, context);
        return json;
    }


    public JSONObject syncContact(JSONObject params,Context context) {
        String OTPUrl = "";
        JSONObject table1 = params;
        String[] arr;


        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "contactsync";


        json = jsonParser.getJSONFromUrlPost(OTPUrl, table1,context);


        return json;

    }

    public JSONObject addconnection(JSONObject params,Context context) {
        String OTPUrl = "";
        JSONObject table = params;
        try {
            Log.e("value seller_id", table.getString("seller_id").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject json = null;

        OTPUrl = AppUtil.URL + "network";
        json = jsonParser.getJSONFromUrlPost(OTPUrl, table,context);
        return json;
    }

    public JSONObject getNetwork(JSONObject params,Context context) {
        String OTPUrl = "";
        JSONObject table = params;
        try
        {
            Log.e("value seller_id", table.getString("userid").toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
        JSONObject json = null;

        try
        {
            OTPUrl = AppUtil.URL + "network/" + table.getString("userid").toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        json = jsonParser.getJSONFromUrlGet(OTPUrl, params1, context);
        return json;
    }

    public JSONObject categoryfetch(Context context) {
        String OTPUrl = "";
        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
        JSONObject json = null;


        OTPUrl = AppUtil.URL + "categories?simple=true&max_nesting_level=2";

        json = jsonParser.getJSONFromUrlGet(OTPUrl, params1, context);


        return json;
    }

    public JSONObject userbusinessdetails(JSONObject params, String userid,Context context) {
        String OTPUrl = "";
        JSONObject table = params;

        Log.e("value seller_id", userid);


        JSONObject json = null;
    /* AppUtil.URL + "/business/5"; *//**//**/
    /*   try {

    } catch (JSONException e) {
      e.printStackTrace();
    }*/
        OTPUrl = AppUtil.URL + "business/" + userid;
        // OTPUrl = AppUtil.URL + "/business/5";
        Log.e("value ff", OTPUrl);
        json = jsonParser.getJSONFromUrlPut(OTPUrl, table, context);
        return json;
    }

    public JSONObject makePrimary(JSONObject params, String userid,Context context) {
        String OTPUrl = "";
        JSONObject table = params;
          JSONObject json = null;
        OTPUrl = AppUtil.URL + "3.0/users/" + userid;
        // OTPUrl = AppUtil.URL + "/business/5";
        Log.i("value ff", OTPUrl);
        Log.i("value params", params.toString());
        json = jsonParser.getJSONFromUrlPut(OTPUrl, table,context);
        return json;
    }

    public JSONObject deleteQuery(String query_id,Context context) {
        String OTPUrl = "";
        Log.e("value product", query_id);
        JSONObject json;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        OTPUrl = AppUtil.URL + "queries/" + query_id;
        json = jsonParser.getJSONFromUrlDel(OTPUrl, params,context);
        return json;
    }


    public JSONObject submitCategory(JSONObject params, String userid,Context context)
    {
        String OTPUrl = "";
        JSONObject table = params;
        Log.e("json", table.toString());
        try
        {
            Log.e("value product", table.getString("action").toString());
            Log.e("value userid", userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject json = null;

        OTPUrl = AppUtil.URL + "business/" + userid;
        Log.e("value OTPUrl", OTPUrl);
        json = jsonParser.getJSONFromUrlPut(OTPUrl, table,context);


        return json;

    }

    public JSONObject shareCatalogWith(JSONObject params,Context context)
    {
        String OTPUrl = "";
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "shareditem";
        Log.e("value OTPUrl", OTPUrl);
        json = jsonParser.getJSONFromUrlPost(OTPUrl, params,context);
        return json;
    }

    public JSONObject getCategory(Context context)
    {
        String OTPUrl = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "categories&simple=true";
        Log.e("value OTPUrl", OTPUrl);
        json = jsonParser.getJSONFromUrlGet(OTPUrl, params,context);
        return json;

    }

    public JSONObject getProduct(String id, String userid, Context context, String varid, String a)
    {
        String OTPUrl = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "/3.0/products/" + id + "?current_user_id=" + userid + "&user_detail=1"+"&varientid="+varid+"&base64="+a;
        Log.e("getproducturl", OTPUrl);
        json = jsonParser.getJSONFromUrlGet(OTPUrl, params,context);
        return json;
    }


    public JSONObject getProductsFilter(String id, String userid, String param,Context context) {
        String OTPUrl = "";
        List<NameValuePair> params = new ArrayList<>();
        JSONObject json = null;
        try {
            Log.d("Param", param.toString());
            String query = URLEncoder.encode(param, "utf-8");
            OTPUrl = AppUtil.URL + "3.0/" + "categories/" + id + "/products?current_user_id=" + userid + "&only_short_fields=1&display_type=grid&sort_by=timestamp&status=A&product_visibility=public&filter_hash_array=" + query;
            Log.e("value OTPUrl", OTPUrl);
            json = jsonParser.getJSONFromUrlGet(OTPUrl, params,context);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject LikeRequest(String Type, JSONObject name, String Object,Context context) {
        String OTPUrl = "";
        JSONObject table = name;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "favouriteitem";
        json = jsonParser.getJSONFromUrlPost(OTPUrl, table,context);


        return json;

    }

    public JSONObject GetLikes(String Type, String user_id, int page_no,Context context) {
        String OTPUrl = "";


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "favouriteitem?user_id=" + user_id + "&object_type=" + Type + "&page=" + page_no;

        json = jsonParser.getJSONFromUrlGet(OTPUrl, params,context);


        return json;

    }


    public JSONObject GetSellerDetails(String userid, String sellerid,Context context) {
        String OTPUrl = "";


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;

        OTPUrl = AppUtil.URL + "sellers/" + sellerid + "?get_image=true&current_user_id=" + userid;

        Log.e("value OTPUrl", OTPUrl);
        json = jsonParser.getJSONFromUrlGet(OTPUrl, params,context);


        return json;

    }


    public JSONObject DisableProduct(JSONObject name, String productid,Context context) {
        String OTPUrl = "";
        JSONObject table = name;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "/products/" + productid;
        json = jsonParser.getJSONFromUrlPut(OTPUrl, table,context);
        Log.i("disable url", OTPUrl + " " + table.toString());


        return json;

    }

    public JSONObject DeleteConnection(JSONObject name, String id,Context context) {
        String OTPUrl = "";
        JSONObject table = name;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        OTPUrl = AppUtil.URL + "network/" + id;
        json = jsonParser.getJSONFromUrlPut(OTPUrl, table,context);
        Log.e("disable url", OTPUrl + " " + table.toString());


        return json;

    }

}

