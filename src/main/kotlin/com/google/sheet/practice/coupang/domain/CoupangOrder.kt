package com.google.sheet.practice.coupang.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
) {
    fun isBeforeDelivery(): Boolean {
        return status.isBeforeDelivery()
    }

    fun flatValues(): List<String> {
        return listOf(
            orderId.toString(),
            shipmentBoxId.toString(),
            orderedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            status.description,
            orderer.name,
            shippingPrice,
            parcelPrintMessage,
            receiver.name,
            receiver.addr1,
            receiver.addr2,
            orderItems.map { it.toFlatString() }.joinToString("\n"),
            overseaShipping!!.ordererPhoneNumber,
            overseaShipping.personalCustomsClearanceCode
        )
    }
}
