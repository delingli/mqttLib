package com.itc.switchdevicecomponent.work

import android.content.Context
import androidx.work.*
import com.blankj.utilcode.util.*
import com.itc.commoncomponent.network.Results
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.impl.DeviceOptModel
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import com.itc.switchdevicecomponent.rooms.RebootOptDB
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 定时开关机
 */
class SwitchMachineWork(appContext: Context, workerParams: WorkerParameters) :
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
        val deviceType ="deviceType"

    }

    lateinit var mDeviceOptModel: DeviceOptModel

    override suspend fun doWork(): Result {
        DeviceOptManager.hasInited()
        mDeviceOptModel = DeviceOptModel()
        var result = mDeviceOptModel.flushDeviceTime()
        if (result != null) {
            when (result) {
                is Results.Sucess -> {
                    if (result?.data?.code == 0) {
                        result?.data?.data?.let {
                            if (it.restart_date_time != null) {
                                var mRebootOptDB = RebootOptDB()
                                mRebootOptDB.mOptType = OptType.TASKTYPE_REBOOT
                                mRebootOptDB.restartDeviceTime = it.restart_date_time
                                mRebootOptDB.deviceType =
                                    SPUtils.getInstance().getString(SwitchMachineWork.deviceType)
                                LogUtils.dTag(TAG, "重启时间，${it.restart_date_time}")
                                RebootDataSingle.instance.getDao(getApplicationContext()).mRebootOptDao.insert(
                                    mRebootOptDB
                                )

                            } else {
                                LogUtils.dTag(TAG, "重启时间为空，不做处理")
                            }
                            if (it.start_date_time != null && it.end_date_time != null) {
                                var mRebootOptDB = RebootOptDB()
                                mRebootOptDB.mOptType = OptType.TASKTYPE_SWITCH_MACHINE
                                mRebootOptDB.startDeviceTime = it.start_date_time
                                mRebootOptDB.closeDeviceTime = it.end_date_time
                                mRebootOptDB.deviceType =
                                    SPUtils.getInstance().getString(SwitchMachineWork.deviceType)
                                RebootDataSingle.instance.getDao(getApplicationContext()).mRebootOptDao.insert(
                                    mRebootOptDB
                                )
                                LogUtils.dTag(
                                    TAG,
                                    "开关机时间为${it.start_date_time}关机:${it.end_date_time}"
                                )
                            } else {
                                LogUtils.dTag(TAG, "开关机时间为空，不做处理")
                            }


                        }


                    } else {
                        ToastUtils.showShort("请求成功数据返回失败 code=${result?.data?.code}")
                    }


                }
                is Results.Error -> {
                    ToastUtils.showShort("请求失败")
                }
            }


        } else {
            ToastUtils.showShort("请求失败")
        }

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
                    RebotWork.REBORT_TAG,
                    ExistingWorkPolicy.REPLACE,
                    dailyWorkRequest
                )
            } else {
                LogUtils.dTag(TAG, "下次重启过时了在当前时间之前，拒绝重启，还要删除数据库")
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
