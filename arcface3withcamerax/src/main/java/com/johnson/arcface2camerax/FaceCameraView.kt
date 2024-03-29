package com.johnson.arcface2camerax

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.hardware.Camera
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.johnson.arcface2camerax.faceserver.FaceServer
import com.johnson.arcface2camerax.model.DrawInfo
import com.johnson.arcface2camerax.utils.DrawHelper
import com.johnson.arcface2camerax.utils.ImageUtil
import com.johnson.arcface2camerax.utils.extensions.bindLifeCycle
import com.johnson.arcface2camerax.utils.extensions.hasPermission
import com.johnson.arcface2camerax.utils.face.FaceHelper
import com.johnson.arcface2camerax.utils.face.RequestFeatureStatus
import com.johnson.arcface2camerax.widget.FaceRectView
import com.johnson.arcface2camerax.utils.ConfigUtil
import com.johnson.arcface2camerax.widget.RoundTextureView
import com.johnson.arcfacedemo.arcface.utils.face.FaceListener
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.NotNull
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @ClassName FaceCameraView
 * @Description TODO
 * @Author WangShunYi
 * @Date 2019-08-02 10:43
 */

open class FaceCameraView : RelativeLayout, LifecycleObserver {
    open var lifecycleOwner: LifecycleOwner? = null
    open var textureView: RoundTextureView? = null
    open var faceRectView: FaceRectView
    open var cameraId: Int = 0


    /**
     * 所需的所有权限信息
     */
    open val NEEDED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    open var lensFacing = CameraX.LensFacing.BACK
    public open var faceEngine: FaceEngine? = null
    open var afCode = -1
    open val MAX_DETECT_NUM = 5
    open val TAG = "FaceCameraView"
    open var faceHelper: FaceHelper? = null
    var previeWidth = -1
    var previeHeight = -1
    open val requestFeatureStatusMap = ConcurrentHashMap<Int, Int>()
    open var drawHelper: DrawHelper? = null
    open var isGetFaceId = AtomicBoolean(false)

    open val isBackFaceCodeNow = AtomicBoolean(false)
    open var tempWidth = -1
    open var tempHeight = -1
    var tempdata: ByteArray? = null
    open var temprect: Rect? = null
    open var firstOne = true
    var tempbitmap: Bitmap? = null
    private var backFaceFeatureListener: ((data: ByteArray) -> Unit)? = null
    open var disposable: Disposable? = null
    open var disposable2: Disposable? = null
    open var onPAUSE = false
    open var singleTask = Executors.newSingleThreadExecutor()

