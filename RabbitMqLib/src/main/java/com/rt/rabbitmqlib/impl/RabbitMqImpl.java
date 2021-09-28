package com.rt.rabbitmqlib.impl;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.LogUtils;
import com.rt.rabbitmqlib.basic.AbsRabbitDispatch;

public
class RabbitMqImpl extends AbsRabbitDispatch {
    @Override
    public void receiveMessage(String message) {
        Log.d(TAG, "接收的消息为:" + message);
        sendtoApp(message);
    }

    private void sendtoApp(String mqttMessage) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(RabitMqAction.RECEOIVER_ACTION);
        intent.putExtra(RabitMqAction.KEY_CONTENT_MESSAGE, mqttMessage);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onError(String consumerTag, String sig) {
        if (!TextUtils.isEmpty(consumerTag) && !TextUtils.isEmpty(sig))
            LogUtils.eTag(TAG, consumerTag, sig);
    }





}
