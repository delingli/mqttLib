package com.example.idsapp

import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.johnson.arcface2camerax.nativeface.NewNativeFaceCameraView
class SecondActivity : AppCompatActivity(), ViewTreeObserver.OnGlobalLayoutListener {
    private val textView: TextView? = null
    var i = 0
    private val button3: Button? = null
    private val button4: Button? = null
    var mNaticeFaceCameraView: NewNativeFaceCameraView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*       activeFaceEngine(
                   this,
                   GlobaConstant.APP_ID,
                   GlobaConstant.SDK_KEY
               )*/
        mNaticeFaceCameraView = findViewById<NewNativeFaceCameraView>(R.id.faceView)
//        removeOnGlobalLayoutListener
        mNaticeFaceCameraView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        mNaticeFaceCameraView?.addOnBackFaceFeatureListener(object :
            NewNativeFaceCameraView.OnBackFaceFeatureListener {
            override fun onReceive(trackId: Int, result: ByteArray?) {
                Log.d("OOP", "trackId" + trackId + ":ByteArray:" + result)
            }
        })
        //在布局结束后才做初始化操作
//        mNaticeFaceCameraView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        /*        mNaticeFaceCameraView.setBackFaceFeatureListener {
                it?.let {
                    Looper.prepare()
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }

                LogUtils.dTag("Aldl", "得到了" + it.toString())
            }*/
//        lifecycle.addObserver(mNaticeFaceCameraView)
        //        mNaticeFaceCameraView.setBackFaceFeatureListener();
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


    override fun onDestroy() {
        super.onDestroy()
        //        mLocalBroadcastManager.unregisterReceiver(myReceiver);
    }

    companion object {
        private const val FILE_SEP = "/"
    }

    override fun onGlobalLayout() {
        mNaticeFaceCameraView?.getViewTreeObserver()?.removeOnGlobalLayoutListener(this);
        mNaticeFaceCameraView?.initEngine(DetectFaceOrientPriority.ASF_OP_ALL_OUT)
        mNaticeFaceCameraView?.initCamera(windowManager)
    }


}