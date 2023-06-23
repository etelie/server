package com.etelie.securities.price.imports.treasury

import com.etelie.schedule.Scheduler
import com.etelie.schedule.createStandardJobDefinition
import com.etelie.schedule.logged
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext

private val log = KotlinLogging.logger {}

class AverageInterestRatesImportJob : Job {
    companion object {
        /** Every day at 12pm UTC */
        private const val CRON_EXPRESSION = "0 0 12 * * *"

        init {
            Scheduler.subscribe(AverageInterestRatesImportJob::class.createStandardJobDefinition(CRON_EXPRESSION))
        }
    }

    override fun execute(context: JobExecutionContext?) {
        logged(log, context) {
            AverageInterestRatesImport.import()
        }
    }
}
