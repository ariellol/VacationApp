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

public class pickDatesBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    DatePicker datePicker;
    Button datePickedButton;
    TextView datePickedTv;
    SimpleDateFormat dateFormat;
    datePicked datePicked;
    String startDate;
    String endDate;

    public pickDatesBottomSheet(datePicked datePicked,String startDate, String endDate) {
        this.datePicked = datePicked;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public pickDatesBottomSheet(){
        this.dismiss();
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

        startDate = dateFormat.format(getNearestWeekStart().getTime());
        endDate = dateFormat.format(getNearestWeekEnd().getTime());
        String weekend = endDate + " - " + startDate;
        datePickedTv.setText(weekend);
        datePicker.setMinDate(getNearestWeekStart().getTimeInMillis());
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String fixedDate = String.format("%02d",dayOfMonth) + "/" + String.format("%02d",(monthOfYear + 1)) + "/" + year;
                    Date startDate = dateFormat.parse(fixedDate);
                    startDate = getWeekendStartInWeek(startDate).getTime();
                    Date endDate = dateFormat.parse(fixedDate);
                    endDate = getWeekendEndInWeek(endDate).getTime();
                    datePicked.onDatePicked(dateFormat.format(startDate),dateFormat.format(endDate));
                    String pickedDate = (dateFormat.format(endDate) + " - " + dateFormat.format(startDate));
                    datePickedTv.setText(pickedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        datePickedButton.setOnClickListener(this);

        return parent;
    }

    public static Calendar getNearestWeekStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        return c;
    }

    public static Calendar getNearestWeekEnd() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return c;
    }

    private Calendar getWeekendStartInWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        return c;
    }

    private Calendar getWeekendEndInWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return c;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == datePickedButton.getId()){
            this.dismiss();
        }
    }
}
