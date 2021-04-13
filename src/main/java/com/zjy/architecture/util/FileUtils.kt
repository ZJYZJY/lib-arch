package com.zjy.architecture.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.documentfile.provider.DocumentFile
import com.zjy.architecture.data.LocalFile
import com.zjy.architecture.ext.bytes2Hex
import com.zjy.architecture.ext.tryWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.*
import java.security.MessageDigest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    suspend fun queryFile(context: Context, path: String?) = suspendCancellableCoroutine<Bundle?> { cont ->
        if (path.isNullOrEmpty()) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }
        if (path.isContent()) {
            val uri = path.toUri()
            val signal = CancellationSignal()
            cont.invokeOnCancellation {
                signal.cancel()
            }
            try {
                // 默认查询返回name和size字段
                context.contentResolver.query(uri, null, null, null, null, signal)?.use {
                    if (it.moveToFirst()) {
                        val name = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                        val size = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                        cont.resume(bundleOf(
                            MediaStore.MediaColumns.DISPLAY_NAME to name,
                            MediaStore.MediaColumns.SIZE to size
                        ))
                    } else {
                        cont.resume(null)
                    }
                } ?: cont.resume(null)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        } else {
            val file = File(path)
            val size = FileInputStream(file).use { it.channel.size() }
            cont.resume(bundleOf(
                MediaStore.MediaColumns.DISPLAY_NAME to file.name,
                MediaStore.MediaColumns.SIZE to size
            ))
        }
    }

    fun getMd5(context: Context, path: String?): String {
        if (path.isNullOrEmpty()) return ""
        return try {
            if (path.isContent()) {
                context.contentResolver.openInputStream(path.toUri())?.use { input ->
                    val digest = MessageDigest.getInstance("MD5")
                    var buffer = input.readBytes()
                    while (buffer.isNotEmpty()) {
                        digest.update(buffer, 0, buffer.size)
                        buffer = input.readBytes()
                    }
                    digest.digest().bytes2Hex()
                } ?: ""
            } else {
                FileInputStream(path).use { input ->
                    val digest = MessageDigest.getInstance("MD5")
                    var buffer = input.readBytes()
                    while (buffer.isNotEmpty()) {
                        digest.update(buffer, 0, buffer.size)
                        buffer = input.readBytes()
                    }
                    digest.digest().bytes2Hex()
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun fileExists(context: Context, url: String?): Boolean {
        if (url.isNullOrEmpty()) return false
        return if (url.isContent()) {
            DocumentFile.fromSingleUri(context, url.toUri())?.exists() ?: false
        } else {
            File(url).exists()
        }
    }

    fun getExtension(path: String?): String = path?.split(".")?.lastOrNull() ?: ""

    @SuppressLint("InlinedApi")
    fun createPictureUri(context: Context, folder: String, fileName: String): Uri? {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .absolutePath + "/" + folder
        val resolver = context.contentResolver
        //设置文件参数到ContentValues中
        val values = ContentValues().apply {
            //设置文件名
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            //设置文件类型
            put(MediaStore.MediaColumns .MIME_TYPE, "image/jpeg")
            //注意：MediaStore.Images.Media.RELATIVE_PATH需要targetSdkVersion=29,
            //故该方法只可在Android10的手机上执行
            if (isAndroidQ) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/$folder"
                )
            } else {
                put(MediaStore.MediaColumns.DATA, "$path/$fileName")
            }
        }
        if (!isAndroidQ) {
            // android10以下可能需要手动创建文件夹
            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }
        val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        //insertUri表示文件保存的uri路径
        return resolver.insert(external, values)
    }

    @SuppressLint("InlinedApi")
    suspend fun saveToDownloads(
        context: Context,
        media: InputStream?,
        folder: String,
        fileName: String,
        mimeType: String,
        progressCallback: ((Float) -> Unit)? = null
    ): LocalFile? = withContext(Dispatchers.IO) {
        if (folder.isEmpty() || media == null) {
            return@withContext null
        }
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .absolutePath + "/" + folder
        val resolver = context.contentResolver
        //设置文件参数到ContentValues中
        val values = ContentValues().apply {
            //设置文件名
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            //设置文件类型
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            //注意：MediaStore.MediaColumns.RELATIVE_PATH需要targetSdkVersion=29,
            //故该方法只可在Android10的手机上执行
            if (isAndroidQ) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DOWNLOADS}/$folder"
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                put(MediaStore.MediaColumns.DATA, "$path/$fileName")
            }
        }
        val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        //insertUri表示文件保存的uri路径
        val insertUri = resolver.insert(external, values)
        if (insertUri != null) {
            try {
                if (!isAndroidQ) {
                    // android10以下可能需要手动创建文件夹
                    val dir = File(path)
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                }
                val output = resolver.openOutputStream(insertUri)
                // 将数据流写入insertUri
                output?.use { outputStream ->
                    media.use {
                        var bytesCopied: Long = 0
                        var oldProgress = 0
                        val total = it.available()
                        val buffer = ByteArray(8 * 1024)
                        var bytes = it.read(buffer)
                        while (bytes >= 0) {
                            outputStream.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            bytes = it.read(buffer)
                            val progress = (bytesCopied * 1.0f / total * 100).toInt()
                            if (progress != oldProgress) {
                                oldProgress = progress
                                progressCallback?.invoke(bytesCopied * 1.0f / total)
                            }
                        }
                        outputStream.flush()
                    }
                }
                if (isAndroidQ) {
                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(insertUri, values, null, null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return@withContext LocalFile("${path}/${fileName}", insertUri)
    }
}

fun File.toUri(context: Context, authority: String): Uri {
    return FileUtils.file2Uri(context, this, authority)
}

const val CONTENT_URI = "content://"

fun String?.isContent() = this?.startsWith(CONTENT_URI) ?: false