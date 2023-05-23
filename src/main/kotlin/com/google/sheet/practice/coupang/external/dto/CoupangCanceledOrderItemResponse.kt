package com.google.sheet.practice.coupang.external.dto

data class CoupangCanceledOrderItemResponse(
    val vendorItemPackageName: String, // 딜명
    val vendorItemName: String, // 옵션명
    val sellerProductName: String, // 업체등록상품명
    val cancelCount: Long,
)