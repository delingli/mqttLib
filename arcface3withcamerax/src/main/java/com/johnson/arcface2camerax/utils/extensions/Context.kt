package com.johnson.arcface2camerax.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @ClassName Context
 * @Description TODO
 * @Author WangShunYi
 * @Date 2019-08-02 17:34
 */

/**
 * 权限检查方法，false代表没有该权限，ture代表有该权限
 */
fun Context.hasPermission(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

