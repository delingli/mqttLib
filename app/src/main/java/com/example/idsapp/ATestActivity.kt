package com.example.idsapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.idsapp.databinding.ActivityAtestBinding
import com.itc.switchdevicecomponent.basic.IDeviceOpt
import java.text.SimpleDateFormat
import java.util.*

class ATestActivity : AppCompatActivity() {
    private var mBinding: ActivityAtestBinding? = null

    companion object {
        const val TAG = "ATestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAtestBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        LogUtils.dTag("MODEL", "${Build.MODEL}")
//        LogUtils.dTag("MODEL", "${DeviceIdUtil.getDeviceId(this)}")
        val sdfs = SimpleDateFormat("yyyy-MM-dd HH:mm")
        mBinding!!.button.setOnClickListener {
            if (checkTime(mBinding!!.etStart.text.toString(), mBinding!!.etClose.text.toString())) {
                var startDeviceTime = mBinding!!.etStart.text.toString()
                var closeDeviceTime = mBinding!!.etClose.text.toString()
                val s: String = startDeviceTime.replace(" ".toRegex(), "-")
                val newstartDeviceTime = s.replace(":".toRegex(), "-")
                var newstartDeviceTimeArray = newstartDeviceTime.split("-")


                val s2: String = closeDeviceTime.replace(" ".toRegex(), "-")
                val newcloseDeviceTime = s2.replace(":".toRegex(), "-")
                var newcloseDeviceTimeArray = newcloseDeviceTime.split("-")
                var timeOn = IntArray(5)
                var timeoff = IntArray(5)

                for (i in newstartDeviceTimeArray.indices) {
                    timeOn[i] = newstartDeviceTimeArray[i].toInt()
                }
                for (i in newcloseDeviceTimeArray.indices) {
                    timeoff[i] = newcloseDeviceTimeArray[i].toInt()
                }
                val intent = Intent("android.intent.action.setpoweronoff")
                intent.putExtra("timeon", timeOn)
                intent.putExtra("timeoff", timeoff)
                intent.putExtra("enable", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) //仅8.1需要添加此行代码
                if (Build.VERSION.RELEASE.equals("8.1.0")) {
                    LogUtils.dTag(TAG, "当前SDK版本执行了FLAG_ACTIVITY_PREVIOUS_IS_TOP")
                }
                sendBroadcast(intent)
                mBinding?.tvState?.setText(
                    "执行了配置:发送广播\n" +
                            "开机时间:" + timeOn[0] + "-" + timeOn[1] + "-" + timeOn[2] + " " + timeOn[3] + ":" + timeOn[4] + "\n"
                            + "关机时间:" + timeoff[0] + "-" + timeoff[1] + "-" + timeoff[2] + " " + timeoff[3] + ":" + timeoff[4]
                            + "\nenable:${true}"
                )
                LogUtils.dTag(
                    TAG, "执行了配置:发送广播\n" +
                            "开机时间:" + timeOn[0] + "-" + timeOn[1] + "-" + timeOn[2] + " " + timeOn[3] + ":" + timeOn[4] + "\n"
                            + "关机时间:" + timeoff[0] + "-" + timeoff[1] + "-" + timeoff[2] + " " + timeoff[3] + ":" + timeoff[4] + "\n"
                            + "enable:${true}"
                )
            } else {
                ToastUtils.showShort("输入数据不符合格式")
            }

        }
        mBinding!!.button2.setOnClickListener {
            if (checkTime(mBinding!!.etStart.text.toString(), mBinding!!.etClose.text.toString())) {
                var startDeviceTime = mBinding!!.etStart.text.toString()
                var closeDeviceTime = mBinding!!.etClose.text.toString()
                val s: String = startDeviceTime.replace(" ".toRegex(), "-")
                val newstartDeviceTime = s.replace(":".toRegex(), "-")
                var newstartDeviceTimeArray = newstartDeviceTime.split("-")


                val s2: String = closeDeviceTime.replace(" ".toRegex(), "-")
                val newcloseDeviceTime = s2.replace(":".toRegex(), "-")
                var newcloseDeviceTimeArray = newcloseDeviceTime.split("-")
                var timeOn = IntArray(5)
                var timeoff = IntArray(5)

                for (i in newstartDeviceTimeArray.indices) {
                    timeOn[i] = newstartDeviceTimeArray[i].toInt()
                }
                for (i in newcloseDeviceTimeArray.indices) {
                    timeoff[i] = newcloseDeviceTimeArray[i].toInt()
                }
                val intent = Intent("android.intent.action.setpoweronoff")
                intent.putExtra("timeon", timeOn)
                intent.putExtra("timeoff", timeoff)
                intent.putExtra("enable", false)
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) //仅8.1需要添加此行代码
                if (Build.VERSION.RELEASE.equals("8.1.0")) {
                    LogUtils.dTag(TAG, "当前SDK版本执行了FLAG_ACTIVITY_PREVIOUS_IS_TOP")
                }
                sendBroadcast(intent)
                mBinding?.tvState?.setText(
                    "取消了配置:发送广播\n" +
                            "开机时间:" + timeOn[0] + "-" + timeOn[1] + "-" + timeOn[2] + " " + timeOn[3] + ":" + timeOn[4] + "\n"
                            + "关机时间:" + timeoff[0] + "-" + timeoff[1] + "-" + timeoff[2] + " " + timeoff[3] + ":" + timeoff[4] + "\n"
                            + "enable:${false}"
                )
                LogUtils.dTag(
                    TAG, "取消了配置:发送广播\n" +
                            "开机时间:" + timeOn[0] + "-" + timeOn[1] + "-" + timeOn[2] + " " + timeOn[3] + ":" + timeOn[4] + "\n"
                            + "关机时间:" + timeoff[0] + "-" + timeoff[1] + "-" + timeoff[2] + " " + timeoff[3] + ":" + timeoff[4] + "\n"
                            + "enable:${false}"
                )
            } else {
                ToastUtils.showShort("输入数据不符合格式")
            }
        }
    }

    fun checkTime(startDeviceTime: String, closeDeviceTime: String): Boolean {
        LogUtils.dTag(IDeviceOpt.TAG, "当前SDK版本:${Build.VERSION.RELEASE}")
        if (startDeviceTime.equals("NULL") || closeDeviceTime.equals("NULL") || startDeviceTime.equals(
                "null"
            ) || closeDeviceTime.equals("null") || startDeviceTime.isNullOrEmpty() || closeDeviceTime.isNullOrEmpty()
        ) {
            return false
        }
        var startData = IDeviceOpt.sdfs.parse(startDeviceTime)
        var closeData = IDeviceOpt.sdfs.parse(closeDeviceTime)
        var startCalas = Calendar.getInstance()
        startCalas.time = startData
        var closeCala = Calendar.getInstance()
        closeCala.time = closeData
        var nowCale = Calendar.getInstance()
        nowCale.time = TimeUtils.getNowDate()
        if (closeCala.before(startCalas) && nowCale.before(closeCala)) {
            return true
        } else {
            false
        }
        return false
    }
}