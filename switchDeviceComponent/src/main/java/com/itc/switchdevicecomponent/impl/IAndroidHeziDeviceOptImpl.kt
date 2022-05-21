package com.itc.switchdevicecomponent.impl

import android.content.Context
import android.os.Build
import android.spirit.SpiritSysCtrl
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.itc.switchdevicecomponent.basic.IAndroidHeziDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.checkTime
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.sdfs
import java.util.*

class IAndroidHeziDeviceOptImpl(override var context: Context?) : IAndroidHeziDeviceOpt {


    /*    override fun startDevice(day: Int, hour: Int, min: Int) {
            LogUtils.dTag(TAG, "智微Android盒子配置开机...")

        }

        override fun closeDevice(day: Int, hour: Int, min: Int) {
            LogUtils.dTag(TAG, "智微Android盒子配置关机...")
            SpiritSysCtrl.getInstance(context).setAlarmPoweroff()


        }*/


    override fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        if (!checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(TAG, "智微Android盒子配置开关机时间不对，关机时间必须在开机时间之前")
            return
        }
        LogUtils.dTag(TAG, "智微Android盒子配置开关机...开机${startDeviceTime} 关机:${closeDeviceTime}")
//        var newStr = sdfs.format(parse)
        LogUtils.dTag(TAG, "当前SDK版本:${Build.VERSION.RELEASE}")

        val s: String = startDeviceTime.replace(" ".toRegex(), "-")
        val newstartDeviceTime = s.replace(":".toRegex(), "-")
        val s2: String = closeDeviceTime.replace(" ".toRegex(), "-")
        val newcloseDeviceTime = s2.replace(":".toRegex(), "-")
        LogUtils.dTag(TAG, "智微Android盒子转换后的:开机时间${newstartDeviceTime}关机时间:${newcloseDeviceTime}")
        val startSplit = newstartDeviceTime.split("-").toTypedArray()
        val closeSplit = newcloseDeviceTime.split("-").toTypedArray()
        val startResult = SpiritSysCtrl.getInstance(Utils.getApp()).setAlarmPoweron(
            startSplit.get(2).toInt(),
            startSplit.get(3).toInt(),
            startSplit.get(4).toInt()
        )
        val closeResult = SpiritSysCtrl.getInstance(Utils.getApp()).setAlarmPoweroff(
            closeSplit.get(2).toInt(),
            closeSplit.get(3).toInt(),
            closeSplit.get(4).toInt()
        )
        LogUtils.dTag(TAG, "智微Android盒子转换后的:开机执行result${startResult}")

    }

    override fun systemReset() {
        LogUtils.dTag(TAG, "智微Android盒子重启...")
        SpiritSysCtrl.getInstance(context).systemReset()
    }

    override fun cancelStartCloseDevice() {
        SpiritSysCtrl.getInstance(context).cancelAlarmPoweron()
        SpiritSysCtrl.getInstance(context).cancelAlarmPoweroff()
        LogUtils.dTag(TAG, "智微Android盒子取消了开关机...")
    }
}