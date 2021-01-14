package com.zjy.architecture.net

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author zhengjy
 * @since 2020/05/20
 * Description:网络请求结果
 */
class HttpResult<T>(
    @SerializedName(value = "code", alternate = ["result"])
    val code: Int,
    @SerializedName(value = "message", alternate = ["msg"])
    val message: String?,
    val data: T
) : Serializable