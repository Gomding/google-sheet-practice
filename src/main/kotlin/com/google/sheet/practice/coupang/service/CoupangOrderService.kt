package com.google.sheet.practice.coupang.service

import com.google.sheet.practice.common.CurrentDateTimeService
import com.google.sheet.practice.coupang.domain.CoupangOrder
import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import com.google.sheet.practice.coupang.external.CoupangOrderClient
import com.google.sheet.practice.googlesheet.GoogleSheetService
import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.googlesheet.external.GoogleSheetRange
import org.springframework.stereotype.Service

@Service
class CoupangOrderService(
    private val coupangOrderClient: CoupangOrderClient,
    private val currentDateTimeService: CurrentDateTimeService,
    private val googleSheetService: GoogleSheetService,
    private val googleSheetClient: GoogleSheetClient,
) {
    fun updateOrders() {
        val originOrders = this.originOrders()
        val newOrders = this.newOrders()
        val orders = newOrders.plus(originOrders).distinctBy { it.orderId }
        val googleSheetRange = GoogleSheetRange.create(
            sheetName = SHEET_NAME,
            columnCount = COLUMN_COUNT,
            rowCount = orders.size - 1,
        )
        googleSheetService.deleteBy(SHEET_NAME, COLUMN_COUNT, 100)
        googleSheetClient.batchUpdateValues(
            range = googleSheetRange,
            values = orders.map { it.flatValues() },
        )
    }

    fun originOrders(): List<CoupangOrder> {
        val coupangOriginOrdersSheetRange = GoogleSheetRange(
            sheetName = SHEET_NAME,
            startColumnRow = SHEET_START_COLUMN_ROW,
            endColumnRow = "N100"
        )
        val originOrderIds = googleSheetClient.getGoogleSheetRows(coupangOriginOrdersSheetRange.changeToString())
            .map { it[0].toString().toLong() }
        return originOrderIds.map { coupangOrderClient.getOrder(it).data[0].toDomain() }
    }

    fun newOrders(): List<CoupangOrder> {
        val searchEndDateTime = currentDateTimeService.currentDateTime()
        val searchStartDateTime = searchEndDateTime.minusHours(1)
        val newOrdersResponse = coupangOrderClient.getOrders(
            searchStartDateTime = searchStartDateTime,
            searchEndDateTime = searchEndDateTime,
            orderStatus = CoupangOrderStatus.ACCEPT,
        )
        return newOrdersResponse.data.map { it.toDomain() }
    }

    companion object {
        private const val SHEET_NAME = "coupang"
        private const val SHEET_START_COLUMN_ROW = "A2"

        private const val COLUMN_COUNT = 13
    }
}