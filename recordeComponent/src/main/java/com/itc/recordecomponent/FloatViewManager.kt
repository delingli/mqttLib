package com.itc.recordecomponent

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.itc.commoncomponent.network.ResultData
import com.itc.commoncomponent.network.Results
import com.petterp.floatingx.assist.helper.ScopeHelper
import com.petterp.floatingx.listener.control.IFxControl
import com.petterp.floatingx.util.activityToFx
import com.petterp.floatingx.util.fragmentToFx
import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.util.*

object FloatViewManager {
    var mIFxControl: IFxControl? = null
    var mContext: Context? = null
    var mExitDialog: ExitDialog? = null
    var start: Boolean = false
    var mRangeTime: Int = 0
    lateinit var mFloatViewModel: FloatViewModel
    var mPauseJob: Job? = null
    var mStartJob: Job? = null
    var mStopJob: Job? = null
    var meeting_id: String? = null
    const val TAG = "FloatViewManager"


    fun toInit(context: Context, meeting_id: String) {
        if (context == null) {
            throw RuntimeException("Context cannot be empty...")
        }
        this.mContext = context
        this.meeting_id = meeting_id
        this.mFloatViewModel = FloatViewModel()
        if (context is FragmentActivity) {
            var act: FragmentActivity = context as FragmentActivity
            mIFxControl = ScopeHelper.build {
                setLayout(R.layout.lubo_layout)
                setEnableScrollOutsideScreen(false)
                setEnableEdgeAdsorption(false)
//        setEdgeOffset(40f)
//        setAnimationImpl(FxAnimationImpl())
                setEnableAnimation(false)
                setEnableLog(true)
                build()
            }.toControl(act)


        } else if (context is Fragment) {
            mIFxControl = ScopeHelper.build {
                setLayout(R.layout.lubo_layout)
                setEnableScrollOutsideScreen(false)
                setEnableEdgeAdsorption(false)
//        setEdgeOffset(40f)
//        setAnimationImpl(FxAnimationImpl())
                setEnableAnimation(false)
                setEnableLog(true)
                build()
            }.toControl(this as Fragment)

        } else {
            throw RuntimeException("Pass in the correct context...")
        }
    }

    fun isShow(): Boolean {
        mIFxControl?.let {
            return it.isShow()
        }
        return false
    }


