package com.rt.rabbitmqlib.impl;


/**
 * 接受数据的回调
 */
public interface IRabbitMqReceiveListener {
    void receiveMessage(String message);

    void onError(String consumerTag, String sig);

}
