package com.example.eisen.sesam;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
                textViewTime.setText(progress+" Mal");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //initial Text für TextViews enrichten
        textViewDuration.setText(seekBarTime.getProgress()+" Sekunden");
        textViewTime.setText(seekBarHowMany.getProgress()+" Mal");
    }

    void prepareTimeWindowlist(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

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
    }

}
