package wydr.sellers.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.slider.CirclePageIndicator;

/**
 * Created by Deepesh_pc on 10-09-2015.
 */


public class ImageActivity extends AppCompatActivity {


    public static int flag = 0;
    public int pos;
    ConnectionDetector cd;
    ArrayList<String> imageArray;
    PagerAdapter adapter;
    ViewPager viewPager;

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagelayout);
        viewPager = (ViewPager) findViewById(R.id.imagepager);
        imageArray = getIntent().getStringArrayListExtra("bitmap");
        adapter = new ImageViewPagerAdapter(ImageActivity.this, imageArray);
        viewPager.setAdapter(adapter);
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.imagetitles);
        titleIndicator.setViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void fun(View v) {
    }

    private class ImageViewPagerAdapter extends PagerAdapter {

        Context context;
        List<String> flag;
        LayoutInflater inflater;

        public ImageViewPagerAdapter(Context context, List<String> flag) {
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
            // Locate the ImageView in viewpager_item.xml
            imgflag = (ImageView) itemView.findViewById(R.id.flag);
            optionflag = (ImageView) itemView.findViewById(R.id.flag2);
            optionflag.setVisibility(View.GONE);
            imgflag.setTag(position);
            Bitmap bmp = decodeSampledBitmapFromResource(flag.get(position),
                    400, 200);
            imgflag.setImageBitmap(bmp);
            container.addView(itemView);
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
}


