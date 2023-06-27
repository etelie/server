package com.etelie.imports.treasury

import com.etelie.schedule.createStandardJobDefinition
import com.etelie.schedule.logged
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext

private val log = KotlinLogging.logger {}

class AverageInterestRatesImportJob : Job {

    override fun execute(context: JobExecutionContext?) {
        logged(log, context) {
            AverageInterestRatesImport.import()
        }
    }

    companion object {
        /** Every day at 12pm UTC */
        private const val CRON_EXPRESSION = "0 0 12 ? * *"
        val jobDefinition = AverageInterestRatesImportJob::class.createStandardJobDefinition(CRON_EXPRESSION)
    }

}
