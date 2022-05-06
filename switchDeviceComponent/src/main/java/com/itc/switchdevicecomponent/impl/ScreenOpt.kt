package com.itc.switchdevicecomponent.impl

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.IScreenOpt
import com.itc.switchdevicecomponent.receiver.SceenOffAdminReceiver
import com.itc.switchdevicecomponent.utils.ScreenUtil
import java.io.DataOutputStream
import java.io.IOException

/**
 * 亮屏息屏操作
 */
class ScreenOpt(override var context: Context?) : IScreenOpt {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun sendScreenOn(
        deviceType: String,
        startTime: String,
        timer: Long,
        isCancel: Boolean
    ) {
        ScreenUtil.getInstance(context?.applicationContext)
            .sendScreenOn(context, startTime, timer, isCancel)

    }

    override fun screenOff(deviceType: String) {
        when (deviceType) {
            DeviceType.MODULE_HRA -> {
                LogUtils.dTag(IDeviceOpt.TAG, "HRA start setting")
                val intent = Intent("com.hra.setDeviceSleeporWakeup")
                intent.putExtra("power_key", false)
                context?.sendBroadcast(intent)
            }
            DeviceType.MODULE_LITIJI -> {
                LogUtils.dTag(IDeviceOpt.TAG, "LITIJI start setting")
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val out = DataOutputStream(
                        process.outputStream
                    )
                    out.writeBytes("echo 1 > sys/class/backlight/rk28_bl/bl_power \n")
                    out.writeBytes("exit\n")
                    out.flush()
                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }
            else -> {
                val policyManager =
                    context?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val adminReceiver: ComponentName = ComponentName(
                    context!!,
                    SceenOffAdminReceiver::class.java
                )
                val admin = policyManager.isAdminActive(adminReceiver)
                if (!admin) {
                    return
                }
                policyManager.lockNow()
            }


        }
    }

    override fun screenOn(deviceType: String) {
        when (deviceType) {
            DeviceType.MODULE_HRA -> {
                val intent = Intent("com.hra.setDeviceSleeporWakeup")
                intent.putExtra("power_key", true)
                context?.sendBroadcast(intent)
                LogUtils.dTag(TAG, "HRA 亮屏...")
            }
            DeviceType.MODULE_LITIJI -> {
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val out = DataOutputStream(
                        process.outputStream
                    )
                    out.writeBytes("echo 0 > sys/class/backlight/rk28_bl/bl_power \n")
                    out.writeBytes("exit\n")
                    out.flush()
                    LogUtils.dTag(TAG, "LITIJI 亮屏...")

                } catch (e: IOException) {
                    e.printStackTrace()
                    LogUtils.eTag(TAG, "LITIJI 异常...")
                }
            }
            else -> {
                //亮屏
                val mPowerManager =
                    context?.getSystemService(Context.POWER_SERVICE) as PowerManager
                @SuppressLint("InvalidWakeLockTag") val mWakeLock = mPowerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                    "tag"
                )

                mWakeLock.acquire()
                mWakeLock.release()
                LogUtils.dTag(TAG, "其它屏 异常...")
            }
        }
    }

    override fun sendScreenOff(deviceType: String, startTime: String, isCancel: Boolean) {
        ScreenUtil.getInstance(context?.applicationContext)
            .sendScreenOff(context, startTime, isCancel)
    }


}