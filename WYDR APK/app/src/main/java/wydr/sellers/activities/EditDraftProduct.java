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
import android.widget.LinearLayout;
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
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.ScalingUtilities;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.adapter.CustomSpinAdapter;
import wydr.sellers.adapter.NavDrawerListAdapter;
import wydr.sellers.gson.CategoryHolder;
import wydr.sellers.modal.CategoryDataModal;
import wydr.sellers.modal.NavDrawerItem;
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
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

//import auriga.sellers.adapter.ImageViewPagerAdapter;

/**
 * Created by surya on 13/8/15.
 */
public class EditDraftProduct extends AppCompatActivity implements View.OnClickListener {
    ImageView sliderMenu;

    SimpleSideDrawer slider;
    String TAG = "EditDraftProduct";
    ListView items;
    NavDrawerListAdapter sliderAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;
    String parentid, childid, subchildid, leafid;
    public int flag1, flag2 = 0;
    public Toolbar mToolbar;
    AlertDialog.Builder alertDialog;
    ConnectionDetector cd;

    public JSONArray jsonMainArr;
    RadioButton radiopub, radiopri;
    public EditText pname, pcode, pmrp, psp, pqty, pmin, pdesc;
    Spinner root_spin, parent_spin, child_spin, last_spin;
    public RadioGroup scope;
    String selectedcatval = "", time, rString = "", error_flag, error_message, row_id;
    public ArrayList<String> subcat;
    Button submit, skip;
    ImageView menu;
    String images, pid;
    int[] flag;
    ViewPager viewPager;
    PagerAdapter adapter;
    private ProgressDialog progress;
    public ArrayList<String> category, category2, category3, category4;
    ArrayList<ProdCatModal> products, products2, products3, products4;
    private Uri fileUri;
    public String visibility, class_name;
    public List<Bitmap> imagegallery = new ArrayList<>();
    public String message, selectedparent, selectedchild, selectedgrandchild, selectedRoot;
    JSONParser parser;
    SessionManager session;
    long totalSize = 0;
    final int DESIREDWIDTH = 800;
    final int DESIREDHEIGHT = 600;
    public String extStorageDirectory;
    public static int inc = 0;
    public static final Uri CONTENT_URI = Uri.parse("content://eu.janmuller.android.simplecropimage.example/");
    CustomSpinAdapter adap_1, adap_2, adap_3, adap_4;
    public ArrayList<String> imagepath = new ArrayList<>();
    public String[] allowedCats, parent_allowedCats;

