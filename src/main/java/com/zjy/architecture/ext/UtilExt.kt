package com.zjy.architecture.ext

import android.annotation.SuppressLint
import com.tencent.mars.xlog.Log
import java.text.SimpleDateFormat
import java.util.*

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

@SuppressLint("SimpleDateFormat")
fun Long.format(format: String, locale: Locale? = null): String {
    return if (locale == null) {
        SimpleDateFormat(format).format(this)
    } else {
        SimpleDateFormat(format, locale).format(this)
    }
}