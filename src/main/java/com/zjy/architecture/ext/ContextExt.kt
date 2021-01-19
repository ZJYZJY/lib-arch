package com.zjy.architecture.ext

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author zhengjy
 * @since 2020/08/04
 * Description:
 */
val Context.versionName: String
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            ""
        }
    }

val Context.versionCode: Int
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: Exception) {
            0
        }
    }

/**
 * 支持Android P及以上的版本获取Long型版本号
 */
val Context.longVersionCode: Long
    @RequiresApi(Build.VERSION_CODES.P)
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).longVersionCode
        } catch (e: Exception) {
            0L
        }
    }

val Context.versionCodeCompat: Long
    get() {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, 0).longVersionCode
            } else {
                packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }