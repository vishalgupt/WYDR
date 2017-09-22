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
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import wydr.sellers.R;
import wydr.sellers.acc.AndroidMultiPartEntity;
import wydr.sellers.acc.ScalingUtilities;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.adapter.CustomSpinAdapter;
import wydr.sellers.gson.CategoryHolder;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.ProdCatModal;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;
import wydr.sellers.network.RestClient;
import wydr.sellers.network.SessionManager;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CategoryTable;
import wydr.sellers.slider.CirclePageIndicator;
import wydr.sellers.slider.LoginDB;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.syncadap.AlteredCatalog;
import wydr.sellers.syncadap.FeedCatalog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public class AddProduct extends AppCompatActivity implements View.OnClickListener
{
    public static final Uri CONTENT_URI = Uri.parse("content://eu.janmuller.android.simplecropimage.example/");
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 200;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 201;
    public static int default_position = 0;
    final int DESIREDWIDTH = 800;
    final int DESIREDHEIGHT = 600;
    final int PIC_CROP = 2;
    public Toolbar mToolbar;
    public JSONArray jsonMainArr;
    public EditText pName, pCode, pMrp, pSp, pQty, pMin, pDesc;
    public RadioGroup scope;
    public ArrayList<String> category, category2, category3, category4, subcat;
    public CirclePageIndicator titleIndicator;
    ImageView sliderMenu;
    ListView items;
    String calFlag = "1", message, parentId, childId, subchildId, images, selectedparent = "", selectedRoot = "",
            selectedchild = "", selectedGrandChild = "", selectedcatval = "", visibility = "", time, rString = "";
    AlertDialog.Builder alertDialog;
    ConnectionDetector cd;
    Helper helper = new Helper();
    RadioButton radiopub, radiopri;
    Spinner root_spin, parent_spin, child_spin, last_spin;
    Button submit, skip;
    ImageView menu;
    int[] flag;
    ViewPager viewPager;
    PagerAdapter adapter;
    ArrayList<ProdCatModal> products, products2, products3, products4;
    JSONParser parser;
    SessionManager session;
    long totalSize = 0;
    ArrayList<String> imagepath = new ArrayList<>();
    String return_flag = "";
    private ProgressDialog progress;
    private List<Bitmap> imagegallery = new ArrayList<>();
    private Uri fileUri;
    private String user_comp_id;
    Controller application;
    Tracker mTracker;
    long startTime = 0;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        calFlag = "1";
        setContentView(R.layout.add_product);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        user_comp_id = helper.getDefaults("company_id", AddProduct.this);
        progressStuff();

        inistuff();
        return_flag = getIntent().getStringExtra("Return");


        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon);

        imagegallery.add(icon);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImageViewPagerAdapter(AddProduct.this, imagegallery);
        viewPager.setAdapter(adapter);
        titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(viewPager);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        tooltitle.setText(getString(R.string.add_product));

        bar.setTitle("");


        submit.setOnClickListener(this);
        skip.setOnClickListener(this);
        radiopub.setOnClickListener(this);
        radiopri.setOnClickListener(this);
        root_spin
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {


                        category2.clear();
                        products2.clear();
                        category3.clear();
                        products3.clear();
                        category4.clear();
                        products4.clear();
                        selectedRoot = products.get(position).getId();
                        selectedparent = "";
                        selectedchild = "";
                        selectedGrandChild = "";
                        BringSubCategory(products.get(position).getId());
                        parent_spin.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
        parent_spin
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        Log.d("root_spin", root_spin.getSelectedItem().toString());
                        if (!root_spin.getSelectedItem().toString().equalsIgnoreCase("SELECT META CATEGORY")) {
                            category3.clear();
                            products3.clear();
                            category4.clear();
                            products4.clear();
                            parent_spin.setEnabled(true);
                            selectedparent = products2.get(position).getId();

                            selectedchild = "";
                            selectedGrandChild = "";
                            selectedcatval = products2.get(position).getId();
                            Log.d("tabela", products2.get(position).getName() + String.valueOf(products2.get(position).getHas_child()));
                            if (products2.get(position).getHas_child()) {
                                child_spin.setVisibility(View.VISIBLE);
                                BringSubSubCategory(products2.get(position).getId());
                                child_spin.setSelection(0);

                            } else
                                child_spin.setVisibility(View.GONE);
                            last_spin.setVisibility(View.GONE);
                        } else {
                            parent_spin.setEnabled(false);
                            child_spin.setVisibility(View.GONE);
                            last_spin.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        child_spin
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        Log.d("root_spin", root_spin.getSelectedItem().toString());
                        if (!parent_spin.getSelectedItem().toString().equalsIgnoreCase("SELECT CATEGORY")) {

                            category4.clear();
                            products4.clear();
                            child_spin.setEnabled(true);

                            selectedGrandChild = "";
                            selectedchild = products3.get(position).getId();
                            selectedcatval = products3.get(position).getId();
                            Log.d("tabela", products3.get(position).getName() + String.valueOf(products3.get(position).getHas_child()));
                            if (products3.get(position).getHas_child()) {
                                last_spin.setVisibility(View.VISIBLE);
                                BringLastCategory(products3.get(position).getId());
                                last_spin.setSelection(0);

                            } else
                                last_spin.setVisibility(View.GONE);

                        } else {
                            child_spin.setEnabled(false);
                            last_spin.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        last_spin
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        selectedGrandChild = products4.get(position).getId();
                        selectedcatval = products4.get(position).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imagegallery.clear();
        imagepath.clear();
        adapter.notifyDataSetChanged();
        progress.dismiss();
    }


    @Override
    public void onBackPressed()
    {
       /* if (EmptyData()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);
            builder.setTitle(getString(R.string.save_changes))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.save_close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (ValidNumbers())
                                SaveAndClose();
                        }
                    }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    if (return_flag.equalsIgnoreCase("1")) {
                        startActivity(new Intent(AddProduct.this, MyCatalog.class));
                        finish();
                    } else {
                        Intent intent = NavUtils.getParentActivityIntent(AddProduct.this);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NavUtils.navigateUpTo(AddProduct.this, intent);
                        finish();
                    }

                }
            });
            builder.show();
        } else {
            imagegallery.clear();
            imagepath.clear();
            adapter.notifyDataSetChanged();
            if (return_flag.equalsIgnoreCase("1")) {
                startActivity(new Intent(AddProduct.this, MyCatalog.class));
                finish();
            } else {
                Intent intent = NavUtils.getParentActivityIntent(AddProduct.this);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(AddProduct.this, intent);
                finish();
            }
        }
*/
        super.onBackPressed();
    }

    private void progressStuff() {
        // TODO Auto-generated method stub

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
            AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);
            builder.setTitle(getString(R.string.get_image_from))
                    .setCancelable(true)
                    .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(galleryIntent, GALLERY_IMAGE_REQUEST_CODE);
                        }
                    }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    captureImage();
                }
            });

            builder.show();
        } else {
            Intent showImage = new Intent(AddProduct.this, ImageActivity.class);
            showImage.putStringArrayListExtra("bitmap", imagepath);
            startActivity(showImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //     Log.i("RESULTCODE--", resultCode + "");
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                //    Log.i("fileUri--", fileUri.toString() + "");
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
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
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                            Bundle extras = data.getExtras();
                            selectedBitmap = extras.getParcelable("data");
                        } else {

                            selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        }
                        imagegallery.add(selectedBitmap);
                        imagepath.add(decodeFileCrop(selectedBitmap));

                        Bitmap s = imagegallery.get(imagegallery.size() - 2);
                        imagegallery.remove(imagegallery.size() - 2);
                        imagegallery.add(s);

                        adapter.notifyDataSetChanged();
                        titleIndicator.setVisibility(View.VISIBLE);
                        // user_pic.setImageBitmap(selectedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (requestCode == GALLERY_IMAGE_REQUEST_CODE && data != null) {
                // Get the Image from data
                //Log.d("HERE","1");
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                if (cursor != null) {

                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    images = cursor.getString(column_index);
                    cursor.close();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        performCrop(data.getData());
                    } else {
                        performCropImage(data.getData());
                    }

                }
            }
        } else {
            // failed to record video
            Toast.makeText(AddProduct.this, getString(R.string.fail_load_image),
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

    private void inistuff() {
        radiopub = (RadioButton) findViewById(R.id.radioPub);
        radiopri = (RadioButton) findViewById(R.id.radioPri);
        pName = (EditText) findViewById(R.id.txtprodname);
        pCode = (EditText) findViewById(R.id.txtprodCode);
        pMrp = (EditText) findViewById(R.id.txtmrp);
        pSp = (EditText) findViewById(R.id.txtsp);
        pQty = (EditText) findViewById(R.id.txtqty);
        pMin = (EditText) findViewById(R.id.txtminqty);
        pDesc = (EditText) findViewById(R.id.txtdesc);
        pName.setOnClickListener(this);
        pCode.setOnClickListener(this);
        pMrp.setOnClickListener(this);
        pQty.setOnClickListener(this);
        pMin.setOnClickListener(this);
        pDesc.setOnClickListener(this);
        pSp.setOnClickListener(this);
        images = "";

        scope = (RadioGroup) findViewById(R.id.radiogrp1);
        parentId = "";
        childId = "";
        subchildId = "";
        subcat = new ArrayList<>();
        submit = (Button) findViewById(R.id.btnsub);
        skip = (Button) findViewById(R.id.btnskip);

        parent_spin = (Spinner) findViewById(R.id.spinner2);
        root_spin = (Spinner) findViewById(R.id.spinner1);
        child_spin = (Spinner) findViewById(R.id.spinner3);
        last_spin = (Spinner) findViewById(R.id.spinner4);
        parent_spin.setFocusable(false);
        parent_spin.setFocusableInTouchMode(true);
        root_spin.setFocusable(true);
        root_spin.setFocusableInTouchMode(true);
        child_spin.setFocusable(true);
        child_spin.setFocusableInTouchMode(true);
        last_spin.setFocusable(true);
        last_spin.setFocusableInTouchMode(true);
        pName.setSelected(false);
        products = new ArrayList<ProdCatModal>();
        // Create an array to populate the spinner
        category = new ArrayList<String>();
        products2 = new ArrayList<ProdCatModal>();
        // Create an array to populate the spinner
        category2 = new ArrayList<String>();
        products3 = new ArrayList<ProdCatModal>();
        // Create an array to populate the spinner
        category3 = new ArrayList<String>();

        products4 = new ArrayList<ProdCatModal>();
        // Create an array to populate the spinner
        category4 = new ArrayList<String>();
        cd = new ConnectionDetector(getApplicationContext());

        time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        getAllowedCategories();

    }

    private void getAllowedCategories() {
/*

        Cursor cursor2 = getContentResolver().query(MyContentProvider.CONTENT_URI_Login, new String[]{LoginDB.KEY_SCOPE}, LoginDB.KEY_USERID + "=?", new String[]{helper.getDefaults("user_id", getApplicationContext())}, null);
        if (cursor2.getCount() > 0) {
            if (cursor2.moveToFirst()) {
                visibility = cursor2.getString(0);

                if (visibility.equalsIgnoreCase("private")) {
                    radiopri.setChecked(true);
                    radiopub.setChecked(false);
                    radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                } else {
                    radiopub.setChecked(true);
                    radiopri.setChecked(false);
                    radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }


            }
            cursor2.close();
        }
        Cursor countCursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_ID}, null, null, null);
        if (countCursor.getCount() == 0) {
            prepareRequest();
        } else {
           BringCategory();
        }
        countCursor.close();

*/
        prepareRequest();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnsub:
                //       Log.i("AddPRoduct", MyContentProvider.CONTENT_URI_ALTER.toString());

                if (CheckValidation()) {
                    if (cd.isConnectingToInternet()) {
                        new UploadFileToServer().execute();

                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) AddProduct.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("Add Catalog", "Move", "AddProduct");
                        // Build and send an App Speed.
                        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Add Catalog Submit").setValue(System.currentTimeMillis() - startTime).build());
                        /*******************************ISTIAQUE***************************************/

                    } else {
                        uploadLocally();

                        /*******************************ISTIAQUE***************************************/
                        application = (Controller) AddProduct.this.getApplication();
                        mTracker = application.getDefaultTracker();
                        application.trackEvent("Add Catalog", "Move", "AddProduct");
                        // Build and send an App Speed.
                        mTracker.send(new HitBuilders.TimingBuilder().setCategory("Add Catalog Submit").setValue(System.currentTimeMillis() - startTime).build());
                        /*******************************ISTIAQUE***************************************/

                    }
                }
                break;
            case R.id.btnskip:

                /*******************************ISTIAQUE***************************************/
                application = (Controller) AddProduct.this.getApplication();
                mTracker = application.getDefaultTracker();
                application.trackEvent("Add Catalog", "Move", "Upload Later");
                /*******************************ISTIAQUE***************************************/

                if (EmptyData()) {
                    if (ValidNumbers())
                        SaveAndClose();
                } else {
                    new AlertDialogManager().showAlertDialog(AddProduct.this, getResources().getString(R.string.oops), getResources().getString(R.string.atleast_one_field));
                }

                break;
            case R.id.radioPub:
                radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
            case R.id.radioPri:
                radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
            case R.id.txtprodname:
                Log.d("nme", "click");
                helper.SetBack(pName, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtprodCode:
                Log.d("nme", "click");
                helper.SetBack(pCode, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtmrp:
                Log.d("nme", "click");
                helper.SetBack(pMrp, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtsp:
                Log.d("nme", "click");
                helper.SetBack(pSp, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtqty:
                Log.d("nme", "click");
                helper.SetBack(pQty, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtminqty:
                helper.SetBack(pMin, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtdesc:
                helper.SetBack(pDesc, getResources().getDrawable(R.drawable.code_bar));
                break;
        }
    }

    private void SaveAndClose() {
        int radioButtonID = scope.getCheckedRadioButtonId();
        View radioButton = scope.findViewById(radioButtonID);
        int idx = scope.indexOfChild(radioButton);
        if (idx == 0)
            visibility = "Public";
        else
            visibility = "Private";

        rString = "";
        for (String each : imagepath) {
            rString = rString + each + "~";
        }
        Log.e("rString", rString);
        if (rString.length() > 1)
            rString = rString.substring(0, rString.length() - 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, "");
        contentValues.put(AlteredCatalog.COLUMN_TITLE, pName.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_CODE, pCode.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_IMAGEPATH, rString.toString().trim());
        contentValues.put(AlteredCatalog.COLUMN_GRANDPARENTCAT, selectedRoot);
        contentValues.put(AlteredCatalog.COLUMN_PARENTCAT, selectedparent);
        contentValues.put(AlteredCatalog.COLUMN_CHILDCAT, selectedchild);
        contentValues.put(AlteredCatalog.COLUMN_GRANDCHILDCAT, selectedGrandChild);
        contentValues.put(AlteredCatalog.COLUMN_CATEGORY_ID, selectedcatval);
        contentValues.put(AlteredCatalog.COLUMN_MRP, pMrp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_SP, pSp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_QTY, pQty.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_MINQTY, pMin.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_DESC, pDesc.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_VISIBILTY, visibility);
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_FLAG, "4");
        contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "2");
        contentValues.put(AlteredCatalog.COLUMN_DEFAULT_POSITION, default_position);
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        contentValues.put(AlteredCatalog.COLUMN_CREATEDAT, time);
        contentValues.put(AlteredCatalog.COLUMN_UPDATED, time);
        contentValues.put(AlteredCatalog.COLUMN_COMPANY_ID, helper.getDefaults("company_id", getApplicationContext()));
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, "");
        if (imagepath.size() > 0)
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));


        Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI_ALTER, contentValues);
        if (uri != null) {
            alertDialog.setTitle(getString(R.string.status));
            alertDialog.setMessage(getString(R.string.product_saved));
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Your positive action
                    startActivity(new Intent(AddProduct.this, MyCatalog.class).putExtra("draft", "1"));
                    finish();
                }
            });
            alertDialog.show();
        } else {
            alertDialog.setTitle(getString(R.string.status));
            alertDialog.setMessage(getString(R.string.product_not_saved));
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Your positive action
                    if (return_flag.equalsIgnoreCase("1")) {
                        startActivity(new Intent(AddProduct.this, MyCatalog.class));
                        finish();
                    } else {
                        Intent intent = NavUtils.getParentActivityIntent(AddProduct.this);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NavUtils.navigateUpTo(AddProduct.this, intent);
                        finish();
                    }
                }
            });
            alertDialog.show();
        }


    }

    private void uploadLocally() {

        //new AlertDialogManager().showAlertDialog(AddProduct.this, "Sorry", "No internet connection");
        int radioButtonID = scope.getCheckedRadioButtonId();
        View radioButton = scope.findViewById(radioButtonID);
        int idx = scope.indexOfChild(radioButton);
        if (idx == 0)
            visibility = "Public";
        else
            visibility = "Private";

        rString = "";
        for (String each : imagepath) {
            rString = rString + each + "~";
        }
        Log.e("rString", rString);
        if (rString.length() > 1)
            rString = rString.substring(0, rString.length() - 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, "");
        contentValues.put(AlteredCatalog.COLUMN_TITLE, pName.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_CODE, pCode.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_IMAGEPATH, rString.toString().trim());
        contentValues.put(AlteredCatalog.COLUMN_GRANDPARENTCAT, selectedRoot);
        contentValues.put(AlteredCatalog.COLUMN_PARENTCAT, selectedparent);
        contentValues.put(AlteredCatalog.COLUMN_CHILDCAT, selectedchild);
        contentValues.put(AlteredCatalog.COLUMN_GRANDCHILDCAT, selectedGrandChild);
        contentValues.put(AlteredCatalog.COLUMN_CATEGORY_ID, selectedcatval);
        contentValues.put(AlteredCatalog.COLUMN_MRP, pMrp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_SP, pSp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_QTY, pQty.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_MINQTY, pMin.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_DESC, pDesc.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_VISIBILTY, visibility);
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_FLAG, "1");
        contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "1");
        contentValues.put(AlteredCatalog.COLUMN_DEFAULT_POSITION, default_position);
        contentValues.put(AlteredCatalog.COLUMN_CREATEDAT, time);
        contentValues.put(AlteredCatalog.COLUMN_UPDATED, "0");
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, "0");
        if (imagepath.size() > 0)
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));
        else
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, "");
        contentValues.put(AlteredCatalog.COLUMN_COMPANY_ID, helper.getDefaults("company_id", getApplicationContext()));
        Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI_ALTER, contentValues);

        if (uri != null) {
            Log.i("Addproduct", "Updating product_id loclly");
            int county = 0;
            Cursor update_cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, new String[]{AlteredCatalog._ID, AlteredCatalog.COLUMN_TITLE}, AlteredCatalog.COLUMN_TITLE + "=? and " + AlteredCatalog.COLUMN_CREATEDAT + "=?", new String[]{pName.getText().toString(), time}, null);
            assert update_cursor != null;

            if (update_cursor.moveToNext()) {
                String id = update_cursor.getString(0);
                String name = update_cursor.getString(1);
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, id);
                county = getContentResolver().update(MyContentProvider.CONTENT_URI_ALTER, contentValues1, AlteredCatalog.COLUMN_TITLE + "=? and " + AlteredCatalog.COLUMN_CREATEDAT + "=?", new String[]{pName.getText().toString(), time});


            }
            update_cursor.close();
            if (county == 1) {
                alertDialog.setTitle(getString(R.string.status));
                alertDialog.setMessage(getString(R.string.product_added));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Your positive action
                        startActivity(new Intent(AddProduct.this, MyCatalog.class));
                        finish();
                    }
                });
                alertDialog.show();
            } else {
                alertDialog.setTitle(getString(R.string.status));
                alertDialog.setMessage(getString(R.string.product_add_failed));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Your positive action
                        startActivity(new Intent(AddProduct.this, MyCatalog.class));
                        finish();
                    }
                });
                alertDialog.show();
            }

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                /*if (EmptyData()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);
                    builder.setTitle(getString(R.string.save_changes))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.save_close), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (ValidNumbers())
                                        SaveAndClose();
                                }
                            }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            imagepath.clear();
                            imagegallery.clear();
                            adapter.notifyDataSetChanged();
                            if (return_flag.equalsIgnoreCase("1")) {
                                startActivity(new Intent(AddProduct.this, MyCatalog.class));
                                finish();
                            } else {
                                Intent intent = NavUtils.getParentActivityIntent(AddProduct.this);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                NavUtils.navigateUpTo(AddProduct.this, intent);
                                finish();
                            }
                        }
                    });
                    builder.show();

                } else {
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    if (return_flag.equalsIgnoreCase("1")) {
                        startActivity(new Intent(AddProduct.this, MyCatalog.class));
                        finish();
                    } else {
                        Intent intent = NavUtils.getParentActivityIntent(AddProduct.this);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NavUtils.navigateUpTo(AddProduct.this, intent);
                        finish();
                    }
                }
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }


    private void BringCategory() {
        Cursor parent = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, null);
        // assert parent != null;
        products.clear();
        category.clear();
        String id, title, has_child;
        ProdCatModal pm_def = new ProdCatModal();
        pm_def.setName("SELECT META CATEGORY");
        pm_def.setId("0");
        pm_def.setHas_child(false);
        products.add(pm_def);
        category.add("SELECT META CATEGORY");
        while (parent.moveToNext()) {
            id = parent.getString(0);
            title = parent.getString(1);
            has_child = parent.getString(2);
            ProdCatModal pm = new ProdCatModal();
            pm.setName(title);
            pm.setId(id);
            // Log.i("Addproduct ", id + " / " + title + " / " + has_child);
            if (has_child.equalsIgnoreCase("1"))
                pm.setHas_child(true);
            else
                pm.setHas_child(false);

//            if (parent_allowedCats.length != 0) {
//                if (Arrays.asList(parent_allowedCats).contains(id)) {
//                    products.add(pm);
//                    category.add(title);
//                }
//            } else {
//                products.add(pm);
//                category.add(title);
//            }
            products.add(pm);
            category.add(title);

        }
        root_spin.setSelection(0, false);
        root_spin.setAdapter(new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category));
        parent.close();
    }

    private void BringSubCategory(String parid) {
        Cursor parent2 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{parid}, null);
        Log.i("Addproduct", parent2.getCount() + "");
        String id, title, has_child;
        products2.clear();
        category2.clear();

        ProdCatModal pm_def = new ProdCatModal();
        pm_def.setName("SELECT CATEGORY");
        pm_def.setId("0");
        pm_def.setHas_child(false);
        products2.add(pm_def);
        category2.add("SELECT CATEGORY");
        while (parent2.moveToNext()) {
            id = parent2.getString(0);
            title = parent2.getString(1);
            has_child = parent2.getString(2);
            ProdCatModal pm = new ProdCatModal();
            pm.setName(title);
            pm.setId(id);

            if (has_child.equalsIgnoreCase("1"))
                pm.setHas_child(true);
            else
                pm.setHas_child(false);

            products2.add(pm);
            category2.add(title);
        }
        parent_spin.setAdapter(new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category2));
        parent2.close();
    }

    private void BringSubSubCategory(String parent) {
        Cursor parent3 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{parent}, null);
        Log.i("Addproduct", parent3.getCount() + "");
        String id, title, has_child;
        products3.clear();
        category3.clear();
        ProdCatModal pm_def = new ProdCatModal();
        pm_def.setName("SELECT LEAF CATEGORY");
        pm_def.setId("0");
        pm_def.setHas_child(false);
        products3.add(pm_def);
        category3.add("SELECT LEAF CATEGORY");
        while (parent3.moveToNext()) {
            id = parent3.getString(0);
            title = parent3.getString(1);
            has_child = parent3.getString(2);
            ProdCatModal pm = new ProdCatModal();
            pm.setName(title);
            pm.setId(id);
            Log.i("Addproduct /", id + " / " + title + " / " + has_child);
            if (has_child.equalsIgnoreCase("1"))
                pm.setHas_child(true);
            else
                pm.setHas_child(false);
            products3.add(pm);
            category3.add(title);
        }
        child_spin.setAdapter(new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category3));
        parent3.close();
    }

    private void BringLastCategory(String parent) {
        Cursor parent4 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{parent}, null);
        Log.i("Addproduct", parent4.getCount() + "");
        String id, title, has_child;
        products4.clear();
        category4.clear();
        ProdCatModal pm_def = new ProdCatModal();
        pm_def.setName("SELECT LEAF CATEGORY");
        pm_def.setId("0");
        pm_def.setHas_child(false);
        products4.add(pm_def);
        category4.add("SELECT LEAF CATEGORY");
        while (parent4.moveToNext()) {
            id = parent4.getString(0);
            title = parent4.getString(1);
            has_child = parent4.getString(2);
            ProdCatModal pm = new ProdCatModal();
            pm.setName(title);
            pm.setId(id);
            Log.i("Addproduct /", id + " / " + title + " / " + has_child);
            if (has_child.equalsIgnoreCase("1"))
                pm.setHas_child(true);
            else
                pm.setHas_child(false);
            products4.add(pm);
            category4.add(title);
        }
        last_spin.setAdapter(new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category4));
        parent4.close();
    }

    private boolean CheckValidation() {
        if (!ValidationUtil.isNull(pName.getText().toString())) {
            if (!ValidationUtil.isNull(pCode.getText().toString())) {
                if (!root_spin.getSelectedItem().toString()
                        .equalsIgnoreCase("SELECT META CATEGORY")) {
                    if (!parent_spin.getSelectedItem().toString()
                            .equalsIgnoreCase("SELECT CATEGORY")) {
                        if (child_spin.getSelectedItem() != null) {
                            if (!child_spin.getSelectedItem().toString()
                                    .equalsIgnoreCase("SELECT LEAF CATEGORY")) {
                                if (last_spin.getSelectedItem() != null) {
                                    if (!last_spin.getSelectedItem().toString()
                                            .equalsIgnoreCase("SELECT LEAF CATEGORY")) {
                                        if (!ValidationUtil.isNull(pMrp.getText().toString())) {
                                            if (!ValidationUtil.isNull(pSp.getText().toString())) {
                                                if (!ValidationUtil.isNull(pQty.getText().toString())) {
                                                    if (!ValidationUtil.isNull(pMin.getText().toString())) {
                                                        if (ValidationUtil.isValidNumber(pMrp.getText().toString())) {
                                                            if (ValidationUtil.isValidNumber(pSp.getText().toString())) {
                                                                if (ValidationUtil.isValidNumber(pQty.getText().toString())) {
                                                                    if (ValidationUtil.isValidNumber(pMin.getText().toString())) {

                                                                        if (Double.parseDouble(pMrp.getText().toString()) >= Double.parseDouble(pSp.getText().toString())) {

                                                                            if (Double.parseDouble(pQty.getText().toString()) >= Double.parseDouble(pMin.getText().toString())) {

                                                                                if (Double.parseDouble(pQty.getText().toString()) > 0)
                                                                                    if (Double.parseDouble(pMrp.getText().toString()) > 0) {
                                                                                        if (Double.parseDouble(pSp.getText().toString()) > 0) {
                                                                                            if (Double.parseDouble(pMin.getText().toString()) > 0) {
                                                                                                return true;
                                                                                            } else {
                                                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.moq_not_0));
                                                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                                    pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                                } else {
                                                                                                    pMin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                                }
                                                                                                pMin.requestFocus();
                                                                                            }
                                                                                        } else {
                                                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.sp_cannot_0));
                                                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                                pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                            } else {
                                                                                                pSp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                            }
                                                                                            pSp.requestFocus();
                                                                                        }
                                                                                    } else {
                                                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.mrp_cannot_0));
                                                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                            pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                        } else {
                                                                                            pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                        }
                                                                                        pMrp.requestFocus();
                                                                                    }
                                                                                else {
                                                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.quantity_cannot_0));
                                                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                        pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                    } else {
                                                                                        pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                    }
                                                                                    pQty.requestFocus();
                                                                                }
                                                                            } else {
                                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.moq_less_quantity));
                                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                } else {
                                                                                    pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                }
                                                                                pQty.requestFocus();
                                                                            }
                                                                        } else {
                                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.sp_less_mrp));
                                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                            } else {
                                                                                pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                            }
                                                                            pMrp.requestFocus();
                                                                        }
//
                                                                    } else {
                                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_moq));
                                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                            pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        } else {
                                                                            pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        }
                                                                        pMin.requestFocus();
                                                                    }
                                                                } else {
                                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_quantity));
                                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                        pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    } else {
                                                                        pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    }
                                                                    pQty.requestFocus();
                                                                }
                                                            } else {
                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                    pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                } else {
                                                                    pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                }
                                                                pSp.requestFocus();
                                                            }
                                                        } else {
                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            } else {
                                                                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            }
                                                            pMrp.requestFocus();
                                                        }
                                                    } else {
                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_moq));
                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                            pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        } else {
                                                            pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        }
                                                        pMin.requestFocus();
                                                    }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_quantity));
                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                        pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    } else {
                                                        pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    }
                                                    pQty.requestFocus();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_sp));
                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                    pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                } else {
                                                    pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                }
                                                pSp.requestFocus();
                                            }
                                        } else {
                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_mrp));
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            } else {
                                                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            }
                                            pMrp.requestFocus();
                                        }
                                    } else {
                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.select_leaf));
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            last_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        } else {
                                            last_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        }
                                        last_spin.requestFocus();
                                    }
                                } else {
                                    if (!ValidationUtil.isNull(pMrp.getText().toString())) {
                                        if (!ValidationUtil.isNull(pSp.getText().toString())) {
                                            if (!ValidationUtil.isNull(pQty.getText().toString())) {
                                                if (!ValidationUtil.isNull(pMin.getText().toString())) {
                                                    if (ValidationUtil.isValidNumber(pMrp.getText().toString())) {
                                                        if (ValidationUtil.isValidNumber(pSp.getText().toString())) {
                                                            if (ValidationUtil.isValidNumber(pQty.getText().toString())) {
                                                                if (ValidationUtil.isValidNumber(pMin.getText().toString())) {

                                                                    if (Double.parseDouble(pMrp.getText().toString()) >= Double.parseDouble(pSp.getText().toString())) {

                                                                        if (Double.parseDouble(pQty.getText().toString()) >= Double.parseDouble(pMin.getText().toString())) {

                                                                            if (Double.parseDouble(pQty.getText().toString()) > 0)
                                                                                if (Double.parseDouble(pMrp.getText().toString()) > 0) {
                                                                                    if (Double.parseDouble(pSp.getText().toString()) > 0) {
                                                                                        if (Double.parseDouble(pMin.getText().toString()) > 0) {
                                                                                            return true;
                                                                                        } else {
                                                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.moq_not_0));
                                                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                                pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                            } else {
                                                                                                pMin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                            }
                                                                                            pMin.requestFocus();
                                                                                        }
                                                                                    } else {
                                                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.sp_cannot_0));
                                                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                            pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                        } else {
                                                                                            pSp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                        }
                                                                                        pSp.requestFocus();
                                                                                    }
                                                                                } else {
                                                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.mrp_cannot_0));
                                                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                        pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                    } else {
                                                                                        pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                    }
                                                                                    pMrp.requestFocus();
                                                                                }
                                                                            else {
                                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.quantity_cannot_0));
                                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                } else {
                                                                                    pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                }
                                                                                pQty.requestFocus();
                                                                            }
                                                                        } else {
                                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.moq_less_quantity));
                                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                            } else {
                                                                                pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                            }
                                                                            pQty.requestFocus();
                                                                        }
                                                                    } else {
                                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.sp_less_mrp));
                                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                            pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        } else {
                                                                            pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                        }
                                                                        pMrp.requestFocus();
                                                                    }
//
                                                                } else {
                                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_moq));
                                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                        pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    } else {
                                                                        pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    }
                                                                    pMin.requestFocus();
                                                                }
                                                            } else {
                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_quantity));
                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                    pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                } else {
                                                                    pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                }
                                                                pQty.requestFocus();
                                                            }
                                                        } else {
                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            } else {
                                                                pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            }
                                                            pSp.requestFocus();
                                                        }
                                                    } else {
                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                            pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        } else {
                                                            pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        }
                                                        pMrp.requestFocus();
                                                    }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_moq));
                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                        pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    } else {
                                                        pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    }
                                                    pMin.requestFocus();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_quantity));
                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                    pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                } else {
                                                    pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                }
                                                pQty.requestFocus();
                                            }
                                        } else {
                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_sp));
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            } else {
                                                pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            }
                                            pSp.requestFocus();
                                        }
                                    } else {
                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_mrp));
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        } else {
                                            pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        }
                                        pMrp.requestFocus();
                                    }
                                }
                            } else {
                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.select_leaf));
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    child_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                } else {
                                    child_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                }
                                child_spin.requestFocus();
                            }
                        } else {
                            if (!ValidationUtil.isNull(pMrp.getText().toString())) {
                                if (!ValidationUtil.isNull(pSp.getText().toString())) {
                                    if (!ValidationUtil.isNull(pQty.getText().toString())) {
                                        if (!ValidationUtil.isNull(pMin.getText().toString())) {
                                            if (ValidationUtil.isValidNumber(pMrp.getText().toString())) {
                                                if (ValidationUtil.isValidNumber(pSp.getText().toString())) {
                                                    if (ValidationUtil.isValidNumber(pQty.getText().toString())) {
                                                        if (ValidationUtil.isValidNumber(pMin.getText().toString())) {

                                                            if (Double.parseDouble(pMrp.getText().toString()) >= Double.parseDouble(pSp.getText().toString())) {

                                                                if (Double.parseDouble(pQty.getText().toString()) >= Double.parseDouble(pMin.getText().toString())) {

                                                                    if (Double.parseDouble(pQty.getText().toString()) > 0)
                                                                        if (Double.parseDouble(pMrp.getText().toString()) > 0) {
                                                                            if (Double.parseDouble(pSp.getText().toString()) > 0) {
                                                                                if (Double.parseDouble(pMin.getText().toString()) > 0) {
                                                                                    return true;
                                                                                } else {
                                                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.moq_not_0));
                                                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                        pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                    } else {
                                                                                        pMin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                    }
                                                                                    pMin.requestFocus();
                                                                                }
                                                                            } else {
                                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.sp_cannot_0));
                                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                } else {
                                                                                    pSp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                }
                                                                                pSp.requestFocus();
                                                                            }
                                                                        } else {
                                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.mrp_cannot_0));
                                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                            } else {
                                                                                pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                            }
                                                                            pMrp.requestFocus();
                                                                        }
                                                                    else {
                                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.quantity_cannot_0));
                                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                            pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        } else {
                                                                            pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                        }
                                                                        pQty.requestFocus();
                                                                    }
                                                                } else {
                                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.moq_less_quantity));
                                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                        pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    } else {
                                                                        pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                    }
                                                                    pQty.requestFocus();
                                                                }
                                                            } else {
                                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.sp_less_mrp));
                                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                    pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                } else {
                                                                    pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                }
                                                                pMrp.requestFocus();
                                                            }
//
                                                        } else {
                                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_moq));
                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                                pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            } else {
                                                                pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            }
                                                            pMin.requestFocus();
                                                        }
                                                    } else {
                                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_quantity));
                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                            pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        } else {
                                                            pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        }
                                                        pQty.requestFocus();
                                                    }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                        pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    } else {
                                                        pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    }
                                                    pSp.requestFocus();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                    pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                } else {
                                                    pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                }
                                                pMrp.requestFocus();
                                            }
                                        } else {
                                            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_moq));
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                                pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            } else {
                                                pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            }
                                            pMin.requestFocus();
                                        }
                                    } else {
                                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_quantity));
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        } else {
                                            pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        }
                                        pQty.requestFocus();
                                    }
                                } else {
                                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_sp));
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                    } else {
                                        pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                    }
                                    pSp.requestFocus();
                                }
                            } else {
                                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_mrp));
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                } else {
                                    pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                }
                                pMrp.requestFocus();
                            }
                        }

                    } else {
                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.select_category));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            parent_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        } else {
                            parent_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        }
                        parent_spin.requestFocus();
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.select_meta_category));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        root_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    } else {
                        root_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    }
                    root_spin.requestFocus();
                }
            } else {
                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_product_code));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    pCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                } else {
                    pCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                }
                pCode.requestFocus();
            }
        } else {
            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.empty_product_name));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                pName.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            } else {
                pName.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            }
            pName.requestFocus();
        }
        return false;
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
            String errorMessage = getString(R.string.crop_not_supported);
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

    private boolean EmptyData() {

        if (ValidationUtil.isNull(pName.getText().toString())) {
            if (ValidationUtil.isNull(pCode.getText().toString())) {
                if (root_spin.getSelectedItem().toString()
                        .equalsIgnoreCase("SELECT META CATEGORY")) {
                    if (parent_spin.getSelectedItem().toString()
                            .equalsIgnoreCase("SELECT CATEGORY")) {
                        if (child_spin.getSelectedItem() != null) {
                            if (child_spin.getSelectedItem().toString()
                                    .equalsIgnoreCase("SELECT LEAF CATEGORY")) {
                                if (ValidationUtil.isNull(pMrp.getText().toString())) {
                                    if (ValidationUtil.isNull(pSp.getText().toString())) {
                                        if (ValidationUtil.isNull(pQty.getText().toString())) {
                                            if (ValidationUtil.isNull(pMin.getText().toString())) {
                                                if (ValidationUtil.isNull(pDesc.getText().toString())) {
                                                    if (imagepath.size() == 0) {
                                                        return false;
                                                    }
                                                } else {
                                                    return true;
                                                }

                                            } else {
                                                return true;
                                            }
                                        } else {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        } else {
                            if (ValidationUtil.isNull(pMrp.getText().toString())) {
                                if (ValidationUtil.isNull(pSp.getText().toString())) {
                                    if (ValidationUtil.isNull(pQty.getText().toString())) {
                                        if (ValidationUtil.isNull(pMin.getText().toString())) {
                                            if (ValidationUtil.isNull(pDesc.getText().toString())) {
                                                if (imagepath.size() == 0) {
                                                    return false;
                                                }
                                            } else {
                                                return true;
                                            }

                                        } else {
                                            return true;
                                        }
                                    } else {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }

                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;

    }

    private boolean ValidNumbers() {

        if (ValidationUtil.isValidNumber2(pMrp.getText().toString())) {
            if (ValidationUtil.isValidNumber2(pSp.getText().toString())) {
                if (ValidationUtil.isValidNumber2(pMin.getText().toString())) {
                    if (ValidationUtil.isValidNumber2(pQty.getText().toString())) {
                        return true;
                    } else {
                        new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_min));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            pQty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        } else {
                            pQty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                        }
                        pQty.requestFocus();
                        return false;
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        pMin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    } else {
                        pMin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                    }
                    pMin.requestFocus();
                    return false;
                }
            } else {
                new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    pSp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                } else {
                    pSp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                }
                pSp.requestFocus();
                return false;
            }
        } else {
            new AlertDialogManager().showAlertDialog(AddProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                pMrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            } else {
                pMrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
            }
            pMrp.requestFocus();
            return false;
        }


    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            if (!isFinishing())
                progress.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @Override
        protected void onPostExecute(String result) {
            if (!isFinishing()) {
                progress.dismiss();
                Log.d("RESPonse", result);
                if (!result.contains("product")) {
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(result);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            try {

                                if (return_flag.equalsIgnoreCase("1")) {
                                    startActivity(new Intent(AddProduct.this, MyCatalog.class));
                                    finish();
                                } else {
                                    Intent intent = NavUtils.getParentActivityIntent(AddProduct.this);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    NavUtils.navigateUpTo(AddProduct.this, intent);
                                    finish();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    alertDialog.show();

                } else if (result.contains("product")) {
                    Log.d("here", "after");
                    JSONObject json = null;
                    try {
                        json = new JSONObject(result);

                        rString = "";
                        for (String each : imagepath) {
                            rString = rString + each + "~";
                        }
                        if (rString.length() > 0)
                            rString = rString.substring(0, rString.length() - 1);
                        Log.i("Addproduct", "imagepath--" + rString.toString().trim());
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(FeedCatalog.COLUMN_PRODUCT_ID, json.getString("product_id"));
                        contentValues2.put(FeedCatalog.COLUMN_TITLE, pName.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_CODE, pCode.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_IMAGEPATH, rString.toString().trim());
                        if (imagepath.size() > 0)
                            contentValues2.put(FeedCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));
                        else
                            contentValues2.put(FeedCatalog.COLUMN_THUMB_PATH, "");
                        contentValues2.put(FeedCatalog.COLUMN_GRANDPARENTCAT, selectedRoot);
                        contentValues2.put(FeedCatalog.COLUMN_PARENTCAT, selectedparent);
                        contentValues2.put(FeedCatalog.COLUMN_CHILDCAT, selectedchild);
                        contentValues2.put(FeedCatalog.COLUMN_GRANDCHILDCAT, selectedGrandChild);
                        contentValues2.put(FeedCatalog.COLUMN_MRP, pMrp.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_SP, pSp.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_QTY, pQty.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_MINQTY, pMin.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_DESC, pDesc.getText().toString());
                        contentValues2.put(FeedCatalog.COLUMN_VISIBILTY, visibility);
                        contentValues2.put(FeedCatalog.COLUMN_CREATEDAT, time);
                        contentValues2.put(FeedCatalog.COLUMN_UPDATEDAT, time);
                        contentValues2.put(FeedCatalog.COLUMN_CATEGORY_ID, selectedcatval);
                        Log.i("selectedcatval", selectedcatval);
                        contentValues2.put(FeedCatalog.COLUMN_LOCAL_PROD_ID, "0");
                        if (imagepath.size() > 0)
                            contentValues2.put(FeedCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));
                        if (visibility.equalsIgnoreCase("Public"))
                            contentValues2.put(FeedCatalog.COLUMN_STATUS, "A");
                        else
                            contentValues2.put(FeedCatalog.COLUMN_STATUS, "D");

                        // Log.i("Addproduct", "product_id" + json.getString("product_id"));

                        Uri uri2 = getContentResolver().insert(MyContentProvider.CONTENT_URI_FEED, contentValues2);
                        if (uri2 != null) {
                            // Log.i("Addproduct", "Updating product_id");
                        }

                        alertDialog.setTitle(getResources().getString(R.string.status));
                        alertDialog.setMessage(getResources().getString(R.string.product_added));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Your positive action
                                startActivity(new Intent(AddProduct.this, MyCatalog.class));
                                finish();
                            }
                        });
                        alertDialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                super.onPostExecute(result);
            }

        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AppUtil.URL + "3.0/products");
            // httppost.addHeader();
            httppost.addHeader("Connection", "Keep-Alive");
            httppost.addHeader("Accept", "*/*");
            httppost.addHeader("Content-Type", "multipart/form-data");
            httppost.addHeader("Authorization", helper.getB64Auth(AddProduct.this));
            //  httppost.set

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                //  publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                // Log.d("TAG_PATH", str);


                Log.d("imagepath", imagepath.size() + "");
                for (int i = 0; i < imagepath.size(); i++) {
                    //  String str = decodeFile(imagepath.get(i));
                    Log.d("path", imagepath.get(i));
                    File sourceFile = new File(imagepath.get(i));

                    entity.addPart("images[" + i + "][image]", new FileBody(sourceFile));
                    if (i == default_position) {
                        entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("1"));

                    } else {
                        entity.addPart("images[" + i + "]" + "[is_default]", new StringBody("0"));
                    }

                }
                // Extra parameters if you want to pass to server

                int radioButtonID = scope.getCheckedRadioButtonId();
                View radioButton = scope.findViewById(radioButtonID);
                int idx = scope.indexOfChild(radioButton);
                if (idx == 0)
                    visibility = "Public";
                else
                    visibility = "Private";
                entity.addPart("product", new StringBody(pName.getText().toString().trim()));
                entity.addPart("product_code", new StringBody(pCode.getText().toString().trim()));
                entity.addPart("company_id", new StringBody(user_comp_id));
                entity.addPart("list_price", new StringBody(pMrp.getText().toString().trim()));
                entity.addPart("price", new StringBody(pSp.getText().toString().trim()));
                entity.addPart("amount", new StringBody(pQty.getText().toString().trim()));
                entity.addPart("min_qty", new StringBody(pMin.getText().toString()));
                entity.addPart("category_ids[0]", new StringBody(selectedcatval));
                entity.addPart("main_category", new StringBody(selectedcatval));
                entity.addPart("full_description", new StringBody(pDesc.getText().toString()));
                entity.addPart("product_visibility", new StringBody(visibility));

