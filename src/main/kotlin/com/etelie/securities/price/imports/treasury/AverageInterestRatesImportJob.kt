package com.etelie.securities.price.imports.treasury

import com.etelie.schedule.Scheduler
import com.etelie.schedule.createStandardJobDetail
import com.etelie.schedule.createStandardTrigger
import com.etelie.schedule.logged
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.SimpleScheduleBuilder

private val log = KotlinLogging.logger {}

class AverageInterestRatesImportJob : Job {
    companion object {
        /** Every day at 12pm UTC */
        private const val CRON_EXPRESSION = "0 0 12 * * *"

        init {
//            Scheduler.subscribe(AverageInterestRatesImportJob::class.createStandardQuartzComponents(CRON_EXPRESSION))
            val job = AverageInterestRatesImportJob::class.createStandardJobDetail()
            val trigger = AverageInterestRatesImportJob::class.createStandardTrigger(
                SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(3)
            )
            Scheduler.subscribe(job, trigger)
        }
    }

    override fun execute(context: JobExecutionContext?) {
        logged(log, context) {
            AverageInterestRatesImport.import()
        }
    }
}
