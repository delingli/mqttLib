package com.itc.commoncomponent.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加共有参数
 */
public class CommonParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //1.获取到request
        Request request = chain.request();
        //2.获取到方法
        String method = request.method();
        if (method.equals("GET")) {

        } else if (method.equals("POST")) {

        }
        return chain.proceed(request);
    }
}
