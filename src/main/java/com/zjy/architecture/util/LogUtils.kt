package com.zjy.architecture.util

import com.tencent.mars.xlog.Log

/**
 * @author zhengjy
 * @since 2021/01/07
 * Description:
 */
const val TAG = "ARCH_LOG::"

inline fun <reified T> T.logV(message: String?) {
    Log.v("$TAG${T::class.java.simpleName}", message)
}

fun logV(tag: String, message: String?) {
    Log.v("$TAG$tag", message)
}

inline fun <reified T> T.logD(message: String?) {
    Log.d("$TAG${T::class.java.simpleName}", message)
}

fun logD(tag: String, message: String?) {
    Log.d("$TAG$tag", message)
}

inline fun <reified T> T.logI(message: String?) {
    Log.i("$TAG${T::class.java.simpleName}", message)
}

fun logI(tag: String, message: String?) {
    Log.i("$TAG$tag", message)
}

inline fun <reified T> T.logW(message: String?) {
    Log.w("$TAG${T::class.java.simpleName}", message)
}

fun logW(tag: String, message: String?) {
    Log.w("$TAG$tag", message)
}

inline fun <reified T> T.logE(message: String?) {
    Log.e("$TAG${T::class.java.simpleName}", message)
}

fun logE(tag: String, message: String?) {
    Log.e("$TAG$tag", message)
}