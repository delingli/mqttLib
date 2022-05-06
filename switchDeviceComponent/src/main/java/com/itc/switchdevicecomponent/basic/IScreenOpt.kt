package com.itc.switchdevicecomponent.basic

import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.basic.IDeviceOpt

/**
 * 息屏操作
 */
interface IScreenOpt : IDeviceOpt {
    fun sendScreenOn(
        @DeviceType.DeviceType deviceType: String,
        startTime: String,
        timer: Long,
        isCancel: Boolean
    )

    fun screenOff(@DeviceType.DeviceType deviceType: String)
    fun screenOn(@DeviceType.DeviceType deviceType: String)
    fun sendScreenOff(
        @DeviceType.DeviceType deviceType: String,
        startTime: String,
        isCancel: Boolean
    )
}