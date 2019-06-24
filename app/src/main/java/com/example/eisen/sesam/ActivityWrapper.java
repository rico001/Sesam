package com.example.eisen.sesam;

import java.util.ArrayList;
import java.util.List;

public class ActivityWrapper {

    private List<Activity> activityFeedList = new ArrayList<>();

    public ActivityWrapper() {}

    public ActivityWrapper(List<Activity> activityFeedList) {
        this.activityFeedList = activityFeedList;
    }

    public List<Activity> getActivityFeedList() {
        return activityFeedList;
    }

    public void setActivityFeedList(List<Activity> activityFeedList) {
        this.activityFeedList = activityFeedList;
    }

}
