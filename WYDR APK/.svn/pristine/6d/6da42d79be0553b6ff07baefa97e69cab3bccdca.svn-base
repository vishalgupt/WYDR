
package wydr.sellers.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.adapter.ImageViewPagerAdapter;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.DetailLoader;
import wydr.sellers.network.JSONParser;
import wydr.sellers.registration.Helper;
import wydr.sellers.slider.CirclePageIndicator;
import wydr.sellers.slider.MyContentProvider;
import wydr.sellers.syncadap.FeedCatalog;

/**
 * Created by Navdeep on 9/9/15.
 */
@SuppressLint("ValidFragment")
public class MyCatalogProdDetFrag extends Fragment {
    TextView productName, mrp, sp, quantity, sellerInfo, productDescription, product_qty;
    public String product_id, productid,  TAG = "MyCatalogDetailsFrag";
    ImageView fav_image, chatseller;
    JSONObject jsonObject;
    Helper helper = new Helper();
    ConnectionDetector cd;
    TextView book_order;
    Button buy_now,coupon_button;
    android.app.AlertDialog.Builder alertDialog;
    DetailLoader loader;
    ViewPager viewPager;
    LinearLayout ll123;
    PagerAdapter adapter;
    public CirclePageIndicator titleIndicator;
    Button des_ish,fea_ish;
    JSONParser parser;

    public MyCatalogProdDetFrag(String product_id) {
        this.product_id = product_id;
    }

    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    private List<Bitmap> imagegallery = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.item_detail, null);
        //--------------istiaque------------------//
        coupon_button = (Button) contentView.findViewById(R.id.coupon_button);
        coupon_button.setVisibility(View.GONE);
        ll123 = (LinearLayout) contentView.findViewById(R.id.ll123);
        ll123.setVisibility(View.GONE);
        loader = new DetailLoader(getActivity().getApplicationContext());
        productName = (TextView) contentView.findViewById(R.id.proname);
        mrp = (TextView) contentView.findViewById(R.id.promrp);
        sp = (TextView) contentView.findViewById(R.id.prosp);
        des_ish = (Button)contentView.findViewById(R.id.desc_title);
        des_ish.setVisibility(View.GONE);
        fea_ish = (Button)contentView.findViewById(R.id.feature_title);
        fea_ish.setVisibility(View.GONE);
        quantity = (TextView) contentView.findViewById(R.id.prominorder);

        sellerInfo = (TextView) contentView.findViewById(R.id.id_sellername);

        chatseller = (ImageView) contentView.findViewById(R.id.chatseller);
        productDescription = (TextView) contentView.findViewById(R.id.prodesc);
        fav_image = (ImageView) contentView.findViewById(R.id.favourite_btn);
        book_order = (TextView) contentView.findViewById(R.id.book_order);
        product_qty = (TextView) contentView.findViewById(R.id.qty);
        buy_now = (Button) contentView.findViewById(R.id.btnBuyNow);
        parser = new JSONParser();
        fav_image.setVisibility(View.GONE);
        book_order.setVisibility(View.GONE);
        chatseller.setVisibility(View.GONE);
        // callseller.setVisibility(View.GONE);
        buy_now.setVisibility(View.GONE);
        viewPager = (ViewPager) contentView.findViewById(R.id.pager);
        titleIndicator = (CirclePageIndicator) contentView.findViewById(R.id.titles);
        adapter = new ImageViewPagerAdapter(getActivity(), imagegallery);

        //   progressStuff();
        GetProductDetails();
        return contentView;
    }

    private void GetProductDetails() {

        final String[] PROJECTION = new String[]{
                FeedCatalog._ID,
                FeedCatalog.COLUMN_PRODUCT_ID,
                FeedCatalog.COLUMN_TITLE,
                FeedCatalog.COLUMN_CODE,
                FeedCatalog.COLUMN_IMAGEPATH,
                FeedCatalog.COLUMN_GRANDPARENTCAT,
                FeedCatalog.COLUMN_PARENTCAT,
                FeedCatalog.COLUMN_CHILDCAT,
                FeedCatalog.COLUMN_MRP,
                FeedCatalog.COLUMN_SP,
                FeedCatalog.COLUMN_QTY,
                FeedCatalog.COLUMN_MINQTY,
                FeedCatalog.COLUMN_DESC,
                FeedCatalog.COLUMN_VISIBILTY,
                FeedCatalog.COLUMN_GRANDCHILDCAT

        };

        int COLUMN_ID = 0, COLUMN_PRODUCT_ID = 1, COLUMN_TITLE = 2, COLUMN_CODE = 3, COLUMN_IMAGEPATH = 4, COLUMN_GRANDPARENTCAT = 5, COLUMN_PARENTCAT = 6,
                COLUMN_CHILDCAT = 7, COLUMN_MRP = 8, COLUMN_SP = 9, COLUMN_QTY = 10, COLUMN_MINQTY = 11, COLUMN_DESC = 12, COLUMN_VISIBILTY = 13, COLUMN_GRANDCHILDCAT = 14/*,COLUMN_STATUS = 14,COLUMN_UPDATEDAT = 15,COLUMN_CREATEDAT = 16*/;
        Cursor parent = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_FEED, PROJECTION, FeedCatalog.COLUMN_PRODUCT_ID + "=?", new String[]{product_id}, null);
        assert parent != null;

        while (parent.moveToNext()) {

            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            titleIndicator.setViewPager(viewPager);
            String[] s = parent.getString(COLUMN_IMAGEPATH).split("~");
            Log.i(TAG, "s.length-" + s.length);
            Bitmap bitmap;
            for (int i = 0; i < s.length; i++) {
                Log.i(TAG, "s[i]-" + s[i]);
                if(s[i]!=null && !s[i].equalsIgnoreCase("")){
                     bitmap = helper.decodeSampledBitmapFromResource(s[i], 300, 200);
                    imagegallery.add(bitmap);
                    adapter.notifyDataSetChanged();
                }

            }
            if(imagegallery.size()==0)
            {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_product);
                imagegallery.add(bitmap);
                adapter.notifyDataSetChanged();
            }
            if (imagegallery.size() == 1)
                titleIndicator.setVisibility(View.GONE);

            productid = parent.getString(COLUMN_PRODUCT_ID);
            productName.setText(parent.getString(COLUMN_TITLE));
            mrp.setText("\u20B9" + String.format("%.2f", Double.parseDouble(parent.getString(COLUMN_MRP))));
            mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            sp.setText("\u20B9" + String.format("%.2f", Double.parseDouble(parent.getString(COLUMN_SP))));
            quantity.setText("MOQ : " + parent.getString(COLUMN_MINQTY));

            product_qty.setText(parent.getString(COLUMN_CODE));

            sellerInfo.setText(MyTextUtils.toTitleCase(helper.getDefaults("firstname", getActivity()) + ", " + helper.getDefaults("company", getActivity())));
            final String phone_no = helper.getDefaults("contact", getActivity());
            Spannable wordtoSpan = new SpannableString("Product Description : " + parent.getString(COLUMN_DESC));
            wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            productDescription.setText(wordtoSpan.toString().replaceAll("null", ""));
            // gettree(parent.getString(COLUMN_GRANDPARENTCAT),parent.getString(COLUMN_PARENTCAT),parent.getString(COLUMN_CHILDCAT),parent.getString(COLUMN_GRANDCHILDCAT));
        }
        parent.close();


    }

