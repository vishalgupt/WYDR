
package wydr.sellers.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import wydr.sellers.R;
import wydr.sellers.acc.AndroidMultiPartEntity;
import wydr.sellers.acc.ScalingUtilities;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.gson.CategoryHolder;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.ProdCatModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.GPSTracker;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.CirclePageIndicator;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.slider.SuperSpinner;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;


/**
 * Created by deepesh on 16/10/15.
 */
public class EditQuery extends AppCompatActivity implements View.OnClickListener {
    SuperSpinner cat, subCat, childcat, last_spin;
    EditText quantity, name, code;
    TextInputLayout input_Date;

    EditText date;
    Button submit;
    String sel_cat = "";
    String selcat = "";

    ConnectionDetector cd;
    JSONParser parser;
    SessionManager session;
    private ProgressDialog progress;
    RadioGroup rg;
    ArrayList<HashMap<String, String>> catList;
    ArrayList<HashMap<String, String>> subCatList;

    int pos = 0;
    SimpleDateFormat sdf;
    String images, selectedcatval = "";
    ViewPager viewPager;
    PagerAdapter adapter;
    public List<Bitmap> imagegallery = new ArrayList<>();
    public ArrayList<String> imagepath = new ArrayList<>();
    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 200;
    private final int GALLERY_IMAGE_REQUEST_CODE = 201;
    public CirclePageIndicator titleIndicator;
    public int default_position = 0;
    final int DESIREDWIDTH = 800;
    final int DESIREDHEIGHT = 800;
    AlertDialog.Builder alertDialog;
    private Uri fileUri;
    long totalSize = 0;
    final int PIC_CROP = 6;
    public JSONArray jsonMainArr;
    public ProdCatModal pm_def;
    public ArrayList<String> category, category2, category3, category4;
    ;
    ArrayList<ProdCatModal> products, products2, products3, products4;
    String par_cat, sub_cat, child_cat, leaf_cat;
    ;

