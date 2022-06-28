package com.itc.switchdevicecomponent.basic

interface ISKDeviceOpt : IDeviceOpt {

    fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String)  //开机
    fun systemReset()//重启
    fun cancelStartCloseDevice(startDeviceTime: String, closeDeviceTime: String)//取消开机和关机
}