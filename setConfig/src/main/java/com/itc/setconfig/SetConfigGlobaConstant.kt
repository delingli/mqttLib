package com.itc.setconfig

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils


object SetConfigGlobaConstant {

    //    const val IP = "10.10.20.12"`
    const val IP = "10.10.20.53"

    //    const val Base_URL = "http://10.10.20.12:7777"
//    const val HOST_PORT = ":7777"
    const val HOST_PORT = "88"
    const val MQTT_PORT = "1883"
    const val MQTT_SCHEME = "tcp://"
    const val HTTP_SCHEME = "http://"
    const val HTTP_SCHEMES = "https://"
    const val device = "device/"
    const val MQTT_USERNAME = "itc"
    const val MQTT_PASS = "itc.rt.pass"
    const val MQTT_PARAM_TYPE = "2"
    const val KEY_MEETDATA_ID = "key_meetData_id"
    const val IP_KEY = "ip"
    const val MQTT_PORT_KEY = "mqtt_port_key"
    const val MQTT_USERNAME_KEY = "mqtt_username"
    const val MQTT_PASSWORD_KEY = "mqtt_password"
    const val NETWORK_PORT_KEY = "network_port"
//    const val KEY_MCARDITEMDATA = "key_mCardItemData"

    //    const val SERVERPATH = HTTP_SCHEME + IP + ":" + HOST_PORT
//
//    const val MQTT_PATH = MQTT_SCHEME + getIP() + ":" + getMqttPort()
    fun getMqttPath(): String {
        return MQTT_SCHEME + getIP() + ":" + getMqttPort()
    }

    @JvmStatic
    fun init(context: Context) {
        Utils.init(context.applicationContext as Application?)
    }


    @JvmStatic
    fun getServerPath(useHttps: Boolean): String {
        if (useHttps) {
            return HTTP_SCHEMES + getIP() + ":" + getPort()
        } else {
            return HTTP_SCHEME + getIP() + ":" + getPort()

        }
    }

    fun getIP(): String {
        return SPUtils.getInstance().getString(IP_KEY, IP)
    }

    fun getMqttPort(): String {
        return SPUtils.getInstance().getString(MQTT_PORT_KEY, MQTT_PORT)
    }

    fun getPort(): String {
        return SPUtils.getInstance().getString(NETWORK_PORT_KEY, HOST_PORT)
    }

    fun getMqttUserName(): String {
        return SPUtils.getInstance().getString(MQTT_USERNAME_KEY, MQTT_USERNAME)
    }

    fun getMqttPassword(): String {
        return SPUtils.getInstance().getString(MQTT_PASSWORD_KEY, MQTT_PASS)
    }
}