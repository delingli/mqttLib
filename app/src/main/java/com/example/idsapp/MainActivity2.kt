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

class MainActivity2 : AppCompatActivity() {


    private val ACTION_REQUEST_PERMISSIONS = 0x001


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
        setContentView(R.layout.activity_main2)
        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                ActivityCompat.requestPermissions(
                    this,
                    NEEDED_PERMISSIONS,
                    ACTION_REQUEST_PERMISSIONS
                )
            } else {
                var mIntent: Intent = Intent()
                mIntent.run {
                    setClass(this@MainActivity2, SecondActivity::class.java)
                }
                startActivity(mIntent)
            }


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