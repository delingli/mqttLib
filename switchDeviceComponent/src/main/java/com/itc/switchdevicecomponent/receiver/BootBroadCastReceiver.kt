package com.itc.switchdevicecomponent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptMode
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import com.itc.switchdevicecomponent.rooms.RebootOptDB
import com.itc.switchdevicecomponent.work.CustomRebotWork
import com.itc.switchdevicecomponent.work.RebotWork
import java.util.concurrent.TimeUnit

//监听开机的广播
open class BootBroadCastReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "BootBroadCastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            intent?.let {
                if (it.action.equals(Intent.ACTION_BOOT_COMPLETED)) {  //开机广播
                    LogUtils.dTag(TAG, "开机的广播")
                    val constraints = Constraints.Builder().setRequiresCharging(true).build()
                    val dailyWorkRequest = OneTimeWorkRequestBuilder<CustomRebotWork>()
                        .setConstraints(constraints).setInitialDelay(200, TimeUnit.MILLISECONDS)
                        .addTag(RebotWork.TAG_OUTPUT).build()
                    WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                        CustomRebotWork.CustomRebotWork,
                        ExistingWorkPolicy.REPLACE,
                        dailyWorkRequest
                    )


                } else if (it.action.equals(Intent.ACTION_SHUTDOWN)) {
                    LogUtils.dTag(TAG, "关机的广播")  //关机广播 不做处理
                } else {

                }
            }
        }

    }
}