package com.itc.switchdevicecomponent.impl

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.ISwitchMachineOpt
import com.itc.switchdevicecomponent.utils.ScreenUtil
import startest.ys.com.poweronoff.PowerOnOffManager

/**
 * 开关机操作
 */
class SwitchMachineOpt(override var context: Context?) : ISwitchMachineOpt {


    override fun handlerOpt(
        deviceType: String,
        startBoot: String,
        delay: Long,
        taskType: Int,
        isOpen: Boolean
    ) {
        LogUtils.dTag(
            TAG,
            "原始数据:" + "deviceType:" + deviceType + "startBoot:" + startBoot + "delay:" + delay + "taskType:" + taskType
                    + "isOpen:" + isOpen
        )

        when (deviceType) {
            DeviceType.MODULE_JINGXIN -> {
                LogUtils.dTag(TAG, "JINGXIN start setting")
//                String startBoot = DateUitils.getCurrentStringDate() + getStartTime();

                setJingxinShutdown(startBoot, delay, isOpen)
            }
            DeviceType.MODULE_HRA -> {
                //开启自动开关机(华瑞安)
                LogUtils.dTag(TAG, "HRA start setting")
                startBootForHra()
                startShutDownForHra()
                //先设置开机时间
//                String startBoot = DateUitils.getCurrentStringDate() + getStartTime();
                //华瑞安厂家开关机指令
                setBootTimeForHra(startBoot, delay)
                //设置关机
                setShutdownForHra(startBoot)
            }
            DeviceType.MODULE_HUAKE -> {
                LogUtils.dTag(TAG, "HUAKE start setting")
//                String startBoot = DateUitils.getCurrentStringDate() + getStartTime();
                //华科厂家盒子指令
                setBootTimeForHuaKe(startBoot, delay)
                setShutdownForHuaKe(startBoot)
            }
            DeviceType.MODULE_HEZI -> {
                //0830B盒子开关机指令
                //先清理之前的文件
                setBootCancelFor0830()
                LogUtils.dTag(TAG, "HEZI 0830Task start setting")
                setBootTimeFor0830(startBoot, delay)
                setShutdownFor0830(startBoot, taskType, isOpen)
            }
            DeviceType.MODULE_LITIJI -> {
                LogUtils.dTag(TAG, "LITIJI start setting")
                setBootTimeForShenHaiReboot(startBoot, delay, isOpen)
            }
            DeviceType.MODULE_CW -> {
                //触沃设备开关机
//                String offTimers = DateUitils.getCurrentStringDate() + getStartTime();
                LogUtils.dTag(TAG, "CW start setting")

                setCW(startBoot, delay)
            }
            DeviceType.MODULE_XINGSHEN, DeviceType.MODULE_XINGMA -> {
                LogUtils.dTag(TAG, "XINGSHEN or XINGMA start setting")
                setBootTimeForXingMaAndXingShen(startBoot, delay)
            }
        }
    }

    override fun openAndroidHeZiPowerOnOff(powerOffTime: String, timer: Long) {
        ScreenUtil.getInstance(context?.applicationContext)
            .openAndroidHeZiPowerOnOff(powerOffTime, timer)
    }

    /**
     * 取消安卓盒子的定时开关机
     */
    override fun cancelAndroidHeZiPowerOnOff() {
        ScreenUtil.getInstance(context?.applicationContext).cancelAndroidHeZiPowerOnOff()
    }


    override fun clearPowerOnOffTimeForXingMaXingShen() {
        val powerOnOffManager = PowerOnOffManager.getInstance(context?.applicationContext)
        powerOnOffManager.clearPowerOnOffTime()
    }

    override fun setShutdownForHra(startBoot: String) {
        ScreenUtil.getInstance(context?.applicationContext).setShutdownForHra(context, startBoot)
    }

    override fun setEndBootForHra() {
        ScreenUtil.getInstance(context?.applicationContext)
            .endBoot(context?.applicationContext)
    }

    override fun setEndShutDownForHra() {
        ScreenUtil.getInstance(context?.applicationContext)
            .endHraShutDown(context?.applicationContext)
    }

    override fun setBootTimeForHra(startBoot: String, delay: Long) {
        ScreenUtil.getInstance(context?.applicationContext)
            .setBootTimeForHra(context?.applicationContext, startBoot, delay)
    }

    override fun startBootForHra() {
        ScreenUtil.getInstance(context?.applicationContext)
            .startBootForHra(context?.applicationContext)
    }

    override fun startShutDownForHra() {
        ScreenUtil.getInstance(context?.applicationContext)
            .startShutDownForHra(context?.applicationContext)
    }

    override fun setJingxinShutdown(timer: String, delay: Long, isOpen: Boolean) {
        ScreenUtil.getInstance(context?.applicationContext)
            .setJingxinShutdown(context?.applicationContext, timer, delay, isOpen)
    }

    override fun setBootTimeForHuaKe(startBoot: String, delay: Long) {
        ScreenUtil.getInstance(context?.applicationContext)
            .setBootTimeForHuaKe(context?.applicationContext, startBoot, delay)
    }

    override fun setShutdownForHuaKe(startBoot: String) {
        ScreenUtil.getInstance(context?.applicationContext)
            .setShutdownForHuaKe(context?.applicationContext, startBoot)
    }

    override fun setBootCancelFor0830() {
        ScreenUtil.getInstance(context?.applicationContext)
            .set0830BootCancel()
    }

    override fun setBootTimeFor0830(startBoot: String, delay: Long) {
        ScreenUtil.getInstance(context?.applicationContext)
            .setBootTimeFor0830(startBoot, delay)
    }

    override fun setShutdownFor0830(

        startBoot: String,
        taskType: Int,
        isOpen: Boolean
    ) {
        ScreenUtil.getInstance(context?.applicationContext)
            .setShutdownFor0830(context?.applicationContext, startBoot, taskType, isOpen)
    }

    override fun setBootTimeForShenHaiReboot(

        startBoot: String,
        delay: Long,
        isOpen: Boolean
    ) {
        ScreenUtil.getInstance(context?.applicationContext).setBootTimeForShenHaiReboot(
            context?.applicationContext,
            startBoot, delay, isOpen
        )
    }

    override fun setCW(startBoot: String, delay: Long) {
        ScreenUtil.getInstance(context?.applicationContext).setCW(
            context?.applicationContext,
            startBoot, delay
        )
    }

    override fun setBootTimeForXingMaAndXingShen(startBoot: String, delay: Long) {
        ScreenUtil.getInstance(context?.applicationContext).setBootTimeForXingMaAndXingShen(
            context?.applicationContext,
            startBoot,
            delay
        )
    }
}