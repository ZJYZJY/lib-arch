package com.zjy.architecture.util

import android.content.Context
import android.os.Build
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import com.zjy.architecture.ext.versionCodeCompat
import com.zjy.architecture.ext.versionName
import java.io.PrintWriter
import java.io.StringWriter

/**
 * @author zhengjy
 * @since 2021/01/13
 * Description:
 */
object CrashHandler : Thread.UncaughtExceptionHandler {

    private lateinit var mContext: Context
    private var debug = false

    private const val NAME_PREFIX = "crash"

    fun init(context: Context, debug: Boolean) {
        this.mContext = context
        this.debug = debug
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        handleException(e)
        ActivityUtils.exitApp(true)
    }

    private fun handleException(e: Throwable?) {
        saveErrorMessages(e)
    }

    /**
     * 2.保存错误信息
     *
     * @param e Throwable
     */
    private fun saveErrorMessages(e: Throwable?) {
        if (e == null) return
        val sb = StringBuilder()
        sb.append("\n")
        sb.append("VERSION_CODE        :${mContext.versionCodeCompat}\n")
        sb.append("VERSION_NAME        :${mContext.versionName}\n")
        sb.append("VERSION.SDK_INT     :${Build.VERSION.SDK_INT}\n")
        sb.append("VERSION.RELEASE     :${Build.VERSION.RELEASE}\n")
        sb.append("VERSION.CODENAME    :${Build.VERSION.CODENAME}\n")
        sb.append("VERSION.INCREMENTAL :${Build.VERSION.INCREMENTAL}\n")
        sb.append("MANUFACTURER        :${Build.MANUFACTURER}\n")
        sb.append("MODEL               :${Build.MODEL}\n")
        sb.append("PRODUCT             :${Build.PRODUCT}\n")
        sb.append("DISPLAY             :${Build.DISPLAY}\n")
        sb.append("FINGERPRINT         :${Build.FINGERPRINT}\n")
        val writer = StringWriter()
        val pw = PrintWriter(writer)
        var cause = e
        do {
            cause?.printStackTrace(pw)
            cause = e.cause
        } while (cause != null)
        pw.close()
        sb.append(writer.toString())

        logE("${TAG}Crash", "\n$sb")
        Log.appenderFlush(true)
        Log.appenderClose()
    }
}