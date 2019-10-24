package com.example.eisen.sesam.communication;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;


public class MqttHelper{

    private MqttAndroidClient mqttAndroidClient;

    public static final String CLIENT_ID = "SesamApp";
    
    public static final String TOPIC_ESP_ACTIVATED = "Sesam/Esp/state";         //for Button SofortÖffnen
    public static final String TOPIC_ESP_SETTINGS = "Sesam/Settings/date";
    public static final String TOPIC_ACTIVITYFEED = "Sesam/activityfeed";
    public static final String TOPIC_OPENDOOR_NOW ="Sesam/openDoorNow";

    private String serverIp;
    private Context context;

    public MqttHelper(final Context context, String ip,MqttCallback mqttCallback, IMqttActionListener iMqttActionListener){
        this.context=context;
        serverIp="tcp://"+ip+":1883";
        mqttAndroidClient = new MqttAndroidClient(context, serverIp, CLIENT_ID);
        mqttAndroidClient.setCallback(mqttCallback);
        connect(iMqttActionListener);
    }

    private void connect(IMqttActionListener iMqttActionListener){
        try {

            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            options.setConnectionTimeout(10000);

            mqttAndroidClient.connect(options, context, iMqttActionListener);


        } catch (MqttException ex){
            Log.d("Mqtt", "HALLOO");
        }

    }

    public void subscribeToTopics() {
        try {
            mqttAndroidClient.subscribe(TOPIC_ESP_ACTIVATED, 0);
            mqttAndroidClient.subscribe(TOPIC_ESP_SETTINGS, 0);
            mqttAndroidClient.subscribe(TOPIC_ACTIVITYFEED, 0);
        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessageTo(String msg, int qos, String anyTopic, boolean retainFlag) throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(retainFlag); //nachrichten mit true kommen öfter(bis trueMessage überschrieben) an auch nachdem nachricht mit false gesendet wurde!!
        message.setQos(qos);
        mqttAndroidClient.publish(anyTopic, message);
    }

    public boolean isConnected(){
       return mqttAndroidClient.isConnected();
    }


}
