package com.rt.rabbitmqlib.impl;

public interface RabitMqAction {
    String RECEOIVER_ACTION = "com.rt.rabbitmqlib.impl.receivemessageaction";
    String ACTION_CONNECT_STATE = "com.rt.rabbitmqlib.impl.notify_connect_state";
    String KEY_CONNECT_STATE = "key_com.rt.rabbitmqlib.impl.notify_connect_state";
    String KEY_CONTENT_MESSAGE = "com.rt.rabbitmqlib.impl.receivemessage";
}
