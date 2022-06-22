package com.itc.switchdevicecomponent.annation

import android.annotation.SuppressLint
import androidx.annotation.IntDef

//操作模式，单次，每天，自定义周期
@Deprecated("废弃...")
class OptMode {
    companion object {
        const val SINGLE = 1     //单次
        const val EVERYDAY = 2     //每天
        const val CUSTOMCYCLEDAY = 3    //自定义周期时间
    }

    @OptModeDef
    var flag = SINGLE

    @MustBeDocumented
    @IntDef(
        SINGLE,
        EVERYDAY,
        CUSTOMCYCLEDAY
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OptModeDef
}