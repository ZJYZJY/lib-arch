package com.zjy.architecture.mvvm

import com.tencent.mars.xlog.Log
import com.zjy.architecture.Arch
import com.zjy.architecture.R
import com.zjy.architecture.data.Result
import com.zjy.architecture.ext.handleException
import com.zjy.architecture.ext.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author zhengjy
 * @since 2020/06/29
 * Description:
 */
abstract class RequestDSL<T> {

    internal var onStart: (() -> Unit)? = null
    internal var onRequest: (suspend CoroutineScope.() -> Result<T>)? = null
    internal var onSuccess: ((T) -> Unit)? = null
    internal var onFail: ((Throwable) -> Unit)? = null
    internal var onComplete: (() -> Unit)? = null

    @Deprecated("如果需要进行初始化操作直接进行即可，无需调用这个方法")
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
                    Log.e("LoadingViewModel", e.message)
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
    GlobalErrorHandler.handler?.invoke(e)
    onError?.invoke(e)
}

/**
 * 可以在这里定义全局请求错误处理方式
 */
object GlobalErrorHandler {
    var handler: ((Throwable) -> Unit)? = {
        Arch.context.toast(it.message ?: Arch.context.getString(R.string.arch_error_unknown1))
    }
}