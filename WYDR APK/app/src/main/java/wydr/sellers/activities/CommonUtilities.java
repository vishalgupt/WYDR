package wydr.sellers.activities;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    public static final String SENDER_ID = "715357366204";
    //715357366204
    public static final String TAG = "WYDR GCM";

    public static final String DISPLAY_MESSAGE_ACTION = "auriga.sellers.activities.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    public static void displayMessage(Context context, String message) {
        // AcceptRequest.msg = message;
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
