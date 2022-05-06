package com.itc.switchdevicecomponent.basic

import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.basic.IDeviceOpt

/**
 * 红外操作
 */
interface ISendredOpt : IDeviceOpt {
    fun sendRed(
        @DeviceType.DeviceType deviceType: String,
        times: Long,
        taskId: Int,
        subInfrared: String
    )
}