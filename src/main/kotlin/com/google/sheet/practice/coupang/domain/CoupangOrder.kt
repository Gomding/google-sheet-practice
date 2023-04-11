package com.google.sheet.practice.coupang.domain

import java.time.LocalDateTime

data class CoupangOrder(
    val shipmentBoxId: Long,
    val orderId: Long,
    val orderedAt: LocalDateTime, // format : yyyy-MM-dd'T'HH:mm:ss
    val status: CoupangOrderStatus,
    val orderer: CoupangOrderer,
    val shippingPrice: String,
    val parcelPrintMessage: String = "",
    val receiver: CoupangOrderReceiver,
    val orderItems: List<CoupangOrderItem>,
    val overseaShipping: CoupangOverseaShipping?,
)
