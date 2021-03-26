package com.zjy.architecture.data

import android.net.Uri
import java.io.Serializable

/**
 * @author zhengjy
 * @since 2021/03/26
 * Description:
 */
data class LocalFile(
    val absolutePath: String?,
    val uri: Uri?
) : Serializable