package com.zjy.architecture.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException

/**
 * @author zhengjy
 * @since 2020/07/27
 * Description:
 */
object FileUtils {

    /**
     * Android10开始无法操作外部文件，建议使用SAF进行文件操作
     * 如果必须要使用[File]对象（如第三方库上传文件等），则可以拷贝到应用
     * 专属缓存目录下进行操作，文件大小不宜过大
     *
     * @param   uri   需要复制的文件uri
     * @return  复制到外部缓存目录的文件
     */
    @RequiresPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    @WorkerThread
    fun copyToCacheFile(context: Context, uri: Uri): File? {
        val path = uri.path ?: return null
        val index = path.lastIndexOf('/')
        val fileName = if (index != -1) {
            path.substring(index + 1)
        } else {
            val document = DocumentFile.fromSingleUri(context, uri)
            if (document?.type == null) {
                throw IllegalArgumentException("uri must be a file not a directory")
            }
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(document.type)
            "${System.currentTimeMillis()}.${ext}"
        }

        val copyFile = File(context.externalCacheDir?.absolutePath + File.separator + fileName)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BufferedOutputStream(FileOutputStream(copyFile)).use {
                    input.copyTo(it)
                    it.flush()
                }
            }
            copyFile
        } catch (e: Exception) {
            null
        }
    }
}