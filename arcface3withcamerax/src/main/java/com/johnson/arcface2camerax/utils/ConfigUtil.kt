package com.johnson.arcface2camerax.utils

import android.content.Context
import com.arcsoft.face.enums.DetectFaceOrientPriority

object ConfigUtil {
    private val APP_NAME = "ArcFace"
    private val TRACK_ID = "trackID"
    private val FT_ORIENT = "ftOrient"

    fun setTrackId(context: Context?, trackId: Int?) {
        if (context == null) {
            return
        }
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt(TRACK_ID, trackId ?: -1)
            .apply()
    }

    fun getTrackId(context: Context?): Int {
        if (context == null) {
            return 0
        }
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(TRACK_ID, 0)
    }

    fun setFtOrient(context: Context?, ftOrient: DetectFaceOrientPriority): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.edit()
                .putString(FT_ORIENT, ftOrient.name)
                .commit()
    }

    fun getFtOrient(context: Context?): DetectFaceOrientPriority? {
        if (context == null) {
            return DetectFaceOrientPriority.ASF_OP_270_ONLY
        }
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        return DetectFaceOrientPriority.valueOf(sharedPreferences.getString(FT_ORIENT, DetectFaceOrientPriority.ASF_OP_270_ONLY.name)!!)
    }

    /**
     * 获取Boolean
     */
    fun getBoolean(context: Context, code: String, default: Boolean): Boolean {
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(code, default)
    }

    /**
     * 获取Boolean
     */
    fun setBoolean(context: Context, code: String, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(code, value).apply()
    }

}
