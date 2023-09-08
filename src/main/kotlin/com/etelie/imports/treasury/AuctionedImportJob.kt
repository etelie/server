package com.etelie.imports.treasury

import com.etelie.imports.ImporterTable
import com.etelie.schedule.EtelieJob
import com.etelie.schedule.JobDefinition
import com.etelie.schedule.createStandardJobDefinition
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

class AuctionedImportJob : EtelieJob {

    override val logger: KLogger = log

    override suspend fun execute() {
        AuctionedImporter.import()
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
