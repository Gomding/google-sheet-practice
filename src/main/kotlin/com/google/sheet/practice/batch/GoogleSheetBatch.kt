package com.google.sheet.practice.batch

import com.google.sheet.practice.googlesheet.GoogleSheetClient
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    name = ["spring.batch.job.names"],
    havingValue = GoogleSheetBatch.JOB_NAME
)
class GoogleSheetBatch(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val googleSheetClient: GoogleSheetClient,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun googleSheetReadJob(): Job {
        logger.info(">> google sheet read job start <<")
        return jobBuilderFactory[JOB_NAME]
            .incrementer(RunIdIncrementer())
            .start(googleSheetReadStep())
            .build()
    }

    private fun googleSheetReadStep(): Step {
        logger.info(">> google sheet read step start <<")
        return stepBuilderFactory["googleSheetReadStep1"]
            .tasklet { _, _ ->
                logger.info(">> google sheet read tasklet start <<")
                println("${googleSheetClient.getGoogleSheetRows()}")

                RepeatStatus.FINISHED
            }
            .build()
    }

    companion object {
        internal const val JOB_NAME = "googleSheetBatch"
    }
}