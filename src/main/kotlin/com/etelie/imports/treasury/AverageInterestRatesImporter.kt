package com.etelie.imports.treasury

import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.imports.ImporterSecurityAssociationTable.detailForSerialName
import com.etelie.imports.ImporterSecurityAssociationTable.serialNames
import com.etelie.imports.PriceImporter
import com.etelie.securities.detail.SecurityDetail
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

object AverageInterestRatesImporter : PriceImporter() {

    const val importerId = 1

    suspend fun import(): String = coroutineScope {
        val currentDate: LocalDate =
            Clock.System.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.MONTH)
        val currentAverageRates = async { FiscalDataClient.averageInterestRateForDate(currentDate) }
        val associatedSecurities = async { ImporterSecurityAssociationTable.fetchSecuritiesForImporter(importerId) }
        val averageRatesPerSecurity: Map<SecurityDetail, BigDecimal> = currentAverageRates.await()
            .filterKeys { key ->
                associatedSecurities.await().serialNames().contains(key)
            }
            .mapKeys { (key, _) ->
                associatedSecurities.await().detailForSerialName(key)
                    ?: throw IllegalStateException("Security type not found after filter")
            }

        getSuccessMessage(0).also {
            val message = averageRatesPerSecurity.entries.joinToString("\n")
            log.info { "Spurious result: $message" }
            log.info { it }
        }
    }

}
