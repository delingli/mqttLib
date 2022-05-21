package com.itc.switchdevicecomponent.rooms

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(RebootOptDB::class), version = 1, exportSchema = false)
abstract class RebootDataBase : RoomDatabase() {
    val mRebootOptDao: RebootOptDao by lazy { rebootOptDao() }
    abstract fun rebootOptDao(): RebootOptDao

}