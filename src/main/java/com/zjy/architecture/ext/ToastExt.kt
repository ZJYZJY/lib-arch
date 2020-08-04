package com.zjy.architecture.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * @author zhengjy
 * @since 2020/05/21
 * Description:
 */
private var sToast: Toast? = null

fun Context.toast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT, config: (Toast.() -> Unit)? = null) {
    sToast?.cancel()
    sToast = Toast.makeText(this, res, duration).apply {
        config?.invoke(this)
        show()
    }
}

fun Context.toast(content: CharSequence, duration: Int = Toast.LENGTH_SHORT, config: (Toast.() -> Unit)? = null) {
    sToast?.cancel()
    sToast = Toast.makeText(this, content, duration).apply {
        config?.invoke(this)
        show()
    }
}