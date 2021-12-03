package com.ldl.upanepochlog.api;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ldl.upanepochlog.LogApiConstant;
import com.ldl.upanepochlog.threadpool.ScheduledThteadpoolImpl;
import com.ldl.upanepochlog.threadpool.ThreadPoolImpl;
import com.ldl.upanepochlog.util.FileUtils;
import com.ldl.upanepochlog.util.LogUtils;
import com.ldl.upanepochlog.util.SDCardUtils;
import com.ldl.upanepochlog.util.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public
abstract class AbsLogApi implements ILogApi {
    private String TAG = "AbsLogApi";
    private static final String FILE_SEP = System.getProperty("file.separator");
    private String mDefaultDir;//路径
    protected ScheduledThteadpoolImpl miThreadPoolApi;//可执行定时相关
    protected ThreadPoolImpl threadPool;//自定义线程池
    private Process mProcess;
    protected boolean logEnable = false;//默认false
    private static final int MAX_LOG_COUNT = 9;//log 最大数量
    private static final int MAX_LOG_SIZE = 1024 * 1024 * 5;//最大Mb
    private int deayTime = 120;//默认值

    //    private static final int MAX_LOG_SIZE = 1024 * 5;
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

    @Override
    public String getUploadUrl() {
        return uploadPath;
    }

    public void enableLog(boolean logEnable) {
        this.logEnable = logEnable;
    }

    protected LogParamsOption logParamsOption;
    protected String uploadPath;
    protected String IP_PORT;

    @Override
    public void setLogParamsOption(LogParamsOption logParamsOption) {
        this.logParamsOption = logParamsOption;
        fillUploadPath();
    }

    public String getUploadPath() {
        return uploadPath;
    }

    private void fillUploadPath() {
        if (null != logParamsOption) {
            if (TextUtils.isEmpty(logParamsOption.getIp())) {
                new IllegalArgumentException("ip 参数不能为空");
                return;
            }
            if (logParamsOption.getPort() <= 0) {
                new IllegalArgumentException("port 参数不合法");
                return;
            }
            this.IP_PORT = logParamsOption.getIp().trim() + ":" + logParamsOption.getPort();
            this.uploadPath = this.IP_PORT + LogApiConstant.API_END.trim();
        } else {
            new IllegalArgumentException("logParamsOption 参数不能为空");
        }
    }

    protected onUploadListener onUploadListener;

    public interface onUploadListener {
        void onUpLoadSucess();

        void onUpLoadError();
    }


