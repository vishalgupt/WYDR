package wydr.sellers.acc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import wydr.sellers.openfire.SmackService;

/**
 * Created by surya on 9/9/15.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";
    Context context;

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, "Network connectivity change");

        if (isConnectingToInternet(context)) {
            context.startService(new Intent(context, SmackService.class));
        } else {
            context.stopService(new Intent(context, SmackService.class));
        }

    }
}

