package com.itc.switchdevicecomponent.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import com.blankj.utilcode.util.LogUtils
import com.itc.switchdevicecomponent.basic.I0830BDeviceOpt
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.TAG
import com.itc.switchdevicecomponent.basic.IDeviceOpt.Companion.sdfs
import com.itc.switchdevicecomponent.receiver.StartRebootReceiver
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class I0830BDeviceOptImpl(override var context: Context?) : I0830BDeviceOpt {

    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    override fun startDevice(startDeviceTime: String) {
        var parse: Date? = null
        LogUtils.dTag(TAG, "当前SDK版本:${Build.VERSION.RELEASE}")
        try {
            parse = sdfs.parse(startDeviceTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var newStr = sdfs.format(parse)
        val s: String = newStr.replace(" ".toRegex(), "-")
        val s1 = s.replace(":".toRegex(), "-")
        val split = s1.split("-").toTypedArray()
        val month = split[1].toInt()
        val day = split[2].toInt()
        val hour = split[3].toInt()
        val minute = split[4].toInt()
        val second = split[5].toInt()
        LogUtils.dTag(TAG, "0830b开机时间配置${split[0]}+${month}+${day}+${hour}+${minute}+${second}")
        setTime("[key]")
        for (i in split.indices) {
            when (i) {
                0 -> setTime("year=" + split[0])
                1 -> setTime("month=$month")
                2 -> setTime("day=$day")
                3 -> {
                    setTime("hour=$hour")
                }
                4 -> {
                    setTime("minute=$minute")
                }
                5 -> setTime("second=0")
            }
        }
        setTime("[poll]")
        setTime("hour=0")
        setTime("minute=0")
        setTime("monday=0")
        setTime("tuesday=0")
        setTime("wednesday=0")
        setTime("thursday=0")
        setTime("friday=0")
        setTime("saturday=0")
        setTime("sunday=0")
    }

    var pi: PendingIntent? = null

    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    override fun closeDevice(closeDeviceTime: String) {
        var parse: Date? = null
        try {
            parse = sdfs.parse(closeDeviceTime)
            val times = parse.time
            val intent = Intent(StartRebootReceiver.ACTION_CUSTOM_STARTREBOOT)
            pi = PendingIntent.getBroadcast(
                context,
                times.toInt(), intent, 0
            )
            val am =
                context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExact(AlarmManager.RTC_WAKEUP, times, pi)
        } catch (e: ParseException) {
            LogUtils.eTag(TAG, "set error:$e")
            e.printStackTrace()
        }
    }

    override fun systemReset() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val out = DataOutputStream(
                process.outputStream
            )
            out.writeBytes("reboot \n")
            out.writeBytes("exit\n")
            out.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtils.dTag(TAG, "重启执行失败:" + e.message)
        }
    }

    override fun cancelStartCloseDevice() {
        val am =
            context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pi?.run {
            am.cancel(this)
        }
        val file = File(Environment.getExternalStorageDirectory().path + "/autoshutdown.ini")
        if (file.exists()) {
            file.delete()
        }
    }

    private fun setTime(message: String) {
        LogUtils.dTag(TAG, "写入文件得开机消息$message")
        try {
            //todo： 路径有问题
            val file = File(Environment.getExternalStorageDirectory().path + "/autoshutdown.ini")
            var fos: FileOutputStream? = null
            fos = if (!file.exists()) {
                file.createNewFile() //如果文件不存在，就创建该文件
                FileOutputStream(file) //首次写入获取
            } else {
                //如果文件已存在，那么就在文件末尾追加写入
                FileOutputStream(file, true) //这里构造方法多了一个参数true,表示在文件末尾追加写入
            }
            val osw = OutputStreamWriter(fos, "UTF-8") //指定以UTF-8格式写入文件
            osw.write(message)
            //每写入一个Map就换一行
            osw.write("\r\n")
            //写入完成关闭流
            osw.close()
            LogUtils.eTag(TAG, "end write")
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.eTag(TAG, "write error:$e")
        }
    }
}