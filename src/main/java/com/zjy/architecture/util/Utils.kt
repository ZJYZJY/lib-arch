package com.zjy.architecture.util

import android.os.Build
import android.os.Environment

/**
 * @author zhengjy
 * @since 2020/07/27
 * Description:
 */
/**
 * 判断是否在Android10及以上
 */
val isAndroidQ: Boolean
    get() {
        var flag = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !Environment.isExternalStorageLegacy()
        ) {
            // targetSdk大于等于29，且关闭了Legacy
            flag = true
        }
        return flag
    }

/**
 * 判断是否在Android N及以上
 */
val isAndroidN: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N