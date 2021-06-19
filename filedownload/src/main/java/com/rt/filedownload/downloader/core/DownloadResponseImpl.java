package com.rt.filedownload.downloader.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.rt.filedownload.downloader.db.DownloadDBController;
import com.rt.filedownload.downloader.domain.DownloadInfo;
import com.rt.filedownload.downloader.domain.DownloadThreadInfo;
import com.rt.filedownload.downloader.exception.DownloadException;

public class DownloadResponseImpl implements DownloadResponse {

    private static final String TAG = "DownloadResponseImpl";
    private final Handler handler;
    private final DownloadDBController downloadDBController;

    public DownloadResponseImpl(DownloadDBController downloadDBController) {
        this.downloadDBController = downloadDBController;

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DownloadInfo downloadInfo = (DownloadInfo) msg.obj;
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_DOWNLOADING:
                        Log.d("xtf--->","STATUS_DOWNLOADING");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener()
                                    .onDownloading(downloadInfo.getProgress(), downloadInfo.getSize());
                        }

                        break;
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                        Log.d("xtf--->","STATUS_PREPARE_DOWNLOAD");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener().onStart();
                        }
                        break;
                    case DownloadInfo.STATUS_WAIT:
                        Log.d("xtf--->","STATUS_WAIT");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener().onWaited();
                        }
                        break;
                    case DownloadInfo.STATUS_PAUSED:
                        Log.d("xtf--->","STATUS_PAUSED");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener().onPaused();
                        }
                        break;
                    case DownloadInfo.STATUS_COMPLETED:
                        Log.d("xtf--->","STATUS_COMPLETED");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener().onDownloadSuccess(downloadInfo);
                        }
                        //TODO submit next downloadInfo task

                        break;
                    case DownloadInfo.STATUS_ERROR:
                        Log.d("xtf--->","STATUS_ERROR");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener().onDownloadFailed(downloadInfo.getException());
                        }
                        break;
                    case DownloadInfo.STATUS_REMOVED:
                        Log.d("xtf--->","STATUS_REMOVED");
                        if (downloadInfo.getDownloadListener() != null) {
                            downloadInfo.getDownloadListener().onRemoved();
                        }
                        break;
                }
            }
        };


    }

    @Override
    public void onStatusChanged(DownloadInfo downloadInfo) {
        Log.d("xtf--->","5858");
        Log.d("xtf--->",downloadInfo.getStatus()+"");
        if (downloadInfo.getStatus() != DownloadInfo.STATUS_REMOVED) {
            Log.d("xtf--->","5555111");
            downloadDBController.createOrUpdate(downloadInfo);
            if (downloadInfo.getDownloadThreadInfos() != null) {
                Log.d("xtf--->","858585");
                for (DownloadThreadInfo threadInfo : downloadInfo.getDownloadThreadInfos()) {
                    downloadDBController.createOrUpdate(threadInfo);
                }
            }
        }

        Message message = handler.obtainMessage(downloadInfo.getId().hashCode());
        message.obj = downloadInfo;
        message.sendToTarget();

        Log.d(TAG, "progress:" + downloadInfo.getProgress() + ",size:" + downloadInfo.getSize());
    }

    @Override
    public void handleException(DownloadException exception) {
        Log.d("DownloadException", exception.toString());
    }
}
