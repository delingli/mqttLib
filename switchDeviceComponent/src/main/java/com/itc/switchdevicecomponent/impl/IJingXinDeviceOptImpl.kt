package com.itc.switchdevicecomponent.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.sdfs
import com.itc.switchdevicecomponent.basic.IJingXinDeviceOpt
import java.util.*

class IJingXinDeviceOptImpl(override var context: Context?) : IJingXinDeviceOpt {


    override fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        if (!IDeviceOpt.checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(TAG, "精鑫配置开关机时间不对，关机时间必须在开机时间之前")
            return
        }
        LogUtils.dTag(TAG, "当前SDK版本:${Build.VERSION.RELEASE}")
        var newDate = Date()
        newDate.time = sdfs.parse(closeDeviceTime).time - (1 * 60 * 1000)
        var tempcloseDeviceTime: String = sdfs.format(newDate)

        val s: String = startDeviceTime.replace(" ".toRegex(), "-")
        val newstartDeviceTime = s.replace(":".toRegex(), "-")
        var newstartDeviceTimeArray = newstartDeviceTime.split("-")


        val s2: String = tempcloseDeviceTime.replace(" ".toRegex(), "-")
        val newcloseDeviceTime = s2.replace(":".toRegex(), "-")
        var newcloseDeviceTimeArray = newcloseDeviceTime.split("-")
        var timeOn = IntArray(5)
        var timeoff = IntArray(5)


        for (i in 0 until newstartDeviceTimeArray.size) {
            var vv = newstartDeviceTimeArray[i].toInt()
            timeOn[i] = vv
        }
        for (i in 0 until newcloseDeviceTimeArray.size) {
            timeoff[i] = newcloseDeviceTimeArray[i].toInt()
        }

        val intent = Intent("android.intent.action.gz.setpoweronoff")
        intent.putExtra("timeon", timeOn)
        intent.putExtra("timeoff", timeoff)
        intent.putExtra("enable", true)
        context?.sendBroadcast(intent)
        LogUtils.dTag(
            TAG,
            "精鑫屏设备配置开关机成功:开机:${timeOn[0]}${timeOn[1]}${timeOn[2]}${timeOn[3]}${timeOn[4]}:关机${timeoff[0]}" +
                    "${timeoff[1]}:" +
                    "${timeoff[2]}:" +
                    "${timeoff[3]}:" +
                    "${timeoff[4]}"
        )

    }


    override fun cancelStartCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        LogUtils.dTag(TAG, "精鑫屏设备取消开关机操作")
        if (!IDeviceOpt.checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(TAG, "精鑫配置开关机时间不对，关机时间必须在开机时间之前")
            return
        }
        val s: String = startDeviceTime.replace(" ".toRegex(), "-")
        val newstartDeviceTime = s.replace(":".toRegex(), "-")
        var newstartDeviceTimeArray = newstartDeviceTime.split("-")


        val s2: String = closeDeviceTime.replace(" ".toRegex(), "-")
        val newcloseDeviceTime = s2.replace(":".toRegex(), "-")
        var newcloseDeviceTimeArray = newcloseDeviceTime.split("-")
        var timeOn = IntArray(5)
        var timeoff = IntArray(5)

        for (i in newstartDeviceTimeArray.indices) {
            timeOn[i] = newstartDeviceTimeArray[i].toInt()
        }
        for (i in newcloseDeviceTimeArray.indices) {
            timeoff[i] = newcloseDeviceTimeArray[i].toInt()
        }

        LogUtils.dTag(TAG, "精鑫屏设备配置开关机取消广播发送")
        val intent = Intent("android.intent.action.gz.setpoweronoff")
        intent.putExtra("timeon", timeOn)
        intent.putExtra("timeoff", timeoff)
        intent.putExtra("enable", false)
        context?.sendBroadcast(intent)
        LogUtils.dTag(
            TAG,
            "精鑫屏设备取消配置开关机成功:开机:${timeOn[0]}${timeOn[1]}${timeOn[2]}${timeOn[3]}${timeOn[4]}:关机${timeoff[0]}" +
                    "${timeoff[1]}:" +
                    "${timeoff[2]}:" +
                    "${timeoff[3]}:" +
                    "${timeoff[4]}"
        )
    }

    override fun systemReset() {
        LogUtils.dTag(TAG, "精鑫屏设备发送重启广播")
        context?.sendBroadcast(Intent("reboot_system"))
    }
}