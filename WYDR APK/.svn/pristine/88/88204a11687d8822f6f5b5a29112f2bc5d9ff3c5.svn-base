package wydr.sellers.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import wydr.sellers.R;

/**
 * Created by Navdeep on 10/9/15.
 */
public class MyCatalogProdDetails extends AppCompatActivity {

    Toolbar mToolbar;
    private ImageView sliderMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("");
        findViewById(R.id.toolbarimg).setVisibility(View.GONE);
        TextView toolbartext = (TextView) findViewById(R.id.toolbartext);
        toolbartext.setText(getIntent().getStringExtra("name"));
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sliderMenu = (ImageView) findViewById(R.id.btnMenu);
        sliderMenu.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyCatalogProdDetFrag(getIntent().getStringExtra("product_id"))).commit();
    }

    public void fun(View v) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
