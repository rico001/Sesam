package com.example.eisen.sesam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    //________________Buttons______________________
    Button buttonSaveSettings;

    //________________Seekbars_____________________
    SeekBar seekBarDuration;
    SeekBar seekBarHowMany;

    //________________TextViews____________________
    TextView textViewTime;
    TextView textViewDuration;
    //_______________EditTexts_____________________
    private EditText editTextServerIP;

    public static final String SHARED_PREFS = "sharedPrefs" ;


    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButtons();
        initSeekBars();
        initTextViews();
        initEditTexts();
    }

    private void initEditTexts() {
        editTextServerIP = (EditText) getView().findViewById(R.id.editTextServerIP);
        editTextServerIP.setText(((MainActivity)getActivity()).loadIP());
        editTextServerIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextServerIP.getText().toString().equals("")){
                    buttonSaveSettings.setEnabled(false);
                }else{
                    buttonSaveSettings.setEnabled(true);
                }
            }
        });

    }

    void initButtons(){
        buttonSaveSettings = (Button) getView().findViewById(R.id.buttonSaveSettings2);
        buttonSaveSettings.setEnabled(false);
        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                updateModel();
                ((MainActivity)getActivity()).saveIP(editTextServerIP.getText().toString());
                ((MainActivity)getActivity()).saveData();
                ((MainActivity)getActivity()).sendDataToServer();
                ((MainActivity)getActivity()).initNewConnection();
            }
        });
    }

    void initSeekBars(){
        SettingsModel settingsModel=((MainActivity)getActivity()).getSettingsModel();

        //seekBar für Öffnungsdauer einrichten
        seekBarDuration = (SeekBar) getView().findViewById(R.id.seekBarDuration2);
        seekBarDuration.setProgress(settingsModel.getDuration());
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        seekBarHowMany = (SeekBar) getView().findViewById(R.id.seekBarHowMany2);
        seekBarHowMany.setProgress(settingsModel.getTimes());
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

    void initTextViews(){
        //TextViews einrichten
        textViewTime = (TextView) getView().findViewById(R.id.textViewHowMany2);
        textViewDuration = (TextView) getView().findViewById(R.id.textViewDuration2);

        textViewDuration.setText(seekBarDuration.getProgress()+" Sekunden");
        textViewTime.setText(seekBarHowMany.getProgress()+" Mal");
    }

    private void updateModel(){
        SettingsModel settingsModel=((MainActivity)getActivity()).getSettingsModel();
        settingsModel.setDuration(seekBarDuration.getProgress());
        settingsModel.setTimes(seekBarHowMany.getProgress());
    }







}
