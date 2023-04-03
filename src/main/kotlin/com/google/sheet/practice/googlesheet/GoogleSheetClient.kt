package com.google.sheet.practice.googlesheet

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.sheet.practice.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.*
import java.util.*

/**
 * 셀(cell) 선택 범위를 나타내는 range 는 아래 링크를 참고
 * https://developers.google.com/sheets/api/guides/concepts?hl=ko#expandable-1
 */
@Component
class GoogleSheetClient {
    @Value("\${spread.sheet.id}")
    private lateinit var spreadsheetId: String

    // TODO: 네이버 탭 정보와 구글 탭 정보를 반환받을 수 있도록 수정해야함
    fun getGoogleSheetRows(
        range: String,
    ): List<List<Any>> {
        val sheets = getSheets()
        val response: ValueRange = sheets.spreadsheets()
            .values()[spreadsheetId, range]
            .execute()
        val values: List<List<Any>> = response.getValues()
        if (values.isEmpty()) {
            throw IllegalStateException("Google Sheet에서 읽어온 데이터가 없습니다. values: $values")
        }
        return values
    }

    @Throws(IOException::class)
    fun batchUpdateValues(
        range: String,
        valueInputOption: GoogleValueInputOption = GoogleValueInputOption.RAW,
        values: List<List<Any>>
    ): BatchUpdateValuesResponse {
        val service = getSheets()
        val body = body(range, values, valueInputOption.value)
        var result: BatchUpdateValuesResponse? = null
        try {
            result = service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute()
            System.out.printf("%d cells updated.", result.totalUpdatedCells)
        } catch (e: GoogleJsonResponseException) {
            val error: GoogleJsonError = e.getDetails()
            if (error.getCode() === 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId)
            } else {
                throw e
            }
        }
        return result ?: throw IllegalArgumentException("failed google sheet batch update")
    }

    private fun body(
        range: String?,
        values: List<List<Any?>?>?,
        valueInputOption: String?
    ): BatchUpdateValuesRequest? {
        val data: MutableList<ValueRange> = ArrayList()
        data.add(
            ValueRange()
                .setRange(range)
                .setValues(values)
        )
        val body = BatchUpdateValuesRequest()
            .setValueInputOption(valueInputOption)
            .setData(data)
        return body
    }

    private fun getSheets(): Sheets {
        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return Sheets.Builder(httpTransport, JSON_FACTORY, authorize(httpTransport))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    /** Authorizes the installed application to access user's protected data.  */
    @Throws(Exception::class)
    private fun authorize(httpTransport: NetHttpTransport): Credential? {
        // load client secrets
        val `in`: InputStream = GoogleSheetPracticeApplication::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(
            JSON_FACTORY,
            InputStreamReader(`in`)
        )
        // set up authorization code flow
        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(MemoryDataStoreFactory())
            .build()
        // authorize
        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
    }

    companion object {
        private val APPLICATION_NAME = "hululuuuu-delivery-sheet"
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

        private val SCOPES: List<String> = Collections.singletonList(SheetsScopes.SPREADSHEETS)
        private val CREDENTIALS_FILE_PATH = "/credentials.json"
    }
}