package wydr.sellers.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import wydr.sellers.R;

/**
 * Created by Deepesh_pc on 04-09-2015.
 */


public class CustomSpinAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> objects;
    String firstElement;
    boolean isFirstTime;

    public CustomSpinAdapter(Context context, int textViewResourceId,
                             ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.isFirstTime = true;
        //setDefaultText(defaultText);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, null);
        }
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        text.setPadding(5, 15, 5, 15);
        text.setGravity(Gravity.CENTER);
     //   Log.e(position + " " + objects.get(position), "dklmsdnosdfbkjsdfnbks");
        text.setTextColor(context.getResources().getColor(R.color.text_color));

        text.setHintTextColor(context.getResources().getColor(R.color.hint_color));
        text.setText(objects.get(position));
        return convertView;
        // return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        return getCustomView(position, convertView, parent);
    }

    public void setDefaultText(String defaultText) {
        this.firstElement = objects.get(0);
        objects.set(0, defaultText);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spin_row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.spinnerTarget1);
        label.setText(objects.get(position));
        Log.e("eeee", objects.get(position));
        if (objects.get(position).equalsIgnoreCase("SELECT META CATEGORY") || objects.get(position).equalsIgnoreCase("SELECT CATEGORY") || objects.get(position).equalsIgnoreCase("SELECT LEAF CATEGORY")) {
            label.setTextColor(context.getResources().getColor(R.color.divider));
            label.setTextSize(15);
        } else
            label.setTextColor(context.getResources().getColor(R.color.text_color));
        return row;
    }

}
