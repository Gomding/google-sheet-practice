package com.google.sheet.practice.coupang.service

import com.google.sheet.practice.common.CurrentDateTimeService
import com.google.sheet.practice.coupang.domain.CoupangOrder
import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import com.google.sheet.practice.coupang.external.CoupangOrderClient
import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import org.springframework.stereotype.Service

@Service
class CoupangOrderService(
    private val coupangOrderClient: CoupangOrderClient,
    private val currentDateTimeService: CurrentDateTimeService,
    private val googleSheetClient: GoogleSheetClient,
) {
    fun getNewOrders(): List<CoupangOrder> {
        val searchEndDateTime = currentDateTimeService.currentDateTime()
        val searchStartDateTime = searchEndDateTime.minusHours(1)
        val newOrdersResponse = coupangOrderClient.getOrders(
            searchStartDateTime = searchStartDateTime,
            searchEndDateTime = searchEndDateTime,
            orderStatus = CoupangOrderStatus.ACCEPT,
        )
        return newOrdersResponse.data.map { it.toDomain() }
    }
}