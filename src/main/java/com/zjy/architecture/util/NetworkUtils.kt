package com.zjy.architecture.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.RequiresPermission
import com.zjy.architecture.Arch

/**
 * @author zhengjy
 * @since 2020/07/29
 * Description:
 */
object NetworkUtils {

    private val manager by lazy {
        Arch.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun getActiveNetworkInfo(): NetworkInfo? {
        return manager.activeNetworkInfo
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val info = getActiveNetworkInfo()
        return info != null && info.isAvailable
    }
}