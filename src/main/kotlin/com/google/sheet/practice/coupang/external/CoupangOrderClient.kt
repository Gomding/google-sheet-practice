package com.google.sheet.practice.coupang.external

import com.google.sheet.practice.coupang.external.auth.HmacGenerator
import com.google.sheet.practice.coupang.external.dto.CoupangOrdersResponse
import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import org.apache.http.client.utils.URIBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CoupangOrderClient(
    private val restTemplate: RestTemplate = RestTemplate(),
) {

    /**
     * [Coupang Open API 주문 분단위 조회 Spec](https://developers.coupangcorp.com/hc/ko/articles/360033792774-%EB%B0%9C%EC%A3%BC%EC%84%9C-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C-%EB%B6%84%EB%8B%A8%EC%9C%84-%EC%A0%84%EC%B2%B4-)
     */
    fun getOrders(searchStartDateTime: LocalDateTime, searchEndDateTime: LocalDateTime): ResponseEntity<CoupangOrdersResponse> {
        validateSearchDateTime(searchStartDateTime, searchEndDateTime)
        val uriBuilder = getOrdersUriBuilder(searchStartDateTime = searchStartDateTime, searchEndDateTime = searchEndDateTime)
        val authorization = HmacGenerator.hmac(HttpMethod.GET, uriBuilder.build().toString())
        uriBuilder.setScheme(SCHEMA).setHost(HOST)
        val headers = headers(authorization)
        val httpEntity = HttpEntity<Any>(headers)
        val response: ResponseEntity<CoupangOrdersResponse> = restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, httpEntity)
        println(response)
        return response
    }

    private fun validateSearchDateTime(searchStartDateTime: LocalDateTime, searchEndDateTime: LocalDateTime) {
        if (searchStartDateTime.isAfter(searchEndDateTime)) {
            throw IllegalArgumentException("검색 시작일시는 검색 종료일시 보다 이전 시간이어야 합니다. searchStartDateTime=$searchStartDateTime, searchEndDateTime=$searchEndDateTime")
        }
    }

    private fun getOrdersUriBuilder(searchStartDateTime: LocalDateTime, searchEndDateTime: LocalDateTime): URIBuilder {
        val path = "/v2/providers/openapi/apis/api/v4/vendors/$VENDOR_ID/ordersheets"
        val uriBuilder = URIBuilder()
            .setPath(path)
            .addParameter("createdAtFrom", searchStartDateTime.format(DATE_TIME_FORMATTER))
            .addParameter("createdAtTo", searchEndDateTime.format(DATE_TIME_FORMATTER))
            .addParameter("searchType", "timeFrame")
            .addParameter("status", CoupangOrderStatus.INSTRUCT.name)
        return uriBuilder
    }

    private fun headers(authorization: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, authorization)
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json")
        return headers
    }

    companion object {
        private const val HOST = "api-gateway.coupang.com"
        private const val SCHEMA = "https"

        // authorization api info
        private const val VENDOR_ID = "A00332047"

        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    }
}