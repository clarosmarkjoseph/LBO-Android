package com.system.mobile.lay_bare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by ITDevJr1 on 4/11/2017.
 */

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    EditText regBday;
    public DateDialog() {

    }

    public Dialog onCreateDialog(Bundle saveInstanceState){
        Calendar c = Calendar.getInstance();
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = year+"-"+day+"-"+month;
        regBday.setText(date);
    }
}
