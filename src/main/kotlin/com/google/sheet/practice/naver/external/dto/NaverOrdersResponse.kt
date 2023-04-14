package com.google.sheet.practice.naver.external.dto

import com.google.sheet.practice.naver.domain.ProductOrderStatus
import com.google.sheet.practice.naver.domain.LastChangedType
import com.google.sheet.practice.naver.domain.LastChangedStatusOrder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NaverOrdersResponse(
    val timestamp: String,
    val data: NaverOrdersDataResponse?,
    val traceId: String,
)

data class NaverOrdersDataResponse(
    val lastChangeStatuses: List<NaverLastChangedStatusOrderResponse>,
    val count: Long,
)

data class NaverLastChangedStatusOrderResponse(
    val receiverAddressChanged: Boolean,
    val productOrderId: Long,
    val orderId: Long,
    val productOrderStatus: String,
    val paymentDate: String,
    val lastChangedDate: String,
    val lastChangedType: String,
) {
    fun toDomain(): LastChangedStatusOrder {
        return LastChangedStatusOrder(
            receiverAddressChanged = this.receiverAddressChanged,
            productOrderId = this.productOrderId,
            orderId = this.orderId,
            productOrderStatus = ProductOrderStatus.of(this.productOrderStatus),
            paymentDate = LocalDateTime.parse(this.paymentDate, DateTimeFormatter.ISO_ZONED_DATE_TIME),
            lastChangedDate = LocalDateTime.parse(this.lastChangedDate, DateTimeFormatter.ISO_ZONED_DATE_TIME),
            lastChangedType = LastChangedType.of(this.lastChangedType),
        )
    }
}
