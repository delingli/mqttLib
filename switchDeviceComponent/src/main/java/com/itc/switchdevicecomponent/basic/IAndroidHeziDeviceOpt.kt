package com.itc.switchdevicecomponent.basic

import android.content.Context
import com.itc.switchdevicecomponent.annation.DeviceType

/**
 * 开关机操作
 */
interface IAndroidHeziDeviceOpt : IDeviceOpt {
    /**
     * 开机时间，startDeviceTime 年月日时分 yyyy-MM-dd HH:mm
     * 关机时间  closeDeviceTime 年月日时分 yyyy-MM-dd HH:mm
     */
    fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String)  //开机

    //    fun startDevice(day: Int, hour: Int, min: Int)  //开机
//    fun closeDevice(day: Int, hour: Int, min: Int) //关机
    fun systemReset()//重启
    fun cancelStartCloseDevice()//取消开机取消关机

}