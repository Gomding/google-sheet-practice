package com.google.sheet.practice.oauth

import org.apache.commons.lang3.StringUtils
import org.mindrot.jbcrypt.BCrypt
import java.nio.charset.StandardCharsets
import java.util.*

object NaverSignatureGenerator {
    fun generateSignature(clientId: String?, clientSecret: String?, timestamp: Long?): String {
        // 밑줄로 연결하여 password 생성
        val password: String = StringUtils.joinWith("_", clientId, timestamp)
        // bcrypt 해싱
        val hashedPw: String = BCrypt.hashpw(password, clientSecret)
        // base64 인코딩
        return Base64.getUrlEncoder().encodeToString(hashedPw.byteInputStream(StandardCharsets.UTF_8).readBytes())
    }
}