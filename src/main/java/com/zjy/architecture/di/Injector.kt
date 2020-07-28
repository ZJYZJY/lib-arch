package com.zjy.architecture.di

import org.koin.core.module.Module

/**
 * @author zhengjy
 * @since 2020/07/28
 * Description:koin依赖注入模块类需要实现的方法
 */
interface Injector {

    fun inject(): Module
}