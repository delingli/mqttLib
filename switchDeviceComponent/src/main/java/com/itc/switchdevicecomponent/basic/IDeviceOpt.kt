package com.itc.switchdevicecomponent.basic

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
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
            if (startDeviceTime.equals("NULL") || closeDeviceTime.equals("NULL") || startDeviceTime.equals(
                    "null"
                ) || closeDeviceTime.equals("null") || startDeviceTime.isNullOrEmpty() || closeDeviceTime.isNullOrEmpty()
            ) {
                return false
            }
            var startData = sdfs.parse(startDeviceTime)
            var closeData = sdfs.parse(closeDeviceTime)
            var startCalas = Calendar.getInstance()
            startCalas.time = startData
            var closeCala = Calendar.getInstance()
            closeCala.time = closeData
            var nowCale = Calendar.getInstance()
            nowCale.time = TimeUtils.getNowDate()
            if (closeCala.before(startCalas) && nowCale.before(closeCala)) {
                return true
            } else {
                false
            }
            return false
        }

        /**
         * 处理时间
         */
        fun handlerTime(str: String): List<String>? {
            if (!str.isNullOrEmpty()) {
                val s: String = str.replace(" ".toRegex(), "-")
                val newstartDeviceTime = s.replace(":".toRegex(), "-")
                LogUtils.dTag(TAG, "处理时间 sucess:${newstartDeviceTime}")

                return newstartDeviceTime.split("-")
            }
            LogUtils.dTag(TAG, "处理时间 error")
            return null

        }

    }
}