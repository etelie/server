package com.etelie.imports.treasury

import com.etelie.imports.ImporterTable
import com.etelie.schedule.JobDefinition
import com.etelie.schedule.coroutineContext
import com.etelie.schedule.createStandardJobDefinition
import com.etelie.schedule.logged
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

private val log = KotlinLogging.logger {}

class SavingsBondsValueFilesJob : Job {

    override fun execute(context: JobExecutionContext?): Unit = runBlocking(this.coroutineContext) {
        logged(log, context) {
            SavingsBondsValueFiles.import()
        }
    }

    companion object {
        private suspend fun getCronExpression(): String {
            return ImporterTable.fetchCronExpression(SavingsBondsValueFiles.importerId)
        }

        suspend fun getJobDefinition(): JobDefinition {
            return SavingsBondsValueFilesJob::class.createStandardJobDefinition(getCronExpression())
        }
    }

}
