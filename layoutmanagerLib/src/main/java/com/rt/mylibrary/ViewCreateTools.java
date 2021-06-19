package com.rt.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.gongwen.marqueen.SimpleMF;
import com.gongwen.marqueen.SimpleMarqueeView;
import com.google.gson.Gson;
import com.rt.mylibrary.basebean.SimpleResp;
import com.rt.mylibrary.basebean.StyleBean;
import com.rt.mylibrary.fragment.VlcFragment;
import com.rt.mylibrary.utils.Utils;
import com.rt.mylibrary.view.WeatherView;
import com.rt.mylibrary.viewbean.ImageBean;
import com.rt.mylibrary.viewbean.PlayItemBean;
import com.rt.mylibrary.viewbean.QRCodeBean;
import com.rt.mylibrary.viewbean.RunTextBean;
import com.rt.mylibrary.viewbean.TextBean;
import com.rt.mylibrary.viewbean.TimeBean;
import com.rt.mylibrary.viewbean.VideoBean;
import com.rt.mylibrary.viewbean.WeatherBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

public class ViewCreateTools {
    private ThreadPoolExecutor myThreadPool;

    public ViewCreateTools(ThreadPoolExecutor myThreadPool) {
        this.myThreadPool = myThreadPool;
    }

    /*文本*/
    public TextView createTextView(Context mContext, SimpleResp<Object> bean) {
        TextBean textBean = new Gson().fromJson(new Gson().toJson(bean.getData()), TextBean.class);
        TextView mTextView = new TextView(mContext);
        switch (textBean.getStyle().getTextAlign()) {
            case "left":
                mTextView.setGravity(Gravity.START);
                break;
            case "center":
                mTextView.setGravity(Gravity.CENTER);
                break;
            case "right":
                mTextView.setGravity(Gravity.END);
                break;
            default:
                break;
        }
//     todo           mTextView.setTextColor(Color.parseColor(color))
        mTextView.setTextSize(textBean.getStyle().getFontSize());
        mTextView.setLineSpacing(textBean.getStyle().getLineHeight(), 0f);
        mTextView.setLetterSpacing(textBean.getStyle().getLetterSpacing());

//        mTextView.setLayoutParams(new ViewGroup.LayoutParams((int) bean.getStyle().getWidth(), (int) bean.getStyle().getHeight()));
//        mTextView.setX(bean.getStyle().getLeft());
//        mTextView.setY(bean.getStyle().getTop());

        setViewStyle(mTextView, bean);
        return mTextView;
    }

    public TextClock createTextClock(Context mContext, SimpleResp<Object> bean) {
        TimeBean timeBean = new Gson().fromJson(new Gson().toJson(bean.getData()), TimeBean.class);
        TextClock textClock = new TextClock(mContext);
        textClock.setTextSize(timeBean.getStyle().getFontSize());
        textClock.setFormat24Hour(timeBean.getType());

        setViewStyle(textClock, bean);
        return textClock;
    }

    public ImageView createImageView(Context mContext, SimpleResp<Object> bean) {
        ImageView img = new ImageView(mContext);
        setViewStyle(img, bean);
        Glide.with(mContext).asBitmap().load(new Gson().fromJson(
                new Gson().toJson(bean.getData()),
                ImageBean.class
                ).getSrc()
        ).into(img);
        return img;
    }

    public ImageView createQRcode(Context mContext, SimpleResp<Object> bean) {
        ImageView img = new ImageView(mContext);
        setViewStyle(img, bean);
        String url = new Gson().fromJson(new
                        Gson().toJson(bean.getData()),
                QRCodeBean.class
        ).getUrl();
        Bitmap bitmap =
                Utils.createQRCode(url, bean.getStyle().getWidth(), bean.getStyle().getHeight());
        img.setImageBitmap(bitmap);

        return img;

    }

