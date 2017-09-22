package wydr.sellers.acc;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import wydr.sellers.R;


public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    Context context;
    String[] objects;
    String firstElement;
    boolean isFirstTime;

    public CustomSpinnerAdapter(Context context, int textViewResourceId,
                                String[] objects, String defaultText) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.isFirstTime = true;
        setDefaultText(defaultText);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (isFirstTime) {
            objects[0] = firstElement;
            isFirstTime = false;
        }
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
        text.setTextColor(context.getResources().getColor(R.color.text_color));
        text.setHintTextColor(context.getResources().getColor(R.color.hint_color));
        text.setText(objects[position]);
        return convertView;
        // return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        return getCustomView(position, convertView, parent);
    }

    public void setDefaultText(String defaultText) {
        this.firstElement = objects[0];
        objects[0] = defaultText;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spin_row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.spinnerTarget1);
        if (objects[position].equalsIgnoreCase("SELECT META CATEGORY") || objects[position].equalsIgnoreCase("SELECT SUBCATEGORY")) {
            label.setTextColor(context.getResources().getColor(R.color.hint_color));
        } else
            label.setTextColor(context.getResources().getColor(R.color.text_color));
        label.setText(objects[position]);

        return row;
    }

}