    public static CirclePageIndicator titleIndicator;
    public static int default_position = 0;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 200;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 201;
    final int PIC_CROP = 4;
    Helper helper = new Helper();

    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("EditDraftProduct");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }


    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.add_product);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView tooltitle = (TextView) findViewById(R.id.tooltext);
        progressStuff();
        Bundle b = getIntent().getBundleExtra("result");
        tooltitle.setText(getString(R.string.edit_product));
        pid = b.getString("prod_id");
        row_id = b.getString("row_id");
        error_flag = b.getString("error_flag");
        error_message = b.getString("error_message");
        imagegallery.clear();
        imagepath.clear();

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImageViewPagerAdapter(EditDraftProduct.this, imagegallery);
        viewPager.setAdapter(adapter);
        titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(viewPager);
        titleIndicator.setVisibility(View.VISIBLE);
        inistuff();
        android.support.v7.app.ActionBar bar = getSupportActionBar();


        Log.i(TAG, "rowid =" + pid);

        /*skip.setText("Cancel");
        submit.setText("Update");*/
        bar.setTitle("");
      /*  submit.setOnClickListener(this);
        skip.setOnClickListener(this);*/
        radiopub.setOnClickListener(this);
        radiopri.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        if (EmptyData()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditDraftProduct.this);
            builder.setTitle(getString(R.string.save_changes))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.save_close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (ValidNumbers())
                                SaveAndClose();

                        }
                    }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                /*Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(EditDraftProduct.this, intent);*/
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    finish();

                }
            });
            builder.show();
        } else {
            imagegallery.clear();
            imagepath.clear();
            adapter.notifyDataSetChanged();
            finish();
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
        contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, pid);
        contentValues.put(AlteredCatalog.COLUMN_TITLE, pname.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_CODE, pcode.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_IMAGEPATH, rString.toString().trim());
        contentValues.put(AlteredCatalog.COLUMN_GRANDPARENTCAT, selectedRoot);
        contentValues.put(AlteredCatalog.COLUMN_PARENTCAT, selectedparent);
        contentValues.put(AlteredCatalog.COLUMN_CHILDCAT, selectedchild);
        contentValues.put(AlteredCatalog.COLUMN_GRANDCHILDCAT, selectedgrandchild);
        contentValues.put(AlteredCatalog.COLUMN_CATEGORY_ID, selectedcatval);
        contentValues.put(AlteredCatalog.COLUMN_MRP, pmrp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_SP, psp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_QTY, pqty.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_MINQTY, pmin.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_DESC, pdesc.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_VISIBILTY, visibility);
        contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "2");
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_FLAG, "4");
        contentValues.put(AlteredCatalog.COLUMN_DEFAULT_POSITION, default_position);
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
        contentValues.put(AlteredCatalog.COLUMN_CREATEDAT, time);
        contentValues.put(AlteredCatalog.COLUMN_UPDATED, time);
        contentValues.put(AlteredCatalog.COLUMN_COMPANY_ID, helper.getDefaults("company_id", getApplicationContext()));
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, pid);
        contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "");
        contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, "");
        if (imagepath.size() > 0)
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));
        else
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, "");

        int save_updtion = getContentResolver().update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{row_id});
        if (save_updtion == 0) {
            Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI_ALTER, contentValues);

            if (uri != null) {

                alertDialog.setTitle(getString(R.string.status));
                alertDialog.setMessage(getString(R.string.product_saved));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Your positive action
                      /* imagegallery.clear();
                       imagepath.clear();
                        startActivity(new Intent(EditDraftProduct.this, MyCatalog.class));
                        finish();*/
                        /*Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NavUtils.navigateUpTo(EditDraftProduct.this, intent);*/
                        imagegallery.clear();
                        imagepath.clear();
                        adapter.notifyDataSetChanged();
                        finish();

                    }
                });
                alertDialog.show();
            } else {
                alertDialog.setTitle(getString(R.string.status));
                alertDialog.setMessage(getString(R.string.product_not_saved));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                      /*  Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NavUtils.navigateUpTo(EditDraftProduct.this, intent);*/
                        imagegallery.clear();
                        imagepath.clear();
                        adapter.notifyDataSetChanged();
                        finish();
                    }
                });

                alertDialog.show();
            }

        } else {
            Log.e(" present", "product");
            alertDialog.setTitle(getString(R.string.status));
            alertDialog.setMessage(getString(R.string.product_saved));
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Your positive action
                   /* Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(EditDraftProduct.this, intent);*/
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    finish();
                }
            });
            alertDialog.show();
        }


    }

    private void progressStuff() {
        cd = new ConnectionDetector(getApplicationContext());
        parser = new JSONParser();
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        alertDialog = new AlertDialog.Builder(this);
    }


    public void fun(View v) {
        int con2 = (int) v.getTag();
        if (con2 == (imagegallery.size() - 1)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditDraftProduct.this);
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
                    captureImage();
                }
            });

            builder.show();
        } else {
            Intent showImage = new Intent(EditDraftProduct.this, ImageActivity.class);
            showImage.putStringArrayListExtra("bitmap", imagepath);
            startActivity(showImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RESULTCODE--", resultCode + "");
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
                        imagepath.add(decodeFileCrop(selectedBitmap));

                        Bitmap s = imagegallery.get(imagegallery.size() - 2);
                        imagegallery.remove(imagegallery.size() - 2);
                        imagegallery.add(s);

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
                ////Log.d("HERE","1");
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

            Toast.makeText(EditDraftProduct.this, getResources().getString(R.string.fail_load_image),
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (EmptyData()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditDraftProduct.this);
                    builder.setTitle(getResources().getString(R.string.save_changes))
                            .setCancelable(true)
                            .setPositiveButton(getResources().getString(R.string.save_close), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (ValidNumbers())
                                        SaveAndClose();


                                }
                            }).setNegativeButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        /*Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NavUtils.navigateUpTo(EditDraftProduct.this, intent);*/
                            imagegallery.clear();
                            imagepath.clear();
                            adapter.notifyDataSetChanged();
                            finish();

                        }
                    });

                    builder.show();
                    return true;
                } else {
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void inistuff() {
        if (error_flag != null) {
            if (error_flag.equalsIgnoreCase("true")) {
                LinearLayout error_linear = (LinearLayout) findViewById(R.id.linear_error);
                error_linear.setVisibility(View.VISIBLE);
                TextView textView = (TextView) findViewById(R.id.errorText);
                Log.i(TAG, "error msg - " + error_message);
                textView.setText(error_message);
                //   textView.setText(error_message.replace(null, ""));

            }
        }

        radiopub = (RadioButton) findViewById(R.id.radioPub);
        radiopri = (RadioButton) findViewById(R.id.radioPri);
        pname = (EditText) findViewById(R.id.txtprodname);
        pcode = (EditText) findViewById(R.id.txtprodCode);
        pmrp = (EditText) findViewById(R.id.txtmrp);
        psp = (EditText) findViewById(R.id.txtsp);
        pqty = (EditText) findViewById(R.id.txtqty);
        pmin = (EditText) findViewById(R.id.txtminqty);
        pdesc = (EditText) findViewById(R.id.txtdesc);

        pname.setOnClickListener(this);
        pcode.setOnClickListener(this);
        pmrp.setOnClickListener(this);
        pqty.setOnClickListener(this);
        pmin.setOnClickListener(this);
        pdesc.setOnClickListener(this);
        psp.setOnClickListener(this);
        images = "";

        scope = (RadioGroup) findViewById(R.id.radiogrp1);
        parentid = "";
        childid = "";
        subchildid = "";
        leafid = "";
        subcat = new ArrayList<>();
        submit = (Button) findViewById(R.id.btnsub);
        skip = (Button) findViewById(R.id.btnskip);
        products = new ArrayList<>();

        category = new ArrayList<>();
        products2 = new ArrayList<>();

        category2 = new ArrayList<>();
        products3 = new ArrayList<>();

        category3 = new ArrayList<>();
        products4 = new ArrayList<>();

        category4 = new ArrayList<>();
        parent_spin = (Spinner) findViewById(R.id.spinner2);
        root_spin = (Spinner) findViewById(R.id.spinner1);
        child_spin = (Spinner) findViewById(R.id.spinner3);
        last_spin = (Spinner) findViewById(R.id.spinner4);
        adap_1 = new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category);
        root_spin.setAdapter(adap_1);
        adap_2 = new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category2);
        parent_spin.setAdapter(adap_2);
        adap_3 = new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category3);
        child_spin.setAdapter(adap_3);
        adap_4 = new CustomSpinAdapter(getApplicationContext(),
                R.layout.spin_row, category4);
        last_spin.setAdapter(adap_4);

        /*cd = new ConnectionDetector(getApplicationContext());*/
        root_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                category2.clear();
                products2.clear();
                category3.clear();
                products3.clear();
                category4.clear();
                products4.clear();
                selectedRoot = products.get(position).getId();
                selectedparent="";
                selectedchild="";
                selectedgrandchild="";
                parent_spin.setEnabled(true);
                String catsel = "";
                adap_2.notifyDataSetChanged();
                adap_3.notifyDataSetChanged();
                adap_4.notifyDataSetChanged();
                Cursor parent2 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{products.get(position).getId()}, null);
                //  Log.i(TAG, "parent2.getCount() " + parent2.getCount() + "");
                String id, title, has_child;
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
                    Log.i(TAG, id + " / " + title + " / " + has_child);
                    if (has_child.equalsIgnoreCase("1"))
                        pm.setHas_child(true);
                    else
                        pm.setHas_child(false);
                    if (childid.equalsIgnoreCase(id)) {
                        catsel = title;
                    }
                    products2.add(pm);
                    category2.add(title);
                }
                adap_2.notifyDataSetChanged();
                if (childid != null && !childid.equalsIgnoreCase(""))
                    parent_spin.setSelection(((CustomSpinAdapter) parent_spin.getAdapter()).getPosition(catsel));
                else
                    parent_spin.setSelection(0);
                child_spin.setVisibility(View.GONE);
                last_spin.setVisibility(View.GONE);
                childid = "";
                parent2.close();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                //Log.d("PPP", "Clilkde");
            }
        });
        parent_spin
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "root_spin " + root_spin.getSelectedItem().toString());
                        if (!root_spin.getSelectedItem().toString().equalsIgnoreCase("SELECT META CATEGORY")) {
                            parent_spin.setEnabled(true);
                            category3.clear();
                            products3.clear();
                            category4.clear();
                            products4.clear();
                            adap_3.notifyDataSetChanged();
                            adap_4.notifyDataSetChanged();
                            selectedchild="";
                            selectedgrandchild="";
                            selectedparent = products2.get(position).getId();
                            selectedcatval = products2.get(position).getId();
                            if (products2.get(position).getHas_child()) {

                                child_spin.setVisibility(View.VISIBLE);
                                String catsel = "";

                                Cursor parent3 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{products2.get(position).getId()}, null);
                                Log.i(TAG, ",parent3.getCount() " + parent3.getCount() + "");
                                String id, title, has_child;
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
                                    Log.i(TAG, id + " / " + title + " / " + has_child);
                                    if (subchildid.equalsIgnoreCase(id)) {
                                        catsel = title;
                                    }
                                    if (has_child.equalsIgnoreCase("1"))
                                        pm.setHas_child(true);
                                    else
                                        pm.setHas_child(false);
                                    products3.add(pm);
                                    category3.add(title);
                                }
                                adap_3.notifyDataSetChanged();
                                if (subchildid != null && !subchildid.equalsIgnoreCase(""))
                                    child_spin.setSelection(((CustomSpinAdapter) child_spin.getAdapter()).getPosition(catsel));
                                else
                                    child_spin.setSelection(0);

                                subchildid = "";
                                parent3.close();
                            } else {
                                child_spin.setVisibility(View.GONE);
                                last_spin.setVisibility(View.GONE);
                            }

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
                        Log.i(TAG, "root_spin " + root_spin.getSelectedItem().toString());
                        if (!parent_spin.getSelectedItem().toString().equalsIgnoreCase("SELECT CATEGORY")) {
                            child_spin.setEnabled(true);

                            category4.clear();
                            products4.clear();
                            selectedchild = products3.get(position).getId();
                            selectedcatval = products3.get(position).getId();
                            selectedgrandchild="";
                            adap_4.notifyDataSetChanged();
                            if (products3.get(position).getHas_child()) {

                                last_spin.setVisibility(View.VISIBLE);
                                String catsel = "";

                                Cursor parent3 = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{products3.get(position).getId()}, null);
                                Log.i(TAG, ",parent3.getCount() " + parent3.getCount() + "");
                                String id, title, has_child;
                                ProdCatModal pm_def = new ProdCatModal();
                                pm_def.setName("SELECT LEAF CATEGORY");
                                pm_def.setId("0");
                                pm_def.setHas_child(false);
                                products4.add(pm_def);
                                category4.add("SELECT LEAF CATEGORY");
                                while (parent3.moveToNext()) {
                                    id = parent3.getString(0);
                                    title = parent3.getString(1);
                                    has_child = parent3.getString(2);
                                    ProdCatModal pm = new ProdCatModal();
                                    pm.setName(title);
                                    pm.setId(id);
                                    Log.i(TAG, id + " / " + title + " / " + has_child);
                                    if (leafid.equalsIgnoreCase(id)) {
                                        catsel = title;
                                    }
                                    if (has_child.equalsIgnoreCase("1"))
                                        pm.setHas_child(true);
                                    else
                                        pm.setHas_child(false);
                                    products4.add(pm);
                                    category4.add(title);
                                }
                                adap_4.notifyDataSetChanged();
                                if (leafid != null && !leafid.equalsIgnoreCase(""))
                                    last_spin.setSelection(((CustomSpinAdapter) last_spin.getAdapter()).getPosition(catsel));
                                else
                                    last_spin.setSelection(0);

                                leafid = "";
                                parent3.close();
                            } else {

                                last_spin.setVisibility(View.GONE);
                            }

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
                        selectedcatval = products4.get(position).getId();
                        selectedgrandchild = products4.get(position).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        time = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);

        getAllowedCategories();

    }

    private void getAllowedCategories() {

//        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI_MYCATEGORY, new String[]{MyCategoryTable.KEY_CATEGORY_ID, MyCategoryTable.KEY_PARENT_ID}, null, null, null);
//
//        if (cursor.getCount() == 0) {
//            alertDialog.setTitle("No Category Selected").setMessage("Cannot add product until cagtegory is selected").setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    startActivity(new Intent(EditDraftProduct.this, Catalog.class));
//                    finish();
//                }
//            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                    finish();
//                }
//            });
//            alertDialog.show();
//        } else {
//            parent_allowedCats = new String[cursor.getCount()];
//            allowedCats = new String[cursor.getCount()];
//            int i = 0;
//            while (cursor.moveToNext()) {
//                Log.i("cursor.getString(1)", cursor.getString(1) + "/" + cursor.getString(0));
//                parent_allowedCats[i] = cursor.getString(1);
//                allowedCats[i] = cursor.getString(0);
//                //Log.i("getAllowedCategories", parent_allowedCats[i] + "/" + allowedCats[i]);
//                i++;
//
//
//            }
//
//
//            //   Log.i("getAllowedCategories", parent_allowedCats.toString() + "/" + parent_allowedCats.length);
//
//        }
        Cursor cursor2 = getContentResolver().query(MyContentProvider.CONTENT_URI_Login, new String[]{LoginDB.KEY_SCOPE}, LoginDB.KEY_USERID + "=?", new String[]{helper.getDefaults("user_id", getApplicationContext())}, null);
        if (cursor2.getCount() > 0) {
            if (cursor2.moveToFirst()) {
                visibility = cursor2.getString(0);
                //Log.d("VISIBILITY", visibility);
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
        OfflineProduct();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radioPub:
                radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
            case R.id.radioPri:
                radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
            case R.id.txtprodname:
                helper.SetBack(pname, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtprodCode:
                //Log.d("nme", "click");
                helper.SetBack(pcode, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtmrp:
                //Log.d("nme", "click");
                helper.SetBack(pmrp, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtsp:
                //Log.d("nme", "click");
                helper.SetBack(psp, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtqty:
                //Log.d("nme", "click");
                helper.SetBack(pqty, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtminqty:
                helper.SetBack(pmin, getResources().getDrawable(R.drawable.code_bar));
                break;
            case R.id.txtdesc:
                helper.SetBack(pdesc, getResources().getDrawable(R.drawable.code_bar));
                break;
        }

    }

    private void updatelocal() {

        int radioButtonID = scope.getCheckedRadioButtonId();
        View radioButton = scope.findViewById(radioButtonID);
        int idx = scope.indexOfChild(radioButton);
        if (idx == 0)
            visibility = "Public";
        else
            visibility = "Private";

        String rString = "";
        for (String each : imagepath) {
            rString = rString + each + "~";
        }
        if (rString.length() > 1)
            rString = rString.substring(0, rString.length() - 1);
        // Log.i(TAG, "rString " + rString);
        Cursor c = getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, null, null, null, null);
        //     Log.i(TAG, "COUNT--before---- " + c.getCount() + " -------------...");
        int countbef = c.getCount();
        c.close();
        Log.i(TAG, "deepesh--" + selectedRoot+"/"+ selectedparent+"/"+ selectedchild+"/"+ selectedgrandchild+"/" + " -------------...");
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, pid);
        contentValues.put(AlteredCatalog.COLUMN_TITLE, pname.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_CODE, pcode.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_IMAGEPATH, rString.toString().trim());
        contentValues.put(AlteredCatalog.COLUMN_GRANDPARENTCAT, selectedRoot);
        contentValues.put(AlteredCatalog.COLUMN_PARENTCAT, selectedparent);
        contentValues.put(AlteredCatalog.COLUMN_CHILDCAT, selectedchild);
        contentValues.put(AlteredCatalog.COLUMN_GRANDCHILDCAT, selectedgrandchild);
        contentValues.put(AlteredCatalog.COLUMN_CATEGORY_ID, selectedcatval);
        contentValues.put(AlteredCatalog.COLUMN_MRP, pmrp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_SP, psp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_QTY, pqty.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_MINQTY, pmin.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_DESC, pdesc.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_VISIBILTY, visibility);
        contentValues.put(AlteredCatalog.COLUMN_DEFAULT_POSITION, default_position);
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_FLAG, "2");
        contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "1");
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
        contentValues.put(AlteredCatalog.COLUMN_CREATEDAT, time);
        contentValues.put(AlteredCatalog.COLUMN_UPDATED, time);
        contentValues.put(AlteredCatalog.COLUMN_COMPANY_ID, helper.getDefaults("company_id", getApplicationContext()));
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, pid);
        contentValues.put(AlteredCatalog.COLUMN_ERROR_FLAG, "false3");
        contentValues.put(AlteredCatalog.COLUMN_ERROR_MESSAGE, "");
        if (imagepath.size() > 0)
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));
        else
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, "");

        int is_updated = getContentResolver().update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{row_id});
        //Log.i(TAG, "is_updated--" + is_updated);
        if (is_updated != 1) {
            alertDialog.setTitle(getResources().getString(R.string.status));
            alertDialog.setMessage(getResources().getString(R.string.product_upd_fail));
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Your positive action
                    Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(EditDraftProduct.this, intent);
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    finish();

                }
            });
            alertDialog.show();
        } else if (is_updated == 1) {
            //  Log.i(TAG, "is_updated--updating");
            alertDialog.setTitle(getResources().getString(R.string.status));
            alertDialog.setMessage(getResources().getString(R.string.product_updated));

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = NavUtils.getParentActivityIntent(EditDraftProduct.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(EditDraftProduct.this, intent);
                    imagegallery.clear();
                    imagepath.clear();
                    adapter.notifyDataSetChanged();
                    finish();
                }
            });
            alertDialog.show();
        }

    }


    private void OfflineProduct() {

        final String[] PROJECTION = new String[]{
                AlteredCatalog._ID,
                AlteredCatalog.COLUMN_PRODUCT_ID,
                AlteredCatalog.COLUMN_TITLE,
                AlteredCatalog.COLUMN_CODE,
                AlteredCatalog.COLUMN_IMAGEPATH,
                AlteredCatalog.COLUMN_GRANDPARENTCAT,
                AlteredCatalog.COLUMN_PARENTCAT,
                AlteredCatalog.COLUMN_CHILDCAT,
                AlteredCatalog.COLUMN_MRP,
                AlteredCatalog.COLUMN_SP,
                AlteredCatalog.COLUMN_QTY,
                AlteredCatalog.COLUMN_MINQTY,
                AlteredCatalog.COLUMN_DESC
                , AlteredCatalog.COLUMN_VISIBILTY,
                AlteredCatalog.COLUMN_THUMB_PATH,
                AlteredCatalog.COLUMN_GRANDCHILDCAT

        };

        int COLUMN_ID = 0, COLUMN_PRODUCT_ID = 1, COLUMN_TITLE = 2, COLUMN_CODE = 3, COLUMN_IMAGEPATH = 4,
                COLUMN_GRANDPARENTCAT = 5, COLUMN_PARENTCAT = 6,
                COLUMN_CHILDCAT = 7, COLUMN_MRP = 8, COLUMN_SP = 9, COLUMN_QTY = 10, COLUMN_MINQTY = 11,
                COLUMN_DESC = 12, COLUMN_VISIBILTY = 13, COLUMN_THUMB_PATH = 14,COLUMN_GRANDCHILDCAT=15/*,COLUMN_STATUS = 14,COLUMN_UPDATEDAT = 15,COLUMN_CREATEDAT = 16*/;
        Cursor parent = getContentResolver().query(MyContentProvider.CONTENT_URI_ALTER, PROJECTION, AlteredCatalog._ID + "=?", new String[]{row_id}, null);
        assert parent != null;
        while (parent.moveToNext()) {
//            Log.i(TAG, "COLUMN_VISIBILTY--" + parent.getString(COLUMN_VISIBILTY));
//            Log.i(TAG, "after printing values--///////////");
            if (parent.getString(COLUMN_PRODUCT_ID).equalsIgnoreCase("")) {
                submit.setText(getString(R.string.update_now));
                skip.setText(getString(R.string.save_close));
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (CheckValidation()) {
                            uploadLocally();
                        }

                    }
                });
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (EmptyData()) {
                            if (ValidNumbers())
                                SaveAndClose();
                        } else {
                            imagegallery.clear();
                            imagepath.clear();
                            adapter.notifyDataSetChanged();
                            finish();
                        }

                    }
                });

            } else {

                skip.setText(getString(R.string.cancel));
                submit.setText(getString(R.string.update_now));
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckValidation()) {
                            updatelocal();
                        }
                    }
                });
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (EmptyData()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditDraftProduct.this);
                            builder.setTitle(getString(R.string.save_changes))
                                    .setCancelable(true)
                                    .setPositiveButton(getString(R.string.save_close), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (ValidNumbers())
                                                SaveAndClose();

                                        }
                                    }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(EditDraftProduct.this, MyCatalog.class));
                                    finish();

                                }
                            });
                            builder.show();
                        } else {
                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getResources().getString(R.string.alert), getResources().getString(R.string.atleast_one_field));
                        }

                    }
                });
            }
            double f;
            if (!parent.getString(COLUMN_MRP).equalsIgnoreCase("") && parent.getString(COLUMN_MRP) != null) {
                f = parent.getDouble(COLUMN_MRP);
                pmrp.setText(BigDecimal.valueOf(f).toPlainString());
            } else
                pmrp.setText(BigDecimal.valueOf(parent.getDouble(COLUMN_MRP)).toPlainString());
            if (!parent.getString(COLUMN_SP).equalsIgnoreCase("") && parent.getString(COLUMN_SP) != null) {
                f = parent.getDouble(COLUMN_SP);
                psp.setText(BigDecimal.valueOf(f).toPlainString());
            } else
                psp.setText(BigDecimal.valueOf(parent.getDouble(COLUMN_SP)).toPlainString());
            pname.setText(parent.getString(COLUMN_TITLE));
            pcode.setText(parent.getString(COLUMN_CODE));
            pqty.setText(BigDecimal.valueOf(parent.getDouble(COLUMN_QTY)).toPlainString());
            pmin.setText(BigDecimal.valueOf(parent.getInt(COLUMN_MINQTY)).toPlainString());
            if (parent.getString(COLUMN_DESC) != null)
                pdesc.setText(parent.getString(COLUMN_DESC));
            parentid = parent.getString(COLUMN_GRANDPARENTCAT);
            if (parent.getString(COLUMN_PARENTCAT) != null)
                childid = parent.getString(COLUMN_PARENTCAT);
            else
                childid = "";
            if (parent.getString(COLUMN_CHILDCAT) != null)
                subchildid = parent.getString(COLUMN_CHILDCAT);
            else
                subchildid = "";
            if (parent.getString(COLUMN_GRANDCHILDCAT) != null)
                leafid = parent.getString(COLUMN_GRANDCHILDCAT);
            else
                leafid = "";
            Log.i(TAG, "ids--" +parentid+"/"+childid+"/"+subchildid+"/"+leafid+"/");
            if (parent.getString(COLUMN_VISIBILTY).equalsIgnoreCase("Public")) {
                radiopub.setChecked(true);
                radiopri.setChecked(false);
                radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            } else {
                radiopri.setChecked(true);
                radiopub.setChecked(false);
                radiopri.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                radiopub.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            Log.i(TAG, "parentid-" + parentid + "/" + childid + "/" + subchildid);
            if (!parent.getString(COLUMN_IMAGEPATH).equalsIgnoreCase("")) {
                String[] s = parent.getString(COLUMN_IMAGEPATH).split("~");
                Log.i(TAG, "s.length-" + s.length);
                for (int i = 0; i < s.length; i++) {
                    Log.i(TAG, "s[i]-" + s[i]);
                    Bitmap bitmap = helper.decodeSampledBitmapFromResource(s[i], 400, 200);
                    imagegallery.add(bitmap);
                    imagepath.add(s[i]);
                }
            }
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon);
            imagegallery.add(icon);
            adapter.notifyDataSetChanged();
        }
        parent.close();
        Cursor countCursor = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_ID}, null, null, null);
        if (countCursor.getCount() == 0) {
            prepareRequest();
        } else {
            OfflineCategory();
        }
        countCursor.close();
    }

    private void uploadLocally() {

        //new AlertDialogManager().showAlertDialog(EditDraftProduct.this, "Sorry", "No internet connection");
        int radioButtonID = scope.getCheckedRadioButtonId();
        View radioButton = scope.findViewById(radioButtonID);
        int idx = scope.indexOfChild(radioButton);
        if (idx == 0)
            visibility = "Public";
        else
            visibility = "Private";
        time = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
        rString = "";
        for (String each : imagepath) {
            rString = rString + each + "~";
        }
        Log.e("rString", rString);
        if (rString.length() > 1)
            rString = rString.substring(0, rString.length() - 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlteredCatalog.COLUMN_PRODUCT_ID, pid);
        contentValues.put(AlteredCatalog.COLUMN_TITLE, pname.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_CODE, pcode.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_IMAGEPATH, rString.toString().trim());
        contentValues.put(AlteredCatalog.COLUMN_GRANDPARENTCAT, selectedRoot);
        contentValues.put(AlteredCatalog.COLUMN_PARENTCAT, selectedparent);
        contentValues.put(AlteredCatalog.COLUMN_CHILDCAT, selectedchild);
        contentValues.put(AlteredCatalog.COLUMN_GRANDCHILDCAT, selectedgrandchild);
        contentValues.put(AlteredCatalog.COLUMN_CATEGORY_ID, selectedcatval);
        contentValues.put(AlteredCatalog.COLUMN_MRP, pmrp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_SP, psp.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_QTY, pqty.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_MINQTY, pmin.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_DESC, pdesc.getText().toString());
        contentValues.put(AlteredCatalog.COLUMN_VISIBILTY, visibility);
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_FLAG, "1");
        contentValues.put(AlteredCatalog.COLUMN_REQUEST_STATUS, "1");
        contentValues.put(AlteredCatalog.COLUMN_DEFAULT_POSITION, default_position);
        contentValues.put(AlteredCatalog.COLUMN_CREATEDAT, time);
        contentValues.put(AlteredCatalog.COLUMN_UPDATED, time);
        contentValues.put(AlteredCatalog.COLUMN_LOCAL_PROD_ID, pid);
        if (imagepath.size() > 0)
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, imagepath.get(default_position));
        else
            contentValues.put(AlteredCatalog.COLUMN_THUMB_PATH, "");
        contentValues.put(AlteredCatalog.COLUMN_COMPANY_ID, helper.getDefaults("company_id", getApplicationContext()));
        int save_upload = getContentResolver().update(MyContentProvider.CONTENT_URI_ALTER, contentValues, AlteredCatalog._ID + "=?", new String[]{row_id});

        if (save_upload == 1) {
            alertDialog.setTitle(getString(R.string.status));
            alertDialog.setMessage(getString(R.string.product_updated));
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Your positive action
                    startActivity(new Intent(EditDraftProduct.this, MyCatalog.class).putExtra("draft", "1"));
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
                    startActivity(new Intent(EditDraftProduct.this, MyCatalog.class).putExtra("draft", "1"));
                    finish();
                }
            });
            alertDialog.show();
        }


    }

    private void OfflineCategory() {
        String catsel = "";
        category.clear();
        products.clear();
        adap_1.notifyDataSetChanged();
        Cursor parent = getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_ID, CategoryTable.KEY_CATEGORY_NAME, CategoryTable.KEY_HAS_CHILD}, CategoryTable.KEY_PARENT_ID + "=?", new String[]{"0"}, null);
        Log.i(TAG, parent.getCount() + "");
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
            Log.i(TAG, "poarentiod--" + parentid + " / " + id);
            if (has_child.equalsIgnoreCase("1"))
                pm.setHas_child(true);
            else
                pm.setHas_child(false);
            if (parentid.equalsIgnoreCase(id)) {
                catsel = title;
            }
