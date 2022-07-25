package com.rt.rabbitmqlib.basic;

import android.content.Context;

import com.rt.rabbitmqlib.impl.RabbitMqOption;
import com.rt.rabbitmqlib.impl.RabbitMqParamsOption;

public interface IRabbitDispatch {
    void initRabbitDispatcher(Context context, RabbitMqOption rabbitMqOption, RabbitMqParamsOption rabbitMqParamsOption);

    void initConnectFactor();

    boolean reCreateRabbitConnection();
    void destroyDispatcher();
    void publishReport(String deviceInfo);
    boolean publishFaceReport(String deviceInfo);
    boolean isConnection();
}