    @Override
    public void init() {
        //1.初始化路径
        mDefaultDir = getDirs() + "systemLog" + FILE_SEP + DateFormat.getDateInstance().format(new Date());//日期
        //2.初始化线程池
        miThreadPoolApi = new ScheduledThteadpoolImpl();
        miThreadPoolApi.init();
        //开启时候开始计算，2小时内服务自动触发上传流程，并且通知服务器状态改变
        if (null != logParamsOption && logParamsOption.getDealyTime() >= 5) {
            deayTime = logParamsOption.getDealyTime();
            LogUtils.dTag(TAG, "延迟时间:" + deayTime);
        }
        miThreadPoolApi.getScheduledThreadPoolExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                //todo 触发上传功能，
                submit(new OnCallBack() {
                    @Override
                    public void onSucess() {
                        if (null != onUploadListener) {
                            onUploadListener.onUpLoadSucess();
                        }
                    }

                    @Override
                    public void onError() {
                        if (null != onUploadListener) {
                            onUploadListener.onUpLoadError();
                        }
                    }
                });
            }
        }, deayTime, TimeUnit.MINUTES);
        threadPool = new ThreadPoolImpl();//线程池
        threadPool.init();
    }

    protected void closeProcess() {
        stopLogcat();
    }

    private String lastPath;
    protected long fileCount = 5;
    protected long fileSize = 5120;

    @Override
    public void startLogcat() {   //测试抓取设备日志
        if (null != miThreadPoolApi && logEnable) {
            LogUtils.getConfig().setLog2FileSwitch(logEnable);
            threadPool.executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (logEnable) {
                            Log.d(TAG, "Start Logcat process in background.");
                            String path = getPathString();
//                                if (!TextUtils.isEmpty(lastPath) && !lastPath.equals(path)) {
//                                    mProcess.destroy();
//                                }
                            lastPath = path;
                            LogUtils.d("ldl", path);
                            //String[] cmd = new String[]{"nohup", "logcat", "-v", "time", "-n", "5", "-r", "5120", "-f", filePath, "&"};
                            String[] cmd = new String[]{"logcat", "-v", "time", "-n", fileCount + "", "-r", fileSize + "", "-f", path};
                            String cmdString = TextUtils.join(" ", cmd);
                            Log.d(TAG, "launch logcat:" + cmdString);
                            mProcess = Runtime.getRuntime().exec(cmd);
//                            mProcess.destroy();
//                            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
//                            BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
                        }
//                        String str = inputStreamReader.readLine();
//                        System.out.println(str);
//                        System.out.println(errorStreamReader.readLine());
                        //todo 对外提供观察者回调

            /*System.out.println(errorStreamReader.readLine());
            System.out.println(errorStreamReader.readLine());
            System.out.println(errorStreamReader.readLine());

            /*process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmdString + "\n");
            *//*os.writeBytes("exit\n");*//*
            os.flush();
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String str = inputStreamReader.readLine();
            System.out.println(str);
            System.out.println(errorStreamReader.readLine());*/

            /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                }
            });
        /*    miThreadPoolApi.getScheduledThreadPoolExecutor().scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {

                }
            }, 0, 1000 * 3, TimeUnit.MILLISECONDS);*/
        }


    }


    @Override
    public void stopLogcat() {
        this.logEnable = false;
        enableLog(logEnable);//关闭logcat关闭运行时日志
        LogUtils.getConfig().setLog2FileSwitch(logEnable);
        Process p = null;
        try {
            if(null!=mProcess){
                p = Runtime.getRuntime().exec("su -c kill " + getTinyCapPID(mProcess));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG,"stopLogcat异常...");

        } finally {
            if (null != mProcess) {
                mProcess.destroy();
                mProcess = null;
            }
            if (null != p) {
                p.destroy();
                p = null;
            }
        }


    }

    @Override
    public void destory() {
        if (null != mProcess) {
            Process p = null;
            try {
                p = Runtime.getRuntime().exec("su -c kill " + getTinyCapPID(mProcess));
//                    Process p=Runtime.getRuntime().exec("kill -2 " + getTinyCapPID(mProcess));
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(TAG,"销毁异常...");
            }
            if (null != mProcess) {
                mProcess.destroy();
                mProcess = null;
            }
            if (null != p) {
                p.destroy();
            }

        }
        if (null != miThreadPoolApi) {
            miThreadPoolApi.dismiss();
        }
        if (null != threadPool) {
            threadPool.closeThread();
        }
    }

    public String getTinyCapPID(Process psProcess) {
        DataOutputStream out = new DataOutputStream(psProcess.getOutputStream());
        InputStream is = psProcess.getInputStream();
        try {
            out.writeBytes("ps | grep 'tinycap' | cut -c 10-15\n");
            out.writeBytes("ps\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.writeBytes("exit\n");
            out.flush();
            psProcess.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String re = "";
        try {
            if (is.read() != 0) {
                byte firstByte = (byte) is.read();
                int available = is.available();
                byte[] characters = new byte[available + 1];
                characters[0] = firstByte;
                is.read(characters, 1, available);
                re = new String(characters);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.dTag(TAG,"查询的进程pid:"+re);
        return re;
    }

    private String getLastLogFileName(String dir, String logName) {
        String returnFileName = String.format("%s1.txt", logName);
        File file = new File(dir);//路径
        if (file.exists()) {
            String[] fileArray = file.list();
            if (fileArray != null && fileArray.length > 0) {
                List<String> logList = new ArrayList<>();
                for (String s : fileArray) {
                    if (s.startsWith(logName)) {
                        logList.add(s);
                    }
                }
                if (!logList.isEmpty()) {
//                    Collections.sort(logList);
                    String lastFileName = logList.get(logList.size() - 1);
                    if (new File(String.format("%s/%s", dir, lastFileName)).length() > MAX_LOG_SIZE) {
                        if (lastFileName.contains(".")) {
                            int LastLogCount = Integer.valueOf(lastFileName.split("\\.")[0].replaceAll(logName, "").trim());
                            Log.d("LogUtils", "LastLogCount: " + LastLogCount);
                            if (LastLogCount > MAX_LOG_COUNT) {
                                try {
                                    FileUtils.delete(file);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("LogUtils", "getLastLogFileName: ", e);
                                }
                                returnFileName = String.format("%s1.txt", logName);
                            } else {
                                LastLogCount++;
                                returnFileName = String.format("%s%s.txt", logName, LastLogCount);
                            }
                        }
                    } else {
                        returnFileName = String.format("%s", lastFileName);
                    }
                }
            }
        }
        return returnFileName;
    }

    @NonNull
    private String getPathString() throws IOException {
//        String pathz = mDefaultDir + FILE_SEP + getLastLogFileName(mDefaultDir, "system");
        String pathz = mDefaultDir + FILE_SEP + "system.txt";
        File file = new File(pathz);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file.getAbsolutePath();
    }
}
