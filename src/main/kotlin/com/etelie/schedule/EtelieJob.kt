package com.etelie.schedule

import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

interface EtelieJob : Job {

    val logger: KLogger

    suspend fun execute()

    override fun execute(context: JobExecutionContext?) = runBlocking(jobCoroutineContext) {
        logged(logger, context) {
            execute()
        }
    }

}
