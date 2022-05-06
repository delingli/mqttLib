package com.itc.switchdevicecomponent.basic

import android.content.Context

/**
 * 设备操作类
 */
interface IDeviceOpt {
    var context: Context?

    companion object {
        const val TAG = "IDeviceOpt"
    }
}