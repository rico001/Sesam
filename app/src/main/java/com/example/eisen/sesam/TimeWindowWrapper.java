package com.example.eisen.sesam;

import java.util.ArrayList;
import java.util.List;

public class TimeWindowWrapper{

    private List<TimeWindow> timeWindows;

    public TimeWindowWrapper() {
        timeWindows = new ArrayList<TimeWindow>();
    }

    public TimeWindowWrapper(List<TimeWindow> timeWindows) {
        this.timeWindows = timeWindows;
    }

    public List<TimeWindow> getTimeWindows() {
        return timeWindows;
    }
}
