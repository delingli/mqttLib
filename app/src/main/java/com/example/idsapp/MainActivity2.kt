package com.example.idsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.johnson.arcface2camerax.nativeface.ActiveEngineManager
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission

class MainActivity2 : AppCompatActivity() {
    private val ACTION_REQUEST_PERMISSIONS = 0x001
    private val ACTION_REQUEST_PERMISSIONSS = 0x003


    fun afterRequestPermission(requestCode: Int, isAllGranted: Boolean) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                var mIntent: Intent = Intent()
                mIntent.run {
                    setClass(this@MainActivity2, SecondActivity::class.java)
                }
                startActivity(mIntent)
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG)
            }
        } else if (requestCode == ACTION_REQUEST_PERMISSIONSS) {
            ActiveEngineManager.getInstance()
                .activeEngine(this, GlobaConstant.APP_ID, GlobaConstant.SDK_KEY)

        }
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


        ActiveEngineManager.getInstance()
            .activeEngine(this, GlobaConstant.APP_ID, GlobaConstant.SDK_KEY)

        /*     if (!checkPermissions(NEEDED_PERMISSIONS_HONGRUAN)) {
                 ActivityCompat.requestPermissions(
                     this,
                     NEEDED_PERMISSIONS_HONGRUAN,
                     ACTION_REQUEST_PERMISSIONSS
                 )
             } else {
                 ActiveEngineManager.getInstance().activeEngine(this,GlobaConstant.APP_ID,GlobaConstant.SDK_KEY)

             }*/
        setContentView(R.layout.activity_main2)
        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
            AndPermission.with(this)
                .runtime()
                .permission(NEEDED_PERMISSIONS)
                .onGranted(Action { permissions: List<String?>? ->
                    var intent: Intent = Intent()
                    intent.setClass(this,SecondActivity::class.java)
                    startActivity(intent)
                })
                .onDenied(Action { permissions: List<String?>? ->
                    //权限禁止
                    if (AndPermission.hasAlwaysDeniedPermission(
                            this,
                            permissions
                        )
                    ) {
                        //是否永久拒绝了权限
                        Toast.makeText(this, "永久拒绝了权限", Toast.LENGTH_SHORT).show();
                    }
                })
                .start()
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