package com.itc.switchdevicecomponent.basic

import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.basic.IDeviceOpt

/**
 * 重启操作
 */
interface IRebootOpt : IDeviceOpt {
    fun reBoot(
        @DeviceType.DeviceType deviceType: String,
        timers: String,
        taskType: Int,
        isCancel: Boolean
    )

    fun start_reboot(@DeviceType.DeviceType deviceType: String)
}