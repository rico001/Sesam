package com.example.eisen.sesam.userinterface;

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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.eisen.sesam.R;
import com.example.eisen.sesam.data.SettingsModel;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements Observer {

    //DebugTags
    public static final String UPDATEFRAGMENT_TAG="updateFragment";

    //________________Buttons______________________
    Button buttonSaveSettings;

    //________________Seekbars_____________________
    SeekBar seekBarDuration;

    //________________TextViews____________________
    TextView textViewDuration;
    //_______________EditTexts_____________________
    private EditText editTextServerIP;
    //______________Observable_____________________
    SettingsModel observableSettingsmodel;
    private boolean ipChanged= false;

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

        observableSettingsmodel =((MainActivity)getActivity()).getSettingsModel();
        observableSettingsmodel.addObserver(this);

        refreshFragment(observableSettingsmodel);
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
                    ipChanged=true;
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

                if(ipChanged==true) {
                    ((MainActivity) getActivity()).initNewConnection();
                    Log.d("IP","ip geändert und reconnect init");
                    ipChanged=false;
                }
            }
        });

    }

    void initSeekBars(){
        //seekBar für Öffnungsdauer einrichten
        seekBarDuration = (SeekBar) getView().findViewById(R.id.seekBarDuration2);
        seekBarDuration.setProgress(3);
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

    }

    void initTextViews(){
        //TextViews einrichten
        textViewDuration = (TextView) getView().findViewById(R.id.textViewDuration2);
        textViewDuration.setText(seekBarDuration.getProgress()+" Sekunden");
    }


    private void updateModel(){
        SettingsModel settingsModel=((MainActivity)getActivity()).getSettingsModel();
        settingsModel.setDuration(seekBarDuration.getProgress());
    }

    public void refreshFragment(SettingsModel settingsModel) {
        seekBarDuration.setProgress((settingsModel.getDuration()));
        seekBarDuration.invalidate();
        Log.d("TEST2",observableSettingsmodel.getDuration()+"settdur");
        Log.d("TEST2",seekBarDuration.getProgress()+"prog");
    }


    @Override
    public void update(Observable o, Object arg) {
        refreshFragment((SettingsModel)o);
        Log.d("TEST2","update Settngsfrag");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        observableSettingsmodel.deleteObserver(this);
        Log.d("TEST2","Settingsfrag destroy");
    }
}
