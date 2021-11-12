package com.johnson.arcface2camerax.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectMode
import com.arcsoft.face.util.ImageUtils
import com.johnson.arcface2camerax.utils.face.FaceHelper
import com.johnson.arcfacedemo.arcface.utils.face.FaceListener
import kotlin.concurrent.thread

/**
 * @ClassName FaceUtils
 * @Description TODO
 * @Author WangShunYi
 * @Date 2019-11-19 13:41
 */
open class FaceUtils(val context: Context, val backFaceFeatureListener: ((data: ByteArray?, trackId: Int?) -> Unit)) {
    open var faceEngine: FaceEngine? = null
    open val TAG = FaceUtils::class.java.simpleName
    open var faceHelper: FaceHelper? = null
    open val MAX_DETECT_NUM = 50     //最大识别人脸数和最大识别线程数
    open var faceListener: FaceListener? = null
    open var afCode = -1


    init {
        initFaceEngine()
    }

    /**
     * 初始化引擎
     */
    open fun initFaceEngine() {
        faceEngine = FaceEngine()
        afCode = faceEngine?.init(
            context,
            DetectMode.ASF_DETECT_MODE_VIDEO,
            ConfigUtil.getFtOrient(context),
            16,
            MAX_DETECT_NUM,
            FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_FACE_DETECT
        ) ?: -1
        //val versionInfo = VersionInfo()
        //faceEngine?.getVersion(versionInfo)
        //Log.i(TAG, "initEngine:  init: $afCode  version:$versionInfo")

        if (afCode != ErrorInfo.MOK) {
//            Toast.makeText(context, context.getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show()
           // Log.i(TAG, "initEngineError:  init: $afCode  version:$versionInfo")
            return
        }

        faceListener = object : FaceListener {
            override fun onFail(e: Exception, requestId: Int?) {
                Log.e(TAG, "onFail: " + e.message)
                backFaceFeatureListener(null, requestId)
            }

            override fun onFaceFeatureInfoGet(faceFeature: FaceFeature?, requestId: Int) {
                backFaceFeatureListener(faceFeature?.featureData, requestId)
            }

        }

        faceHelper = FaceHelper.Builder()
            .faceEngine(faceEngine)
            .frThreadNum(MAX_DETECT_NUM)
            .faceListener(faceListener)
            .build()
    }

    /**
     * 销毁引擎
     */
    open fun unInitEngine() {
        faceEngine ?: return
        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine!!.unInit()
            Log.i(TAG, "unInitEngine: $afCode")
        }
    }

    open fun unInit() {
        if (faceHelper != null) {
            synchronized(faceHelper!!) {
                unInitEngine()
            }
            faceHelper?.release()
        } else {
            unInitEngine()
        }
    }

    /**
     * bitmap 需要获取特征码的图片
     * trackId 图片的标记值
     */
    open fun getFaceCodeInImage(bitmap: Bitmap?, trackId: Int?) {
        if (bitmap == null || trackId == null) return
        thread {
            var tempBitmap = bitmap
            val width = tempBitmap.width - tempBitmap.width % 4
            val height = tempBitmap.height

            //图片不是4的倍数需要裁减
            if (tempBitmap.width % 4 != 0) {
                tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, width, height, null, false)
            }
            val bitmapToBgr24 = ImageUtils.bitmapToBgr24(tempBitmap)
            val faceInfoList = faceHelper?.findFaceByImage(bitmapToBgr24, width, height, trackId)
            //未找到人脸直接回掉
            if (faceInfoList.isNullOrEmpty())
                backFaceFeatureListener(null, trackId)

            faceInfoList?.forEach { faceInfo: FaceInfo ->
                faceHelper?.requestFaceFeature(
                    bitmapToBgr24,
                    faceInfo,
                    width,
                    height,
                    FaceEngine.CP_PAF_BGR24,
                    trackId
                )
            }
        }

    }

    /**
     * 对比两个特征码的相似度
     */
    open fun comparisonCode(firstCode: ByteArray?, secondCode: ByteArray?): Float? {
        if (firstCode == null || secondCode == null) return null
        val firstFaceFeature = FaceFeature(firstCode)
        val secondFaceFeature = FaceFeature(secondCode)

        val faceSimilar = FaceSimilar()
        faceEngine ?: return null
        synchronized(faceEngine!!) {
            faceEngine?.compareFaceFeature(firstFaceFeature, secondFaceFeature, faceSimilar)
        }

        return faceSimilar.score
    }
}