package com.zjy.architecture.ext

import android.graphics.Paint
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.zjy.architecture.base.Loadable
import com.zjy.architecture.mvvm.Loading

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

inline fun View.haptic(feedbackConstant: Int = HapticFeedbackConstants.KEYBOARD_TAP) {
    performHapticFeedback(feedbackConstant)
}

/**
 * 设置中粗体
 */
fun TextView.mediumBold() {
    paint.apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
    }
}

fun TextView.mediumText(text: CharSequence) {
    mediumBold()
    setText(text)
}

fun TextView.mediumText(res: Int) {
    mediumBold()
    setText(res)
}

/**
 * Reverse the view's visibility
 */
inline fun View.reverseVisibility(needInvisible: Boolean = false) {
    if (isVisible) {
        if (needInvisible) invisible() else gone()
    } else visible()
}

var <T : View> T.lastClickTime: Long
    set(value) = setTag(1766613352, value)
    get() = getTag(1766613352) as? Long ?: 0

inline fun <T : View> T.singleClick(time: Long = 500L, crossinline block: (T) -> Unit) {
    setOnClickListener {
        checkSingle(time, block)
    }
}

inline fun <T : View> T.checkSingle(time: Long = 500L, crossinline block: (T) -> Unit) {
    val currentTimeMillis = System.currentTimeMillis()
    if (currentTimeMillis - lastClickTime > time || this is Checkable) {
        lastClickTime = currentTimeMillis
        block(this)
    }
}

fun <T : View> T.singleClick(time: Long = 500L, listener: View.OnClickListener) {
    setOnClickListener {
        checkSingle(time, listener)
    }
}

fun <T : View> T.checkSingle(time: Long = 500L, listener: View.OnClickListener) {
    val currentTimeMillis = System.currentTimeMillis()
    if (currentTimeMillis - lastClickTime > time || this is Checkable) {
        lastClickTime = currentTimeMillis
        listener.onClick(this)
    }
}

fun DialogFragment.setupLoading(loading: Loading) {
    val ctx = activity
    if (ctx is Loadable?) {
        if (loading.loading) {
            ctx?.loading(loading.cancelable)
        } else {
            ctx?.dismiss()
        }
    }
}