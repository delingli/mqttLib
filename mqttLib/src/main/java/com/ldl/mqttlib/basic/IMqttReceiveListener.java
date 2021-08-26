package com.ldl.mqttlib.basic;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 接受数据的回调
 */
public interface IMqttReceiveListener {
    void receiveMessage(String topid, String mqttMessage);

    void receiveComplete(IMqttDeliveryToken iMqttDeliveryToken);

    void disConnection(Throwable throwable);
}
