package com.johnson.arcface2camerax.db

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: T)

    @Delete
    fun delete(element: T)

    @Delete
    fun delete(vararg elements:T)

    @Update
    fun update(element: T)

}