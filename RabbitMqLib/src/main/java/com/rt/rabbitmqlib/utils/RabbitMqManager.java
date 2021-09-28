package com.rt.rabbitmqlib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.IntRange;

import com.blankj.utilcode.util.LogUtils;
import com.rt.rabbitmqlib.basic.IRabbitDispatch;

import org.json.JSONObject;


public class RabbitMqManager {
    private static final RabbitMqManager ourInstance = new RabbitMqManager();
    private IRabbitDispatch dispatcher;

    public static RabbitMqManager getInstance() {
        return ourInstance;
    }

    public IRabbitDispatch getRabbbitDispatch() {
        return dispatcher;
    }

    private RabbitMqManager() {
    }

    public void init(IRabbitDispatch rabbitDispatch) {
        this.dispatcher = rabbitDispatch;
    }

    public void publish(String deviceInfo) {
        if (dispatcher != null) {
            dispatcher.publishReport(deviceInfo);
        }
    }

    public boolean publishFace(String deviceInfo) {

        if (dispatcher != null) {
            return dispatcher.publishFaceReport(deviceInfo);
        }
        return false;
    }


}
