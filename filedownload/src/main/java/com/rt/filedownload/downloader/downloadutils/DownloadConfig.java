package com.rt.filedownload.downloader.downloadutils;

import android.content.Context;

import com.rt.filedownload.downloader.callback.DownloadListener;

public class DownloadConfig {
    private String id;//ID
    private String url;
    private String localUrl;
    private String fileName;
    private DownloadListener downloadListener;

    public DownloadConfig setId(String id) {
        this.id = id;
        return this;
    }

    public DownloadConfig() {
    }

    public DownloadConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public DownloadConfig setLocalUrl(String url) {
        this.localUrl = url;
        return this;
    }

    public DownloadConfig setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DownloadConfig setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    public void load(Context context) {
        if (url.isEmpty()) {
            throw new NullPointerException("url is null");
        }
        DownloadUtils.get().setDownloadListener(downloadListener);
        DownloadUtils.get().setFileName(fileName);
        DownloadUtils.get().setUrl(url);
        DownloadUtils.get().setLocalUrl(localUrl);
        DownloadUtils.get().setId(id);
        DownloadUtils.get().createDownload(context);

    }
}
