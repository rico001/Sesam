package com.example.eisen.sesam;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.Handler;
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

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    //DebugTags
    public static final String MQTTDEBUG_TAG="mqttdebug";

    //_____________________SAVE DATA LOCAL_________________________________________
    public static final String SHARED_PREFS = "sharedPrefs" ;
    public static final String SAVESETIINGS="savesettings";
    public static final String SERVERIP="IP";

    public static final String IP="192.168.178.80";

    //________________Menu_________________________________________________________
    private BottomNavigationView mMainNav;
    //private Button buttonNoConnection;

    //_____________Fragments_______________________________________________________
    private OpenDoorFragment openDoorFragment = new OpenDoorFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private TimeWindowsFragment timeWindowsFragment = new TimeWindowsFragment();

    //____________mqtt_____________________________________________________________
    private MqttHelper mqttHelper;
    final String SETTINGSTOPIC = "Sesam/Settings/date";

    //__________________Model______________________________________________________
    private SettingsModel settingsModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsModel= new SettingsModel();
        String ip= loadIP();
        mqttHelper = new MqttHelper(getApplicationContext(),ip,this);


        //Menü einrichten
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        setFragment(openDoorFragment);
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){

                    case R.id.nav_open:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(openDoorFragment);
                        return true;

                    case R.id.nav_timewidows:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(timeWindowsFragment);
                        return true;

                    case R.id.nav_settings:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(settingsFragment);
                        return true;

                        default:
                            return false;
                }
            }
        });


        loadData();
//        initButtons();
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
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

    public void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(SAVESETIINGS)) {

            String jsonLoad= sharedPreferences.getString(SAVESETIINGS, "");
            Log.d("JSON", jsonLoad);
            Gson gson = new Gson();
            settingsModel = gson.fromJson(jsonLoad, SettingsModel.class);

        }else{
            Log.d("JSON", "SAVESETTINGS existiert noch nicht");
        }
    }

    public void getTimeWindowsFRomServer(SettingsModel settingsModel){

    }

    public String loadIP(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(SERVERIP)) {
            String ip= sharedPreferences.getString(SERVERIP, "");
            Log.d("Mqtt", ip);
            return ip;
        }else{
            Log.d("IP", "SERVERIP existiert noch nicht");
            return MainActivity.IP;
        }
    }

    public void saveIP(String ip){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVERIP, ip);
        editor.commit();
    }

    public void saveData(){
        Gson gson = new Gson();
        String jsonSave = gson.toJson(settingsModel);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVESETIINGS, jsonSave);
        editor.commit();
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
        mqttHelper = new MqttHelper(getApplicationContext(),loadIP(),this);
    }

    public boolean ConectionToServer(){
       return mqttHelper.isConnected();
    }


    @Override
    public void connectionLost(Throwable cause) {
        Log.d(MQTTDEBUG_TAG,"connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(MQTTDEBUG_TAG,"messageArrived"+message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTTDEBUG_TAG,"deliveryComplete");
    }
}
