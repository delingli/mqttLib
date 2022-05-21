package com.itc.switchdevicecomponent.rooms

import android.content.Context
import androidx.room.Room

class RebootDataSingle private constructor() {
    companion object {
        val instance: RebootDataSingle by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RebootDataSingle()
        }
    }

    fun getDao(context: Context): RebootDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            RebootDataBase::class.java,
            "RebootDataBase.db"
        ).build()
    }
}