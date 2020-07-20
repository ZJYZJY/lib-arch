package com.zjy.architecture.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.zjy.architecture.ext.vibrator

/**
 * @author zhengjy
 * @since 2018/10/25
 * Description:震动工具类
 */
object VibrateUtils {

    private var vibrator: Vibrator? = null

    /**
     * 简单震动
     *
     * @param context     调用震动的Context
     * @param millisecond 震动的时间，毫秒
     */
    @SuppressLint("MissingPermission")
    fun simple(context: Context, millisecond: Int) {
        vibrator = context.vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    millisecond.toLong(),
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator?.vibrate(millisecond.toLong())
        }
    }

    /**
     * 复杂的震动
     *
     * @param context 调用震动的Context
     * @param pattern 震动形式
     * @param repeat  震动的次数，-1不重复，非-1为从pattern的指定下标开始重复
     */
    @SuppressLint("MissingPermission")
    fun complicated(
        context: Context,
        pattern: LongArray?,
        repeat: Int
    ) {
        vibrator = context.vibrator
        vibrator?.vibrate(pattern, repeat)
    }

    /**
     * 停止震动
     */
    @SuppressLint("MissingPermission")
    fun stop() {
        vibrator?.cancel()
        vibrator = null
    }
}