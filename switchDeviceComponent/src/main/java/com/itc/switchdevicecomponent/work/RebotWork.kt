package com.itc.switchdevicecomponent.work

import android.content.Context
import androidx.work.*
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import com.itc.switchdevicecomponent.rooms.RebootOptDB

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
        const val REBORT_TAG = "RebotWork"
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
                LogUtils.dTag(REBORT_TAG, "执行一次重启设备娃哈哈..." + "")
                when (it.deviceType) {
                    DeviceType.MODULE_HKBASIC -> {
                        DeviceOptManager.getHKBasicDeviceOpt()?.systemReset()
                    }
                    DeviceType.MODULE_HK8305 -> {
                        DeviceOptManager.getI0830BDeviceOptImpl()?.systemReset()
                    }
                    DeviceType.MODULE_SK -> {
                        DeviceOptManager.getSkDeviceOpt()
                            ?.systemReset()
                    }
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
