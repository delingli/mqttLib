package com.johnson.arcface2camerax.faceserver

import android.content.Context
import android.util.Log

import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.FaceFeature
import com.arcsoft.face.FaceSimilar
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.johnson.arcface2camerax.model.FaceTempInfo
import com.johnson.arcface2camerax.utils.ConfigUtil
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 人脸库操作类，包含注册和搜索
 */
class FaceServer {

    /**
     * 是否正在搜索人脸，保证搜索操作单线程进行
     */
    private val TAG = "FaceServer"
    private val saveNumber = 10
    private val SIMILAR_THRESHOLD = 0.8f
    private val leave = 2000
    private var isProcessing = AtomicBoolean(false)

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @return 是否初始化成功
     */
    @Synchronized
    fun init(context: Context?): Boolean {
        if (context != null && faceEngine == null) {
            faceEngine = FaceEngine()
            val engineCode = faceEngine?.init(
                    context,
                    DetectMode.ASF_DETECT_MODE_VIDEO,
                    DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                    16,
                    10,
                    FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_FACE_DETECT
            )
            if (engineCode == ErrorInfo.MOK) {
                return true
            } else {
                faceEngine = null
                Log.e(TAG, "init: failed! code = $engineCode")
                return false
            }
        }
        return false
    }

    /**
     * 销毁
     */
    @Synchronized
    fun unInit() {
        faceTempInfoList.clear()
        faceEngine?.unInit()
        faceEngine = null
    }


    /**
     * 人脸是否存在缓存特征库中
     *
     * @return 是否存在
     */
    @Synchronized
    fun faceIsExistList(faceFeature: FaceFeature?): Boolean {
        synchronized(this) {
            if (faceEngine == null || isProcessing.get() || faceFeature == null) {
                return true
            }

            isProcessing.set(true)

            //移除已经离开的人脸
            faceTempInfoList.forEach {
                if (Date().time - it.addTime.time > leave) {
                    faceTempInfoList.remove(it)
                }
            }

            val tempFaceFeature = FaceFeature()
            val faceSimilar = FaceSimilar()
            var maxSimilar = 0f
            var maxSimilarIndex = -1

            for (i in faceTempInfoList.indices) {
                tempFaceFeature.featureData = faceTempInfoList[i].featureData
                faceEngine?.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar)
                if (faceSimilar.score > maxSimilar) {
                    maxSimilar = faceSimilar.score
                    maxSimilarIndex = i
                }
            }

            //暂存列表为空时候时直接加入
            if (faceTempInfoList.size == 0) {
                faceTempInfoList.add(FaceTempInfo(faceFeature.featureData, Date()))
            }

            if (maxSimilar < SIMILAR_THRESHOLD) {
                //如果长度不够移除最早添加的人员
                if (faceTempInfoList.size >= saveNumber) {
                    val temp =
                        faceTempInfoList.maxByOrNull { it: FaceTempInfo? -> Date().time - it!!.addTime.time }
                    faceTempInfoList.remove(temp)
                }
                faceTempInfoList.add(FaceTempInfo(faceFeature.featureData, Date()))

            } else {
                //找到该人员在列表中 修改添加事件
                faceTempInfoList[maxSimilarIndex].addTime = Date()
            }

            isProcessing.set(false)
            return maxSimilar > SIMILAR_THRESHOLD
        }
    }

    companion object {
        private var faceServer: FaceServer? = null
        private var faceEngine: FaceEngine? = null
        private var faceTempInfoList: CopyOnWriteArrayList<FaceTempInfo> = CopyOnWriteArrayList()


        val instance: FaceServer
            @Synchronized get() {
                val temp = faceServer ?: FaceServer()
                faceServer = temp
                return temp
            }
    }

}
