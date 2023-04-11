package com.google.sheet.practice.coupang.external.dto

import com.google.sheet.practice.coupang.domain.CoupangOrderReceiver

data class CoupangOrderReceiverResponse(
    val name: String,
    val safeNumber: String,
    val addr1: String,
    val addr2: String,
    val postCode: String,
) {
    fun toDomain(): CoupangOrderReceiver {
        return CoupangOrderReceiver(
            name = this.name,
            safeNumber = this.safeNumber,
            addr1 = addr1,
            addr2 = addr2,
            postCode = postCode,
        )
    }
}
