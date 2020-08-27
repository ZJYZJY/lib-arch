package com.zjy.architecture.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.zjy.architecture.ext.tryWith
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

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
    fun copyToCacheFile(context: Context, uri: Uri?): File? {
        if (uri == null) return null
        var fileName = tryWith {
            context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) {
                    it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                } else {
                    ""
                }
            }
        }
        if (fileName.isNullOrEmpty()) {
            val document = DocumentFile.fromSingleUri(context, uri)
            if (document?.type == null) {
                throw IllegalArgumentException("uri must be a file not a directory")
            }
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(document.type)
            fileName = "${System.currentTimeMillis()}.${ext}"
        }

        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val copyFile = File(cacheDir.absolutePath + File.separator + fileName)
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

    /**
     * 根据mime类型，创建一个缓存文件Uri
     */
    fun createCacheUri(context: Context, mimeType: String, authority: String): Uri {
        return createCacheFile(context, mimeType).toUri(context, authority)
    }

    /**
     * 根据mime类型，创建一个缓存文件File
     */
    fun createCacheFile(context: Context, mimeType: String): File {
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val timeStamp = System.currentTimeMillis()
        val prefix = when {
            mimeType.startsWith("image") -> "IMG"
            mimeType.startsWith("video") -> "VID"
            else -> "DOC"
        }
        val cacheDir = context.externalCacheDir ?: context.cacheDir
        return File(cacheDir, "${prefix}_${timeStamp}.$ext")
    }

    fun file2Uri(context: Context, file: File, authority: String): Uri {
        return if (isAndroidN) {
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }
}

fun File.toUri(context: Context, authority: String): Uri {
    return FileUtils.file2Uri(context, this, authority)
}