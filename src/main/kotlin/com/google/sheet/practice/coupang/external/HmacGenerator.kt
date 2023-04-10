package com.google.sheet.practice.coupang.external

import org.apache.commons.codec.binary.Hex
import org.springframework.http.HttpMethod
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacGenerator {
    fun hmac(httpMethod: HttpMethod, uri: String): String {
        return Hmac.generate(httpMethod.name, uri, SECRET_KEY, ACCESS_KEY)
    }

    private const val ACCESS_KEY = "e6b54338-4b48-4328-a047-ac721bb3112f"
    private const val SECRET_KEY = "6dad2375ac3d5f7ba090a30cfc5dfc921e4ee191"
}

object Hmac {
    private const val HMAC_SHA_256 = "HmacSHA256"
    private const val HMAC_SHA_1 = "HmacSHA1"
    fun generate(method: String, uri: String, secretKey: String, accessKey: String?): String {
        val parts = uri.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (parts.size > 2) {
            throw RuntimeException("incorrect uri format")
        } else {
            val path = parts[0]
            var query = ""
            if (parts.size == 2) {
                query = parts[1]
            }
            val dateFormatGmt = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
            dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
            val datetime = dateFormatGmt.format(Date())
            val message = datetime + method + path + query
            val signature: String
            signature = try {
                val signingKey =
                    SecretKeySpec(secretKey.toByteArray(Charset.forName("UTF-8")), "HmacSHA256")
                val mac = Mac.getInstance("HmacSHA256")
                mac.init(signingKey)
                val rawHmac = mac.doFinal(message.toByteArray(Charset.forName("UTF-8")))
                Hex.encodeHexString(rawHmac)
            } catch (var14: GeneralSecurityException) {
                throw IllegalArgumentException("Unexpected error while creating hash: " + var14.message, var14)
            }
            String.format(
                "CEA algorithm=%s, access-key=%s, signed-date=%s, signature=%s",
                "HmacSHA256",
                accessKey,
                datetime,
                signature
            )
        }
    }
}
