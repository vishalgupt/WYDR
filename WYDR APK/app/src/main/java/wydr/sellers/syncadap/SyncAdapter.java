
package wydr.sellers.syncadap;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.AndroidMultiPartEntity;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.SyncProductModal;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.MyContentProvider;


/**
 * Define a sync adapter for the app.
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    JSONParser parser;
    Context context;
    String max_time = "0";
    private int page_no;
    private final ContentResolver mContentResolver;
    String entryId, title, updatedtime, cretedtime;
    String extStorageDirectory;
    ConnectionDetector cd;
    boolean conti = true;
    Helper helper = new Helper();
    InputStream is = null;
    JSONObject jObj = null;


    int code;
    String jsonString = "";
    int[] statusCodeArr = {200, 201, 202, 204};

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        this.context = context;
        page_no = 1;
        cd = new ConnectionDetector(context);
    }

    ArrayList<String> path;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");

        FetchCategory(syncResult);

        Cursor upload_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null,
                AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?",
                new String[]{"1", "1"},
                null);

        if (upload_cursor.getCount() > 0) {
            UploadCatalog();
        }
        upload_cursor.close();
        Cursor update_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null,
                AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?",
                new String[]{"1", "2"}, null);
        if (update_cursor.getCount() > 0) {
            updateCatalog();
        }
        update_cursor.close();

        Cursor enable_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null,
                AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?",
                new String[]{"1", "5"}, null);
        if (enable_cursor.getCount() > 0) {
            UpdateDisable();
        }
        enable_cursor.close();

        Cursor max_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_FEED, new String[]{"MAX(" + FeedCatalog.COLUMN_UPDATEDAT + ")"}, null, null, null);
        while (max_cursor.moveToNext()) {
            if (max_cursor.getString(0) != null && !max_cursor.getString(0).equalsIgnoreCase("")) {
                max_time = BigDecimal.valueOf(max_cursor.getLong(0)).toPlainString();
            }

        }
        max_cursor.close();
        if (!Boolean.parseBoolean(helper.getDefaults("sync_flag", context))) {
            if (!max_time.equalsIgnoreCase("0")) {
                               max_time = String.valueOf(Long.parseLong(max_time) - 1);
                        }
        }

        BringCatalog(syncResult);
        Log.i(TAG, "Network synchronization complete");
    }

    private void FetchCategory(SyncResult syncResult) {


        String KEY_SUCCESS = "categories";
        ArrayList<CategoryDataModal> catdata = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject childJSONObject;
        int flag = 0;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("simple", "true"));
        params.add(new BasicNameValuePair("force_product_count", "1"));
        JSONObject json = parser.makeHttpRequest(AppUtil.URL + "3.0/categories", "GET", params, context);

        try {
            if (json != null) {
                if (json.has(KEY_SUCCESS)) {
                    childJSONObject = json.getJSONObject(KEY_SUCCESS);
                    catdata = new ArrayList<>();
                    Iterator<?> keys = childJSONObject.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        JSONObject js = childJSONObject.getJSONObject(key);
                        CategoryDataModal cd1 = new CategoryDataModal();
                        cd1.setId(js.getString("category_id"));
                        cd1.setName(js.getString("category"));
                        cd1.setParentid(js.getString("parent_id"));
                        cd1.setProduct_count(js.getString("product_count"));
                        if (js.has("subcategories"))
                            cd1.setHas_child(true);
                        else
                            cd1.setHas_child(false);
                        catdata.add(cd1);
                        Log.i(TAG, "DATA -- > cd1 " + cd1.getId() + "/" + cd1.getName() + "/" + cd1.getHas_child() + "/" + cd1.getParentid() + "/" + cd1.getProduct_count());
                        if (js.has("subcategories")) {
                            JSONArray js1 = js.getJSONArray("subcategories");
                            for (int k = 0; k < js1.length(); k++) {
                                JSONObject js2 = js1.getJSONObject(k);
                                CategoryDataModal cd2 = new CategoryDataModal();
                                cd2.setId(js2.getString("category_id"));
                                cd2.setName(js2.getString("category"));
                                cd2.setParentid(js2.getString("parent_id"));
                                cd2.setProduct_count(js2.getString("product_count"));
                                if (js2.has("subcategories"))
                                    cd2.setHas_child(true);
                                else
                                    cd2.setHas_child(false);
                                catdata.add(cd2);
                                Log.i(TAG, "DATA -- > cd2" + cd2.getId() + "/" + cd2.getName() + "/" + cd2.getHas_child() + "/" + cd2.getParentid() + "/" + cd2.getProduct_count());
                                if (js2.has("subcategories")) {
                                    JSONArray js3 = js2.getJSONArray("subcategories");
                                    for (int l = 0; l < js3.length(); l++) {
                                        JSONObject js4 = js3.getJSONObject(l);
                                        CategoryDataModal cd3 = new CategoryDataModal();
                                        cd3.setId(js4.getString("category_id"));
                                        cd3.setName(js4.getString("category"));
                                        cd3.setParentid(js4.getString("parent_id"));
                                        cd3.setProduct_count(js4.getString("product_count"));
                                        if (js4.has("subcategories"))
                                            cd3.setHas_child(true);
                                        else
                                            cd3.setHas_child(false);
                                        Log.i(TAG, "DATA -- >cd3 " + cd3.getId() + "/" + cd3.getName() + "/" + cd3.getHas_child() + "/" + cd3.getParentid() + "/" + cd3.getProduct_count());
                                        catdata.add(cd3);
                                        if (js4.has("subcategories")) {
                                            JSONArray js6 = js4.getJSONArray("subcategories");
                                            for (int t = 0; t < js6.length(); t++) {
                                                JSONObject js7 = js6.getJSONObject(t);
                                                CategoryDataModal cd4 = new CategoryDataModal();
                                                cd4.setId(js7.getString("category_id"));
                                                cd4.setName(js7.getString("category"));
                                                cd4.setParentid(js7.getString("parent_id"));
                                                cd4.setProduct_count(js7.getString("product_count"));
                                                if (js7.has("subcategories"))
                                                    cd4.setHas_child(true);
                                                else
                                                    cd4.setHas_child(false);
                                                Log.i(TAG, "DATA -- >cd4 " + cd4.getId() + "/" + cd4.getName() + "/" + cd4.getHas_child() + "/" + cd4.getParentid() + "/" + cd4.getProduct_count());
                                                catdata.add(cd4);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                /*********************** ISTIAQUE ***************************/
                if (json.has("401")) {
                    final SessionManager sessionManager = new SessionManager(context);
                    sessionManager.logoutUser();
                }
                /*********************** ISTIAQUE ***************************/

            } else {
                flag = 1;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            syncResult.databaseError = true;
        }

        if (catdata != null && flag == 0) {
            try {
                //mContentResolver.delete(MyContentProvider.CONTENT_URI_Category, null, null);
                for (int s = 0; s < catdata.size(); s++) {
                    ContentValues values = new ContentValues();
                    values.put(CategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                    values.put(CategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                    values.put(CategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                    values.put(CategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                    values.put(CategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());

                    Log.i(TAG, "DATA -- > " + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getHas_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                    int a = mContentResolver.update(MyContentProvider.CONTENT_URI_Category, values, CategoryTable.KEY_CATEGORY_ID + "=?", new String[]{catdata.get(s).getId()});
                    if (a == 0)
                        mContentResolver.insert(MyContentProvider.CONTENT_URI_Category, values);
                }
            } catch (Exception e) {
                Log.e("ERROR --FetchCat", e.toString());
                syncResult.databaseError = true;
            }
        }
    }


    private void UpdateDisable() {
        try {
            Cursor upd_dis_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?", new String[]{"1", "5"}, AlteredCatalog.COLUMN_UPDATED + " ASC");
            String responseString = null;
            if (upd_dis_cursor.getCount() > 0) {
                while (upd_dis_cursor.moveToNext()) {
                    String local_flag = "";
                    //is_pending = upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_REQUEST_STATUS));
                    local_flag = upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_LOCAL_FLAG));
                    //Log.i(TAG, "is_pending " + is_pending + " ...");
                    // Log.i(TAG, "COLUMN_PRODUCT_ID " + upd_dis_cursor.getInt(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PRODUCT_ID)));
                    //Log.i(TAG, "local_flag " + local_flag + " ...");
                    String id = upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PRODUCT_ID));
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPut httppost = new HttpPut(AppUtil.URL + "3.0/products/" + id);
                    httppost.addHeader("Connection", "Keep-Alive");
                    httppost.addHeader("Accept", "*/*");
                    httppost.addHeader("Content-Type", "multipart/form-data");
                    httppost.addHeader("Authorization", helper.getB64Auth(context));

                    try {
                        AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                                new AndroidMultiPartEntity.ProgressListener() {
                                    @Override
                                    public void transferred(long num) {
                                        //  publishProgress((int) ((num / (float) totalSize) * 100));
                                    }
                                });


                        entity.addPart("status", new StringBody(upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_STATUS))));

                        //Log.i(TAG, "COLUMN_PRODUCT_ID " + upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PRODUCT_ID)) + " ...");
                        //Log.i(TAG, "COLUMN_STATUS " + upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_STATUS)) + " ...");

                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity r_entity = response.getEntity();
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 201 || statusCode == 200 || statusCode == 202 || statusCode == 203) {
                            responseString = EntityUtils.toString(r_entity);
                        } else {
                            if (statusCode == 401) {

                                final SessionManager sessionManager = new SessionManager(context);
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.logoutUser();
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                            }
                            responseString = EntityUtils.toString(r_entity);
                        }
                        // Log.i(TAG, "responseString" + responseString + " ...");
                        if (responseString.contains("product")) {
                            JSONObject json = new JSONObject(responseString);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "0");
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(upd_dis_cursor.getInt(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});
                            // Log.i(TAG, " updation ag sync  " + a + " ...");
                            String time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put(FeedCatalog.COLUMN_UPDATEDAT, time);
                            contentValues2.put(FeedCatalog.COLUMN_STATUS, upd_dis_cursor.getString(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_STATUS)));
                            //Log.i(TAG, "necessary check for ids-pid/colid--" + json.getString("product_id") + "/" + id);
                            mContentResolver.update(MyContentProvider.CONTENT_URI_FEED, contentValues2, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{id});
                            //    Log.i(TAG, "count of update at edit " + u);
                        } else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, responseString);
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(upd_dis_cursor.getInt(upd_dis_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});
                            //Log.i(TAG, "count of update at a " + a);
                        }
                    } catch (ClientProtocolException e) {
                        responseString = e.toString();
                    } catch (IOException e) {
                        responseString = e.toString();
                    }
                }
            }
            upd_dis_cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "ex" + e.toString());
        }
    }


    private void updateCatalog() {

        try {
            Cursor updCat_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?", new String[]{"1", "2"}, AlteredCatalog.COLUMN_UPDATED + " ASC");
            String responseString = null;
            // Log.i(TAG, "updation count " + updCat_cursor.getCount() + " ...");
            if (updCat_cursor.getCount() > 0) {
                while (updCat_cursor.moveToNext()) {
                    String is_pending = "", local_flag = "";

                    is_pending = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_REQUEST_STATUS));
                    local_flag = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_LOCAL_FLAG));
                    //  Log.i(TAG, "is_pending " + is_pending + " ...");
                    //   Log.i(TAG, "COLUMN_PRODUCT_ID " + updCat_cursor.getInt(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)));
                    //  Log.i(TAG, "local_flag " + local_flag + " ...");
                    String id = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PRODUCT_ID));
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPut httppost = new HttpPut(AppUtil.URL + "3.0/products/" + id);
                    httppost.addHeader("Connection", "Keep-Alive");
                    httppost.addHeader("Accept", "*/*");
                    httppost.addHeader("Content-Type", "multipart/form-data");
                    httppost.addHeader("Authorization", helper.getB64Auth(context));

                    try {
                        AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                                new AndroidMultiPartEntity.ProgressListener() {
                                    @Override
                                    public void transferred(long num) {
                                        //  publishProgress((int) ((num / (float) totalSize) * 100));
                                    }
                                });
                        String position = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_DEFAULT_POSITION));
                        String col_image = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_IMAGEPATH));
                        String[] imagepath = col_image.split("~");
                        //      Log.i(TAG, "imagepath " + imagepath.length + " ..." + updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE)));
                        for (int i = 0; i < imagepath.length; i++) {
                            Log.e("path", imagepath[i]);
                            if (!imagepath[i].equalsIgnoreCase("") && imagepath[i] != null) {
                                File sourceFile = new File(imagepath[i]);
                                entity.addPart("images[" + i + "][image]", new FileBody(sourceFile));
                                if (i == Long.parseLong(position)) {
                                    entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("1"));
                                } else {
                                    entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("0"));
                                }
                            }

                        }
