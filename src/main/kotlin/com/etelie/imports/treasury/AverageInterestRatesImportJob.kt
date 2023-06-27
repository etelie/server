package com.etelie.imports.treasury

import com.etelie.imports.ImporterTable
import com.etelie.schedule.JobDefinition
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
        private suspend fun getCronExpression(): String {
            return ImporterTable.fetchCronExpression(1)
        }
        suspend fun getJobDefinition(): JobDefinition {
            return AverageInterestRatesImportJob::class.createStandardJobDefinition(getCronExpression())
        }
    }

}
