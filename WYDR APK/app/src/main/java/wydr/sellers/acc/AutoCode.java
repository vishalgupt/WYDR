package wydr.sellers.acc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatActivity;
import wydr.sellers.registration.Helper;

/**
 * Created by surya on 30/11/15.
 */
public class AutoCode extends ArrayAdapter<ProductCode> implements Filterable {
    private static final String LOG_TAG = "ExampleApp";
    private static LayoutInflater inflater = null;
    //AutoCompleteTextView textView
    String company_1, company_2;
    private ArrayList<ProductCode> resultList;
    Helper helper = new Helper();
    Context context;

    public AutoCode(Context context, int textViewResourceId, String company1, String company2) {
        super(context, textViewResourceId);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.company_1 = company_1;
        this.company_2 = company_2;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.auto_code_layout, null);

        TextView name = (TextView) vi.findViewById(R.id.spinnerCode); // title

        if (resultList.size() > position) {

            ProductCode item = resultList.get(position);

            // Setting all values in listview
            name.setText(item.getCode());
            //  address.setText(item.getAddress());

        }
        return vi;
    }

    @Override
    public int getCount() {
        if (resultList != null) {
            return resultList.size();
        }
        return 0;
    }

    @Override
    public ProductCode getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());
                    if (resultList != null) {
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    // Assign the data to the FilterResults

                }
                // notifyDataSetChanged();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    // private   String COUNTRY = AppUtil.URL + "autosuggest?company_id[0]=" + company_1 + "&company_id[1]=" + company_2 + "&pname=N&q=";

    private ArrayList<ProductCode> autocomplete(String input)
    {
        ArrayList<ProductCode> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try
        {
            StringBuilder sb = new StringBuilder(AppUtil.URL + "autosuggest?company_id[0]=" + ChatActivity.company + "&pname=N&q=");
            Log.d("url", sb.toString());

            sb.append(URLEncoder.encode(input, "utf8"));
            //	Log.d("auto url ", COUNTRY + input);
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", helper.getB64Auth(context));
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            Log.d("heloo ", "" + jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            JSONArray predsJsonArray = jsonObj.getJSONArray("products");
            Log.d("Code", predsJsonArray.toString());

            // Extract the Place descriptions from the results
            resultList = new ArrayList<ProductCode>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject object = predsJsonArray.getJSONObject(i);
                ProductCode items = new ProductCode();
                items.setCode(object.getString("product_code"));
                items.setId(object.getString("product_id"));
                items.setName(object.getString("product"));
                items.setMoq(object.getInt("min_qty"));
                items.setAmount(object.getInt("amount"));

                resultList.add(items);
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }
}
//}