    fun show() {
        if (mIFxControl == null) {
            throw RuntimeException("Call the toInit(context: Context) initialization method first")
        }
        mIFxControl?.let {
            if (!it.isShow()) {
                it.show()
            }
            var mChronometer = it.getManagerViewHolder()?.getView<Chronometer>(R.id.timer)
            var img = it.getManagerViewHolder()?.getView<ImageView>(R.id.iv_open_off)
            if (!start) {
                //todo 默认请求开启，开始计时
                mChronometer?.let {
                    it.setBase(SystemClock.elapsedRealtime());//计时器清零
                    var hour = ((SystemClock.elapsedRealtime() - it.getBase()) / 1000 / 60) as Long
                    it.setFormat("0" + hour.toString() + ":%s");
//                    it.start()
                    toRequestStart(img, mChronometer)
                }
            }
            it.updateView {
                it.getView<ImageView>(R.id.iv_open_off)?.setOnClickListener {
                    //todo 请求网络开关接口
                    if (it is ImageView) {
                        var img: ImageView = it as ImageView
                        if (start) {
                            //开始
                            mRangeTime = SystemClock.elapsedRealtime().toInt();
                            //todo 请求暂停
                            mPauseJob = GlobalScope.launch {
                                var results: Results<ResultData<String>> = mFloatViewModel.openOff(
                                    meeting_id,
                                    "pause"
                                )
                                when (results) {
                                    is Results.Sucess -> {
                                        if (results.data.code == 0) {
                                            //成功了
                                            img.setImageResource(R.drawable.ic_pause)
                                            mChronometer?.stop()
                                        } else {
                                            //失败,不变换
                                            ToastUtils.showShort("暂停失败,请检察设备")
                                            LogUtils.eTag(
                                                TAG,
                                                "请求失败" + results.data.code + results.data.message
                                            )
                                        }
                                    }
                                    is Results.Error -> {
                                        //失败不变换
                                        ToastUtils.showShort("暂停失败,请检察设备")
                                        LogUtils.eTag(TAG, "请求失败")

                                    }
                                }
                            }


                        } else {
                            toRequestStart(img, mChronometer)

                        }


                    }
                    start = !start
                }
            }
            it.updateView {
                it.getView<View>(R.id.iv_exit)?.setOnClickListener {
                    if (mExitDialog == null) {
                        mExitDialog = ExitDialog()
                    }
                    if (mContext is FragmentActivity) {
                        mExitDialog?.show(
                            (mContext as FragmentActivity).supportFragmentManager,
                            "dialog"
                        )
                    } else if (mContext is Fragment) {
                        mExitDialog?.show(
                            (mContext as Fragment).childFragmentManager,
                            "dialog"
                        )
                    }
                    mExitDialog?.addOnSureClickListeners(object : ExitDialog.OnSureClickListeners {
                        override fun sure() {
                            //todo  去请求关闭
                            mStopJob = GlobalScope.launch {
                                var results: Results<ResultData<String>> = mFloatViewModel.openOff(
                                    meeting_id,
                                    "stop"
                                )
                                withContext(Dispatchers.Main) {
                                    when (results) {
                                        is Results.Sucess -> {


                                            if (results.data.code == 0) {
                                                //成功了
                                            } else {
                                                //失败
                                                ToastUtils.showShort("关闭失败,请检察设备")
                                            }
                                            destory()
                                        }
                                        is Results.Error -> {
                                            //失败
                                            destory()
                                            ToastUtils.showShort("关闭失败,请检察设备")
                                            LogUtils.eTag(TAG, "请求失败")
                                        }
                                    }
                                }

                            }

                        }

                        override fun cancel() {

                        }
                    })

                }
            }
            it.updateView {
                it.getView<Chronometer>(R.id.timer)?.setOnClickListener {
                    var mChronometer: Chronometer = it as Chronometer
                    mChronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
                    val hour =
                        ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000 / 60) as Int
                    mChronometer.setFormat("0" + hour.toString() + ":%s")
                    mChronometer.start()
                }
            }
        }

    }

    private fun toRequestStart(
        img: ImageView?,
        mChronometer: Chronometer?
    ) {
        mStartJob = GlobalScope.launch {
            var results: Results<ResultData<String>> = mFloatViewModel.openOff(
                meeting_id,
                "start"
            )
            withContext(Dispatchers.Main) {
                when (results) {
                    is Results.Sucess -> {
                        if (results.data.code == 0) {
                            //成功了
                            start = true
                            img?.setImageResource(R.drawable.ic_start)
                            mChronometer?.let {
                                if (mRangeTime != 0) {
                                    it.setBase(it.getBase() + (SystemClock.elapsedRealtime() - mRangeTime));
                                } else {
                                    it.setBase(SystemClock.elapsedRealtime());
                                }
                                it.start()
                            }
                        } else {
                            //失败不变
                            ToastUtils.showShort("开启失败,请检察设备")
                            img?.setImageResource(R.drawable.ic_pause)
                            start = false
                            LogUtils.eTag(TAG, "请求失败" + results.data.code + results.data.message)

                        }
                    }
                    is Results.Error -> {
                        //失败
                        img?.setImageResource(R.drawable.ic_pause)
                        start = false
                        LogUtils.eTag(TAG, "请求失败")
                        ToastUtils.showShort("开启失败,请检察设备")

                    }
                }
            }

        }
    }

    fun destory() {
        mPauseJob?.cancel()
        mStartJob?.cancel()
        mStopJob?.cancel()
        mExitDialog?.destory()
        mIFxControl?.cancel()


    }
}