package com.itc.switchdevicecomponent.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.checkOpenCloseTime
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.sdfs
import com.itc.switchdevicecomponent.basic.IHKBasicDeviceOpt
import java.io.*
import java.text.ParseException
import java.util.*

class IHKBasicDeviceOptImpl(override var context: Context?) : IHKBasicDeviceOpt {

    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    override fun startDevice(startDeviceTime: String) {
        if (!checkOpenCloseTime(startDeviceTime)) {
            LogUtils.dTag(TAG, "华科广谱设备开机时间配置不对，请检查配置")
            return
        }
        var parse: Date? = null
        try {
            parse = sdfs.parse(startDeviceTime)
            val times = parse.time
            val intent = Intent("android.timerswitch.run_power_on")//
            startDevicePendingIntent = PendingIntent.getBroadcast(
                context,
                times.toInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
            val am =
                context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //am.set，定时开机，第一个参数必须为4，第二个参数为开机时间（年月日时分秒转换后的毫秒值）
            am.setExact(4, times, startDevicePendingIntent)

        } catch (e: ParseException) {
            LogUtils.eTag(TAG, "set error:$e")
            e.printStackTrace()
        }
    }

    var closeDevicePendingIntent: PendingIntent? = null
    var startDevicePendingIntent: PendingIntent? = null

    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    override fun closeDevice(closeDeviceTime: String) {
        if (!checkOpenCloseTime(closeDeviceTime)) {
            LogUtils.dTag(TAG, "华科广谱设备关机时间配置不对，请检查配置")
            return
        }
        var parse: Date? = null
        try {
            parse = sdfs.parse(closeDeviceTime)
            val times = parse.time
            val intent = Intent("android.timerswitch.run_power_off")
            closeDevicePendingIntent = PendingIntent.getBroadcast(
                context,
                times.toInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
            val am =
                context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            am.setExact(AlarmManager.RTC_WAKEUP, times, closeDevicePendingIntent)
        } catch (e: ParseException) {
            LogUtils.eTag(TAG, "set error:$e")
            e.printStackTrace()
        }
    }

    override fun systemReset() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val out = DataOutputStream(
                process.outputStream
            )
            out.writeBytes("reboot \n")
            out.writeBytes("exit\n")
            out.flush()
            LogUtils.dTag(TAG, "重启执行成功...")
        } catch (e: IOException) {
            e.printStackTrace()
            e?.message?.let {
                LogUtils.dTag(TAG, "重启执行失败:" + it)
            }

        }
    }

    override fun cancelStartCloseDevice() {
        val am =
            context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        closeDevicePendingIntent?.let {
            am.cancel(it)
            LogUtils.dTag(TAG, "AlarmManager 关机意图取消成功")
        }
        startDevicePendingIntent?.let {
            am.cancel(it)
            LogUtils.dTag(TAG, "AlarmManager 开机意图取消成功")

        }
    }

}