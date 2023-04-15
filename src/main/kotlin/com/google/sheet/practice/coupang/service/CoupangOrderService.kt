package com.google.sheet.practice.coupang.service

import com.google.sheet.practice.coupang.domain.CoupangOrder
import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import com.google.sheet.practice.coupang.external.CoupangOrderClient
import com.google.sheet.practice.googlesheet.GoogleSheetService
import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.googlesheet.external.GoogleSheetRange
import com.google.sheet.practice.line.external.LineNotificationClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoupangOrderService(
    private val coupangOrderClient: CoupangOrderClient,
    private val googleSheetService: GoogleSheetService,
    private val googleSheetClient: GoogleSheetClient,
    private val lineNotificationClient: LineNotificationClient,
) {
    fun updateOrders(searchEndDateTime: LocalDateTime) {
        val originOrders = this.originOrders()
        val newOrders = this.newOrders(searchEndDateTime)
        this.sendNewOrdersNotification(newOrders)
        val orders = newOrders.plus(originOrders)
            .distinctBy { it.orderId }
            .filter { it.isBeforeDelivery() }
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

    private fun sendNewOrdersNotification(newOrders: List<CoupangOrder>) {
        newOrders.forEach {
            val orderItemsToString = it.orderItems.map { it.toFlatString() }.joinToString("\n")
            val message = with(it) {
                """쿠팡에 새로 주문이 들어왔어요.
                    |
                    |${orderItemsToString}
                    |
                    |${receiver.name} / ${overseaShipping?.ordererPhoneNumber ?: receiver.safeNumber}
                    |
                    |${receiver.addr1}
                    |${receiver.addr2}
                    |
                    |우편번호 ${receiver.postCode}
                """.trimMargin()
            }
            lineNotificationClient.sendNotification(message)
        }
    }

    fun originOrders(): List<CoupangOrder> {
        val coupangOriginOrdersSheetRange = GoogleSheetRange(
            sheetName = SHEET_NAME,
            startColumnRow = SHEET_START_COLUMN_ROW,
            endColumnRow = "N100"
        )
        val originOrderIds = googleSheetClient.getGoogleSheetRows(coupangOriginOrdersSheetRange.changeToString())
            .map { it[0].toString().toLong() }
        return originOrderIds.mapNotNull { coupangOrderClient.getOrder(it) }
            .map { it.data[0].toDomain() }
    }

    fun newOrders(searchEndDateTime: LocalDateTime): List<CoupangOrder> {
        val searchStartDateTime = searchEndDateTime.minusHours(2)
        if (searchStartDateTime.dayOfMonth != searchEndDateTime.dayOfMonth) {
            val midnight = LocalDateTime.of(
                searchEndDateTime.year,
                searchEndDateTime.month,
                searchEndDateTime.dayOfMonth,
                0,
                0,
            )
            val beforeMidnight = midnight.minusNanos(1)
            val newOrdersMidnightToNow = coupangOrderClient.getOrders(
                searchStartDateTime = midnight,
                searchEndDateTime = searchEndDateTime,
                orderStatus = CoupangOrderStatus.ACCEPT,
            ).data.map { it.toDomain() }
            val newOrdersBeforeDay = coupangOrderClient.getOrders(
                searchStartDateTime = searchStartDateTime,
                searchEndDateTime = beforeMidnight,
                orderStatus = CoupangOrderStatus.ACCEPT,
            ).data.map { it.toDomain() }
            return newOrdersMidnightToNow.plus(newOrdersBeforeDay)
        }
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
        private const val COLUMN_COUNT = 14
    }
}