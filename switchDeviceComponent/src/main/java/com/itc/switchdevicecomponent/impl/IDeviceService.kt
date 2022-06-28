package com.itc.switchdevicecomponent.impl

import com.itc.commoncomponent.network.ResultData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface IDeviceService {


    @GET()
    suspend fun device_execute_time(
        @Url url: String,
        @Query("device_sn") device_sn: String?
    ): ResponseBody
}