package com.google.sheet.practice.naver.service

import com.google.sheet.practice.googlesheet.GoogleSheetService
import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.googlesheet.external.GoogleSheetRange
import com.google.sheet.practice.line.external.LineNotificationClient
import com.google.sheet.practice.naver.domain.ProductOrderStatus
import com.google.sheet.practice.naver.domain.NaverProductOrderDetail
import com.google.sheet.practice.naver.external.NaverOrderClient
import com.google.sheet.practice.naver.external.dto.NaverProductOrdersDetailRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NaverOrderService(
    private val naverOrderClient: NaverOrderClient,
    private val googleSheetService: GoogleSheetService,
    private val googleSheetClient: GoogleSheetClient,
    private val lineNotificationClient: LineNotificationClient,
) {
    fun updateRowFromNewDelivery(searchDateTime: LocalDateTime) {
        val originProductOrderIds = this.originProductOrderIds()
        val newProductOrderIds = this.newProductOrderIds(searchDateTime)
        sendNewOrdersNotification(newProductOrderIds)
        val orderIds = originProductOrderIds.plus(newProductOrderIds).distinct()

        val orderDetailsBeforeDelivery = orderDetailsBeforeDelivery(orderIds)
        val values = orderDetailsBeforeDelivery.map { it.flatValues() }
        val rangeForUpdate = rangeForUpdate(orderDetailsBeforeDelivery)
        googleSheetService.deleteBy(SHEET_NAME, COLUMN_COUNT, 100)
        googleSheetClient.batchUpdateValues(range = rangeForUpdate, values = values)
    }

    private fun originProductOrderIds(): List<Long> {
        // TODO(2023-04-11): range 를 동적으로 구할 수 있도록 리팩터링 필요
        val originOrders = googleSheetClient.getGoogleSheetRows("naver!A2:N100")
        return originOrders.map { it[ORDER_ID_SHEET_INDEX].toString().toLong() }
    }

    private fun newProductOrderIds(searchStartDateTime: LocalDateTime): List<Long> {
        val lastChangedStatusOrders = naverOrderClient.lastChangedStatusOrders(searchStartDateTime)
        if (lastChangedStatusOrders.data == null) {
            return emptyList()
        }
        val ordersResponse = lastChangedStatusOrders.data.lastChangeStatuses
        val newOrders = ordersResponse.map { it.toDomain() }
            .filter { it.isPayed() }
        return newOrders.map { it.productOrderId }
    }

    private fun sendNewOrdersNotification(newOrderIds: List<Long>) {
        val newProductOrderDetails = this.orderDetailsBeforeDelivery(newOrderIds)
        newProductOrderDetails.forEach {
            val message = with(it) {
                """네이버 쇼핑몰에 새로 주문이 들어왔어요.
                    |
                    |$productName
                    |$quantity 개
                    |
                    |주문 상품 페이지: https://smartstore.naver.com/hulululu/products/${productId}
                    |
                    |${shippingAddress.name} / ${shippingAddress.tel1}
                    |
                    |${shippingAddress.baseAddress}
                    |${shippingAddress.detailedAddress}
                    |
                    |우편번호 ${shippingAddress.zipCode}
                """.trimMargin()
            }
            lineNotificationClient.sendNotification(message)
        }
    }

    private fun orderDetailsBeforeDelivery(orderIds: List<Long>): List<NaverProductOrderDetail> {
        if (orderIds.isEmpty()) {
            return emptyList()
        }
        val naverProductOrdersDetailRequest = NaverProductOrdersDetailRequest(orderIds)
        val ordersDetailResponse = naverOrderClient.productOrdersDetail(naverProductOrdersDetailRequest)
        return ordersDetailResponse.data.map { it.toDomain() }
            .filter { it.productOrderStatus == ProductOrderStatus.PAYED }
    }

    private fun rangeForUpdate(orderDetailsBeforeDelivery: List<NaverProductOrderDetail>): GoogleSheetRange {
        return GoogleSheetRange.create(
            sheetName = SHEET_NAME,
            columnCount = COLUMN_COUNT,
            rowCount = orderDetailsBeforeDelivery.size - 1
        )
    }

    companion object {
        private const val SHEET_NAME = "naver"
        private const val COLUMN_COUNT = 14
        private const val ORDER_ID_SHEET_INDEX = 0
    }
}