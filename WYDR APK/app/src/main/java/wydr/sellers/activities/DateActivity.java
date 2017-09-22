package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import wydr.sellers.R;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.JSONParser;

/**
 * Created by surya on 2/10/15.
 */
public class DateActivity extends AppCompatActivity implements View.OnClickListener {
    Button cancel, apply;
    TextView from, to;
    ConnectionDetector cd;
    JSONParser parser;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_activity);

        apply = (Button) findViewById(R.id.buttonApplyDate);
        cancel = (Button) findViewById(R.id.buttonCancelDate);

        apply.setOnClickListener(this);
        cancel.setOnClickListener(this);

        //displayView(0);

        from = (TextView) findViewById(R.id.buttonfrom);
        to = (TextView) findViewById(R.id.buttonTO);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonApplyDate:
                if (from.getText().toString().length() > 1) {
                    if (to.getText().toString().length() > 1) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("from", from.getText().toString().trim());
                        returnIntent.putExtra("to", to.getText().toString().trim());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getString(R.string.select_date_range));
                    }

                } else {
                    new AlertDialogManager().showAlertDialog(this, getResources().getString(R.string.oops), getString(R.string.select_date_range));
                }

                break;
            case R.id.buttonCancelDate:
                Intent returnIntent1 = new Intent();
                //  JSONObject object = new JSONObject();
                // returnIntent1.putExtra("result", object.toString());
                setResult(RESULT_CANCELED, returnIntent1);
                finish();
                break;

        }
    }


}
