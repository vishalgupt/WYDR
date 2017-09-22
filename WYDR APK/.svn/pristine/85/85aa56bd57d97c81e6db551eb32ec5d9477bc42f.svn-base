package wydr.sellers.slider;


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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    private final String boundary1 = "*****";
    public int[] statusOk = {200, 201, 202, 204};
    String json = "";
    int statusCode;
    Helper helper = new Helper();
    SessionManager sessionManager ;

    private ContentResolver mContentResolver;
    android.app.AlertDialog.Builder alertDialog;
    // constructor
    public JSONParser() {

    }

    private static HttpEntity createStringEntity(JSONObject params) {
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString(), "UTF-8");
            se.setContentType("application/json; charset=UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("Failed", "Failed to create StringEntity", e);

        }
        return se;
    }

    public static boolean CheckCode(int[] arr, int targetValue) {
        for (int s : arr) {
            if (s == targetValue)
                return true;
        }
        return false;
    }

    public JSONObject getJSONFromUrlGet(String url, List<NameValuePair> para,Context context) {

        // Making HTTP request
        try {
            // defaultHttpClient
               Log.e("url", String.valueOf(url));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.addHeader("Accept", "application/json");
            httpGet.addHeader("Authorization", getB64Auth(context));
            //httpGet.addHeader("Authorization", "Basic c2hyaW1hbGkuaGl0ZXNoMjAxMEBnbWFpbC5jb206QTJQbElUMXYyNUNLOVp2VDg3OUlXNTA2Z0IySTRHMmo=");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // Log.e("url", String.valueOf(url));
              Log.e("httpResponse", String.valueOf(httpResponse));
            if (httpResponse == null || String.valueOf(httpResponse.getStatusLine().getStatusCode()) == null) {
                //  Log.d("insdie null", "NULL RESPONSE");
                statusCode = 600;
            } else {
                statusCode = httpResponse.getStatusLine().getStatusCode();
            }
              Log.i("Coddddddeee", statusCode+"");

            if (!(statusCode == 204 || statusCode == 600)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  Log.e("sttt", statusCode);


        return ProcessStatus(statusCode,context);

    }

//    public JSONObject getJSONFromUrlPost(String url, JSONObject params) {
//
//        // Making HTTP request
//        try {
//            // defaultHttpClient
//
//            JSONObject table = params;
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(url);
//            httpPost.addHeader("Content-Type", "application/json");
//            httpPost.addHeader("Accept", "application/json");
//            httpPost.addHeader("Authorization", "Basic c2hyaW1hbGkuaGl0ZXNoMjAxMEBnbWFpbC5jb206QTJQbElUMXYyNUNLOVp2VDg3OUlXNTA2Z0IySTRHMmo=");
//            ArrayList<NameValuePair> postParameters;
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//
//
//            httpPost.setEntity(createStringEntity(table));
//            Log.e("Coddddddeee", String.valueOf(httpPost));
//            Log.e("Coddddddeee", url);
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            statusCode = httpResponse.getStatusLine().getStatusCode();
//            if (String.valueOf(httpResponse.getStatusLine().getStatusCode()) == null) {
//                statusCode = 600;
//            }
//
//
//            if (!(statusCode == 204 || statusCode == 600)) {
//                HttpEntity httpEntity = httpResponse.getEntity();
//                is = httpEntity.getContent();
//
//            }
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // return JSON String
//        return ProcessStatus(statusCode);
//
//    }

    public JSONObject getJSONFromUrl(final String url)
    {

        // Making HTTP request
        try {
            // Construct the client and the HTTP request.
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

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

    public JSONObject getJSONFromUrlDel(String url, List<NameValuePair> params,Context context) {

        try {
            // defaultHttpClient
            Log.e("url", String.valueOf(url));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpPost = new HttpDelete(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Authorization", getB64Auth(context));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.e("url", String.valueOf(url));
            Log.e("httpResponse", String.valueOf(httpResponse));
            if (httpResponse == null || String.valueOf(httpResponse.getStatusLine().getStatusCode()) == null) {
                Log.e("insdie null", "NULL RESPONSE");
                statusCode = 600;
            } else
                statusCode = httpResponse.getStatusLine().getStatusCode();


            if (!(statusCode == 600)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                if(statusCode==204)
                {
                    is = null;
                }
                else {
                    is = httpEntity.getContent();
                }



            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // return JSON String
        return ProcessStatus(statusCode,context);

    }

    public JSONObject getJSONFromUrlPut(String url, JSONObject params,Context context) {

        // Making HTTP request
        try {
            // defaultHttpClient

            JSONObject table = params;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader("Content-Type", "application/json");
            httpPut.addHeader("Accept", "application/json");
            httpPut.addHeader("Authorization", getB64Auth(context));
            ArrayList<NameValuePair> postParameters;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);


            httpPut.setEntity(createStringEntity(table));
            Log.i("Coddddddeee", String.valueOf(httpPut));
            HttpResponse httpResponse = httpClient.execute(httpPut);
            statusCode = httpResponse.getStatusLine().getStatusCode();

            //
            if (String.valueOf(httpResponse.getStatusLine().getStatusCode()) == null) {
                statusCode = 600;
            }


            if (!(statusCode == 204 || statusCode == 600)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ProcessStatus(statusCode,context);

    }



    public JSONObject ProcessStatus(int statusCode,Context context) {

        Log.i("JsonParser", "  " + statusCode);
        Log.i("JsonParser", "  " + CheckCode(statusOk, statusCode));
        if (CheckCode(statusOk, statusCode))
        {
            if (statusCode == 204) {
                try {
                    //   Log.e("response string", "dddddd");
                    jObj = new JSONObject();
                    jObj.put("error", context.getString(R.string.no_content));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    json = convertStreamToString(is);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                    //    Log.e("response string", json);
                    jObj = new JSONObject(json);

                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
            }
            // return JSON String


        } else if (statusCode == 401) {

            sessionManager = new SessionManager(context);
            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() { sessionManager.logoutUser();} // This is your code
            };
            mainHandler.post(myRunnable);
        }
        else if (statusCode == 404) {

            try {
                jObj = new JSONObject();
                jObj.put("error", "The profile is currently inactive. You can email to support@wydr.in to get the profile activated.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

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

    public JSONObject ProcessStatus_Invite(String url, JSONObject params,Context context) {
        try {
            // defaultHttpClient

            JSONObject table = params;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Authorization", getB64Auth(context));
            ArrayList<NameValuePair> postParameters;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            httpPost.setEntity(createStringEntity(table));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            if (String.valueOf(httpResponse.getStatusLine().getStatusCode()) == null) {
                statusCode = 600;
            }

            if (statusCode == 401)
            {

                sessionManager = new SessionManager(context);
                Handler mainHandler = new Handler(Looper.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() { sessionManager.logoutUser();} // This is your code
                };
                mainHandler.post(myRunnable);
            }
            if (!(statusCode == 204 || statusCode == 600)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("JSON deeepesh", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            Log.e("response string", json);
            jObj = new JSONObject(json);

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }


        return jObj;

    }

    public JSONObject getJSONFromUrlPost(String url, JSONObject params, Context context) {

        try
        {
            String data = getB64Auth(context);
            // Log.d("ssti",data);
            // defaultHttpClient
            JSONObject table = params;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Authorization",data);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");


            ArrayList<NameValuePair> postParameters;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

            httpPost.setEntity(createStringEntity(table));
            Log.e("Coddddddeee", String.valueOf(httpPost));
            Log.e("Coddddddeee", url);
          //  Log.e("Header",getB64Auth(context));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            Log.e("Coddddddeee", statusCode+"");
            if (String.valueOf(httpResponse.getStatusLine().getStatusCode()) == null) {
                statusCode = 600;
            }

            if (!(statusCode == 204 || statusCode == 600)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return JSON String
        return ProcessStatus(statusCode,context);

    }

    private String getB64Auth(Context context) {
        mContentResolver = context.getContentResolver();
        String login, pass, source = "";
        Cursor cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_AUTHENTICATION, null, null, null, null);
        if (cursor.getCount() == 0)
        {
            cursor.close();
            return null;

        }

        else {
            while (cursor.moveToNext()) {
                login = cursor.getString(cursor.getColumnIndexOrThrow(Authentication.login));
                pass = cursor.getString(cursor.getColumnIndexOrThrow(Authentication.api_key));
                source = login + ":" + pass;
            }
            cursor.close();
            String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
            Log.d("string", ret);
            cursor.close();
            return ret;
        }

    }


}