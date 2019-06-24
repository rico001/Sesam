package com.example.eisen.sesam;

/*TODO
refreshen nachdem NoConnectionBtn geklick wurde
->noConnectionBtn verschwindet
 */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eisen.sesam.com.example.eisen.interfaces.IActivitiesFeed;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivitiesFragment extends Fragment implements IActivitiesFeed {

    private ActivityWrapper activityWrapper = new ActivityWrapper();
    private LinearLayout activityLayout = null;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshFragment();
    }

    private void initLayouts(){
        activityLayout = getView().findViewById(R.id.linearlayout_activityFeed_placeholder);
    }

    @Override
    public void onMainActivityReceiveActivities(MqttMessage m) {
        Log.d(MainActivity.MQTTDEBUG_TAG,"hello from activityfragment"+m.toString());
        Gson gson = new Gson();
        activityWrapper = gson.fromJson(m.toString(), ActivityWrapper.class);
        refreshFragment();
    }

    @Override
    public void refreshFragment() {

        if(activityWrapper==null)return;

        initLayouts();

        activityLayout.removeAllViews();

        List<Activity> activityFeedList = activityWrapper.getActivityFeedList();
        Collections.reverse(activityFeedList);

        for(Activity activity: activityFeedList){
            Log.d(MainActivity.MQTTDEBUG_TAG, activity.toString());

            View textEntryView = getLayoutInflater().inflate(R.layout.activity_item, null);
            TextView item_attribut;
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_title);
            item_attribut.setText(activity.getDate()+System.lineSeparator()+activity.getTime()+"Uhr");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_ringNumber);
            item_attribut.setText(activity.getRingNumber() + "Mal");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_wasOpened);
            item_attribut.setText(activity.isWasOpened()? "ja":"nein");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_batteryStatus);
            item_attribut.setText(activity.getBatteryStatus()/2+"V");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_openForTitles);

            String titles= activity.getOpenForTitles().size()==0? "keine":"";
            titles+=activity.getOpenForTitles().toString()
                    .replaceAll("[\\[\\]]", "");

            item_attribut.setText(titles);
            activityLayout.addView(textEntryView);
        }
    }

}
