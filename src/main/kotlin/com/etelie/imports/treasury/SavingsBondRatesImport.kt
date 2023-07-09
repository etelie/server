package com.etelie.imports.treasury

import com.etelie.network.WebContentNotFoundException
import com.etelie.securities.SecurityTerm
import com.etelie.securities.SecurityType
import com.etelie.securities.price.SecurityPrice
import com.etelie.securities.price.SecurityPriceTable
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

object SavingsBondRatesImport {

    const val importerId = 3

    suspend fun import(): String = coroutineScope {
        val scraperResult: TreasuryDirectScraperResult?
        try {
            scraperResult = TreasuryDirectScraper.scrape()
        } catch (e: WebContentNotFoundException) {
            return@coroutineScope "${this::class.simpleName} failed; scrape failure".also {
                log.error(e) { it }
            }
        }

        val eeBondPrice = SecurityPrice(
            purchasedTimestamp = null,
            issuedTimestamp = null,
            term = SecurityTerm.months(30 * 12),
            interestRateFixed = BigDecimal(scraperResult.eeBondFixedRate),
            interestRateVariable = BigDecimal(0),
        )
        val iBondPrice = SecurityPrice(
            purchasedTimestamp = null,
            issuedTimestamp = null,
            term = SecurityTerm.months(30 * 12),
            interestRateFixed = BigDecimal(scraperResult.iBondFixedRate),
            interestRateVariable = BigDecimal(scraperResult.iBondVariableRate),
        )

        var insertedPricesCount = 0
        insertedPricesCount += SecurityPriceTable.insert(SecurityType.TREASURY_SAVINGS_EE, eeBondPrice)
        insertedPricesCount += SecurityPriceTable.insert(SecurityType.TREASURY_SAVINGS_I, iBondPrice)


        "${this@SavingsBondRatesImport::class.simpleName} complete; $insertedPricesCount prices inserted into security_price table".also {
            log.info { it.replace("\n", "") }
        }
    }

}
