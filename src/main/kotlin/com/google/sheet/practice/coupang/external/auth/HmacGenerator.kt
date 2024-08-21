package com.google.sheet.practice.coupang.external.auth

import org.springframework.http.HttpMethod

/**
 * [쿠팡 Open API Hmac 가이드](https://developers.coupangcorp.com/hc/ko/articles/360033461914-HMAC-Signature-%EC%83%9D%EC%84%B1)
 */
object HmacGenerator {
    fun hmac(httpMethod: HttpMethod, uri: String): String {
        return Hmac.generate(httpMethod.name, uri, SECRET_KEY, ACCESS_KEY)
    }

    private const val ACCESS_KEY = "e6b54338-4b48-4328-a047-ac721bb3112f"
    private const val SECRET_KEY = "96622f8d666bd4ef9aa77213d96cbd3f5a6e9fdb"
}

