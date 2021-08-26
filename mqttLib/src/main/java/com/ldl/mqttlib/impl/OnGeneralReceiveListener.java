package com.ldl.mqttlib.impl;

public
interface OnGeneralReceiveListener {
    /**
     * @param topid       主题
     * @param mqttMessage
     */
    void receiveMessage(String topid, String mqttMessage);
}
