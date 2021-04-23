package com.zjy.architecture

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import com.zjy.architecture.di.Injector
import com.zjy.architecture.util.ActivityUtils
import com.zjy.architecture.util.CrashHandler
import org.koin.android.ext.koin.androidContext
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
@SuppressLint("StaticFieldLeak")
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

    /**
     * 在Application的onCreate中初始化
     *
     * @param   context         Application Context
     * @param   debug           是否开启调试模式
     * @param   encryptKey      加密日志用的密钥，如果传空字符串，则正式环境下不会有日志输出
     * @param   injectRouters   额外注入操作对应的路由
     */
    @JvmStatic
    fun init(context: Context, debug: Boolean = false, encryptKey: String = "",
             injectRouters: Array<String> = arrayOf()) {
        init(context, debug, encryptKey) {
            for (path in injectRouters) {
                val router = ARouter.getInstance().build(path).navigation()
                if (router is Injector? && router != null) {
                    modules(router.inject())
                }
            }
        }
    }

    /**
     * 在Application的onCreate中初始化
     *
     * @param   context     Application Context
     * @param   debug       是否开启调试模式
     * @param   encryptKey  加密日志用的密钥，如果传空字符串，则正式环境下不会有日志输出
     * @param   inject      额外的注入操作
     */
    @JvmStatic
    fun init(context: Context, debug: Boolean = false, encryptKey: String = "",
             inject: (KoinApplication.() -> Unit)? = null) {
        this.mContext = context.applicationContext
        this.debug = debug
        openXLog(context, debug, encryptKey)
        ActivityUtils.registerActivityLifecycleCallbacks(context as Application)
        // 初始化ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(context)

        // 初始化依赖注入
        startKoin {
            if (debug) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.ERROR)
            }
            androidContext(this@Arch.context)

            inject?.invoke(this)
        }
    }

    /**
     * 用于释放资源，关闭日志
     */
    @JvmStatic
    fun release() {
        Log.appenderFlush(true)
        Log.appenderClose()
        stopKoin()
    }

    /**
     * 开启日志
     */
    private fun openXLog(context: Context, debug: Boolean, encryptKey: String) {
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
            Log.setLevel(Log.LEVEL_VERBOSE, false)
            Log.appenderFlush(false)
        } else if (encryptKey.isNotEmpty()) {
            Xlog.appenderOpen(
                Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logPath,
                logFileName, 0, encryptKey
            )
            Xlog.setConsoleLogOpen(false)
            Log.setLogImp(Xlog())
            Log.appenderFlush(false)
        }
    }
}