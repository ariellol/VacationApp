package com.ariellip.vacationApp;

import android.icu.util.Calendar;
import android.icu.util.HebrewCalendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class pickDatesBottomSheet extends BottomSheetDialogFragment {

    DatePicker datePicker;
    Button datePickedButton;
    TextView datePickedTv;
    SimpleDateFormat dateFormat;
    String dateStr;
    Calendar weekStart;

    public pickDatesBottomSheet() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.pick_date_bottom_sheet, container, false);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        datePicker = parent.findViewById(R.id.datePicker);
        datePickedButton = parent.findViewById(R.id.date_picked_button);
        datePickedTv = parent.findViewById(R.id.date_picked_tv);


        String startWeekend = dateFormat.format(getNearestWeekStart().getTime());
        String endWeekend = dateFormat.format(getNearestWeekEnd().getTime());
        String weekend = startWeekend + " - " + endWeekend;
        datePickedTv.setText(weekend);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {

                    Date date = dateFormat.parse(dayOfMonth + "/" + monthOfYear + "/" + year);
                    date = getWeekStartInMonth(date).getTime();
                    String pickedDate = date.toString();
                    datePickedTv.setText(pickedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return parent;
    }

    private Calendar getNearestWeekStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        return c;
    }

    private Calendar getNearestWeekEnd() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return c;
    }

    private Calendar getWeekStartInMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        return c;
    }

}
