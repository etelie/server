package com.etelie.securities.price.imports.treasury

import com.etelie.schedule.Scheduler
import com.etelie.schedule.createStandardQuartzComponents
import org.quartz.Job
import org.quartz.JobExecutionContext

object AverageInterestRates {

    /** Every day at 12pm UTC */
    private const val CRON_EXPRESSION = "0 0 12 * * *"

    init {
        Scheduler.subscribe(AverageInterestRatesImportJob::class.createStandardQuartzComponents(CRON_EXPRESSION))
    }

    class AverageInterestRatesImportJob : Job {
        override fun execute(context: JobExecutionContext?) {
            // todo: invoke a service method
        }
    }

}
