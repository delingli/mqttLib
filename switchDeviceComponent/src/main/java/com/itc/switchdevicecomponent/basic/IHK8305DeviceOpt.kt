package com.itc.switchdevicecomponent.basic

import android.content.Context
import com.itc.switchdevicecomponent.annation.DeviceType

/**
 * 开关机操作
 */
interface IHK8305DeviceOpt : IDeviceOpt {
    fun startDevice(startDeviceTime: String)  //开机
    fun closeDevice(closeDeviceTime: String) //关机
    fun systemReset()//设备重启
    fun cancelStartCloseDevice() //取消关关机
}