package com.itc.recordecomponent

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.itc.commoncomponent.network.ResultData
import com.itc.commoncomponent.network.Results
import com.itc.commoncomponent.network.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FloatViewModel(private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default) {
    companion object {
        val TAG = "FloatViewModel"
    }

    //获取会议列表
    suspend fun openOff(
        meeting_id: String?,
        status: String,
        timestamp: String,
    ): Results<ResultData<String>> {
        return withContext(defaultDispatcher) {
            try {
                // 举例：md5(md5(meeting_id + status + timestamp + "TQcsWgh@fbNM@O&L"))
                var str = meeting_id + status + timestamp + "TQcsWgh@fbNM@O&L"
                LogUtils.dTag(TAG, str)
                var realSign = EncryptUtils.encryptMD2ToString(EncryptUtils.encryptMD2ToString(str))
                LogUtils.dTag(TAG, "realSign" + realSign)
                Results.Sucess(
                    RetrofitClient.retrofit.create(IFloatViewService::class.java)
                        .openOff(meeting_id, status, timestamp, realSign)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Results.Error(e)
            }
        }
    }

}