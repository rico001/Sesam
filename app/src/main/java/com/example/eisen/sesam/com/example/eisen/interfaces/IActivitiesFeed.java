package com.example.eisen.sesam.com.example.eisen.interfaces;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface IActivitiesFeed {

    String TAG = "IUpdatableFragment";

    void onMainActivityReceiveActivities(MqttMessage m);

    void refreshFragment();
}
