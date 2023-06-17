package com.etelie.schedule

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopPreparing
import org.quartz.impl.StdSchedulerFactory

object Scheduler {
    private val scheduler = StdSchedulerFactory().scheduler

    private fun start(): Unit = scheduler.start()
    private fun shutdown(wait: Boolean = false): Unit = scheduler.shutdown(wait)

    fun listen(environment: ApplicationEnvironment) {
        environment.monitor.subscribe(ApplicationStarted) {
            start()
        }

        environment.monitor.subscribe(ApplicationStopPreparing) {
            shutdown()
        }
    }

}
