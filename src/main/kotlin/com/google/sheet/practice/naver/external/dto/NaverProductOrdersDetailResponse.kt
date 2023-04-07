package com.google.sheet.practice.naver.external.dto

import com.google.sheet.practice.naver.domain.ProductOrderStatus
import com.google.sheet.practice.naver.domain.NaverOrderShippingAddress
import com.google.sheet.practice.naver.domain.NaverProductOrderDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NaverProductOrdersDetailResponse(
    val timestamp: String,
    val data: List<NaverProductOrderDetailDataResponse>,
    val traceId: String,
)

data class NaverProductOrderDetailDataResponse(
    val productOrder: NaverProductOrderDetailResponse,
    val order: OrderResponse,
) {
    fun toDomain(): NaverProductOrderDetail {
        return NaverProductOrderDetail(
            quantity = productOrder.quantity,
            productOrderId = productOrder.productOrderId,
            productOrderStatus = ProductOrderStatus.of(productOrder.productOrderStatus),
            productName = productOrder.productName,
            productOption = productOrder.productOption,
            totalPaymentAmount = productOrder.totalPaymentAmount,
            expectedSettlementAmount = productOrder.expectedSettlementAmount,
            deliveryFeeAmount = productOrder.deliveryFeeAmount,
            orderDate = LocalDateTime.parse(this.order.orderDate, DateTimeFormatter.ISO_ZONED_DATE_TIME),
            shippingAddress = productOrder.shippingAddress.toDomain()
        )
    }
}

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
)

data class OrderShippingAddressResponse(
    val zipCode: String,
    val baseAddress: String,
    val detailedAddress: String,
    val tel1: String,
    val name: String,
) {
    fun toDomain(): NaverOrderShippingAddress {
        return NaverOrderShippingAddress(
            zipCode = this.zipCode,
            baseAddress = this.baseAddress,
            detailedAddress = this.detailedAddress,
            tel1 = this.tel1,
            name = this.name,
        )
    }
}

data class OrderResponse(
    val orderDate: String,
)
