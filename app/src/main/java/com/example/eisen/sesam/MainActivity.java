package com.example.eisen.sesam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ExpandableListView expListView;
    ExpandableListAdapter listAdapterGeneralSettings;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    Button buttonSaveSettings;
    Button buttonSaveTimeWindow;

    SeekBar seekBarTime;
    SeekBar seekBarHowMany;

    TextView textViewTime;
    TextView textViewDuration;

    TextView  editTextDate;
    EditText editTextTitel;
    EditText editTextVon;
    EditText editTextBis;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    //______________________________________________________________
    boolean editTextIsTouched=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareTimeWindowlist();
        initWidgetsFromActivityMainScreen();
    }


    //initialisert Widgets des ActivityMainScreen
    void initWidgetsFromActivityMainScreen(){
        setContentView(R.layout.activity_main);
        //erweiterbare Liste einrichten
        expListView = (ExpandableListView) findViewById(R.id.lvExpTimeWidows);
        listAdapterGeneralSettings = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapterGeneralSettings);

        //Buttons einrichten
        buttonSaveSettings = (Button) findViewById(R.id.buttonSaveSettings);
        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Einstellungen wurden gespeichert",Toast.LENGTH_SHORT).show();
            }
        });
        buttonSaveTimeWindow = (Button) findViewById(R.id.buttonSaveTimeWindow);
        buttonSaveTimeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addTimeWindow();
            }
        });
        //TextViews einrichten
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDuration = (TextView) findViewById(R.id.textViewDuration);

        //seekBar für Öffnungsdauer einrichten
        seekBarTime = (SeekBar) findViewById(R.id.seekBarTime);
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                buttonSaveSettings.setEnabled(true);
                textViewDuration.setText(progress+" Sekunden");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //seekBar für Male bis geöffnet wird einrichten
        seekBarHowMany = (SeekBar) findViewById(R.id.seekBarHowMany);
        seekBarHowMany.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                buttonSaveSettings.setEnabled(true);
                if(progress!=0){
                    textViewTime.setText(progress + " Mal");
                }else{
                    textViewTime.setText("deaktiviert");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //init EditTexts
        editTextTitel = (EditText) findViewById(R.id.editTextTitel);

        editTextDate = (TextView) findViewById(R.id.editTextDate);
        editTextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editTextIsTouched==false) {
                    editTextIsTouched = true;

                    Calendar c;
                    c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);

                    datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth) {
                            editTextDate.setText(dayOfMonth + "." + (mMonth + 1) + "." + mYear);
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
        });

        editTextVon = (EditText) findViewById(R.id.editTextVon);
        editTextVon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editTextIsTouched==false) {
                    editTextIsTouched = true;

                    Calendar c;
                    c = Calendar.getInstance();
                    int min = c.get(Calendar.MINUTE);
                    int hour = c.get(Calendar.HOUR);

                    timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editTextVon.setText(hourOfDay+":"+minute);
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
        });

        editTextBis = (EditText) findViewById(R.id.editTextBis);
        editTextBis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editTextIsTouched==false) {
                    editTextIsTouched = true;

                    /*
                    Calendar c;
                    c = Calendar.getInstance();
                    int min = c.get(Calendar.MINUTE);
                    int hour = c.get(Calendar.HOUR);
                    */

                    timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editTextBis.setText(hourOfDay+":"+minute);
                        }

                    },23,59,true);

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
        });

        //initial Text für TextViews enrichten
        textViewDuration.setText(seekBarTime.getProgress()+" Sekunden");
        textViewTime.setText(seekBarHowMany.getProgress()+" Mal");
    }

    void prepareTimeWindowlist(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        /*
        //Titel
        listDataHeader.add("Feiern");
        listDataHeader.add("Postbote DHL");
        listDataHeader.add("Postbote HERMES");

        //Daturm und Uhrzeit zu jewiligen Titel
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
        */
    }

    void addTimeWindow(){
        if(validateInputFields()){
            listDataHeader.add(editTextTitel.getText().toString());
            List<String> data = new ArrayList<String>();
            data.add(editTextDate.getText().toString());
            data.add(editTextVon.getText().toString()+" bis "+editTextBis.getText().toString());

            listDataChild.put(listDataHeader.get(listDataHeader.size()-1), data);

            listAdapterGeneralSettings = new ExpandableListAdapter(this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapterGeneralSettings);
        }else{
            Toast.makeText(getApplicationContext(),"Füllen Sie alle Felder aus, um ein\n      Zeitfenster hinzuzufügen.",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputFields(){
        boolean correct =true;

        if(editTextDate.getText().toString().equals("")){
            return false;
        }
        if(editTextTitel.getText().toString().equals("")){
            return false;
        }
        if(editTextVon.getText().toString().equals("")){
            return false;
        }
        if(editTextBis.getText().toString().equals("")){
            return false;
        }

        return correct;
    }

}
