package com.example.eisen.sesam;



import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
public class MainActivity extends AppCompatActivity {

    //_____________________SAVE DATA_____________________________________
    public static final String SHARED_PREFS = "sharedPrefs" ;
    public static final String SAVESETIINGS="savesettings";
    //__________________________________________________________________


    //-----Menu
    private BottomNavigationView mMainNav;

    //_____________Fragments_________________________________________________
    private OpenDoorFragment openDoorFragment = new OpenDoorFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private TimeWindowsFragment timeWindowsFragment = new TimeWindowsFragment();

    //_______________________________________________________________________

    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttHelper = new MqttHelper(getApplicationContext());

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


       // loadData();
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public void pubTo(String message, String anyTopic){
        try {
            mqttHelper.publishMessageTo(message,0,anyTopic);
        }catch(Exception e){
            Log.d("Mqtt", "Publish Fehler von MainActivity");
        }
    }

}
