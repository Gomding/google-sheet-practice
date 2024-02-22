package com.google.sheet.practice

import com.google.sheet.practice.common.CurrentDateTimeService
import com.google.sheet.practice.coupang.service.CoupangOrderService
import com.google.sheet.practice.naver.service.NaverOrderService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.*


@SpringBootApplication
class GoogleSheetPracticeApplication {
}

fun main(args: Array<String>) {
    runApplication<GoogleSheetPracticeApplication>(*args)
}

@Component
class GoogleSheetApplicationRunner(
    private val coupangOrderService: CoupangOrderService,
    private val naverOrderService: NaverOrderService,
    private val currentDateTimeService: CurrentDateTimeService,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val currentKstDateTime = currentDateTimeService.currentDateTime().plusHours(9)
        naverOrderService.updateRowFromNewDelivery(currentKstDateTime)
        coupangOrderService.updateOrders(currentKstDateTime)
    }
}
