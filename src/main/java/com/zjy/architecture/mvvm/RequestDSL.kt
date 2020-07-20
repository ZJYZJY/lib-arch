package com.zjy.architecture.mvvm

import com.tencent.mars.xlog.Log
import com.zjy.architecture.data.Result
import com.zjy.architecture.ext.handleException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author zhengjy
 * @since 2020/06/29
 * Description:
 */
abstract class RequestDSL<T> {

    var onStart: (() -> Unit)? = null
    var onRequest: (suspend CoroutineScope.() -> Result<T>)? = null
    var onSuccess: ((T) -> Unit)? = null
    var onFail: ((Throwable) -> Unit)? = null
    var onComplete: (() -> Unit)? = null

    fun onStart(block: () -> Unit) {
        this.onStart = block
    }

    fun onRequest(block: suspend CoroutineScope.() -> Result<T>) {
        this.onRequest = block
    }

    fun onSuccess(block: (T) -> Unit) {
        this.onSuccess = block
    }

    fun onFail(block: (Throwable) -> Unit) {
        this.onFail = block
    }

    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    abstract fun build()
}

fun <T> LoadingViewModel.request(
    loading: Boolean = true,
    cancelable: Boolean = true,
    block: RequestDSL<T>.() -> Unit
) {
    object : RequestDSL<T>() {
        override fun build() {
            launch {
                try {
                    if (loading) {
                        loading(cancelable)
                    }
                    onStart?.invoke()
                    onRequest?.invoke(this)?.apply {
                        if (isSucceed()) {
                            onSuccess?.invoke(data())
                        } else {
                            processError(onFail, error())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LoadingViewModel", "", e)
                    if (e is CancellationException) {
                        // do nothing
                    } else {
                        processError(onFail, handleException(e))
                    }
                } finally {
                    onComplete?.invoke()
                    if (loading) {
                        dismiss()
                    }
                }
            }
        }
    }.apply(block).build()
}

private fun processError(onError: ((Throwable) -> Unit)? = null, e: Throwable) {
    onError?.invoke(e)
}