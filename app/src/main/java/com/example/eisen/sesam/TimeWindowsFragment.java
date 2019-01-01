package com.example.eisen.sesam;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeWindowsFragment extends Fragment {

    SettingsModel settingsModel;

    ExpandableListView expListView;
    ExpandableListAdapter listAdapterGeneralSettings;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //________________SAVEDATA_______________
    public static final String SHARED_PREFS = "sharedPrefs" ;
    public static final String SAVESETIINGS="savesettings";

    //_______________Buttons_________________
    Button buttonSaveTimeWindow;
    Button buttonDeleteAllWindows;
    //_______________EditTexts_______________
    EditText editTextTitel;
    EditText editTextVon;
    EditText editTextBis;
    TextView  editTextDate;
    //__________TimePicker___________________
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    boolean editTextIsTouched=false;




    public TimeWindowsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_windows, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsModel=new SettingsModel();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        loadData();
        initTimeWindowlist();
        initEditTexts();
        initButtons();
    }

    void initTimeWindowlist(){
        expListView = (ExpandableListView) getView().findViewById(R.id.expListViewTimeWidows2);
        initFillWindowList();
        listAdapterGeneralSettings = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapterGeneralSettings);
    }

    private void initFillWindowList(){
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

    private void loadData(){

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(SAVESETIINGS)) {

            String jsonLoad= sharedPreferences.getString(SAVESETIINGS, "");
            Log.d("test", jsonLoad);
            Gson gson = new Gson();
            settingsModel = gson.fromJson(jsonLoad, SettingsModel.class);
        }else{
            Log.d("test", "SAVESETTINGS existiert noch nicht");
            Log.d("test", settingsModel.getListDataHeader().size()+"");
        }
    }

    void initButtons(){


        buttonSaveTimeWindow = (Button) getView().findViewById(R.id.buttonSaveTimeWindow2);
        buttonSaveTimeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeWindow();
                saveData();
            }
        });

        buttonDeleteAllWindows = (Button) getView().findViewById(R.id.buttonDeleteAllWindows2);
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

        buttonDeleteAllWindows = (Button) getView().findViewById(R.id.buttonDeleteAllWindows2);


    }

    private void saveData(){
        settingsModel.setListDataChild(listDataChild);
        settingsModel.setListDataHeader(listDataHeader);
        Gson gson = new Gson();
        String jsonSave = gson.toJson(settingsModel);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVESETIINGS, jsonSave);
        editor.commit();
    }

    void addTimeWindow(){
        if(validateInputFields()){
            listDataHeader.add(editTextTitel.getText().toString());
            List<String> data = new ArrayList<String>();
            data.add(editTextDate.getText().toString());
            data.add(editTextVon.getText().toString()+" bis "+editTextBis.getText().toString());

            listDataChild.put(listDataHeader.get(listDataHeader.size()-1), data);

            listAdapterGeneralSettings = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapterGeneralSettings);

            setEmptyEditTexts();
            buttonSaveTimeWindow.setEnabled(false);
            buttonDeleteAllWindows.setEnabled(true);
        }else{
            Toast.makeText(getContext(),"Füllen Sie alle Felder aus, um ein\n      Zeitfenster hinzuzufügen.",Toast.LENGTH_SHORT).show();
        }
    }

    void initEditTexts(){
        //init EditTexts
        editTextTitel = (EditText) getView().findViewById(R.id.editTextTitel2);
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

        editTextDate = (TextView) getView().findViewById(R.id.editTextDate2);
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

                    datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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

        editTextVon = (EditText) getView().findViewById(R.id.editTextVon2);
        editTextVon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editTextIsTouched==false) {
                    editTextIsTouched = true;

                    Calendar c;
                    c = Calendar.getInstance();
                    int min = c.get(Calendar.MINUTE);
                    int hour = c.get(Calendar.HOUR);

                    timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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

        editTextBis = (EditText) getView().findViewById(R.id.editTextBis2);
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

                    timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editTextBis.setText(hourOfDay+":"+minute);
                        }

                    },00,00,true);

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

    void deleleteWindowList(){
        listDataHeader.clear();
        listDataChild.clear();
        expListView.setAdapter(listAdapterGeneralSettings);
        buttonDeleteAllWindows.setEnabled(false);
    }

    private void setEmptyEditTexts(){
        editTextBis.setText("");
        editTextVon.setText("");
        editTextDate.setText("");
        editTextTitel.setText("");
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
}