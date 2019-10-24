package com.example.eisen.sesam.userinterface;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.eisen.sesam.data.app.AppSettings;
import com.example.eisen.sesam.data.mqtt.ActivityWrapper;
import com.example.eisen.sesam.communication.MqttHelper;
import com.example.eisen.sesam.R;
import com.example.eisen.sesam.data.mqtt.EspSettings;
import com.example.eisen.sesam.storage.StorageOrganizer;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements MqttCallback, IMqttActionListener{

    //DebugTags
    public static final String MQTTDEBUG_TAG="mqttdebug";

    //________________Menu_________________________________________________________
    private BottomNavigationView mMainNav;

    //________________Button_______________________________________________________
    private Button button_connectionfail;

    //_____________Fragments_______________________________________________________
    private OpenDoorFragment openDoorFragment = new OpenDoorFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private TimeWindowsFragment timeWindowsFragment = new TimeWindowsFragment();
    private ActivitiesFragment activitiesFragment = new ActivitiesFragment();

    //____________mqtt_____________________________________________________________
    private MqttHelper mqttHelper;

    //__________________Data______________________________________________________
    private EspSettings espSettings;
    private ActivityWrapper activityWrapper = new ActivityWrapper();
    private AppSettings appSettings = new AppSettings();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Menü einrichten
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){

                    case R.id.nav_open:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        initFragmentTransaction(openDoorFragment);
                        return true;

                    case R.id.nav_timewidows:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        initFragmentTransaction(timeWindowsFragment);
                        return true;

                    case R.id.nav_settings:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        initFragmentTransaction(settingsFragment);
                        return true;

                    case R.id.nav_activitiesfeed:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        initFragmentTransaction(activitiesFragment);
                        return true;

                        default:
                            return false;
                }
            }
        });

        initFragmentTransaction(openDoorFragment);
        initButton();
        loadData();
        initMqttConnection();
    }

    public void initMqttConnection(){
        mqttHelper = new MqttHelper(getApplicationContext(),appSettings.getBrokerIP(),this, this);
    }


    private void initFragmentTransaction(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public void initButton(){
        button_connectionfail = (Button) findViewById(R.id.button_connectionfail);
        button_connectionfail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMqttConnection();
            }
        });
    }

    public void loadData(){
        try{
            activityWrapper = StorageOrganizer.loadObject(this,StorageOrganizer.SHARED_PREFS, StorageOrganizer.SLOT_ESP_ACTIVITIES, ActivityWrapper.class);
            appSettings = StorageOrganizer.loadObject(this,StorageOrganizer.SHARED_PREFS, StorageOrganizer.SLOT_APP_SETIINGS, AppSettings.class);
            espSettings = StorageOrganizer.loadObject(this,StorageOrganizer.SHARED_PREFS, StorageOrganizer.SLOT_ESP_SETIINGS, EspSettings.class);
        }catch (InstantiationException | IllegalAccessException e){
            Log.d("loaddata",e.toString());
        }
    }

    public void saveData(boolean saveAppSettings, boolean saveActivityWrapper, boolean saveEspSettings) {
        if (saveActivityWrapper)
            StorageOrganizer.saveObject(this, StorageOrganizer.SHARED_PREFS, StorageOrganizer.SLOT_ESP_ACTIVITIES, activityWrapper);
        if (saveEspSettings)
            StorageOrganizer.saveObject(this, StorageOrganizer.SHARED_PREFS, StorageOrganizer.SLOT_ESP_SETIINGS, espSettings);
        if (saveAppSettings)
            StorageOrganizer.saveObject(this, StorageOrganizer.SHARED_PREFS, StorageOrganizer.SLOT_APP_SETIINGS, appSettings);
    }

    public void sendDataToServer(String anyTopic, String data, boolean retainFlag){
        try {
            mqttHelper.publishMessageTo(data,0,anyTopic, retainFlag);
        }catch(MqttException | UnsupportedEncodingException e){
            Log.d("Mqtt", "Publish Fehler von MainActivity");
        }
    }

    public ActivityWrapper getActivityWrapper() {
        return activityWrapper;
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public EspSettings getEspSettings() {
        return espSettings;
    }

    @Override
    public void connectionLost(Throwable cause) {
        button_connectionfail.setVisibility(View.VISIBLE);
        Log.d(MQTTDEBUG_TAG,"connectionLost");
        Toast.makeText(this,"Serververbindung verloren",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message){
        if(topic.equals(MqttHelper.TOPIC_ESP_SETTINGS)){
            Log.d("TEST",message.toString());
            espSettings.update(new Gson().fromJson(message.toString(), EspSettings.class));
        }

        if(topic.equals(MqttHelper.TOPIC_ACTIVITYFEED)){
            activityWrapper.update(new Gson().fromJson(message.toString(), ActivityWrapper.class));
            saveData(false,true,false);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTTDEBUG_TAG,"deliveryComplete");
        Toast.makeText(this,"Übermittlung zum Server erfolgreich",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.d("Mqtt", "Connect erfolgreich");
        mqttHelper.subscribeToTopics();
        button_connectionfail.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        button_connectionfail.setVisibility(View.VISIBLE);
    }
}
