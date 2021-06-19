package com.rt.mylibrary.http;

/**
 * Created by lei on 2017/11/1.
 */

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 拦截器
 * <p>
 * 向请求体里添加公共参数
 * Created by lei on 17/11/08.
 */

public class HttpCommonInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder newRequest = originRequest.newBuilder();
        System.out.println(originRequest.url());
        Map<String, String> commonParams = ParamsUtils.getCommonParams();
        for (Map.Entry<String, String> entry : commonParams.entrySet()) {
            newRequest.addHeader(entry.getKey(), entry.getValue());
        }
        return chain.proceed(newRequest.build());
    }


}