package com.rt.mylibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

import com.rt.mylibrary.utils.ApiConfig;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WeatherView extends AppCompatTextView {

    public WeatherView(Context context, String city) {
        super(context);
        initData(city);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initData(String city) {
        ApiConfig.apiServer.getWeather(city).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        setText(s.toString());
                        Log.i("testLoading", "data:" + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("testLoading", "error：" + throwable.toString());
                    }
                });
    }
}
