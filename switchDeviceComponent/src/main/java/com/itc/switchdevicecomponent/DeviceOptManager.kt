package com.itc.switchdevicecomponent

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.basic.*
import com.itc.switchdevicecomponent.impl.*
import com.itc.switchdevicecomponent.receiver.BootBroadCastReceiver
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import com.itc.switchdevicecomponent.rooms.RebootOptDB
import com.itc.switchdevicecomponent.work.CustomRebotWork
import com.itc.switchdevicecomponent.work.RebotWork
import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object DeviceOptManager {
    var mRestartJob: Job? = null
    var maddOpenCloseJob: Job? = null
    var mCancelRestart: Job? = null
    var mcancelOpenCloseJob: Job? = null
    var deviceTypes: String? = null
    var mContext: Context? = null
    var mI0830BDeviceOptImpl: I0830BDeviceOpt? = null
    var mIAndroidHeziDeviceOptImpl: IAndroidHeziDeviceOpt? = null
    var mIJingXinDeviceOptImpl: IJingXinDeviceOpt? = null
    var mISHRGDeviceOptImpl: ISHRGDeviceOpt? = null
    const val TAG = "DeviceOptManager"

    fun toInit(context: Context) {
        this.mContext = context
        checkNotNull()
        mI0830BDeviceOptImpl = I0830BDeviceOptImpl(mContext)
        mIAndroidHeziDeviceOptImpl = IAndroidHeziDeviceOptImpl(mContext)
        mIJingXinDeviceOptImpl = IJingXinDeviceOptImpl(mContext)
        mISHRGDeviceOptImpl = ISHRGDeviceOptImpl(mContext)
    }

    fun getI0830BDeviceOptImpl(): I0830BDeviceOpt? {
        checkNotNull()
        return mI0830BDeviceOptImpl
    }

    fun getAndroidHeziDeviceOptImpl(): IAndroidHeziDeviceOpt? {
        checkNotNull()
        return mIAndroidHeziDeviceOptImpl
    }

    fun getJingXinDeviceOptImpl(): IJingXinDeviceOpt? {
        checkNotNull()
        return mIJingXinDeviceOptImpl
    }

    fun getISHRGDeviceOpt(): ISHRGDeviceOpt? {
        checkNotNull()
        return mISHRGDeviceOptImpl
    }

    fun checkNotNull() {
        if (mContext == null) {
            throw RuntimeException("Context is not can be null")
        }
    }

    fun getDeviceType(): String? {
        checkNotNull()
        return deviceTypes
    }

    /**
     * 添加重启
     */
    fun addRestartDevice(
        restartTime: String?,
        @DeviceType.DeviceType deviceType: String?
    ) {
        checkNotNull()
        if (restartTime.isNullOrEmpty() || deviceType.isNullOrEmpty()) {
            throw RuntimeException("params is not can be null")
        }
        this.deviceTypes = deviceType
        mRestartJob = GlobalScope.launch {
            withContext(Dispatchers.IO) {
                mContext?.let {
                    var mRebootOptDB = RebootOptDB()
                    mRebootOptDB.restartDeviceTime = restartTime
                    mRebootOptDB.mOptType = OptType.TASKTYPE_REBOOT
                    mRebootOptDB.deviceType = deviceType
                    RebootDataSingle.instance.getDao(it).mRebootOptDao.insert(mRebootOptDB)
                    var mRebootOptDBs: RebootOptDB? =
                        RebootDataSingle.instance.getDao(it).mRebootOptDao.selectOptData(OptType.TASKTYPE_REBOOT)
                    mRebootOptDBs?.let {
                        var restartDate = CustomRebotWork.sdfs.parse(it.restartDeviceTime)
                        val currentDateCala = Calendar.getInstance()
                        val restartDateCala = Calendar.getInstance()
                        currentDateCala.time = TimeUtils.getNowDate()
                        restartDateCala.time = restartDate
                        if (!currentDateCala.before(restartDateCala)) {
                            LogUtils.dTag(TAG, "重启时间过时了，拒绝执行")
                        } else {
                            WorkManager.getInstance(mContext!!.applicationContext)
                                .cancelUniqueWork(RebotWork.TAG_OUTPUT)
                            val timeDiff =
                                restartDateCala.timeInMillis - currentDateCala.timeInMillis
                            val constraints =
                                Constraints.Builder().setRequiresCharging(true).build()
                            val dailyWorkRequest = OneTimeWorkRequestBuilder<RebotWork>()
                                .setConstraints(constraints)
                                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                                .addTag(RebotWork.TAG_OUTPUT).build()
                            WorkManager.getInstance(mContext!!.applicationContext)
                                .enqueueUniqueWork(
                                    RebotWork.TAG_OUTPUT,
                                    ExistingWorkPolicy.REPLACE,
                                    dailyWorkRequest
                                )
                        }

                    }
                }
            }
        }

    }

    fun cancelRestartDevice(
        @DeviceType.DeviceType deviceType: String?
    ) {
        checkNotNull()
        mCancelRestart = GlobalScope.launch {
            withContext(Dispatchers.IO) {
                WorkManager.getInstance(mContext!!.applicationContext)
                    .cancelUniqueWork(RebotWork.TAG_OUTPUT)
                RebootDataSingle.instance.getDao(mContext!!).mRebootOptDao.deleteOptData(OptType.TASKTYPE_REBOOT)
            }
        }

    }


    fun unInit() {
        mRestartJob?.cancel()
        maddOpenCloseJob?.cancel()
        mCancelRestart?.cancel()
        mcancelOpenCloseJob?.cancel()
    }

    /**
     * 添加开关机任务
     */
    fun addOpenCloseTime(
        startDeviceTime: String?,
        closeDeviceTime: String?,
        @DeviceType.DeviceType deviceType: String?
    ) {
        checkNotNull()
        if (startDeviceTime.isNullOrEmpty() || closeDeviceTime.isNullOrEmpty()) {
            throw RuntimeException("params is not can be null")
        }
        this.deviceTypes = deviceType
        maddOpenCloseJob = GlobalScope.launch {
            withContext(Dispatchers.Default) {
                mContext?.let {
                    var mRebootOptDB = RebootOptDB()
                    mRebootOptDB.startDeviceTime = startDeviceTime
                    mRebootOptDB.closeDeviceTime = closeDeviceTime
                    mRebootOptDB.deviceType = deviceType
                    mRebootOptDB.mOptType = OptType.TASKTYPE_SWITCH_MACHINE
                    RebootDataSingle.instance.getDao(it).mRebootOptDao.insert(mRebootOptDB)
                    var mRebootOptDBResult: RebootOptDB? =
                        RebootDataSingle.instance.getDao(it).mRebootOptDao.selectOptData(OptType.TASKTYPE_SWITCH_MACHINE)
                    mRebootOptDBResult?.let {
                        when (it.deviceType) {
                            DeviceType.MODULE_ANDROID_HE_ZI -> {
                                getAndroidHeziDeviceOptImpl()
                                    ?.startCloseDevice(it.startDeviceTime, it.closeDeviceTime)
                            }
                            DeviceType.MODULE_SHRG -> {
                                getISHRGDeviceOpt()
                                    ?.startCloseDevice(it.startDeviceTime, it.closeDeviceTime)

                            }
                            DeviceType.MODULE_JINGXIN -> {
                                getJingXinDeviceOptImpl()
                                    ?.startCloseDevice(it.startDeviceTime, it.closeDeviceTime)
                            }
                            DeviceType.MODULE_HEZI0830B -> {
                                getI0830BDeviceOptImpl()
                                    ?.startDevice(it.startDeviceTime)
                                getI0830BDeviceOptImpl()
                                    ?.closeDevice(it.closeDeviceTime)
                            }
                            else -> {}
                        }

                    }
                }
            }
        }


    }

    fun cancelOpenCloseTime(
        @DeviceType.DeviceType deviceType: String?
    ) {
        checkNotNull()
        mcancelOpenCloseJob = GlobalScope.launch {
            withContext(Dispatchers.Default) {
                var mRebootOptDBs: RebootOptDB? =
                    RebootDataSingle.instance.getDao(mContext!!).mRebootOptDao.selectOptData(OptType.TASKTYPE_SWITCH_MACHINE)
                mRebootOptDBs?.let {
                    when (deviceType) {
                        DeviceType.MODULE_ANDROID_HE_ZI -> {
                            getAndroidHeziDeviceOptImpl()?.cancelStartCloseDevice()
                        }
                        DeviceType.MODULE_SHRG -> {
                            getISHRGDeviceOpt()
                                ?.cancelStartCloseDevice(it.startDeviceTime, it.closeDeviceTime)

                        }
                        DeviceType.MODULE_JINGXIN -> {
                            getJingXinDeviceOptImpl()
                                ?.cancelStartCloseDevice(it.startDeviceTime, it.closeDeviceTime)
                        }
                        DeviceType.MODULE_HEZI0830B -> {
                            getI0830BDeviceOptImpl()?.cancelStartCloseDevice()
                        }
                        else -> {}
                    }
                    RebootDataSingle.instance.getDao(mContext!!).mRebootOptDao.deleteOptData(OptType.TASKTYPE_SWITCH_MACHINE)

                }
            }
        }

    }

}