//                Log.i("Addproduct pro", "product " + pName.getText().toString().trim());
//                Log.i("Addproduct pro", "pCode "+pCode.getText().toString().trim());
//                Log.i("Addproduct pro", "user_comp_id "+user_comp_id);
//                Log.i("Addproduct pro", "pMrp "+pMrp.getText().toString().trim());
//                Log.i("Addproduct pro", "pSp "+pSp.getText().toString().trim());
//                Log.i("Addproduct pro", "pQty "+pQty.getText().toString().trim());
//                Log.i("Addproduct pro", "pMin "+pMin.getText().toString().trim());
//                Log.i("Addproduct pro", "pDesc "+pDesc.getText().toString().trim());
//                Log.i("Addproduct pro", "category_ids[0] "+selectedcatval);
//                Log.i("Addproduct pro", "main_category "+selectedcatval);
//                Log.i("Addproduct pro", "user_comp_id "+user_comp_id);
//
//                Log.i("Addproduct pro", "visibility "+visibility);


                totalSize = entity.getContentLength();


                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                Log.i("sttscode", statusCode + "");
                if (statusCode == 201) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    if (statusCode == 401) {

                        final SessionManager sessionManager = new SessionManager(AddProduct.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    } else {
                        responseString = getResources().getString(R.string.server_error);
                    }

                }
            } catch (ClientProtocolException e) {
                responseString = getResources().getString(R.string.server_error);
            } catch (IOException e) {
                responseString = getResources().getString(R.string.server_error);
            }
            return responseString;
        }


    }

    private class ImageViewPagerAdapter extends PagerAdapter {
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
            //Deepesh
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.imgeviewpager_item, container,
                    false);

            imgflag = (ImageView) itemView.findViewById(R.id.flag);
            imgflag.setTag(position);
            Log.i("POSIT--", position + "/" + flag.get(position));
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
                                adapter.notifyDataSetChanged();
                            }

                            return true;
                        }
                    });

                    popup.show();
                }
            });

            container.addView(itemView);
            Log.d("positon", position + "/" + (flag.size() - 1));
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
        Call<CategoryHolder> call = service.FetchCategory("true", "1", helper.getB64Auth(AddProduct.this), "application/json", "application/json");
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
                                    Log.e("ADDPRODUCT", "SQLiteConstraintException:" + e.getMessage());
                                } catch (android.database.sqlite.SQLiteException e) {
                                    Log.e("ADDPRODUCT", "SQLiteException:" + e.getMessage());
                                } catch (Exception e) {
                                    Log.e("ADDPRODUCT", "Exception:" + e.getMessage());
                                }
                            }
                            Cursor countCursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_ID}, null, null, null);
                            if (countCursor.getCount() > 0) {
                                BringCategory();
                            } else {
                                    new AlertDialogManager().showAlertDialog(AddProduct.this , getString(R.string.oops),
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

                        final SessionManager sessionManager = new SessionManager(AddProduct.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    } else {
                        if (AddProduct.this != null && !AddProduct.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(AddProduct.this,
                                    getString(R.string.error),
                                    getString(R.string.server_error));
                        }
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {

                progress.dismiss();
                if (AddProduct.this != null && !AddProduct.this.isFinishing()) {

                    new AlertDialogManager().showAlertDialog(AddProduct.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
                }

            }
        });

    }


}