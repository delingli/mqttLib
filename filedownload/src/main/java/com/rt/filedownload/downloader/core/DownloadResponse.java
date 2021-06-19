package com.rt.filedownload.downloader.core;

import com.rt.filedownload.downloader.domain.DownloadInfo;
import com.rt.filedownload.downloader.exception.DownloadException;

public interface DownloadResponse {

    void onStatusChanged(DownloadInfo downloadInfo);

    void handleException(DownloadException exception);
}
