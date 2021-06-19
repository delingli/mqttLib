package com.rt.filedownload.downloader.db;


import java.util.List;

import com.rt.filedownload.downloader.domain.DownloadInfo;
import com.rt.filedownload.downloader.domain.DownloadThreadInfo;


public interface DownloadDBController {

    List<DownloadInfo> findAllDownloading();

    List<DownloadInfo> findAllDownloaded();

    DownloadInfo findDownloadedInfoById(String id);

    void pauseAllDownloading();

    void createOrUpdate(DownloadInfo downloadInfo);

    void createOrUpdate(DownloadThreadInfo downloadThreadInfo);

    void delete(DownloadInfo downloadInfo);

    void delete(DownloadThreadInfo download);
}
