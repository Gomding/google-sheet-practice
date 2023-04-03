package com.google.sheet.practice.naver

import com.google.sheet.practice.naver.oauth.OauthNaverClient
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.lang.IllegalArgumentException
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class NaverOrderClient(
    private val restTemplate: RestTemplate = RestTemplate(),
    private val oauthNaverClient: OauthNaverClient,
) {

    fun orders(): NaverOrdersResponse {
        val currentDateTime = LocalDateTime.now()
        val lastChangedFrom = currentDateTime.minusHours(33) // naver 에서 KST 기준으로 구하기 때문에 24 + 9시간(KST)을 더 해줌
        val url = URI.create(
            "https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders/last-changed-statuses?" +
                    "lastChangedFrom=${lastChangedFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))}"
        )
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer ${oauthNaverClient.accessToken().access_token}")
        val request = HttpEntity<Any>(headers)
        val response: ResponseEntity<NaverOrdersResponse> = restTemplate.exchange(url, HttpMethod.GET, request)
        return response.body?: throw IllegalArgumentException("네이버 주문 목록을 불러오지 못했습니다. statusCode = ${response.statusCode}")
    }

    fun productOrdersDetail(productOrderIds: List<Long>): NaverProductOrdersDetailResponse {
        val url = URI.create("https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders/query")
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer ${oauthNaverClient.accessToken().access_token}")
        val request = HttpEntity<Any>(NaverProductOrdersDetailRequest(productOrderIds), headers)
        val response: ResponseEntity<NaverProductOrdersDetailResponse> = restTemplate.exchange(url, HttpMethod.POST, request)
        return response.body?: throw IllegalArgumentException("네이버 주문 목록 상세를 불러오지 못했습니다. statusCode = ${response.statusCode}")
    }
}

data class NaverProductOrdersDetailRequest(
    val productOrderIds: List<Long>,
)

data class NaverProductOrdersDetailResponse(
    val timestamp: String,
    val data: List<NaverProductOrderDetailDataResponse>,
    val traceId: String,
)

data class NaverProductOrderDetailDataResponse(
    val productOrder: NaverProductOrderDetailResponse,
)

data class NaverProductOrderDetailResponse(
    val quantity: Int,
    val productOrderId: Long,
    val productOrderStatus: String,
    val productName: String,
    val productOption: String,
    val totalPaymentAmount: Int,
    val expectedSettlementAmount: Int,
    val deliveryFeeAmount: Int,
    val shippingAddress: OrderShippingAddressResponse,
) {
    fun toList(): List<String> {
        return listOf(
            quantity.toString(),
            productOrderId.toString(),
            productOrderStatus,
            productName,
            productOption,
            totalPaymentAmount.toString(),
            expectedSettlementAmount.toString(),
            deliveryFeeAmount.toString(),
            shippingAddress.zipCode,
            shippingAddress.detailedAddress,
            shippingAddress.baseAddress,
            shippingAddress.tel1,
            shippingAddress.name,
        )
    }
}

data class OrderShippingAddressResponse(
    val zipCode: String,
    val baseAddress: String,
    val detailedAddress: String,
    val tel1: String,
    val name: String,
)

data class NaverOrdersResponse(
    val timestamp: String,
    val data: NaverOrdersDataResponse,
    val traceId: String,
)

data class NaverOrdersDataResponse(
    val lastChangeStatuses: List<NaverLastChangeStatusResponse>,
    val count: Long,
)

data class NaverLastChangeStatusResponse(
    val receiverAddressChanged: Boolean,
    val productOrderId: Long,
    val orderId: Long,
    val productOrderStatus: String,
    val paymentDate: String,
    val lastChangedDate: String,
    val lastChangedType: String,
)