//                        String Category = "";
//                        if (updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CHILDCAT)).equalsIgnoreCase("")) {
//                            Category = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PARENTCAT));
//                        } else if (updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDCHILDCAT)).equalsIgnoreCase("")) {
//                            Category = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CHILDCAT));
//                        } else {
//                            Category = updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDCHILDCAT));
//                        }
//                        Log.i("CATEGORY--", Category);
                        entity.addPart("product", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE))));
                        entity.addPart("product_code", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CODE))));
                        entity.addPart("company_id", new StringBody(helper.getDefaults("company_id", getContext())));
                        entity.addPart("list_price", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MRP))));
                        entity.addPart("price", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_SP))));
                        entity.addPart("amount", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_QTY))));
                        entity.addPart("min_qty", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MINQTY))));
                        entity.addPart("category_ids[0]", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CATEGORY_ID))));
                        entity.addPart("main_category", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CATEGORY_ID))));
                        entity.addPart("full_description", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_DESC))));
                        entity.addPart("product_visibility", new StringBody(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_VISIBILTY))));
                        //   Log.i(TAG, "COLUMN_TITLE " + updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE)) + " ...");

                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity r_entity = response.getEntity();
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 201) {
                            responseString = EntityUtils.toString(r_entity);
                        } else {
                            if (statusCode == 401) {

                                final SessionManager sessionManager = new SessionManager(context);
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.logoutUser();
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                            }
                            responseString = EntityUtils.toString(r_entity);
                        }
                        //  Log.i(TAG, "responseString" + responseString + " ...");
                        if (responseString.contains("product")) {
                            JSONObject json = new JSONObject(responseString);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "0");
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(updCat_cursor.getInt(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});
                            //  Log.i(TAG, " updation ag sync  " + a + " ...");
                            String time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put(FeedCatalog.COLUMN_TITLE, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE)));
                            contentValues2.put(FeedCatalog.COLUMN_CODE, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CODE)));
                            contentValues2.put(FeedCatalog.COLUMN_IMAGEPATH, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_IMAGEPATH)));
                            contentValues2.put(FeedCatalog.COLUMN_GRANDPARENTCAT, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDPARENTCAT)));
                            contentValues2.put(FeedCatalog.COLUMN_PARENTCAT, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PARENTCAT)));
                            contentValues2.put(FeedCatalog.COLUMN_CHILDCAT, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CHILDCAT)));
                            contentValues2.put(FeedCatalog.COLUMN_GRANDCHILDCAT, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDCHILDCAT)));
                            contentValues2.put(FeedCatalog.COLUMN_MRP, updCat_cursor.getDouble(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MRP)));
                            contentValues2.put(FeedCatalog.COLUMN_SP, updCat_cursor.getDouble(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_SP)));
                            contentValues2.put(FeedCatalog.COLUMN_QTY, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_QTY)));
                            contentValues2.put(FeedCatalog.COLUMN_MINQTY, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MINQTY)));
                            contentValues2.put(FeedCatalog.COLUMN_DESC, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_DESC)));
                            contentValues2.put(FeedCatalog.COLUMN_CATEGORY_ID, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CATEGORY_ID)));
                            contentValues2.put(FeedCatalog.COLUMN_VISIBILTY, helper.ConvertCamel(updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_VISIBILTY))));
                            contentValues2.put(FeedCatalog.COLUMN_UPDATEDAT, time);
                            contentValues2.put(FeedCatalog.COLUMN_CREATEDAT, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CREATEDAT)));
                            if (updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_VISIBILTY)).equalsIgnoreCase("Public"))
                                contentValues2.put(FeedCatalog.COLUMN_STATUS, "A");
                            else
                                contentValues2.put(FeedCatalog.COLUMN_STATUS, "D");
                            contentValues2.put(FeedCatalog.COLUMN_THUMB_PATH, updCat_cursor.getString(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_THUMB_PATH)));
                            //Log.i(TAG, "necessary check for ids-pid/colid--" + json.getString("product_id") + "/" + id);
                            mContentResolver.update(MyContentProvider.CONTENT_URI_FEED, contentValues2, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{id});
                            //   Log.i(TAG, "count of update at edit " + u);
                        } else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, responseString);
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(updCat_cursor.getInt(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});
                            // Log.i(TAG, "count of update at a " + a);
                        }
                    } catch (ClientProtocolException e) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                        contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, e.toString());
                        mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(updCat_cursor.getInt(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});

                        // responseString = e.toString();
                    } catch (FileNotFoundException e) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                        contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, "Image File Not Found");
                        mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(updCat_cursor.getInt(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});

                        Log.i(TAG, "File ni mili--" + e.toString());
                        responseString = e.toString();
                    } catch (IOException e) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                        contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, "Data Exception.Fill Proper Data");
                        mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(updCat_cursor.getInt(updCat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});

                    }


                }

            }
            updCat_cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "ex" + e.toString());
        }
    }


    private void UploadCatalog() {

        try {
            Cursor uplcat_cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?", new String[]{"1", "1"}, AlteredCatalog.COLUMN_UPDATED + " ASC");
            String responseString = null;
            //  Log.i(TAG, "count first " + uplcat_cursor.getCount() + " ...");
            if (uplcat_cursor.getCount() > 0) {
                while (uplcat_cursor.moveToNext()) {
                    //Log.i(TAG, "Found " + uplcat_cursor.getCount() + " ..." + uplcat_cursor.getString(COLUMN_UPDATED));
                    String is_pending = "", local_flag = "";

                    is_pending = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_REQUEST_STATUS));
                    local_flag = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_LOCAL_FLAG));//uplcat_cursor.getString(COLUMN_LOCAL_FLAG);
                    // Log.i(TAG, "is_pending " + is_pending + " ...");
                    //  Log.i(TAG, "COLUMN_ID " + uplcat_cursor.getInt(COLUMN_ID));
                    // Log.i(TAG, "local_flag " + local_flag + " ...");
                    if (is_pending.equalsIgnoreCase("1") && local_flag.equalsIgnoreCase("1")) {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(AppUtil.URL + "3.0/products");
                        httppost.addHeader("Connection", "Keep-Alive");
                        httppost.addHeader("Accept", "**/*//*");
                        httppost.addHeader("Content-Type", "multipart/form-data");
                        httppost.addHeader("Authorization", helper.getB64Auth(context));
                        try {
                            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                                    new AndroidMultiPartEntity.ProgressListener() {
                                        @Override
                                        public void transferred(long num) {
                                            //  publishProgress((int) ((num / (float) totalSize) * 100));
                                        }
                                    });
                            // //Log.d("TAG_PATH", str);


                            String position = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_DEFAULT_POSITION));
                            String[] imagepath = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_IMAGEPATH)).trim().split("~");
                            for (int i = 0; i < imagepath.length; i++) {
                                if (!imagepath[i].equalsIgnoreCase("") && imagepath[i] != null) {

                                    File sourceFile = new File(imagepath[i]);
                                    entity.addPart("images[" + i + "][image]", new FileBody(sourceFile));
                                    if (i == Long.parseLong(position)) {
                                        entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("1"));
                                    } else {
                                        entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("0"));
                                    }
                                }
                            }
                            //     String Category = "";
