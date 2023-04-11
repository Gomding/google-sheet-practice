package com.google.sheet.practice.googlesheet.external

class GoogleSheetRange(
    val sheetName: String,
    val startColumnRow: String,
    val endColumnRow: String,
) {
    fun changeToString(): String {
        return sheetName + NAME_RANGE_DELIMITER + startColumnRow + RANGE_START_END_DELIMITER + endColumnRow
    }

    companion object {
        private const val NAME_RANGE_DELIMITER = "!"
        private const val RANGE_START_END_DELIMITER = ":"
        private const val SHEET_START_COLUMN = 'A'
        private const val SHEET_START_ROW = 2

        fun create(sheetName: String, columnCount: Int, rowCount: Int): GoogleSheetRange {
            val endColumn = Char(SHEET_START_COLUMN.code + columnCount).toString()
            val endRow = SHEET_START_ROW + rowCount
            return GoogleSheetRange(
                sheetName = sheetName,
                startColumnRow = SHEET_START_COLUMN.toString() + SHEET_START_ROW,
                endColumnRow = endColumn + endRow
            )
        }
    }
}