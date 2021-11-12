package com.example.idsapp;

import android.app.Application;

import com.ldl.upanepochlog.api.LogParamsOption;
import com.ldl.upanepochlog.log.LogUtilManager;
import com.ldl.upanepochlog.util.CrashUtils;
import com.ldl.upanepochlog.util.LogUtils;
import com.ldl.upanepochlog.util.Utils;

public
class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Utils.init(this);
//        10.10.20.200:9039
        LogParamsOption logParamsOption = new LogParamsOption.LogParamsOptionBuilder("http://10.10.20.200")
                .port(9039)
                .dealyTime(5)
                .device_sn("abcdefg")
                .build();
        LogUtilManager.getInstance().initLog(this, logParamsOption);
    }
}
