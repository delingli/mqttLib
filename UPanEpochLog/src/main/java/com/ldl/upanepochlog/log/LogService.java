package com.ldl.upanepochlog.log;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import com.ldl.upanepochlog.R;
import com.ldl.upanepochlog.api.AbsLogApi;
import com.ldl.upanepochlog.api.ILogApi;
import com.ldl.upanepochlog.api.LogApi;
import com.ldl.upanepochlog.util.LogUtils;

public class LogService extends Service implements AbsLogApi.onUploadListener {
    private static String key_opt = "opt";
    private static String KEY_FILE_SIZE = "key_file_size";
    private static String KEY_FILE_COUNT = "key_file_count";
    private static String key_switch = "switch_logs";
    private static final String TAG = "LogService";
    private ILogApi mLogApi;
    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setCustomNotification();
        LogUtilManager.getInstance().setLogApi(mLogApi = new LogApi(this));//创建初始化
        LogUtils.dTag(TAG, "服务开启");
        mLogApi.init();
        mLogApi.enableLog(true);
        mLogApi.startLogcat();//开启异步执行
    }


    public static void startServices(Context context) {
        Intent intent = new Intent(context, LogService.class);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
//            context.startService(intent);
//        }
        context.startService(intent);
    }

    public static void startServices(Context context, @ServiceOpt.ServiceOpts int opt) {
        Intent intent = new Intent(context, LogService.class);
        intent.putExtra(key_opt, opt);
        context.startService(intent);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
//            context.startService(intent);
//        }
    }

    public static void startServices(Context context, int fileCount, int fileSize) {
        Intent intent = new Intent(context, LogService.class);
        intent.putExtra(KEY_FILE_SIZE, fileSize);
        intent.putExtra(KEY_FILE_COUNT, fileCount);
        context.startService(intent);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
//            context.startService(intent);
//        }
    }


    public static void stopServices(Context context) {
        Intent intent = new Intent(context, LogService.class);
        context.stopService(intent);
    }

    private String CHANNEL_ID = "chandidss";

    private void setNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel;
            notificationChannel = new NotificationChannel(CHANNEL_ID, getPackageName(), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("日志收集服务")
                    .setContentText("日志收集进行中...")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.app_itc)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("日志收集服务")//设置标题
                    .setContentText("日志收集进行中...")//设置内容
                    .setWhen(System.currentTimeMillis())//设置创建时间
                    .setSmallIcon(R.mipmap.app_itc)//设置状态栏图标
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_itc))//设置通知栏图标
                    .build();
        }
        startForeground(1, notification);
    }
    final int NOTIFICATION_ID = 12234;
    private void setCustomNotification() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel;
            notificationChannel = new NotificationChannel(CHANNEL_ID, getPackageName(), NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(notificationChannel);
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("日志收集服务")
                    .setContentText("日志收集进行中...")
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.app_itc)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("日志收集服务")//设置标题
                    .setContentText("日志收集进行中...")//设置内容
                    .setWhen(System.currentTimeMillis())//设置创建时间
                    .setSmallIcon(R.mipmap.app_itc)//设置状态栏图标
                    .setAutoCancel(false)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_itc))//设置通知栏图标
                    .build();
        }
        mNotificationManager.notify(NOTIFICATION_ID,notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handlerLogFileConfig(intent);
            handlerOpt(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handlerLogFileConfig(Intent intent) {
        int file_size = intent.getIntExtra(KEY_FILE_SIZE, -1);
        int file_count = intent.getIntExtra(KEY_FILE_COUNT, -1);
        if (null != mLogApi && file_count != -1 && file_size != -1) {
            mLogApi.configLogFile(file_size, file_count);
        }
    }

    private void handlerOpt(Intent intent) {
        int opts = intent.getIntExtra(key_opt, -1);
        switch (opts) {
            case ServiceOpt.START:
                if (null != mLogApi) {
                    mLogApi.startLogcat();
                }
                break;
            case ServiceOpt.STOP:
                if (null != mLogApi) {
                    mLogApi.stopLogcat();
                }
                break;
            case ServiceOpt.SUBMIT:
                if (null != mLogApi) {
                    mLogApi.submit(new ILogApi.OnCallBack() {
                        @Override
                        public void onSucess() {
                            mLogApi.deleteFile();
                            LogUtils.eTag(TAG, "上传成功了关闭服务...");
//                            stopForeground(true);
                            stopSelf();
                        }

                        @Override
                        public void onError() {
                            LogUtils.eTag(TAG, "提交上传失败，啥也不做...");
                            //不动
                        }
                    });
                }
                break;
            case ServiceOpt.CLEARALLLOG:
                if (null != mLogApi) {
                    mLogApi.clearAllLog();
                }
                break;
            case ServiceOpt.GET_LOGS_SPACE:
                if (null != mLogApi) {
                    mLogApi.getLogSpace();
                }
                break;
            case ServiceOpt.CLEARAPPLOG:
                if (null != mLogApi) {
                    mLogApi.clearAppLog();
                }
                break;
            case ServiceOpt.CLEARANRLOG:
                if (null != mLogApi) {
                    mLogApi.clearAnrLog();
                }
                break;
            case ServiceOpt.CLEARCRASHLOG:
                if (null != mLogApi) {
                    mLogApi.clearCrashLog();
                }
                break;
            case ServiceOpt.CLEARSYSTEMLOG:
                if (null != mLogApi) {
                    mLogApi.clearSystemLog();
                }
                break;
        }
    }


    @Override
    public void onDestroy() {
//        stopForeground(true);
        mNotificationManager.cancel(NOTIFICATION_ID);
        if (null != mLogApi) {
            mLogApi.notifyDeviceState(new ILogApi.OnCallBack() {
                    @Override
                    public void onSucess() {
                        LogUtils.dTag(TAG, "服务在onddestory中执行成功通知服务器改状态");
                    }

                    @Override
                    public void onError() {
                        LogUtils.dTag(TAG, "服务在onddestory中执行失败");

                    }
                });
            mLogApi.stopLogcat();
            mLogApi.destory();
        }

        super.onDestroy();
    }


    @Override
    public void onUpLoadSucess() {
        //这里上传成功后就开始关闭自己通器知远程
        LogUtils.dTag(TAG, "2小时机制触发，主动上传成功去通知服务器修改状态");
        toCloseService();
    }

    @Override
    public void onUpLoadError() {
        LogUtils.dTag(TAG, "2小时机制触发，主动上传失败了去通知服务器修改状态");
        toCloseService();
    }

    private void toCloseService() {
        if (mLogApi != null) {
            mLogApi.notifyDeviceState(new ILogApi.OnCallBack() {
                @Override
                public void onSucess() {
                    LogUtils.dTag(TAG, "通知服务器修改状态Sucess");
                    stopSelf();
//                    stopForeground(true);
                }

                @Override
                public void onError() {
                    LogUtils.dTag(TAG, "通知服务器修改状态Error");
                    stopSelf();
//                    stopForeground(true);
                }
            });
        }
    }
}