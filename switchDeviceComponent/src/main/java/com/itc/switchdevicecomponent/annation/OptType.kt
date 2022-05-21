package com.itc.switchdevicecomponent.annation

import android.annotation.SuppressLint
import androidx.annotation.IntDef

//操作类型，开关机操作，重启操作
class OptType {
    companion object {
        const val TASKTYPE_UNKNOW = -1     //未知
        const val TASKTYPE_REBOOT = 1     //定时重启
        const val TASKTYPE_SWITCH_MACHINE = 2    //开关机
    }

    @OptTypeFlagDef
    var flag = TASKTYPE_UNKNOW

    @MustBeDocumented
    @IntDef(
        TASKTYPE_UNKNOW,
        TASKTYPE_REBOOT,
        TASKTYPE_SWITCH_MACHINE,
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OptTypeFlagDef
}