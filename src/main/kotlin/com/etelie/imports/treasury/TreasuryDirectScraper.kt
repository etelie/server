package com.etelie.imports.treasury

import com.etelie.network.WebContentNotFoundException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.Locale

object TreasuryDirectScraper {

    private const val basePath = "https://treasurydirect.gov"
    private val dateFormat = DateTimeFormatter.ofPattern("MMM d, y", Locale.US)
    private val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)
    private val client: HttpHandler = ApacheClient()

    fun scrapeSavingsBondRates(): SavingsBondScrapeResult {
        val eePath = "$basePath/savings-bonds/ee-bonds/"
        val iPath = "$basePath/savings-bonds/i-bonds/"
        val eeRequest = Request(Method.GET, eePath)
        val iRequest = Request(Method.GET, iPath)
        val eeResponse = client(eeRequest).bodyString()
        val iResponse = client(iRequest).bodyString()

        val eeIssueMatches: List<MatchResult> = Regex("""For EE bonds issued (\w+ \d+, \d+) to .+.""")
            .findAll(eeResponse)
            .toList()
        val eeRateMatches: List<MatchResult> = Regex("""(\d+\.\d+)%""")
            .findAll(eeResponse)
            .toList()
        val iIssueMatches: List<MatchResult> = Regex("""For I bonds issued (\w+ \d+, \d+) to .+.""")
            .findAll(iResponse)
            .toList()
        val iRateMatches: List<MatchResult> = Regex("""(\d+\.\d+)%""")
            .findAll(iResponse)
            .toList()

        if (eeIssueMatches.size != 1 ||
            iIssueMatches.size != 1 ||
            eeRateMatches.size != 1 ||
            iRateMatches.size != 2
        ) {
            throw WebContentNotFoundException("${this::class.simpleName} failed to find interest rates")
        }

        val eeBondIssueDate: LocalDate = eeIssueMatches
            .get(0)
            .groupValues
            .get(1)
            .let { java.time.LocalDate.parse(it, dateFormat).toKotlinLocalDate() }
        val eeBondFixedRate: BigDecimal = eeRateMatches
            .map { it.groupValues.get(1) }
            .get(0)
            .let { BigDecimal(it) }
        val iBondIssueDate: LocalDate = iIssueMatches
            .get(0)
            .groupValues
            .get(1)
            .let { java.time.LocalDate.parse(it, dateFormat).toKotlinLocalDate() }
        val (
            iBondFixedRate: BigDecimal,
            iBondVariableRate: BigDecimal,
        ) = iRateMatches
            .map { it.groupValues.get(1) }
            .let { Pair(BigDecimal(it.get(0)), BigDecimal(it.get(1))) }

        return SavingsBondScrapeResult(
            eeBondIssueDate = eeBondIssueDate,
            eeBondFixedRate = eeBondFixedRate,
            iBondIssueDate = iBondIssueDate,
            iBondFixedRate = iBondFixedRate,
            iBondVariableRate = iBondVariableRate,
        )
    }

}
