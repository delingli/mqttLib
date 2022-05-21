package com.itc.switchdevicecomponent.rooms

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class RebootOptDao : BaseDao<RebootOptDB>() {

    @Query("SELECT * FROM RebootOptDB WHERE mOptType=(:mOptType)")
    abstract fun selectOptData(mOptType: Int): RebootOptDB?

    @Query("SELECT * FROM RebootOptDB")
    abstract fun selectOptDatas(): List<RebootOptDB>?

    @Query("delete FROM RebootOptDB WHERE mOptType=(:mOptType)")
    abstract fun deleteOptData(mOptType: Int): Int?
/*    override fun delete(entity: UserFaceEngine): Int? {
    }*/
}