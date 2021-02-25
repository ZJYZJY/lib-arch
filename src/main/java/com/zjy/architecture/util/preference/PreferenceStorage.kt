package com.zjy.architecture.util.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.zjy.architecture.Arch

/**
 * @author zhengjy
 * @since 2020/07/15
 * Description:SharedPreferences存储
 */
class PreferenceStorage(
    private val sp: SharedPreferences,
    private val globalSync: Boolean = false
) : IStorage {

    override fun <T> getValue(name: String, default: T): T = with(sp) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default) ?: ""
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw java.lang.IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        res as T
    }

    override fun <T> putValue(name: String, value: T, sync: Boolean) = sp.edit(globalSync or sync) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }
    }
}

/**
 * 应用默认的SharedPreferences
 */
internal class DefaultPreference {

    companion object {

        val instance by lazy {
            val shared = Arch.context.getSharedPreferences(
                "${Arch.context.packageName}.default_preference",
                Context.MODE_PRIVATE
            )
            PreferenceStorage(shared)
        }
    }
}