//    private void gettree(String grand, String parentid, String child,String leafid) {
//       // Log.i(TAG, "gettree - " + grand + "/" + parentid + "/" + child);
//        String tree="";
//        if(grand!=null)
//        {
//            Cursor parent = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_NAME}, CategoryTable.KEY_CATEGORY_ID + "=?", new String[]{grand}, null);
//            assert parent!=null;
//            while(parent.moveToNext())
//            {
//                tree=tree+ parent.getString(0);
//                //Log.i(TAG,"gettree - grand " + grand+"/"+parent.getString(0));
//            }
//            parent.close();
//        }
//        if(parentid!=null) {
//            Cursor parent2 = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_NAME}, CategoryTable.KEY_CATEGORY_ID + "=?", new String[]{parentid}, null);
//            assert parent2 != null;
//            while (parent2.moveToNext()) {
//                tree = tree + " > " + parent2.getString(0);
//               // Log.i(TAG, "gettree - parent " + grand + "/" + parent2.getString(0));
//            }
//            parent2.close();
//        }
//        if(child!=null && !child.equalsIgnoreCase(""))
//        {
//            Cursor parent3 = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_NAME}, CategoryTable.KEY_CATEGORY_ID + "=?", new String[]{child}, null);
//            assert parent3!=null;
//            while(parent3.moveToNext())
//            {
//                tree=tree+ " > " + parent3.getString(0);
//               // Log.i(TAG,"gettree - child " + grand+"/"+parent3.getString(0));
//            }
//            parent3.close();
//        }
//        if(leafid!=null && !leafid.equalsIgnoreCase(""))
//        {
//            Cursor parent3 = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI_Category, new String[]{CategoryTable.KEY_CATEGORY_NAME}, CategoryTable.KEY_CATEGORY_ID + "=?", new String[]{leafid}, null);
//            assert parent3!=null;
//            while(parent3.moveToNext())
//            {
//                tree=tree+ " > " + parent3.getString(0);
//                // Log.i(TAG,"gettree - child " + grand+"/"+parent3.getString(0));
//            }
//            parent3.close();
//        }
//        category.setText(tree);
//     //   Log.i(TAG,"final tree - " + tree);
//
//
//    }


    public void fun(View v) {

    }


}
