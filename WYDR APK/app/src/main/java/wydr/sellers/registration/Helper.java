package wydr.sellers.registration;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import wydr.sellers.activities.Contact;
import wydr.sellers.slider.Authentication;
import wydr.sellers.slider.Base64;
import wydr.sellers.slider.MyContentProvider;

/**
 * Created by Deepesh_pc on 04-08-2015.
 */
public class Helper {
    public String TAG = "helper";

    public  String MyPREFERENCES = "MyPrefs" ;
    public Helper setDefaults(String key, String value, Context context) {
        SharedPreferences prefs =context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
        return null;
    }

    public String getDefaults(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key,null);
    }

    public String cleanPhoneNo(String phone_no, Context c) {
        phone_no = phone_no.replaceAll("\\s+", "");
        return phone_no;
    }
    public String getB64Auth(Context context) {
        ContentResolver mContentResolver;
        mContentResolver = context.getContentResolver();
        String login, pass, source = "";
        Cursor cursor = mContentResolver.query(MyContentProvider.CONTENT_URI_AUTHENTICATION, null, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            while (cursor.moveToNext()) {

                login = cursor.getString(cursor.getColumnIndexOrThrow(Authentication.login));
                pass = cursor.getString(cursor.getColumnIndexOrThrow(Authentication.api_key));
                source = login + ":" + pass;
            }
            cursor.close();
            String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
            cursor.close();
            return ret;
        }

    }

    public String getApp_Version(Context context)
    {
        PackageInfo pInfo = null;

        try
        {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        }

        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return pInfo.versionName;
    }

    public String ConvertCamel(String value) {
        if (value == null) {
            return "";
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(value);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
// Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }


    public void SetBack(TextView tv, Drawable drawable) {
        // Log.e("clicked", tv.toString());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            tv.setBackgroundDrawable(drawable);
        } else {
            tv.setBackground(drawable);
        }

    }


    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(String pathName,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public void DownloadFromUrl(String fileName, String URL)  {
            Log.i("Helpet", "Downloading image " + fileName);
        int count;
        try {
            URL url = new URL(URL);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream
            OutputStream output = new FileOutputStream(fileName);
            long freeSize = 0L;


            try {
                Runtime info = Runtime.getRuntime();
                freeSize = info.freeMemory();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(lenghtOfFile<freeSize) {
                byte data[] = new byte[lenghtOfFile];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
            }
            // flushing output
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

    }

    public File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "WYDR/Images");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("TAG", "Oops! Failed create "
                        + "WYDR" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;
        if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }

        return mediaFile;
    }
}
