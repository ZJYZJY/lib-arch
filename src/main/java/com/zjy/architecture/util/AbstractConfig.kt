package com.zjy.architecture.util

import android.content.Context
import com.tencent.mars.xlog.Log
import com.zjy.architecture.Arch
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties

/**
 * @author zhengjy
 * @since 2020/08/11
 * Description:
 */
abstract class AbstractConfig(fileName: String, context: Context = Arch.context) {

    lateinit var config: Properties

    init {
        try {
            context.assets.open(fileName).use {
                config = Properties()
                InputStreamReader(it, StandardCharsets.UTF_8).use { reader ->
                    config.load(reader)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            logD("打开配置文件失败：${fileName}")
        }
    }

    operator fun get(key: String): String {
        return config.getProperty(key, "")
    }
}