package com.google.sheet.practice.naver.external

import com.google.sheet.practice.naver.external.dto.*
import com.google.sheet.practice.naver.oauth.OauthNaverClient
import org.apache.http.client.utils.URIBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * https://apicenter.commerce.naver.com/ko/basic/commerce-api
 */
@Component
class NaverOrderClient(
    private val restTemplate: RestTemplate = RestTemplate(),
    private val oauthNaverClient: OauthNaverClient,
) {

    fun lastChangedStatusOrders(dateTime: LocalDateTime): NaverOrdersResponse {
        val lastChangedFrom = dateTime.minusHours(24)
        val path = "/external/v1/pay-order/seller/product-orders/last-changed-statuses"
        val url = baseUriBuilder(path)
            .addParameter(
                "lastChangedFrom",
                lastChangedFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
            )
            .build()
        val request = httpEntity<Any>()
        val response: ResponseEntity<NaverOrdersResponse?> = restTemplate.exchange(url, HttpMethod.GET, request)
        return response.body
            ?: throw IllegalArgumentException("네이버 주문 목록을 불러오지 못했습니다. statusCode = ${response.statusCode}")
    }

    fun productOrdersDetail(requestBody: NaverProductOrdersDetailRequest): NaverProductOrdersDetailResponse {
        val path = "/external/v1/pay-order/seller/product-orders/query"
        val url = baseUriBuilder(path).build()
        val request = httpEntity(body = requestBody)
        val response: ResponseEntity<NaverProductOrdersDetailResponse> =
            restTemplate.exchange(url, HttpMethod.POST, request)
        return response.body
            ?: throw IllegalArgumentException("네이버 주문 목록 상세를 불러오지 못했습니다. statusCode = ${response.statusCode}")
    }

    fun productOrderConfirm(productOrderIds: List<String>): NaverProductOrderConfirmResponse {
        val path = "/external/v1/pay-order/seller/product-orders/confirm"
        val url = baseUriBuilder(path).build()
        val body = NaverProductOrderConfirmRequest(productOrderIds = productOrderIds)
        val request = httpEntity(body)
        val response: ResponseEntity<NaverProductOrderConfirmResponse> =
            restTemplate.exchange(url, HttpMethod.POST, request)
        return response.body
            ?: throw IllegalArgumentException("네이버 주문 발주 확인 처리 요청이 실패했습니다. statusCode = ${response.statusCode}")
    }

    private fun baseUriBuilder(path: String): URIBuilder {
        return URIBuilder()
            .setScheme(SCHEMA)
            .setHost(HOST)
            .setPath(path)
    }

    private fun <T> httpEntity(body: T? = null): HttpEntity<T> {
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, this.authorizationHeaderValue())
        return HttpEntity<T>(body, headers)
    }

    private fun authorizationHeaderValue(): String {
        return "Bearer ${oauthNaverClient.accessToken().access_token}"
    }

    companion object {
        private const val SCHEMA = "https"
        private const val HOST = "api.commerce.naver.com"
    }
}
