package com.google.sheet.practice.coupang.external.dto

import com.google.sheet.practice.coupang.domain.CoupangOrder
import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import java.time.LocalDateTime

data class CoupangOrderResponse(
    val shipmentBoxId: Long,
    val orderId: Long,
    val orderedAt: String, // format : yyyy-MM-dd'T'HH:mm:ss
    val status: String,
    val orderer: CoupangOrdererResponse,
    val shippingPrice: String,
    val parcelPrintMessage: String?,
    val receiver: CoupangOrderReceiverResponse,
    val orderItems: List<CoupangOrderItemsResponse>,
    val overseaShippingInfoDto: CoupangOverseaShippingResponse?,
) {
    fun toDomain(): CoupangOrder {
        return CoupangOrder(
            shipmentBoxId = this.shipmentBoxId,
            orderId = this.orderId,
            orderedAt = LocalDateTime.parse(orderedAt),
            status = CoupangOrderStatus.valueOf(status),
            orderer = this.orderer.toDomain(),
            shippingPrice = this.shippingPrice,
            parcelPrintMessage = this.parcelPrintMessage?: "",
            receiver = receiver.toDomain(),
            orderItems = this.orderItems.map { it.toDomain() },
            overseaShipping = this.overseaShippingInfoDto?.toDomain()
        )
    }
}
