package com.zjy.architecture.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.zjy.architecture.ext.activityManager

/**
 * @author zhengjy
 * @since 2021/04/23
 * Description:
 */
object ProcessUtil {

    private var currentProcessName: String? = null

    /**
     * @return 当前进程名
     */
    fun getCurrentProcessName(context: Context): String? {
        if (!currentProcessName.isNullOrEmpty()) {
            return currentProcessName
        }

        //1)通过Application的API获取当前进程名
        currentProcessName = getCurrentProcessNameByApplication()
        if (!currentProcessName.isNullOrEmpty()) {
            return currentProcessName
        }

        //2)通过反射ActivityThread获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityThread()
        if (!currentProcessName.isNullOrEmpty()) {
            return currentProcessName
        }

        //3)通过ActivityManager获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityManager(context)
        return currentProcessName
    }

    /**
     * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
     */
    private fun getCurrentProcessNameByApplication(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else null
    }

    /**
     * 通过反射ActivityThread获取进程名，避免了ipc
     */
    private fun getCurrentProcessNameByActivityThread(): String? {
        var processName: String? = null
        try {
            val declaredMethod = Class.forName(
                "android.app.ActivityThread", false,
                Application::class.java.classLoader
            ).getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
            declaredMethod.isAccessible = true
            val invoke: Any = declaredMethod.invoke(null, arrayOfNulls<Any>(0))
            if (invoke is String) {
                processName = invoke
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return processName
    }

    /**
     * 通过ActivityManager 获取进程名，需要IPC通信
     */
    private fun getCurrentProcessNameByActivityManager(context: Context): String? {
        val pid = Process.myPid()
        context.activityManager?.runningAppProcesses?.forEach {
            if (it.pid == pid) {
                return it.processName
            }
        }
        return null
    }
}