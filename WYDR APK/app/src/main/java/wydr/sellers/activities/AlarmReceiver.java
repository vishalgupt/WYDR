package wydr.sellers.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import wydr.sellers.acc.ChatSchema;

/**
 * Created by surya on 18/1/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        //  Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();
        Log.d("ARG", "GROUP");
//        Calendar cal = Calendar.getInstance();
//        String date = format.format(cal.getTime());
        Calendar cal2 = Calendar.getInstance();

        cal2.add(Calendar.DAY_OF_MONTH, -15);
        String date2 = format.format(cal2.getTime());
        arg0.getContentResolver().delete(ChatProvider.CONTENT_URI, ChatSchema.KEY_CREATED + "<?", new String[]{date2});

    }

}