//            if (parent_allowedCats != null && parent_allowedCats.length != 0) {
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
        adap_1.notifyDataSetChanged();
        root_spin.setSelection(((CustomSpinAdapter) root_spin.getAdapter()).getPosition(catsel));
        parentid = "";
        parent.close();
    }

    private boolean CheckValidation() {
        if (!ValidationUtil.isNull(pname.getText().toString())) {
            if (!ValidationUtil.isNull(pcode.getText().toString())) {
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
                                        if (!ValidationUtil.isNull(pmrp.getText().toString())) {
                                            if (!ValidationUtil.isNull(psp.getText().toString())) {
                                                if (!ValidationUtil.isNull(pqty.getText().toString())) {
                                                    if (!ValidationUtil.isNull(pmin.getText().toString())) {
                                                        if (ValidationUtil.isValidNumber(pmrp.getText().toString())) {
                                                            if (ValidationUtil.isValidNumber(psp.getText().toString())) {
                                                                if (ValidationUtil.isValidNumber(pqty.getText().toString())) {
                                                                    if (ValidationUtil.isValidNumber(pmin.getText().toString())) {

                                                                        if (Double.parseDouble(pmrp.getText().toString()) >= Double.parseDouble(psp.getText().toString())) {

                                                                            if (Double.parseDouble(pqty.getText().toString()) >= Double.parseDouble(pmin.getText().toString())) {

                                                                                if (Double.parseDouble(pqty.getText().toString()) > 0)
                                                                                    if (Double.parseDouble(pmrp.getText().toString()) > 0) {
                                                                                        if (Double.parseDouble(psp.getText().toString()) > 0) {
                                                                                            if (Double.parseDouble(pmin.getText().toString()) > 0) {
                                                                                                return true;
                                                                                            } else {
                                                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.moq_not_0));
                                                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                                    pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                                } else {
                                                                                                    pmin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                                }
                                                                                                pmin.requestFocus();
                                                                                            }
                                                                                        } else {
                                                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.sp_cannot_0));
                                                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                                psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                            } else {
                                                                                                psp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                            }
                                                                                            psp.requestFocus();
                                                                                        }
                                                                                    } else {
                                                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.mrp_cannot_0));
                                                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                            pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                        } else {
                                                                                            pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                        }
                                                                                        pmrp.requestFocus();
                                                                                    }
                                                                                else {
                                                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.quantity_cannot_0));
                                                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                        pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                    } else {
                                                                                        pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                    }
                                                                                    pqty.requestFocus();
                                                                                }
                                                                            } else {
                                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.moq_less_quantity));
                                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                } else {
                                                                                    pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                }
                                                                                pqty.requestFocus();
                                                                            }
                                                                        } else {
                                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.sp_less_mrp));
                                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                            } else {
                                                                                pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                            }
                                                                            pmrp.requestFocus();
                                                                        }
