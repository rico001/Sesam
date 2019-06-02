package com.example.eisen.sesam;

import java.util.ArrayList;
import java.util.List;

public class TimeWindowWrapper{

    private List<TimeWindow> timeWindows;

    public TimeWindowWrapper() {
        timeWindows = new ArrayList<TimeWindow>();
    }

    public TimeWindowWrapper(ArrayList<TimeWindow> timeWindows) {
        this.timeWindows = timeWindows;
    }

    public List<TimeWindow> getTimeWindows() {
        return timeWindows;
    }

    public static ArrayList<String> generateTimeWindowsTitleList(List<TimeWindow> timeWindows){
        final ArrayList<String> titleList = new ArrayList<>();
        timeWindows.stream().forEach(timeWindow -> titleList.add(timeWindow.getTitle()));
        return titleList;
    }
}
