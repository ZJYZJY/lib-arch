package com.zjy.architecture.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

object FragmentHandleBackUtil {

    private fun handleBackPress(fragmentManager: FragmentManager): Boolean {
        val fragments = fragmentManager.fragments
        if (fragments.isEmpty()) {
            return false
        }
        fragments.reversed().forEach {
            if (isFragmentBackHandled(it)) {
                return true
            }
        }
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun handleBackPress(fragment: Fragment): Boolean {
        return handleBackPress(fragment.childFragmentManager)
    }

    fun handleBackPress(fragmentActivity: FragmentActivity): Boolean {
        return handleBackPress(fragmentActivity.supportFragmentManager)
    }

    private fun isFragmentBackHandled(fragment: Fragment): Boolean {
        return fragment is FragmentHandleBackInterface &&
                fragment.isVisible &&
                fragment.onBackPressedSupport()
    }
}