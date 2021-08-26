package com.ldl.mqttlib.basic;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * 监听连接成功与否
 */
public interface MqttActionListener {
    void onSucess(IMqttToken mqttToken);

    void onFailures(IMqttToken iMqttToken, Throwable throwable);
}
