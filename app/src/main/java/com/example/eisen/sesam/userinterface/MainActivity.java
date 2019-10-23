package com.example.eisen.sesam.userinterface;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.eisen.sesam.data.ActivityWrapper;
import com.example.eisen.sesam.communication.MqttHelper;
import com.example.eisen.sesam.R;
import com.example.eisen.sesam.storage.StorageOrganizer;
import com.example.eisen.sesam.data.SettingsModel;
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
    public static final String SHARED_PREFS = "sharedPrefs" ;
    public static final String SAVESETIINGS="savesettings";
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
    private SettingsModel settingsModel;
    private ActivityWrapper activityWrapper = new ActivityWrapper();


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
        initSettingsModel();
        initActivityWrapper();
        initMqttHelper();
    }

    private void initMqttHelper(){
        String ip= loadIP();
        mqttHelper = new MqttHelper(getApplicationContext(),ip,this, this);
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

    public void initSettingsModel(){
        try{
            settingsModel= StorageOrganizer.loadObject(this,SHARED_PREFS,SAVESETIINGS,SettingsModel.class);
        }catch (InstantiationException | IllegalAccessException e){
            Log.d("loaddata",e.toString());
        }
    }

    public void initActivityWrapper(){
        activityWrapper = new ActivityWrapper();
    }

    public void pubTo(String message, String anyTopic, boolean retainFlag){
        try {
            mqttHelper.publishMessageTo(message,0,anyTopic, retainFlag);
        }catch(Exception e){
            Log.d("Mqtt", "Publish Fehler von MainActivity");
        }
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    public String loadIP(){
        return StorageOrganizer.loadString(this,SHARED_PREFS,SERVERIP);
    }

    public void saveIP(String ip){
        StorageOrganizer.saveString(this,SHARED_PREFS,SERVERIP,ip);
    }

    public void saveData(){
        StorageOrganizer.saveObject(this,SHARED_PREFS,SAVESETIINGS, settingsModel);
    }

    public void sendDataToServer(){
       // String data =SettingsModel.createDatesStringforMqtt(settingsModel);
        String data =SettingsModel.convertSettingsToJSON(settingsModel);
        pubTo(data, SETTINGSTOPIC, true);
    }

    public void sendDataToServer(String anyTopic, String data, boolean retainFlag){
        pubTo(data, anyTopic, retainFlag);
    }

    public MqttHelper getMqttHelper() {
        return mqttHelper;
    }

    public void initNewConnection(){
        mqttHelper = new MqttHelper(getApplicationContext(),loadIP(),this, this);
    }

    public ActivityWrapper getActivityWrapper() {
        return activityWrapper;
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
            settingsModel.update(new Gson().fromJson(message.toString(), SettingsModel.class));
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
