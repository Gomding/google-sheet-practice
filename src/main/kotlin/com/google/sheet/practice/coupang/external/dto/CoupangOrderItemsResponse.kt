package com.google.sheet.practice.coupang.external.dto

import com.google.sheet.practice.coupang.domain.CoupangOrderItem

data class CoupangOrderItemsResponse(
    val vendorItemName: String,
    val shippingCount: Int,
    val salesPrice: Int, // 개당 상품 가격
    val orderPrice: Int, // 총 결제 가격
    val sellerProductItemName: String,
    val canceled: Boolean, // 주문 취소 여부
) {
    fun toDomain(): CoupangOrderItem {
        return CoupangOrderItem(
            vendorItemName = this.vendorItemName,
            shippingCount = this.shippingCount,
            salesPrice = this.salesPrice,
            orderPrice = this.orderPrice,
            sellerProductItemName = this.sellerProductItemName,
            canceled = this.canceled,
        )
    }
}
