package wydr.sellers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.pushwoosh.PushManager;

import wydr.sellers.acc.IdHolder;

import static wydr.sellers.activities.CommonUtilities.SENDER_ID;
import static wydr.sellers.activities.CommonUtilities.displayMessage;


public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, message, when);
//
//        String title = context.getString(R.string.app_name);
//
//        Intent notificationIntent = new Intent(context, SharedItems.class);
//        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent =
//                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//       // notification.setLatestEventInfo(context, title, message, intent);
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        // Play default notification sound
//        notification.defaults |= Notification.DEFAULT_SOUND;
//
//        // Vibrate if vibrate is enabled
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        notificationManager.notify(0, notification);

    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        new IdHolder(getApplicationContext()).setToken(registrationId);
        //Log.d("error", registrationId);
        displayMessage(context, "Your device registred with GCM");

    }

    /**
     * Method called on device un registred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        // ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
        if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
         Log.d("THERE", intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
        }
//        Log.i(TAG, "Received message" + intent.getExtras().getString("message"));
//        Log.i(TAG, "Received id" + intent.getExtras().getString("user_id"));
//        Log.i(TAG, "Received name" + intent.getExtras().getString("user_name"));
//        Log.i(TAG, "Received action" + intent.getExtras().getString("action"));
////        String message = intent.getExtras().getString("message");
//        try {
//            //  JSONObject jsonObject = new JSONObject(message);
//            if (intent.getExtras().getString("action").equalsIgnoreCase("deleteNetwork")) {
//                String arguments = intent.getExtras().getString("user_id");
//                ContentValues values = new ContentValues();
//                values.put(NetSchema.USER_STATUS, "0");
//                int a = context.getContentResolver().update(ChatProvider.NET_URI, values, NetSchema.USER_ID + "=?", new String[]{arguments});
//                values.put(ContactsDb.KEY_STATUS, "1");
//                int b = context.getContentResolver().update(ChatProvider.BOOK_URI, values, ContactsDb.USER_ID + "=?", new String[]{arguments});
//                context.getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);
//            } else {
//                generateNotification(context, intent.getExtras().getString("message"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.d("MSG", message);
//        {
//            "data":{
//            "message":"[User name] deleted connection with you.",
//                    "user_id":116,
//                    "action": "deleteNetwork"
//
//        }
//        }

//share Item
//        {
//            "data":{
//            "message":"[User name] share a product([item_name]) with you.",
//                    "product_id":116,
//                    "user_id":116,
//                    "user_name":"hitesh"
//            "action": "share"
//
//        }
//        }
//        try {
//            JSONObject object = new JSONObject(message);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        //  if (new SessionManager(getApplicationContext()).isLoggedIn()) {
//        displayMessage(context, "message hello ");
//
//        generateNotification(context, message);
        // }

    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context,
                getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }
}
