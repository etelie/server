package com.etelie.imports.treasury

import com.etelie.imports.ImporterTable
import com.etelie.schedule.JobDefinition
import com.etelie.schedule.createStandardJobDefinition
import com.etelie.schedule.jobCoroutineContext
import com.etelie.schedule.logged
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

private val log = KotlinLogging.logger {}

class AuctionedImportJob : Job {

    override fun execute(context: JobExecutionContext?): Unit = runBlocking(jobCoroutineContext) {
        logged(log, context) {
            AuctionedImporter.import()
        }
    }

    companion object {
        private suspend fun getCronExpression(): String {
            return ImporterTable.fetchCronExpression(AuctionedImporter.importerId)
        }

        suspend fun getJobDefinition(): JobDefinition {
            return AuctionedImportJob::class.createStandardJobDefinition(getCronExpression())
        }
    }

}
