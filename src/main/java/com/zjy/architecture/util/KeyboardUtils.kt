package com.zjy.architecture.util

import android.view.View
import com.zjy.architecture.ext.inputMethodManager

object KeyboardUtils {

    @JvmStatic
    fun showKeyboard(view: View) {
        view.context.inputMethodManager?.apply {
            view.requestFocus()
            showSoftInput(view, 0)
        }
    }

    @JvmStatic
    fun hideKeyboard(view: View) {
        view.context.inputMethodManager?.apply {
            hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @JvmStatic
    fun toggleSoftInput(view: View) {
        view.context.inputMethodManager?.apply {
            toggleSoftInput(0, 0)
        }
    }
}