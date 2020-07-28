package com.zjy.architecture.ext

import com.google.gson.GsonBuilder
import com.zjy.architecture.Arch
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author zhengjy
 * @since 2019/11/04
 * Description:创建[OkHttpClient]和[Retrofit]的方法
 */
fun OkHttpClient.Builder.addChangeUrlInterceptor(manager: RetrofitUrlManager): OkHttpClient.Builder {
    return manager.with(this)
}

fun OkHttpClient.Builder.addInterceptors(vararg interceptor: Interceptor): OkHttpClient.Builder {
    interceptor.forEach {
        addInterceptor(it)
    }
    return this
}

/**
 * 创建Retrofit对象通用方法
 *
 * @param client    实际请求的OkHttpClient
 * @param url       请求接口的baseUrl
 */
fun createRetrofit(client: OkHttpClient, url: String): Retrofit {
    return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .baseUrl(url)
            .build()
}

/**
 * 创建Retrofit对象通用方法
 *
 * @param manager       用于接口地址切换的对象
 * @param interceptor   具体业务逻辑所需的拦截器
 */
fun createOkHttpClient(manager: RetrofitUrlManager, vararg interceptor: Interceptor): OkHttpClient {
    val builder = OkHttpClient.Builder()
    if (Arch.debug) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
    }
    return builder.connectTimeout(20L, TimeUnit.SECONDS)
            .readTimeout(20L, TimeUnit.SECONDS)
            .writeTimeout(20L, TimeUnit.SECONDS)
            .addInterceptors(*interceptor)
            .addChangeUrlInterceptor(manager)
            .build()
}