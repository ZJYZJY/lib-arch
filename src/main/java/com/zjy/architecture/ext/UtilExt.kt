package com.zjy.architecture.ext

import android.annotation.SuppressLint
import com.zjy.architecture.util.logV
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
        logV(T::class.java.name, e.message)
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

/**
 * 字符串截取小数点后number位
 * 末位不足，则补全 0
 *
 * @param count
 * @return
 */
fun String.checkString(count: Int): String {
    var result = this
    if (this.contains(".")) {
        val pointLength = length - 1 - indexOf(".")
        when {
            count == 0 -> result = substring(0, indexOf("."))
            pointLength >= count -> result = substring(0, indexOf(".") + count + 1)
            pointLength < count -> {
                val add = count - pointLength
                val builder = StringBuilder(this)
                for (i in 0 until add) {
                    builder.append("0")
                }
                result = builder.toString()
            }
        }
    } else {
        if (count > 0) {
            val builder = StringBuilder(this)
            builder.append(".")
            for (i in 0 until count) {
                builder.append("0")
            }
            result = builder.toString()
        }
    }
    return result
}