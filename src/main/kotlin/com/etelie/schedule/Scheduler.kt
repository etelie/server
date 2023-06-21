package com.etelie.schedule

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import org.quartz.JobDetail
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.impl.StdSchedulerFactory

private val log = KotlinLogging.logger {}

object Scheduler {
    private val scheduler = StdSchedulerFactory().scheduler

    private fun start(): Unit = scheduler.start()
    private fun shutdown(wait: Boolean = false): Unit = scheduler.shutdown(wait)

    fun listen(environment: ApplicationEnvironment) {
        environment.monitor.subscribe(ApplicationStarting) {
            log.info("ApplicationStarting")
        }
        environment.monitor.subscribe(ApplicationStarted) {
            log.info("ApplicationStarted")
            start()
        }
        environment.monitor.subscribe(ApplicationStopPreparing) {
            log.info("ApplicationStopPreparing")
            shutdown()
        }
        environment.monitor.subscribe(ApplicationStopping) {
            log.info("ApplicationStopping")
        }
        environment.monitor.subscribe(ApplicationStopped) {
            log.info("ApplicationStopped")
        }
    }

    fun subscribe(job: JobDetail, trigger: Trigger) {
        try {
            scheduler.scheduleJob(job, trigger)
        } catch (e: SchedulerException) {
            log.error(e) { "Error scheduling job ${job.description}" }
        }
    }

    fun subscribe(components: QuartzComponents) {
        this.subscribe(components.jobDetail, components.trigger)
    }

}
