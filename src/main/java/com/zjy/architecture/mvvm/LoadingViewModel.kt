package com.zjy.architecture.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.Serializable

/**
 * @author zhengjy
 * @since 2019/08/15
 * Description:带加载框状态的ViewModel
 */
open class LoadingViewModel : LifecycleViewModel() {

    private val _loading: MutableLiveData<Loading> by lazy { MutableLiveData<Loading>() }
    val loading: LiveData<Loading> = _loading

    /**
     * 加载框引用计数
     */
    private var count = 0

    fun loading(cancelable: Boolean) {
        if (count++ <= 0) {
            _loading.value = Loading(true, cancelable)
        }
    }

    fun dismiss() {
        if (--count <= 0) {
            count = 0
            _loading.value = Loading(false)
        }
    }
}

data class Loading(
    /**
     * 是否正在显示
     */
    var loading: Boolean = true,
    /**
     * 加载框是否可取消
     */
    var cancelable: Boolean = true
) : Serializable