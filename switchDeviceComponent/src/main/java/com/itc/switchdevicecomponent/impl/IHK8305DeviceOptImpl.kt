package com.itc.switchdevicecomponent.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import com.android.hky.AlarmManagerUtils
import com.android.hky.PowerManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.sdfs
import com.itc.switchdevicecomponent.basic.IHK8305DeviceOpt
import com.itc.switchdevicecomponent.receiver.HuaKe8305PowerOnOffReceiver
import java.io.*
import java.text.ParseException
import java.util.*

class IHK8305DeviceOptImpl(override var context: Context?) : IHK8305DeviceOpt {

    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    override fun startDevice(startDeviceTime: String) {
        if (!IDeviceOpt.checkOpenCloseTime(startDeviceTime)) {
            LogUtils.dTag(TAG, "华科 8305 设备开机时间配置不对，请检查配置")
            return
        }
        var parse: Date? = null
        LogUtils.dTag(TAG, "当前SDK版本:${Build.VERSION.RELEASE}")
        try {
            parse = sdfs.parse(startDeviceTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        parse?.time?.let {
            val alarmManagerUtils = AlarmManagerUtils(context)
            alarmManagerUtils.setOpenTime(it)
            LogUtils.dTag(TAG, "8305开机时间配置${it}:${startDeviceTime}")
        }
    }

    var pi: PendingIntent? = null

    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    override fun closeDevice(closeDeviceTime: String) {
        if (!IDeviceOpt.checkOpenCloseTime(closeDeviceTime)) {
            LogUtils.dTag(TAG, "华科 8305 设备关机时间配置不对，请检查配置")
            return
        }
        var parse: Date? = null
        try {
            parse = sdfs.parse(closeDeviceTime)
            val times = parse.time
            LogUtils.dTag(
                TAG,
                "关机时间$times---${closeDeviceTime}"
            )
            val intent = Intent(HuaKe8305PowerOnOffReceiver.HUA_KE_8305_ACTION)
//            if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.BASE)
            pi = PendingIntent.getBroadcast(
                context,
                times.toInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
            val am =
                context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExact(AlarmManager.RTC_WAKEUP, times, pi)
        } catch (e: ParseException) {
            LogUtils.eTag(TAG, "set error:$e")
            e.printStackTrace()
        }
    }

    override fun systemReset() {
        try {
            var mPowerManager = PowerManager(context)
            mPowerManager.reboot()
        } catch (e: IOException) {
            e.printStackTrace()
            e?.message?.let {
                LogUtils.eTag(TAG, "重启执行失败:" + it)
            }

        }
    }

    override fun cancelStartCloseDevice() {
        pi?.let {
            var alarmManagerUtils = AlarmManagerUtils(context)
            alarmManagerUtils.cancelCloseTime(it)
            LogUtils.eTag(TAG, "华科8305取消执行成功...")
        }
        pi = null
    }


}