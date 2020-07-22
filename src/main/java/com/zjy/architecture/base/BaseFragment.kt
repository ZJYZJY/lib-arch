package com.zjy.architecture.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.zjy.architecture.mvvm.Loading
import com.zjy.architecture.widget.LoadingDialog

/**
 * @author zhengjy
 * @since 2020/07/22
 * Description:
 */
abstract class BaseFragment : Fragment(), Loadable {

    var TAG = javaClass.simpleName

    private var dialog: LoadingDialog? = null

    @get:LayoutRes
    abstract val layoutId: Int

    protected abstract fun initView(view: View?, savedInstanceState: Bundle?)

    protected abstract fun initData()

    protected abstract fun setEvent()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view, savedInstanceState)
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
        val aty = activity ?: return
        if (aty is Loadable) {
            aty.loading(cancelable)
            return
        }
        if (dialog == null) {
            dialog = LoadingDialog(activity, cancelable)
        }
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    override fun dismiss() {
        val aty = activity ?: return
        if (aty is Loadable) {
            aty.dismiss()
            return
        }
        if (!aty.isFinishing && dialog?.isShowing == true) {
            dialog!!.cancel()
        }
    }
}