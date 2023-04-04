package com.google.sheet.practice.naver.domain

import com.google.sheet.practice.naver.ProductOrderStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NaverProductOrderDetail(
    val quantity: Int,
    val productOrderId: Long,
    val productOrderStatus: ProductOrderStatus,
    val productName: String,
    val productOption: String,
    val totalPaymentAmount: Int,
    val expectedSettlementAmount: Int,
    val deliveryFeeAmount: Int,
    val orderDate: LocalDateTime,
    val shippingAddress: NaverOrderShippingAddress,
) {
    fun flatValues(): List<String> {
        return listOf(
            productOrderId.toString(),
            productOrderStatus.description,
            orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            productName,
            productOption,
            quantity.toString(),
            totalPaymentAmount.toString(),
            expectedSettlementAmount.toString(),
            deliveryFeeAmount.toString(),
            shippingAddress.zipCode,
            shippingAddress.baseAddress,
            shippingAddress.detailedAddress,
            shippingAddress.tel1,
            shippingAddress.name,
        )
    }
}