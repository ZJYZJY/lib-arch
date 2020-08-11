package com.zjy.architecture.util.preference

import kotlin.reflect.KProperty

/**
 * @author zhengjy
 * @since 2020/01/13
 * Description:Preference存储代理类
 */
class PreferenceDelegate<T>(
    private val key: String,
    private val value: T,
    private val sp: IStorage = DefaultPreference.instance
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return sp.getValue(key, value)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        sp.putValue(key, value)
    }
}

/**
 * 应用需要使用存储时，实现此接口
 *
 * @example:
 *
 * object AppPreference : Preference {
 *     override val sp: IStorage = PreferenceStorage(sharedPreference)
 *     val isLogin by PreferenceDelegate("IS_LOGIN", false, sp)
 * }
 *
 */
interface Preference {

    /**
     * 提供存储功能的具体实现
     */
    val sp: IStorage
        get() = DefaultPreference.instance

    operator fun <T> get(key: String, defaultValue: T): T {
        return sp.getValue(key, defaultValue)
    }

    operator fun <T> set(key: String, value: T) {
        sp.putValue(key, value)
    }
}