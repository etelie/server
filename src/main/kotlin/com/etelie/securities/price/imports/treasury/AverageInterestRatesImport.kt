package com.etelie.securities.price.imports.treasury

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val log = KotlinLogging.logger {}

object AverageInterestRatesImport {

    fun import(): TreasuryClient.Response {
        val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        val response = TreasuryClient.averageInterestRateForTypeAndDate(SecurityType.TREASURY_BILL, currentDate)
        return response
    }

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
