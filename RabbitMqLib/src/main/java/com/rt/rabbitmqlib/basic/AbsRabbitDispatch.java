package com.rt.rabbitmqlib.basic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.rt.rabbitmqlib.impl.IRabbitMqReceiveListener;
import com.rt.rabbitmqlib.impl.RabbitMqOption;
import com.rt.rabbitmqlib.impl.RabbitMqParamsOption;
import com.rt.rabbitmqlib.utils.DeviceIdUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract public class AbsRabbitDispatch implements IRabbitDispatch, IRabbitMqReceiveListener {
    protected static String TAG = "AbsRabbitDispatch";
    protected Context context;
    private RabbitMqOption rabbitMqOption;
    private RabbitMqParamsOption rabbitMqParamsOption;
    private ConnectionFactory factory;
    private Connection connection;
    private int CONNECTIONTIMEOUT = 1000 * 30;
    private int HEARTBEAT = 10;
    private boolean underDestroy = false;
    private Map params = new ArrayMap<String, String>();//参数
    protected RabbitEventListener eventListener;

    public interface RabbitEventListener {
        void onMessageReceived(String message);

        void onShutdownSignaled(String consumerTag, String sig);
    }
/*private boolean canConnect(){
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isAvailable()) {
        String name = info.getTypeName();
        Log.i(TAG, "当前网络名称：" + name);
        return true;
    } else {
        Log.i(TAG, "没有可用网络");
        *//*没有可用网络的时候，延迟30秒再尝试重连*//*
        mHandler.sendEmptyMessageDelayed(1, RECONNECT_TIME);
        return false;
    }
}*/
    private String devicesn;

    @Override
    public void initRabbitDispatcher(Context context, RabbitMqOption rabbitMqOption, RabbitMqParamsOption rabbitMqParamsOption) {
        this.context = context.getApplicationContext();
        this.rabbitMqOption = rabbitMqOption;
        this.rabbitMqParamsOption = rabbitMqParamsOption;
        if (this.context == null) {
            throw new RuntimeException("Context 参数不得为空");
        }
        if (this.rabbitMqOption == null) {
            throw new RuntimeException("RabbitMqOption 配置参数不得为空");
        }
        this.devicesn = DeviceIdUtil.getDeviceId(context);
        //初始参数
        params.put("type", 1);
        params.put("device_sn", devicesn);
        params.put("resolution", DeviceIdUtil.getScreenHeight(context) + "*" + DeviceIdUtil.getScreenWidth(context));
        params.put("memory", DeviceIdUtil.getTotalMemory() / 1024);
        params.put("device_ip", Objects.requireNonNull(DeviceIdUtil.getLocalIpAddress()));
        params.put("device_model", DeviceIdUtil.getSystemModel());
        params.put("device_version", DeviceIdUtil.getSystemVersion());
        params.put("root", DeviceIdUtil.isRoot() ? 1 : 0);
        params.put("storage", getDeviceStroge());//存储总内存
        if (null != this.rabbitMqParamsOption && null != this.rabbitMqParamsOption.getParams()) {
            //转换
            ArrayMap<String, String> params = rabbitMqParamsOption.getParams();
            for (Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> next = it.next();
                this.params.put(next.getKey(), next.getValue());
            }
        }
        LogUtils.d(TAG, "params:" + this.params.toString());
        this.devicesn = (String) this.params.get("device_sn");
        initConnectFactor();
    }

    @Override
    public void initConnectFactor() {
        factory = new ConnectionFactory();
        factory.setHost(rabbitMqOption.getHost());
        factory.setUsername(rabbitMqOption.getUsername());
        factory.setPassword(rabbitMqOption.getPassword());
        factory.setPort(rabbitMqOption.getPort());
        factory.setClientProperties(params);
        factory.setConnectionTimeout(CONNECTIONTIMEOUT);
        factory.setRequestedHeartbeat(HEARTBEAT);//是否断网
        factory.setAutomaticRecoveryEnabled(false);
        //   factory.setNetworkRecoveryInterval(10);// 设置 10s ，重试一次
        factory.setTopologyRecoveryEnabled(false);// 设置不重新声明交换器，队列等信息。
        factory.setSharedExecutor(new ThreadPoolExecutor(
                3, 5,
                Integer.MAX_VALUE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        ));
    }

    private static String QUEUE_NAMES = "ReportQRM";
    private static String QUEUE_EXCHANGE = "CERM";
    private static String FACE_EXCHANGE = "FACE_EXC";
    private static String FACEP_QUEUE = "face.*";
    private Channel channel;
    private Channel report;
    private String consumerTag;

    @Override
    public void publishReport(String deviceInfo) {
        if (report != null) {
            try {
                report.basicPublish(QUEUE_EXCHANGE, QUEUE_NAMES, null, deviceInfo.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean publishFaceReport(String deviceInfo) {
        if (report != null) {
            try {
                report.basicPublish(FACE_EXCHANGE, FACEP_QUEUE, null, deviceInfo.getBytes());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private int ix = 1;

    @Override
    public boolean reCreateRabbitConnection() {
        LogUtils.dTag(TAG, "RabbitDispatcher try connection & channel creation.");
        try {
            if (factory == null) {
                initConnectFactor();
            }
            if (factory != null) {
                connection = factory.newConnection();
                channel = connection.createChannel(ix);
                ix++;
                //设备上报的channel
                report = connection.createChannel();
                // 声明队列
                LogUtils.dTag(TAG, "RabbitDispatcher device_sn" + devicesn);
//                channel.queueDeclare(devicesn, true, false, false, null);
                channel.queueDeclare(devicesn, true, false, false, null);
                consumerTag = channel.basicConsume(devicesn, true, new RabbitConsumer(channel));

            }
        } catch (IOException e) {
            LogUtils.eTag(TAG, "IOException emitted!");
            e.printStackTrace();
            return false;
        } catch (TimeoutException t) {
            t.printStackTrace();
            LogUtils.eTag(TAG, "TimeoutException emitted!");
        }
        return true;

    }

    @Override
    public void destroyDispatcher() {
        underDestroy = true;
        LogUtils.dTag(TAG, "DestroyDispatcher called!");
        cleanConnectionResource(consumerTag);
        factory = null;
    }

    public void cleanConnectionResource(final String consumerTag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (report != null) report.close();
                    if (consumerTag != null && channel != null) {
                        channel.basicCancel(consumerTag);
                    }
                    if (channel != null) channel.close();
                    if (connection != null) connection.close();
                    LogUtils.dTag(TAG, "Clean Rabbit's connection resource successfully!");
                } catch (IOException e) {
                    LogUtils.dTag(TAG, "Clean Rabbit's connection resource, IOException emitted!");
                    e.printStackTrace();
                } catch (Exception e) {
                    LogUtils.dTag(TAG, "Clean Rabbit's connection resource, TimeoutException emitted!");
                    e.printStackTrace();
                } finally {
                    channel = null;
                    report = null;
                    connection = null;
                }
            }
        }).start();
    }

    private long lastMsgId = -1;

    public class RabbitConsumer extends DefaultConsumer {
        public RabbitConsumer(Channel channel) {
            super(channel);
        }


        @Override
        public void handleDelivery(String consumerTag,
                                   Envelope envelope,
                                   AMQP.BasicProperties properties,
                                   byte[] body) throws IOException {


            String routingKey = envelope.getRoutingKey();
            String contentType = properties.getContentType();
            LogUtils.dTag(TAG, "handle message with routingKey:" + routingKey + " contentType: " + contentType);
            long deliveryTag = envelope.getDeliveryTag();
            String message = new String(body);
            if (lastMsgId != deliveryTag) {
                lastMsgId = deliveryTag;
                if (!TextUtils.isEmpty(message) && !underDestroy) {
                    LogUtils.dTag(TAG, "message:" + message + "deliveryTag:" + deliveryTag);
                    receiveMessage(message);
                }

            }
            LogUtils.dTag(TAG, "ChannelNumber:" + channel.getChannelNumber() + "消息:" + message);
//            channel.basicAck(deliveryTag, false);

//            channel.basicConsume(deliveryTag, false);
            if (eventListener != null && underDestroy == false) {
                eventListener.onMessageReceived(message);
            }

        }


        @Override
        public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
            // the work to do here
            LogUtils.dTag(TAG, "ShutdownSignal received with consumerTag: " + consumerTag + " sig: " + sig.toString());
            try {
                report.close();
                channel.basicCancel(consumerTag);
                channel.close();
                connection.close();
            } catch (IOException e) {
                LogUtils.eTag(TAG, "IOException emitted!");
                e.printStackTrace();
            } catch (TimeoutException e) {
                LogUtils.eTag(TAG, "TimeoutException emitted!");
                e.printStackTrace();
            } catch (AlreadyClosedException e) {
                LogUtils.eTag(TAG, "AlreadyClosedException emitted!");
                e.printStackTrace();
            } finally {
                report = null;
                channel = null;
                connection = null;
                if (eventListener != null && underDestroy == false) {
                    LogUtils.dTag(TAG, "trigger callback listener: " + eventListener);
                    eventListener.onShutdownSignaled(consumerTag, sig.toString());
                    onError(consumerTag, sig.toString());

                }

            }
        }
    }

    private int getDeviceStroge() {
        int totalStroge = 0;
        try {
            String s1 = DeviceIdUtil.showROMTotal();
            String s2 = DeviceIdUtil.showROMAvail();
            String substring = s1.substring(s1.length() - 2, s1.length());
            String substring2 = s2.substring(s2.length() - 2, s2.length());

            if (substring.contains("MB")) {
                String[] mbs = s1.split("MB");
                String mb = mbs[0];
                String[] split = mb.split(",");
                totalStroge = Integer.parseInt(split[0] + split[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return totalStroge;
    }
}
