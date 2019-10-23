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
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements MqttCallback, IMqttActionListener{

    //DebugTags
    public static final String MQTTDEBUG_TAG="mqttdebug";

    //_____________________SAVE DATA LOCAL_________________________________________
    public static final String SHARED_PREFS = "SHARED_PREFS" ;
    public static final String SAVE_ESP_SETIINGS ="SAVE_ESP_SETIINGS";
    public static final String SAVE_APP_SETIINGS ="SAVE_APP_SETIINGS";
    public static final String SERVERIP="IP";

    public static final String IP="192.168.2.108";

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
    final String SETTINGSTOPIC = "Sesam/Settings/date";
    final String TOPIC_ACTIVITYFEED = "Sesam/activityfeed";

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
        initFragmentTransaction(openDoorFragment);
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

        initButton();
        initData();
        initMqttHelper();
    }

    private void initMqttHelper(){
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
                initNewConnection();
            }
        });
    }

    public void initData(){
        try{
            activityWrapper = new ActivityWrapper();
            appSettings = StorageOrganizer.loadObject(this,SHARED_PREFS, SAVE_APP_SETIINGS, AppSettings.class);
            espSettings = StorageOrganizer.loadObject(this,SHARED_PREFS, SAVE_ESP_SETIINGS, EspSettings.class);
        }catch (InstantiationException | IllegalAccessException e){
            Log.d("loaddata",e.toString());
        }
    }

    public void pubTo(String message, String anyTopic, boolean retainFlag){
        try {
            mqttHelper.publishMessageTo(message,0,anyTopic, retainFlag);
        }catch(Exception e){
            Log.d("Mqtt", "Publish Fehler von MainActivity");
        }
    }

    public EspSettings getEspSettings() {
        return espSettings;
    }

    public void saveData(){
        StorageOrganizer.saveObject(this,SHARED_PREFS, SAVE_ESP_SETIINGS, espSettings);
        StorageOrganizer.saveObject(this,SHARED_PREFS, SAVE_APP_SETIINGS, appSettings);
    }

    public void sendDataToServer(){
       // String data =EspSettings.createDatesStringforMqtt(espSettings);
        String data = EspSettings.convertSettingsToJSON(espSettings);
        pubTo(data, SETTINGSTOPIC, true);
    }

    public void sendDataToServer(String anyTopic, String data, boolean retainFlag){
        pubTo(data, anyTopic, retainFlag);
    }

    public MqttHelper getMqttHelper() {
        return mqttHelper;
    }

    public void initNewConnection(){
        mqttHelper = new MqttHelper(getApplicationContext(),appSettings.getBrokerIP(),this, this);
    }

    public ActivityWrapper getActivityWrapper() {
        return activityWrapper;
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    @Override
    public void connectionLost(Throwable cause) {
        button_connectionfail.setVisibility(View.VISIBLE);
        Log.d(MQTTDEBUG_TAG,"connectionLost");
        Toast.makeText(this,"Serververbindung verloren",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message){
        if(topic.equals(SETTINGSTOPIC)){
            Log.d("TEST",message.toString());
            espSettings.update(new Gson().fromJson(message.toString(), EspSettings.class));
        }

        if(topic.equals(TOPIC_ACTIVITYFEED)){
            activityWrapper.update(new Gson().fromJson(message.toString(), ActivityWrapper.class));
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
