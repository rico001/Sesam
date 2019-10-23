package com.example.eisen.sesam.data;


import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;
import android.util.Log;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SettingsModel extends Observable {

    private int duration;

    private List<TimeWindow> timeWindows;

    public SettingsModel() {
        this.duration = 0;
        this.timeWindows = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<TimeWindow> getTimeWindows() {
        return timeWindows;
    }

    public void setTimeWindowWrapper(List<TimeWindow> timeWindows) {
        this.timeWindows = timeWindows;
    }

    public static String convertSettingsToJSON(SettingsModel s) {
        Gson gson = new Gson();
        String settingsDataJSON = gson.toJson(s);
        return settingsDataJSON;
    }

    public void update(SettingsModel s) {
        setDuration(s.getDuration());
        setTimeWindowWrapper(s.getTimeWindows());
        setChanged();
        notifyObservers();
        Log.d("TEST2","refresh observeranzahl"+countObservers());
    }
}
