package com.example.idsapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.view.MotionEvent
import com.example.idsapp.App
import com.johnson.arcface2camerax.FaceCameraView.Companion.activeFaceEngine
import com.ldl.upanepochlog.api.LogParamsOption
import com.ldl.upanepochlog.api.LogParamsOption.LogParamsOptionBuilder
import com.ldl.upanepochlog.log.LogUtilManager

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Exception

class App : Application() {
    val isInMainProcess: Boolean
        get() {
            val procName = processName
            return !TextUtils.isEmpty(procName) && procName == this.packageName
        }

    override fun onCreate() {
        super.onCreate()
        //        Utils.init(this);ad
//        10.10.20.200:9039

        val logParamsOption = LogParamsOptionBuilder("http://" + "10.10.20.200") //地址ip
            .port(9039) //端口
            .dealyTime(60) //运行最长时间，
            .device_sn("3A914B6F3787") //设备id 注意要跟业务中mqtt或者rabbitmq获取的保持一致，服务器做了数据库查询比对;
            .build()
        LogUtilManager.getInstance().initLog(this, logParamsOption) //初始化
    }

    companion object {
        val processName: String?
            get() = try {
                val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
                val myBufferReader = BufferedReader(FileReader(file))
                val processName = myBufferReader.readLine().trim { it <= ' ' }
                myBufferReader.close()
                processName
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
    }
}