//
                                                                    } else {
                                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_moq));
                                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                            pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        } else {
                                                                            pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        }
                                                                        pmin.requestFocus();
                                                                    }
                                                                } else {
                                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_quantity));
                                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                        pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    } else {
                                                                        pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    }
                                                                    pqty.requestFocus();
                                                                }
                                                            } else {
                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                    psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                } else {
                                                                    psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                }
                                                                psp.requestFocus();
                                                            }
                                                        } else {
                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            } else {
                                                                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            }
                                                            pmrp.requestFocus();
                                                        }
                                                    } else {
                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_moq));
                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                            pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        } else {
                                                            pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        }
                                                        pmin.requestFocus();
                                                    }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_quantity));
                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                        pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    } else {
                                                        pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    }
                                                    pqty.requestFocus();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_sp));
                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                    psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                } else {
                                                    psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                }
                                                psp.requestFocus();
                                            }
                                        } else {
                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_mrp));
                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            } else {
                                                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            }
                                            pmrp.requestFocus();
                                        }
                                    } else {
                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.select_leaf));
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                            last_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        } else {
                                            last_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        }
                                        last_spin.requestFocus();
                                    }
                                } else {
                                    if (!ValidationUtil.isNull(pmrp.getText().toString())) {
                                        if (!ValidationUtil.isNull(psp.getText().toString())) {
                                            if (!ValidationUtil.isNull(pqty.getText().toString())) {
                                                if (!ValidationUtil.isNull(pmin.getText().toString())) {
                                                    if (ValidationUtil.isValidNumber(pmrp.getText().toString())) {
                                                        if (ValidationUtil.isValidNumber(psp.getText().toString())) {
                                                            if (ValidationUtil.isValidNumber(pqty.getText().toString())) {
                                                                if (ValidationUtil.isValidNumber(pmin.getText().toString())) {

                                                                    if (Double.parseDouble(pmrp.getText().toString()) >= Double.parseDouble(psp.getText().toString())) {

                                                                        if (Double.parseDouble(pqty.getText().toString()) >= Double.parseDouble(pmin.getText().toString())) {

                                                                            if (Double.parseDouble(pqty.getText().toString()) > 0)
                                                                                if (Double.parseDouble(pmrp.getText().toString()) > 0) {
                                                                                    if (Double.parseDouble(psp.getText().toString()) > 0) {
                                                                                        if (Double.parseDouble(pmin.getText().toString()) > 0) {
                                                                                            return true;
                                                                                        } else {
                                                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.moq_not_0));
                                                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                                pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                            } else {
                                                                                                pmin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                            }
                                                                                            pmin.requestFocus();
                                                                                        }
                                                                                    } else {
                                                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.sp_cannot_0));
                                                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                            psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                        } else {
                                                                                            psp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                        }
                                                                                        psp.requestFocus();
                                                                                    }
                                                                                } else {
                                                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.mrp_cannot_0));
                                                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                        pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                    } else {
                                                                                        pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                    }
                                                                                    pmrp.requestFocus();
                                                                                }
                                                                            else {
                                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.quantity_cannot_0));
                                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                } else {
                                                                                    pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                }
                                                                                pqty.requestFocus();
                                                                            }
                                                                        } else {
                                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.moq_less_quantity));
                                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                            } else {
                                                                                pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                            }
                                                                            pqty.requestFocus();
                                                                        }
                                                                    } else {
                                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.sp_less_mrp));
                                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                            pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        } else {
                                                                            pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                        }
                                                                        pmrp.requestFocus();
                                                                    }
