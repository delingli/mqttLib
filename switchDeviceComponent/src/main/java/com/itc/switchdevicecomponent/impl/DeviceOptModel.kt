package com.itc.switchdevicecomponent.impl

import com.blankj.utilcode.util.LogUtils
import com.itc.commoncomponent.network.ResultData
import com.itc.commoncomponent.network.Results
import com.itc.commoncomponent.network.RetrofitClient
import com.itc.switchdevicecomponent.work.DeviceOptManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject

class DeviceOptModel(private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default) {
    companion object {
        val TAG = "FloatViewModel"
    }


    suspend fun flushDeviceTime(): Results<ResultData<DeviceExecute>>? {
        var result: Results<ResultData<DeviceExecute>>? = null
        DeviceOptManager.getSwitchDeviceOption()?.let {
            withContext(defaultDispatcher) {
                result = try {
                    LogUtils.dTag(TAG, "it.configUrl${it.configUrl}it.device_sn${it.device_sn}")

                    var mResponseBody: ResponseBody = RetrofitClient.getRetrofit()
                        .create(IDeviceService::class.java)
                        .device_execute_time(it.configUrl, it.device_sn)

                    var mDeviceExecute = processData(mResponseBody.string())
                    var resultData = ResultData<DeviceExecute>()
                    resultData.code = 0
                    resultData.message = "sucess"
                    resultData.data = mDeviceExecute
                    Results.Sucess(resultData)

                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtils.dTag(TAG, "" + e.message + e.toString() + e?.message)
                    Results.Error(e)
                }
            }


        }
        return result
    }

    private fun processData(str: String): DeviceExecute? {
        LogUtils.dTag(TAG, "请求数据开始解析数据源${str}")
        var mDeviceExecute: DeviceExecute? = null
        var job = JSONObject(str)
        if (job.has("status_code")) {
            if (job.optInt("status_code") == 200) {
                val optJSONObject = job.optJSONObject("data")
                val restart_date_time:String? = optJSONObject.optString("restart_date_time",null)
                val end_date_time:String? = optJSONObject.optString("end_date_time",null)
                val start_date_time :String?= optJSONObject.optString("start_date_time",null)
                mDeviceExecute = DeviceExecute(end_date_time, restart_date_time, start_date_time)
                LogUtils.dTag(TAG, "status_code=200  解r析数据${mDeviceExecute.toString()}")
            } else {
                mDeviceExecute = DeviceExecute(null, null, null)
            }
        } else if (job.has("code")) {
            if (job.optInt("code") == 0) {
                val optJSONObject = job.optJSONObject("data")
                val restart_date_time = optJSONObject.optString("restart_date_time")
                val end_date_time = optJSONObject.optString("end_date_time")
                val start_date_time = optJSONObject.optString("start_date_time")
                mDeviceExecute = DeviceExecute(end_date_time, restart_date_time, start_date_time)
                LogUtils.dTag(TAG, "code=0解析数据${mDeviceExecute.toString()}")
            } else {
                mDeviceExecute = DeviceExecute(null, null, null)

            }
        } else {
            mDeviceExecute = DeviceExecute(null, null, null)

        }
        return mDeviceExecute
    }


}