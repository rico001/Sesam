package com.example.eisen.sesam.userinterface;

/*TODO
refreshen nachdem NoConnectionBtn geklick wurde
->noConnectionBtn verschwindet
 */
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eisen.sesam.R;
import com.example.eisen.sesam.data.SettingsModel;
import com.example.eisen.sesam.data.TimeWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeWindowsFragment extends Fragment implements Observer {

    //DebugTags
    public static final String UPDATEFRAGMENT_TAG="updateFragment";

    private ExpandableListView expListView;
    private ExpandableListAdapter listAdapter;
    //_______________Buttons_________________
    private Button buttonSaveTimeWindow;
    private Button buttonDeleteAllWindows;
    //_______________EditTexts_______________
    private EditText editTextTitel;
    private EditText editTextVon;
    private EditText editTextBis;
    private TextView editTextDate2;
    private TextView editTextDate1;
    private TextView textViewTime;
    //_______________Seekbars_______________
    SeekBar seekbarKlingeln;
    //__________TimePicker___________________
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    //__________Observable___________________
    SettingsModel observableSettinsmodel;

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
        initSeekBars();
        initEditTexts();
        initButtons();

        observableSettinsmodel =((MainActivity)getActivity()).getSettingsModel();
        observableSettinsmodel.addObserver(this);
        refreshFragment(observableSettinsmodel);
        
    }

    void initSeekBars(){
        //seekBar für Male bis geöffnet wird einrichten
        seekbarKlingeln = (SeekBar) getView().findViewById(R.id.seekBarKlingeln);
        seekbarKlingeln.setProgress(3);
        seekbarKlingeln.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress!=0){
                    textViewTime.setText((progress)+" Mal");
                }else{
                    textViewTime.setText("Zeitfenster deaktiviert");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });
    }

    private void refreshFragment(SettingsModel settingsModel){
        expListView = (ExpandableListView) getView().findViewById(R.id.expListViewTimeWidows2);
        listAdapter = new ExpandableListAdapter(getContext(), settingsModel.getTimeWindows());
        expListView.setAdapter(listAdapter);
    }

    void initButtons(){


        buttonSaveTimeWindow = (Button) getView().findViewById(R.id.buttonSaveTimeWindow2);
        buttonSaveTimeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeWindow();
                ((MainActivity)getActivity()).saveData();
                ((MainActivity)getActivity()).sendDataToServer();
            }
        });

        buttonDeleteAllWindows = (Button) getView().findViewById(R.id.buttonDeleteAllWindows2);
        if(((MainActivity)getActivity()).getSettingsModel().getTimeWindows().size()!=0) {
            buttonDeleteAllWindows.setEnabled(true);
        }


        buttonDeleteAllWindows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleleteWindowList();
                ((MainActivity)getActivity()).saveData();
                ((MainActivity)getActivity()).sendDataToServer();
            }
        });

        buttonDeleteAllWindows = (Button) getView().findViewById(R.id.buttonDeleteAllWindows2);
    }

    private void addTimeWindow(){
        if(validateInputFields()){

            List<String> data = new ArrayList<String>();
            TimeWindow timeWindow = new TimeWindow();
            timeWindow.setTitle(editTextTitel.getText().toString());
            timeWindow.setFromDate(editTextDate1.getText().toString());
            timeWindow.setTillDate(editTextDate2.getText().toString());
            timeWindow.setFromTime(editTextVon.getText().toString());
            timeWindow.setTillTime(editTextBis.getText().toString());
            timeWindow.setRingNumber(seekbarKlingeln.getProgress());

            listAdapter.addTimeWindow(timeWindow);

            setEmptyEditTexts();
            buttonSaveTimeWindow.setEnabled(false);
            buttonDeleteAllWindows.setEnabled(true);
        }else{
            Toast.makeText(getContext(),"Füllen Sie alle Felder aus, um ein\n      Zeitfenster hinzuzufügen.",Toast.LENGTH_SHORT).show();
        }
    }

    private void initEditTexts(){
        //init TextViewKlingeln
        textViewTime = (TextView) getView().findViewById(R.id.textViewKlingeln);
        textViewTime.setText(seekbarKlingeln.getProgress()+" Mal");

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

        editTextDate2 = (TextView) getView().findViewById(R.id.editTextDate2);
        editTextDate2.setOnTouchListener(new View.OnTouchListener() {
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
                            editTextDate2.setText(date);
                            closeKeyboard();
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

        editTextDate1 = (TextView) getView().findViewById(R.id.editTextDate1);
        editTextDate1.setOnTouchListener(new View.OnTouchListener() {
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
                            editTextDate1.setText(date);
                            closeKeyboard();
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
                            String timeText= formatTime(hourOfDay,minute);
                            editTextVon.setText(timeText);
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
                            String timeText= formatTime(hourOfDay,minute);
                            editTextBis.setText(timeText);
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

    private void deleleteWindowList(){
        listAdapter.clearTimeWindows();
        buttonDeleteAllWindows.setEnabled(false);
    }

    private void setEmptyEditTexts(){
        editTextBis.setText("");
        editTextVon.setText("");
        editTextDate1.setText("");
        editTextDate2.setText("");
        editTextTitel.setText("");
    }

    private boolean validateInputFields(){
        boolean correct =true;

        if(editTextDate1.getText().toString().equals("")){
            return false;
        }

        if(editTextDate2.getText().toString().equals("")){
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

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view!=null){
            InputMethodManager imm= (InputMethodManager) getActivity().getSystemService((Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
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

    @Override
    public void update(Observable o, Object arg) {
        refreshFragment((SettingsModel)o);
        Log.d("TEST2","updat Timewindows -Frag");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        observableSettinsmodel.deleteObserver(this);
        Log.d("TEST2","timewindows -frag destroy");
    }
}
