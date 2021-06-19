package com.rt.filedownload.downloader.untils;

public interface FileListener {
    /**
     * md5校验成功
     */
    void MD5CheckSuccess();

    /**
     * md5校验失败
     */
    void MD5CheckFail();
}
