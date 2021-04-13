package com.zjy.architecture.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
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

/**
 * 适配统一分辨率，宽度统一为360dp
 */
fun setCustomDensity(activity: Activity) {
    val applicationMetrics = Resources.getSystem().displayMetrics
    val targetDensity: Float = (applicationMetrics.widthPixels / 360).toFloat()
    val targetDensityDpi = 160 * targetDensity

    applicationMetrics.density = targetDensity
    applicationMetrics.scaledDensity = targetDensity
    applicationMetrics.densityDpi = targetDensityDpi.toInt()

    val activityMetrics = activity.resources.displayMetrics
    activityMetrics.density = targetDensity
    activityMetrics.scaledDensity = targetDensity
    activityMetrics.densityDpi = targetDensityDpi.toInt()
}

/**
 * 全屏状态下，允许内容显示到刘海中
 */
fun notchSupport(window: Window?) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        window?.attributes = window?.attributes?.apply {
            layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }
}