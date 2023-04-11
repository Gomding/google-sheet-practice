package com.google.sheet.practice.coupang.external.dto

import com.google.sheet.practice.coupang.domain.CoupangOrderer

data class CoupangOrdererResponse(
    val name: String,
    val email: String,
    val safeNumber: String,
    val ordererNumber: String?,
) {
    fun toDomain(): CoupangOrderer {
        return CoupangOrderer(
            name = this.name,
            email = this.email,
            safeNumber = this.safeNumber,
            ordererNumber = this.ordererNumber,
        )
    }
}
