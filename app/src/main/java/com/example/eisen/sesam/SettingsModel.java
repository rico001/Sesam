package com.example.eisen.sesam;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SettingsModel {

    private int duration;
    private int times;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingsModel(){
        this.duration = 5;
        this.times=3;
        this.listDataHeader=new LinkedList<>();
        this.listDataChild=new HashMap<>();
    }

    public SettingsModel(int duration, int times, List<String> listDataHeader, HashMap<String,List<String>> listDataChild ){
        this.duration = duration;
        this.times=times;
        this.listDataHeader=listDataHeader;
        this.listDataChild=listDataChild;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public List<String> getListDataHeader() {
        return listDataHeader;
    }

    public void setListDataHeader(List<String> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

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
                    date+="c";
                    time1+="c";
                    time2+="c";
                }
            }
        }

        String dataReady="";

        if(date.length()!=0){
            dataReady = date + "b" + time1 + "b" + time2 + "a"+s.getTimes()+"a"+s.getDuration();
        }else{
            dataReady=s.getTimes()+"a"+s.getDuration();
        }

        return dataReady;
    }


}
