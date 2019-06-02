package com.example.eisen.sesam;


import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SettingsModel {

    private int duration;

    private TimeWindowWrapper timeWindowWrapper;

    public SettingsModel() {
        this.duration = 0;
        this.timeWindowWrapper = new TimeWindowWrapper();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TimeWindowWrapper getTimeWindowWrapper() {
        return timeWindowWrapper;
    }

    public void setTimeWindowWrapper(TimeWindowWrapper timeWindowWrapper) {
        this.timeWindowWrapper = timeWindowWrapper;
    }

    public static String convertSettingsToJSON(SettingsModel s) {
        Gson gson = new Gson();
        String settingsDataJSON = gson.toJson(s);
        return settingsDataJSON;
    }

}
