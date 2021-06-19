package com.rt.filedownload.downloader;

import android.content.Context;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rt.filedownload.downloader.callback.DownloadManager;
import com.rt.filedownload.downloader.config.Config;
import com.rt.filedownload.downloader.core.DownloadResponse;
import com.rt.filedownload.downloader.core.DownloadResponseImpl;
import com.rt.filedownload.downloader.core.DownloadTaskImpl;
import com.rt.filedownload.downloader.core.task.DownloadTask;
import com.rt.filedownload.downloader.db.DefaultDownloadDBController;
import com.rt.filedownload.downloader.db.DownloadDBController;
import com.rt.filedownload.downloader.domain.DownloadInfo;

public final class DownloadManagerImpl implements DownloadManager, DownloadTaskImpl.DownloadTaskListener {

    private static final int MIN_EXECUTE_INTERVAL = 500;
    private static DownloadManagerImpl instance;
    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, DownloadTask> cacheDownloadTask;
    private final List<DownloadInfo> downloadingCaches;
    private final Context context;

    private final DownloadResponse downloadResponse;
    private final DownloadDBController downloadDBController;
    private final Config config;
    private long lastExecuteTime;

    private DownloadManagerImpl(Context context, Config config) {
        this.context = context;
        if (config == null) {
            this.config = new Config();
        } else {
            this.config = config;
        }

        if (this.config.getDownloadDBController() == null) {
            downloadDBController = new DefaultDownloadDBController(context, this.config);
        } else {
            downloadDBController = this.config.getDownloadDBController();
        }

        if (downloadDBController.findAllDownloading() == null) {
            downloadingCaches = new ArrayList<>();
        } else {
            downloadingCaches = downloadDBController.findAllDownloading();
        }

        cacheDownloadTask = new ConcurrentHashMap<>();

        downloadDBController.pauseAllDownloading();

        executorService = Executors.newFixedThreadPool(this.config.getDownloadThread());

        downloadResponse = new DownloadResponseImpl(downloadDBController);
    }

    public static DownloadManager getInstance(Context context, Config config) {
        synchronized (DownloadManagerImpl.class) {
            if (instance == null) {
                instance = new DownloadManagerImpl(context, config);
            }
        }
        return instance;
    }


    @Override
    public void download(DownloadInfo downloadInfo) {
        try {
            if (downloadInfo != null){
                downloadingCaches.add(downloadInfo);
                prepareDownload(downloadInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void prepareDownload(DownloadInfo downloadInfo) {
        try{
            Log.d("xtf--->","1");
            if (downloadInfo == null)return;
            Log.d("xtf--->","2");
            if (cacheDownloadTask.size() >= config.getDownloadThread()) {
                Log.d("xtf--->","3");
                downloadInfo.setStatus(DownloadInfo.STATUS_WAIT);
                downloadResponse.onStatusChanged(downloadInfo);
            } else {
                Log.d("xtf--->","4");
                DownloadTaskImpl downloadTask = new DownloadTaskImpl(executorService, downloadResponse,
                        downloadInfo, config, this);
                Log.d("xtf--->","45");
                cacheDownloadTask.put(downloadInfo.getId(), downloadTask);
                Log.d("xtf--->","456");
                downloadInfo.setStatus(DownloadInfo.STATUS_PREPARE_DOWNLOAD);
                Log.d("xtf--->","4567");
                downloadResponse.onStatusChanged(downloadInfo);
                Log.d("xtf--->","45678");
                downloadTask.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void pause(DownloadInfo downloadInfo) {
        try{
            if (isExecute() && downloadInfo != null) {
                pauseInner(downloadInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void prepareDownloadNextTask() {
        try{
            for (DownloadInfo downloadInfo : downloadingCaches) {
                if (downloadInfo != null  && downloadInfo.getStatus() == DownloadInfo.STATUS_WAIT) {
                    prepareDownload(downloadInfo);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void resume(DownloadInfo downloadInfo) {
        try{
            if (isExecute() && downloadInfo != null) {
                prepareDownload(downloadInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void remove(DownloadInfo downloadInfo) {
        try{
            if (downloadInfo != null){
                downloadInfo.setStatus(DownloadInfo.STATUS_REMOVED);
                cacheDownloadTask.remove(downloadInfo.getId());
                downloadingCaches.remove(downloadInfo);
                downloadDBController.delete(downloadInfo);
                downloadResponse.onStatusChanged(downloadInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void destroy() {
        instance=null;
    }

    @Override
    public DownloadInfo getDownloadById(String id) {
        DownloadInfo downloadInfo = null;
        try{
            Log.d("DownloadManagerImpl","id:"+id+" \n downloadingCaches size: " +downloadingCaches.size());
            if (downloadingCaches.size()>0){
                for (DownloadInfo d : downloadingCaches) {
                    Log.d("DownloadManagerImpl","downloadingCaches id: " + d.getId());
                    if (d != null && d.getId().equals(id)) {
                        downloadInfo = d;
                        break;
                    }else {
                        continue;
                    }
                }
            }

            if (downloadInfo == null) {
                downloadInfo = downloadDBController.findDownloadedInfoById(id);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return downloadInfo;
    }

    @Override
    public List<DownloadInfo> findAllDownloading() {
        return downloadingCaches;
    }

    @Override
    public List<DownloadInfo> findAllDownloaded() {
        return downloadDBController.findAllDownloaded();
    }

    @Override
    public DownloadDBController getDownloadDBController() {
        return downloadDBController;
    }

    @Override
    public void resumeAll() {

        try{
            if (isExecute()) {
                for (DownloadInfo downloadInfo : downloadingCaches) {
                    if (downloadInfo != null)
                        prepareDownload(downloadInfo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void pauseAll() {
        try{
            if (isExecute()) {
                for (DownloadInfo downloadInfo : downloadingCaches) {
                    if (downloadInfo != null)pauseInner(downloadInfo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void pauseInner(DownloadInfo downloadInfo) {

        try{
            if (downloadInfo != null){
                downloadInfo.setStatus(DownloadInfo.STATUS_PAUSED);
                cacheDownloadTask.remove(downloadInfo.getId());
                downloadResponse.onStatusChanged(downloadInfo);
                prepareDownloadNextTask();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onDownloadSuccess(DownloadInfo downloadInfo) {

        try{
            if (downloadInfo != null){
                cacheDownloadTask.remove(downloadInfo.getId());
                downloadingCaches.remove(downloadInfo);
                prepareDownloadNextTask();
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public boolean isExecute() {
        if (System.currentTimeMillis() - lastExecuteTime > MIN_EXECUTE_INTERVAL) {
            lastExecuteTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }


}
