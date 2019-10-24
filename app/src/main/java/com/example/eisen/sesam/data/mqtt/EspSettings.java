package com.example.eisen.sesam.data.mqtt;


import android.util.Log;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class EspSettings extends Observable {

    private int duration;

    private List<TimeWindow> timeWindows;

    public EspSettings() {
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

    public String convertSettingsToJSON() {
        return new Gson().toJson(this);
    }

    public void update(EspSettings s) {
        setDuration(s.getDuration());
        setTimeWindowWrapper(s.getTimeWindows());
        setChanged();
        notifyObservers();
        Log.d("TEST2","refresh observeranzahl"+countObservers());
    }


}
