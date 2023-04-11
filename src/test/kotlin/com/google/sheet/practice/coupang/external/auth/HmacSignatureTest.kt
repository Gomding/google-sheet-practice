package com.google.sheet.practice.coupang.`external`.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.text.SimpleDateFormat
import java.util.*

internal class HmacSignatureTest : FunSpec({
    test("Hmac 시그니쳐를 생성할 수 있다.") {
        // given
        val secretKey = "secret"
        val datetimeFormat = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
        val datetime = datetimeFormat.format(Date(2023, 5, 30))
        val uri = "http://localhost:8080/test"
        val method = "GET"

        // when
        val signature = HmacSignature.create(
            secretKey = secretKey,
            datetime = datetime,
            uri = uri,
            method = method,
        )

        // then
        signature shouldBe "89e9cdf6c254517b17940d4787eaf76644043a739e7fe52352e1f347582c4fb5"
    }

    test("Query String이 있는 URI로 Hmac 시그니쳐를 생성할 수 있다.") {
        // given
        val secretKey = "secret"
        val datetimeFormat = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
        val datetime = datetimeFormat.format(Date(2023, 5, 30))
        val uri = "http//localhost:8080/test?testparam=test"
        val method = "GET"

        // when
        val signature = HmacSignature.create(
            secretKey = secretKey,
            datetime = datetime,
            uri = uri,
            method = method,
        )

        // then
        signature shouldBe "b0f984f237ccd1cac4cba81d0ac48b5e330f856744f35db7a3e7c19a68453075"
    }

    test("Query String 시작 문자인 `?`가 두 개 이상 이면 Hmac 시그니쳐를 생성할 때 예외가 발생한다.") {
        // given
        val secretKey = "secret"
        val datetimeFormat = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
        val datetime = datetimeFormat.format(Date(2023, 5, 30))
        val uri = "http//localhost:8080/test?testparam=test?secondParam=test"
        val method = "GET"

        // when then
        val thrownException = shouldThrow<RuntimeException> {
            HmacSignature.create(
                secretKey = secretKey,
                datetime = datetime,
                uri = uri,
                method = method,
            )
        }
        thrownException.message shouldBe "incorrect uri format"
    }
})