//
                                                                } else {
                                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_moq));
                                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                        pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    } else {
                                                                        pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    }
                                                                    pmin.requestFocus();
                                                                }
                                                            } else {
                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_quantity));
                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                    pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                } else {
                                                                    pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                }
                                                                pqty.requestFocus();
                                                            }
                                                        } else {
                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            } else {
                                                                psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            }
                                                            psp.requestFocus();
                                                        }
                                                    } else {
                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                            pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        } else {
                                                            pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        }
                                                        pmrp.requestFocus();
                                                    }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_moq));
                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                        pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    } else {
                                                        pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    }
                                                    pmin.requestFocus();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_quantity));
                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                    pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                } else {
                                                    pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                }
                                                pqty.requestFocus();
                                            }
                                        } else {
                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_sp));
                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            } else {
                                                psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            }
                                            psp.requestFocus();
                                        }
                                    } else {
                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_mrp));
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                            pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        } else {
                                            pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        }
                                        pmrp.requestFocus();
                                    }
                                }
                            } else {
                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.select_leaf));
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    child_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                } else {
                                    child_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                }
                                child_spin.requestFocus();
                            }
                        } else {
                            if (!ValidationUtil.isNull(pmrp.getText().toString())) {
                                if (!ValidationUtil.isNull(psp.getText().toString())) {
                                    if (!ValidationUtil.isNull(pqty.getText().toString())) {
                                        if (!ValidationUtil.isNull(pmin.getText().toString())) {
                                            if (ValidationUtil.isValidNumber(pmrp.getText().toString())) {
                                                if (ValidationUtil.isValidNumber(psp.getText().toString())) {
                                                    if (ValidationUtil.isValidNumber(pqty.getText().toString())) {
                                                        if (ValidationUtil.isValidNumber(pmin.getText().toString())) {

                                                            if (Double.parseDouble(pmrp.getText().toString()) >= Double.parseDouble(psp.getText().toString())) {

                                                                if (Double.parseDouble(pqty.getText().toString()) >= Double.parseDouble(pmin.getText().toString())) {

                                                                    if (Double.parseDouble(pqty.getText().toString()) > 0)
                                                                        if (Double.parseDouble(pmrp.getText().toString()) > 0) {
                                                                            if (Double.parseDouble(psp.getText().toString()) > 0) {
                                                                                if (Double.parseDouble(pmin.getText().toString()) > 0) {
                                                                                    return true;
                                                                                } else {
                                                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.moq_not_0));
                                                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                        pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                    } else {
                                                                                        pmin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                    }
                                                                                    pmin.requestFocus();
                                                                                }
                                                                            } else {
                                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.sp_cannot_0));
                                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                                } else {
                                                                                    psp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                                }
                                                                                psp.requestFocus();
                                                                            }
                                                                        } else {
                                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.mrp_cannot_0));
                                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                            } else {
                                                                                pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                            }
                                                                            pmrp.requestFocus();
                                                                        }
                                                                    else {
                                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.quantity_cannot_0));
                                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                            pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                        } else {
                                                                            pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                        }
                                                                        pqty.requestFocus();
                                                                    }
                                                                } else {
                                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.moq_less_quantity));
                                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                        pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                    } else {
                                                                        pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                    }
                                                                    pqty.requestFocus();
                                                                }
                                                            } else {
                                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.sp_less_mrp));
                                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                    pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                                } else {
                                                                    pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                                                                }
                                                                pmrp.requestFocus();
                                                            }
