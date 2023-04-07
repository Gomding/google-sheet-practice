package com.google.sheet.practice.naver.domain

import java.time.LocalDateTime

class LastChangedStatusOrder(
    val receiverAddressChanged: Boolean,
    val productOrderId: Long,
    val orderId: Long,
    val productOrderStatus: ProductOrderStatus,
    val paymentDate: LocalDateTime,
    val lastChangedDate: LocalDateTime,
    val lastChangedType: LastChangedType,
) {
    fun isPayed(): Boolean {
        return this.lastChangedType == LastChangedType.PAYED
    }
}