package com.google.sheet.practice.coupang.domain

data class CoupangOrderItem(
    val vendorItemName: String,
    val shippingCount: Int,
    val salesPrice: Int, // 개당 상품 가격
    val orderPrice: Int, // 총 결제 가격
    val sellerProductItemName: String,
    val canceled: Boolean,
) {
    fun toFlatString(): String {
        return """------
            |주문 상품 이름:$vendorItemName
            |주문 개수:$shippingCount
            |총 결제금액:$orderPrice
            |------
               """.trimMargin()
    }
}
