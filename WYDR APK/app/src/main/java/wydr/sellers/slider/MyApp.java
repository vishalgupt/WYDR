package wydr.sellers.slider;

import android.app.Application;

/**
 * Created by Deepesh_pc on 04-09-2015.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        TypeFaceUtil.overrideFont(getApplicationContext(), "SANS", "fonts/OpenSans-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}