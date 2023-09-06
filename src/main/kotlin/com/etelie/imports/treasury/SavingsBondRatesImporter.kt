package com.etelie.imports.treasury

import com.etelie.imports.PriceImporter
import com.etelie.network.WebContentNotFoundException
import com.etelie.securities.SecurityTerm
import com.etelie.securities.SecurityType
import com.etelie.securities.price.SecurityPrice
import com.etelie.securities.price.SecurityPriceTable
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

object SavingsBondRatesImporter : PriceImporter() {

    const val importerId = 3

    suspend fun import(): String = coroutineScope {
        val scraperResult: SavingsBondScrapeResult?
        try {
            scraperResult = TreasuryDirectScraper.scrapeSavingsBondRates()
        } catch (e: WebContentNotFoundException) {
            return@coroutineScope getFailureMessage("scrape failure").also {
                log.error(e) { it }
            }
        }

        val eeBondPrice = SecurityPrice(
            purchasedTimestamp = null,
            issuedTimestamp = scraperResult.eeBondIssueDate.atStartOfDayIn(TimeZone.UTC),
            term = SecurityTerm.months(30 * 12),
            interestRateFixed = scraperResult.eeBondFixedRate,
            interestRateVariable = BigDecimal(0),
        )
        val iBondPrice = SecurityPrice(
            purchasedTimestamp = null,
            issuedTimestamp = scraperResult.iBondIssueDate.atStartOfDayIn(TimeZone.UTC),
            term = SecurityTerm.months(30 * 12),
            interestRateFixed = scraperResult.iBondFixedRate,
            interestRateVariable = scraperResult.iBondVariableRate,
        )

        val insertedPricesCount: Int = awaitAll(
            async { SecurityPriceTable.insert(SecurityType.TREASURY_SAVINGS_EE, eeBondPrice) },
            async { SecurityPriceTable.insert(SecurityType.TREASURY_SAVINGS_I, iBondPrice) },
        ).fold(0) { total, insertions ->
            total + insertions
        }

        getSuccessMessage(insertedPricesCount).also {
            log.info { it }
        }
    }

}
