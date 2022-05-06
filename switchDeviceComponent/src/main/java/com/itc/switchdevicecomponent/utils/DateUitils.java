package com.itc.switchdevicecomponent.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


;

/**
 * Created by ${zml} on 2018/2/28.
 */
public class DateUitils {


    //获取当前系统具体时间
    public static String getCurrentStringDate() {
        Date parse = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");//设置日期格式
        Date date = new Date();
        String format = df.format(date);

        return format;
    }
}
