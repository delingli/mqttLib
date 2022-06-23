package com.itc.switchdevicecomponent.impl

data class DeviceExecute(
    val end_date_time: String?,//开机   不要问为什么这么起名字，php这么干的，
    val restart_date_time: String?,
    val start_date_time: String?//关机  不要问为什么这么起名字，php这么干的，
)