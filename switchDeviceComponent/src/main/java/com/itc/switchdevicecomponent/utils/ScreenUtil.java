package com.itc.switchdevicecomponent.utils;


import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.spirit.SpiritSysCtrl;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.itc.switchdevicecomponent.annation.DeviceType;
import com.itc.switchdevicecomponent.receiver.SceenOffAdminReceiver;
import com.ys.rkapi.MyManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import startest.ys.com.poweronoff.PowerOnOffManager;


public class ScreenUtil {
    public int height;
    public int width;
    private Context context;
    private static ScreenUtil instance;
    private final String TAG = ScreenUtil.class.getSimpleName();

    public ScreenUtil(Context context) {
        this.context = context;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    public static ScreenUtil getInstance(Context context) {
        if (instance == null) {
            instance = new ScreenUtil(context);
        }
        return instance;
    }

    /**
     * 取消安卓盒子的定时开关机
     */
    public void cancelAndroidHeZiPowerOnOff() {
        SpiritSysCtrl spiritSysCtrl = SpiritSysCtrl.getInstance(Utils.getApp());
        spiritSysCtrl.cancelAlarmPoweroff();
        spiritSysCtrl.cancelAlarmPoweron();
    }

    /**
     * 安卓盒子的定时开关机
     */
    public void openAndroidHeZiPowerOnOff(String powerOffTime, long timer) {
        LogUtils.dTag(TAG, "关机时间:" + powerOffTime);
        LogUtils.dTag(TAG, "开机时间间隔:" + timer);
        String day = TimeUtils.getNowString(new SimpleDateFormat("dd"));
        LogUtils.dTag(TAG, "当前的日期为:" + day);
        String[] powerOffTimeArray = powerOffTime.split(":");
        for (String s : powerOffTimeArray) {
            LogUtils.dTag(TAG, "ss == " + s);
        }
        if (powerOffTimeArray.length == 3) {
            SpiritSysCtrl.getInstance(Utils.getApp()).setAlarmPoweroff(Integer.parseInt(day), Integer.parseInt(powerOffTimeArray[0]), Integer.parseInt(powerOffTimeArray[1]));
        }
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = sdfs.parse(DateUitils.getCurrentStringDate() + powerOffTime);
            long startTimes = parse.getTime();
            long times = startTimes + timer * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss"); //设置格式
            String powerOnTime = format.format(times);
            LogUtils.dTag(TAG, "开机时间为:" + powerOnTime);
            String[] powerOnTimeArray = powerOnTime.split(":");
            int i = SpiritSysCtrl.getInstance(Utils.getApp()).setAlarmPoweron(Integer.parseInt(powerOnTimeArray[2]), Integer.parseInt(powerOnTimeArray[3]), Integer.parseInt(powerOnTimeArray[4]));
            LogUtils.dTag(TAG, "定时开关机设置结果:" + i);
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.dTag(TAG, "开关机时间转化出错:" + e.getMessage());
        }
    }

