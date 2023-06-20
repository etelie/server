package com.etelie.securities.price.imports.treasury

import com.etelie.schedule.Scheduler
import com.etelie.schedule.withEtelieIdentity
import org.quartz.CronScheduleBuilder
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.TriggerBuilder

object AverageInterestRates {

    /** Every day at 12pm UTC */
    private const val CRON_EXPRESSION = "0 0 12 * * *"

    private val jobDetail = JobBuilder.newJob()
        .ofType(AverageInterestRatesImportJob::class.java)
        .storeDurably()
        .withEtelieIdentity(AverageInterestRatesImportJob::class)
        .build()

    private val scheduleBuilder = CronScheduleBuilder
        .cronSchedule(CRON_EXPRESSION)
        .withMisfireHandlingInstructionFireAndProceed()

    private val trigger = TriggerBuilder.newTrigger()
        .withSchedule(scheduleBuilder)
        .forJob(jobDetail)
        .withEtelieIdentity(AverageInterestRatesImportJob::class)
        .build()

    init {
        Scheduler.subscribe(jobDetail, trigger)
    }

    class AverageInterestRatesImportJob : Job {
        override fun execute(context: JobExecutionContext?) {
            // todo: invoke a service method
        }
    }

}
