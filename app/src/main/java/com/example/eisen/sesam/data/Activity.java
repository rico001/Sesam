package com.example.eisen.sesam.data;
import java.util.List;

public class Activity{

    private String time;
    private String date;
    private int ringNumber;
    private boolean wasOpened;
    private float batteryStatus;
    private List<String> openForTitles;

    public Activity(){

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRingNumber() {
        return ringNumber;
    }

    public void setRingNumber(int ringNumber) {
        this.ringNumber = ringNumber;
    }

    public boolean isWasOpened() {
        return wasOpened;
    }

    public void setWasOpened(boolean wasOpened) {
        this.wasOpened = wasOpened;
    }

    public float getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(float batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public List<String> getOpenForTitles() {
        return openForTitles;
    }

    public void setOpenForTitles(List<String> openForTitles) {
        this.openForTitles = openForTitles;
    }

    @Override
    public String toString() {

        return  "\n--------------------------"+"\n"+
                "Time:"+this.getTime()+"\n"+
                "Date:"+this.getDate()+"\n"+
                "Battery"+this.getBatteryStatus()+"\n"+
                "ringNumber:"+this.getRingNumber()+"\n"+
                "--------------------------"+"\n";
    }

}
