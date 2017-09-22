package wydr.sellers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import wydr.sellers.R;

/**
 * Created by Deepesh_pc on 21-12-2015.
 */
public class Feedback extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + getResources().getString(R.string.feedback_id)));
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Feedback");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
