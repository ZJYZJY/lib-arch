package com.zjy.architecture.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.zjy.architecture.R
import java.io.File

/**
 * @author zhengjy
 * @since 2020/05/21
 * Description:
 */
object InstallUtil {

    const val UNKNOWN_CODE = 2020

    /**
     * 安装apk
     *
     * @param context   Activity上下文
     * @param file      需要安装的apk文件
     * @param authority FileProvider对应的authority
     */
    fun install(context: Context, file: File, authority: String) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startInstallO(context, file, authority)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> startInstallN(context, file, authority)
            else -> startInstall(context, file)
        }
    }
    fun install(context: Context, apkUri: Uri) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startInstallO(context, apkUri)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> startInstallN(context, apkUri)
            else -> startInstall(context, apkUri)
        }
    }

    /**
     * android1.x-6.x
     */
    private fun startInstall(context: Context, file: File) {
        val apkUri = Uri.fromFile(file)
        startInstall(context, apkUri)
    }

    /**
     * android1.x-6.x
     */
    private fun startInstall(context: Context, apkUri: Uri) {
        val install = Intent(Intent.ACTION_VIEW)
        install.setDataAndType(apkUri, "application/vnd.android.package-archive")
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(install)
    }

    /**
     * android7.x
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun startInstallN(context: Context, file: File, authority: String) {
        // authority是指在AndroidManifest中的android:authorities值
        val apkUri = FileProvider.getUriForFile(context, authority, file)
        startInstallN(context, apkUri)
    }

    /**
     * android7.x
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun startInstallN(context: Context, apkUri: Uri) {
        val install = Intent(Intent.ACTION_VIEW)
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // 添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        install.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(install)
    }

    /**
     * android8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallO(context: Context, file: File, authority: String) {
        val isGranted = context.packageManager.canRequestPackageInstalls()
        if (isGranted) {
            startInstallN(context, file, authority)
        } else {
            AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(context.getString(R.string.arch_request_install_unknown))
                .setPositiveButton(
                    context.getString(R.string.arch_confirm)
                ) { _, _ ->
                    val intent = Intent()
                    intent.data = Uri.parse("package:" + context.packageName)
                    intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                    (context as Activity).startActivityForResult(
                        intent,
                        UNKNOWN_CODE
                    )
                }
                .show()
        }
    }

    /**
     * android8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallO(context: Context, apkUri: Uri) {
        val isGranted = context.packageManager.canRequestPackageInstalls()
        if (isGranted) {
            startInstallN(context, apkUri)
        } else {
            AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(context.getString(R.string.arch_request_install_unknown))
                .setPositiveButton(
                    context.getString(R.string.arch_confirm)
                ) { _, _ ->
                    val intent = Intent()
                    intent.data = Uri.parse("package:" + context.packageName)
                    intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                    (context as Activity).startActivityForResult(
                        intent,
                        UNKNOWN_CODE
                    )
                }
                .show()
        }
    }
}