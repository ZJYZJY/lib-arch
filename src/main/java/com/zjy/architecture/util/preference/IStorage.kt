package com.zjy.architecture.util.preference

/**
 * @author zhengjy
 * @since 2020/07/15
 * Description:通用存储接口
 */
interface IStorage {

    fun <T> getValue(name: String, default: T): T

    fun <T> putValue(name: String, value: T)
}