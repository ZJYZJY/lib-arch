package com.zjy.architecture.util.filters

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned

/**
 * @author zhengjy
 * @since 2020/08/24
 * Description:限制EditText小数输入位数
 */
class AccuracyInputFilter(
    private val accuracy: Int
) : InputFilter {

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        if (source == null || dest == null) {
            return null
        }
        if (".".contentEquals(source)) {
            return if (dest.isEmpty()) {
                "0."
            } else {
                null
            }
        } else {
            val spannable = SpannableStringBuilder(dest)
            spannable.replace(dstart, dend, source, start, end)
            val result = spannable.toString()
            val index = result.indexOf(".")
            return if (index > 0 && index < result.length - 1 - accuracy) {
                ""
            } else {
                null
            }
        }
    }
}