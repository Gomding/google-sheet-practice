package com.google.sheet.practice.coupang.external.dto

data class CoupangUpdateNewOrderStatusRequest(
    val vendorId: String,
    val shipmentBoxIds: List<Long>,
)