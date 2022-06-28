package com.example.idsapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.work.*
import com.blankj.utilcode.util.LogUtils

import com.itc.switchdevicecomponent.work.RebotWork
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity2 : AppCompatActivity() {
    private val ACTION_REQUEST_PERMISSIONS = 0x001
    private val ACTION_REQUEST_PERMISSIONSS = 0x003


    fun afterRequestPermission(requestCode: Int, isAllGranted: Boolean) {

    }

    private var NEEDED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isAllGranted = true
        for (grantResult in grantResults) {
            isAllGranted = isAllGranted and (grantResult == PackageManager.PERMISSION_GRANTED)
        }
        afterRequestPermission(requestCode, isAllGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*     if (!checkPermissions(NEEDED_PERMISSIONS_HONGRUAN)) {
                 ActivityCompat.requestPermissions(
                     this,
                     NEEDED_PERMISSIONS_HONGRUAN,
                     ACTION_REQUEST_PERMISSIONSS
                 )
             } else {
                 ActiveEngineManager.getInstance().activeEngine(this,GlobaConstant.APP_ID,GlobaConstant.SDK_KEY)

             }*/


//        DeviceOptManagerTest


        setContentView(R.layout.activity_main2)
        findViewById<Button>(R.id.btn_goto).setOnClickListener(View.OnClickListener {
            WorkManager.getInstance(this).cancelAllWorkByTag(RebotWork.TAG_OUTPUT)

            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            currentDate.timeInMillis

// 设置在大约 05:00:00 AM 执行
            dueDate.set(Calendar.HOUR_OF_DAY, 14)
            dueDate.set(Calendar.MINUTE, 47)
            dueDate.set(Calendar.SECOND, 0)

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
                LogUtils.dTag("RebotWork", "执行了before")
            }
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
            var dates = Date()
            dates.time = dueDate.timeInMillis
            var mSimpleDateFormate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var str = mSimpleDateFormate.format(dates)
            LogUtils.dTag("RebotWork", "${str}时间开始执行")

//            val timeDiff = dueDate.timeInMillis — currentDate.timeInMillis
//            val timeDiff = dueDate.timeInMillis — currentDate.timeInMillis
            val constraints = Constraints.Builder().setRequiresCharging(true).build()
            val dailyWorkRequest = OneTimeWorkRequestBuilder<RebotWork>()
                .setConstraints(constraints).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(RebotWork.TAG_OUTPUT).build()
            WorkManager.getInstance(this).enqueueUniqueWork(
                RebotWork.TAG_OUTPUT,
                ExistingWorkPolicy.KEEP,
                dailyWorkRequest
            )
//            WorkManager.getInstance(this).enqueue(dailyWorkRequest)

        })
        findViewById<Button>(R.id.btn_cancel).setOnClickListener(View.OnClickListener {
            WorkManager.getInstance(this).cancelAllWorkByTag("TAG_OUTPUT")


        })
    }


    protected fun checkPermissions(neededPermissions: Array<String>): Boolean {
        if (neededPermissions == null || neededPermissions.size == 0) {
            return true
        }
        var allGranted = true
        for (neededPermission in neededPermissions) {
            allGranted = allGranted and (ContextCompat.checkSelfPermission(
                this,
                neededPermission!!
            ) == PackageManager.PERMISSION_GRANTED)
        }
        return allGranted
    }
}