package com.google.sheet.practice.coupang.external.auth

import org.apache.commons.codec.binary.Hex
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacSignature {
    private const val HMAC_SHA_256 = "HmacSHA256"
    private val CHARSET = Charset.forName("UTF-8")

    fun create(secretKey: String, datetime: String, uri: String, method: String): String {
        try {
            val message = message(datetime = datetime, uri = uri, method = method)
            val signingKey = SecretKeySpec(secretKey.toByteArray(CHARSET), HMAC_SHA_256)
            val mac = Mac.getInstance(HMAC_SHA_256)
            mac.init(signingKey)
            val rawHmac = mac.doFinal(message.toByteArray(CHARSET))
            return Hex.encodeHexString(rawHmac)
        } catch (var14: GeneralSecurityException) {
            throw IllegalArgumentException("Unexpected error while creating hash: " + var14.message, var14)
        }
    }

    private fun message(datetime: String, uri: String, method: String): String {
        val parts = uri.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size > 2) {
            throw RuntimeException("incorrect uri format")
        }
        val path = parts[0]
        var query = ""
        if (parts.size == 2) {
            query = parts[1]
        }
        return datetime + method + path + query
    }
}