package com.zjy.architecture.exception

import com.zjy.architecture.Arch
import com.zjy.architecture.R
import com.zjy.architecture.data.*
import java.io.IOException

/**
 * @author zhengjy
 * @since 2020/07/27
 * Description:
 */
class ApiException : IOException {

    private var errorCode: Int = IGNORE_ERROR
    val code: Int
        get() = errorCode

    /**
     * 动态指定message
     *
     * @param errorCode 错误码，-1表示忽略错误显示
     */
    @JvmOverloads
    constructor(
        errorCode: Int,
        errorMessage: String? = getApiExceptionMessage(errorCode)
    ) : this(errorMessage) {
        this.errorCode = errorCode
    }

    constructor(errorMessage: String?) : super(errorMessage)

    constructor(e: Exception) : super(e)

    companion object {
        /**
         * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
         * 需要根据错误码对错误信息进行一个转换，在显示给用户
         *
         * @param code
         * @return
         */
        private fun getApiExceptionMessage(code: Int): String? {
            return when (code) {
                IGNORE_ERROR -> null
                UNKNOWN_ERROR -> Arch.context.getString(R.string.arch_error_unknown)
                CERT_ERROR -> Arch.context.getString(R.string.arch_error_certificate)
                HOST_ERROR -> Arch.context.getString(R.string.arch_error_service_domain)
                SERVER_ERROR -> Arch.context.getString(R.string.arch_error_service)
                CONNECTION_ERROR -> Arch.context.getString(R.string.arch_error_network)
                PARSE_ERROR -> Arch.context.getString(R.string.arch_error_response_parse)
                REQUEST_ERROR -> Arch.context.getString(R.string.arch_error_request)
                DISCONNECT_ERROR -> Arch.context.getString(R.string.arch_error_connect)
                STRUCTURE_ERROR -> Arch.context.getString(R.string.arch_error_data_structure)
                else -> Arch.context.getString(R.string.arch_error_unknown1)
            }
        }
    }
}