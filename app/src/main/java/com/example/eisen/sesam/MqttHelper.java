package com.example.eisen.sesam;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;


public class MqttHelper{

    private MqttAndroidClient mqttAndroidClient;

    private final String clientId = "SesamApp";
    private final String subscriptionTopic = "Sesam/Esp/state";         //for Button SofortÖffnen
    private String serverIp;
    private Context context;




    public MqttHelper(final Context context, String ip){
        this.context=context;
        serverIp="tcp://"+ip+":1883";
        mqttAndroidClient = new MqttAndroidClient(context, serverIp, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("Mqtt", "Verbindung verloren");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Mqtt", "Message arrived:  " +message.toString());
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

            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            options.setConnectionTimeout(10000);

            //Löscht letzten OpenDoorNow-Befehl nach AppSchließung->""/leerer String
            try {
                String s="";
                byte[] encodedPayload = new byte[0];
                encodedPayload = s.getBytes("UTF-8");
                options.setWill(OpenDoorFragment.OPENDOORTOPIC,encodedPayload,2,true);
            } catch (Exception ex) { }

            mqttAndroidClient.connect(options,context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Mqtt", "Connect erfolgreich");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Mqtt", "Connect fehlgeschlagen"+ exception.toString());
                }
            });


        } catch (MqttException ex){
            Log.d("Mqtt", "HALLOO");
        }

    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, context, new IMqttActionListener() {
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

    public void publishMessageTo(String msg, int qos, String anyTopic, boolean retainFlag) throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(retainFlag); //nachrichten mit true kommen öfter(bis trueMessage überschrieben) an auch nachdem nachricht mit false gesendet wurde!!
        message.setQos(qos);
        mqttAndroidClient.publish(anyTopic, message);
    }

    public void setCallback(MqttCallback callback){
        mqttAndroidClient.setCallback(callback);
    }


}
