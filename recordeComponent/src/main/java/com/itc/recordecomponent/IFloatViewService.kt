package com.itc.recordecomponent

import com.itc.commoncomponent.network.ResultData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
interface IFloatViewService {


    @POST("api/record/switchRecordPublic")
    suspend fun openOff(
        @Query("meeting_id") meeting_id: String?,
        @Query("status") status: String,
        @Query("timestamp") timestamp: String,
        @Query("sign") sign: String?
    ): ResultData<String>
}