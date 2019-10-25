package com.example.eisen.sesam.userinterface.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateEditText extends AppCompatTextView implements View.OnTouchListener {

    private DatePickerDialog datePickerDialog;
    boolean editTextIsTouched=false;

    public DateEditText(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(editTextIsTouched==false) {
            editTextIsTouched = true;

            Calendar c;
            c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth) {
                    String date= formatDate(dayOfMonth,mMonth+1,mYear);
                    //editTextDate2.setText(dayOfMonth + "." + (mMonth + 1) + "." + mYear);
                    setText(date);
                }
            }, year, month, day);

            datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {

                    editTextIsTouched = false;
                }
            });
            datePickerDialog.show();
        }
        return false;
    }

    private String formatDate(int day, int month, int year) {
        String date = "";

        if (day < 10){
            date+="0"+day+".";
        }else{
            date+=day+".";
        }

        if (month < 10){
            date+="0"+month+".";
        }else{
            date+=month+".";
        }

        date+=year;

        return date;
    }
}
