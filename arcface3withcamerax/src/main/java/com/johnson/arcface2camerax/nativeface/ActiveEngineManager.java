package com.johnson.arcface2camerax.nativeface;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.johnson.arcface2camerax.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public
class ActiveEngineManager {
    private static final ActiveEngineManager ourInstance = new ActiveEngineManager();
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    public static ActiveEngineManager getInstance() {
        return ourInstance;
    }

    private static String TAG = "ActiveEngineManager";

    private ActiveEngineManager() {
    }

    protected void showToast(Context context, String s) {
        Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private static final int ACTION_REQUEST_PERMISSIONS = 0x003;

    protected boolean checkPermissions(Context context,String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(context, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    public void activeEngine(Activity activity, String APP_ID, String SDK_KEY) {
        if (!checkPermissions(activity,NEEDED_PERMISSIONS)) {
//            ActivityCompat.requestPermissions(activity, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            showToast(activity, "请首先授权READ_PHONE_STATE否则无法接活虹软引擎");
            return;
        }
        toActive(activity.getApplicationContext(), APP_ID, SDK_KEY);

    }

    private void toActive(Context context, String APP_ID, String SDK_KEY) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(context, APP_ID, SDK_KEY);
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
                            Log.d(TAG, "引擎已激活，无需再次激活");
//                            showToast(context, context.getString(R.string.already_activated));
                        } else {
                            showToast(context, context.getString(R.string.active_failed, activeCode));
                        }


                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(context, activeFileInfo);
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
}
