package com.itc.switchdevicecomponent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import java.io.DataOutputStream
import java.io.IOException

open class StartRebootReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "StartRebootReceiver"
        const val ACTION_CUSTOM_STARTREBOOT = "ACTION_CUSTOM_STARTREBOOT"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action.equals(ACTION_CUSTOM_STARTREBOOT)) {
                LogUtils.dTag(TAG, "收到了自定义reboot的广播")
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val out = DataOutputStream(
                        process.outputStream
                    )
                    out.writeBytes("reboot \n")
                    out.writeBytes("exit\n")
                    out.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                    LogUtils.dTag(TAG, "重启执行失败:" + e.message)
                }
            }


        }

    }
}