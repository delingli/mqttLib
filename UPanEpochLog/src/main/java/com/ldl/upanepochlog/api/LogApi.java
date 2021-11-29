package com.ldl.upanepochlog.api;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ldl.upanepochlog.LogApiConstant;
import com.ldl.upanepochlog.util.FileUtils;
import com.ldl.upanepochlog.util.LogUtils;
import com.ldl.upanepochlog.util.Utils;
import com.ldl.upanepochlog.util.ZipUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public
class LogApi extends AbsLogApi {
    private String TAG = "LogApi";
    private String deleteOutFileDir=null;
    public LogApi(onUploadListener onUploadListener) {
    this.onUploadListener=onUploadListener;
    }

    @Override
    public void deleteFile() {
        File f = new File(getDirs());
        if (f.exists()) {
            FileUtils.delete(f);
            LogUtils.dTag(TAG, "删除本地成功");
        }
        if(!TextUtils.isEmpty(deleteOutFileDir)){
            FileUtils.delete(deleteOutFileDir);
            LogUtils.dTag(TAG, "删除本地zip成功");
        }
    }

    @Override
    public void getLogSpace() {
        this.logEnable = false;
        LogUtils.getConfig().setLog2FileSwitch(logEnable);
        closeProcess();
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    long space = FileUtils.getLength(getDirs());
                    LogUtils.dTag(TAG, "查询文件大小为:" + space);
                    //todo 调用上传接口
                    OkGo.<String>
                            post(IP_PORT + LogApiConstant.API_SET_LOG_SIZE)
                            .tag(Utils.getApp())
                            .isMultipart(false)
                            .params("device_sn", logParamsOption.getDeviceSn())
                            .params("log_file_size", space + "").
                            execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                  if(response.isSuccessful()){
                                      LogUtils.dTag(TAG, "文件大小通知成功");
                                      //开启继续收集
                                      logEnable = true;
                                      LogUtils.getConfig().setLog2FileSwitch(logEnable);
                                      startLogcat();
                                  }
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    response.getException().printStackTrace();
                                    LogUtils.dTag(TAG, "文件大小通知失败");
                                    logEnable = true;
                                    LogUtils.getConfig().setLog2FileSwitch(logEnable);
                                    startLogcat();
                                }
                            });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public void configLogFile(int fileSize, int fileCount) {
        this.fileCount = fileCount;
        this.fileSize = fileSize * 1024;
        startLogcat();
    }

    @Override
    public void notifyDeviceState(OnCallBack onCallBack) {
        OkGo.<String>
                post(IP_PORT + LogApiConstant.DEVICE_SET_LOG_STATUS)
                .tag(Utils.getApp())
                .isMultipart(false)
                .params("device_sn", logParamsOption.getDeviceSn()).
                execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.dTag(TAG, "通知关闭设备状态成功");
                        if(null!=onCallBack){
                            onCallBack.onSucess();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.eTag(TAG, "通知关闭设备状态失败");
                        if(null!=onCallBack){
                            onCallBack.onError();
                        }
                    }
                });

    }


    @Override
    public void submit(OnCallBack onCallBack) {
        this.logEnable = false;
        //理应关闭运行时日志写文件操作
        LogUtils.getConfig().setLog2FileSwitch(logEnable);
        closeProcess();
        //todo 打包上传log
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String outPath = null;
                    File f = new File(getDirs());
                    if (f.exists()) {
                        File outFile = getOutLogname(f);
                        //todo 查询是否 有未上传的文件
                        File[] flist = f.getParentFile().listFiles();
                        if (flist != null && flist.length > 0) {
                            for (File ff : flist) {
                                if (ff.exists() && ff.isFile() && ff.getName().endsWith(".zip")) {
                                    outPath = ff.getAbsolutePath();
                                    LogUtils.dTag(TAG, "开始上传上传失败的遗留zip");
                                    break;
                                }
                            }

                        }
                        if (outPath == null) { //说明没有遗留文件,就压缩文件
//                            boolean b=   ZipUtils.zipFile(f.getAbsolutePath(),outFile.getAbsolutePath());
                            boolean b = ZipUtils.zipFile(f, outFile);
                            outPath = outFile.getAbsolutePath();
                            LogUtils.dTag(TAG, "压缩打包成功:开始上传"+b);
                        }

                        LogUtils.dTag(TAG, "上传文件全路径" + outPath);
                  deleteOutFileDir=outPath;
                        OkGo.<String>
                                post(getUploadUrl())
                                .tag(Utils.getApp())
                                .isMultipart(false)
                                .params("device_sn", logParamsOption.getDeviceSn())
                                .params("run_log_file", new File(outPath)).
                                execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        LogUtils.dTag(TAG, "上传成功回调...");
                                        if (null != onCallBack) {
                                            onCallBack.onSucess();
                                        }
                                    }

                                    @Override
                                    public void uploadProgress(Progress progress) {
                                        super.uploadProgress(progress);
//                                                String currentSize = Formatter.formatFileSize(LogService.this, progress.currentSize);
//                                                String totalSize = Formatter.formatFileSize(LogService.this, progress.totalSize);
//                                                LogUtils.dTag(TAG, "当前上传的大小" + currentSize + "总大小:" + totalSize);
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        super.onError(response);
                                        LogUtils.eTag(TAG, "上传失败回调...");
                                        if (null != onCallBack) {
                                            onCallBack.onError();
                                        }
                                    }
                                });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @NonNull
    private File getOutLogname(File f) {
        Date d = new Date();
        SimpleDateFormat sbf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String log = sbf.format(d) + "-" + "logs.zip";
        String ss = f.getParent()+ File.separator + log.trim();
        return new File(ss);
    }

    @Override
    public void clearAllLog() {
        this.logEnable = false;
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {
                closeProcess();
                File file = new File(getDirs());
                if (file.exists() && file.isDirectory()) {
                    //存在
                    FileUtils.delete(file);
                    logEnable = true;
                    startLogcat();
                }

            }
        });


    }

    @Override
    public void clearAppLog() {
        this.logEnable = false;
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {
                closeProcess();
                File file = new File(getDirs());
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            Log.d(TAG, "文件名:" + f.getName());
                            if (f.isDirectory() && f.getName().equals("AppLog")) {
                                FileUtils.delete(f);
                                //先关闭写入相关的，删除文件后在恢复，服务不会停止
                                logEnable = true;
                                startLogcat();
                            }
                        }
                    }

                }

            }
        });


    }

    @Override
    public void clearAnrLog() {
        this.logEnable = false;
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {

                closeProcess();
                File file = new File(getDirs());
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            if (f.isDirectory() && f.getName().equals("anrLog")) {
                                FileUtils.delete(f);
                                logEnable = true;
                                startLogcat();
                            }
                        }
                    }

                }

            }
        });


    }

    @Override
    public void clearCrashLog() {
        this.logEnable = false;
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {
                closeProcess();
                File file = new File(getDirs());
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            if (f.isDirectory() && f.getName().equals("crash")) {
                                FileUtils.delete(f);
                                logEnable = true;
                                startLogcat();
                            }
                        }
                    }

                }

            }
        });


    }

    @Override
    public void clearSystemLog() {
        this.logEnable = false;
        threadPool.executeTask(new Runnable() {
            @Override
            public void run() {
                closeProcess();
                File file = new File(getDirs());
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            if (f.isDirectory() && f.getName().equals("systemLog")) {
                                FileUtils.delete(f);
                                logEnable = true;
                                startLogcat();
                            }
                        }
                    }

                }

            }
        });


    }

    @Override
    public void enableLog(boolean enable) {
        this.logEnable = enable;
    }
}
