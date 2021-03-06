package com.johnson.arcface2camerax.nativeface;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.RuntimeABI;
import com.johnson.arcface2camerax.R;
import com.johnson.arcface2camerax.nativeface.faceserver.CompareResult;
import com.johnson.arcface2camerax.nativeface.faceserver.FaceServer;
import com.johnson.arcface2camerax.nativeface.model.DrawInfo;
import com.johnson.arcface2camerax.nativeface.model.FacePreviewInfo;
import com.johnson.arcface2camerax.nativeface.util.ConfigUtil;
import com.johnson.arcface2camerax.nativeface.util.DrawHelper;
import com.johnson.arcface2camerax.nativeface.util.camera.CameraHelper;
import com.johnson.arcface2camerax.nativeface.util.camera.CameraListener;
import com.johnson.arcface2camerax.nativeface.util.face.FaceHelper;
import com.johnson.arcface2camerax.nativeface.util.face.FaceListener;
import com.johnson.arcface2camerax.nativeface.util.face.LivenessType;
import com.johnson.arcface2camerax.nativeface.util.face.RecognizeColor;
import com.johnson.arcface2camerax.nativeface.util.face.RequestFeatureStatus;
import com.johnson.arcface2camerax.nativeface.util.face.RequestLivenessStatus;
import com.johnson.arcface2camerax.widget.RoundTextureView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public
class NewNativeFaceCameraView extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private Integer rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    /**
     * ?????????????????????????????????
     */
    private static final int REGISTER_STATUS_PROCESSING = 1;
    /**
     * ????????????????????????????????????????????????????????????
     */
    private static final int REGISTER_STATUS_DONE = 2;

    private int registerStatus = REGISTER_STATUS_DONE;
    /**
     * ???????????????????????????ms???
     */
    private static final long FAIL_RETRY_INTERVAL = 1000;
    /**
     * ????????????
     */
    private static final float SIMILAR_THRESHOLD = 0.7F;
    /**
     * ???FR??????????????????????????????FR?????????????????????
     */
    private static final int WAIT_LIVENESS_INTERVAL = 100;
    /**
     * VIDEO??????????????????????????????????????????????????????
     */
    private FaceEngine ftEngine;
    /**
     * ????????????????????????
     */
    private static final int MAX_RETRY_TIME = 5;
    /**
     * ???????????????????????????
     */
    private FaceEngine frEngine;
    /**
     * IMAGE????????????????????????????????????????????????????????????
     */
    private FaceEngine flEngine;
    private RoundTextureView mPreviewView;
    private FaceRectView faceRectView;
    private FaceHelper faceHelper;
    private CameraHelper cameraHelper;
    /**
     * ?????????????????????
     */
    private boolean livenessDetect = false;
    /**
     * ????????????????????????????????????
     */
    private static final int REGISTER_STATUS_READY = 0;

    public NewNativeFaceCameraView(@NonNull Context context) {
        this(context, null);

    }

    public NewNativeFaceCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NewNativeFaceCameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public NewNativeFaceCameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context context;

    public void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_native_faceview, this, true);
        mPreviewView = findViewById(R.id.single_camera_texture_preview);
        faceRectView = findViewById(R.id.single_camera_face_rect_view);
        mPreviewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        FaceServer.getInstance().init(context);
        compareResultList = new ArrayList<>();
    }

    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(context, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private Camera.Size previewSize;

    public void setRadius(int radius) {
        if (null != mPreviewView) {
            mPreviewView.setRadius(radius);
            mPreviewView.turnRound();
        }

    }

    public void release() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.clear();
        }
        if (delayFaceTaskCompositeDisposable != null) {
            delayFaceTaskCompositeDisposable.clear();
        }
        if (faceHelper != null) {
            ConfigUtil.setTrackedFaceCount(context, faceHelper.getTrackedFaceCount());
            faceHelper.release();
            faceHelper = null;
        }

        FaceServer.getInstance().unInit();
    }

    public FaceHelper getFaceHelper() {
        return faceHelper;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.clear();
        }
        if (delayFaceTaskCompositeDisposable != null) {
            delayFaceTaskCompositeDisposable.clear();
        }
        if (faceHelper != null) {
            ConfigUtil.setTrackedFaceCount(context, faceHelper.getTrackedFaceCount());
            faceHelper.release();
            faceHelper = null;
        }

        FaceServer.getInstance().unInit();
    }

    @Override
    public void onGlobalLayout() {
        mPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        AndPermission.with(context)
                .runtime()
                .permission(NEEDED_PERMISSIONS).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
//                initEngine();
//                initCamera();

            }
        }).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                Toast.makeText(context, "?????????", Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * ???????????????faceHelper??????????????????????????????????????????????????????????????????crash
     */
    private void unInitEngine() {
        if (ftInitCode == ErrorInfo.MOK && ftEngine != null) {
            synchronized (ftEngine) {
                int ftUnInitCode = ftEngine.unInit();
                Log.i(TAG, "unInitEngine: " + ftUnInitCode);
            }
        }
        if (frInitCode == ErrorInfo.MOK && frEngine != null) {
            synchronized (frEngine) {
                int frUnInitCode = frEngine.unInit();
                Log.i(TAG, "unInitEngine: " + frUnInitCode);
            }
        }
        if (flInitCode == ErrorInfo.MOK && flEngine != null) {
            synchronized (flEngine) {
                int flUnInitCode = flEngine.unInit();
                Log.i(TAG, "unInitEngine: " + flUnInitCode);
            }
        }
    }

    private static String TAG = "NewNativeFaceCameraView";
    private static final int MAX_DETECT_NUM = 10;
    private int ftInitCode = -1;
    private int frInitCode = -1;
    private int flInitCode = -1;
    /**
     * ?????????????????????
     */
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();

    public void initEngine(DetectFaceOrientPriority priority) {
        if (ftEngine == null) {
            ftEngine = new FaceEngine();
            ftInitCode = ftEngine.init(context, DetectMode.ASF_DETECT_MODE_VIDEO, priority,
                    16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT);
        }

        if (null == frEngine) {
            frEngine = new FaceEngine();
            frInitCode = frEngine.init(context, DetectMode.ASF_DETECT_MODE_IMAGE, priority,
                    16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);
        }

        if (null == flEngine) {
            flEngine = new FaceEngine();
            flInitCode = flEngine.init(context, DetectMode.ASF_DETECT_MODE_IMAGE, priority,
                    16, MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS);

            Log.i(TAG, "initEngine:  init: " + ftInitCode);
        }
        if (ftInitCode != ErrorInfo.MOK) {
            String error = context.getString(R.string.specific_engine_init_failed, "ftEngine", ftInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);
        }
        if (frInitCode != ErrorInfo.MOK) {
            String error = context.getString(R.string.specific_engine_init_failed, "frEngine", frInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);
        }
        if (flInitCode != ErrorInfo.MOK) {
            String error = context.getString(R.string.specific_engine_init_failed, "flEngine", flInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);
        }
    }

    protected void showToast(String s) {
        Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        List<DrawInfo> drawInfoList = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
            Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
            Integer recognizeStatus = requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId());

            // ?????????????????????????????????????????????
            int color = RecognizeColor.COLOR_UNKNOWN;
            if (recognizeStatus != null) {
                if (recognizeStatus == RequestFeatureStatus.FAILED) {
                    color = RecognizeColor.COLOR_FAILED;
                }
                if (recognizeStatus == RequestFeatureStatus.SUCCEED) {
                    color = RecognizeColor.COLOR_SUCCESS;
                }
            }
            if (liveness != null && liveness == LivenessInfo.NOT_ALIVE) {
                color = RecognizeColor.COLOR_FAILED;
            }

            drawInfoList.add(new DrawInfo(drawHelper.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()),
                    GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE, liveness == null ? LivenessInfo.UNKNOWN : liveness, color,
                    name == null ? String.valueOf(facePreviewInfoList.get(i).getTrackId()) : name));
        }
        drawHelper.draw(faceRectView, drawInfoList);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    public interface OnBackFaceFeatureListener {
        void onReceive(int trackId, byte[] result);
    }

    private OnBackFaceFeatureListener mOnBackFaceFeatureListener;

    public void addOnBackFaceFeatureListener(OnBackFaceFeatureListener onBackFaceFeatureListener) {
        this.mOnBackFaceFeatureListener = onBackFaceFeatureListener;
    }

    private DrawHelper drawHelper;

    public void initCamera(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFail: " + e.getMessage());
            }

            //??????FR?????????
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId, final Integer errorCode) {
                //FR??????
                if (faceFeature != null) {
//                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);
                    Integer liveness = livenessMap.get(requestId);
                    Log.e(TAG, "????????????:??????::: " + faceFeature.getFeatureData());
                    //??????????????????????????????????????????
                    if (mOnBackFaceFeatureListener != null) {
                        mOnBackFaceFeatureListener.onReceive(requestId, faceFeature.getFeatureData());
                    }

                    if (!livenessDetect) {
               /*         if (mOnBackFaceFeatureListener != null) {
                            mOnBackFaceFeatureListener.onReceive(requestId, faceFeature.getFeatureData());
                        }
*/

//                        searchFace(faceFeature, requestId);

                    }
                    //?????????????????????????????????
                    else if (liveness != null && liveness == LivenessInfo.ALIVE) {
//                        searchFace(faceFeature, requestId);
                    }
                    //??????????????????????????????????????????????????????????????????
                    else {
                        if (requestFeatureStatusMap.containsKey(requestId)) {
                            Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                    .subscribe(new Observer<Long>() {
                                        Disposable disposable;

                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            disposable = d;
                                            getFeatureDelayedDisposables.add(disposable);
                                        }

                                        @Override
                                        public void onNext(Long aLong) {
                                            onFaceFeatureInfoGet(faceFeature, requestId, errorCode);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            getFeatureDelayedDisposables.remove(disposable);
                                        }
                                    });
                        }
                    }

                } else { //??????????????????
                    if (increaseAndGetValue(extractErrorRetryMap, requestId) > MAX_RETRY_TIME) {
                        extractErrorRetryMap.put(requestId, 0);
                        String msg;
                        // ?????????FaceInfo????????????????????????????????????????????????????????????RGB????????????????????????????????????
                        if (errorCode != null && errorCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            msg = context.getString(R.string.low_confidence_level);
                        } else {
                            msg = "ExtractCode:" + errorCode;
                        }
                        faceHelper.setName(requestId, context.getString(R.string.recognize_failed_notice, msg));
                        // ??????????????????????????????????????????????????????????????????????????????
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        retryRecognizeDelayed(requestId);
                    } else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                    }
                }
            }


            @Override
            public void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, final Integer requestId, Integer errorCode) {
                if (livenessInfo != null) {
                    int liveness = livenessInfo.getLiveness();
                    livenessMap.put(requestId, liveness);
                    // ??????????????????
                    if (liveness == LivenessInfo.NOT_ALIVE) {
                        faceHelper.setName(requestId, context.getString(R.string.recognize_failed_notice, "NOT_ALIVE"));
                        // ?????? FAIL_RETRY_INTERVAL ??????????????????????????????UNKNOWN????????????????????????????????????????????????
                        retryLivenessDetectDelayed(requestId);
                    }
                } else {
                    if (increaseAndGetValue(livenessErrorRetryMap, requestId) > MAX_RETRY_TIME) {
                        livenessErrorRetryMap.put(requestId, 0);
                        String msg;
                        // ?????????FaceInfo????????????????????????????????????????????????????????????RGB????????????????????????????????????
                        if (errorCode != null && errorCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            msg = context.getString(R.string.low_confidence_level);
                        } else {
                            msg = "ProcessCode:" + errorCode;
                        }
                        faceHelper.setName(requestId, context.getString(R.string.recognize_failed_notice, msg));
                        retryLivenessDetectDelayed(requestId);
                    } else {
                        livenessMap.put(requestId, LivenessInfo.UNKNOWN);
                    }
                }
            }


        };
        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Camera.Size lastPreviewSize = previewSize;
                previewSize = camera.getParameters().getPreviewSize();
                int w = mPreviewView.getWidth();
                int h = mPreviewView.getHeight();
                drawHelper = new DrawHelper(
                        previewSize.width,
                        previewSize.height,
                        mPreviewView.getWidth(),
                        mPreviewView.getHeight(),
                        displayOrientation
                        , cameraId,
                        isMirror,
                        true,
                        false);
                Log.i(TAG, "onCameraOpened: " + drawHelper.toString());
                // ????????????????????????????????????????????????????????????
                if (faceHelper == null ||
                        lastPreviewSize == null ||
                        lastPreviewSize.width != previewSize.width || lastPreviewSize.height != previewSize.height) {
                    Integer trackedFaceCount = null;
                    // ??????????????????????????????
                    if (faceHelper != null) {
                        trackedFaceCount = faceHelper.getTrackedFaceCount();
                        faceHelper.release();
                    }
                    faceHelper = new FaceHelper.Builder()
                            .ftEngine(ftEngine)
                            .frEngine(frEngine)
                            .flEngine(flEngine)
                            .frQueueSize(MAX_DETECT_NUM)
                            .flQueueSize(MAX_DETECT_NUM)
                            .previewSize(previewSize)
                            .faceListener(faceListener)
                            .trackedFaceCount(trackedFaceCount == null ? ConfigUtil.getTrackedFaceCount(context.getApplicationContext()) : trackedFaceCount)
                            .build();
                }
            }


            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21);
                if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {
                    drawPreviewInfo(facePreviewInfoList);
                }
                registerFace(nv21, facePreviewInfoList);
                clearLeftFace(facePreviewInfoList);

                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        Integer status = requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId());
                        /**
                         * ????????????????????????????????????????????????????????????????????????????????????????????????ANALYZING???????????????????????????ALIVE???NOT_ALIVE??????????????????????????????
                         */
                        if (livenessDetect && (status == null || status != RequestFeatureStatus.SUCCEED)) {
                            Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
                            if (liveness == null
                                    || (liveness != LivenessInfo.ALIVE && liveness != LivenessInfo.NOT_ALIVE && liveness != RequestLivenessStatus.ANALYZING)) {
                                livenessMap.put(facePreviewInfoList.get(i).getTrackId(), RequestLivenessStatus.ANALYZING);
                                faceHelper.requestFaceLiveness(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId(), LivenessType.RGB);
                            }
                        }
                        /**
                         * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                         * ??????????????????????????????????????????{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer, Integer)}?????????
                         */
                        if (status != null) {
                            Log.d(TAG, "status:" + status);
                        }

                        if (status == null
                                || status == RequestFeatureStatus.TO_RETRY || status == RequestFeatureStatus.FAILED) {
                            requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                            faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
//                            Log.i(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackedFaceCount());
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(mPreviewView.getMeasuredWidth(), mPreviewView.getMeasuredHeight()))
                .rotation(windowManager.getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraID != null ? rgbCameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .additionalRotation(0)
                .previewOn(mPreviewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }

    /**
     * ????????????????????????????????????
     */
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private List<CompareResult> compareResultList;
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    private CompositeDisposable delayFaceTaskCompositeDisposable = new CompositeDisposable();
    /**
     * ??????????????????????????????????????????
     */
    private ConcurrentHashMap<Integer, Integer> livenessErrorRetryMap = new ConcurrentHashMap<>();

    /**
     * ????????????????????????????????????????????????
     */
    private ConcurrentHashMap<Integer, Integer> extractErrorRetryMap = new ConcurrentHashMap<>();

    private void registerFace(final byte[] nv21, final List<FacePreviewInfo> facePreviewInfoList) {
        if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
            registerStatus = REGISTER_STATUS_PROCESSING;
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {

                    boolean success = FaceServer.getInstance().registerNv21(context, nv21.clone(), previewSize.width, previewSize.height,
                            facePreviewInfoList.get(0).getFaceInfo(), "registered " + faceHelper.getTrackedFaceCount());
                    emitter.onNext(success);
                }
            })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean success) {
                            String result = success ? "register success!" : "register failed!";
                            showToast(result);
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            showToast("register failed!");
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * ???map???key?????????value???1??????
     *
     * @param countMap map
     * @param key      key
     * @return ???1??????value
     */
    public int increaseAndGetValue(Map<Integer, Integer> countMap, int key) {
        if (countMap == null) {
            return 0;
        }
        Integer value = countMap.get(key);
        if (value == null) {
            value = 0;
        }
        countMap.put(key, ++value);
        return value;
    }

    /**
     * ???????????????????????????
     *
     * @param facePreviewInfoList ?????????trackId??????
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!requestFeatureStatusMap.containsKey(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
//                    adapter.notifyItemRemoved(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            livenessErrorRetryMap.clear();
            extractErrorRetryMap.clear();
            if (getFeatureDelayedDisposables != null) {
                getFeatureDelayedDisposables.clear();
            }
            return;
        }
        Enumeration<Integer> keys = requestFeatureStatusMap.keys();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == key) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(key);
                livenessMap.remove(key);
                livenessErrorRetryMap.remove(key);
                extractErrorRetryMap.remove(key);
            }
        }


    }


    protected static void showToast(Context context, String s) {
        Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public static void activeEngine(Context context, String APP_ID, String SDK_KEY) {
/*        AndPermission.with(context)
                .runtime()
                .permission(NEEDED_PERMISSIONS).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {


            }
        }).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                Toast.makeText(context, "???READ_PHONE_STATE??????", Toast.LENGTH_LONG).show();

            }
        });*/
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(context.getApplicationContext(), APP_ID, SDK_KEY);
                Log.i(TAG, "subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast(context, context.getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            Log.d(TAG, "????????????????????????????????????");
//                            showToast(context, context.getString(R.string.already_activated));
                        } else {
                            showToast(context, context.getString(R.string.active_failed, activeCode));
                        }


                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(context.getApplicationContext(), activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(context, e.getMessage());
              /*          if (view != null) {
                            view.setClickable(true);
                        }*/
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void searchFace(final FaceFeature frFace, final Integer requestId) {
        Observable
                .create(new ObservableOnSubscribe<CompareResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<CompareResult> emitter) {
//                        Log.i(TAG, "subscribe: fr search start = " + System.currentTimeMillis() + " trackId = " + requestId);
                        CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace);
//                        Log.i(TAG, "subscribe: fr search end = " + System.currentTimeMillis() + " trackId = " + requestId);
                        emitter.onNext(compareResult);

                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
//                            faceHelper.setName(requestId, "VISITOR " + requestId);
                            return;
                        }

//                        Log.i(TAG, "onNext: fr search get result  = " + System.currentTimeMillis() + " trackId = " + requestId + "  similar = " + compareResult.getSimilar());


                        Log.d("ALDL", "compareResult??????");
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            Log.d("ALDL", "compareResult???????????????");
                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
//                                faceHelper.setName(requestId, "VISITOR " + requestId);
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                //??????????????????????????????????????????????????? MAX_DETECT_NUM ??????????????????????????????????????????????????????
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
//                                    adapter.notifyItemRemoved(0);
                                }
                                //?????????????????????????????????trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
//                                adapter.notifyItemInserted(compareResultList.size() - 1);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
//                            faceHelper.setName(requestId, context.getString(R.string.recognize_success_notice, compareResult.getUserName()));

                        } else {
                            Log.d("ALDL", "compareResult???????????????");
//                            faceHelper.setName(requestId, context.getString(R.string.recognize_failed_notice, "NOT_REGISTERED"));
                            retryRecognizeDelayed(requestId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ALDL", "compareResult?????????");
//                        faceHelper.setName(requestId, context.getString(R.string.recognize_failed_notice, "NOT_REGISTERED"));
                        retryRecognizeDelayed(requestId);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * ?????? FAIL_RETRY_INTERVAL ????????????????????????
     *
     * @param requestId ??????ID
     */
    private void retryRecognizeDelayed(final Integer requestId) {
        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
        Observable.timer(FAIL_RETRY_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        delayFaceTaskCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // ????????????????????????????????????FAILED????????????????????????????????????????????????
//                        faceHelper.setName(requestId, Integer.toString(requestId));
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                        delayFaceTaskCompositeDisposable.remove(disposable);
                    }
                });
    }

    /**
     * ?????? FAIL_RETRY_INTERVAL ????????????????????????
     *
     * @param requestId ??????ID
     */
    private void retryLivenessDetectDelayed(final Integer requestId) {
        Observable.timer(FAIL_RETRY_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        delayFaceTaskCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // ????????????????????????UNKNOWN????????????????????????????????????????????????
                        if (livenessDetect) {
//                            faceHelper.setName(requestId, Integer.toString(requestId));
                        }
                        livenessMap.put(requestId, LivenessInfo.UNKNOWN);
                        delayFaceTaskCompositeDisposable.remove(disposable);
                    }
                });
    }
}
