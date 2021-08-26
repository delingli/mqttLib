package com.ldl.mqttlib.utils;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.IntRange;

import com.ldl.mqttlib.basic.IMqtt;
import com.ldl.mqttlib.impl.MqttImpl;
import com.ldl.mqttlib.impl.OnGeneralReceiveListener;

import org.json.JSONObject;


public class MqttManager {
    private static final MqttManager ourInstance = new MqttManager();
    private IMqtt iMqtt;

    public static MqttManager getInstance() {
        return ourInstance;
    }

    public IMqtt getiMqtt() {
        return iMqtt;
    }

    private MqttManager() {
    }

    public void init() {
        iMqtt = new MqttImpl();
    }


    /**
     * @param message
     * @param qos      控制传输质量等级的参数
     *                 qos 0：最多一次的传输
     *                 qos 1：至少一次的传输，(鸡肋)
     *                 qos 2： 只有一次的传输
     * @param retained 一般设置true ，保留消息，当订阅消费端服务器重新连接MQTT服务器时，总能拿到该主题最新消息
     *                 retained    false  订阅消费端服务器重新连接MQTT服务器时，不能拿到该主题最新消息，只能拿连接后发布的消息
     */

    public void publish(String message, @IntRange(from = 0, to = 2) int qos, boolean retained) {
        if (null != iMqtt && !TextUtils.isEmpty(message)) {
            iMqtt.publish(message, qos, retained);
        }
    }

    /**
     * 发布方法
     * 一般传原始json
     *
     * @param message
     */
    public void publish(String message) {
        if (!TextUtils.isEmpty(message) && null != iMqtt) {
            iMqtt.publish(message, 2, true);
        }
    }

    public void subscribe(String topid, int pos) {
        if (!TextUtils.isEmpty(topid) && null != iMqtt) {
            iMqtt.subscribe(topid, pos);
        }
    }

    /**
     * 发布方法重载方法
     * 可以传map
     *
     * @param arrayMap
     */
    public void publish(ArrayMap<String, String> arrayMap) {
        if (null != iMqtt && null != arrayMap && !arrayMap.isEmpty()) {
            JSONObject object = new JSONObject(arrayMap);
            iMqtt.publish(object.toString(), 2, true);
        }
    }
}
