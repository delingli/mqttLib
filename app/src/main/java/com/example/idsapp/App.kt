package com.example.idsapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.view.MotionEvent
import com.example.idsapp.App
import com.johnson.arcface2camerax.FaceCameraView.Companion.activeFaceEngine
import com.johnson.arcface2camerax.nativeface.ActiveEngineManager

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