package com.zjy.architecture.ext

import com.google.gson.Gson
import com.zjy.architecture.Arch
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author zhengjy
 * @since 2019/11/04
 * Description:创建[OkHttpClient]和[Retrofit]的方法
 */
fun OkHttpClient.Builder.addChangeUrlInterceptor(manager: RetrofitUrlManager?): OkHttpClient.Builder {
    if (manager == null) {
        return this
    }
    return manager.with(this)
}

fun OkHttpClient.Builder.addInterceptors(vararg interceptor: Interceptor): OkHttpClient.Builder {
    interceptor.forEach {
        addInterceptor(it)
    }
    return this
}

fun OkHttpClient.Builder.addSSLSocketFactory(sslFactory: SSLSocketFactory?, trustManager: X509TrustManager?): OkHttpClient.Builder {
    if (sslFactory != null && trustManager != null) {
        sslSocketFactory(sslFactory, trustManager)
    }
    return this
}

/**
 * 创建Retrofit对象通用方法
 *
 * @param client    实际请求的OkHttpClient
 * @param gson      Gson
 * @param url       请求接口的baseUrl
 */
fun createRetrofit(client: OkHttpClient, gson: Gson, url: String): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(url)
        .build()
}

/**
 * 创建Retrofit对象通用方法
 *
 * @param manager       用于接口地址切换的对象
 * @param interceptor   具体业务逻辑所需的拦截器
 */
fun createOkHttpClient(manager: RetrofitUrlManager?, sslFactory: SSLSocketFactory?,
                       trustManager: X509TrustManager?, vararg interceptor: Interceptor): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(20L, TimeUnit.SECONDS)
        .readTimeout(20L, TimeUnit.SECONDS)
        .writeTimeout(20L, TimeUnit.SECONDS)
        .addSSLSocketFactory(sslFactory, trustManager)
        .addInterceptors(*interceptor)
        .addChangeUrlInterceptor(manager)
    if (Arch.debug) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
    }
    return builder.build()
}