package com.rt.filedownload.downloader.callback;

import com.rt.filedownload.downloader.domain.DownloadInfo;
import com.rt.filedownload.downloader.exception.DownloadException;

public interface DownloadListener {

    void onStart();

    void onWaited();

    void onPaused();

    void onDownloading(long progress, long size);

    void onRemoved();

    void onDownloadSuccess(DownloadInfo info);

    void onDownloadFailed(DownloadException e);
}
