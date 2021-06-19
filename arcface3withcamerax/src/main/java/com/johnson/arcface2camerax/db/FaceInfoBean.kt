package com.johnson.arcface2camerax.db


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faceInfoTable")
data class FaceInfoBean(
        @PrimaryKey(autoGenerate = true)
        var primaryId: Int?,
        @ColumnInfo(name = "id")
        var id: Int?,
        @ColumnInfo(name = "name")
        var name: String?,
        @ColumnInfo(name = "faceCode")
        var faceCode: String?,
//        @ColumnInfo(name = "role")//0学生1老师
//        var role: Int?,
//        @ColumnInfo(name = "mac")
//        var macAddress: String?,
//        @ColumnInfo(name = "time")
//        var time: String?,
//        @ColumnInfo(name = "optType")//0本地缓存，1录入，2签到
//        var optType: Int = 0,
        @ColumnInfo(name = "face")
        var face: String? = "",
//        @ColumnInfo(name = "classId")
//        var classId: Int?,
        @ColumnInfo(name = "face_status")
        var face_status: Int? = 0
)