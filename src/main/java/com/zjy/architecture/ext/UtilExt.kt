package com.zjy.architecture.ext

import com.tencent.mars.xlog.Log

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
inline fun <reified T, R> T.tryWith(crossinline block: () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        Log.e(T::class.java.name, "", e)
        null
    }
}