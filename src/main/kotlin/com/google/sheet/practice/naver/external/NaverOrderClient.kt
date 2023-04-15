package com.google.sheet.practice.naver.external

import com.google.sheet.practice.naver.external.dto.NaverOrdersResponse
import com.google.sheet.practice.naver.external.dto.NaverProductOrdersDetailRequest
import com.google.sheet.practice.naver.external.dto.NaverProductOrdersDetailResponse
import com.google.sheet.practice.naver.oauth.OauthNaverClient
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI
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
        val lastChangedFrom = dateTime.minusHours(33) // naver 에서 KST 기준으로 구하기 때문에 24 + 9시간(KST)을 더 해줌
        val url = URI.create(
            "https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders/last-changed-statuses?" +
                    "lastChangedFrom=${lastChangedFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))}"
        )
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer ${oauthNaverClient.accessToken().access_token}")
        val request = HttpEntity<Any>(headers)
        val response: ResponseEntity<NaverOrdersResponse?> = restTemplate.exchange(url, HttpMethod.GET, request)
        return response.body
            ?: throw IllegalArgumentException("네이버 주문 목록을 불러오지 못했습니다. statusCode = ${response.statusCode}")
    }

    fun productOrdersDetail(naverProductOrdersDetailRequest: NaverProductOrdersDetailRequest): NaverProductOrdersDetailResponse {
        val url = URI.create("https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders/query")
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer ${oauthNaverClient.accessToken().access_token}")
        val request = HttpEntity<Any>(naverProductOrdersDetailRequest, headers)
        val response: ResponseEntity<NaverProductOrdersDetailResponse> =
            restTemplate.exchange(url, HttpMethod.POST, request)
        return response.body
            ?: throw IllegalArgumentException("네이버 주문 목록 상세를 불러오지 못했습니다. statusCode = ${response.statusCode}")
    }
}
