package com.google.sheet.practice.naver.service

import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.naver.domain.NaverProductOrderDetail
import com.google.sheet.practice.naver.external.NaverOrderClient
import com.google.sheet.practice.naver.external.dto.NaverProductOrdersDetailRequest
import org.springframework.stereotype.Component

@Component
class NaverOrderService(
    private val naverOrderClient: NaverOrderClient,
    private val googleSheetClient: GoogleSheetClient,
) {
    fun newOrdersDetail(): List<NaverProductOrderDetail> {
        val ordersResponse = naverOrderClient.lastChangedStatusOrders().data.lastChangeStatuses
        val newOrders = ordersResponse.map { it.toDomain() }
            .filter { it.isPayed() }
        val naverProductOrdersDetailRequest = NaverProductOrdersDetailRequest(newOrders.map { it.productOrderId })
        val ordersDetailResponse = naverOrderClient.productOrdersDetail(naverProductOrdersDetailRequest)
        return ordersDetailResponse.data.map { it.toDomain() }
    }

    fun updateRowFromNewDelivery() {
        val newOrdersDetail = this.newOrdersDetail()
        // naver!A2:M3
        val endColumn = Char(START_SHEET_COLUMN.code + COLUMN_COUNT).toString()
        val endRow = START_SHEET_ROW + newOrdersDetail.size - 1
        val range = "$SHEET_RANGE$endColumn$endRow"
        val values = newOrdersDetail.map { it.flatValues() }
        googleSheetClient.batchUpdateValues(range = range, values = values)
    }

    companion object {
        private const val SHEET_RANGE = "naver!A2:"
        private const val START_SHEET_ROW = 2
        private const val START_SHEET_COLUMN = 'A'
        private const val COLUMN_COUNT = 14
    }
}