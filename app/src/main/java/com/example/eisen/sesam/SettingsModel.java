package com.example.eisen.sesam;

import java.util.HashMap;
import java.util.List;

public class SettingsModel {

    private int duration;
    private int times;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingsModel(int duration, int times, List<String> listDataHeader, HashMap<String,List<String>> listDataChild ){
        this.duration = duration;
        this.times=times;
        this.listDataHeader=listDataHeader;
        this.listDataChild=listDataChild;
    }

    //_______________________________________Duration_________________________________________
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    //_______________________________________Times_________________________________________
    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
    //_______________________________________ListDataHeader_________________________________________
    public List<String> getListDataHeader() {
        return listDataHeader;
    }

    public void setListDataHeader(List<String> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

    //_______________________________________ListDataChild_________________________________________
    public HashMap<String, List<String>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(HashMap<String, List<String>> listDataChild) {
        this.listDataChild = listDataChild;
    }

}
