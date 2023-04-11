package com.google.sheet.practice.coupang.external.dto

import com.google.sheet.practice.coupang.domain.CoupangOverseaShipping

data class CoupangOverseaShippingResponse(
    val personalCustomsClearanceCode: String,
    val ordererPhoneNumber: String,
) {
    fun toDomain(): CoupangOverseaShipping {
        return CoupangOverseaShipping(
            personalCustomsClearanceCode = this.personalCustomsClearanceCode,
            ordererPhoneNumber = this.ordererPhoneNumber,
        )
    }
}
