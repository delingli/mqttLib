package com.itc.switchdevicecomponent.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.ISHRGDeviceOpt

class ISHRGDeviceOptImpl(override var context: Context?) : ISHRGDeviceOpt {
    override fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        if (!IDeviceOpt.checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(IDeviceOpt.TAG, "深海瑞格配置开关机时间不对，关机时间必须在开机时间之前，后端去查验")
            return
        }
        LogUtils.dTag(TAG, "当前SDK版本:${Build.VERSION.RELEASE}")
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
        val intent = Intent("android.intent.action.setpoweronoff")
        intent.putExtra("timeon", timeOn)
        intent.putExtra("timeoff", timeoff)
        intent.putExtra("enable", true)
        if (Build.VERSION.RELEASE.equals("8.1.0")) {
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) //仅8.1需要添加此行代码
            LogUtils.dTag(TAG, "当前SDK版本执行了FLAG_ACTIVITY_PREVIOUS_IS_TOP")
        }
        context?.sendBroadcast(intent)
        LogUtils.dTag(
            IDeviceOpt.TAG,
            "深海瑞格设备配置开关机成功:开机:${timeOn[0]}${timeOn[1]}${timeOn[2]}${timeOn[3]}${timeOn[4]}:关机${timeoff[0]}" +
                    "${timeoff[1]}:" +
                    "${timeoff[2]}:" +
                    "${timeoff[3]}:" +
                    "${timeoff[4]}"
        )
    }

    override fun cancelStartCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        if (!IDeviceOpt.checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(IDeviceOpt.TAG, "深海瑞格配置开关机时间不对，关机时间必须在开机时间之前，后端去查验")
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
        val intent = Intent("android.intent.action.setpoweronoff")
        intent.putExtra("timeon", timeOn)
        intent.putExtra("timeoff", timeoff)
        LogUtils.dTag(TAG, "当前SDK版本:${Build.VERSION.RELEASE}")
        if (Build.VERSION.RELEASE.equals("8.1.0")) {
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) //仅8.1需要添加此行代码
            LogUtils.dTag(TAG, "当前SDK版本执行了FLAG_ACTIVITY_PREVIOUS_IS_TOP")
        }
        intent.putExtra("enable", false)
        context?.sendBroadcast(intent)
        LogUtils.dTag(
            TAG,
            "深海瑞格设备取消配置开关机成功:开机:${timeOn[0]}${timeOn[1]}${timeOn[2]}${timeOn[3]}${timeOn[4]}:关机${timeoff[0]}" +
                    "${timeoff[1]}:" +
                    "${timeoff[2]}:" +
                    "${timeoff[3]}:" +
                    "${timeoff[4]}"
        )
    }

    override fun systemReset() {
        LogUtils.dTag(IDeviceOpt.TAG, "深海瑞格设备重启")
        val intent = Intent("com.android.ostar.power.reboot")
        context?.sendBroadcast(intent)
    }
}