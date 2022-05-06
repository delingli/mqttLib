package com.itc.switchdevicecomponent

import android.content.Context
import com.itc.switchdevicecomponent.annation.OptType
import com.itc.switchdevicecomponent.basic.*
import com.itc.switchdevicecomponent.impl.RebootOpt
import com.itc.switchdevicecomponent.impl.ScreenOpt
import com.itc.switchdevicecomponent.impl.SendredOpt
import com.itc.switchdevicecomponent.impl.SwitchMachineOpt
import java.lang.RuntimeException

object DeviceOptManager {
    var mContext: Context? = null
    var mIRebootOpt: IRebootOpt? = null
    var mIScreenOpt: IScreenOpt? = null
    var mISendredOpt: ISendredOpt? = null
    var mISwitchMachineOpt: ISwitchMachineOpt? = null
    fun toInit(context: Context) {
        this.mContext = context
        if (mContext == null) {
            throw RuntimeException("Context is not can be null")
        }
        this.mIRebootOpt = RebootOpt(context)
        this.mIScreenOpt = ScreenOpt(context)
        this.mISendredOpt = SendredOpt(context)
        this.mISwitchMachineOpt = SwitchMachineOpt(context)


    }

    fun getRebootOpt(): IRebootOpt? {
        return mIRebootOpt
    }

    fun getScreenOpt(): IScreenOpt? {
        return mIScreenOpt
    }

    fun getSendredOpt(): ISendredOpt? {
        return mISendredOpt
    }

    fun getSwitchMachineOpt(): ISwitchMachineOpt? {
        return mISwitchMachineOpt
    }

    fun unInit() {
        if (null != mIRebootOpt) {
            mIRebootOpt = null
        }
        if (null != mIScreenOpt) {
            mIScreenOpt = null
        }
        if (null != mISendredOpt) {
            mISendredOpt = null
        }
        if (null != mISwitchMachineOpt) {
            mISwitchMachineOpt = null
        }
    }
/*
    fun handlerOpt(@OptType.OptTypeFlagDef optType: Int) {
        if (optType == OptType.TASKTYPE_REBOOT) {
            var mRebootOpt: IRebootOpt = RebootOpt(mContext)
            mRebootOpt.re
        }

    }*/


}