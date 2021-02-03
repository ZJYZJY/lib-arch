package com.zjy.architecture.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.os.Process
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * This provides methods to help Activities load their UI.
 */
object ActivityUtils {

    private val activityList: MutableList<Activity> = ArrayList()
    private var activityCount = AtomicInteger(0)

    fun registerActivityLifecycleCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                registerActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                activityStart()
            }

            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                activityStop()
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                unRegisterActivity(activity)
            }
        })
    }

    @Synchronized
    private fun registerActivity(activity: Activity) {
        activityList.add(activity)
    }

    @Synchronized
    private fun unRegisterActivity(activity: Activity) {
        activityList.remove(activity)
    }

    private fun activityStart() {
        if (isBackground) {
            for (l in listeners) {
                l.onResumeApp()
            }
        }
        activityCount.incrementAndGet()
    }

    private fun activityStop() {
        activityCount.decrementAndGet()
        if (isBackground) {
            for (l in listeners) {
                l.onLeaveApp()
            }
        }
    }

    fun getTopActivity(): Activity {
        return activityList.last()
    }

    fun isActivityTop(context: Context, clazz: Class<*>): Boolean {
        return isActivityTop(context, clazz.name)
    }

    /**
     *
     * 判断某activity是否处于栈顶
     * @return  true在栈顶 false不在栈顶
     */
    fun isActivityTop(context: Context, clazzName: String): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val name = manager.getRunningTasks(1)[0].topActivity!!.className
        return name == clazzName
    }

    /**
     * 退出程序
     */
    @Synchronized
    fun exitApp(kill: Boolean = false) {
        // 结束activity队列中的所有activity
        for (activity in activityList) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        if (kill) {
            Process.killProcess(Process.myPid())
        }
    }

    val isBackground: Boolean
        get() = activityCount.get() == 0

    fun getActivityCount(): Int {
        return activityList.size
    }

    fun isActivityExist(activityName: String): Boolean {
        for (activity in activityList) {
            if (activity.javaClass.name == activityName) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun finish(activityName: String) {
        for (activity in activityList) {
            if (activity.javaClass.name == activityName) {
                activity.finish() // 会执行destroy方法，而此方法中有removeActivity(activity)的方法
            }
        }
    }

    private val listeners = Collections.synchronizedList(ArrayList<OnAppStateChangedListener>())

    fun addOnAppStateChangedListener(listener: OnAppStateChangedListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeOnAppStateChangedListener(listener: OnAppStateChangedListener?) {
        listeners.remove(listener)
    }

    interface OnAppStateChangedListener {
        /**
         * app进入前台
         */
        fun onResumeApp()

        /**
         * app进入后台
         */
        fun onLeaveApp()
    }
}