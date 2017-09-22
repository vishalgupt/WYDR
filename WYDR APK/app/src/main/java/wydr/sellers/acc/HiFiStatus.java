package wydr.sellers.acc;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.SessionManager;

/**
 * Created by surya on 18/1/16.
 */
public class HiFiStatus extends AsyncTask<String, String, String> {
    Context context;
    String msgId;
    int flag;


//flag 1 = from  , 0 = to
    public HiFiStatus(Context context, String packetId,int flag) {
        this.context = context;
        this.msgId = packetId;
        this.flag = flag;
    }

    @Override
    protected String doInBackground(String... args) {
        JSONParser parser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ref_id", msgId));

        JSONObject json = parser.makeHttpRequest(AppUtil.URL + "deal", "GET", params, context);
        Log.d("Json", json.toString());
        int status = json.optInt("status");

        /*********************** ISTIAQUE ***************************/
        if (json.has("401")) {
            final SessionManager sessionManager = new SessionManager(context);
            sessionManager.logoutUser();
        }
        /*********************** ISTIAQUE ***************************/

        if(flag==1){
            if (status == 0) {
                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 1);
                context.getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                context.getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            }
            else if (status == 1) {
                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 5);
                context.getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                context.getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            } else if (status == 2) {
                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 6);
                context.getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                context.getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            }
        }
        else
        {
            if (status == 0) {
                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 0);
                context.getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                context.getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            }
            else if (status == 1) {
                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 2);
                context.getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                context.getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            } else if (status == 2) {
                ContentValues cv = new ContentValues();
                cv.put(HiFiSchema.ACCEPT, 8);
                context.getContentResolver().update(ChatProvider.HiFi_URI, cv, HiFiSchema.KEY_ROW_ID + "=?", new String[]{msgId});
                context.getContentResolver().notifyChange(ChatProvider.HiFi_URI, null, false);
                context.getContentResolver().notifyChange(ChatProvider.CONTENT_URI, null, false);
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
}
