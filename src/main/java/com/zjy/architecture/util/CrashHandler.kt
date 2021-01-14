package com.zjy.architecture.util

import android.content.Context

/**
 * @author zhengjy
 * @since 2021/01/13
 * Description:
 */
object CrashHandler : Thread.UncaughtExceptionHandler {

    fun init(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {

    }


}