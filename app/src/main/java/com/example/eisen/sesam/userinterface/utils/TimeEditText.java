package com.example.eisen.sesam.userinterface.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeEditText extends AppCompatEditText implements View.OnTouchListener {

    private TimePickerDialog timePickerDialog;
    boolean editTextIsTouched=false;

    private int defaultHour=-1;
    private int defaultMin=-1;

    public TimeEditText(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public TimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public TimeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(editTextIsTouched==false) {
            editTextIsTouched = true;


            int min;
            int hour;
            if (defaultMin < 0 && defaultHour < 0) {
                Calendar c;
                c = Calendar.getInstance();
                min = c.get(Calendar.MINUTE);
                hour = c.get(Calendar.HOUR);
            }else{
                min=defaultMin;
                hour=defaultHour;
            }

            timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String timeText= formatTime(hourOfDay,minute);
                    setText(timeText);
                }

            },hour,min,true);

            timePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    editTextIsTouched = false;
                }
            });

            timePickerDialog.show();
        }
        return false;
    }
    private String formatTime(int hour, int min) {
        String time = "";

        if (hour < 10){
            time+="0"+hour+":";
        }else{
            time+=hour+":";
        }

        if (min < 10){
            time+="0"+min;
        }else{
            time+=min;
        }

        return time;
    }

    public void setDefaulTime(int hour, int min){
        this.defaultHour=hour;
        this.defaultMin=min;
    }

}
