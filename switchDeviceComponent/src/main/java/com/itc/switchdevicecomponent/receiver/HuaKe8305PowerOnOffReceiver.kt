package com.itc.switchdevicecomponent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.hky.PowerManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils

/**
 * Created by LB on 2022/6/22.
 * 华科8305定时关机的广播接收者  要注册
 */
class HuaKe8305PowerOnOffReceiver : BroadcastReceiver() {
    companion object {
        const val HUA_KE_8305_ACTION = "android.huake.8305.run_power_off"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            return
        }
        val action = intent.action
        LogUtils.dTag(javaClass.simpleName, "监听到的广播动作:$action")
        if (StringUtils.isEmpty(action)) {
            return
        }
        if (StringUtils.equals(action, HUA_KE_8305_ACTION)) {
            val powerManager = PowerManager(context)
            powerManager.shutdown(false, false)
        }
    }
}
