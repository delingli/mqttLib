package com.itc.switchdevicecomponent.impl

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.handlerTime
import com.itc.switchdevicecomponent.basic.ISKDeviceOpt
import com.zcapi

class ISKDeviceOptImpl(override var context: Context?) : ISKDeviceOpt {
    private var zcApi: zcapi? = null

    init {
        zcApi = zcapi()
        zcApi?.getContext(context)
    }

    override fun startCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        if (!IDeviceOpt.checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(IDeviceOpt.TAG, "视拓Android设备配置开关机时间不对，关机时间必须在开机时间之前,并且，关机必须在当前时间之后")
            return
        }
        val enable = true //取消或者使能定时开关机
        val startSplit: List<String>? = handlerTime(startDeviceTime)
        val closeSplit: List<String>? = handlerTime(closeDeviceTime)
        startSplit?.let {
            closeSplit?.run {
                LogUtils.dTag(
                    IDeviceOpt.TAG,
                    "视拓Android设备执行取消成功,操作开机时间...${startSplit[0]}:${startSplit[1]}:${startSplit[2]}:${startSplit[3]}:${startSplit[4]}"
                )
                LogUtils.dTag(
                    IDeviceOpt.TAG,
                    "视拓Android设备执行取消操作成功,关机时间...${closeSplit[0]}:${closeSplit[1]}:${closeSplit[2]}:${closeSplit[3]}:${closeSplit[4]}"
                )

                //当前1分钟后关机，3分钟后开机
                val onTime = intArrayOf(
                    startSplit[0].toInt(),
                    startSplit[1].toInt(),
                    startSplit[2].toInt(),
                    startSplit[3].toInt(),
                    startSplit[4].toInt()
                )
                val offTime = intArrayOf(
                    closeSplit[0].toInt(),
                    closeSplit[1].toInt(),
                    closeSplit[2].toInt(),
                    closeSplit[3].toInt(),
                    closeSplit[4].toInt()
                )
                zcApi?.setPowetOnOffTime(enable, onTime, offTime)
            }
        }


    }

    override fun systemReset() {
        zcApi?.reboot()
        LogUtils.dTag(IDeviceOpt.TAG, "视拓Android设备配置执行了重启")

    }


    override fun cancelStartCloseDevice(startDeviceTime: String, closeDeviceTime: String) {
        if (!IDeviceOpt.checkTime(startDeviceTime, closeDeviceTime)) {
            LogUtils.dTag(IDeviceOpt.TAG, "视拓Android设备配置开关机时间不对，关机时间必须在开机时间之前,无法取消")
            return
        }
        val enable = false //取消或者使能定时开关机
        val startSplit: List<String>? = handlerTime(startDeviceTime)
        val closeSplit: List<String>? = handlerTime(closeDeviceTime)
        startSplit?.let {
            closeSplit?.run {
                LogUtils.dTag(
                    IDeviceOpt.TAG,
                    "视拓Android设备执行取消成功,操作开机时间...${startSplit[0]}:${startSplit[1]}:${startSplit[2]}:${startSplit[3]}:${startSplit[4]}"
                )
                LogUtils.dTag(
                    IDeviceOpt.TAG,
                    "视拓Android设备执行取消操作成功,关机时间...${closeSplit[0]}:${closeSplit[1]}:${closeSplit[2]}:${closeSplit[3]}:${closeSplit[4]}"
                )

                //当前1分钟后关机，3分钟后开机
                val onTime = intArrayOf(
                    startSplit[0].toInt(),
                    startSplit[1].toInt(),
                    startSplit[2].toInt(),
                    startSplit[3].toInt(),
                    startSplit[4].toInt()
                )
                val offTime = intArrayOf(
                    closeSplit[0].toInt(),
                    closeSplit[1].toInt(),
                    closeSplit[2].toInt(),
                    closeSplit[3].toInt(),
                    closeSplit[4].toInt()
                )
                zcApi?.setPowetOnOffTime(enable, onTime, offTime)

            }

        }


    }
}