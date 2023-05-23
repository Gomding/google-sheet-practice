package com.google.sheet.practice.coupang.external.dto

data class CoupangCanceledOrdersResponse(
    val code: Int,
    val message: String,
    val data: List<CoupangCanceledOrderResponse>,
)
