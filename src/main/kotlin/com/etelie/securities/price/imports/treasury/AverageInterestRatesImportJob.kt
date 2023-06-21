package com.etelie.securities.price.imports.treasury

import com.etelie.schedule.Scheduler
import com.etelie.schedule.createStandardQuartzComponents
import com.etelie.schedule.finishMessage
import com.etelie.schedule.startMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext

private val log = KotlinLogging.logger {}

class AverageInterestRatesImportJob : Job {
    companion object {
        /** Every day at 12pm UTC */
        private const val CRON_EXPRESSION = "0 0 12 * * *"

        init {
            Scheduler.subscribe(AverageInterestRatesImportJob::class.createStandardQuartzComponents(CRON_EXPRESSION))
        }
    }

    override fun execute(context: JobExecutionContext?) {
        log.info { context.startMessage }
        AverageInterestRatesImport.import()
        log.info { context.finishMessage }
    }
}
