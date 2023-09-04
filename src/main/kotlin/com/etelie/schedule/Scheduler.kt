@file:Suppress("MemberVisibilityCanBePrivate")

package com.etelie.schedule

import com.etelie.application.ExecutionEnvironment
import com.etelie.application.logger
import com.etelie.imports.treasury.AuctionedImportJob
import com.etelie.imports.treasury.AverageInterestRatesImportJob
import com.etelie.imports.treasury.SavingsBondRatesImportJob
import com.etelie.persistence.database.DatabaseConfigFactory
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.quartz.JobDetail
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.impl.StdSchedulerFactory

private val log = logger {}

object Scheduler {

    init {
        val dataConfig = DatabaseConfigFactory.fromExecutionEnvironment(ExecutionEnvironment.current)
        check(dataConfig != null) { "Connecting to database within an unsupported environment [${ExecutionEnvironment.current.label}]" }

        val sysProps = System.getProperties()
        sysProps.setProperty("org.quartz.dataSource.postgresql.URL", dataConfig.jdbcUrl)
        sysProps.setProperty("org.quartz.dataSource.postgresql.user", dataConfig.user)
        sysProps.setProperty("org.quartz.dataSource.postgresql.password", dataConfig.password)
    }

    private val scheduler = StdSchedulerFactory().scheduler

    init {
        runBlocking(CoroutineName("${this::class.simpleName}-init")) {
            awaitAll(
                async { AverageInterestRatesImportJob.getJobDefinition() },
                async { AuctionedImportJob.getJobDefinition() },
                async { SavingsBondRatesImportJob.getJobDefinition() },
            ).forEach { definition ->
                subscribe(definition)
                log.info { "Scheduled job: ${definition.jobDetail.description}" }
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
