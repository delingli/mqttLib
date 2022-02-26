package com.itc.recordecomponent

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.itc.commoncomponent.network.ResultData
import com.itc.commoncomponent.network.Results
import com.itc.commoncomponent.network.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class FloatViewModel(private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default) {
    companion object {
        val TAG = "FloatViewModel"
    }

    fun getSecondTimestampTwo(date: Date?): Int {
        if (null == date) {
            return 0
        }
        val timestamp = java.lang.String.valueOf(date.time / 1000)
        return Integer.valueOf(timestamp)
    }

    fun getMD5Str(str: String?): String? {
        var digest: ByteArray? = null
        try {
            val md5: MessageDigest = MessageDigest.getInstance("md5")
            digest = md5.digest(str?.toByteArray(charset("utf-8")))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        //16是表示转换为16进制数
        return BigInteger(1, digest).toString(16)
    }

    //获取会议列表
    suspend fun openOff(
        meeting_id: String?,
        status: String
    ): Results<ResultData<String>> {
        return withContext(defaultDispatcher) {
            try {
                // 举例：md5(md5(meeting_id + status + timestamp + "TQcsWgh@fbNM@O&L"))
                var timestamp: String = getSecondTimestampTwo(Date()).toString()
                var str = meeting_id + status + timestamp + "TQcsWgh@fbNM@O&L"
                LogUtils.dTag(TAG, str)
                var realSign =  getMD5Str(getMD5Str(str))

//                var realSign = EncryptUtils.encryptMD5ToString(EncryptUtils.encryptMD5ToString(str))
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