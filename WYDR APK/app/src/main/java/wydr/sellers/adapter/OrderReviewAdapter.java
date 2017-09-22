package wydr.sellers.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import wydr.sellers.R;
import wydr.sellers.modal.OrderReviewObjects;

/**
 * Created by Ishtiyaq on 4/13/2016.
 */


public class OrderReviewAdapter extends RecyclerView.Adapter<OrderReviewAdapter.ViewHolder>
{
    private List<OrderReviewObjects> orderReviewObjects;
    Activity context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public ImageView productImg;
        public TextView productTitle, productQty, originalPrice, disPrice, couponCode, couponCodeStatus, discountPercent;

        public ViewHolder(View v) {
            super(v);

            //mTextView = v;
            productImg = (ImageView) v.findViewById(R.id.productImg);
            productTitle = (TextView) v.findViewById(R.id.productTitle);
            productQty = (TextView) v.findViewById(R.id.productQty);
            originalPrice = (TextView) v.findViewById(R.id.originalPrice);
            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            disPrice = (TextView) v.findViewById(R.id.disPrice);
            couponCode = (TextView) v.findViewById(R.id.couponCode);
            couponCodeStatus = (TextView) v.findViewById(R.id.couponCodeStatus);
            discountPercent = (TextView) v.findViewById(R.id.discountPercent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderReviewAdapter(List<OrderReviewObjects> orderReviewObjects, Activity context) {
        this.orderReviewObjects = orderReviewObjects;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Picasso.with(context)
                .load(orderReviewObjects.get(position).getProductImg())
                .error(R.drawable.default_product)
                .placeholder(R.drawable.default_product)
                .into(holder.productImg);


        holder.productTitle.setText(orderReviewObjects.get(position).getProductTitle());
        holder.productQty.setText(orderReviewObjects.get(position).getProductQty());

        String dp = orderReviewObjects.get(position).getDisPrice();
        if (Double.parseDouble(dp) > 0) {
            holder.disPrice.setText(orderReviewObjects.get(position).getDisPrice());
            holder.originalPrice.setText(orderReviewObjects.get(position).getOriginalPrice());
        } else {
            holder.disPrice.setText(orderReviewObjects.get(position).getOriginalPrice());
            holder.originalPrice.setText("");
        }

        holder.couponCode.setText(orderReviewObjects.get(position).getCouponCode());
        holder.couponCodeStatus.setText(orderReviewObjects.get(position).getCouponCodeStatus());
        holder.discountPercent.setText(orderReviewObjects.get(position).getDiscountPercent());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return orderReviewObjects == null ? 0 : orderReviewObjects.size();
    }
}