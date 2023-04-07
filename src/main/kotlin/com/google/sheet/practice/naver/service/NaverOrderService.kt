package com.google.sheet.practice.naver.service

import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.naver.domain.ProductOrderStatus
import com.google.sheet.practice.naver.domain.NaverProductOrderDetail
import com.google.sheet.practice.naver.external.NaverOrderClient
import com.google.sheet.practice.naver.external.dto.NaverProductOrdersDetailRequest
import org.springframework.stereotype.Component

@Component
class NaverOrderService(
    private val naverOrderClient: NaverOrderClient,
    private val googleSheetClient: GoogleSheetClient,
) {
    fun updateRowFromNewDelivery() {
        val originProductOrderIds = this.originProductOrderIds()
        val newProductOrderIds = this.newProductOrderIds()
        val orderIds = originProductOrderIds.plus(newProductOrderIds).distinct()

        val orderDetailsBeforeDelivery = orderDetailsBeforeDelivery(orderIds)
        val values = orderDetailsBeforeDelivery.map { it.flatValues() }
        val rangeForUpdate = rangeForUpdate(orderDetailsBeforeDelivery)
        googleSheetClient.batchUpdateValues(range = rangeForUpdate, values = values)
    }

    private fun orderDetailsBeforeDelivery(orderIds: List<Long>): List<NaverProductOrderDetail> {
        val naverProductOrdersDetailRequest = NaverProductOrdersDetailRequest(orderIds)
        val ordersDetailResponse = naverOrderClient.productOrdersDetail(naverProductOrdersDetailRequest)
        val ordersDetail = ordersDetailResponse.data.map { it.toDomain() }
            .filter { it.productOrderStatus == ProductOrderStatus.PAYED }
        return ordersDetail
    }

    private fun originProductOrderIds(): List<Long> {
        val originOrders = googleSheetClient.getGoogleSheetRows("naver!A2:N100")
        val originProductOrderIds = originOrders.map { it[0].toString().toLong() }
        return originProductOrderIds
    }

    private fun rangeForUpdate(orderDetailsBeforeDelivery: List<NaverProductOrderDetail>): String {
        val endColumn = Char(SHEET_START_COLUMN.code + COLUMN_COUNT).toString()
        val endRow = SHEET_START_ROW + orderDetailsBeforeDelivery.size - 1
        val range = "$SHEET_RANGE$endColumn$endRow"
        return range
    }

    private fun newProductOrderIds(): List<Long> {
        val ordersResponse = naverOrderClient.lastChangedStatusOrders().data.lastChangeStatuses
        val newOrders = ordersResponse.map { it.toDomain() }
            .filter { it.isPayed() }
        val newProductOrderIds = newOrders.map { it.productOrderId }
        return newProductOrderIds
    }

    companion object {
        private const val SHEET_RANGE = "naver!A2:"
        private const val SHEET_START_ROW = 2
        private const val SHEET_START_COLUMN = 'A'
        private const val COLUMN_COUNT = 14
    }
}