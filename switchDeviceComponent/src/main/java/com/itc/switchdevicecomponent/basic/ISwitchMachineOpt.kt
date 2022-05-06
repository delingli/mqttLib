package com.itc.switchdevicecomponent.basic

import android.content.Context
import com.itc.switchdevicecomponent.annation.DeviceType

/**
 * 开关机操作
 */
interface ISwitchMachineOpt : IDeviceOpt {
    fun handlerOpt(
        @DeviceType.DeviceType deviceType: String,
        startBoot: String,
        delay: Long,
        taskType: Int,
        isOpen: Boolean
    )

    fun openAndroidHeZiPowerOnOff(powerOffTime: String, timer: Long)
    fun cancelAndroidHeZiPowerOnOff()
    fun clearPowerOnOffTimeForXingMaXingShen()
    fun setShutdownForHra(startBoot: String)
    fun setEndBootForHra()
    fun setEndShutDownForHra()
    fun setBootTimeForHra(startBoot: String, delay: Long)
    fun startBootForHra()
    fun startShutDownForHra()

    fun setJingxinShutdown(timer: String, delay: Long, isOpen: Boolean)

    fun setBootTimeForHuaKe(startBoot: String, delay: Long)

    fun setShutdownForHuaKe(startBoot: String)

    fun setBootCancelFor0830()

    fun setBootTimeFor0830(startBoot: String, delay: Long)

    fun setShutdownFor0830(startBoot: String, taskType: Int, isOpen: Boolean)

    fun setBootTimeForShenHaiReboot(

        startBoot: String,
        delay: Long,
        isOpen: Boolean
    )

    fun setCW(startBoot: String, delay: Long)

    fun setBootTimeForXingMaAndXingShen(startBoot: String, delay: Long)

}