    public void createMusic(Context mContext) throws Exception {
        if (myThreadPool == null) {
            throw new Exception("do not initialization ThreadPool");
        }

        Log.i("MediaPlayLog", "创建音乐");
        final MediaPlayer mediaPlayer = new MediaPlayer();
        myThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource("https://sharefs.yun.kugou.com/202006191819/68c0027d5a60f96fe908c13d72cfc558/G071/M04/0A/09/54YBAFdkQNuAIyAyADrXMKk8lzA358.mp3");
                    mediaPlayer.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                    Log.i("MediaPlayLog", "设置连接");
//                try {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Log.i("MediaPlayLog", "开始播放");
                                    mediaPlayer.start(); // 准备好了就播放
                                }

                            }, 3000);
                        }
                    });

                } catch (Exception e) {
                    Log.e("MediaPlayLog", "播放出错$e");
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("MediaPlayLog", "播放出错" + extra);
                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(
                        "MediaPlayLog",
                        "Progress:" +
                                percent +
                                "%");
            }
        });
    }

    public void createVLC(final Context mContext, SimpleResp<Object> bean, ViewGroup rootView) throws Exception {
        if (myThreadPool == null) {
            throw new Exception("do not initialization ThreadPool");
        }
        VideoBean data = new Gson().fromJson(new Gson().toJson(bean.getData()), VideoBean.class);
        FrameLayout fm = new FrameLayout(mContext);
        setViewStyle(fm, bean);
        final int id = View.generateViewId();
        fm.setId(id);
        rootView.addView(fm);

        final ArrayList<PlayItemBean> list = data.getPlayItemList();
        final int[] tag = {0};// 播放标记
        final int[] temp = {0};//时间中间值
        myThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (temp[0] <= 0) {
                        temp[0] = list.get(tag[0]).getTimer() * 1000;
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                                .replace(
                                        id,
                                        VlcFragment.getInstance("http://vodkgeyttp8.vod.126.net/cloudmusic/MTA1MzMxMzAy/83156654658d5f09a36352637661420e/320484d03272ab6e2b1850f6148ab965.mp4?wsSecret=74564a7ef732412b33d7a83e2d19ed15&wsTime=1592462440")
                                )
                                .commitAllowingStateLoss();
                        tag[0]++;
                        tag[0] %= list.size();

                    } else {
                        temp[0] -= 1000;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    public SimpleMarqueeView<String> createRunText(Context mContext, SimpleResp<Object> baseBean) {
        RunTextBean bean =
                new Gson().fromJson(new Gson().toJson(baseBean.getData()), RunTextBean.class);
        SimpleMarqueeView<String> runText = new SimpleMarqueeView<String>(mContext);
        SimpleMF<String> marqueeFactory = new SimpleMF<String>(mContext);
        marqueeFactory.setData(java.util.Collections.singletonList(bean.getText()));
        runText.setMarqueeFactory(marqueeFactory);
        setViewStyle(runText, baseBean);
        runText.setTextSize(bean.getStyle().getFontSize());
//       todo         setTextColor(Color.parseColor(bean.style.color))
        //todo 行高;
        runText.setTextEllipsize(TextUtils.TruncateAt.END);
        runText.setTextSingleLine(true);
        runText.setTextGravity(Gravity.CENTER);
        runText.setInAnimation(mContext, R.anim.in_right);
        runText.setOutAnimation(mContext, R.anim.in_left);

        runText.startFlipping();

        return runText;
    }

    public WeatherView createWeatherView(Context mConext, SimpleResp<Object> baseBean) {
        WeatherBean weatherBean =
                new Gson().fromJson(new Gson().toJson(baseBean.getData()), WeatherBean.class);
        WeatherView weatherView = new WeatherView(mConext, weatherBean.getArea().get(0));
        setViewStyle(weatherView, baseBean);
        return weatherView;
    }


    private void setViewStyle(View v, SimpleResp<Object> bean) {
        v.setLayoutParams(new ViewGroup.LayoutParams((int) bean.getStyle().getWidth(), (int) bean.getStyle().getHeight()));
        v.setX(bean.getStyle().getLeft());
        v.setY(bean.getStyle().getTop());
    }
}