//                            if (uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CHILDCAT)).equalsIgnoreCase("")) {
//                                Category = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PARENTCAT));
//                            } else if (uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDCHILDCAT)).equalsIgnoreCase("")) {
//                                Category = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CHILDCAT));
//                            } else {
//                                Category = uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDCHILDCAT));
//                            }
//                            Log.i("CATEGORY--", Category);
//                            entity.addPart("product", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE))));
                            entity.addPart("product_code", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CODE))));
                            entity.addPart("company_id", new StringBody(helper.getDefaults("company_id", getContext())));
                            entity.addPart("list_price", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MRP))));
                            entity.addPart("price", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_SP))));
                            entity.addPart("amount", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_QTY))));
                            entity.addPart("min_qty", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MINQTY))));
                            entity.addPart("category_ids[0]", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CATEGORY_ID))));
                            entity.addPart("main_category", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CATEGORY_ID))));
                            entity.addPart("full_description", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_DESC))));
                            entity.addPart("product_visibility", new StringBody(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_VISIBILTY))));
                            //  Log.i(TAG, "COLUMN_TITLE " + uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE)) + " ...");
                            httppost.setEntity(entity);
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity r_entity = response.getEntity();
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode == 201) {
                                // Server response
                                responseString = EntityUtils.toString(r_entity);
                            } else {
                                if (statusCode == 401) {

                                    final SessionManager sessionManager = new SessionManager(context);
                                    Handler mainHandler = new Handler(Looper.getMainLooper());

                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            sessionManager.logoutUser();
                                        } // This is your code
                                    };
                                    mainHandler.post(myRunnable);
                                }
                                responseString = EntityUtils.toString(r_entity);
                            }
                            //  Log.i(TAG, "response--" + responseString);
                            if (responseString.contains("product")) {
                                // Log.i(TAG, "responseString " + responseString);
                                JSONObject json = new JSONObject(responseString);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "0");
                                contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, json.getString("product_id"));
                                contentValues.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, json.getString("product_id"));
                                mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(uplcat_cursor.getInt(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});
                                //  Log.i(TAG, "a--a " + a + " ...");
                                //    Cursor cb = mContentResolver.query(MyContentProvider.CONTENT_URI_ALTER, null, AlteredCatalog.COLUMN_REQUEST_STATUS + "=? and " + AlteredCatalog.COLUMN_LOCAL_FLAG + "=?", new String[]{"1", "2"}, AlteredCatalog.COLUMN_UPDATED + " ASC");
                                //  Log.i(TAG, "befo9re count " + cb.getCount() + " ...");
                                //  cb.close();
                                ContentValues contentValues2 = new ContentValues();
                                contentValues2.put(FeedCatalog.COLUMN_TITLE, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_TITLE)));
                                contentValues2.put(FeedCatalog.COLUMN_CODE, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CODE)));
                                contentValues2.put(FeedCatalog.COLUMN_IMAGEPATH, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_IMAGEPATH)));
                                contentValues2.put(FeedCatalog.COLUMN_GRANDPARENTCAT, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDPARENTCAT)));
                                contentValues2.put(FeedCatalog.COLUMN_PARENTCAT, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_PARENTCAT)));
                                contentValues2.put(FeedCatalog.COLUMN_CHILDCAT, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CHILDCAT)));
                                contentValues2.put(FeedCatalog.COLUMN_GRANDCHILDCAT, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_GRANDCHILDCAT)));
                                contentValues2.put(FeedCatalog.COLUMN_MRP, uplcat_cursor.getDouble(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MRP)));
                                contentValues2.put(FeedCatalog.COLUMN_SP, uplcat_cursor.getDouble(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_SP)));
                                contentValues2.put(FeedCatalog.COLUMN_QTY, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_QTY)));
                                contentValues2.put(FeedCatalog.COLUMN_MINQTY, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_MINQTY)));
                                contentValues2.put(FeedCatalog.COLUMN_DESC, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_DESC)));
                                contentValues2.put(FeedCatalog.COLUMN_VISIBILTY, helper.ConvertCamel(uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_VISIBILTY))));
                                contentValues2.put(FeedCatalog.COLUMN_PRODUCT_ID, json.getString("product_id"));
                                contentValues2.put(FeedCatalog.COLUMN_UPDATEDAT, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CREATEDAT)));
                                contentValues2.put(FeedCatalog.COLUMN_CREATEDAT, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CREATEDAT)));
                                contentValues2.put(FeedCatalog.COLUMN_THUMB_PATH, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_THUMB_PATH)));
                                contentValues2.put(FeedCatalog.COLUMN_CATEGORY_ID, uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_CATEGORY_ID)));
                                if (uplcat_cursor.getString(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog.COLUMN_VISIBILTY)).equalsIgnoreCase("Public"))
                                    contentValues2.put(FeedCatalog.COLUMN_STATUS, "A");
                                else
                                    contentValues2.put(FeedCatalog.COLUMN_STATUS, "D");
                                contentValues2.put(FeedCatalog.COLUMN_LOCAL_PROD_ID, json.getString("product_id"));
                                mContentResolver.insert(MyContentProvider.CONTENT_URI_FEED, contentValues2);
                                // Log.i(TAG, uri2.toString());
                                context.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_FEED, null, false);
                                context.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_ALTER, null, false);
                            } else {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                                contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, responseString);
                                mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(uplcat_cursor.getInt(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});
                                context.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_FEED, null, false);
                                context.getContentResolver().notifyChange(MyContentProvider.CONTENT_URI_ALTER, null, false);
                            }
                        } catch (ClientProtocolException e) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, e.toString());
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(uplcat_cursor.getInt(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});

                            // responseString = e.toString();
                        } catch (FileNotFoundException e) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, "Image File Not Found");
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(uplcat_cursor.getInt(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});

                            Log.i(TAG, "File ni mili--" + e.toString());
                            responseString = e.toString();
                        } catch (IOException e) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "true");
                            contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, e.toString());
                            mContentResolver.update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{String.valueOf(uplcat_cursor.getInt(uplcat_cursor.getColumnIndexOrThrow(AlteredCatalog._ID)))});

                        }
                    }
                }

            }
            uplcat_cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "ex" + e.toString());
        }
    }

    public void BringCatalog(SyncResult syncResult) {
        if (!Boolean.parseBoolean(helper.getDefaults("sync_flag", context))) {
            try {
                try {

                    parser = new JSONParser();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("company_id", helper.getDefaults("company_id", context)));
                    params.add(new BasicNameValuePair("sort_by", "updated_timestamp"));
                    params.add(new BasicNameValuePair("sort_order", "asc"));
                    params.add(new BasicNameValuePair("syncperiod", "true"));
                    params.add(new BasicNameValuePair("time_from", max_time));
                    params.add(new BasicNameValuePair("page", "" + page_no));
                    JSONObject json = makeHttpRequest(AppUtil.URL + "3.0/products", "GET", params);
                    //  Log.i(TAG, "page No - " + page_no);
                    if (json != null) {
                        if (json.has("products")) {
                            try {

                                if (json.getJSONObject("params").getInt("page") < page_no) {
                                    page_no = 0;

                                } else {
                                    JSONArray jsonArray = json.getJSONArray("products");
                                    final ContentResolver contentResolver = getContext().getContentResolver();
                                    HashMap<String, SyncProductModal> entryMap = new HashMap<>();
                                    if (jsonArray.length() == 0) {
                                        page_no = 0;
                                        helper.setDefaults("sync_flag", "true", context);
                                    } else {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            path = new ArrayList<>();
                                            JSONObject object = jsonArray.getJSONObject(i);
                                            SyncProductModal modal = new SyncProductModal();
                                            modal.setId(object.optString("product_id"));
                                            modal.setCode(object.optString("product_code"));
                                            modal.setPrice(object.optDouble("list_price"));
                                            modal.setSellingPrice(object.optDouble("price"));
                                            modal.setName(object.optString("product"));
                                            modal.setQty(object.optString("amount"));
                                            modal.setMinqty(object.optString("min_qty"));
                                            modal.setStatus(object.optString("status").toUpperCase());
                                            if (object.has("seo_path")) {
                                                String[] cat_arr;
                                                cat_arr = object.optString("seo_path").split("/");
                                                modal.setGrandparent_id(cat_arr[0]);
                                                if (cat_arr.length > 1)
                                                    modal.setParent_id(cat_arr[1]);
                                                else {
                                                    modal.setParent_id("");
                                                    modal.setChild_id("");
                                                }
                                                if (cat_arr.length > 2)
                                                    modal.setChild_id(cat_arr[2]);
                                                if (cat_arr.length > 3)
                                                    modal.setLeaf_id(cat_arr[3]);
                                                modal.setCat(cat_arr[cat_arr.length - 1]);

                                                Log.i("CATEGORY-ID", " for " + object.getString("product") + " " + cat_arr[cat_arr.length - 1]);
                                            }

                                            modal.setDesc(object.optString("full_description"));
                                            modal.setVisibility(object.optString("product_visibility"));
                                            modal.setTimestamp(object.optString("timestamp"));
                                            modal.setUpdated_timestamp(object.optLong("updated_timestamp"));

                                            if (object.has("main_pair")) {
                                                if (object.get("main_pair") instanceof JSONObject) {
                                                    path.add(object.optJSONObject("main_pair").optJSONObject("detailed").optString("http_image_path"));
                                                    modal.setImgUrl(object.optJSONObject("main_pair").optJSONObject("detailed").optString("http_image_path"));
                                                } else {
                                                    modal.setImgUrl("");
                                                }
                                            }
                                            if (object.has("image_pairs")) {
                                                if (object.get("image_pairs") instanceof JSONObject) {
                                                    JSONObject obj = object.optJSONObject("image_pairs");
                                                    Iterator<?> keys = obj.keys();
                                                    while (keys.hasNext()) {
                                                        String key = (String) keys.next();
                                                        path.add(obj.optJSONObject(key).optJSONObject("detailed").optString("http_image_path"));
                                                    }
                                                }
                                            }
                                            //   Log.i(TAG, "name & getId --i " + i + "  " + modal.getName() + ".. i  ." + modal.getUpdated_timestamp());
                                            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WYDR/Images");
                                            if (!folder.exists())
                                                folder.mkdirs();
                                            extStorageDirectory = folder.toString();
                                            // Log.i(TAG, "extStorageDirectory " + extStorageDirectory);
                                            String image_path = "";

                                            for (int a = 0; a < path.size(); a++) {
                                                String path_var = Loadimage(path.get(a), extStorageDirectory);
                                                if (!path_var.equalsIgnoreCase(""))
                                                    image_path = image_path + path_var + "~";
                                                // Log.i(TAG, "image_path +" + image_path);
                                            }

                                            if (image_path.length() > 1)
                                                image_path = image_path.substring(0, image_path.length() - 1);
                                            modal.setUrl_list(image_path);

                                            Log.i(TAG, "image_path thumbnail" + modal.getImgUrl());
                                            entryMap.put(modal.getId(), modal);
                                            if (jsonArray.length() < 10) {
                                                page_no = 0;
                                            }
                                        }
                                        if (conti) {
                                            Cursor feed_cursor = contentResolver.query(MyContentProvider.CONTENT_URI_FEED, null, null, null, null);
                                            assert feed_cursor != null;
                                            //Log.i(TAG, "Found " + feed_cursor.getCount() + " local entries. Computing merge solution...");
                                            int id;


                                            while (feed_cursor.moveToNext()) {
                                                syncResult.stats.numEntries++;
                                                id = feed_cursor.getInt(feed_cursor.getColumnIndexOrThrow(FeedCatalog._ID));
                                                entryId = feed_cursor.getString(feed_cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_PRODUCT_ID));
                                                title = feed_cursor.getString(feed_cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_TITLE));
                                                updatedtime = BigDecimal.valueOf(feed_cursor.getLong(feed_cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_UPDATEDAT))).toString();
                                                cretedtime = feed_cursor.getString(feed_cursor.getColumnIndexOrThrow(FeedCatalog.COLUMN_CREATEDAT));
                                                // sp = c.getString(c.getColumnIndexOrThrow(FeedCatalog.COLUMN_VISIBILTY));
                                                // Log.i(TAG, "title-visibility ---" + title + "," + updatedtime);
                                                SyncProductModal match = entryMap.get(entryId);
                                                if (match != null) {
                                                    // Entry exists. Remove from entry map to prevent insert later.
                                                    entryMap.remove(entryId);
                                                    // Check to see if the entry needs to be updated
                                                    //       Log.i(TAG, "title-id" + title + "--" + id);
                                                    if ((match.getUpdated_timestamp() != null && !match.getUpdated_timestamp().equals(updatedtime)) || (match.getTimestamp() != null && !match.getTimestamp().equals(cretedtime))) {
                                                        //   Log.i(TAG, "enterted updtion for " + match.getId());
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put(FeedCatalog.COLUMN_TITLE, match.getName());
                                                        contentValues.put(FeedCatalog.COLUMN_CODE, match.getCode());
                                                        contentValues.put(FeedCatalog.COLUMN_IMAGEPATH, match.getUrl_list());
                                                        contentValues.put(FeedCatalog.COLUMN_GRANDPARENTCAT, match.getGrandparent_id());
                                                        contentValues.put(FeedCatalog.COLUMN_PARENTCAT, match.getParent_id());
                                                        contentValues.put(FeedCatalog.COLUMN_CHILDCAT, match.getChild_id());
                                                        contentValues.put(FeedCatalog.COLUMN_GRANDCHILDCAT, match.getLeaf_id());
                                                        contentValues.put(FeedCatalog.COLUMN_MRP, match.getPrice());
                                                        contentValues.put(FeedCatalog.COLUMN_SP, match.getSellingPrice());
                                                        contentValues.put(FeedCatalog.COLUMN_QTY, match.getQty());
                                                        contentValues.put(FeedCatalog.COLUMN_MINQTY, match.getMinqty());
                                                        contentValues.put(FeedCatalog.COLUMN_DESC, match.getDesc());
                                                        contentValues.put(FeedCatalog.COLUMN_VISIBILTY, helper.ConvertCamel(match.getVisibility()));
                                                        contentValues.put(FeedCatalog.COLUMN_PRODUCT_ID, match.getId());
                                                        contentValues.put(FeedCatalog.COLUMN_UPDATEDAT, BigDecimal.valueOf(match.getUpdated_timestamp()).toPlainString());
                                                        contentValues.put(FeedCatalog.COLUMN_CREATEDAT, match.getTimestamp());
                                                        contentValues.put(FeedCatalog.COLUMN_STATUS, match.getStatus());
                                                        contentValues.put(FeedCatalog.COLUMN_LOCAL_PROD_ID, match.getId());
                                                        contentValues.put(FeedCatalog.COLUMN_THUMB_PATH, match.getImgUrl());
                                                        contentValues.put(FeedCatalog.COLUMN_CATEGORY_ID, match.getCat());
                                                        mContentResolver.update(MyContentProvider.CONTENT_URI_FEED, contentValues, FeedCatalog._ID + "=?", new String[]{String.valueOf(id)});

                                                    } else {
                                                        //Log.i(TAG, "No action: for update" + MyContentProvider.CONTENT_URI_FEED);
                                                    }
                                                } else {
                                                    //  Log.i(TAG, "entry doesn't exist. Remove it from the database");
                                                    // Entry doesn't exist. Remove it from the database.
                                                    //   mContentResolver.delete(MyContentProvider.CONTENT_URI_FEED, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{c.getString(c.getColumnIndexOrThrow(FeedCatalog.COLUMN_PRODUCT_ID))});

                                                }
                                            }
                                            feed_cursor.close();
                                            //Log.i(TAG, "entryMap" + entryMap.size());
                                            for (SyncProductModal e : entryMap.values()) {
                                                //  Log.i(TAG, "Scheduling insert: entry_id=" + e.getName() + "/" + BigDecimal.valueOf(e.getUpdated_timestamp()).toPlainString());
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put(FeedCatalog.COLUMN_TITLE, e.getName());
                                                contentValues.put(FeedCatalog.COLUMN_CODE, e.getCode());
                                                contentValues.put(FeedCatalog.COLUMN_IMAGEPATH, e.getUrl_list());
                                                contentValues.put(FeedCatalog.COLUMN_GRANDPARENTCAT, e.getGrandparent_id());
                                                contentValues.put(FeedCatalog.COLUMN_PARENTCAT, e.getParent_id());
                                                contentValues.put(FeedCatalog.COLUMN_CHILDCAT, e.getChild_id());
                                                contentValues.put(FeedCatalog.COLUMN_GRANDCHILDCAT, e.getLeaf_id());
                                                contentValues.put(FeedCatalog.COLUMN_MRP, e.getPrice());
                                                contentValues.put(FeedCatalog.COLUMN_SP, e.getSellingPrice());
                                                contentValues.put(FeedCatalog.COLUMN_QTY, e.getQty());
                                                contentValues.put(FeedCatalog.COLUMN_MINQTY, e.getMinqty());
                                                contentValues.put(FeedCatalog.COLUMN_DESC, e.getDesc());
                                                contentValues.put(FeedCatalog.COLUMN_VISIBILTY, helper.ConvertCamel(e.getVisibility()));
                                                contentValues.put(FeedCatalog.COLUMN_PRODUCT_ID, e.getId());
                                                contentValues.put(FeedCatalog.COLUMN_UPDATEDAT, BigDecimal.valueOf(e.getUpdated_timestamp()).toPlainString());
                                                contentValues.put(FeedCatalog.COLUMN_CREATEDAT, e.getTimestamp());
                                                contentValues.put(FeedCatalog.COLUMN_STATUS, e.getStatus());
                                                contentValues.put(FeedCatalog.COLUMN_LOCAL_PROD_ID, e.getId());
                                                contentValues.put(FeedCatalog.COLUMN_THUMB_PATH, e.getImgUrl());
                                                contentValues.put(FeedCatalog.COLUMN_CATEGORY_ID, e.getCat());
                                                mContentResolver.insert(MyContentProvider.CONTENT_URI_FEED, contentValues);
                                            }


                                            mContentResolver.notifyChange(
                                                    MyContentProvider.CONTENT_URI_FEED, // URI where data was modified
                                                    null,                           // No local observer
                                                    false);


                                            if (page_no != 0) {
                                                page_no++;
                                                BringCatalog(syncResult);
                                            }
                                        }
                                    }
                                }
                                helper.setDefaults("sync_flag", "true", context);

                            } catch (JSONException e) {
                                helper.setDefaults("sync_flag", "false", context);
                                Log.i(TAG, e.toString());
                                e.printStackTrace();
                            } catch (Exception e) {
                                helper.setDefaults("sync_flag", "false", context);
                                Log.i(TAG, e.toString());
                                e.printStackTrace();
                            }
                        }

                        /*********************** ISTIAQUE ***************************/
                        if (json.has("401")) {
                            final SessionManager sessionManager = new SessionManager(context);
                            sessionManager.logoutUser();
                        }
                        /*********************** ISTIAQUE ***************************/

                    }


                } catch (Exception e) {
                    helper.setDefaults("sync_flag", "false", context);
                    //Log.i(TAG, "Prev_SYNC =" + prev_sync);
                    Log.e(TAG, "Error updating database: " + e.toString());
                    syncResult.databaseError = true;

                }
            } catch (Exception e) {
                helper.setDefaults("sync_flag", "false", context);
                //  Log.i(TAG, "Prev_SYNC =" + prev_sync);
                Log.i(TAG, e.toString());
                e.printStackTrace();
            }
        }
    }
    private String Loadimage(String path, String extStorageDirectory) throws MalformedURLException {


        File mFileTemp;
        //    Log.i(TAG, " image  is " + path.trim().replace("?", "/").split("/")[6]);
        mFileTemp = new File(extStorageDirectory, path.trim().substring(path.lastIndexOf('/') + 1, path.lastIndexOf('?')));
        Log.i(TAG, " image  is " + path.trim().substring(path.lastIndexOf('/') + 1, path.lastIndexOf('?')));
        conti = cd.isConnectingToInternet();
        if (mFileTemp.exists()) {
            // Log.i("VALUE--", "1");
            if (mFileTemp.length() == 0) {
                helper.DownloadFromUrl(mFileTemp.getAbsolutePath(), path);
            }
            //helper.DownloadFromUrl(mFileTemp.getAbsolutePath(), path);
        } else {
            // Log.i("VALUE--", "2");
            mFileTemp = new File(extStorageDirectory, path.trim().substring(path.lastIndexOf('/') + 1, path.lastIndexOf('?')));
            helper.DownloadFromUrl(mFileTemp.getAbsolutePath(), path);
        }
        if (mFileTemp.length() == 0)
            return "";
        else
            return mFileTemp.getAbsolutePath();

    }


    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {


            DefaultHttpClient httpClient = new DefaultHttpClient();

            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
            Log.e("URL--", url);
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.addHeader("Accept", "application/json");
            httpGet.addHeader("Authorization", helper.getB64Auth(context));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            code = httpResponse.getStatusLine().getStatusCode();


        } catch (UnsupportedEncodingException e) {
            helper.setDefaults("sync_flag", "false", context);
            // Log.i(TAG, "Prev_SYNC =" + prev_sync);
            Log.i(TAG, "exception at UnsupportedEncodingException " + e.toString());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            helper.setDefaults("sync_flag", "false", context);
            // Log.i(TAG, "Prev_SYNC =" + prev_sync);
            Log.i(TAG, "exception at ClientProtocolException " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            helper.setDefaults("sync_flag", "false", context);
            // Log.i(TAG, "Prev_SYNC =" + prev_sync);
            Log.i(TAG, "exception at IOException " + e.toString());
            e.printStackTrace();
        } catch (StackOverflowError e) {
            helper.setDefaults("sync_flag", "false", context);
            // Log.i(TAG, "Prev_SYNC =" + prev_sync);
            Log.i(TAG, "exception at StackOverflowError " + e.toString());
            e.printStackTrace();
        }

        return processStatus(code);

    }




    public boolean CheckCode(int[] arr, int targetValue) {
        for (int s : arr) {
            if (s == targetValue)
                return true;
        }
        return false;
    }

    public JSONObject processStatus(int statusCode) {


        if (CheckCode(statusCodeArr, statusCode))

        {
            try {
                jsonString = convertStreamToString(is);

            } catch (OutOfMemoryError e) {
                helper.setDefaults("sync_flag", "false", context);
                Log.i(TAG, "exception at Exception " + e.toString());
            } catch (Exception e) {
                helper.setDefaults("sync_flag", "false", context);
                Log.i(TAG, "exception at Exception " + e.toString());
            }
            try {
                jObj = new JSONObject(jsonString);
            } catch (JSONException e) {
                helper.setDefaults("sync_flag", "false", context);
                Log.e(TAG, "exception at JSONException " + e.toString());
            }


        } else if (statusCode == 401) {

            final SessionManager sessionManager = new SessionManager(context);
            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    sessionManager.logoutUser();
                } // This is your code
            };
            mainHandler.post(myRunnable);
        } else if (statusCode == 404) {

            try {
                jObj = new JSONObject();
                jObj.put("message", context.getResources().getString(R.string.page_not_found));
            } catch (JSONException e) {
                helper.setDefaults("sync_flag", "false", context);
                e.printStackTrace();
            }
        } else {

            try {
                jObj = new JSONObject();
                jObj.put("error", "SOMETHING BAD HAPPENED");
            } catch (JSONException e) {
                e.printStackTrace();
                helper.setDefaults("sync_flag", "false", context);
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

    private void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}
// Request status = 0 for nothing , 1 = pending and  2 =save
// Local Flag = 1 for add , 2 = update and  3 =delete 4 = save 5 =for enable disable