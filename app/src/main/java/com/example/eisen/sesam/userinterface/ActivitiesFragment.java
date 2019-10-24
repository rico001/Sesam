package com.example.eisen.sesam.userinterface;

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
import com.example.eisen.sesam.data.mqtt.Activity;
import com.example.eisen.sesam.data.mqtt.ActivityWrapper;
import com.example.eisen.sesam.R;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivitiesFragment extends Fragment implements Observer {

    private LinearLayout activityLayout;
    private ActivityWrapper observableActivityWrapper;

    public ActivitiesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observableActivityWrapper =((MainActivity)getActivity()).getActivityWrapper();
        observableActivityWrapper.addObserver(this);
        refreshFragment(observableActivityWrapper);
    }

    private void initLayouts(){
        activityLayout = getView().findViewById(R.id.linearlayout_activityFeed_placeholder);
    }

    public void refreshFragment(ActivityWrapper activityWrapper) {

        initLayouts();

        activityLayout.removeAllViews();

        List<Activity> activityFeedList = activityWrapper.getActivityFeedList();

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

    @Override
    public void update(Observable o, Object arg) {
        if(arg==null)return;
        refreshFragment((ActivityWrapper)arg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        observableActivityWrapper.deleteObserver(this);
        Log.d("TEST2","Settingsfrag destroy");
    }
}
