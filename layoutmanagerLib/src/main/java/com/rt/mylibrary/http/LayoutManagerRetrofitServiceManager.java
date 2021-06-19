package com.rt.mylibrary.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//import okhttp3.OkHttpClient;
//
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lei on 2017/10/30.
 */
public class LayoutManagerRetrofitServiceManager {
    private static final int DEFAULT_TIME_OUT = 5;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 10;
    private static Retrofit mRetrofit;

    private LayoutManagerRetrofitServiceManager() {

    }

    public static void init(String baseUrl) {
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        // 添加公共参数拦截器
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor();
        builder.addInterceptor(commonInterceptor);

        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    private static class SingletonHolder {
        private static final LayoutManagerRetrofitServiceManager INSTANCE = getInstance();
    }

    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static LayoutManagerRetrofitServiceManager getInstance() {

        if (SingletonHolder.INSTANCE == null)
            return new LayoutManagerRetrofitServiceManager();
        else
            return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}

