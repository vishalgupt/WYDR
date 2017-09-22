package wydr.sellers.network;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import wydr.sellers.slider.Authentication;
import wydr.sellers.slider.Base64;
import wydr.sellers.slider.MyContentProvider;

public class JSONParser
{
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    int code;
    private ContentResolver mContentResolver;

    //public String[] statusOk= new String []{"200","201","202","204"};
    int[] statusCodeArr = {200, 201, 202, 204};
    SessionManager sessionManager ;
    //constructor

    public JSONParser() {}

    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params,Context context)
    {

        // Making HTTP request
        try {

            // check for request method
            if (method == "POST") {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.addHeader("Accept", "application/json");
                httpPost.addHeader("Authorization", getB64Auth(context));
                //	se.setContentType("application/json; charset=UTF-8");
                //	(JSONObject)JSONSerializer.toJSON(params);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // / String ip =
                // EntityUtils.toString(httpResponse.getEntity());
                // Log.d("hi", ip);
                is = httpEntity.getContent();
                code = httpResponse.getStatusLine().getStatusCode();
            } else if (method == "GET")
            {
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();

                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                Log.e("URL--", url);
                HttpGet httpGet = new HttpGet(url);
                httpGet.addHeader("Content-Type", "application/json");
                httpGet.addHeader("Accept", "application/json");
                httpGet.addHeader("Authorization", getB64Auth(context));
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                code = httpResponse.getStatusLine().getStatusCode();
            }

        } catch (UnsupportedEncodingException e) {
            Log.i("JSONPARSER", "1");
            e.printStackTrace();
            return processStatus(400,context);
        } catch (ClientProtocolException e) {
            Log.i("JSONPARSER", "2");
            e.printStackTrace();
            return processStatus(400,context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("JSONPARSER", "3");
            return processStatus(400,context);
        }
        Log.i("JSON PARSER--", code + "");
        return processStatus(code,context);
    }

    private String getB64Auth(Context context) {
        mContentResolver = context.getContentResolver();
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
    public JSONObject processStatus(int statusCode,Context context) {

        if (CheckCode(statusCodeArr, statusCode))

        {
            try {
                json = convertStreamToString(is);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(
//                        is, "iso-8859-1"), 8);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                is.close();
//                json = sb.toString();
                //   Log.e("JSON ", json);
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                //   Log.d("response", json);
                jObj = new JSONObject(json);

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            // return JSON String


        } else if (statusCode == 401) {

            /*sessionManager = new SessionManager(context);
            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() { sessionManager.logoutUser();} // This is your code
            };
            mainHandler.post(myRunnable);*/

            try {
                jObj = new JSONObject();
                jObj.put("401", "401");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (statusCode == 404) {

            try {
                jObj = new JSONObject();
                jObj.put("message", "The profile is currently inactive. You can email to support@wydr.in to get the profile activated.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            try {
                jObj = new JSONObject();
                jObj.put("error", "SOMETHING BAD HAPPENED");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return jObj;

    }
    private String convertStreamToString(InputStream is) {
        ByteArrayOutputStream oas = new ByteArrayOutputStream();
        copyStream(is, oas);
        String t = oas.toString();
        try {
            oas.close();
            oas = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
    }

    private void copyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    public  boolean CheckCode(int[] arr, int targetValue) {
        for (int s : arr) {
            if (s == targetValue)
                return true;
        }
        return false;
    }

}
