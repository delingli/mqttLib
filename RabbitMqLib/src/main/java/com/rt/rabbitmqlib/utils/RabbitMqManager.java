package com.rt.rabbitmqlib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.IntRange;

import com.blankj.utilcode.util.LogUtils;
import com.rt.rabbitmqlib.basic.IRabbitDispatch;
import com.rt.rabbitmqlib.impl.IRabbitMqReceiveListener;
import com.rt.rabbitmqlib.impl.RabbitMqImpl;

import org.json.JSONObject;


public class RabbitMqManager {
    private static final RabbitMqManager ourInstance = new RabbitMqManager();
    private IRabbitDispatch dispatcher;

    public static RabbitMqManager getInstance() {
        return ourInstance;
    }

    private static final String TAG = "RabbitMqManager";

    public IRabbitDispatch getRabbbitDispatch() {
        return dispatcher;
    }

    private RabbitMqManager() {
    }


    private IRabbitMqReceiveListener mIRabbitMqReceiveListener = new IRabbitMqReceiveListener() {
        @Override
        public void receiveMessage(String message) {
            LogUtils.dTag(TAG, "message:" + message);
            if (null != mOnReceivedMessageListener) {
                mOnReceivedMessageListener.receiveMessage(message);
            }
        }

        @Override
        public void onError(String consumerTag, String sig) {
            LogUtils.eTag(TAG, "consumerTag:" + consumerTag + ":" + sig);
            if (null != mOnReceivedMessageListener) {
                mOnReceivedMessageListener.onError(consumerTag, sig);
            }
        }
    };
    private OnReceivedMessageListener mOnReceivedMessageListener;

    public interface OnReceivedMessageListener {
        void receiveMessage(String message);

        void onError(String consumerTag, String sig);
    }

    public void addOnRabbitMqReceiveListener(OnReceivedMessageListener onReceivedMessageListener) {
        this.mOnReceivedMessageListener = onReceivedMessageListener;
    }

    public void init(IRabbitDispatch rabbitDispatch) {
        this.dispatcher = rabbitDispatch;
        if (dispatcher instanceof RabbitMqImpl) {
            RabbitMqImpl mRabbitMqImpl = (RabbitMqImpl) dispatcher;
            mRabbitMqImpl.setOnRabbitMqReceiveListener(mIRabbitMqReceiveListener);
        }
    }

    public void unRegistRabbitMqReceiveListener() {
        if (dispatcher != null && dispatcher instanceof RabbitMqImpl) {
            RabbitMqImpl mRabbitMqImpl = (RabbitMqImpl) dispatcher;
            mRabbitMqImpl.setOnRabbitMqReceiveListener(null);
        }
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
