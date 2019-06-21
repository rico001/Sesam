package com.example.eisen.sesam;

import java.util.List;

public class ActivityWrapper {

    private List<Activity> activityFeedList;

    public ActivityWrapper() {
        this.activityFeedList = null;
    }

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
