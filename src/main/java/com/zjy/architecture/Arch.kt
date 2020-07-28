package com.zjy.architecture

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level

/**
 * @author zhengjy
 * @since 2020/05/21
 * Description:
 */
object Arch {

    /**
     * 获取应用全局[Context]
     */
    val context: Context
        get() = checkNotNull(mContext) { "Please init Arch first" }

    /**
     * 是否开启Debug模式
     */
    var debug: Boolean = false

    private var mContext: Context? = null
    private var encryptKey: String = ""

    /**
     * 在Application的onCreate中初始化
     */
    @JvmStatic
    @JvmOverloads
    fun init(context: Context, debug: Boolean = false, encryptKey: String = "", inject: (KoinApplication.() -> Unit)? = null) {
        this.mContext = context.applicationContext
        this.debug = debug
        this.encryptKey = encryptKey
        if (encryptKey.isNotEmpty()) {
            openXLog(context, debug)
        }

        // 初始化依赖注入
        startKoin {
            if (debug) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.ERROR)
            }

            inject?.invoke(this)
        }
    }

    /**
     * 通常在Application的onTerminate中调用，用于释放资源，关闭日志
     */
    @JvmStatic
    fun release() {
        if (encryptKey.isNotEmpty()) {
            Log.appenderClose()
        }
        stopKoin()
    }

    /**
     * 开启日志
     */
    private fun openXLog(context: Context, debug: Boolean) {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")
        val pid = Process.myPid()
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in am.runningAppProcesses) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName
                break
            }
        }
        if (processName == null) {
            return
        }

        val root = context.getExternalFilesDir("")
        val logPath = "$root/arch/log"

        val logFileName = if (processName.indexOf(":") == -1)
            "Arch"
        else
            "Arch_${processName.substring(processName.indexOf(":") + 1)}"

        if (debug) {
            Xlog.appenderOpen(
                    Xlog.LEVEL_VERBOSE, Xlog.AppednerModeAsync, "", logPath,
                    "DEBUG_$logFileName", 0, ""
            )
            Xlog.setConsoleLogOpen(true)
        } else {
            Xlog.appenderOpen(
                    Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logPath,
                    logFileName, 0, encryptKey
            )
            Xlog.setConsoleLogOpen(false)
        }
        Log.setLogImp(Xlog())
    }
}