package com.zjy.architecture.di

import org.koin.core.context.KoinContextHandler
import org.koin.core.scope.Scope

/**
 * @author zhengjy
 * @since 2020/08/14
 * Description:
 */

/**
 * 获取rootScope
 */
val rootScope: Scope
    get() = KoinContextHandler.get()._scopeRegistry.rootScope