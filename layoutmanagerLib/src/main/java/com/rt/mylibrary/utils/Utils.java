package com.rt.mylibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Hashtable;

public class Utils {

    public static Bitmap createQRCode(String url, float width, float height) {
        int w = (int) width;
        int h = (int) height;
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static MediaPlayer vlcInit(SurfaceView sView, Context context, String mUrl) {

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        LibVLC mLibVLC = new LibVLC(context, args);
        MediaPlayer mMediaPlayer = new MediaPlayer(mLibVLC);

        //init mMediaPlayer
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        if (sView != null) {
            vlcVout.setVideoView(sView);
        }


        vlcVout.attachViews();

        Media media = new Media(mLibVLC, Uri.parse(mUrl));
        media.setHWDecoderEnabled(true, true);
        media.addOption(":no-audio");
        media.addOption(":network-caching=150");
        media.addOption(":file-caching=150");
        media.addOption(":sout-mux-caching=150");
        media.addOption(":live-caching=150");
        mMediaPlayer.setMedia(media);
        mMediaPlayer.getVLCVout().setWindowSize(800, 654);
        mMediaPlayer.setAspectRatio(800 + ":" + 654);
        mMediaPlayer.setScale(0);

        media.release();

        return mMediaPlayer;

    }

    public static void needScale(Context mContext, int width, int height) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if (width != 0 && height != 0) {
            ApiConfig.mScaleX = (float) screenWidth / (float) width;
            ApiConfig.mScaleY = (float) screenHeight / (float) height;
            ApiConfig.mScale = (float) (screenHeight * width) / (float) (width * height);
        }
    }
}
