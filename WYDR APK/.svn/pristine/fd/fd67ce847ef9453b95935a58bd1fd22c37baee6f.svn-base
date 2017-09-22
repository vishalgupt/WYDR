package wydr.sellers.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

import wydr.sellers.R;
import wydr.sellers.acc.TouchImageView;


/**
 * Created by surya on 4/11/15.
 */
public class ImagePagerActivity extends Activity {

    private static final String STATE_POSITION = "STATE_POSITION";
    static int pagerPosition;
    //
    DisplayImageOptions options;
    ImageView btnClose, btnPre, btnNext;
    ViewPager pager;
    ImageLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fullscreen_image);
        btnClose = (ImageView) findViewById(R.id.btnClose);
        btnPre = (ImageView) findViewById(R.id.btnPreImg);
        btnNext = (ImageView) findViewById(R.id.btnnextImg);
        loader = ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(this));
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final String[] imageUrls = bundle.getStringArray("url");
        Log.d("image", imageUrls[0]);
//        pagerPosition = bundle.getInt("pos");
//
//        if (savedInstanceState != null) {
//            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
//        }

        options = new DisplayImageOptions.Builder()

                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(500)).build();

        pager = (ViewPager) findViewById(R.id.pager2);
        pager.setAdapter(new ImagePagerAdapter(imageUrls));
        pager.setCurrentItem(pagerPosition);
        if (imageUrls.length == 1) {
            btnPre.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (pagerPosition > 0) {
                    pagerPosition--;
                    pager.setCurrentItem(pagerPosition);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (pagerPosition < imageUrls.length) {
                    pagerPosition++;
                    pager.setCurrentItem(pagerPosition);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private String[] images;
        private LayoutInflater inflater;

        ImagePagerAdapter(String[] images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {

            View imageLayout = inflater.inflate(R.layout.item_pager_image,
                    view, false);
            assert imageLayout != null;
            TouchImageView imageView = (TouchImageView) imageLayout
                    .findViewById(R.id.image_zoom);

            final ProgressBar spinner = (ProgressBar) imageLayout
                    .findViewById(R.id.loading);
            spinner.setVisibility(View.GONE);
            Log.d("Hiiiii", images[position]);
            File imgFile = new File(images[position]);

            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                // ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);

                imageView.setImageBitmap(myBitmap);

            }

            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }
}
