package wydr.sellers.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.acc.MyTextUtils;
import wydr.sellers.modal.CatalogProductModal;

/**
 * Created by surya on 3/10/15.
 */
public class CatalogAdapter extends ArrayAdapter<CatalogProductModal> {

    public ListLoader imageLoader;
    private List<CatalogProductModal> itemList;
    private Context ctx;
    private int layoutId;

    public CatalogAdapter(Context ctx, List<CatalogProductModal> itemList, int layoutId) {
        super(ctx, layoutId, itemList);
        this.itemList = itemList;
        this.ctx = ctx;
        this.layoutId = layoutId;
        imageLoader = new ListLoader(ctx.getApplicationContext());
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public CatalogProductModal getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(layoutId, parent, false);
        }

        // We should use class holder pattern
        TextView title = (TextView) result.findViewById(R.id.txtTitleAttach); //
        TextView code = (TextView) result.findViewById(R.id.txtCodeAttech); //
        TextView mrp = (TextView) result.findViewById(R.id.txtMRPAttech); //
        TextView moq = (TextView) result.findViewById(R.id.txtmoq);
        TextView sell = (TextView) result.findViewById(R.id.txtsp); //
        ImageView thumb_image = (ImageView) result.findViewById(R.id.orderThumb);
        CatalogProductModal song = itemList.get(position);
        moq.setText("MOQ : " + song.getMoq());
        title.setText(MyTextUtils.toTitleCase(song.getName()));
        code.setText("Product Code : " + song.getCode());
        mrp.setText("MRP :" + ctx.getResources().getString(R.string.rs) + " " + song.getPrice());
        sell.setText("SP :" + ctx.getResources().getString(R.string.rs) + " " + song.getSellingPrice());


        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (song.getImgUrl() != null) {
            Log.d("iamge", song.getImgUrl());
            imageLoader.DisplayImage(song.getImgUrl(), thumb_image, R.drawable.default_product);

        }
        return result;

    }
}
