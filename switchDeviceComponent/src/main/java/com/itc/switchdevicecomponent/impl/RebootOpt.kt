package com.itc.switchdevicecomponent.impl

import android.content.Context
import com.itc.switchdevicecomponent.basic.IRebootOpt
import com.itc.switchdevicecomponent.utils.ScreenUtil

/**
 * 定时重启
 */
class RebootOpt(override var context: Context?) : IRebootOpt {
    override fun reBoot(deviceType: String, timers: String, taskType: Int, isCancel: Boolean) {
        ScreenUtil.getInstance(context?.applicationContext)
            .sendRebootBroad(context, timers, taskType, isCancel)
    }

    override fun start_reboot(deviceType: String) {
        ScreenUtil.getInstance(context?.applicationContext).start_reboot(context, deviceType)

    }


}