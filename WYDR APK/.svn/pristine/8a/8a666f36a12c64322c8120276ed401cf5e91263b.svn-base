package wydr.sellers.activities;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.LastActivityManager;
import org.jivesoftware.smackx.packet.LastActivity;

/**
 * Created by surya on 24/12/15.
 */
public class StatusService extends IntentService {

    String status;
    String account;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public StatusService(String name) {
        super(name);
    }

    public StatusService() {
        super("Empty Constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        account = intent.getStringExtra("account");

        try {
//            Roster roster = XmppConnection.getInstance().getConnection().getRoster();z
//            Presence availability = roster.getPresence(account);
//            //  Presence.Mode userMode = availability.getMode();
           // Log.d("availability", "" + " " + XmppConnection.getInstance().IsUserOnLine(account));
            LastActivity activity = LastActivityManager.getLastActivity(XmppConnection.getInstance().getConnection(), account);
            Log.d("Last Activity", "" + activity.lastActivity);

            if (activity.lastActivity == -1) {
                status = "Online";
                ////   status.setText("Online");
            } else if (activity.lastActivity == 0) {
                status = "Online";
                //status.setText("Online");
            } else {
                long time = activity.lastActivity;
                status = convertTime(time);
                // status.setText();

            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        // Creating an intent for broadcastreceiver
        Intent broadcastIntent = new Intent(AppUtil.BROADCAST_ACTION);

        // Attaching data to the intent
        broadcastIntent.putExtra(AppUtil.EXTRA_STATUS, status);

        if (status != null) {
            // Sending the broadcast
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
        }
    }

    private String convertTime(long sec) {
        if (sec > 60) {
            long min = sec / 60;
            if (min > 60) {
                long hour = min / 60;
                if (hour > 24) {
                    long day = hour / 24;
                    return "Last seen " + day + " day ago";
                } else {
                    return "Last seen " + hour + " hour ago";
                }
            } else {
                return "Last seen " + min + " min ago";
            }
        } else {
            return "Last seen " + sec + " sec ago";
        }

    }

}
