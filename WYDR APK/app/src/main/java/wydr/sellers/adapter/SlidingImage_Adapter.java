package wydr.sellers.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.activities.MarketPlaceProductsActivity;
import wydr.sellers.activities.ProductDetailsActivity;
import wydr.sellers.modal.BannerModel;

/**
 * Created by SONU on 29/08/15.
 */
public class SlidingImage_Adapter extends PagerAdapter
{
    private ArrayList<BannerModel> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager fm;

    public SlidingImage_Adapter(Context context, ArrayList<BannerModel> IMAGES)
    {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getCount()
    {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("p".equalsIgnoreCase(IMAGES.get(position).getProductType())) {
                    Intent i = new Intent(context, ProductDetailsActivity.class);
                    i.putExtra("name", IMAGES.get(position).getProductName());
                    i.putExtra("product_id", IMAGES.get(position).getProduct_id());
                    //Toast.makeText(context,IMAGES.get(position).getProduct_id(),Toast.LENGTH_SHORT).show();
                    context.startActivity(i);
                }
                else if("N".equalsIgnoreCase(IMAGES.get(position).getProductType()))
                {

                }
                else {
                    Intent i2 = new Intent(context, MarketPlaceProductsActivity.class);
                    i2.putExtra("name", IMAGES.get(position).getProductName());
                    i2.putExtra("id", IMAGES.get(position).getProduct_id());
                    //  Toast.makeText(context,IMAGES.get(position).getProduct_id(),Toast.LENGTH_SHORT).show();
                    context.startActivity(i2);
                }
            }
        });

    //imageView.setImageResource(IMAGES.get(position));
        Picasso.with(context)
                .load(IMAGES.get(position).getBanner_photo())
               // .placeholder(R.drawable.def)
                .into(imageView);
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
