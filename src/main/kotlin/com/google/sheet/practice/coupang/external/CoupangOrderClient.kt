package com.google.sheet.practice.coupang.external

import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import com.google.sheet.practice.coupang.external.auth.HmacGenerator
import com.google.sheet.practice.coupang.external.dto.CoupangOrderConfirmResponse
import com.google.sheet.practice.coupang.external.dto.CoupangOrdersResponse
import com.google.sheet.practice.coupang.external.dto.CoupangSingleOrderResponse
import com.google.sheet.practice.coupang.external.dto.CoupangUpdateNewOrderStatusRequest
import org.apache.http.client.utils.URIBuilder
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException.BadRequest
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CoupangOrderClient(
    private val restTemplate: RestTemplate = RestTemplate(),
) {

    fun getOrder(orderId: Long): CoupangSingleOrderResponse? {
        try {
            val path = "/v2/providers/openapi/apis/api/v4/vendors/$VENDOR_ID/$orderId/ordersheets"
            val uriBuilder = URIBuilder().setPath(path)
            val authorization = authorization(HttpMethod.GET, uriBuilder)
            uriBuilder.setScheme(SCHEMA).setHost(HOST)
            val httpEntity = httpEntity<Any>(authorization = authorization)
            val response: ResponseEntity<CoupangSingleOrderResponse> =
                restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, httpEntity)
            val body = response.body
                ?: throw RuntimeException("쿠팡 단건 주문을 조회했으나 응답 body가 존재하지 않습니다. statusCode=${response.statusCode}, body=${response.body}")
            if (body.code != HttpStatus.OK.value()) {
                throw RuntimeException("쿠팡 단건 주문을 요청이 실패했습니다. statusCode=${body.code}, message=${body.message}")
            }
            return body
        } catch (e: BadRequest) {
            return null
        }
    }

    /**
     * [Coupang Open API 주문 분단위 조회 Spec](https://developers.coupangcorp.com/hc/ko/articles/360033792774-%EB%B0%9C%EC%A3%BC%EC%84%9C-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C-%EB%B6%84%EB%8B%A8%EC%9C%84-%EC%A0%84%EC%B2%B4-)
     */
    fun getOrders(
        searchStartDateTime: LocalDateTime,
        searchEndDateTime: LocalDateTime,
        orderStatus: CoupangOrderStatus
    ): CoupangOrdersResponse {
        validateSearchDateTime(searchStartDateTime, searchEndDateTime)
        val uriBuilder = getOrdersUriBuilder(
            searchStartDateTime = searchStartDateTime,
            searchEndDateTime = searchEndDateTime,
            orderStatus = orderStatus
        )
        val response: ResponseEntity<CoupangOrdersResponse> = requestGetOrders(uriBuilder)
        val body = response.body
            ?: throw RuntimeException("쿠팡 주문 목록을 조회했으나 응답 body가 존재하지 않습니다. statusCode=${response.statusCode}, body=${response.body}")
        if (body.code != HttpStatus.OK.value()) {
            throw RuntimeException("쿠팡 주문 목록을 가져오는데 실패했습니다. statusCode=${body.code}, message=${body.message}")
        }
        return body
    }

    private fun requestGetOrders(uriBuilder: URIBuilder): ResponseEntity<CoupangOrdersResponse> {
        val authorization = authorization(HttpMethod.GET, uriBuilder)
        val httpEntity = httpEntity<Any>(authorization = authorization)
        uriBuilder.setScheme(SCHEMA).setHost(HOST)
        return restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, httpEntity)
    }

    private fun authorization(httpMethod: HttpMethod, uriBuilder: URIBuilder) =
        HmacGenerator.hmac(httpMethod, uriBuilder.build().toString())

    private fun validateSearchDateTime(searchStartDateTime: LocalDateTime, searchEndDateTime: LocalDateTime) {
        if (searchStartDateTime.isAfter(searchEndDateTime)) {
            throw IllegalArgumentException("검색 시작일시는 검색 종료일시 보다 이전 시간이어야 합니다. searchStartDateTime=$searchStartDateTime, searchEndDateTime=$searchEndDateTime")
        }
    }

    private fun getOrdersUriBuilder(
        searchStartDateTime: LocalDateTime,
        searchEndDateTime: LocalDateTime,
        orderStatus: CoupangOrderStatus
    ): URIBuilder {
        val path = "/v2/providers/openapi/apis/api/v4/vendors/$VENDOR_ID/ordersheets"
        return URIBuilder()
            .setPath(path)
            .addParameter("createdAtFrom", searchStartDateTime.format(DATE_TIME_FORMATTER))
            .addParameter("createdAtTo", searchEndDateTime.format(DATE_TIME_FORMATTER))
            .addParameter("searchType", "timeFrame")
            .addParameter("status", orderStatus.name)
    }

    private fun <T> httpEntity(authorization: String, body: T? = null): HttpEntity<T> {
        val headers = headers(authorization)
        return HttpEntity<T>(body, headers)
    }

    private fun headers(authorization: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, authorization)
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json")
        return headers
    }

    fun updateOrdersConfirm(shipmentBoxIds: List<Long>): CoupangOrderConfirmResponse {
        val path = "/v2/providers/openapi/apis/api/v4/vendors/$VENDOR_ID/ordersheets/acknowledgement"
        val uriBuilder = URIBuilder()
            .setPath(path)
        val authorization = authorization(HttpMethod.PUT, uriBuilder)
        val body = CoupangUpdateNewOrderStatusRequest(vendorId = VENDOR_ID, shipmentBoxIds = shipmentBoxIds)
        val httpEntity = httpEntity(body = body, authorization = authorization)
        uriBuilder.setScheme(SCHEMA).setHost(HOST)
        val response: ResponseEntity<CoupangOrderConfirmResponse> =
            restTemplate.exchange(uriBuilder.build(), HttpMethod.PUT, httpEntity)
        return response.body?: throw java.lang.IllegalArgumentException("쿠팡 주문 상태를 상품 준비중으로 변경하는데 실패했습니다.")
    }

    companion object {
        private const val HOST = "api-gateway.coupang.com"
        private const val SCHEMA = "https"

        // authorization api info
        private const val VENDOR_ID = "A00332047"

        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    }
}