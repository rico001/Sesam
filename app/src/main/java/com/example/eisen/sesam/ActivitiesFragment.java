package com.example.eisen.sesam;

/*TODO
refreshen nachdem NoConnectionBtn geklick wurde
->noConnectionBtn verschwindet
 */
import android.os.Bundle;
import android.os.Debug;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivitiesFragment extends Fragment implements IActivitiesFeed {

    private ActivityWrapper activityWrapper = new ActivityWrapper();
    private List<LinearLayout> activityLayouts = new ArrayList<>();

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
        activityLayouts.clear();
        activityLayouts.add((LinearLayout) getView().findViewById(R.id.activityplaceholder0));
        activityLayouts.add((LinearLayout) getView().findViewById(R.id.activityplaceholder1));
        activityLayouts.add((LinearLayout) getView().findViewById(R.id.activityplaceholder2));
    }

    @Override
    public void onMainActivityReceiveActivities(MqttMessage m) {
        Log.d(MainActivity.MQTTDEBUG_TAG,"hello from activityfragment"+m.toString());
        Gson gson = new Gson();
        activityWrapper = gson.fromJson(m.toString(), ActivityWrapper.class);
        Log.d(MainActivity.MQTTDEBUG_TAG, activityWrapper.getActivityFeedList().toString());
        refreshFragment();

    }

    @Override
    public void refreshFragment() {
        List<Activity> activityFeedList = activityWrapper.getActivityFeedList();

        initLayouts();

        if(activityFeedList==null || activityFeedList.size()<activityLayouts.size()){//sonst out of BoundsExc
            Log.d(MainActivity.MQTTDEBUG_TAG,"activityfeedlist<+"+activityLayouts.size()+" oder null");
            return;
        }

        int index=0;
        for(LinearLayout activityLayout: activityLayouts){
            activityLayout.removeAllViews();

            View textEntryView = getLayoutInflater().inflate(R.layout.activity_item, activityLayout);
            TextView item_attribut;

            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_time);
            item_attribut.setText(activityFeedList.get(index).getTime()+"Uhr");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_date);
            item_attribut.setText(activityFeedList.get(index).getDate());
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_ringNumber);
            item_attribut.setText(activityFeedList.get(index).getRingNumber() + "Mal");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_wasOpened);
            item_attribut.setText(activityFeedList.get(index).isWasOpened()? "ja":"nein");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_batteryStatus);
            item_attribut.setText(activityFeedList.get(index).getBatteryStatus()/2+"V");
            item_attribut= (TextView) textEntryView.findViewById(R.id.textView_activity_openForTitles);

            String titles= activityFeedList.get(index).getOpenForTitles().size()==0? "keine":"";
            titles+=activityFeedList.get(index).getOpenForTitles().toString()
                    .replaceAll("[\\[\\]]", "");

            item_attribut.setText(titles);
            index++;
        }
    }

}
