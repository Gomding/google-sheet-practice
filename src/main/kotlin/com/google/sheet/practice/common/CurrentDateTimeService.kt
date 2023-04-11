package com.google.sheet.practice.common

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CurrentDateTimeService {
    fun currentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}