package com.etelie.schedule

import com.etelie.imports.treasury.AuctionedImportJob
import com.etelie.imports.treasury.AverageInterestRatesImportJob
import com.etelie.imports.treasury.SavingsBondsValueFilesJob
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.quartz.JobDetail
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.impl.StdSchedulerFactory

private val log = KotlinLogging.logger {}

object Scheduler {

    private val scheduler = StdSchedulerFactory().scheduler

    init {
        runBlocking(CoroutineName("${this::class.simpleName}-init")) {
            awaitAll(
                async { AverageInterestRatesImportJob.getJobDefinition() },
                async { AuctionedImportJob.getJobDefinition() },
                async { SavingsBondsValueFilesJob.getJobDefinition() },
            ).forEach {
                subscribe(it)
                log.info { "Scheduled job: ${it.jobDetail.description}" }
            }
        }
    }

    fun start(environment: ApplicationEnvironment) {
        environment.monitor.subscribe(ApplicationStarting) {
            log.info("ApplicationStarting")
        }

        environment.monitor.subscribe(ApplicationStarted) {
            log.info("ApplicationStarted")
            scheduler.start()
        }

        environment.monitor.subscribe(ApplicationStopPreparing) {
            log.info("ApplicationStopPreparing")
            scheduler.shutdown(false)
        }

        environment.monitor.subscribe(ApplicationStopping) {
            log.info("ApplicationStopping")
        }

        environment.monitor.subscribe(ApplicationStopped) {
            log.info("ApplicationStopped")
        }
    }

    fun subscribe(job: JobDetail, trigger: Trigger) {
        if (scheduler.checkExists(job.key)) {
            return
        }

        try {
            scheduler.scheduleJob(job, trigger)
        } catch (e: SchedulerException) {
            log.error(e) { "Error scheduling job ${job.description}" }
        }
    }

    fun subscribe(components: JobDefinition) {
        this.subscribe(components.jobDetail, components.trigger)
    }

}
