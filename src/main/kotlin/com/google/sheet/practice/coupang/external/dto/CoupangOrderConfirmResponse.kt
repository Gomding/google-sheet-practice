package com.google.sheet.practice.coupang.external.dto

data class CoupangOrderConfirmResponse(
    val code: Int,
    val message: String,
    val data: CoupangOrderConfirmDataResponse
)

data class CoupangOrderConfirmDataResponse(
    val responseKey: String,
    val responseCode: Int,
    val responseMessage: String,
    val responseList: List<CoupangOrderConfirmInfoResponse>,
)

data class CoupangOrderConfirmInfoResponse(
    val shipmentBoxId: Long,
    val succeed: Boolean,
    val resultCode: String,
    val resultMessage: String,
    val retryRequired: Boolean,
)
