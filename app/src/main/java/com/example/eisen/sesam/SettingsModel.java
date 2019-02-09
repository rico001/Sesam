package com.example.eisen.sesam;


import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SettingsModel {

    private int duration;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingsModel() {
        this.duration = 5;
        this.listDataHeader = new LinkedList<>();
        this.listDataChild = new HashMap<>();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public static String settingsInJSON(SettingsModel s) {

        if (s.getListDataHeader().size() > 0) {

            ArrayList<String> dateFrom = new ArrayList<>();
            ArrayList<String> dateUntil = new ArrayList<>();
            ArrayList<String> timeFrom = new ArrayList<>();
            ArrayList<String> timeUntil = new ArrayList<>();
            ArrayList<String> klingeln = new ArrayList<>();

            if (s.getListDataHeader().size() > 0) {
                for (int i = 0; i <= s.getListDataHeader().size() - 1; i++) {
                    dateFrom.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(0));
                    dateUntil.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(1));    //Datum
                    timeFrom.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(2));   //Zeit ab
                    timeUntil.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(3));
                    klingeln.add(s.getListDataChild().get(s.getListDataHeader().get(i)).get(4));//Zeit bis
                }
            }


            Settings se = new Settings(dateFrom, dateUntil, timeFrom, timeUntil, klingeln, s.getDuration());

            Gson gson = new Gson();
            String settingsDataJSON = gson.toJson(se);
            return settingsDataJSON;
        }


        return "";
    }

}

    class Settings {

        private List<String> klingeln;
        private List<String> dateFrom;
        private List<String> dateUntil;
        private List<String> timeFrom;
        private List<String> timeUntil;
        private int oeffnungsdauer;

        public Settings(){

        }

        public Settings(ArrayList<String> dateFrom,ArrayList<String> dateUntil, ArrayList<String> timeFrom, ArrayList<String> timeUntil, ArrayList<String> klingeln, int oeffnungsdauer) {
            this.dateFrom = dateFrom;
            this.dateUntil = dateUntil;
            this.timeFrom = timeFrom;
            this.timeUntil = timeUntil;
            this.klingeln = klingeln;
            this.oeffnungsdauer=oeffnungsdauer;
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

        public List<String> getKlingeln() {
            return klingeln;
        }

        public void setKlingeln(List<String> klingeln) {
            this.klingeln = klingeln;
        }

        public int getOeffnungsdauer() {
            return oeffnungsdauer;
        }

        public void setOeffnungsdauer(int oeffnungsdauer) {
            this.oeffnungsdauer = oeffnungsdauer;
        }


    }
