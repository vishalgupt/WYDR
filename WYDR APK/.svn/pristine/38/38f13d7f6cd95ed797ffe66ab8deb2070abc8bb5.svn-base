package wydr.sellers.activities;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.modal.IssueDetails;
import wydr.sellers.network.AlertDialogManager;
import wydr.sellers.network.ConnectionDetector;
import wydr.sellers.network.RestClient;
import wydr.sellers.registration.Helper;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import wydr.sellers.slider.JSONParser;

/**
 * Created by Istiaque on 3/5/2016.
 */
public class ContactUs extends AppCompatActivity {

    Toolbar mToolbar;
    Spinner issueType, subIssue;
    TextView instruction_lbl;
    EditText issueDesc;
    Button submitIssue;
    JSONObject rootJSON;
    JSONArray rangesJSON;
    ConnectionDetector cd;
    ArrayList<String> issueTypeList, subIssueList;
    String issue_type, sub_issue;
    private ProgressDialog progress;
    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.Contact));

        // Network Info
        cd = new ConnectionDetector(ContactUs.this);

        //getting reference of views
        getViews();

        if (cd.isConnectingToInternet()) {
            // Calling async task to get json
            new Issues().execute();
        } else {
            Toast.makeText(ContactUs.this, "Network error. Please check your internet connection", Toast.LENGTH_LONG).show();
        }

        setOnClickListener();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Controller application = (Controller) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Contact Us");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void init() {
        try {
            if (rootJSON != null) {
                //JSONObject mainJSON = new JSONObject(String.valueOf(rootJSON));
                rangesJSON= rootJSON.getJSONArray("ranges");

                Log.e("ranges", rangesJSON.length()+"");
                issueTypeList = new ArrayList<String>();

                for (int i = 0; i < rangesJSON.length(); i++) {
                    JSONObject jsonObject = rangesJSON.getJSONObject(i);
                    String issueType = jsonObject.getString("issue_type");
                    issueTypeList.add(issueType);
                }

                ArrayAdapter<String> issueTypeAdapter = new ArrayAdapter<String>(
                        ContactUs.this, android.R.layout.simple_spinner_item, issueTypeList);
                issueTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                issueType.setAdapter(issueTypeAdapter);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*** Async task class to get json by making HTTP call ****/

    class Issues extends AsyncTask<String,String,String> {
        List<NameValuePair> para;
        @Override
        protected String doInBackground(String... strings) {//2a
            JSONParser jsonParser = new JSONParser();
            rootJSON = jsonParser.getJSONFromUrlGet(AppUtil.URL + "issues",para,getApplicationContext());
            Log.e("jsonIssueObject", rootJSON.toString());
            return null;
        }

        @Override
        protected void onPreExecute() {//1
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {//3
            super.onPostExecute(s);
            init();
        }

        @Override
        protected void onProgressUpdate(String... values) {//2b
            super.onProgressUpdate(values);
        }
    }

    /***************** OnClick listener ***********************/
    private void setOnClickListener() {

        //issueTypes
        issueType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                issue_type = parent.getItemAtPosition(position).toString();

                if (issue_type.equalsIgnoreCase("Orders")){
                    subIssueList = new ArrayList<String>();

                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(0);
                        JSONArray subIssueJSON = jsonObject.getJSONArray("sub_issue_type");

                        for (int i = 0; i < subIssueJSON.length(); i++){
                            subIssueList.add(subIssueJSON.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> subIssueAdapter = new ArrayAdapter<String>(
                            ContactUs.this, android.R.layout.simple_spinner_item, subIssueList);
                    subIssueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIssue.setAdapter(subIssueAdapter);
                } else if (issue_type.equalsIgnoreCase("Catalog")){
                    subIssueList = new ArrayList<String>();

                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(1);
                        JSONArray subIssueJSON = jsonObject.getJSONArray("sub_issue_type");
                        for (int i = 0; i < subIssueJSON.length(); i++){
                            subIssueList.add(subIssueJSON.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> subIssueAdapter = new ArrayAdapter<String>(
                            ContactUs.this, android.R.layout.simple_spinner_item, subIssueList);
                    subIssueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIssue.setAdapter(subIssueAdapter);
                } else if (issue_type.equalsIgnoreCase("Return/Refund")){
                    subIssueList = new ArrayList<String>();

                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(2);
                        JSONArray subIssueJSON = jsonObject.getJSONArray("sub_issue_type");
                        for (int i = 0; i < subIssueJSON.length(); i++){
                            subIssueList.add(subIssueJSON.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> subIssueAdapter = new ArrayAdapter<String>(
                            ContactUs.this, android.R.layout.simple_spinner_item, subIssueList);
                    subIssueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIssue.setAdapter(subIssueAdapter);
                } else if (issue_type.equalsIgnoreCase("Signup")){
                    subIssueList = new ArrayList<String>();

                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(3);
                        JSONArray subIssueJSON = jsonObject.getJSONArray("sub_issue_type");
                        for (int i = 0; i < subIssueJSON.length(); i++){
                            subIssueList.add(subIssueJSON.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> subIssueAdapter = new ArrayAdapter<String>(
                            ContactUs.this, android.R.layout.simple_spinner_item, subIssueList);
                    subIssueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIssue.setAdapter(subIssueAdapter);
                } else if (issue_type.equalsIgnoreCase("Others")){
                    subIssueList = new ArrayList<String>();

                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(4);
                        JSONArray subIssueJSON = jsonObject.getJSONArray("sub_issue_type");
                        for (int i = 0; i < subIssueJSON.length(); i++){
                            subIssueList.add(subIssueJSON.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> subIssueAdapter = new ArrayAdapter<String>(
                            ContactUs.this, android.R.layout.simple_spinner_item, subIssueList);
                    subIssueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIssue.setAdapter(subIssueAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // subIssuesType
        subIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sub_issue = parent.getItemAtPosition(position).toString();

                if (sub_issue.equalsIgnoreCase("Order Tracking")){
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(0);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (sub_issue.equalsIgnoreCase("Payment Issue")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(0);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("New Catalog")){
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(1);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (sub_issue.equalsIgnoreCase("Catalog Update")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(1);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("New Category Addition")){
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(1);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (sub_issue.equalsIgnoreCase("New Brand Addition")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(1);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(3));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("File a Return")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(2);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("Refund not Processed")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(2);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("How to signup as a seller ?")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(3);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("Know more about the app")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(3);
                        JSONArray instructionJSON = jsonObject.getJSONArray("instruction");
                        instruction_lbl.setText(instructionJSON.getString(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (sub_issue.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonObject = rangesJSON.getJSONObject(4);
                        JSONArray instructionJSON = jsonObject.getJSONArray("");
                        instruction_lbl.setText(instructionJSON.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        submitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************************ISTIAQUE***************************************/
                Controller application = (Controller) getApplication();
                Tracker mTracker = application.getDefaultTracker();
                application.trackEvent("ContactUs", "onClick", "Submit Issue");
                /*******************************ISTIAQUE***************************************/

                if (issueDesc.getText().toString().isEmpty()) {
                    Toast.makeText(ContactUs.this, "Please add issue detail. And try again.", Toast.LENGTH_SHORT).show();
                } else {
                    if (cd.isConnectingToInternet()) {
                        sendIssues(new IssueDetails(issue_type.trim(), sub_issue.trim(), instruction_lbl.getText().toString().trim(), issueDesc.getText().toString().trim()));
                        issueDesc.setText("");
                    } else {
                        Toast.makeText(ContactUs.this, "Please check your internet connection. And try again.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void sendIssues(final IssueDetails issueDetails)
    {
        Gson gson = new Gson();
        Log.e("create--", gson.toJson(issueDetails));
        RestClient.GitApiInterface service = RestClient.getClient();
        Call<JsonElement> call = service.issueDetails(helper.getDefaults("user_id",getApplicationContext()),issueDetails, helper.getB64Auth(ContactUs.this));
        call.enqueue(new Callback<JsonElement>()
        {
            @Override
            public void onFailure(Throwable t)
            {
                if (ContactUs.this != null && !ContactUs.this.isFinishing())
                    new AlertDialogManager().showAlertDialog(ContactUs.this,
                            getString(R.string.error),
                            getString(R.string.server_error));
            }

            @Override
            public void onResponse(Response response) {
                Log.d("IssueResponse", "" + response.raw());
                progress.dismiss();
                if (response.isSuccess()) {
                    Toast.makeText(ContactUs.this, "Your message has been sent successfully", Toast.LENGTH_LONG).show();
                   // finish();
                } else {
                    Toast.makeText(ContactUs.this, "Your request has been failed please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /************getting reference of views*******************/
    private void getViews() {
        issueType = (Spinner) findViewById(R.id.issueType);
        subIssue = (Spinner) findViewById(R.id.subIssue);
        instruction_lbl = (TextView) findViewById(R.id.instruction_lbl);
        issueDesc = (EditText) findViewById(R.id.issueDesc);
        submitIssue = (Button) findViewById(R.id.submitIssue);
        helper = new Helper();
        progress = new ProgressDialog(this);
        progress.setMessage("Message sending...");
        progress.setIndeterminate(false);
        progress.setCancelable(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
