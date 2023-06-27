package com.etelie.imports.treasury

import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.securities.detail.SecurityDetail
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

object AverageInterestRatesImport {

    private const val importerId = 1

    fun import(): String {
        val associatedSecurities = ImporterSecurityAssociationTable.fetchSecuritiesForImporter(importerId)
        val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.MONTH)
        val currentAverageRates: Map<SecurityDetail, BigDecimal> =
            TreasuryClient.averageInterestRateForDate(currentDate)
                .filterKeys { key ->
                    associatedSecurities.map { (_, serialName) ->
                        serialName
                    }
                        .contains(key)
                }
                .mapKeys { (key, _) ->
                    associatedSecurities.find { (_, serialName) ->
                        serialName == key
                    }
                        ?.first
                        ?: throw IllegalStateException("SecurityType not found after filter")
                }
        return currentAverageRates.entries.joinToString("\n")
    }

}
