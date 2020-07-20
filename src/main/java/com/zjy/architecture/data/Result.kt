package com.zjy.architecture.data


/**
 * @author zhengjy
 * @since 2019/09/30
 * Description:
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val e: Throwable) : Result<Nothing>()

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$e]"
            is Loading -> "Loading"
        }
    }

    fun isSucceed(): Boolean {
        return this is Success
    }

    fun isLoading(): Boolean {
        return this is Loading
    }

    fun data(): T {
        return (this as Success<T>).data ?: (Any() as T)
    }

    fun dataOrNull(): T? {
        return (this as? Success<T>)?.data
    }

    fun error(): Throwable {
        return (this as Error).e
    }

    fun errorOrNull(): Throwable? {
        return (this as? Error)?.e
    }
}