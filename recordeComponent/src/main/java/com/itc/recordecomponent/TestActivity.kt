package com.itc.recordecomponent

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.LogUtils
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.assist.Direction
import com.petterp.floatingx.assist.helper.ScopeHelper
import com.petterp.floatingx.listener.IFxScrollListener
import com.petterp.floatingx.listener.control.IFxControl
import com.petterp.floatingx.util.activityToFx
import com.petterp.floatingx.util.createFx

class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_recorde_layout)
        var str: String = "255"
        FloatViewManager.toInit(this, str,"")
        FloatViewManager.show()

    }

    override fun getResources(): Resources {
        val isHorizontalScreen =
            applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        return AdaptScreenUtils.adaptWidth(super.getResources(), 1920)

//        if(isHorizontalScreen){
//         return AdaptScreenUtils.adaptWidth(super.getResources(), 1920)
//
//     }else{
//         reurn AdaptScreenUtils.adaptWidth(supe r.getResources(), 1080)
//
//     }
    }

    override fun onDestroy() {
        super.onDestroy()
        FloatViewManager.destory()
    }

}