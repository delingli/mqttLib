package com.ldl.mqttlib.impl;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ldl.mqttlib.basic.AbsMqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public class MqttImpl extends AbsMqtt {


    public MqttImpl() {
    }

    @Override
    public void receiveMessage(String topid, String mqttMessage) {
        Log.d(tag, "接收的消息为:" + topid + mqttMessage);
        sendtoApp(topid, mqttMessage);

    }

    private void sendtoApp(String topid, String mqttMessage) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        Intent intent = new Intent(MqttAction.RECEOIVER_ACTION);
        intent.putExtra(MqttAction.KEY_CONTENT_MESSAGE, mqttMessage);
        intent.putExtra(MqttAction.KEY_TOPID_MESSAGE, topid);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void receiveComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(tag, "receiveComplete:" + iMqttDeliveryToken.toString());
    }

    @Override
    public void disConnection(Throwable throwable) {
        throwable.printStackTrace();
    }


}
