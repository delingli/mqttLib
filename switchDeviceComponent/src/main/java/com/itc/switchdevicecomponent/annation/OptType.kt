package com.itc.switchdevicecomponent.annation

import android.annotation.SuppressLint
import androidx.annotation.IntDef

class OptType {
    companion object {
        const val TASKTYPE_UNKNOW = -1     //未知
        const val TASKTYPE_REBOOT = 1     //定时重启
        const val TASKTYPE_SWITCH_MACHINE = 2    //开关机
        const val TASKTYPE_SCREEN = 3    //熄亮屏
        const val TASKTYPE_SHUTDOWN_0830 = 4    //0830B设备关机时间
        const val TASKTYPE_SENDRED = 5    //发送红外
    }

    @OptTypeFlagDef
    var flag = TASKTYPE_UNKNOW

    @MustBeDocumented
    @IntDef(
        TASKTYPE_UNKNOW,
        TASKTYPE_REBOOT,
        TASKTYPE_SWITCH_MACHINE,
        TASKTYPE_SCREEN,
        TASKTYPE_SHUTDOWN_0830,
        TASKTYPE_SENDRED
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OptTypeFlagDef
}