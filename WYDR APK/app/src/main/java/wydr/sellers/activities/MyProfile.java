package wydr.sellers.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.AndroidMultiPartEntity;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.acc.ImageHelper;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.ScalingUtilities;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.UserFunctions;

public class MyProfile extends AppCompatActivity {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 200;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 201;
    final int DESIREDWIDTH = 150;
    final int DESIREDHEIGHT = 150;
    final int PIC_CROP = 1;
    public Toolbar toolbar;
    public TextView tv_first_name, tv_last_name, tv_email, tv_phone, tv_address, tv_city, tv_state, tv_zipcode;
    public EditText et_first_name, et_last_name, et_address, et_state, et_zipcode;
    TextInputLayout text_input_firstname,text_input_lastname,text_input_address,text_input_city,text_input_state,text_input_zipcode;
private  boolean isEditing =false;
    public ImageView user_pic;//btn_edit_profile, btn_save_profile;
    public ListLoader imageLoader;
    //public Boolean editing = false;
    ConnectionDetector cd;
    AlertDialog.Builder alertDialog;
    Helper helper = new Helper();
    private AutoCompleteTextView et_city;
    private Button btn_view_catalog, btn_view_queries;
    private ProgressDialog progress;
    private Uri fileUri;
    private String imagepath, user_id;
    private static FrameLayout notifCount;
    private static int mNotifCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        progressStuff();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        tooltitle.setText(getString(R.string.myprofile));
        user_id = helper.getDefaults("user_id", MyProfile.this);
        inistuff();
        BringDetails();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (isEditing) {
            new AlertDialog.Builder(MyProfile.this)
                    .setTitle(getString(R.string.discard_changes))
                    .setMessage(getString(R.string.press_yes_to_dis))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MyProfile.this, MyProfile.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return;
        } else {
            super.onBackPressed();
            startActivity(new Intent(MyProfile.this, Home.class));
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();

                return true;
            case R.id.myprofile_edit:
                tv_first_name.setVisibility(View.GONE);
                tv_last_name.setVisibility(View.GONE);

                tv_address.setVisibility(View.GONE);
                tv_city.setVisibility(View.GONE);
                tv_state.setVisibility(View.GONE);
                tv_zipcode.setVisibility(View.GONE);

                et_first_name.setVisibility(View.VISIBLE);
                et_last_name.setVisibility(View.VISIBLE);

                et_address.setVisibility(View.VISIBLE);
                text_input_address.setVisibility(View.VISIBLE);
                et_city.setVisibility(View.VISIBLE);
                text_input_city.setVisibility(View.VISIBLE);
                et_state.setVisibility(View.VISIBLE);
                text_input_state.setVisibility(View.VISIBLE);
                et_zipcode.setVisibility(View.VISIBLE);
                text_input_zipcode.setVisibility(View.VISIBLE);

                btn_view_catalog.setVisibility(View.GONE);
                btn_view_queries.setVisibility(View.GONE);

                final View.OnClickListener image_click = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
                        builder.setTitle(getResources().getString(R.string.get_image_from))
                                .setCancelable(true)
                                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, GALLERY_IMAGE_REQUEST_CODE);
                                    }
                                }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                fileUri = Uri.fromFile(helper.getOutputMediaFile(2));
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                            }
                        });
                        builder.show();
                    }
                };
                user_pic.setOnClickListener(image_click);

                //editing = true;
                isEditing=true;
                invalidateOptionsMenu();
                break;
            case R.id.myprofile_save:
                if (CheckValidation()) {
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new UpdateProfile().execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void BringDetails() {
        if (cd.isConnectingToInternet()) {
            new GetUserDetails().execute();
        } else {
            new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
        }
    }

    private boolean CheckValidation() {
        if (!ValidationUtil.isNull(et_first_name.getText().toString().trim())) {
            if (!ValidationUtil.isNull(et_address.getText().toString().trim())) {
                if (!ValidationUtil.isNull(et_city.getText().toString().trim())) {
                    if (!ValidationUtil.isNull(et_state.getText().toString().trim())) {
                        if (!ValidationUtil.isNull(et_zipcode.getText().toString().trim())) {
                            if (et_zipcode.getText().toString().trim().length() >= 6) {
                                if (ValidationUtil.isValidText(et_state.getText().toString().trim())) {
                                    if (ValidationUtil.isValidText(et_city.getText().toString().trim())) {
                                        return true;
                                    } else {
                                        et_city.setError(getResources().getString(R.string.invalid_city));
                                        requestFocus(et_city);
                                        //    new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.invalid_city));
                                    }
                                } else {
                                    et_state.setError(getResources().getString(R.string.invalid_state));
                                    requestFocus(et_state);
                                    //  new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.invalid_state));
                                }
                            } else {
                                et_zipcode.setError(getResources().getString(R.string.invalid_zip));
                                requestFocus(et_zipcode);
                                //  new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.invalid_zip));
                            }
                        } else {
                            et_zipcode.setError(getResources().getString(R.string.empty_zip));
                            requestFocus(et_zipcode);
                            // new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_zip));
                        }
                    } else {
                        et_state.setError(getResources().getString(R.string.empty_state));
                        requestFocus(et_state);
                        //  new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_state));
                    }
                } else {
                    et_state.setError(getResources().getString(R.string.empty_state));
                    requestFocus(et_state);
                    //  new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_city));
                }
            } else {
                et_address.setError(getResources().getString(R.string.address_empty));
                requestFocus(et_address); // new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.address_empty));
            }
        } else {

            et_first_name.setError(getResources().getString(R.string.empty_firstname));
            requestFocus(et_first_name);//  new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.empty_firstname));
        }
        return false;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void inistuff() {
        tv_first_name = (TextView) findViewById(R.id.tv_first_name);
        tv_last_name = (TextView) findViewById(R.id.tv_last_name);
        tv_email = (TextView) findViewById(R.id.user_email);
        tv_phone = (TextView) findViewById(R.id.user_contact);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_zipcode = (TextView) findViewById(R.id.tv_zipcode);

        text_input_address=(TextInputLayout)findViewById(R.id.textInputAddress);
        text_input_city=(TextInputLayout)findViewById(R.id.textInputCity);
        text_input_state=(TextInputLayout)findViewById(R.id.textInputState);
        text_input_zipcode=(TextInputLayout)findViewById(R.id.textInputZipcode);


        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);

        et_address = (EditText) findViewById(R.id.et_address);
        et_city = (AutoCompleteTextView) findViewById(R.id.et_city);
        et_state = (EditText) findViewById(R.id.et_state);
        et_zipcode = (EditText) findViewById(R.id.et_zipcode);

        btn_view_catalog = (Button) findViewById(R.id.btn_view_catalog);
        btn_view_queries = (Button) findViewById(R.id.btn_view_queries);

        btn_view_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Profile Leads", "Move", "MyCatalog");
                /*******************************ISTIAQUE***************************************/

                Intent intent = new Intent(MyProfile.this, MyCatalog.class);
                intent.putExtra("parent_myProfile", "true");
                startActivity(intent);
            }
        });
        btn_view_queries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("My Profile Leads", "Move", "MyQuery");
                /*******************************ISTIAQUE***************************************/

                Intent intent = new Intent(MyProfile.this, MyQuery.class);
                intent.putExtra("parent_myProfile", "true");
                startActivity(intent);
            }
        });
        user_pic = (ImageView) findViewById(R.id.contacticon);
        cd = new ConnectionDetector(getApplicationContext());
        imageLoader = new ListLoader(MyProfile.this);


        final List<String> cities = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_cities_autosuggest, cities);
        et_city.setThreshold(2);
        et_city.setAdapter(adapter);
        et_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().indexOf(",") != -1) {
                    int asterisk1 = s.toString().indexOf(',');
                    boolean hasTowAsterisks = asterisk1 != -1 && s.toString().indexOf(',', asterisk1 + 1) != -1;
                    if (hasTowAsterisks) {
                        et_city.setTag(s.subSequence(s.toString().indexOf(",") + 2, s.toString().indexOf(",", s.toString().indexOf(",") + 1)));
                        et_city.setText(s.subSequence(0, s.toString().indexOf(",")));
                        et_state.setText(s.subSequence(s.toString().indexOf(",") + 2, s.toString().indexOf(",", s.toString().indexOf(",") + 1)));
                    } else {
                        et_city.setTag(s.subSequence(0, s.toString().indexOf(",")));
                        et_city.setText(s.subSequence(0, s.toString().indexOf(",")));
                        et_state.setText(s.subSequence(0, s.toString().indexOf(",")));
                    }


                } else {
                    et_city.setTag("");
                }
                Log.i("", "len: " + et_city.length());
                if (et_city.length() >= et_city.getThreshold()) {
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAUqbnkXE4z-TXleAIbqy8j3PyHjYqUzeg&types=(cities)&sensor=false&components=country:in&input=" + et_city.getText()).openConnection();
                                    httpUrlConnection.setUseCaches(false);
                                    httpUrlConnection.setDoOutput(true);
                                    httpUrlConnection.setRequestMethod("GET");
                                    httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                                    httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
//            httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();
                                    while ((inputLine = bufferedReader.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    bufferedReader.close();
                                    httpUrlConnection.disconnect();
                                    Log.i("", "response: " + response);
                                    return response.toString();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String response) {
                                super.onPostExecute(response);
                                if (response == null) {
                                    return;
                                }
                                try {
                                    JSONArray cities_array = new JSONObject(response.toString()).getJSONArray("predictions");
                                    cities.clear();
                                    adapter.clear();
                                    for (int i = 0; i <= cities_array.length() - 1; i++) {

                                        Log.i("", "city: " + cities_array.getJSONObject(i).getString("description"));
                                        adapter.add(cities_array.getJSONObject(i).getString("description"));
                                        cities.add(cities_array.getJSONObject(i).getString("description"));
                                    }
//                                adapter.notifyDataSetChanged();
//                                city.setAdapter(adapter);
//                                adapter.getFilter().filter(city.getText(), null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_edit_profile.setVisibility(View.GONE);
//                btn_save_profile.setVisibility(View.VISIBLE);
//
//
//            }
//        });
//
//        btn_save_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (CheckValidation())
//                    new UpdateProfile().execute();
//
//
//            }
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    performCrop(fileUri);
                } else {
                    performCropImage(fileUri);
                }
            }
            if (requestCode == PIC_CROP) {
                if (data != null) {
                    try {
                        Uri imageUri = data.getData();
                        Bitmap selectedBitmap;
                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                            Bundle extras = data.getExtras();
                            selectedBitmap = extras.getParcelable("data");
                        } else {

                            selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        }

                        imagepath = decodeFileCrop(selectedBitmap);
                        user_pic.setImageBitmap(selectedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (requestCode == GALLERY_IMAGE_REQUEST_CODE && data != null) {
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                cursor.close();
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    performCrop(data.getData());
                } else {
                    performCropImage(data.getData());
                }

            }
        } else {
            Toast.makeText(MyProfile.this, getResources().getString(R.string.fail_load_image), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean performCropImage(Uri mFinalImageUri) {
        Uri mCropImagedUri;
        try {
            if (mFinalImageUri != null) {
                //call the standard crop action intent (the user device may not support it)
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                //indicate image type and Uri
                cropIntent.setDataAndType(mFinalImageUri, "image/*");
                //set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("scale", true);
                //indicate output X and Y
                cropIntent.putExtra("outputX", 200);
                cropIntent.putExtra("outputY", 200);
                //retrieve data on return
                cropIntent.putExtra("return-data", false);

                File f = createNewFile("CROP_");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
                //start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, PIC_CROP);
                return true;
            }
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = getString(R.string.crop_not_supported);
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return false;
    }

    private File createNewFile(String prefix) {
        if (prefix == null || "".equalsIgnoreCase(prefix)) {
            prefix = "IMG_";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory() + "/mypics/");
        if (!newDirectory.exists()) {
            if (newDirectory.mkdir()) {
                Log.d("DIR", newDirectory.getAbsolutePath() + " directory created");
            }
        }
        File file = new File(newDirectory, (prefix + System.currentTimeMillis() + ".jpg"));
        if (file.exists()) {
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private void progressStuff() {
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(this);
    }

    private void performCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 150);
            cropIntent.putExtra("outputY", 150);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.crop_not_supported);
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private String decodeFileCrop(Bitmap bitmap) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.createScaledBitmap(bitmap,
                    DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            // Log.d("STAGE", "" + 1);
//            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap
//                    .getHeight() <= DESIREDHEIGHT)) {
//                // Part 2: Scale image
//                // Log.d("STAGE", "" + 2);
            scaledBitmap = ScalingUtilities.createScaledBitmap(
                    unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT,
                    ScalingUtilities.ScalingLogic.FIT);
//            } else {
//                unscaledBitmap.recycle();
//                return strMyImagePath;
//            }

            // Store to tmp file

            String extr = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File mFolder = new File(extr + "/WYDR/Images");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            // Log.d("STAGE", "" + 3);
            String s = Calendar.getInstance().getTimeInMillis() + ".png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            // Log.d("STAGE", "" + 4 + strMyImagePath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return "";
        }
        return strMyImagePath;

    }

    private class GetUserDetails extends AsyncTask<String, String, JSONObject> {

        public String error_msg = "", email, phone, address, city, first_name, last_name, zipcode, state;
        public int flag = 0, pos;
        private Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isFinishing()) {
                {
                    progress.show();
                }

            }
        }

        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = null;
            if (!isFinishing()) {
                json = userFunction.GetSellerDetails(user_id, user_id, MyProfile.this);
                if (json != null) {
                    if (json.has("user_id")) {

                        try {
                            if (json.has("firstname"))
                                first_name = json.getString("firstname");
                            if (json.has("lastname"))
                                last_name = json.getString("lastname");
                            if (json.has("email"))
                                email = json.getString("email");
                            if (json.has("phone"))
                                phone = json.getString("phone");
//                    if (json.has("company_name"))
//                        company = json.getString("company_name");
                            if (json.has("address"))
                                address = json.getString("address");
                            if (json.has("city"))
                                city = json.getString("city");
                            if (json.has("state"))
                                state = json.getString("state");
                            if (json.has("zipcode"))
                                zipcode = json.getString("zipcode");

                            if (json.has("main_pair") && json.get("main_pair") instanceof JSONObject) {
                                JSONObject img_obj = json.getJSONObject("main_pair");
                                if (img_obj.length() != 0) {
                                    String image_url = img_obj.getJSONObject("icon").getString("image_path");
                                    Log.i("", "image_url: " + image_url);
                                    File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WYDR/Images");
                                    if (!folder.exists())
                                        folder.mkdirs();
                                    String extStorageDirectory = folder.toString();

                                    File mFileTemp = new File(extStorageDirectory, "profile_image.jpg");
                                    helper.DownloadFromUrl(mFileTemp.getAbsolutePath(), image_url);
                                    bitmap = ImageHelper.getRoundedCornerBitmap(helper.decodeSampledBitmapFromResource(mFileTemp.getAbsolutePath(), 400, 200), 10);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        if (json.has("error")) {
                            try {
                                error_msg = json.getString("error");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        flag = 2;
                    }

                } else {
                    flag = 1;
                }

            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (!isFinishing()) {
                progress.dismiss();
                if (flag == 1) {
                    new AlertDialogManager().showAlertDialog(MyProfile.this, getResources().getString(R.string.sorry), getResources().getString(R.string.server_error));
                } else if (flag == 2) {
                    alertDialog.setTitle(getResources().getString(R.string.sorry));
                    alertDialog.setMessage(error_msg);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                } else {
                    tv_first_name.setText(first_name);
                    helper.setDefaults("firstname", first_name, MyProfile.this);
                    tv_last_name.setText(last_name);
                    helper.setDefaults("lastname", last_name, MyProfile.this);
                    tv_email.setText(email);
                    helper.setDefaults("email", email, MyProfile.this);
                    tv_phone.setText(phone);
//                if (company != null) {
//                    tv_company_name.setText(company);
//                }
                    if (address != null) {
                        tv_address.setText(address);
                    }
                    if (city != null) {
                        tv_city.setText(city);
                    }
                    if (state != null) {
                        tv_state.setText(state);
                    }
                    if (zipcode != null) {
                        tv_zipcode.setText(zipcode);
                    }

                    et_first_name.setText(first_name);
                    et_last_name.setText(last_name);

                    if (address != null) {
                        et_address.setText(address);
                    }
                    if (city != null) {
                        et_city.setText(city);
                    }
                    if (state != null) {
                        et_state.setText(state);
                    }
                    if (zipcode != null) {
                        et_zipcode.setText(zipcode);
                    }

                    if (bitmap != null) {
                        user_pic.setImageBitmap(bitmap);
                    }
                }
            }

        }
    }

    private class UpdateProfile extends AsyncTask<JSONObject, String, String> {

        private String firstname, lastname, address, city, state, zipcode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.setMessage(getResources().getString(R.string.updating));

            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();

            firstname = et_first_name.getText().toString().trim();
            lastname = et_last_name.getText().toString().trim();

            address = et_address.getText().toString().trim();
            city = et_city.getText().toString().trim();
            state = et_state.getText().toString().trim();
            zipcode = et_zipcode.getText().toString().trim();
        }

        @Override
        protected String doInBackground(JSONObject... args) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();

            HttpPut httppost = new HttpPut(AppUtil.URL + "sellers/" + user_id);

            httppost.addHeader("Connection", "Keep-Alive");
            httppost.addHeader("Accept", "*/*");
            httppost.addHeader("Content-Type", "multipart/form-data");
            httppost.addHeader("Authorization", helper.getB64Auth(MyProfile.this));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                    }
                });

                if (imagepath != null) {
                    File sourceFile = new File(imagepath);
                    entity.addPart("images[0][image]", new FileBody(sourceFile));
                    entity.addPart("images[0]" + "[is_default]", new StringBody("1"));
                }
                entity.addPart("firstname", new StringBody(firstname));
                entity.addPart("lastname", new StringBody(lastname));

                entity.addPart("address", new StringBody(address));
                entity.addPart("city", new StringBody(city));
                entity.addPart("state", new StringBody(state));
                entity.addPart("zipcode", new StringBody(zipcode));
//                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                Log.d("sttscode", statusCode + "");
                if (statusCode == 201) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(MyProfile.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }
                    responseString = "Error occurred! Http Status Code: "
                            + EntityUtils.toString(r_entity) + "  " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            if (MyProfile.this != null && !isFinishing()) {
                super.onPostExecute(s);
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                Toast.makeText(MyProfile.this, getResources().getString(R.string.profile_updated), Toast.LENGTH_LONG).show();
                BringDetails();
                View view = MyProfile.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

//                btn_edit_profile.setVisibility(View.VISIBLE);
//                btn_save_profile.setVisibility(View.GONE);
                tv_first_name.setVisibility(View.VISIBLE);
                tv_last_name.setVisibility(View.VISIBLE);
                tv_email.setVisibility(View.VISIBLE);
                tv_address.setVisibility(View.VISIBLE);
                tv_city.setVisibility(View.VISIBLE);
                tv_state.setVisibility(View.VISIBLE);
                tv_zipcode.setVisibility(View.VISIBLE);

                et_first_name.setVisibility(View.GONE);
                et_last_name.setVisibility(View.GONE);

                et_address.setVisibility(View.GONE);
                et_city.setVisibility(View.GONE);
                et_state.setVisibility(View.GONE);
                et_zipcode.setVisibility(View.GONE);
                text_input_address.setVisibility(View.GONE);
                text_input_city.setVisibility(View.GONE);
                text_input_state.setVisibility(View.GONE);
                text_input_zipcode.setVisibility(View.GONE);

                btn_view_catalog.setVisibility(View.VISIBLE);
                btn_view_queries.setVisibility(View.VISIBLE);

                user_pic.setOnClickListener(null);

                //editing = false;

                ContentValues values = new ContentValues();

                values.put(LoginDB.KEY_ADDRESS, address);
                values.put(LoginDB.KEY_STATE, state);
                values.put(LoginDB.KEY_CITY, city);

                getContentResolver().update(MyContentProvider.CONTENT_URI_Login, values, LoginDB.KEY_USERID + "=?", new String[]{helper.getDefaults("user_id", MyProfile.this)});
                isEditing = false;
                invalidateOptionsMenu();
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotifCount();
        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        //   Log.i(TAG, "Setting screen name: " + "Home ");
        mTracker.setScreenName(getString(R.string.myprofile));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    private void setNotifCount() {
        Cursor mCursor = getContentResolver().query(ChatProvider.CART_URI, new String[]{CartSchema.PRODUCT_ID}, null, null, null);
        mNotifCount = mCursor.getCount();
        mCursor.close();
        if (notifCount != null) {
            if (mNotifCount == 0) {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
            } else {
                notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);
            }
        }

        invalidateOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.myprofile_menu, menu);
        MenuItem item = menu.findItem(R.id.myprofile_cart);
        MenuItem editItem = menu.findItem(R.id.myprofile_edit);
        MenuItem saveItem = menu.findItem(R.id.myprofile_save);
        if(isEditing){
            saveItem.setVisible(true);
            editItem.setVisible(false);
        }
        else {
            saveItem.setVisible(false);
            editItem.setVisible(true);
        }

        MenuItemCompat.setActionView(item, R.layout.cart_count);
        notifCount = (FrameLayout) MenuItemCompat.getActionView(item);
        if (mNotifCount == 0) {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.GONE);
        } else {
            notifCount.findViewById(R.id.counterValuePanel).setVisibility(View.VISIBLE);

            TextView textView = (TextView) notifCount.findViewById(R.id.count);
            textView.setText(String.valueOf(mNotifCount));

        }
        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("Cart", "Move", "Cart Activity");

                startActivity(new Intent(MyProfile.this, CartActivity.class));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}