//
                                                        } else {
                                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_moq));
                                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                                pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            } else {
                                                                pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                            }
                                                            pmin.requestFocus();
                                                        }
                                                    } else {
                                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_quantity));
                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                            pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        } else {
                                                            pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                        }
                                                        pqty.requestFocus();
                                                    }
                                                } else {
                                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                        psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    } else {
                                                        psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                    }
                                                    psp.requestFocus();
                                                }
                                            } else {
                                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
                                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                    pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                } else {
                                                    pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                                }
                                                pmrp.requestFocus();
                                            }
                                        } else {
                                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_moq));
                                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            } else {
                                                pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                            }
                                            pmin.requestFocus();
                                        }
                                    } else {
                                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_quantity));
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                            pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        } else {
                                            pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                        }
                                        pqty.requestFocus();
                                    }
                                } else {
                                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_sp));
                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                        psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                    } else {
                                        psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                    }
                                    psp.requestFocus();
                                }
                            } else {
                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_mrp));
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                } else {
                                    pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                                }
                                pmrp.requestFocus();
                            }
                        }

                    } else {
                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.select_category));
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            parent_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        } else {
                            parent_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        }
                        parent_spin.requestFocus();
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.select_meta_category));
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        root_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    } else {
                        root_spin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    }
                    root_spin.requestFocus();
                }
            } else {
                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_product_code));
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    pcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                } else {
                    pcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                }
                pcode.requestFocus();
            }
        } else {
            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.empty_product_name));
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                pname.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            } else {
                pname.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            }
            pname.requestFocus();
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

        if (ValidationUtil.isNull(pname.getText().toString())) {
            if (ValidationUtil.isNull(pcode.getText().toString())) {
                if (root_spin.getSelectedItem().toString()
                        .equalsIgnoreCase("SELECT META CATEGORY")) {
                    if (parent_spin.getSelectedItem().toString()
                            .equalsIgnoreCase("SELECT CATEGORY")) {
                        if (child_spin.getSelectedItem() != null) {
                            if (child_spin.getSelectedItem().toString()
                                    .equalsIgnoreCase("SELECT LEAF CATEGORY")) {
                                if (ValidationUtil.isNull(pmrp.getText().toString())) {
                                    if (ValidationUtil.isNull(psp.getText().toString())) {
                                        if (ValidationUtil.isNull(pqty.getText().toString())) {
                                            if (ValidationUtil.isNull(pmin.getText().toString())) {
                                                if (ValidationUtil.isNull(pdesc.getText().toString())) {
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
                            if (ValidationUtil.isNull(pmrp.getText().toString())) {
                                if (ValidationUtil.isNull(psp.getText().toString())) {
                                    if (ValidationUtil.isNull(pqty.getText().toString())) {
                                        if (ValidationUtil.isNull(pmin.getText().toString())) {
                                            if (ValidationUtil.isNull(pdesc.getText().toString())) {
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

        if (ValidationUtil.isValidNumber2(pmrp.getText().toString())) {
            if (ValidationUtil.isValidNumber2(psp.getText().toString())) {
                if (ValidationUtil.isValidNumber2(pmin.getText().toString())) {
                    if (ValidationUtil.isValidNumber2(pqty.getText().toString())) {
                        return true;
                    } else {
                        new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_min));
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            pqty.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                        } else {
                            pqty.setBackground(getResources().getDrawable(R.drawable.error_bar));
                        }
                        pqty.requestFocus();
                        return false;
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        pmin.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                    } else {
                        pmin.setBackground(getResources().getDrawable(R.drawable.error_bar));
                    }
                    pmin.requestFocus();
                    return false;
                }
            } else {
                new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_sp));
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    psp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
                } else {
                    psp.setBackground(getResources().getDrawable(R.drawable.error_bar));
                }
                psp.requestFocus();
                return false;
            }
        } else {
            new AlertDialogManager().showAlertDialog(EditDraftProduct.this, getString(R.string.oops), getString(R.string.invalid_mrp));
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                pmrp.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_bar));
            } else {
                pmrp.setBackground(getResources().getDrawable(R.drawable.error_bar));
            }
            pmrp.requestFocus();
            return false;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        adapter.notifyDataSetChanged();
        progress.dismiss();
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
                                EditDraftProduct.default_position = (int) imgflag.getTag();
                            }
                            if (item.getTitle().toString().equalsIgnoreCase("Delete")) {

                                imagegallery.remove((int) imgflag.getTag());
                                imagepath.remove((int) imgflag.getTag());
                                if (imagegallery.size() == 1)
                                    EditDraftProduct.titleIndicator.setVisibility(View.GONE);
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
        Call<CategoryHolder> call = service.FetchCategory("true", "1", helper.getB64Auth(EditDraftProduct.this), "application/json", "application/json");
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
                                OfflineCategory();
                            } else {
                                new AlertDialogManager().showAlertDialog(EditDraftProduct.this , getString(R.string.oops),
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

                        final SessionManager sessionManager = new SessionManager(EditDraftProduct.this);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.logoutUser();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    } else {
                        if (EditDraftProduct.this != null && !EditDraftProduct.this.isFinishing()) {
                            new AlertDialogManager().showAlertDialog(EditDraftProduct.this,
                                    getString(R.string.error),
                                    getString(R.string.server_error));
                        }
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {

                progress.dismiss();
                if (EditDraftProduct.this != null && !EditDraftProduct.this.isFinishing()) {

                    new AlertDialogManager().showAlertDialog(EditDraftProduct.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
                }

            }
        });

    }

}