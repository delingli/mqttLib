package com.ldl.mqttlib.basic;

import android.content.Context;

import com.ldl.mqttlib.impl.MqttOption;
import com.ldl.mqttlib.impl.OnGeneralReceiveListener;
import com.ldl.mqttlib.impl.OnlineInforOption;

public interface IMqtt {


    /**
     * 初始化连接相关配置
     */
    void init(Context context, MqttOption option, OnlineInforOption onlineInforOption);

    /**
     * 连接方法
     */
    void toClientConnection();

    void publish(String message, int qos, boolean retained);

    void subscribe(String topid, int qos);

    boolean isConnected();

    //断开连接
    void toDisConnect();

    boolean canConnect();

}
