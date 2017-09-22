package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import wydr.sellers.R;
import wydr.sellers.registration.Helper;

/**
 * Created by aksha_000 on 4/20/2016.
 */

public class TermsAndUse extends AppCompatActivity {

    Toolbar mToolbar;
    WebView termsAndUse;
    Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_use);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(helper.getDefaults("term_condition", TermsAndUse.this));//getString(R.string.termsUse));

        termsAndUse = (WebView) findViewById(R.id.termsAndUse);

        final ProgressDialog pd = ProgressDialog.show(TermsAndUse.this, "Please Wait", "Page is Loading...", true);

        termsAndUse.getSettings().setJavaScriptEnabled(true); // enable javascript

        termsAndUse.getSettings().setLoadWithOverviewMode(false);
        /*webView.getSettings().setUseWideViewPort(true);
        termsAndUse.getSettings().setBuiltInZoomControls(true);*/

        termsAndUse.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(TermsAndUse.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();
                /*String webUrl = webView.getUrl();
                Toast.makeText(Help.this, webUrl+"", Toast.LENGTH_SHORT).show()*/;
            }

        });

        termsAndUse.loadUrl("http://wydr.in/termsofuse.php");



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
