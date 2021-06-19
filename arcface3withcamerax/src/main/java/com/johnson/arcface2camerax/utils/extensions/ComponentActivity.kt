package com.johnson.arcface2camerax.utils.extensions

import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * @ClassName ComponentActivity
 * @Description TODO
 * @Author WangShunYi
 * @Date 2019-08-03 11:31
 */

fun <T> ComponentActivity.bindLifeCycle(): AutoDisposeConverter<T> = AutoDispose.autoDisposable<T>(
    AndroidLifecycleScopeProvider.from(lifecycle))

fun <T> LifecycleOwner.bindLifeCycle() = AutoDispose.autoDisposable<T>(
    AndroidLifecycleScopeProvider.from(lifecycle))
