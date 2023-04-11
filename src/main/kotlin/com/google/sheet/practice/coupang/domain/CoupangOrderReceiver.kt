package com.google.sheet.practice.coupang.domain

data class CoupangOrderReceiver(
    val name: String,
    val safeNumber: String,
    val addr1: String,
    val addr2: String,
    val postCode: String,
)
