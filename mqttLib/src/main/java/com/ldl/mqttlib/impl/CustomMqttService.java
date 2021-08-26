package com.ldl.mqttlib.impl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ldl.mqttlib.utils.MqttManager;

public class CustomMqttService extends Service {
    public CustomMqttService() {
    }

    private static String OPTION_KEY = "option_key";
    private static String ONLINE_OPTION_KEY = "online_option_key";

    /**
     * @param context
     * @param option            连接相关参数的配置
     * @param onlineInforOption 上线参数的相关配置值，可传null
     */

    public static void startMqttService(Context context, MqttOption option, OnlineInforOption onlineInforOption) {
        Intent intent = new Intent(context, CustomMqttService.class);
        intent.putExtra(OPTION_KEY, option);
        if (null != onlineInforOption) {
            intent.putExtra(ONLINE_OPTION_KEY, option);
        }
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MqttManager.getInstance().init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && null != intent.getParcelableExtra(OPTION_KEY)) {
            MqttOption option = intent.getParcelableExtra(OPTION_KEY);
            OnlineInforOption onlineinforoption = intent.getParcelableExtra(ONLINE_OPTION_KEY);
            MqttManager.getInstance().getiMqtt().init(this.getApplicationContext(), option, onlineinforoption);
        } else {
            stopSelf();
            throw new RuntimeException("请传递MqttOption配置参数");
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MqttManager.getInstance().getiMqtt().toDisConnect();
    }
}