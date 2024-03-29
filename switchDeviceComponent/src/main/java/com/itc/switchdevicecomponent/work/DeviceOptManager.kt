package com.itc.switchdevicecomponent.work

import android.content.Context
import androidx.work.*
import com.blankj.utilcode.util.SPUtils
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.basic.*
import com.itc.switchdevicecomponent.impl.*
import com.itc.switchdevicecomponent.rooms.RebootDataSingle
import com.itc.switchdevicecomponent.rooms.RebootOptDB
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

object DeviceOptManager {

    var mCancelRestart: Job? = null
    var mcancelOpenCloseJob: Job? = null
    var mSwitchDeviceOption: SwitchDeviceOption? = null
    var mContext: Context? = null
    var mI0830BDeviceOptImpl: I0830BDeviceOpt? = null
    var mIAndroidHeziDeviceOptImpl: IAndroidHeziDeviceOpt? = null
    var mIJingXinDeviceOptImpl: IJingXinDeviceOpt? = null
    var mISHRGDeviceOptImpl: ISHRGDeviceOpt? = null
    var mISKDeviceOpt: ISKDeviceOpt? = null
    var mIHK8305DeviceOpt: IHK8305DeviceOpt? = null
    var mHKBasicDeviceOpt: IHKBasicDeviceOpt? = null
    fun getHK8305DeviceOpt(): IHK8305DeviceOpt? {
        checkNotNull()
        return mIHK8305DeviceOpt
    }

    fun getHKBasicDeviceOpt(): IHKBasicDeviceOpt? {
        checkNotNull()
        return mHKBasicDeviceOpt
    }

    fun getSkDeviceOpt(): ISKDeviceOpt? {
        checkNotNull()
        return mISKDeviceOpt
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

    fun getSwitchDeviceOption(): SwitchDeviceOption? {
        return mSwitchDeviceOption
    }

    fun checkNotNull() {
        if (mSwitchDeviceOption == null) {
            throw RuntimeException("mSwitchDeviceOption is not can be null")
        }
    }

    fun toInit(context: Context?, mSwitchDeviceOption: SwitchDeviceOption?) {
        this.mSwitchDeviceOption = mSwitchDeviceOption
        this.mContext = context
        checkNotNull()
        this.mI0830BDeviceOptImpl = I0830BDeviceOptImpl(mContext)
        this.mIAndroidHeziDeviceOptImpl = IAndroidHeziDeviceOptImpl(mContext)
        this.mIJingXinDeviceOptImpl = IJingXinDeviceOptImpl(mContext)
        this.mISHRGDeviceOptImpl = ISHRGDeviceOptImpl(mContext)
        this.mISKDeviceOpt = ISKDeviceOptImpl(mContext)
        this.mIHK8305DeviceOpt = IHK8305DeviceOptImpl(mContext)
        this.mHKBasicDeviceOpt = IHKBasicDeviceOptImpl(mContext)
        SPUtils.getInstance().put(SwitchMachineWork.deviceType, mSwitchDeviceOption?.deviceType())
    }

    fun hasInited(): Boolean {
        if (this.mContext != null && this.mSwitchDeviceOption != null) {
            return true
        } else {
            return false
        }

    }

    fun flushDeviceTime() {
        if (this.mSwitchDeviceOption == null) {
            throw RuntimeException("you must call toInit(context: Context, mSwitchDeviceOption: SwitchDeviceOption) to init ")
        }
        if (this.mContext == null) {
            throw RuntimeException("mContext can not be null ")
        }
        this.mContext?.let {
            val constraints = Constraints.Builder()
//                .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.CONNECTED).build()
            val dailyWorkRequest = OneTimeWorkRequestBuilder<SwitchMachineWork>()
                .setConstraints(constraints).setInitialDelay(200, TimeUnit.MILLISECONDS)
                .addTag(RebotWork.TAG_OUTPUT).build()
            WorkManager.getInstance(it.applicationContext).enqueueUniqueWork(
                SwitchMachineWork.CustomRebotWork,
                ExistingWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        }


    }

    fun cancelRestartDevice(
        @DeviceType.DeviceType deviceType: String?
    ) {
        checkNotNull()
        mCancelRestart = GlobalScope.launch {
            withContext(Dispatchers.IO) {
                WorkManager.getInstance(mContext!!.applicationContext)
                    .cancelUniqueWork(RebotWork.REBORT_TAG)
                RebootDataSingle.instance.getDao(mContext!!).mRebootOptDao.deleteOptData(
                    OptType.TASKTYPE_REBOOT
                )
            }
        }

    }

    fun unInit() {
        this.mCancelRestart?.cancel()
        this.mcancelOpenCloseJob?.cancel()
    }

    fun cancelOpenCloseTime(
        @DeviceType.DeviceType deviceType: String?
    ) {
        checkNotNull()
        this.mcancelOpenCloseJob = GlobalScope.launch {
            withContext(Dispatchers.Default) {
                var mRebootOptDBs: RebootOptDB? =
                    RebootDataSingle.instance.getDao(mContext!!).mRebootOptDao.selectOptData(
                        OptType.TASKTYPE_SWITCH_MACHINE
                    )
                mRebootOptDBs?.let {
                    when (deviceType) {
                        DeviceType.MODULE_HKBASIC -> {
                            getHKBasicDeviceOpt()?.cancelStartCloseDevice()
                        }
                        DeviceType.MODULE_HK8305 -> {
                            getHK8305DeviceOpt()?.cancelStartCloseDevice()
                        }
                        DeviceType.MODULE_SK -> {
                            getSkDeviceOpt()?.cancelStartCloseDevice(
                                it.startDeviceTime,
                                it.closeDeviceTime
                            )
                        }
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
                    RebootDataSingle.instance.getDao(mContext!!).mRebootOptDao.deleteOptData(
                        OptType.TASKTYPE_SWITCH_MACHINE
                    )

                }
            }
        }

    }
}