package com.ldl.upanepochlog.api;

public interface ILogApi {
    void init();

    void deleteFile();

    void getLogSpace();

    void configLogFile(int fileSize, int fileCount);

    void setLogParamsOption(LogParamsOption logParamsOption);

    String getUploadUrl();

    void notifyDeviceState(OnCallBack onCallBack);

    void startLogcat();

    void stopLogcat();

    void submit(OnCallBack onCallBack);

    void clearAllLog();

    void clearAppLog();

    void clearAnrLog();

    void clearCrashLog();

    void clearSystemLog();

    void enableLog(boolean enable);

    void destory();

    interface OnCallBack {
        void onSucess();
        void onError();
    }
}
