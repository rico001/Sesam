package com.example.eisen.sesam;

public class TimeWindow{

    private String title;

    private String FromDate;
    private String tillDate;

    private String FromTime;
    private String tillTime;

    private int ringNumber;
    private int openDuration;

    public TimeWindow(){}

    public TimeWindow(String fromDate, String tillDate, String fromTime, String tillTime, int ringNumber, int openDuration) {
        FromDate = fromDate;
        this.tillDate = tillDate;
        FromTime = fromTime;
        this.tillTime = tillTime;
        this.ringNumber = ringNumber;
        this.openDuration = openDuration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getTillDate() {
        return tillDate;
    }

    public void setTillDate(String tillDate) {
        this.tillDate = tillDate;
    }

    public String getFromTime() {
        return FromTime;
    }

    public void setFromTime(String fromTime) {
        FromTime = fromTime;
    }

    public String getTillTime() {
        return tillTime;
    }

    public void setTillTime(String tillTime) {
        this.tillTime = tillTime;
    }

    public int getRingNumber() {
        return ringNumber;
    }

    public void setRingNumber(int ringNumber) {
        this.ringNumber = ringNumber;
    }

    public int getOpenDuration() {
        return openDuration;
    }

    public void setOpenDuration(int openDuration) {
        this.openDuration = openDuration;
    }

}
