package com.itc.switchdevicecomponent.work

import android.content.Context
import androidx.work.*
import com.blankj.utilcode.util.*
import com.itc.switchdevicecomponent.DeviceOptManager
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 定时开关机
 */
class CustomRebotWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
/*    override suspend fun getForegroundInfo(): ForegroundInfo {
        return  ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    private fun createNotification(): Notification {

    }*/

    companion object {
        const val TAG = "CustomRebotWork"
        const val CustomRebotWork = "CustomRebotWork"
        val sdfs = SimpleDateFormat("yyyy-MM-dd HH:mm")

    }

    override suspend fun doWork(): Result {
        //todo get 数据
//        var mRebootOptDB = RebootOptDB()
////        DeviceOptManager.getDeviceType()
//        RebootDataSingle.instance.getDao(getApplicationContext()).mRebootOptDao.insert(
//            mRebootOptDB
//        )
        var mswitchDB =
            RebootDataSingle.instance.getDao(getApplicationContext()).mRebootOptDao.selectOptData(
                OptType.TASKTYPE_SWITCH_MACHINE
            )
        var rebootDB =
            RebootDataSingle.instance.getDao(getApplicationContext()).mRebootOptDao.selectOptData(
                OptType.TASKTYPE_REBOOT
            )
        mswitchDB?.let {
            when (it.deviceType) {
                DeviceType.MODULE_ANDROID_HE_ZI -> {
                    DeviceOptManager.getAndroidHeziDeviceOptImpl()
                        ?.startCloseDevice(it.startDeviceTime, it.closeDeviceTime)
                }
                DeviceType.MODULE_SHRG -> {
                    DeviceOptManager.getISHRGDeviceOpt()
                        ?.startCloseDevice(it.startDeviceTime, it.closeDeviceTime)

                }
                DeviceType.MODULE_JINGXIN -> {
                    DeviceOptManager.getJingXinDeviceOptImpl()
                        ?.startCloseDevice(it.startDeviceTime, it.closeDeviceTime)
                }
                DeviceType.MODULE_HEZI0830B -> {
                    DeviceOptManager.getI0830BDeviceOptImpl()?.startDevice(it.startDeviceTime)
                    DeviceOptManager.getI0830BDeviceOptImpl()?.closeDevice(it.closeDeviceTime)
                }
                else -> {}
            }

        }
        rebootDB?.let {
            WorkManager.getInstance(getApplicationContext()).cancelUniqueWork(RebotWork.TAG_OUTPUT)
            var restartDate = sdfs.parse(it.restartDeviceTime)
            val currentDateCala = Calendar.getInstance()
            val restartDateCala = Calendar.getInstance()
            currentDateCala.time = TimeUtils.getNowDate()
            restartDateCala.time = restartDate
            if (currentDateCala.before(restartDate)) {
                val timeDiff = restartDateCala.timeInMillis - currentDateCala.timeInMillis
                val constraints = Constraints.Builder().setRequiresCharging(true).build()
                val dailyWorkRequest = OneTimeWorkRequestBuilder<RebotWork>()
                    .setConstraints(constraints).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .addTag(RebotWork.TAG_OUTPUT).build()
                WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(
                    RebotWork.TAG_OUTPUT,
                    ExistingWorkPolicy.REPLACE,
                    dailyWorkRequest
                )
            } else {
                LogUtils.dTag(TAG, "下次重启过时了在当前时间之前了，拒绝重启，还要删除数据库")
            }


        }


        return Result.success()
    }

/*
    suspend fun toUpdateItemFace(context: Context, uid: Int, pic: String?) {
        var userSignatureList: ArrayList<UploadDataInfo> =
            arrayListOf()
        var mUserFaceEngine: UserFaceEngine? = null
        withContext(Dispatchers.IO) {
            if (TextUtils.isEmpty(pic)) {
                FaceOptManager.getInstance().deleteFaceCodeFromId(context, uid)
            } else {
                var screencode = SPUtils.getConfigString(
                    context,
                    MeetConfigConstant.SCREEN_CODE_KEY
                )
                screencode?.let {
                    mCommonRepostory.toGetFaceUserList(context, it, uid)

                }

//                FaceOptManager.getInstance().getImageForFaceCode(
//                    context,
//                    SetConfigGlobaConstant.getServerPath(false) + pic,
//                    uid
//                )
            }

//            mUserFaceEngine =
//                ApkDataSingle.instance.getDao(context).userFaceEngineDao.selectUserData(
//                    uid.toString()
//                )
        }


*/
/*        if (mUserFaceEngine != null) {
            userSignatureList.add(
                UploadDataInfo(
                    mUserFaceEngine!!.uid.toInt(),
                    true
                )
            )
        } else {
            userSignatureList.add(
                UploadDataInfo(
                    uid,
                    false
                )
            )

        }

        if (!userSignatureList.isNullOrEmpty()) {
            var pushJson = Gson().toJson(userSignatureList)
            LogUtils.dTag(TAG, "上报的json?${pushJson}")
            var uploadResults: Results<ResultData<String?>> =
                mCommonRepostory.uploadToServer(pushJson)
            when (uploadResults) {
                is Results.Sucess -> {
                    if (uploadResults.data?.code == 0) {
                        LogUtils.dTag(TAG, "上报的json相应成功...")
                    } else {
                        LogUtils.dTag(TAG, "上报的json相应失败...")

                    }

                }
                is Results.Sucess -> {
                    LogUtils.dTag(TAG, "上报的json相应失败...")
                }
            }
        }*//*

    }
*/


}
