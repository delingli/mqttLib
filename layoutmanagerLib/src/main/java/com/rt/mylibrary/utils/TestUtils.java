package com.rt.mylibrary.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.rt.mylibrary.basebean.BaseResp;

public class TestUtils {
    public static BaseResp jsonFormat(String json) {
        BaseResp baseResp;
        try {
            baseResp = new Gson().fromJson(json, BaseResp.class);
        } catch (Exception e) {
            Log.e("TestUtils", e.toString());
            baseResp = null;
        }
        return baseResp;
    }
}
