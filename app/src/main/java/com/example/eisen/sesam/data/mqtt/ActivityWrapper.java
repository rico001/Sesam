package com.example.eisen.sesam.data.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ActivityWrapper extends Observable {

    private List<Activity> activityFeedList = new ArrayList<>();

    public ActivityWrapper() {}

    public ActivityWrapper(List<Activity> activityFeedList) {
        this.activityFeedList = activityFeedList;
        setChanged();
        notifyObservers();
    }

    public List<Activity> getActivityFeedList() {
        return activityFeedList;
    }

    public void update(ActivityWrapper activityWrapper) {
        this.activityFeedList = activityWrapper.getActivityFeedList();
        setChanged();
        notifyObservers(this);
    }

}
