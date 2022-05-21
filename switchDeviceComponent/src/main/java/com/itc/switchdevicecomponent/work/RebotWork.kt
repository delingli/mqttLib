package com.itc.switchdevicecomponent.work

import android.app.Notification
import android.content.Context
import android.text.TextUtils
import androidx.work.*
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.itc.switchdevicecomponent.DeviceOptManager
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.impl.IAndroidHeziDeviceOptImpl
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import com.itc.switchdevicecomponent.rooms.RebootOptDB

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 重启设备
 */
class RebotWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
/*    override suspend fun getForegroundInfo(): ForegroundInfo {
        return  ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    private fun createNotification(): Notification {

    }*/

    companion object {
        const val TAG = "RebotWork"
        const val TAG_OUTPUT = "TAG_OUTPUT"
    }

//    private val mCommonRepostory by lazy {
//        CommonRepostory()
//    }


    override suspend fun doWork(): Result {
        inputData.run {
            var mRebootOptDB: RebootOptDB? =
                RebootDataSingle.instance.getDao(applicationContext).mRebootOptDao.selectOptData(
                    OptType.TASKTYPE_REBOOT
                )

            mRebootOptDB?.let {
                LogUtils.dTag(TAG, "执行一次重启设备娃哈哈..." + "")
                when (it.deviceType) {
                    DeviceType.MODULE_ANDROID_HE_ZI -> {
                        DeviceOptManager.getAndroidHeziDeviceOptImpl()?.systemReset()
                    }
                    DeviceType.MODULE_SHRG -> {
                        DeviceOptManager.getISHRGDeviceOpt()?.systemReset()

                    }
                    DeviceType.MODULE_JINGXIN -> {
                        DeviceOptManager.getJingXinDeviceOptImpl()?.systemReset()
                    }
                    DeviceType.MODULE_HEZI0830B -> {
                        DeviceOptManager.getI0830BDeviceOptImpl()?.systemReset()
                    }
                    else -> {}
                }
            }
            return Result.success()
        }
    }
}
