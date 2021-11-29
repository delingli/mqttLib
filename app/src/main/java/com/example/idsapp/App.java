package com.example.idsapp;

import android.app.Application;
import android.text.TextUtils;

import com.ldl.upanepochlog.api.LogParamsOption;
import com.ldl.upanepochlog.log.LogUtilManager;
import com.ldl.upanepochlog.util.CrashUtils;
import com.ldl.upanepochlog.util.LogUtils;
import com.ldl.upanepochlog.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public
class App extends Application {
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader myBufferReader = new BufferedReader(new FileReader(file));
            String processName = myBufferReader.readLine().trim();
            myBufferReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public boolean isInMainProcess(){
        String procName = getProcessName();
        return !TextUtils.isEmpty(procName) && procName.equals(this.getPackageName());
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        Utils.init(this);ad
//        10.10.20.200:9039

            LogParamsOption logParamsOption = new LogParamsOption.LogParamsOptionBuilder("http://" + "10.10.20.200")//地址ip
                    .port(9039)  //端口
                    .dealyTime(60)  //运行最长时间，
                    .device_sn("3A914B6F3787")//设备id 注意要跟业务中mqtt或者rabbitmq获取的保持一致，服务器做了数据库查询比对;
                    .build();
            LogUtilManager.getInstance().initLog(this, logParamsOption); //初始化

    }
}