    Helper helper = new Helper();
    private AutoCompleteTextView city;
    //RelativeLayout linear_neededBy;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_query);
//        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
//            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
//                    "EditQuery"));
//        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.edit_query));
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon);
        imagegallery.add(icon);
        viewPager = (ViewPager) findViewById(R.id.pager);
        user_id = helper.getDefaults("user_id", getApplicationContext());
        progressStuff();
        products = new ArrayList<>();
        category = new ArrayList<>();
        products2 = new ArrayList<>();
        category2 = new ArrayList<>();
        products3 = new ArrayList<>();
        category3 = new ArrayList<>();
        products4 = new ArrayList<>();
        category4 = new ArrayList<>();
        cat = (SuperSpinner) findViewById(R.id.spinnerCategory);
        subCat = (SuperSpinner) findViewById(R.id.spinnerSubCategory);
        childcat = (SuperSpinner) findViewById(R.id.childCategory);
        last_spin = (SuperSpinner) findViewById(R.id.grandChildCategory);
        cat.init("SELECT META CATEGORY");
        subCat.init("SELECT SUB CATEGORY");
        childcat.init("SELECT LEAF CATEGORY");
        last_spin.init("SELECT LEAF CATEGORY");


        // displayLocation();

        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    cat.setBackgroundDrawable(getResources().getDrawable(R.drawable.code_bar));
                } else {
                    cat.setBackground(getResources().getDrawable(R.drawable.code_bar));
                }
                selectedcatval = "";
                BringSubCategory(products.get(position).getId());
                childcat.setVisibility(View.GONE);
                last_spin.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        subCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    subCat.setBackgroundDrawable(getResources().getDrawable(R.drawable.code_bar));
                } else {
                    subCat.setBackground(getResources().getDrawable(R.drawable.code_bar));
                }
                if (products2.get(position).getHas_child()) {
                    childcat.setVisibility(View.VISIBLE);
                    selectedcatval = "";
                    BringChildCategory(products2.get(position).getId());

                } else {
                    selectedcatval = products2.get(position).getId();
                    childcat.setVisibility(View.GONE);
                    last_spin.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        childcat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    childcat.setBackgroundDrawable(getResources().getDrawable(R.drawable.code_bar));
                } else {
                    childcat.setBackground(getResources().getDrawable(R.drawable.code_bar));
                }
                if (products3.get(position).getHas_child()) {
                    last_spin.setVisibility(View.VISIBLE);

                    selectedcatval = "";
                    BringGrandChildCategory(products3.get(position).getId());

                } else {
                    selectedcatval = products3.get(position).getId();
                    last_spin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        last_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    last_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.code_bar));
                } else {
                    last_spin.setBackground(getResources().getDrawable(R.drawable.code_bar));
                }
                selectedcatval = products4.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        iniStuff();
        Cursor countCursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_ID}, null, null, null);
        if (countCursor.getCount() == 0) {
            prepareRequest();
        } else {
            BringCategory();
        }
        countCursor.close();

        if (PermissionsUtils.verifyLocationPermissions(EditQuery.this)) {
            displayLocation();
        }
        name.setOnClickListener(this);
        city.setOnClickListener(this);
        code.setOnClickListener(this);
        quantity.setOnClickListener(this);


    }

    private void displayLocation() {

        GPSTracker gpsTracker = new GPSTracker(EditQuery.this);
        //   Log.i("GPS-latitude", gpsTracker.getLatitude() + "");
        // Log.i("GPS-getLongitude", gpsTracker.getLongitude() + "");
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String city1 = "";

            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    city1 = addresses.get(0).getLocality();
                    city.setText(city1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imagegallery.clear();
        imagepath.clear();
        adapter.notifyDataSetChanged();
        progress.dismiss();
    }

    public void BringCategory() {
        category.clear();
        products.clear();
        category2.clear();
        products2.clear();
        category3.clear();
        products3.clear();
        category4.clear();
        products4.clear();
//        adap_1.notifyDataSetChanged();
        Cursor parent = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, null);
        assert parent != null;
        // Log.i("SubmitQuery", parent.getCount() + "");
        String id, title, has_child;
        ProdCatModal pm_def = new ProdCatModal();
        pm_def.setName("SELECT META CATEGORY");
        pm_def.setId("0");
        pm_def.setHas_child(false);
        products.add(pm_def);
        //  category.add("SELECT META CATEGORY");
        while (parent.moveToNext()) {
            id = parent.getString(0);
            title = parent.getString(1);
            has_child = parent.getString(2);
            ProdCatModal pm = new ProdCatModal();
            pm.setName(title);
            pm.setId(id);
            //  Log.i("SubmitQuery ", id + " / " + title + " / " + has_child);
            if (has_child.equalsIgnoreCase("1"))
                pm.setHas_child(true);
            else
                pm.setHas_child(false);
            if (id.equalsIgnoreCase(par_cat)) {
                sel_cat = title;
            }
            products.add(pm);
            category.add(title);
        }

        SuperSpinner.SpinnerItem spinnerItem;
        for (String catItem : category) {
            spinnerItem = new SuperSpinner.SpinnerItem();
            spinnerItem.setDisplayText(catItem);
            spinnerItem.setId(catItem);
            cat.add(spinnerItem);
            //category.add();
        }
        cat.setSelection(0);
        for (int i = 0; i < category.size(); i++) {
            if (category.get(i).equalsIgnoreCase(sel_cat)) {
                Log.i("BringCategory  ", category.get(i) + " / " + sel_cat);
                cat.setSelection(i + 1);
                break;
            }

        }

        parent.close();

    }

    public void BringSubCategory(String par_id) {
        category2.clear();
        products2.clear();
        category3.clear();
        products3.clear();
        category4.clear();
        products4.clear();

        if (!par_id.equalsIgnoreCase("0")) {
            Cursor parent2 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{par_id}, null);
            String id, title, has_child;
            ProdCatModal pm_def = new ProdCatModal();
            pm_def.setName("SELECT CATEGORY");
            pm_def.setId("0");
            pm_def.setHas_child(false);
            products2.add(pm_def);
            // category2.add("SELECT CATEGORY");
            while (parent2.moveToNext()) {
                id = parent2.getString(0);
                title = parent2.getString(1);
                has_child = parent2.getString(2);
                ProdCatModal pm = new ProdCatModal();
                pm.setName(title);
                pm.setId(id);
                //   Log.i("Addproduct //////", id + " / " + title + " / " + has_child);
                if (has_child.equalsIgnoreCase("1"))
                    pm.setHas_child(true);
                else
                    pm.setHas_child(false);
                if (id.equalsIgnoreCase(sub_cat)) {
                    selcat = title;
                }
                Log.i("Subcategory", id + " / " + sub_cat);
                products2.add(pm);
                category2.add(title);
            }
            parent2.close();
        } else {
            ProdCatModal pm_def = new ProdCatModal();
            pm_def.setName("SELECT CATEGORY");
            pm_def.setId("0");
            pm_def.setHas_child(false);
            products2.add(pm_def);
            category2.add("SELECT CATEGORY");
        }

        subCat.clear();
        childcat.clear();
        last_spin.clear();

        SuperSpinner.SpinnerItem spinnerItem;
        for (String subcatItem : category2) {
            spinnerItem = new SuperSpinner.SpinnerItem();
            spinnerItem.setDisplayText(subcatItem);
            spinnerItem.setId(subcatItem);
            subCat.add(spinnerItem);

        }
        subCat.setSelection(0);
        for (int i = 0; i < category2.size(); i++) {
            if (category2.get(i).equalsIgnoreCase(selcat)) {
                subCat.setSelection(i + 1);
                break;
            }
        }

    }

    public void BringChildCategory(String par_id) {
        String selchildcat = "";
        category3.clear();
        products3.clear();
        category4.clear();
        products4.clear();
        if (!par_id.equalsIgnoreCase("0")) {
            Cursor parent3 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{par_id}, null);
            //       Log.i("SubmitQuery", parent3.getCount() + "");
            String id, title, has_child;
            ProdCatModal pm_def = new ProdCatModal();
            pm_def.setName("SELECT LEAF CATEGORY");
            pm_def.setId("0");
            pm_def.setHas_child(false);
            products3.add(pm_def);
            //   category3.add("SELECT LEAF CATEGORY");
            while (parent3.moveToNext()) {
                id = parent3.getString(0);
                title = parent3.getString(1);
                has_child = parent3.getString(2);
                ProdCatModal pm = new ProdCatModal();
                pm.setName(title);
                pm.setId(id);

                if (has_child.equalsIgnoreCase("1"))
                    pm.setHas_child(true);
                else
                    pm.setHas_child(false);
                if (id.equalsIgnoreCase(child_cat)) {
                    selchildcat = title;
                }
                products3.add(pm);
                category3.add(title);
            }
            parent3.close();
        } else {
            ProdCatModal pm_def = new ProdCatModal();
            pm_def.setName("SELECT LEAF CATEGORY");
            pm_def.setId("0");
            pm_def.setHas_child(false);
            products3.add(pm_def);

        }


        childcat.clear();
        last_spin.clear();
        SuperSpinner.SpinnerItem spinnerItem;
        for (String childcatItem : category3) {
            spinnerItem = new SuperSpinner.SpinnerItem();
            spinnerItem.setDisplayText(childcatItem);
            spinnerItem.setId(childcatItem);
            childcat.add(spinnerItem);
            //category.add();
        }
        childcat.setSelection(0);
        for (int i = 0; i < category3.size(); i++) {
            if (category3.get(i).equalsIgnoreCase(selchildcat)) {
                childcat.setSelection(i + 1);
                break;
            }
        }


    }

    public void BringGrandChildCategory(String par_id) {
        String selcat = "";

        category4.clear();
        products4.clear();

        if (!par_id.equalsIgnoreCase("0")) {
            Cursor parent3 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{par_id}, null);
            //    Log.i("SubmitQuery", parent3.getCount() + "");
            String id, title, has_child;
            ProdCatModal pm_def = new ProdCatModal();
            pm_def.setName("SELECT LEAF CATEGORY");
            pm_def.setId("0");
            pm_def.setHas_child(false);
            products4.add(pm_def);
            //  category4.add("SELECT LEAF CATEGORY");
            while (parent3.moveToNext()) {
                id = parent3.getString(0);
                title = parent3.getString(1);
                has_child = parent3.getString(2);
                ProdCatModal pm = new ProdCatModal();
                pm.setName(title);
                pm.setId(id);

                if (has_child.equalsIgnoreCase("1"))
                    pm.setHas_child(true);
                else
                    pm.setHas_child(false);
                if (id.equalsIgnoreCase(leaf_cat)) {
                    selcat = title;
                }
                products4.add(pm);
                category4.add(title);
            }
            parent3.close();
        } else {
            ProdCatModal pm_def = new ProdCatModal();
            pm_def.setName("SELECT LEAF CATEGORY");
            pm_def.setId("0");
            pm_def.setHas_child(false);
            products4.add(pm_def);
            //     category4.add("SELECT LEAF CATEGORY");
        }
        last_spin.clear();
        SuperSpinner.SpinnerItem spinnerItem;
        for (String childcatItem : category4) {
            spinnerItem = new SuperSpinner.SpinnerItem();
            spinnerItem.setDisplayText(childcatItem);
            spinnerItem.setId(childcatItem);
            last_spin.add(spinnerItem);

        }
        last_spin.setSelection(0);
        for (int i = 0; i < category4.size(); i++) {
            if (category4.get(i).equalsIgnoreCase(selcat)) {
                last_spin.setSelection(i + 1);
                break;
            }
        }


    }

    private void progressStuff() {
        // TODO Auto-generated method stub
        // session = new SessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        alertDialog = new AlertDialog.Builder(this);
    }

    public void fun(View v) {
        int con2 = (int) v.getTag();
        if (con2 == (imagegallery.size() - 1)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditQuery.this);
            builder.setTitle(getResources().getString(R.string.get_image_from))
                    .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(galleryIntent, GALLERY_IMAGE_REQUEST_CODE);
                        }
                    }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    captureImage();
                }
            });

            builder.show();
        } else {
            Intent showImage = new Intent(EditQuery.this, ImageActivity.class);
            showImage.putStringArrayListExtra("bitmap", imagepath);
            startActivity(showImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  Log.i("RESULTCODE--", resultCode + "");
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
                    Uri imageUri = data.getData();
                    try {
                        Bitmap selectedBitmap;
                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                            Bundle extras = data.getExtras();
                            selectedBitmap = extras.getParcelable("data");
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        }
                        imagegallery.add(selectedBitmap);
                        adapter.notifyDataSetChanged();
                        imagepath.add(decodeFileCrop(selectedBitmap));

                        Bitmap s = this.imagegallery.get(this.imagegallery.size() - 2);
                        this.imagegallery.remove(this.imagegallery.size() - 2);
                        this.imagegallery.add(s);

                        adapter.notifyDataSetChanged();
                        titleIndicator.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // user_pic.setImageBitmap(selectedBitmap);
                }
            }
            if (requestCode == GALLERY_IMAGE_REQUEST_CODE && data != null) {
                // Get the Image from data
                //Log.i("HERE","1");
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    images = cursor.getString(column_index);
                    cursor.close();
                    if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        performCrop(data.getData());
                    } else {
                        performCropImage(data.getData());
                    }
                }


            }
        } else {
            // failed to record video
            Toast.makeText(EditQuery.this, getResources().getString(R.string.fail_load_image),
                    Toast.LENGTH_SHORT).show();
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

    private void iniStuff() {

        quantity = (EditText) findViewById(R.id.editTextQuantity);
        city = (AutoCompleteTextView) findViewById(R.id.editTextLocation);
        final List<String> cities = new ArrayList<>();
        final ArrayAdapter<String> cities_adapter = new ArrayAdapter<String>(this, R.layout.item_cities_autosuggest, cities);
        city.setThreshold(2);
        city.setAdapter(cities_adapter);
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().indexOf(",") != -1) {
                    int asterisk1 = s.toString().indexOf(',');
                    boolean hasTowAsterisks = asterisk1 != -1 && s.toString().indexOf(',', asterisk1 + 1) != -1;
                    if (hasTowAsterisks) {
                        city.setTag(s.subSequence(s.toString().indexOf(",") + 2, s.toString().indexOf(",", s.toString().indexOf(",") + 1)));
                        city.setText(s.subSequence(0, s.toString().indexOf(",")));

                    } else {
                        city.setTag(s.subSequence(0, s.toString().indexOf(",")));
                        city.setText(s.subSequence(0, s.toString().indexOf(",")));

                    }
                } else {
                    city.setTag("");
                }
                if (city.length() >= city.getThreshold()) {
                    if (cd.isConnectingToInternet())
                    {//ISTIAQUE
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAUqbnkXE4z-TXleAIbqy8j3PyHjYqUzeg&types=(cities)&sensor=false&components=country:in&input=" + city.getText()).openConnection();
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
                                    cities_adapter.clear();
                                    for (int i = 0; i <= cities_array.length() - 1; i++) {
                                        cities_adapter.add(cities_array.getJSONObject(i).getString("description"));
                                        cities.add(cities_array.getJSONObject(i).getString("description"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                    }

                    else
                    {
                        new AlertDialogManager().showAlertDialog(EditQuery.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
      //  linear_neededBy = (RelativeLayout) findViewById(R.id.linear_date);
        name = (EditText) findViewById(R.id.editTextProductName);
        code = (EditText) findViewById(R.id.editTextCode);
        date = (EditText) findViewById(R.id.editTextDate);
        submit = (Button) findViewById(R.id.buttonSubmitQuery);
        input_Date = (TextInputLayout) findViewById(R.id.textInputDate);
        rg = (RadioGroup) findViewById(R.id.radiogrporder);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton RB1 = (RadioButton) findViewById(R.id.radioButton);

                RadioButton RB2 = (RadioButton) findViewById(R.id.radioButton2);

                if (checkedId == R.id.radioButton) {
                    RB1.setButtonDrawable(R.drawable.checked_form);
                    RB2.setButtonDrawable(R.drawable.unchecked_form);

                } else if (checkedId == R.id.radioButton2) {
                    RB2.setButtonDrawable(R.drawable.checked_form);
                    RB1.setButtonDrawable(R.drawable.unchecked_form);

                }

            }
        });
        submit.setOnClickListener(this);
        catList = new ArrayList<>();
        subCatList = new ArrayList<>();
        input_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                //    newFragment.sh
                newFragment.show(getSupportFragmentManager(), "datePicker");
                newFragment.setCancelable(false);
                SetBack_date(date, getResources().getDrawable(R.drawable.code_bar));
            }
        });
        // getAllowedCategories();
        images = "";

        if (getIntent().getStringExtra("query_id") != null) {
            if (cd.isConnectingToInternet())
            {//ISTIAQUE
                new AsyncTask<Void, Void, Void>() {

                    private Bitmap icon;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progress.setMessage(getResources().getString(R.string.loading));
                        if (!isFinishing())
                            progress.show();
                        icon = imagegallery.get(0);
                        imagegallery.clear();
                        imagepath.clear();

                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        String[] image_urls = getIntent().getStringArrayExtra("image_urls");
                        Log.i("image_urls",image_urls.toString());
                        //   Log.i("image_urls",image_urls.length+"");
                        for (int i = 0; i < image_urls.length; i++) {
                            //Log.i("", "image_url: " + image_urls[i]);
                            if (image_urls[i] != null && image_urls[i].length() > 0 && !image_urls[i].equalsIgnoreCase("")) {
                                File folder = new File(Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WYDR/Images");
                                if (!folder.exists())
                                    folder.mkdirs();
                                String extStorageDirectory = folder.toString();

                                File mFileTemp;
                                //    Log.i(TAG, " image  is " + path.trim().replace("?", "/").split("/")[6]);
                                mFileTemp = new File(extStorageDirectory, image_urls[i].trim().substring(image_urls[i].lastIndexOf('/') + 1, image_urls[i].lastIndexOf('?')));

                                if (mFileTemp.exists()) {
                                    Log.e("VALUE--", "1");
                                    helper.DownloadFromUrl(mFileTemp.getAbsolutePath(), image_urls[i]);
                                } else {
                                    Log.e("VALUE--", "2");
                                    mFileTemp = new File(extStorageDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                                    helper.DownloadFromUrl(mFileTemp.getAbsolutePath(), image_urls[i]);
                                }
                                imagepath.add(mFileTemp.getAbsolutePath());
                                Bitmap bitmap = helper.decodeSampledBitmapFromResource(mFileTemp.getAbsolutePath(),
                                        400, 200);
                                imagegallery.add(bitmap);
                            }


                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (!isFinishing()) {
                            imagegallery.add(icon);
                            adapter = new ImageViewPagerAdapter(EditQuery.this, imagegallery);
                            viewPager.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progress.dismiss();
                            titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
                            titleIndicator.setViewPager(viewPager);
                            super.onPostExecute(aVoid);
                        }

                    }
                }.execute();
            }

            else
            {
                new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));
            }
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_query));
            //  Log.i("", "Editing query: " + getIntent().getStringExtra("query_id"));
            name.setText(getIntent().getStringExtra("title"));
            code.setText(getIntent().getStringExtra("prod_code"));
            quantity.setText(getIntent().getStringExtra("quantity"));
            if (getIntent().getStringExtra("query_type").equals("One")) {
                rg.check(R.id.radioButton);
            } else {
                rg.check(R.id.radioButton2);
            }
            date.setText(getIntent().getStringExtra("needed_by").substring(0, getIntent().getStringExtra("needed_by").indexOf(" ") + 1));
            city.setText(getIntent().getStringExtra("location"));
            submit.setText("Update");
            par_cat = getIntent().getStringExtra("category_id");
            sub_cat = getIntent().getStringExtra("sub_category_id");
            child_cat = getIntent().getStringExtra("child_category_id");
            leaf_cat = getIntent().getStringExtra("grand_child_category_id");
            Log.i("IDS -- ", par_cat + "/" + sub_cat + "/" + child_cat + "/" + leaf_cat);
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditQuery.this);
        builder.setTitle(getString(R.string.discard_query)).setMessage(getString(R.string.want_discard_query))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imagegallery.clear();
                        imagepath.clear();
                        adapter.notifyDataSetChanged();
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //finish();
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                AlertDialog.Builder builder = new AlertDialog.Builder(EditQuery.this);
                builder.setTitle(getString(R.string.discard_query)).setMessage(getString(R.string.want_discard_query))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                imagegallery.clear();
                                imagepath.clear();
                                adapter.notifyDataSetChanged();
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                    }
                });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetBack(TextView tv, Drawable drawable) {
        Log.e("clicked", tv.toString());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            tv.setBackgroundDrawable(drawable);
        } else {
            tv.setBackground(drawable);
        }

    }

    private boolean validate(TextView tv, String Message) {
        if (tv.getText().toString().equals("")) {
            new AlertDialogManager().showAlertDialog(EditQuery.this, getResources().getString(R.string.alert), Message);

            SetBack(tv, getResources().getDrawable(R.drawable.error_bar));
            tv.requestFocus();
            // runThread("ENTER NAME");
            return false;

        }


        return true;
    }

    private boolean validate_date(TextView tv, String Message) {
        if (tv.getText().toString().equals("")) {
            new AlertDialogManager().showAlertDialog(EditQuery.this, getResources().getString(R.string.alert), Message);

            SetBack_date(date, getResources().getDrawable(R.drawable.error_bar));
            tv.requestFocus();
            // runThread("ENTER NAME");
            return false;

        }


        return true;
    }

    private void SetBack_date(TextView tv, Drawable drawable) {
        Log.e("clicked", tv.toString());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            input_Date.setBackgroundDrawable(drawable);
        } else {
            input_Date.setBackground(drawable);
        }

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatewithTextInput(TextView tv, String Message) {
        if (tv.getText().toString().equals("")) {
            //    new AlertDialogManager().showAlertDialog(SubmitQuery.this, "Alert", Message);
            //  SetBack(tv, getResources().getDrawable(R.drawable.error_bar));
            //tv.requestFocus();
            // runThread("ENTER NAME");
            tv.setError(Message);
            requestFocus(tv);
            //tv.requestFocus();
            return false;

        }


        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmitQuery:
                String dateString = date.getText().toString().trim();

                if (cat.getSelectedItem() == null) {
                    cat.setError(getString(R.string.select_meta_category));
                    requestFocus(cat);
                    return;

                }
                if (subCat.getSelectedItem() == null) {
                    subCat.setError(getString(R.string.select_category));
                    requestFocus(subCat);
                    return;
                    // has_error = true;
                }
                if (childcat.getVisibility() == View.VISIBLE && childcat.getSelectedItem() == null) {
                    childcat.setError(getString(R.string.select_leaf));
                    requestFocus(childcat);
                    return;
                    //has_erro = true;
                }
                if (last_spin.getVisibility() == View.VISIBLE && last_spin.getSelectedItem() == null) {
                    last_spin.setError(getString(R.string.select_leaf));
                    requestFocus(last_spin);
                    return;
                    //has_erro = true;
                }
                if (validatewithTextInput(name, getResources().getString(R.string.empty_product_name))) {
                    if (validatewithTextInput(code, getResources().getString(R.string.empty_product_code))) {
                        if (validatewithTextInput(quantity, getResources().getString(R.string.empty_quantity))) {
                            if (validatewithTextInput(city, getResources().getString(R.string.empty_location))) {
                                if (ValidationUtil.isValidNumber(quantity.getText().toString())) {
                                    if (validate_date(date, "Please Pick Date")) {
                                        Calendar c1 = Calendar.getInstance();
                                        Date today = c1.getTime();
                                        String day = sdf.format(today);
                                        Date datePost = null;
                                        Date date1 = null;
                                        try {
                                            date1 = sdf.parse(dateString);
                                            datePost = sdf.parse(day);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (date1.compareTo(datePost) > 0) {
                                            if (cd.isConnectingToInternet()) {

                                                try {
                                                    new EditQueryServer().execute();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(EditQuery.this, getResources().getString(R.string.oops), getResources().getString(R.string.no_internet_conn));

                                            }

                                        } else {
                                            input_Date.setError(getResources().getString(R.string.date_greater_todaydate));
                                            requestFocus(date);
                                            SetBack_date(date, getResources().getDrawable(R.drawable.error_bar));
                                            //    date.requestFocus();
                                        }
                                    }
                                } else {
                                    quantity.setError(getResources().getString(R.string.invalid_quantity));
                                    requestFocus(quantity);
                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                        quantity.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                    } else {
                                        quantity.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                    }
                                }
                            }
                        }
                    }

                }


                break;


            case R.id.editTextProductName:
                Log.e("nme", "click");
                SetBack(name, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.editTextCode:
                Log.e("nme", "click");
                SetBack(code, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.editTextQuantity:
                Log.e("nme", "click");
                SetBack(quantity, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.editTextLocation:
                Log.e("nme", "click");
                SetBack(city, getResources().getDrawable(R.drawable.code_bar));
                break;
        }

    }

    private class EditQueryServer extends AsyncTask<JSONObject, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.setMessage(getResources().getString(R.string.updating));
            if (!isFinishing())
                progress.show();
        }

        @Override
        protected String doInBackground(JSONObject... args) {
            return uploadFile();
        }

        @Override
        protected void onPostExecute(String s) {

            if (!isFinishing()) {
                progress.dismiss();
                alertDialog.setTitle(getResources().getString(R.string.success)).setMessage(getString(R.string.query_success)).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(EditQuery.this, MyQuery.class));
                        finish();
                    }
                });
                alertDialog.show();
                super.onPostExecute(s);
            }


        }

        private String uploadFile() {
            String responseString = null;


            HttpClient httpclient = new DefaultHttpClient();

            String url = AppUtil.URL + "queries";
            url += "/" + getIntent().getStringExtra("query_id");
            HttpEntityEnclosingRequestBase httppost;
           /* if (getIntent().getStringExtra("query_id") != null) {
                url += "/" + getIntent().getStringExtra("query_id");
                httppost = new HttpPut(url);
            } else {
                httppost = new HttpPost(url);
            }*/
            //  Log.i("", "update url: " + url);
            httppost = new HttpPut(url);
            // httppost.addHeader();
            httppost.addHeader("Connection", "Keep-Alive");
            httppost.addHeader("Accept", "*/*");
            httppost.addHeader("Content-Type", "multipart/form-data");
            httppost.addHeader("Authorization", helper.getB64Auth(EditQuery.this));
            //  httppost.set

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                //  publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                //    Log.i("imagepath", imagepath.size() + "");
                if (imagepath.size() == 1)
                    default_position = 0;

                for (int i = 0; i < imagepath.size(); i++) {
                    //  String str = decodeFile(imagepath.get(i));
                    //   Log.i("path", imagepath.get(i));
                    File sourceFile = new File(imagepath.get(i));

                    entity.addPart("images[" + i + "][image]", new FileBody(sourceFile));
                    if (i == default_position) {
                        Log.e("1--", "1");
                        entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("1"));

                    } else {
                        Log.e("1--", "2");
                        entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("0"));
                    }

                }
                String share = "";
                int radioButtonID2 = rg.getCheckedRadioButtonId();
                View radioButton2 = rg.findViewById(radioButtonID2);
                int idx = rg.indexOfChild(radioButton2);

                if (idx == 0)
                    share = "One";
                else
                    share = "Recurring";
                entity.addPart("vendor_id", new StringBody(user_id));
                entity.addPart("sub_category_id", new StringBody(selectedcatval));
                entity.addPart("product_name", new StringBody(name.getText().toString().trim()));
                entity.addPart("order_type", new StringBody(share));
                entity.addPart("quantity", new StringBody(quantity.getText().toString()));
                entity.addPart("product_code", new StringBody(code.getText().toString()));
                entity.addPart("needed_date", new StringBody(date.getText().toString()));
                entity.addPart("location", new StringBody(city.getText().toString()));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                Log.e("ENTITY", entity.toString());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                  Log.i("sttscode", statusCode + "");
                Log.i("selectedcatval", selectedcatval + "");
                if (statusCode == 200 || statusCode == 201 || statusCode == 202 || statusCode == 203 || statusCode == 204) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(EditQuery.this);
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
                //    Log.i("responseString   ", responseString + "");
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        String str;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            str = sdf.format(new Date(year - 1900, month, day));
            // date.setText("" + str);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            str = sdf.format(new Date(year - 1900, month, day));
            // Date date =
            date.setText("" + str + "  ");
        }
    }


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(2);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(helper.getOutputMediaFile(type));
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
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
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

    public class ImageViewPagerAdapter extends PagerAdapter {
        // Declare Variables
        Context context;


        List<Bitmap> flag;
        LayoutInflater inflater;

        public ImageViewPagerAdapter(Context context, List<Bitmap> flag) {
            this.context = context;
            this.flag = flag;


        }

        @Override
        public int getCount() {
            return flag.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            final ImageView imgflag, optionflag;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.imgeviewpager_item, container,
                    false);

            imgflag = (ImageView) itemView.findViewById(R.id.flag);
            imgflag.setTag(position);
            Log.d("POSIT--", position + "/" + flag.get(position));
            if (position == (flag.size() - 1)) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                imgflag.setLayoutParams(layoutParams);
            }
            // Capture position and set to the ImageView
            imgflag.setImageBitmap(flag.get(position));
            optionflag = (ImageView) itemView.findViewById(R.id.flag2);

            optionflag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, optionflag);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_imageslider, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getTitle().toString().equalsIgnoreCase("Set as default")) {
                                default_position = (int) imgflag.getTag();
                            }
                            if (item.getTitle().toString().equalsIgnoreCase("Delete")) {

                                imagegallery.remove((int) imgflag.getTag());
                                imagepath.remove((int) imgflag.getTag());
                                if (imagegallery.size() == 1)
                                    titleIndicator.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }

                            return true;
                        }
                    });

                    popup.show();
                }
            });


            container.addView(itemView);

            if (position == (flag.size() - 1))
                optionflag.setVisibility(View.GONE);


            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            container.removeView((RelativeLayout) object);

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void prepareRequest() {
        if (!isFinishing())
            progress.show();

        RestClient.GitApiInterface service = RestClient.getClient();
        Call<CategoryHolder> call = service.FetchCategory("true", "1", helper.getB64Auth(EditQuery.this), "application/json", "application/json");
        call.enqueue(new Callback<CategoryHolder>() {
            @Override
            public void onResponse(Response response) {


                if (response.isSuccess()) {
                    progress.dismiss();
                    CategoryHolder holder = (CategoryHolder) response.body();
                    ArrayList<CategoryDataModal> catdata;
                    JsonElement element = holder.getCategories();
                    if (element != null && element.isJsonObject()) {
                        try {

                            JSONObject childJSONObject = new JSONObject(element.getAsJsonObject().toString());
                            catdata = new ArrayList<>();
                            Iterator<?> keys = childJSONObject.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                JSONObject js = childJSONObject.getJSONObject(key);
                                CategoryDataModal cd1 = new CategoryDataModal();
                                cd1.setId(js.getString("category_id"));
                                cd1.setName(js.getString("category"));
                                cd1.setParentid(js.getString("parent_id"));
                                cd1.setProduct_count(js.getString("product_count"));
                                if (js.has("subcategories"))
                                    cd1.setHas_child(true);
                                else
                                    cd1.setHas_child(false);
                                catdata.add(cd1);

                                if (js.has("subcategories")) {
                                    JSONArray js1 = js.getJSONArray("subcategories");
                                    for (int k = 0; k < js1.length(); k++) {
                                        JSONObject js2 = js1.getJSONObject(k);
                                        CategoryDataModal cd2 = new CategoryDataModal();
                                        cd2.setId(js2.getString("category_id"));
                                        cd2.setName(js2.getString("category"));
                                        cd2.setParentid(js2.getString("parent_id"));
                                        cd2.setProduct_count(js2.getString("product_count"));
                                        if (js2.has("subcategories"))
                                            cd2.setHas_child(true);
                                        else
                                            cd2.setHas_child(false);
                                        catdata.add(cd2);

                                        if (js2.has("subcategories")) {
                                            JSONArray js3 = js2.getJSONArray("subcategories");
                                            for (int l = 0; l < js3.length(); l++) {
                                                JSONObject js4 = js3.getJSONObject(l);
                                                CategoryDataModal cd3 = new CategoryDataModal();
                                                cd3.setId(js4.getString("category_id"));
                                                cd3.setName(js4.getString("category"));
                                                cd3.setParentid(js4.getString("parent_id"));
                                                cd3.setProduct_count(js4.getString("product_count"));
                                                if (js4.has("subcategories"))
                                                    cd3.setHas_child(true);
                                                else
                                                    cd3.setHas_child(false);

                                                catdata.add(cd3);
                                                if (js4.has("subcategories")) {
                                                    JSONArray js6 = js4.getJSONArray("subcategories");
                                                    for (int t = 0; t < js6.length(); t++) {
                                                        JSONObject js7 = js6.getJSONObject(t);
                                                        CategoryDataModal cd4 = new CategoryDataModal();
                                                        cd4.setId(js7.getString("category_id"));
                                                        cd4.setName(js7.getString("category"));
                                                        cd4.setParentid(js7.getString("parent_id"));
                                                        cd4.setProduct_count(js7.getString("product_count"));
                                                        if (js7.has("subcategories"))
                                                            cd4.setHas_child(true);
                                                        else
                                                            cd4.setHas_child(false);
                                                        catdata.add(cd4);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (catdata != null) {
                                try {

                                    String time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
                                    for (int s = 0; s < catdata.size(); s++) {
                                        ContentValues values = new ContentValues();
                                        values.put(CategoryTable.KEY_CATEGORY_ID, catdata.get(s).getId());
                                        values.put(CategoryTable.KEY_CATEGORY_NAME, catdata.get(s).getName());
                                        values.put(CategoryTable.KEY_HAS_CHILD, catdata.get(s).getHas_child());
                                        values.put(CategoryTable.KEY_PARENT_ID, catdata.get(s).getParentid());
                                        values.put(CategoryTable.KEY_PRODUCT_COUNT, catdata.get(s).getProduct_count());
                                        values.put(CategoryTable.KEY_TIMESTAMP, time);
                                        Log.i("ADDPRODUCT", "DATA -- > " + catdata.get(s).getId() + "/" + catdata.get(s).getName() + "/" + catdata.get(s).getHas_child() + "/" + catdata.get(s).getParentid() + "/" + catdata.get(s).getProduct_count());
                                        int a = getContentResolver().update(MyContentProvider.CONTENT_URI_Category, values, CategoryTable.KEY_CATEGORY_ID + "=?", new String[]{catdata.get(s).getId()});
                                        if (a == 0)
                                            getContentResolver().insert(MyContentProvider.CONTENT_URI_Category, values);
                                    }
                                    getContentResolver().delete(MyContentProvider.CONTENT_URI_Category, CategoryTable.KEY_TIMESTAMP + "!=? or " + CategoryTable.KEY_TIMESTAMP + " is null", new String[]{time});

                                } catch (android.database.sqlite.SQLiteConstraintException e) {
                                    Log.e("EditQuery", "SQLiteConstraintException:" + e.getMessage());
                                } catch (android.database.sqlite.SQLiteException e) {
                                    Log.e("EditQuery", "SQLiteException:" + e.getMessage());
                                } catch (Exception e) {
                                    Log.e("EditQuery", "Exception:" + e.getMessage());
                                }
                            }
                            Cursor countCursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_ID}, null, null, null);
                            if (countCursor.getCount() > 0) {
                                BringCategory();
                            } else {
                                new AlertDialogManager().showAlertDialog(EditQuery.this , getString(R.string.oops),
                                        getString(R.string.no_cat_found));
                            }
                            countCursor.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                } else {
                    int statusCode = response.code();
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(EditQuery.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    } else {
                        if (EditQuery.this != null && !EditQuery.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(EditQuery.this,
                                    getString(R.string.error),
                                    getString(R.string.server_error));
                        }
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {

                progress.dismiss();
                if (EditQuery.this != null && !EditQuery.this.isFinishing()) {

                    new AlertDialogManager().showAlertDialog(EditQuery.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("EditQuery");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}