package com.itc.switchdevicecomponent.rooms

import androidx.room.*

@Dao
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: T):Long
    @Update
    abstract fun update(entity: T)
    @Delete
    abstract fun delete(entity: T):Int?
}