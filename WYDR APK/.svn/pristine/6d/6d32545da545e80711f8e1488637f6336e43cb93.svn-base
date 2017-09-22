package wydr.sellers.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wydr.sellers.R;

/**
 * Created by surya on 2/10/15.
 */
public class DateRange extends Fragment implements View.OnClickListener {
    static TextView from;
    static TextView to;
    String date_from, date_to;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date_from = getActivity().getIntent().getStringExtra("date_from");
        date_to = getActivity().getIntent().getStringExtra("date_to");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_range, container, false);
        iniStuff(view);
        return view;
    }

    private void iniStuff(View view) {
        from = (TextView) view.findViewById(R.id.buttonfrom);
        to = (TextView) view.findViewById(R.id.buttonTO);
        if (date_from != null) {
            from.setText(date_from);
        }
        if (date_to != null) {
            to.setText(date_to);
        }
        from.setOnClickListener(this);
        to.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonfrom:
                DialogFragment fromFrag = new DatePickerFragmentFrom();
                //    newFragment.sh
                fromFrag.show(getFragmentManager(), "datePicker");
                fromFrag.setCancelable(false);
                break;
            case R.id.buttonTO:
                DialogFragment toFrag = new DatePickerFragmentTo();
                //    newFragment.sh
                toFrag.show(getFragmentManager(), "datePicker");
                toFrag.setCancelable(false);
                break;
        }
    }

    public static class DatePickerFragmentFrom extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        String str;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            str = sdf.format(new Date(year - 1900, month, day));
            // date.setText("" + str);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            str = sdf.format(new Date(year - 1900, month, day));
            // Date date =
            from.setText("" + str + "  ");


        }
    }

      public static class DatePickerFragmentTo extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        String str;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            str = sdf.format(new Date(year - 1900, month, day));
            // date.setText("" + str);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            str = sdf.format(new Date(year - 1900, month, day));
            // Date date =
            to.setText("" + str + "  ");

        }
    }
}
