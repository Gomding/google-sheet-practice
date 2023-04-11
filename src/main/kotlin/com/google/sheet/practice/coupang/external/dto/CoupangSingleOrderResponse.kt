package com.google.sheet.practice.coupang.external.dto

data class CoupangSingleOrderResponse(
    val code: Int,
    val message: String,
    val data: List<CoupangOrderResponse>,
)