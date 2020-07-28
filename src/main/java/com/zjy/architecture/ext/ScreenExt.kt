package com.zjy.architecture.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * @author zhengjy
 * @since 2020/05/18
 * Description:
 */
/**
 * 将dp转换为px
 */
// 将dp转换为px
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).roundToInt()

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

/**
 * 将px转换为dp
 */
val Int.px
    get() = this / Resources.getSystem().displayMetrics.density

/**
 * 获取屏幕的宽高
 */
val Context.screenSize: Point
    get() {
        val point = Point()
        windowManager?.defaultDisplay?.getSize(point)
        return point
    }
