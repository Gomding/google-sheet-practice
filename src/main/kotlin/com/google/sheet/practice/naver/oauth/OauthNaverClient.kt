package com.google.sheet.practice.naver.oauth

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import java.net.URI

@Component
class OauthNaverClient(
    private val restTemplate: RestTemplate = RestTemplate(),
) {

    fun accessToken(): NaverAccessTokenResponse {
        val clientId = "3H79nDA9wDD0BiyuZ31Rex"
        val timestamp = System.currentTimeMillis()
        val clientSecretSign = this.generateSignature(timestamp)
        val grantType = "client_credentials"
        val type = "SELF"
        val url = URI.create("https://api.commerce.naver.com/external/v1/oauth2/token?" +
                "client_id=$clientId&" +
                "timestamp=$timestamp&" +
                "client_secret_sign=$clientSecretSign&" +
                "grant_type=$grantType&" +
                "type=$type")
        val response: NaverAccessTokenResponse = restTemplate.postForObject(url = url)
        return response
    }

    private fun generateSignature(timestamp: Long): String {
        return NaverSignatureGenerator.generateSignature(
            clientId = "3H79nDA9wDD0BiyuZ31Rex",
            clientSecret = "$2a$04\$gIlB6XBkjqCoJUur3tumJ.",
            timestamp = timestamp,
        )
    }
}

data class NaverAccessTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
)