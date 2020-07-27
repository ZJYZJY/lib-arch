package com.zjy.architecture.ext

import android.view.View
import android.widget.Checkable
import androidx.core.view.isVisible

/**
 * @author zhengjy
 * @since 2020/05/21
 * Description:
 */
/**
 * Set view visible
 */
inline fun View.visible() {
    visibility = View.VISIBLE
}

inline fun View.setVisible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * Set view invisible
 */
inline fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Set view gone
 */
inline fun View.gone() {
    visibility = View.GONE
}

/**
 * Reverse the view's visibility
 */
inline fun View.reverseVisibility(needInvisible: Boolean = true) {
    if (isVisible) {
        if (needInvisible) invisible() else gone()
    } else visible()
}

var <T : View> T.lastClickTime: Long
    set(value) = setTag(1766613352, value)
    get() = getTag(1766613352) as? Long ?: 0

inline fun <T : View> T.singleClick(time: Long = 500L, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            block(this)
        }
    }
}

fun <T : View> T.singleClick(time: Long = 500L, listener: View.OnClickListener) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            listener.onClick(this)
        }
    }
}