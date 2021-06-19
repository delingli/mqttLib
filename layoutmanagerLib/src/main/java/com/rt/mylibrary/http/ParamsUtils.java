package com.rt.mylibrary.http;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class ParamsUtils {
    public static Map<String, String> getCommonParams() {
        Map<String, String> params = new HashMap<>();
        String token = "";
        if (!TextUtils.isEmpty(token)) {
            params.put("Authorization", token);
        }


        return params;
    }
}
