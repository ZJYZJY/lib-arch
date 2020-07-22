package com.zjy.architecture.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.zjy.architecture.mvvm.Loading
import com.zjy.architecture.widget.LoadingDialog

/**
 * @author zhengjy
 * @since 2020/07/22
 * Description:
 */
abstract class BaseActivity : AppCompatActivity(), Loadable {

    var TAG = javaClass.simpleName

    @get:LayoutRes
    abstract val layoutId: Int

    //自定义加载框
    open var dialog: LoadingDialog? = null

    protected abstract fun initView()

    protected abstract fun initData()

    protected abstract fun setEvent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutId != 0) {
            setContentView(layoutId)
        }
        initView()
        initData()
        setEvent()
    }

    fun setupLoading(loading: Loading) {
        if (loading.loading) {
            loading(loading.cancelable)
        } else {
            dismiss()
        }
    }

    override fun loading(cancelable: Boolean) {
        if (dialog == null) {
            dialog = LoadingDialog(this, cancelable)
            dialog?.setCanceledOnTouchOutside(false)
        }
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    override fun dismiss() {
        if (!isFinishing && dialog?.isShowing == true) {
            dialog?.cancel()
        }
    }
}

val BaseActivity.instance
    get() = this