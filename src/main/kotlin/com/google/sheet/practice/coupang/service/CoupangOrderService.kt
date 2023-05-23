package com.google.sheet.practice.coupang.service

import com.google.sheet.practice.coupang.domain.CoupangOrder
import com.google.sheet.practice.coupang.domain.CoupangOrderStatus
import com.google.sheet.practice.coupang.external.CoupangOrderClient
import com.google.sheet.practice.googlesheet.GoogleSheetService
import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.googlesheet.external.GoogleSheetRange
import com.google.sheet.practice.line.external.LineNotificationClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoupangOrderService(
    private val coupangOrderClient: CoupangOrderClient,
    private val googleSheetService: GoogleSheetService,
    private val googleSheetClient: GoogleSheetClient,
    private val lineNotificationClient: LineNotificationClient,
) {
    private val logger = LoggerFactory.getLogger(CoupangOrderService::class.java)

    fun updateOrders(searchEndDateTime: LocalDateTime) {
        val originOrders = this.originOrders()
        val newOrders = this.newOrders(searchEndDateTime)
        this.sendNewOrdersNotification(newOrders)
        this.updateNewOrdersConfirm(newOrders)
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

    private fun updateNewOrdersConfirm(newOrders: List<CoupangOrder>) {
        if (newOrders.isNotEmpty()) {
            val response = coupangOrderClient.updateOrdersConfirm(newOrders.map { it.shipmentBoxId })
            val successOrderConfirmResults = response.data.responseList.filter { it.succeed }
            val failOrderConfirmResults = response.data.responseList.filter { !it.succeed }
            logger.info("쿠팡 신규 주문 발주 확인 처리. 발주 확인 건수: ${successOrderConfirmResults.size}, " +
                    "발주 확인 주문 ID: ${successOrderConfirmResults.map { it.shipmentBoxId }}")
            if (failOrderConfirmResults.isNotEmpty()) {
                logger.error("쿠팡 신규 주문 발주 확인 처리 실패. 발주 확인 실패 건수: ${failOrderConfirmResults.size}, " +
                        "발주 확인 실패 주문 ID: ${failOrderConfirmResults.map { it.shipmentBoxId }}")
            }
        }
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

    private fun newOrders(searchEndDateTime: LocalDateTime): List<CoupangOrder> {
        val searchStartDateTime = searchEndDateTime.minusHours(24)
        if (isItBeforeAndAfterMidnight(searchStartDateTime, searchEndDateTime)) {
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

    private fun isItBeforeAndAfterMidnight(
        searchStartDateTime: LocalDateTime,
        searchEndDateTime: LocalDateTime
    ) = searchStartDateTime.dayOfMonth != searchEndDateTime.dayOfMonth

    companion object {
        private const val SHEET_NAME = "coupang"
        private const val SHEET_START_COLUMN_ROW = "A2"
        private const val COLUMN_COUNT = 14
    }
}