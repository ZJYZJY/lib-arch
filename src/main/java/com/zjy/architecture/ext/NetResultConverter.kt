package com.zjy.architecture.ext

import com.google.gson.JsonSyntaxException
import com.zjy.architecture.data.*
import com.zjy.architecture.exception.ApiException
import com.zjy.architecture.net.HttpResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.lang.ClassCastException
import java.net.MalformedURLException
import java.net.UnknownHostException
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
                Result.Error(ApiException(it.message))
            }
        }
    } catch (e: Exception) {
        if (e is CancellationException) {
            // do nothing
            throw e
        } else {
            Result.Error(handleException(e))
        }
    }
}

fun handleException(t: Exception?): ApiException {
    return if (t == null) {
        ApiException(UNKNOWN_ERROR)
    } else if (t is CertificateException || t is SSLHandshakeException) {
        ApiException(CERT_ERROR)
    } else if (t is MalformedURLException || t is UnknownHostException) {
        ApiException(HOST_ERROR)
    } else if (t is HttpException) {
        if (t.code() / 100 == 5) {
            // 状态码5XX则为服务器错误
            ApiException(SERVER_ERROR)
        } else {
            // 其他状态码统一报请求错误
            ApiException(REQUEST_ERROR)
        }
    } else if (t is IOException) {
        ApiException(CONNECTION_ERROR)
    } else if (t is JsonSyntaxException) {
        ApiException(PARSE_ERROR)
    } else if (t is ClassCastException) {
        ApiException(STRUCTURE_ERROR)
    } else if (t is ApiException) {
        t
    } else {
        ApiException(t)
    }
}