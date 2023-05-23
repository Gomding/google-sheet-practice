package com.google.sheet.practice.coupang.external.dto

data class CoupangCanceledOrderResponse(
    val orderId: Long,
    val paymentId: Long,
    val receiptType: String,
    val receiptStatus: String,
    val requesterName: String,
    val requesterPhoneNumber: String,
    val cancelReason: String,
    val cancelReasonCategory1: String,
    val cancelReasonCategory2: String,
    val returnItems: List<CoupangCanceledOrderItemResponse>
)
