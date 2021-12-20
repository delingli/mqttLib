package com.ldl.upanepochlog.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


import com.ldl.upanepochlog.R;
import com.ldl.upanepochlog.log.LogService;
import com.ldl.upanepochlog.log.LogUtilManager;
import com.ldl.upanepochlog.util.LogUtils;
import com.ldl.upanepochlog.util.SDCardUtils;
import com.ldl.upanepochlog.util.Utils;
import com.ldl.upanepochlog.util.ZipUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogSetActivity extends AppCompatActivity implements View.OnClickListener {
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_set);
        mSwitch = findViewById(R.id.switchLog);
        findViewById(R.id.btn_clearApp).setOnClickListener(this);
        findViewById(R.id.btn_crash).setOnClickListener(this);
        findViewById(R.id.btn_anrlog).setOnClickListener(this);
        findViewById(R.id.btn_clearSystem).setOnClickListener(this);
        findViewById(R.id.btn_clearAll).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        ActivityManager mActivityManager =
                (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);
        boolean state = MusicServiceIsStart(mServiceList, "com.ldl.upanepochlog.log.LogService");
        mSwitch.setChecked(state);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtilManager.getInstance().setAppLogSwitch(isChecked,10,10);
            }
        });
    }

    //通过Service的类名来判断是否启动某个服务
    private boolean MusicServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList, String className) {
        for (int i = 0; i < mServiceList.size(); i++) {
            if (className.equals(mServiceList.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //获取所有启动的服务的类名
    private String getServiceClassName(List<ActivityManager.RunningServiceInfo> mServiceList) {
        String res = "";
        for (int i = 0; i < mServiceList.size(); i++) {
            res += mServiceList.get(i).service.getClassName() + " \n";
        }

        return res;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.btn_clearApp) {
            LogUtilManager.getInstance().setAppLogSwitch(true,10,10);

//            LogUtilManager.getInstance().clearAppLog();
        } else if (id == R.id.btn_crash) {
            LogUtilManager.getInstance().clearCrashLog();
        } else if (id == R.id.btn_anrlog) {
            LogUtilManager.getInstance().clearAnrLog();

        } else if (id == R.id.btn_clearSystem) {
            LogUtilManager.getInstance().clearSystemLog();
        } else if (id == R.id.btn_clearAll) {
            LogUtilManager.getInstance().configLogFileConfig(4,10);
        } else if (id == R.id.btn_submit) {
            LogUtilManager.getInstance().submit();
        }

    }

    private static final String FILE_SEP = System.getProperty("file.separator");

    private File getOutLogname(File f) {
        Date d = new Date();
        SimpleDateFormat sbf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String log = sbf.format(d) + "-" + "logs.zip";
        String ss = f.getAbsolutePath() + File.separator + log.trim();
        return new File(ss);
    }

    public String getDirs() {
        String dir;
        if (SDCardUtils.isSDCardEnableByEnvironment()
                && Utils.getApp().getExternalFilesDir(null) != null)
            dir = Utils.getApp().getExternalFilesDir(null) + FILE_SEP + "Logs" + FILE_SEP;
        else {
            dir = Utils.getApp().getFilesDir() + FILE_SEP + "Logs" + FILE_SEP;
        }
        return dir;
    }
}