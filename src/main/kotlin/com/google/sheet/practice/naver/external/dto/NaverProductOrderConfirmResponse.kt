package com.google.sheet.practice.naver.external.dto

data class NaverProductOrderConfirmResponse(
    val timestamp: String,
    val traceId: String,
    val data: NaverProductOrderConfirmResultResponse,
)

data class NaverProductOrderConfirmResultResponse(
    val successProductOrderInfos: List<NaverProductOrderConfirmSuccessResponse>,
    val failProductOrderInfos: List<NaverProductOrderConfirmFailResponse>,
)

data class NaverProductOrderConfirmSuccessResponse(
    val productOrderId: String,
    val isReceiverAddressChanged: Boolean
)

data class NaverProductOrderConfirmFailResponse(
    val productOrderId: String,
    val code: String,
    val message: String,
)