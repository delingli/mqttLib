package com.example.idsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ldl.mqttlib.impl.CustomMqttService;
import com.ldl.mqttlib.impl.MqttAction;
import com.ldl.mqttlib.impl.MqttOption;
import com.ldl.mqttlib.utils.DeviceIdUtil;
import com.ldl.mqttlib.utils.MqttManager;


public class MainActivity extends AppCompatActivity {
    String host = "tcp://10.10.20.200:1883";
    String topic = "device/" + DeviceIdUtil.getDeviceId(this);
    String deviceId = DeviceIdUtil.getDeviceId(this);
    private TextView textView;
    private LocalBroadcastManager mLocalBroadcastManager;
    private MyReceiver myReceiver;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toConnectService();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                MqttManager.getInstance().publish(i + "号选手你好");
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MqttAction.RECEOIVER_ACTION);
        myReceiver = new MyReceiver();
        mLocalBroadcastManager.registerReceiver(myReceiver, intentFilter);
        myReceiver.setOnReceiverMessageListener(new MyReceiver.OnReceiverMessageListener() {
            @Override
            public void onMessage(String topId, String mqttMessage) {
                textView.setText(topId + "\n" + mqttMessage);
            }
        });

    }

    private void toConnectService() {
        MqttOption option = new MqttOption.MqttOptionBuilder(host)
                .publish_topid(topic)
                .response_topid(topic)
                .username("itc")
                .clientId(deviceId)
                .password("itc.pass").build();
        CustomMqttService.startMqttService(this, option, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(myReceiver);

    }
}