    /**
     * @param context
     * @param start
     * @param timer
     */
    //星神,星马定时开关机
    public void setBootTimeForXingMaAndXingShen(Context context, String start, long timer) {
        String date = DateUitils.getCurrentStringDate().trim();
        PowerOnOffManager powerOnOffManager = PowerOnOffManager.getInstance(context);
        try {
            int[] timeoffArray = new int[5];
            String[] dateSplit = date.split("-");
            timeoffArray[0] = Integer.parseInt(dateSplit[0]);
            timeoffArray[1] = Integer.parseInt(dateSplit[1]);
            timeoffArray[2] = Integer.parseInt(dateSplit[2]);
            String[] startSplit = start.split(":");
            timeoffArray[3] = Integer.parseInt(startSplit[0]);
            timeoffArray[4] = Integer.parseInt(startSplit[1]);

            int[] timeonArray = new int[5];
            SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = null;

            String startTime = DateUitils.getCurrentStringDate() + start;
            try {
                parse = sdfs.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            LogUtils.eTag(TAG, "XingMa shutDownTime=" + parse.toString());
            //开机时间
            long time = parse.getTime() + timer * 1000;
            Date date2 = new Date(time);
            String format = sdfs.format(date2);

            String[] onTime = format.split(" ");
            String[] onDate = onTime[0].split("-");
            String[] onHms = onTime[1].split(":");

            timeonArray[0] = Integer.parseInt(onDate[0]);
            timeonArray[1] = Integer.parseInt(onDate[1]);
            timeonArray[2] = Integer.parseInt(onDate[2]);
            timeonArray[3] = Integer.parseInt(onHms[0]);
            timeonArray[4] = Integer.parseInt(onHms[1]);
            LogUtils.eTag(TAG, "XingMa 关机时间：" + Arrays.toString(timeoffArray) + "，XingMa 开机时间：" + Arrays.toString(timeonArray));
            powerOnOffManager.setPowerOnOff(timeonArray, timeoffArray);
        } catch (Exception e) {
            LogUtils.eTag(TAG, Log.getStackTraceString(e));
        }
    }


    /**
     * 得到手机屏幕的宽度, pix单位
     */
    public int getScreenWidth() {
        return width;
    }

    /**
     * 得到手机屏幕的宽度, pix单位
     */
    public int getScreenHeight() {
        return height;
    }

    public void screenOn(Context context, @DeviceType.DeviceType String deviceType) {
        switch (deviceType) {
            case DeviceType.MODULE_HRA: {
                Intent intent = new Intent("com.hra.setDeviceSleeporWakeup");
                intent.putExtra("power_key", true);
                context.sendBroadcast(intent);
                break;
            }
            case DeviceType.MODULE_LITIJI: {
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream out = new DataOutputStream(
                            process.getOutputStream());
                    out.writeBytes("echo 0 > sys/class/backlight/rk28_bl/bl_power \n");
                    out.writeBytes("exit\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                //亮屏
                PowerManager mPowerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "tag");
                mWakeLock.acquire();
                mWakeLock.release();
                break;
            }
        }
    }

    public boolean screenOff(Context context, @DeviceType.DeviceType String deviceType) {
        switch (deviceType) {
            case DeviceType.MODULE_HRA: {
                Intent intent = new Intent("com.hra.setDeviceSleeporWakeup");
                intent.putExtra("power_key", false);
                context.sendBroadcast(intent);
                break;
            }
            case DeviceType.MODULE_LITIJI: {
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream out = new DataOutputStream(
                            process.getOutputStream());
                    out.writeBytes("echo 1 > sys/class/backlight/rk28_bl/bl_power \n");
                    out.writeBytes("exit\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            default: {
                DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName adminReceiver = new ComponentName(context, SceenOffAdminReceiver.class);
                boolean admin = policyManager.isAdminActive(adminReceiver);
                if (!admin) {
                    return false;
                }
                policyManager.lockNow();
                break;
            }
        }
        return true;
    }


    //重启设备
    public void start_reboot(Context context, @DeviceType.DeviceType String deviceType) {
        LogUtils.eTag(TAG, "start_reboot");
        switch (deviceType) {
            case DeviceType.MODULE_ANDROID_HE_ZI: {
                SpiritSysCtrl.getInstance(Utils.getApp()).systemReset();
                break;
            }
            case DeviceType.MODULE_JINGXIN: {
                context.sendBroadcast(new Intent("reboot_system"));
                break;
            }
            case DeviceType.MODULE_CW: {
                MyManager manager = MyManager.getInstance(context);
                manager.reboot();
                break;
            }
            default: {
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream out = new DataOutputStream(
                            process.getOutputStream());
                    out.writeBytes("reboot \n");
                    out.writeBytes("exit\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    //给系统发送定时重启的闹铃
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendRebootBroad(Context context, String timers, int taskType, boolean isCancel) {
        LogUtils.eTag(TAG, "设备重启的时间" + timers);
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            parse = sdfs.parse(timers);
            long times = parse.getTime();
            Intent intent = new Intent("Screen");
            intent.putExtra("taskType", taskType);
            LogUtils.eTag(TAG, "设备重启的时间2" + times);
            PendingIntent pi = PendingIntent.getBroadcast(context, (int) times, intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(pi);
            if (!isCancel) {
                am.setExact(AlarmManager.RTC_WAKEUP, times, pi);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.eTag(TAG, "设备重启的时间3");
        }
    }

    public void getScreenType(Activity mactivity) {
        /*int screenType = PrefUtils.getInt(UiUtils.getContext(), "screenType", 0);
        if (screenType == 0) {
            // 设置为横屏模式  
            mactivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mactivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }*/
    }

    public boolean isScreenOrientationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public void setShutdownForHra(Context context, String timer) {
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(timer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long times = parse.getTime();
        Intent intent = new Intent("com.hra.setShutdownDate");
        intent.putExtra("key", times);
        LogUtils.eTag(TAG, "华瑞安要关机了 " + times);
        context.sendBroadcast(intent);
    }

    public void setBootTimeForHra(Context context, String timer, long delay) {
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(timer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.eTag(TAG, "华瑞安设置开机时间:" + timer);
        long times = parse.getTime() + delay * 1000;
        LogUtils.eTag(TAG, "华瑞安设置开机时间+延迟后" + times);

        Intent intent = new Intent("com.hra.setBootDate");
        intent.putExtra("key", times);
        context.sendBroadcast(intent);
    }

    public void setJingxinShutdown(Context context, String timer, long delay, boolean isOpen) {
        LogUtils.eTag(TAG, "setJingxinShutdown");
        Intent intent = new Intent("android.intent.action.gz.setpoweronoff");
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(timer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTimes = parse.getTime();
        long times = parse.getTime() + delay * 1000;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm"); //设置格式
        String openTimeText = format.format(times);
        if (openTimeText.contains("-")) {
            String[] split = openTimeText.split("-");
            int[] time = {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4])};
            intent.putExtra("timeon", time);
            LogUtils.eTag(TAG, Arrays.toString(time));
        }
        String startText = format.format(startTimes);
        LogUtils.eTag(TAG, "startText " + startText + ";openTimeText=" + openTimeText);
        if (startText.contains("-")) {
            String[] split = startText.split("-");
            int[] time = {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4])};
            intent.putExtra("timeoff", time);
            LogUtils.eTag(TAG, Arrays.toString(time));
        }
        intent.putExtra("enable", isOpen);
        context.sendBroadcast(intent);
    }

    public void startShutDownForHra(Context context) {
        LogUtils.eTag(TAG, "华瑞安startShutDown ");

        Intent intent = new Intent("com.hra.setAutoShutdown");
        intent.putExtra("key", true);
        context.sendBroadcast(intent);
    }

    public void startBootForHra(Context context) {
        LogUtils.eTag(TAG, "华瑞安开启 ");

        Intent intent = new Intent("com.hra.setAutoBoot");
        intent.putExtra("key", true);
        context.sendBroadcast(intent);


    }

    public void endHraShutDown(Context context) {
        Intent intent = new Intent("com.hra.setAutoShutdown");
        intent.putExtra("key", false);
        context.sendBroadcast(intent);

    }

    public void endBoot(Context context) {
        Intent intent = new Intent("com.hra.setAutoBoot");
        intent.putExtra("key", false);
        context.sendBroadcast(intent);
    }

    public void setBootTimeForShenHaiReboot(Context context, String shutDownTime, long bootTime, boolean isOpen) {
        if (shutDownTime.contains(" ")) {
            String[] strArr = shutDownTime.split(" ");
            shutDownTime = strArr[1];
        }
        LogUtils.eTag(TAG, "关机时间1:" + shutDownTime + "  开机时间1:" + bootTime);
        String[] split = shutDownTime.trim().split(":");
        StringBuilder timeShutdown = new StringBuilder(DateUitils.getCurrentStringDate().trim());
        for (int i = 0; i < split.length - 1; i++) {
            timeShutdown.append("-").append(split[i]);
        }
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse;

        try {
            parse = sdfs.parse(DateUitils.getCurrentStringDate() + shutDownTime);
            LogUtils.eTag("ScreenUtils", DateUitils.getCurrentStringDate() + shutDownTime);
            long times = parse.getTime() + bootTime * 1000;

            Date date = new Date(times);
            String format = sdfs.format(date);
            String s1 = format.replaceAll(" ", "-");
            String s2 = s1.replaceAll(":", "-");
            String[] split1 = s2.split("-");
            StringBuilder bootTimes = new StringBuilder();
            for (int i = 0; i < split1.length - 1; i++) {
                if (i == 0) {
                    bootTimes = new StringBuilder(split1[0]);
                } else {
                    bootTimes.append("-").append(split1[i]);
                }
            }
            LogUtils.eTag(TAG, "关机时间2:" + timeShutdown.toString() + "  开机时间2:" + bootTimes.toString());
            Intent intent = new Intent("android.intent.action.setpoweronoff");
            String bootTimesStr = bootTimes.toString();
            String timeShutdownTimesStr = timeShutdown.toString();
            String[] bootOnArray = bootTimesStr.split("-");
            String[] bootOffArray = timeShutdownTimesStr.split("-");
            int[] timeOnArray = new int[5];//下次开机具体日期时间
            int[] timeOffArray = new int[5]; //下次关机具体日期时间
            for (int index = 0; index < bootOnArray.length; index++) {
                timeOnArray[index] = Integer.parseInt(bootOnArray[index]);
                LogUtils.eTag(TAG, "开机时间:" + timeOnArray[index]);
            }
            for (int i = 0; i < bootOffArray.length; i++) {
                timeOffArray[i] = Integer.parseInt(bootOffArray[i]);
                LogUtils.eTag(TAG, "关机时间:" + timeOffArray[i]);
            }
            intent.putExtra("timeon", timeOnArray);
            intent.putExtra("timeoff", timeOffArray);
            intent.putExtra("enable", isOpen); //使能开关机功能，设为false,则为关闭
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP); //仅8.1需要添加此行代码
            context.sendBroadcast(intent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

//    public  void shenHaiReboot(String shutDownTime, long bootTime,String isOpen) {
//        LogUtils.eTag("ScreenUtil","关机时间1:" + shutDownTime + "  开机时间1:" + bootTime);
//        String[] split = shutDownTime.trim().split(":");
//        StringBuilder timeShutdown = new StringBuilder(DateUitils.getCurrentStringDate().trim());
//        for (int i = 0; i < split.length - 1; i++) {
//            timeShutdown.append("-").append(split[i]);
//        }
//        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date parse = null;
//
//        try {
//            parse = sdfs.parse(DateUitils.getCurrentStringDate() + shutDownTime);
//            long times = parse.getTime() + bootTime * 1000;
//
//            Date date = new Date(times);
//            String format = sdfs.format(date);
//            String s1 = format.replaceAll(" ", "-");
//            String s2 = s1.replaceAll(":", "-");
//            String[] split1 = s2.split("-");
//            StringBuilder bootTimes = new StringBuilder();
//            for (int i = 0; i < split1.length - 1; i++) {
//                if (i == 0) {
//                    bootTimes = new StringBuilder(split1[0]);
//                } else {
//                    bootTimes.append("-").append(split1[i]);
//                }
//            }
//            ArrayList<String> data = new ArrayList<String>();
//            data.add(isOpen); // 功能开关 true/false
//            data.add("date");
//            data.add(bootTimes.toString()); // 开机时间  data.add("2018-07-27-17-26");
//            data.add(timeShutdown.toString()); // 关机时间
//            LogUtils.eTag("ScreenUtil","关机时间2:" + timeShutdown.toString() + "  开机时间2:" + bootTimes.toString());
//            Intent intent = new Intent("com.ostar.schedule.config.set");
//            intent.putStringArrayListExtra("data", data);
//            UiUtils.getContext().sendBroadcast(intent);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 华科厂家安卓盒子的关机指令
     *
     * @param shutDownTime
     */
    public void setShutdownForHuaKe(Context context, String shutDownTime) {
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(shutDownTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = parse.getTime();
        LogUtils.eTag(TAG, "华科关机时间" + time);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent shutdown = new Intent("android.timerswitch.run_power_off");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, shutdown, PendingIntent.FLAG_CANCEL_CURRENT);
        //am.set，定时关机，第二个参数为关机机时间（年月日时分秒转换后的毫秒值）
        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);    //1秒后关机
    }

    /**
     * 华科厂家安卓盒子开机指令
     *
     * @param shutDownTime
     * @param bootTime
     */
    @SuppressLint("WrongConstant")
    public void setBootTimeForHuaKe(Context context, String shutDownTime, long bootTime) {


        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(shutDownTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = parse.getTime() + bootTime * 1000;
        LogUtils.eTag(TAG, "华科开机时间" + time);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent poweron = new Intent("android.timerswitch.run_power_on");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, poweron, PendingIntent.FLAG_CANCEL_CURRENT);

        //am.set，定时开机，第一个参数必须为4，第二个参数为开机时间（年月日时分秒转换后的毫秒值）
        am.set(4, time, pendingIntent);   //当前系统时间+60s开机
    }


    public void setBootTimeFor0830(String shutDownTime, long bootTime) {
        LogUtils.eTag(TAG, "0830 Task start setTime");
        String saveHour = "";
        String saveMinute = "";

        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(shutDownTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.eTag(TAG, "shutDownTime=" + parse.toString());
        long time = parse.getTime() + bootTime * 1000;
        Date date = new Date(time);
        String format = sdfs.format(date);
        LogUtils.eTag(TAG, "opentime=" + format);
        String s = format.replaceAll(" ", "-");
        String s1 = s.replaceAll(":", "-");
        String[] split = s1.split("-");
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);
        int hour = Integer.parseInt(split[3]);
        int minute = Integer.parseInt(split[4]);
        int second = Integer.parseInt(split[5]);

        setTime("[key]");
        for (int i = 0; i < split.length; i++) {
            switch (i) {
                case 0:
                    setTime("year=" + split[0]);
                    break;
                case 1:
                    setTime("month=" + month);
                    break;
                case 2:
                    setTime("day=" + day);
                    break;
                case 3:
                    saveHour = "hour=" + hour;
                    setTime("hour=" + hour);
                    break;
                case 4:
                    saveMinute = "minute=" + minute;
                    setTime("minute=" + minute);
                    break;
                case 5:
                    setTime("second=" + second);
                    break;

            }
        }
        setTime("[poll]");
        setTime(saveHour);
        setTime(saveMinute);
        setTime("monday=0");
        setTime("tuesday=0");
        setTime("wednesday=0");
        setTime("thursday=0");
        setTime("friday=0");
        setTime("saturday=0");
        setTime("sunday=0");
    }


    private void setTime(String message) {
        LogUtils.dTag("---", "写入文件得开机消息" + message);
        try {
            //todo： 路径有问题
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/autoshutdown.ini");
            FileOutputStream fos = null;
            if (!file.exists()) {
                file.createNewFile();//如果文件不存在，就创建该文件
                fos = new FileOutputStream(file);//首次写入获取
            } else {
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
            }

            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件

            osw.write(message);
            //每写入一个Map就换一行
            osw.write("\r\n");
            //写入完成关闭流
            osw.close();
            LogUtils.eTag("0830Task", "end write");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.eTag("0830Task", "write error:" + e.toString());
        }
    }


    //给系统发送定时重启的闹铃
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setShutdownFor0830(Context context, String timers, int taskType, boolean isCancel) {
        LogUtils.eTag(TAG, "0830B设备关机得时间" + timers);
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            parse = sdfs.parse(timers);
            long times = parse.getTime();
            Intent intent = new Intent("Screen");
            intent.putExtra("taskType", taskType);
            PendingIntent pi = PendingIntent.getBroadcast(context, (int) times, intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (isCancel) {
                am.cancel(pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, times, pi);
            }

        } catch (ParseException e) {

            LogUtils.eTag(TAG, "0830B set error:" + e.toString());
            e.printStackTrace();
        }
    }


    public void set0830BootCancel() {
        LogUtils.eTag(TAG, "0830开始删除已存的文件");
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/autoshutdown.ini");
        if (file.exists()) {
            file.delete();
            LogUtils.eTag(TAG, "0830开始删除已存的文件完成");

        }
    }

    //发送系统息屏时间
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendScreenOff(Context context, String startTime, boolean isCancel) {

        long time = 0;
        LogUtils.eTag("TimerTask", "send timer sceenOn");
        //拼接当前日期和时间
        String currentStringDate = DateUitils.getCurrentStringDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = sdf.parse(currentStringDate + startTime);
            time = parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.eTag(TAG, "发送系统息屏时间" + time);
        Intent intent = new Intent("Screen");
        intent.putExtra("taskType", 3);
        intent.putExtra("taskId", 1);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) time, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isCancel) {
            am.cancel(pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendScreenOn(Context context, String startTime, long timer, boolean isCancel) {
        long time = 0;
        LogUtils.eTag("TimerTask", "send timer sceenOn");
        //拼接当前日期和时间
        String currentStringDate = DateUitils.getCurrentStringDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = sdf.parse(currentStringDate + startTime);
            time = parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long times = time + timer * 1000;
        LogUtils.eTag(TAG, "系统亮屏时间" + times);
        Intent intent = new Intent("Screen");
        intent.putExtra("taskType", 3);
        intent.putExtra("taskId", 0);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) times, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isCancel) {
            am.cancel(pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, times, pi);
        }


    }

    public void setCW(Context context, String offTimer, long durationTimer) {
        LogUtils.eTag(TAG, "cwTask start setTime");
        String saveHour = "";
        String saveMinute = "";

        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;

        try {
            parse = sdfs.parse(offTimer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.eTag(TAG, "shutDownTime=" + parse.toString());
        //开机时间
        long time = parse.getTime() + durationTimer * 1000;
        Date date = new Date(time);
        String format = sdfs.format(date);
        LogUtils.eTag(TAG, "opentime=" + format);
        String s = format.replaceAll(" ", "-");
        String s1 = s.replaceAll(":", "-");
        String[] split = s1.split("-");
        int[] inton = arrayToint(split);


        //关机时间
        Date onDate = new Date(parse.getTime());
        String formatOn = sdfs.format(onDate);
        LogUtils.eTag(TAG, "cwTask closetime=" + formatOn);
        String s3 = formatOn.replaceAll(" ", "-");
        String s4 = s3.replaceAll(":", "-");
        String[] split1 = s4.split("-");
        int[] intoff = arrayToint(split1);

        MyManager instance = MyManager.getInstance(context);
        instance.setPowerOnOff(inton, intoff);

        for (int value : inton) {
            LogUtils.eTag(TAG, "cw open timer " + value);

        }

        for (int value : intoff) {
            LogUtils.eTag(TAG, "cw close timer " + value);
        }


    }


    private int[] arrayToint(String[] lists) {
        try {
            if (lists != null) {
                int[] temp = new int[lists.length - 1];

                for (int i = 0; i < lists.length - 1; i++) {

                    temp[i] = Integer.parseInt(lists[i]);
                }
                return temp;
            }

        } catch (Exception e) {

        }

        return null;
    }


}