package com.example.idsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ldl.mqttlib.impl.MqttAction;

public class MyReceiver extends BroadcastReceiver {
    private String tag = "MyReceiver";
    private OnReceiverMessageListener onReceiverMessageListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String contentMessage = intent.getStringExtra(MqttAction.KEY_CONTENT_MESSAGE);
            String topId = intent.getStringExtra(MqttAction.KEY_TOPID_MESSAGE);
            Log.d(tag, topId + "::" + contentMessage);
            if (null != onReceiverMessageListener) {
                onReceiverMessageListener.onMessage(topId, contentMessage);
            }
        }

    }

    public void setOnReceiverMessageListener(OnReceiverMessageListener onReceiverMessageListener) {
        this.onReceiverMessageListener = onReceiverMessageListener;
    }

    public interface OnReceiverMessageListener {
        void onMessage(String topId, String mqttMessage);
    }
}
