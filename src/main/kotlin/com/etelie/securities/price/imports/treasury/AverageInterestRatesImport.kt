package com.etelie.securities.price.imports.treasury

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


    fun import(): String {
        val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.MONTH)
        val currentAverageRates: Map<String, BigDecimal> = TreasuryClient.averageInterestRateForDate(currentDate)
            .filterKeys { key ->
                SecurityType.values().map { securityType ->
                    securityType.treasurySerialName
                }.contains(key)
            }

        return currentAverageRates.toString()
    }


    @Deprecated(
        message = "Will be obsoleted by SRV-48",
        level = DeprecationLevel.WARNING,
    )
    enum class SecurityType(
        val treasurySerialName: String,
        val detailId: Int,
    ) {
        TREASURY_BILL("Treasury Bills", 3),
        TREASURY_BOND("Treasury Bonds", 4),
        TREASURY_NOTE("Treasury Notes", 5),
        TREASURY_FLOATING_RATE_NOTE("Treasury Floating Rate Notes (FRN)", 6),
        TREASURY_INFLATION_PROTECTED_SECURITY("Treasury Inflation-Protected Securities (TIPS)", 7),
    }

}
