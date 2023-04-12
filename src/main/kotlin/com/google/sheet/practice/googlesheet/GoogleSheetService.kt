package com.google.sheet.practice.googlesheet

import com.google.sheet.practice.googlesheet.external.GoogleSheetClient
import com.google.sheet.practice.googlesheet.external.GoogleSheetRange
import org.springframework.stereotype.Service

@Service
class GoogleSheetService(
    private val googleSheetClient: GoogleSheetClient,
) {

    fun deleteBy(sheetName: String, columnCount: Int, rowCount: Int) {
        val googleSheetRange = GoogleSheetRange.create(
            sheetName = sheetName,
            columnCount = columnCount,
            rowCount = rowCount,
        )
        val values = List(rowCount) { List(columnCount) { "" } }
        googleSheetClient.batchUpdateValues(
            range = googleSheetRange,
            values = values,
        )
    }
}