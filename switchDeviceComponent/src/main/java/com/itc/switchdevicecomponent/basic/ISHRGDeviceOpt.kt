package com.itc.switchdevicecomponent.basic

import android.content.Context
import com.itc.switchdevicecomponent.annation.DeviceType

/**
 * 深海瑞格
 */
interface ISHRGDeviceOpt : IDeviceOpt {


    fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String)  //开机
    fun cancelStartCloseDevice(startDeviceTime: String, closeDeviceTime: String) //取消关关机使能false
    fun systemReset()//重启


}