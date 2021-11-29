package com.example.idsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.idsapp.databinding.ActivityTestLogBinding
import com.ldl.upanepochlog.log.LogUtilManager
import com.ldl.upanepochlog.ui.LogSetActivity

class TestLogActivity : AppCompatActivity() {
    private var viewBinding: ActivityTestLogBinding? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTestLogBinding.inflate(layoutInflater)
//        R.layout.activity_test_log
        setContentView(binding.root)
        binding.btnClose.setOnClickListener { v ->
            LogUtilManager.getInstance().setAppLogSwitch(false)
            Log.d("ldl", "btnClose")

        }
        binding.btnGetspace.setOnClickListener { v ->
//            LogSetActivity
            var intents: Intent = Intent()
            intents.setClass(
                this,
                LogSetActivity::class.java
            )
            startActivity(intents)
//            LogUtilManager.getInstance().getLogSpace()

        }
        binding.btnStart.setOnClickListener { v ->
            LogUtilManager.getInstance().setAppLogSwitch(true)
            Log.d("ldl", "btnStart")
        }
        binding.btnSubmit.setOnClickListener { v ->
            LogUtilManager.getInstance().submit()
            Log.d("ldl", "btnSubmit")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }
}