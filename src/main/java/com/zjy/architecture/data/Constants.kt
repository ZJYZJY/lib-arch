package com.zjy.architecture.data

/**
 * @author zhengjy
 * @since 2020/07/28
 * Description:常量类
 */

/** 接口基本错误码*/
// 忽略错误
const val IGNORE_ERROR = -1
// 未知错误
const val UNKNOWN_ERROR = 0
// 证书错误
const val CERT_ERROR = 1
// 域名错误
const val HOST_ERROR = 2
// 服务器错误
const val SERVER_ERROR = 3
// 网络连接错误
const val CONNECTION_ERROR = 4
// 数据解析错误
const val PARSE_ERROR = 5
// 请求错误
const val REQUEST_ERROR = 6
// 网络未连接错误
const val DISCONNECT_ERROR = 7
// 返回结构错误
const val STRUCTURE_ERROR = 8

// 默认动画时长
const val DEFAULT_ANIM_TIME = 250L