package com.example.eisen.sesam;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    SettingsModel settingsModel=new SettingsModel();

    //________________Buttons______________________
    Button buttonSaveSettings;
    //________________Seekbars_____________________
    SeekBar seekBarDuration;
    SeekBar seekBarHowMany;
    //________________TextViews____________________
    TextView textViewTime;
    TextView textViewDuration;
    //________________SAVEDATA_______________
    public static final String SHARED_PREFS = "sharedPrefs" ;
    public static final String SAVESETIINGS="savesettings";

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
        loadData();
        initButtons();
        initSeekBars();
        initTextViews();
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
        buttonSaveSettings = (Button) getView().findViewById(R.id.buttonSaveSettings2);
        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                saveData();
            }
        });
    }

    void initSeekBars(){
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

    private void saveData(){
        settingsModel.setDuration(seekBarDuration.getProgress());
        settingsModel.setTimes(seekBarHowMany.getProgress());
        Gson gson = new Gson();
        String jsonSave = gson.toJson(settingsModel);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVESETIINGS, jsonSave);
        editor.commit();
        sendDataToServer(jsonSave);
    }

    private void sendDataToServer(String data){
        ((MainActivity)getActivity()).pub(data);
    }



}
