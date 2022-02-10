package com.itc.commoncomponent.network

import android.util.Log
import com.itc.commoncomponent.network.interceptor.CommonParamsInterceptor
import com.itc.commoncomponent.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val DEFAULT_TIME_OUT = 30L
    const val Base_URL = "http://10.10.20.12:7777"

    //    var loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(object : Logger() {
//        fun log(message: String) {
//            //打印retrofit日志
//            Log.i("RetrofitLog", "retrofitBack = $message")
//        }
//    })
    val okHttpClient = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(HeaderInterceptor())
//        .addInterceptor(loggingInterceptor)
        .addInterceptor(CommonParamsInterceptor())
        .retryOnConnectionFailure(true);
    val retrofit = Retrofit.Builder()
        .baseUrl(Base_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())//支持字符串
        .client(okHttpClient.build())
        .build()
//    val mmService=retrofit.create()

}