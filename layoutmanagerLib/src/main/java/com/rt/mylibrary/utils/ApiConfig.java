package com.rt.mylibrary.utils;

import com.rt.mylibrary.http.ApiServer;
import com.rt.mylibrary.http.LayoutManagerRetrofitServiceManager;

public class ApiConfig {
    public static float mScaleX = 1;
    public static float mScaleY = 1;
    public static float mScale = 1;

    public static String BASE_URL = "http://empty.com/";

    public static ApiServer apiServer = LayoutManagerRetrofitServiceManager.getInstance().create(ApiServer.class);
}
