package com.sipa.boot.sdk.android

import java.util.UUID

/**
 * @author caszhou
 * @date 2019-01-22
 */
class SipaAndroid {
    companion object {
        // 前台
        const val APPLICATION_ID_FP_TODO = 1000001

        // 请求头 token key
        const val HEADER_TOKEN_KEY = "Authorization"

        // 请求头 token value prefix
        const val HEADER_TOKEN_VALUE_PREFIX = "Bearer "

        // token key
        const val STORAGE_TOKEN_KEY = "TOKEN"

        // login info key
        const val STORAGE_LOGIN_INFO_KEY = "LOGIN_INFO"

        // user info key
        const val STORAGE_USER_INFO_KEY = "USER_INFO"

        // 成功
        const val SUCCESS_CODE = "000000"
        const val STATUS_KEY = "errorCode"

        // 登陆校验失败
        val LOGIN_VERIFICATION_FAILED = listOf("000030", "000034", "000035", "000036", "000037", "000038")

        // 权限校验失败
        val PERMISSION_VERIFICATION_FAILED = listOf("000031", "000032", "000033")

        const val REQUEST_ID = "X-Request-Id"
        const val REQUEST_FROM = "X-Request-From"
        const val MERCHANT_ID = "X-Merchant-Id"

        const val REQUEST_FROM_TODO = "todo"

        const val APPLICATION_ID_TODO = "1000001"

        fun getRequestId(): String {
            return UUID.randomUUID().toString()
        }

        // web存储 设备 token key
        fun storageDeviceKey(applicationId: String, companyId: String): String {
            return "$STORAGE_TOKEN_KEY-$applicationId-$companyId"
        }
    }

    fun version(): String {
        return "1.0.0"
    }
}
