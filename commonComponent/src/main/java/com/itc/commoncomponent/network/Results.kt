package com.itc.commoncomponent.network

import java.lang.Exception

sealed class Results<out R> {
    data class Sucess<out T>(val data: T) : Results<T>()
    data class Error(val exception: Exception) : Results<Nothing>()
}
