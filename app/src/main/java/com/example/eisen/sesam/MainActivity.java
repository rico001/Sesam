package com.example.eisen.sesam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SettingsModel settingsModel=new SettingsModel();

    ExpandableListView expListView;
    ExpandableListAdapter listAdapterGeneralSettings;
    List<String> listDataHeader = new ArrayList<String>();;
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();;

    Button buttonSaveSettings;
    Button buttonSaveTimeWindow;
    Button buttonDeleteAllWindows;
    Button testButton;

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

    //_____________________SAVE DATA________________________________________
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SAVESETIINGS="savesettings";
    //__________________________________________________________________

    boolean editTextIsTouched=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        initTimeWindowlist();
        initButtons();
        initSeekBars();
        initEditTexts();
        initTextViews();
    }

    void initButtons(){
        //TestButton
        testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });


        //Buttons einrichten
        buttonSaveSettings = (Button) findViewById(R.id.buttonSaveSettings);
        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                saveData();
                Toast.makeText(getApplicationContext(),"Einstellungen wurden gespeichert",Toast.LENGTH_SHORT).show();
            }
        });

        buttonSaveTimeWindow = (Button) findViewById(R.id.buttonSaveTimeWindow);
        buttonSaveTimeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeWindow();
                saveData();
            }
        });

        buttonDeleteAllWindows = (Button) findViewById(R.id.buttonDeleteAllWindows);
        if(settingsModel.getListDataHeader().size()>0) {
            buttonDeleteAllWindows.setEnabled(true);
        }
        buttonDeleteAllWindows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleleteWindowList();
                saveData();
            }
        });
    }

    void initTextViews(){
        //TextViews einrichten
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDuration = (TextView) findViewById(R.id.textViewDuration);

        textViewDuration.setText(seekBarTime.getProgress()+" Sekunden");
        textViewTime.setText(seekBarHowMany.getProgress()+" Mal");
    }

    void initSeekBars(){
        //seekBar für Öffnungsdauer einrichten
        seekBarTime = (SeekBar) findViewById(R.id.seekBarTime);
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                buttonSaveSettings.setEnabled(true);
                textViewDuration.setText(progress+" Sekunden");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
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
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });
    }

    void initEditTexts(){
        //init EditTexts
        editTextTitel = (EditText) findViewById(R.id.editTextTitel);
        editTextTitel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(editTextTitel.getText().toString().equals("")){
                    buttonSaveTimeWindow.setEnabled(false);
                }else{
                    buttonSaveTimeWindow.setEnabled(true);
                }
            }
        });

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
    }

    void initTimeWindowlist(){
        expListView = (ExpandableListView) findViewById(R.id.lvExpTimeWidows);
        initFillWindowList();
        listAdapterGeneralSettings = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapterGeneralSettings);
    }

    void deleleteWindowList(){
        listDataHeader.clear();
        listDataChild.clear();
        expListView.setAdapter(listAdapterGeneralSettings);
        buttonDeleteAllWindows.setEnabled(false);
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

            setEmptyEditTexts();
            buttonSaveTimeWindow.setEnabled(false);
            buttonDeleteAllWindows.setEnabled(true);
        }else{
            Toast.makeText(getApplicationContext(),"Füllen Sie alle Felder aus, um ein\n      Zeitfenster hinzuzufügen.",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validateInputFields(){
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

    private void setEmptyEditTexts(){
        editTextBis.setText("");
        editTextVon.setText("");
        editTextDate.setText("");
        editTextTitel.setText("");
    }

    private void saveData(){
        SettingsModel settingsModel = new SettingsModel(seekBarTime.getProgress(), seekBarHowMany.getProgress(),listDataHeader, listDataChild );
        Gson gson = new Gson();
        String jsonSave = gson.toJson(settingsModel);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVESETIINGS, jsonSave);
        editor.commit();
    }

    /**
     * aktualisiert settingsModel, sofern Speicherstand existiert
     */
    private void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        if(sharedPreferences.contains(SAVESETIINGS)) {

            String jsonLoad= sharedPreferences.getString(SAVESETIINGS, "");
            Log.d("test", jsonLoad);
            Gson gson = new Gson();
            settingsModel = gson.fromJson(jsonLoad, SettingsModel.class);

            if (settingsModel.getListDataHeader().size() > 0 && settingsModel.getListDataHeader() != null) {
                Log.d("test", settingsModel.getListDataHeader().get(0));
            } else {
                if (settingsModel.getListDataHeader() == null) {
                    Log.d("test", "dataheader is null");
                }
                if (settingsModel.getListDataHeader().size() == 0) {
                    Log.d("test", "dataheader nicht belegt mit infos");
                }
            }

        }else{
            Log.d("test", "SAVESETTINGS existiert noch nicht");
            Log.d("test", settingsModel.getListDataHeader().size()+"");
        }
    }

    /**
     * befüllt WIndowlist, wenn ggf settingsModel bereits Daten enthält (abh. von loadData())
     */
    private void initFillWindowList(){
        Log.d("anzahl", "dataheader nicht belegt mit infos");
        if(settingsModel.getListDataHeader().size()>0){
            for(int i=0; i<=settingsModel.getListDataHeader().size()-1;i++){
                listDataHeader.add(settingsModel.getListDataHeader().get(i));
                List<String> data = new ArrayList<String>();
                data.add(settingsModel.getListDataChild().get(settingsModel.getListDataHeader().get(i)).get(0));
                data.add(settingsModel.getListDataChild().get(settingsModel.getListDataHeader().get(i)).get(1));
                listDataChild.put(settingsModel.getListDataHeader().get(i), data);
            }
        }
    }

}
