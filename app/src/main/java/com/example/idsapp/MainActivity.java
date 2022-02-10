package com.example.idsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ldl.mqttlib.impl.CustomMqttService;
import com.ldl.mqttlib.impl.MqttOption;
import com.ldl.mqttlib.utils.DeviceIdUtil;
import com.ldl.upanepochlog.util.SDCardUtils;
import com.ldl.upanepochlog.util.Utils;

public class MainActivity extends AppCompatActivity {
    String host = "tcp://10.10.20.200:1883";
    String topic = "device/" + DeviceIdUtil.getDeviceId(this);
    String deviceId = DeviceIdUtil.getDeviceId(this);
    private TextView textView;
    private LocalBroadcastManager mLocalBroadcastManager;
    private MyReceiver myReceiver;
    int i = 0;
    private Button button3, button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        textView = find·ViewById(R.id.textView);
//        button3 = findViewById(R.id.button3);
//        button4 = findViewById(R.id.button4);
//        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogService.startServices(MainActivity.this);
//            }
//        });
//        findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, TestLogActivity.class);
//                MainActivity.this.startActivity(intent);
//            }
//        });
//        LogUtils.getConfig().setLog2FileSwitch(true);
//        LogUtils.dTag("ldl", "测试打印日志啊" + "Root:" + Utils.isDeviceRooted());
//        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
//        button4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
///*//                String comm = "bugreport" + " " + getPath();
//                String path = getPath();
////                String cc = "cd " + path+"\n";
//              String  filePath = path.replace(" ", "");
//                filePath = path.replace(":", "-");
//                String[] cmd = new String[]{
//                        "logcat", "-v", "time", "-n", "5", "-r", "5120", "-f",path};
//                String cmdString = TextUtils.join(" ", cmd);
//                LogUtils.d("ldl", "launch logcat:" + cmdString);
////                ShellUtils.execCmd(cmd,true);
//
//                ShellUtils.execCmdAsync(cmd, true, new Utils.Consumer<ShellUtils.CommandResult>() {
//                    @Override
//                    public void accept(ShellUtils.CommandResult commandResult) {
//                        LogUtils.d("ldl", commandResult.result + commandResult.successMsg);
//                    }
//                });*/
//                Intent intent = new Intent(MainActivity.this, LogService.class);
//                MainActivity.this.startService(intent);
//            }
//        });
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RabbitMqOption rabbitMqOption = new RabbitMqOption
//                        .RabitMqOptionBuilder("sss")
//                        .password("")
//                        .username("dddds")
//                        .build();
//                ArrayMap arrayMap = new ArrayMap();
//                arrayMap.put("lfl", "aaa");
//                arrayMap.put("device_sn", "老母猪吐泡泡...");
//                arrayMap.put("lfls", "aaddda");
//                arrayMap.put("lfzl", "aaacccccc");
//                RabbitMqParamsOption rabbitMqParamsOption = new RabbitMqParamsOption.RabbitMqParamsOptionBuilder().params(arrayMap)
//                        .build();
//
//                RabbitService.startRabbitService(MainActivity.this, rabbitMqOption, rabbitMqParamsOption
//
//                );
//    /*            try {
//                    Thread.sleep(1000 * 15);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }*/
//
//            }
//        });
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LogUtils.dTag("ldl", "测试打印日志button?");
//                LogUtils.d("ldl", "天王盖地虎", "宝塔镇河妖", "谁要加班谁是狗.");
////                toConnectService();
//            }
//        });
//        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                i++;
//                MqttManager.getInstance().publish(i + "号选手你好");
//                int x = 5 / 0;
//            }
//        });
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MqttAction.RECEOIVER_ACTION);
//        myReceiver = new MyReceiver();
//        mLocalBroadcastManager.registerReceiver(myReceiver, intentFilter);
//        myReceiver.setOnReceiverMessageListener(new MyReceiver.OnReceiverMessageListener() {
//            @Override
//            public void onMessage(String topId, String mqttMessage) {
//                textView.setText(topId + "\n" + mqttMessage);
//            }
//        });
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

    private static final String FILE_SEP = "/";

    private String getPath() {
        String mDefaultDir;
        if (SDCardUtils.isSDCardEnableByEnvironment()
                && Utils.getApp().getExternalFilesDir(null) != null)
//            mDefaultDir = Utils.getApp().getExternalFilesDir(null) + FILE_SEP + "Logs" + FILE_SEP + "systemLog" + FILE_SEP+"abc.txt";
            mDefaultDir = Utils.getApp().getExternalFilesDir(null) + FILE_SEP + "Logs" + FILE_SEP + "systemLog" + FILE_SEP + "abc.txt";
        else {
            mDefaultDir = Utils.getApp().getFilesDir() + FILE_SEP + "Logs" + FILE_SEP + "systemLog" + FILE_SEP + "abc.txt";
        }
        return mDefaultDir;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLocalBroadcastManager.unregisterReceiver(myReceiver);
    }
}