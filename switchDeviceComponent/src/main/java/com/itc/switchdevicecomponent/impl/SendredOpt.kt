package com.itc.switchdevicecomponent.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.ISendredOpt

/**
 * 红外操作
 */
class SendredOpt(override var context: Context?) : ISendredOpt {
    override fun sendRed(deviceType: String, times: Long, taskId: Int, subInfrared: String) {
        if (deviceType != DeviceType.MODULE_HEZI) {
            LogUtils.dTag(TAG, "不是盒子设备返回")
            return
        }
        val intent = Intent("sendRed")
        intent.putExtra("redData", subInfrared)
        val pendingIntent = PendingIntent.getBroadcast(
            context, taskId, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        var alarmManager: AlarmManager =
            context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, times, pendingIntent)
        LogUtils.eTag(TAG, "send red time：" + times)
    }
}