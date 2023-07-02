package com.etelie.imports.treasury

import com.etelie.imports.ImporterSecurityAssociationTable
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

object AverageInterestRatesImport {

    const val importerId = 1

    suspend fun import(): String = coroutineScope {
        val currentDate: LocalDate =
            Clock.System.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.MONTH)
        val currentAverageRates = async { TreasuryClient.averageInterestRateForDate(currentDate) }
        val associatedSecurities = async { ImporterSecurityAssociationTable.fetchSecuritiesForImporter(importerId) }
        val averageRatesPerSecurity: Map<SecurityDetail, BigDecimal> = currentAverageRates.await()
            .filterKeys { key ->
                associatedSecurities.await().map { (_, serialName) ->
                    serialName
                }
                    .contains(key)
            }
            .mapKeys { (key, _) ->
                associatedSecurities.await().find { (_, serialName) ->
                    serialName == key
                }
                    ?.first
                    ?: throw IllegalStateException("SecurityType not found after filter")
            }

        averageRatesPerSecurity.entries.joinToString("\n")
    }

}
