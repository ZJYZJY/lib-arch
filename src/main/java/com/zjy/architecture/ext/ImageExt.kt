package com.zjy.architecture.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions

/**
 * @author zhengjy
 * @since 2020/05/18
 * Description:ImageView扩展函数
 */
fun ImageView.load(
    url: String,
    placeholder: Int = 0,
    block: RequestBuilder<Drawable>.(Int) -> RequestBuilder<Drawable> = DEFAULT_APPLY
) {
    Glide.with(this).load(url).let {
        block.invoke(it, placeholder)
    }.into(this)
}

fun ImageView.load(
    uri: Uri,
    placeholder: Int = 0,
    block: RequestBuilder<Drawable>.(Int) -> RequestBuilder<Drawable> = DEFAULT_APPLY
) {
    Glide.with(this).load(uri).let {
        block.invoke(it, placeholder)
    }.into(this)
}

private val DEFAULT_APPLY: RequestBuilder<Drawable>.(Int) -> RequestBuilder<Drawable> = {
    apply(
        if (it == 0) {
            RequestOptions()
        } else {
            RequestOptions().placeholder(it)
        }
    )
}