    @JvmOverloads
    constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        textureView = RoundTextureView(context)
        faceRectView = FaceRectView(context)
        val ttvlp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(textureView, ttvlp)
        addView(faceRectView, ttvlp)


    }

    fun setRadius(radius: Int) {
        textureView?.radius = radius
        textureView?.turnRound()
    }

    /**
     * 初始化环境
     */
    open fun initEnvironment() {
        if (!context.hasPermission(*NEEDED_PERMISSIONS)) {
            Toast.makeText(context, R.string.permission_denied1, Toast.LENGTH_SHORT).show()
            return
        }
        if (lifecycleOwner == null) {
            Toast.makeText(context, R.string.bind_fail, Toast.LENGTH_LONG).show()
            return
        }
        initEngine()
        initCamera()
    }


    /**
     * 初始化引擎
     */
    open fun initEngine() {
        if (faceEngine == null) {
            faceEngine = FaceEngine()
            afCode = faceEngine?.init(
                context,
                DetectMode.ASF_DETECT_MODE_VIDEO,
                DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16,
                MAX_DETECT_NUM,
                FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_FACE_DETECT
            ) ?: -1
            //val versionInfo = VersionInfo()
            //faceEngine?.getVersion(versionInfo)
            //Log.i(TAG, "initEngine:  init: $afCode  version:$versionInfo")

            if (afCode != ErrorInfo.MOK) {
                Toast.makeText(
                    context,
                    context.getString(R.string.init_failed, afCode),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    /**
     * 初始化相机预览
     */
    open fun initCamera() {
        //星神屏摄像头默认是前置，其他屏都是后置
        if (BuildConfig.BUILD_TYPE.equals("xingshenDebug") || BuildConfig.BUILD_TYPE.equals("xingshen") || BuildConfig.BUILD_TYPE.equals("yitiji")) {
            lensFacing = CameraX.LensFacing.FRONT
            cameraId = 1
        }
        //相机数量为2则打开1,1则打开0,相机ID 1为前置，0为后置
        //若指定了相机ID且该相机存在，则打开指定的相机
//        if (specificCameraId != null && specificCameraId <= cameraId) {
//            cameraId = specificCameraId
//        }

        //没有相机
        if (cameraId == -1) {
            Log.e(TAG, "没有相机")
            return
        }
        textureView?.post { startCamera() }

        textureView?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

    }

    /**
     * 开启相机
     */
    open fun startCamera() = try {
        val metrics = DisplayMetrics().also { textureView?.display!!.getRealMetrics(it) }
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "cameraId:${cameraId}")
        if (lensFacing == CameraX.LensFacing.BACK) {
            Log.d(TAG, "BACK")
        } else {
            Log.d(TAG, "FRONT")
        }
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(textureView?.display!!.rotation)
//                setTargetResolution(Size(tv_viewFinder.width, tv_viewFinder.height))
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = textureView?.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)
            textureView!!?.setSurfaceTexture(it.surfaceTexture)
//            textureView?.surfaceTexture=it.surfaceTexture
            updateTransform()
        }


        CameraX.bindToLifecycle(lifecycleOwner, buildImageAnalysisUseCase(), preview)
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e(TAG, e.toString())
        Toast.makeText(context, "打开相机失败!", Toast.LENGTH_LONG).show()
    }

    open fun buildImageAnalysisUseCase(): ImageAnalysis {

        val metrics = DisplayMetrics().also { textureView?.display?.getRealMetrics(it) }
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        // 分析器配置 Config 的建造者
        val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
        val analysisConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(lensFacing)
            // 分辨率
//            setTargetResolution(Size(tv_viewFinder.width, tv_viewFinder.height))
            // 宽高比例
            setTargetAspectRatio(screenAspectRatio)
            // 图像渲染模式
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            // 设置回调的线程
            setCallbackHandler(Handler(analyzerThread.looper))
            setTargetRotation(textureView?.display!!.rotation)
        }.build()

        // 创建分析器 ImageAnalysis 对象
        val analysis = ImageAnalysis(analysisConfig)

        // setAnalyzer 传入实现了 analyze 接口的类
        analysis.setAnalyzer { image, _ ->
            // 可以得到的一些图像信息，参见 ImageProxy 类相关方法
            temprect = image.cropRect
//            val type = image.format
//            previeWidth = image.width
//            previeHeight = image.height
            tempWidth = image.width
            tempHeight = image.height
            if (firstOne) {
                firstOne = false
                previeWidth = tempWidth
                previeHeight = tempHeight
                initFaceHelp()
            }

//            val buffer = image.planes[0].buffer
//            val bytes = ByteArray(buffer.remaining())
//            buffer.get(bytes)
            if (!onPAUSE && !isGetFaceId.get()) {
                isGetFaceId.set(true)

                tempdata = ImageUtil.getBytesFromImageAsType(image.image, ImageUtil.NV21)
//                doGetFaceCode(tempdata, tempWidth, tempHeight)
                try {
                    if (!singleTask.isShutdown) {
                        singleTask.execute {
                            doGetFaceCode(tempdata, previeWidth, previeHeight)
                        }
                    }
                } catch (e: Exception) {

                }

            }

        }
        return analysis
    }

    open fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = textureView!!.width / 2f
        val centerY = textureView!!.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (textureView?.display!!.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        textureView?.setTransform(matrix)
    }

    /**
     * 在相机初始化后初始化人脸识别工具
     */
    open fun initFaceHelp() {
        //如果相机未初始化
        if (previeWidth == -1 || previeHeight == -1) return
        val faceListener = object : FaceListener {
            override fun onFail(e: Exception, requestId: Int?) {
                Log.e(TAG, "onFail: " + e.message)
            }

            override fun onFaceFeatureInfoGet(faceFeature: FaceFeature?, requestId: Int) {
                //FR成功
                if (faceFeature != null) {
                    Log.e(
                        TAG,
                        "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId
                    )
                    searchFace(faceFeature)
                } else {
                    requestFeatureStatusMap[requestId] = RequestFeatureStatus.FAILED
                }//FR 失败
            }


        }
//        val matrix = Matrix()
//        tv_viewFinder.getTransform(matrix)
//
//        var a: FloatArray = FloatArray(10)
//        matrix.getValues(a)

        drawHelper = textureView?.display?.rotation?.let {
            DrawHelper(
                previeWidth,
                previeHeight,
                faceRectView.width,
                faceRectView.height,
                it,
                cameraId,
                false
            )
        }

        faceHelper = FaceHelper.Builder()
            .faceEngine(faceEngine)
            .frThreadNum(MAX_DETECT_NUM)
            .previewSize(previeWidth, previeHeight)
            .faceListener(faceListener)
            .currentTrackId(ConfigUtil.getTrackId(context))
            .build()
    }

    open fun doGetFaceCode(data: ByteArray?, width: Int, height: Int) {
        if (data == null) {
            isGetFaceId.set(false)
            return
        }
//            val yuv = YuvImage(data, ImageFormat.NV21, width, height, null)
//            val ops = ByteArrayOutputStream()
//            yuv.compressToJpeg(rect, 60, ops)
//            val data1 = ops.toByteArray()
//            tempbitmap = BitmapFactory.decodeByteArray(data1, 0, data1.size)
//
//            val a = 10


        faceRectView.clearFaceInfo()

        val facePreviewInfoList = faceHelper?.onPreviewFrame(data)
        if (facePreviewInfoList != null && drawHelper != null) {
            val drawInfoList = ArrayList<DrawInfo>()
            for (i in facePreviewInfoList.indices) {
                val name = faceHelper?.getName(facePreviewInfoList[i].trackId)
                drawInfoList.add(
                    DrawInfo(
                        facePreviewInfoList[i].faceInfo.rect,
                        GenderInfo.UNKNOWN,
                        AgeInfo.UNKNOWN_AGE,
                        LivenessInfo.UNKNOWN,
                        name ?: facePreviewInfoList[i].trackId.toString()
                    )
                )
            }
            drawHelper?.draw(faceRectView, drawInfoList)
        }

        if (facePreviewInfoList != null) {
            for (i in facePreviewInfoList.indices) {
                faceHelper?.requestFaceFeature(
                    data,
                    facePreviewInfoList[i].faceInfo,
                    width,
                    height,
                    FaceEngine.CP_PAF_NV21,
                    facePreviewInfoList[i].trackId
                )
            }
        }

        isGetFaceId.set(false)
    }


    open fun searchFace(frFace: FaceFeature) {
        if (isBackFaceCodeNow.get()) return
        isBackFaceCodeNow.set(true)
        val compareResult = FaceServer.instance.faceIsExistList(frFace)
        if (!compareResult) {
            backFaceFeatureListener?.invoke(frFace.featureData)
        }
        isBackFaceCodeNow.set(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(@NotNull owner: LifecycleOwner) {
        lifecycleOwner = owner
        //使用前释放相机
        CameraX.unbindAll()
    }

    /**
     * 当onResume的时候初始化相机
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
        onPAUSE = false
        firstOne = true
        Log.d("ALDL", "onResume...")
        //延迟初始化相机
        Handler().postDelayed({
            initEnvironment()
        }, 3000)
        //本地人脸库初始化
        FaceServer.instance.init(context)
    }

    /**
     * 当onPAUSE的时候释放资源
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPAUSE() {
        onPAUSE = true
        CameraX.unbindAll()
        Log.d("ALDL", "onResume...")
        //延迟1s释放资源
        Handler().postDelayed({
            if (disposable?.isDisposed == false) {
                disposable?.dispose()
            }
            if (disposable2?.isDisposed == false) {
                disposable2?.dispose()
            }

            if (faceHelper != null) {
                synchronized(faceHelper!!) {
                    unInitEngine()
                }
                ConfigUtil.setTrackId(context, faceHelper?.currentTrackId)
                faceHelper?.release()
            } else {
                unInitEngine()
            }

            FaceServer.instance.unInit()
            singleTask.shutdown()
        }, 1000)
    }

    open fun setBackFaceFeatureListener(listener: (data: ByteArray) -> Unit) {
        backFaceFeatureListener = listener
    }

    public open fun getBackFaceFeatureListener(): ((ByteArray) -> Unit)? {
        return backFaceFeatureListener;
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

    companion object {
        const val ISACTIVEKEY = "isActive"

        /**
         * 激活人脸识别引擎
         */
        @JvmStatic
        open fun activeFaceEngine(activity: ComponentActivity, appId: String, sdkKey: String) {
            val isActive = ConfigUtil.getBoolean(activity, ISACTIVEKEY, false)
            Log.i("FaceCameraView", "isActive: $isActive")
            if (isActive) return
            Observable.create(ObservableOnSubscribe<Int> { emitter ->
                //val faceEngine = FaceEngine()
                //val activeCode = faceEngine.active(activity, appId, sdkKey)
                val activeCode = FaceEngine.active(activity, appId, sdkKey)
                emitter.onNext(activeCode)
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .`as`(activity.bindLifeCycle())
                .subscribe {
                    if (it == ErrorInfo.MOK) {
                        Toast.makeText(activity, R.string.active_success, Toast.LENGTH_LONG).show()
                        ConfigUtil.setBoolean(activity, ISACTIVEKEY, true)
                    } else if (it == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                        Toast.makeText(activity, R.string.already_activated, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.active_failed, it),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

}