package wydr.sellers.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import wydr.sellers.R;

/**
 * Created by Deepesh_pc on 29-09-2015.
 */
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

        optionflag.setVisibility(View.GONE);

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
