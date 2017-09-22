package wydr.sellers.activities;

import android.app.Application;
import android.os.PowerManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wydr.sellers.AnalyticsTrackers;
import wydr.sellers.slider.TypeFaceUtil;
import io.fabric.sdk.android.Fabric;

public class Controller extends Application {

    private static Controller instance;
    private final int MAX_ATTEMPTS = 5;
    private final int BACKOFF_MILLI_SECONDS = 2000;
    private final Random random = new Random();
    private PowerManager.WakeLock wakeLock;
    private String tag = "App";
    private ExecutorService es;

    public static Controller getInstance() {
        return instance;
    }

    public void execRunnable(Runnable r) {
        es.execute(r);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //  if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {             Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,                    "AttachProduct"));        }
        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/OpenSans-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        TypeFaceUtil.overrideFont(getApplicationContext(), "MONOSPACE", "fonts/OpenSans-Regular.ttf");
        instance = this;
     /*   AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);*/
        //  Thread.setDefaultUncaughtExceptionHandler(AppException.getInstance());
        es = Executors.newFixedThreadPool(3);
    }


    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-71811822-1");
          //  mTracker = analytics.newTracker("UA-66415848-2");
        }
        return mTracker;
    }
    public void trackEvent(String category, String action, String label) {
        Tracker t = getDefaultTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }


    /*public void trackAppSpeed(String category, long value) {
        Tracker t = getDefaultTracker();

        // Build and send an App Speed.
        t.send(new HitBuilders.TimingBuilder().setCategory(category).setValue(value).build());
    }*/


    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }


    // Sends the ecommerce data.
    public void sendDataToTwoTrackers(Map<String, String> params) {

        Tracker ecommerceTracker = getDefaultTracker();
     //   appTracker.send(params);
        ecommerceTracker.send(params);
       // ecommerceTracker.addTransaction
    }
}