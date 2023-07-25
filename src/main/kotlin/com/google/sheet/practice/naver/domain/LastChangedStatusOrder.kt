package com.google.sheet.practice.naver.domain

import java.time.LocalDateTime

data class LastChangedStatusOrder(
    val receiverAddressChanged: Boolean,
    val productOrderId: Long,
    val orderId: Long,
    val productOrderStatus: ProductOrderStatus,
    val paymentDate: LocalDateTime,
    val lastChangedDate: LocalDateTime,
    val lastChangedType: LastChangedType,
) {
    fun isNewOrder() : Boolean {
        return this.isPayed() || this.isDeliveryAddressChanged()
    }

    private fun isPayed(): Boolean {
        return this.lastChangedType == LastChangedType.PAYED
    }

    private fun isDeliveryAddressChanged(): Boolean {
        return this.lastChangedType == LastChangedType.DELIVERY_ADDRESS_CHANGED
    }

    fun isCancelRequestOrder(): Boolean {
        return this.lastChangedType == LastChangedType.CLAIM_REQUESTED
    }
}