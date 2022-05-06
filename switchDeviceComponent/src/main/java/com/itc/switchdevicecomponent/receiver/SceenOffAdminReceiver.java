package com.itc.switchdevicecomponent.receiver;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.itc.switchdevicecomponent.R;


/**
 * Created by ${zml} on 2018/10/17.
 */
@SuppressLint("NewApi")
public class SceenOffAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        ToastUtils.showShort(R.string.has_root);

    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        ToastUtils.showShort(R.string.no_root);
    }
}
