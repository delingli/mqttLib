package com.itc.switchdevicecomponent.rooms

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.itc.switchdevicecomponent.annation.OptMode
import com.itc.switchdevicecomponent.annation.OptType

@Entity(tableName = "RebootOptDB")
class RebootOptDB {
    @PrimaryKey
    var mOptType: Int = OptType.TASKTYPE_UNKNOW  //操作类型
    var startDeviceTime: String = ""// 定时重启的时间,年月日时分
    var closeDeviceTime:String = ""  //定时关机的时间,年月日时分
    var restartDeviceTime:String = ""  //重启,年月日时分
    var deviceType:String?=null  //设备


}