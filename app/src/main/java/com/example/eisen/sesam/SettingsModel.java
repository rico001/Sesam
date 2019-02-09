package com.example.eisen.sesam;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SettingsModel {

    private int duration;
    private int times;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingsModel() {
        this.duration = 5;
        this.times = 3;
        this.listDataHeader = new LinkedList<>();
        this.listDataChild = new HashMap<>();
    }

    public SettingsModel(int duration, int times, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.duration = duration;
        this.times = times;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
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

    public static String createDatesStringforMqtt(SettingsModel s) {
        String date = "";
        String time1 = "";
        String time2 = "";

        if (s.getListDataHeader().size() > 0) {
            for (int i = 0; i <= s.getListDataHeader().size() - 1; i++) {
                date += s.getListDataChild().get(s.getListDataHeader().get(i)).get(0);
                time1 += s.getListDataChild().get(s.getListDataHeader().get(i)).get(1);
                time2 += s.getListDataChild().get(s.getListDataHeader().get(i)).get(2);

                if (i != s.getListDataHeader().size() - 1) {
                    date += "c";
                    time1 += "c";
                    time2 += "c";
                }
            }
        }

        String dataReady = "";

        if (date.length() != 0) {
            dataReady = date + "b" + time1 + "b" + time2 + "a" + s.getTimes() + "a" + s.getDuration();
        } else {
            dataReady = s.getTimes() + "a" + s.getDuration();
        }

        return dataReady;
    }

    public static String settingsInJSON(SettingsModel s) {

        if (s.getListDataHeader().size() > 0) {

            ArrayList<String> dateFrom = new ArrayList<>();
            ArrayList<String> dateUntil = new ArrayList<>();
            ArrayList<String> timeFrom = new ArrayList<>();
            ArrayList<String> timeUntil = new ArrayList<>();

            if (s.getListDataHeader().size() > 0) {
                for (int i = 0; i <= s.getListDataHeader().size() - 1; i++) {
                    dateFrom.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(0));
                    dateUntil.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(1));    //Datum
                    timeFrom.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(2));   //Zeit ab
                    timeUntil.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(3));      //Zeit bis
                }
            }


            Settings se = new Settings(dateFrom, dateUntil, timeFrom, timeUntil);

            Gson gson = new Gson();
            String settingsDataJSON = gson.toJson(se);
            return settingsDataJSON;
        }


        return "";
    }

}
    //es fehlen noch duration und anzahlKlingeln
    class Settings {

        private List<String> dateFrom;
        private List<String> dateUntil;
        private List<String> timeFrom;
        private List<String> timeUntil;

        public Settings(){

        }

        public Settings(ArrayList<String> dateFrom,ArrayList<String> dateUntil, ArrayList<String> timeFrom, ArrayList<String> timeUntil) {
            this.dateFrom = dateFrom;
            this.dateUntil = dateUntil;
            this.timeFrom = timeFrom;
            this.timeUntil = timeUntil;
        }
        public List<String> getDateFrom() {
            return dateFrom;
        }

        public void setDateFrom(List<String> dateFrom) {
            this.dateFrom = dateFrom;
        }

        public List<String> getDateUntil() {
            return dateUntil;
        }

        public void setDateUntil(List<String> dates) {
            this.dateUntil = dates;
        }

        public List<String> getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(List<String> timeFrome) {
            this.timeFrom = timeFrome;
        }

        public List<String> getTimeUntil() {
            return timeUntil;
        }

        public void setTimeUntil(List<String> timeUntil) {
            this.timeUntil = timeUntil;
        }

}
