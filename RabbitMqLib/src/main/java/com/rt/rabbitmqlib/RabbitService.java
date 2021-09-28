package com.rt.rabbitmqlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.rt.rabbitmqlib.basic.IRabbitDispatch;
import com.rt.rabbitmqlib.impl.RabbitMqImpl;
import com.rt.rabbitmqlib.impl.RabbitMqOption;
import com.rt.rabbitmqlib.impl.RabbitMqParamsOption;
import com.rt.rabbitmqlib.impl.RabitMqAction;
import com.rt.rabbitmqlib.utils.RabbitMqManager;


//import cn_rt.BuildConfig;

/**
 * Created by ${zml} on 2019/4/17.
 */
public class RabbitService extends Service {
    private static String tag = "RabbitService";
    private static IRabbitDispatch dispatcher;
    private static Handler dispatcherHandler;
    private HandlerThread dispatcherThread;
    private static Runnable runner;
    private static String OPTION_KEY = "option_keyz";
    private static String ONLINE_OPTION_KEY = "rabbitmqparamsoption_keyz";
    private RabbitMqOption option;
    private RabbitMqParamsOption onlineinforoption;

    public static void startRabbitService(Context context, RabbitMqOption option, RabbitMqParamsOption rabbitMqParamsOption) {
        Intent intent = new Intent(context, RabbitService.class);
        intent.putExtra(OPTION_KEY, option);
        if (null != rabbitMqParamsOption) {
            intent.putExtra(ONLINE_OPTION_KEY, option);
        }
        context.startService(intent);
    }

/*    public void onMessageReceived(String message) {
        LogUtils.dTag(tag, "onMessageReceived: " + message);
    }

    public void onShutdownSignaled(String consumerTag, String sig) {
        LogUtils.dTag(tag, "onShutdownSignaled: " + consumerTag + " " + sig);
        LogUtils.dTag(tag, "On shutdown signaled，recreate dispatcher resources.");
        dispatcherHandler.postDelayed(runner, 5000);
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //如果没有配置，显示默认ip
        if (runner == null) {
            runner = new Runnable() {
                @Override
                public void run() {
                    LogUtils.dTag(tag, "Call startRabbitDispatcher in runner ... ");
                    startRabbitDispatcher();
                }
            };
        }
        dispatcherThread = new HandlerThread("dispatcher");
        //开启一个线程
        dispatcherThread.start();
        //在这个线程中创建一个handler对象
        dispatcherHandler = new Handler(dispatcherThread.getLooper());

    }


    public boolean startRabbitDispatcher() {
        LogUtils.iTag(tag, "Start Rabbit dispatcher creation ... ");
        if (dispatcher == null) {
            RabbitMqManager.getInstance().init(dispatcher = new RabbitMqImpl());
            dispatcher.initRabbitDispatcher(this.getApplicationContext(), option, onlineinforoption);
        }
        boolean ret = dispatcher.reCreateRabbitConnection();
        if (!ret) {
            LogUtils.iTag(tag, "Start Rabbit dispatcher failed, recreate it after 5s.");
            dispatcherHandler.postDelayed(runner, 5000);
        } else {
            LogUtils.iTag(tag, "Start Rabbit dispatcher successful.");
        }
        return ret;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && null != intent.getParcelableExtra(OPTION_KEY)) {
            option = intent.getParcelableExtra(OPTION_KEY);
            onlineinforoption = intent.getParcelableExtra(ONLINE_OPTION_KEY);
        } else {
            stopSelf();
            throw new RuntimeException("请传递MqttOption配置参数");
        }

        dispatcherHandler.postDelayed(runner, 0);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        LogUtils.dTag(tag, "RabbitService's onDestroy called");
        super.onDestroy();
        if (dispatcher != null) {
            dispatcher.destroyDispatcher();
            dispatcher = null;
        }
        dispatcherHandler.removeCallbacksAndMessages(null);
        dispatcherHandler = null;
        runner = null;
        dispatcherThread.quit();
        dispatcherThread = null;

    }
}


