package com.itc.switchdevicecomponent.impl

import com.blankj.utilcode.util.LogUtils
import com.itc.commoncomponent.network.ResultData
import com.itc.commoncomponent.network.Results
import com.itc.commoncomponent.network.RetrofitClient
import com.itc.switchdevicecomponent.work.DeviceOptManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceOptModel(private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default) {
    companion object {
        val TAG = "FloatViewModel"
    }


    suspend fun flushDeviceTime(): Results<ResultData<DeviceExecute>>? {
        var result: Results<ResultData<DeviceExecute>>?=null
        DeviceOptManager.getSwitchDeviceOption()?.let {
            withContext(defaultDispatcher) {
                try {
                    result = Results.Sucess(
                        RetrofitClient.getRetrofitSS(it.configUrl)
                            .create(IDeviceService::class.java)
                            .device_execute_time(it.device_sn)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtils.dTag(TAG, "" + e.message + e.toString() + e?.message)
                    result = Results.Error(e)
                }
            }


        }
        return result
    }


}