package com.itc.switchdevicecomponent.annation

import androidx.annotation.StringDef

class DeviceType {
    companion object {
        const val MODULE_HEZI0830B = "HEZI0830B"    //0830B
        const val MODULE_JINGXIN = "JINGXIN"  //精鑫
        const val MODULE_SHRG = "SHRG"  //深海瑞格
        const val MODULE_ANDROID_HE_ZI = "ANDROID_HE_ZI"  //智微Android盒子
        const val MODULE_SK = "ANDROID_SK"  //视拓
    }

    @StringDef(
        MODULE_SHRG,
        MODULE_HEZI0830B,
        MODULE_JINGXIN,
        MODULE_ANDROID_HE_ZI,
        MODULE_SK
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class DeviceType

}