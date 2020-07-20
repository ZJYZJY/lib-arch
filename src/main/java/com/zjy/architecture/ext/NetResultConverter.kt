package com.zjy.architecture.ext

import com.bumptech.glide.load.HttpException
import com.zjy.architecture.data.Result
import com.zjy.architecture.net.HttpResult
import java.io.IOException
import java.lang.ClassCastException
import java.lang.RuntimeException
import java.net.MalformedURLException
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException

/**
 * @author zhengjy
 * @since 2019/11/04
 * Description:网络请求相关的扩展函数
 */
const val SUCCESS_CODE = 0

suspend fun <T> apiCall(code: Int = SUCCESS_CODE, call: suspend () -> HttpResult<T>): Result<T> {
    return try {
        call().let {
            if (it.code == code) {
                Result.Success(it.data)
            } else {
                Result.Error(handleException(Exception(it.message)))
            }
        }
    } catch (e: Exception) {
        Result.Error(handleException(e))
    }
}

fun handleException(t: Exception?): Exception {
    return if (t == null) {
        RuntimeException("unknown error")
    } else if (t is CertificateException || t is SSLHandshakeException) {
        t
    } else if (t is MalformedURLException) {
        t
    } else if (t is HttpException) {
        t
    } else if (t is IOException) {
        t
    } else if (t is ClassCastException) {
        t
    } else {
        t
    }
}