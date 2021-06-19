package com.rt.filedownload.downloader.untils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

public class FileUtils {
    private static String TAG = "FileUtils";

    public static boolean compMD5(final String path, final String md5Code) {
        final File file = new File(path);
        if (file.exists()) {
            String fileMD5 = getFileMD5(file);
            Log.d(TAG, "Download success file md5: " + fileMD5 + " md5code: " + md5Code + " file path: " + path);
            return (!fileMD5.equals(md5Code));
        }else {
            return false;
        }
    }

    /**
     * 获取单个文件的MD5值！
     * @param file
     * @return
     */
    /**
     * 获取单个文件的MD5值！
     *
     * @param file 文件
     * @return 文件32位MD5
     */

    private static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String strMd5 = bigInt.toString(16);
        if (strMd5.length() < 32) {
            for (int i = 0; i < 32 - strMd5.length(); i++) {
                strMd5 = "0" + strMd5;//首位是0的MD5码补全首位的0
            }
        }
        return strMd5;
    }

}
