package com.google.sheet.practice.coupang.external.auth

import java.text.SimpleDateFormat
import java.util.*

object Hmac {
    private const val HMAC_SHA_256 = "HmacSHA256"
    private const val HMAC_SHA_1 = "HmacSHA1"

    fun generate(method: String, uri: String, secretKey: String, accessKey: String?): String {
        val datetime = datetime()
        val signature = HmacSignature.create(secretKey = secretKey, datetime = datetime, uri = uri, method = method)
        return "CEA algorithm=$HMAC_SHA_256, access-key=$accessKey, signed-date=$datetime, signature=$signature"
    }

    private fun datetime(): String {
        val dateFormatGmt = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
        dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormatGmt.format(Date())
    }
}