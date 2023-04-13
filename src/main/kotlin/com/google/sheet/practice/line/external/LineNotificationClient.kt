package com.google.sheet.practice.line.external

import org.apache.http.client.utils.URIBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

/**
 * https://notify-bot.line.me/doc/en/
 */
@Component
class LineNotificationClient(
    private val restTemplate: RestTemplate = RestTemplate(),
) {

    fun sendNotification(message: String) {
        val host = "notify-api.line.me"
        val path = "/api/notify"
        val uri = URIBuilder()
            .setScheme("https")
            .setHost(host)
            .setPath(path)
            .addParameter("message", message)
            .build()
        val headers = HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, "Bearer $TOKEN") }
        val httpEntity: HttpEntity<Any> = HttpEntity(headers)
        val response: ResponseEntity<Any> = restTemplate.exchange(uri, HttpMethod.POST, httpEntity)
    }

    companion object {
        private const val TOKEN = "83vgbHSTkZ4yIUqbg7SkvnnKsrYryuM6ZPLRdyNOx1k"
    }
}
