package com.itc.switchdevicecomponent.impl

import com.itc.commoncomponent.network.ResultData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IDeviceService {


    @GET("itcsaas/apps/device/api/v1/device_execute_time")
    suspend fun device_execute_time(
        @Query("device_sn") device_sn: String?
    ): ResultData<DeviceExecute>
}