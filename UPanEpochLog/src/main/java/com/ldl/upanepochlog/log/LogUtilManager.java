package com.ldl.upanepochlog.log;

import static com.ldl.upanepochlog.log.ServiceOpt.CLEARALLLOG;
import static com.ldl.upanepochlog.log.ServiceOpt.CLEARANRLOG;
import static com.ldl.upanepochlog.log.ServiceOpt.CLEARAPPLOG;
import static com.ldl.upanepochlog.log.ServiceOpt.CLEARCRASHLOG;
import static com.ldl.upanepochlog.log.ServiceOpt.CLEARSYSTEMLOG;
import static com.ldl.upanepochlog.log.ServiceOpt.ENABLELOG;
import static com.ldl.upanepochlog.log.ServiceOpt.GET_LOGS_SPACE;
import static com.ldl.upanepochlog.log.ServiceOpt.START;
import static com.ldl.upanepochlog.log.ServiceOpt.STOP;
import static com.ldl.upanepochlog.log.ServiceOpt.SUBMIT;

import android.app.Application;

import com.ldl.upanepochlog.api.ILogApi;
import com.ldl.upanepochlog.api.LogParamsOption;
import com.ldl.upanepochlog.util.CrashUtils;
import com.ldl.upanepochlog.util.LogUtils;
import com.ldl.upanepochlog.util.Utils;

public
class LogUtilManager {
    private static final LogUtilManager ourInstance = new LogUtilManager();

    public static LogUtilManager getInstance() {
        return ourInstance;
    }

    private ILogApi logApi;
    private LogParamsOption logParamsOption;

    private LogUtilManager() {
    }

    public void setLogApi(ILogApi logApi) {
        this.logApi = logApi;
        if (null != logParamsOption) {
            logApi.setLogParamsOption(logParamsOption);
        }
    }

    private Application app;

    /**
     * 全局初始化log；可以收集应用线程崩溃问题，这是全局捕获异常的处理
     *
     * @param application
     */
    public void initLog(Application application, LogParamsOption logParamsOption) {
        this.app = application;
        this.logParamsOption = logParamsOption;
        Utils.init(application);
        CrashUtils.init();
    }

    /**
     * 开启系统 日志收集和运行时日志收集，
     *
     * @param switchLog
     */
    public void setAppLogSwitch(boolean switchLog,int fileCount,int fileSize) {
        LogUtils.getConfig().setLog2FileSwitch(switchLog);
        if (switchLog) {
            LogService.startServices(app,fileCount,fileSize);
        } else {
            LogService.stopServices(app);
        }


    }

    public void startLogcat() {//异步的
        if (null != logApi) {
            LogService.startServices(app, START);
        }
    }

    public void stopLogcat() {
        if (null != logApi) {
            LogService.startServices(app, STOP);
        }
    }

    public void submit() {
        if (null != logApi) {
            LogService.startServices(app, SUBMIT);
        }
    }
    public void getLogSpace() {
        if (null != logApi) {
            LogService.startServices(app, GET_LOGS_SPACE);
        }
    }
    public void clearAllLog() {
        if (null != logApi) {
            LogService.startServices(app, CLEARALLLOG);
        }
    }
    @Deprecated
    public void configLogFileConfig(int fileCount,int fileSize) {
        if (null != logApi) {
            LogService.startServices(app, fileCount,fileSize);
        }
    }
    public void clearAppLog() {
        if (null != logApi) {
            LogService.startServices(app, CLEARAPPLOG);
        }
    }

    public void clearAnrLog() {
        if (null != logApi) {
            LogService.startServices(app, CLEARANRLOG);

        }
    }

    public void clearCrashLog() {
        if (null != logApi) {
            LogService.startServices(app, CLEARCRASHLOG);
        }
    }

    public void clearSystemLog() {
        if (null != logApi) {
            LogService.startServices(app, CLEARSYSTEMLOG);
        }
    }

    public void enableLog(boolean enable) {
        if (null != logApi) {
            logApi.enableLog(enable);
        }
    }
}
