package wydr.sellers.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.modal.ProductOrderedDetails;

/**
 * Created by surya on 3/12/15.
 */
public class ThankActivity extends AppCompatActivity {
    Button continueShopping;
    Toolbar mToolbar;
    LinearLayout linear_white, linear_thank;
    String orderId, status, product_id, name, qty, ordertotal, category, price;
    TextView statusReport,thankYou;
    String string;
    ImageView thanksImage;
    ArrayList<ProductOrderedDetails> productOrderedDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank);
        thanksImage = (ImageView) findViewById(R.id.thank_image);
        statusReport = (TextView) findViewById(R.id.txtStatusReport);
        thankYou = (TextView) findViewById(R.id.thankU);
        linear_white = (LinearLayout) findViewById(R.id.ll_white);
        linear_thank = (LinearLayout) findViewById(R.id.ll_thanku);
        linear_white.setVisibility(View.GONE);
        linear_thank.setVisibility(View.VISIBLE);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderId = getIntent().getStringExtra("order_id");
        status = getIntent().getStringExtra("status");
        product_id = getIntent().getStringExtra("product_id");
        name = getIntent().getStringExtra("name");
        qty = getIntent().getStringExtra("qty");
        ordertotal = getIntent().getStringExtra("ordertotal");
        category = getIntent().getStringExtra("category");

        productOrderedDetailsList = (ArrayList<ProductOrderedDetails>)getIntent().getSerializableExtra("productOrderedDetailsList");

        if (status.equalsIgnoreCase("P"))
        {
            thanksImage.setImageResource(R.drawable.thanku_icon);
            getSupportActionBar().setTitle("Order Successful");
            thankYou.setText(getString(R.string.thank_u));
            statusReport.setText(getString(R.string.order_has_been_placed) + orderId + "\nStatus: Paid");
        }
        else if (status.equalsIgnoreCase("G"))
        {
            thanksImage.setImageResource(R.drawable.thanku_icon);
            getSupportActionBar().setTitle("Order Successful");
            thankYou.setText(getString(R.string.thank_u));
            statusReport.setText(getString(R.string.order_has_been_placed) + orderId + "\nStatus: Open COD");
        }
        else {
            thanksImage.setImageResource(R.drawable.bg_cross);
            thankYou.setText(getString(R.string.oops));
            getSupportActionBar().setTitle("Order Failed");
            statusReport.setText(getString(R.string.order_has_been_failed) + orderId + "\nStatus: Failed");
        }


        continueShopping = (Button) findViewById(R.id.txtContinueShopping);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            for (ProductOrderedDetails productOrderedDetails : productOrderedDetailsList)
            {
            /*Toast.makeText(ThankActivity.this, productOrderedDetails.getProduct_id() + "\n" + productOrderedDetails.getName()
                    + "\n" + productOrderedDetails.getCategory() + "\n" + productOrderedDetails.getPrice() + "\n" + productOrderedDetails.getQty()
                    + "\n" + productOrderedDetails.getOrderId() + "\n" + ordertotal, Toast.LENGTH_SHORT).show();

            Log.d("PRODUCT_DETAIL", productOrderedDetails.getProduct_id() + "\n" + productOrderedDetails.getName()
                    + "\n" + productOrderedDetails.getCategory() + "\n" + productOrderedDetails.getPrice() + "\n" + productOrderedDetails.getQty()
                    + "\n" + productOrderedDetails.getOrderId() + "\n" + ordertotal +"");*/

                Product product =  new Product()
                        .setId(productOrderedDetails.getProduct_id()) // product id
                        .setName(productOrderedDetails.getName()) // product name
                        .setCategory(productOrderedDetails.getCategory())
                        .setPrice(Double.parseDouble(productOrderedDetails.getPrice())) // product price
                        .setQuantity(Integer.parseInt(productOrderedDetails.getQty())); // product quantity
                ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                        .setTransactionId(productOrderedDetails.getOrderId()) // orderID
                        .setTransactionRevenue(Double.parseDouble(ordertotal)); // total amount

                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);

                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                mTracker.setScreenName("ThankActivity");
                mTracker.set("&cu", "INR");
                mTracker.send(new HitBuilders.ScreenViewBuilder().addProduct(product).setProductAction(productAction).build());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
