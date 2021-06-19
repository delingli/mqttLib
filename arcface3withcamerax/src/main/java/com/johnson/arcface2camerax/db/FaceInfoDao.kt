package com.johnson.arcface2camerax.db

import androidx.room.Dao
import androidx.room.Query
import com.arcsoft.face.FaceInfo

@Dao
interface FaceInfoDao:BaseDao<FaceInfoBean> {

    @Query("select * from faceInfoTable")
    fun getAllFaceInfos():MutableList<FaceInfoBean>?

    @Query("select * from faceInfoTable where id = :id")
    fun getFaceInfoWithID(id: Int):FaceInfoBean?

    @Query("update  faceInfoTable SET face = :face,faceCode = :faceCode ,name = :name where id = :id")
    fun updateFaceInfoWithId(id: Int,face: String?,faceCode: String?,name: String?)

    @Query("delete from faceInfoTable")
    fun  deleteAll()//删除全部信息

}