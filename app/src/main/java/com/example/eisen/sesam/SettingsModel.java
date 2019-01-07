package com.example.eisen.sesam;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SettingsModel {

    private int duration;
    private int times;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingsModel(){
        this.duration = 4;
        this.times=2;
        this.listDataHeader=new LinkedList<>();
        this.listDataChild=new HashMap<>();
    }

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

    public static String createDatesStringforMqtt(SettingsModel s){
        String date="";
        String time1="";
        String time2="";

        if(s.getListDataHeader().size()>0){
            for(int i=0; i<=s.getListDataHeader().size()-1;i++){
                date+=s.getListDataChild().get(s.getListDataHeader().get(i)).get(0);
                time1+=s.getListDataChild().get(s.getListDataHeader().get(i)).get(1);
                time2+=s.getListDataChild().get(s.getListDataHeader().get(i)).get(2);

                if(i!=s.getListDataHeader().size()-1){
                    date+="#";
                    time1+="#";
                    time2+="#";
                }
            }
        }

        String dataReady="";

        if(date.length()!=0){
            dataReady = date + "|" + time1 + "|" + time2;
        }


        return dataReady;
    }

    public static String createBehaviorStringforMqtt(SettingsModel s){
       String behavior =s.getTimes()+""+s.getDuration();
        return behavior;
    }

}
