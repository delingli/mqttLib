package com.rt.filedownload.downloader.downloadutils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.rt.filedownload.downloader.DownloadService;
import com.rt.filedownload.downloader.callback.DownloadListener;
import com.rt.filedownload.downloader.callback.DownloadManager;
import com.rt.filedownload.downloader.domain.DownloadInfo;

import java.io.File;

public class DownloadUtils {
    private final String TAG = this.getClass().getSimpleName();
    private String id;//ID
    private String url;
    private String localUrl;
    private String fileName;
    private DownloadListener downloadListener;

    private static DownloadUtils downloadUtils = null;

    public static DownloadConfig init() {
        return DownloadUtils.get().getDownloadConfig();
    }

    public static synchronized DownloadUtils get() {
        if (downloadUtils == null) {
            downloadUtils = new DownloadUtils();
        }
        return downloadUtils;
    }

    private DownloadConfig getDownloadConfig() {
        return new DownloadConfig();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocalUrl(String localUrl){
        this.localUrl = localUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public static DownloadUtils getDownloadUtils() {
        return downloadUtils;
    }

    public void createDownload(Context context) {
        Log.i(TAG, "该文件走入下载:" + id);
        DownloadInfo downloadInfo = DownloadService.getDownloadManager(context).getDownloadById(id);
        if (downloadInfo == null) {//不存在下载任务
            Log.i(TAG, "该文件正常下载:" + id);
            startDownload(context);
        } else {
            switch (downloadInfo.getStatus()) {
                case DownloadInfo.STATUS_NONE:
                case DownloadInfo.STATUS_PAUSED:
                case DownloadInfo.STATUS_ERROR:
                    Log.i(TAG, "STATUS_NONE||STATUS_PAUSED||STATUS_ERROR");
                    startDownload(context);
                    //resume downloadInfo
                    //  downloadManager.resume(downloadInfo);
//                    createDownload(url, fileName);
                    break;
                case DownloadInfo.STATUS_DOWNLOADING:
                    Log.i(TAG, "STATUS_DOWNLOADING");
                    break;
                case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    Log.i(TAG, "STATUS_PREPARE_DOWNLOAD");
                    //print downloadInfo
                    break;
                case DownloadInfo.STATUS_COMPLETED:
                    Log.i(TAG, "STATUS_COMPLETED");
                    //downloadManager.remove(downloadInfo);
                    break;
                case DownloadInfo.STATUS_REMOVED:
                    Log.i(TAG, "STATUS_REMOVED");
                    break;
                case DownloadInfo.STATUS_WAIT:
                    Log.i(TAG, "STATUS_WAIT");
                    break;
                default:
                    break;

            }
        }

    }

    private void startDownload(Context context) {
        if (url.isEmpty()) {
            return;
        }
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), localUrl);
        if (!dir.exists()) {
            boolean res = dir.mkdirs();
            if (res)
                Log.d(TAG, "Prepare downloading，createDownload mkdir success:" + dir.getAbsolutePath());
            else
                Log.d(TAG, "Prepare downloading，createDownload mkdir fail:" + dir.getAbsolutePath());
        } else {
            Log.d(TAG, "Prepare downloading，folder already exist:" + dir.getAbsolutePath());
        }
        String path = dir.getAbsolutePath().concat("/").concat(fileName);
        /*String path = dir.getAbsolutePath().concat("/").concat(fileName);*/
        Log.d(TAG, "Prepare downloading file to: " + path);

        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(url).setId(fileName)
                .setPath(path)
                .build();

        downloadInfo.setId(id);
        downloadInfo.setDownloadListener(downloadListener);

        DownloadService.getDownloadManager(context).download(downloadInfo);

    }

    public static void removeInfo(Context context, String id) {
        DownloadManager downloadManager = DownloadService.getDownloadManager(context);
        DownloadInfo downloadInfo = downloadManager.getDownloadById(id);
        downloadManager.remove(downloadInfo);
    }

    public void removeDownload(Context context, String id) {
        DownloadManager downloadManager = DownloadService.getDownloadManager(context);
        DownloadInfo downloadInfo = downloadManager.getDownloadById(id);
        if (downloadInfo == null) return;
        switch (downloadInfo.getStatus()) {
            case DownloadInfo.STATUS_NONE:
            case DownloadInfo.STATUS_PAUSED:
            case DownloadInfo.STATUS_ERROR:
            case DownloadInfo.STATUS_DOWNLOADING:
            case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                downloadManager.pause(downloadInfo);
                File file = new File(downloadInfo.getPath());
                if (file.exists()) {
                    file.delete();
                }
                downloadInfo.setRetryTime(0);
                downloadManager.remove(downloadInfo);
                break;
            case DownloadInfo.STATUS_COMPLETED:
                break;
            default:
                break;
        }
        return;
    }

    public DownloadInfo getDownloadInfo(Context context, String id) {
        DownloadManager downloadManager = DownloadService.getDownloadManager(context);
        DownloadInfo downloadInfo = downloadManager.getDownloadById(id);
        return downloadInfo;
    }

}
