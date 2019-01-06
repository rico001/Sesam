package com.example.eisen.sesam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import android.databinding.*;

import java.io.UnsupportedEncodingException;


public class MqttHelper extends BaseObservable{

    private MqttAndroidClient mqttAndroidClient;

    final String serverIP = "tcp://192.168.178.80:1883";        //raspi bzw mqttServer
    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "Sesam/Esp/state";         //for Button SofortÖffnen
    final String publishTopic = "Sesam/Settings/date";          //for ESP


    public MqttHelper(final Context context){
        mqttAndroidClient = new MqttAndroidClient(context, serverIP, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("Mqtt", "Verbindung verloren" + serverIP);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Mqtt", "Message arrived:  " +message.toString()+ serverIP);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Log.d("Mqtt", token.getMessage().toString());
                }catch(Exception e){}
            }
        });

        connect();
    }

    private void connect(){
        try {

            mqttAndroidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Mqtt", "Connect erfolgreich" + serverIP);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Mqtt", "Connect fehlgeschlagen" + serverIP + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }

    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });
        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage(String msg, int qos)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(false); //nachrichten mit true kommen öfter(bis trueMessage überschrieben) an auch nachdem nachricht mit false gesendet wurde!!
        message.setQos(qos);
        mqttAndroidClient.publish(publishTopic, message);
    }

    public void publishMessageTo(String msg, int qos, String anyTopic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(false); //nachrichten mit true kommen öfter(bis trueMessage überschrieben) an auch nachdem nachricht mit false gesendet wurde!!
        message.setQos(qos);
        mqttAndroidClient.publish(anyTopic, message);
    }



}
