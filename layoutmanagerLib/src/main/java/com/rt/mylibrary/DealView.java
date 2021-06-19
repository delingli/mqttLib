package com.rt.mylibrary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rt.mylibrary.basebean.BaseResp;
import com.rt.mylibrary.basebean.PagesBean;
import com.rt.mylibrary.basebean.SimpleResp;
import com.rt.mylibrary.utils.Utils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class DealView {
    public static void dealData(Context mContext, BaseResp bean, final ViewGroup rootView, /*List<SimpleResp> layersList,*/ ThreadPoolExecutor myThreadPool) {
        Glide.with(mContext)
                .load("http://e.hiphotos.baidu.com/zhidao/pic/item/b64543a98226cffc7a951157b8014a90f703ea9c.jpg")
                .into(new CustomViewTarget<ViewGroup, Drawable>(rootView) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        rootView.setBackground(errorDrawable);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        rootView.setBackground(resource);

                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {
                        rootView.setBackground(placeholder);

                    }
                });


        List<PagesBean> list = bean.getPages();
        List<SimpleResp> layersList = list.get(0).getLayers();
        Utils.needScale(
                mContext,
                (int) layersList.get(0).getStyle().getWidth(),
                (int) layersList.get(0).getStyle().getHeight()
        );
        ViewCreateTools tools = new ViewCreateTools(myThreadPool);
        for (int i = 0; i < layersList.size(); i++) {
            Log.i("Layers", layersList.get(i).getType());
            switch (Objects.requireNonNull(layersList.get(i).getType())) {
                case "text":
                    rootView.addView(tools.createTextView(mContext, layersList.get(i)));

                    break;
                case "video":
                    try {
                        tools.createVLC(mContext, layersList.get(i), rootView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case "qrcode":
                    rootView.addView(tools.createQRcode(mContext, layersList.get(i)));

                    break;
                case "img":
                    rootView.addView(tools.createImageView(mContext, layersList.get(i)));

                    break;
                case "weather":
                    rootView.addView(tools.createWeatherView(mContext, layersList.get(i)));

                    break;

                case "run":
                    rootView.addView(tools.createRunText(mContext, layersList.get(i)));

                    break;

                case "btn":

                    break;
                case "music":
                    try {
                        tools.createMusic(mContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case "time":
                    rootView.addView(tools.createTextClock(mContext, layersList.get(i)));
                    break;

                default:
                    break;

            }
        }

    }
}
