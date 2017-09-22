package wydr.sellers.webconnector;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import wydr.sellers.slider.Authentication;
import wydr.sellers.slider.Base64;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.webconnector.callbacks.ResponseCallback;
import wydr.sellers.webconnector.callbacks.ResponseHolder;

/**
 * Created by FADOO on 5/19/2016.
 */
public class WebHits {

    public final static String BASE_URL = "http://staging.wydr.in/";
    public final static String PREFIX_WALK_THROUGH = "api/";
    public final static String PREFIX_XMP = "/message_api/index.php";
    public final static String PREFIX_ORDER="/orders";

    public final static String PREFIX_ORDER_CANCEL="api/cancelorder";

    public static final int TIME_OUT = 10000;

    public final static int RESPONSE_ORDER_CANCEL = 1007;
    public final static int RESPONSE_ORDER = 1008;
    private static ProgressDialog progressDialog;

    public static void getOrders(ResponseCallback callback) {
        String url=BASE_URL+PREFIX_ORDER;
        new WebHits().orderHit(url, RESPONSE_ORDER, callback);
    }
    public static void cancelOrder(Context context,ResponseCallback callback,JSONObject jsonObject){
        String url=BASE_URL+PREFIX_ORDER_CANCEL;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new WebHits().cancelOrder(url, RESPONSE_ORDER_CANCEL, callback, jsonObject, context);
    }

    private  void orderHit(final String hitUrl, final int token, final ResponseCallback callback) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d("tag", "doInBackground url =" + hitUrl);
                try {
                    final HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams,
                            TIME_OUT);
                    HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
                    HttpClient httpClient = new DefaultHttpClient(httpParams);
                    HttpGet httpget = new HttpGet(hitUrl);
                    HttpResponse res = httpClient.execute(httpget);
                    HttpEntity entity = res.getEntity();
                    InputStream in = entity.getContent();
                    String response = streamToString(in);
                    Log.d("Response", response);
                    in.close();
                    return response;

                } catch (ConnectTimeoutException ex) {
                    ex.printStackTrace();
                    return "Timeout Exception";
                } catch (HttpHostConnectException e) {
                    e.printStackTrace();
                    return "Connect Exception";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                ResponseHolder holder = new ResponseHolder(result);
                if (result.equalsIgnoreCase("Timeout Exception") || result.equalsIgnoreCase("Connect Exception") || result == null)
                    callback.onFailure("Error 404");
                callback.onSuccess(holder, token);
            }

            ;
        }.execute();
    }

    private  void cancelOrder(final String hitUrl, final int token, final ResponseCallback callback, final JSONObject jsonObject,final Context context) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                Log.d("WEB", "HIT_URL : " + hitUrl);
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(hitUrl);

                    post.addHeader("Content-Type", "application/json");
                    post.addHeader("Accept", "application/json");
                    post.addHeader("Authorization", getB64Auth(context));
                    Log.d("TAG_ORDER_CANCEL",jsonObject.toString());
                    post.setEntity(new StringEntity(jsonObject.toString(), "UTF8"));

                    HttpResponse response = client.execute(post);
                    InputStream in = response.getEntity().getContent();
                    String respons = streamToString(in);
                    Log.d("tag", respons);
                    return respons;

                } catch (ConnectTimeoutException ex) {
                    ex.printStackTrace();
                    return "Timeout Exception";
                } catch (HttpHostConnectException e) {
                    e.printStackTrace();
                    return "Connect Exception";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                if(progressDialog!=null)
                progressDialog.dismiss();
                ResponseHolder holder = new ResponseHolder(result);
                if (result.equalsIgnoreCase("Timeout Exception") || result.equalsIgnoreCase("Connect Exception") || result == null)
                    callback.onFailure("Error 404");
                callback.onSuccess(holder, token);

            };
        }.execute();
    }


    private  String streamToString(InputStream in) throws IOException {
        int c = -1;
        StringBuffer r = new StringBuffer();
        try {
            while ((c = in.read()) != -1)
                r.append((char) c);
            return r.toString();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private  String getB64Auth(Context context) {
        ContentResolver mContentResolver = context.getContentResolver();
        String login, pass, source = "";
        Cursor cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_AUTHENTICATION, null, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            while (cursor.moveToNext()) {

                login = cursor.getString(cursor.getColumnIndexOrThrow(Authentication.login));
                pass = cursor.getString(cursor.getColumnIndexOrThrow(Authentication.api_key));
                source = login + ":" + pass;
            }
            cursor.close();
            String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
            cursor.close();
            return ret;
        }

    }

}
