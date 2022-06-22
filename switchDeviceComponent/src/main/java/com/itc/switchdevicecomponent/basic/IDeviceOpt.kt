package com.itc.switchdevicecomponent.basic

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * 设备操作类
 */
interface IDeviceOpt {
    var context: Context?

    companion object {
        const val TAG = "IDeviceOpt"
        val sdfs = SimpleDateFormat("yyyy-MM-dd HH:mm")
        fun checkTime(startDeviceTime: String, closeDeviceTime: String): Boolean {
            var startData = sdfs.parse(startDeviceTime)
            var closeData = sdfs.parse(closeDeviceTime)
            var startCalas = Calendar.getInstance()
            startCalas.time = startData
            var closeCala = Calendar.getInstance()
            closeCala.time = closeData
            return closeCala.before(startCalas)
        }
    }
}