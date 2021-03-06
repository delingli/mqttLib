package com.ldl.mqttlib.basic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.blankj.utilcode.util.NetworkUtils;
import com.ldl.mqttlib.impl.MqttOption;
import com.ldl.mqttlib.impl.OnlineInforOption;
import com.ldl.mqttlib.utils.DeviceIdUtil;
import com.ldl.mqttlib.utils.MqttManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbsMqtt implements IMqtt, MqttActionListener, IMqttReceiveListener {
    public static String tag = "MqttImpl";
    private MqttAndroidClient mQttAndroidClient;
    public static int CLIENTTIMEOUT = 30;
    public static int HESRTTIME = 15;
    public static int RECONNECT_TIME = 30 * 1000;
    private MqttOption mqttOption;
    private OnlineInforOption onlineInforOption;
    private MqttConnectOptions mQttConnectOptions;
    protected Context mContext;
    private Integer qos = 2;

    @Override
    public boolean isConnected() {
        if (null != mQttAndroidClient) {
            return mQttAndroidClient.isConnected();
        }
        return false;
    }

    private Handler.Callback mCallBack = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                toClientConnection();
            }
            return true;
        }
    };
    private CustomHandler mHandler = new CustomHandler(mCallBack);

    static class CustomHandler extends Handler {
        private WeakReference<Callback> reference;

        public CustomHandler(Callback callback) {
            reference = new WeakReference<Callback>(callback);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (reference != null && reference.get() != null) {
                Callback callback = reference.get();
                callback.handleMessage(msg);
            }

        }
    }

    @Override
    public boolean canConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(tag, "?????????????????????" + name);
            return true;
        } else {
            Log.i(tag, "??????????????????");
            /*????????????????????????????????????30??????????????????*/
            mHandler.sendEmptyMessageDelayed(1, RECONNECT_TIME);
            return false;
        }
    }

    MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.e(tag, "connectComplete;reconnect=" + reconnect + ";serverURI=" + serverURI);
            if (reconnect) {
                try {
                    Log.v(tag, "???????????? 2");
                    if (null != mQttAndroidClient) {
                        Log.v(tag, "???????????? 2 null != mQttAndroidClient ????????????");
                        mQttAndroidClient.subscribe(mqttOption.getPublish_topid(), qos);//?????????????????????????????????????????????
                    }
                    //??????????????????;???
                    if (mqttOption != null) {
                        Log.v(tag, "???????????? 3 ??????????????????");
                        String str = getOnlineMessage();
                        publish(str, qos, mqttOption.getRetained());
                    }

                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.e(tag, "????????????");
            if (null != cause) {
                cause.printStackTrace();
            }
            onFailures(null, cause);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            receiveMessage(topic, new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            receiveComplete(token);
        }
    };
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            //???????????????
            onSucess(asyncActionToken);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            onFailures(asyncActionToken, exception);
            exception.printStackTrace();
        }
    };

    @Override
    public void init(Context context, MqttOption option, OnlineInforOption onlineInforOption) {
        this.mContext = context.getApplicationContext();
        this.mqttOption = option;
        this.onlineInforOption = onlineInforOption;
        mQttAndroidClient = new MqttAndroidClient(mContext, mqttOption.getHost(), mqttOption.getClientid());
        mQttAndroidClient.setCallback(mqttCallbackExtended);
        mQttConnectOptions = new MqttConnectOptions();
        mQttConnectOptions.setCleanSession(true);//?????????
        mQttConnectOptions.setConnectionTimeout(CLIENTTIMEOUT);//????????????
        mQttConnectOptions.setKeepAliveInterval(HESRTTIME);//??????????????? ???
        mQttConnectOptions.setAutomaticReconnect(true);//?????????????????????????????????????????????????????????????????????????????????????????????
        mQttConnectOptions.setUserName(mqttOption.getUsername());
        mQttConnectOptions.setPassword(mqttOption.getPassword().toCharArray());
        boolean connect = true;
        try {
            JSONObject willJson = new JSONObject();
            //????????????  "{"status":"offline","device_sn":"AB9CDDBDB62D"}"
            willJson.put("status", "offline");
            willJson.put("device_sn", option.getDevice_sn());
            // //setWill??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mQttConnectOptions.setWill(mqttOption.getPublish_topid(), willJson.toString().getBytes(), qos, false);
        } catch (Exception e) {
            e.printStackTrace();
            connect = false;
            onFailures(null, e);
        }
        if (connect) {
            toClientConnection();
        }
        NetworkUtils.registerNetworkStatusChangedListener(onNetworkStatusChangedListener);


    }

    private NetworkUtils.OnNetworkStatusChangedListener onNetworkStatusChangedListener = new NetworkUtils.OnNetworkStatusChangedListener() {
        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnected(NetworkUtils.NetworkType networkType) {
            if (null != mQttAndroidClient && !mQttAndroidClient.isConnected()) {
                if (null != mHandler) {
                    mHandler.sendEmptyMessageDelayed(1, RECONNECT_TIME);
                }
            }

        }
    };

    @Override
    public void publish(String message, int qos, boolean retained) {
        if (null != mQttAndroidClient && mQttAndroidClient.isConnected()) {
            try {
                mQttAndroidClient.publish(mqttOption.getPublish_topid(), message.getBytes(), qos, retained);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void toClientConnection() {
        try {
            if (null != mQttAndroidClient && !mQttAndroidClient.isConnected() && canConnect()) {
                mQttAndroidClient.connect(mQttConnectOptions, null, iMqttActionListener);
            }
        } catch (MqttException e) {
            e.printStackTrace();
            onFailures(null, e);
        }
    }

    @Override
    public void onSucess(IMqttToken mqttToken) {
        Log.d(tag, "???????????? ");
        try {
            if (null != mQttAndroidClient) {
                mQttAndroidClient.subscribe(mqttOption.getPublish_topid(), qos);//?????????????????????????????????????????????
            }
            //??????????????????;???
            if (mqttOption != null) {
                String str = getOnlineMessage();
                publish(str, qos, mqttOption.getRetained());
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(String topid, int qos) {

        try {
            if (null != mQttAndroidClient) {
                mQttAndroidClient.subscribe(topid, qos);//?????????????????????????????????????????????
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    protected String getOnlineMessage() {
        try {
            JSONObject objJson = new JSONObject();
            objJson.put("type", 1);
            objJson.put("device_sn", mqttOption.getDevice_sn());
            objJson.put("status", "online");
            objJson.put("resolution", DeviceIdUtil.getScreenHeight(mContext) + "*" + DeviceIdUtil.getScreenWidth(mContext));
            objJson.put("app_version", "1.0");
            objJson.put("memory", DeviceIdUtil.getTotalMemory() / 1024);
            objJson.put("device_ip", Objects.requireNonNull(DeviceIdUtil.getLocalIpAddress()));
            objJson.put("device_model", DeviceIdUtil.getSystemModel());
            objJson.put("device_version", DeviceIdUtil.getSystemVersion());
            objJson.put("root", DeviceIdUtil.isRoot() ? 1 : 0);
            objJson.put("storage", getDeviceStroge());//???????????????

            if (null != onlineInforOption) {  //?????????
                if (null != onlineInforOption.getParams()) {
                    ArrayMap<String, String> params = onlineInforOption.getParams();
                    for (Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<String, String> next = it.next();
                        objJson.put(next.getKey(), next.getValue());
                    }
                }
                if (!(onlineInforOption.getType() < 0)) {//type???????????????0?????????
                    objJson.put("type", onlineInforOption.getType());
                }

            }


            return objJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void onFailures(IMqttToken iMqttToken, Throwable throwable) {
        Log.e(tag, "????????????");
        if (null != throwable) {
            throwable.printStackTrace();
        }
        if (null == mHandler) {
            mHandler = new CustomHandler(mCallBack);
        }
        mHandler.sendEmptyMessageDelayed(1, RECONNECT_TIME);
    }

    @Override
    public void toDisConnect() {
        try {
            if (null != mqttOption) {
                JSONObject willJson = new JSONObject();
                //????????????  "{"status":"offline","device_sn":"AB9CDDBDB62D"}"
                willJson.put("status", "offline");
                willJson.put("device_sn", mqttOption.getDevice_sn());
                MqttManager.getInstance().publish(willJson.toString(), false);
            }
            if (null != mQttAndroidClient) {
                mQttAndroidClient.disconnect();
//                mQttAndroidClient.close();

            }
            if (null != mHandler) {
                mHandler.removeMessages(1);
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            NetworkUtils.unregisterNetworkStatusChangedListener(onNetworkStatusChangedListener);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
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
