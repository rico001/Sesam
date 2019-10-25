package com.example.eisen.sesam.userinterface;

/*TODO
refreshen nachdem NoConnectionBtn geklick wurde
->noConnectionBtn verschwindet
 */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eisen.sesam.R;
import com.example.eisen.sesam.data.mqtt.EspSettings;
import com.example.eisen.sesam.data.mqtt.TimeWindow;
import com.example.eisen.sesam.userinterface.utils.DateEditText;
import com.example.eisen.sesam.userinterface.utils.ExpandableListAdapter;
import com.example.eisen.sesam.userinterface.utils.TimeEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeWindowsFragment extends Fragment implements Observer {

    private ExpandableListView expListView;
    private ExpandableListAdapter listAdapter;
    //_______________Buttons_________________
    private Button buttonSaveTimeWindow;
    //_______________EditTexts_______________
    private EditText editTextTitel;
    private TimeEditText editTextTime1;
    private TimeEditText editTextTIme2;
    private DateEditText editTextDate1;
    private DateEditText editTextDate2;
    private View.OnClickListener onClick_editTextTitel_clearFocus = v -> editTextTitel.clearFocus();
    //_______________Textviews______________
    private TextView textViewTime;
    //_______________Seekbars_______________
    SeekBar seekbarKlingeln;
    //__________Observable___________________
    EspSettings observableEspSettings;

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

        observableEspSettings =((MainActivity)getActivity()).getEspSettings();
        observableEspSettings.addObserver(this);

        initSeekBars();
        initEditTexts();
        initButtons();
        refreshFragment(observableEspSettings);
        
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
                editTextTitel.clearFocus();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });
    }

    private void refreshFragment(EspSettings espSettings){
        expListView = (ExpandableListView) getView().findViewById(R.id.expListViewTimeWidows2);
        listAdapter = new ExpandableListAdapter(getContext(), espSettings, ((MainActivity)getActivity()));
        expListView.setAdapter(listAdapter);
    }

    void initButtons(){
        buttonSaveTimeWindow = (Button) getView().findViewById(R.id.buttonSaveTimeWindow2);
        buttonSaveTimeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeWindow();
            }
        });
    }

    private void addTimeWindow(){
        if(validateInputFields()){

            List<String> data = new ArrayList<String>();
            TimeWindow timeWindow = new TimeWindow();
            timeWindow.setTitle(editTextTitel.getText().toString());
            timeWindow.setFromDate(editTextDate1.getText().toString());
            timeWindow.setTillDate(editTextDate2.getText().toString());
            timeWindow.setFromTime(editTextTime1.getText().toString());
            timeWindow.setTillTime(editTextTIme2.getText().toString());
            timeWindow.setRingNumber(seekbarKlingeln.getProgress());

            listAdapter.addTimeWindow(timeWindow);
            setEmptyEditTexts();
            buttonSaveTimeWindow.setEnabled(false);
            editTextTitel.clearFocus();
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

        editTextDate1 = (DateEditText) getView().findViewById(R.id.editTextDate1);
        editTextDate1.setOnClickListener(onClick_editTextTitel_clearFocus);

        editTextDate2 = (DateEditText) getView().findViewById(R.id.editTextDate2);
        editTextDate2.setOnClickListener(onClick_editTextTitel_clearFocus);

        editTextTime1 = (TimeEditText) getView().findViewById(R.id.editTextTime1);
        editTextTime1.setOnClickListener(onClick_editTextTitel_clearFocus);

        editTextTIme2 = (TimeEditText) getView().findViewById(R.id.editTextTime2);
        editTextTIme2.setOnClickListener(onClick_editTextTitel_clearFocus);
        editTextTIme2.setDefaulTime(23,59);

    }

    private void setEmptyEditTexts(){
        editTextTime1.setText("");
        editTextTIme2.setText("");
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
        if(editTextTime1.getText().toString().equals("")){
            return false;
        }
        if(editTextTIme2.getText().toString().equals("")){
            return false;
        }

        return correct;
    }

    @Override
    public void update(Observable o, Object arg) {
        refreshFragment((EspSettings)o);
        Log.d("TEST2","updat Timewindows -Frag");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        observableEspSettings.deleteObserver(this);
        Log.d("TEST2","timewindows -frag destroy");
    }
}
