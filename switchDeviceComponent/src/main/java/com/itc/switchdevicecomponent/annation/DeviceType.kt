package com.itc.switchdevicecomponent.annation

import androidx.annotation.StringDef

class DeviceType {
    companion object {
        const val MODULE_HRA = "HRA"
        const val MODULE_HUAKE = "HUAKE"
        const val MODULE_HEZI = "HEZI"
        const val MODULE_LITIJI = "LITIJI"
        const val MODULE_JINGXIN = "JINGXIN"
        const val MODULE_XINGMA = "XINGMA"
        const val MODULE_XINGSHEN = "XINGSHEN"
        const val MODULE_SHRG = "SHRG"
        const val MODULE_CW = "CW"
        const val MODULE_ANDROID_HE_ZI = "ANDROID_HE_ZI"
    }

    @StringDef(
        MODULE_HRA,
        MODULE_HUAKE,
        MODULE_HEZI,
        MODULE_LITIJI,
        MODULE_JINGXIN,
        MODULE_XINGMA,
        MODULE_XINGSHEN,
        MODULE_CW,
        MODULE_ANDROID_HE_ZI
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class DeviceType

}