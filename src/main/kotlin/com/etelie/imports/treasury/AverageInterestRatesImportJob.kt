package com.etelie.imports.treasury

import com.etelie.schedule.createStandardJobDefinition
import com.etelie.schedule.logged
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

private val log = KotlinLogging.logger {}

class AverageInterestRatesImportJob : Job {

    private val coroutineName = CoroutineName("${this::class.simpleName}")
    private val coroutineContext = Dispatchers.IO + coroutineName

    override fun execute(context: JobExecutionContext?): Unit = runBlocking(coroutineContext) {
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
