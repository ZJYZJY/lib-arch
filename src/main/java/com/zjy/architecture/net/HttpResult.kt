package com.zjy.architecture.net

import java.io.Serializable

/**
 * @author zhengjy
 * @since 2020/05/20
 * Description:网络请求结果
 */
class HttpResult<T>(
    val code: Int,
    val message: String?,
    val data: T
) : Serializable