package com.google.sheet.practice.googlesheet.external

/**
 * https://developers.google.com/sheets/api/reference/rest/v4/ValueInputOption?hl=ko
 * 구글 시트에 입력 데이터를 해석하는 방법
 */
enum class GoogleValueInputOption(
    val value: String,
) {
    RAW("RAW"),
    USER_ENTERED("USER_ENTERED")
}